package com.example.zhaom.wafer;

import android.net.Uri;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/26
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class UriTest {
    public static final String URL = "http://127.0.0.1:" + "8080";

    @Test
    public void uriTest() throws Exception {
        Uri parse = Uri.parse("smb://zhaomx:123456@192.168.0.108/movie/一二三.txt");
        Uri uri = Uri.parse(URL + File.separator +
                Uri.encode(parse.getScheme() + File.separator +
                        parse.getUserInfo() + "@" +
                        parse.getHost() +
                        parse.getEncodedPath())
        );
        System.out.println(Uri.encode(uri.toString()) + "-------------------");
        Assert.assertNotEquals("http://127.0.0.1:8080/smb/zhaomx:123456@192.168.0.108/movie/一二三.txt", Uri.encode(uri.toString()));
    }

    @Test
    public void uriStart() throws Exception {
//          serve:getUri /smb/zhaomx@192.168.0.108/movie/一二三.txt
//          serve: smb://zhaomx:123456@192.168.0.108/movie/一二三.txt
        Uri parse = Uri.parse("/smb/zhaomx@192.168.0.108/movie/一二三.txt");
        Assert.assertEquals("smb", parse.getPathSegments().get(0));
        Assert.assertEquals("zhaomx", parse.getUserInfo());
        System.out.println(parse.getPath());
    }

    @Test
    public void getPathSegmentsTest() throws Exception {
//        Uri uri = Uri.parse("/smb/zhaomx:123456@192.168.0.108/movie/一二三.txt");
        Uri uri = Uri.parse("smb://zhaomx:123456@192.168.0.108/movie/一二三.txt");
        Log.d("TEST", "getPathSegments: " + uri.getPathSegments().toString());
        Log.d("TEST", "getLastPathSegment: " + uri.getLastPathSegment());
        Assert.assertEquals(uri.getPathSegments().get(0), "smb");
    }
}
