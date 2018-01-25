package com.xiang.wafer;

import android.os.Environment;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/25
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class MainActivityTest {
    @Test
    public void pathTest() throws Exception {
        Assert.assertEquals("", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
    }

    @Test
    public void fileCopyTest() throws Exception {
        File file = new File("/storage/sdcard1/DCIM/Camera/IMG_20171225_154954.jpg");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(new FileOutputStream("/storage/sdcard1/DCIM/Camera/IMG_test.jpg"));
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                out.write(buffer);
                buffer = new byte[1024];
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}