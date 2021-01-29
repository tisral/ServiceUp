package com.example.ServiceUp;

import java.util.HashMap;

public class User {

    public User() {

    }


    public User(String userId, String image, String firstName, String lastName, String email, String phoneNumber, String address, String city, String state, String zipCode, String profession, String jobDescription, String rating, HashMap<String, String> myCustomers, HashMap<String, String> myProviders, HashMap<String, Boolean> availability, String startTime, String endTime, Double rate) {
        this.userId = userId;
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.profession = profession;
        this.jobDescription = jobDescription;
        this.rating = rating;
        this.myCustomers = myCustomers;
        this.myProviders = myProviders;
        this.availability = availability;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rate = rate;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public HashMap<String, String> getMyCustomers() {
        return myCustomers;
    }

    public void setMyCustomers(HashMap<String, String> myCustomers) {
        this.myCustomers = myCustomers;
    }

    public HashMap<String, String> getMyProviders() {
        return myProviders;
    }

    public void setMyProviders(HashMap<String, String> myProviders) {
        this.myProviders = myProviders;
    }

    public HashMap<String, Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(HashMap<String, Boolean> availability) {
        this.availability = availability;
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

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    private String userId;
    public String image;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String profession; // key : job title, value : job description
    private String jobDescription;
    private String rating; // key : job title, value : rate
    private  HashMap<String, String> myCustomers;
    private  HashMap<String, String> myProviders;
    private HashMap<String, Boolean> availability;
    private String startTime;
    private String endTime;
    private Double rate;


}

