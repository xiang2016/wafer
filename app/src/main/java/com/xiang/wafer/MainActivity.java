package com.xiang.wafer;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.xiang.wafer.databinding.ActivityMainBinding;
import com.xiang.wafer.model.User;
import com.xiang.wafer.model.WinServer;

import java.io.File;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import static com.xiang.wafer.MainFileServer.URL;
import static fi.iki.elonen.NanoHTTPD.getMimeTypeForFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainFileAdpter adapter;
    private ActivityMainBinding binding;
    private SmbFile currentSmbFileDir;
    private SmbFileClickCallback callback;
    private MainFileServer mainFileServer;
    private WinServer winServer;
    private User user;
    private AlertDialog fileAlertDialog;
    private Uri uriFile;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        winServer = new WinServer("192.168.0.108", "");
        user = new User("zhaomx", "123456");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MainViewModel.Factory factory = new MainViewModel.Factory(
                new SmbFileRepository(new AppExecutors()), user, winServer);
        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        mainViewModel.getSmbfiles().observe(this, smbFiles -> {
            Log.d(TAG, "onCreate: " + smbFiles.toString());
            adapter.setCurrentSmbFiles(smbFiles);
            adapter.notifyDataSetChanged();
            binding.setLoadStatus(0);
        });
        mainViewModel.getSmbfile().observe(this, smbFile ->
                {
                    if (smbFile == null) {
                        Toast.makeText(this, "smbfile null", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.setCurrentPath(smbFile.getUncPath());
                        currentSmbFileDir = smbFile;
                    }
                }
        );
        initParams();
        myCheckPermission();
        mainFileServer = new MainFileServer();
    }


    private void initParams() {
        binding.setServer(winServer);
        binding.setUser(user);
        binding.setCurrentPath("//192.168.0.108/");
        binding.setLoadStatus(1);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        callback = smbFile -> {
            try {
                if (smbFile.isDirectory()) {
//                    mainViewModel.getPath().setValue(smbFile.getUncPath());
                } else {
                    Uri uriSmb = Uri.parse(smbFile.getUncPath());
                    Uri uri = Uri.parse(URL + File.separator +
                            Uri.encode(uriSmb.getScheme() + File.separator +
                                    getUserName(uriSmb.getUserInfo()) + "@" +
                                    uriSmb.getHost() +
                                    uriSmb.getEncodedPath()
                            )
                    );
                    uriFile = uri;
                    fileAlertDialog.setTitle(uri.getLastPathSegment());
                    fileAlertDialog.setMessage(uri.toString());
                    fileAlertDialog.show();
                }
            } catch (SmbException e) {
                Toast.makeText(this, "SmbException" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        };
        adapter = new MainFileAdpter(callback);
        binding.recyclerView.setAdapter(adapter);
        binding.layoutLogin.btnStart.setOnClickListener(view -> {
            User user = binding.layoutLogin.getUser();
            mainViewModel.getUser().setValue(user);
            WinServer server = binding.getServer();
            mainViewModel.getWinServer().setValue(server);
            mainViewModel.getData(user, server);
        });
        fileAlertDialog = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uriFile, getMimeTypeForFile(uriFile.toString()));
                    startActivity(intent);
                    dialogInterface.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .create();
    }

    @Override
    public void onBackPressed() {
        if (currentSmbFileDir != null && !currentSmbFileDir.getParent().equals("smb://")) {
            try {
                callback.onSmbFileClick(new SmbFile(currentSmbFileDir.getParent()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                super.onBackPressed();
            }
        } else if (binding.getLoadStatus() == 1) {
            super.onBackPressed();
        } else {
            binding.setLoadStatus(1);
            // 不显示登录 0
            // 显示登录 不显示进度 1
            // 显示登录 显示进度 2
        }
    }

    private static String getUserName(String userInfo) {
        int index = userInfo.indexOf(":");
        if (index != -1) {
            return userInfo.substring(0, index);
        } else {
            return userInfo;
        }
    }

    @Override
    protected void onDestroy() {
        mainFileServer.stop();
        super.onDestroy();
    }

    private void myCheckPermission() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }
}
