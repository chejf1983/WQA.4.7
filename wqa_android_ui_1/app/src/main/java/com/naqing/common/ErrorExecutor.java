package com.naqing.common;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/6/1.
 */
public class ErrorExecutor {
    public static Activity lastparent;
    private static IosAlertDialog dialog;

    private static Handler messagehandler = new Handler() {
        public void handleMessage(Message msg) {
//            if (dialog != null) dialog.dismiss();
//            dialog = new IosAlertDialog(lastparent).builder()
//                    .setMsg(msg.obj.toString())
//                    .setNegativeButton("确定", (View v) -> {
//                    });
//            dialog.show();
            Toast.makeText(lastparent, msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public static void PrintErrorInfo(String msg) {
        Message message = new Message();
        message.obj = msg;
        messagehandler.sendMessage(message);
//        Toast.makeText(parent, msg, Toast.LENGTH_SHORT).show();
    }
}
