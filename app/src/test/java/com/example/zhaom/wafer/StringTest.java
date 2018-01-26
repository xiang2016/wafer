package com.example.zhaom.wafer;

import org.junit.Assert;
import org.junit.Test;

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
}
