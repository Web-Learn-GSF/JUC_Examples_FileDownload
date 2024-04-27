package com.example.multi_thread_with_pool.ByCountDownLatch;

import com.example.multi_thread_with_pool.basic.DownLoad_Runnable_pool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiThreadDownloadWithPool_CountDownLatch {
    private URL url;    // 目标地址
    private File file;  // 本地文件
    private int thread_amount;                 // 线程数
    private ThreadPoolExecutor pool;
    private static final String DOWNLOAD_DIR_PATH = "D:/Download";      // 下载目录
    private int threadLen;                                      // 每个线程下载多少


    public MultiThreadDownloadWithPool_CountDownLatch(String address, String filename, int thread_amount, ThreadPoolExecutor pool) throws IOException {
        this.url = new URL(address);

        File dir = new File(DOWNLOAD_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.file = new File(dir, filename);

        this.thread_amount = thread_amount;

        this.pool = pool;
    }

    public void download() throws IOException {

        long stime = System.currentTimeMillis();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);

        int totalLen = conn.getContentLength();                             // 获取文件长度
        this.threadLen = (totalLen + this.thread_amount - 1) / this.thread_amount;         // 计算每个线程要下载的长度

        System.out.println("totalLen=" + totalLen + ",threadLen:" + this.threadLen);

        RandomAccessFile raf = new RandomAccessFile(this.file, "rws");           // 在本地创建一个和服务端大小相同的文件
        raf.setLength(totalLen);                                                       // 设置文件的大小
        raf.close();

        CountDownLatch latch = new CountDownLatch(this.thread_amount);

        // 开启线程, 每个线程下载一部分数据到本地文件中
        for (int id = 0; id < this.thread_amount; id++){
            DownLoad_Runnable_pool_CountDownLatch downLoad_runnable = new DownLoad_Runnable_pool_CountDownLatch(id, this.threadLen, this.file, this.url, latch);
            pool.execute(downLoad_runnable);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long etime = System.currentTimeMillis();
        System.out.printf("总计下载时间：%d 毫秒.", (etime - stime));
    }

}
