package com.example.multi_thread_no_pool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultiThreadDownload {
    private URL url;    // 目标地址
    private File file;  // 本地文件
    private int thread_amount;                 // 线程数
    private static final String DOWNLOAD_DIR_PATH = "D:/Download";      // 下载目录
    private int threadLen;                                      // 每个线程下载多少

    public MultiThreadDownload(String address, String filename, int thread_amount) throws IOException {
        this.url = new URL(address);
        
        File dir = new File(DOWNLOAD_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.file = new File(dir, filename);
        
        this.thread_amount = thread_amount;
    }

    // 通过继承thread开启线程调用
    public void download_thread() throws IOException {

        long stime = System.currentTimeMillis();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);

        int totalLen = conn.getContentLength();                             // 获取文件长度
        this.threadLen = (totalLen + this.thread_amount - 1) / this.thread_amount;         // 计算每个线程要下载的长度

        System.out.println("totalLen=" + totalLen + ",threadLen:" + this.threadLen);

        RandomAccessFile raf = new RandomAccessFile(this.file, "rws");           // 在本地创建一个和服务端大小相同的文件
        raf.setLength(totalLen);                                                       // 设置文件的大小
        raf.close();

        Thread[] threads = new Thread[this.thread_amount];

        // 开启线程, 每个线程下载一部分数据到本地文件中
        for (int id = 0; id < this.thread_amount; id++){
            threads[id] = new DownLoad_Thread(id, this.threadLen, this.file, this.url);
            threads[id].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long etime = System.currentTimeMillis();
        System.out.printf("总计下载时间：%d 毫秒.", (etime - stime));
    }


    // 通过实现runnable开启线程调用
    public void download_runnable() throws IOException {

        long stime = System.currentTimeMillis();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(50000);

        int totalLen = conn.getContentLength();                             // 获取文件长度
        this.threadLen = (totalLen + this.thread_amount - 1) / this.thread_amount;         // 计算每个线程要下载的长度

        System.out.println("totalLen=" + totalLen + ",threadLen:" + this.threadLen);

        RandomAccessFile raf = new RandomAccessFile(this.file, "rws");           // 在本地创建一个和服务端大小相同的文件
        raf.setLength(totalLen);                                                       // 设置文件的大小
        raf.close();

        Thread[] threads = new Thread[this.thread_amount];

        // 开启线程, 每个线程下载一部分数据到本地文件中
        for (int id = 0; id < this.thread_amount; id++){
            threads[id] = new Thread( new DownLoad_Runnable(id, this.threadLen, this.file, this.url) );
            threads[id].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long etime = System.currentTimeMillis();
        System.out.printf("总计下载时间：%d 毫秒.", (etime - stime));
    }

}
