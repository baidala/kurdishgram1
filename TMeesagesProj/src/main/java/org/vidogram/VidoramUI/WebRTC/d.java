package org.vidogram.VidogramUi.WebRTC;

import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import org.vidogram.VidogramUi.WebRTC.a.a.b;
import org.vidogram.messenger.FileLog;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.DataChannel.Buffer;
import org.webrtc.DataChannel.Init;
import org.webrtc.DataChannel.Observer;
import org.webrtc.EglBase.Context;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.Logging.Severity;
import org.webrtc.Logging.TraceLevel;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.BundlePolicy;
import org.webrtc.PeerConnection.ContinualGatheringPolicy;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.KeyType;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.RTCConfiguration;
import org.webrtc.PeerConnection.RtcpMuxPolicy;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.PeerConnection.TcpCandidatePolicy;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.PeerConnectionFactory.Options;
import org.webrtc.RtpParameters;
import org.webrtc.RtpParameters.Encoding;
import org.webrtc.RtpSender;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioUtils;

public class d
{
  private static final d b = new d();
  private d A;
  private LinkedList<IceCandidate> B;
  private c C;
  private boolean D;
  private SessionDescription E;
  private MediaStream F;
  private VideoCapturer G;
  private boolean H;
  private VideoTrack I;
  private VideoTrack J;
  private RtpSender K;
  private VideoRenderer L;
  private boolean M;
  private AudioTrack N;
  private DataChannel O;
  private boolean P;
  PeerConnectionFactory.Options a = null;
  private final b c = new b(null);
  private final e d = new e(null);
  private final ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();
  private Context f;
  private PeerConnectionFactory g;
  private PeerConnection h;
  private AudioSource i;
  private VideoSource j;
  private boolean k;
  private boolean l;
  private String m;
  private boolean n;
  private boolean o;
  private Timer p;
  private VideoRenderer.Callbacks q;
  private List<VideoRenderer.Callbacks> r;
  private a.b s;
  private MediaConstraints t;
  private int u;
  private int v;
  private int w;
  private MediaConstraints x;
  private ParcelFileDescriptor y;
  private MediaConstraints z;

  public static d a()
  {
    return b;
  }

  private VideoTrack a(VideoCapturer paramVideoCapturer)
  {
    this.j = this.g.createVideoSource(paramVideoCapturer);
    paramVideoCapturer.startCapture(this.u, this.v, this.w);
    this.I = this.g.createVideoTrack("ARDAMSv0", this.j);
    this.I.setEnabled(false);
    g();
    this.L = new VideoRenderer(this.q);
    this.I.addRenderer(this.L);
    return this.I;
  }

