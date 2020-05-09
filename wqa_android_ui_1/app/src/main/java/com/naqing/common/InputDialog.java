package com.naqing.common;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import com.github.mikephil.charting.charts.BubbleChart;
import com.naqing.monitor.model_monitor_holder;
import com.naqing.wqa_android_ui_1.R;

import java.util.ArrayList;

public class InputDialog {
    public interface InputCallBack {
        public void InputValue(String value);
    }

    public interface SelectCallBack {
        public void InputValue(int value);
    }

    // <editor-fold desc="初始化密码界面">
    public static void ShowInputDialog(Activity parent, String title, String def_value, InputCallBack handler) {
        ShowInputDialog(parent, title, def_value, handler, false);
    }

    public static void ShowInputDialog(Activity parent, String title, String def_value, InputCallBack handler, boolean passord) {
        new InputDialog().initDialog(parent, title, def_value, handler, passord);
    }

    public static int MAXPWDLEN = 32;
    public static int DIALOG_OK = 0;
    public static int DIALOG_CANCLE = 1;

    private void initDialog(Activity parent, String title, String def_value, InputCallBack handler, boolean passord) {
        /** 初始化密码窗体 */
        Display display = parent.getWindowManager().getDefaultDisplay();
        Point display_size = new Point();
        display.getSize(display_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, display_size.y * 4 / 5);
        View view = LayoutInflater.from(parent).inflate(R.layout.dialog_num_input, null, false);
        AlertDialog dialog = new AlertDialog.Builder(parent, R.style.TransparentDialog).create();

        dialog.show();
        dialog.getWindow().setLayout(display_size.x * 4 / 5, display_size.y * 4 / 5);
        dialog.setContentView(view, lp);

        /** 设置标题*/
        TextView d_title = view.findViewById(R.id.dialog_title);
        d_title.setText(title);

        TextView pwd = view.findViewById(R.id.dialog_password_input);
        pwd.setText(def_value);
        if (passord)
            pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        else
            pwd.setInputType(InputType.TYPE_CLASS_TEXT);

        /** 设置确认按钮 */
        Button button_ok = view.findViewById(R.id.pwd_button_ok);
        button_ok.setOnClickListener((View v) -> {
            handler.InputValue(pwd.getText().toString());
            dialog.dismiss();
        });

        /** 设置取消按钮 */
        Button button_cancel = view.findViewById(R.id.pwd_button_canel);
        button_cancel.setOnClickListener((View v) -> {
            dialog.dismiss();
        });

        /** 设置数字按钮 */
        this.initPwdButton(view, R.id.pwd_0);
        this.initPwdButton(view, R.id.pwd_1);
        this.initPwdButton(view, R.id.pwd_2);
        this.initPwdButton(view, R.id.pwd_3);
        this.initPwdButton(view, R.id.pwd_4);
        this.initPwdButton(view, R.id.pwd_5);
        this.initPwdButton(view, R.id.pwd_6);
        this.initPwdButton(view, R.id.pwd_7);
        this.initPwdButton(view, R.id.pwd_8);
        this.initPwdButton(view, R.id.pwd_9);
        this.initPwdButton(view, R.id.pwd_plus);
        this.initPwdButton(view, R.id.pwd_point);
        this.initPwdButton(view, R.id.pwd_miner);

        /** 设置退格按钮 */
        Button button_tmp = view.findViewById(R.id.pwd_c);
        button_tmp.setOnClickListener((View v) -> {
            String spwd = pwd.getText().toString();
            if (spwd.length() > 0) {
                pwd.setText(spwd.substring(0, spwd.length() - 1));
            }
        });

        /** 设置清除按钮 */
        button_tmp = view.findViewById(R.id.pwd_clr);
        button_tmp.setOnClickListener((View v) -> {
            pwd.setText("");
        });
    }

    private void initPwdButton(View view, int id) {
        Button button_tmp = view.findViewById(id);
        button_tmp.setOnClickListener((View v) -> {
            TextView pwd = view.findViewById(R.id.dialog_password_input);
            if (pwd.getText().toString().length() < MAXPWDLEN) {
                pwd.setText(pwd.getText().toString() + button_tmp.getText().toString());
            }
        });
    }
    // </editor-fold>

    // <editor-fold desc="初始日期输入界面">
    public static void ShowDateDialog(Activity parent, String title, String def_value, InputCallBack handler) {
        new InputDialog().initDateDialog(parent, title, def_value, handler);
    }

