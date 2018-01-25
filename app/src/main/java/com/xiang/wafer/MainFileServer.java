package com.xiang.wafer;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/25
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class MainFileServer extends NanoHTTPD {
    private static final String TAG = "MainFileServer";
    public MainFileServer() {
        super(8080);
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
//            http:127.0.0.1:8080/smb/192.168.0.101/movie/asdf.txt"
            //uri：用于标示文件资源的字符串，这里即是文件路径
            String uri = session.getUri();
            Log.d(TAG, "serve:toString " + session.toString());
            Log.d(TAG, "serve:getUri " + session.getUri());
            Log.d(TAG, "serve:getQueryParameterString " + session.getQueryParameterString());
            Log.d(TAG, "serve:getRemoteHostName " + session.getRemoteHostName());
            Log.d(TAG, "serve:getRemoteIpAddress " + session.getRemoteIpAddress());
            Log.d(TAG, "serve:getHeaders " + session.getHeaders().toString());
            Log.d(TAG, "serve:getParameters " + session.getParameters().toString());
            Log.d(TAG, "serve:getParms " + session.getParms().toString());
            //文件输入流
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + "/01.png");
            FileInputStream fis = new FileInputStream(file);
            // 返回OK，同时传送文件，为了安全这里应该再加一个处理，即判断这个文件是否是我们所分享的文件，避免客户端访问了其他个人文件
            Response response = newFixedLengthResponse(Response.Status.OK, "image/png", fis, fis.available());
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        msg += "<p>Hello, " + "world" + "!</p>";
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}
