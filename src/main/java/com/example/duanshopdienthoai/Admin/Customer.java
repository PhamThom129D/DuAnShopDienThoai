package com.example.duanshopdienthoai.Admin;

public class  Customer {
    private int customerID;
    private String customerName;
    private String customerPassword;
    private String customerAddress;
    private String customerPhone;
    private Boolean customerState;

    public Customer(int customerID, String customerName, String customerPassword, String customerAddress, String customerPhone, Boolean customerState) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerPassword = customerPassword;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.customerState = customerState;
    }

    public Boolean getCustomerState() {
        return customerState;
    }
    public void setCustomerState(Boolean customerState) {
        this.customerState = customerState;
    }
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPassword() {
        return customerPassword;
    }

    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + customerID +
                ", customerName='" + customerName + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                '}';
    }

}
