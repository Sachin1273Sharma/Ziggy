package com.ziggy.utils

import org.slf4j.{Logger as Slf4jLogger, LoggerFactory}

trait Logger {
  protected lazy val log: Slf4jLogger = LoggerFactory.getLogger(getClass)
}
