package com.example.single_thread;

import java.io.IOException;

public class SingleThreadDownloadTest {

    public static void main(String[] args) throws IOException {
        String address = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe";
        String filename = "QQ7.9.exe";
        new SingleThreadDownload(address, filename).download();
    }
}
