package com.example.administrator.baofen.bean;

public class NuoMiItemVO {

    private String poiname;
    private String phone;

    public String getPoiname() {
        return poiname;
    }

    public void setPoiname(String poiname) {
        this.poiname = poiname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "NuoMiItemVO{" +
                "poiname='" + poiname + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
