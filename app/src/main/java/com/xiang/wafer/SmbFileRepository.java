package com.xiang.wafer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public LiveData<SmbFile> SmbFile(String server, String userName, String password, String path) {
        MutableLiveData<SmbFile> smbFileMutableLiveData = new MediatorLiveData<>();
        String smbPath = String.format("smb://%1$s:%2$s@%3$s/%4$s", userName, password, server, path);
        Log.d(TAG, "SmbFile: " +smbPath);
        appExecutors.diskIO().execute(() -> {
            try {
                smbFileMutableLiveData.postValue(new SmbFile(smbPath));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                smbFileMutableLiveData.postValue(null);
            }
        });
        return smbFileMutableLiveData;
    }

    public LiveData<List<SmbFile>> SmbFileList(SmbFile smbfile) {
        MutableLiveData<List<SmbFile>> liveData = new MediatorLiveData<>();
        appExecutors.diskIO().execute(() -> {
            if (smbfile != null) {
                try {
                    List<SmbFile> smbFiles = Arrays.asList(smbfile.listFiles());
                    liveData.postValue(smbFiles);
                    Log.d(TAG, "SmbFileList: " + smbFiles.toString());
                } catch (SmbException e) {
                    e.printStackTrace();
                }
            }
        });
        liveData.postValue(Collections.emptyList());
        return liveData;
    }
}
