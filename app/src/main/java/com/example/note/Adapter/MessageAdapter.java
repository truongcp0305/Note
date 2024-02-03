package com.example.note.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.note.Data.UserData;
import com.example.note.Model.Message;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.Tools.AnotherTools.ConvertImg;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    Activity mActivity;
    int layoutId;
    List<Message> msgs;

    public MessageAdapter(Activity mActivity, int layoutId, List<Message> msgs) {
        super(mActivity, layoutId, msgs);
        this.mActivity = mActivity;
        this.layoutId = layoutId;
        this.msgs = msgs;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public List<Message> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<Message> msgs) {
        this.msgs = msgs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            viewHolder = new ViewHolder();

            viewHolder.linearLayout = convertView.findViewById(R.id.linearLayout);
            viewHolder.imgOnMsg = convertView.findViewById(R.id.imgOnMsg);
            viewHolder.tvFrom = convertView.findViewById(R.id.tvFrom);
            viewHolder.tvTimeSend = convertView.findViewById(R.id.tvTimeSend);
            viewHolder.tvMsg = convertView.findViewById(R.id.tvMsg);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message message = msgs.get(position);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        if (message.getIdSinhVien() == SinhVien.getIdFromMaSinhVien(UserData.getIdSinhVien())) {

            layoutParams.gravity = Gravity.RIGHT;
            viewHolder.tvFrom.setText("Tôi");
        } else {
            layoutParams.gravity = Gravity.START;
            viewHolder.tvFrom.setText(message.getTenSinhVien());
        }

        viewHolder.linearLayout.setLayoutParams(layoutParams);

        if(message.getCoImg() == 0) {
            viewHolder.imgOnMsg.setVisibility(View.GONE);
        } else {
            Bitmap bitmap;
            bitmap = ConvertImg.String2Image(message.getImg());
            if(bitmap != null) {
                viewHolder.imgOnMsg.setImageBitmap(bitmap);
            }
        }

        viewHolder.tvTimeSend.setText(message.getThoiGian());
        viewHolder.tvMsg.setText(message.getTinNhan());

        return convertView;
    }

    public class ViewHolder {
        LinearLayout linearLayout;
        TextView tvFrom, tvTimeSend, tvMsg;
        ImageView imgOnMsg;
    }
}
