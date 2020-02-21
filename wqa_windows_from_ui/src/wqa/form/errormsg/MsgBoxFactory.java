/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.errormsg;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JFrame;
import wqa.form.main.MainForm;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class MsgBoxFactory {

    private MsgBoxFactory() {
    }
    private static MsgBoxFactory instance;

    public static MsgBoxFactory Instance() {
        if (instance == null) {
            instance = new MsgBoxFactory();
        }
        return instance;
    }

    //消息队列
    private ArrayList<String> msg_list = new ArrayList();
    //最大消息队列长度
    private int MaxMsgNum = 5;
    //消息队列锁
    private final ReentrantLock msg_list_lock = new ReentrantLock(true);

    private boolean is_filter = false;

    private Date lasttime = new Date();

    //打印消息
    public void ShowMsgBox(String msg) {
//        new ErrorMessage(parent, msg).setVisible(true);
        msg_list_lock.lock();
        try {
            //如果队列空，启动弹出窗体进程， 这个必须放在前面
            if (msg_list.isEmpty()) {
                WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
                    //JOptionPane.showm
                    while (true) {
                        //获取需要弹出的消息
                        String pmsg = GetMessage();
                        //如果消息队列空，则退出
                        if (pmsg != null) {
                            //JOptionPane.showMessageDialog(null, pmsg);
                            MessageBox.ShowMessageBox(MainForm.main_parent, pmsg);
                        } else {
                            break;
                        }
                    }
                });
            }
            
            //没有屏蔽添加消息
            if (!this.is_filter) {
                if (msg_list.size() < MaxMsgNum) {
                    //添加消息
                    this.msg_list.add(msg);
                } else {
                    //开启3分钟屏蔽
                    this.msg_list.add("弹出错误太多,3分钟内暂停弹出错误信息,错误信息记录在log文件当中");
                    this.is_filter = true;
                    lasttime = new Date();
                }
            } else {
                //检查屏蔽是否结束
                this.is_filter = !(new Date().getTime() - lasttime.getTime() > 1000 * 60 * 3);
                //结束了，添加信息
                if (!this.is_filter) {
                    this.msg_list.add(msg);
                } else {
                    //屏蔽中直接返回
                    return;
                }
            }

        } finally {
            msg_list_lock.unlock();
        }
    }

    //获取消息队列中的消息
    private String GetMessage() {
        msg_list_lock.lock();
        try {
            if (msg_list.isEmpty()) {
                return null;
            } else {
                return msg_list.remove(0);
            }
        } finally {
            msg_list_lock.unlock();
        }
    }

    public static void main(String... args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            String test = "测试长输入弹出错误太多,3分钟内暂停弹出错误信息,错误信息记录在log文件当中  || ";
            MsgBoxFactory.Instance().ShowMsgBox(test + test + test + test + test + test + test + test + test + test);
            MsgBoxFactory.Instance().ShowMsgBox("测试短输入");

//        String test = "123<br>456";
//        String[] split = test.split("<br>");
//        for(int i = 0; i < split.length; i++){
//            System.out.println(split[i]);
//        }
//        
//        ArrayList<String> describe = new ArrayList();
//        System.out.println(describe.toArray(new String[0]).length);
        }
        //System.exit(0);
    }
}
