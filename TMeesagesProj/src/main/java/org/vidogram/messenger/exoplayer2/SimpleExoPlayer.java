package org.vidogram.messenger.exoplayer2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.PlaybackParams;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.vidogram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.vidogram.messenger.exoplayer2.decoder.DecoderCounters;
import org.vidogram.messenger.exoplayer2.drm.DrmSessionManager;
import org.vidogram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.vidogram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.metadata.MetadataRenderer;
import org.vidogram.messenger.exoplayer2.metadata.MetadataRenderer.Output;
import org.vidogram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.vidogram.messenger.exoplayer2.source.MediaSource;
import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.text.TextRenderer;
import org.vidogram.messenger.exoplayer2.text.TextRenderer.Output;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelector;
import org.vidogram.messenger.exoplayer2.video.MediaCodecVideoRenderer;
import org.vidogram.messenger.exoplayer2.video.VideoRendererEventListener;

@TargetApi(16)
public class SimpleExoPlayer
  implements ExoPlayer
{
  public static final int EXTENSION_RENDERER_MODE_OFF = 0;
  public static final int EXTENSION_RENDERER_MODE_ON = 1;
  public static final int EXTENSION_RENDERER_MODE_PREFER = 2;
  protected static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
  private static final String TAG = "SimpleExoPlayer";
  private AudioRendererEventListener audioDebugListener;
  private DecoderCounters audioDecoderCounters;
  private Format audioFormat;
  private final int audioRendererCount;
  private int audioSessionId;
  private int audioStreamType;
  private float audioVolume;
  private final ComponentListener componentListener = new ComponentListener();
  private final Handler mainHandler = new Handler();
  private MetadataRenderer.Output metadataOutput;
  private boolean needSetSurface = true;
  private boolean ownsSurface;
  private PlaybackParamsHolder playbackParamsHolder;
  private final ExoPlayer player;
  private final Renderer[] renderers;
  private Surface surface;
  private SurfaceHolder surfaceHolder;
  private TextRenderer.Output textOutput;
  private TextureView textureView;
  private VideoRendererEventListener videoDebugListener;
  private DecoderCounters videoDecoderCounters;
  private Format videoFormat;
  private VideoListener videoListener;
  private final int videoRendererCount;
  private int videoScalingMode;

  protected SimpleExoPlayer(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, long paramLong)
  {
    ArrayList localArrayList = new ArrayList();
    buildRenderers(paramContext, this.mainHandler, paramDrmSessionManager, paramInt, paramLong, localArrayList);
    this.renderers = ((Renderer[])localArrayList.toArray(new Renderer[localArrayList.size()]));
    paramContext = this.renderers;
    int k = paramContext.length;
    int j = 0;
    int i = 0;
    paramInt = 0;
    if (paramInt < k)
    {
      switch (paramContext[paramInt].getTrackType())
      {
      default:
      case 2:
      case 1:
      }
      while (true)
      {
        paramInt += 1;
        break;
        i += 1;
        continue;
        j += 1;
      }
    }
    this.videoRendererCount = i;
    this.audioRendererCount = j;
    this.audioVolume = 1.0F;
    this.audioSessionId = 0;
    this.audioStreamType = 3;
    this.videoScalingMode = 1;
    this.player = new ExoPlayerImpl(this.renderers, paramTrackSelector, paramLoadControl);
  }

  private void buildRenderers(Context paramContext, Handler paramHandler, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, long paramLong, ArrayList<Renderer> paramArrayList)
  {
    buildVideoRenderers(paramContext, paramHandler, paramDrmSessionManager, paramInt, this.componentListener, paramLong, paramArrayList);
    buildAudioRenderers(paramContext, paramHandler, paramDrmSessionManager, paramInt, this.componentListener, paramArrayList);
    buildTextRenderers(paramContext, paramHandler, paramInt, this.componentListener, paramArrayList);
    buildMetadataRenderers(paramContext, paramHandler, paramInt, this.componentListener, paramArrayList);
    buildMiscellaneousRenderers(paramContext, paramHandler, paramInt, paramArrayList);
  }

  private void removeSurfaceCallbacks()
  {
    if (this.textureView != null)
    {
      if (this.textureView.getSurfaceTextureListener() == this.componentListener)
        break label60;
      Log.w("SimpleExoPlayer", "SurfaceTextureListener already unset or replaced.");
    }
    while (true)
    {
      this.textureView = null;
      if (this.surfaceHolder != null)
      {
        this.surfaceHolder.removeCallback(this.componentListener);
        this.surfaceHolder = null;
      }
      return;
      label60: this.textureView.setSurfaceTextureListener(null);
    }
  }

  private void setVideoSurfaceInternal(Surface paramSurface, boolean paramBoolean)
  {
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.videoRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 2)
        break label147;
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 1, paramSurface);
      i = k;
    }
    label147: 
    while (true)
    {
      j += 1;
      break;
      if ((this.surface != null) && (this.surface != paramSurface))
      {
        if (this.ownsSurface)
          this.surface.release();
        this.player.blockingSendMessages(arrayOfExoPlayerMessage);
      }
      while (true)
      {
        this.surface = paramSurface;
        this.ownsSurface = paramBoolean;
        return;
        this.player.sendMessages(arrayOfExoPlayerMessage);
      }
    }
  }

  public void addListener(ExoPlayer.EventListener paramEventListener)
  {
    this.player.addListener(paramEventListener);
  }

  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
  {
    this.player.blockingSendMessages(paramArrayOfExoPlayerMessage);
  }

  // ERROR //
  protected void buildAudioRenderers(Context paramContext, Handler paramHandler, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, AudioRendererEventListener paramAudioRendererEventListener, ArrayList<Renderer> paramArrayList)
  {
    // Byte code:
    //   0: aload 6
    //   2: new 267	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer
    //   5: dup
    //   6: getstatic 273	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecSelector:DEFAULT	Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecSelector;
    //   9: aload_3
    //   10: iconst_1
    //   11: aload_2
    //   12: aload 5
    //   14: aload_1
    //   15: invokestatic 279	org/vidogram/messenger/exoplayer2/audio/AudioCapabilities:getCapabilities	(Landroid/content/Context;)Lorg/vidogram/messenger/exoplayer2/audio/AudioCapabilities;
    //   18: invokespecial 282	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:<init>	(Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecSelector;Lorg/vidogram/messenger/exoplayer2/drm/DrmSessionManager;ZLandroid/os/Handler;Lorg/vidogram/messenger/exoplayer2/audio/AudioRendererEventListener;Lorg/vidogram/messenger/exoplayer2/audio/AudioCapabilities;)V
    //   21: invokevirtual 286	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   24: pop
    //   25: iload 4
    //   27: ifne +4 -> 31
    //   30: return
    //   31: aload 6
    //   33: invokevirtual 104	java/util/ArrayList:size	()I
    //   36: istore 8
    //   38: iload 8
    //   40: istore 7
    //   42: iload 4
    //   44: iconst_2
    //   45: if_icmpne +9 -> 54
    //   48: iload 8
    //   50: iconst_1
    //   51: isub
    //   52: istore 7
    //   54: ldc_w 288
    //   57: invokestatic 294	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   60: iconst_2
    //   61: anewarray 290	java/lang/Class
    //   64: dup
    //   65: iconst_0
    //   66: ldc 85
    //   68: aastore
    //   69: dup
    //   70: iconst_1
    //   71: ldc_w 296
    //   74: aastore
    //   75: invokevirtual 300	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   78: iconst_2
    //   79: anewarray 4	java/lang/Object
    //   82: dup
    //   83: iconst_0
    //   84: aload_2
    //   85: aastore
    //   86: dup
    //   87: iconst_1
    //   88: aload_0
    //   89: getfield 93	org/vidogram/messenger/exoplayer2/SimpleExoPlayer:componentListener	Lorg/vidogram/messenger/exoplayer2/SimpleExoPlayer$ComponentListener;
    //   92: aastore
    //   93: invokevirtual 306	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   96: checkcast 106	org/vidogram/messenger/exoplayer2/Renderer
    //   99: astore_1
    //   100: iload 7
    //   102: iconst_1
    //   103: iadd
    //   104: istore 4
    //   106: aload 6
    //   108: iload 7
    //   110: aload_1
    //   111: invokevirtual 309	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   114: ldc 34
    //   116: ldc_w 311
    //   119: invokestatic 314	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   122: pop
    //   123: ldc_w 316
    //   126: invokestatic 294	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   129: iconst_2
    //   130: anewarray 290	java/lang/Class
    //   133: dup
    //   134: iconst_0
    //   135: ldc 85
    //   137: aastore
    //   138: dup
    //   139: iconst_1
    //   140: ldc_w 296
    //   143: aastore
    //   144: invokevirtual 300	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   147: iconst_2
    //   148: anewarray 4	java/lang/Object
    //   151: dup
    //   152: iconst_0
    //   153: aload_2
    //   154: aastore
    //   155: dup
    //   156: iconst_1
    //   157: aload_0
    //   158: getfield 93	org/vidogram/messenger/exoplayer2/SimpleExoPlayer:componentListener	Lorg/vidogram/messenger/exoplayer2/SimpleExoPlayer$ComponentListener;
    //   161: aastore
    //   162: invokevirtual 306	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   165: checkcast 106	org/vidogram/messenger/exoplayer2/Renderer
    //   168: astore_1
    //   169: iload 4
    //   171: iconst_1
    //   172: iadd
    //   173: istore 7
    //   175: aload 6
    //   177: iload 4
    //   179: aload_1
    //   180: invokevirtual 309	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   183: ldc 34
    //   185: ldc_w 318
    //   188: invokestatic 314	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   191: pop
    //   192: aload 6
    //   194: iload 7
    //   196: ldc_w 320
    //   199: invokestatic 294	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   202: iconst_2
    //   203: anewarray 290	java/lang/Class
    //   206: dup
    //   207: iconst_0
    //   208: ldc 85
    //   210: aastore
    //   211: dup
    //   212: iconst_1
    //   213: ldc_w 296
    //   216: aastore
    //   217: invokevirtual 300	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   220: iconst_2
    //   221: anewarray 4	java/lang/Object
    //   224: dup
    //   225: iconst_0
    //   226: aload_2
    //   227: aastore
    //   228: dup
    //   229: iconst_1
    //   230: aload_0
    //   231: getfield 93	org/vidogram/messenger/exoplayer2/SimpleExoPlayer:componentListener	Lorg/vidogram/messenger/exoplayer2/SimpleExoPlayer$ComponentListener;
    //   234: aastore
    //   235: invokevirtual 306	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   238: checkcast 106	org/vidogram/messenger/exoplayer2/Renderer
    //   241: invokevirtual 309	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   244: ldc 34
    //   246: ldc_w 322
    //   249: invokestatic 314	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   252: pop
    //   253: return
    //   254: astore_1
    //   255: return
    //   256: astore_1
    //   257: iload 7
    //   259: istore 4
    //   261: goto -138 -> 123
    //   264: astore_1
    //   265: new 324	java/lang/RuntimeException
    //   268: dup
    //   269: aload_1
    //   270: invokespecial 327	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   273: athrow
    //   274: astore_1
    //   275: iload 4
    //   277: istore 7
    //   279: goto -87 -> 192
    //   282: astore_1
    //   283: new 324	java/lang/RuntimeException
    //   286: dup
    //   287: aload_1
    //   288: invokespecial 327	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   291: athrow
    //   292: astore_1
    //   293: new 324	java/lang/RuntimeException
    //   296: dup
    //   297: aload_1
    //   298: invokespecial 327	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   301: athrow
    //   302: astore_1
    //   303: iload 7
    //   305: istore 4
    //   307: goto -32 -> 275
    //   310: astore_1
    //   311: goto -50 -> 261
    //
    // Exception table:
    //   from	to	target	type
    //   192	253	254	java/lang/ClassNotFoundException
    //   54	100	256	java/lang/ClassNotFoundException
    //   54	100	264	java/lang/Exception
    //   106	123	264	java/lang/Exception
    //   123	169	274	java/lang/ClassNotFoundException
    //   123	169	282	java/lang/Exception
    //   175	192	282	java/lang/Exception
    //   192	253	292	java/lang/Exception
    //   175	192	302	java/lang/ClassNotFoundException
    //   106	123	310	java/lang/ClassNotFoundException
  }

  protected void buildMetadataRenderers(Context paramContext, Handler paramHandler, int paramInt, MetadataRenderer.Output paramOutput, ArrayList<Renderer> paramArrayList)
  {
    paramArrayList.add(new MetadataRenderer(paramOutput, paramHandler.getLooper(), new Id3Decoder()));
  }

  protected void buildMiscellaneousRenderers(Context paramContext, Handler paramHandler, int paramInt, ArrayList<Renderer> paramArrayList)
  {
  }

  protected void buildTextRenderers(Context paramContext, Handler paramHandler, int paramInt, TextRenderer.Output paramOutput, ArrayList<Renderer> paramArrayList)
  {
    paramArrayList.add(new TextRenderer(paramOutput, paramHandler.getLooper()));
  }

  protected void buildVideoRenderers(Context paramContext, Handler paramHandler, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, VideoRendererEventListener paramVideoRendererEventListener, long paramLong, ArrayList<Renderer> paramArrayList)
  {
    paramArrayList.add(new MediaCodecVideoRenderer(paramContext, MediaCodecSelector.DEFAULT, paramLong, paramDrmSessionManager, false, paramHandler, paramVideoRendererEventListener, 50));
    if (paramInt == 0)
      return;
    int i = paramArrayList.size();
    if (paramInt == 2);
    for (paramInt = i - 1; ; paramInt = i)
      try
      {
        paramArrayList.add(paramInt, (Renderer)Class.forName("org.vidogram.messenger.exoplayer2.ext.vp9.LibvpxVideoRenderer").getConstructor(new Class[] { Boolean.TYPE, Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE }).newInstance(new Object[] { Boolean.valueOf(true), Long.valueOf(paramLong), paramHandler, this.componentListener, Integer.valueOf(50) }));
        Log.i("SimpleExoPlayer", "Loaded LibvpxVideoRenderer.");
        return;
      }
      catch (java.lang.ClassNotFoundException paramContext)
      {
        return;
      }
      catch (java.lang.Exception paramContext)
      {
        throw new RuntimeException(paramContext);
      }
  }

  public void clearVideoSurface()
  {
    setVideoSurface(null);
  }

  public DecoderCounters getAudioDecoderCounters()
  {
    return this.audioDecoderCounters;
  }

  public Format getAudioFormat()
  {
    return this.audioFormat;
  }

  public int getAudioSessionId()
  {
    return this.audioSessionId;
  }

  public int getAudioStreamType()
  {
    return this.audioStreamType;
  }

  public int getBufferedPercentage()
  {
    return this.player.getBufferedPercentage();
  }

  public long getBufferedPosition()
  {
    return this.player.getBufferedPosition();
  }

  public ComponentListener getComponentListener()
  {
    return this.componentListener;
  }

  public Object getCurrentManifest()
  {
    return this.player.getCurrentManifest();
  }

  public int getCurrentPeriodIndex()
  {
    return this.player.getCurrentPeriodIndex();
  }

  public long getCurrentPosition()
  {
    return this.player.getCurrentPosition();
  }

  public Timeline getCurrentTimeline()
  {
    return this.player.getCurrentTimeline();
  }

  public TrackGroupArray getCurrentTrackGroups()
  {
    return this.player.getCurrentTrackGroups();
  }

  public TrackSelectionArray getCurrentTrackSelections()
  {
    return this.player.getCurrentTrackSelections();
  }

  public int getCurrentWindowIndex()
  {
    return this.player.getCurrentWindowIndex();
  }

  public long getDuration()
  {
    return this.player.getDuration();
  }

  public boolean getPlayWhenReady()
  {
    return this.player.getPlayWhenReady();
  }

  @TargetApi(23)
  public PlaybackParams getPlaybackParams()
  {
    if (this.playbackParamsHolder == null)
      return null;
    return this.playbackParamsHolder.params;
  }

  public int getPlaybackState()
  {
    return this.player.getPlaybackState();
  }

  public int getRendererCount()
  {
    return this.player.getRendererCount();
  }

  public int getRendererType(int paramInt)
  {
    return this.player.getRendererType(paramInt);
  }

  public DecoderCounters getVideoDecoderCounters()
  {
    return this.videoDecoderCounters;
  }

  public Format getVideoFormat()
  {
    return this.videoFormat;
  }

  public int getVideoScalingMode()
  {
    return this.videoScalingMode;
  }

  public float getVolume()
  {
    return this.audioVolume;
  }

  public boolean isLoading()
  {
    return this.player.isLoading();
  }

  public void prepare(MediaSource paramMediaSource)
  {
    this.player.prepare(paramMediaSource);
  }

  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.player.prepare(paramMediaSource, paramBoolean1, paramBoolean2);
  }

  public void release()
  {
    this.player.release();
    removeSurfaceCallbacks();
    if (this.surface != null)
    {
      if (this.ownsSurface)
        this.surface.release();
      this.surface = null;
    }
  }

  public void removeListener(ExoPlayer.EventListener paramEventListener)
  {
    this.player.removeListener(paramEventListener);
  }

  public void seekTo(int paramInt, long paramLong)
  {
    this.player.seekTo(paramInt, paramLong);
  }

  public void seekTo(long paramLong)
  {
    this.player.seekTo(paramLong);
  }

  public void seekToDefaultPosition()
  {
    this.player.seekToDefaultPosition();
  }

  public void seekToDefaultPosition(int paramInt)
  {
    this.player.seekToDefaultPosition(paramInt);
  }

  public void sendMessages(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
  {
    this.player.sendMessages(paramArrayOfExoPlayerMessage);
  }

  public void setAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    this.audioDebugListener = paramAudioRendererEventListener;
  }

  public void setAudioStreamType(int paramInt)
  {
    this.audioStreamType = paramInt;
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.audioRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 1)
        break label97;
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 4, Integer.valueOf(paramInt));
      i = k;
    }
    label97: 
    while (true)
    {
      j += 1;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }

  @Deprecated
  public void setId3Output(MetadataRenderer.Output paramOutput)
  {
    setMetadataOutput(paramOutput);
  }

  public void setMetadataOutput(MetadataRenderer.Output paramOutput)
  {
    this.metadataOutput = paramOutput;
  }

  public void setPlayWhenReady(boolean paramBoolean)
  {
    this.player.setPlayWhenReady(paramBoolean);
  }

  @TargetApi(23)
  public void setPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage;
    int j;
    if (paramPlaybackParams != null)
    {
      paramPlaybackParams.allowDefaults();
      this.playbackParamsHolder = new PlaybackParamsHolder(paramPlaybackParams);
      arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.audioRendererCount];
      Renderer[] arrayOfRenderer = this.renderers;
      int m = arrayOfRenderer.length;
      j = 0;
      int i = 0;
      label45: if (j >= m)
        break label106;
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 1)
        break label118;
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 3, paramPlaybackParams);
      i = k;
    }
    label106: label118: 
    while (true)
    {
      j += 1;
      break label45;
      this.playbackParamsHolder = null;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }

  public void setTextOutput(TextRenderer.Output paramOutput)
  {
    this.textOutput = paramOutput;
  }

  public void setVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    this.videoDebugListener = paramVideoRendererEventListener;
  }

  public void setVideoListener(VideoListener paramVideoListener)
  {
    this.videoListener = paramVideoListener;
  }

  public void setVideoScalingMode(int paramInt)
  {
    this.videoScalingMode = paramInt;
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.videoRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 2)
        break label97;
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 5, Integer.valueOf(paramInt));
      i = k;
    }
    label97: 
    while (true)
    {
      j += 1;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }

  public void setVideoSurface(Surface paramSurface)
  {
    removeSurfaceCallbacks();
    setVideoSurfaceInternal(paramSurface, false);
  }

  public void setVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder)
  {
    removeSurfaceCallbacks();
    this.surfaceHolder = paramSurfaceHolder;
    if (paramSurfaceHolder == null)
    {
      setVideoSurfaceInternal(null, false);
      return;
    }
    setVideoSurfaceInternal(paramSurfaceHolder.getSurface(), false);
    paramSurfaceHolder.addCallback(this.componentListener);
  }

  public void setVideoSurfaceView(SurfaceView paramSurfaceView)
  {
    setVideoSurfaceHolder(paramSurfaceView.getHolder());
  }

  public ComponentListener setVideoTextureView(TextureView paramTextureView)
  {
    Surface localSurface = null;
    removeSurfaceCallbacks();
    this.textureView = paramTextureView;
    if (paramTextureView == null)
    {
      setVideoSurfaceInternal(null, true);
      return this.componentListener;
    }
    if (paramTextureView.getSurfaceTextureListener() != null)
      Log.w("SimpleExoPlayer", "Replacing existing SurfaceTextureListener.");
    SurfaceTexture localSurfaceTexture = paramTextureView.getSurfaceTexture();
    if (localSurfaceTexture == null);
    while (true)
    {
      setVideoSurfaceInternal(localSurface, true);
      if (localSurfaceTexture != null)
        this.needSetSurface = false;
      paramTextureView.setSurfaceTextureListener(this.componentListener);
      break;
      localSurface = new Surface(localSurfaceTexture);
    }
  }

  public void setVolume(float paramFloat)
  {
    this.audioVolume = paramFloat;
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.audioRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 1)
        break label97;
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 2, Float.valueOf(paramFloat));
      i = k;
    }
    label97: 
    while (true)
    {
      j += 1;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }

  public void stop()
  {
    this.player.stop();
  }

  public final class ComponentListener
    implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener, AudioRendererEventListener, MetadataRenderer.Output, TextRenderer.Output, VideoRendererEventListener
  {
    public ComponentListener()
    {
    }

    public void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      if (SimpleExoPlayer.this.audioDebugListener != null)
        SimpleExoPlayer.this.audioDebugListener.onAudioDecoderInitialized(paramString, paramLong1, paramLong2);
    }

    public void onAudioDisabled(DecoderCounters paramDecoderCounters)
    {
      if (SimpleExoPlayer.this.audioDebugListener != null)
        SimpleExoPlayer.this.audioDebugListener.onAudioDisabled(paramDecoderCounters);
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$502(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$702(SimpleExoPlayer.this, 0);
    }

    public void onAudioEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$502(SimpleExoPlayer.this, paramDecoderCounters);
      if (SimpleExoPlayer.this.audioDebugListener != null)
        SimpleExoPlayer.this.audioDebugListener.onAudioEnabled(paramDecoderCounters);
    }

    public void onAudioInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, paramFormat);
      if (SimpleExoPlayer.this.audioDebugListener != null)
        SimpleExoPlayer.this.audioDebugListener.onAudioInputFormatChanged(paramFormat);
    }

    public void onAudioSessionId(int paramInt)
    {
      SimpleExoPlayer.access$702(SimpleExoPlayer.this, paramInt);
      if (SimpleExoPlayer.this.audioDebugListener != null)
        SimpleExoPlayer.this.audioDebugListener.onAudioSessionId(paramInt);
    }

    public void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      if (SimpleExoPlayer.this.audioDebugListener != null)
        SimpleExoPlayer.this.audioDebugListener.onAudioTrackUnderrun(paramInt, paramLong1, paramLong2);
    }

    public void onCues(List<Cue> paramList)
    {
      if (SimpleExoPlayer.this.textOutput != null)
        SimpleExoPlayer.this.textOutput.onCues(paramList);
    }

    public void onDroppedFrames(int paramInt, long paramLong)
    {
      if (SimpleExoPlayer.this.videoDebugListener != null)
        SimpleExoPlayer.this.videoDebugListener.onDroppedFrames(paramInt, paramLong);
    }

    public void onMetadata(Metadata paramMetadata)
    {
      if (SimpleExoPlayer.this.metadataOutput != null)
        SimpleExoPlayer.this.metadataOutput.onMetadata(paramMetadata);
    }

    public void onRenderedFirstFrame(Surface paramSurface)
    {
      if ((SimpleExoPlayer.this.videoListener != null) && (SimpleExoPlayer.this.surface == paramSurface))
        SimpleExoPlayer.this.videoListener.onRenderedFirstFrame();
      if (SimpleExoPlayer.this.videoDebugListener != null)
        SimpleExoPlayer.this.videoDebugListener.onRenderedFirstFrame(paramSurface);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      if (SimpleExoPlayer.this.needSetSurface)
      {
        SimpleExoPlayer.this.setVideoSurfaceInternal(new Surface(paramSurfaceTexture), true);
        SimpleExoPlayer.access$1202(SimpleExoPlayer.this, false);
      }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
    {
      if (SimpleExoPlayer.this.videoListener.onSurfaceDestroyed(paramSurfaceTexture))
        return false;
      SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
      SimpleExoPlayer.access$1202(SimpleExoPlayer.this, true);
      return true;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
    {
      SimpleExoPlayer.this.videoListener.onSurfaceTextureUpdated(paramSurfaceTexture);
    }

    public void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      if (SimpleExoPlayer.this.videoDebugListener != null)
        SimpleExoPlayer.this.videoDebugListener.onVideoDecoderInitialized(paramString, paramLong1, paramLong2);
    }

    public void onVideoDisabled(DecoderCounters paramDecoderCounters)
    {
      if (SimpleExoPlayer.this.videoDebugListener != null)
        SimpleExoPlayer.this.videoDebugListener.onVideoDisabled(paramDecoderCounters);
      SimpleExoPlayer.access$202(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$002(SimpleExoPlayer.this, null);
    }

    public void onVideoEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$002(SimpleExoPlayer.this, paramDecoderCounters);
      if (SimpleExoPlayer.this.videoDebugListener != null)
        SimpleExoPlayer.this.videoDebugListener.onVideoEnabled(paramDecoderCounters);
    }

    public void onVideoInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$202(SimpleExoPlayer.this, paramFormat);
      if (SimpleExoPlayer.this.videoDebugListener != null)
        SimpleExoPlayer.this.videoDebugListener.onVideoInputFormatChanged(paramFormat);
    }

    public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      if (SimpleExoPlayer.this.videoListener != null)
        SimpleExoPlayer.this.videoListener.onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
      if (SimpleExoPlayer.this.videoDebugListener != null)
        SimpleExoPlayer.this.videoDebugListener.onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
    {
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(paramSurfaceHolder.getSurface(), false);
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(null, false);
    }
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface ExtensionRendererMode
  {
  }

  @TargetApi(23)
  private static final class PlaybackParamsHolder
  {
    public final PlaybackParams params;

    public PlaybackParamsHolder(PlaybackParams paramPlaybackParams)
    {
      this.params = paramPlaybackParams;
    }
  }

  public static abstract interface VideoListener
  {
    public abstract void onRenderedFirstFrame();

    public abstract boolean onSurfaceDestroyed(SurfaceTexture paramSurfaceTexture);

    public abstract void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture);

    public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.SimpleExoPlayer
 * JD-Core Version:    0.6.0
 */