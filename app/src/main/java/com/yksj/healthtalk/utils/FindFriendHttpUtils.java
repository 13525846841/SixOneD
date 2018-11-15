package com.yksj.healthtalk.utils;


/**
 * 找病友模块的Http请求工具类
 * @author lmk
 *
 */
public class FindFriendHttpUtils {
	
	/**
	 * 加载病友列表
	 * @param cus
	 * @param sourceType
	 * @param httpListener
	 
	public static void loadFriends(final CustomerInfoEntity cus,final String type,final int sourceType,final FriendHttpListener httpListener){
		ApiService.doHttpRequestSearchFriends(cus, type,sourceType, new ObjectHttpResponseHandler() {
			@Override
			public Object onParseResponse(String content) {
				if(content!=null &&content.contains("error_message"))
					return content;
				return FriendHttpUtil.jsonAnalysisFriendEntity(content, false);
			}
			@Override
			public void onSuccess(Object response) {
				super.onSuccess(response);
				//	//{"error_code":"0","error_message":"网络出现问题，请稍后再试"}
				if(response !=null && response instanceof List){
					httpListener.responseSuccess(cus.getType(), sourceType, (ArrayList<CustomerInfoEntity>) response);
				}else if(response !=null && response instanceof String){
					httpListener.responseError(cus.getType(), sourceType, (String) response);
				}
			}
		});
	}*/
	
	
}
