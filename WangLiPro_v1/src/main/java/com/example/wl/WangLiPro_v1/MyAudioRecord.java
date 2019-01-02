package com.example.wl.WangLiPro_v1;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.jwl.android.jwlandroidlib.udp.UdpConfig;
import com.jwl.android.jwlandroidlib.udp.UdpManager;
import com.jwlkj.idc.jni.JwlJni;


public class MyAudioRecord extends Thread {
    private boolean stopFlag = true;
    private final int frequency = 8000;
    private final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int recordBufSize = 0;
    private AudioRecord audioRecord;
    private short[] newRecordBuf = null;
    private byte[] encodedBuf = null;
    private short[] recordBuff = null;
    private byte[] ysData;
    private int audioFrameSize = 0;
    private byte[] comByte;
    private boolean audioBoo = false;

    private OpenRecordInter inter;

    public interface OpenRecordInter {
        abstract void recorderFail();
    }

    public MyAudioRecord(OpenRecordInter inter) {

        this.inter = inter;
        recordBufSize = AudioRecord.getMinBufferSize(frequency,
                AudioFormat.CHANNEL_IN_MONO, audioEncoding);
        recordBufSize = 4000;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                AudioFormat.CHANNEL_IN_MONO, audioEncoding, 8000);
        recordBuff = new short[recordBufSize + 160];
        encodedBuf = new byte[recordBufSize + 160];
        newRecordBuf = new short[recordBufSize + 160];
        ysData = new byte[2000];
    }


    public void setAudioBoo(boolean b) {
        audioBoo = b;
    }

    @Override
    public void run() {
        try {
            audioRecord.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
            //创建一个dialog
            inter.recorderFail();
            return;
        }

        int frameNo = 2000;
        while (UdpConfig.udpRunBoo) {
//            if (!audioBoo) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            } else {
            if (audioRecord == null) {
                return;
            }
            audioRecord.read(ysData, 0, ysData.length);
            audioFrameSize = 500;
            comByte = new byte[audioFrameSize];

            comByte = JwlJni.encodeAdpcm(ysData, ysData.length);

            try {
                Thread.sleep(10);
                UdpManager.getinstance().sendAudio(comByte);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        }

        try {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 分片操作，4000字节，分4次发送 每次发送1000

    int frameNo = 100;// 从100开始发起
    int fpSize = 1366;
    byte[] cData = new byte[fpSize + 20];


    public void close() {
        JwlJni.unInitLocalDenoise();
        // audioRecord.stop();
        // audioRecord.release();
        // if (audioRecord != null) {
        // audioRecord.stop();
        // audioRecord.release();
        // audioRecord = null;
        // }

    }

    public void pause() {
        // stopFlag = true;
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }

    public void begin() {
        stopFlag = false;
    }
}
