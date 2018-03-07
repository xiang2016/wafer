package com.xiang.wafer.model;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/24
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class User {
    public String userName;
    public String password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
