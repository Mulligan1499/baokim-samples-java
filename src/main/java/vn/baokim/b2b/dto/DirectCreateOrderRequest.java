package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * DTO cho API tạo đơn hàng Direct Connection
 */
public class DirectCreateOrderRequest {
    @SerializedName("mrc_order_id")
    private String mrcOrderId;

    @SerializedName("total_amount")
    private int totalAmount;

    @SerializedName("description")
    private String description;

    @SerializedName("url_success")
    private String urlSuccess;

    @SerializedName("url_fail")
    private String urlFail;

    @SerializedName("store_code")
    private String storeCode;

    @SerializedName("branch_code")
    private String branchCode;

    @SerializedName("staff_code")
    private String staffCode;

    @SerializedName("items")
    private List<Map<String, Object>> items;

    @SerializedName("customer_info")
    private CustomerInfo customerInfo;

    @SerializedName("payment_method")
    private String paymentMethod;

    public DirectCreateOrderRequest() {}

    // Getters & Setters

    public String getMrcOrderId() { return mrcOrderId; }
    public void setMrcOrderId(String mrcOrderId) { this.mrcOrderId = mrcOrderId; }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrlSuccess() { return urlSuccess; }
    public void setUrlSuccess(String urlSuccess) { this.urlSuccess = urlSuccess; }

    public String getUrlFail() { return urlFail; }
    public void setUrlFail(String urlFail) { this.urlFail = urlFail; }

    public String getStoreCode() { return storeCode; }
    public void setStoreCode(String storeCode) { this.storeCode = storeCode; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    public CustomerInfo getCustomerInfo() { return customerInfo; }
    public void setCustomerInfo(CustomerInfo customerInfo) { this.customerInfo = customerInfo; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
