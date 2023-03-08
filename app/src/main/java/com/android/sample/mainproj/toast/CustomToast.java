package com.android.sample.mainproj.toast;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sample.mainproj.R;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;

public class CustomToast {

    private static Queue<Dialog> dialogs = new LinkedList<>();

    // WeakReference 의 경우 백그라운드에서 남아있는 내역도
    // 자동으로 Garbage Collection에 의헤서 우선적으로 정리되도록 선언하기 위한 객체이다.
    // Garbage Collection 정리의 최우선 대상이 된다.
    private WeakReference<Activity> activity; // 약한 참조

    private Dialog dialog;

    // 에니메이션 효과 시간
    private int duration = 700;

    // 글자가 사라지는 시간
    private int fadeOutDuration = 4000;

    // 토스트 사이의 거리
    private float translationDistance = 60;

    public static CustomToast makeToast(Activity activity, String text) {

        return new CustomToast(activity, text);
    }

    private CustomToast(Activity activity, String messageText) {

        this.activity = new WeakReference<>(activity);

        if(this.activity.get() != null) {

            dialog = new Dialog(this.activity.get()) {

                @Override
                public void onBackPressed() {

                    CustomToast.this.activity.get().onBackPressed();
                }
            };

            initToast(dialog, messageText);

            dialogs.add(dialog);
        }
    }

    private void initToast(Dialog dialog, String messageText) {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.toast_custom);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.getWindow().setDimAmount(0.0F);

        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // 다이얼로그 실행 중 다이얼로그는 터치가 되지 않도록 설정
        // 다이얼로그 실행 중 바깥을 터치할 수 있도록 설정
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.tv_toast_msg)).setText(messageText);
        ((TextView) dialog.findViewById(R.id.tv_toast_left_blur)).setText(messageText);
        ((TextView) dialog.findViewById(R.id.tv_toast_right_blur)).setText(messageText);

    }

    public CustomToast setTextColor(int color) {

        ((TextView) dialog.findViewById(R.id.tv_toast_msg)).setTextColor(color);
        ((TextView) dialog.findViewById(R.id.tv_toast_left_blur)).setTextColor(color);
        ((TextView) dialog.findViewById(R.id.tv_toast_right_blur)).setTextColor(color);

        return this;
    }

    public CustomToast setTextTypeface(Typeface typeface) {

        ((TextView) dialog.findViewById(R.id.tv_toast_msg)).setTypeface(typeface);
        ((TextView) dialog.findViewById(R.id.tv_toast_left_blur)).setTypeface(typeface);
        ((TextView) dialog.findViewById(R.id.tv_toast_right_blur)).setTypeface(typeface);

        return this;
    }

    public void show() {

        int dialogPos = dialogs.size() -1;

        dialog.show();

        for(Dialog tmpDialog : dialogs) {

            tmpDialog.getWindow().getDecorView().animate()
                    .translationY(-dialogPos * translationDistance)
                    .setStartDelay(0).setDuration(this.duration).start();

            dialogPos--;
        }

        // dialog의 루트 뷰(xxx.xml) 의 정보는 DecorView를 통해 취득
        View decorView = dialog.getWindow().getDecorView();

        decorView.animate().alpha(0.0f).setStartDelay(this.duration).setDuration(this.fadeOutDuration).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(! activity.get().isFinishing()) {

                    dissmissDialog();

                    dialogs.poll();
                }
            }
        }, this.duration + this.fadeOutDuration);

    }

    private void dissmissDialog() {

        if(dialog != null && dialog.isShowing()) {

            dialog.dismiss();
        }
    }
}
