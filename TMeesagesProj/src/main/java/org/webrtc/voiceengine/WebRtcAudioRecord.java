package org.webrtc.voiceengine;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Process;
import java.nio.ByteBuffer;
import org.webrtc.Logging;
import org.webrtc.ThreadUtils;

public class WebRtcAudioRecord
{
  private static final long AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS = 2000L;
  private static final int BITS_PER_SAMPLE = 16;
  private static final int BUFFERS_PER_SECOND = 100;
  private static final int BUFFER_SIZE_FACTOR = 2;
  private static final int CALLBACK_BUFFER_SIZE_MS = 10;
  private static final boolean DEBUG = false;
  private static final String TAG = "WebRtcAudioRecord";
  private static volatile boolean microphoneMute = false;
  private AudioRecord audioRecord = null;
  private AudioRecordThread audioThread = null;
  private ByteBuffer byteBuffer;
  private final Context context;
  private WebRtcAudioEffects effects = null;
  private byte[] emptyBytes;
  private final long nativeAudioRecord;

  WebRtcAudioRecord(Context paramContext, long paramLong)
  {
    Logging.d("WebRtcAudioRecord", "ctor" + WebRtcAudioUtils.getThreadInfo());
    this.context = paramContext;
    this.nativeAudioRecord = paramLong;
    this.effects = WebRtcAudioEffects.create();
  }

  private static void assertTrue(boolean paramBoolean)
  {
    if (!paramBoolean)
      throw new AssertionError("Expected condition to be true");
  }

  private boolean enableBuiltInAEC(boolean paramBoolean)
  {
    Logging.d("WebRtcAudioRecord", "enableBuiltInAEC(" + paramBoolean + ')');
    if (this.effects == null)
    {
      Logging.e("WebRtcAudioRecord", "Built-in AEC is not supported on this platform");
      return false;
    }
    return this.effects.setAEC(paramBoolean);
  }

  private boolean enableBuiltInAGC(boolean paramBoolean)
  {
    Logging.d("WebRtcAudioRecord", "enableBuiltInAGC(" + paramBoolean + ')');
    if (this.effects == null)
    {
      Logging.e("WebRtcAudioRecord", "Built-in AGC is not supported on this platform");
      return false;
    }
    return this.effects.setAGC(paramBoolean);
  }

  private boolean enableBuiltInNS(boolean paramBoolean)
  {
    Logging.d("WebRtcAudioRecord", "enableBuiltInNS(" + paramBoolean + ')');
    if (this.effects == null)
    {
      Logging.e("WebRtcAudioRecord", "Built-in NS is not supported on this platform");
      return false;
    }
    return this.effects.setNS(paramBoolean);
  }

  private int initRecording(int paramInt1, int paramInt2)
  {
    Logging.d("WebRtcAudioRecord", "initRecording(sampleRate=" + paramInt1 + ", channels=" + paramInt2 + ")");
    if (!WebRtcAudioUtils.hasPermission(this.context, "android.permission.RECORD_AUDIO"))
    {
      Logging.e("WebRtcAudioRecord", "RECORD_AUDIO permission is missing");
      return -1;
    }
    if (this.audioRecord != null)
    {
      Logging.e("WebRtcAudioRecord", "InitRecording() called twice without StopRecording()");
      return -1;
    }
    int i = paramInt1 / 100;
    this.byteBuffer = ByteBuffer.allocateDirect(paramInt2 * 2 * i);
    Logging.d("WebRtcAudioRecord", "byteBuffer.capacity: " + this.byteBuffer.capacity());
    this.emptyBytes = new byte[this.byteBuffer.capacity()];
    nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioRecord);
    paramInt2 = AudioRecord.getMinBufferSize(paramInt1, 16, 2);
    if ((paramInt2 == -1) || (paramInt2 == -2))
    {
      Logging.e("WebRtcAudioRecord", "AudioRecord.getMinBufferSize failed: " + paramInt2);
      return -1;
    }
    Logging.d("WebRtcAudioRecord", "AudioRecord.getMinBufferSize: " + paramInt2);
    paramInt2 = Math.max(paramInt2 * 2, this.byteBuffer.capacity());
    Logging.d("WebRtcAudioRecord", "bufferSizeInBytes: " + paramInt2);
    try
    {
      this.audioRecord = new AudioRecord(7, paramInt1, 16, 2, paramInt2);
      if ((this.audioRecord == null) || (this.audioRecord.getState() != 1))
      {
        Logging.e("WebRtcAudioRecord", "Failed to create a new AudioRecord instance");
        return -1;
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Logging.e("WebRtcAudioRecord", localIllegalArgumentException.getMessage());
      return -1;
    }
    Logging.d("WebRtcAudioRecord", "AudioRecord session ID: " + this.audioRecord.getAudioSessionId() + ", " + "audio format: " + this.audioRecord.getAudioFormat() + ", " + "channels: " + this.audioRecord.getChannelCount() + ", " + "sample rate: " + this.audioRecord.getSampleRate());
    if (this.effects != null)
      this.effects.enable(this.audioRecord.getAudioSessionId());
    return i;
  }

