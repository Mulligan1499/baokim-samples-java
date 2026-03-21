package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO cho API tạo Virtual Account (Merchant Hosted)
 * Dùng chung cho cả Dynamic VA và Static VA
 */
public class MerchantVARequest {
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

    @SerializedName("memo")
    private String memo;

    @SerializedName("store_code")
    private String storeCode;

    @SerializedName("staff_code")
    private String staffCode;

    @SerializedName("bank_code")
    private String bankCode;

    public MerchantVARequest() {}

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

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public String getStoreCode() { return storeCode; }
    public void setStoreCode(String storeCode) { this.storeCode = storeCode; }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
}
