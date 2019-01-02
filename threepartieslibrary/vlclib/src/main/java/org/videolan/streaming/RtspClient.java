/*
 * Copyright (C) 2011-2014 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.videolan.streaming;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RFC 2326. A basic and asynchronous RTSP client. The original purpose of this
 * class was to implement a small RTSP client compatible with Wowza. It
 * implements Digest Access Authentication according to RFC 2069.
 */
public class RtspClient {

	private static final Logger log = Logger.getLogger("RTSP_CLIENT");

	/** Message sent when the connection to the RTSP server failed. */
	public final static int ERROR_CONNECTION_FAILED = 0x01;

	/** Message sent when the credentials are wrong. */
	public final static int ERROR_WRONG_CREDENTIALS = 0x03;

	/** Use this to use UDP for the transport protocol. */
	public final static int TRANSPORT_UDP =  0x00;

	/** Use this to use TCP for the transport protocol. */
	public final static int TRANSPORT_TCP = 0x01;

	/**
	 * Message sent when the connection with the RTSP server has been lost for
	 * some reason (for example, the user is going under a bridge). When the
	 * connection with the server is lost, the client will automatically try to
	 * reconnect as long as {@link #stopStream()} is not called.
	 **/
	public final static int ERROR_CONNECTION_LOST = 0x04;

	/**
	 * Message sent when the connection with the RTSP server has been
	 * reestablished. When the connection with the server is lost, the client
	 * will automatically try to reconnect as long as {@link #stopStream()} is
	 * not called.
	 */
	public final static int MESSAGE_CONNECTION_RECOVERED = 0x05;

	private final static int STATE_STARTED = 0x00;
	private final static int STATE_STARTING = 0x01;
	private final static int STATE_STOPPING = 0x02;
	private final static int STATE_STOPPED = 0x03;
	private int mState = 0;

	private class Parameters {
		public String host;
		public String username;
		public String password;
		public String path;
//		public Session session;
		public int port;
		public int transport;

		@Override
		public Parameters clone() {
			Parameters params = new Parameters();
			params.host = host;
			params.username = username;
			params.password = password;
			params.path = path;
//			params.session = session;
			params.port = port;
			params.transport = transport;
			return params;
		}
	}

	private Parameters mTmpParameters;
	private Parameters mParameters;

	private int mCSeq;
	private Socket mSocket;
	private String mSessionID;
	private String mAuthorization;
	private BufferedReader mBufferedReader;
	public OutputStream mOutputStream;
	private Handler mMainHandler;
	private Handler mHandler;

	private SocketChannel mSC;
	Saudioclient client;


	public RtspClient() {
		mCSeq = 0;
		mTmpParameters = new Parameters();
		mTmpParameters.port = 10700;
		mTmpParameters.path = "/";
		mTmpParameters.transport = TRANSPORT_UDP;
		mAuthorization = null;
		mMainHandler = new Handler(Looper.getMainLooper());
		mState = STATE_STOPPED;


//		final Semaphore signal = new Semaphore(0);
//		new HandlerThread("RtspClient") {
//			@Override
//			protected void onLooperPrepared() {
//				mHandler = new Handler() {
//					@Override
//					public void handleMessage(Message msg) {
//						super.handleMessage(msg);
//					}
//				};
//				signal.release();
//			}
//		}.start();
//		signal.acquireUninterruptibly();
	}


	/**
	 * Sets the destination address of the RTSP server.
	 * 
	 * @param host
	 *            The destination address
	 * @param port
	 *            The destination port
	 */
	public void setServerAddress(String host, int port) {
		mTmpParameters.port = port;
		mTmpParameters.host = host;
	}

	/**
	 * If authentication is enabled on the server, you need to call this with a
	 * valid username/password pair. Only implements Digest Access
	 * Authentication according to RFC 2069.
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 */
	public void setCredentials(String username, String password) {
		mTmpParameters.username = username;
		mTmpParameters.password = password;
	}

	/**
	 * The path to which the stream will be sent to.
	 * 
	 * @param path
	 *            The path
	 */
	public void setStreamPath(String path) {
		mTmpParameters.path = path;
	}

