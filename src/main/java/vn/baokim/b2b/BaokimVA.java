package vn.baokim.b2b;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * BaokimVA - API Virtual Account (Host to Host)
 */
public class BaokimVA {
    private static final String ENDPOINT_CREATE_VA = "/b2b/core/api/ext/mm/bank-transfer/create";
    private static final String ENDPOINT_UPDATE_VA = "/b2b/core/api/ext/mm/bank-transfer/update";
    private static final String ENDPOINT_QUERY_TRANSACTION = "/b2b/core/api/ext/mm/bank-transfer/detail";
    
    public static final int VA_TYPE_DYNAMIC = 1;
    public static final int VA_TYPE_STATIC = 2;
    
    private BaokimAuth auth;
    private HttpClient httpClient;
    private Gson gson;
    
    public BaokimVA(BaokimAuth auth) {
        this.auth = auth;
        this.httpClient = new HttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Tạo Dynamic VA
     */
    public BaokimOrder.ApiResponse createDynamicVA(String accName, String mrcOrderId, int amount, String description) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("acc_name", accName);
        requestBody.put("acc_type", VA_TYPE_DYNAMIC);
        requestBody.put("mrc_order_id", mrcOrderId);
        requestBody.put("collect_amount_min", amount);
        requestBody.put("collect_amount_max", amount);
        if (description != null && !description.isEmpty()) {
            requestBody.put("description", description);
        }
        
        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }
    
    /**
     * Tạo Static VA
     */
    public BaokimOrder.ApiResponse createStaticVA(String accName, String mrcOrderId, String description) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("acc_name", accName);
        requestBody.put("acc_type", VA_TYPE_STATIC);
        requestBody.put("mrc_order_id", mrcOrderId);
        if (description != null && !description.isEmpty()) {
            requestBody.put("description", description);
        }
        
        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }
    
    /**
     * Cập nhật VA
     */
    public BaokimOrder.ApiResponse updateVA(String accNo, Map<String, Object> updateData) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("VA_UPD"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("acc_no", accNo);
        requestBody.putAll(updateData);
        
        return sendRequest(ENDPOINT_UPDATE_VA, requestBody);
    }
    
    /**
     * Tra cứu giao dịch VA
     */
    public BaokimOrder.ApiResponse queryTransaction(String accNo) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("VA_QRY"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("acc_no", accNo);
        
        return sendRequest(ENDPOINT_QUERY_TRANSACTION, requestBody);
    }
    
    private BaokimOrder.ApiResponse sendRequest(String endpoint, Map<String, Object> requestBody) throws Exception {
        String jsonBody = gson.toJson(requestBody);
        String signature = SignatureHelper.sign(jsonBody);
        
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", auth.getAuthorizationHeader());
        headers.put("Signature", signature);
        
        HttpClient.HttpResponse response = httpClient.post(endpoint, jsonBody, headers);
        
        JsonObject data = gson.fromJson(response.body, JsonObject.class);
        int code = data.has("code") ? data.get("code").getAsInt() : -1;
        String message = data.has("message") ? data.get("message").getAsString() : "";
        JsonObject responseData = data.has("data") ? data.getAsJsonObject("data") : null;
        
        return new BaokimOrder.ApiResponse(ErrorCode.isSuccess(code), code, message, responseData, data);
    }
    
    private String generateRequestId(String prefix) {
        return prefix + "_" + formatDateTime().replaceAll("[- :]", "") + "_" + 
               Long.toHexString(System.currentTimeMillis()).substring(4);
    }
    
    private String formatDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(new Date());
    }
}
