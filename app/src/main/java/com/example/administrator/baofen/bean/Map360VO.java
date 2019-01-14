package com.example.administrator.baofen.bean;

import com.example.administrator.baofen.bean.Map360DetailVO;

public class Map360VO {

    private Map360DetailVO detail;

    public Map360DetailVO getDetail() {
        return detail;
    }

    public void setDetail(Map360DetailVO detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Map360VO{" +
                "detail=" + detail +
                '}';
    }
}
