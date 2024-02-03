package com.example.note.UI.Calendar.CalendarToolsModel;

public class MyCell {
    int row;
    int col;
    String content;

    public MyCell(int row, int col, String content) {
        this.row = row;
        this.col = col;
        this.content = content;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
