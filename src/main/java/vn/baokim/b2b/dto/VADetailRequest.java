package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO cho API tra cứu chi tiết Virtual Account (Merchant Hosted)
 */
public class VADetailRequest {
    @SerializedName("acc_no")
    private String accNo;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("current_page")
    private Integer currentPage;

    @SerializedName("per_page")
    private Integer perPage;

    public VADetailRequest() {}

    public VADetailRequest(String accNo) {
        this.accNo = accNo;
    }

    // Getters & Setters

    public String getAccNo() { return accNo; }
    public void setAccNo(String accNo) { this.accNo = accNo; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

    public Integer getPerPage() { return perPage; }
    public void setPerPage(Integer perPage) { this.perPage = perPage; }
}
