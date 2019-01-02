package org.videolan.streaming;

/**
 * @class name：net.majorkernelpanic.streaming.audio
 * @anthor create by Zhaoli.Wang
 * @time 2017/8/11 16:30
 */


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.OutputStream;
import java.util.LinkedList;


public class Saudioclient extends Thread
{
    protected AudioRecord m_in_rec ;
    protected int         m_in_buf_size ;
    protected byte []     m_in_bytes ;
    protected boolean     m_keep_running ;
    protected OutputStream dout;
    protected LinkedList<byte[]> m_in_q ;

    protected int mFrameSize = 640;
    private final int RtpheadSize = 12;
    private volatile Thread runner;


    public void run()
    {
        try
        {
            android.os.Process.setThreadPriority(
                        android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            m_in_buf_size =  AudioRecord.getMinBufferSize(8000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if(null == m_in_rec){
                m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        m_in_buf_size) ;
            }
            int seqn = 0;
            long time = 0;
            byte[] buffer = new byte[mFrameSize + RtpheadSize];
            RtpPacket rtp_packet = new RtpPacket(buffer, 0);
            m_in_bytes = new byte[mFrameSize];

            rtp_packet.setPayloadType(0);
            m_in_rec.startRecording() ;
            while(m_keep_running)
            {
                byte[] packet = rtp_packet.getPacket();
                int bytesRecord =m_in_rec.read(m_in_bytes, 0, mFrameSize);
                if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION || bytesRecord == AudioRecord.ERROR_BAD_VALUE) {
                    continue;
                }
                if (bytesRecord != 0 && bytesRecord != -1){
                    //音频增强
                    for (int i = 0; i <m_in_bytes.length ; i++) {
                        m_in_bytes[i]= (byte) (m_in_bytes[i]*2);
                    }
                    System.arraycopy(m_in_bytes, 0, packet, RtpheadSize, m_in_bytes.length);
                    rtp_packet.setSequenceNumber(seqn++);
                    rtp_packet.setTimestamp(time);

                    //发送数据
                    byte[] mTcpHeader = new byte[]{'$', 0, 0, 0};
                    mTcpHeader[1] = 0;
                    int len =packet.length;
                    mTcpHeader[2] = (byte) (len >> 8);
                    mTcpHeader[3] = (byte) (len & 0xFF);
                    Log.e("Saudioclient","Saudioclient"+len);
                    dout.write(mTcpHeader);
                    dout.write(packet, 0, len);
                    time += mFrameSize;
                }

            }
            free();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void startThread()
    {
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }


    public void init(OutputStream mOutputStream)
    {
        m_keep_running = true ;
        m_in_q=new LinkedList<byte[]>();
        this.dout = mOutputStream;

//        m_in_rec.read(m_in_bytes, 0, m_in_buf_size) ;
//        bytes_pkg = m_in_bytes.clone() ;
//        if(m_in_q.size() >= 2)
//        {
//            dout.write(m_in_q.removeFirst() , 0, m_in_q.removeFirst() .length);
//        }
//        m_in_q.add(bytes_pkg) ;
    }



    public void free()
    {
        try {
            m_keep_running = false ;
            Thread.sleep(500) ;
            m_in_rec.stop() ;
            m_in_rec.release();
            m_in_rec = null ;
            m_in_bytes = null ;
//            dout.close();
        } catch(Exception e) {
            Log.d("sleep exceptions...\n","") ;
        }
    }



    public void  stopPush(){
        free();
        runner = null;

    }


}