  private void a(Context paramContext)
  {
    PeerConnectionFactory.initializeInternalTracer();
    if (this.A.b)
      PeerConnectionFactory.startInternalTracingCapture(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "webrtc-trace.txt");
    FileLog.d("PCRTCClientCreate peer connection factory. Use video: " + this.A.a);
    this.o = false;
    label151: boolean bool;
    if (this.A.i)
    {
      PeerConnectionFactory.initializeFieldTrials("WebRTC-FlexFEC-03/Enabled/");
      FileLog.d("PCRTCClientEnable FlexFEC field trial.");
      this.m = "VP8";
      if ((this.k) && (this.A.g != null))
      {
        if (!this.A.g.equals("VP9"))
          break label390;
        this.m = "VP9";
      }
      FileLog.d("PCRTCClientPreferred video codec: " + this.m);
      if ((this.A.k == null) || (!this.A.k.equals("ISAC")))
        break label416;
      bool = true;
      label205: this.l = bool;
      if (this.A.n)
        break label421;
      FileLog.d("PCRTCClientDisable OpenSL ES audio even if device supports it");
      WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true);
      label230: if (!this.A.o)
        break label434;
      FileLog.d("PCRTCClientDisable built-in AEC even if device supports it");
      WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
      label250: if (!this.A.p)
        break label447;
      FileLog.d("PCRTCClientDisable built-in AGC even if device supports it");
      WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);
      label270: if (!this.A.q)
        break label460;
      FileLog.d("PCRTCClientDisable built-in NS even if device supports it");
      WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
    }
    while (true)
    {
      if (!PeerConnectionFactory.initializeAndroidGlobals(paramContext, true, true, this.A.h))
        this.C.b("Failed to initializeAndroidGlobals");
      if (this.a != null)
        FileLog.d("PCRTCClientFactory networkIgnoreMask option: " + this.a.networkIgnoreMask);
      this.f = paramContext;
      this.g = new PeerConnectionFactory(this.a);
      FileLog.d("PCRTCClientPeer connection factory created.");
      return;
      PeerConnectionFactory.initializeFieldTrials("");
      break;
      label390: if (!this.A.g.equals("H264"))
        break label151;
      this.m = "H264";
      break label151;
      label416: bool = false;
      break label205;
      label421: FileLog.d("PCRTCClientAllow OpenSL ES audio if device supports it");
      WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false);
      break label230;
      label434: FileLog.d("PCRTCClientEnable built-in AEC if device supports it");
      WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false);
      break label250;
      label447: FileLog.d("PCRTCClientEnable built-in AGC if device supports it");
      WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(false);
      break label270;
      label460: FileLog.d("PCRTCClientEnable built-in NS if device supports it");
      WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false);
    }
  }

  private void a(EglBase.Context paramContext)
  {
    if ((this.g == null) || (this.o))
    {
      FileLog.e("PCRTCClientPeerconnection factory is not created");
      return;
    }
    FileLog.d("PCRTCClientCreate peer connection.");
    FileLog.d("PCRTCClientPCConstraints: " + this.t.toString());
    this.B = new LinkedList();
    if (this.k)
    {
      FileLog.d("PCRTCClientEGLContext: " + paramContext);
      this.g.setVideoHwAccelerationOptions(paramContext, paramContext);
    }
    paramContext = new PeerConnection.RTCConfiguration(this.s.a);
    paramContext.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
    paramContext.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
    paramContext.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
    paramContext.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
    paramContext.keyType = PeerConnection.KeyType.ECDSA;
    this.h = this.g.createPeerConnection(paramContext, this.t, this.c);
    if (this.P)
    {
      paramContext = new DataChannel.Init();
      paramContext.ordered = d.a(this.A).a;
      paramContext.negotiated = d.a(this.A).e;
      paramContext.maxRetransmits = d.a(this.A).c;
      paramContext.maxRetransmitTimeMs = d.a(this.A).b;
      paramContext.id = d.a(this.A).f;
      paramContext.protocol = d.a(this.A).d;
      this.O = this.h.createDataChannel("Vidogram WebRTC data", paramContext);
    }
    this.D = false;
    Logging.enableTracing("logcat:", EnumSet.of(Logging.TraceLevel.TRACE_DEFAULT));
    Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
    this.F = this.g.createLocalMediaStream("ARDAMS");
    if (this.k)
      this.F.addTrack(a(this.G));
    this.F.addTrack(m());
    this.h.addStream(this.F);
    if (this.k)
      n();
    if (this.A.m);
    try
    {
      this.y = ParcelFileDescriptor.open(new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Download/audio.aecdump"), 1006632960);
      this.g.startAecDump(this.y.getFd(), -1);
      FileLog.d("PCRTCClientPeer connection created.");
      return;
    }
    catch (java.io.IOException paramContext)
    {
      while (true)
        FileLog.e("PCRTCClientCan not open aecdump file", paramContext);
    }
  }

  private static String b(String paramString1, String paramString2, boolean paramBoolean)
  {
    int i4 = 0;
    String[] arrayOfString = paramString1.split("\r\n");
    String str2 = null;
    Pattern localPattern = Pattern.compile("^a=rtpmap:(\\d+) " + paramString2 + "(/\\d+)+[\r]?$");
    String str1 = "m=video ";
    if (paramBoolean)
      str1 = "m=audio ";
    int i1 = 0;
    int i2 = -1;
    if ((i1 < arrayOfString.length) && ((i2 == -1) || (str2 == null)))
    {
      int i3;
      if (arrayOfString[i1].startsWith(str1))
        i3 = i1;
      while (true)
      {
        i1 += 1;
        i2 = i3;
        break;
        Matcher localMatcher = localPattern.matcher(arrayOfString[i1]);
        i3 = i2;
        if (!localMatcher.matches())
          continue;
        str2 = localMatcher.group(1);
        i3 = i2;
      }
    }
    if (i2 == -1)
    {
      FileLog.w("PCRTCClientNo " + str1 + " line, so can't prefer " + paramString2);
      return paramString1;
    }
    if (str2 == null)
    {
      FileLog.w("PCRTCClientNo rtpmap for " + paramString2);
      return paramString1;
    }
    FileLog.d("PCRTCClientFound " + paramString2 + " rtpmap " + str2 + ", prefer at " + arrayOfString[i2]);
    paramString1 = arrayOfString[i2].split(" ");
    if (paramString1.length > 3)
    {
      paramString2 = new StringBuilder();
      paramString2.append(paramString1[0]).append(" ");
      paramString2.append(paramString1[1]).append(" ");
      paramString2.append(paramString1[2]).append(" ");
      paramString2.append(str2);
      i1 = 3;
      while (i1 < paramString1.length)
      {
        if (!paramString1[i1].equals(str2))
          paramString2.append(" ").append(paramString1[i1]);
        i1 += 1;
      }
      arrayOfString[i2] = paramString2.toString();
      FileLog.d("PCRTCClientChange media description: " + arrayOfString[i2]);
    }
    while (true)
    {
      paramString1 = new StringBuilder();
      i2 = arrayOfString.length;
      i1 = i4;
      while (i1 < i2)
      {
        paramString1.append(arrayOfString[i1]).append("\r\n");
        i1 += 1;
      }
      FileLog.e("PCRTCClientWrong SDP media description format: " + arrayOfString[i2]);
    }
    return paramString1.toString();
  }

  private static String b(String paramString1, boolean paramBoolean, String paramString2, int paramInt)
  {
    int i3 = 0;
    String[] arrayOfString = paramString2.split("\r\n");
    int i4 = -1;
    Object localObject2 = null;
    Pattern localPattern = Pattern.compile("^a=rtpmap:(\\d+) " + paramString1 + "(/\\d+)+[\r]?$");
    int i1 = 0;
    Object localObject1;
    int i2;
    while (true)
    {
      localObject1 = localObject2;
      i2 = i4;
      if (i1 < arrayOfString.length)
      {
        localObject1 = localPattern.matcher(arrayOfString[i1]);
        if (((Matcher)localObject1).matches())
        {
          localObject1 = ((Matcher)localObject1).group(1);
          i2 = i1;
        }
      }
      else
      {
        if (localObject1 != null)
          break;
        FileLog.w("PCRTCClientNo rtpmap for " + paramString1 + " codec");
        return paramString2;
      }
      i1 += 1;
    }
    FileLog.d("PCRTCClientFound " + paramString1 + " rtpmap " + (String)localObject1 + " at " + arrayOfString[i2]);
    paramString2 = Pattern.compile("^a=fmtp:" + (String)localObject1 + " \\w+=\\d+.*[\r]?$");
    i1 = 0;
    if (i1 < arrayOfString.length)
      if (paramString2.matcher(arrayOfString[i1]).matches())
      {
        FileLog.d("PCRTCClientFound " + paramString1 + " " + arrayOfString[i1]);
        if (paramBoolean)
        {
          arrayOfString[i1] = (arrayOfString[i1] + "; x-google-start-bitrate=" + paramInt);
          label324: FileLog.d("PCRTCClientUpdate remote SDP line: " + arrayOfString[i1]);
        }
      }
    for (i1 = 1; ; i1 = 0)
    {
      paramString2 = new StringBuilder();
      label362: if (i3 < arrayOfString.length)
      {
        paramString2.append(arrayOfString[i3]).append("\r\n");
        if ((i1 == 0) && (i3 == i2))
          if (!paramBoolean)
            break label539;
        for (paramString1 = "a=fmtp:" + (String)localObject1 + " " + "x-google-start-bitrate" + "=" + paramInt; ; paramString1 = "a=fmtp:" + (String)localObject1 + " " + "maxaveragebitrate" + "=" + paramInt * 1000)
        {
          FileLog.d("PCRTCClientAdd remote SDP line: " + paramString1);
          paramString2.append(paramString1).append("\r\n");
          i3 += 1;
          break label362;
          arrayOfString[i1] = (arrayOfString[i1] + "; maxaveragebitrate=" + paramInt * 1000);
          break label324;
          i1 += 1;
          break;
        }
      }
      label539: return paramString2.toString();
    }
  }

  private void b(String paramString)
  {
    FileLog.e("PCRTCClientPeerconnection error: " + paramString);
    this.e.execute(new Runnable(paramString)
    {
      public void run()
      {
        if (!d.k(d.this))
        {
          d.d(d.this).b(this.a);
          d.d(d.this, true);
        }
      }
    });
  }

  private static void b(JSONObject paramJSONObject, String paramString, Object paramObject)
  {
    try
    {
      paramJSONObject.put(paramString, paramObject);
      return;
    }
    catch (JSONException paramJSONObject)
    {
    }
    throw new RuntimeException(paramJSONObject);
  }

  private void j()
  {
    this.t = new MediaConstraints();
    this.t.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
    if (this.G == null)
    {
      FileLog.w("PCRTCClientNo camera on device. Switch to audio only call.");
      this.k = false;
    }
    if (this.k)
    {
      this.u = this.A.c;
      this.v = this.A.d;
      this.w = this.A.e;
      if ((this.u == 0) || (this.v == 0))
      {
        this.u = 1280;
        this.v = 720;
      }
      if (this.w == 0)
        this.w = 30;
      Logging.d("PCRTCClient", "Capturing format: " + this.u + "x" + this.v + "@" + this.w);
    }
    this.x = new MediaConstraints();
    if (this.A.l)
    {
      FileLog.d("PCRTCClientDisabling audio processing");
      this.x.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "false"));
      this.x.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "false"));
      this.x.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "false"));
      this.x.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "false"));
    }
    if (this.A.r)
    {
      FileLog.d("PCRTCClientEnabling level control.");
      this.x.mandatory.add(new MediaConstraints.KeyValuePair("levelControl", "true"));
    }
    this.z = new MediaConstraints();
    this.z.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
    if (this.k)
    {
      this.z.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
      return;
    }
    this.z.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
  }

  private void k()
  {
    if ((this.g != null) && (this.A.m))
      this.g.stopAecDump();
    FileLog.d("PCRTCClientClosing peer connection.");
    this.p.cancel();
    if (this.O != null)
    {
      this.O.dispose();
      this.O = null;
    }
    if (this.h != null)
    {
      this.h.dispose();
      this.h = null;
    }
    FileLog.d("PCRTCClientClosing audio source.");
    if (this.i != null)
    {
      this.i.dispose();
      this.i = null;
    }
    FileLog.d("PCRTCClientStopping capture.");
    if (this.G != null);
    try
    {
      this.G.stopCapture();
      this.n = true;
      this.G.dispose();
      this.G = null;
      FileLog.d("PCRTCClientClosing video source.");
      if (this.j != null)
      {
        this.j.dispose();
        this.j = null;
      }
      FileLog.d("PCRTCClientClosing peer connection factory.");
      if (this.g != null)
      {
        this.g.dispose();
        this.g = null;
      }
      this.a = null;
      FileLog.d("PCRTCClientClosing peer connection done.");
      this.C.u();
      PeerConnectionFactory.stopInternalTracingCapture();
      PeerConnectionFactory.shutdownInternalTracer();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    throw new RuntimeException(localInterruptedException);
  }

  private void l()
  {
    if ((this.h == null) || (this.o));
    do
      return;
    while (this.h.getStats(new StatsObserver()
    {
      public void onComplete(StatsReport[] paramArrayOfStatsReport)
      {
        d.c(d.this).getReceivers();
        d.c(d.this).getRemoteDescription();
        d.c(d.this).signalingState();
        d.d(d.this).a(paramArrayOfStatsReport);
      }
    }
    , null));
    FileLog.e("PCRTCClientgetStats() returns false!");
  }

  private AudioTrack m()
  {
    this.i = this.g.createAudioSource(this.x);
    this.N = this.g.createAudioTrack("ARDAMSa0", this.i);
    this.N.setEnabled(this.M);
    return this.N;
  }

  private void n()
  {
    Iterator localIterator = this.h.getSenders().iterator();
    while (localIterator.hasNext())
    {
      RtpSender localRtpSender = (RtpSender)localIterator.next();
      if ((localRtpSender.track() == null) || (!localRtpSender.track().kind().equals("video")))
        continue;
      FileLog.d("PCRTCClientFound video sender.");
      this.K = localRtpSender;
    }
  }

  private void o()
  {
    if (this.B != null)
    {
      FileLog.d("PCRTCClientAdd " + this.B.size() + " remote candidates");
      Iterator localIterator = this.B.iterator();
      while (localIterator.hasNext())
      {
        IceCandidate localIceCandidate = (IceCandidate)localIterator.next();
        this.h.addIceCandidate(localIceCandidate);
      }
      this.B = null;
    }
  }

  private void p()
  {
    if ((this.G instanceof CameraVideoCapturer))
    {
      if ((!this.k) || (this.o) || (this.G == null))
      {
        FileLog.e("PCRTCClientFailed to switch camera. Video: " + this.k + ". Error : " + this.o);
        return;
      }
      FileLog.d("PCRTCClientSwitch camera");
      ((CameraVideoCapturer)this.G).switchCamera(null);
      return;
    }
    FileLog.d("PCRTCClientWill not switch camera, video caputurer is not a camera");
  }

  public void a(Context paramContext, d paramd, c paramc)
  {
    this.A = paramd;
    this.C = paramc;
    this.k = paramd.a;
    if (d.a(paramd) != null);
    for (boolean bool = true; ; bool = false)
    {
      this.P = bool;
      this.f = null;
      this.g = null;
      this.h = null;
      this.l = false;
      this.n = false;
      this.o = false;
      this.B = null;
      this.E = null;
      this.F = null;
      this.G = null;
      this.H = true;
      this.I = null;
      this.J = null;
      this.K = null;
      this.M = true;
      this.N = null;
      this.p = new Timer();
      this.e.execute(new Runnable(paramContext)
      {
        public void run()
        {
          d.a(d.this, this.a);
        }
      });
      return;
    }
  }

  public void a(Integer paramInteger)
  {
    this.e.execute(new Runnable(paramInteger)
    {
      public void run()
      {
        if ((d.c(d.this) == null) || (d.z(d.this) == null) || (d.k(d.this)))
          return;
        FileLog.d("PCRTCClientRequested max video bitrate: " + this.a);
        if (d.z(d.this) == null)
        {
          FileLog.w("PCRTCClientSender is not ready.");
          return;
        }
        RtpParameters localRtpParameters = d.z(d.this).getParameters();
        if (localRtpParameters.encodings.size() == 0)
        {
          FileLog.w("PCRTCClientRtpParameters are not ready.");
          return;
        }
        Iterator localIterator = localRtpParameters.encodings.iterator();
        if (localIterator.hasNext())
        {
          RtpParameters.Encoding localEncoding = (RtpParameters.Encoding)localIterator.next();
          if (this.a == null);
          for (Integer localInteger = null; ; localInteger = Integer.valueOf(this.a.intValue() * 1000))
          {
            localEncoding.maxBitrateBps = localInteger;
            break;
          }
        }
        if (!d.z(d.this).setParameters(localRtpParameters))
          FileLog.e("PCRTCClientRtpSender.setParameters failed.");
        FileLog.d("PCRTCClientConfigured max video bitrate to: " + this.a);
      }
    });
  }

  public void a(String paramString)
  {
    this.e.execute(new Runnable(paramString)
    {
      public void run()
      {
        if (d.y(d.this) != null)
        {
          Object localObject = new JSONObject();
          d.a((JSONObject)localObject, "type", this.a);
          localObject = ByteBuffer.wrap(((JSONObject)localObject).toString().getBytes());
          d.y(d.this).send(new DataChannel.Buffer((ByteBuffer)localObject, false));
        }
      }
    });
  }

  public void a(EglBase.Context paramContext, VideoRenderer.Callbacks paramCallbacks, List<VideoRenderer.Callbacks> paramList, VideoCapturer paramVideoCapturer, a.b paramb)
  {
    if (this.A == null)
    {
      FileLog.e("PCRTCClientCreating peer connection without initializing factory.");
      return;
    }
    this.q = paramCallbacks;
    this.r = paramList;
    this.G = paramVideoCapturer;
    this.s = paramb;
    this.e.execute(new Runnable(paramContext)
    {
      public void run()
      {
        try
        {
          d.a(d.this);
          d.a(d.this, this.a);
          return;
        }
        catch (Exception localException)
        {
          d.a(d.this, "Failed to create peer connection: " + localException.getMessage());
        }
        throw localException;
      }
    });
  }

  public void a(IceCandidate paramIceCandidate)
  {
    this.e.execute(new Runnable(paramIceCandidate)
    {
      public void run()
      {
        if ((d.c(d.this) != null) && (!d.k(d.this)))
        {
          if (d.n(d.this) != null)
            d.n(d.this).add(this.a);
        }
        else
          return;
        d.c(d.this).addIceCandidate(this.a);
      }
    });
  }

  public void a(SessionDescription paramSessionDescription)
  {
    this.e.execute(new Runnable(paramSessionDescription)
    {
      public void run()
      {
        if ((d.c(d.this) == null) || (d.k(d.this)))
          return;
        Object localObject2 = this.a.description;
        Object localObject1 = localObject2;
        if (d.p(d.this))
          localObject1 = d.a((String)localObject2, "ISAC", true);
        localObject2 = localObject1;
        if (d.q(d.this))
          localObject2 = d.a((String)localObject1, d.r(d.this), false);
        localObject1 = localObject2;
        if (d.s(d.this).j > 0)
          localObject1 = d.a("opus", false, (String)localObject2, d.s(d.this).j);
        FileLog.d("PCRTCClientSet remote SDP.");
        localObject1 = new SessionDescription(this.a.type, (String)localObject1);
        d.c(d.this).setRemoteDescription(d.l(d.this), (SessionDescription)localObject1);
      }
    });
  }

  public void a(boolean paramBoolean)
  {
    this.e.execute(new Runnable(paramBoolean)
    {
      public void run()
      {
        d.a(d.this, this.a);
        if (d.g(d.this) != null)
          d.g(d.this).setEnabled(d.h(d.this));
      }
    });
  }

  public void a(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
      try
      {
        this.p.schedule(new TimerTask()
        {
          public void run()
          {
            d.f(d.this).execute(new Runnable()
            {
              public void run()
              {
                d.e(d.this);
              }
            });
          }
        }
        , 0L, paramInt);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e("PCRTCClientCan not schedule statistics timer", localException);
        return;
      }
    this.p.cancel();
  }

  public void a(IceCandidate[] paramArrayOfIceCandidate)
  {
    this.e.execute(new Runnable(paramArrayOfIceCandidate)
    {
      public void run()
      {
        if ((d.c(d.this) == null) || (d.k(d.this)))
          return;
        d.o(d.this);
        d.c(d.this).removeIceCandidates(this.a);
      }
    });
  }

  public void b()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        d.b(d.this);
      }
    });
  }

  public void c()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        d.i(d.this).setEnabled(true);
        d.this.h();
      }
    });
  }

  public void d()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        d.i(d.this).setEnabled(false);
        d.this.g();
      }
    });
  }

  public void e()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        if ((d.c(d.this) != null) && (!d.k(d.this)))
        {
          FileLog.d("PCRTCClientPC Create OFFER");
          d.b(d.this, true);
          d.c(d.this).createOffer(d.l(d.this), d.m(d.this));
        }
      }
    });
  }

  public void f()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        if ((d.c(d.this) != null) && (!d.k(d.this)))
        {
          FileLog.d("PCRTCClientPC create ANSWER");
          d.b(d.this, false);
          d.c(d.this).createAnswer(d.l(d.this), d.m(d.this));
        }
      }
    });
  }

  public void g()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        if ((d.t(d.this) != null) && (!d.u(d.this)))
          FileLog.d("PCRTCClientStop video source.");
        try
        {
          d.t(d.this).stopCapture();
          label37: d.c(d.this, true);
          return;
        }
        catch (InterruptedException localInterruptedException)
        {
          break label37;
        }
      }
    });
  }

  public void h()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        if ((d.t(d.this) != null) && (d.u(d.this)))
        {
          FileLog.d("PCRTCClientRestart video source.");
          d.t(d.this).startCapture(d.v(d.this), d.w(d.this), d.x(d.this));
          d.c(d.this, false);
        }
      }
    });
  }

  public void i()
  {
    this.e.execute(new Runnable()
    {
      public void run()
      {
        d.A(d.this);
      }
    });
  }

  public static class a
  {
    public final boolean a;
    public final int b;
    public final int c;
    public final String d;
    public final boolean e;
    public final int f;

    public a(boolean paramBoolean1, int paramInt1, int paramInt2, String paramString, boolean paramBoolean2, int paramInt3)
    {
      this.a = paramBoolean1;
      this.b = paramInt1;
      this.c = paramInt2;
      this.d = paramString;
      this.e = paramBoolean2;
      this.f = paramInt3;
    }
  }

  private class b
    implements PeerConnection.Observer
  {
    private b()
    {
    }

    public void onAddStream(MediaStream paramMediaStream)
    {
      d.f(d.this).execute(new Runnable(paramMediaStream)
      {
        public void run()
        {
          if ((d.c(d.this) == null) || (d.k(d.this)));
          while (true)
          {
            return;
            if ((this.a.audioTracks.size() > 1) || (this.a.videoTracks.size() > 1))
            {
              d.a(d.this, "Weird-looking stream: " + this.a);
              return;
            }
            if (this.a.videoTracks.size() != 1)
              continue;
            d.a(d.this, (VideoTrack)this.a.videoTracks.get(0));
            d.B(d.this).setEnabled(d.j(d.this));
            Iterator localIterator = d.C(d.this).iterator();
            while (localIterator.hasNext())
            {
              VideoRenderer.Callbacks localCallbacks = (VideoRenderer.Callbacks)localIterator.next();
              d.B(d.this).addRenderer(new VideoRenderer(localCallbacks));
            }
          }
        }
      });
    }

    public void onDataChannel(DataChannel paramDataChannel)
    {
      FileLog.d("PCRTCClientNew Data channel " + paramDataChannel.label());
      if (!d.D(d.this))
        return;
      paramDataChannel.registerObserver(new DataChannel.Observer(paramDataChannel)
      {
        public void onBufferedAmountChange(long paramLong)
        {
          FileLog.d("PCRTCClientData channel buffered amount changed: " + this.a.label() + ": " + this.a.state());
        }

        public void onMessage(DataChannel.Buffer paramBuffer)
        {
          if (paramBuffer.binary)
            FileLog.d("PCRTCClientReceived binary msg over " + this.a);
          do
            while (true)
            {
              return;
              paramBuffer = paramBuffer.data;
              byte[] arrayOfByte = new byte[paramBuffer.capacity()];
              paramBuffer.get(arrayOfByte);
              paramBuffer = new String(arrayOfByte);
              FileLog.d("PCRTCClientGot msg: " + paramBuffer + " over " + this.a);
              try
              {
                paramBuffer = new JSONObject(paramBuffer).getString("type");
                if (paramBuffer.length() <= 0)
                  continue;
                paramBuffer = new JSONObject(paramBuffer).optString("type");
                if (!paramBuffer.equals("start_remote"))
                  break;
                d.d(d.this).m();
                return;
              }
              catch (JSONException paramBuffer)
              {
                paramBuffer.printStackTrace();
                return;
              }
            }
          while (!paramBuffer.equals("stop_remote"));
          d.d(d.this).l();
        }

        public void onStateChange()
        {
          FileLog.d("PCRTCClientData channel state changed: " + this.a.label() + ": " + this.a.state());
        }
      });
    }

    public void onIceCandidate(IceCandidate paramIceCandidate)
    {
      d.f(d.this).execute(new Runnable(paramIceCandidate)
      {
        public void run()
        {
          d.d(d.this).b(this.a);
        }
      });
    }

    public void onIceCandidatesRemoved(IceCandidate[] paramArrayOfIceCandidate)
    {
      d.f(d.this).execute(new Runnable(paramArrayOfIceCandidate)
      {
        public void run()
        {
          d.d(d.this).b(this.a);
        }
      });
    }

    public void onIceConnectionChange(PeerConnection.IceConnectionState paramIceConnectionState)
    {
      d.f(d.this).execute(new Runnable(paramIceConnectionState)
      {
        public void run()
        {
          FileLog.d("PCRTCClientIceConnectionState: " + this.a);
          if (this.a == PeerConnection.IceConnectionState.CONNECTED)
            d.d(d.this).s();
          do
          {
            return;
            if (this.a != PeerConnection.IceConnectionState.DISCONNECTED)
              continue;
            d.d(d.this).t();
            return;
          }
          while (this.a != PeerConnection.IceConnectionState.FAILED);
          d.d(d.this).t();
        }
      });
    }

    public void onIceConnectionReceivingChange(boolean paramBoolean)
    {
      FileLog.d("PCRTCClientIceConnectionReceiving changed to " + paramBoolean);
    }

    public void onIceGatheringChange(PeerConnection.IceGatheringState paramIceGatheringState)
    {
      FileLog.d("PCRTCClientIceGatheringState: " + paramIceGatheringState);
    }

    public void onRemoveStream(MediaStream paramMediaStream)
    {
      d.f(d.this).execute(new Runnable()
      {
        public void run()
        {
          d.a(d.this, null);
        }
      });
    }

    public void onRenegotiationNeeded()
    {
    }

    public void onSignalingChange(PeerConnection.SignalingState paramSignalingState)
    {
      FileLog.d("PCRTCClientSignalingState: " + paramSignalingState);
    }
  }

  public static abstract interface c
  {
    public abstract void a(StatsReport[] paramArrayOfStatsReport);

    public abstract void b(String paramString);

    public abstract void b(IceCandidate paramIceCandidate);

    public abstract void b(IceCandidate[] paramArrayOfIceCandidate);

    public abstract void c(SessionDescription paramSessionDescription);

    public abstract void l();

    public abstract void m();

    public abstract void s();

    public abstract void t();

    public abstract void u();
  }

  public static class d
  {
    public final boolean a;
    public final boolean b;
    public final int c;
    public final int d;
    public final int e;
    public final int f;
    public final String g;
    public final boolean h;
    public final boolean i;
    public final int j;
    public final String k;
    public final boolean l;
    public final boolean m;
    public final boolean n;
    public final boolean o;
    public final boolean p;
    public final boolean q;
    public final boolean r;
    private final d.a s;

    public d(boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString1, boolean paramBoolean3, boolean paramBoolean4, int paramInt5, String paramString2, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, boolean paramBoolean8, boolean paramBoolean9, boolean paramBoolean10, boolean paramBoolean11, d.a parama)
    {
      this.a = paramBoolean1;
      this.b = paramBoolean2;
      this.c = paramInt1;
      this.d = paramInt2;
      this.e = paramInt3;
      this.f = paramInt4;
      this.g = paramString1;
      this.i = paramBoolean4;
      this.h = paramBoolean3;
      this.j = paramInt5;
      this.k = paramString2;
      this.l = paramBoolean5;
      this.m = paramBoolean6;
      this.n = paramBoolean7;
      this.o = paramBoolean8;
      this.p = paramBoolean9;
      this.q = paramBoolean10;
      this.r = paramBoolean11;
      this.s = parama;
    }
  }

  private class e
    implements SdpObserver
  {
    private e()
    {
    }

    public void onCreateFailure(String paramString)
    {
      d.a(d.this, "createSDP error: " + paramString);
    }

    public void onCreateSuccess(SessionDescription paramSessionDescription)
    {
      if (d.E(d.this) != null)
      {
        d.a(d.this, "Multiple SDP create.");
        return;
      }
      Object localObject2 = paramSessionDescription.description;
      Object localObject1 = localObject2;
      if (d.p(d.this))
        localObject1 = d.a((String)localObject2, "ISAC", true);
      localObject2 = localObject1;
      if (d.q(d.this))
        localObject2 = d.a((String)localObject1, d.r(d.this), false);
      paramSessionDescription = new SessionDescription(paramSessionDescription.type, (String)localObject2);
      d.a(d.this, paramSessionDescription);
      d.f(d.this).execute(new Runnable(paramSessionDescription)
      {
        public void run()
        {
          if ((d.c(d.this) != null) && (!d.k(d.this)))
          {
            FileLog.d("PCRTCClientSet local SDP from " + this.a.type);
            d.c(d.this).setLocalDescription(d.l(d.this), this.a);
          }
        }
      });
    }

    public void onSetFailure(String paramString)
    {
      d.a(d.this, "setSDP error: " + paramString);
    }

    public void onSetSuccess()
    {
      d.f(d.this).execute(new Runnable()
      {
        public void run()
        {
          if ((d.c(d.this) == null) || (d.k(d.this)))
            return;
          if (d.F(d.this))
          {
            if (d.c(d.this).getRemoteDescription() == null)
            {
              FileLog.d("PCRTCClientLocal SDP set succesfully");
              d.d(d.this).c(d.E(d.this));
              return;
            }
            FileLog.d("PCRTCClientRemote SDP set succesfully");
            d.o(d.this);
            return;
          }
          if (d.c(d.this).getLocalDescription() != null)
          {
            FileLog.d("PCRTCClientLocal SDP set succesfully");
            d.d(d.this).c(d.E(d.this));
            d.o(d.this);
            return;
          }
          FileLog.d("PCRTCClientRemote SDP set succesfully");
        }
      });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.d
 * JD-Core Version:    0.6.0
 */