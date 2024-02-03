package com.example.note.Tools.SecutityTools;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class KeyStoreSystem_RSA {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "MyKeyAlias";

    public static String encryptData(String dataToEncrypt) {
        byte[] dataToEncryptArr = dataToEncrypt.getBytes(StandardCharsets.UTF_8);
        try {
            // Khởi tạo KeyStore từ hệ thống Android
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            // Nếu khóa không tồn tại, tạo một cặp khóa mới
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                createNewKeyPair();
            } else {
                Log.d("TAG_1", "not gen new key");
            }
            // Lấy khóa riêng tư từ KeyStore
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore
                    .getEntry(KEY_ALIAS, null);
            // Mã hóa dữ liệu sử dụng khóa công khai
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //
            cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

            // Thực hiện mã hóa
            byte[] encryptedData = cipher.doFinal(dataToEncryptArr);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(encryptedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createNewKeyPair() {
        try {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 25);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setKeySize(2048)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setCertificateSubject(new X500Principal("CN=" + KEY_ALIAS))
                        .setCertificateSerialNumber(BigInteger.ONE)
                        .setCertificateNotBefore(start.getTime())
                        .setCertificateNotAfter(end.getTime())
                        .build();
                keyPairGenerator.initialize(spec);
            } else {
                RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048,
                        RSAKeyGenParameterSpec.F4);

                keyPairGenerator.initialize(spec);
            }
            keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String decryptData(String dataToDecrypted) {
        byte[] decryptedDataArr = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decryptedDataArr = Base64.getDecoder().decode(dataToDecrypted);
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore
                    .getEntry(KEY_ALIAS, null);

            // Giải mã dữ liệu sử dụng khóa riêng tư
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_RSA + "/"
                    + KeyProperties.BLOCK_MODE_ECB + "/"
                    + KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            byte[] decryptedData = cipher.doFinal(decryptedDataArr);

            return new String(decryptedData);
            // Xử lý dữ liệu đã giải mã
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

