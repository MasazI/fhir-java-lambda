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
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import example.pojo.patient.Patient;
import example.pojo.observation.Observation;

//TODO implementation for s3 event
public class HandlerS3 implements RequestHandler<S3Event, String> {
  Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
    
    //TODO get object and transform to new format
    Patient pat = new Patient();
    
    //TODO put it into API Gateway (rest)
    String fhirPatient = gson.toJson(pat);
    
    //TODO put it into s3 destination
    
    return null;
  }
}

// TODO need to add test for HandlerS3