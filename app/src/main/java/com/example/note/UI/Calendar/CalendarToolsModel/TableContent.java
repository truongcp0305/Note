package com.example.note.UI.Calendar.CalendarToolsModel;

public class TableContent {
    private int start, end, colTenMonHoc, colSoTin, colLopTinChi, colLichHoc;

    public TableContent(){}

    public TableContent(int start, int end, int colTenMonHoc, int colSoTin, int colLopTinChi, int colLichHoc) {
        this.start = start;
        this.end = end;
        this.colTenMonHoc = colTenMonHoc;
        this.colSoTin = colSoTin;
        this.colLopTinChi = colLopTinChi;
        this.colLichHoc = colLichHoc;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getColTenMonHoc() {
        return colTenMonHoc;
    }

    public void setColTenMonHoc(int colTenMonHoc) {
        this.colTenMonHoc = colTenMonHoc;
    }

    public int getColSoTin() {
        return colSoTin;
    }

    public void setColSoTin(int colSoTin) {
        this.colSoTin = colSoTin;
    }

    public int getColLopTinChi() {
        return colLopTinChi;
    }

    public void setColLopTinChi(int colLopTinChi) {
        this.colLopTinChi = colLopTinChi;
    }

    public int getColLichHoc() {
        return colLichHoc;
    }

    public void setColLichHoc(int colLichHoc) {
        this.colLichHoc = colLichHoc;
    }
}
