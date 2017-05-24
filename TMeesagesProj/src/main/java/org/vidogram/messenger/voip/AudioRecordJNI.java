package org.vidogram.messenger.voip;

import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;
import java.nio.ByteBuffer;
import org.vidogram.messenger.FileLog;

public class AudioRecordJNI
{
  private AcousticEchoCanceler aec;
  private AutomaticGainControl agc;
  private AudioRecord audioRecord;
  private ByteBuffer buffer;
  private int bufferSize;
  private long nativeInst;
  private NoiseSuppressor ns;
  private boolean running;
  private Thread thread;

  public AudioRecordJNI(long paramLong)
  {
    this.nativeInst = paramLong;
  }

  private int getBufferSize(int paramInt)
  {
    return Math.max(AudioRecord.getMinBufferSize(48000, 16, 2), paramInt);
  }

  private native void nativeCallback(ByteBuffer paramByteBuffer);

  private void startThread()
  {
    if (this.thread != null)
      throw new IllegalStateException("thread already started");
    this.running = true;
    this.thread = new Thread(new Runnable()
    {
      public void run()
      {
        while (true)
        {
          if (AudioRecordJNI.this.running);
          try
          {
            AudioRecordJNI.this.audioRecord.read(AudioRecordJNI.this.buffer, 1920);
            if (!AudioRecordJNI.this.running)
            {
              AudioRecordJNI.this.audioRecord.stop();
              Log.i("tg-voip", "audiotrack thread exits");
              return;
            }
            AudioRecordJNI.this.nativeCallback(AudioRecordJNI.this.buffer);
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      }
    });
    this.thread.start();
  }

  public void init(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.audioRecord != null)
      throw new IllegalStateException("already inited");
    int i = getBufferSize(paramInt4);
    this.bufferSize = paramInt4;
    if (paramInt3 == 1)
      paramInt2 = 16;
    try
    {
      while (true)
      {
        this.audioRecord = new AudioRecord(7, paramInt1, paramInt2, 2, i);
        this.buffer = ByteBuffer.allocateDirect(paramInt4);
        return;
        paramInt2 = 12;
      }
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e("AudioRecord init failed!", localException);
    }
  }

  public void release()
  {
    this.running = false;
    if (this.thread != null);
    try
    {
      this.thread.join();
      this.thread = null;
      if (this.audioRecord != null)
      {
        this.audioRecord.release();
        this.audioRecord = null;
      }
      if (this.agc != null)
      {
        this.agc.release();
        this.agc = null;
      }
      if (this.ns != null)
      {
        this.ns.release();
        this.ns = null;
      }
      if (this.aec != null)
      {
        this.aec.release();
        this.aec = null;
      }
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      while (true)
        FileLog.e(localInterruptedException);
    }
  }

  // ERROR //
  public boolean start()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 68	org/vidogram/messenger/voip/AudioRecordJNI:thread	Ljava/lang/Thread;
    //   4: ifnonnull +216 -> 220
    //   7: aload_0
    //   8: getfield 44	org/vidogram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   11: ifnonnull +5 -> 16
    //   14: iconst_0
    //   15: ireturn
    //   16: aload_0
    //   17: getfield 44	org/vidogram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   20: invokevirtual 145	android/media/AudioRecord:startRecording	()V
    //   23: getstatic 150	android/os/Build$VERSION:SDK_INT	I
    //   26: istore_1
    //   27: iload_1
    //   28: bipush 16
    //   30: if_icmplt +121 -> 151
    //   33: invokestatic 153	android/media/audiofx/AutomaticGainControl:isAvailable	()Z
    //   36: ifeq +121 -> 157
    //   39: aload_0
    //   40: aload_0
    //   41: getfield 44	org/vidogram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   44: invokevirtual 157	android/media/AudioRecord:getAudioSessionId	()I
    //   47: invokestatic 161	android/media/audiofx/AutomaticGainControl:create	(I)Landroid/media/audiofx/AutomaticGainControl;
    //   50: putfield 123	org/vidogram/messenger/voip/AudioRecordJNI:agc	Landroid/media/audiofx/AutomaticGainControl;
    //   53: aload_0
    //   54: getfield 123	org/vidogram/messenger/voip/AudioRecordJNI:agc	Landroid/media/audiofx/AutomaticGainControl;
    //   57: ifnull +12 -> 69
    //   60: aload_0
    //   61: getfield 123	org/vidogram/messenger/voip/AudioRecordJNI:agc	Landroid/media/audiofx/AutomaticGainControl;
    //   64: iconst_0
    //   65: invokevirtual 165	android/media/audiofx/AutomaticGainControl:setEnabled	(Z)I
    //   68: pop
    //   69: invokestatic 166	android/media/audiofx/NoiseSuppressor:isAvailable	()Z
    //   72: ifeq +112 -> 184
    //   75: aload_0
    //   76: aload_0
    //   77: getfield 44	org/vidogram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   80: invokevirtual 157	android/media/AudioRecord:getAudioSessionId	()I
    //   83: invokestatic 169	android/media/audiofx/NoiseSuppressor:create	(I)Landroid/media/audiofx/NoiseSuppressor;
    //   86: putfield 128	org/vidogram/messenger/voip/AudioRecordJNI:ns	Landroid/media/audiofx/NoiseSuppressor;
    //   89: aload_0
    //   90: getfield 128	org/vidogram/messenger/voip/AudioRecordJNI:ns	Landroid/media/audiofx/NoiseSuppressor;
    //   93: ifnull +17 -> 110
    //   96: aload_0
    //   97: getfield 128	org/vidogram/messenger/voip/AudioRecordJNI:ns	Landroid/media/audiofx/NoiseSuppressor;
    //   100: ldc 171
    //   102: iconst_1
    //   103: invokestatic 177	org/vidogram/messenger/voip/VoIPServerConfig:getBoolean	(Ljava/lang/String;Z)Z
    //   106: invokevirtual 178	android/media/audiofx/NoiseSuppressor:setEnabled	(Z)I
    //   109: pop
    //   110: invokestatic 179	android/media/audiofx/AcousticEchoCanceler:isAvailable	()Z
    //   113: ifeq +89 -> 202
    //   116: aload_0
    //   117: aload_0
    //   118: getfield 44	org/vidogram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   121: invokevirtual 157	android/media/AudioRecord:getAudioSessionId	()I
    //   124: invokestatic 182	android/media/audiofx/AcousticEchoCanceler:create	(I)Landroid/media/audiofx/AcousticEchoCanceler;
    //   127: putfield 133	org/vidogram/messenger/voip/AudioRecordJNI:aec	Landroid/media/audiofx/AcousticEchoCanceler;
    //   130: aload_0
    //   131: getfield 133	org/vidogram/messenger/voip/AudioRecordJNI:aec	Landroid/media/audiofx/AcousticEchoCanceler;
    //   134: ifnull +17 -> 151
    //   137: aload_0
    //   138: getfield 133	org/vidogram/messenger/voip/AudioRecordJNI:aec	Landroid/media/audiofx/AcousticEchoCanceler;
    //   141: ldc 184
    //   143: iconst_1
    //   144: invokestatic 177	org/vidogram/messenger/voip/VoIPServerConfig:getBoolean	(Ljava/lang/String;Z)Z
    //   147: invokevirtual 185	android/media/audiofx/AcousticEchoCanceler:setEnabled	(Z)I
    //   150: pop
    //   151: aload_0
    //   152: invokespecial 187	org/vidogram/messenger/voip/AudioRecordJNI:startThread	()V
    //   155: iconst_1
    //   156: ireturn
    //   157: ldc 189
    //   159: invokestatic 192	org/vidogram/messenger/FileLog:w	(Ljava/lang/String;)V
    //   162: goto -93 -> 69
    //   165: astore_2
    //   166: ldc 194
    //   168: aload_2
    //   169: invokestatic 113	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   172: goto -103 -> 69
    //   175: astore_2
    //   176: ldc 196
    //   178: aload_2
    //   179: invokestatic 113	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   182: iconst_0
    //   183: ireturn
    //   184: ldc 198
    //   186: invokestatic 192	org/vidogram/messenger/FileLog:w	(Ljava/lang/String;)V
    //   189: goto -79 -> 110
    //   192: astore_2
    //   193: ldc 200
    //   195: aload_2
    //   196: invokestatic 113	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   199: goto -89 -> 110
    //   202: ldc 202
    //   204: invokestatic 192	org/vidogram/messenger/FileLog:w	(Ljava/lang/String;)V
    //   207: goto -56 -> 151
    //   210: astore_2
    //   211: ldc 204
    //   213: aload_2
    //   214: invokestatic 113	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   217: goto -66 -> 151
    //   220: aload_0
    //   221: getfield 44	org/vidogram/messenger/voip/AudioRecordJNI:audioRecord	Landroid/media/AudioRecord;
    //   224: invokevirtual 145	android/media/AudioRecord:startRecording	()V
    //   227: goto -72 -> 155
    //
    // Exception table:
    //   from	to	target	type
    //   33	69	165	java/lang/Throwable
    //   157	162	165	java/lang/Throwable
    //   0	14	175	java/lang/Exception
    //   16	27	175	java/lang/Exception
    //   33	69	175	java/lang/Exception
    //   69	110	175	java/lang/Exception
    //   110	151	175	java/lang/Exception
    //   151	155	175	java/lang/Exception
    //   157	162	175	java/lang/Exception
    //   166	172	175	java/lang/Exception
    //   184	189	175	java/lang/Exception
    //   193	199	175	java/lang/Exception
    //   202	207	175	java/lang/Exception
    //   211	217	175	java/lang/Exception
    //   220	227	175	java/lang/Exception
    //   69	110	192	java/lang/Throwable
    //   184	189	192	java/lang/Throwable
    //   110	151	210	java/lang/Throwable
    //   202	207	210	java/lang/Throwable
  }

  public void stop()
  {
    if (this.audioRecord != null)
      this.audioRecord.stop();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.voip.AudioRecordJNI
 * JD-Core Version:    0.6.0
 */