package org.vidogram.messenger.voip;

import android.media.AudioTrack;
import org.vidogram.messenger.FileLog;

public class AudioTrackJNI
{
  private AudioTrack audioTrack;
  private byte[] buffer = new byte[1920];
  private int bufferSize;
  private long nativeInst;
  private boolean running;
  private Thread thread;

  public AudioTrackJNI(long paramLong)
  {
    this.nativeInst = paramLong;
  }

  private int getBufferSize(int paramInt)
  {
    return Math.max(AudioTrack.getMinBufferSize(48000, 4, 2), paramInt);
  }

  private native void nativeCallback(byte[] paramArrayOfByte);

  private void startThread()
  {
    if (this.thread != null)
      throw new IllegalStateException("thread already started");
    this.running = true;
    this.thread = new Thread(new Runnable()
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   4: invokestatic 27	org/vidogram/messenger/voip/AudioTrackJNI:access$000	(Lorg/vidogram/messenger/voip/AudioTrackJNI;)Landroid/media/AudioTrack;
        //   7: invokevirtual 32	android/media/AudioTrack:play	()V
        //   10: aload_0
        //   11: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   14: invokestatic 36	org/vidogram/messenger/voip/AudioTrackJNI:access$100	(Lorg/vidogram/messenger/voip/AudioTrackJNI;)Z
        //   17: ifeq +59 -> 76
        //   20: aload_0
        //   21: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   24: aload_0
        //   25: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   28: invokestatic 40	org/vidogram/messenger/voip/AudioTrackJNI:access$200	(Lorg/vidogram/messenger/voip/AudioTrackJNI;)[B
        //   31: invokestatic 44	org/vidogram/messenger/voip/AudioTrackJNI:access$300	(Lorg/vidogram/messenger/voip/AudioTrackJNI;[B)V
        //   34: aload_0
        //   35: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   38: invokestatic 27	org/vidogram/messenger/voip/AudioTrackJNI:access$000	(Lorg/vidogram/messenger/voip/AudioTrackJNI;)Landroid/media/AudioTrack;
        //   41: aload_0
        //   42: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   45: invokestatic 40	org/vidogram/messenger/voip/AudioTrackJNI:access$200	(Lorg/vidogram/messenger/voip/AudioTrackJNI;)[B
        //   48: iconst_0
        //   49: sipush 1920
        //   52: invokevirtual 48	android/media/AudioTrack:write	([BII)I
        //   55: pop
        //   56: aload_0
        //   57: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   60: invokestatic 36	org/vidogram/messenger/voip/AudioTrackJNI:access$100	(Lorg/vidogram/messenger/voip/AudioTrackJNI;)Z
        //   63: ifne -53 -> 10
        //   66: aload_0
        //   67: getfield 17	org/vidogram/messenger/voip/AudioTrackJNI$1:this$0	Lorg/vidogram/messenger/voip/AudioTrackJNI;
        //   70: invokestatic 27	org/vidogram/messenger/voip/AudioTrackJNI:access$000	(Lorg/vidogram/messenger/voip/AudioTrackJNI;)Landroid/media/AudioTrack;
        //   73: invokevirtual 51	android/media/AudioTrack:stop	()V
        //   76: ldc 53
        //   78: ldc 55
        //   80: invokestatic 61	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
        //   83: pop
        //   84: return
        //   85: astore_1
        //   86: ldc 63
        //   88: aload_1
        //   89: invokestatic 69	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   92: return
        //   93: astore_1
        //   94: aload_1
        //   95: invokestatic 72	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   98: goto -88 -> 10
        //
        // Exception table:
        //   from	to	target	type
        //   0	10	85	java/lang/Exception
        //   20	76	93	java/lang/Exception
      }
    });
    this.thread.start();
  }

  public void init(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.audioTrack != null)
      throw new IllegalStateException("already inited");
    int i = getBufferSize(paramInt4);
    this.bufferSize = paramInt4;
    if (paramInt3 == 1);
    for (paramInt2 = 4; ; paramInt2 = 12)
    {
      this.audioTrack = new AudioTrack(0, paramInt1, paramInt2, 2, i, 1);
      return;
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
      if (this.audioTrack != null)
      {
        this.audioTrack.release();
        this.audioTrack = null;
      }
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      while (true)
        FileLog.e(localInterruptedException);
    }
  }

  public void start()
  {
    if (this.thread == null)
    {
      startThread();
      return;
    }
    this.audioTrack.play();
  }

  public void stop()
  {
    if (this.audioTrack != null)
      this.audioTrack.stop();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.voip.AudioTrackJNI
 * JD-Core Version:    0.6.0
 */