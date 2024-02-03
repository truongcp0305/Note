package com.example.note.Model;

import android.graphics.Bitmap;

import com.example.note.Tools.AnotherTools.ConvertImg;

public class Message {
    private int id;
    private int idSinhVien;
    private String tenSinhVien;
    private String thoiGian;
    private String tinNhan;
    private int coImg;
    private String img;

    public Message(int id, int idSinhVien, String tenSinhVien, String thoiGian, String tinNhan, int coImg, String img) {
        this.id = id;
        this.idSinhVien = idSinhVien;
        this.tenSinhVien = tenSinhVien;
        this.thoiGian = thoiGian;
        this.tinNhan = tinNhan;
        this.coImg = coImg;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSinhVien() {
        return idSinhVien;
    }

    public void setIdSinhVien(int idSinhVien) {
        this.idSinhVien = idSinhVien;
    }

    public String getTenSinhVien() {
        return tenSinhVien;
    }

    public void setTenSinhVien(String tenSinhVien) {
        this.tenSinhVien = tenSinhVien;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getTinNhan() {
        return tinNhan;
    }

    public void setTinNhan(String tinNhan) {
        this.tinNhan = tinNhan;
    }

    public int getCoImg() {
        return coImg;
    }

    public void setCoImg(int coImg) {
        this.coImg = coImg;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Bitmap getImgOnBitmap() {
        return ConvertImg.String2Image(this.img);
    }
}
