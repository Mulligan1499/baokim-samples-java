package vn.baokim.b2b;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * HttpClient - HTTP Client với logging
 */
public class HttpClient {
    private String baseUrl;
    private int timeout;
    
    public HttpClient() {
        this.baseUrl = Config.get("base_url", "https://devtest.baokim.vn");
        this.timeout = Config.getInt("timeout", 30000);
    }
    
    /**
     * Gửi POST request
     */
    public HttpResponse post(String endpoint, String jsonBody, Map<String, String> headers) throws Exception {
        return request("POST", endpoint, jsonBody, headers);
    }
    
    /**
     * Gửi GET request
     */
    public HttpResponse get(String endpoint, Map<String, String> headers) throws Exception {
        return request("GET", endpoint, null, headers);
    }
    
    /**
     * Gửi request chung
     */
    public HttpResponse request(String method, String endpoint, String body, Map<String, String> headers) throws Exception {
        String urlStr = buildUrl(endpoint);
        
        // Log request
        Logger.logRequest(method, urlStr, headers != null ? headers.toString() : "", body);
        
        long startTime = System.currentTimeMillis();
        
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        
        // Set headers
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        
        // Write body
        if (body != null && !body.isEmpty()) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();
            os.close();
        }
        
        // Read response
        int httpCode = conn.getResponseCode();
        BufferedReader reader;
        if (httpCode >= 200 && httpCode < 400) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        long duration = System.currentTimeMillis() - startTime;
        String responseBody = response.toString();
        
        // Log response
        Logger.logResponse(httpCode, responseBody, duration);
        
        return new HttpResponse(httpCode >= 200 && httpCode < 400, httpCode, responseBody);
    }
    
    private String buildUrl(String endpoint) {
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            return endpoint;
        }
        return baseUrl + endpoint;
    }
    
    /**
     * HTTP Response wrapper
     */
    public static class HttpResponse {
        public boolean success;
        public int httpCode;
        public String body;
        
        public HttpResponse(boolean success, int httpCode, String body) {
            this.success = success;
            this.httpCode = httpCode;
            this.body = body;
        }
    }
}
