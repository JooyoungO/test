package com.android.sample.mainproj.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.listener.RadioCheckedChangeListener;
import com.android.sample.mainproj.log.LogService;

import java.util.Timer;
import java.util.TimerTask;

public class HandlerActivity extends AppCompatActivity {

    private Activity activity;

    private ImageButton ibtn_back_click;

    private TextView tv_handler_time;

    private Button btn_not_handler_start;

    private Button btn_handler_start, btn_handler_stop;

    private Button btn_handler_delay_start, btn_handler_delay_cancel;

    private Button btn_timer_start;

    private RadioGroup rg_timer;

    private RadioButton rb_timer_wiget, rb_timer_runonui, rb_timer_handler;

    private Button btn_timer_stop;

    private Thread errorThread;

    private Thread handlerThread;

    private Handler handler;

    private Timer timer;

    private HandlerTask handlerTask;

    //1 : 위젯 포스트 사용, 2 : runOnUi 사용, 3 : Handler 사용
    private int taskMode = 1;

    private int count = 0;

    private final int SET_RUN_TIME = 0;

    private Boolean flagHandle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_handler);

            init();

            setting();

            addListener();
        }
        catch(Exception ex) {

            LogService.error(this, ex.getMessage(), ex);
        }
    }

    private void init() {

        activity = this;

        timer = new Timer();

        ibtn_back_click = findViewById(R.id.ibtn_back_click);

        tv_handler_time = findViewById(R.id.tv_handler_time);

        btn_not_handler_start = findViewById(R.id.btn_not_handler_start);

        btn_handler_start = findViewById(R.id.btn_handler_start);

        btn_handler_stop = findViewById(R.id.btn_handler_stop);

        btn_handler_delay_start = findViewById(R.id.btn_handler_delay_start);

        btn_handler_delay_cancel = findViewById(R.id.btn_handler_delay_cancel);

        btn_timer_start = findViewById(R.id.btn_timer_start);

        rg_timer = findViewById(R.id.rg_timer);

        rb_timer_wiget = findViewById(R.id.rb_timer_wiget);

        rb_timer_runonui = findViewById(R.id.rb_timer_runonui);

        rb_timer_handler = findViewById(R.id.rb_timer_handler);

        btn_timer_stop = findViewById(R.id.btn_timer_stop);
    }

    private void setting() {

        errorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {

                    count++;

                    try {
                        tv_handler_time.setText(" - " + count + " - ");
                        //LogService.info(activity, " - " + count + " - ");

                        Thread.sleep(1000);
                    }
                    catch (Exception ex) {

                        LogService.error(activity, ex.getMessage(), ex);
                        break;
                    }
                }
            }
        });

        handlerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    count++;

                    try {

                        Message msg = handler.obtainMessage();
                        msg.what = SET_RUN_TIME;        //Message 구분 값
                        msg.obj = " - " + count + " - "; //실제 Message Data

                       handler.sendMessage(msg);

                        Thread.sleep(1000);
                    }
                    catch (InterruptedException iex) {

                        LogService.info(activity, "쓰레드 종료");
                        break;
                    }
                    catch (Exception ex) {

                        LogService.error(activity, ex.getMessage(), ex);
                        break;
                    }
                }

            }
        });

        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {

                super.handleMessage(msg);

                if(msg.what == SET_RUN_TIME) {

                    String data = (String) msg.obj;

                    tv_handler_time.setText(data);
                }
            }
        };

        handlerTask = new HandlerTask(tv_handler_time, taskMode, count);

    }

    private void addListener() {

        ibtn_back_click.setOnClickListener(listener_back_click);

        btn_not_handler_start.setOnClickListener(listener_not_handler_start);

        btn_handler_start.setOnClickListener(listener_handler_start);

        btn_handler_stop.setOnClickListener(listener_handler_stop);
        
        btn_handler_delay_start.setOnClickListener(listener_handler_delay_start);

        btn_handler_delay_cancel.setOnClickListener(listener_handler_delay_cancel);

        btn_timer_start.setOnClickListener(listener_timer_start);

        btn_timer_stop.setOnClickListener(listener_timer_stop);

        rg_timer.setOnCheckedChangeListener(new RadioCheckedChangeListener(activity, rg_timer,listener_timer_mode_change));



    }

    private View.OnClickListener listener_back_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            finish();
        }
    };

    private View.OnClickListener listener_not_handler_start = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            errorThread.start();
        }
    };

    private View.OnClickListener listener_handler_start = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(flagHandle == false) {

                handlerThread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        while(true) {

                            count++;

                            try {

                                Message msg = handler.obtainMessage();
                                msg.what = SET_RUN_TIME;        //Message 구분 값
                                msg.obj = " - " + count + " - "; //실제 Message Data

                                handler.sendMessage(msg);

                                Thread.sleep(1000);
                            }
                            catch (InterruptedException iex) {

                                LogService.info(activity, "쓰레드 종료");
                                break;
                            }
                            catch (Exception ex) {

                                LogService.error(activity, ex.getMessage(), ex);
                                break;
                            }
                        }

                    }
                });

                handlerThread.start();

                flagHandle = true;
            }

        }
    };

    private View.OnClickListener listener_handler_stop = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            handlerThread.interrupt();

            flagHandle = false;
        }
    };

    private View.OnClickListener listener_handler_delay_start = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    
                    tv_handler_time.setText("지연 실행");
                    Toast.makeText(activity, "핸들러 지연 실행", Toast.LENGTH_SHORT).show();
                    
                }
            }, 3000);
        }
    };

    private View.OnClickListener listener_handler_delay_cancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            handler.removeCallbacksAndMessages(null);
        }
    };

    private View.OnClickListener listener_timer_start = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(handlerTask.hasStarted() == false) {

                timer.schedule(handlerTask, 0, 1000);

            }
        }
    };

    private View.OnClickListener listener_timer_stop = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            handlerTask.cancel();
            count = handlerTask.getCount();
            handlerTask = new HandlerTask(tv_handler_time, taskMode, count);
        }
    };

    private RadioCheckedChangeListener.OnCheckedChangedListener listener_timer_mode_change = new RadioCheckedChangeListener.OnCheckedChangedListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId, int beforeCheckedId) {

            if(checkedId == R.id.rb_timer_wiget) {

                taskMode = 1;
            }
            else if(checkedId == R.id.rb_timer_runonui) {

                taskMode = 2;
            }
            else if(checkedId == R.id.rb_timer_handler) {

                taskMode = 3;
            }

            if(handlerTask.hasStarted() == true) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("타이머가 이미 실행중입니다. \n현재 실행중인 타이머를 중지하시고 변경하시겠습니까?");

                builder.setPositiveButton("에", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        listener_timer_stop.onClick(btn_timer_stop);
                    }
                });

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        rg_timer.setOnCheckedChangeListener(null);

                        if(beforeCheckedId == R.id.rb_timer_wiget) {

                            taskMode = 1;

                            ((RadioButton) rg_timer.getChildAt(0)).setChecked(true);
                        }
                        else if(beforeCheckedId == R.id.rb_timer_runonui) {

                            taskMode = 2;

                            ((RadioButton) rg_timer.getChildAt(1)).setChecked(true);

                        }
                        else if(beforeCheckedId == R.id.rb_timer_handler) {

                            taskMode = 3;

                            ((RadioButton) rg_timer.getChildAt(2)).setChecked(true);
                        }

                         rg_timer.setOnCheckedChangeListener(new RadioCheckedChangeListener(activity, rg_timer,listener_timer_mode_change));

                    }
                });

                builder.setCancelable(false);
                builder.show();
            }
            else {

                handlerTask.setTaskMode(taskMode);

            }

        }
    };

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if(handlerThread != null) {

            handlerThread.interrupt();
            handlerThread = null;
        }

        LogService.info(activity, "화면 종료");
    }

    public class HandlerTask extends TimerTask {

        private TextView tv_handler_time;

        private int taskMode;

        private int count;

        private boolean checkRun = false;

        public int getCount() {

            return count;
        }

        public boolean hasStarted() {

            return checkRun;
        }

        public void setTaskMode(int taskMode) {

            this.taskMode = taskMode;
        }

        private HandlerTask(TextView tv_handler_time, int taskMode, int count) {

            this.tv_handler_time = tv_handler_time;

            this.taskMode = taskMode;

            this.count = count;

        }

        @Override
        public void run() {

            checkRun = true;

            count++;

            if(taskMode == 1) {

                LogService.info(activity, "위젯에 post 호출");
                //1. view 위젯에 post를 호출
                tv_handler_time.post(new Runnable() {
                    @Override
                    public void run() {

                        tv_handler_time.setText(" - " + count + " - ");

                    }
                });
            }
            else if(taskMode == 2) {

                LogService.info(activity, "runOnUiThread 사용 호출");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tv_handler_time.setText(" - " + count + " - ");

                    }
                });
            }
            else if(taskMode == 3) {

                LogService.info(activity, "handler 사용 호출");

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        tv_handler_time.setText(" - " + count + " - ");

                    }
                });
            }
        }
    }

}