package com.naqing.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.naqing.wqa_android_ui_1.R;

import java.util.Timer;
import java.util.TimerTask;

public class NQProcessDialog2 {
    private AlertDialog mProgressDialog;
    private NQProcessView process;

    private NQProcessDialog2(AlertDialog mProgressDialog, NQProcessView process) {
        this.mProgressDialog = mProgressDialog;
        this.process = process;
    }

    public static NQProcessDialog2 ShowProcessDialog(Activity parent, String title) {
        /** 初始化密码窗体 */
        Display display = parent.getWindowManager().getDefaultDisplay();
        Point display_size = new Point();
        display.getSize(display_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
        View view = LayoutInflater.from(parent).inflate(R.layout.dialog_process, null, false);
        AlertDialog dialog = new AlertDialog.Builder(parent, R.style.TransparentDialog).create();

        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setLayout(display_size.x * 4 / 5, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view, lp);

        TextView d_title = view.findViewById(R.id.dialog_title);
        d_title.setText(title);
        /** 设置标题*/
        NQProcessView process = view.findViewById(R.id.dialog_process_bar);

        return new NQProcessDialog2(dialog, process);
    }

    public interface TimeoutEvent {
        void TimeOut();
    }

    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            process.setProgress(msg.what);
        }
    };

    public void SetPecent(int pecent) {
        if (!isFinished())
            messagehandler.sendEmptyMessage(pecent);
            //process.setProgress(pecent);
    }

    public boolean isFinished() {
        return mProgressDialog == null;
    }

    public void Finish() {
        if (!isFinished()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void SetTimout(int timeout, NQProcessDialog2.TimeoutEvent event) {
        if (!isFinished())
        /** 增加超时机制 */
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mProgressDialog != null) {
                        Finish();
                        event.TimeOut();
                    }
                }
            }, timeout);
    }
}