	/**
	 * Call this with {@link #TRANSPORT_TCP} or {@value #TRANSPORT_UDP} to
	 * choose the transport protocol that will be used to send RTP/RTCP packets.
	 * Not ready yet !
	 */
	public void setTransportMode(int mode) {
		mTmpParameters.transport = mode;
	}

	public boolean isStreaming() {
		return mState == STATE_STARTED | mState == STATE_STARTING;
	}

	/**
	 * Connects to the RTSP server to publish the stream, and the effectively
	 * starts streaming. You need to call {@link #setServerAddress(String, int)}
	 * and optionnally  and
	 * {@link #setCredentials(String, String)} before calling this. Should be
	 * called of the main thread !
	 */
	public void startStream() {
		if (mTmpParameters.host == null)
			throw new IllegalStateException("setServerAddress(String,int) has not been called !");
//		if (mTmpParameters.session == null)
//			throw new IllegalStateException("setSession() has not been called !");
//		Runnable r = null;
//		r = new Runnable() {
//			@Override
//			public void run() {
				if (mState != STATE_STOPPED)
					return;
				mState = STATE_STARTING;

				log.info("Connecting to RTSP server...");

				// If the user calls some methods to configure the client, it
				// won't modify its behavior until the stream is restarted
				mParameters = mTmpParameters.clone();

				try {
					tryConnection();

				} catch (Exception e) {
					e.printStackTrace();
					abord();
					return;
				}

				try {
					mState = STATE_STARTED;
					//pcm数据
//					client = new Saudioclient();
//					client.init(mOutputStream);
//					client.startThread();

				} catch (Exception e) {
					e.printStackTrace();
					abord();
				}

//			}
//		};// mdy by jzb
//		r = new Runnable() {
//
//			@Override
//			public void run() {
//				mParameters = mTmpParameters.clone();
//				if (mRtpThread == null) {
//					mRtpThread = new RtpThread(mParameters.host, mParameters.port);
//					/**
//					 * if exception happens, send WHAT_THREAD_END_UNEXCEPTION message to handler
//					 */
//					mRtpThread.start(mHandler);
//				}
//				mParameters.session.setDestination(mTmpParameters.host);
//
//				try {
//					mParameters.session.syncConfigure();
//					tryNewConnection();
//
//					//pcm数据
//					client = new Saudioclient();
//					client.init(mOutputStream);
//					client.startThread();
//
////					mParameters.session.syncStart();
//				} catch (Exception e) {
//					e.printStackTrace();
//					mParameters.session = null;
//					mState = STATE_STOPPED;
//					return;
//				}
//			}
//		};
//		mHandler.post(r);
	}

