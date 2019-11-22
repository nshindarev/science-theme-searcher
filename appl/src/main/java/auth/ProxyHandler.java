package auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class ProxyHandler {
    /*
        available proxy server list:
        -  139.59.73.89:80  - not available
        -  123.1.150.244:80
        -  117.102.88.121:80
        -  14.98.64.110:80
        -  14.0.92.119:80
        -  124.41.213.33:80

        -  212.232.75.195:53531
     */

    private static final Logger logger = LoggerFactory.getLogger(ProxyHandler.class);

    public static void setNewProxy(){
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6, "139.59.73.89", 80);

        webClient.getProxyConfig().toString();
    }
    public static String getNewProxy() {
        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet("https://api.getproxylist.com/proxy");
            HttpResponse response = client.execute(request);

            String json = EntityUtils.toString(response.getEntity());
            ProxyData proxy = new ObjectMapper().readValue(json, ProxyData.class);

            return response.toString();
        }
        catch (ClientProtocolException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return new String();
     }
}
