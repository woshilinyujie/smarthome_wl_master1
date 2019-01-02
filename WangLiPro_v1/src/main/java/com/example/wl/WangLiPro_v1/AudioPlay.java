package com.example.wl.WangLiPro_v1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

import com.jwl.android.jwlandroidlib.udp.UdpConfig;
import com.jwlkj.idc.jni.JwlJni;

import java.util.concurrent.ConcurrentLinkedQueue;


public class AudioPlay extends Thread {
    private final int frequency = 8000;
    private final int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    private final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int recordBufSize = 0;
    private int playBufSize = 0;
    private AudioTrack audioTrack;

    public AudioPlay() {
        recordBufSize = AudioRecord.getMinBufferSize(frequency,
                AudioFormat.CHANNEL_IN_MONO, audioEncoding);
        playBufSize = AudioTrack.getMinBufferSize(frequency, channelConfig,
                audioEncoding);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                channelConfig, audioEncoding, playBufSize,
                AudioTrack.MODE_STREAM);
        list.clear();
    }

    public static ConcurrentLinkedQueue<byte[]> list = new ConcurrentLinkedQueue<byte[]>();



    @Override
    public void run() {
        list.clear();
        audioTrack.play();
        byte[] audioBuf;
        while (UdpConfig.udpRunBoo) {
            if (!list.isEmpty()) {
                audioBuf = list.poll();
                if (audioBuf == null)
                    return;
                byte[] decodeAudioBuf = JwlJni.decodeAdpcm(audioBuf,
                        audioBuf.length);
                audioTrack.write(decodeAudioBuf, 0, decodeAudioBuf.length);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    public void close() {
        JwlJni.unInitFarDenoise();
        list.clear();
    }
}