package com.android.sample.mainproj.listener;

import android.app.Activity;
import android.widget.RadioGroup;

import com.android.sample.mainproj.R;
import com.android.sample.mainproj.log.LogService;

public class RadioCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

    private Activity activity;

    private RadioGroup radioGroup;

    private int beforecheckedId;

    private OnCheckedChangedListener callback;

    public interface OnCheckedChangedListener {

        public void onCheckedChanged(RadioGroup radioGroup, int checkedId, int beforeCheckedId);
    }

    public RadioCheckedChangeListener(Activity activity, RadioGroup radioGroup, OnCheckedChangedListener callback) {

        this.activity = activity;

        this.radioGroup = radioGroup;

        beforecheckedId = radioGroup.getCheckedRadioButtonId();

        this.callback = callback;

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

        this.onCheckedChanged(radioGroup, checkedId, beforecheckedId);

    }

    public void onCheckedChanged(RadioGroup radioGroup, int checkedID, int beforecheckedId) {

        this.callback.onCheckedChanged(radioGroup, checkedID, beforecheckedId);

        this.beforecheckedId = checkedID;
    }
}
