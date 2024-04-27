package com.example.multi_thread_with_pool.ByCountDownLatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class DownLoad_Runnable_pool_CountDownLatch implements Runnable{
    private URL url;
    private int id;             // 线程id
    private int threadLen;      // 线程的下载长度
    private File file;          // 线程下载结果的存储位置

    private CountDownLatch latch;

    public DownLoad_Runnable_pool_CountDownLatch(int id, int threadlen, File file, URL url, CountDownLatch latch){
        this.id = id;
        this.threadLen = threadlen;
        this.file = file;
        this.url = url;
        this.latch = latch;
    }

    @Override
    public void run(){
        int start = id * this.threadLen;                     // 起始位置
        int end = (id + 1) * this.threadLen - 1;             // 结束位置
        System.out.println("线程" + id + ": " + start + "-" + end);

        try {
            HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Range", "bytes=" + start + "-" + end);     // 设置当前线程下载的范围

            InputStream in = conn.getInputStream();
            RandomAccessFile raf = new RandomAccessFile(this.file, "rws");
            raf.seek(start);            // 设置保存数据的位置

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1)
                raf.write(buffer, 0, len);
            raf.close();

            System.out.println("线程" + id + "下载完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.latch.countDown();
    }
}
