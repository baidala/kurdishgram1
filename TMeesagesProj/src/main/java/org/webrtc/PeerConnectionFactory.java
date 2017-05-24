package org.webrtc;

import java.util.List;

public class PeerConnectionFactory
{
  private static final String TAG = "PeerConnectionFactory";
  private static Thread networkThread;
  private static Thread signalingThread;
  private static Thread workerThread;
  private EglBase localEglbase;
  private final long nativeFactory;
  private EglBase remoteEglbase;

  static
  {
    System.loadLibrary("jingle_peerconnection_so");
  }

  @Deprecated
  public PeerConnectionFactory()
  {
    this(null);
  }

  public PeerConnectionFactory(Options paramOptions)
  {
    this.nativeFactory = nativeCreatePeerConnectionFactory(paramOptions);
    if (this.nativeFactory == 0L)
      throw new RuntimeException("Failed to initialize PeerConnectionFactory!");
  }

  public static native boolean initializeAndroidGlobals(Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);

  public static native void initializeFieldTrials(String paramString);

  public static native void initializeInternalTracer();

  private static native long nativeCreateAudioSource(long paramLong, MediaConstraints paramMediaConstraints);

  private static native long nativeCreateAudioTrack(long paramLong1, String paramString, long paramLong2);

  private static native long nativeCreateLocalMediaStream(long paramLong, String paramString);

  private static native long nativeCreateObserver(PeerConnection.Observer paramObserver);

  private static native long nativeCreatePeerConnection(long paramLong1, PeerConnection.RTCConfiguration paramRTCConfiguration, MediaConstraints paramMediaConstraints, long paramLong2);

  private static native long nativeCreatePeerConnectionFactory(Options paramOptions);

  private static native long nativeCreateVideoSource(long paramLong, EglBase.Context paramContext, VideoCapturer paramVideoCapturer, MediaConstraints paramMediaConstraints);

  private static native long nativeCreateVideoSource2(long paramLong, EglBase.Context paramContext);

  private static native long nativeCreateVideoTrack(long paramLong1, String paramString, long paramLong2);

  private static native void nativeFreeFactory(long paramLong);

  private static native void nativeInitializeVideoCapturer(long paramLong1, VideoCapturer paramVideoCapturer, long paramLong2, VideoCapturer.CapturerObserver paramCapturerObserver);

  private static native void nativeSetVideoHwAccelerationOptions(long paramLong, Object paramObject1, Object paramObject2);

  private static native boolean nativeStartAecDump(long paramLong, int paramInt1, int paramInt2);

  private static native void nativeStopAecDump(long paramLong);

  private static native void nativeThreadsCallbacks(long paramLong);

  private static void onNetworkThreadReady()
  {
    networkThread = Thread.currentThread();
    Logging.d("PeerConnectionFactory", "onNetworkThreadReady");
  }

  private static void onSignalingThreadReady()
  {
    signalingThread = Thread.currentThread();
    Logging.d("PeerConnectionFactory", "onSignalingThreadReady");
  }

  private static void onWorkerThreadReady()
  {
    workerThread = Thread.currentThread();
    Logging.d("PeerConnectionFactory", "onWorkerThreadReady");
  }

  private static void printStackTrace(Thread paramThread, String paramString)
  {
    if (paramThread != null)
    {
      paramThread = paramThread.getStackTrace();
      if (paramThread.length > 0)
      {
        Logging.d("PeerConnectionFactory", paramString + " stacks trace:");
        int j = paramThread.length;
        int i = 0;
        while (i < j)
        {
          Logging.d("PeerConnectionFactory", paramThread[i].toString());
          i += 1;
        }
      }
    }
  }

  public static void printStackTraces()
  {
    printStackTrace(networkThread, "Network thread");
    printStackTrace(workerThread, "Worker thread");
    printStackTrace(signalingThread, "Signaling thread");
  }

  public static native void shutdownInternalTracer();

  public static native boolean startInternalTracingCapture(String paramString);

  public static native void stopInternalTracingCapture();

  public AudioSource createAudioSource(MediaConstraints paramMediaConstraints)
  {
    return new AudioSource(nativeCreateAudioSource(this.nativeFactory, paramMediaConstraints));
  }

  public AudioTrack createAudioTrack(String paramString, AudioSource paramAudioSource)
  {
    return new AudioTrack(nativeCreateAudioTrack(this.nativeFactory, paramString, paramAudioSource.nativeSource));
  }

