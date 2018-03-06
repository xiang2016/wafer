package com.xiang.wafer.model;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/03/06
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class Login {
    private User user;
    private WinServer winServe;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WinServer getWinServe() {
        return winServe;
    }

    public void setWinServe(WinServer winServe) {
        this.winServe = winServe;
    }
}
