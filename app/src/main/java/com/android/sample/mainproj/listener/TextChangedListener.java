package com.android.sample.mainproj.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextChangedListener implements TextWatcher {

    public interface OnTextChangedListener {

        public void onTextChanged(EditText editText, int beforeCursorPos, String beforeText, String currentText);
    }
    private int beforeCursorPos;

    private String beforeText;

    private String currentText;

    private EditText editText;

    private OnTextChangedListener callback;

    public TextChangedListener(EditText editText, OnTextChangedListener callback) {

        this.editText = editText;

        this.callback = callback;
    }

    // 텍스트 뷰의 문자열이 변경되기 전 문자를 반환하는 메소드
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        beforeCursorPos = start;

        beforeText = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        currentText = s.toString();
    }

    @Override
    public void afterTextChanged(Editable editable) {

        this.callback.onTextChanged(editText, beforeCursorPos, beforeText, currentText);
    }
}
