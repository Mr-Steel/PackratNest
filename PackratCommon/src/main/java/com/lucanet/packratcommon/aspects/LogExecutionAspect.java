package com.lucanet.packratcommon.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionAspect {
  // =========================== Class Variables ===========================79
  // ============================ Class Methods ============================79
  // ============================   Variables    ===========================79
  // ============================  Constructors  ===========================79
  public LogExecutionAspect() {
  }

  // ============================ Public Methods ===========================79
  @Before("@annotation(LogExecution)")
  public void logBefore(JoinPoint joinPoint) {
    String targetClassName = joinPoint.getTarget().getClass().getName();
    Logger logger = LoggerFactory.getLogger(targetClassName);
    logger.trace("Entering: {}:{}", targetClassName, joinPoint.getSignature().getName());
  }

  @After("@annotation(LogExecution)")
  public void logAfter(JoinPoint joinPoint) {
    String targetClassName = joinPoint.getTarget().getClass().getName();
    Logger logger = LoggerFactory.getLogger(targetClassName);
    logger.trace("Exiting: {}:{}", targetClassName, joinPoint.getSignature().getName());
  }

  // ========================== Protected Methods ==========================79
  // =========================== Private Methods ===========================79
}
