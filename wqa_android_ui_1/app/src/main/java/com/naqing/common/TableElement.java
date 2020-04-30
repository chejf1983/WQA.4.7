package com.naqing.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.view.ContextThemeWrapper;

import com.naqing.wqa_android_ui_1.R;

import wqa.dev.intf.SConfigItem;

public class TableElement {

    public static View initSelectView(TextView name, Activity activity, String def_value, String[] range, View.OnClickListener list) {
        name.setText(def_value);
        name.setOnClickListener((View view) -> {
            InputDialog.ShowListDialog(activity, range, (View vv) -> {
                name.setText(((TextView) vv).getText().toString());
                if (list != null)
                    list.onClick(name);
            });
        });
        name.setGravity(Gravity.CENTER);
        return name;
    }

    // <editor-fold desc="创建单项">

    /**
     * 新建一行
     */
    public static LinearLayout createLine(Context context) {
        /** 新建一行*/
        LinearLayout row = new LinearLayout(context);
        row.setWeightSum(2);
        row.setOrientation(TableRow.HORIZONTAL);
        return row;
    }

    /**
     * 创建文本View
     */
    public static View createTextView(Context context, String value) {
        TextView name = new TextView(new ContextThemeWrapper(context, R.style.common_text));
        name.setGravity(Gravity.CENTER);
        name.setText(value);
        return name;
    }

    public static View createInputView(Activity activity, String dialog_name, String defvalue, View.OnClickListener listener) {
        TextView tvalue = new TextView(new ContextThemeWrapper(activity, R.style.common_input_text));
        tvalue.setText(defvalue);
        tvalue.setOnClickListener((View vew) -> {
            InputDialog.ShowInputDialog(activity, dialog_name, defvalue, (String value) -> {
                tvalue.setText(value);
                if (listener != null)
                    listener.onClick(tvalue);
            });
        });
        return tvalue;
    }

    public static View createSwitchView(Activity activity, boolean value, CompoundButton.OnCheckedChangeListener listener) {
        ToggleButton s_button = new ToggleButton(activity);
        s_button.setTextOff("");
        s_button.setTextOn("");
        s_button.setBackground(activity.getResources().getDrawable(R.drawable.button_switch));
        s_button.setChecked(Boolean.valueOf(value));
        s_button.setGravity(Gravity.CENTER);
        s_button.setPadding(0, 0, 0, 0);
        s_button.setOnCheckedChangeListener(listener);
        return s_button;
    }

    public static View createButtonView(Activity activity, String text, View.OnClickListener list) {
        Button name = new Button(new ContextThemeWrapper(activity, R.style.common_button_style));
        name.setTextColor(Color.WHITE);
        name.setText(text);
        name.setOnClickListener(list);
        return name;
    }

    public static View createSelectView(Activity activity, String def_value, String[] range, View.OnClickListener list) {
        TextView name = new TextView(new ContextThemeWrapper(activity, R.style.common_input_text));
        return initSelectView(name, activity, def_value, range, list);
    }

    public static View createDateView(Activity activity, String dialog_name, String defvalue, View.OnClickListener listener) {
        TextView tvalue = new TextView(new ContextThemeWrapper(activity, R.style.common_input_text));
        tvalue.setText(defvalue);
        tvalue.setOnClickListener((View vew) -> {
            InputDialog.ShowDateDialog(activity, dialog_name, defvalue, (String value) -> {
                tvalue.setText(value);
                if (listener != null)
                    listener.onClick(tvalue);
            });
        });
        return tvalue;
    }
    // </editor-fold>

    // <editor-fold desc="创建行">

    /**
     * 创建纯文本
     */
    public static View CreateTextViewLine(Context context, String data_name, String data_info) {
        /** 新建一行*/
        LinearLayout row = createLine(context);

        /** 创建格式 */
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;

        /** 添加名称项目*/
        row.addView(createTextView(context, data_name), lp);

        /** 添加内容项目*/
        lp.setMargins(5, 5, 5, 5);
        row.addView(createTextView(context, data_info), lp);

        return row;
    }

    /**
     * 创建输入文本
     */
    public static View CreateInputViewLine(Activity activity, String data_name, String def_value, View.OnClickListener listener) {
        /** 新建一行*/
        LinearLayout row = createLine(activity);

        /** 添加名称项目*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        /** 添加名称项目*/
        row.addView(createTextView(activity, data_name), lp);

        /** 添加内容项目*/
        lp.setMargins(5, 5, 5, 5);
        row.addView(createInputView(activity, data_name, def_value, listener), lp);
        return row;
    }

    /**
     * 创建输入文本
     */
    public static View CreateDateViewLine(Activity activity, String data_name, String def_value, View.OnClickListener listener) {
        /** 新建一行*/
        LinearLayout row = createLine(activity);

        /** 添加名称项目*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        /** 添加名称项目*/
        row.addView(createTextView(activity, data_name), lp);

        /** 添加内容项目*/
        lp.setMargins(5, 5, 5, 5);
        row.addView(createDateView(activity, data_name, def_value, listener), lp);
        return row;
    }

    /**
     * 创建开关项2
     */
    public static View CreateSwitchViewLine(Activity activity, String data_name, boolean value, CompoundButton.OnCheckedChangeListener listener) {
        /** 新建一行*/
        LinearLayout row = createLine(activity);

        /** 添加名称项目*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        /** 添加名称项目*/
        row.addView(createTextView(activity, data_name), lp);

        /** 添加内容项目*/
        lp.setMargins(5, 5, 5, 5);
        row.addView(createSwitchView(activity, value, listener), lp);
        return row;
    }

    /**
     * 创建选择项
     */
    public static View CreateSelectViewLine(Activity activity, String data_name, String def_value, String[] range, View.OnClickListener list) {
        /** 新建一行*/
        LinearLayout row = createLine(activity);

        /** 添加名称项目*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        /** 添加名称项目*/
        row.addView(createTextView(activity, data_name), lp);

        /** 添加内容项目*/
        lp.setMargins(5, 5, 5, 5);


        row.addView(createSelectView(activity, def_value, range, list), lp);
        return row;
    }

    /**
     * 创建按钮
     */
    public static View CreateButtonViewLine(Activity activity, String data_name, String text, View.OnClickListener list) {
        /** 新建一行*/
        LinearLayout row = createLine(activity);

        /** 添加名称项目*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        /** 添加名称项目*/
        row.addView(createTextView(activity, data_name), lp);

        /** 添加内容项目*/
        lp.setMargins(5, 5, 5, 5);
        row.addView(createButtonView(activity, text, list), lp);
        return row;
    }

    /**
     * 根据配置项创建行
     */
    public static View CreateLine(Activity parent, SConfigItem item) {
        switch (item.inputtype) {
            case W:
                return TableElement.CreateInputViewLine(parent, item.data_name, item.GetValue(), (View view) -> {
                    item.SetValue(((TextView) view).getText().toString());
                });
            case B:
                return TableElement.CreateSwitchViewLine(parent, item.data_name, Boolean.valueOf(item.GetValue()), (CompoundButton var1, boolean var2) -> {
                    item.SetValue(String.valueOf(var2));
                });
            case S:
                return TableElement.CreateSelectViewLine(parent, item.data_name, item.GetValue(), item.range, (View view) -> {
                    item.SetValue(((TextView) view).getText().toString());
                });
            case R:
                return TableElement.CreateTextViewLine(parent, item.data_name, item.GetValue());
            default:
        }
        return TableElement.CreateTextViewLine(parent, item.data_name, item.GetValue());
    }
    // </editor-fold>
}
