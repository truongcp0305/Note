package com.example.note.UI.Calendar.CalendarToolsModel;

import androidx.annotation.NonNull;

public class NgayHoc {
    String ngayHoc;
    int caHoc;
    String phongHoc;

    public NgayHoc() {}

    public NgayHoc(String ngayHoc, int caHoc, String phongHoc) {
        this.ngayHoc = ngayHoc;
        this.caHoc = caHoc;
        this.phongHoc = phongHoc;
    }

    public static NgayHoc getNgayHocFromString(String str) {
        return null;
    }

    public String getNgayHoc() {
        return ngayHoc;
    }

    public void setNgayHoc(String ngayHoc) {
        this.ngayHoc = ngayHoc;
    }

    public int getCaHoc() {
        return caHoc;
    }

    public void setCaHoc(int caHoc) {
        this.caHoc = caHoc;
    }

    public String getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.phongHoc = phongHoc;
    }

    @NonNull
    @Override
    public String toString() {
        return this.ngayHoc + " ca: " + this.caHoc + " phong: " + this.phongHoc;
    }
}
