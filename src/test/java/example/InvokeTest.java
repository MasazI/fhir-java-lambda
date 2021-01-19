package example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.RequestParametersEntity;
import com.amazonaws.services.s3.event.S3EventNotification.ResponseElementsEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.UserIdentityEntity;
import com.amazonaws.services.s3.event.S3EventNotification.GlacierEventDataEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.event.S3EventNotification.UserIdentityEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.lang.Long;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.strategy.sampling.NoSamplingStrategy;

class InvokeTest {
  private static final Logger logger = LoggerFactory.getLogger(InvokeTest.class);

  public InvokeTest() {
    AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();
    builder.withSamplingStrategy(new NoSamplingStrategy());
    AWSXRay.setGlobalRecorder(builder.build());
  }

  @Test
  void invokeTest() throws IOException {
    AWSXRay.beginSegment("fhir-java-lambda-test");
    
    CognitoAuth auth = new CognitoAuth();
    
    auth.sightIn();
    
    // assertTrue(result.contains("Ok"));
    AWSXRay.endSegment();
  }

}
