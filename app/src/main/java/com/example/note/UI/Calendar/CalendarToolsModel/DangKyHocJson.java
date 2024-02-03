package com.example.note.UI.Calendar.CalendarToolsModel;

import java.util.List;

public class DangKyHocJson {
    private int msv;
    private List<MonHoc> monHocs;

    public DangKyHocJson(int msv, List<MonHoc> monHocs) {
        this.msv = msv;
        this.monHocs = monHocs;
    }

    public int getMsv() {
        return msv;
    }

    public void setMsv(int msv) {
        this.msv = msv;
    }

    public List<MonHoc> getMonHocs() {
        return monHocs;
    }

    public void setMonHocs(List<MonHoc> monHocs) {
        this.monHocs = monHocs;
    }
}
