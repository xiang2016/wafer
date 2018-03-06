package com.xiang.wafer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.text.TextUtils;

import com.xiang.wafer.model.Login;

import java.util.Collections;
import java.util.List;

import jcifs.smb.SmbFile;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/03/05
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class MainViewModel extends ViewModel {
    private Login mLogin;
    private final MutableLiveData<Integer> status = new MutableLiveData<>();
    private final MutableLiveData<String> currentPath = new MutableLiveData<>();
    // 不显示登录 0
    // 显示登录 不显示进度 1
    // 显示登录 显示进度 2
    private final LiveData<List<SmbFile>> smbfiles;

    public MainViewModel(SmbFileRepository smbFileRepository) {
        smbfiles = Transformations.switchMap(currentPath, input -> {
            if (mLogin != null) {
                if (!TextUtils.isEmpty(input)) {
                    return smbFileRepository.SmbFileList(mLogin.getWinServe().host,
                            mLogin.getUser().userName, mLogin.getUser().password,
                            input.replace(mLogin.getWinServe().host + "/", ""));
                } else {
                    MutableLiveData<List<SmbFile>> data = new MediatorLiveData<>();
                    data.setValue(Collections.emptyList());
                    status.setValue(1);
                    return data;
                }
            } else {
                MutableLiveData<List<SmbFile>> data = new MediatorLiveData<>();
                data.setValue(Collections.emptyList());
                status.setValue(1);
                return data;
            }
        });
        status.setValue(1);
        currentPath.setValue("");
    }

    public Login getmLogin() {
        return mLogin;
    }

    public void setmLogin(Login mLogin) {
        this.mLogin = mLogin;
    }

    public MutableLiveData<String> getCurrentPath() {
        return currentPath;
    }

    public MutableLiveData<Integer> getStatus() {
        return status;
    }

    public LiveData<List<SmbFile>> getSmbfiles() {
        return smbfiles;
    }

    public void goBack() {
        String currentPath = getCurrentPath().getValue();
        if (TextUtils.isEmpty(currentPath)) {
            status.setValue(1);
        } else {
            getCurrentPath().setValue(handlePath(currentPath));
        }
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final SmbFileRepository mSmbFileRepository;

        public Factory(SmbFileRepository smbFileRepository) {
            mSmbFileRepository = smbFileRepository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new MainViewModel(mSmbFileRepository);
        }
    }

    private String handlePath(String path) {
        String substring = path.substring(0, path.length() - 1);
        return substring.substring(0, substring.lastIndexOf("/") + 1);
    }
}