  private native void nativeCacheDirectBufferAddress(ByteBuffer paramByteBuffer, long paramLong);

  private native void nativeDataIsRecorded(int paramInt, long paramLong);

  public static void setMicrophoneMute(boolean paramBoolean)
  {
    Logging.w("WebRtcAudioRecord", "setMicrophoneMute(" + paramBoolean + ")");
    microphoneMute = paramBoolean;
  }

  private boolean startRecording()
  {
    Logging.d("WebRtcAudioRecord", "startRecording");
    boolean bool;
    if (this.audioRecord != null)
      bool = true;
    while (true)
    {
      assertTrue(bool);
      if (this.audioThread == null)
      {
        bool = true;
        assertTrue(bool);
      }
      try
      {
        this.audioRecord.startRecording();
        if (this.audioRecord.getRecordingState() == 3)
          break;
        Logging.e("WebRtcAudioRecord", "AudioRecord.startRecording failed");
        return false;
        bool = false;
        continue;
        bool = false;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        Logging.e("WebRtcAudioRecord", "AudioRecord.startRecording failed: " + localIllegalStateException.getMessage());
        return false;
      }
    }
    this.audioThread = new AudioRecordThread("AudioRecordJavaThread");
    this.audioThread.start();
    return true;
  }

  private boolean stopRecording()
  {
    Logging.d("WebRtcAudioRecord", "stopRecording");
    if (this.audioThread != null);
    for (boolean bool = true; ; bool = false)
    {
      assertTrue(bool);
      this.audioThread.stopThread();
      if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000L))
        Logging.e("WebRtcAudioRecord", "Join of AudioRecordJavaThread timed out");
      this.audioThread = null;
      if (this.effects != null)
        this.effects.release();
      this.audioRecord.release();
      this.audioRecord = null;
      return true;
    }
  }

  private class AudioRecordThread extends Thread
  {
    private volatile boolean keepAlive = true;

    public AudioRecordThread(String arg2)
    {
      super();
    }

    public void run()
    {
      Process.setThreadPriority(-19);
      Logging.d("WebRtcAudioRecord", "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());
      boolean bool;
      if (WebRtcAudioRecord.this.audioRecord.getRecordingState() == 3)
      {
        bool = true;
        WebRtcAudioRecord.access$100(bool);
        System.nanoTime();
      }
      while (true)
      {
        if (!this.keepAlive)
          break label200;
        int i = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
        if (i == WebRtcAudioRecord.this.byteBuffer.capacity())
        {
          if (WebRtcAudioRecord.microphoneMute)
          {
            WebRtcAudioRecord.this.byteBuffer.clear();
            WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
          }
          WebRtcAudioRecord.this.nativeDataIsRecorded(i, WebRtcAudioRecord.this.nativeAudioRecord);
          continue;
          bool = false;
          break;
        }
        Logging.e("WebRtcAudioRecord", "AudioRecord.read failed: " + i);
        if (i != -3)
          continue;
        this.keepAlive = false;
      }
      try
      {
        label200: WebRtcAudioRecord.this.audioRecord.stop();
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        Logging.e("WebRtcAudioRecord", "AudioRecord.stop failed: " + localIllegalStateException.getMessage());
      }
    }

    public void stopThread()
    {
      Logging.d("WebRtcAudioRecord", "stopThread");
      this.keepAlive = false;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.voiceengine.WebRtcAudioRecord
 * JD-Core Version:    0.6.0
 */