package org.example.mycomponent;

import org.example.util.CommonUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MyJFrame extends JFrame {
    public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public MyServerMessagePanel panel;
    public MyJFrame() {
        this(800, 600);
    }

    public MyJFrame(int width, int height) {
        this.setLayout(null);
        this.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        this.addWindowListener(new MyJFrameCloseListener(this));
    }

    public void setMyServerMessagePanel(MyServerMessagePanel panel) {
        this.panel = panel;
    }
    public MyServerMessagePanel getMyServerMessagePanel(){
        return panel;
    }
}


class MyJFrameCloseListener extends WindowAdapter {
    MyJFrame myJFrame;
    MyJFrameCloseListener(MyJFrame myJFrame){
        this.myJFrame = myJFrame;
    }
    @Override
    public void windowClosing(WindowEvent e) {
        List<String> list = myJFrame.panel.getTextFieldValues();
        String line = String.format("label1:%s\0label2:%s",list.get(1),list.get(2));
        CommonUtil.writeToFile(line);
        System.exit(0);
    }
}