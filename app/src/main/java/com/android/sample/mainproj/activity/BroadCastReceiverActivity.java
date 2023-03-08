package com.android.sample.mainproj.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.dialog.PermissionDialog;
import com.android.sample.mainproj.log.LogService;
import com.android.sample.mainproj.receiver.BatteryReceiver;
import com.android.sample.mainproj.receiver.SMSReceiveReceiver;
import com.android.sample.mainproj.receiver.SMSSendReceiver;

/*
브로드캐스트 리시버는 안드로이드에서 발생하는 여러 브로드캐스트(이벤트)를 감지하는 역활을 한다.
예를 들어 부팅이 완료되었을 때, 날짜가 변경되었을 때 등을 알 수있다.
브로드 캐스트의 종류는 다음과 같다.
ACTION_BOOT_COMPLETED : 부팅이 완료되었을 때 발생
ACTION_BATTERY_CHANGED : 배터리 상태가 변경되었을 때 발생
ACTION_CAMERA_BUTTON : 카메라 버튼을 클릭했을 때 발생
ACTION_DATE_CHANGED : 날짜가 변경되었을 때 발생
ACTION_TIME_CHANGED : 시간이 변경되었을 때 발생
ACTION_MEDIA_BUTTON : 미디어 버튼이 클릭되었을 때 발생
ACTION_MEDIA_MOUNTED : 외부 저장 미디어를 추가하였을 때 발생
ACTION_MEDIA_UNMOUNTED : 외부 저장 미디어를 제거하였을 때 발생
ACTION_SCREEN_ON : 화면이 켜졌을 떄 발생
ACTION_SCREEN_OFF : 와면이 꺼졌을 때 발생
ACTION_TIMEZONE_CHANGED : 시간대가 변경되었을 때 발생
브로드 캐스트 리시버는 10초 이내의 작업만을 보장하므로 오랜 시간 동안 동작하는 작업은
별도의 서비스 나 스레드에 구현하여야 한다.
*/
public class BroadCastReceiverActivity extends AppCompatActivity
{
    private final int REQUEST_SEND_SMS = 1005;

    private Activity activity;

    private ImageButton ibtn_back_click;

    private TextView tv_charging_method;

    private TextView tv_charging_status;

    private TextView tv_charging_level;

    private Button btn_charging_info;

    private Button btn_boot_auto_run;

    private EditText et_receiver_number;

    private EditText et_send_message;

    private Button btn_send_message;

    private BatteryReceiver batteryReceiver;

    private ActivityResultLauncher<Intent> resultLauncher;

    private PendingIntent sendIntent;

    private PendingIntent deliveryIntent;

    private SMSSendReceiver smsSendReceiver;

    private SMSReceiveReceiver smsReceiveReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            setContentView(R.layout.activity_broadcast_receiver);

            init();

            setting();

