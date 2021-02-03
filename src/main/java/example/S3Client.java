package example;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3Client {
    private static final Logger logger = LoggerFactory.getLogger(S3Client.class);
    private Regions clientRegion = Regions.DEFAULT_REGION;
    
    public void put(){
        //TODO put the json to S3 destination.
    }
    
    public S3Object get(String bucketName, String key) throws IOException{
        S3Object fullObject = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            logger.info("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return fullObject;
        }
    }
}