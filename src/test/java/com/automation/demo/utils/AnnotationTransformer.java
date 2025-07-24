package com.automation.demo.utils;


import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger


public class AnnotationTransformer implements IAnnotationTransformer {

    private static final Logger logger = LoggerUtil.getLogger(AnnotationTransformer.class); // Initialize logger

    @Override
  public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Add null checks for both testMethod and testClass before using them
        if (testMethod != null) {
            String className = (testClass != null) ? testClass.getName() : "UnknownClass";
            logger.info("AnnotationTransformer: Processing method: " + testMethod.getName() + " in class: " + className);

            // CRITICAL CHANGE: Always set the custom retry analyzer,
            // overriding any existing one (like DisabledRetryAnalyzer)
            annotation.setRetryAnalyzer(TestRetryAnalyzer.class);
            logger.info("AnnotationTransformer: Successfully applied TestRetryAnalyzer to: " + testMethod.getName());
        }
        else {
            logger.warn("AnnotationTransformer: testMethod is null, skipping transformation.");
        }   
    }
}

