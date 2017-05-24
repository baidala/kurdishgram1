package org.webrtc;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PeerConnection
{
  private final List<MediaStream> localStreams;
  private final long nativeObserver;
  private final long nativePeerConnection;
  private List<RtpReceiver> receivers;
  private List<RtpSender> senders;

  static
  {
    System.loadLibrary("jingle_peerconnection_so");
  }

  PeerConnection(long paramLong1, long paramLong2)
  {
    this.nativePeerConnection = paramLong1;
    this.nativeObserver = paramLong2;
    this.localStreams = new LinkedList();
    this.senders = new LinkedList();
    this.receivers = new LinkedList();
  }

  private static native void freeObserver(long paramLong);

  private static native void freePeerConnection(long paramLong);

  private native boolean nativeAddIceCandidate(String paramString1, int paramInt, String paramString2);

  private native boolean nativeAddLocalStream(long paramLong);

  private native RtpSender nativeCreateSender(String paramString1, String paramString2);

  private native List<RtpReceiver> nativeGetReceivers();

  private native List<RtpSender> nativeGetSenders();

  private native boolean nativeGetStats(StatsObserver paramStatsObserver, long paramLong);

  private native boolean nativeRemoveIceCandidates(IceCandidate[] paramArrayOfIceCandidate);

  private native void nativeRemoveLocalStream(long paramLong);

  private native boolean nativeStartRtcEventLog(int paramInt1, int paramInt2);

  private native void nativeStopRtcEventLog();

  public boolean addIceCandidate(IceCandidate paramIceCandidate)
  {
    return nativeAddIceCandidate(paramIceCandidate.sdpMid, paramIceCandidate.sdpMLineIndex, paramIceCandidate.sdp);
  }

  public boolean addStream(MediaStream paramMediaStream)
  {
    if (!nativeAddLocalStream(paramMediaStream.nativeStream))
      return false;
    this.localStreams.add(paramMediaStream);
    return true;
  }

  public native void close();

  public native void createAnswer(SdpObserver paramSdpObserver, MediaConstraints paramMediaConstraints);

  public native DataChannel createDataChannel(String paramString, DataChannel.Init paramInit);

  public native void createOffer(SdpObserver paramSdpObserver, MediaConstraints paramMediaConstraints);

  public RtpSender createSender(String paramString1, String paramString2)
  {
    paramString1 = nativeCreateSender(paramString1, paramString2);
    if (paramString1 != null)
      this.senders.add(paramString1);
    return paramString1;
  }

  public void dispose()
  {
    close();
    Iterator localIterator = this.localStreams.iterator();
    while (localIterator.hasNext())
    {
      MediaStream localMediaStream = (MediaStream)localIterator.next();
      nativeRemoveLocalStream(localMediaStream.nativeStream);
      localMediaStream.dispose();
    }
    this.localStreams.clear();
    localIterator = this.senders.iterator();
    while (localIterator.hasNext())
      ((RtpSender)localIterator.next()).dispose();
    this.senders.clear();
    localIterator = this.receivers.iterator();
    while (localIterator.hasNext())
      ((RtpReceiver)localIterator.next()).dispose();
    this.receivers.clear();
    freePeerConnection(this.nativePeerConnection);
    freeObserver(this.nativeObserver);
  }

  public native SessionDescription getLocalDescription();

  public List<RtpReceiver> getReceivers()
  {
    Iterator localIterator = this.receivers.iterator();
    while (localIterator.hasNext())
      ((RtpReceiver)localIterator.next()).dispose();
    this.receivers = nativeGetReceivers();
    return Collections.unmodifiableList(this.receivers);
  }

  public native SessionDescription getRemoteDescription();

  public List<RtpSender> getSenders()
  {
    Iterator localIterator = this.senders.iterator();
    while (localIterator.hasNext())
      ((RtpSender)localIterator.next()).dispose();
    this.senders = nativeGetSenders();
    return Collections.unmodifiableList(this.senders);
  }

  public boolean getStats(StatsObserver paramStatsObserver, MediaStreamTrack paramMediaStreamTrack)
  {
    long l;
    if (paramMediaStreamTrack == null)
      l = 0L;
    while (true)
    {
      return nativeGetStats(paramStatsObserver, l);
      l = paramMediaStreamTrack.nativeTrack;
    }
  }

  public native IceConnectionState iceConnectionState();

  public native IceGatheringState iceGatheringState();

  public boolean removeIceCandidates(IceCandidate[] paramArrayOfIceCandidate)
  {
    return nativeRemoveIceCandidates(paramArrayOfIceCandidate);
  }

  public void removeStream(MediaStream paramMediaStream)
  {
    nativeRemoveLocalStream(paramMediaStream.nativeStream);
    this.localStreams.remove(paramMediaStream);
  }

  public native boolean setConfiguration(RTCConfiguration paramRTCConfiguration);

  public native void setLocalDescription(SdpObserver paramSdpObserver, SessionDescription paramSessionDescription);

  public native void setRemoteDescription(SdpObserver paramSdpObserver, SessionDescription paramSessionDescription);

  public native SignalingState signalingState();

  public boolean startRtcEventLog(int paramInt1, int paramInt2)
  {
    return nativeStartRtcEventLog(paramInt1, paramInt2);
  }

  public void stopRtcEventLog()
  {
    nativeStopRtcEventLog();
  }

  public static enum BundlePolicy
  {
    static
    {
      $VALUES = new BundlePolicy[] { BALANCED, MAXBUNDLE, MAXCOMPAT };
    }
  }

  public static enum CandidateNetworkPolicy
  {
    static
    {
      $VALUES = new CandidateNetworkPolicy[] { ALL, LOW_COST };
    }
  }

  public static enum ContinualGatheringPolicy
  {
    static
    {
      GATHER_CONTINUALLY = new ContinualGatheringPolicy("GATHER_CONTINUALLY", 1);
      $VALUES = new ContinualGatheringPolicy[] { GATHER_ONCE, GATHER_CONTINUALLY };
    }
  }

  public static enum IceConnectionState
  {
    static
    {
      CHECKING = new IceConnectionState("CHECKING", 1);
      CONNECTED = new IceConnectionState("CONNECTED", 2);
      COMPLETED = new IceConnectionState("COMPLETED", 3);
      FAILED = new IceConnectionState("FAILED", 4);
      DISCONNECTED = new IceConnectionState("DISCONNECTED", 5);
      CLOSED = new IceConnectionState("CLOSED", 6);
      $VALUES = new IceConnectionState[] { NEW, CHECKING, CONNECTED, COMPLETED, FAILED, DISCONNECTED, CLOSED };
    }
  }

  public static enum IceGatheringState
  {
    static
    {
      GATHERING = new IceGatheringState("GATHERING", 1);
      COMPLETE = new IceGatheringState("COMPLETE", 2);
      $VALUES = new IceGatheringState[] { NEW, GATHERING, COMPLETE };
    }
  }

  public static class IceServer
  {
    public final String password;
    public final String uri;
    public final String username;

    public IceServer(String paramString)
    {
      this(paramString, "", "");
    }

    public IceServer(String paramString1, String paramString2, String paramString3)
    {
      this.uri = paramString1;
      this.username = paramString2;
      this.password = paramString3;
    }

    public String toString()
    {
      return this.uri + "[" + this.username + ":" + this.password + "]";
    }
  }

  public static enum IceTransportsType
  {
    static
    {
      NOHOST = new IceTransportsType("NOHOST", 2);
      ALL = new IceTransportsType("ALL", 3);
      $VALUES = new IceTransportsType[] { NONE, RELAY, NOHOST, ALL };
    }
  }

  public static enum KeyType
  {
    static
    {
      ECDSA = new KeyType("ECDSA", 1);
      $VALUES = new KeyType[] { RSA, ECDSA };
    }
  }

  public static abstract interface Observer
  {
    public abstract void onAddStream(MediaStream paramMediaStream);

    public abstract void onDataChannel(DataChannel paramDataChannel);

    public abstract void onIceCandidate(IceCandidate paramIceCandidate);

    public abstract void onIceCandidatesRemoved(IceCandidate[] paramArrayOfIceCandidate);

    public abstract void onIceConnectionChange(PeerConnection.IceConnectionState paramIceConnectionState);

    public abstract void onIceConnectionReceivingChange(boolean paramBoolean);

    public abstract void onIceGatheringChange(PeerConnection.IceGatheringState paramIceGatheringState);

    public abstract void onRemoveStream(MediaStream paramMediaStream);

    public abstract void onRenegotiationNeeded();

    public abstract void onSignalingChange(PeerConnection.SignalingState paramSignalingState);
  }

  public static class RTCConfiguration
  {
    public boolean audioJitterBufferFastAccelerate;
    public int audioJitterBufferMaxPackets;
    public PeerConnection.BundlePolicy bundlePolicy = PeerConnection.BundlePolicy.BALANCED;
    public PeerConnection.CandidateNetworkPolicy candidateNetworkPolicy;
    public PeerConnection.ContinualGatheringPolicy continualGatheringPolicy;
    public int iceBackupCandidatePairPingInterval;
    public int iceCandidatePoolSize;
    public int iceConnectionReceivingTimeout;
    public List<PeerConnection.IceServer> iceServers;
    public PeerConnection.IceTransportsType iceTransportsType = PeerConnection.IceTransportsType.ALL;
    public PeerConnection.KeyType keyType;
    public boolean presumeWritableWhenFullyRelayed;
    public boolean pruneTurnPorts;
    public PeerConnection.RtcpMuxPolicy rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.NEGOTIATE;
    public PeerConnection.TcpCandidatePolicy tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;

    public RTCConfiguration(List<PeerConnection.IceServer> paramList)
    {
      PeerConnection.CandidateNetworkPolicy localCandidateNetworkPolicy = this.candidateNetworkPolicy;
      this.candidateNetworkPolicy = PeerConnection.CandidateNetworkPolicy.ALL;
      this.iceServers = paramList;
      this.audioJitterBufferMaxPackets = 50;
      this.audioJitterBufferFastAccelerate = false;
      this.iceConnectionReceivingTimeout = -1;
      this.iceBackupCandidatePairPingInterval = -1;
      this.keyType = PeerConnection.KeyType.ECDSA;
      this.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_ONCE;
      this.iceCandidatePoolSize = 0;
      this.pruneTurnPorts = false;
      this.presumeWritableWhenFullyRelayed = false;
    }
  }

  public static enum RtcpMuxPolicy
  {
    static
    {
      $VALUES = new RtcpMuxPolicy[] { NEGOTIATE, REQUIRE };
    }
  }

  public static enum SignalingState
  {
    static
    {
      HAVE_LOCAL_OFFER = new SignalingState("HAVE_LOCAL_OFFER", 1);
      HAVE_LOCAL_PRANSWER = new SignalingState("HAVE_LOCAL_PRANSWER", 2);
      HAVE_REMOTE_OFFER = new SignalingState("HAVE_REMOTE_OFFER", 3);
      HAVE_REMOTE_PRANSWER = new SignalingState("HAVE_REMOTE_PRANSWER", 4);
      CLOSED = new SignalingState("CLOSED", 5);
      $VALUES = new SignalingState[] { STABLE, HAVE_LOCAL_OFFER, HAVE_LOCAL_PRANSWER, HAVE_REMOTE_OFFER, HAVE_REMOTE_PRANSWER, CLOSED };
    }
  }

  public static enum TcpCandidatePolicy
  {
    static
    {
      DISABLED = new TcpCandidatePolicy("DISABLED", 1);
      $VALUES = new TcpCandidatePolicy[] { ENABLED, DISABLED };
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.PeerConnection
 * JD-Core Version:    0.6.0
 */