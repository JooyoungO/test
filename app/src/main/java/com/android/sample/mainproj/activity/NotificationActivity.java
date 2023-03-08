package com.android.sample.mainproj.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.log.LogService;

public class NotificationActivity extends AppCompatActivity {

    private Activity activity;

    private NotificationManager notificationManager;

    private ImageButton ibtn_back_click;

    private EditText et_noti_tel;

    private Button btn_noti_normal, btn_noti_extend, btn_noti_inbox, btn_noti_dialog;

    public static final int NOTIFICATION_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_noti);

            init();

            setting();

            addListener();
        }
        catch(Exception ex) {

            LogService.error(activity, ex.getMessage(), ex);
        }
    }

    private void init() {

        activity = this;

        ibtn_back_click = findViewById(R.id.ibtn_back_click);

        et_noti_tel = findViewById(R.id.et_noti_tel);

        btn_noti_normal = findViewById(R.id.btn_noti_normal);

        btn_noti_extend = findViewById(R.id.btn_noti_extend);

        btn_noti_inbox = findViewById(R.id.btn_noti_inbox);

        btn_noti_dialog = findViewById(R.id.btn_noti_dialog);
    }

    private void setting() {

        //시스템 서비스 통지 작업 처리가 가능한 NotificationManager 객체 생성
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void addListener() {

        ibtn_back_click.setOnClickListener(listener_back_click);

        btn_noti_normal.setOnClickListener(listener_noti_normal);

        btn_noti_extend.setOnClickListener(listener_noti_extend);

        btn_noti_inbox.setOnClickListener(listener_noti_inbox);

        btn_noti_dialog.setOnClickListener(listener_noti_dialog);

    }

    private View.OnClickListener listener_back_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    };

    private View.OnClickListener listener_noti_normal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {

                NotificationCompat.Builder builder = getDefaultBuilder();

                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
            catch (Exception ex) {

                LogService.error(activity, ex.getMessage(), ex);
            }

        }
    };

    private View.OnClickListener listener_noti_extend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            NotificationCompat.Builder builder = getDefaultBuilder();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.steak);
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle(builder);
            bigPictureStyle.setBigContentTitle("고깃집 전화번호입니다.");
            bigPictureStyle.setSummaryText("현재 고기가 구워졌습니다.");
            bigPictureStyle.bigPicture(bitmap);
            builder.setStyle(bigPictureStyle);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    };

    private View.OnClickListener listener_noti_inbox = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            NotificationCompat.Builder builder = getDefaultBuilder();

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
            inboxStyle.setSummaryText("더 보기");
            inboxStyle.addLine("여러분이");
            inboxStyle.addLine("입력한");
            inboxStyle.addLine("전화번호입니다.");

            builder.setStyle(inboxStyle);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    };

    private View.OnClickListener listener_noti_dialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            NotificationCompat.Builder builder = getDefaultBuilder();

            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + et_noti_tel.getText().toString()));
            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            Intent cancel = new Intent("android.intent.action.NOTIFICATION_CANCEL");
            //cancel.setPackage("com.android.sample.mainproj");
            cancel.setPackage(getPackageName());
            cancel.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            PendingIntent cancelIntent = PendingIntent.getBroadcast(activity, 0, cancel, PendingIntent.FLAG_IMMUTABLE);

            builder.setContentIntent(null);

            builder.addAction(android.R.drawable.star_on, "확인", pendingIntent);
            builder.addAction(android.R.drawable.star_off, "취소", cancelIntent);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    };

    private NotificationCompat.Builder getDefaultBuilder() {

        String channelId = "tel_notification_channel";

        //API 26 버전 이상부터는 알림 통지를 받을 수 있는 채널을 생성해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {

                NotificationChannel notificationChannel = new NotificationChannel(

                        channelId,
                        "Tel Notification Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );

                notificationChannel.setDescription("전화 알림 채널");

                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + et_noti_tel.getText().toString()));

        //특정 인텐트를 다른 곳에서 사용할 수 있게 해주는 특수 인텐트
        //PendingIntent는 실제 실행 하고자 하는 인텐트를 지정한다.
        //FLAG_CANCLE_CURRENT : 동일한 펜딩 인덴트가 있으면 취소하도록 하는 플레그
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        else {

            pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, channelId);

        builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setContentTitle("전화");

        //알람 발생시 진동, 사운드 등 설정 - 안드로이드 버전 혹은 기종에 따라 동작하지 않음
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        builder.setContentIntent(pendingIntent);

        //알람 터치시 삭제 되는지 설정
        builder.setAutoCancel(false);

        //알림 우선도 설정
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder;
    }

}