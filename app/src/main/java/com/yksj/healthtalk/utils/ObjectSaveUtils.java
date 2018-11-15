package com.yksj.healthtalk.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

/**
 * 对象存储工具类
 * 
 * @author crj
 * 
 */
public class ObjectSaveUtils<T extends Serializable> {

	/**
	 * 读取序列化对象
	 * @param userid
	 * @param context
	 * @return
	 */
	public List<T> readObjectFromStorage(String userid, Context context,
			String path) {
		List<T> data = new ArrayList<T>();
		String filePath = path + "_" + userid;
		ObjectInputStream objectInputStream = null;
		FileInputStream fileInputStream = null;

		try {
			fileInputStream = context.openFileInput(filePath);
			objectInputStream = new ObjectInputStream(fileInputStream);
			int size = Integer.parseInt(objectInputStream.readObject()
					.toString());
			for (int i = 0; i < size; i++) {
				data.add((T) objectInputStream.readObject());
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (objectInputStream != null)
					objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 对象序列化写入对应用户对象到对应的文件内
	 * 
	 * @param data
	 * @param userid
	 */
	public void writeObjectToStorage(List<T> data, String userid,
			Context context, String path) {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		String filepath = path + "_" + userid;
		try {
			fileOutputStream = context.openFileOutput(filepath,Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(data.size() + "");
			for (int i = 0; i < data.size(); i++) {
				objectOutputStream.writeObject(data.get(i));
			}
			objectOutputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectOutputStream != null)
					objectOutputStream.close();
				if (fileOutputStream != null)
					fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeMapToStorage(HashMap<String, String> data, String userid,
			Context context, String path) {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		String filepath = path + "_" + userid;
		try {
			fileOutputStream = context.openFileOutput(filepath,
					Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(data);
			objectOutputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectOutputStream != null)
					objectOutputStream.close();
				if (fileOutputStream != null)
					fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public HashMap<String, String> readMapFromStorage(String userid, Context context,
			String path) {
		HashMap<String, String> data = new HashMap<String, String>();;
		String filePath = path + "_" + userid;
		ObjectInputStream objectInputStream = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = context.openFileInput(filePath);
			objectInputStream = new ObjectInputStream(fileInputStream);
			data =  (HashMap<String, String>) objectInputStream.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
				if (objectInputStream != null)
					objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
}
