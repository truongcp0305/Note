package com.example.note.UI.Calendar.CalendarToolsModel;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MonHoc {
    private String tenMonHoc;
    private int soTin;
    private int lopTinChi;
    private List<NgayHoc> lichHoc;

    public MonHoc(String tenMonHoc, int soTin, int lopTinChi, List<NgayHoc> lichHoc) {
        this.tenMonHoc = tenMonHoc;
        this.soTin = soTin;
        this.lopTinChi = lopTinChi;
        this.lichHoc = lichHoc;
    }

    public static List<NgayHoc> getLichHocFromString(String str) {
        List<NgayHoc> ngayHocs = new ArrayList<>();



        return ngayHocs;
    }

    public static int getSoTinFromString(String str) {
        return (int) Float.parseFloat(str);
    }

    public static int getLopTinChiFromString(String str) {
        int end = str.length() - 1;
        return (int) Float.parseFloat(str.substring(end - 2, end));
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public int getSoTin() {
        return soTin;
    }

    public void setSoTin(int soTin) {
        this.soTin = soTin;
    }

    public int getLopTinChi() {
        return lopTinChi;
    }

    public void setLopTinChi(int lopTinChi) {
        this.lopTinChi = lopTinChi;
    }

    public List<NgayHoc> getLichHoc() {
        return lichHoc;
    }

    public void setLichHoc(List<NgayHoc> lichHoc) {
        this.lichHoc = lichHoc;
    }

    public void addLichHoc(NgayHoc ngayHoc) {
        this.lichHoc.add(ngayHoc);
    }

    @NonNull
    @Override
    public String toString() {
        return this.tenMonHoc
                + " - " + this.soTin
                + " - " + this.lopTinChi;
    }

    public void printLichHoc() {
        for (int i = 0; i < lichHoc.size(); i++) {
            Log.d("lich hoc", lichHoc.get(i).toString());
        }
    }
}

