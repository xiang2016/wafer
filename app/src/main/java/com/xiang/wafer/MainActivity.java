package com.xiang.wafer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setCurrentPath(getCurrentPath(smbPath));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainFileAdpter(new SmbFile[]{}, new SmbFileClickCallback() {
            @Override
            public void onSmbFileClick(SmbFile smbFile) {
                try {
                    if (smbFile.isDirectory()) {
                        binding.setCurrentPath(smbFile.getCanonicalPath());
                        Log.d(TAG, "run: getCanonicalPath" + smbFile.getCanonicalPath());
                        Log.d(TAG, "run: getPath" + smbFile.getPath());
                        Log.d(TAG, "run: getUncPath" + smbFile.getUncPath());
                        Log.d(TAG, "run: getDfsPath" + smbFile.getDfsPath());
                        Log.d(TAG, "run: getParent" + smbFile.getParent());
                        Log.d(TAG, "run: getShare" + smbFile.getShare());
                        adapter.setCurrentSmbFiles(smbFile.listFiles());
                        handler.sendEmptyMessage(1);
                    }
                } catch (SmbException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            SmbFile remoteFile = new SmbFile(smbPath);
                            adapter.setCurrentSmbFiles(remoteFile.listFiles());
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

    private String getCurrentPath(String smbPath) {
        return smbPath;
    }
}
