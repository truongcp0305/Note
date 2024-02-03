package com.example.note.UI.Calendar.CalendarToolsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LichHocStructure {
    List<NgayHoc> ngayHocs;

    public LichHocStructure() {
    }
    public LichHocStructure(String str) {
        ngayHocs = new ArrayList<>();
        String fisrtSplit[] = str.split("Từ ");
        fisrtSplit = Arrays.copyOfRange(fisrtSplit, 1, fisrtSplit.length);

        for (int i = 0; i < fisrtSplit.length; i++) {
            String secondSplit[] = fisrtSplit[i].split(":\n");

            String start = secondSplit[0].substring(0, 10);
            String end = secondSplit[0].substring(15);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate, endDate;

            try {
                startDate = dateFormat.parse(start);
                endDate = dateFormat.parse(end);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            List<Date> dateList = createDateList(startDate, endDate);

            List<LichTrongNgay> lichTrongNgays = new ArrayList<>();

            String thirdSplit[] = secondSplit[1].split("\n");
            for (int j = 0; j < thirdSplit.length; j++) {

                int dayOfWeek;

                if(thirdSplit[j].charAt(1) == 'T') {
                    dayOfWeek = Integer.parseInt("" + thirdSplit[j].charAt(5));
                } else {
                    dayOfWeek = Calendar.SUNDAY;
                }

                int startIndex = thirdSplit[j].indexOf("tiết") + 4;
                String tietTmp = thirdSplit[j].substring(startIndex);
                String tiet = tietTmp.charAt(2) == ','? tietTmp.substring(1, 2) : tietTmp.substring(1, 3);
                int caHoc = getCaHoc(Integer.parseInt(tiet));

                lichTrongNgays.add(new LichTrongNgay(dayOfWeek, caHoc));
            }

            Calendar calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int j = 0; j < dateList.size(); j++) {
                calendar.setTime(dateList.get(j));
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                for (int k = 0; k < lichTrongNgays.size(); k++) {
                    if (lichTrongNgays.get(k).getThu() == dayOfWeek) {
                        ngayHocs.add(new NgayHoc(dateFormat.format(dateList.get(j)),
                                lichTrongNgays.get(k).getCa(),
                                ""));
                    }
                }
            }
        }
    }

    private List<Date> createDateList(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        while (!startDate.after(endDate)) {
            dates.add(startDate);
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            startDate = calendar.getTime();
        }
        return dates;
    }

    public int getCaHoc(int i) {
        switch (i) {
            case 1: return 1;
            case 4: return 2;
            case 7: return 3;
            case 9: return 4;
            case 13: return 5;
        }
        return 0;
    }

    public List<NgayHoc> getNgayHocs() {
        return ngayHocs;
    }

    public void setNgayHocs(List<NgayHoc> ngayHocs) {
        this.ngayHocs = ngayHocs;
    }
}

