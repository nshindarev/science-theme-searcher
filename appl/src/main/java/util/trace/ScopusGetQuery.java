package util.trace;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScopusGetQuery {

    private static final Logger logger = LoggerFactory.getLogger(ScopusGetQuery.class);
    private static final String apiKey = "bb33bc61b09bf6e301859b29b14b8a05";
    private static final String apiKey1 = "[8d7e18c7e1db78551e87a6d6cbf4bcfb]";
    private static final String apiKey2 = "[9a7035d8b0950d7e9f3aaf23489c38be]";
    private static final String apiKey3 = "9a7035d8b0950d7e9f3aaf23489c38be";

    private static final String apiLabel = "nshindarevScopusApi";

    public ScopusGetQuery(){

    }

    public static void getAuthorized(){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try{
            HttpGet getScopusData = new HttpGet("http://api.elsevier.com/authenticate?platform=SCOPUS");
//            HttpGet getScopusData = new HttpGet("http://api.elsevier.com/authenticate?platform=EMBASE");
//            HttpGet getScopusData = new HttpGet("http://api.elsevier.com/authenticate");
            getScopusData.addHeader("X-ELS-APIKey",  apiKey2);

            CloseableHttpResponse responseScopus = httpclient.execute(getScopusData);
            logger.info(responseScopus.getStatusLine().toString());
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }

    }
    public static void getDataScopus() throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try{
            HttpGet getScopusData = new HttpGet("https://api.elsevier.com/content/search/scopus");

            getScopusData.addHeader("Accept", "application/json");
            getScopusData.addHeader("Authorization", "bb33bc61b09bf6e301859b29b14b8a05");
            getScopusData.addHeader("X-ELS-APIKey",  "bb33bc61b09bf6e301859b29b14b8a05");

            CloseableHttpResponse responseScopus = httpclient.execute(getScopusData);
            logger.info(responseScopus.getStatusLine().toString());
        }
        catch (IOException ex){
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }

    }
    public static void exampleHttpClient() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet("http://httpbin.org/get");
            CloseableHttpResponse response1 = httpclient.execute(httpGet);

            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try {
                System.out.println(response1.getStatusLine());
                HttpEntity entity1 = response1.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity1);
            } finally {
                response1.close();
            }

            HttpPost httpPost = new HttpPost("http://httpbin.org/post");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response2 = httpclient.execute(httpPost);

            try {
                System.out.println(response2.getStatusLine());
                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
                response2.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
