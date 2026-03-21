package vn.baokim.b2b.merchanthosted;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.*;
import vn.baokim.b2b.*;
import vn.baokim.b2b.dto.MerchantVARequest;
import vn.baokim.b2b.dto.MerchantVAUpdateRequest;
import vn.baokim.b2b.dto.VADetailRequest;
import vn.baokim.b2b.mastersub.BaokimOrder;

/**
 * BaokimMerchantVA - API Virtual Account (Merchant Hosted / Direct Connection)
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
     * @param request DTO chứa thông tin VA (accType sẽ tự động set = DYNAMIC)
     */
    public BaokimOrder.ApiResponse createDynamicVA(MerchantVARequest request) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_name", request.getAccName());
        requestBody.put("acc_type", VA_TYPE_DYNAMIC);
        requestBody.put("mrc_order_id", request.getMrcOrderId());
        requestBody.put("collect_amount_min", request.getCollectAmountMin());
        requestBody.put("collect_amount_max", request.getCollectAmountMax());
        addIfNotEmpty(requestBody, "memo", request.getMemo());

        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }

    /**
     * Tạo Static VA (Merchant Hosted)
     * 
     * @param request DTO chứa thông tin VA (accType sẽ tự động set = STATIC)
     */
    public BaokimOrder.ApiResponse createStaticVA(MerchantVARequest request) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_name", request.getAccName());
        requestBody.put("acc_type", VA_TYPE_STATIC);
        requestBody.put("mrc_order_id", request.getMrcOrderId());
        requestBody.put("expire_date", request.getExpireDate());
        requestBody.put("collect_amount_min", request.getCollectAmountMin());
        requestBody.put("collect_amount_max", request.getCollectAmountMax());

        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }

    /**
     * Tạo VA với đầy đủ tham số (Merchant Hosted)
     * 
     * @param request DTO chứa thông tin VA bao gồm cả optional fields
     */
    public BaokimOrder.ApiResponse createVA(MerchantVARequest request) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_name", request.getAccName());
        requestBody.put("acc_type", request.getAccType());
        requestBody.put("mrc_order_id", request.getMrcOrderId());
        requestBody.put("collect_amount_max", request.getCollectAmountMax());

        // Optional fields
        addIfNotNull(requestBody, "collect_amount_min", request.getCollectAmountMin());
        addIfNotEmpty(requestBody, "store_code", request.getStoreCode());
        addIfNotEmpty(requestBody, "staff_code", request.getStaffCode());
        addIfNotEmpty(requestBody, "bank_code", request.getBankCode());
        addIfNotEmpty(requestBody, "expire_date", request.getExpireDate());
        addIfNotEmpty(requestBody, "memo", request.getMemo());

        return sendRequest(ENDPOINT_CREATE_VA, requestBody);
    }

    /**
     * Cập nhật VA (Merchant Hosted)
     * 
     * @param request DTO chứa thông tin cập nhật VA
     */
    public BaokimOrder.ApiResponse updateVA(MerchantVAUpdateRequest request) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA_UPD"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("mrc_order_id", request.getMrcOrderId());

        // Optional update fields
        addIfNotEmpty(requestBody, "acc_name", request.getAccName());
        addIfNotNull(requestBody, "collect_amount_min", request.getCollectAmountMin());
        addIfNotNull(requestBody, "collect_amount_max", request.getCollectAmountMax());
        addIfNotEmpty(requestBody, "expire_date", request.getExpireDate());

        return sendRequest(ENDPOINT_UPDATE_VA, requestBody);
    }

    /**
     * Tra cứu chi tiết VA (Merchant Hosted)
     * 
     * @param accNo Số VA cần tra cứu (required)
     */
    public BaokimOrder.ApiResponse detailVA(String accNo) throws Exception {
        VADetailRequest request = new VADetailRequest(accNo);
        return detailVA(request);
    }

    /**
     * Tra cứu chi tiết VA với bộ lọc (Merchant Hosted)
     * 
     * @param request DTO chứa thông tin tra cứu
     */
    public BaokimOrder.ApiResponse detailVA(VADetailRequest request) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        requestBody.put("request_id", generateRequestId("MH_VA_DTL"));
        requestBody.put("request_time", formatDateTime());
        requestBody.put("merchant_code", getMerchantCode());
        requestBody.put("acc_no", request.getAccNo());

        addIfNotEmpty(requestBody, "start_date", request.getStartDate());
        addIfNotEmpty(requestBody, "end_date", request.getEndDate());
        addIfNotNull(requestBody, "current_page", request.getCurrentPage());
        addIfNotNull(requestBody, "per_page", request.getPerPage());

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
        JsonObject responseData = (data.has("data") && !data.get("data").isJsonNull()) ? data.getAsJsonObject("data")
                : null;

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
     * Note: Baokim dùng merchant_code trong request_id để thống kê và gửi thông báo
     * cập nhật SDK.
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
