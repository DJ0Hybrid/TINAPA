package com.tinapaproject.tinapa.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.tinapaproject.tinapa.R;

public class SearchSpinner extends LinearLayout {

    private Spinner mSpinner;
    private EditText mTextField;
    private CursorAdapter mCursorAdapter;

    public SearchSpinner(Context context) {
        this(context, null);
    }

    public SearchSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.widget_search_spinner, this, true);

        mSpinner = (Spinner) findViewById(R.id.search_spinner_spinner);

        mTextField = (EditText) findViewById(R.id.search_spinner_text_field);

        mTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCursorAdapter != null && mCursorAdapter.getFilter() != null) {
                    mCursorAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setAdapter(CursorAdapter adapter) {
        mCursorAdapter = adapter;
        if (mSpinner != null) {
            mSpinner.setAdapter(mCursorAdapter);
        }
    }

    public void setSelection(int i, boolean b) {
        if (mSpinner != null) {
            mSpinner.setSelection(i, b);
        }
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        if (mSpinner != null) {
            mSpinner.setOnItemSelectedListener(onItemSelectedListener);
        }
    }

    public long getSelectedItemId() {
        if (mSpinner != null) {
            return mSpinner.getSelectedItemId();
        }
        return -1;
    }
}
