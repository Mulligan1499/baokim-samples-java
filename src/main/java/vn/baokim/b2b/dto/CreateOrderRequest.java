package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * DTO cho API tạo đơn hàng (Basic/Pro - MasterSub)
 */
public class CreateOrderRequest {
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

    @SerializedName("payment_method")
    private Integer paymentMethod;

    @SerializedName("items")
    private List<Map<String, Object>> items;

    @SerializedName("customer_info")
    private CustomerInfo customerInfo;

    @SerializedName("service_code")
    private String serviceCode;

    @SerializedName("save_token")
    private Boolean saveToken;

    public CreateOrderRequest() {}

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

    public Integer getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Integer paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    public CustomerInfo getCustomerInfo() { return customerInfo; }
    public void setCustomerInfo(CustomerInfo customerInfo) { this.customerInfo = customerInfo; }

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }

    public Boolean getSaveToken() { return saveToken; }
    public void setSaveToken(Boolean saveToken) { this.saveToken = saveToken; }
}
