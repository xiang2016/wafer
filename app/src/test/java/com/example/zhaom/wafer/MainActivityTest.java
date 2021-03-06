package com.example.zhaom.wafer;

import android.net.Uri;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/22
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class MainActivityTest {

    @Test
    public void name() throws Exception {
        SmbFile remoteFile = new SmbFile("smb://zhaomx:123456@192.168.0.108/");
        //这一句很重要
        remoteFile.connect();
        showFileList(remoteFile, "");
    }

    public static void showFileList(SmbFile dir, String space) {
        System.out.println("UncPath " + dir.getUncPath());
        System.out.println("Path " + dir.getPath());
        try {
            if (dir.isDirectory()) {
                System.out.println(space + dir.getName());
                SmbFile[] smbFiles = dir.listFiles();
                for (SmbFile smbFile : smbFiles) {
                    showFileList(smbFile, space + "\t");
                }
            } else {
                System.out.println(space + dir.getName());
            }
        } catch (SmbException e) {
//            e.printStackTrace();
        }
    }

    @Test
    public void smbGetTest() throws Exception {
        smbGet("", "E:\\jcifs");
    }

    /**
     * 从共享目录拷贝文件到本地
     *
     * @param remoteUrl 共享目录上的文件路径
     * @param localDir  本地目录
     */
    public static void smbGet(String remoteUrl, String localDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            SmbFile remoteFile = new SmbFile("smb://zhaomx:123456@192.168.0.108/movie/test.txt");
            //这一句很重要
            remoteFile.connect();
            String fileName = remoteFile.getName();
            File localFile = new File(localDir + File.separator + fileName);
            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                out.write(buffer);
                buffer = new byte[1024];
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SmbException) {
                System.out.println(((SmbException) e).getNtStatus());
            }
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

    /**
     * 从本地上传文件到共享目录
     *
     * @param remoteUrl     共享文件目录
     * @param localFilePath 本地文件绝对路径
     * @Version1.0 Sep 25, 2009 3:49:00 PM
     */
    public void smbPut(String remoteUrl, String localFilePath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File localFile = new File(localFilePath);

            String fileName = localFile.getName();
            SmbFile remoteFile = new SmbFile(remoteUrl + "/" + fileName);
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                out.write(buffer);
                buffer = new byte[1024];
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void stringTest() throws Exception {
        String path = "smb://zhaomx:123456@192.168.0.108/D$/";
        String path2 = "192.168.0.108/movie/new1/new/";
        String path3 = "192.168.0.108/";

        Assert.assertEquals("192.168.0.108/D$/", path.substring(path.indexOf("@") + 1));
        Assert.assertEquals("192.168.0.108/movie/new1/", handlePath(path2));
        Assert.assertEquals("", handlePath(path3));
    }

    private String handlePath(String path) {
        String substring = path.substring(0, path.length() - 1);
        return substring.substring(0,substring.lastIndexOf("/") + 1);
    }

    @Test
    public void uriTest() throws Exception {
//        UncPath \\192.168.0.108
//        Path smb://zhaomx:123456@192.168.0.108/
        String  uncpPath = "UncPath \\\\192.168.0.108";
        String  path = "smb://zhaomx:123456@192.168.0.108/";
        Uri uriSmb = Uri.parse(path);
        System.out.println(uriSmb.getUserInfo());
    }
}