package com.example.note.UI.School;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.Adapter.MessageAdapter;
import com.example.note.Adapter.StudentAdapter;
import com.example.note.Data.UserData;
import com.example.note.Model.Course;
import com.example.note.Model.Message;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.Tools.AnotherTools.ConvertImg;
import com.example.note.Tools.AnotherTools.TimeSuport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowClassActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private String idSinhVienStr;
    private Course course;
    private TextView classNameValues, totalCreditValues, teacherValues, tv_listName;
    private ImageButton btnBack, btnStdList, btnChatList, btnSendMsg, btnAddImg, btnClearMsg;
    private EditText chatBox;
    private ImageView image;
    private ListView listView;
    private List<SinhVien> studentList;
    private List<Message> msgList;
    private StudentAdapter studentAdapter;
    private MessageAdapter messageAdapter;


    private Uri imageUri;
    private String imgOnString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classmate_layout);

        initUi();

        idSinhVienStr = UserData.getIdSinhVien();
        course = (Course) getIntent().getExtras().get("course");
        UserData.setCourse(course);
        course = UserData.getCourse();

        classNameValues.setText(course.getTenLop());
        teacherValues.setText(course.getTenGiaoVien());
        totalCreditValues.setText("" + course.getSoTinChi());
        studentList = new ArrayList<>();
        msgList = new ArrayList<>();

        callApi();

        btnClickListener();
    }

    private void btnClickListener() {
        btnStdList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApi();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        btnClearMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgOnString = null;
                image.setImageResource(0);
                chatBox.setText("");
                Toast.makeText(ShowClassActivity.this, "Clear Msg", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!chatBox.getText().toString().equals("") || imgOnString != null) {

                    CountDownLatch countDownLatch = new CountDownLatch(1);

                    Thread sendMsgThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessage();
                            countDownLatch.countDown();
                        }
                    });

                    Thread getMsgThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                countDownLatch.await();

                                getMessages();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    sendMsgThread.start();
                    getMsgThread.start();

                    try {
                        sendMsgThread.join();
                        getMsgThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    imgOnString = null;
                    image.setImageResource(0);
                    chatBox.setText("");
                } else Toast.makeText(ShowClassActivity.this, "No Msg", Toast.LENGTH_SHORT).show();
            }
        });

        btnChatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMessages();
            }
        });
    }

    private void sendMessage() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("idSinhVien", SinhVien.getIdFromMaSinhVien(idSinhVienStr));
        jsonObject.addProperty("thoiGian", TimeSuport.getCurrentTime());
        jsonObject.addProperty("maMon", course.getMaMon());
        jsonObject.addProperty("lopTinChi", course.getLopTinChi());
        jsonObject.addProperty("tinNhan", chatBox.getText().toString());
        int coImg = imgOnString != null ? 1  : 0;
        jsonObject.addProperty("coImg", coImg);
        String imgStr = imgOnString != null ? imgOnString  : "";
        jsonObject.addProperty("img", imgStr);


        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "get msg: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/insertMessage.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowClassActivity.this, "False", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                runOnUiThread(new Runnable() {
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
                        if(status) {
                            Toast.makeText(ShowClassActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ShowClassActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void getMessages() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", SinhVien.getIdFromMaSinhVien(UserData.getIdSinhVien()));
        jsonObject.addProperty("maMon", course.getMaMon());
        jsonObject.addProperty("lopTinChi", course.getLopTinChi());

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "get message: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/getMessages.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ShowClassActivity.this, "False", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        boolean status = jsonObject.get("status").getAsBoolean();
                        if (status) {
                            JsonArray messageJsonArray = jsonObject.get("msgs").getAsJsonArray();
                            msgList.clear();
                            for (JsonElement messageJsonElement : messageJsonArray) {
                                JsonObject messageJsonObject = messageJsonElement.getAsJsonObject();
                                Message message = gson.fromJson(messageJsonObject, Message.class);

                                msgList.add(message);
                            }
                            messageAdapter = new MessageAdapter(ShowClassActivity.this, R.layout.message_item_layout, msgList);
                            listView.setAdapter(messageAdapter);
                            messageAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(ShowClassActivity.this, "Không Lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void callApi() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("maMon", course.getMaMon());
        jsonObject.addProperty("lopTinChi", course.getLopTinChi());

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/getStudents.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ShowClassActivity.this, "False", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        boolean status = jsonObject.get("status").getAsBoolean();
                        if (status) {
                            JsonArray studentJsonArray = jsonObject.get("sinhVien").getAsJsonArray();
                            studentList.clear();
                            for (JsonElement studentJsonElement : studentJsonArray) {
                                JsonObject studentJsonObject = studentJsonElement.getAsJsonObject();
                                SinhVien sinhVien = gson.fromJson(studentJsonObject, SinhVien.class);

                                studentList.add(sinhVien);
                            }
                            if(messageAdapter == null) {
                                studentAdapter = new StudentAdapter(ShowClassActivity.this, R.layout.student_item, studentList);
                                listView.setAdapter(studentAdapter);
                            } else {
                                messageAdapter.notifyDataSetChanged();
                            }

                            // Sử dụng danh sách noteList như mong muốn
                        } else {
                            Toast.makeText(ShowClassActivity.this, "Không Lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initUi() {
        classNameValues = findViewById(R.id.classNameValues);
        tv_listName = findViewById(R.id.tv_listName);
        teacherValues = findViewById(R.id.teacherValues);
        totalCreditValues = findViewById(R.id.totalCreditValues);
        listView = findViewById(R.id.studentListView);
        btnBack = findViewById(R.id.btnBack);
        btnStdList = findViewById(R.id.btnStdList);
        btnChatList = findViewById(R.id.btnChatList);
        btnSendMsg = findViewById(R.id.btnSendMsg);
        btnAddImg = findViewById(R.id.btnAddImg);
        image = findViewById(R.id.image);
        chatBox = findViewById(R.id.chatBox);
        btnClearMsg = findViewById(R.id.btnClearMsg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy đường dẫn của hình ảnh đã chọn
            imageUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgOnString = ConvertImg.Image2String(bitmap);
                image.setImageBitmap(bitmap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}