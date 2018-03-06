package com.xiang.wafer;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/03/05
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class SmbFileRepository {
    private static final String TAG = "SmbFileRepository";
    private AppExecutors appExecutors;

    public SmbFileRepository(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public LiveData<List<SmbFile>> SmbFileList(String server, String userName, String password, String path) {
        String smbPath = String.format("smb://%1$s:%2$s@%3$s/%4$s", userName, password, server, path);
        Log.d(TAG, "SmbFile: " + smbPath);
        return new LiveData<List<SmbFile>>() {
            AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (!started.compareAndSet(false, true)) {
                    return;
                }
                appExecutors.diskIO().execute(() -> {
                    try {
                        SmbFile smbFile = new SmbFile(smbPath);
                        List<SmbFile> smbFiles = Arrays.asList(smbFile.listFiles());
                        postValue(smbFiles);
                    } catch (SmbException | MalformedURLException e) {
                        postValue(Collections.emptyList());
                        e.printStackTrace();
                    }
                });
            }
        };
    }
}
