package com.jwlkj.idc.jni;

public class JwlJni {
	private static final int DEFAULT_COMPRESSION = 4;
	static {
		System.loadLibrary("jwl");
//		open(DEFAULT_COMPRESSION);
	}
	
	public static void init(){
		open(DEFAULT_COMPRESSION);
	}

	public static native byte[] decodeVideo(byte[] data, int size);
	public static native long[] decodeOpen();
	public static native int open(int quality);
	public static native int encode(short[] pInBuf, int Offset, byte[] pOutBuf,
			int size);
	public static native int decode(byte[] pInBuf, int len, short[] pOutBuf,
			int size);
	public static native int getFrameSize();
	public static native void close();

	public static native void initFarMicDenoise();
	public static native void initLocalMicDenoise();
	public static native void doFarDenoise(byte buf[], int length);
	public static native void doLocalDenoise(short buf[],int length);
//	public native void unInitDenoise();
	public static native void unInitFarDenoise();
	public static native void unInitLocalDenoise();
	
	//解码h264
	public static native int openH264(int with,int height);
	public static native int decodeH264(byte[] data, int size,byte[] out);
	public static native int closeH264();
	//编解码音频
	public static native byte[] decodeAdpcm(byte[] data, int size);
	public static native byte[] encodeAdpcm(byte[] data, int size);
	
	
	
	
//	public static native void initAce(int frameSize, int length, int rate);
//	public static native void doAce(short[] mic,short[] dest, int size);
//	public static native void unInitAce();
//	public static native void initFarMicDenoise();
//	public static native void farMicDoDenoise(short[] farMic, int size);
//	public static native void unInitFarMicDenoise();
	
	// public static native String openLock();
}
