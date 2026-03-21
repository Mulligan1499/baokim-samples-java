package vn.baokim.b2b.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO chứa thông tin khách hàng
 */
public class CustomerInfo {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("gender")
    private Integer gender;

    @SerializedName("code")
    private String code;

    public CustomerInfo() {}

    public CustomerInfo(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.gender = 1;
    }

    // Getters & Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
