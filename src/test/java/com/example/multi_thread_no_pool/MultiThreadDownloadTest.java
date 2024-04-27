package com.example.multi_thread_no_pool;

import java.io.IOException;

public class MultiThreadDownloadTest {
    public static void main(String[] args) throws IOException {
        String address = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe";
        String  filename = "QQ7.9.exe";
        int thread_num = 8;

        MultiThreadDownload download_init = new MultiThreadDownload(address, filename, thread_num);

        //download_init.download_thread();
        download_init.download_runnable();
    }
}
