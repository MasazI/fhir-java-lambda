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

import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.lang.Long;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.http.HttpResponse;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.strategy.sampling.NoSamplingStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class InvokeTest {
  private static final Logger logger = LoggerFactory.getLogger(InvokeTest.class);
  private static String ENV_API_END_POINT = "ENV_API_END_POINT";
  private Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public InvokeTest() {
    AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();
    builder.withSamplingStrategy(new NoSamplingStrategy());
    AWSXRay.setGlobalRecorder(builder.build());
  }

  @Test
  void invokeTransformTest() throws IOException {
        AWSXRay.beginSegment("fhir-java-lambda-transform-test");

        Patient pat = null;
        Observation[] obxs = null;
        
        try{
          InputStream is = new FileInputStream("test/test.txt");
          V2MessageConverter conv = new V2MessageConverter(is);
    
          pat = conv.getPatient();
          String fhirPatient = gson.toJson(pat);
          
          System.out.println(fhirPatient);
          
          obxs = conv.getObservations();
          for (Observation obx: obxs){
            System.out.println(gson.toJson(obx));
          }
          
        } catch (Exception e) {
          System.err.println(e.getMessage());
          System.exit(1);
        }
    
        AWSXRay.endSegment();
  }

  @Test
  void invokeAuthTest() throws IOException {
    AWSXRay.beginSegment("fhir-java-lambda-auth-test");
    
    CognitoAuth auth = new CognitoAuth();
    String token = auth.sightIn();
    System.out.println("Access Token: " + token);
    
    AWSXRay.endSegment();
  }
  
  @Test
  void invokePostTest() throws IOException {
        AWSXRay.beginSegment("fhir-java-lambda-post-test");
        // api gateway client
        ApiGatewayClient client = new ApiGatewayClient();
        String baseurl = System.getenv(ENV_API_END_POINT);
        
        // get token
        CognitoAuth auth = new CognitoAuth();
        String token = auth.sightIn();
        System.out.println("Access Token: " + token);
    
        // Transform data
        Patient pat = null;
        Observation[] obxs = null;
        InputStream is = new FileInputStream("test/test.txt");
        V2MessageConverter conv = new V2MessageConverter(is);
  
        // Post Patient Test
        pat = conv.getPatient();
        String path_patient = "/Patient";
        String fhirPatient = gson.toJson(pat);
        String patient_id = null;
        String output_patient = null;
        try{
          HttpResponse<String> res = client.post(baseurl, path_patient, token, fhirPatient);
          output_patient = res.body();
          Gson gson = new Gson();
          Map<String, Object> map = new HashMap<String, Object>();
          map = (Map<String, Object>)gson.fromJson(output_patient, map.getClass());
          System.out.println("Patient id: " + map.get("id").toString());
          patient_id = map.get("id").toString();
        }catch (Exception e) {
          e.printStackTrace();
        }
        conv.setSubject(patient_id);
        
        // Post Observation Test  
        obxs = conv.getObservations();
        String path_observation = "/Observation";
        List<SimpleEntry<String, String>> observation_list = new ArrayList<>();
        for (Observation obx: obxs){
          try{
            HttpResponse<String> res = client.post(baseurl, path_observation, token, gson.toJson(obx));
            String body = res.body();
            Map<String, Object> map = new HashMap<String, Object>();
            map = (Map<String, Object>)gson.fromJson(body, map.getClass());
            System.out.println("Observation id: " + map.get("id").toString());
            observation_list.add(new SimpleEntry<>(map.get("id").toString(), body));
          }catch (Exception e) {
            e.printStackTrace();
          }
        }
        
        AWSXRay.endSegment();
  }
}
