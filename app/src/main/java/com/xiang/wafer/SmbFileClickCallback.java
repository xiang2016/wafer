package com.xiang.wafer;

import jcifs.smb.SmbFile;

/**
 * <pre>
 *     author : ZhaoMiXiang
 *     time   : 2018/01/24
 *     desc   : 描述
 *     version: 1.0
 * </pre>
 */
public interface SmbFileClickCallback {
    void onSmbFileClick(SmbFile smbFile);
}
