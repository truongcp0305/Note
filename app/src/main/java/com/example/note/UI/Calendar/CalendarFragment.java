package com.example.note.UI.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.note.Adapter.ScheduleAdapter;
import com.example.note.LoginActivity;
import com.example.note.UI.Calendar.CalendarToolsModel.DangKyHocJson;
import com.example.note.UI.Calendar.CalendarToolsModel.LichHocStructure;
import com.example.note.UI.Calendar.CalendarToolsModel.MonHoc;
import com.example.note.UI.Calendar.CalendarToolsModel.MyCell;
import com.example.note.UI.Calendar.CalendarToolsModel.TableContent;
import com.example.note.UI.home.AddNoteActivity;
import com.example.note.MainActivity;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.databinding.FragmentCalendarBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalendarFragment extends Fragment {

    private final int RESULT_CODE_ADDNOTE = 1;
    private static final int PICK_EXCEL_FILE_REQUEST = 2;

    private Context context;

    private FragmentCalendarBinding binding;
    private ListView listView;
    private List<Schedule> schedules;
    private ScheduleAdapter scheduleAdapter;
    private Button btnAddCalendar;
    private String idSinhVien;
    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = getContext();

        idSinhVien = ((MainActivity) getActivity()).idSinhVien;
        schedules = new ArrayList<>();

        calendarView = root.findViewById(R.id.calendarView);
        listView = root.findViewById(R.id.calendarListView);
        btnAddCalendar = root.findViewById(R.id.btnAddCalendar);
        scheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.schedule_layout, schedules);
        listView.setAdapter(scheduleAdapter);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date selectedDate = new Date(year - 1900, month, dayOfMonth);
                String formattedDate = sdf.format(selectedDate);

                listView.setVisibility(View.VISIBLE);

                callApi(formattedDate);
                String toastMessage = "Đây là lịch của ngày " + dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                        intent.putExtra("idSinhVien", idSinhVien);
                        intent.putExtra("belong", schedules.get(i).getTenMon() + " : " + schedules.get(i).getNgayHocToString());
                        startActivityForResult(intent, RESULT_CODE_ADDNOTE);

//                        Toast.makeText(getActivity(), schedules.get(i).getTenMon() + " : " + schedules.get(i).getNgayHocToString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnAddCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.ms-excel");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_EXCEL_FILE_REQUEST);
            }
        });

        return root;
    }

    private void callApi(String selectedDate) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", SinhVien.getIdFromMaSinhVien(idSinhVien));
        jsonObject.addProperty("selectedDate", selectedDate);

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/getCalendar.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            boolean status = jsonObject.get("status").getAsBoolean();
                            if (status) {
                                JsonArray schedulesJsonArray = jsonObject.get("schedules").getAsJsonArray();
                                schedules.clear();
                                for (JsonElement scheduleJsonElement : schedulesJsonArray) {
                                    JsonObject scheduleJsonObject = scheduleJsonElement.getAsJsonObject();
                                    String ngayHoc = scheduleJsonObject.get("ngayHoc").getAsString();

                                    // Kiểm tra ngayHoc trùng khớp với selectedDate
                                    if (ngayHoc.equals(selectedDate)) {
                                        int maMon = scheduleJsonObject.get("maMon").getAsInt();
                                        String tenMon = scheduleJsonObject.get("tenMon").getAsString();
                                        int soTinChi = scheduleJsonObject.get("soTinChi").getAsInt();
                                        int lopTinChi = scheduleJsonObject.get("lopTinChi").getAsInt();
                                        int caHoc = scheduleJsonObject.get("caHoc").getAsInt();
                                        String phongHoc = scheduleJsonObject.get("phongHoc").getAsString();

                                        Schedule schedule = new Schedule(maMon, tenMon, soTinChi, lopTinChi, ngayHoc, caHoc, phongHoc);
                                        schedules.add(schedule);
                                    }
                                }
                                scheduleAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "Không Lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_ADDNOTE) {
            if (resultCode == Activity.RESULT_OK) {
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PICK_EXCEL_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);

                    String filePath = getRealPathFromUri(uri); // Lấy đường dẫn từ Uri
                    Workbook workbook = getWorkbook(inputStream, filePath);

                    // Lấy sheet cần đọc
                    Sheet sheet = workbook.getSheetAt(0);

                    // Duyệt qua từng dòng và từng ô để lấy dữ liệu
                    List<Row> rows = new ArrayList<>();
                    Iterator<Row> rowIterator = sheet.iterator();

                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        rows.add(row);
                    }

                    int numRows = rows.size();
                    int maxNumCols = 10;

                    String[][] dataArray = new String[numRows][maxNumCols];

                    for (int i = 0; i < numRows; i++) {
                        Row row = rows.get(i);
                        for (int j = 0; j < maxNumCols; j++) {
                            Cell cell = row.getCell(j);
                            dataArray[i][j] = (cell != null) ? cell.toString() : "";
                        }
                    }

