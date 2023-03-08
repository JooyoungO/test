package com.android.sample.mainproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.log.LogService;
import com.android.sample.mainproj.util.ConvertUnitUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScrollActivity extends AppCompatActivity {

    private Activity activity;

    private ImageButton ibtn_back_click;

    private FloatingActionButton fab_scroll;

    private LinearLayout layout_scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_scroll);

            init();

            setting();

            addListener();

        }
        catch (Exception ex) {

            LogService.error(this, ex.getMessage(), ex);
        }

    }

    private void init() {

        ibtn_back_click = findViewById(R.id.ibtn_back_click);

        fab_scroll = findViewById(R.id.fab_scroll);

        layout_scroll = findViewById(R.id.layout_scroll);

        activity = this;

    }

    private void setting() {

    }

    private void addListener() {

        ibtn_back_click.setOnClickListener(listener_back_click);

        fab_scroll.setOnClickListener(listener_fab_scroll);

    }

    private View.OnClickListener listener_back_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            finish();
        }
    };

    private View.OnClickListener listener_fab_scroll = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            layout_scroll.addView(createNewView());
        }
    };

    private TextView createNewView() {

        TextView textView = new TextView(activity);

        textView.setText("새롭게 추가된 뷰입니다.");

        textView.setLayoutParams
                (
                        new LinearLayout.LayoutParams(

                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ConvertUnitUtil.ConvertSizeToDP(activity, 50)

                        )
                );

        textView.setGravity(Gravity.CENTER);

        textView.setTextColor(Color.rgb(0, 0, 0));

        textView.setBackgroundColor(Color.rgb(255, 174, 201));

        return textView;
    }
}