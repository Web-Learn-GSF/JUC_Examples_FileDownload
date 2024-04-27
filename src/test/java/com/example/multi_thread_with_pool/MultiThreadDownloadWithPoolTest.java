package com.example.multi_thread_with_pool;


import com.example.multi_thread_with_pool.ByCountDownLatch.MultiThreadDownloadWithPool_CountDownLatch;
import com.example.multi_thread_with_pool.basic.MultiThreadDownloadWithPool;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiThreadDownloadWithPoolTest {

    public static void main(String[] args) throws IOException {


        String address = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe";
        String  filename = "QQ7.9.exe";
        int thread_num = 8;
        ThreadPoolExecutor pool = new ThreadPoolExecutor(thread_num, thread_num + 1, 5, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

        //// 方式1
        //MultiThreadDownloadWithPool download_init = new MultiThreadDownloadWithPool(address, filename, thread_num, pool);
        //download_init.download();

        //方式2
        MultiThreadDownloadWithPool_CountDownLatch multiThreadDownloadWithPool_countDownLatch =
                new MultiThreadDownloadWithPool_CountDownLatch(address, filename, thread_num, pool);
        multiThreadDownloadWithPool_countDownLatch.download();

        // 关闭线程池
        pool.shutdownNow();
    }
}
