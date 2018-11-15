package com.yksj.healthtalk.utils;

import android.os.AsyncTask;

public class DeleteAsyncTask extends AsyncTask<Void,Void,Void>{

	private Runnable preExecuteListener;
	private Runnable PostExecuteListener;
	private Runnable doInBackgroudListener;
	
	public DeleteAsyncTask(Runnable preExecuteListener,
			Runnable postExecuteListener, Runnable doInBackgroudListener) {
		this.preExecuteListener = preExecuteListener;
		this.PostExecuteListener = postExecuteListener;
		this.doInBackgroudListener = doInBackgroudListener;
	}

	
	@Override
	protected void onPreExecute() {
		if(preExecuteListener !=null){
			preExecuteListener.run();
		}
	}


	@Override
	protected void onPostExecute(Void result) {
		if (PostExecuteListener != null)
			PostExecuteListener.run();
	}


	@Override
	protected Void doInBackground(Void... params) {
		if (doInBackgroudListener != null)
			doInBackgroudListener.run();
		return null;
	}

}
