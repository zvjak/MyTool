package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UploadServer {
    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            8,
            20,
            5000,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }, new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8089);
            while (true) {
                Socket s = serverSocket.accept();
                threadPool.execute(new UploadTask(s));
            }
        } catch (Exception e) {

        }
    }

}

class UploadTask implements Runnable {
    private Socket s = null;

    UploadTask(Socket socket) {
        s = socket;
    }

    File file = null;

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedReader read = null;
        FileOutputStream fos = null;
        try {
            inputStream = s.getInputStream();
            int b = 0;
            byte[] buffers = new byte[1024];
            int index = 0;
            while (true) {
                b = inputStream.read();
                if (b == '\n') {
                    break;
                }
                buffers[index++] = (byte) b;
            }
            String line = new String(buffers, 0, index, StandardCharsets.UTF_8);
            boolean flag = false;
            if (line.indexOf('\3') != 0) {
                line = line.split("\3")[0];
                index--;
                flag = true;
            }
            System.out.println(line);
            String[] split = line.split(";");
            String path = split[0];
            String fileName = split[1];
            File pathDir = new File(path);
            if (!pathDir.exists()) {
                pathDir.mkdirs();
            } else {
                if (!pathDir.isDirectory()) {
                    System.out.println("路径存在，且不为文件夹");
                    return;
                }
            }

            File file = new File(path + "/" + fileName);
            if (flag) {
                file.delete();
                System.out.println("客户端取消文件上传");
                return;
            }
            this.file = file;
            System.out.printf("文件名:%s,文件路径:%s\n", file.getName(), file.getAbsolutePath());
            if (file.exists()) {
                System.out.printf("文件%s存在\n", file.getAbsolutePath());
                return;
            }
            if (!file.createNewFile()) {
                System.out.println("创建文件失败");
            }
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 1024];
            int readCount = 0;
            while (true) {
                readCount = inputStream.read(buffer, 0, 1024 * 1024);
                if (readCount < 0) {
                    break;
                }
                fos.write(buffer, 0, readCount);
                fos.flush();
            }
        } catch (SocketException e) {
            System.out.println("客户端取消上传");
            if (file != null) {
                file.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            releaseResource(inputStream, read, fos);
        }
    }

    public void releaseResource(Closeable... args) {
        for (Closeable e : args) {
            if (e != null) {
                try {
                    e.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}