package com.naqing.common;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

public class NQProcessDialog {
    private ProgressDialog mProgressDialog;

    private NQProcessDialog(ProgressDialog mProgressDialog) {
        this.mProgressDialog = mProgressDialog;
    }

    public static NQProcessDialog ShowProcessDialog(Context context, String title) {
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(title);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.setMax(100);
        mProgressDialog.show();

        return new NQProcessDialog(mProgressDialog);
    }

    public interface TimeoutEvent {
        void TimeOut();
    }

    public void SetPecent(int pecent) {
        if (!isFinished())
            mProgressDialog.setProgress(pecent);
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

    public void SetTimout(int timeout, TimeoutEvent event) {
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
