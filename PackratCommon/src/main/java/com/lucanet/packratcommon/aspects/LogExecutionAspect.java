package com.lucanet.packratcommon.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
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
    logger.trace("Entering {}:{}", targetClassName, joinPoint.getSignature().getName());
  }

  /**
   * Log an exit out of the annotated function for with a return value for trace reporting purposes.
   * @param joinPoint The function that the aspect will log as exiting.
   * @param returnValue The value that the function returned.
   */
  @AfterReturning(
      value = "@annotation(LogExecution)",
      returning = "returnValue"
  )
  public void logAfterReturning(JoinPoint joinPoint, Object returnValue) {
    String targetClassName = joinPoint.getTarget().getClass().getName();
    Logger logger = LoggerFactory.getLogger(targetClassName);
    logger.trace("Exiting {}:{} with a return of {} | value '{}'", targetClassName, joinPoint.getSignature().getName(), returnValue.getClass().toString(), returnValue);
  }

  /**
   * Log an exit out of the annotated function with a thrown exception for trace reporting purposes.
   * @param joinPoint The function that the aspect will log as exiting.
   * @param thrownException The exception that the function threw.
   */
  @AfterThrowing(
      value = "@annotation(LogExecution)",
      throwing = "thrownException"
  )
  public void logAfterThrowing(JoinPoint joinPoint, Throwable thrownException) {
    String targetClassName = joinPoint.getTarget().getClass().getName();
    Logger logger = LoggerFactory.getLogger(targetClassName);
    logger.trace("Exiting {}:{} throwing {}", targetClassName, joinPoint.getSignature().getName(), thrownException);
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
