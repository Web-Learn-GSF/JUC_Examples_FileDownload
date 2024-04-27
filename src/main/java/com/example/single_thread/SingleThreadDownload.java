package com.example.single_thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class SingleThreadDownload {

    private URL url;    // 目标地址
    private File file;  // 文件对象

    private int file_total_len;
    private static final String DOWNLOAD_DIR_PATH = "D:/Download";      // 下载目录


    public SingleThreadDownload(String address, String filename) throws IOException {     // 通过构造函数传入下载地址
        url = new URL(address);
        File dir = new File(DOWNLOAD_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file = new File(dir, filename);
    }

    public void download() throws IOException {
        long stime = System.currentTimeMillis();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);

        this.file_total_len = conn.getContentLength();                             // 获取文件长度
        System.out.println("totalLen=" + this.file_total_len);

        RandomAccessFile raf = new RandomAccessFile(file, "rws");           // 在本地创建一个和服务端大小相同的文件。RandomAccessFile支持随机位置写入文件内容
        raf.setLength(this.file_total_len);                                                 // 设置文件的大小
        raf.close();

        download_file();    // 下载文件

        long etime = System.currentTimeMillis();
        System.out.printf("总计下载时间：%d 毫秒.", (etime - stime));
    }

    private void download_file() {
        int start = 0;
        int end = this.file_total_len;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Range", "bytes=" + 0 + "-" + end);     // 设置当前线程下载的范围

            InputStream in = conn.getInputStream();
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.seek(start);            // 设置保存数据的位置

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1)
                raf.write(buffer, 0, len);
            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
