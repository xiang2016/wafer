package com.xiang.wafer.model;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/24
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class WinServer {
    public String host;
    public String sharePath;

    public WinServer(String host, String sharePath) {
        this.host = host;
        this.sharePath = sharePath;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