//                    for (int i = 0; i < dataArray.length; i++) {
//                        for (int j = 0; j < dataArray[0].length; j++) {
//                            String cellInfo = "Cell at Row " + (i + 1) + " and Column " + (j + 1) + " Value: " + dataArray[i][j];
//                            Log.d("ExcelReader", cellInfo);
//                        }
//                    }

                    MyCell maSinhVien = findMSV(dataArray);
                    Log.d("MSV", maSinhVien.getContent());

//                    printData(dataArray);

                    TableContent tableContent = new TableContent();
                    tableContent = findTableContent(dataArray);

//                    Log.d("content", "start: " + dataArray[tableContentStart][3] + tableContentStart
//                        + " end: " + dataArray[tableContentEnd][3] + tableContentEnd);

                    List<MonHoc> monHocs = getMonHocFromFile(dataArray,tableContent);

                    if (!monHocs.isEmpty()) {
                        callApiUpdateCalendar(monHocs, SinhVien.getIdFromMaSinhVien(maSinhVien.getContent()));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void callApiUpdateCalendar(List<MonHoc> monHocs, int msv) {
        DangKyHocJson dangKyHocJson = new DangKyHocJson(msv, monHocs);
        Gson gson = new Gson();
        String json = gson.toJson(dangKyHocJson);
//        Log.d("json", json);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/updateCalendar.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            String message = "";
                            boolean status = false;
                            if (jsonObject.has("message")) {
                                message = jsonObject.get("message").getAsString();
                            }
                            if (jsonObject.has("status")) {
                                status = jsonObject.get("status").getAsBoolean();
                            }
                            if (status) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private List<MonHoc> getMonHocFromFile(String[][] dataArray, TableContent tableContent) {
        List<MonHoc> monHocs = new ArrayList<>();

        for (int i = tableContent.getStart(); i <= tableContent.getEnd(); i++) {
            String tenMonHoc = dataArray[i][tableContent.getColTenMonHoc()];
            int soTin = MonHoc.getSoTinFromString(dataArray[i][tableContent.getColSoTin()]);
            int lopTinChi = MonHoc.getLopTinChiFromString(dataArray[i][tableContent.getColLopTinChi()]);
            String lichHoc = dataArray[i][tableContent.getColLichHoc()];

            LichHocStructure lichHocStructure = new LichHocStructure(lichHoc);
            MonHoc monHoc = new MonHoc(tenMonHoc, soTin, lopTinChi, lichHocStructure.getNgayHocs());
            monHocs.add(monHoc);
        }

        return monHocs;
    }

    private TableContent findTableContent(String[][] dataArray) {
        TableContent tableContent = new TableContent();
        int numRows = dataArray.length;
        int numCols = dataArray[0].length;
        for (int i = 0; i < numRows; i++) {
            if (dataArray[i][0].equals("STT")) {
                tableContent.setStart(i + 1);

                for (int j = 0; j < numCols; j++) {
                    if (dataArray[i][j].equals("Tên học phần")) {
                        tableContent.setColTenMonHoc(j);
                        continue;
                    }
                    if (dataArray[i][j].equals("Số TC")) {
                        tableContent.setColSoTin(j);
                        continue;
                    }
                    if (dataArray[i][j].equals("Lớp học phần")) {
                        tableContent.setColLopTinChi(j);
                        continue;
                    }
                    if (dataArray[i][j].equals("Thời gian địa điểm")) {
                        tableContent.setColLichHoc(j);
                        break;
                    }
                }
            }
            if (dataArray[i][0].equals("Tổng cộng:")) {
                tableContent.setEnd(i - 1);
                break;
            }
        }

        return tableContent;
    }

    private MyCell findMSV(String[][] dataArray) {
        int numRows = dataArray.length;
        int numCols = dataArray[0].length;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if(dataArray[i][j].equals("Mã số :")) {
                    return new MyCell(i, j+1, dataArray[i][j+1]);
                }
            }
        }
        return null;
    }

    private void printData(String[][] dataArray) {
        int numRows = dataArray.length;
        int numCols = dataArray[0].length;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Log.d("table", "row: " + i + ", col: " + j + ", content: " + dataArray[i][j]);
            }
        }
    }

    private Workbook getWorkbook(InputStream inputStream, String filePath) throws IOException {
        Workbook workbook;

        if (filePath != null && filePath.endsWith(".xls")) {
            // Xử lý định dạng Excel cũ (.xls) bằng HSSF
            workbook = new HSSFWorkbook(inputStream);
        } else if (filePath != null && filePath.endsWith(".xlsx")) {
            // Xử lý định dạng Excel mới (.xlsx) bằng XSSF
            workbook = new XSSFWorkbook(inputStream);
        } else {
            // Xử lý các định dạng khác nếu cần
            throw new UnsupportedOperationException("Unsupported Excel format");
        }

        return workbook;
    }

    private String getRealPathFromUri(Uri uri) {
        String result = null;
        String[] projection = {OpenableColumns.DISPLAY_NAME};
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
                result = cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}