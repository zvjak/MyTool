package org.example.mycomponent;

import org.example.run.RunUpdate;
import org.example.run.RunUpload;
import org.example.util.BarMessage;
import org.example.util.CommonUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class MyButton extends JButton implements UpdateAble {
    private static final int defaultWidth = 120;
    private static final int defaultHeight = 50;
    private MyJFrame myJFrame;

    public MyButton(String args, MyJFrame myJFrame) {
        this(args, myJFrame, defaultWidth, defaultHeight);
    }

    public MyButton(String args, MyJFrame myJFrame, int width, int height) {
        super(args);
        this.setBounds(myJFrame.getSize().width - width >> 1, myJFrame.getSize().height - height >> 1, width, height);
        this.myJFrame = myJFrame;
        this.addActionListener(new UploadAction(myJFrame));
        myJFrame.add(this);
    }

    @Override
    public void update() {
        this.setBounds((myJFrame.getSize().width - this.getWidth()) / 2, (myJFrame.getSize().height - this.getHeight()) / 2, this.getWidth(), this.getHeight());
    }
}

class UploadAction extends AbstractAction {
    private MyJFrame myJFrame;

    UploadAction(MyJFrame myJFrame) {
        this.myJFrame = myJFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> list = myJFrame.panel.getTextFieldValues();
        System.out.println(list);
        String line = String.format("label1:%s\0label2:%s",list.get(1),list.get(2));
        CommonUtil.writeToFile(line);
        //保存到文件
        File selectedFile = openFileChooser();
        if(selectedFile==null){
            return ;
        }
        BarMessage message=new BarMessage();
        message.fileLength = selectedFile.length();
        //创建进度条
        MyDialog myDialog=new MyDialog("Non-Modal Dialog",myJFrame,message);
        myDialog.setVisible(true);
        //传输，这里要用多线程
        Thread t = new Thread(new RunUpload(myJFrame.panel,selectedFile,message));
        message.uploadThread = t;
        //更新进度条
        Thread t2 = new Thread(new RunUpdate(myJFrame,message,myDialog));
        message.redrawThread = t2;
        t.start();
        t2.start();
    }

    private File openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setBounds(myJFrame.getBounds());
        int result = fileChooser.showOpenDialog(myJFrame);
        File selectedFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }
        return selectedFile;
    }
}