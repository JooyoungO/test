package com.android.sample.mainproj.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.adapter.ClickApplyAdapter;
import com.android.sample.mainproj.log.LogService;

import java.util.ArrayList;
import java.util.List;

public class ClickApplyActivity extends AppCompatActivity implements View.OnClickListener, View.OnCreateContextMenuListener {

    private Activity activity;

    private ImageButton ibtn_back_click;

    private EditText et_click_item_add;

    private Button btn_click_item_add;

    private RecyclerView rv_click_apply;

    private ClickApplyAdapter clickApplyAdapter;

    private List<String> itemNameList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_click_apply);

            init();

            setting();

            addListener();

        }
        catch (Exception ex) {

            LogService.error(this, ex.getMessage(), ex);

        }

    }

    private void init() {

        activity = this;

        ibtn_back_click = findViewById(R.id.ibtn_back_click);

        et_click_item_add = findViewById(R.id.et_click_item_add);

        btn_click_item_add = findViewById(R.id.btn_click_item_add);

        rv_click_apply = findViewById(R.id.rv_click_apply);

        itemNameList = new ArrayList<>();

        clickApplyAdapter = new ClickApplyAdapter(activity, itemNameList, this, this);

    }

    private void setting() {

        rv_click_apply.setAdapter(clickApplyAdapter);

        //리니어 레이아웃 매니저로 수평으로 배치 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_click_apply.setLayoutManager(linearLayoutManager);

    }

    private void addListener() {

        ibtn_back_click.setOnClickListener(this);

        btn_click_item_add.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.ibtn_back_click) {

            finish();

        }
        else if(view.getId() == R.id.btn_click_item_add) {

            String item = et_click_item_add.getText().toString();

            if(item.isEmpty()) {

                Toast.makeText(activity, "추가할 아이템 이름을 입력하여 주세요", Toast.LENGTH_SHORT).show();

            }
            else {

                itemNameList.add(item);

                clickApplyAdapter.notifyDataSetChanged();
            }
        }
        else if(view.getId() == R.id.btn_click_item) {

            Toast.makeText(activity, view.getTag().toString() + " 클릭", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        //v.getTag()

        clickApplyAdapter.setCurrentPosition((int) v.getTag());

        menu.setHeaderTitle("텍스트 배경 변경");

        //groupId, ItemId, Order
        menu.add(0, 201, 0, "빨강");
        menu.add(0, 202, 0, "파랑");
        menu.add(0, 203, 0, "초록");

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        LogService.info(activity, "position : " + clickApplyAdapter.getCurrentPosition());

        int position = clickApplyAdapter.getCurrentPosition();

        TextView itemView = rv_click_apply.findViewHolderForLayoutPosition(position).itemView.findViewById(R.id.tv_click_name);

        if(item.getItemId() == 201) {

            itemView.setBackgroundColor(Color.RED);

        }
        else if(item.getItemId() == 202) {

            itemView.setBackgroundColor(Color.BLUE);

        }
        else if(item.getItemId() == 203) {

            itemView.setBackgroundColor(Color.GREEN);

        }

        return super.onContextItemSelected(item);
    }
}