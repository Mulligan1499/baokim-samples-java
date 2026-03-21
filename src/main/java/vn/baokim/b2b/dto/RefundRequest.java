package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO cho API hoàn tiền (Basic/Pro - MasterSub)
 */
public class RefundRequest {
    @SerializedName("mrc_order_id")
    private String mrcOrderId;

    @SerializedName("amount")
    private int amount;

    @SerializedName("description")
    private String description;

    public RefundRequest() {}

    public RefundRequest(String mrcOrderId, int amount, String description) {
        this.mrcOrderId = mrcOrderId;
        this.amount = amount;
        this.description = description;
    }

    // Getters & Setters

    public String getMrcOrderId() { return mrcOrderId; }
    public void setMrcOrderId(String mrcOrderId) { this.mrcOrderId = mrcOrderId; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
