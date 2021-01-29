package com.example.ServiceUp;

public class Work {

    public Work() {

    }


    public Work(String orderId, String providerId, String customerId, String providerFirstName, String providerLastName, String customerFirstName, String customerLastName, String address, String comment, String instruction, String rating, String startTime, String endTime, String workDate, int status, String transactionDate, String profession, String cancellerId, String cancellationDate, String workTotal) {
        this.orderId = orderId;
        this.providerId = providerId;
        this.customerId = customerId;
        this.providerFirstName = providerFirstName;
        this.providerLastName = providerLastName;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.address = address;
        this.comment = comment;
        this.instruction = instruction;
        this.rating = rating;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workDate = workDate;
        this.status = status;
        this.transactionDate = transactionDate;
        this.profession = profession;
        this.cancellerId = cancellerId;
        this.cancellationDate = cancellationDate;
        this.workTotal = workTotal;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProviderFirstName() {
        return providerFirstName;
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = providerFirstName;
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public void setProviderLastName(String providerLastName) {
        this.providerLastName = providerLastName;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCancellerId() {
        return cancellerId;
    }

    public void setCancellerId(String cancellerId) {
        this.cancellerId = cancellerId;
    }

    public String getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(String cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getWorkTotal() {
        return workTotal;
    }

    public void setWorkTotal(String workTotal) {
        this.workTotal = workTotal;
    }

    private String orderId;
    private String providerId;
    private String customerId;
    private String providerFirstName;
    private String providerLastName;
    private String customerFirstName;
    private String customerLastName;
    private String address;
    private String comment;
    private String instruction;
    private String rating;
    private String startTime;
    private String endTime;
    private String workDate;
    private int status;
    private String transactionDate;
    private String profession;
    private String cancellerId;
    private String cancellationDate;
    private String workTotal;

}
