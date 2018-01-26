package com.xiang.wafer;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

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
//        Response res = null;
//        try {
//
//            long startFrom = 0;
//            long endAt = -1;
//            String range = session.getHeaders().get("range");
//            if (range != null) {
//                if (range.startsWith("bytes=")) {
//                    range = range.substring("bytes=".length());
//                    int minus = range.indexOf('-');
//                    try {
//                        if (minus > 0) {
//                            startFrom = Long.parseLong(range.substring(0, minus));
//                            endAt = Long.parseLong(range.substring(minus + 1));
//                        }
//                    } catch (NumberFormatException nfe) {
//                    }
//                }
//            }
//            Log.d("Explorer", "Request: " + range + " from: " + startFrom + ", to: " + endAt);
//
//            // Change return code and add Content-Range header when skipping
//            // is requested
//            //source.open();
//            final StreamSource source = new StreamSource(sourceFile);
//            long fileLen = source.length();
//            if (range != null && startFrom > 0) {
//                if (startFrom >= fileLen) {
//                    res =  newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, null);
//                    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
//                } else {
//                    if (endAt < 0)
//                        endAt = fileLen - 1;
//                    long newLen = fileLen - startFrom;
//                    if (newLen < 0)
//                        newLen = 0;
//                    Log.d("Explorer", "start=" + startFrom + ", endAt=" + endAt + ", newLen=" + newLen);
//                    final long dataLen = newLen;
//                    source.moveTo(startFrom);
//                    Log.d("Explorer", "Skipped " + startFrom + " bytes");
//
//                    res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, source.getMimeType(), source.input,source.available());
//                    res.addHeader("Content-length", "" + dataLen);
//                }
//            } else {
//                source.reset();
//                res = newFixedLengthResponse(Response.Status.OK, source.getMimeType(), source);
//                res.addHeader("Content-Length", "" + fileLen);
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//            res = new Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, null);
//        }
//
//        res.addHeader("Accept-Ranges", "bytes"); // Announce that the file
//        // server accepts partial
//        // content requestes
//        return res;


//-------------------------------------------------------
        String uriStr = Uri.decode(session.getUri());
        if (session.getMethod() == Method.GET && uriStr.startsWith("/smb")) {
            try {
                String password = "123456";
                String smbPath = uriStr
                        .replace("/smb", "smb:/")
                        .replace("@", ":" + password + "@");
                Log.d(TAG, "serve: " + smbPath);
                SmbFile smbFile = new SmbFile(smbPath);
                SmbFileInputStream smbFileInputStream = new SmbFileInputStream(smbFile);
//                FileInputStream fis = new FileInputStream();
                InputStream inputStream = smbFile.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                Log.d(TAG, "serve: " + smbFileInputStream.available());
                // 返回OK，同时传送文件，为了安全这里应该再加一个处理，即判断这个文件是否是我们所分享的文件，避免客户端访问了其他个人文件
                Response response = newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(smbPath), smbFileInputStream,smbFileInputStream.available());
//                response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                return serveFile(null,session.getHeaders(),smbFile,getMimeTypeForFile(smbPath));
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "*/*", "not found");
            }
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "*/*", "not found");
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
                            inputStream,dataLen);
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
                            smbFile.getInputStream(),fileLen);
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
