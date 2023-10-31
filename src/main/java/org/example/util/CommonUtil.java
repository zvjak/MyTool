package org.example.util;

import org.example.mycomponent.MyServerMessagePanel;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
    private static final String configFileName = "./config.txt";
    private static final String defaultPort = "8089";

    public static List<String> readFormFile() {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            File file = new File(configFileName);
            //文件不存在创建文件
            if (!file.exists()) {
                file.createNewFile();
                String write = String.format("label1:\0label2:");
                fos = new FileOutputStream(file);
                fos.write(write.getBytes(StandardCharsets.UTF_8));
                fos.close();
                fos = null;
            }
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int read = fis.read(buffer);
            String line = new String(buffer, 0, read);
            String[] ss = line.split("\0");
            List<String> list = new ArrayList<>();
            for (String e : ss) {
                String[] sps = e.split(":");
                list.add(sps.length == 1 ? "" : sps[1]);
            }
            System.out.println(list);
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            releaseResource(fos, fis);
        }
    }

    public static void writeToFile(String line) {
        FileOutputStream fos = null;
        try {
            File file = new File(configFileName);
            fos = new FileOutputStream(file);
            fos.write(line.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            releaseResource(fos);
        }
    }

    public static void releaseResource(Closeable... args) {
        try {
            for (Closeable e : args) {
                if (e != null)
                    e.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void upload(MyServerMessagePanel panel, File file, BarMessage message) {
        List<String> list = panel.getTextFieldValues();
        String[] ip_port = list.get(0).split(":");
        if (list.get(2).isEmpty()) {
            list.remove(2);
            list.add(file.getName());
        }
        System.out.println(file);
        FileInputStream fis = null;
        Socket socket = null;
        OutputStream os = null;
        String host = ip_port[0];
        String port = ip_port.length == 1 ? "8089" : ip_port[1];
        String targetPath = list.get(1);
        String remoteFileName = list.get(2);
        try {
            fis = new FileInputStream(file);
            socket = new Socket(host, Integer.valueOf(port));
            os = socket.getOutputStream();
            os.write((targetPath + ";" + remoteFileName + "\n").getBytes(StandardCharsets.UTF_8));
            os.flush();
            byte[] buffer = new byte[1024 * 1024];
            int read = 0;
            while (true) {
                read = fis.read(buffer, 0, 1024 * 1024);
                if (read < 0) {
                    break;
                }
                os.write(buffer, 0, read);
                os.flush();
                synchronized (message) {
                    message.sendLength += read;
                }
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException e) {
            message.fileName = remoteFileName;
            message.host = host;
            message.port = port;
            message.filePath = targetPath;
            System.out.println(host+" "+message.host+message.port+message);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel,e.getMessage(),"网络错误/路径错误",JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);

        } finally {
            releaseResource(fis, socket, os);
        }
    }
}
