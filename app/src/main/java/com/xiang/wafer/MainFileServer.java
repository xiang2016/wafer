package com.xiang.wafer;

import android.net.Uri;
import android.util.Log;

import com.xiang.wafer.model.Login;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import jcifs.smb.SmbFile;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/25
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class MainFileServer extends NanoHTTPD {
    public static final int PORT = 8080;
    public static final String URL = "http://127.0.0.1:" + PORT;
    private static final String TAG = "MainFileServer";
    private Login login;

    public MainFileServer() {
        super(PORT);
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uriStr = Uri.decode(session.getUri());

        if (session.getMethod() == Method.GET && checkUser(uriStr, login)) {
            String password = "123456";
            String smbPath = uriStr
                    .replace("/smb", "smb:/")
                    .replace("@", ":" + password + "@");
            Log.d(TAG, "serve: " + smbPath);
            try {
                SmbFile smbFile = new SmbFile(smbPath);
                InputStream inputStream = smbFile.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                // 返回OK，同时传送文件，为了安全这里应该再加一个处理，即判断这个文件是否是我们所分享的文件，避免客户端访问了其他个人文件
//                Response response = newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(smbPath), smbFileInputStream,smbFileInputStream.available());
                return serveFile(null, session.getHeaders(), smbFile, getMimeTypeForFile(smbPath));
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "*/*", "not found");
            }
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "*/*", "not found");
        }

    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    private boolean checkUser(String uriStr, Login login) {
        if (login == null) {
            return false;
        } else {
            String[] split = uriStr.split("/");
            String[] split1 = split[2].split("@");
            boolean smb = split[1].equals("smb");
            boolean userName = split1[0].equals(login.getUser().userName);
            boolean host = split1[1].equals(login.getWinServe().host);
            return smb && userName && host;
        }
    }


    private Response serveFile(String uri, Map<String, String> header,
                               SmbFile smbFile, String mime) {
        Response res;
        try {
            // Calculate etag
            String etag = Integer.toHexString((smbFile.getPath()
                    + smbFile.lastModified() + "" + smbFile.length()).hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range
                                    .substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is
            // requested
            long fileLen = smbFile.length();
            if (range != null && startFrom >= 0) {
                if (startFrom >= fileLen) {
                    res = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE,
                            NanoHTTPD.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    final long dataLen = newLen;
                    InputStream inputStream = smbFile.getInputStream();
                    inputStream.skip(startFrom);

                    res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mime,
                            inputStream, dataLen);
                    res.addHeader("Content-Length", "" + dataLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-"
                            + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {
                if (etag.equals(header.get("if-none-match")))
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                else {
                    res = newFixedLengthResponse(Response.Status.OK, mime,
                            smbFile.getInputStream(), fileLen);
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = newFixedLengthResponse(Response.Status.FORBIDDEN,
                    NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading smbFile failed.");
        }

        return res;
    }
}
