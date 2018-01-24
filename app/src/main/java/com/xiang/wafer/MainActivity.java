package com.xiang.wafer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.xiang.wafer.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };
    private MainFileAdpter adapter;
    private String smbPath = "smb://zhaomx:123456@192.168.0.108/movie/";
    private ActivityMainBinding binding;
    private SmbFile currentSmbFileDir;
    private SmbFileClickCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setCurrentPath("\\\\192.168.0.108/movie/");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        callback = new SmbFileClickCallback() {
            @Override
            public void onSmbFileClick(SmbFile smbFile) {
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
            }
        };
        adapter = new MainFileAdpter(new SmbFile[]{}, callback);
        binding.recyclerView.setAdapter(adapter);
        binding.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            currentSmbFileDir = new SmbFile(smbPath);
                            adapter.setCurrentSmbFiles(currentSmbFileDir.listFiles());
                            handler.sendEmptyMessage(1);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
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
}
