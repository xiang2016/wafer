package com.example.zhaom.wafer;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/26
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class StringTest {
    @Test
    public void getUserNameTest() throws Exception {
        Assert.assertEquals("zhaomx", getUserName("zhaomx:123456"));
    }

    private String getUserName(String userInfo) {
        int index = userInfo.indexOf(":");
        if (index != -1) {
            return userInfo.substring(0, index);
        } else {
            return userInfo;
        }
    }

    @Test
    public void getSmbPath() throws Exception {
        String uriSmb = "/smb/zhaomx@192.168.0.108/movie/一二三.txt";
        String password = "123456";
        String smbPath = uriSmb
                .replace("/smb", "smb:/")
                .replace("@", ":" + password + "@");
        Assert.assertEquals("smb://zhaomx:123456@192.168.0.108/movie/一二三.txt", smbPath);
    }

    @Test
    public void stream() throws Exception {
        List<String> names = new ArrayList<>();
        names.stream().map(s -> s + "123").forEach(System.out::println);
        String data = "Hello World";
    }

    @Test
    public void parseTest() throws Exception {
        String uri = "/smb/zhaomx@192.168.0.108/movie/一二三.txt";
        String[] split = uri.split("/");
        Arrays.stream(split).forEach(System.out::println);
        String[] strings = split[2].split("@");
        Assert.assertEquals("smb", split[1]);
        Assert.assertEquals("zhaomx", strings[0]);
        Assert.assertEquals("192.168.0.108", strings[1]);
    }
}
