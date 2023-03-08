package com.android.sample.mainproj.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.sample.mainproj.R;

import java.util.List;

public class ClickApplyAdapter extends RecyclerView.Adapter<ClickApplyAdapter.ClickApplyHolder> {

    private  Activity activity;

    private List<String> itemNameList;

    private View.OnClickListener clickListener;

    private View.OnCreateContextMenuListener menuListener;

    private int currentPosition = 0;

    public ClickApplyAdapter(Activity activity, List<String> itemNameList, View.OnClickListener clickListener, View.OnCreateContextMenuListener menuListener) {

        this.activity = activity;

        this.itemNameList = itemNameList;

        this.clickListener = clickListener;

        this.menuListener = menuListener;

    }

    public class ClickApplyHolder extends RecyclerView.ViewHolder {

        private TextView tv_click_name;

        private Button btn_click_item;

        public ClickApplyHolder(@NonNull View itemView) {

            super(itemView);

            tv_click_name = itemView.findViewById(R.id.tv_click_name);

            btn_click_item = itemView.findViewById(R.id.btn_click_item);
        }
    }


    @NonNull
    @Override
    public ClickApplyAdapter.ClickApplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(activity);

        View itemView = layoutInflater.inflate(R.layout.item_click_apply, parent, false);

        return new ClickApplyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClickApplyAdapter.ClickApplyHolder holder, int position) {

        String itemName = itemNameList.get(position);

        holder.tv_click_name.setText(itemName);

        holder.btn_click_item.setOnClickListener(clickListener);

        holder.btn_click_item.setTag(position);

        holder.btn_click_item.setOnCreateContextMenuListener(menuListener);

    }

    @Override
    public int getItemCount() {

        return itemNameList.size();
    }

    public int getCurrentPosition() {

        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {

        this.currentPosition = currentPosition;
    }
}
