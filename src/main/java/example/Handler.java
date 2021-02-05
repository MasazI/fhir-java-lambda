package example;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.strategy.sampling.NoSamplingStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.http.HttpResponse;

import example.V2MessageConverter;
import example.pojo.patient.Patient;
import example.pojo.observation.Observation;



//TODO implementation for s3 event
public class Handler implements RequestHandler<S3Event, String> {
  private static String ENV_API_END_POINT = "ENV_API_END_POINT";
  
  Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  //TODO put it into API Gateway (rest)
  CognitoClient auth = new CognitoClient();

  // X-ray  
  AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();

  private static final Logger logger = LoggerFactory.getLogger(Handler.class);
  @Override
  public String handleRequest(S3Event s3event, Context context) {
    builder.withSamplingStrategy(new NoSamplingStrategy());
    AWSXRay.setGlobalRecorder(builder.build());
    AWSXRay.beginSegment("fhir-java-lambda-get-origin");

    logger.info("EVENT: " + gson.toJson(s3event));
    S3EventNotificationRecord record = s3event.getRecords().get(0);
    
    String srcBucket = record.getS3().getBucket().getName();
    logger.info("Source bucket: " + srcBucket);
    // Object key may have spaces or unicode non-ASCII characters.
    String srcKey = record.getS3().getObject().getUrlDecodedKey();
    logger.info("Source key: " + srcKey);
    AWSXRay.endSegment();
    
    // api gateway client
    AWSXRay.beginSegment("fhir-java-lambda-get-token");
    ApiGatewayClient client = new ApiGatewayClient();
    String baseurl = System.getenv(ENV_API_END_POINT);
    
    // get token
    CognitoClient auth = new CognitoClient();
    String token = auth.sightIn();
    logger.info("Access Token: " + token);
    AWSXRay.endSegment();

    // Transform data
    AWSXRay.beginSegment("fhir-java-lambda-transform-data");
    Patient pat = null;
    Observation[] obxs = null;
    S3Client s3client = new S3Client();
    S3Object originObject = null;
    logger.info("getting object from S3");
    try{
      originObject = s3client.get(srcBucket, srcKey);
    }catch(Exception e){
      e.printStackTrace();
    }
    logger.info("transforming object");
    V2MessageConverter conv = new V2MessageConverter((InputStream)originObject.getObjectContent());
    AWSXRay.endSegment();

    // Post Patient Test
    AWSXRay.beginSegment("fhir-java-lambda-post-fhir-on-aws");
    pat = conv.getPatient();
    String path_patient = "/Patient";
    String fhirPatient = gson.toJson(pat);
    logger.info("put Patient object");
    String patient_id = null;
    String output_patient = null;
    try{
      HttpResponse<String> res = client.post(baseurl, path_patient, token, fhirPatient);
      String body = res.body();
      output_patient = body;
      Map<String, Object> map = new HashMap<String, Object>();
      map = (Map<String, Object>)gson.fromJson(body, map.getClass());
      System.out.println("Patient id: " + map.get("id").toString());
      patient_id = map.get("id").toString();
    }catch (Exception e) {
      e.printStackTrace();
    }
    
    // Set Subject for Observation
    conv.setSubject(patient_id);
    
    // Post Observation Test  
    obxs = conv.getObservations();
    String path_observation = "/Observation";
    List<SimpleEntry<String, String>> observation_list = new ArrayList<>();
    logger.info("put Observation object");
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
    
    // output to S3
    AWSXRay.beginSegment("fhir-java-lambda-put-to-s3");
    String patient_output_key = "Patient/"+patient_id;
    logger.info("put json to S3");
    System.out.println(patient_output_key);
    s3client.put(srcBucket, patient_output_key+".json", output_patient);
    for (SimpleEntry<String, String> observation_entry: observation_list) {
      String observation_key = observation_entry.getKey();
      String observation_body = observation_entry.getValue();
      System.out.println(observation_key);
      s3client.put(srcBucket, "Observation/"+observation_key+".json", observation_body);
    }
    AWSXRay.endSegment();

    return null;
  }
}