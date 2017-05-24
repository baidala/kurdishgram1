package org.webrtc.voiceengine;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import java.nio.ByteBuffer;
import org.webrtc.Logging;

public class WebRtcAudioTrack
{
  private static final int BITS_PER_SAMPLE = 16;
  private static final int BUFFERS_PER_SECOND = 100;
  private static final int CALLBACK_BUFFER_SIZE_MS = 10;
  private static final boolean DEBUG = false;
  private static final String TAG = "WebRtcAudioTrack";
  private static volatile boolean speakerMute = false;
  private final AudioManager audioManager;
  private AudioTrackThread audioThread = null;
  private AudioTrack audioTrack = null;
  private ByteBuffer byteBuffer;
  private final Context context;
  private byte[] emptyBytes;
  private final long nativeAudioTrack;

  WebRtcAudioTrack(Context paramContext, long paramLong)
  {
    Logging.d("WebRtcAudioTrack", "ctor" + WebRtcAudioUtils.getThreadInfo());
    this.context = paramContext;
    this.nativeAudioTrack = paramLong;
    this.audioManager = ((AudioManager)paramContext.getSystemService("audio"));
  }

  private static void assertTrue(boolean paramBoolean)
  {
    if (!paramBoolean)
      throw new AssertionError("Expected condition to be true");
  }

  private int getStreamMaxVolume()
  {
    Logging.d("WebRtcAudioTrack", "getStreamMaxVolume");
    if (this.audioManager != null);
    for (boolean bool = true; ; bool = false)
    {
      assertTrue(bool);
      return this.audioManager.getStreamMaxVolume(0);
    }
  }

  private int getStreamVolume()
  {
    Logging.d("WebRtcAudioTrack", "getStreamVolume");
    if (this.audioManager != null);
    for (boolean bool = true; ; bool = false)
    {
      assertTrue(bool);
      return this.audioManager.getStreamVolume(0);
    }
  }

