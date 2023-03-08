package com.android.sample.mainproj.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.vo.RecycleMemberVo;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleHolder> {

    private Activity activity;

    private List<RecycleMemberVo> memberList;

    public RecycleAdapter(Activity activity, List<RecycleMemberVo> memberList) {

        this.activity = activity;

        this.memberList = memberList;

    }

    public class RecycleHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout_list_item;

        private ImageView iv_profile;

        private TextView tv_item_name;

        private TextView tv_item_age;


        public RecycleHolder(@NonNull View itemView) {
            super(itemView);

            iv_profile = itemView.findViewById(R.id.iv_custom_profile);

            tv_item_name = itemView.findViewById(R.id.tv_custom_item_name);

            tv_item_age = itemView.findViewById(R.id.tv_custom_item_age);

            layout_list_item = itemView.findViewById(R.id.layout_list_item);
        }
    }

    @NonNull
    @Override //아이템 구조에 대한
    public RecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_recycle, parent, false);

        RecycleHolder viewHolder  = new RecycleHolder(view);


        return viewHolder;

    }

    @Override // 아이템 값에 대한 것, 정보가 있어야 함
    public void onBindViewHolder(@NonNull RecycleHolder holder, int position) {

        holder.tv_item_name.setText(memberList.get(position).getName());

        holder.tv_item_age.setText(memberList.get(position).getAge());

        if(position % 2 == 1) {

            holder.iv_profile.setImageResource(R.drawable.ic_woman_profile);
        }
        else {

            holder.iv_profile.setImageResource(R.drawable.ic_profile);
        }

        holder.layout_list_item.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.down)); //AnimationUtils 에니메이션 효과를 줄 수 있는 유틸파일
    }

    @Override
    public int getItemCount() {

        return memberList.size();
    }

    public void addItem(String name, String age) {

        RecycleMemberVo recycleMemberVo = new RecycleMemberVo();

        recycleMemberVo.setName(name);

        recycleMemberVo.setAge(age);

        memberList.add(recycleMemberVo);

        this.notifyDataSetChanged();
    }
}
