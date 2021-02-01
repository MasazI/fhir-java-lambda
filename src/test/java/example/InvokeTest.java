package example;

import example.pojo.patient.Patient;
import example.pojo.observation.Observation;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class InvokeTest {
  private static final Logger logger = LoggerFactory.getLogger(InvokeTest.class);
  private static String ENV_API_END_POINT = "ENV_API_END_POINT";

  public InvokeTest() {
    AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();
    builder.withSamplingStrategy(new NoSamplingStrategy());
    AWSXRay.setGlobalRecorder(builder.build());
  }

  @Test
  void invokeTransformTest() throws IOException {
        AWSXRay.beginSegment("fhir-java-lambda-s3-test");


        AWSXRay.endSegment();
  }

  @Test
  void invokeAuthTest() throws IOException {
    AWSXRay.beginSegment("fhir-java-lambda-auth-test");
    
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
    System.out.println("Access Token: " + token);
    
    Patient pat = new Patient();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String fhirPatient = gson.toJson(pat);
    System.out.println("Patient: " + fhirPatient);
    
    ApiGatewayClient client = new ApiGatewayClient();
    String baseurl = System.getenv(ENV_API_END_POINT);
    String path_patient = "/Patient";
    try{
      client.post(baseurl, path_patient, token, fhirPatient);
    }catch (Exception e) {
      e.printStackTrace();
    }

    String path_observation = "/Observation";
    for(Observation obx: obxs){
      try{
        client.post(baseurl, path_observation, token, gson.toJson(obx));
      }catch (Exception e) {
        e.printStackTrace();
      }
    }

    AWSXRay.endSegment();
  }
  
    @Test
  void invokePostTest() throws IOException {
        AWSXRay.beginSegment("fhir-java-lambda-post-test");

        AWSXRay.endSegment();
  }

}
