package com.github.longkerdandy.burger.backend.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.github.longkerdandy.burger.backend.dto.message.CommentMessage;
import com.github.longkerdandy.burger.backend.util.Jacksons;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

/**
 * EventHub message consuming and processing service. This service is the entry of all IoT messages.
 * It will consume all the ICD messages sent from the gateway, processes the messages and re-send to
 * the internal EventHubs.
 *
 * <p>{@link EventProcessorClient} provides a convenient mechanism to consume events from all
 * partitions of an Event Hub in the context of a consumer group. Event Processor-based application
 * consists of one or more instances of EventProcessorClient(s) which are set up to consume events
 * from the same Event Hub, consumer group to balance the workload across different instances and
 * track progress when events are processed. Based on the number of instances running, each
 * EventProcessorClient may own zero or more partitions to balance the workload among all the
 * instances.
 *
 * @see <a
 * href="https://docs.microsoft.com/en-us/azure/event-hubs/event-processor-balance-partition-load">
 * Event Processor</a>
 */
@Slf4j
@Service
public class EventHubService implements HealthIndicator {

  private final String connStr;    // Event Hubs Namespace connecting string
  private final String commentTopic;     // Event Hub name for comment message

  // Asynchronous producer responsible for transmitting EventData to a specific Event Hub.
  private EventHubProducerAsyncClient commentsPub;

  /**
   * Constructor.
   */
  @Autowired
  public EventHubService(
      @Value("${azure.eventhub.conn.str}") String connStr,
      @Value("${azure.eventhub.topic.comment}") String commentTopic) {
    this.connStr = connStr;
    this.commentTopic = commentTopic;
  }

  /**
   * Initialize the Event Hubs service.
   */
  @PostConstruct
  public void init() {
    log.info("EventHub Service has been initialized");
  }

  /**
   * Destroy the Event Hubs service.
   */
  @PreDestroy
  public void destroy() {
    for (EventHubProducerAsyncClient publisher : List.of(this.commentsPub)) {
      if (publisher != null) {
        publisher.close();
      }
    }

    log.info("EventHub Service has been destroyed");
  }

  @Override
  public Health health() {
    if (this.commentsPub == null) {
      return new Health.Builder().outOfService().build();
    } else {
      return new Health.Builder().up().build();
    }
  }

  /**
   * Send the {@link CommentMessage} message to EventHub.
   *
   * @param commentMsg {@link CommentMessage}
   * @throws IOException when JSON serialization failed
   */
  public void sendCommentMessage(CommentMessage commentMsg) throws IOException {
    // Lazy initializing producer
    if (this.commentsPub == null) {
      this.commentsPub = createPublisher(this.connStr, this.commentTopic);
    }
    sendMessage(this.commentsPub, commentMsg.getReviewId().toHexString(),
        Jacksons.getWriter().writeValueAsBytes(commentMsg));
  }

  /**
   * Create a new {@link EventHubProducerAsyncClient} based on the configuration.
   *
   * @param eventHubNamespaceConnStr Event Hubs Namespace connecting string
   * @param eventHubName             EventHub name
   * @return {@link EventHubProducerAsyncClient}
   */
  protected EventHubProducerAsyncClient createPublisher(String eventHubNamespaceConnStr,
      String eventHubName) {
    return new EventHubClientBuilder()
        .connectionString(eventHubNamespaceConnStr, eventHubName)
        .buildAsyncProducerClient();
  }

  /**
   * Send message to the EventHub.
   *
   * @param publisher    {@link EventHubProducerAsyncClient}
   * @param partitionKey Partition key
   * @param msg          Raw data message
   */
  protected void sendMessage(EventHubProducerAsyncClient publisher, String partitionKey,
      byte[] msg) {
    // Send message to EventHub
    CreateBatchOptions batchOptions = new CreateBatchOptions().setPartitionKey(partitionKey);
    publisher.createBatch(batchOptions).flatMap(batch -> {
          batch.tryAdd(new EventData(msg));
          return publisher.send(batch);
        }
    ).subscribe(unused -> {},
        ex -> log
            .warn("Error occurred when publishing message to EventHub {} with partition key {}",
                publisher.getEventHubName(), partitionKey, ex),
        () -> log.debug("Successfully sent message to EventHub {} with unit partition key {}",
            publisher.getEventHubName(), partitionKey));
  }
}