  private boolean initPlayout(int paramInt1, int paramInt2)
  {
    Logging.d("WebRtcAudioTrack", "initPlayout(sampleRate=" + paramInt1 + ", channels=" + paramInt2 + ")");
    ByteBuffer localByteBuffer = this.byteBuffer;
    this.byteBuffer = ByteBuffer.allocateDirect(paramInt2 * 2 * (paramInt1 / 100));
    Logging.d("WebRtcAudioTrack", "byteBuffer.capacity: " + this.byteBuffer.capacity());
    this.emptyBytes = new byte[this.byteBuffer.capacity()];
    nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioTrack);
    paramInt2 = AudioTrack.getMinBufferSize(paramInt1, 4, 2);
    Logging.d("WebRtcAudioTrack", "AudioTrack.getMinBufferSize: " + paramInt2);
    boolean bool;
    if (this.audioTrack == null)
      bool = true;
    while (true)
    {
      assertTrue(bool);
      if (this.byteBuffer.capacity() < paramInt2)
      {
        bool = true;
        assertTrue(bool);
      }
      try
      {
        this.audioTrack = new AudioTrack(0, paramInt1, 4, 2, paramInt2, 1);
        if (this.audioTrack.getState() == 1)
          break;
        Logging.e("WebRtcAudioTrack", "Initialization of audio track failed.");
        return false;
        bool = false;
        continue;
        bool = false;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        Logging.d("WebRtcAudioTrack", localIllegalArgumentException.getMessage());
        return false;
      }
    }
    return true;
  }

  @TargetApi(21)
  private boolean isVolumeFixed()
  {
    if (!WebRtcAudioUtils.runningOnLollipopOrHigher())
      return false;
    return this.audioManager.isVolumeFixed();
  }

  private native void nativeCacheDirectBufferAddress(ByteBuffer paramByteBuffer, long paramLong);

  private native void nativeGetPlayoutData(int paramInt, long paramLong);

  public static void setSpeakerMute(boolean paramBoolean)
  {
    Logging.w("WebRtcAudioTrack", "setSpeakerMute(" + paramBoolean + ")");
    speakerMute = paramBoolean;
  }

  private boolean setStreamVolume(int paramInt)
  {
    Logging.d("WebRtcAudioTrack", "setStreamVolume(" + paramInt + ")");
    if (this.audioManager != null);
    for (boolean bool = true; ; bool = false)
    {
      assertTrue(bool);
      if (!isVolumeFixed())
        break;
      Logging.e("WebRtcAudioTrack", "The device implements a fixed volume policy.");
      return false;
    }
    this.audioManager.setStreamVolume(0, paramInt, 0);
    return true;
  }

  private boolean startPlayout()
  {
    Logging.d("WebRtcAudioTrack", "startPlayout");
    if (this.audioTrack != null)
    {
      bool = true;
      assertTrue(bool);
      if (this.audioThread != null)
        break label58;
    }
    label58: for (boolean bool = true; ; bool = false)
    {
      assertTrue(bool);
      if (this.audioTrack.getState() == 1)
        break label63;
      Logging.e("WebRtcAudioTrack", "Audio track is not successfully initialized.");
      return false;
      bool = false;
      break;
    }
    label63: this.audioThread = new AudioTrackThread("AudioTrackJavaThread");
    this.audioThread.start();
    return true;
  }

  private boolean stopPlayout()
  {
    Logging.d("WebRtcAudioTrack", "stopPlayout");
    if (this.audioThread != null);
    for (boolean bool = true; ; bool = false)
    {
      assertTrue(bool);
      this.audioThread.joinThread();
      this.audioThread = null;
      if (this.audioTrack != null)
      {
        this.audioTrack.release();
        this.audioTrack = null;
      }
      return true;
    }
  }

  private class AudioTrackThread extends Thread
  {
    private volatile boolean keepAlive = true;

    public AudioTrackThread(String arg2)
    {
      super();
    }

    @TargetApi(21)
    private int writeOnLollipop(AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt)
    {
      return paramAudioTrack.write(paramByteBuffer, paramInt, 0);
    }

    private int writePreLollipop(AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt)
    {
      return paramAudioTrack.write(paramByteBuffer.array(), paramByteBuffer.arrayOffset(), paramInt);
    }

    public void joinThread()
    {
      this.keepAlive = false;
      while (isAlive())
        try
        {
          join();
        }
        catch (InterruptedException localInterruptedException)
        {
        }
    }

    public void run()
    {
      boolean bool2 = true;
      Process.setThreadPriority(-19);
      Logging.d("WebRtcAudioTrack", "AudioTrackThread" + WebRtcAudioUtils.getThreadInfo());
      boolean bool1;
      while (true)
      {
        int j;
        try
        {
          WebRtcAudioTrack.this.audioTrack.play();
          if (WebRtcAudioTrack.this.audioTrack.getPlayState() != 3)
            continue;
          bool1 = true;
          WebRtcAudioTrack.access$100(bool1);
          j = WebRtcAudioTrack.this.byteBuffer.capacity();
          if (!this.keepAlive)
            break;
          WebRtcAudioTrack.this.nativeGetPlayoutData(j, WebRtcAudioTrack.this.nativeAudioTrack);
          if (j <= WebRtcAudioTrack.this.byteBuffer.remaining())
          {
            bool1 = true;
            WebRtcAudioTrack.access$100(bool1);
            if (!WebRtcAudioTrack.speakerMute)
              continue;
            WebRtcAudioTrack.this.byteBuffer.clear();
            WebRtcAudioTrack.this.byteBuffer.put(WebRtcAudioTrack.this.emptyBytes);
            WebRtcAudioTrack.this.byteBuffer.position(0);
            if (!WebRtcAudioUtils.runningOnLollipopOrHigher())
              break label285;
            i = writeOnLollipop(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, j);
            if (i == j)
              continue;
            Logging.e("WebRtcAudioTrack", "AudioTrack.write failed: " + i);
            if (i != -3)
              continue;
            this.keepAlive = false;
            WebRtcAudioTrack.this.byteBuffer.rewind();
            continue;
            bool1 = false;
            continue;
          }
        }
        catch (IllegalStateException localIllegalStateException1)
        {
          Logging.e("WebRtcAudioTrack", "AudioTrack.play failed: " + localIllegalStateException1.getMessage());
          return;
        }
        bool1 = false;
        continue;
        label285: int i = writePreLollipop(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, j);
      }
      try
      {
        WebRtcAudioTrack.this.audioTrack.stop();
        if (WebRtcAudioTrack.this.audioTrack.getPlayState() == 1)
        {
          bool1 = bool2;
          WebRtcAudioTrack.access$100(bool1);
          WebRtcAudioTrack.this.audioTrack.flush();
          return;
        }
      }
      catch (IllegalStateException localIllegalStateException2)
      {
        while (true)
        {
          Logging.e("WebRtcAudioTrack", "AudioTrack.stop failed: " + localIllegalStateException2.getMessage());
          continue;
          bool1 = false;
        }
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.voiceengine.WebRtcAudioTrack
 * JD-Core Version:    0.6.0
 */