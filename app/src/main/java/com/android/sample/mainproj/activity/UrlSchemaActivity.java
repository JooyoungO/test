package com.android.sample.mainproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.log.LogService;

public class UrlSchemaActivity extends AppCompatActivity {

    private Activity activity;

    private ImageButton ibtn_back_click;

    private EditText et_run_package;

    private Button btn_run_package;

    private EditText et_schema_send_id;

    private EditText et_schema_send_msg;

    private TextView tv_schema_res_msg;

    private Button btn_url_schema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_url_schema);

            init();

            setting();

            addListener();
        }
        catch (Exception ex) {

            LogService.error(activity, ex.getMessage(), ex);
        }
    }

    private void init() {

        activity = this;

        ibtn_back_click = findViewById(R.id.ibtn_back_click);

        et_run_package = findViewById(R.id.et_run_package);

        btn_run_package = findViewById(R.id.btn_run_package);

        et_schema_send_id = findViewById(R.id.et_schema_send_id);

        et_schema_send_msg = findViewById(R.id.et_schema_send_msg);

        tv_schema_res_msg = findViewById(R.id.tv_schema_res_msg);

        btn_url_schema = findViewById(R.id.btn_url_schema);
    }

    private void setting() {

        checkURLSchema();

    }

    private void addListener() {

        ibtn_back_click.setOnClickListener(listener_back_click);

        btn_run_package.setOnClickListener(listener_run_package);

        btn_url_schema.setOnClickListener(listener_url_schema);
    }

    private View.OnClickListener listener_back_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    };

    private View.OnClickListener listener_run_package = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {

                String packageName = et_run_package.getText().toString();

                if(packageName.isEmpty() == false) {

                    Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

                    if(intent == null) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setMessage("앱이 설치되지 않았습니다. 앱 스토어에서 설치를 진행하시겠습니까?");

                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                marketIntent.setData(Uri.parse("market://details?id=" + packageName));
                                startActivity(marketIntent);
                            }
                        });

                        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        builder.setCancelable(false);
                        builder.show();
                    }
                    else {
                        intent.addCategory(Intent.CATEGORY_DEFAULT);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //현재 실행중인 창 내리고 새로운 패키지창 열겠다.

                        startActivity(intent);

                    }
                }
                else {

                    Toast.makeText(activity, "실행 패키지 명을 입력하여 주세요", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception ex) {

                LogService.error(activity, ex.getMessage(), ex);
            }

        }
    };

    private View.OnClickListener listener_url_schema = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {

                String id = et_schema_send_id.getText().toString();

                String msg = et_schema_send_msg.getText().toString();

                String urlSchema = "supp://android?id=" + id + "&message=" + msg;

                Intent intent = Intent.parseUri(urlSchema, Intent.URI_INTENT_SCHEME);

                startActivity(intent);
            }
            catch (Exception ex) {

                LogService.error(activity, ex.getMessage(), ex);
            }

        }
    };

    private void checkURLSchema() {

        Intent intent = getIntent();

        if(Intent.ACTION_VIEW.equals(intent.getAction())) {

            Uri uri = intent.getData();

            if(uri != null) {

                if (uri.getScheme().equals("mpp") && uri.getHost().equals("android")) {

                    String message = uri.getQueryParameter("message");

                    tv_schema_res_msg.setText(message);
                }
            }
        }
    }
}