package com.yksj.healthtalk.net.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.library.base.utils.StorageUtils;

/**
 * 文件下载异步类
 * @author zhao
 *
 */
public class FileDownHttpResponseHandler extends BinaryHttpResponseHandler {
	String mFileName;
	String mFilePath;
	public File mFile;
	
	public FileDownHttpResponseHandler(String filePath,String name) {
		this.mFileName = name;
		this.mFilePath = filePath;
	}
	
	@Override
	public boolean onProcess(byte[] bytes) throws IOException {
		if(!StorageUtils.isSDMounted())throw new IOException();
		if(bytes == null || bytes.length == 0)throw new IOException("Save file fail");
		File dir = new File(mFilePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		File file = new File(dir,mFileName);
		if(file.exists())return false;
		file.createNewFile();
		mFile = file;
		FileOutputStream fileOutputStream = null;
		try{
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bytes);
		}finally{
			if(fileOutputStream != null){
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		return false;
	}
}
