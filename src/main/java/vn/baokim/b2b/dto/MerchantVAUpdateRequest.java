package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO cho API cập nhật Virtual Account (Merchant Hosted)
 */
public class MerchantVAUpdateRequest {
    @SerializedName("mrc_order_id")
    private String mrcOrderId;

    @SerializedName("acc_name")
    private String accName;

    @SerializedName("collect_amount_min")
    private Integer collectAmountMin;

    @SerializedName("collect_amount_max")
    private Integer collectAmountMax;

    @SerializedName("expire_date")
    private String expireDate;

    public MerchantVAUpdateRequest() {}

    public MerchantVAUpdateRequest(String mrcOrderId) {
        this.mrcOrderId = mrcOrderId;
    }

    // Getters & Setters

    public String getMrcOrderId() { return mrcOrderId; }
    public void setMrcOrderId(String mrcOrderId) { this.mrcOrderId = mrcOrderId; }

    public String getAccName() { return accName; }
    public void setAccName(String accName) { this.accName = accName; }

    public Integer getCollectAmountMin() { return collectAmountMin; }
    public void setCollectAmountMin(Integer collectAmountMin) { this.collectAmountMin = collectAmountMin; }

    public Integer getCollectAmountMax() { return collectAmountMax; }
    public void setCollectAmountMax(Integer collectAmountMax) { this.collectAmountMax = collectAmountMax; }

    public String getExpireDate() { return expireDate; }
    public void setExpireDate(String expireDate) { this.expireDate = expireDate; }
}
