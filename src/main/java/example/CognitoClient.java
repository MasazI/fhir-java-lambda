package example;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class CognitoClient {
    private static final Logger logger = LoggerFactory.getLogger(CognitoClient.class);
    
    private static String ENV_USERNAME = "ENV_USERNAME";
    private static String ENV_PASSWORD = "ENV_PASSWORD";
    private static String ENV_ACCESS_KEY = "ENV_ACCESS_KEY";
    private static String ENV_SECRET_KEY = "ENV_SECRET_KEY";
    private static String ENV_USER_POOL = "ENV_USER_POOL";
    private static String ENV_CLIENT_ID = "ENV_CLIENT_ID";

    public String sightIn() {
        AWSCredentials credentials = new BasicAWSCredentials(System.getenv(ENV_ACCESS_KEY), System.getenv(ENV_SECRET_KEY));
        AWSCognitoIdentityProvider client = AWSCognitoIdentityProviderClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(Regions.US_WEST_2)
        .build();
        
        Map<String, String> authParameters = new HashMap<>();
        authParameters.put("USERNAME", System.getenv(ENV_USERNAME));
        authParameters.put("PASSWORD", System.getenv(ENV_PASSWORD));
        
        AdminInitiateAuthRequest request = new AdminInitiateAuthRequest();
        
        request
            .withAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
            .withUserPoolId(System.getenv(ENV_USER_POOL))
            .withClientId(System.getenv(ENV_CLIENT_ID))
            .withAuthParameters(authParameters);
        
        AdminInitiateAuthResult response = client.adminInitiateAuth(request);
        String accessToken = response.getAuthenticationResult().getAccessToken();
        return accessToken;
    }
}