    private void initDateDialog(Activity parent, String title, String def_value, InputCallBack handler) {
        /** 初始化密码窗体 */
        Display display = parent.getWindowManager().getDefaultDisplay();
        Point display_size = new Point();
        display.getSize(display_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, display_size.y * 4 / 5);
        View view = LayoutInflater.from(parent).inflate(R.layout.dialog_date_input, null, false);
        AlertDialog dialog = new AlertDialog.Builder(parent, R.style.TransparentDialog).create();

        dialog.show();
        dialog.getWindow().setLayout(display_size.x * 4 / 5, display_size.y * 4 / 5);
        dialog.setContentView(view, lp);

        /** 设置标题*/
        TextView d_title = view.findViewById(R.id.dialog_title);
        d_title.setText(title);

        TextView pwd = view.findViewById(R.id.dialog_password_input);
        pwd.setText(def_value);

        date.clear();
        /** 设置确认按钮 */
        Button button_ok = view.findViewById(R.id.pwd_button_ok);
        button_ok.setOnClickListener((View v) -> {
            if(date.size() > 0) {
                while (date.size() < 2) {
                    date.add("9");
                }
                while (date.size() < 3) {
                    date.add("7");
                }
                while (date.size() < 14) {
                    date.add("0");
                }
                UpdateText(view);
            }
            handler.InputValue(pwd.getText().toString());
            dialog.dismiss();
        });

        /** 设置取消按钮 */
        Button button_cancel = view.findViewById(R.id.pwd_button_canel);
        button_cancel.setOnClickListener((View v) -> {
            dialog.dismiss();
        });

        /** 设置数字按钮 */
        this.initDateButton(view, R.id.pwd_0);
        this.initDateButton(view, R.id.pwd_1);
        this.initDateButton(view, R.id.pwd_2);
        this.initDateButton(view, R.id.pwd_3);
        this.initDateButton(view, R.id.pwd_4);
        this.initDateButton(view, R.id.pwd_5);
        this.initDateButton(view, R.id.pwd_6);
        this.initDateButton(view, R.id.pwd_7);
        this.initDateButton(view, R.id.pwd_8);
        this.initDateButton(view, R.id.pwd_9);

        /** 设置退格按钮 */
        Button button_tmp = view.findViewById(R.id.pwd_c);
        button_tmp.setOnClickListener((View v) -> {
            if (!date.isEmpty()) {
                date.remove(date.size() - 1);
                UpdateText(view);
            }
        });

        /** 设置清除按钮 */
        button_tmp = view.findViewById(R.id.pwd_clr);
        button_tmp.setOnClickListener((View v) -> {
            date.clear();
            UpdateText(view);
        });
    }

    private void initDateButton(View view, int id) {
        Button button_tmp = view.findViewById(id);
        button_tmp.setOnClickListener((View v) -> {
            TextView pwd = view.findViewById(R.id.dialog_password_input);
            if (pwd.getText().toString().length() < MAXPWDLEN) {
                date.add(button_tmp.getText().toString());
                UpdateText(view);
            }
        });
    }

    private ArrayList<String> date = new ArrayList();
    private String pattern = "####-##-## ##:##:##";

    private void UpdateText(View view) {
        String tmp_pattern = pattern;
        for (String tmp : date) {
            tmp_pattern = tmp_pattern.replaceFirst("#", tmp);
        }
        TextView pwd = view.findViewById(R.id.dialog_password_input);
        pwd.setText(tmp_pattern);
    }
    // </editor-fold>

    // <editor-fold desc="初始话选择列表">
    public static void ShowListDialog(Activity parent, String[] slist, View.OnClickListener list) {
        new InputDialog().initListDialog(parent, slist, Gravity.CENTER, list);
    }
    public static void ShowListDialog(Activity parent, String[] slist, int gravity, View.OnClickListener list) {
        new InputDialog().initListDialog(parent, slist, gravity, list);
    }

    private void initListDialog(Activity parent, String[] slist, int gravity, View.OnClickListener list) {
        /** 初始化密码窗体 */
        Display display = parent.getWindowManager().getDefaultDisplay();
        Point display_size = new Point();
        display.getSize(display_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, display_size.y * 4 / 5);
        View view = LayoutInflater.from(parent).inflate(R.layout.dialog_select_list, null, false);
        AlertDialog dialog = new AlertDialog.Builder(parent, R.style.TransparentDialog).create();

        dialog.show();
        dialog.getWindow().setLayout(display_size.x * 3 / 5, display_size.y * 4 / 5);
        dialog.setContentView(view, lp);

        /** 设置标题*/
        LinearLayout d_title = view.findViewById(R.id.dlg_sel_list);
        for (int i = 0; i <slist.length; i++) {
            View tt = createButton(parent, slist[i], gravity);
            tt.setOnClickListener((View tview)->{
                list.onClick(tt);
                dialog.dismiss();
            });
            d_title.addView(tt);
        }
    }

    private Button createButton(Activity activity, String text, int gravity) {
        Button name = new Button(new ContextThemeWrapper(activity, R.style.common_button_style));
        name.setTextColor(Color.WHITE);
        name.setText(text);
        name.setGravity(gravity);
        return name;
    }
    // </editor-fold>
}
