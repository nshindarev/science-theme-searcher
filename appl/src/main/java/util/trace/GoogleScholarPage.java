package util.trace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GoogleScholarPage {
    private static List<String> googleScholarCookies = new ArrayList<String>();

    public void getCookies(String fullyQualifiedURL){
        try {
            // Create a URLConnection object for a URL
            URL url = new URL(fullyQualifiedURL);
            URLConnection conn = url.openConnection();

            // Get all cookies from the server.
            // Note: The first call to getHeaderFieldKey() will implicit send
            // the HTTP request to the server.
            for (int i=0; ; i++) {
                String headerName = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);

                if (headerName == null && headerValue == null) {
                    // No more headers
                    break;
                }

                if ("Set-Cookie".equalsIgnoreCase(headerName)) {
                    // Parse cookie.
                    //TODO: Splits the chain at every semicolon (;) followed by a tab, space and so on..
                    String[] fields = headerValue.split(";\\s*");

                    String cookieValue = fields[0];
                    String expires = null;
                    String path = null;
                    String domain = null;
                    boolean secure = false;

                    // Parse each field
                    for (int j=1; j<fields.length; j++) {
                        if ("secure".equalsIgnoreCase(fields[j])) {
                            secure = true;
                        } else if (fields[j].indexOf('=') > 0) {
                            String[] f = fields[j].split("=");
                            if ("expires".equalsIgnoreCase(f[0])) {
                                expires = f[1];
                            } else if ("domain".equalsIgnoreCase(f[0])) {
                                domain = f[1];
                            } else if ("path".equalsIgnoreCase(f[0])) {
                                path = f[1];
                            }
                        }
                    }
                    // Save the cookie..
                    googleScholarCookies.add(cookieValue);
                }
            }
            getServerResponse(conn);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }
    //Only prints the cookies values in the console.
    private void printCookiesValues(){
        for (int i = 0; i < googleScholarCookies.size(); i++){
            System.out.println(googleScholarCookies.get(i));
        }
    }


    private String getcookiesValues() {
        String cookiesValues = null;
        for (int i = 0; i < googleScholarCookies.size(); i++){
            if (i == 0) {
                cookiesValues = googleScholarCookies.get(i);
            } else {
                cookiesValues += "; " + googleScholarCookies.get(i);
            }
        }
        return cookiesValues;
    }

    private void sendCookiesGET(String fullyQualifiedURL){
        try {

            // Create a URLConnection object for a URL
            URL url = new URL(fullyQualifiedURL + "/scholar?q=Albert+Einstein");

            // Set the cookie value to send
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Cookie", getcookiesValues());

            //Test based on text book. Doesn't work.
            getHTTPResponseMessage(conn);

            StringBuffer sb = new StringBuffer();
            String line;
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null){
                    sb.append(line);
                }
                rd.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //getServerResponse(conn);

            String result = sb.toString();
            System.out.println(result);


        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }
    private void getHTTPResponseMessage(URLConnection conn) throws IOException {
        HttpURLConnection httpconnection = (HttpURLConnection) conn;

        int code = httpconnection.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK){
            String message = httpconnection.getResponseMessage();
            System.out.println(code + " " + message);
        }
    }

    //Doesn't work. Tried to use it instead of using GET. Also tried to encode the
    //values. Something is wrong and I don't know enough to fix it.
    private void sendCookiesPOST(String fullyQualifiedURL){
        try {

            // Create a URLConnection object for a URL
            URL url = new URL(fullyQualifiedURL + "/scholar?q=Albert+Einstein");

            // Set the cookie value to send
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Cookie", getcookiesValues());

            //Test based on text book. Doesn't work.
            String encodedParams = URLEncoder.encode("Albert Einstein","UTF-8");
            Integer encodedParamsLengh = encodedParams.length();
            conn.setRequestProperty("Content-Length", encodedParamsLengh.toString());

            conn.setDoOutput(true);

            getHTTPResponseMessage(conn);

            OutputStream out = conn.getOutputStream();
            PrintWriter writer = new PrintWriter(out);
            writer.print("?q=" + encodedParams);
            writer.close();
            // End test

            // Send the request to the server
            conn.connect();


            StringBuffer sb = new StringBuffer();
            String line;
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null){
                    sb.append(line);
                }
                rd.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            getServerResponse(conn);

            String result = sb.toString();
            System.out.println(result);


        } catch (MalformedURLException e) {
        } catch (IOException e) { }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GoogleScholarPage googleScholarPage = new GoogleScholarPage();
        String fullyQualifiedURL = "http://scholar.google.com";
        googleScholarPage.getCookies(fullyQualifiedURL);
        //gC.printCookiesValues();
        googleScholarPage.sendCookiesGET(fullyQualifiedURL);
    }

    public void getServerResponse(URLConnection conn) {
        try {
            //Create a URLConnection object for a URL
            //URL url = new URL("http://hostname:80");
            //URLConnection conn = url.openConnection();

            // List all the response headers from the server.
            // Note: The first call to getHeaderFieldKey() will implicit send
            // the HTTP request to the server.
            for (int i=0; ; i++) {
                String headerName = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);

                if (headerName == null && headerValue == null) {
                    // No more headers
                    break;
                }
                if (headerName == null) {
                    // The header value contains the server's HTTP version
                }
                System.out.println("Header name: " + headerName + "Header value:" + headerValue);
            }
        } catch (Exception e) {
        }
    }
}