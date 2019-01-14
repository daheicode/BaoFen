package com.example.administrator.baofen.bean;

public class Map360DetailVO {

    private String poi_name;
    private String phone;

    public String getPoi_name() {
        return poi_name;
    }

    public void setPoi_name(String poi_name) {
        this.poi_name = poi_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Map360DetailVO{" +
                "poi_name='" + poi_name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
