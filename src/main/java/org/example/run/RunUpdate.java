package org.example.run;

import org.example.mycomponent.MyDialog;
import org.example.mycomponent.MyJFrame;
import org.example.util.BarMessage;

import java.util.Random;

public class RunUpdate implements Runnable{
    MyJFrame myJFrame;
    BarMessage barMessage;
    MyDialog myDialog;
    public RunUpdate(MyJFrame myJFrame,BarMessage barMessage,MyDialog myDialog){
        this.myJFrame = myJFrame;
        this.barMessage = barMessage;
        this.myDialog = myDialog;
    }
    @Override
    public void run() {
        try {
            int sec = 0;
            long lastLength = 0;
            Random ran=new Random();
            while (true) {
                synchronized (barMessage){
                    myDialog.setTitle(String.format("%.2f M/%.2f M  %d s   %dM /s", 1.0 * barMessage.sendLength / 1024 / 1024, 1.0 * barMessage.fileLength / 1024 / 1024, sec,(barMessage.sendLength-lastLength)/1024/1024));
                    myDialog.bar.setValue(
                            (int) (1.0 * barMessage.sendLength / barMessage.fileLength * 100)
                    );
                    myJFrame.repaint();
                    myDialog.bar.repaint();
                    sec++;
                    if(Thread.currentThread().isInterrupted()){
                        throw new InterruptedException();
                    }
                }
                lastLength = barMessage.sendLength;
                Thread.sleep(1000);
            }
        }catch (Exception e){
            myDialog.dispose();
        }
    }
}
