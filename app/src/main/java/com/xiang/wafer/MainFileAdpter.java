package com.xiang.wafer;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xiang.wafer.databinding.ItemFileBinding;

import jcifs.smb.SmbFile;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/24
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class MainFileAdpter  extends RecyclerView.Adapter<MainFileAdpter.FileHolder> {
    private SmbFile[] currentSmbFiles;
    private SmbFileClickCallback callback;

    public MainFileAdpter(SmbFile[] currentSmbFiles,SmbFileClickCallback callback) {
        this.currentSmbFiles = currentSmbFiles;
        this.callback = callback;
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_file, parent, false);
        return new FileHolder(binding);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        holder.binding.setSmbFile(currentSmbFiles[position]);
        holder.binding.setCallback(callback);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return currentSmbFiles.length;
    }

    public void setCurrentSmbFiles(SmbFile[] currentSmbFiles) {
        this.currentSmbFiles = currentSmbFiles;
    }

    class FileHolder extends RecyclerView.ViewHolder {

        private final ItemFileBinding binding;

        public FileHolder(ItemFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}