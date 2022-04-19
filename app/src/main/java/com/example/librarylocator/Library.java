package com.example.librarylocator;

public class Library {

    Library() {}

    private String name, hoursOfOperation, address, url, phone, city, state, zip;
    private int objectID = 0;

    public void setName(String name) { this.name = name; }
    public void setHoursOfOperation(String hoursOfOperation) { this.hoursOfOperation = hoursOfOperation; }
    public void setAddress(String address) { this.address = address; }
    public void setUrl(String url) { this.url = url; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) {this.state = state; }
    public void setZip(String zip) { this.zip = zip; }
    public void setObjectID(int id) { this.objectID = id;}

    public String getName() { return this.name; }
    public String getHoursOfOperation() { return this.hoursOfOperation; }
    public String getAddress() { return this.address; }
    public String getUrl() { return this.url; }
    public String getPhone() { return this.phone; }
    public String getCity() { return  this.city; }
    public String getState() { return this.state; }
    public String getZip() { return this.zip; }
    public int getObjectID() { return this.objectID; }

}
