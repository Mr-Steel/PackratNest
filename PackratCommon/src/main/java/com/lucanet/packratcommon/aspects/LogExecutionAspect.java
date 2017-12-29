package com.lucanet.packratcommon.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for conducting trace logging on functions annotated with {@link LogExecution}.
 * @author <a href="mailto:severne@lucanet.com">Severn Everett</a>
 */
@Aspect
@Component
public class LogExecutionAspect {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  // ============================  Constructors  ===========================79
  /**
   * Default Constructor.
   */
  public LogExecutionAspect() {
  }

  // ============================ Public Methods ===========================79

  /**
   * Log an entry into the annotated function for trace reporting purposes.
   * @param joinPoint The function that the aspect will log as entering.
   */
  @Before("@annotation(LogExecution)")
  public void logBefore(JoinPoint joinPoint) {
    String targetClassName = joinPoint.getTarget().getClass().getName();
    Logger logger = LoggerFactory.getLogger(targetClassName);
    logger.trace("Entering: {}:{}", targetClassName, joinPoint.getSignature().getName());
  }

  /**
   * Log an exit into the annotated function for trace reporting purposes.
   * @param joinPoint The function that the aspect will log as exiting.
   */
  @After("@annotation(LogExecution)")
  public void logAfter(JoinPoint joinPoint) {
    String targetClassName = joinPoint.getTarget().getClass().getName();
    Logger logger = LoggerFactory.getLogger(targetClassName);
    logger.trace("Exiting: {}:{}", targetClassName, joinPoint.getSignature().getName());
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
