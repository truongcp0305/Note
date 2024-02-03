package com.example.note.Tools.AnotherTools;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Img2String extends AsyncTask<Bitmap, Void, String> {
    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        Bitmap bitmap = bitmaps[0];
        String result;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(bitmap!= null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArr = byteArrayOutputStream.toByteArray();
            result = Base64.encodeToString(byteArr, Base64.DEFAULT);
            return result;
        } else return null;
    }
}