  public MediaStream createLocalMediaStream(String paramString)
  {
    return new MediaStream(nativeCreateLocalMediaStream(this.nativeFactory, paramString));
  }

  public PeerConnection createPeerConnection(List<PeerConnection.IceServer> paramList, MediaConstraints paramMediaConstraints, PeerConnection.Observer paramObserver)
  {
    return createPeerConnection(new PeerConnection.RTCConfiguration(paramList), paramMediaConstraints, paramObserver);
  }

  public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration paramRTCConfiguration, MediaConstraints paramMediaConstraints, PeerConnection.Observer paramObserver)
  {
    long l1 = nativeCreateObserver(paramObserver);
    if (l1 == 0L)
      return null;
    long l2 = nativeCreatePeerConnection(this.nativeFactory, paramRTCConfiguration, paramMediaConstraints, l1);
    if (l2 == 0L)
      return null;
    return new PeerConnection(l2, l1);
  }

  public VideoSource createVideoSource(VideoCapturer paramVideoCapturer)
  {
    if (this.localEglbase == null);
    for (Object localObject = null; ; localObject = this.localEglbase.getEglBaseContext())
    {
      long l = nativeCreateVideoSource2(this.nativeFactory, (EglBase.Context)localObject);
      localObject = new VideoCapturer.AndroidVideoTrackSourceObserver(l);
      nativeInitializeVideoCapturer(this.nativeFactory, paramVideoCapturer, l, (VideoCapturer.CapturerObserver)localObject);
      return new VideoSource(l);
    }
  }

  public VideoSource createVideoSource(VideoCapturer paramVideoCapturer, MediaConstraints paramMediaConstraints)
  {
    if (this.localEglbase == null);
    for (EglBase.Context localContext = null; ; localContext = this.localEglbase.getEglBaseContext())
      return new VideoSource(nativeCreateVideoSource(this.nativeFactory, localContext, paramVideoCapturer, paramMediaConstraints));
  }

  public VideoTrack createVideoTrack(String paramString, VideoSource paramVideoSource)
  {
    return new VideoTrack(nativeCreateVideoTrack(this.nativeFactory, paramString, paramVideoSource.nativeSource));
  }

  public void dispose()
  {
    nativeFreeFactory(this.nativeFactory);
    networkThread = null;
    workerThread = null;
    signalingThread = null;
    if (this.localEglbase != null)
      this.localEglbase.release();
    if (this.remoteEglbase != null)
      this.remoteEglbase.release();
  }

  @Deprecated
  public native void nativeSetOptions(long paramLong, Options paramOptions);

  @Deprecated
  public void setOptions(Options paramOptions)
  {
    nativeSetOptions(this.nativeFactory, paramOptions);
  }

  public void setVideoHwAccelerationOptions(EglBase.Context paramContext1, EglBase.Context paramContext2)
  {
    if (this.localEglbase != null)
    {
      Logging.w("PeerConnectionFactory", "Egl context already set.");
      this.localEglbase.release();
    }
    if (this.remoteEglbase != null)
    {
      Logging.w("PeerConnectionFactory", "Egl context already set.");
      this.remoteEglbase.release();
    }
    this.localEglbase = EglBase.create(paramContext1);
    this.remoteEglbase = EglBase.create(paramContext2);
    nativeSetVideoHwAccelerationOptions(this.nativeFactory, this.localEglbase.getEglBaseContext(), this.remoteEglbase.getEglBaseContext());
  }

  public boolean startAecDump(int paramInt1, int paramInt2)
  {
    return nativeStartAecDump(this.nativeFactory, paramInt1, paramInt2);
  }

  public void stopAecDump()
  {
    nativeStopAecDump(this.nativeFactory);
  }

  public void threadsCallbacks()
  {
    nativeThreadsCallbacks(this.nativeFactory);
  }

  public static class Options
  {
    static final int ADAPTER_TYPE_CELLULAR = 4;
    static final int ADAPTER_TYPE_ETHERNET = 1;
    static final int ADAPTER_TYPE_LOOPBACK = 16;
    static final int ADAPTER_TYPE_UNKNOWN = 0;
    static final int ADAPTER_TYPE_VPN = 8;
    static final int ADAPTER_TYPE_WIFI = 2;
    public boolean disableEncryption;
    public boolean disableNetworkMonitor;
    public int networkIgnoreMask;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.PeerConnectionFactory
 * JD-Core Version:    0.6.0
 */