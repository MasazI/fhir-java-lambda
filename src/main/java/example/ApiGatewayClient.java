package example;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiGatewayClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayClient.class);
    
    private static String ENV_API_KEY = "ENV_API_KEY";
    
    public HttpResponse<String> get(String host, String path, String token) throws Exception {
        try {
            logger.info(System.getenv(ENV_API_KEY));
            
            HttpRequest req = HttpRequest.newBuilder(new URI("https", host, path, null))
            .setHeader(
                "x-api-key", System.getenv(ENV_API_KEY)
            ).setHeader(
                "Authorization", token
            ).GET().build();
            
            HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
            
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            int statusCode = res.statusCode();
            String body = res.body();
            System.out.println("statusCode: " + statusCode);
            System.out.println("body" + body);
            
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public HttpResponse<String> post(String host, String path, String token, String payload) throws Exception {
        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("https", host, path, null))
            .setHeader(
                "x-api-key", System.getenv(ENV_API_KEY)
            ).setHeader(
                "Content-Type", "application/json"
            ).setHeader(
                "Authorization", token
            ).POST(
                HttpRequest.BodyPublishers.ofString(payload)    
            ).build();
            
            HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
            
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            int statusCode = res.statusCode();
            String body = res.body();
            System.out.println("statusCode: " + statusCode);
            System.out.println("body" + body);
            
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}