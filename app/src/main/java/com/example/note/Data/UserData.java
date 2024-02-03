package com.example.note.Data;

import android.util.Log;

import com.example.note.Model.Course;
import com.example.note.Tools.SecutityTools.KeyStoreSystem_RSA;

import java.nio.charset.StandardCharsets;

public class UserData {
    private static String idSinhVien;
    private static String course;

    public UserData(String idSinhVien) {
        this.idSinhVien = KeyStoreSystem_RSA.encryptData(idSinhVien);
    }

    public static String getIdSinhVien() {
        return KeyStoreSystem_RSA.decryptData(idSinhVien);
    }

    public static void setIdSinhVien(String idSinhVien) {
        UserData.idSinhVien = KeyStoreSystem_RSA.encryptData(idSinhVien);
    }

    public static Course getCourse() {
        String strTmp = KeyStoreSystem_RSA.decryptData(course);
        String[] arrTmp = strTmp.split("/");
        Course result = new Course(Integer.parseInt(arrTmp[0]),
                arrTmp[1],
                Integer.parseInt(arrTmp[2]),
                Integer.parseInt(arrTmp[3]),
                arrTmp.length < 5? "" : arrTmp[4]);
        Log.d("TAG", result.toString());
        return result;
    }

    public static void setCourse(Course course) {
        UserData.course = KeyStoreSystem_RSA.encryptData(course.toString());
        Log.d("TAG", UserData.course);
    }
}
