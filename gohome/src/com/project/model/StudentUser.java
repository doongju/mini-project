package com.project.model;

public class StudentUser extends User {
    public StudentUser(String id, String name, String phone, String birthDate) {
        super(id, name, phone, birthDate);
    }

    @Override
    public int calculatePrice(int hours) {
        return hours * 1800;
    }
}