package com.yksj.healthtalk.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TextView;

import com.yksj.consultation.login.LoginOutDialogActivity;
import com.yksj.consultation.login.UserLoginActivity;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.consultation.sonDoc.R;

/**
 * 系统所有的dialog面板
 * @author Administrator
 *
 */
public class DialogUtils {

	/**
	 * 默认系统加载 
	 * @param context
	 * @param content
	 * @return
	 */
	public static Dialog getLoadingDialog(Context context,String content){
		Dialog dialog = new Dialog(context,R.style.translucent_dialog);
		dialog.setContentView(R.layout.loading_dialog_layout);
		TextView textView = (TextView)dialog.findViewById(R.id.loadingTxt);
		textView.setText(content);
		return dialog;
	}

	/**
	 * 系统错误dialog
	 * @param context
	 * @param title 标题
	 * @param str 错误提示内容
	 * @return dialog
	 */
	public static Dialog getErrorDialog(Context context,String title,String content){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title);
		builder.setMessage(content);
		builder.setNeutralButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}

	/**
	 * 挤下线对话框
	 * @param context
	 * @return
	 */
	public static void showLoginOutDialog2(final Context context){
		Intent intent = new Intent(context,LoginOutDialogActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * titleName 名字
	 * message 内容
	 * @return
	 */
	public static AlertDialog.Builder showBasicDialog(Context mContext,String titleName,String message){
		AlertDialog.Builder builder=new Builder(mContext);
		builder.setTitle(titleName+"");
		builder.setMessage(message);
		return builder;
	}

	/**
	 * 关注的时候提示信息
	 * @param context
	 */
	public static void  PromptDialogBox(final Context context,String message,final CustomerInfoEntity entity ){
		AlertDialog.Builder mBuilder = DialogUtils.showBasicDialog(
				context, context.getString(R.string.app_name),
				message);
		mBuilder.setNegativeButton(context.getString(R.string.cancel), null);
		mBuilder.setPositiveButton(context.getString(R.string.sure),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {//0 没有关系 1是关注的 2是黑名单 3是客户 4医生 5 我的合作者
				int id=Integer.valueOf(SmartFoxClient.getLoginUserId());
				if (entity.getIsAttentionFriend() == 2) {//从黑名单  到 加关注
					FriendHttpUtil.requestHttpAboutFriend(id, entity, 1);
					//							FriendHttpUtil.requestHttpAboutFriend(Integer.valueOf(SmartFoxClient.getLoginUserId()),entity,5);
					//						FriendHttpUtil.showuploadPopWindow(context, entity);
				}else if(entity.getIsAttentionFriend() == 3){//现在是客户 要做取消关注操作
					FriendHttpUtil.requestHttpAboutFriend(id,entity,5);
				}else if(entity.getIsAttentionFriend() == 4){
					FriendHttpUtil.requestHttpAboutFriend(id,entity,7);
				}
			}
		});
		mBuilder.create().show();
	}

	/**
	 * 从关注--黑名单
	 * @param context
	 * @param id
	 */
	public static void attToBlacklistDialog(Context context ,final CustomerInfoEntity entity){
		AlertDialog.Builder mBuilder = DialogUtils.showBasicDialog(
				context, context.getString(R.string.app_name),
				context.getString(R.string.jiaru_black_note));
		mBuilder.setNegativeButton("取消", null);
		mBuilder.setPositiveButton("加入黑名单",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				FriendHttpUtil.requestHttpAboutFriend(Integer.valueOf(SmartFoxClient.getLoginUserId()),entity,2);
			}
		});
		mBuilder.create().show();
	}
}
