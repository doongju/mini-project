package com.project.model;

public abstract class User {
    private String id;
    private String name;
    private String phone;
    private String birthDate;
    private int remainingHours;
    private int totalPaid;

    public User(String id, String name, String phone, String birthDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    public abstract int calculatePrice(int hours);

    public String getId() { 
    	return id; 
    	}
    public void setId(String id) { 
    	this.id = id;
    	}
    public String getName() { 
    	return name; 
    	}
    public void setName(String name) {
    	this.name = name; 
    	}
    public String getPhone() { 
    	return phone; 
    	}
    public void setPhone(String phone) { 
    	this.phone = phone; 
    	}
    public String getBirthDate() { 
    	return birthDate; 
    	}
    public void setBirthDate(String birthDate) { 
    	this.birthDate = birthDate;
    	}
    public int getRemainingHours() {
    	return remainingHours; 
    	}
    public void setRemainingHours(int remainingHours) {
    	this.remainingHours = remainingHours; 
    	}
    public int getTotalPaid() { 
    	return totalPaid; 
    	}
    public void setTotalPaid(int totalPaid) { 
    	this.totalPaid = totalPaid; 
    	}
}