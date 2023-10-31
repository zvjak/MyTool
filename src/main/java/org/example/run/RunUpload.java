package org.example.run;

import org.example.mycomponent.MyServerMessagePanel;
import org.example.util.BarMessage;
import org.example.util.CommonUtil;

import java.io.File;

public class RunUpload implements Runnable {
    private MyServerMessagePanel panel;
    private File file;
    private BarMessage message;

    public RunUpload(MyServerMessagePanel panel, File file, BarMessage message) {
        this.panel = panel;
        this.file = file;
        this.message = message;
    }

    @Override
    public void run() {
        CommonUtil.upload(panel,file,message);
    }
}
