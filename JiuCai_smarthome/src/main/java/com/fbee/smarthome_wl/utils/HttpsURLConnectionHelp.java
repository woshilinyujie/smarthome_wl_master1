package com.fbee.smarthome_wl.utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsURLConnectionHelp {
	
	public static final int HTTP_READ_TIMEOUT = 70 * 1000;
	public static final int HTTP_CONNECT_TIMEOUT = 70 * 1000;
	/**
	 * 加密协议
	 */
	public static final TrustManager truseAllManager = new X509TrustManager() {

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	private static int contentLength;

//	public static String requesByGetToString(String url, String jsessionid) {
//		String message = null;
//		try {
//			HttpsURLConnection conn = getHttpsConnection(url);
//
//			if(jsessionid != null){
//				conn.setRequestProperty("cookie", jsessionid);
//			}
//			// 请求成功（相应码 == 200）
//			if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
//				// 取得该连接的输入流，以读取响应内容
//				InputStreamReader insr = new InputStreamReader(
//						conn.getInputStream());
//				StringBuffer strb = new StringBuffer();
//				int s;
//				while ((s = insr.read()) != -1) {
//					strb.append((char) s);
//				}
//				message = strb.toString();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return message;
//	}
	
	public static InputStream requesByGetToStream(String url) {
		InputStream inputStream = null;
		try {
			HttpsURLConnection conn = getHttpsConnection(url);
			//自动重定向新地址
			conn.setInstanceFollowRedirects(true);
			int resCode=conn.getResponseCode();
			// 请求成功（相应码 == 200）
			if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				contentLength = conn.getContentLength();
				inputStream = conn.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}

//	public static Bitmap requesByGetToBitmap(String url) {
//		Bitmap bitmap = null;
//		try {
//			HttpsURLConnection conn = getHttpsConnection(url);
//			//自动重定向新地址
//			conn.setInstanceFollowRedirects(true);
//			// 请求成功（相应码 == 200）
//			if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
////				String cookieval = conn.getHeaderField("set-cookie");
////				String jsessionid = cookieval.substring(0, cookieval.indexOf(";"));
//
//				InputStream is = conn.getInputStream();
//
//				BitmapFactory.Options newOpts = new BitmapFactory.Options();
//				//开始读入图片，此时把options.inJustDecodeBounds 设回true了
//				newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
//				newOpts.inJustDecodeBounds = false;
//				Bitmap tempbitmap = BitmapFactory.decodeStream(is,null,newOpts);
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				tempbitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//				ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//				bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStrea
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bitmap;
//	}
	
//	public String requesByGetToParams(String urlStr, Map<String,String> params, String jsessionid){
//		String responseContent = null;
//		StringBuilder sb = new StringBuilder(urlStr);
//		sb.append("?");
//		for(Map.Entry<String, String> entry:params.entrySet()){
//			sb.append(entry.getKey());
//			sb.append("=");
//			//防止中文乱码
//			sb.append(entry.getValue());
//			sb.append("&");
//		}
//		sb.deleteCharAt(sb.length() -1);
//		try {
//			HttpsURLConnection conn = getHttpsConnection(urlStr);
//
//			conn.setRequestProperty("cookie", jsessionid);
//
//			//请求成功（相应码 == 200）
//			if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
//				InputStream is = conn.getInputStream();
//				int s;
//				StringBuffer strb = new StringBuffer();
//				while((s = is.read()) != -1){
//					strb.append((char)s);
//				}
//				responseContent = strb.toString();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return responseContent;
//	}
	
	public static HttpsURLConnection getHttpsConnection(String urlStr) {
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		HttpsURLConnection conn = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { truseAllManager }, null);
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			URL myURL = new URL(urlStr);
			
			conn = (HttpsURLConnection) myURL.openConnection();
			//设置加密协议
			conn.setSSLSocketFactory(ssf);
			//设置请求方式
			conn.setRequestMethod("GET");
			//设置连接超时时长
			conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
			
			conn.setReadTimeout(HTTP_READ_TIMEOUT);
			
			conn.setHostnameVerifier(new HostnameVerifier(){   
                public boolean verify(String hostname, SSLSession session) {   
                        return true;   
                }});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	private int  downloadZip(String url, String path) {
		int result=0;
		InputStream is = null;
		OutputStream outputStream = null;
		try {
			is = HttpsURLConnectionHelp.requesByGetToStream(url);
			boolean tag=FileUtils.createFile(path);
			if(tag==true){
				outputStream = new FileOutputStream(path);
				int byteCount = 0;

				byte[] bytes = new byte[1024];
				while ((byteCount = is.read(bytes)) != -1) {
					outputStream.write(bytes, 0, byteCount);
				}
				result=1;
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if(is!=null){
					is.close();
				}
				if(outputStream!=null){
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
