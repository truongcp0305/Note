package com.example.note.Tools.AnotherTools;

import android.graphics.Bitmap;

import java.util.concurrent.ExecutionException;

    public class ConvertImg {

        public static String Image2String(Bitmap bitmap) {
            Img2String img2String = new Img2String();
            try {
                return img2String.execute(bitmap).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static Bitmap String2Image(String str) {
            String2Img string2Img = new String2Img();
            try {
                return string2Img.execute(str).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
}
