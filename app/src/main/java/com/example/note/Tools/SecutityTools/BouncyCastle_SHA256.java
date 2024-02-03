package com.example.note.Tools.SecutityTools;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class BouncyCastle_SHA256 {
    static {
        // Thêm Bouncy Castle Provider
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String hashWithSHA256(String input) throws NoSuchAlgorithmException {
        try {
            // Tạo đối tượng MessageDigest với thuật toán SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Thực hiện băm dữ liệu
            byte[] hashedBytes = digest.digest(input.getBytes());

            // Chuyển đổi mảng byte thành chuỗi hex
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
