package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO cho API tạo Virtual Account (Host-to-Host)
 * Dùng chung cho cả Dynamic VA và Static VA
 */
public class CreateVARequest {
    @SerializedName("acc_name")
    private String accName;

    @SerializedName("acc_type")
    private Integer accType;

    @SerializedName("mrc_order_id")
    private String mrcOrderId;

    @SerializedName("collect_amount_min")
    private Integer collectAmountMin;

    @SerializedName("collect_amount_max")
    private Integer collectAmountMax;

    @SerializedName("expire_date")
    private String expireDate;

    @SerializedName("description")
    private String description;

    public CreateVARequest() {}

    // Getters & Setters

    public String getAccName() { return accName; }
    public void setAccName(String accName) { this.accName = accName; }

    public Integer getAccType() { return accType; }
    public void setAccType(Integer accType) { this.accType = accType; }

    public String getMrcOrderId() { return mrcOrderId; }
    public void setMrcOrderId(String mrcOrderId) { this.mrcOrderId = mrcOrderId; }

    public Integer getCollectAmountMin() { return collectAmountMin; }
    public void setCollectAmountMin(Integer collectAmountMin) { this.collectAmountMin = collectAmountMin; }

    public Integer getCollectAmountMax() { return collectAmountMax; }
    public void setCollectAmountMax(Integer collectAmountMax) { this.collectAmountMax = collectAmountMax; }

    public String getExpireDate() { return expireDate; }
    public void setExpireDate(String expireDate) { this.expireDate = expireDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
