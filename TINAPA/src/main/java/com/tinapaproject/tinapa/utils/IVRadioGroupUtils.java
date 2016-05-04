package com.tinapaproject.tinapa.utils;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tinapaproject.tinapa.R;

public class IVRadioGroupUtils {

    public static String getIVValueSelected(RadioGroup radioGroup, Context context) {
        RadioButton selectedButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        if (selectedButton.getText().equals(context.getString(R.string.planned_iv_min))) {
            return "0";
        } else if (selectedButton.getText().equals(context.getString(R.string.planned_iv_max))) {
            return "31";
        } else {
            return "-1";
        }
    }

    public static void setCorrectIVValue(String ivValue, RadioGroup radioGroup) {
        String radioButtonTitle;
        if (ivValue.equals("31")) {
            radioButtonTitle = radioGroup.getContext().getString(R.string.planned_iv_max);
        } else if (ivValue.equals("0")) {
            radioButtonTitle = radioGroup.getContext().getString(R.string.planned_iv_min);
        } else {
            radioButtonTitle = radioGroup.getContext().getString(R.string.planned_iv_random);
        }

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton && ((RadioButton) o).getText().toString().equals(radioButtonTitle)) {
                ((RadioButton) o).setChecked(true);
                return;
            }
        }
    }
}