            addListener();
        }
        catch(Exception ex)
        {
            LogService.error(this, ex.getMessage(), ex);
        }
    }

    private void init()
    {
        activity = this;

        ibtn_back_click = findViewById(R.id.ibtn_back_click);

        tv_charging_method = findViewById(R.id.tv_charging_method);

        tv_charging_status = findViewById(R.id.tv_charging_status);

        tv_charging_level = findViewById(R.id.tv_charging_level);

        btn_charging_info = findViewById(R.id.btn_charging_info);

        btn_boot_auto_run = findViewById(R.id.btn_boot_auto_run);

        et_receiver_number = findViewById(R.id.et_receiver_number);

        et_send_message = findViewById(R.id.et_send_message);

        btn_send_message = findViewById(R.id.btn_send_message);

        batteryReceiver = new BatteryReceiver();

        smsSendReceiver = new SMSSendReceiver();

        smsReceiveReceiver = new SMSReceiveReceiver();
    }

    private void setting()
    {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultCallBack);

        //송신 상태를 파악후 이벤트를 브로드캐스트 리시버로 보내주는 팬딩 인텐트
        sendIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMSSendReceiver.ACTION_DELIVERY_COMPLETE), 0);

        //수신자가 SMS를 수신하였는지 파악하는 메소드
        //운송업체에 따라 전달해 주지 않는 경우가 많다.
        deliveryIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMSSendReceiver.ACTION_DELIVERY_COMPLETE), 0);

        registerReceiver(smsSendReceiver, new IntentFilter(SMSSendReceiver.ACTION_SEND_COMPLETE));

        registerReceiver(smsSendReceiver, new IntentFilter(SMSSendReceiver.ACTION_DELIVERY_COMPLETE));

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        registerReceiver(smsReceiveReceiver, new IntentFilter(SMSReceiveReceiver.ACTION_RECEIVE_COMPLETE));
    }

    private void addListener()
    {
        ibtn_back_click.setOnClickListener(listener_back_click);

        btn_charging_info.setOnClickListener(listener_charging_info);

        btn_boot_auto_run.setOnClickListener(listener_boot_auto_run);

        btn_send_message.setOnClickListener(listener_send_message);
    }

    private View.OnClickListener listener_back_click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

            finish();
        }
    };

    private View.OnClickListener listener_charging_info = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            tv_charging_method.setText(batteryReceiver.getPlugged());

            tv_charging_status.setText(batteryReceiver.getStatus());

            tv_charging_level.setText(batteryReceiver.getLevel());

        }
    };

    private View.OnClickListener listener_boot_auto_run = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //화면 부팅시 앱을 실행하기 위해서는
            //첫 번째로 BootReceiver 작성
            //두 번째로 RECEIVE_BOOT_COMPLETE 권한이 필요
            //세 번째로 다른 화면에 그리기 권한을 위한 SYSTEM_ALERT_WINDOW 가 필요하다
            if(Settings.canDrawOverlays(activity) == false) {

                PermissionDialog dialog = new PermissionDialog(activity, "다른 앱 위에 그리기");
                dialog.setDialogClickListener(new PermissionDialog.OnDialogClickListener() {
                    @Override
                    public void onYesClick() {

                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

                        resultLauncher.launch(intent);
                    }

                    @Override
                    public void onNoClick() {

                    }
                });
                dialog.show();
            }
            else {

                Toast.makeText(activity, "부팅시 앱 자동 실행 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private View.OnClickListener listener_send_message = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            /*
            SMS 송신을 위해서는 android.permission.SEND_SMS 권한이 필요하다
             */
            try {

                if (

                    checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                )
                {
                    String[] permissions = {

                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS
                    };

                    requestPermissions(permissions, REQUEST_SEND_SMS);
                }
                else {

                    sendMessage();
                }
            }

            catch (Exception ex) {

                LogService.error(activity, ex.getMessage(), ex);
            }

        }
    };

    private ActivityResultCallback<ActivityResult> activityResultCallBack = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(Settings.canDrawOverlays(activity) == true) {

                Toast.makeText(activity, "부팅시 앱 자동 실행 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else {

                Toast.makeText(activity, "부팅시 앱 자동 실행 설정이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void sendMessage() {

        String receiver_number = et_receiver_number.getText().toString();

        String message = et_send_message.getText().toString();

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(receiver_number, null, message, sendIntent, deliveryIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_SEND_SMS) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {

                sendMessage();
            }
            else {

                PermissionDialog dialog = new PermissionDialog(activity, "SMS송신");
                dialog.setDialogClickListener(new PermissionDialog.OnDialogClickListener() {
                    @Override
                    public void onYesClick() {

                        Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

                        appDetail.addCategory(Intent.CATEGORY_DEFAULT);

                        appDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(appDetail);
                    }

                    @Override
                    public void onNoClick() {

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        BroadcastReceiver[] receivers = {batteryReceiver, smsReceiveReceiver, smsReceiveReceiver};

        for(int i = 0; i < receivers.length; i++) {

            try {

                if (receivers[i] != null) {

                    unregisterReceiver(receivers[i]);
                }
            }
            catch (RuntimeException ex) {

            }
        }
    }
}