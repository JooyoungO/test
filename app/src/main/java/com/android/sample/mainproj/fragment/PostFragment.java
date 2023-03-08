package com.android.sample.mainproj.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.sample.mainproj.R;

public class PostFragment extends Fragment {

    private Activity activity;

    public PostFragment(Activity activity) {

        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_basic, container, false);

        Button btn_basic_input = view.findViewById(R.id.btn_basic_input);

        btn_basic_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(activity, "베이직!", Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}