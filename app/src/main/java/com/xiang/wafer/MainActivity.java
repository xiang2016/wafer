package com.xiang.wafer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.xiang.wafer.databinding.ActivityMainBinding;
import com.xiang.wafer.model.User;
import com.xiang.wafer.model.WinServer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainFileAdpter adapter;
    private String smbPath = "smb://zhaomx:123456@192.168.0.108/";
    private ActivityMainBinding binding;
    private SmbFile currentSmbFileDir;
    private SmbFileClickCallback callback;
    private MainHandler handler;
    private MainFileServer mainFileServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        WinServer winServer = new WinServer("192.168.0.108", "");
        User user = new User("zhaomx", "123456");
        handler = new MainHandler(this);
        binding.setServer(winServer);
        binding.setUser(user);
        binding.setCurrentPath("\\\\192.168.0.108/");
        binding.setLoadStatus(0);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        callback = smbFile -> {
            try {
                if (smbFile.isDirectory()) {
                    currentSmbFileDir = smbFile;
                    binding.setCurrentPath(smbFile.getUncPath());
                    adapter.setCurrentSmbFiles(smbFile.listFiles());
                    handler.sendEmptyMessage(1);
                }
            } catch (SmbException e) {
                e.printStackTrace();
            }
        };
        adapter = new MainFileAdpter(new SmbFile[]{}, callback);
        binding.recyclerView.setAdapter(adapter);
        binding.btnStart.setOnClickListener(view -> {
            binding.setLoadStatus(1);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        currentSmbFileDir = new SmbFile(smbPath);
                        adapter.setCurrentSmbFiles(currentSmbFileDir.listFiles());
                        handler.sendEmptyMessage(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        });

//        binding.btnFile.setOnClickListener(view -> );
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        mainFileServer = new MainFileServer();
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
        } else {
            super.onBackPressed();
        }
    }

    static class MainHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        public MainHandler(MainActivity mainActivity) {
            this.weakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = weakReference.get();
            mainActivity.binding.setLoadStatus(2);
            mainActivity.adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        mainFileServer.stop();
        super.onDestroy();
    }
}
