package vn.baokim.b2b.merchanthosted;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.*;
import vn.baokim.b2b.*;
import vn.baokim.b2b.mastersub.BaokimOrder;

/**
 * BaokimMerchantVA - API Virtual Account (Merchant Hosted / Direct Connection)
 * 
 * Khác với Host-to-Host (Master/Sub), Merchant Hosted dùng merchant_code
 * thay vì master_merchant_code + sub_merchant_code.
 * 
 * Endpoints:
 * - Tạo VA: POST /b2b/core/api/merchant-hosted/bank-transfer/create
 * - Cập nhật VA: POST /b2b/core/api/merchant-hosted/bank-transfer/update
 * - Chi tiết VA: POST /b2b/core/api/merchant-hosted/bank-transfer/detail
 */
public class BaokimMerchantVA {
    private static final String ENDPOINT_CREATE_VA = "/b2b/core/api/merchant-hosted/bank-transfer/create";
    private static final String ENDPOINT_UPDATE_VA = "/b2b/core/api/merchant-hosted/bank-transfer/update";
    private static final String ENDPOINT_DETAIL_VA = "/b2b/core/api/merchant-hosted/bank-transfer/detail";
    
    public static final int VA_TYPE_DYNAMIC = 1;
    public static final int VA_TYPE_STATIC = 2;
    
    private String token;
    private HttpClient httpClient;
    private Gson gson;
    
    public BaokimMerchantVA(String token) {
        this.token = token;
        this.httpClient = new HttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Tạo Dynamic VA (Merchant Hosted)
     * 
     * @param accName Tên chủ tài khoản VA
     * @param mrcOrderId Mã đơn hàng (max 25 ký tự)
     * @param amount Số tiền cần thu (tối thiểu 2000)
     * @param memo Ghi chú (optional, max 255 ký tự)
     */
    public BaokimOrder.ApiResponse createDynamicVA(String accName, String mrcOrderId, int amount, String memo) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_name", accName);
        requestBody.put("acc_type", VA_TYPE_DYNAMIC);
        requestBody.put("mrc_order_id", mrcOrderId);
        requestBody.put("collect_amount_min", amount);
        requestBody.put("collect_amount_max", amount);
        if (memo != null && !memo.isEmpty()) {
            requestBody.put("memo", memo);
        }
        
        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }
    
    /**
     * Tạo Static VA (Merchant Hosted)
     * 
     * @param accName Tên chủ tài khoản VA
     * @param mrcOrderId Mã đơn hàng
     * @param expireDate Ngày hết hạn (yyyy-MM-dd HH:mm:ss)
     * @param collectAmountMax Số tiền thu tối đa (required, tối thiểu 2000)
     * @param collectAmountMin Số tiền thu tối thiểu (optional, tối thiểu 2000)
     */
    public BaokimOrder.ApiResponse createStaticVA(String accName, String mrcOrderId, String expireDate, 
                                                   int collectAmountMax, int collectAmountMin) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_name", accName);
        requestBody.put("acc_type", VA_TYPE_STATIC);
        requestBody.put("mrc_order_id", mrcOrderId);
        requestBody.put("expire_date", expireDate);
        requestBody.put("collect_amount_min", collectAmountMin);
        requestBody.put("collect_amount_max", collectAmountMax);
        
        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }
    
    /**
     * Tạo VA với đầy đủ tham số (Merchant Hosted)
     * 
     * @param vaData Map chứa thông tin VA:
     *   - acc_name (required), acc_type (required), mrc_order_id (required), collect_amount_max (required)
     *   - collect_amount_min, store_code, staff_code, bank_code, expire_date, memo (optional)
     */
    public BaokimOrder.ApiResponse createVA(Map<String, Object> vaData) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_name", vaData.get("acc_name"));
        requestBody.put("acc_type", vaData.get("acc_type"));
        requestBody.put("mrc_order_id", vaData.get("mrc_order_id"));
        requestBody.put("collect_amount_max", vaData.get("collect_amount_max"));
        
        // Optional fields
        addIfNotNull(requestBody, "collect_amount_min", vaData.get("collect_amount_min"));
        addIfNotEmpty(requestBody, "store_code", vaData.get("store_code"));
        addIfNotEmpty(requestBody, "staff_code", vaData.get("staff_code"));
        addIfNotEmpty(requestBody, "bank_code", vaData.get("bank_code"));
        addIfNotEmpty(requestBody, "expire_date", vaData.get("expire_date"));
        addIfNotEmpty(requestBody, "memo", vaData.get("memo"));
        
        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }
    
    /**
     * Cập nhật VA (Merchant Hosted)
     * 
     * @param mrcOrderId Mã đơn hàng (required)
     * @param updateData Dữ liệu cập nhật: acc_name, collect_amount_min, collect_amount_max, expire_date
     */
    public BaokimOrder.ApiResponse updateVA(String mrcOrderId, Map<String, Object> updateData) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA_UPD"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("mrc_order_id", mrcOrderId);
        
        // Optional update fields
        addIfNotEmpty(requestBody, "acc_name", updateData.get("acc_name"));
        addIfNotNull(requestBody, "collect_amount_min", updateData.get("collect_amount_min"));
        addIfNotNull(requestBody, "collect_amount_max", updateData.get("collect_amount_max"));
        addIfNotEmpty(requestBody, "expire_date", updateData.get("expire_date"));
        
        return sendRequest(ENDPOINT_UPDATE_VA, requestBody);
    }
    
    /**
     * Tra cứu chi tiết VA (Merchant Hosted)
     * 
     * @param accNo Số VA cần tra cứu (required)
     */
    public BaokimOrder.ApiResponse detailVA(String accNo) throws Exception {
        return detailVA(accNo, null);
    }
    
    /**
     * Tra cứu chi tiết VA với bộ lọc (Merchant Hosted)
     * 
     * @param accNo Số VA cần tra cứu (required)
     * @param queryData Map bổ sung: start_date, end_date, current_page, per_page (all optional)
     */
    public BaokimOrder.ApiResponse detailVA(String accNo, Map<String, Object> queryData) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA_DTL"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_no", accNo);
        
        if (queryData != null) {
            addIfNotEmpty(requestBody, "start_date", queryData.get("start_date"));
            addIfNotEmpty(requestBody, "end_date", queryData.get("end_date"));
            addIfNotNull(requestBody, "current_page", queryData.get("current_page"));
            addIfNotNull(requestBody, "per_page", queryData.get("per_page"));
        }
        
        return sendRequest(ENDPOINT_DETAIL_VA, requestBody);
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
    
    private String getMerchantCode() {
        String directCode = Config.get("direct_merchant_code", null);
        return (directCode != null && !directCode.isEmpty()) ? directCode : Config.get("merchant_code");
    }
    
    private void addIfNotEmpty(Map<String, Object> map, String key, Object value) {
        if (value != null && !value.toString().isEmpty()) {
            map.put(key, value);
        }
    }
    
    private void addIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
    
    /**
     * Tạo request ID duy nhất
     * Note: Baokim dùng merchant_code trong request_id để thống kê và gửi thông báo cập nhật SDK.
     * Vui lòng giữ nguyên format này.
     */
    private String generateRequestId(String prefix) {
        return getMerchantCode() + "_" + prefix + "_" + formatDateTime().replaceAll("[- :]", "") + "_" + 
               Long.toHexString(System.currentTimeMillis()).substring(4);
    }
    
    private String formatDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(new Date());
    }
}
