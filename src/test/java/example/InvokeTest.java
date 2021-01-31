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

    Patient pat = null;
    Observation[] obxs = null;
    String srcBucket;
    String srcKey;
    
    try{
      AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
      S3Object o = s3.getObject(srcBucket, srcKey);
      S3ObjectInputStream s3is = o.getObjectContent();
      V2MessageConverter conv = new V2MessageConverter((InputStream)s3is);

      pat = conv.getPatient();
      obxs = conv.getObservations();
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    
    String token = auth.sightIn();
    String fhirPatient = gson.toJson(pat);
    ApiGatewayClient client = ApiGatewayClient();
    client.post("https://rq4p08uyxa.execute-api.us-west-2.amazonaws.com/dev/Patient", token, fhirPatient);
    for(int i = 0: i<fhirObservations.i++){
      client.post("https://rq4p08uyxa.execute-api.us-west-2.amazonaws.com/dev/Observation", token, fhirObservations[i]);
    }
    // assertTrue(result.contains("Ok"));
    AWSXRay.endSegment();
  }

}
