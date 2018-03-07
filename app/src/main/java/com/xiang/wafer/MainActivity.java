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
import android.widget.Toast;

import com.xiang.wafer.databinding.ActivityMainBinding;
import com.xiang.wafer.model.Login;
import com.xiang.wafer.model.User;
import com.xiang.wafer.model.WinServer;

import java.io.File;

import jcifs.smb.SmbException;

import static com.xiang.wafer.MainFileServer.URL;
import static fi.iki.elonen.NanoHTTPD.getMimeTypeForFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainFileAdpter adapter;
    private ActivityMainBinding binding;
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
        user = new User("", "");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MainViewModel.Factory factory = new MainViewModel.Factory(new SmbFileRepository(new AppExecutors()));
        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        mainViewModel.getSmbfiles().observe(this, smbFiles -> {
            if (smbFiles.size() > 0) {
                binding.setLoadStatus(0);
            }
            adapter.setCurrentSmbFiles(smbFiles);
            adapter.notifyDataSetChanged();
        });
        mainViewModel.getStatus().observe(this, integer -> {
            binding.setLoadStatus(integer);
        });
        mainViewModel.getCurrentPath().observe(this, s -> {
            binding.setCurrentPath(s);
        });
        initParams();
        myCheckPermission();
        mainFileServer = new MainFileServer();
    }


    private void initParams() {
        binding.setServer(winServer);
        binding.setUser(user);
        binding.setLoadStatus(1);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        callback = smbFile -> {
            try {
                if (smbFile.isDirectory()) {
                    String path = smbFile.getPath();
                    String currentPath = path.substring(path.indexOf("@") + 1);
                    mainViewModel.getCurrentPath().setValue(currentPath);
                } else {
                    Uri uriSmb = Uri.parse(smbFile.getPath());
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
            Login login = new Login();
            User user = binding.layoutLogin.getUser();
            WinServer server = binding.getServer();
            login.setUser(user);
            login.setWinServe(server);
            mainViewModel.setLogin(login);
            mainViewModel.getCurrentPath().setValue(binding.getServer().host + "/");
            mainFileServer.setLogin(login);
            binding.setLoadStatus(2);
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
        if (binding.getLoadStatus() == 1) {
            super.onBackPressed();
        } else {
            mainViewModel.goBack();
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
