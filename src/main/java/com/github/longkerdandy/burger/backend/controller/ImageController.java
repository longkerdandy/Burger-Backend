package com.github.longkerdandy.burger.backend.controller;

import static com.azure.storage.common.sas.SasProtocol.HTTPS_ONLY;
import static java.time.OffsetDateTime.now;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.github.longkerdandy.burger.backend.dto.response.ServiceSasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Image controller.
 */
@Slf4j
@CrossOrigin
@RestController
public class ImageController {

  private final BlobContainerClient container;       // Blob storage container

  @Value("${azure.storage.sas.expiration}")
  private long sasExpiration;

  /**
   * Constructor.
   */
  @Autowired
  public ImageController(@Value("${azure.storage.conn.str}") String storageConnStr,
      @Value("${azure.storage.container.name}") String storageContainerName) {
    this.container = new BlobContainerClientBuilder()
        .connectionString(storageConnStr)
        .containerName(storageContainerName)
        .buildClient();
  }

  /**
   * Generates a temporal service SAS for the container.
   *
   * @return {@link ServiceSasResponse}
   */
  @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
  @GetMapping("/api/images/sas")
  public ResponseEntity<?> getServiceSas() {
    // Generate service SAS
    BlobContainerSasPermission permission = new BlobContainerSasPermission()
        .setReadPermission(true)
        .setWritePermission(true)
        .setListPermission(true);
    BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
        now().plusMinutes(this.sasExpiration), permission).setProtocol(HTTPS_ONLY);
    BlobClient blob = this.container.getBlobClient("");
    String sasToken = blob.generateSas(values);
    // Response
    ServiceSasResponse response = new ServiceSasResponse()
        .setSasToken(sasToken)
        .setSasURL(String.format("https://%s.blob.core.windows.net/%s?%s",
            this.container.getAccountName(), this.container.getBlobContainerName(), sasToken));
    return ResponseEntity.ok(response);
  }
}
