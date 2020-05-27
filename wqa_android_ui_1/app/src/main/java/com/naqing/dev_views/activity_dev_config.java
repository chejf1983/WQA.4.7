package com.naqing.dev_views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.naqing.wqa_android_ui_1.R;

import java.util.ArrayList;
import java.util.logging.Level;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.config.DevCalConfig;
import wqa.control.config.DevConfigBean;
import wqa.control.config.DevConfigTable;
import wqa.control.config.DevMotorConfig;

/**
 * 设备配置界面，主要包括设备信息，参数配置，电机配置，定标界面
 * */
public class activity_dev_config extends AppCompatActivity {

    /**设备配置模块*/
    public static DevConfigBean configbean;
    public static model_dev_view dev_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_config);

        /**设置设备名称*/
        ((TextView)findViewById(R.id.dev_config_name)).setText(getIntent().getStringExtra("name"));

        /**初始配置列表*/
        this.initView();

        /** 设置消息响应*/
        configbean.SetMessageImple((String s)->{
            LogCenter.Instance().ShowMessBox(Level.INFO, s);
        });

        /**设置退出按钮*/
        View quit = findViewById(R.id.dev_config_quit);
        quit.setOnClickListener((View view) -> {
            configbean.Quit();
            activity_dev_config.this.finish();
        });

        /**设置退出事件*/
        configbean.CloseEvent.RegeditListener(new EventListener() {
            @Override
            public void recevieEvent(Event event) {
                activity_dev_config.this.finish();
            }
        });
    }

    // <editor-fold desc="初始化">
    /**初始化fragement界面*/
    private void initView() {
        if(configbean == null){
            finish();
        }

        /** 创建配置列表*/
        for(DevConfigTable config_table : configbean.GetBaseDevConfig()){
            initConfigTable(config_table);
        }

        /**创建定标界面*/
        this.initCalTable(configbean.GetDevCalConfig());

        /** 创建电机控制界面*/
        this.initMotoTable(configbean.GetMotorConfig());

        /** 创建显示控制界面*/
        initViewSet(dev_view);
    }
    // </editor-fold>

    // <editor-fold desc="界面">
    /**配置fragments列表*/
    ArrayList<Fragment> fragments = new ArrayList();
    /**初始化通用配置界面*/
    private void initConfigTable(DevConfigTable config_table){
        /**创建通用设置fragment*/
        fragment_dev_config_table table = new fragment_dev_config_table(config_table);
        fragments.add(table);

        /**设置切换按钮*/
        RadioButton button = initButton(config_table.GetListName());
        button.setOnCheckedChangeListener((CompoundButton var1, boolean checked)->{
            FragmentTransaction fragmentTransaction = activity_dev_config.this.getSupportFragmentManager().beginTransaction();
            if(checked) {
                /**重新设置一下config_table 模块，避免丢失*/
                table.reset_calconfig(config_table);
                fragmentTransaction.replace(R.id.fragment_dev_config_area, table).commit();
            }
        });

        /**选中第一个配置模块*/
        if(fragments.size() == 1){
            button.setChecked(true);
        }
    }

    /**初始化定标界面*/
    private void initCalTable(DevCalConfig cal_config){
        RadioButton button = initButton("参数校准");
        fragment_dev_config_cal table = new fragment_dev_config_cal(cal_config);
        fragments.add(table);
        button.setOnCheckedChangeListener((CompoundButton var1, boolean checked)->{
            FragmentTransaction fragmentTransaction = activity_dev_config.this.getSupportFragmentManager().beginTransaction();
            if(checked) {
                table.reset_calconfig(cal_config);
                fragmentTransaction.replace(R.id.fragment_dev_config_area, table).commit();
            }
        });
    }

    /**初始化显示设置界面*/
    private void initViewSet(model_dev_view config){
        if(config == null){
            return;
        }

        RadioButton button = initButton("显示设置");
        fragement_dev_viewset table = new fragement_dev_viewset(config);
        fragments.add(table);
        button.setOnCheckedChangeListener((CompoundButton var1, boolean checked)->{
            FragmentTransaction fragmentTransaction = activity_dev_config.this.getSupportFragmentManager().beginTransaction();
            if(checked) {
                table.reset_calconfig(config);
                fragmentTransaction.replace(R.id.fragment_dev_config_area, table).commit();
            }
        });
    }

    /**初始化电机控制界面*/
    private void initMotoTable(DevMotorConfig config){
        if(config == null){
            return;
        }

        RadioButton button = initButton("电机控制");
        fragment_dev_config_motor table = new fragment_dev_config_motor(config);
        fragments.add(table);
        button.setOnCheckedChangeListener((CompoundButton var1, boolean checked)->{
            FragmentTransaction fragmentTransaction = activity_dev_config.this.getSupportFragmentManager().beginTransaction();
            if(checked) {
                table.reset_calconfig(config);
                fragmentTransaction.replace(R.id.fragment_dev_config_area, table).commit();
            }
        });
    }
    // </editor-fold>

    /**初始化列表按钮*/
    private RadioButton initButton(String name) {
        RadioGroup radioGroup = findViewById(R.id.dev_config_rbgroup);
//        RadioButton radioButton = new RadioButton(new ContextThemeWrapper(this, R.style.config_radio_style));
        RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.model_button, null);
        radioButton.setText(name);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        radioGroup.addView(radioButton, lp);
        return radioButton;
    }
}
