package com.naqing.wqa_android_ui_1;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.naqing.control.control_info;
import com.naqing.control.fragment_control_dev;
import com.naqing.control.fragment_control_history;
import com.naqing.control.fragment_control_io;
import com.naqing.control.fragment_control_system;
import com.naqing.wqa_android_ui_1.R;

public class fragment_control_main extends Fragment {

    private Activity parent;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_controlmain, container, false);

        this.initComponents();

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.parent = (Activity)context;
    }

    // <editor-fold desc="按钮菜单初始化">
    private fragment_control_dev fragment_dev = new fragment_control_dev();
    private fragment_control_io fraggment_io = new fragment_control_io();
    private fragment_control_history fraggment_history = new fragment_control_history();
    private control_info fragment_info = new control_info();
    private fragment_control_system fragment_system = new fragment_control_system();
    private Fragment lastfragment;

    private void initComponents() {
        /** 替换fragment*/
        this.initRadioButton(R.id.config_rb_info, R.drawable.config_8, R.drawable.config_7, fragment_info);
        this.initRadioButton(R.id.config_rb_setup, R.drawable.config_14, R.drawable.config_13, fragment_system);
        this.initRadioButton(R.id.config_rb_conn, R.drawable.config_2, R.drawable.config_1, fraggment_io);
        this.initRadioButton(R.id.config_rb_devs, R.drawable.config_6, R.drawable.config_5, fragment_dev);
        this.initRadioButton(R.id.config_rb_log, R.drawable.config_10, R.drawable.config_9, fraggment_history);
        this.initRadioButton(R.id.config_rb_da, R.drawable.config_4, R.drawable.config_3, new control_info());
        this.initRadioButton(R.id.config_rb_inter, R.drawable.config_12, R.drawable.config_11, new control_info());

        RadioButton rb_temp = root.findViewById(R.id.config_rb_info);
        model_dev_view_manager.Instance().SetDevHolderAdapter(this.fragment_dev);
        rb_temp.setChecked(true);
    }

    /**
     * 初始化单个RadioButton
     */
    private void initRadioButton(int id, int drawable_check, int drawable_uncheck, Fragment fragment) {
        RadioButton rb_temp = root.findViewById(id);

        FragmentTransaction fragmentTransaction = ((FragmentActivity)parent).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_control_area,fragment);
        fragmentTransaction.hide(fragment).commit();
//        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_control_area, fragment).commit();
        rb_temp.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
            RadioButton rb_button = root.findViewById(id);
            if (b) {
                /** 替换fragment*/
                FragmentTransaction fragmentTransaction1 = ((FragmentActivity)parent).getSupportFragmentManager().beginTransaction();
                if(lastfragment != null) fragmentTransaction1.hide(lastfragment);
                fragmentTransaction1.show(fragment).commit();
                lastfragment = fragment;

                /** 替换图标 */
                rb_button.setCompoundDrawablesWithIntrinsicBounds(root.getResources().getDrawable(drawable_check), null, getResources().getDrawable(R.drawable.config_15), null);
            } else {
                rb_button.setCompoundDrawablesWithIntrinsicBounds(root.getResources().getDrawable(drawable_uncheck), null, null, null);
            }
        });
    }
    // </editor-fold>
}
