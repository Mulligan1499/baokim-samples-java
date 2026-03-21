package vn.baokim.b2b.hosttohost;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.*;
import vn.baokim.b2b.*;
import vn.baokim.b2b.dto.CreateVARequest;
import vn.baokim.b2b.mastersub.BaokimOrder;

/**
 * BaokimVA - API Virtual Account (Host to Host)
 */
public class BaokimVA {
    private static final String ENDPOINT_CREATE_VA = "/b2b/core/api/ext/mm/bank-transfer/create";
    private static final String ENDPOINT_UPDATE_VA = "/b2b/core/api/ext/mm/bank-transfer/update";
    private static final String ENDPOINT_QUERY_TRANSACTION = "/b2b/core/api/ext/mm/bank-transfer/detail";
    
    public static final int VA_TYPE_DYNAMIC = 1;
    public static final int VA_TYPE_STATIC = 2;
    
    private String token;
    private HttpClient httpClient;
    private Gson gson;
    
    public BaokimVA(String token) {
        this.token = token;
        this.httpClient = new HttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Tạo Dynamic VA
     * 
     * @param request DTO chứa thông tin VA (accType sẽ tự động set = DYNAMIC)
     */
    public BaokimOrder.ApiResponse createDynamicVA(CreateVARequest request) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("acc_name", request.getAccName());
        requestBody.put("acc_type", VA_TYPE_DYNAMIC);
        requestBody.put("mrc_order_id", request.getMrcOrderId());
        requestBody.put("collect_amount_min", request.getCollectAmountMin());
        requestBody.put("collect_amount_max", request.getCollectAmountMax());
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            requestBody.put("description", request.getDescription());
        }
        
        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }
    
    /**
     * Tạo Static VA
     * 
     * @param request DTO chứa thông tin VA (accType sẽ tự động set = STATIC)
     */
    public BaokimOrder.ApiResponse createStaticVA(CreateVARequest request) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("acc_name", request.getAccName());
        requestBody.put("acc_type", VA_TYPE_STATIC);
        requestBody.put("mrc_order_id", request.getMrcOrderId());
        requestBody.put("expire_date", request.getExpireDate());
        requestBody.put("collect_amount_min", request.getCollectAmountMin());
        requestBody.put("collect_amount_max", request.getCollectAmountMax());
        
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
        headers.put("Authorization", "Bearer " + this.token);
        headers.put("Signature", signature);
        
        HttpClient.HttpResponse response = httpClient.post(endpoint, jsonBody, headers);
        
        JsonObject data = gson.fromJson(response.body, JsonObject.class);
        int code = data.has("code") ? data.get("code").getAsInt() : -1;
        String message = data.has("message") ? data.get("message").getAsString() : "";
        JsonObject responseData = data.has("data") ? data.getAsJsonObject("data") : null;
        
        return new BaokimOrder.ApiResponse(ErrorCode.isSuccess(code), code, message, responseData, data);
    }
    
    /**
     * Tạo request ID duy nhất
     * Note: Baokim dùng merchant_code trong request_id để thống kê và gửi thông báo cập nhật SDK.
     * Vui lòng giữ nguyên format này.
     */
    private String generateRequestId(String prefix) {
        return Config.get("sub_merchant_code") + "_" + prefix + "_" + formatDateTime().replaceAll("[- :]", "") + "_" + 
               Long.toHexString(System.currentTimeMillis()).substring(4);
    }
    
    private String formatDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(new Date());
    }
}
