package vn.baokim.b2b.mastersub;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.*;
import vn.baokim.b2b.*;

/**
 * BaokimOrder - API đơn hàng (Basic Pro)
 */
public class BaokimOrder {
    private static final String ENDPOINT_CREATE_ORDER = "/b2b/core/api/ext/mm/order/send";
    private static final String ENDPOINT_QUERY_ORDER = "/b2b/core/api/ext/mm/order/get-order";
    private static final String ENDPOINT_REFUND_ORDER = "/b2b/core/api/ext/mm/refund/send";
    private static final String ENDPOINT_CANCEL_AUTO_DEBIT = "/b2b/core/api/ext/mm/autodebit/cancel";
    
    public static final int PAYMENT_METHOD_VA = 1;
    public static final int PAYMENT_METHOD_VNPAY_QR = 6;
    public static final int PAYMENT_METHOD_AUTO_DEBIT = 22;
    
    private BaokimAuth auth;
    private HttpClient httpClient;
    private Gson gson;
    
    public BaokimOrder(BaokimAuth auth) {
        this.auth = auth;
        this.httpClient = new HttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Tạo đơn hàng mới
     */
    public ApiResponse createOrder(Map<String, Object> orderData) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId());
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("mrc_order_id", orderData.get("mrcOrderId"));
        requestBody.put("total_amount", orderData.get("totalAmount"));
        requestBody.put("description", orderData.get("description"));
        requestBody.put("url_success", orderData.containsKey("urlSuccess") ? orderData.get("urlSuccess") : Config.get("url_success"));
        requestBody.put("url_fail", orderData.containsKey("urlFail") ? orderData.get("urlFail") : Config.get("url_fail"));
        
        if (orderData.containsKey("paymentMethod")) {
            requestBody.put("payment_method", orderData.get("paymentMethod"));
        }
        if (orderData.containsKey("items")) {
            requestBody.put("items", orderData.get("items"));
        }
        if (orderData.containsKey("customerInfo")) {
            requestBody.put("customer_info", orderData.get("customerInfo"));
        }
        if (orderData.containsKey("serviceCode")) {
            requestBody.put("service_code", orderData.get("serviceCode"));
        }
        if (orderData.containsKey("saveToken")) {
            requestBody.put("save_token", orderData.get("saveToken"));
        }
        
        return sendRequest(ENDPOINT_CREATE_ORDER, requestBody);
    }
    
    /**
     * Tra cứu đơn hàng
     */
    public ApiResponse queryOrder(String mrcOrderId) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId());
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("mrc_order_id", mrcOrderId);
        
        return sendRequest(ENDPOINT_QUERY_ORDER, requestBody);
    }
    
    /**
     * Hoàn tiền
     */
    public ApiResponse refundOrder(String mrcOrderId, int amount, String description) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId());
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("mrc_order_id", mrcOrderId);
        requestBody.put("amount", amount);
        requestBody.put("description", description);
        
        return sendRequest(ENDPOINT_REFUND_ORDER, requestBody);
    }
    
    /**
     * Hủy thu hộ tự động
     */
    public ApiResponse cancelAutoDebit(String token) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId());
        requestBody.put("request_time", formatDateTime());
        requestBody.put("master_merchant_code", Config.get("master_merchant_code"));
        requestBody.put("sub_merchant_code", Config.get("sub_merchant_code"));
        requestBody.put("url_success", Config.get("url_success"));
        requestBody.put("url_fail", Config.get("url_fail"));
        requestBody.put("token", token);
        
        return sendRequest(ENDPOINT_CANCEL_AUTO_DEBIT, requestBody);
    }
    
    private ApiResponse sendRequest(String endpoint, Map<String, Object> requestBody) throws Exception {
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
        
        return new ApiResponse(ErrorCode.isSuccess(code), code, message, responseData, data);
    }
    
    /**
     * Tạo request ID duy nhất
     * Note: Baokim dùng merchant_code trong request_id để thống kê và gửi thông báo cập nhật SDK.
     * Vui lòng giữ nguyên format này.
     */
    private String generateRequestId() {
        return Config.get("sub_merchant_code") + "_" + formatDateTime().replaceAll("[- :]", "") + "_" + 
               Long.toHexString(System.currentTimeMillis()).substring(4);
    }
    
    private String formatDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(new Date());
    }
    
    public static Map<String, Object> buildCustomerInfo(String name, String email, String phone, String address) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("name", name);
        info.put("email", email);
        info.put("phone", phone);
        info.put("address", address);
        info.put("gender", 1);
        return info;
    }
    
    /**
     * API Response wrapper
     */
    public static class ApiResponse {
        public boolean success;
        public int code;
        public String message;
        public JsonObject data;
        public JsonObject rawResponse;
        
        public ApiResponse(boolean success, int code, String message, JsonObject data, JsonObject rawResponse) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.data = data;
            this.rawResponse = rawResponse;
        }
    }
}