	public OutputStream getmOutputStream() {
		if(null == mOutputStream  ){
                try {
					mOutputStream = new DataOutputStream(mSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		return mOutputStream;
	}


	/**
	 * Udp
	 * @return
     */
	private DatagramSocket socket;
	private int rtpport;
	public  DatagramSocket getUdpSocket(){
		if(null  == socket  ){
			try {
				socket = new  DatagramSocket (rtpport);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return socket;
	}
	private InetAddress serverAddress;
	public InetAddress  getAddress() {
		if(null ==serverAddress){
			try {
				 serverAddress = InetAddress.getByName("192.168.1.12");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return serverAddress;
	}


	public int getRtpport() {
		return rtpport;
	}

	public void changeSpeak(boolean b){
			if(client != null ){
				if(b){
					client.init(mOutputStream);
					client.startThread();
				}else{
					client.stopPush();
				}
			}else{
				log.info("client is null");
			}
	}

	/**
	 * Stops the stream, and informs the RTSP server.
	 */
	public void stopStream() {
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
				if (mState != STATE_STOPPED) {
					mState = STATE_STOPPING;
					abord();
				}

				//停止发送
//				if (null !=client){
//					client.free();
//				}
//				try {
//					if(null != mOutputStream)
//						mOutputStream.close();
//					if(mSocket != null)
//					mSocket.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}

//			}
//		});
	}

	public void release() {
		stopStream();
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				mHandler.getLooper().quit();
//			}
//		});

	}

	private void abord() {
		try {
			sendRequestTeardown();
		} catch (Exception ignore) {
		}
		try {
			if(null != mOutputStream)
				mOutputStream.close();
			mSocket.close();
			socket.close();
		} catch (Exception ignore) {
		}
		mState = STATE_STOPPED;
	}

	private void tryConnection() throws IOException {
		mCSeq = 0;
		mSocket = new Socket(mParameters.host, mParameters.port);
		mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		mOutputStream = new BufferedOutputStream(mSocket.getOutputStream());
		sendRequestOption();
		sendRequestAnnounce();
		sendRequestSetup();
		sendRequestRecord();
	}

	/**
	 * Forges and sends the ANNOUNCE request
	 */
	private void sendRequestAnnounce() throws IllegalStateException, SocketException, IOException {

		String body = getSessionDescription();
		String request = "ANNOUNCE rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + "CSeq: " + (++mCSeq) + "\r\n" + "Content-Length: "
				+ body.length() + "\r\n" + "Content-Type: application/sdp \r\n\r\n" + body;
//		log.info(request.substring(0, request.indexOf("\r\n")));
		log.info(request);
		mOutputStream.write(request.getBytes("UTF-8"));
		mOutputStream.flush();
		Response response = Response.parseResponse(mBufferedReader);

		if (response.headers.containsKey("server")) {
			//log.debug("RTSP server name:" + response.headers.get("server"));
		} else {
			//log.debug("RTSP server name unknown");
		}

		try {
			Matcher m = Response.rexegSession.matcher(response.headers.get("session"));
			m.find();
			mSessionID = m.group(1);
		} catch (Exception e) {
			// throw new
			// IOException("Invalid response from server. Session id: "+mSessionID);
			// mSessionID = "0";
			e.printStackTrace();
		}

		if (response.status == 401) {
			String nonce, realm;
			Matcher m;

			if (mParameters.username == null || mParameters.password == null)
				throw new IllegalStateException("Authentication is enabled and setCredentials(String,String) was not called !");

			try {
				m = Response.rexegAuthenticate.matcher(response.headers.get("www-authenticate"));
				m.find();
				nonce = m.group(2);
				realm = m.group(1);
			} catch (Exception e) {
				throw new IOException("Invalid response from server");
			}

			String uri = "rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path;
			String hash1 = computeMd5Hash(mParameters.username + ":" + m.group(1) + ":" + mParameters.password);
			String hash2 = computeMd5Hash("ANNOUNCE" + ":" + uri);
			String hash3 = computeMd5Hash(hash1 + ":" + m.group(2) + ":" + hash2);

			mAuthorization = "Digest username=\"" + mParameters.username + "\",realm=\"" + realm + "\",nonce=\"" + nonce + "\",uri=\"" + uri + "\",response=\"" + hash3 + "\"";

			request = "ANNOUNCE rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + "CSeq: " + (++mCSeq) + "\r\n" + "Content-Length: "
					+ body.length() + "\r\n" + "Authorization: " + mAuthorization + "\r\n" + (TextUtils.isEmpty(mSessionID) ? "" : ("Session: " + mSessionID + "\r\n"))
					+ "Content-Type: application/sdp \r\n\r\n" + body;

			log.info(request.substring(0, request.indexOf("\r\n")));

			mOutputStream.write(request.getBytes("UTF-8"));
			mOutputStream.flush();
			response = Response.parseResponse(mBufferedReader);

			if (response.status == 401)
				throw new RuntimeException("Bad credentials !");

		} else if (response.status == 403) {
			throw new RuntimeException("Access forbidden !");
		}

	}

	/**
	 * Forges and sends the SETUP request
	 */
	private void sendRequestSetup() throws IllegalStateException, SocketException, IOException {
		int interleaved = 0;
//		for (int i = 0; i < 2; i++) {
//			Stream stream = mParameters.session.getTrack(i);
//			if (stream != null) {
//		String params = "/TCP;unicast;mode=record;interleaved=" + 2 * interleaved + "-" + (2 * interleaved + 1);
		String params  = (";unicast;mode=record;client_port=" + (5000 + 2 * 0) + "-" + (5000 + 2 * 0 + 1));
//				String params = mParameters.transport == TRANSPORT_TCP ? tcp_inter : (";unicast;mode=record;client_port=" + (5000 + 2 * i) + "-" + (5000 + 2 * i + 1));
				String request = "SETUP rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + "/trackID=" + 0 + " RTSP/1.0\r\n" + "Transport: RTP/AVP" + params
						+ "\r\n" + addHeaders();

				log.info(request);

//				log.info(request.substring(0, request.indexOf("\r\n")));

				mOutputStream.write(request.getBytes("UTF-8"));
				mOutputStream.flush();
				Response response = Response.parseResponse(mBufferedReader);

				log.info(response.toString());

				try {
					Matcher m = Response.rexegSession.matcher(response.headers.get("session"));
					m.find();
					mSessionID = m.group(1);
				} catch (Exception e) {
					// throw new
					// IOException("Invalid response from server. Session id: "+mSessionID);
					// mSessionID = "0";
					e.printStackTrace();
				}

				Matcher m;
//				if (mParameters.transport == TRANSPORT_UDP) {
					try {
						m = Response.rexegTransport.matcher(response.headers.get("transport"));
						m.find();
						rtpport = Integer.parseInt(m.group(3));
						log.info("rtpport"+rtpport);
//						stream.setDestinationPorts(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
						//log.debug("Setting destination ports: " + Integer.parseInt(m.group(3)) + ", " + Integer.parseInt(m.group(4)));
					} catch (Exception e) {
						e.printStackTrace();
//						int[] ports = stream.getDestinationPorts();
						//log.debug("Server did not specify ports, using default ports: " + ports[0] + "-" + ports[1]);
					}
//				} else {
//					stream.setOutputStream(mOutputStream, (byte) (2 * interleaved));
//				}
//				interleaved++;
//			}
//		}
	}

	/**
	 * Forges and sends the RECORD request
	 */
	public void sendRequestRecord() throws IllegalStateException, SocketException, IOException {
		String request = "RECORD rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + "Range: npt=0.000-\r\n" + addHeaders();
//		log.info(request.substring(0, request.indexOf("\r\n")));
		log.info(request);
		mOutputStream.write(request.getBytes("UTF-8"));
		mOutputStream.flush();
		Response.parseResponse(mBufferedReader);
	}

	/**
	 * Forges and sends the RECORD request
	 */
	private void sendRequestPlay() throws IllegalStateException, SocketException, IOException {
		String request = "PLAY rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + "Range: npt=0.000-\r\n" + addHeaders();
		log.info(request.substring(0, request.indexOf("\r\n")));
		mOutputStream.write(request.getBytes("UTF-8"));
		mOutputStream.flush();
		Response.parseResponse(mBufferedReader);
	}


	/**
	 * 暂停请求
	 * @throws IOException
     */
	public void sendRequestPause() throws IOException {
		String request = "PAUSE rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + addHeaders();
		log.info(request.substring(0, request.indexOf("\r\n")));
		mOutputStream.write(request.getBytes("UTF-8"));
		mOutputStream.flush();
	}

	/**
	 * Forges and sends the RECORD request
	 */
	public void send2RequestRecord() throws IllegalStateException, SocketException, IOException {
		String request = "RECORD rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + "Range: npt=now-\r\n" + addHeaders();
//		log.info(request.substring(0, request.indexOf("\r\n")));
		log.info(request);
		mOutputStream.write(request.getBytes("UTF-8"));
		mOutputStream.flush();
		Response.parseResponse(mBufferedReader);
	}


	/**
	 * Forges and sends the TEARDOWN request
	 */
	private void sendRequestTeardown() throws IOException {
		String request = "TEARDOWN rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + addHeaders();
		log.info(request.substring(0, request.indexOf("\r\n")));
		mOutputStream.write(request.getBytes("UTF-8"));
		mOutputStream.flush();
	}

	/**
	 * Forges and sends the OPTIONS request
	 */
	private void sendRequestOption() throws IOException {
		String request = "OPTIONS rtsp://" + mParameters.host + ":" + mParameters.port + mParameters.path + " RTSP/1.0\r\n" + addHeaders();
		log.info(request.substring(0, request.indexOf("\r\n")));
		mOutputStream.write(request.getBytes("UTF-8"));
		mOutputStream.flush();
		Response.parseResponse(mBufferedReader);
	}

	private String addHeaders() {
		return "CSeq: " + (++mCSeq) + "\r\n" + (TextUtils.isEmpty(mSessionID) ? "" : ("Session: " + mSessionID + "\r\n")) +
		// For some reason you may have to remove last "\r\n" in the
		// next line
		// to make the RTSP client work with your wowza server :/
				(mAuthorization != null ? "Authorization: " + mAuthorization + "\r\n" : "") + "\r\n";
	}




	public String getSessionDescription() {
		long uptime = System.currentTimeMillis();
		long mTimestamp = (uptime / 1000) << 32
				& (((uptime - ((uptime / 1000) * 1000)) >> 32) / 1000); // NTP
		// timestamp
		String mOrigin = "127.0.0.0";

		StringBuilder sessionDescription = new StringBuilder();
		if (mParameters.host == null) {
			throw new IllegalStateException(
					"setDestination() has not been called !");
		}
		sessionDescription.append("v=0\r\n");
		// TODO: Add IPV6 support
		sessionDescription.append("o=- " + mTimestamp + " " + mTimestamp
				+ " IN IP4 " + mOrigin + "\r\n");
		sessionDescription.append("s=Unnamed\r\n");
		sessionDescription.append("i=N/A\r\n");
//		sessionDescription.append("c=IN IP4 " + "124.193.154.2" + "\r\n");
		sessionDescription.append("c=IN IP4 "+mParameters.host+"\r\n");
		// t=0 0 means the session is permanent (we don't know when it will
		// stop)
		sessionDescription.append("t=0 0\r\n");
		sessionDescription.append("a=recvonly\r\n");
		// Prevents two different sessions from using the same peripheral at the
		// same time

		sessionDescription.append(getAudioSessionDescription());
		sessionDescription.append("a=control:trackID=" + 0 + "\r\n");

		return sessionDescription.toString();
	}

	public String getAudioSessionDescription() {
		return "m=audio "+"0"+" RTP/AVP 97\r\n" +
				"a=rtpmap:97 PCMU/8000\r\n" +
				"a=fmtp:97 octet-align=1;\r\n";
	}




	final protected static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/** Needed for the Digest Access Authentication. */
	private String computeMd5Hash(String buffer) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			return bytesToHex(md.digest(buffer.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException ignore) {
		} catch (UnsupportedEncodingException e) {
		}
		return "";
	}



	public static class Response {

		// Parses method & uri
		public static final Pattern regexStatus = Pattern.compile("RTSP/\\d.\\d (\\d+) (\\w+)", Pattern.CASE_INSENSITIVE);
		// Parses a request header
		public static final Pattern rexegHeader = Pattern.compile("(\\S+):(.+)", Pattern.CASE_INSENSITIVE);
		// Parses a WWW-Authenticate header
		public static final Pattern rexegAuthenticate = Pattern.compile("realm=\"(.+)\",\\s+nonce=\"(\\w+)\"", Pattern.CASE_INSENSITIVE);
		// Parses a Session header
		public static final Pattern rexegSession = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE);
		// Parses a Transport header
		public static final Pattern rexegTransport = Pattern.compile("client_port=(\\d+)-(\\d+).+server_port=(\\d+)-(\\d+)", Pattern.CASE_INSENSITIVE);

		public int status;
		public HashMap<String, String> headers = new HashMap<String, String>();

		/** Parse the method, uri & headers of a RTSP request */
		public static Response parseResponse(BufferedReader input) throws IOException, IllegalStateException, SocketException {
			Response response = new Response();
			String line;
			Matcher matcher;
			// Parsing request method & uri
			if ((line = input.readLine()) == null)
				throw new SocketException("Connection lost");
			//log.debug(line);
			matcher = regexStatus.matcher(line);
			matcher.find();
			response.status = Integer.parseInt(matcher.group(1));

			// Parsing headers of the request
			while ((line = input.readLine()) != null) {
				//log.debug(line);
				if (line.length() > 3) {
					matcher = rexegHeader.matcher(line);
					matcher.find();
					response.headers.put(matcher.group(1).toLowerCase(Locale.US), matcher.group(2));
				} else {
					break;
				}
			}
			if (line == null)
				throw new SocketException("Connection lost");

			//log.debug("Response from server: " + response.status);

			return response;
		}
	}

}
