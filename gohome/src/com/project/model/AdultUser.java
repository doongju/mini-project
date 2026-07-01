package com.project.model;

public class AdultUser extends User {
    public AdultUser(String id, String name, String phone, String birthDate) {
        super(id, name, phone, birthDate);
    }

    @Override
    public int calculatePrice(int hours) {
        return hours * 2000;
    }
}