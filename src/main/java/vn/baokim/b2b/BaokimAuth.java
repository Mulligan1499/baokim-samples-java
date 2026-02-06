package vn.baokim.b2b;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;

/**
 * BaokimAuth - Xác thực OAuth2
 */
public class BaokimAuth {
    private static final String ENDPOINT_GET_TOKEN = "/b2b/auth-service/api/oauth/get-token";
    
    private HttpClient httpClient;
    private Gson gson;
    private String token;
    private long tokenExpiresAt;
    
    // Cho phép override credentials cho Direct connection
    private String merchantCode;
    private String clientId;
    private String clientSecret;
    
    public BaokimAuth() {
        this(null, null, null);
    }
    
    public BaokimAuth(String merchantCode, String clientId, String clientSecret) {
        this.httpClient = new HttpClient();
        this.gson = new Gson();
        this.token = null;
        this.tokenExpiresAt = 0;
        
        // Use provided credentials or defaults from config
        this.merchantCode = merchantCode != null ? merchantCode : Config.get("master_merchant_code");
        this.clientId = clientId != null ? clientId : Config.get("client_id");
        this.clientSecret = clientSecret != null ? clientSecret : Config.get("client_secret");
    }
    
    /**
     * Factory method cho Direct connection
     */
    public static BaokimAuth forDirectConnection() {
        String directMerchantCode = Config.get("direct_merchant_code", Config.get("merchant_code"));
        String directClientId = Config.get("direct_client_id", Config.get("client_id"));
        String directClientSecret = Config.get("direct_client_secret", Config.get("client_secret"));
        return new BaokimAuth(directMerchantCode, directClientId, directClientSecret);
    }
    
    /**
     * Lấy access token (có cache)
     */
    public String getToken() throws Exception {
        return getToken(false);
    }
    
    public String getToken(boolean forceRefresh) throws Exception {
        // Check cache
        if (!forceRefresh && isTokenValid()) {
            return token;
        }
        
        // Build request
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("merchant_code", merchantCode);
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        
        String jsonBody = gson.toJson(requestBody);
        String signature = SignatureHelper.sign(jsonBody);
        
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Signature", signature);
        
        HttpClient.HttpResponse response = httpClient.post(ENDPOINT_GET_TOKEN, jsonBody, headers);
        
        if (!response.success) {
            throw new Exception("Failed to get token: HTTP " + response.httpCode);
        }
        
        JsonObject data = gson.fromJson(response.body, JsonObject.class);
        int code = data.get("code").getAsInt();
        
        if (code != 100 || !data.has("data")) {
            String message = data.has("message") ? data.get("message").getAsString() : "Unknown error";
            throw new Exception("Token API error: " + message);
        }
        
        JsonObject tokenData = data.getAsJsonObject("data");
        token = tokenData.get("access_token").getAsString();
        
        // Parse expires_at
        if (tokenData.has("expires_at")) {
            // Simple parse - just add 1 hour from now
            tokenExpiresAt = System.currentTimeMillis() + 3600000;
        }
        
        return token;
    }
    
    /**
     * Kiểm tra token còn hiệu lực
     */
    public boolean isTokenValid() {
        if (token == null || tokenExpiresAt == 0) {
            return false;
        }
        // Buffer 60 seconds
        return System.currentTimeMillis() < (tokenExpiresAt - 60000);
    }
    
    /**
     * Lấy Authorization header
     */
    public String getAuthorizationHeader() throws Exception {
        return "Bearer " + getToken();
    }
}

