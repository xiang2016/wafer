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
            return userInfo.substring(0,index);
        } else {
            return userInfo;
        }
    }
}
