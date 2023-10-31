package org.example.mycomponent;

import org.example.util.BarMessage;
import org.example.util.CommonUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MyDialog extends JDialog {
    BarMessage barMessage;
    MyJFrame myJFrame;
    public JProgressBar bar;

    public MyDialog(String title, MyJFrame myJFrame, BarMessage barMessage) {
        super(myJFrame, title, false);
        this.myJFrame = myJFrame;
        this.barMessage = barMessage;
        this.setLayout(new GridBagLayout());
        this.setSize(Math.min(myJFrame.getSize().width - 150, 550), 150);
        this.setBounds((int) myJFrame.getBounds().getX() + (myJFrame.getSize().width - this.getWidth() >> 1),
                (int) myJFrame.getBounds().getY() + (myJFrame.getSize().height - this.getHeight() >> 1), this.getWidth(), this.getHeight());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (barMessage.redrawThread.isAlive()) {
                    barMessage.redrawThread.interrupt();
                    barMessage.uploadThread.interrupt();
                }
                while (barMessage.uploadThread.isAlive()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if (barMessage.host != null) {
                    Socket socket = null;
                    OutputStream os = null;
                    try {
                        System.out.println(barMessage.host + barMessage.port + barMessage);
                        socket = new Socket(barMessage.host, Integer.valueOf(barMessage.port));
                        os = socket.getOutputStream();
                        os.write((barMessage.filePath + ";" + barMessage.fileName + "\3" + "\n").getBytes(StandardCharsets.UTF_8));
                        os.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } finally {
                        CommonUtil.releaseResource(os, socket);
                    }
                }
                dispose();
            }
        });
//        JButton cancel = new JButton("取消");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.insets = new Insets(10, 10, 10, 10);
        bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setForeground(Color.green);
        bar.setPreferredSize(new Dimension(300, 32));
        bar.setBounds(0, 100, 100, 20);
        bar.setForeground(Color.green);
        this.add(bar, constraints);
        constraints.gridy = 1;
//        this.add(cancel, constraints);
//        cancel.setVisible(true);
//        cancel.addActionListener(new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                barMessage.uploadThread.interrupt();
//                barMessage.redrawThread.interrupt();
//                while (barMessage.uploadThread.isAlive()) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException ex) {
//                        throw new RuntimeException(ex);
//                    }
//                }
//            }
//        });
    }
}

