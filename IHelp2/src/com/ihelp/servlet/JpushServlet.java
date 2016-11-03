package com.ihelp.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.DeviceEnum;
import cn.jpush.api.push.CustomMessageParams;
import cn.jpush.api.push.MessageResult;
import cn.jpush.api.push.NotificationParams;
import cn.jpush.api.push.ReceiverTypeEnum;

public class JpushServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final String appKey ="0766e97bc1d6ea22d8d253dd";
	private static final String masterSecret = "abadfcf7d266c0953f276844";
	
	/**
	 * Constructor of the object.
	 */
	public JpushServlet() {
		super();
	}
	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an errSor occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//闁兼儳鍢茶ぐ鍥╋拷閵忊�鐓曠紒鏃撴嫹閻犙呭█閿熶粙鏌岄幋锝囨垢"濞磋偐濮靛鐢告儍閸曨厽鏆忛柟鏉戝槻閹拷
		//response.setContentType("utf-8");
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		String tuisonguserid=URLDecoder.decode(request.getParameter("tuisonguserid"),"UTF-8");
		System.out.println(tuisonguserid+"a2222");
		System.out.println(action+"aaaa");
		//创建JpushClient,用于发送jpush通知
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, 0, DeviceEnum.Android, false);

		//閻犲鍟伴弫顦減ushClient闁汇劌澧昬ndNotification闁哄倽顬冪涵鍫曞矗閹达讣鎷烽梺顐ｆ皑閻擄拷
		if(action.equals("sendgoldchange")){
			String number =URLDecoder.decode(request.getParameter("number"),"UTF-8");
			String giveUsername =URLDecoder.decode(request.getParameter("giveUsername"),"UTF-8");
			System.out.println(number+"1111");
			System.out.println(giveUsername+"bbbb");
			//发送通知的参数
			NotificationParams params = new NotificationParams();
			params.setReceiverType(ReceiverTypeEnum.ALIAS);
			params.setReceiverValue(tuisonguserid);
			params.setAndroidNotificationTitle(action);
			params.setTimeToLive(864000);
			MessageResult msgResult = jpushClient.sendNotification("收到来自"+giveUsername+"的金豆"+number+"颗！～～请查收～", params, null);
			if (msgResult.isResultOK()) {
				System.out.println(msgResult);
//		        Log.info("msgResult - " + msgResult);
//		        LOG.info("messageId - " + msgResult.getMessageId());
			} else {
			    if (msgResult.getErrorCode() > 0) {
			        // 婵炴垶鎸婚懝鐐叏閻斿鍤曢柛灞炬皑閸╋拷
			        System.out.println("Service error - ErrorCode: "
			                + msgResult.getErrorCode() + ", ErrorMessage: "
			                + msgResult.getErrorMessage());
			    } else {
			        // 闂佸搫鐗滄禍婊堝春鐏炵偓缍囬柨鐔虹ウPush 
			        System.out.println("Other excepitons - "
			                + msgResult.responseResult.exceptionString);
			    }
			}
		}
		else if(action.equals("sendchatmessage")){
			String username =URLDecoder.decode(request.getParameter("username"),"UTF-8");
			String message =URLDecoder.decode(request.getParameter("message"),"UTF-8");
			System.out.println(username+"4444");
			System.out.println(message+"3333");
			//发送消息的参数
//			CustomMessageParams params1=new CustomMessageParams();
//			params1.setReceiverType(ReceiverTypeEnum.ALIAS);
//			params1.setReceiverValue(tuisonguserid);
//			params1.setTimeToLive(864000);
//			MessageResult msgResult = jpushClient.sendCustomMessage("请求帮助", "来自"+username+"的消息："+message, params1, null);
			NotificationParams params = new NotificationParams();
			params.setReceiverType(ReceiverTypeEnum.ALIAS);
			params.setReceiverValue(tuisonguserid);			
			params.setAndroidNotificationTitle(action);
			params.setTimeToLive(864000);
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("name", "李畅");
		    //MessageResult msgResult = jpushClient.sendNotification("来自"+username+"的消息："+message, params, null);
			MessageResult msgResult = jpushClient.sendNotification("来自"+username+"的消息："+message, params,map);
			if (msgResult.isResultOK()) {
				System.out.println(msgResult);
//		        Log.info("msgResult - " + msgResult);
//		        LOG.info("messageId - " + msgResult.getMessageId());
			} else {
			    if (msgResult.getErrorCode() > 0) {
			        // 婵炴垶鎸婚懝鐐叏閻斿鍤曢柛灞炬皑閸╋拷
			        System.out.println("Service error - ErrorCode: "
			                + msgResult.getErrorCode() + ", ErrorMessage: "
			                + msgResult.getErrorMessage());
			    } else {
			        // 闂佸搫鐗滄禍婊堝春鐏炵偓缍囬柨鐔虹ウPush 
			        System.out.println("Other excepitons - "
			                + msgResult.responseResult.exceptionString);
			    }
			}
		}
		
		//MessageResult msgResult = jpushClient.sendNotification("新的消息·～～", params, null);
//        LOG.debug("responseContent - " + msgResult.responseResult.responseContent);
		
	}
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
}
