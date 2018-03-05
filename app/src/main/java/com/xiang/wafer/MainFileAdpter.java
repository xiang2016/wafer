package com.xiang.wafer;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xiang.wafer.databinding.ItemFileBinding;

import java.util.Collections;
import java.util.List;

import jcifs.smb.SmbFile;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/24
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public class MainFileAdpter extends RecyclerView.Adapter<MainFileAdpter.FileHolder> {
    private List<SmbFile> currentSmbFiles;
    private SmbFileClickCallback callback;

    public MainFileAdpter(SmbFileClickCallback callback) {
        this.callback = callback;
        currentSmbFiles = Collections.emptyList();
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_file, parent, false);
        return new FileHolder(binding);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, int position) {
        holder.binding.setSmbFile(currentSmbFiles.get(position));
        holder.binding.setCallback(callback);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return currentSmbFiles.size();
    }

    public void setCurrentSmbFiles(List<SmbFile> currentSmbFiles) {
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