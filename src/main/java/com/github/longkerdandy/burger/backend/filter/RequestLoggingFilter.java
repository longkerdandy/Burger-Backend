package com.github.longkerdandy.burger.backend.filter;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

/**
 * Simple request logging filter that writes the request URI (and optionally the query string) to
 * the Commons Log.
 */
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    return logger.isInfoEnabled();
  }

  /**
   * Writes a log message before the request is processed.
   */
  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
    logger.info(message);
  }

  /**
   * Writes a log message after the request is processed.
   */
  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
  }
}
