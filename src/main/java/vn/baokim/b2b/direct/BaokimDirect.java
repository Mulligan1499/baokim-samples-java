package vn.baokim.b2b.direct;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import vn.baokim.b2b.*;
import vn.baokim.b2b.dto.DirectCreateOrderRequest;
import vn.baokim.b2b.dto.CustomerInfo;
import vn.baokim.b2b.mastersub.BaokimOrder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * BaokimDirect - API Direct Connection (không qua Master Merchant)
 */
public class BaokimDirect {
    private static final String ENDPOINT_CREATE_ORDER = "/b2b/core/api/ext/order/send";
    private static final String ENDPOINT_QUERY_ORDER = "/b2b/core/api/ext/order/get-order";
    private static final String ENDPOINT_CANCEL_ORDER = "/b2b/core/api/ext/order/cancel";
    
    // Payment methods
    public static final int PAYMENT_METHOD_VA = 1;
    public static final int PAYMENT_METHOD_BNPL = 2;
    public static final int PAYMENT_METHOD_CREDIT_CARD = 3;
    public static final int PAYMENT_METHOD_ATM = 4;
    public static final int PAYMENT_METHOD_QR_PAY = 6;
    
    private String token;
    private HttpClient httpClient;
    private Gson gson;
    
    public BaokimDirect(String token) {
        this.token = token;
        this.httpClient = new HttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Tạo đơn hàng Direct
     * 
     * @param request DTO chứa thông tin đơn hàng
     */
    public BaokimOrder.ApiResponse createOrder(DirectCreateOrderRequest request) throws Exception {
        // Validate required fields
        if (request.getMrcOrderId() == null || request.getMrcOrderId().isEmpty()) {
            throw new Exception("Missing required field: mrc_order_id");
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new Exception("Missing required field: description");
        }
        
        // Chuẩn bị request body
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", String.valueOf(System.currentTimeMillis()) + new Random().nextInt(1000));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", Config.get("direct_merchant_code", Config.get("merchant_code")));
        requestBody.put("mrc_order_id", request.getMrcOrderId());
        requestBody.put("description", request.getDescription());
        requestBody.put("total_amount", request.getTotalAmount());
        requestBody.put("url_success", request.getUrlSuccess() != null ? request.getUrlSuccess() : Config.get("url_success"));
        requestBody.put("url_fail", request.getUrlFail() != null ? request.getUrlFail() : Config.get("url_fail"));
        
        // Thêm optional fields CHỈ KHI có giá trị (không empty)
        // QUAN TRỌNG: Empty strings gây lỗi signature!
        addIfNotEmpty(requestBody, "store_code", request.getStoreCode());
        addIfNotEmpty(requestBody, "branch_code", request.getBranchCode());
        addIfNotEmpty(requestBody, "staff_code", request.getStaffCode());
        
        // Items
        if (request.getItems() != null) {
            requestBody.put("items", request.getItems());
        }
        
        // Customer info
        CustomerInfo custInfo = request.getCustomerInfo();
        if (custInfo != null) {
            Map<String, Object> customer = new LinkedHashMap<String, Object>();
            customer.put("name", custInfo.getName() != null ? custInfo.getName() : "NGUYEN VAN A");
            customer.put("email", custInfo.getEmail() != null ? custInfo.getEmail() : "test@example.com");
            customer.put("phone", custInfo.getPhone() != null ? custInfo.getPhone() : "0901234567");
            customer.put("address", custInfo.getAddress() != null ? custInfo.getAddress() : "123 Test");
            customer.put("gender", custInfo.getGender() != null ? custInfo.getGender() : 1);
            if (custInfo.getCode() != null) {
                customer.put("code", custInfo.getCode());
            }
            requestBody.put("customer_info", customer);
        }
        
        // Payment method
        if (request.getPaymentMethod() != null) {
            requestBody.put("payment_method", request.getPaymentMethod());
        }
        
        return sendRequest(ENDPOINT_CREATE_ORDER, requestBody);
    }
    
    /**
     * Tra cứu đơn hàng
     */
    public BaokimOrder.ApiResponse queryOrder(String mrcOrderId) throws Exception {
        if (mrcOrderId == null || mrcOrderId.isEmpty()) {
            throw new Exception("Missing required field: mrc_order_id");
        }
        
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("QRY"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", Config.get("direct_merchant_code", Config.get("merchant_code")));
        requestBody.put("mrc_order_id", mrcOrderId);
        
        return sendRequest(ENDPOINT_QUERY_ORDER, requestBody);
    }
    
    /**
     * Hủy đơn hàng
     */
    public BaokimOrder.ApiResponse cancelOrder(String mrcOrderId, String reason) throws Exception {
        if (mrcOrderId == null || mrcOrderId.isEmpty()) {
            throw new Exception("Missing required field: mrc_order_id");
        }
        
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("CANCEL"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", Config.get("direct_merchant_code", Config.get("merchant_code")));
        requestBody.put("mrc_order_id", mrcOrderId);
        
        // Chỉ thêm reason nếu có giá trị
        if (reason != null && !reason.isEmpty()) {
            requestBody.put("reason", reason);
        }
        
        return sendRequest(ENDPOINT_CANCEL_ORDER, requestBody);
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
        JsonObject responseData = (data.has("data") && !data.get("data").isJsonNull()) ? data.getAsJsonObject("data") : null;
        
        return new BaokimOrder.ApiResponse(ErrorCode.isSuccess(code), code, message, responseData, data);
    }
    
    private void addIfNotEmpty(Map<String, Object> map, String key, Object value) {
        if (value != null && !value.toString().isEmpty()) {
            map.put(key, value);
        }
    }
    
    /**
     * Tạo request ID duy nhất
     * Note: Baokim dùng merchant_code trong request_id để thống kê và gửi thông báo cập nhật SDK.
     * Vui lòng giữ nguyên format này.
     */
    private String generateRequestId(String prefix) {
        String merchantCode = Config.get("direct_merchant_code", Config.get("merchant_code"));
        return merchantCode + "_" + prefix + "_" + formatDateTime().replaceAll("[- :]", "") + "_" + 
               Long.toHexString(System.currentTimeMillis()).substring(4);
    }
    
    private String formatDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(new Date());
    }
}
