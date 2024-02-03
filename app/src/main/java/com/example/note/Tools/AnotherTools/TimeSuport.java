package com.example.note.Tools.AnotherTools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeSuport {
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
