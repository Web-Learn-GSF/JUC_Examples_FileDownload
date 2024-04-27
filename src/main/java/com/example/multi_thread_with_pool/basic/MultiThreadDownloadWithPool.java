package com.example.multi_thread_with_pool.basic;

import com.example.multi_thread_with_pool.basic.DownLoad_Runnable_pool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiThreadDownloadWithPool {
    private URL url;    // 目标地址
    private File file;  // 本地文件
    private int thread_amount;                 // 线程数
    private ThreadPoolExecutor pool;
    private static final String DOWNLOAD_DIR_PATH = "D:/Download";      // 下载目录
    private int threadLen;                                      // 每个线程下载多少


    public MultiThreadDownloadWithPool(String address, String filename, int thread_amount, ThreadPoolExecutor pool) throws IOException {
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

        // 开启线程, 每个线程下载一部分数据到本地文件中
        for (int id = 0; id < this.thread_amount; id++){
            DownLoad_Runnable_pool downLoad_runnable = new DownLoad_Runnable_pool(id, this.threadLen, this.file, this.url);
            pool.execute(downLoad_runnable);
        }

        // 不再提交新的线程，并等待已有线程执行完毕
        pool.shutdown();
        try {
            // 等待所有任务完成执行，或者等待超时
            if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
                System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // 如果当前线程也被中断，则重新中断
            Thread.currentThread().interrupt();
            // 尝试强制关闭线程池
            pool.shutdownNow();
        }

        long etime = System.currentTimeMillis();
        System.out.printf("总计下载时间：%d 毫秒.", (etime - stime));
    }

}
