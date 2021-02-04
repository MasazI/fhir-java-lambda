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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.io.FileNotFoundException;
//import java.io.IOException;
import java.io.InputStream;

import example.V2MessageConverter;
import example.pojo.patient.Patient;
import example.pojo.observation.Observation;


//TODO implementation for s3 event
public class HandlerS3 implements RequestHandler<S3Event, String> {
  private static String ENV_API_END_POINT = "ENV_API_END_POINT";
  
  Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  //TODO put it into API Gateway (rest)
  CognitoAuth auth = new CognitoAuth();
  
  private static final Logger logger = LoggerFactory.getLogger(HandlerS3.class);
  @Override
  public String handleRequest(S3Event s3event, Context context) {
    logger.info("EVENT: " + gson.toJson(s3event));
    S3EventNotificationRecord record = s3event.getRecords().get(0);
    
    String srcBucket = record.getS3().getBucket().getName();
    logger.info("Source bucket: " + srcBucket);
    // Object key may have spaces or unicode non-ASCII characters.
    String srcKey = record.getS3().getObject().getUrlDecodedKey();
    logger.info("Source key: " + srcKey);
    
    //TODO put it into s3 destination
     // api gateway client
    ApiGatewayClient client = new ApiGatewayClient();
    String baseurl = System.getenv(ENV_API_END_POINT);
    
    // get token
    CognitoAuth auth = new CognitoAuth();
    String token = auth.sightIn();
    logger.info("Access Token: " + token);

    // Transform data
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

    // Post Patient Test
    pat = conv.getPatient();
    String path_patient = "/Patient";
    String fhirPatient = gson.toJson(pat);
    logger.info("put Patient object");
    try{
      client.post(baseurl, path_patient, token, fhirPatient);
    }catch (Exception e) {
      e.printStackTrace();
    }
    
    // Set Subject for Observation
    conv.setSubject("AAA");
    
    // Post Observation Test  
    obxs = conv.getObservations();
    String path_observation = "/Observation";
    logger.info("put Observation object");
    try{
      for (Observation obx: obxs){
        client.post(baseurl, path_observation, token, gson.toJson(obx));
      }
    }catch (Exception e) {
        e.printStackTrace();
    }
    
    return null;
  }
}

// TODO need to add test for HandlerS3