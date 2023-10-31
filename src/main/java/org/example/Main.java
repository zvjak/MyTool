package org.example;

import org.example.mycomponent.MyButton;
import org.example.mycomponent.MyJFrame;
import org.example.mycomponent.MyServerMessagePanel;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        MyJFrame myJFrame=new MyJFrame();
        MyServerMessagePanel panel=new MyServerMessagePanel(myJFrame);
        MyButton myButton=new MyButton("上传文件",myJFrame);
        myJFrame.setVisible(true);
    }
}