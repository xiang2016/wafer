package com.xiang.wafer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.xiang.wafer.model.User;
import com.xiang.wafer.model.WinServer;

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
//    private final MutableLiveData<String> mPath;
    private final MutableLiveData<User> mUser = new MutableLiveData<>();
    private final MutableLiveData<WinServer> mWinServer = new MutableLiveData<>();
    private final LiveData<List<SmbFile>> smbfiles;
    private final MediatorLiveData<SmbFile> smbfile;
    private final SmbFileRepository mSmbFileRepository;
    private LiveData<SmbFile> smbFileLiveData;

    public MainViewModel(SmbFileRepository smbFileRepository, User user, WinServer winServer) {
        mSmbFileRepository = smbFileRepository;
        smbfile = new MediatorLiveData<>();
//        smbfile.setValue(null);
        smbfiles = Transformations.switchMap(smbfile, mSmbFileRepository::SmbFileList);
//        mPath = new MediatorLiveData<>();
//        mPath.setValue("");
//        mPath.observeForever(s -> {
//            smbfile.addSource(smbFileLiveData, smbfile::setValue);
//        });
    }

    public MediatorLiveData<SmbFile> getSmbfile() {
        return smbfile;
    }

    public LiveData<List<SmbFile>> getSmbfiles() {
        return smbfiles;
    }

//    public MutableLiveData<String> getPath() {
//        return mPath;
//    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    public MutableLiveData<WinServer> getWinServer() {
        return mWinServer;
    }

    public void getData(User user, WinServer winServer) {
        smbFileLiveData = mSmbFileRepository.SmbFile(winServer.host, user.userName, user.password, "");
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final WinServer mServer;
        private final User mUser;
        private final SmbFileRepository mSmbFileRepository;

        public Factory(SmbFileRepository smbFileRepository, User user, WinServer winServer) {
            mServer = winServer;
            mUser = user;
            mSmbFileRepository = smbFileRepository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new MainViewModel(mSmbFileRepository, mUser, mServer);
        }
    }
}
