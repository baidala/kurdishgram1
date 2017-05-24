package org.vidogram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.vidogram.messenger.audioinfo.AudioInfo;
import org.vidogram.messenger.query.SharedMediaQuery;
import org.vidogram.messenger.video.MP4Builder;
import org.vidogram.messenger.voip.VoIPService;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.InputDocument;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_encryptedChat;
import org.vidogram.tgnet.TLRPC.TL_messages_messages;
import org.vidogram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.vidogram.tgnet.TLRPC.messages_Messages;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ChatActivity;
import org.vidogram.ui.Components.EmbedBottomSheet;
import org.vidogram.ui.PhotoViewer;

public class MediaController
  implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate
{
  private static final int AUDIO_FOCUSED = 2;
  private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
  private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
  public static final int AUTODOWNLOAD_MASK_AUDIO = 2;
  public static final int AUTODOWNLOAD_MASK_DOCUMENT = 8;
  public static final int AUTODOWNLOAD_MASK_GIF = 32;
  public static final int AUTODOWNLOAD_MASK_MUSIC = 16;
  public static final int AUTODOWNLOAD_MASK_PHOTO = 1;
  public static final int AUTODOWNLOAD_MASK_VIDEO = 4;
  private static volatile MediaController Instance;
  public static final String MIME_TYPE = "video/avc";
  private static final int PROCESSOR_TYPE_INTEL = 2;
  private static final int PROCESSOR_TYPE_MTK = 3;
  private static final int PROCESSOR_TYPE_OTHER = 0;
  private static final int PROCESSOR_TYPE_QCOM = 1;
  private static final int PROCESSOR_TYPE_SEC = 4;
  private static final int PROCESSOR_TYPE_TI = 5;
  private static final float VOLUME_DUCK = 0.2F;
  private static final float VOLUME_NORMAL = 1.0F;
  public static AlbumEntry allPhotosAlbumEntry;
  private static Runnable broadcastPhotosRunnable;
  private static final String[] projectionPhotos;
  private static final String[] projectionVideo;
  public static int[] readArgs = new int[3];
  private Sensor accelerometerSensor;
  private boolean accelerometerVertical;
  private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap();
  private boolean allowStartRecord;
  private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList();
  private int audioFocus = 0;
  private AudioInfo audioInfo;
  private MediaPlayer audioPlayer = null;
  private AudioRecord audioRecorder = null;
  private AudioTrack audioTrackPlayer = null;
  private boolean autoplayGifs = true;
  private int buffersWrited;
  private boolean callInProgress;
  private boolean cancelCurrentVideoConversion = false;
  private int countLess;
  private int currentPlaylistNum;
  private long currentTotalPcmDuration;
  private boolean customTabs = true;
  private boolean decodingFinished = false;
  private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList();
  private boolean directShare = true;
  private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList();
  private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap();
  private boolean downloadingCurrentMessage;
  private ExternalObserver externalObserver = null;
  private ByteBuffer fileBuffer;
  private DispatchQueue fileDecodingQueue;
  private DispatchQueue fileEncodingQueue;
  private boolean forceLoopCurrentPlaylist;
  private ArrayList<AudioBuffer> freePlayerBuffers = new ArrayList();
  private HashMap<String, MessageObject> generatingWaveform = new HashMap();
  private ArrayList<DownloadObject> gifDownloadQueue = new ArrayList();
  private float[] gravity = new float[3];
  private float[] gravityFast = new float[3];
  private Sensor gravitySensor;
  private int hasAudioFocus;
  private int ignoreFirstProgress = 0;
  private boolean ignoreOnPause;
  private boolean ignoreProximity;
  private boolean inputFieldHasText;
  private InternalObserver internalObserver = null;
  private boolean isPaused = false;
  private int lastCheckMask = 0;
  private long lastMediaCheckTime = 0L;
  private long lastPlayPcm;
  private int lastProgress = 0;
  private float lastProximityValue = -100.0F;
  private TLRPC.EncryptedChat lastSecretChat = null;
  private long lastSecretChatEnterTime = 0L;
  private long lastSecretChatLeaveTime = 0L;
  private ArrayList<Long> lastSecretChatVisibleMessages = null;
  private int lastTag = 0;
  private long lastTimestamp = 0L;
  private float[] linearAcceleration = new float[3];
  private Sensor linearSensor;
  private boolean listenerInProgress = false;
  private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap();
  private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap();
  private String[] mediaProjections = null;
  public int mobileDataDownloadMask = 0;
  private ArrayList<DownloadObject> musicDownloadQueue = new ArrayList();
  private HashMap<Integer, String> observersByTag = new HashMap();
  private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList();
  private boolean playMusicAgain;
  private int playerBufferSize = 0;
  private final Object playerObjectSync = new Object();
  private DispatchQueue playerQueue;
  private final Object playerSync = new Object();
  private MessageObject playingMessageObject;
  private ArrayList<MessageObject> playlist = new ArrayList();
  private float previousAccValue;
  private Timer progressTimer = null;
  private final Object progressTimerSync = new Object();
  private boolean proximityHasDifferentValues;
  private Sensor proximitySensor;
  private boolean proximityTouched;
  private PowerManager.WakeLock proximityWakeLock;
  private ChatActivity raiseChat;
  private boolean raiseToEarRecord;
  private boolean raiseToSpeak = true;
  private int raisedToBack;
  private int raisedToTop;
  private int recordBufferSize;
  private ArrayList<ByteBuffer> recordBuffers = new ArrayList();
  private long recordDialogId;
  private DispatchQueue recordQueue;
  private MessageObject recordReplyingMessageObject;
  private Runnable recordRunnable = new Runnable()
  {
    public void run()
    {
      ByteBuffer localByteBuffer;
      int n;
      double d2;
      double d1;
      if (MediaController.this.audioRecorder != null)
        if (!MediaController.this.recordBuffers.isEmpty())
        {
          localByteBuffer = (ByteBuffer)MediaController.this.recordBuffers.get(0);
          MediaController.this.recordBuffers.remove(0);
          localByteBuffer.rewind();
          n = MediaController.this.audioRecorder.read(localByteBuffer, localByteBuffer.capacity());
          if (n <= 0)
            break label512;
          localByteBuffer.limit(n);
          d2 = 0.0D;
          d1 = d2;
        }
      while (true)
      {
        int m;
        float f2;
        int j;
        double d3;
        try
        {
          long l = MediaController.this.samplesCount;
          d1 = d2;
          l = n / 2 + l;
          d1 = d2;
          k = (int)(MediaController.this.samplesCount / l * MediaController.this.recordSamples.length);
          d1 = d2;
          m = MediaController.this.recordSamples.length;
          if (k == 0)
            continue;
          d1 = d2;
          f2 = MediaController.this.recordSamples.length / k;
          f1 = 0.0F;
          j = 0;
          if (j >= k)
            continue;
          d1 = d2;
          MediaController.this.recordSamples[j] = MediaController.access$400(MediaController.this)[(int)f1];
          f1 += f2;
          j += 1;
          continue;
          localByteBuffer = ByteBuffer.allocateDirect(MediaController.this.recordBufferSize);
          localByteBuffer.order(ByteOrder.nativeOrder());
          break;
          float f3 = n / 2.0F / (m - k);
          f1 = 0.0F;
          j = 0;
          d1 = d2;
          if (j >= n / 2)
            continue;
          d1 = d2;
          int i = localByteBuffer.getShort();
          d3 = d2;
          if (i <= 2500)
            continue;
          d3 = d2 + i * i;
          f2 = f1;
          m = k;
          if (j != (int)f1)
            break label540;
          d1 = d3;
          f2 = f1;
          m = k;
          if (k >= MediaController.this.recordSamples.length)
            break label540;
          d1 = d3;
          MediaController.this.recordSamples[k] = i;
          f2 = f1 + f3;
          m = k + 1;
          break label540;
          d1 = d2;
          MediaController.access$302(MediaController.this, l);
          localByteBuffer.position(0);
          d1 = Math.sqrt(d2 / n / 2.0D);
          if (n == localByteBuffer.capacity())
            continue;
          bool = true;
          if (n == 0)
            continue;
          MediaController.this.fileEncodingQueue.postRunnable(new Runnable(localByteBuffer, bool)
          {
            public void run()
            {
              int i;
              if (this.val$finalBuffer.hasRemaining())
              {
                if (this.val$finalBuffer.remaining() <= MediaController.this.fileBuffer.remaining())
                  break label280;
                i = this.val$finalBuffer.limit();
                this.val$finalBuffer.limit(MediaController.this.fileBuffer.remaining() + this.val$finalBuffer.position());
              }
              while (true)
              {
                MediaController.this.fileBuffer.put(this.val$finalBuffer);
                MediaController localMediaController;
                ByteBuffer localByteBuffer;
                if ((MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit()) || (this.val$flush))
                {
                  localMediaController = MediaController.this;
                  localByteBuffer = MediaController.this.fileBuffer;
                  if (this.val$flush)
                    break label247;
                }
                label247: for (int j = MediaController.this.fileBuffer.limit(); ; j = this.val$finalBuffer.position())
                {
                  if (localMediaController.writeFrame(localByteBuffer, j) != 0)
                  {
                    MediaController.this.fileBuffer.rewind();
                    MediaController.access$702(MediaController.this, MediaController.this.recordTimeCount + MediaController.this.fileBuffer.limit() / 2 / 16);
                  }
                  if (i == -1)
                    break;
                  this.val$finalBuffer.limit(i);
                  break;
                }
                MediaController.this.recordQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    MediaController.this.recordBuffers.add(MediaController.1.1.this.val$finalBuffer);
                  }
                });
                return;
                label280: i = -1;
              }
            }
          });
          MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
          AndroidUtilities.runOnUIThread(new Runnable(d1)
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, new Object[] { Long.valueOf(System.currentTimeMillis() - MediaController.access$1100(MediaController.this)), Double.valueOf(this.val$amplitude) });
            }
          });
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          d2 = d1;
          continue;
          boolean bool = false;
          continue;
        }
        label512: MediaController.this.recordBuffers.add(localByteBuffer);
        MediaController.this.stopRecordingInternal(MediaController.this.sendAfterDone);
        return;
        label540: j += 1;
        float f1 = f2;
        int k = m;
        d2 = d3;
      }
    }
  };
  private short[] recordSamples = new short[1024];
  private Runnable recordStartRunnable;
  private long recordStartTime;
  private long recordTimeCount;
  private TLRPC.TL_document recordingAudio = null;
  private File recordingAudioFile = null;
  private Runnable refreshGalleryRunnable;
  private int repeatMode;
  private boolean resumeAudioOnFocusGain;
  public int roamingDownloadMask = 0;
  private long samplesCount;
  private boolean saveToGallery = true;
  private int sendAfterDone;
  private SensorManager sensorManager;
  private boolean sensorsStarted;
  private boolean shuffleMusic;
  private ArrayList<MessageObject> shuffledPlaylist = new ArrayList();
  private int startObserverToken = 0;
  private StopMediaObserverRunnable stopMediaObserverRunnable = null;
  private final Object sync = new Object();
  private long timeSinceRaise;
  private HashMap<Long, Long> typingTimes = new HashMap();
  private boolean useFrontSpeaker;
  private ArrayList<AudioBuffer> usedPlayerBuffers = new ArrayList();
  private boolean videoConvertFirstWrite = true;
  private ArrayList<MessageObject> videoConvertQueue = new ArrayList();
  private final Object videoConvertSync = new Object();
  private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList();
  private final Object videoQueueSync = new Object();
  private ArrayList<MessageObject> voiceMessagesPlaylist;
  private HashMap<Integer, MessageObject> voiceMessagesPlaylistMap;
  private boolean voiceMessagesPlaylistUnread;
  public int wifiDownloadMask = 0;

  static
  {
    projectionPhotos = new String[] { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken", "orientation" };
    projectionVideo = new String[] { "_id", "bucket_id", "bucket_display_name", "_data", "datetaken" };
    Instance = null;
  }

  public MediaController()
  {
    try
    {
      this.recordBufferSize = AudioRecord.getMinBufferSize(16000, 16, 2);
      if (this.recordBufferSize <= 0)
        this.recordBufferSize = 1280;
      this.playerBufferSize = AudioTrack.getMinBufferSize(48000, 4, 2);
      if (this.playerBufferSize <= 0)
      {
        this.playerBufferSize = 3840;
        break label1263;
        while (i < 5)
        {
          ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(4096);
          localByteBuffer.order(ByteOrder.nativeOrder());
          this.recordBuffers.add(localByteBuffer);
          i += 1;
        }
        while (i < 3)
        {
          this.freePlayerBuffers.add(new AudioBuffer(this.playerBufferSize));
          i += 1;
        }
      }
    }
    catch (Exception localException5)
    {
      while (true)
      {
        FileLog.e(localException1);
        try
        {
          this.sensorManager = ((SensorManager)ApplicationLoader.applicationContext.getSystemService("sensor"));
          this.linearSensor = this.sensorManager.getDefaultSensor(10);
          this.gravitySensor = this.sensorManager.getDefaultSensor(9);
          if ((this.linearSensor == null) || (this.gravitySensor == null))
          {
            FileLog.e("gravity or linear sensor not found");
            this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
            this.linearSensor = null;
            this.gravitySensor = null;
          }
          this.proximitySensor = this.sensorManager.getDefaultSensor(8);
          this.proximityWakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
          this.fileBuffer = ByteBuffer.allocateDirect(1920);
          this.recordQueue = new DispatchQueue("recordQueue");
          this.recordQueue.setPriority(10);
          this.fileEncodingQueue = new DispatchQueue("fileEncodingQueue");
          this.fileEncodingQueue.setPriority(10);
          this.playerQueue = new DispatchQueue("playerQueue");
          this.fileDecodingQueue = new DispatchQueue("fileDecodingQueue");
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
          this.mobileDataDownloadMask = ((SharedPreferences)localObject1).getInt("mobileDataDownloadMask", 51);
          this.wifiDownloadMask = ((SharedPreferences)localObject1).getInt("wifiDownloadMask", 51);
          this.roamingDownloadMask = ((SharedPreferences)localObject1).getInt("roamingDownloadMask", 0);
          this.saveToGallery = ((SharedPreferences)localObject1).getBoolean("save_gallery", false);
          this.autoplayGifs = ((SharedPreferences)localObject1).getBoolean("autoplay_gif", true);
          this.raiseToSpeak = ((SharedPreferences)localObject1).getBoolean("raise_to_speak", true);
          this.customTabs = ((SharedPreferences)localObject1).getBoolean("custom_tabs", true);
          this.directShare = ((SharedPreferences)localObject1).getBoolean("direct_share", true);
          this.shuffleMusic = ((SharedPreferences)localObject1).getBoolean("shuffleMusic", false);
          this.repeatMode = ((SharedPreferences)localObject1).getInt("repeatMode", 0);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidFailedLoad);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.didReceivedNewMessages);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.messagesDeleted);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileDidLoaded);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileLoadProgressChanged);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.FileUploadProgressChanged);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.musicDidLoaded);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.httpFileDidLoaded);
              NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.httpFileDidFailedLoad);
            }
          });
          localObject1 = new BroadcastReceiver()
          {
            public void onReceive(Context paramContext, Intent paramIntent)
            {
              MediaController.this.checkAutodownloadSettings();
            }
          };
          localObject2 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
          ApplicationLoader.applicationContext.registerReceiver((BroadcastReceiver)localObject1, (IntentFilter)localObject2);
          if (UserConfig.isClientActivated())
            checkAutodownloadSettings();
          if (Build.VERSION.SDK_INT >= 16)
            this.mediaProjections = new String[] { "_data", "_display_name", "bucket_display_name", "datetaken", "title", "width", "height" };
        }
        catch (Exception localException5)
        {
          try
          {
            ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, new GalleryObserverExternal());
          }
          catch (Exception localException5)
          {
            try
            {
              ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, false, new GalleryObserverInternal());
            }
            catch (Exception localException5)
            {
              try
              {
                while (true)
                {
                  Object localObject1 = new PhoneStateListener()
                  {
                    public void onCallStateChanged(int paramInt, String paramString)
                    {
                      AndroidUtilities.runOnUIThread(new Runnable(paramInt)
                      {
                        public void run()
                        {
                          if (this.val$state == 1)
                            if ((!MediaController.this.isPlayingAudio(MediaController.this.getPlayingMessageObject())) || (MediaController.this.isAudioPaused()));
                          do
                          {
                            MediaController.this.pauseAudio(MediaController.this.getPlayingMessageObject());
                            while (true)
                            {
                              localEmbedBottomSheet = EmbedBottomSheet.getInstance();
                              if (localEmbedBottomSheet != null)
                                localEmbedBottomSheet.pause();
                              MediaController.access$2102(MediaController.this, true);
                              return;
                              if ((MediaController.this.recordStartRunnable == null) && (MediaController.this.recordingAudio == null))
                                continue;
                              MediaController.this.stopRecording(2);
                            }
                            if (this.val$state != 0)
                              continue;
                            MediaController.access$2102(MediaController.this, false);
                            return;
                          }
                          while (this.val$state != 2);
                          EmbedBottomSheet localEmbedBottomSheet = EmbedBottomSheet.getInstance();
                          if (localEmbedBottomSheet != null)
                            localEmbedBottomSheet.pause();
                          MediaController.access$2102(MediaController.this, true);
                        }
                      });
                    }
                  };
                  Object localObject2 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
                  if (localObject2 != null)
                    ((TelephonyManager)localObject2).listen((PhoneStateListener)localObject1, 32);
                  return;
                  localException2 = localException2;
                  FileLog.e(localException2);
                  continue;
                  this.mediaProjections = new String[] { "_data", "_display_name", "bucket_display_name", "datetaken", "title" };
                  continue;
                  localException3 = localException3;
                  FileLog.e(localException3);
                  continue;
                  localException4 = localException4;
                  FileLog.e(localException4);
                }
              }
              catch (Exception localException5)
              {
                FileLog.e(localException5);
                return;
              }
            }
          }
        }
        label1263: int i = 0;
        continue;
        i = 0;
      }
    }
  }

  private static void broadcastNewPhotos(int paramInt1, ArrayList<AlbumEntry> paramArrayList1, Integer paramInteger1, ArrayList<AlbumEntry> paramArrayList2, Integer paramInteger2, AlbumEntry paramAlbumEntry, int paramInt2)
  {
    if (broadcastPhotosRunnable != null)
      AndroidUtilities.cancelRunOnUIThread(broadcastPhotosRunnable);
    paramArrayList1 = new Runnable(paramInt1, paramArrayList1, paramInteger1, paramArrayList2, paramInteger2, paramAlbumEntry)
    {
      public void run()
      {
        if (PhotoViewer.getInstance().isVisible())
        {
          MediaController.access$6000(this.val$guid, this.val$albumsSorted, this.val$cameraAlbumIdFinal, this.val$videoAlbumsSorted, this.val$cameraAlbumVideoIdFinal, this.val$allPhotosAlbumFinal, 1000);
          return;
        }
        MediaController.access$6102(null);
        MediaController.allPhotosAlbumEntry = this.val$allPhotosAlbumFinal;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.albumsDidLoaded, new Object[] { Integer.valueOf(this.val$guid), this.val$albumsSorted, this.val$cameraAlbumIdFinal, this.val$videoAlbumsSorted, this.val$cameraAlbumVideoIdFinal });
      }
    };
    broadcastPhotosRunnable = paramArrayList1;
    AndroidUtilities.runOnUIThread(paramArrayList1, paramInt2);
  }

  private void buildShuffledPlayList()
  {
    if (this.playlist.isEmpty());
    while (true)
    {
      return;
      ArrayList localArrayList = new ArrayList(this.playlist);
      this.shuffledPlaylist.clear();
      MessageObject localMessageObject = (MessageObject)this.playlist.get(this.currentPlaylistNum);
      localArrayList.remove(this.currentPlaylistNum);
      this.shuffledPlaylist.add(localMessageObject);
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        int k = Utilities.random.nextInt(localArrayList.size());
        this.shuffledPlaylist.add(localArrayList.get(k));
        localArrayList.remove(k);
        i += 1;
      }
    }
  }

  private void checkAudioFocus(MessageObject paramMessageObject)
  {
    if (paramMessageObject.isVoice())
      if (this.useFrontSpeaker)
        i = 3;
    while (true)
    {
      if (this.hasAudioFocus != i)
      {
        this.hasAudioFocus = i;
        if (i != 3)
          break;
        i = NotificationsController.getInstance().audioManager.requestAudioFocus(this, 0, 1);
        if (i == 1)
          this.audioFocus = 2;
      }
      return;
      i = 2;
      continue;
      i = 1;
    }
    paramMessageObject = NotificationsController.getInstance().audioManager;
    if (i == 2);
    for (int i = 3; ; i = 1)
    {
      i = paramMessageObject.requestAudioFocus(this, 3, i);
      break;
    }
  }

  private void checkConversionCanceled()
  {
    synchronized (this.videoConvertSync)
    {
      boolean bool = this.cancelCurrentVideoConversion;
      if (bool)
        throw new RuntimeException("canceled conversion");
    }
  }

  private void checkDecoderQueue()
  {
    this.fileDecodingQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (MediaController.this.decodingFinished)
          MediaController.this.checkPlayerQueue();
        while (true)
        {
          return;
          int i = 0;
          while (true)
          {
            MediaController.AudioBuffer localAudioBuffer = null;
            synchronized (MediaController.this.playerSync)
            {
              if (!MediaController.this.freePlayerBuffers.isEmpty())
              {
                localAudioBuffer = (MediaController.AudioBuffer)MediaController.this.freePlayerBuffers.get(0);
                MediaController.this.freePlayerBuffers.remove(0);
              }
              if (!MediaController.this.usedPlayerBuffers.isEmpty())
                i = 1;
              if (localAudioBuffer == null)
                break label249;
              MediaController.this.readOpusFile(localAudioBuffer.buffer, MediaController.this.playerBufferSize, MediaController.readArgs);
              localAudioBuffer.size = MediaController.readArgs[0];
              localAudioBuffer.pcmOffset = MediaController.readArgs[1];
              localAudioBuffer.finished = MediaController.readArgs[2];
              if (localAudioBuffer.finished == 1)
                MediaController.access$3202(MediaController.this, true);
              if (localAudioBuffer.size == 0)
                break;
              localAudioBuffer.buffer.rewind();
              localAudioBuffer.buffer.get(localAudioBuffer.bufferBytes);
            }
            synchronized (MediaController.this.playerSync)
            {
              MediaController.this.usedPlayerBuffers.add(localAudioBuffer);
              i = 1;
              continue;
              localObject1 = finally;
              throw localObject1;
            }
          }
          synchronized (MediaController.this.playerSync)
          {
            MediaController.this.freePlayerBuffers.add(localObject2);
            label249: if (i == 0)
              continue;
            MediaController.this.checkPlayerQueue();
            return;
          }
        }
      }
    });
  }

  private void checkDownloadFinished(String paramString, int paramInt)
  {
    DownloadObject localDownloadObject = (DownloadObject)this.downloadQueueKeys.get(paramString);
    if (localDownloadObject != null)
    {
      this.downloadQueueKeys.remove(paramString);
      if ((paramInt == 0) || (paramInt == 2))
        MessagesStorage.getInstance().removeFromDownloadQueue(localDownloadObject.id, localDownloadObject.type, false);
      if (localDownloadObject.type != 1)
        break label82;
      this.photoDownloadQueue.remove(localDownloadObject);
      if (this.photoDownloadQueue.isEmpty())
        newDownloadObjectsAvailable(1);
    }
    label82: 
    do
    {
      do
        while (true)
        {
          return;
          if (localDownloadObject.type == 2)
          {
            this.audioDownloadQueue.remove(localDownloadObject);
            if (!this.audioDownloadQueue.isEmpty())
              continue;
            newDownloadObjectsAvailable(2);
            return;
          }
          if (localDownloadObject.type == 4)
          {
            this.videoDownloadQueue.remove(localDownloadObject);
            if (!this.videoDownloadQueue.isEmpty())
              continue;
            newDownloadObjectsAvailable(4);
            return;
          }
          if (localDownloadObject.type == 8)
          {
            this.documentDownloadQueue.remove(localDownloadObject);
            if (!this.documentDownloadQueue.isEmpty())
              continue;
            newDownloadObjectsAvailable(8);
            return;
          }
          if (localDownloadObject.type != 16)
            break;
          this.musicDownloadQueue.remove(localDownloadObject);
          if (!this.musicDownloadQueue.isEmpty())
            continue;
          newDownloadObjectsAvailable(16);
          return;
        }
      while (localDownloadObject.type != 32);
      this.gifDownloadQueue.remove(localDownloadObject);
    }
    while (!this.gifDownloadQueue.isEmpty());
    newDownloadObjectsAvailable(32);
  }

  private void checkIsNextMusicFileDownloaded()
  {
    Object localObject2 = null;
    if ((getCurrentDownloadMask() & 0x10) == 0)
      return;
    Object localObject1;
    label26: MessageObject localMessageObject;
    if (this.shuffleMusic)
    {
      localObject1 = this.shuffledPlaylist;
      if ((localObject1 == null) || (((ArrayList)localObject1).size() < 2))
        break label191;
      int j = this.currentPlaylistNum + 1;
      int i = j;
      if (j >= ((ArrayList)localObject1).size())
        i = 0;
      localMessageObject = (MessageObject)((ArrayList)localObject1).get(i);
      localObject1 = localObject2;
      if (localMessageObject.messageOwner.attachPath != null)
      {
        localObject1 = localObject2;
        if (localMessageObject.messageOwner.attachPath.length() > 0)
        {
          localObject1 = new File(localMessageObject.messageOwner.attachPath);
          if (((File)localObject1).exists())
            break label206;
          localObject1 = localObject2;
        }
      }
    }
    label191: label204: label206: 
    while (true)
    {
      if (localObject1 != null);
      for (localObject2 = localObject1; ; localObject2 = FileLoader.getPathToMessage(localMessageObject.messageOwner))
      {
        if (((localObject2 != null) && (((File)localObject2).exists())) && ((localObject2 == null) || (localObject2 == localObject1) || (((File)localObject2).exists()) || (!localMessageObject.isMusic())))
          break label204;
        FileLoader.getInstance().loadFile(localMessageObject.getDocument(), false, false);
        return;
        localObject1 = this.playlist;
        break label26;
        break;
      }
      break;
    }
  }

  private void checkPlayerQueue()
  {
    this.playerQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        while (true)
        {
          synchronized (MediaController.this.playerObjectSync)
          {
            if ((MediaController.this.audioTrackPlayer == null) || (MediaController.this.audioTrackPlayer.getPlayState() != 3))
              return;
            synchronized (MediaController.this.playerSync)
            {
              if (MediaController.this.usedPlayerBuffers.isEmpty())
                break label290;
              ??? = (MediaController.AudioBuffer)MediaController.this.usedPlayerBuffers.get(0);
              MediaController.this.usedPlayerBuffers.remove(0);
              if (??? == null);
            }
          }
          try
          {
            i = MediaController.this.audioTrackPlayer.write(((MediaController.AudioBuffer)???).bufferBytes, 0, ((MediaController.AudioBuffer)???).size);
            MediaController.access$4008(MediaController.this);
            if (i > 0)
            {
              long l = ((MediaController.AudioBuffer)???).pcmOffset;
              if (((MediaController.AudioBuffer)???).finished == 1)
                AndroidUtilities.runOnUIThread(new Runnable(l, i, MediaController.this.buffersWrited)
                {
                  public void run()
                  {
                    MediaController.access$2802(MediaController.this, this.val$pcm);
                    if (this.val$marker != -1)
                    {
                      if (MediaController.this.audioTrackPlayer != null)
                        MediaController.this.audioTrackPlayer.setNotificationMarkerPosition(1);
                      if (this.val$finalBuffersWrited == 1)
                        MediaController.this.cleanupPlayer(true, true, true);
                    }
                  }
                });
            }
            else
            {
              if (((MediaController.AudioBuffer)???).finished != 1)
                MediaController.this.checkPlayerQueue();
              if ((??? == null) || ((??? != null) && (((MediaController.AudioBuffer)???).finished != 1)))
                MediaController.this.checkDecoderQueue();
              if (??? == null)
                break;
              synchronized (MediaController.this.playerSync)
              {
                MediaController.this.freePlayerBuffers.add(???);
                return;
              }
              localObject6 = finally;
              monitorexit;
              throw localObject6;
              localObject3 = finally;
              monitorexit;
              throw localObject3;
            }
          }
          catch (Exception localException)
          {
            while (true)
            {
              FileLog.e(localException);
              int i = 0;
              continue;
              i = -1;
            }
          }
          label290: Object localObject4 = null;
        }
      }
    });
  }

  private void checkScreenshots(ArrayList<Long> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()) || (this.lastSecretChatEnterTime == 0L) || (this.lastSecretChat == null) || (!(this.lastSecretChat instanceof TLRPC.TL_encryptedChat)));
    label162: label163: 
    while (true)
    {
      return;
      paramArrayList = paramArrayList.iterator();
      for (int i = 0; paramArrayList.hasNext(); i = 1)
      {
        label45: Long localLong = (Long)paramArrayList.next();
        if ((this.lastMediaCheckTime != 0L) && (localLong.longValue() <= this.lastMediaCheckTime))
          break label45;
        if ((localLong.longValue() < this.lastSecretChatEnterTime) || ((this.lastSecretChatLeaveTime != 0L) && (localLong.longValue() > this.lastSecretChatLeaveTime + 2000L)))
          break label162;
        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, localLong.longValue());
      }
      while (true)
      {
        break;
        if (i == 0)
          break label163;
        SecretChatHelper.getInstance().sendScreenshotMessage(this.lastSecretChat, this.lastSecretChatVisibleMessages, null);
        return;
      }
    }
  }

  private native void closeOpusFile();

  // ERROR //
  @TargetApi(16)
  private boolean convertVideo(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   4: getfield 1136	org/vidogram/messenger/VideoEditedInfo:originalPath	Ljava/lang/String;
    //   7: astore 29
    //   9: aload_1
    //   10: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   13: getfield 1139	org/vidogram/messenger/VideoEditedInfo:startTime	J
    //   16: lstore 21
    //   18: aload_1
    //   19: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   22: getfield 1142	org/vidogram/messenger/VideoEditedInfo:endTime	J
    //   25: lstore 23
    //   27: aload_1
    //   28: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   31: getfield 1145	org/vidogram/messenger/VideoEditedInfo:resultWidth	I
    //   34: istore_3
    //   35: aload_1
    //   36: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   39: getfield 1148	org/vidogram/messenger/VideoEditedInfo:resultHeight	I
    //   42: istore_2
    //   43: aload_1
    //   44: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   47: getfield 1151	org/vidogram/messenger/VideoEditedInfo:rotationValue	I
    //   50: istore 4
    //   52: aload_1
    //   53: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   56: getfield 1154	org/vidogram/messenger/VideoEditedInfo:originalWidth	I
    //   59: istore 6
    //   61: aload_1
    //   62: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   65: getfield 1157	org/vidogram/messenger/VideoEditedInfo:originalHeight	I
    //   68: istore 9
    //   70: aload_1
    //   71: getfield 1131	org/vidogram/messenger/MessageObject:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   74: getfield 1160	org/vidogram/messenger/VideoEditedInfo:bitrate	I
    //   77: istore 16
    //   79: new 1060	java/io/File
    //   82: dup
    //   83: aload_1
    //   84: getfield 1050	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   87: getfield 1055	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   90: invokespecial 1061	java/io/File:<init>	(Ljava/lang/String;)V
    //   93: astore 42
    //   95: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   98: bipush 18
    //   100: if_icmpge +152 -> 252
    //   103: iload_2
    //   104: iload_3
    //   105: if_icmple +147 -> 252
    //   108: iload_3
    //   109: iload 6
    //   111: if_icmpeq +141 -> 252
    //   114: iload_2
    //   115: iload 9
    //   117: if_icmpeq +135 -> 252
    //   120: bipush 90
    //   122: istore 4
    //   124: sipush 270
    //   127: istore 5
    //   129: iload_2
    //   130: istore 8
    //   132: iload_3
    //   133: istore 7
    //   135: iload 5
    //   137: istore_3
    //   138: iload 4
    //   140: istore_2
    //   141: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   144: ldc_w 1162
    //   147: iconst_0
    //   148: invokevirtual 631	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   151: astore 43
    //   153: new 1060	java/io/File
    //   156: dup
    //   157: aload 29
    //   159: invokespecial 1061	java/io/File:<init>	(Ljava/lang/String;)V
    //   162: astore 30
    //   164: aload_1
    //   165: invokevirtual 1165	org/vidogram/messenger/MessageObject:getId	()I
    //   168: ifeq +159 -> 327
    //   171: aload 43
    //   173: ldc_w 1167
    //   176: iconst_1
    //   177: invokeinterface 646 3 0
    //   182: istore 27
    //   184: aload 43
    //   186: invokeinterface 1171 1 0
    //   191: ldc_w 1167
    //   194: iconst_0
    //   195: invokeinterface 1177 3 0
    //   200: invokeinterface 1180 1 0
    //   205: pop
    //   206: aload 30
    //   208: invokevirtual 1183	java/io/File:canRead	()Z
    //   211: ifeq +8 -> 219
    //   214: iload 27
    //   216: ifne +111 -> 327
    //   219: aload_0
    //   220: aload_1
    //   221: aload 42
    //   223: iconst_1
    //   224: iconst_1
    //   225: invokespecial 1187	org/vidogram/messenger/MediaController:didWriteData	(Lorg/vidogram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   228: aload 43
    //   230: invokeinterface 1171 1 0
    //   235: ldc_w 1167
    //   238: iconst_1
    //   239: invokeinterface 1177 3 0
    //   244: invokeinterface 1180 1 0
    //   249: pop
    //   250: iconst_0
    //   251: ireturn
    //   252: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   255: bipush 20
    //   257: if_icmple +3033 -> 3290
    //   260: iload 4
    //   262: bipush 90
    //   264: if_icmpne +18 -> 282
    //   267: iload_3
    //   268: istore 7
    //   270: iload_2
    //   271: istore 8
    //   273: iconst_0
    //   274: istore_2
    //   275: sipush 270
    //   278: istore_3
    //   279: goto -138 -> 141
    //   282: iload 4
    //   284: sipush 180
    //   287: if_icmpne +18 -> 305
    //   290: iload_2
    //   291: istore 7
    //   293: iload_3
    //   294: istore 8
    //   296: iconst_0
    //   297: istore_2
    //   298: sipush 180
    //   301: istore_3
    //   302: goto -161 -> 141
    //   305: iload 4
    //   307: sipush 270
    //   310: if_icmpne +2980 -> 3290
    //   313: iload_3
    //   314: istore 7
    //   316: iload_2
    //   317: istore 8
    //   319: iconst_0
    //   320: istore_2
    //   321: bipush 90
    //   323: istore_3
    //   324: goto -183 -> 141
    //   327: aload_0
    //   328: iconst_1
    //   329: putfield 400	org/vidogram/messenger/MediaController:videoConvertFirstWrite	Z
    //   332: invokestatic 1192	java/lang/System:currentTimeMillis	()J
    //   335: lstore 25
    //   337: iload 8
    //   339: ifeq +2745 -> 3084
    //   342: iload 7
    //   344: ifeq +2740 -> 3084
    //   347: aconst_null
    //   348: astore 29
    //   350: aconst_null
    //   351: astore 31
    //   353: new 1194	android/media/MediaCodec$BufferInfo
    //   356: dup
    //   357: invokespecial 1195	android/media/MediaCodec$BufferInfo:<init>	()V
    //   360: astore 44
    //   362: new 1197	org/vidogram/messenger/video/Mp4Movie
    //   365: dup
    //   366: invokespecial 1198	org/vidogram/messenger/video/Mp4Movie:<init>	()V
    //   369: astore 32
    //   371: aload 32
    //   373: aload 42
    //   375: invokevirtual 1202	org/vidogram/messenger/video/Mp4Movie:setCacheFile	(Ljava/io/File;)V
    //   378: aload 32
    //   380: iload_2
    //   381: invokevirtual 1205	org/vidogram/messenger/video/Mp4Movie:setRotation	(I)V
    //   384: aload 32
    //   386: iload 8
    //   388: iload 7
    //   390: invokevirtual 1209	org/vidogram/messenger/video/Mp4Movie:setSize	(II)V
    //   393: new 1211	org/vidogram/messenger/video/MP4Builder
    //   396: dup
    //   397: invokespecial 1212	org/vidogram/messenger/video/MP4Builder:<init>	()V
    //   400: aload 32
    //   402: invokevirtual 1216	org/vidogram/messenger/video/MP4Builder:createMovie	(Lorg/vidogram/messenger/video/Mp4Movie;)Lorg/vidogram/messenger/video/MP4Builder;
    //   405: astore 33
    //   407: new 1218	android/media/MediaExtractor
    //   410: dup
    //   411: invokespecial 1219	android/media/MediaExtractor:<init>	()V
    //   414: astore 37
    //   416: aload 37
    //   418: aload 30
    //   420: invokevirtual 1223	java/io/File:toString	()Ljava/lang/String;
    //   423: invokevirtual 1226	android/media/MediaExtractor:setDataSource	(Ljava/lang/String;)V
    //   426: aload_0
    //   427: invokespecial 1228	org/vidogram/messenger/MediaController:checkConversionCanceled	()V
    //   430: iload 8
    //   432: iload 6
    //   434: if_icmpne +14 -> 448
    //   437: iload 7
    //   439: iload 9
    //   441: if_icmpne +7 -> 448
    //   444: iload_3
    //   445: ifeq +2512 -> 2957
    //   448: aload_0
    //   449: aload 37
    //   451: iconst_0
    //   452: invokespecial 1232	org/vidogram/messenger/MediaController:selectTrack	(Landroid/media/MediaExtractor;Z)I
    //   455: istore 17
    //   457: iload 17
    //   459: iflt +2825 -> 3284
    //   462: aconst_null
    //   463: astore 30
    //   465: ldc2_w 1233
    //   468: lstore 19
    //   470: iconst_0
    //   471: istore 12
    //   473: iconst_0
    //   474: istore 6
    //   476: iconst_0
    //   477: istore 5
    //   479: iconst_0
    //   480: istore 14
    //   482: iconst_0
    //   483: istore 9
    //   485: bipush 251
    //   487: istore 10
    //   489: iconst_0
    //   490: istore_2
    //   491: aload 30
    //   493: astore 29
    //   495: getstatic 1239	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   498: invokevirtual 1242	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   501: astore 31
    //   503: aload 30
    //   505: astore 29
    //   507: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   510: bipush 18
    //   512: if_icmpge +2861 -> 3373
    //   515: aload 30
    //   517: astore 29
    //   519: ldc 157
    //   521: invokestatic 1246	org/vidogram/messenger/MediaController:selectCodec	(Ljava/lang/String;)Landroid/media/MediaCodecInfo;
    //   524: astore 32
    //   526: aload 30
    //   528: astore 29
    //   530: aload 32
    //   532: ldc 157
    //   534: invokestatic 1250	org/vidogram/messenger/MediaController:selectColorFormat	(Landroid/media/MediaCodecInfo;Ljava/lang/String;)I
    //   537: istore 15
    //   539: iload 15
    //   541: ifne +234 -> 775
    //   544: aload 30
    //   546: astore 29
    //   548: new 1006	java/lang/RuntimeException
    //   551: dup
    //   552: ldc_w 1252
    //   555: invokespecial 1009	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   558: athrow
    //   559: astore 32
    //   561: aconst_null
    //   562: astore 35
    //   564: aconst_null
    //   565: astore 31
    //   567: aconst_null
    //   568: astore 30
    //   570: aload 29
    //   572: astore 34
    //   574: aload 35
    //   576: astore 29
    //   578: aload 32
    //   580: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   583: iconst_1
    //   584: istore 27
    //   586: aload 31
    //   588: astore 32
    //   590: aload 29
    //   592: astore 31
    //   594: aload 30
    //   596: astore 29
    //   598: aload 37
    //   600: iload 17
    //   602: invokevirtual 1255	android/media/MediaExtractor:unselectTrack	(I)V
    //   605: aload 31
    //   607: ifnull +8 -> 615
    //   610: aload 31
    //   612: invokevirtual 1260	org/vidogram/messenger/video/OutputSurface:release	()V
    //   615: aload 32
    //   617: ifnull +8 -> 625
    //   620: aload 32
    //   622: invokevirtual 1263	org/vidogram/messenger/video/InputSurface:release	()V
    //   625: aload 29
    //   627: ifnull +13 -> 640
    //   630: aload 29
    //   632: invokevirtual 1268	android/media/MediaCodec:stop	()V
    //   635: aload 29
    //   637: invokevirtual 1269	android/media/MediaCodec:release	()V
    //   640: aload 34
    //   642: ifnull +13 -> 655
    //   645: aload 34
    //   647: invokevirtual 1268	android/media/MediaCodec:stop	()V
    //   650: aload 34
    //   652: invokevirtual 1269	android/media/MediaCodec:release	()V
    //   655: aload_0
    //   656: invokespecial 1228	org/vidogram/messenger/MediaController:checkConversionCanceled	()V
    //   659: goto +2645 -> 3304
    //   662: iload 27
    //   664: ifne +28 -> 692
    //   667: iload 16
    //   669: iconst_m1
    //   670: if_icmpeq +22 -> 692
    //   673: aload_0
    //   674: aload_1
    //   675: aload 37
    //   677: aload 33
    //   679: aload 44
    //   681: lload 19
    //   683: lload 23
    //   685: aload 42
    //   687: iconst_1
    //   688: invokespecial 1273	org/vidogram/messenger/MediaController:readAndWriteTrack	(Lorg/vidogram/messenger/MessageObject;Landroid/media/MediaExtractor;Lorg/vidogram/messenger/video/MP4Builder;Landroid/media/MediaCodec$BufferInfo;JJLjava/io/File;Z)J
    //   691: pop2
    //   692: aload 37
    //   694: ifnull +8 -> 702
    //   697: aload 37
    //   699: invokevirtual 1274	android/media/MediaExtractor:release	()V
    //   702: aload 33
    //   704: ifnull +9 -> 713
    //   707: aload 33
    //   709: iconst_0
    //   710: invokevirtual 1277	org/vidogram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   713: new 1279	java/lang/StringBuilder
    //   716: dup
    //   717: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   720: ldc_w 1282
    //   723: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   726: invokestatic 1192	java/lang/System:currentTimeMillis	()J
    //   729: lload 25
    //   731: lsub
    //   732: invokevirtual 1289	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   735: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   738: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   741: aload 43
    //   743: invokeinterface 1171 1 0
    //   748: ldc_w 1167
    //   751: iconst_1
    //   752: invokeinterface 1177 3 0
    //   757: invokeinterface 1180 1 0
    //   762: pop
    //   763: aload_0
    //   764: aload_1
    //   765: aload 42
    //   767: iconst_1
    //   768: iload 27
    //   770: invokespecial 1187	org/vidogram/messenger/MediaController:didWriteData	(Lorg/vidogram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   773: iconst_1
    //   774: ireturn
    //   775: aload 30
    //   777: astore 29
    //   779: aload 32
    //   781: invokevirtual 1295	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   784: astore 34
    //   786: aload 30
    //   788: astore 29
    //   790: aload 34
    //   792: ldc_w 1297
    //   795: invokevirtual 1301	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   798: ifeq +719 -> 1517
    //   801: iconst_1
    //   802: istore 11
    //   804: aload 30
    //   806: astore 29
    //   808: iload 11
    //   810: istore_2
    //   811: iload 9
    //   813: istore 4
    //   815: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   818: bipush 16
    //   820: if_icmpne +43 -> 863
    //   823: aload 30
    //   825: astore 29
    //   827: aload 31
    //   829: ldc_w 1303
    //   832: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   835: ifne +2476 -> 3311
    //   838: aload 30
    //   840: astore 29
    //   842: iload 11
    //   844: istore_2
    //   845: iload 9
    //   847: istore 4
    //   849: aload 31
    //   851: ldc_w 1308
    //   854: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   857: ifeq +6 -> 863
    //   860: goto +2451 -> 3311
    //   863: aload 30
    //   865: astore 29
    //   867: new 1279	java/lang/StringBuilder
    //   870: dup
    //   871: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   874: ldc_w 1310
    //   877: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   880: aload 32
    //   882: invokevirtual 1295	android/media/MediaCodecInfo:getName	()Ljava/lang/String;
    //   885: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   888: ldc_w 1312
    //   891: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   894: aload 31
    //   896: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   899: ldc_w 1314
    //   902: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   905: getstatic 1317	android/os/Build:MODEL	Ljava/lang/String;
    //   908: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   911: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   914: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   917: iload 4
    //   919: istore 14
    //   921: aload 30
    //   923: astore 29
    //   925: new 1279	java/lang/StringBuilder
    //   928: dup
    //   929: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   932: ldc_w 1319
    //   935: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   938: iload 15
    //   940: invokevirtual 1322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   943: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   946: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   949: aload 30
    //   951: astore 29
    //   953: iload 8
    //   955: iload 7
    //   957: imul
    //   958: iconst_3
    //   959: imul
    //   960: iconst_2
    //   961: idiv
    //   962: istore 11
    //   964: iload_2
    //   965: ifne +651 -> 1616
    //   968: iload 7
    //   970: bipush 16
    //   972: irem
    //   973: ifeq +2305 -> 3278
    //   976: bipush 16
    //   978: iload 7
    //   980: bipush 16
    //   982: irem
    //   983: isub
    //   984: iload 7
    //   986: iadd
    //   987: iload 7
    //   989: isub
    //   990: iload 8
    //   992: imul
    //   993: istore 9
    //   995: aload 30
    //   997: astore 29
    //   999: iload 11
    //   1001: iload 9
    //   1003: iconst_5
    //   1004: imul
    //   1005: iconst_4
    //   1006: idiv
    //   1007: iadd
    //   1008: istore 11
    //   1010: aload 30
    //   1012: astore 29
    //   1014: aload 37
    //   1016: iload 17
    //   1018: invokevirtual 1324	android/media/MediaExtractor:selectTrack	(I)V
    //   1021: lload 21
    //   1023: lconst_0
    //   1024: lcmp
    //   1025: ifle +702 -> 1727
    //   1028: aload 30
    //   1030: astore 29
    //   1032: aload 37
    //   1034: lload 21
    //   1036: iconst_0
    //   1037: invokevirtual 1328	android/media/MediaExtractor:seekTo	(JI)V
    //   1040: aload 30
    //   1042: astore 29
    //   1044: aload 37
    //   1046: iload 17
    //   1048: invokevirtual 1332	android/media/MediaExtractor:getTrackFormat	(I)Landroid/media/MediaFormat;
    //   1051: astore 35
    //   1053: aload 30
    //   1055: astore 29
    //   1057: ldc 157
    //   1059: iload 8
    //   1061: iload 7
    //   1063: invokestatic 1338	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   1066: astore 31
    //   1068: aload 30
    //   1070: astore 29
    //   1072: aload 31
    //   1074: ldc_w 1340
    //   1077: iload 15
    //   1079: invokevirtual 1343	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1082: iload 16
    //   1084: ifle +713 -> 1797
    //   1087: iload 16
    //   1089: istore_2
    //   1090: aload 30
    //   1092: astore 29
    //   1094: aload 31
    //   1096: ldc_w 1344
    //   1099: iload_2
    //   1100: invokevirtual 1343	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1103: aload 30
    //   1105: astore 29
    //   1107: aload 31
    //   1109: ldc_w 1346
    //   1112: bipush 25
    //   1114: invokevirtual 1343	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1117: aload 30
    //   1119: astore 29
    //   1121: aload 31
    //   1123: ldc_w 1348
    //   1126: bipush 10
    //   1128: invokevirtual 1343	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1131: aload 30
    //   1133: astore 29
    //   1135: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1138: bipush 18
    //   1140: if_icmpge +34 -> 1174
    //   1143: aload 30
    //   1145: astore 29
    //   1147: aload 31
    //   1149: ldc_w 1350
    //   1152: iload 8
    //   1154: bipush 32
    //   1156: iadd
    //   1157: invokevirtual 1343	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1160: aload 30
    //   1162: astore 29
    //   1164: aload 31
    //   1166: ldc_w 1352
    //   1169: iload 7
    //   1171: invokevirtual 1343	android/media/MediaFormat:setInteger	(Ljava/lang/String;I)V
    //   1174: aload 30
    //   1176: astore 29
    //   1178: ldc 157
    //   1180: invokestatic 1356	android/media/MediaCodec:createEncoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   1183: astore 34
    //   1185: aload 34
    //   1187: astore 29
    //   1189: aload 34
    //   1191: aload 31
    //   1193: aconst_null
    //   1194: aconst_null
    //   1195: iconst_1
    //   1196: invokevirtual 1360	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   1199: aload 34
    //   1201: astore 29
    //   1203: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1206: bipush 18
    //   1208: if_icmplt +2064 -> 3272
    //   1211: aload 34
    //   1213: astore 29
    //   1215: new 1262	org/vidogram/messenger/video/InputSurface
    //   1218: dup
    //   1219: aload 34
    //   1221: invokevirtual 1364	android/media/MediaCodec:createInputSurface	()Landroid/view/Surface;
    //   1224: invokespecial 1367	org/vidogram/messenger/video/InputSurface:<init>	(Landroid/view/Surface;)V
    //   1227: astore 30
    //   1229: aload 30
    //   1231: invokevirtual 1370	org/vidogram/messenger/video/InputSurface:makeCurrent	()V
    //   1234: aload 34
    //   1236: invokevirtual 1373	android/media/MediaCodec:start	()V
    //   1239: aload 35
    //   1241: ldc_w 1375
    //   1244: invokevirtual 1379	android/media/MediaFormat:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   1247: invokestatic 1382	android/media/MediaCodec:createDecoderByType	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   1250: astore 32
    //   1252: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1255: bipush 18
    //   1257: if_icmplt +547 -> 1804
    //   1260: new 1257	org/vidogram/messenger/video/OutputSurface
    //   1263: dup
    //   1264: invokespecial 1383	org/vidogram/messenger/video/OutputSurface:<init>	()V
    //   1267: astore 31
    //   1269: aload 32
    //   1271: aload 35
    //   1273: aload 31
    //   1275: invokevirtual 1386	org/vidogram/messenger/video/OutputSurface:getSurface	()Landroid/view/Surface;
    //   1278: aconst_null
    //   1279: iconst_0
    //   1280: invokevirtual 1360	android/media/MediaCodec:configure	(Landroid/media/MediaFormat;Landroid/view/Surface;Landroid/media/MediaCrypto;I)V
    //   1283: aload 32
    //   1285: invokevirtual 1373	android/media/MediaCodec:start	()V
    //   1288: aconst_null
    //   1289: astore 29
    //   1291: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1294: bipush 21
    //   1296: if_icmpge +1967 -> 3263
    //   1299: aload 32
    //   1301: invokevirtual 1390	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   1304: astore 36
    //   1306: aload 34
    //   1308: invokevirtual 1393	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   1311: astore 29
    //   1313: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1316: bipush 18
    //   1318: if_icmpge +1939 -> 3257
    //   1321: aload 34
    //   1323: invokevirtual 1390	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   1326: astore 35
    //   1328: aload_0
    //   1329: invokespecial 1228	org/vidogram/messenger/MediaController:checkConversionCanceled	()V
    //   1332: iload 12
    //   1334: istore 4
    //   1336: iload 10
    //   1338: istore_3
    //   1339: iload 6
    //   1341: istore_2
    //   1342: iload 4
    //   1344: ifne +1586 -> 2930
    //   1347: aload_0
    //   1348: invokespecial 1228	org/vidogram/messenger/MediaController:checkConversionCanceled	()V
    //   1351: iload_2
    //   1352: ifne +1899 -> 3251
    //   1355: aload 37
    //   1357: invokevirtual 1396	android/media/MediaExtractor:getSampleTrackIndex	()I
    //   1360: istore 6
    //   1362: iload 6
    //   1364: iload 17
    //   1366: if_icmpne +2031 -> 3397
    //   1369: aload 32
    //   1371: ldc2_w 1397
    //   1374: invokevirtual 1402	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   1377: istore 6
    //   1379: iload 6
    //   1381: iflt +2013 -> 3394
    //   1384: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1387: bipush 21
    //   1389: if_icmpge +456 -> 1845
    //   1392: aload 36
    //   1394: iload 6
    //   1396: aaload
    //   1397: astore 38
    //   1399: aload 37
    //   1401: aload 38
    //   1403: iconst_0
    //   1404: invokevirtual 1405	android/media/MediaExtractor:readSampleData	(Ljava/nio/ByteBuffer;I)I
    //   1407: istore 10
    //   1409: iload 10
    //   1411: ifge +446 -> 1857
    //   1414: aload 32
    //   1416: iload 6
    //   1418: iconst_0
    //   1419: iconst_0
    //   1420: lconst_0
    //   1421: iconst_4
    //   1422: invokevirtual 1409	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   1425: iconst_1
    //   1426: istore_2
    //   1427: goto +1893 -> 3320
    //   1430: iload_2
    //   1431: istore 12
    //   1433: iload 6
    //   1435: ifeq +1891 -> 3326
    //   1438: aload 32
    //   1440: ldc2_w 1397
    //   1443: invokevirtual 1402	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   1446: istore 6
    //   1448: iload_2
    //   1449: istore 12
    //   1451: iload 6
    //   1453: iflt +1873 -> 3326
    //   1456: aload 32
    //   1458: iload 6
    //   1460: iconst_0
    //   1461: iconst_0
    //   1462: lconst_0
    //   1463: iconst_4
    //   1464: invokevirtual 1409	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   1467: iconst_1
    //   1468: istore 12
    //   1470: goto +1856 -> 3326
    //   1473: aload_0
    //   1474: invokespecial 1228	org/vidogram/messenger/MediaController:checkConversionCanceled	()V
    //   1477: aload 34
    //   1479: aload 44
    //   1481: ldc2_w 1397
    //   1484: invokevirtual 1413	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   1487: istore 18
    //   1489: iload 18
    //   1491: iconst_m1
    //   1492: if_icmpne +390 -> 1882
    //   1495: iconst_0
    //   1496: istore 5
    //   1498: iload_3
    //   1499: istore 6
    //   1501: iload_2
    //   1502: istore_3
    //   1503: iload 18
    //   1505: iconst_m1
    //   1506: if_icmpeq +868 -> 2374
    //   1509: iload_3
    //   1510: istore_2
    //   1511: iload 6
    //   1513: istore_3
    //   1514: goto +1846 -> 3360
    //   1517: aload 30
    //   1519: astore 29
    //   1521: aload 34
    //   1523: ldc_w 1415
    //   1526: invokevirtual 1301	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   1529: ifeq +12 -> 1541
    //   1532: iconst_2
    //   1533: istore_2
    //   1534: iload 9
    //   1536: istore 4
    //   1538: goto -675 -> 863
    //   1541: aload 30
    //   1543: astore 29
    //   1545: aload 34
    //   1547: ldc_w 1417
    //   1550: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1553: ifeq +12 -> 1565
    //   1556: iconst_3
    //   1557: istore_2
    //   1558: iload 9
    //   1560: istore 4
    //   1562: goto -699 -> 863
    //   1565: aload 30
    //   1567: astore 29
    //   1569: aload 34
    //   1571: ldc_w 1419
    //   1574: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1577: ifeq +11 -> 1588
    //   1580: iconst_4
    //   1581: istore_2
    //   1582: iconst_1
    //   1583: istore 4
    //   1585: goto -722 -> 863
    //   1588: aload 30
    //   1590: astore 29
    //   1592: iload 9
    //   1594: istore 4
    //   1596: aload 34
    //   1598: ldc_w 1421
    //   1601: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1604: ifeq -741 -> 863
    //   1607: iconst_5
    //   1608: istore_2
    //   1609: iload 9
    //   1611: istore 4
    //   1613: goto -750 -> 863
    //   1616: iload_2
    //   1617: iconst_1
    //   1618: if_icmpne +1765 -> 3383
    //   1621: aload 30
    //   1623: astore 29
    //   1625: aload 31
    //   1627: invokevirtual 1242	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1630: ldc_w 1303
    //   1633: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1636: ifne +1642 -> 3278
    //   1639: iload 8
    //   1641: iload 7
    //   1643: imul
    //   1644: sipush 2047
    //   1647: iadd
    //   1648: sipush -2048
    //   1651: iand
    //   1652: iload 8
    //   1654: iload 7
    //   1656: imul
    //   1657: isub
    //   1658: istore 9
    //   1660: iload 11
    //   1662: iload 9
    //   1664: iadd
    //   1665: istore 11
    //   1667: goto -657 -> 1010
    //   1670: iload_2
    //   1671: iconst_3
    //   1672: if_icmpne +1606 -> 3278
    //   1675: aload 30
    //   1677: astore 29
    //   1679: aload 31
    //   1681: ldc_w 1423
    //   1684: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1687: ifeq +1591 -> 3278
    //   1690: bipush 16
    //   1692: iload 7
    //   1694: bipush 16
    //   1696: irem
    //   1697: isub
    //   1698: iload 7
    //   1700: iadd
    //   1701: iload 7
    //   1703: isub
    //   1704: iload 8
    //   1706: imul
    //   1707: istore 9
    //   1709: aload 30
    //   1711: astore 29
    //   1713: iload 11
    //   1715: iload 9
    //   1717: iconst_5
    //   1718: imul
    //   1719: iconst_4
    //   1720: idiv
    //   1721: iadd
    //   1722: istore 11
    //   1724: goto -714 -> 1010
    //   1727: aload 30
    //   1729: astore 29
    //   1731: aload 37
    //   1733: lconst_0
    //   1734: iconst_0
    //   1735: invokevirtual 1328	android/media/MediaExtractor:seekTo	(JI)V
    //   1738: goto -698 -> 1040
    //   1741: astore_1
    //   1742: aload 37
    //   1744: astore 29
    //   1746: aload 29
    //   1748: ifnull +8 -> 1756
    //   1751: aload 29
    //   1753: invokevirtual 1274	android/media/MediaExtractor:release	()V
    //   1756: aload 33
    //   1758: ifnull +9 -> 1767
    //   1761: aload 33
    //   1763: iconst_0
    //   1764: invokevirtual 1277	org/vidogram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   1767: new 1279	java/lang/StringBuilder
    //   1770: dup
    //   1771: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   1774: ldc_w 1282
    //   1777: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1780: invokestatic 1192	java/lang/System:currentTimeMillis	()J
    //   1783: lload 25
    //   1785: lsub
    //   1786: invokevirtual 1289	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1789: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1792: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   1795: aload_1
    //   1796: athrow
    //   1797: ldc_w 1424
    //   1800: istore_2
    //   1801: goto -711 -> 1090
    //   1804: new 1257	org/vidogram/messenger/video/OutputSurface
    //   1807: dup
    //   1808: iload 8
    //   1810: iload 7
    //   1812: iload_3
    //   1813: invokespecial 1427	org/vidogram/messenger/video/OutputSurface:<init>	(III)V
    //   1816: astore 31
    //   1818: goto -549 -> 1269
    //   1821: astore 29
    //   1823: aload 32
    //   1825: astore 35
    //   1827: aload 29
    //   1829: astore 32
    //   1831: aconst_null
    //   1832: astore 29
    //   1834: aload 30
    //   1836: astore 31
    //   1838: aload 35
    //   1840: astore 30
    //   1842: goto -1264 -> 578
    //   1845: aload 32
    //   1847: iload 6
    //   1849: invokevirtual 1430	android/media/MediaCodec:getInputBuffer	(I)Ljava/nio/ByteBuffer;
    //   1852: astore 38
    //   1854: goto -455 -> 1399
    //   1857: aload 32
    //   1859: iload 6
    //   1861: iconst_0
    //   1862: iload 10
    //   1864: aload 37
    //   1866: invokevirtual 1433	android/media/MediaExtractor:getSampleTime	()J
    //   1869: iconst_0
    //   1870: invokevirtual 1409	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   1873: aload 37
    //   1875: invokevirtual 1436	android/media/MediaExtractor:advance	()Z
    //   1878: pop
    //   1879: goto +1515 -> 3394
    //   1882: iload 18
    //   1884: bipush 253
    //   1886: if_icmpne +32 -> 1918
    //   1889: iload_2
    //   1890: istore 6
    //   1892: iload_3
    //   1893: istore 10
    //   1895: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1898: bipush 21
    //   1900: if_icmpge +1550 -> 3450
    //   1903: aload 34
    //   1905: invokevirtual 1393	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   1908: astore 29
    //   1910: iload_3
    //   1911: istore 6
    //   1913: iload_2
    //   1914: istore_3
    //   1915: goto -412 -> 1503
    //   1918: iload 18
    //   1920: bipush 254
    //   1922: if_icmpne +32 -> 1954
    //   1925: aload 34
    //   1927: invokevirtual 1440	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   1930: astore 38
    //   1932: iload_2
    //   1933: istore 6
    //   1935: iload_2
    //   1936: bipush 251
    //   1938: if_icmpne +1476 -> 3414
    //   1941: aload 33
    //   1943: aload 38
    //   1945: iconst_0
    //   1946: invokevirtual 1444	org/vidogram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   1949: istore 6
    //   1951: goto +1463 -> 3414
    //   1954: iload 18
    //   1956: ifge +32 -> 1988
    //   1959: new 1006	java/lang/RuntimeException
    //   1962: dup
    //   1963: new 1279	java/lang/StringBuilder
    //   1966: dup
    //   1967: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   1970: ldc_w 1446
    //   1973: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1976: iload 18
    //   1978: invokevirtual 1322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1981: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1984: invokespecial 1009	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   1987: athrow
    //   1988: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   1991: bipush 21
    //   1993: if_icmpge +50 -> 2043
    //   1996: aload 29
    //   1998: iload 18
    //   2000: aaload
    //   2001: astore 38
    //   2003: aload 38
    //   2005: ifnonnull +50 -> 2055
    //   2008: new 1006	java/lang/RuntimeException
    //   2011: dup
    //   2012: new 1279	java/lang/StringBuilder
    //   2015: dup
    //   2016: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   2019: ldc_w 1448
    //   2022: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2025: iload 18
    //   2027: invokevirtual 1322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2030: ldc_w 1450
    //   2033: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2036: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2039: invokespecial 1009	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   2042: athrow
    //   2043: aload 34
    //   2045: iload 18
    //   2047: invokevirtual 1453	android/media/MediaCodec:getOutputBuffer	(I)Ljava/nio/ByteBuffer;
    //   2050: astore 38
    //   2052: goto -49 -> 2003
    //   2055: iload_2
    //   2056: istore 6
    //   2058: aload 44
    //   2060: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2063: iconst_1
    //   2064: if_icmple +42 -> 2106
    //   2067: aload 44
    //   2069: getfield 1458	android/media/MediaCodec$BufferInfo:flags	I
    //   2072: iconst_2
    //   2073: iand
    //   2074: ifne +56 -> 2130
    //   2077: iload_2
    //   2078: istore 6
    //   2080: aload 33
    //   2082: iload_2
    //   2083: aload 38
    //   2085: aload 44
    //   2087: iconst_1
    //   2088: invokevirtual 1462	org/vidogram/messenger/video/MP4Builder:writeSampleData	(ILjava/nio/ByteBuffer;Landroid/media/MediaCodec$BufferInfo;Z)Z
    //   2091: ifeq +15 -> 2106
    //   2094: aload_0
    //   2095: aload_1
    //   2096: aload 42
    //   2098: iconst_0
    //   2099: iconst_0
    //   2100: invokespecial 1187	org/vidogram/messenger/MediaController:didWriteData	(Lorg/vidogram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   2103: iload_2
    //   2104: istore 6
    //   2106: aload 44
    //   2108: getfield 1458	android/media/MediaCodec$BufferInfo:flags	I
    //   2111: iconst_4
    //   2112: iand
    //   2113: ifeq +1354 -> 3467
    //   2116: iconst_1
    //   2117: istore 10
    //   2119: aload 34
    //   2121: iload 18
    //   2123: iconst_0
    //   2124: invokevirtual 1466	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   2127: goto +1323 -> 3450
    //   2130: iload_2
    //   2131: istore 6
    //   2133: iload_2
    //   2134: bipush 251
    //   2136: if_icmpne -30 -> 2106
    //   2139: aload 44
    //   2141: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2144: newarray byte
    //   2146: astore 45
    //   2148: aload 38
    //   2150: aload 44
    //   2152: getfield 1469	android/media/MediaCodec$BufferInfo:offset	I
    //   2155: aload 44
    //   2157: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2160: iadd
    //   2161: invokevirtual 1473	java/nio/ByteBuffer:limit	(I)Ljava/nio/Buffer;
    //   2164: pop
    //   2165: aload 38
    //   2167: aload 44
    //   2169: getfield 1469	android/media/MediaCodec$BufferInfo:offset	I
    //   2172: invokevirtual 1476	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   2175: pop
    //   2176: aload 38
    //   2178: aload 45
    //   2180: invokevirtual 1479	java/nio/ByteBuffer:get	([B)Ljava/nio/ByteBuffer;
    //   2183: pop
    //   2184: aconst_null
    //   2185: astore 40
    //   2187: aconst_null
    //   2188: astore 41
    //   2190: aload 44
    //   2192: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2195: iconst_1
    //   2196: isub
    //   2197: istore_2
    //   2198: aload 41
    //   2200: astore 39
    //   2202: aload 40
    //   2204: astore 38
    //   2206: iload_2
    //   2207: iflt +113 -> 2320
    //   2210: aload 41
    //   2212: astore 39
    //   2214: aload 40
    //   2216: astore 38
    //   2218: iload_2
    //   2219: iconst_3
    //   2220: if_icmple +100 -> 2320
    //   2223: aload 45
    //   2225: iload_2
    //   2226: baload
    //   2227: iconst_1
    //   2228: if_icmpne +1232 -> 3460
    //   2231: aload 45
    //   2233: iload_2
    //   2234: iconst_1
    //   2235: isub
    //   2236: baload
    //   2237: ifne +1223 -> 3460
    //   2240: aload 45
    //   2242: iload_2
    //   2243: iconst_2
    //   2244: isub
    //   2245: baload
    //   2246: ifne +1214 -> 3460
    //   2249: aload 45
    //   2251: iload_2
    //   2252: iconst_3
    //   2253: isub
    //   2254: baload
    //   2255: ifne +1205 -> 3460
    //   2258: iload_2
    //   2259: iconst_3
    //   2260: isub
    //   2261: invokestatic 1482	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   2264: astore 38
    //   2266: aload 44
    //   2268: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2271: iload_2
    //   2272: iconst_3
    //   2273: isub
    //   2274: isub
    //   2275: invokestatic 1482	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   2278: astore 39
    //   2280: aload 38
    //   2282: aload 45
    //   2284: iconst_0
    //   2285: iload_2
    //   2286: iconst_3
    //   2287: isub
    //   2288: invokevirtual 1486	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   2291: iconst_0
    //   2292: invokevirtual 1476	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   2295: pop
    //   2296: aload 39
    //   2298: aload 45
    //   2300: iload_2
    //   2301: iconst_3
    //   2302: isub
    //   2303: aload 44
    //   2305: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2308: iload_2
    //   2309: iconst_3
    //   2310: isub
    //   2311: isub
    //   2312: invokevirtual 1486	java/nio/ByteBuffer:put	([BII)Ljava/nio/ByteBuffer;
    //   2315: iconst_0
    //   2316: invokevirtual 1476	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   2319: pop
    //   2320: ldc 157
    //   2322: iload 8
    //   2324: iload 7
    //   2326: invokestatic 1338	android/media/MediaFormat:createVideoFormat	(Ljava/lang/String;II)Landroid/media/MediaFormat;
    //   2329: astore 40
    //   2331: aload 38
    //   2333: ifnull +28 -> 2361
    //   2336: aload 39
    //   2338: ifnull +23 -> 2361
    //   2341: aload 40
    //   2343: ldc_w 1488
    //   2346: aload 38
    //   2348: invokevirtual 1492	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   2351: aload 40
    //   2353: ldc_w 1494
    //   2356: aload 39
    //   2358: invokevirtual 1492	android/media/MediaFormat:setByteBuffer	(Ljava/lang/String;Ljava/nio/ByteBuffer;)V
    //   2361: aload 33
    //   2363: aload 40
    //   2365: iconst_0
    //   2366: invokevirtual 1444	org/vidogram/messenger/video/MP4Builder:addTrack	(Landroid/media/MediaFormat;Z)I
    //   2369: istore 6
    //   2371: goto -265 -> 2106
    //   2374: iload 13
    //   2376: ifne +863 -> 3239
    //   2379: aload 32
    //   2381: aload 44
    //   2383: ldc2_w 1397
    //   2386: invokevirtual 1413	android/media/MediaCodec:dequeueOutputBuffer	(Landroid/media/MediaCodec$BufferInfo;J)I
    //   2389: istore 18
    //   2391: iload 18
    //   2393: iconst_m1
    //   2394: if_icmpne +1090 -> 3484
    //   2397: iconst_0
    //   2398: istore_2
    //   2399: goto +1074 -> 3473
    //   2402: iload 18
    //   2404: bipush 254
    //   2406: if_icmpne +40 -> 2446
    //   2409: aload 32
    //   2411: invokevirtual 1440	android/media/MediaCodec:getOutputFormat	()Landroid/media/MediaFormat;
    //   2414: astore 38
    //   2416: new 1279	java/lang/StringBuilder
    //   2419: dup
    //   2420: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   2423: ldc_w 1496
    //   2426: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2429: aload 38
    //   2431: invokevirtual 1499	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2434: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2437: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   2440: iload 4
    //   2442: istore_2
    //   2443: goto +1030 -> 3473
    //   2446: iload 18
    //   2448: ifge +32 -> 2480
    //   2451: new 1006	java/lang/RuntimeException
    //   2454: dup
    //   2455: new 1279	java/lang/StringBuilder
    //   2458: dup
    //   2459: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   2462: ldc_w 1501
    //   2465: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2468: iload 18
    //   2470: invokevirtual 1322	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2473: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2476: invokespecial 1009	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   2479: athrow
    //   2480: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   2483: bipush 18
    //   2485: if_icmplt +248 -> 2733
    //   2488: aload 44
    //   2490: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2493: ifeq +1004 -> 3497
    //   2496: iconst_1
    //   2497: istore 27
    //   2499: iload 27
    //   2501: istore 28
    //   2503: iload 13
    //   2505: istore 10
    //   2507: iload 12
    //   2509: istore_2
    //   2510: lload 23
    //   2512: lconst_0
    //   2513: lcmp
    //   2514: ifle +45 -> 2559
    //   2517: iload 27
    //   2519: istore 28
    //   2521: iload 13
    //   2523: istore 10
    //   2525: iload 12
    //   2527: istore_2
    //   2528: aload 44
    //   2530: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2533: lload 23
    //   2535: lcmp
    //   2536: iflt +23 -> 2559
    //   2539: iconst_1
    //   2540: istore_2
    //   2541: iconst_1
    //   2542: istore 10
    //   2544: iconst_0
    //   2545: istore 28
    //   2547: aload 44
    //   2549: aload 44
    //   2551: getfield 1458	android/media/MediaCodec$BufferInfo:flags	I
    //   2554: iconst_4
    //   2555: ior
    //   2556: putfield 1458	android/media/MediaCodec$BufferInfo:flags	I
    //   2559: lload 21
    //   2561: lconst_0
    //   2562: lcmp
    //   2563: ifle +673 -> 3236
    //   2566: lload 19
    //   2568: ldc2_w 1233
    //   2571: lcmp
    //   2572: ifne +664 -> 3236
    //   2575: aload 44
    //   2577: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2580: lload 21
    //   2582: lcmp
    //   2583: ifge +171 -> 2754
    //   2586: iconst_0
    //   2587: istore 28
    //   2589: new 1279	java/lang/StringBuilder
    //   2592: dup
    //   2593: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   2596: ldc_w 1506
    //   2599: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2602: lload 21
    //   2604: invokevirtual 1289	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   2607: ldc_w 1508
    //   2610: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2613: aload 44
    //   2615: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2618: invokevirtual 1289	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   2621: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2624: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   2627: aload 32
    //   2629: iload 18
    //   2631: iload 28
    //   2633: invokevirtual 1466	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   2636: iload 28
    //   2638: ifeq +50 -> 2688
    //   2641: iconst_0
    //   2642: istore 12
    //   2644: aload 31
    //   2646: invokevirtual 1511	org/vidogram/messenger/video/OutputSurface:awaitNewImage	()V
    //   2649: iload 12
    //   2651: ifne +37 -> 2688
    //   2654: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   2657: bipush 18
    //   2659: if_icmplt +118 -> 2777
    //   2662: aload 31
    //   2664: iconst_0
    //   2665: invokevirtual 1514	org/vidogram/messenger/video/OutputSurface:drawImage	(Z)V
    //   2668: aload 30
    //   2670: aload 44
    //   2672: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2675: ldc2_w 1515
    //   2678: lmul
    //   2679: invokevirtual 1520	org/vidogram/messenger/video/InputSurface:setPresentationTime	(J)V
    //   2682: aload 30
    //   2684: invokevirtual 1523	org/vidogram/messenger/video/InputSurface:swapBuffers	()Z
    //   2687: pop
    //   2688: aload 44
    //   2690: getfield 1458	android/media/MediaCodec$BufferInfo:flags	I
    //   2693: iconst_4
    //   2694: iand
    //   2695: ifeq +528 -> 3223
    //   2698: iconst_0
    //   2699: istore 4
    //   2701: ldc_w 1525
    //   2704: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   2707: getstatic 691	android/os/Build$VERSION:SDK_INT	I
    //   2710: bipush 18
    //   2712: if_icmplt +152 -> 2864
    //   2715: aload 34
    //   2717: invokevirtual 1528	android/media/MediaCodec:signalEndOfInputStream	()V
    //   2720: iload_2
    //   2721: istore 12
    //   2723: iload 4
    //   2725: istore_2
    //   2726: iload 10
    //   2728: istore 13
    //   2730: goto +743 -> 3473
    //   2733: aload 44
    //   2735: getfield 1455	android/media/MediaCodec$BufferInfo:size	I
    //   2738: ifne +765 -> 3503
    //   2741: aload 44
    //   2743: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2746: lconst_0
    //   2747: lcmp
    //   2748: ifeq +761 -> 3509
    //   2751: goto +752 -> 3503
    //   2754: aload 44
    //   2756: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2759: lstore 19
    //   2761: goto -134 -> 2627
    //   2764: astore 38
    //   2766: iconst_1
    //   2767: istore 12
    //   2769: aload 38
    //   2771: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2774: goto -125 -> 2649
    //   2777: aload 34
    //   2779: ldc2_w 1397
    //   2782: invokevirtual 1402	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2785: istore 12
    //   2787: iload 12
    //   2789: iflt +66 -> 2855
    //   2792: aload 31
    //   2794: iconst_1
    //   2795: invokevirtual 1514	org/vidogram/messenger/video/OutputSurface:drawImage	(Z)V
    //   2798: aload 31
    //   2800: invokevirtual 1532	org/vidogram/messenger/video/OutputSurface:getFrame	()Ljava/nio/ByteBuffer;
    //   2803: astore 38
    //   2805: aload 35
    //   2807: iload 12
    //   2809: aaload
    //   2810: astore 39
    //   2812: aload 39
    //   2814: invokevirtual 1535	java/nio/ByteBuffer:clear	()Ljava/nio/Buffer;
    //   2817: pop
    //   2818: aload 38
    //   2820: aload 39
    //   2822: iload 15
    //   2824: iload 8
    //   2826: iload 7
    //   2828: iload 9
    //   2830: iload 14
    //   2832: invokestatic 1539	org/vidogram/messenger/Utilities:convertVideoFrame	(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;IIIII)I
    //   2835: pop
    //   2836: aload 34
    //   2838: iload 12
    //   2840: iconst_0
    //   2841: iload 11
    //   2843: aload 44
    //   2845: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2848: iconst_0
    //   2849: invokevirtual 1409	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2852: goto -164 -> 2688
    //   2855: ldc_w 1541
    //   2858: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   2861: goto -173 -> 2688
    //   2864: aload 34
    //   2866: ldc2_w 1397
    //   2869: invokevirtual 1402	android/media/MediaCodec:dequeueInputBuffer	(J)I
    //   2872: istore 12
    //   2874: iload 12
    //   2876: iflt +18 -> 2894
    //   2879: aload 34
    //   2881: iload 12
    //   2883: iconst_0
    //   2884: iconst_1
    //   2885: aload 44
    //   2887: getfield 1504	android/media/MediaCodec$BufferInfo:presentationTimeUs	J
    //   2890: iconst_4
    //   2891: invokevirtual 1409	android/media/MediaCodec:queueInputBuffer	(IIIJI)V
    //   2894: iload_2
    //   2895: istore 12
    //   2897: iload 4
    //   2899: istore_2
    //   2900: iload 10
    //   2902: istore 13
    //   2904: goto +569 -> 3473
    //   2907: iload_2
    //   2908: istore 4
    //   2910: iload_3
    //   2911: istore 6
    //   2913: iload 13
    //   2915: istore 5
    //   2917: iload 12
    //   2919: istore_2
    //   2920: iload 4
    //   2922: istore_3
    //   2923: iload 6
    //   2925: istore 4
    //   2927: goto -1585 -> 1342
    //   2930: lload 19
    //   2932: ldc2_w 1233
    //   2935: lcmp
    //   2936: ifeq +280 -> 3216
    //   2939: aload 32
    //   2941: astore 29
    //   2943: iconst_0
    //   2944: istore 27
    //   2946: lload 19
    //   2948: lstore 21
    //   2950: aload 30
    //   2952: astore 32
    //   2954: goto -2356 -> 598
    //   2957: aload_0
    //   2958: aload_1
    //   2959: aload 37
    //   2961: aload 33
    //   2963: aload 44
    //   2965: lload 21
    //   2967: lload 23
    //   2969: aload 42
    //   2971: iconst_0
    //   2972: invokespecial 1273	org/vidogram/messenger/MediaController:readAndWriteTrack	(Lorg/vidogram/messenger/MessageObject;Landroid/media/MediaExtractor;Lorg/vidogram/messenger/video/MP4Builder;Landroid/media/MediaCodec$BufferInfo;JJLjava/io/File;Z)J
    //   2975: lstore 19
    //   2977: lload 19
    //   2979: ldc2_w 1233
    //   2982: lcmp
    //   2983: ifeq +223 -> 3206
    //   2986: iconst_0
    //   2987: istore 27
    //   2989: goto -2327 -> 662
    //   2992: astore 29
    //   2994: aload 29
    //   2996: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2999: goto -2286 -> 713
    //   3002: astore 30
    //   3004: iconst_1
    //   3005: istore 27
    //   3007: aload 30
    //   3009: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3012: aload 31
    //   3014: ifnull +8 -> 3022
    //   3017: aload 31
    //   3019: invokevirtual 1274	android/media/MediaExtractor:release	()V
    //   3022: aload 29
    //   3024: ifnull +9 -> 3033
    //   3027: aload 29
    //   3029: iconst_0
    //   3030: invokevirtual 1277	org/vidogram/messenger/video/MP4Builder:finishMovie	(Z)V
    //   3033: new 1279	java/lang/StringBuilder
    //   3036: dup
    //   3037: invokespecial 1280	java/lang/StringBuilder:<init>	()V
    //   3040: ldc_w 1282
    //   3043: invokevirtual 1286	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3046: invokestatic 1192	java/lang/System:currentTimeMillis	()J
    //   3049: lload 25
    //   3051: lsub
    //   3052: invokevirtual 1289	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3055: invokevirtual 1290	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3058: invokestatic 587	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   3061: goto -2320 -> 741
    //   3064: astore 29
    //   3066: aload 29
    //   3068: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3071: goto -38 -> 3033
    //   3074: astore 29
    //   3076: aload 29
    //   3078: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3081: goto -1314 -> 1767
    //   3084: aload 43
    //   3086: invokeinterface 1171 1 0
    //   3091: ldc_w 1167
    //   3094: iconst_1
    //   3095: invokeinterface 1177 3 0
    //   3100: invokeinterface 1180 1 0
    //   3105: pop
    //   3106: aload_0
    //   3107: aload_1
    //   3108: aload 42
    //   3110: iconst_1
    //   3111: iconst_1
    //   3112: invokespecial 1187	org/vidogram/messenger/MediaController:didWriteData	(Lorg/vidogram/messenger/MessageObject;Ljava/io/File;ZZ)V
    //   3115: iconst_0
    //   3116: ireturn
    //   3117: astore_1
    //   3118: aconst_null
    //   3119: astore 29
    //   3121: aconst_null
    //   3122: astore 33
    //   3124: goto -1378 -> 1746
    //   3127: astore_1
    //   3128: aconst_null
    //   3129: astore 29
    //   3131: goto -1385 -> 1746
    //   3134: astore_1
    //   3135: aload 29
    //   3137: astore 33
    //   3139: aload 31
    //   3141: astore 29
    //   3143: goto -1397 -> 1746
    //   3146: astore 30
    //   3148: aload 33
    //   3150: astore 29
    //   3152: goto -148 -> 3004
    //   3155: astore 30
    //   3157: aload 37
    //   3159: astore 31
    //   3161: aload 33
    //   3163: astore 29
    //   3165: goto -161 -> 3004
    //   3168: astore 32
    //   3170: aconst_null
    //   3171: astore 29
    //   3173: aconst_null
    //   3174: astore 35
    //   3176: aload 30
    //   3178: astore 31
    //   3180: aload 35
    //   3182: astore 30
    //   3184: goto -2606 -> 578
    //   3187: astore 32
    //   3189: aconst_null
    //   3190: astore 29
    //   3192: aconst_null
    //   3193: astore 35
    //   3195: aload 30
    //   3197: astore 31
    //   3199: aload 35
    //   3201: astore 30
    //   3203: goto -2625 -> 578
    //   3206: lload 21
    //   3208: lstore 19
    //   3210: iconst_0
    //   3211: istore 27
    //   3213: goto -2551 -> 662
    //   3216: lload 21
    //   3218: lstore 19
    //   3220: goto -281 -> 2939
    //   3223: iload_2
    //   3224: istore 12
    //   3226: iload 4
    //   3228: istore_2
    //   3229: iload 10
    //   3231: istore 13
    //   3233: goto +240 -> 3473
    //   3236: goto -609 -> 2627
    //   3239: iload 4
    //   3241: istore_2
    //   3242: goto +231 -> 3473
    //   3245: iconst_0
    //   3246: istore 6
    //   3248: goto -1818 -> 1430
    //   3251: iload_2
    //   3252: istore 12
    //   3254: goto +72 -> 3326
    //   3257: aconst_null
    //   3258: astore 35
    //   3260: goto -1932 -> 1328
    //   3263: aconst_null
    //   3264: astore 35
    //   3266: aconst_null
    //   3267: astore 36
    //   3269: goto -1941 -> 1328
    //   3272: aconst_null
    //   3273: astore 30
    //   3275: goto -2041 -> 1234
    //   3278: iconst_0
    //   3279: istore 9
    //   3281: goto -2271 -> 1010
    //   3284: iconst_0
    //   3285: istore 27
    //   3287: goto +17 -> 3304
    //   3290: iload_2
    //   3291: istore 7
    //   3293: iload_3
    //   3294: istore 8
    //   3296: iload 4
    //   3298: istore_2
    //   3299: iconst_0
    //   3300: istore_3
    //   3301: goto -3160 -> 141
    //   3304: lload 21
    //   3306: lstore 19
    //   3308: goto -2646 -> 662
    //   3311: iconst_1
    //   3312: istore 4
    //   3314: iload 11
    //   3316: istore_2
    //   3317: goto -2454 -> 863
    //   3320: iconst_0
    //   3321: istore 6
    //   3323: goto -1893 -> 1430
    //   3326: iload 5
    //   3328: ifne +81 -> 3409
    //   3331: iconst_1
    //   3332: istore_2
    //   3333: iconst_1
    //   3334: istore 10
    //   3336: iload_2
    //   3337: istore 6
    //   3339: iload 5
    //   3341: istore 13
    //   3343: iload 4
    //   3345: istore 5
    //   3347: iload_3
    //   3348: istore_2
    //   3349: iload 6
    //   3351: istore 4
    //   3353: iload 5
    //   3355: istore_3
    //   3356: iload 10
    //   3358: istore 5
    //   3360: iload 4
    //   3362: ifne -1889 -> 1473
    //   3365: iload 5
    //   3367: ifeq -460 -> 2907
    //   3370: goto -1897 -> 1473
    //   3373: ldc_w 1542
    //   3376: istore 15
    //   3378: iconst_0
    //   3379: istore_2
    //   3380: goto -2459 -> 921
    //   3383: iload_2
    //   3384: iconst_5
    //   3385: if_icmpne -1715 -> 1670
    //   3388: iconst_0
    //   3389: istore 9
    //   3391: goto -2381 -> 1010
    //   3394: goto -74 -> 3320
    //   3397: iload 6
    //   3399: iconst_m1
    //   3400: if_icmpne -155 -> 3245
    //   3403: iconst_1
    //   3404: istore 6
    //   3406: goto -1976 -> 1430
    //   3409: iconst_0
    //   3410: istore_2
    //   3411: goto -78 -> 3333
    //   3414: iload_3
    //   3415: istore_2
    //   3416: iload 6
    //   3418: istore_3
    //   3419: iload_2
    //   3420: istore 6
    //   3422: goto -1919 -> 1503
    //   3425: astore 29
    //   3427: aload 32
    //   3429: astore 35
    //   3431: aload 29
    //   3433: astore 32
    //   3435: aload 31
    //   3437: astore 29
    //   3439: aload 30
    //   3441: astore 31
    //   3443: aload 35
    //   3445: astore 30
    //   3447: goto -2869 -> 578
    //   3450: iload 6
    //   3452: istore_3
    //   3453: iload 10
    //   3455: istore 6
    //   3457: goto -1954 -> 1503
    //   3460: iload_2
    //   3461: iconst_1
    //   3462: isub
    //   3463: istore_2
    //   3464: goto -1266 -> 2198
    //   3467: iconst_0
    //   3468: istore 10
    //   3470: goto -1351 -> 2119
    //   3473: iload_2
    //   3474: istore 4
    //   3476: iload_3
    //   3477: istore_2
    //   3478: iload 6
    //   3480: istore_3
    //   3481: goto -121 -> 3360
    //   3484: iload 18
    //   3486: bipush 253
    //   3488: if_icmpne -1086 -> 2402
    //   3491: iload 4
    //   3493: istore_2
    //   3494: goto -21 -> 3473
    //   3497: iconst_0
    //   3498: istore 27
    //   3500: goto -1001 -> 2499
    //   3503: iconst_1
    //   3504: istore 27
    //   3506: goto -1007 -> 2499
    //   3509: iconst_0
    //   3510: istore 27
    //   3512: goto -1013 -> 2499
    //
    // Exception table:
    //   from	to	target	type
    //   495	503	559	java/lang/Exception
    //   507	515	559	java/lang/Exception
    //   519	526	559	java/lang/Exception
    //   530	539	559	java/lang/Exception
    //   548	559	559	java/lang/Exception
    //   779	786	559	java/lang/Exception
    //   790	801	559	java/lang/Exception
    //   815	823	559	java/lang/Exception
    //   827	838	559	java/lang/Exception
    //   849	860	559	java/lang/Exception
    //   867	917	559	java/lang/Exception
    //   925	949	559	java/lang/Exception
    //   953	964	559	java/lang/Exception
    //   999	1010	559	java/lang/Exception
    //   1014	1021	559	java/lang/Exception
    //   1032	1040	559	java/lang/Exception
    //   1044	1053	559	java/lang/Exception
    //   1057	1068	559	java/lang/Exception
    //   1072	1082	559	java/lang/Exception
    //   1094	1103	559	java/lang/Exception
    //   1107	1117	559	java/lang/Exception
    //   1121	1131	559	java/lang/Exception
    //   1135	1143	559	java/lang/Exception
    //   1147	1160	559	java/lang/Exception
    //   1164	1174	559	java/lang/Exception
    //   1178	1185	559	java/lang/Exception
    //   1189	1199	559	java/lang/Exception
    //   1203	1211	559	java/lang/Exception
    //   1215	1229	559	java/lang/Exception
    //   1521	1532	559	java/lang/Exception
    //   1545	1556	559	java/lang/Exception
    //   1569	1580	559	java/lang/Exception
    //   1596	1607	559	java/lang/Exception
    //   1625	1639	559	java/lang/Exception
    //   1679	1690	559	java/lang/Exception
    //   1713	1724	559	java/lang/Exception
    //   1731	1738	559	java/lang/Exception
    //   416	430	1741	finally
    //   448	457	1741	finally
    //   495	503	1741	finally
    //   507	515	1741	finally
    //   519	526	1741	finally
    //   530	539	1741	finally
    //   548	559	1741	finally
    //   578	583	1741	finally
    //   598	605	1741	finally
    //   610	615	1741	finally
    //   620	625	1741	finally
    //   630	640	1741	finally
    //   645	655	1741	finally
    //   655	659	1741	finally
    //   673	692	1741	finally
    //   779	786	1741	finally
    //   790	801	1741	finally
    //   815	823	1741	finally
    //   827	838	1741	finally
    //   849	860	1741	finally
    //   867	917	1741	finally
    //   925	949	1741	finally
    //   953	964	1741	finally
    //   999	1010	1741	finally
    //   1014	1021	1741	finally
    //   1032	1040	1741	finally
    //   1044	1053	1741	finally
    //   1057	1068	1741	finally
    //   1072	1082	1741	finally
    //   1094	1103	1741	finally
    //   1107	1117	1741	finally
    //   1121	1131	1741	finally
    //   1135	1143	1741	finally
    //   1147	1160	1741	finally
    //   1164	1174	1741	finally
    //   1178	1185	1741	finally
    //   1189	1199	1741	finally
    //   1203	1211	1741	finally
    //   1215	1229	1741	finally
    //   1229	1234	1741	finally
    //   1234	1252	1741	finally
    //   1252	1269	1741	finally
    //   1269	1288	1741	finally
    //   1291	1328	1741	finally
    //   1328	1332	1741	finally
    //   1347	1351	1741	finally
    //   1355	1362	1741	finally
    //   1369	1379	1741	finally
    //   1384	1392	1741	finally
    //   1399	1409	1741	finally
    //   1414	1425	1741	finally
    //   1438	1448	1741	finally
    //   1456	1467	1741	finally
    //   1473	1489	1741	finally
    //   1521	1532	1741	finally
    //   1545	1556	1741	finally
    //   1569	1580	1741	finally
    //   1596	1607	1741	finally
    //   1625	1639	1741	finally
    //   1679	1690	1741	finally
    //   1713	1724	1741	finally
    //   1731	1738	1741	finally
    //   1804	1818	1741	finally
    //   1845	1854	1741	finally
    //   1857	1879	1741	finally
    //   1895	1910	1741	finally
    //   1925	1932	1741	finally
    //   1941	1951	1741	finally
    //   1959	1988	1741	finally
    //   1988	1996	1741	finally
    //   2008	2043	1741	finally
    //   2043	2052	1741	finally
    //   2058	2077	1741	finally
    //   2080	2103	1741	finally
    //   2106	2116	1741	finally
    //   2119	2127	1741	finally
    //   2139	2184	1741	finally
    //   2190	2198	1741	finally
    //   2258	2320	1741	finally
    //   2320	2331	1741	finally
    //   2341	2361	1741	finally
    //   2361	2371	1741	finally
    //   2379	2391	1741	finally
    //   2409	2440	1741	finally
    //   2451	2480	1741	finally
    //   2480	2496	1741	finally
    //   2528	2539	1741	finally
    //   2547	2559	1741	finally
    //   2575	2586	1741	finally
    //   2589	2627	1741	finally
    //   2627	2636	1741	finally
    //   2644	2649	1741	finally
    //   2654	2688	1741	finally
    //   2688	2698	1741	finally
    //   2701	2720	1741	finally
    //   2733	2751	1741	finally
    //   2754	2761	1741	finally
    //   2769	2774	1741	finally
    //   2777	2787	1741	finally
    //   2792	2805	1741	finally
    //   2812	2852	1741	finally
    //   2855	2861	1741	finally
    //   2864	2874	1741	finally
    //   2879	2894	1741	finally
    //   2957	2977	1741	finally
    //   1252	1269	1821	java/lang/Exception
    //   1804	1818	1821	java/lang/Exception
    //   2644	2649	2764	java/lang/Exception
    //   707	713	2992	java/lang/Exception
    //   353	407	3002	java/lang/Exception
    //   3027	3033	3064	java/lang/Exception
    //   1761	1767	3074	java/lang/Exception
    //   353	407	3117	finally
    //   407	416	3127	finally
    //   3007	3012	3134	finally
    //   407	416	3146	java/lang/Exception
    //   416	430	3155	java/lang/Exception
    //   448	457	3155	java/lang/Exception
    //   578	583	3155	java/lang/Exception
    //   598	605	3155	java/lang/Exception
    //   610	615	3155	java/lang/Exception
    //   620	625	3155	java/lang/Exception
    //   630	640	3155	java/lang/Exception
    //   645	655	3155	java/lang/Exception
    //   655	659	3155	java/lang/Exception
    //   673	692	3155	java/lang/Exception
    //   2957	2977	3155	java/lang/Exception
    //   1229	1234	3168	java/lang/Exception
    //   1234	1252	3187	java/lang/Exception
    //   1269	1288	3425	java/lang/Exception
    //   1291	1328	3425	java/lang/Exception
    //   1328	1332	3425	java/lang/Exception
    //   1347	1351	3425	java/lang/Exception
    //   1355	1362	3425	java/lang/Exception
    //   1369	1379	3425	java/lang/Exception
    //   1384	1392	3425	java/lang/Exception
    //   1399	1409	3425	java/lang/Exception
    //   1414	1425	3425	java/lang/Exception
    //   1438	1448	3425	java/lang/Exception
    //   1456	1467	3425	java/lang/Exception
    //   1473	1489	3425	java/lang/Exception
    //   1845	1854	3425	java/lang/Exception
    //   1857	1879	3425	java/lang/Exception
    //   1895	1910	3425	java/lang/Exception
    //   1925	1932	3425	java/lang/Exception
    //   1941	1951	3425	java/lang/Exception
    //   1959	1988	3425	java/lang/Exception
    //   1988	1996	3425	java/lang/Exception
    //   2008	2043	3425	java/lang/Exception
    //   2043	2052	3425	java/lang/Exception
    //   2058	2077	3425	java/lang/Exception
    //   2080	2103	3425	java/lang/Exception
    //   2106	2116	3425	java/lang/Exception
    //   2119	2127	3425	java/lang/Exception
    //   2139	2184	3425	java/lang/Exception
    //   2190	2198	3425	java/lang/Exception
    //   2258	2320	3425	java/lang/Exception
    //   2320	2331	3425	java/lang/Exception
    //   2341	2361	3425	java/lang/Exception
    //   2361	2371	3425	java/lang/Exception
    //   2379	2391	3425	java/lang/Exception
    //   2409	2440	3425	java/lang/Exception
    //   2451	2480	3425	java/lang/Exception
    //   2480	2496	3425	java/lang/Exception
    //   2528	2539	3425	java/lang/Exception
    //   2547	2559	3425	java/lang/Exception
    //   2575	2586	3425	java/lang/Exception
    //   2589	2627	3425	java/lang/Exception
    //   2627	2636	3425	java/lang/Exception
    //   2654	2688	3425	java/lang/Exception
    //   2688	2698	3425	java/lang/Exception
    //   2701	2720	3425	java/lang/Exception
    //   2733	2751	3425	java/lang/Exception
    //   2754	2761	3425	java/lang/Exception
    //   2769	2774	3425	java/lang/Exception
    //   2777	2787	3425	java/lang/Exception
    //   2792	2805	3425	java/lang/Exception
    //   2812	2852	3425	java/lang/Exception
    //   2855	2861	3425	java/lang/Exception
    //   2864	2874	3425	java/lang/Exception
    //   2879	2894	3425	java/lang/Exception
  }

  // ERROR //
  public static String copyFileToCache(Uri paramUri, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: invokestatic 1549	org/vidogram/messenger/MediaController:getFileName	(Landroid/net/Uri;)Ljava/lang/String;
    //   7: astore 4
    //   9: aload 4
    //   11: astore_3
    //   12: aload 4
    //   14: ifnonnull +44 -> 58
    //   17: getstatic 1552	org/vidogram/messenger/UserConfig:lastLocalId	I
    //   20: istore_2
    //   21: getstatic 1552	org/vidogram/messenger/UserConfig:lastLocalId	I
    //   24: iconst_1
    //   25: isub
    //   26: putstatic 1552	org/vidogram/messenger/UserConfig:lastLocalId	I
    //   29: iconst_0
    //   30: invokestatic 1555	org/vidogram/messenger/UserConfig:saveConfig	(Z)V
    //   33: getstatic 1561	java/util/Locale:US	Ljava/util/Locale;
    //   36: ldc_w 1563
    //   39: iconst_2
    //   40: anewarray 4	java/lang/Object
    //   43: dup
    //   44: iconst_0
    //   45: iload_2
    //   46: invokestatic 1569	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   49: aastore
    //   50: dup
    //   51: iconst_1
    //   52: aload_1
    //   53: aastore
    //   54: invokestatic 1573	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   57: astore_3
    //   58: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   61: invokevirtual 703	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   64: aload_0
    //   65: invokevirtual 1577	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   68: astore_0
    //   69: new 1060	java/io/File
    //   72: dup
    //   73: invokestatic 1072	org/vidogram/messenger/FileLoader:getInstance	()Lorg/vidogram/messenger/FileLoader;
    //   76: iconst_4
    //   77: invokevirtual 1581	org/vidogram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
    //   80: aload_3
    //   81: invokespecial 1584	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   84: astore 5
    //   86: new 1586	java/io/FileOutputStream
    //   89: dup
    //   90: aload 5
    //   92: invokespecial 1588	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   95: astore_1
    //   96: aload_1
    //   97: astore 4
    //   99: aload_0
    //   100: astore_3
    //   101: sipush 20480
    //   104: newarray byte
    //   106: astore 7
    //   108: aload_1
    //   109: astore 4
    //   111: aload_0
    //   112: astore_3
    //   113: aload_0
    //   114: aload 7
    //   116: invokevirtual 1594	java/io/InputStream:read	([B)I
    //   119: istore_2
    //   120: iload_2
    //   121: iconst_m1
    //   122: if_icmpeq +55 -> 177
    //   125: aload_1
    //   126: astore 4
    //   128: aload_0
    //   129: astore_3
    //   130: aload_1
    //   131: aload 7
    //   133: iconst_0
    //   134: iload_2
    //   135: invokevirtual 1598	java/io/FileOutputStream:write	([BII)V
    //   138: goto -30 -> 108
    //   141: astore 5
    //   143: aload_1
    //   144: astore 4
    //   146: aload_0
    //   147: astore_3
    //   148: aload 5
    //   150: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   153: aload_0
    //   154: ifnull +7 -> 161
    //   157: aload_0
    //   158: invokevirtual 1601	java/io/InputStream:close	()V
    //   161: aload 6
    //   163: astore_0
    //   164: aload_1
    //   165: ifnull +10 -> 175
    //   168: aload_1
    //   169: invokevirtual 1602	java/io/FileOutputStream:close	()V
    //   172: aload 6
    //   174: astore_0
    //   175: aload_0
    //   176: areturn
    //   177: aload_1
    //   178: astore 4
    //   180: aload_0
    //   181: astore_3
    //   182: aload 5
    //   184: invokevirtual 1605	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   187: astore 5
    //   189: aload 5
    //   191: astore_3
    //   192: aload_0
    //   193: ifnull +7 -> 200
    //   196: aload_0
    //   197: invokevirtual 1601	java/io/InputStream:close	()V
    //   200: aload_3
    //   201: astore_0
    //   202: aload_1
    //   203: ifnull -28 -> 175
    //   206: aload_1
    //   207: invokevirtual 1602	java/io/FileOutputStream:close	()V
    //   210: aload_3
    //   211: areturn
    //   212: astore_0
    //   213: aload_0
    //   214: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   217: aload_3
    //   218: areturn
    //   219: astore_0
    //   220: aload_0
    //   221: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   224: goto -24 -> 200
    //   227: astore_0
    //   228: aload_0
    //   229: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   232: goto -71 -> 161
    //   235: astore_0
    //   236: aload_0
    //   237: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   240: aconst_null
    //   241: areturn
    //   242: astore_1
    //   243: aconst_null
    //   244: astore 4
    //   246: aconst_null
    //   247: astore_0
    //   248: aload_0
    //   249: ifnull +7 -> 256
    //   252: aload_0
    //   253: invokevirtual 1601	java/io/InputStream:close	()V
    //   256: aload 4
    //   258: ifnull +8 -> 266
    //   261: aload 4
    //   263: invokevirtual 1602	java/io/FileOutputStream:close	()V
    //   266: aload_1
    //   267: athrow
    //   268: astore_0
    //   269: aload_0
    //   270: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   273: goto -17 -> 256
    //   276: astore_0
    //   277: aload_0
    //   278: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   281: goto -15 -> 266
    //   284: astore_1
    //   285: aconst_null
    //   286: astore 4
    //   288: goto -40 -> 248
    //   291: astore_1
    //   292: aload_3
    //   293: astore_0
    //   294: goto -46 -> 248
    //   297: astore 5
    //   299: aconst_null
    //   300: astore_1
    //   301: aconst_null
    //   302: astore_0
    //   303: goto -160 -> 143
    //   306: astore 5
    //   308: aconst_null
    //   309: astore_1
    //   310: goto -167 -> 143
    //
    // Exception table:
    //   from	to	target	type
    //   101	108	141	java/lang/Exception
    //   113	120	141	java/lang/Exception
    //   130	138	141	java/lang/Exception
    //   182	189	141	java/lang/Exception
    //   206	210	212	java/lang/Exception
    //   196	200	219	java/lang/Exception
    //   157	161	227	java/lang/Exception
    //   168	172	235	java/lang/Exception
    //   3	9	242	finally
    //   17	58	242	finally
    //   58	69	242	finally
    //   252	256	268	java/lang/Exception
    //   261	266	276	java/lang/Exception
    //   69	96	284	finally
    //   101	108	291	finally
    //   113	120	291	finally
    //   130	138	291	finally
    //   148	153	291	finally
    //   182	189	291	finally
    //   3	9	297	java/lang/Exception
    //   17	58	297	java/lang/Exception
    //   58	69	297	java/lang/Exception
    //   69	96	306	java/lang/Exception
  }

  private void didWriteData(MessageObject paramMessageObject, File paramFile, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = this.videoConvertFirstWrite;
    if (bool)
      this.videoConvertFirstWrite = false;
    AndroidUtilities.runOnUIThread(new Runnable(paramBoolean2, paramBoolean1, paramMessageObject, paramFile, bool)
    {
      public void run()
      {
        if ((this.val$error) || (this.val$last));
        synchronized (MediaController.this.videoConvertSync)
        {
          MediaController.access$6302(MediaController.this, false);
          MediaController.this.videoConvertQueue.remove(this.val$messageObject);
          MediaController.this.startVideoConvertFromQueue();
          if (this.val$error)
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingFailed, new Object[] { this.val$messageObject, this.val$file.toString() });
            return;
          }
        }
        if (this.val$firstWrite)
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.FilePreparingStarted, new Object[] { this.val$messageObject, this.val$file.toString() });
        ??? = NotificationCenter.getInstance();
        int i = NotificationCenter.FileNewChunkAvailable;
        MessageObject localMessageObject = this.val$messageObject;
        String str = this.val$file.toString();
        long l;
        if (this.val$last)
          l = this.val$file.length();
        while (true)
        {
          ((NotificationCenter)???).postNotificationName(i, new Object[] { localMessageObject, str, Long.valueOf(l) });
          return;
          l = 0L;
        }
      }
    });
  }

  private int getCurrentDownloadMask()
  {
    if (ConnectionsManager.isConnectedToWiFi())
      return this.wifiDownloadMask;
    if (ConnectionsManager.isRoaming())
      return this.roamingDownloadMask;
    return this.mobileDataDownloadMask;
  }

  // ERROR //
  public static String getFileName(Uri paramUri)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore_2
    //   4: aload_0
    //   5: invokevirtual 1621	android/net/Uri:getScheme	()Ljava/lang/String;
    //   8: ldc_w 1623
    //   11: invokevirtual 1306	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   14: ifeq +158 -> 172
    //   17: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   20: invokevirtual 703	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   23: aload_0
    //   24: iconst_1
    //   25: anewarray 345	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: ldc_w 693
    //   33: aastore
    //   34: aconst_null
    //   35: aconst_null
    //   36: aconst_null
    //   37: invokevirtual 1627	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   40: astore 4
    //   42: aload 4
    //   44: astore_3
    //   45: aload_3
    //   46: astore 4
    //   48: aload_3
    //   49: invokeinterface 1632 1 0
    //   54: ifeq +22 -> 76
    //   57: aload_3
    //   58: astore 4
    //   60: aload_3
    //   61: aload_3
    //   62: ldc_w 693
    //   65: invokeinterface 1635 2 0
    //   70: invokeinterface 1638 2 0
    //   75: astore_2
    //   76: aload_3
    //   77: ifnull +100 -> 177
    //   80: aload_3
    //   81: invokeinterface 1639 1 0
    //   86: aload_2
    //   87: astore_3
    //   88: aload_2
    //   89: ifnonnull +30 -> 119
    //   92: aload_0
    //   93: invokevirtual 1642	android/net/Uri:getPath	()Ljava/lang/String;
    //   96: astore_0
    //   97: aload_0
    //   98: bipush 47
    //   100: invokevirtual 1645	java/lang/String:lastIndexOf	(I)I
    //   103: istore_1
    //   104: aload_0
    //   105: astore_3
    //   106: iload_1
    //   107: iconst_m1
    //   108: if_icmpeq +11 -> 119
    //   111: aload_0
    //   112: iload_1
    //   113: iconst_1
    //   114: iadd
    //   115: invokevirtual 1648	java/lang/String:substring	(I)Ljava/lang/String;
    //   118: astore_3
    //   119: aload_3
    //   120: areturn
    //   121: astore_2
    //   122: aconst_null
    //   123: astore_3
    //   124: aload_3
    //   125: astore 4
    //   127: aload_2
    //   128: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   131: aload_3
    //   132: ifnull +40 -> 172
    //   135: aload_3
    //   136: invokeinterface 1639 1 0
    //   141: aconst_null
    //   142: astore_2
    //   143: goto -57 -> 86
    //   146: astore_0
    //   147: aload_3
    //   148: astore_2
    //   149: aload_2
    //   150: ifnull +9 -> 159
    //   153: aload_2
    //   154: invokeinterface 1639 1 0
    //   159: aload_0
    //   160: athrow
    //   161: astore_0
    //   162: aload 4
    //   164: astore_2
    //   165: goto -16 -> 149
    //   168: astore_2
    //   169: goto -45 -> 124
    //   172: aconst_null
    //   173: astore_2
    //   174: goto -88 -> 86
    //   177: goto -91 -> 86
    //
    // Exception table:
    //   from	to	target	type
    //   17	42	121	java/lang/Exception
    //   17	42	146	finally
    //   48	57	161	finally
    //   60	76	161	finally
    //   127	131	161	finally
    //   48	57	168	java/lang/Exception
    //   60	76	168	java/lang/Exception
  }

  public static MediaController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        MediaController localMediaController = Instance;
        localObject1 = localMediaController;
        if (localMediaController == null)
        {
          localObject1 = new MediaController();
          Instance = (MediaController)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (MediaController)localObject2;
  }

  private native long getTotalPcmDuration();

  public static boolean isGif(Uri paramUri)
  {
    int i = 0;
    Object localObject1 = null;
    Uri localUri = null;
    try
    {
      paramUri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
      localUri = paramUri;
      localObject1 = paramUri;
      Object localObject2 = new byte[3];
      localUri = paramUri;
      localObject1 = paramUri;
      boolean bool;
      if (paramUri.read(localObject2, 0, 3) == 3)
      {
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = new String(localObject2);
        if (localObject2 != null)
        {
          localUri = paramUri;
          localObject1 = paramUri;
          bool = ((String)localObject2).equalsIgnoreCase("gif");
          if (bool)
          {
            bool = true;
            i = bool;
            if (paramUri == null);
          }
        }
      }
      do
        try
        {
          paramUri.close();
          i = bool;
          return i;
        }
        catch (Exception paramUri)
        {
          FileLog.e(paramUri);
          return true;
        }
      while (paramUri == null);
      try
      {
        paramUri.close();
        return false;
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
        return false;
      }
    }
    catch (Exception paramUri)
    {
      do
      {
        localObject1 = localUri;
        FileLog.e(paramUri);
      }
      while (localUri == null);
      try
      {
        localUri.close();
        return false;
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
        return false;
      }
    }
    finally
    {
      if (localObject1 == null);
    }
    try
    {
      ((InputStream)localObject1).close();
      throw paramUri;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private boolean isNearToSensor(float paramFloat)
  {
    return (paramFloat < 5.0F) && (paramFloat != this.proximitySensor.getMaximumRange());
  }

  private native int isOpusFile(String paramString);

  private static boolean isRecognizedFormat(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return false;
    case 19:
    case 20:
    case 21:
    case 39:
    case 2130706688:
    }
    return true;
  }

  public static boolean isWebp(Uri paramUri)
  {
    int i = 0;
    Object localObject1 = null;
    Uri localUri = null;
    try
    {
      paramUri = ApplicationLoader.applicationContext.getContentResolver().openInputStream(paramUri);
      localUri = paramUri;
      localObject1 = paramUri;
      Object localObject2 = new byte[12];
      localUri = paramUri;
      localObject1 = paramUri;
      boolean bool;
      if (paramUri.read(localObject2, 0, 12) == 12)
      {
        localUri = paramUri;
        localObject1 = paramUri;
        localObject2 = new String(localObject2);
        if (localObject2 != null)
        {
          localUri = paramUri;
          localObject1 = paramUri;
          localObject2 = ((String)localObject2).toLowerCase();
          localUri = paramUri;
          localObject1 = paramUri;
          if (((String)localObject2).startsWith("riff"))
          {
            localUri = paramUri;
            localObject1 = paramUri;
            bool = ((String)localObject2).endsWith("webp");
            if (bool)
            {
              bool = true;
              i = bool;
              if (paramUri == null);
            }
          }
        }
      }
      do
        try
        {
          paramUri.close();
          i = bool;
          return i;
        }
        catch (Exception paramUri)
        {
          FileLog.e(paramUri);
          return true;
        }
      while (paramUri == null);
      try
      {
        paramUri.close();
        return false;
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
        return false;
      }
    }
    catch (Exception paramUri)
    {
      do
      {
        localObject1 = localUri;
        FileLog.e(paramUri);
      }
      while (localUri == null);
      try
      {
        localUri.close();
        return false;
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
        return false;
      }
    }
    finally
    {
      if (localObject1 == null);
    }
    try
    {
      ((InputStream)localObject1).close();
      throw paramUri;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public static void loadGalleryPhotosAlbums(int paramInt)
  {
    Thread localThread = new Thread(new Runnable(paramInt)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: new 27	java/util/ArrayList
        //   3: dup
        //   4: invokespecial 28	java/util/ArrayList:<init>	()V
        //   7: astore 20
        //   9: new 27	java/util/ArrayList
        //   12: dup
        //   13: invokespecial 28	java/util/ArrayList:<init>	()V
        //   16: astore 21
        //   18: new 30	java/util/HashMap
        //   21: dup
        //   22: invokespecial 31	java/util/HashMap:<init>	()V
        //   25: astore 22
        //   27: new 33	java/lang/StringBuilder
        //   30: dup
        //   31: invokespecial 34	java/lang/StringBuilder:<init>	()V
        //   34: getstatic 40	android/os/Environment:DIRECTORY_DCIM	Ljava/lang/String;
        //   37: invokestatic 44	android/os/Environment:getExternalStoragePublicDirectory	(Ljava/lang/String;)Ljava/io/File;
        //   40: invokevirtual 50	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   43: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   46: ldc 56
        //   48: invokevirtual 54	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   51: invokevirtual 59	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   54: astore 18
        //   56: getstatic 64	android/os/Build$VERSION:SDK_INT	I
        //   59: bipush 23
        //   61: if_icmplt +22 -> 83
        //   64: getstatic 64	android/os/Build$VERSION:SDK_INT	I
        //   67: bipush 23
        //   69: if_icmplt +1199 -> 1268
        //   72: getstatic 70	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   75: ldc 72
        //   77: invokevirtual 78	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   80: ifne +1188 -> 1268
        //   83: getstatic 70	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   86: invokevirtual 82	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   89: getstatic 88	android/provider/MediaStore$Images$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   92: invokestatic 92	org/vidogram/messenger/MediaController:access$5800	()[Ljava/lang/String;
        //   95: aconst_null
        //   96: aconst_null
        //   97: ldc 94
        //   99: invokestatic 98	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   102: astore 12
        //   104: aload 12
        //   106: ifnull +1145 -> 1251
        //   109: aload 12
        //   111: ldc 100
        //   113: invokeinterface 105 2 0
        //   118: istore_1
        //   119: aload 12
        //   121: ldc 107
        //   123: invokeinterface 105 2 0
        //   128: istore_2
        //   129: aload 12
        //   131: ldc 109
        //   133: invokeinterface 105 2 0
        //   138: istore_3
        //   139: aload 12
        //   141: ldc 111
        //   143: invokeinterface 105 2 0
        //   148: istore 4
        //   150: aload 12
        //   152: ldc 113
        //   154: invokeinterface 105 2 0
        //   159: istore 5
        //   161: aload 12
        //   163: ldc 115
        //   165: invokeinterface 105 2 0
        //   170: istore 6
        //   172: aconst_null
        //   173: astore 13
        //   175: aconst_null
        //   176: astore 14
        //   178: aload 12
        //   180: invokeinterface 119 1 0
        //   185: ifeq +307 -> 492
        //   188: aload 12
        //   190: iload_1
        //   191: invokeinterface 123 2 0
        //   196: istore 7
        //   198: aload 12
        //   200: iload_2
        //   201: invokeinterface 123 2 0
        //   206: istore 8
        //   208: aload 12
        //   210: iload_3
        //   211: invokeinterface 127 2 0
        //   216: astore 24
        //   218: aload 12
        //   220: iload 4
        //   222: invokeinterface 127 2 0
        //   227: astore 23
        //   229: aload 12
        //   231: iload 5
        //   233: invokeinterface 131 2 0
        //   238: lstore 10
        //   240: aload 12
        //   242: iload 6
        //   244: invokeinterface 123 2 0
        //   249: istore 9
        //   251: aload 23
        //   253: ifnull -75 -> 178
        //   256: aload 23
        //   258: invokevirtual 137	java/lang/String:length	()I
        //   261: ifeq -83 -> 178
        //   264: new 139	org/vidogram/messenger/MediaController$PhotoEntry
        //   267: dup
        //   268: iload 8
        //   270: iload 7
        //   272: lload 10
        //   274: aload 23
        //   276: iload 9
        //   278: iconst_0
        //   279: invokespecial 142	org/vidogram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   282: astore 19
        //   284: aload 14
        //   286: ifnonnull +962 -> 1248
        //   289: new 144	org/vidogram/messenger/MediaController$AlbumEntry
        //   292: dup
        //   293: iconst_0
        //   294: ldc 146
        //   296: ldc 147
        //   298: invokestatic 152	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   301: aload 19
        //   303: iconst_0
        //   304: invokespecial 155	org/vidogram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/vidogram/messenger/MediaController$PhotoEntry;Z)V
        //   307: astore 15
        //   309: aload 15
        //   311: astore 16
        //   313: aload 20
        //   315: iconst_0
        //   316: aload 15
        //   318: invokevirtual 159	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   321: aload 15
        //   323: astore 14
        //   325: aload 14
        //   327: ifnull +14 -> 341
        //   330: aload 14
        //   332: astore 16
        //   334: aload 14
        //   336: aload 19
        //   338: invokevirtual 163	org/vidogram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/vidogram/messenger/MediaController$PhotoEntry;)V
        //   341: aload 14
        //   343: astore 16
        //   345: aload 22
        //   347: iload 8
        //   349: invokestatic 169	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   352: invokevirtual 173	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   355: checkcast 144	org/vidogram/messenger/MediaController$AlbumEntry
        //   358: astore 17
        //   360: aload 17
        //   362: astore 15
        //   364: aload 17
        //   366: ifnonnull +123 -> 489
        //   369: aload 14
        //   371: astore 16
        //   373: new 144	org/vidogram/messenger/MediaController$AlbumEntry
        //   376: dup
        //   377: iload 8
        //   379: aload 24
        //   381: aload 19
        //   383: iconst_0
        //   384: invokespecial 155	org/vidogram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/vidogram/messenger/MediaController$PhotoEntry;Z)V
        //   387: astore 15
        //   389: aload 14
        //   391: astore 16
        //   393: aload 22
        //   395: iload 8
        //   397: invokestatic 169	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   400: aload 15
        //   402: invokevirtual 177	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   405: pop
        //   406: aload 13
        //   408: ifnonnull +69 -> 477
        //   411: aload 18
        //   413: ifnull +64 -> 477
        //   416: aload 23
        //   418: ifnull +59 -> 477
        //   421: aload 14
        //   423: astore 16
        //   425: aload 23
        //   427: aload 18
        //   429: invokevirtual 181	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   432: ifeq +45 -> 477
        //   435: aload 14
        //   437: astore 16
        //   439: aload 20
        //   441: iconst_0
        //   442: aload 15
        //   444: invokevirtual 159	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   447: iload 8
        //   449: invokestatic 169	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   452: astore 13
        //   454: aload 15
        //   456: aload 19
        //   458: invokevirtual 163	org/vidogram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/vidogram/messenger/MediaController$PhotoEntry;)V
        //   461: goto -283 -> 178
        //   464: astore 12
        //   466: aload 12
        //   468: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   471: aconst_null
        //   472: astore 18
        //   474: goto -418 -> 56
        //   477: aload 14
        //   479: astore 16
        //   481: aload 20
        //   483: aload 15
        //   485: invokevirtual 190	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   488: pop
        //   489: goto -35 -> 454
        //   492: aload 12
        //   494: astore 15
        //   496: aload 14
        //   498: astore 12
        //   500: aload 15
        //   502: astore 14
        //   504: aload 14
        //   506: astore 17
        //   508: aload 13
        //   510: astore 16
        //   512: aload 12
        //   514: astore 15
        //   516: aload 14
        //   518: ifnull +715 -> 1233
        //   521: aload 14
        //   523: invokeinterface 193 1 0
        //   528: aload 14
        //   530: astore 15
        //   532: aload 12
        //   534: astore 14
        //   536: aload 15
        //   538: astore 12
        //   540: getstatic 64	android/os/Build$VERSION:SDK_INT	I
        //   543: bipush 23
        //   545: if_icmplt +22 -> 567
        //   548: getstatic 64	android/os/Build$VERSION:SDK_INT	I
        //   551: bipush 23
        //   553: if_icmplt +674 -> 1227
        //   556: getstatic 70	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   559: ldc 72
        //   561: invokevirtual 78	android/content/Context:checkSelfPermission	(Ljava/lang/String;)I
        //   564: ifne +663 -> 1227
        //   567: aload 22
        //   569: invokevirtual 196	java/util/HashMap:clear	()V
        //   572: getstatic 70	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   575: invokevirtual 82	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   578: getstatic 199	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   581: invokestatic 202	org/vidogram/messenger/MediaController:access$5900	()[Ljava/lang/String;
        //   584: aconst_null
        //   585: aconst_null
        //   586: ldc 94
        //   588: invokestatic 98	android/provider/MediaStore$Images$Media:query	(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   591: astore 15
        //   593: aload 15
        //   595: astore 12
        //   597: aload 12
        //   599: ifnull +622 -> 1221
        //   602: aload 12
        //   604: ldc 100
        //   606: invokeinterface 105 2 0
        //   611: istore_1
        //   612: aload 12
        //   614: ldc 107
        //   616: invokeinterface 105 2 0
        //   621: istore_2
        //   622: aload 12
        //   624: ldc 109
        //   626: invokeinterface 105 2 0
        //   631: istore_3
        //   632: aload 12
        //   634: ldc 111
        //   636: invokeinterface 105 2 0
        //   641: istore 4
        //   643: aload 12
        //   645: ldc 113
        //   647: invokeinterface 105 2 0
        //   652: istore 5
        //   654: aconst_null
        //   655: astore 17
        //   657: aconst_null
        //   658: astore 15
        //   660: aload 12
        //   662: invokeinterface 119 1 0
        //   667: ifeq +359 -> 1026
        //   670: aload 12
        //   672: iload_1
        //   673: invokeinterface 123 2 0
        //   678: istore 6
        //   680: aload 12
        //   682: iload_2
        //   683: invokeinterface 123 2 0
        //   688: istore 7
        //   690: aload 12
        //   692: iload_3
        //   693: invokeinterface 127 2 0
        //   698: astore 25
        //   700: aload 12
        //   702: iload 4
        //   704: invokeinterface 127 2 0
        //   709: astore 24
        //   711: aload 12
        //   713: iload 5
        //   715: invokeinterface 131 2 0
        //   720: lstore 10
        //   722: aload 24
        //   724: ifnull -64 -> 660
        //   727: aload 24
        //   729: invokevirtual 137	java/lang/String:length	()I
        //   732: ifeq -72 -> 660
        //   735: new 139	org/vidogram/messenger/MediaController$PhotoEntry
        //   738: dup
        //   739: iload 7
        //   741: iload 6
        //   743: lload 10
        //   745: aload 24
        //   747: iconst_0
        //   748: iconst_1
        //   749: invokespecial 142	org/vidogram/messenger/MediaController$PhotoEntry:<init>	(IIJLjava/lang/String;IZ)V
        //   752: astore 23
        //   754: aload 17
        //   756: astore 16
        //   758: aload 17
        //   760: ifnonnull +31 -> 791
        //   763: new 144	org/vidogram/messenger/MediaController$AlbumEntry
        //   766: dup
        //   767: iconst_0
        //   768: ldc 204
        //   770: ldc 205
        //   772: invokestatic 152	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   775: aload 23
        //   777: iconst_1
        //   778: invokespecial 155	org/vidogram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/vidogram/messenger/MediaController$PhotoEntry;Z)V
        //   781: astore 16
        //   783: aload 21
        //   785: iconst_0
        //   786: aload 16
        //   788: invokevirtual 159	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   791: aload 16
        //   793: ifnull +10 -> 803
        //   796: aload 16
        //   798: aload 23
        //   800: invokevirtual 163	org/vidogram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/vidogram/messenger/MediaController$PhotoEntry;)V
        //   803: aload 22
        //   805: iload 7
        //   807: invokestatic 169	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   810: invokevirtual 173	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   813: checkcast 144	org/vidogram/messenger/MediaController$AlbumEntry
        //   816: astore 19
        //   818: aload 19
        //   820: astore 17
        //   822: aload 19
        //   824: ifnonnull +199 -> 1023
        //   827: new 144	org/vidogram/messenger/MediaController$AlbumEntry
        //   830: dup
        //   831: iload 7
        //   833: aload 25
        //   835: aload 23
        //   837: iconst_1
        //   838: invokespecial 155	org/vidogram/messenger/MediaController$AlbumEntry:<init>	(ILjava/lang/String;Lorg/vidogram/messenger/MediaController$PhotoEntry;Z)V
        //   841: astore 17
        //   843: aload 22
        //   845: iload 7
        //   847: invokestatic 169	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   850: aload 17
        //   852: invokevirtual 177	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   855: pop
        //   856: aload 15
        //   858: ifnonnull +157 -> 1015
        //   861: aload 18
        //   863: ifnull +152 -> 1015
        //   866: aload 24
        //   868: ifnull +147 -> 1015
        //   871: aload 24
        //   873: aload 18
        //   875: invokevirtual 181	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   878: ifeq +137 -> 1015
        //   881: aload 21
        //   883: iconst_0
        //   884: aload 17
        //   886: invokevirtual 159	java/util/ArrayList:add	(ILjava/lang/Object;)V
        //   889: iload 7
        //   891: invokestatic 169	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   894: astore 15
        //   896: aload 17
        //   898: aload 23
        //   900: invokevirtual 163	org/vidogram/messenger/MediaController$AlbumEntry:addPhoto	(Lorg/vidogram/messenger/MediaController$PhotoEntry;)V
        //   903: aload 16
        //   905: astore 17
        //   907: goto -247 -> 660
        //   910: astore 15
        //   912: aload 15
        //   914: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   917: aload 12
        //   919: astore 15
        //   921: aload 14
        //   923: astore 12
        //   925: aload 15
        //   927: astore 14
        //   929: goto -389 -> 540
        //   932: astore 15
        //   934: aconst_null
        //   935: astore 12
        //   937: aconst_null
        //   938: astore 13
        //   940: aconst_null
        //   941: astore 14
        //   943: aload 15
        //   945: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   948: aload 12
        //   950: astore 17
        //   952: aload 13
        //   954: astore 16
        //   956: aload 14
        //   958: astore 15
        //   960: aload 12
        //   962: ifnull +271 -> 1233
        //   965: aload 12
        //   967: invokeinterface 193 1 0
        //   972: goto -432 -> 540
        //   975: astore 15
        //   977: aload 15
        //   979: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   982: goto -442 -> 540
        //   985: astore 13
        //   987: aconst_null
        //   988: astore 12
        //   990: aload 12
        //   992: ifnull +10 -> 1002
        //   995: aload 12
        //   997: invokeinterface 193 1 0
        //   1002: aload 13
        //   1004: athrow
        //   1005: astore 12
        //   1007: aload 12
        //   1009: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   1012: goto -10 -> 1002
        //   1015: aload 21
        //   1017: aload 17
        //   1019: invokevirtual 190	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   1022: pop
        //   1023: goto -127 -> 896
        //   1026: aload 15
        //   1028: astore 16
        //   1030: aload 12
        //   1032: ifnull +14 -> 1046
        //   1035: aload 12
        //   1037: invokeinterface 193 1 0
        //   1042: aload 15
        //   1044: astore 16
        //   1046: aload_0
        //   1047: getfield 16	org/vidogram/messenger/MediaController$23:val$guid	I
        //   1050: aload 20
        //   1052: aload 13
        //   1054: aload 21
        //   1056: aload 16
        //   1058: aload 14
        //   1060: iconst_0
        //   1061: invokestatic 209	org/vidogram/messenger/MediaController:access$6000	(ILjava/util/ArrayList;Ljava/lang/Integer;Ljava/util/ArrayList;Ljava/lang/Integer;Lorg/vidogram/messenger/MediaController$AlbumEntry;I)V
        //   1064: return
        //   1065: astore 12
        //   1067: aload 12
        //   1069: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   1072: aload 15
        //   1074: astore 16
        //   1076: goto -30 -> 1046
        //   1079: astore 16
        //   1081: aconst_null
        //   1082: astore 15
        //   1084: aload 16
        //   1086: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   1089: aload 15
        //   1091: astore 16
        //   1093: aload 12
        //   1095: ifnull -49 -> 1046
        //   1098: aload 12
        //   1100: invokeinterface 193 1 0
        //   1105: aload 15
        //   1107: astore 16
        //   1109: goto -63 -> 1046
        //   1112: astore 12
        //   1114: aload 12
        //   1116: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   1119: aload 15
        //   1121: astore 16
        //   1123: goto -77 -> 1046
        //   1126: astore 13
        //   1128: aload 12
        //   1130: ifnull +10 -> 1140
        //   1133: aload 12
        //   1135: invokeinterface 193 1 0
        //   1140: aload 13
        //   1142: athrow
        //   1143: astore 12
        //   1145: aload 12
        //   1147: invokestatic 187	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   1150: goto -10 -> 1140
        //   1153: astore 13
        //   1155: goto -27 -> 1128
        //   1158: astore 13
        //   1160: goto -32 -> 1128
        //   1163: astore 16
        //   1165: aconst_null
        //   1166: astore 15
        //   1168: goto -84 -> 1084
        //   1171: astore 16
        //   1173: goto -89 -> 1084
        //   1176: astore 16
        //   1178: goto -94 -> 1084
        //   1181: astore 13
        //   1183: goto -193 -> 990
        //   1186: astore 13
        //   1188: goto -198 -> 990
        //   1191: astore 15
        //   1193: aconst_null
        //   1194: astore 13
        //   1196: aconst_null
        //   1197: astore 14
        //   1199: goto -256 -> 943
        //   1202: astore 15
        //   1204: goto -261 -> 943
        //   1207: astore 15
        //   1209: goto -266 -> 943
        //   1212: astore 15
        //   1214: aload 16
        //   1216: astore 14
        //   1218: goto -275 -> 943
        //   1221: aconst_null
        //   1222: astore 15
        //   1224: goto -198 -> 1026
        //   1227: aconst_null
        //   1228: astore 15
        //   1230: goto -204 -> 1026
        //   1233: aload 17
        //   1235: astore 12
        //   1237: aload 16
        //   1239: astore 13
        //   1241: aload 15
        //   1243: astore 14
        //   1245: goto -705 -> 540
        //   1248: goto -923 -> 325
        //   1251: aconst_null
        //   1252: astore 13
        //   1254: aconst_null
        //   1255: astore 15
        //   1257: aload 12
        //   1259: astore 14
        //   1261: aload 15
        //   1263: astore 12
        //   1265: goto -761 -> 504
        //   1268: aconst_null
        //   1269: astore 14
        //   1271: aconst_null
        //   1272: astore 13
        //   1274: aconst_null
        //   1275: astore 12
        //   1277: goto -773 -> 504
        //
        // Exception table:
        //   from	to	target	type
        //   27	56	464	java/lang/Exception
        //   521	528	910	java/lang/Exception
        //   56	83	932	java/lang/Throwable
        //   83	104	932	java/lang/Throwable
        //   965	972	975	java/lang/Exception
        //   56	83	985	finally
        //   83	104	985	finally
        //   995	1002	1005	java/lang/Exception
        //   1035	1042	1065	java/lang/Exception
        //   540	567	1079	java/lang/Throwable
        //   567	593	1079	java/lang/Throwable
        //   1098	1105	1112	java/lang/Exception
        //   540	567	1126	finally
        //   567	593	1126	finally
        //   1133	1140	1143	java/lang/Exception
        //   602	654	1153	finally
        //   660	722	1153	finally
        //   727	754	1153	finally
        //   763	791	1153	finally
        //   796	803	1153	finally
        //   803	818	1153	finally
        //   827	856	1153	finally
        //   871	889	1153	finally
        //   896	903	1153	finally
        //   1015	1023	1153	finally
        //   1084	1089	1158	finally
        //   602	654	1163	java/lang/Throwable
        //   896	903	1171	java/lang/Throwable
        //   660	722	1176	java/lang/Throwable
        //   727	754	1176	java/lang/Throwable
        //   763	791	1176	java/lang/Throwable
        //   796	803	1176	java/lang/Throwable
        //   803	818	1176	java/lang/Throwable
        //   827	856	1176	java/lang/Throwable
        //   871	889	1176	java/lang/Throwable
        //   1015	1023	1176	java/lang/Throwable
        //   109	172	1181	finally
        //   178	251	1181	finally
        //   256	284	1181	finally
        //   289	309	1181	finally
        //   313	321	1181	finally
        //   334	341	1181	finally
        //   345	360	1181	finally
        //   373	389	1181	finally
        //   393	406	1181	finally
        //   425	435	1181	finally
        //   439	447	1181	finally
        //   454	461	1181	finally
        //   481	489	1181	finally
        //   943	948	1186	finally
        //   109	172	1191	java/lang/Throwable
        //   454	461	1202	java/lang/Throwable
        //   178	251	1207	java/lang/Throwable
        //   256	284	1207	java/lang/Throwable
        //   289	309	1207	java/lang/Throwable
        //   313	321	1212	java/lang/Throwable
        //   334	341	1212	java/lang/Throwable
        //   345	360	1212	java/lang/Throwable
        //   373	389	1212	java/lang/Throwable
        //   393	406	1212	java/lang/Throwable
        //   425	435	1212	java/lang/Throwable
        //   439	447	1212	java/lang/Throwable
        //   481	489	1212	java/lang/Throwable
      }
    });
    localThread.setPriority(1);
    localThread.start();
  }

  private native int openOpusFile(String paramString);

  private void playNextMessage(boolean paramBoolean)
  {
    ArrayList localArrayList;
    if (this.shuffleMusic)
    {
      localArrayList = this.shuffledPlaylist;
      if ((!paramBoolean) || (this.repeatMode != 2) || (this.forceLoopCurrentPlaylist))
        break label62;
      cleanupPlayer(false, false);
      playAudio((MessageObject)localArrayList.get(this.currentPlaylistNum));
    }
    label62: label338: 
    do
    {
      do
      {
        return;
        localArrayList = this.playlist;
        break;
        this.currentPlaylistNum += 1;
        if (this.currentPlaylistNum < localArrayList.size())
          break label338;
        this.currentPlaylistNum = 0;
        if ((!paramBoolean) || (this.repeatMode != 0) || (this.forceLoopCurrentPlaylist))
          break label338;
      }
      while ((this.audioPlayer == null) && (this.audioTrackPlayer == null));
      if (this.audioPlayer != null);
      while (true)
      {
        try
        {
          this.audioPlayer.reset();
        }
        catch (Exception localException3)
        {
          try
          {
            this.audioPlayer.stop();
          }
          catch (Exception localException3)
          {
            try
            {
              this.audioPlayer.release();
              this.audioPlayer = null;
              stopProgressTimer();
              this.lastProgress = 0;
              this.buffersWrited = 0;
              this.isPaused = true;
              this.playingMessageObject.audioProgress = 0.0F;
              this.playingMessageObject.audioProgressSec = 0;
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
              return;
              localException1 = localException1;
              FileLog.e(localException1);
              continue;
              localException2 = localException2;
              FileLog.e(localException2);
              continue;
            }
            catch (Exception localException3)
            {
              FileLog.e(localException3);
              continue;
            }
          }
        }
        if (this.audioTrackPlayer == null)
          continue;
        try
        {
          synchronized (this.playerObjectSync)
          {
            this.audioTrackPlayer.pause();
            this.audioTrackPlayer.flush();
          }
        }
        catch (Exception localException5)
        {
          try
          {
            while (true)
            {
              this.audioTrackPlayer.release();
              this.audioTrackPlayer = null;
              monitorexit;
              break;
              localObject2 = finally;
              monitorexit;
              throw localObject2;
              localException4 = localException4;
              FileLog.e(localException4);
            }
          }
          catch (Exception localException5)
          {
            while (true)
              FileLog.e(localException5);
          }
        }
      }
    }
    while ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= ???.size()));
    this.playMusicAgain = true;
    playAudio((MessageObject)???.get(this.currentPlaylistNum));
  }

  private void processLaterArrays()
  {
    Iterator localIterator = this.addLaterArray.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      addLoadingFileObserver((String)localEntry.getKey(), (FileDownloadProgressListener)localEntry.getValue());
    }
    this.addLaterArray.clear();
    localIterator = this.deleteLaterArray.iterator();
    while (localIterator.hasNext())
      removeLoadingFileObserver((FileDownloadProgressListener)localIterator.next());
    this.deleteLaterArray.clear();
  }

  @TargetApi(16)
  private long readAndWriteTrack(MessageObject paramMessageObject, MediaExtractor paramMediaExtractor, MP4Builder paramMP4Builder, MediaCodec.BufferInfo paramBufferInfo, long paramLong1, long paramLong2, File paramFile, boolean paramBoolean)
  {
    int n = selectTrack(paramMediaExtractor, paramBoolean);
    Object localObject;
    int i1;
    int i;
    int k;
    long l1;
    int j;
    label181: int m;
    if (n >= 0)
    {
      paramMediaExtractor.selectTrack(n);
      localObject = paramMediaExtractor.getTrackFormat(n);
      i1 = paramMP4Builder.addTrack((MediaFormat)localObject, paramBoolean);
      i = ((MediaFormat)localObject).getInteger("max-input-size");
      k = 0;
      if (paramLong1 > 0L)
      {
        paramMediaExtractor.seekTo(paramLong1, 0);
        localObject = ByteBuffer.allocateDirect(i);
        l1 = -1L;
        checkConversionCanceled();
        if (k != 0)
          break label559;
        checkConversionCanceled();
        j = 0;
        i = paramMediaExtractor.getSampleTrackIndex();
        if (i != n)
          break label539;
        paramBufferInfo.size = paramMediaExtractor.readSampleData((ByteBuffer)localObject, 0);
        if (Build.VERSION.SDK_INT < 21)
        {
          ((ByteBuffer)localObject).position(0);
          ((ByteBuffer)localObject).limit(paramBufferInfo.size);
        }
        if (paramBoolean)
          break label348;
        byte[] arrayOfByte = ((ByteBuffer)localObject).array();
        if (arrayOfByte == null)
          break label348;
        i = ((ByteBuffer)localObject).arrayOffset();
        int i2 = i + ((ByteBuffer)localObject).limit();
        j = -1;
        if (i > i2 - 4)
          break label348;
        if (((arrayOfByte[i] != 0) || (arrayOfByte[(i + 1)] != 0) || (arrayOfByte[(i + 2)] != 0) || (arrayOfByte[(i + 3)] != 1)) && (i != i2 - 4))
          break label586;
        if (j == -1)
          break label341;
        if (i == i2 - 4)
          break label335;
        m = 4;
        label256: m = i - j - m;
        arrayOfByte[j] = (byte)(m >> 24);
        arrayOfByte[(j + 1)] = (byte)(m >> 16);
        arrayOfByte[(j + 2)] = (byte)(m >> 8);
        arrayOfByte[(j + 3)] = (byte)m;
        j = i;
      }
    }
    label533: label539: label559: label579: label586: 
    while (true)
    {
      i += 1;
      break label181;
      paramMediaExtractor.seekTo(0L, 0);
      break;
      label335: m = 0;
      break label256;
      label341: j = i;
      continue;
      label348: label368: long l3;
      long l2;
      if (paramBufferInfo.size >= 0)
      {
        paramBufferInfo.presentationTimeUs = paramMediaExtractor.getSampleTime();
        i = 0;
        l3 = l1;
        if (paramBufferInfo.size <= 0)
          break label579;
        l3 = l1;
        if (i != 0)
          break label579;
        l2 = l1;
        if (paramLong1 > 0L)
        {
          l2 = l1;
          if (l1 == -1L)
            l2 = paramBufferInfo.presentationTimeUs;
        }
        if ((paramLong2 >= 0L) && (paramBufferInfo.presentationTimeUs >= paramLong2))
          break label533;
        paramBufferInfo.offset = 0;
        paramBufferInfo.flags = paramMediaExtractor.getSampleFlags();
        l3 = l2;
        if (!paramMP4Builder.writeSampleData(i1, (ByteBuffer)localObject, paramBufferInfo, false))
          break label579;
        didWriteData(paramMessageObject, paramFile, false, false);
      }
      while (true)
      {
        label480: j = i;
        l1 = l2;
        if (i == 0)
        {
          paramMediaExtractor.advance();
          l1 = l2;
          j = i;
        }
        label506: if (j != 0);
        for (i = 1; ; i = k)
        {
          k = i;
          break;
          paramBufferInfo.size = 0;
          i = 1;
          break label368;
          i = 1;
          break label480;
          if (i == -1)
          {
            j = 1;
            break label506;
          }
          paramMediaExtractor.advance();
          break label506;
          paramMediaExtractor.unselectTrack(n);
          return l1;
          return -1L;
        }
        l2 = l3;
      }
    }
  }

  private native void readOpusFile(ByteBuffer paramByteBuffer, int paramInt, int[] paramArrayOfInt);

  // ERROR //
  public static void saveFile(String paramString1, Context paramContext, int paramInt, String paramString2, String paramString3)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +4 -> 5
    //   4: return
    //   5: aload_0
    //   6: ifnull +151 -> 157
    //   9: aload_0
    //   10: invokevirtual 1058	java/lang/String:length	()I
    //   13: ifeq +144 -> 157
    //   16: new 1060	java/io/File
    //   19: dup
    //   20: aload_0
    //   21: invokespecial 1061	java/io/File:<init>	(Ljava/lang/String;)V
    //   24: astore 5
    //   26: aload 5
    //   28: astore_0
    //   29: aload 5
    //   31: invokevirtual 1064	java/io/File:exists	()Z
    //   34: ifne +5 -> 39
    //   37: aconst_null
    //   38: astore_0
    //   39: aload_0
    //   40: ifnull -36 -> 4
    //   43: iconst_1
    //   44: newarray boolean
    //   46: astore 6
    //   48: aload_0
    //   49: invokevirtual 1064	java/io/File:exists	()Z
    //   52: ifeq -48 -> 4
    //   55: aload_1
    //   56: ifnull +96 -> 152
    //   59: new 1792	org/vidogram/ui/ActionBar/AlertDialog
    //   62: dup
    //   63: aload_1
    //   64: iconst_2
    //   65: invokespecial 1795	org/vidogram/ui/ActionBar/AlertDialog:<init>	(Landroid/content/Context;I)V
    //   68: astore_1
    //   69: aload_1
    //   70: ldc_w 1797
    //   73: ldc_w 1798
    //   76: invokestatic 1803	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   79: invokevirtual 1807	org/vidogram/ui/ActionBar/AlertDialog:setMessage	(Ljava/lang/CharSequence;)V
    //   82: aload_1
    //   83: iconst_0
    //   84: invokevirtual 1810	org/vidogram/ui/ActionBar/AlertDialog:setCanceledOnTouchOutside	(Z)V
    //   87: aload_1
    //   88: iconst_1
    //   89: invokevirtual 1813	org/vidogram/ui/ActionBar/AlertDialog:setCancelable	(Z)V
    //   92: aload_1
    //   93: new 60	org/vidogram/messenger/MediaController$21
    //   96: dup
    //   97: aload 6
    //   99: invokespecial 1816	org/vidogram/messenger/MediaController$21:<init>	([Z)V
    //   102: invokevirtual 1820	org/vidogram/ui/ActionBar/AlertDialog:setOnCancelListener	(Landroid/content/DialogInterface$OnCancelListener;)V
    //   105: aload_1
    //   106: invokevirtual 1823	org/vidogram/ui/ActionBar/AlertDialog:show	()V
    //   109: new 1691	java/lang/Thread
    //   112: dup
    //   113: new 62	org/vidogram/messenger/MediaController$22
    //   116: dup
    //   117: iload_2
    //   118: aload_3
    //   119: aload_0
    //   120: aload 6
    //   122: aload_1
    //   123: aload 4
    //   125: invokespecial 1826	org/vidogram/messenger/MediaController$22:<init>	(ILjava/lang/String;Ljava/io/File;[ZLorg/vidogram/ui/ActionBar/AlertDialog;Ljava/lang/String;)V
    //   128: invokespecial 1695	java/lang/Thread:<init>	(Ljava/lang/Runnable;)V
    //   131: invokevirtual 1697	java/lang/Thread:start	()V
    //   134: return
    //   135: astore 5
    //   137: aconst_null
    //   138: astore_1
    //   139: aload 5
    //   141: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   144: goto -35 -> 109
    //   147: astore 5
    //   149: goto -10 -> 139
    //   152: aconst_null
    //   153: astore_1
    //   154: goto -45 -> 109
    //   157: aconst_null
    //   158: astore_0
    //   159: goto -120 -> 39
    //
    // Exception table:
    //   from	to	target	type
    //   59	69	135	java/lang/Exception
    //   69	109	147	java/lang/Exception
  }

  private native int seekOpusFile(float paramFloat);

  private void seekOpusPlayer(float paramFloat)
  {
    if (paramFloat == 1.0F)
      return;
    if (!this.isPaused)
      this.audioTrackPlayer.pause();
    this.audioTrackPlayer.flush();
    this.fileDecodingQueue.postRunnable(new Runnable(paramFloat)
    {
      public void run()
      {
        MediaController.this.seekOpusFile(this.val$progress);
        synchronized (MediaController.this.playerSync)
        {
          MediaController.this.freePlayerBuffers.addAll(MediaController.this.usedPlayerBuffers);
          MediaController.this.usedPlayerBuffers.clear();
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!MediaController.this.isPaused)
              {
                MediaController.access$2602(MediaController.this, 3);
                MediaController.access$2802(MediaController.this, ()((float)MediaController.this.currentTotalPcmDuration * MediaController.12.this.val$progress));
                if (MediaController.this.audioTrackPlayer != null)
                  MediaController.this.audioTrackPlayer.play();
                MediaController.access$2702(MediaController.this, (int)((float)MediaController.this.currentTotalPcmDuration / 48.0F * MediaController.12.this.val$progress));
                MediaController.this.checkPlayerQueue();
              }
            }
          });
          return;
        }
      }
    });
  }

  @SuppressLint({"NewApi"})
  public static MediaCodecInfo selectCodec(String paramString)
  {
    int k = MediaCodecList.getCodecCount();
    Object localObject1 = null;
    int i = 0;
    while (i < k)
    {
      MediaCodecInfo localMediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
      Object localObject2;
      if (!localMediaCodecInfo.isEncoder())
      {
        localObject2 = localObject1;
        i += 1;
        localObject1 = localObject2;
        continue;
      }
      String[] arrayOfString = localMediaCodecInfo.getSupportedTypes();
      int m = arrayOfString.length;
      int j = 0;
      while (true)
      {
        localObject2 = localObject1;
        if (j >= m)
          break;
        if (arrayOfString[j].equalsIgnoreCase(paramString))
        {
          if (!localMediaCodecInfo.getName().equals("OMX.SEC.avc.enc"));
          do
            return localMediaCodecInfo;
          while (localMediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder"));
          localObject1 = localMediaCodecInfo;
        }
        j += 1;
      }
    }
    return localObject1;
  }

  @SuppressLint({"NewApi"})
  public static int selectColorFormat(MediaCodecInfo paramMediaCodecInfo, String paramString)
  {
    int i = 0;
    paramString = paramMediaCodecInfo.getCapabilitiesForType(paramString);
    int j = 0;
    while (i < paramString.colorFormats.length)
    {
      int k = paramString.colorFormats[i];
      if (isRecognizedFormat(k))
      {
        if ((!paramMediaCodecInfo.getName().equals("OMX.SEC.AVC.Encoder")) || (k != 19))
          return k;
        j = k;
      }
      i += 1;
    }
    return j;
  }

  @TargetApi(16)
  private int selectTrack(MediaExtractor paramMediaExtractor, boolean paramBoolean)
  {
    int j = paramMediaExtractor.getTrackCount();
    int i = 0;
    while (i < j)
    {
      String str = paramMediaExtractor.getTrackFormat(i).getString("mime");
      if (paramBoolean)
      {
        if (!str.startsWith("audio/"));
      }
      else
        do
          return i;
        while (str.startsWith("video/"));
      i += 1;
    }
    return -5;
  }

  private void setPlayerVolume()
  {
    while (true)
    {
      try
      {
        if (this.audioFocus == 1)
          break label51;
        f = 1.0F;
        if (this.audioPlayer == null)
          continue;
        this.audioPlayer.setVolume(f, f);
        return;
        if (this.audioTrackPlayer != null)
        {
          this.audioTrackPlayer.setStereoVolume(f, f);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
      return;
      label51: float f = 0.2F;
    }
  }

  private void startAudioAgain(boolean paramBoolean)
  {
    if (this.playingMessageObject == null)
      return;
    if (this.audioPlayer != null);
    MessageObject localMessageObject;
    for (int i = 1; ; i = 0)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioRouteChanged, new Object[] { Boolean.valueOf(this.useFrontSpeaker) });
      localMessageObject = this.playingMessageObject;
      float f = this.playingMessageObject.audioProgress;
      cleanupPlayer(false, true);
      localMessageObject.audioProgress = f;
      playAudio(localMessageObject);
      if (!paramBoolean)
        break;
      if (i == 0)
        break label103;
      AndroidUtilities.runOnUIThread(new Runnable(localMessageObject)
      {
        public void run()
        {
          MediaController.this.pauseAudio(this.val$currentMessageObject);
        }
      }
      , 100L);
      return;
    }
    label103: pauseAudio(localMessageObject);
  }

  private void startProgressTimer(MessageObject paramMessageObject)
  {
    synchronized (this.progressTimerSync)
    {
      Timer localTimer = this.progressTimer;
      if (localTimer != null);
      try
      {
        this.progressTimer.cancel();
        this.progressTimer = null;
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask(paramMessageObject)
        {
          public void run()
          {
            synchronized (MediaController.this.sync)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((MediaController.5.this.val$currentPlayingMessageObject != null) && ((MediaController.this.audioPlayer != null) || (MediaController.this.audioTrackPlayer != null)) && (!MediaController.this.isPaused))
                  {
                    int j;
                    int k;
                    do
                    {
                      try
                      {
                        if (MediaController.this.ignoreFirstProgress != 0)
                        {
                          MediaController.access$2610(MediaController.this);
                          return;
                        }
                        if (MediaController.this.audioPlayer != null)
                        {
                          i = MediaController.this.audioPlayer.getCurrentPosition();
                          f = MediaController.this.lastProgress / MediaController.this.audioPlayer.getDuration();
                          if (i <= MediaController.this.lastProgress)
                            break;
                          MediaController.access$2702(MediaController.this, i);
                          MediaController.5.this.val$currentPlayingMessageObject.audioProgress = f;
                          MediaController.5.this.val$currentPlayingMessageObject.audioProgressSec = (MediaController.this.lastProgress / 1000);
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(MediaController.5.this.val$currentPlayingMessageObject.getId()), Float.valueOf(f) });
                          return;
                        }
                      }
                      catch (Exception localException)
                      {
                        FileLog.e(localException);
                        return;
                      }
                      j = (int)((float)MediaController.this.lastPlayPcm / 48.0F);
                      float f = (float)MediaController.this.lastPlayPcm / (float)MediaController.this.currentTotalPcmDuration;
                      k = MediaController.this.lastProgress;
                      int i = j;
                    }
                    while (j != k);
                  }
                }
              });
              return;
            }
          }
        }
        , 0L, 17L);
        return;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }

  private native int startRecord(String paramString);

  private boolean startVideoConvertFromQueue()
  {
    int j = 0;
    if (!this.videoConvertQueue.isEmpty());
    while (true)
    {
      int i;
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = false;
        ??? = (MessageObject)this.videoConvertQueue.get(0);
        Intent localIntent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
        localIntent.putExtra("path", ((MessageObject)???).messageOwner.attachPath);
        if (((MessageObject)???).messageOwner.media.document == null)
          continue;
        i = 0;
        if (i >= ((MessageObject)???).messageOwner.media.document.attributes.size())
          continue;
        if (((TLRPC.DocumentAttribute)((MessageObject)???).messageOwner.media.document.attributes.get(i) instanceof TLRPC.TL_documentAttributeAnimated))
        {
          localIntent.putExtra("gif", true);
          if (((MessageObject)???).getId() == 0)
            continue;
          ApplicationLoader.applicationContext.startService(localIntent);
          VideoConvertRunnable.runConversion((MessageObject)???);
          j = 1;
          return j;
        }
      }
      i += 1;
    }
  }

  private void stopProgressTimer()
  {
    synchronized (this.progressTimerSync)
    {
      Timer localTimer = this.progressTimer;
      if (localTimer != null);
      try
      {
        this.progressTimer.cancel();
        this.progressTimer = null;
        return;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }

  private native void stopRecord();

  private void stopRecordingInternal(int paramInt)
  {
    if (paramInt != 0)
    {
      TLRPC.TL_document localTL_document = this.recordingAudio;
      File localFile = this.recordingAudioFile;
      this.fileEncodingQueue.postRunnable(new Runnable(localTL_document, localFile, paramInt)
      {
        public void run()
        {
          MediaController.this.stopRecord();
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              String str = null;
              MediaController.19.this.val$audioToSend.date = ConnectionsManager.getInstance().getCurrentTime();
              MediaController.19.this.val$audioToSend.size = (int)MediaController.19.this.val$recordingAudioFileToSend.length();
              Object localObject = new TLRPC.TL_documentAttributeAudio();
              ((TLRPC.TL_documentAttributeAudio)localObject).voice = true;
              ((TLRPC.TL_documentAttributeAudio)localObject).waveform = MediaController.this.getWaveform2(MediaController.this.recordSamples, MediaController.this.recordSamples.length);
              if (((TLRPC.TL_documentAttributeAudio)localObject).waveform != null)
                ((TLRPC.TL_documentAttributeAudio)localObject).flags |= 4;
              long l = MediaController.this.recordTimeCount;
              ((TLRPC.TL_documentAttributeAudio)localObject).duration = (int)(MediaController.this.recordTimeCount / 1000L);
              MediaController.19.this.val$audioToSend.attributes.add(localObject);
              if (l > 700L)
              {
                if (MediaController.19.this.val$send == 1)
                  SendMessagesHelper.getInstance().sendMessage(MediaController.19.this.val$audioToSend, null, MediaController.19.this.val$recordingAudioFileToSend.getAbsolutePath(), MediaController.this.recordDialogId, MediaController.this.recordReplyingMessageObject, null, null);
                NotificationCenter localNotificationCenter = NotificationCenter.getInstance();
                int i = NotificationCenter.audioDidSent;
                if (MediaController.19.this.val$send == 2);
                for (localObject = MediaController.19.this.val$audioToSend; ; localObject = null)
                {
                  if (MediaController.19.this.val$send == 2)
                    str = MediaController.19.this.val$recordingAudioFileToSend.getAbsolutePath();
                  localNotificationCenter.postNotificationName(i, new Object[] { localObject, str });
                  return;
                }
              }
              MediaController.19.this.val$recordingAudioFileToSend.delete();
            }
          });
        }
      });
    }
    try
    {
      if (this.audioRecorder != null)
      {
        this.audioRecorder.release();
        this.audioRecorder = null;
      }
      this.recordingAudio = null;
      this.recordingAudioFile = null;
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private native int writeFrame(ByteBuffer paramByteBuffer, int paramInt);

  public void addLoadingFileObserver(String paramString, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    addLoadingFileObserver(paramString, null, paramFileDownloadProgressListener);
  }

  public void addLoadingFileObserver(String paramString, MessageObject paramMessageObject, FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress)
    {
      this.addLaterArray.put(paramString, paramFileDownloadProgressListener);
      return;
    }
    removeLoadingFileObserver(paramFileDownloadProgressListener);
    ArrayList localArrayList2 = (ArrayList)this.loadingFileObservers.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.loadingFileObservers.put(paramString, localArrayList1);
    }
    localArrayList1.add(new WeakReference(paramFileDownloadProgressListener));
    if (paramMessageObject != null)
    {
      localArrayList2 = (ArrayList)this.loadingFileMessagesObservers.get(paramString);
      localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.loadingFileMessagesObservers.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramMessageObject);
    }
    this.observersByTag.put(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()), paramString);
  }

  public boolean canAutoplayGifs()
  {
    return this.autoplayGifs;
  }

  public boolean canCustomTabs()
  {
    return this.customTabs;
  }

  public boolean canDirectShare()
  {
    return this.directShare;
  }

  public boolean canDownloadMedia(int paramInt)
  {
    return (getCurrentDownloadMask() & paramInt) != 0;
  }

  public boolean canRaiseToSpeak()
  {
    return this.raiseToSpeak;
  }

  public boolean canSaveToGallery()
  {
    return this.saveToGallery;
  }

  public void cancelVideoConvert(MessageObject arg1)
  {
    if (??? == null)
      synchronized (this.videoConvertSync)
      {
        this.cancelCurrentVideoConversion = true;
        return;
      }
    if (!this.videoConvertQueue.isEmpty())
    {
      if (this.videoConvertQueue.get(0) == ???)
        synchronized (this.videoConvertSync)
        {
          this.cancelCurrentVideoConversion = true;
          return;
        }
      this.videoConvertQueue.remove(???);
    }
  }

  public void checkAutodownloadSettings()
  {
    int j = getCurrentDownloadMask();
    if (j == this.lastCheckMask);
    label61: label84: label105: int i;
    label128: label223: label498: 
    do
    {
      return;
      this.lastCheckMask = j;
      if ((j & 0x1) != 0)
      {
        if (this.photoDownloadQueue.isEmpty())
          newDownloadObjectsAvailable(1);
        if ((j & 0x2) == 0)
          break label223;
        if (this.audioDownloadQueue.isEmpty())
          newDownloadObjectsAvailable(2);
        if ((j & 0x8) == 0)
          break label278;
        if (this.documentDownloadQueue.isEmpty())
          newDownloadObjectsAvailable(8);
        if ((j & 0x4) == 0)
          break label333;
        if (this.videoDownloadQueue.isEmpty())
          newDownloadObjectsAvailable(4);
        if ((j & 0x10) == 0)
          break label388;
        if (this.musicDownloadQueue.isEmpty())
          newDownloadObjectsAvailable(16);
        if ((j & 0x20) == 0)
          break label443;
        if (this.gifDownloadQueue.isEmpty())
          newDownloadObjectsAvailable(32);
      }
      while (true)
      {
        i = getAutodownloadMask();
        if (i != 0)
          break label498;
        MessagesStorage.getInstance().clearDownloadQueue(0);
        return;
        i = 0;
        Object localObject;
        while (i < this.photoDownloadQueue.size())
        {
          localObject = (DownloadObject)this.photoDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.PhotoSize)((DownloadObject)localObject).object);
          i += 1;
        }
        this.photoDownloadQueue.clear();
        break;
        i = 0;
        while (i < this.audioDownloadQueue.size())
        {
          localObject = (DownloadObject)this.audioDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
          i += 1;
        }
        this.audioDownloadQueue.clear();
        break label61;
        i = 0;
        while (i < this.documentDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.documentDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.documentDownloadQueue.clear();
        break label84;
        i = 0;
        while (i < this.videoDownloadQueue.size())
        {
          localObject = (DownloadObject)this.videoDownloadQueue.get(i);
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)((DownloadObject)localObject).object);
          i += 1;
        }
        this.videoDownloadQueue.clear();
        break label105;
        i = 0;
        while (i < this.musicDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.musicDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.musicDownloadQueue.clear();
        break label128;
        i = 0;
        while (i < this.gifDownloadQueue.size())
        {
          localObject = (TLRPC.Document)((DownloadObject)this.gifDownloadQueue.get(i)).object;
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)localObject);
          i += 1;
        }
        this.gifDownloadQueue.clear();
      }
      if ((i & 0x1) == 0)
        MessagesStorage.getInstance().clearDownloadQueue(1);
      if ((i & 0x2) == 0)
        MessagesStorage.getInstance().clearDownloadQueue(2);
      if ((i & 0x4) == 0)
        MessagesStorage.getInstance().clearDownloadQueue(4);
      if ((i & 0x8) == 0)
        MessagesStorage.getInstance().clearDownloadQueue(8);
      if ((i & 0x10) != 0)
        continue;
      MessagesStorage.getInstance().clearDownloadQueue(16);
    }
    while ((i & 0x20) != 0);
    label278: label333: MessagesStorage.getInstance().clearDownloadQueue(32);
    label388: label443: return;
  }

  public void checkSaveToGalleryFiles()
  {
    try
    {
      File localFile2 = new File(Environment.getExternalStorageDirectory(), "Telegram");
      File localFile1 = new File(localFile2, "Telegram Images");
      localFile1.mkdir();
      localFile2 = new File(localFile2, "Telegram Video");
      localFile2.mkdir();
      if (this.saveToGallery)
      {
        if (localFile1.isDirectory())
          new File(localFile1, ".nomedia").delete();
        if (localFile2.isDirectory())
        {
          new File(localFile2, ".nomedia").delete();
          return;
        }
      }
      else
      {
        if (localFile1.isDirectory())
          new File(localFile1, ".nomedia").createNewFile();
        if (localFile2.isDirectory())
        {
          new File(localFile2, ".nomedia").createNewFile();
          return;
        }
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void cleanup()
  {
    cleanupPlayer(false, true);
    this.audioInfo = null;
    this.playMusicAgain = false;
    this.photoDownloadQueue.clear();
    this.audioDownloadQueue.clear();
    this.documentDownloadQueue.clear();
    this.videoDownloadQueue.clear();
    this.musicDownloadQueue.clear();
    this.gifDownloadQueue.clear();
    this.downloadQueueKeys.clear();
    this.videoConvertQueue.clear();
    this.playlist.clear();
    this.shuffledPlaylist.clear();
    this.generatingWaveform.clear();
    this.typingTimes.clear();
    this.voiceMessagesPlaylist = null;
    this.voiceMessagesPlaylistMap = null;
    cancelVideoConvert(null);
  }

  public void cleanupPlayer(boolean paramBoolean1, boolean paramBoolean2)
  {
    cleanupPlayer(paramBoolean1, paramBoolean2, false);
  }

  public void cleanupPlayer(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (this.audioPlayer != null);
    while (true)
    {
      try
      {
        this.audioPlayer.reset();
      }
      catch (Exception localException3)
      {
        try
        {
          this.audioPlayer.stop();
        }
        catch (Exception localException3)
        {
          try
          {
            this.audioPlayer.release();
            this.audioPlayer = null;
            stopProgressTimer();
            this.lastProgress = 0;
            this.buffersWrited = 0;
            this.isPaused = false;
            if (this.playingMessageObject == null)
              continue;
            if (!this.downloadingCurrentMessage)
              continue;
            FileLoader.getInstance().cancelLoadFile(this.playingMessageObject.getDocument());
            Object localObject1 = this.playingMessageObject;
            this.playingMessageObject.audioProgress = 0.0F;
            this.playingMessageObject.audioProgressSec = 0;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioProgressDidChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()), Integer.valueOf(0) });
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (!paramBoolean1)
              continue;
            NotificationsController.getInstance().audioManager.abandonAudioFocus(this);
            this.hasAudioFocus = 0;
            if (this.voiceMessagesPlaylist == null)
              continue;
            if ((!paramBoolean3) || (this.voiceMessagesPlaylist.get(0) != localObject1))
              break label425;
            this.voiceMessagesPlaylist.remove(0);
            this.voiceMessagesPlaylistMap.remove(Integer.valueOf(((MessageObject)localObject1).getId()));
            if (!this.voiceMessagesPlaylist.isEmpty())
              continue;
            this.voiceMessagesPlaylist = null;
            this.voiceMessagesPlaylistMap = null;
            if (this.voiceMessagesPlaylist == null)
              break label438;
            playAudio((MessageObject)this.voiceMessagesPlaylist.get(0));
            if (!paramBoolean2)
              continue;
            localObject1 = new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class);
            ApplicationLoader.applicationContext.stopService((Intent)localObject1);
            if ((this.useFrontSpeaker) || (this.raiseToSpeak))
              continue;
            localObject1 = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat);
            this.raiseChat = ((ChatActivity)localObject1);
            return;
            localException1 = localException1;
            FileLog.e(localException1);
            continue;
            localException2 = localException2;
            FileLog.e(localException2);
            continue;
          }
          catch (Exception localException3)
          {
            FileLog.e(localException3);
            continue;
          }
        }
      }
      if (this.audioTrackPlayer == null)
        continue;
      try
      {
        synchronized (this.playerObjectSync)
        {
          this.audioTrackPlayer.pause();
          this.audioTrackPlayer.flush();
        }
      }
      catch (Exception localException5)
      {
        try
        {
          while (true)
          {
            this.audioTrackPlayer.release();
            this.audioTrackPlayer = null;
            monitorexit;
            break;
            localObject3 = finally;
            monitorexit;
            throw localObject3;
            localException4 = localException4;
            FileLog.e(localException4);
          }
        }
        catch (Exception localException5)
        {
          while (true)
            FileLog.e(localException5);
        }
      }
      label425: this.voiceMessagesPlaylist = null;
      this.voiceMessagesPlaylistMap = null;
      continue;
      label438: if ((???.isVoice()) && (???.getId() != 0))
        startRecordingIfFromSpeaker();
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioDidReset, new Object[] { Integer.valueOf(???.getId()), Boolean.valueOf(paramBoolean2) });
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    int k = 0;
    int j = 0;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if ((paramInt == NotificationCenter.FileDidFailedLoad) || (paramInt == NotificationCenter.httpFileDidFailedLoad))
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramArrayOfObject[0];
      localObject2 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject2 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject2).size())
        {
          localObject3 = (WeakReference)((ArrayList)localObject2).get(paramInt);
          if (((WeakReference)localObject3).get() != null)
          {
            ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onFailedDownload((String)localObject1);
            this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener)((WeakReference)localObject3).get()).getObserverTag()));
          }
          paramInt += 1;
        }
        this.loadingFileObservers.remove(localObject1);
      }
      this.listenerInProgress = false;
      processLaterArrays();
      checkDownloadFinished((String)localObject1, ((Integer)paramArrayOfObject[1]).intValue());
      return;
    }
    if ((paramInt == NotificationCenter.FileDidLoaded) || (paramInt == NotificationCenter.httpFileDidLoaded))
    {
      this.listenerInProgress = true;
      paramArrayOfObject = (String)paramArrayOfObject[0];
      if ((this.downloadingCurrentMessage) && (this.playingMessageObject != null) && (FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(paramArrayOfObject)))
      {
        this.playMusicAgain = true;
        playAudio(this.playingMessageObject);
      }
      localObject1 = (ArrayList)this.loadingFileMessagesObservers.get(paramArrayOfObject);
      if (localObject1 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          ((MessageObject)((ArrayList)localObject1).get(paramInt)).mediaExists = true;
          paramInt += 1;
        }
        this.loadingFileMessagesObservers.remove(paramArrayOfObject);
      }
      localObject1 = (ArrayList)this.loadingFileObservers.get(paramArrayOfObject);
      if (localObject1 != null)
      {
        paramInt = 0;
        while (paramInt < ((ArrayList)localObject1).size())
        {
          localObject2 = (WeakReference)((ArrayList)localObject1).get(paramInt);
          if (((WeakReference)localObject2).get() != null)
          {
            ((FileDownloadProgressListener)((WeakReference)localObject2).get()).onSuccessDownload(paramArrayOfObject);
            this.observersByTag.remove(Integer.valueOf(((FileDownloadProgressListener)((WeakReference)localObject2).get()).getObserverTag()));
          }
          paramInt += 1;
        }
        this.loadingFileObservers.remove(paramArrayOfObject);
      }
      this.listenerInProgress = false;
      processLaterArrays();
      checkDownloadFinished(paramArrayOfObject, 0);
      return;
    }
    if (paramInt == NotificationCenter.FileLoadProgressChanged)
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramArrayOfObject[0];
      localObject2 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject2 != null)
      {
        paramArrayOfObject = (Float)paramArrayOfObject[1];
        localObject2 = ((ArrayList)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (WeakReference)((Iterator)localObject2).next();
          if (((WeakReference)localObject3).get() == null)
            continue;
          ((FileDownloadProgressListener)((WeakReference)localObject3).get()).onProgressDownload((String)localObject1, paramArrayOfObject.floatValue());
        }
      }
      this.listenerInProgress = false;
      processLaterArrays();
      return;
    }
    if (paramInt == NotificationCenter.FileUploadProgressChanged)
    {
      this.listenerInProgress = true;
      localObject1 = (String)paramArrayOfObject[0];
      localObject3 = (ArrayList)this.loadingFileObservers.get(localObject1);
      if (localObject3 != null)
      {
        localObject2 = (Float)paramArrayOfObject[1];
        paramArrayOfObject = (Boolean)paramArrayOfObject[2];
        localObject3 = ((ArrayList)localObject3).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          WeakReference localWeakReference = (WeakReference)((Iterator)localObject3).next();
          if (localWeakReference.get() == null)
            continue;
          ((FileDownloadProgressListener)localWeakReference.get()).onProgressUpload((String)localObject1, ((Float)localObject2).floatValue(), paramArrayOfObject.booleanValue());
        }
      }
      this.listenerInProgress = false;
      processLaterArrays();
    }
    while (true)
    {
      long l;
      try
      {
        paramArrayOfObject = SendMessagesHelper.getInstance().getDelayedMessages((String)localObject1);
        if (paramArrayOfObject == null)
          break;
        paramInt = j;
        if (paramInt >= paramArrayOfObject.size())
          break;
        localObject1 = (SendMessagesHelper.DelayedMessage)paramArrayOfObject.get(paramInt);
        if (((SendMessagesHelper.DelayedMessage)localObject1).encryptedChat != null)
          break label1288;
        l = ((SendMessagesHelper.DelayedMessage)localObject1).obj.getDialogId();
        localObject2 = (Long)this.typingTimes.get(Long.valueOf(l));
        if ((localObject2 != null) && (((Long)localObject2).longValue() + 4000L >= System.currentTimeMillis()))
          break label1288;
        if (!MessageObject.isVideoDocument(((SendMessagesHelper.DelayedMessage)localObject1).documentLocation))
          continue;
        MessagesController.getInstance().sendTyping(l, 5, 0);
        this.typingTimes.put(Long.valueOf(l), Long.valueOf(System.currentTimeMillis()));
        break label1288;
        if (((SendMessagesHelper.DelayedMessage)localObject1).documentLocation != null)
        {
          MessagesController.getInstance().sendTyping(l, 3, 0);
          continue;
        }
      }
      catch (Exception paramArrayOfObject)
      {
        FileLog.e(paramArrayOfObject);
        return;
      }
      if (((SendMessagesHelper.DelayedMessage)localObject1).location == null)
        continue;
      MessagesController.getInstance().sendTyping(l, 4, 0);
      continue;
      if (paramInt == NotificationCenter.messagesDeleted)
      {
        paramInt = ((Integer)paramArrayOfObject[1]).intValue();
        paramArrayOfObject = (ArrayList)paramArrayOfObject[0];
        if ((this.playingMessageObject != null) && (paramInt == this.playingMessageObject.messageOwner.to_id.channel_id) && (paramArrayOfObject.contains(Integer.valueOf(this.playingMessageObject.getId()))))
          cleanupPlayer(true, true);
        if ((this.voiceMessagesPlaylist == null) || (this.voiceMessagesPlaylist.isEmpty()) || (paramInt != ((MessageObject)this.voiceMessagesPlaylist.get(0)).messageOwner.to_id.channel_id))
          break;
        paramInt = i;
        while (paramInt < paramArrayOfObject.size())
        {
          localObject1 = (MessageObject)this.voiceMessagesPlaylistMap.remove(paramArrayOfObject.get(paramInt));
          if (localObject1 != null)
            this.voiceMessagesPlaylist.remove(localObject1);
          paramInt += 1;
        }
        break;
      }
      if (paramInt == NotificationCenter.removeAllMessagesFromDialog)
      {
        l = ((Long)paramArrayOfObject[0]).longValue();
        if ((this.playingMessageObject == null) || (this.playingMessageObject.getDialogId() != l))
          break;
        cleanupPlayer(false, true);
        return;
      }
      if (paramInt == NotificationCenter.musicDidLoaded)
      {
        l = ((Long)paramArrayOfObject[0]).longValue();
        if ((this.playingMessageObject == null) || (!this.playingMessageObject.isMusic()) || (this.playingMessageObject.getDialogId() != l))
          break;
        paramArrayOfObject = (ArrayList)paramArrayOfObject[1];
        this.playlist.addAll(0, paramArrayOfObject);
        if (this.shuffleMusic)
        {
          buildShuffledPlayList();
          this.currentPlaylistNum = 0;
          return;
        }
        paramInt = this.currentPlaylistNum;
        this.currentPlaylistNum = (paramArrayOfObject.size() + paramInt);
        return;
      }
      if ((paramInt != NotificationCenter.didReceivedNewMessages) || (this.voiceMessagesPlaylist == null) || (this.voiceMessagesPlaylist.isEmpty()))
        break;
      localObject1 = (MessageObject)this.voiceMessagesPlaylist.get(0);
      if (((Long)paramArrayOfObject[0]).longValue() != ((MessageObject)localObject1).getDialogId())
        break;
      paramArrayOfObject = (ArrayList)paramArrayOfObject[1];
      paramInt = k;
      while (paramInt < paramArrayOfObject.size())
      {
        localObject1 = (MessageObject)paramArrayOfObject.get(paramInt);
        if ((((MessageObject)localObject1).isVoice()) && ((!this.voiceMessagesPlaylistUnread) || ((((MessageObject)localObject1).isContentUnread()) && (!((MessageObject)localObject1).isOut()))))
        {
          this.voiceMessagesPlaylist.add(localObject1);
          this.voiceMessagesPlaylistMap.put(Integer.valueOf(((MessageObject)localObject1).getId()), localObject1);
        }
        paramInt += 1;
      }
      break;
      label1288: paramInt += 1;
    }
  }

  public int generateObserverTag()
  {
    int i = this.lastTag;
    this.lastTag = (i + 1);
    return i;
  }

  public void generateWaveform(MessageObject paramMessageObject)
  {
    String str1 = paramMessageObject.getId() + "_" + paramMessageObject.getDialogId();
    String str2 = FileLoader.getPathToMessage(paramMessageObject.messageOwner).getAbsolutePath();
    if (this.generatingWaveform.containsKey(str1))
      return;
    this.generatingWaveform.put(str1, paramMessageObject);
    Utilities.globalQueue.postRunnable(new Runnable(str2, str1)
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable(MediaController.getInstance().getWaveform(this.val$path))
        {
          public void run()
          {
            MessageObject localMessageObject = (MessageObject)MediaController.this.generatingWaveform.remove(MediaController.18.this.val$id);
            if (localMessageObject == null);
            do
              return;
            while (this.val$waveform == null);
            int i = 0;
            while (true)
            {
              Object localObject;
              if (i < localMessageObject.getDocument().attributes.size())
              {
                localObject = (TLRPC.DocumentAttribute)localMessageObject.getDocument().attributes.get(i);
                if ((localObject instanceof TLRPC.TL_documentAttributeAudio))
                {
                  ((TLRPC.DocumentAttribute)localObject).waveform = this.val$waveform;
                  ((TLRPC.DocumentAttribute)localObject).flags |= 4;
                }
              }
              else
              {
                localObject = new TLRPC.TL_messages_messages();
                ((TLRPC.TL_messages_messages)localObject).messages.add(localMessageObject.messageOwner);
                MessagesStorage.getInstance().putMessages((TLRPC.messages_Messages)localObject, localMessageObject.getDialogId(), -1, 0, false);
                localObject = new ArrayList();
                ((ArrayList)localObject).add(localMessageObject);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(localMessageObject.getDialogId()), localObject });
                return;
              }
              i += 1;
            }
          }
        });
      }
    });
  }

  public AudioInfo getAudioInfo()
  {
    return this.audioInfo;
  }

  protected int getAutodownloadMask()
  {
    int j = 0;
    if (((this.mobileDataDownloadMask & 0x1) != 0) || ((this.wifiDownloadMask & 0x1) != 0) || ((this.roamingDownloadMask & 0x1) != 0))
      j = 1;
    int i;
    if (((this.mobileDataDownloadMask & 0x2) == 0) && ((this.wifiDownloadMask & 0x2) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x2) == 0);
    }
    else
    {
      i = j | 0x2;
    }
    if (((this.mobileDataDownloadMask & 0x4) == 0) && ((this.wifiDownloadMask & 0x4) == 0))
    {
      j = i;
      if ((this.roamingDownloadMask & 0x4) == 0);
    }
    else
    {
      j = i | 0x4;
    }
    if (((this.mobileDataDownloadMask & 0x8) == 0) && ((this.wifiDownloadMask & 0x8) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x8) == 0);
    }
    else
    {
      i = j | 0x8;
    }
    if (((this.mobileDataDownloadMask & 0x10) == 0) && ((this.wifiDownloadMask & 0x10) == 0))
    {
      j = i;
      if ((this.roamingDownloadMask & 0x10) == 0);
    }
    else
    {
      j = i | 0x10;
    }
    if (((this.mobileDataDownloadMask & 0x20) == 0) && ((this.wifiDownloadMask & 0x20) == 0))
    {
      i = j;
      if ((this.roamingDownloadMask & 0x20) == 0);
    }
    else
    {
      i = j | 0x20;
    }
    return i;
  }

  public MessageObject getPlayingMessageObject()
  {
    return this.playingMessageObject;
  }

  public int getPlayingMessageObjectNum()
  {
    return this.currentPlaylistNum;
  }

  public int getRepeatMode()
  {
    return this.repeatMode;
  }

  public native byte[] getWaveform(String paramString);

  public native byte[] getWaveform2(short[] paramArrayOfShort, int paramInt);

  public boolean isAudioPaused()
  {
    return (this.isPaused) || (this.downloadingCurrentMessage);
  }

  public boolean isDownloadingCurrentMessage()
  {
    return this.downloadingCurrentMessage;
  }

  public boolean isPlayingAudio(MessageObject paramMessageObject)
  {
    return ((this.audioTrackPlayer != null) || (this.audioPlayer != null)) && (paramMessageObject != null) && (this.playingMessageObject != null) && ((this.playingMessageObject == null) || ((this.playingMessageObject.getId() == paramMessageObject.getId()) && (!this.downloadingCurrentMessage)));
  }

  protected boolean isRecordingAudio()
  {
    return (this.recordStartRunnable != null) || (this.recordingAudio != null);
  }

  public boolean isShuffleMusic()
  {
    return this.shuffleMusic;
  }

  protected void newDownloadObjectsAvailable(int paramInt)
  {
    int i = getCurrentDownloadMask();
    if (((i & 0x1) != 0) && ((paramInt & 0x1) != 0) && (this.photoDownloadQueue.isEmpty()))
      MessagesStorage.getInstance().getDownloadQueue(1);
    if (((i & 0x2) != 0) && ((paramInt & 0x2) != 0) && (this.audioDownloadQueue.isEmpty()))
      MessagesStorage.getInstance().getDownloadQueue(2);
    if (((i & 0x4) != 0) && ((paramInt & 0x4) != 0) && (this.videoDownloadQueue.isEmpty()))
      MessagesStorage.getInstance().getDownloadQueue(4);
    if (((i & 0x8) != 0) && ((paramInt & 0x8) != 0) && (this.documentDownloadQueue.isEmpty()))
      MessagesStorage.getInstance().getDownloadQueue(8);
    if (((i & 0x10) != 0) && ((paramInt & 0x10) != 0) && (this.musicDownloadQueue.isEmpty()))
      MessagesStorage.getInstance().getDownloadQueue(16);
    if (((i & 0x20) != 0) && ((paramInt & 0x20) != 0) && (this.gifDownloadQueue.isEmpty()))
      MessagesStorage.getInstance().getDownloadQueue(32);
  }

  public void onAccuracyChanged(Sensor paramSensor, int paramInt)
  {
  }

  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == -1)
    {
      if ((isPlayingAudio(getPlayingMessageObject())) && (!isAudioPaused()))
        pauseAudio(getPlayingMessageObject());
      this.hasAudioFocus = 0;
      this.audioFocus = 0;
    }
    while (true)
    {
      setPlayerVolume();
      return;
      if (paramInt == 1)
      {
        this.audioFocus = 2;
        if (!this.resumeAudioOnFocusGain)
          continue;
        this.resumeAudioOnFocusGain = false;
        if ((!isPlayingAudio(getPlayingMessageObject())) || (!isAudioPaused()))
          continue;
        playAudio(getPlayingMessageObject());
        continue;
      }
      if (paramInt == -3)
      {
        this.audioFocus = 1;
        continue;
      }
      if (paramInt != -2)
        continue;
      this.audioFocus = 0;
      if ((!isPlayingAudio(getPlayingMessageObject())) || (isAudioPaused()))
        continue;
      pauseAudio(getPlayingMessageObject());
      this.resumeAudioOnFocusGain = true;
    }
  }

  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (!this.sensorsStarted);
    label1550: 
    while (true)
    {
      return;
      if (VoIPService.getSharedInstance() != null)
        continue;
      label95: float f;
      label252: boolean bool;
      if (paramSensorEvent.sensor == this.proximitySensor)
      {
        FileLog.e("proximity changed to " + paramSensorEvent.values[0]);
        if (this.lastProximityValue == -100.0F)
        {
          this.lastProximityValue = paramSensorEvent.values[0];
          if (this.proximityHasDifferentValues)
            this.proximityTouched = isNearToSensor(paramSensorEvent.values[0]);
          if ((paramSensorEvent.sensor == this.linearSensor) || (paramSensorEvent.sensor == this.gravitySensor) || (paramSensorEvent.sensor == this.accelerometerSensor))
          {
            f = this.gravity[0] * this.linearAcceleration[0] + this.gravity[1] * this.linearAcceleration[1] + this.gravity[2] * this.linearAcceleration[2];
            if (this.raisedToBack != 6)
            {
              if ((f <= 0.0F) || (this.previousAccValue <= 0.0F))
                break label1106;
              if ((f <= 15.0F) || (this.raisedToBack != 0))
                break label1044;
              if ((this.raisedToTop < 6) && (!this.proximityTouched))
              {
                this.raisedToTop += 1;
                if (this.raisedToTop == 6)
                  this.countLess = 0;
              }
            }
            this.previousAccValue = f;
            if ((this.gravityFast[1] <= 2.5F) || (Math.abs(this.gravityFast[2]) >= 4.0F) || (Math.abs(this.gravityFast[0]) <= 1.5F))
              break label1250;
            bool = true;
            label306: this.accelerometerVertical = bool;
          }
          if ((this.raisedToBack != 6) || (!this.accelerometerVertical) || (!this.proximityTouched) || (NotificationsController.getInstance().audioManager.isWiredHeadsetOn()))
            break label1335;
          FileLog.e("sensor values reached");
          if ((this.playingMessageObject != null) || (this.recordStartRunnable != null) || (this.recordingAudio != null) || (PhotoViewer.getInstance().isVisible()) || (!ApplicationLoader.isScreenOn) || (this.inputFieldHasText) || (!this.allowStartRecord) || (this.raiseChat == null) || (this.callInProgress))
            break label1256;
          if (!this.raiseToEarRecord)
          {
            FileLog.e("start record");
            this.useFrontSpeaker = true;
            if (!this.raiseChat.playFirstUnreadVoiceMessage())
            {
              this.raiseToEarRecord = true;
              this.useFrontSpeaker = false;
              startRecording(this.raiseChat.getDialogId(), null);
            }
            this.ignoreOnPause = true;
            if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld()))
              this.proximityWakeLock.acquire();
          }
          label503: this.raisedToBack = 0;
          this.raisedToTop = 0;
          this.countLess = 0;
        }
      }
      while (true)
      {
        if ((this.timeSinceRaise == 0L) || (this.raisedToBack != 6) || (Math.abs(System.currentTimeMillis() - this.timeSinceRaise) <= 1000L))
          break label1550;
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.countLess = 0;
        this.timeSinceRaise = 0L;
        return;
        if (this.lastProximityValue == paramSensorEvent.values[0])
          break;
        this.proximityHasDifferentValues = true;
        break;
        if (paramSensorEvent.sensor == this.accelerometerSensor)
        {
          double d1;
          if (this.lastTimestamp == 0L)
            d1 = 0.9800000190734863D;
          while (true)
          {
            this.lastTimestamp = paramSensorEvent.timestamp;
            this.gravity[0] = (float)(this.gravity[0] * d1 + (1.0D - d1) * paramSensorEvent.values[0]);
            this.gravity[1] = (float)(this.gravity[1] * d1 + (1.0D - d1) * paramSensorEvent.values[1]);
            arrayOfFloat1 = this.gravity;
            double d2 = this.gravity[2];
            arrayOfFloat1[2] = (float)((1.0D - d1) * paramSensorEvent.values[2] + d2 * d1);
            this.gravityFast[0] = (0.8F * this.gravity[0] + 0.2F * paramSensorEvent.values[0]);
            this.gravityFast[1] = (0.8F * this.gravity[1] + 0.2F * paramSensorEvent.values[1]);
            this.gravityFast[2] = (0.8F * this.gravity[2] + 0.2F * paramSensorEvent.values[2]);
            this.linearAcceleration[0] = (paramSensorEvent.values[0] - this.gravity[0]);
            this.linearAcceleration[1] = (paramSensorEvent.values[1] - this.gravity[1]);
            this.linearAcceleration[2] = (paramSensorEvent.values[2] - this.gravity[2]);
            break;
            d1 = 1.0D / (1.0D + (paramSensorEvent.timestamp - this.lastTimestamp) / 1000000000.0D);
          }
        }
        if (paramSensorEvent.sensor == this.linearSensor)
        {
          this.linearAcceleration[0] = paramSensorEvent.values[0];
          this.linearAcceleration[1] = paramSensorEvent.values[1];
          this.linearAcceleration[2] = paramSensorEvent.values[2];
          break label95;
        }
        if (paramSensorEvent.sensor != this.gravitySensor)
          break label95;
        float[] arrayOfFloat1 = this.gravityFast;
        float[] arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[0];
        arrayOfFloat2[0] = f;
        arrayOfFloat1[0] = f;
        arrayOfFloat1 = this.gravityFast;
        arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[1];
        arrayOfFloat2[1] = f;
        arrayOfFloat1[1] = f;
        arrayOfFloat1 = this.gravityFast;
        arrayOfFloat2 = this.gravity;
        f = paramSensorEvent.values[2];
        arrayOfFloat2[2] = f;
        arrayOfFloat1[2] = f;
        break label95;
        label1044: if (f < 15.0F)
          this.countLess += 1;
        if ((this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0))
          break label252;
        this.raisedToBack = 0;
        this.raisedToTop = 0;
        this.countLess = 0;
        break label252;
        label1106: if ((f >= 0.0F) || (this.previousAccValue >= 0.0F))
          break label252;
        if ((this.raisedToTop == 6) && (f < -15.0F))
        {
          if (this.raisedToBack >= 6)
            break label252;
          this.raisedToBack += 1;
          if (this.raisedToBack != 6)
            break label252;
          this.raisedToTop = 0;
          this.countLess = 0;
          this.timeSinceRaise = System.currentTimeMillis();
          break label252;
        }
        if (f > -15.0F)
          this.countLess += 1;
        if ((this.countLess != 10) && (this.raisedToTop == 6) && (this.raisedToBack == 0))
          break label252;
        this.raisedToTop = 0;
        this.raisedToBack = 0;
        this.countLess = 0;
        break label252;
        label1250: bool = false;
        break label306;
        label1256: if ((this.playingMessageObject == null) || (!this.playingMessageObject.isVoice()) || (this.useFrontSpeaker))
          break label503;
        FileLog.e("start listen");
        if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld()))
          this.proximityWakeLock.acquire();
        this.useFrontSpeaker = true;
        startAudioAgain(false);
        this.ignoreOnPause = true;
        break label503;
        label1335: if (this.proximityTouched)
        {
          if ((this.playingMessageObject == null) || (!this.playingMessageObject.isVoice()) || (this.useFrontSpeaker))
            continue;
          FileLog.e("start listen by proximity only");
          if ((this.proximityHasDifferentValues) && (this.proximityWakeLock != null) && (!this.proximityWakeLock.isHeld()))
            this.proximityWakeLock.acquire();
          this.useFrontSpeaker = true;
          startAudioAgain(false);
          this.ignoreOnPause = true;
          continue;
        }
        if (this.proximityTouched)
          continue;
        if (this.raiseToEarRecord)
        {
          FileLog.e("stop record");
          stopRecording(2);
          this.raiseToEarRecord = false;
          this.ignoreOnPause = false;
          if ((!this.proximityHasDifferentValues) || (this.proximityWakeLock == null) || (!this.proximityWakeLock.isHeld()))
            continue;
          this.proximityWakeLock.release();
          continue;
        }
        if (!this.useFrontSpeaker)
          continue;
        FileLog.e("stop listen");
        this.useFrontSpeaker = false;
        startAudioAgain(true);
        this.ignoreOnPause = false;
        if ((!this.proximityHasDifferentValues) || (this.proximityWakeLock == null) || (!this.proximityWakeLock.isHeld()))
          continue;
        this.proximityWakeLock.release();
      }
    }
  }

  public boolean pauseAudio(MessageObject paramMessageObject)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId())))
      return false;
    stopProgressTimer();
    try
    {
      if (this.audioPlayer != null)
        this.audioPlayer.pause();
      while (true)
      {
        this.isPaused = true;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
        return true;
        if (this.audioTrackPlayer == null)
          continue;
        this.audioTrackPlayer.pause();
      }
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e(paramMessageObject);
      this.isPaused = false;
    }
    return false;
  }

  // ERROR //
  public boolean playAudio(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +5 -> 6
    //   4: iconst_0
    //   5: ireturn
    //   6: aload_0
    //   7: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   10: ifnonnull +10 -> 20
    //   13: aload_0
    //   14: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   17: ifnull +54 -> 71
    //   20: aload_0
    //   21: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   24: ifnull +47 -> 71
    //   27: aload_1
    //   28: invokevirtual 1165	org/vidogram/messenger/MessageObject:getId	()I
    //   31: aload_0
    //   32: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   35: invokevirtual 1165	org/vidogram/messenger/MessageObject:getId	()I
    //   38: if_icmpne +33 -> 71
    //   41: aload_0
    //   42: getfield 450	org/vidogram/messenger/MediaController:isPaused	Z
    //   45: ifeq +9 -> 54
    //   48: aload_0
    //   49: aload_1
    //   50: invokevirtual 2363	org/vidogram/messenger/MediaController:resumeAudio	(Lorg/vidogram/messenger/MessageObject;)Z
    //   53: pop
    //   54: aload_0
    //   55: getfield 430	org/vidogram/messenger/MediaController:raiseToSpeak	Z
    //   58: ifne +11 -> 69
    //   61: aload_0
    //   62: aload_0
    //   63: getfield 2047	org/vidogram/messenger/MediaController:raiseChat	Lorg/vidogram/ui/ChatActivity;
    //   66: invokevirtual 2366	org/vidogram/messenger/MediaController:startRaiseToEarSensors	(Lorg/vidogram/ui/ChatActivity;)V
    //   69: iconst_1
    //   70: ireturn
    //   71: aload_1
    //   72: invokevirtual 2196	org/vidogram/messenger/MessageObject:isOut	()Z
    //   75: ifne +30 -> 105
    //   78: aload_1
    //   79: invokevirtual 2193	org/vidogram/messenger/MessageObject:isContentUnread	()Z
    //   82: ifeq +23 -> 105
    //   85: aload_1
    //   86: getfield 1050	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   89: getfield 2166	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   92: getfield 2171	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   95: ifne +10 -> 105
    //   98: invokestatic 2151	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   101: aload_1
    //   102: invokevirtual 2369	org/vidogram/messenger/MessagesController:markMessageContentAsRead	(Lorg/vidogram/messenger/MessageObject;)V
    //   105: aload_0
    //   106: getfield 1746	org/vidogram/messenger/MediaController:playMusicAgain	Z
    //   109: ifne +220 -> 329
    //   112: iconst_1
    //   113: istore 4
    //   115: aload_0
    //   116: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   119: ifnull +6 -> 125
    //   122: iconst_0
    //   123: istore 4
    //   125: aload_0
    //   126: iload 4
    //   128: iconst_0
    //   129: invokevirtual 1703	org/vidogram/messenger/MediaController:cleanupPlayer	(ZZ)V
    //   132: aload_0
    //   133: iconst_0
    //   134: putfield 1746	org/vidogram/messenger/MediaController:playMusicAgain	Z
    //   137: aload_1
    //   138: getfield 1050	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   141: getfield 1055	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   144: ifnull +965 -> 1109
    //   147: aload_1
    //   148: getfield 1050	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   151: getfield 1055	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   154: invokevirtual 1058	java/lang/String:length	()I
    //   157: ifle +952 -> 1109
    //   160: new 1060	java/io/File
    //   163: dup
    //   164: aload_1
    //   165: getfield 1050	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   168: getfield 1055	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   171: invokespecial 1061	java/io/File:<init>	(Ljava/lang/String;)V
    //   174: astore 6
    //   176: aload 6
    //   178: astore 5
    //   180: aload 6
    //   182: invokevirtual 1064	java/io/File:exists	()Z
    //   185: ifne +6 -> 191
    //   188: aconst_null
    //   189: astore 5
    //   191: aload 5
    //   193: ifnull +142 -> 335
    //   196: aload 5
    //   198: astore 6
    //   200: aload 6
    //   202: ifnull +170 -> 372
    //   205: aload 6
    //   207: aload 5
    //   209: if_acmpeq +163 -> 372
    //   212: aload 6
    //   214: invokevirtual 1064	java/io/File:exists	()Z
    //   217: ifne +155 -> 372
    //   220: aload_1
    //   221: invokevirtual 1067	org/vidogram/messenger/MessageObject:isMusic	()Z
    //   224: ifeq +148 -> 372
    //   227: invokestatic 1072	org/vidogram/messenger/FileLoader:getInstance	()Lorg/vidogram/messenger/FileLoader;
    //   230: aload_1
    //   231: invokevirtual 1076	org/vidogram/messenger/MessageObject:getDocument	()Lorg/vidogram/tgnet/TLRPC$Document;
    //   234: iconst_0
    //   235: iconst_0
    //   236: invokevirtual 1080	org/vidogram/messenger/FileLoader:loadFile	(Lorg/vidogram/tgnet/TLRPC$Document;ZZ)V
    //   239: aload_0
    //   240: iconst_1
    //   241: putfield 2035	org/vidogram/messenger/MediaController:downloadingCurrentMessage	Z
    //   244: aload_0
    //   245: iconst_0
    //   246: putfield 450	org/vidogram/messenger/MediaController:isPaused	Z
    //   249: aload_0
    //   250: iconst_0
    //   251: putfield 456	org/vidogram/messenger/MediaController:lastProgress	I
    //   254: aload_0
    //   255: lconst_0
    //   256: putfield 796	org/vidogram/messenger/MediaController:lastPlayPcm	J
    //   259: aload_0
    //   260: aconst_null
    //   261: putfield 2024	org/vidogram/messenger/MediaController:audioInfo	Lorg/vidogram/messenger/audioinfo/AudioInfo;
    //   264: aload_0
    //   265: aload_1
    //   266: putfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   269: aload_0
    //   270: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   273: invokevirtual 1076	org/vidogram/messenger/MessageObject:getDocument	()Lorg/vidogram/tgnet/TLRPC$Document;
    //   276: ifnull +71 -> 347
    //   279: new 1910	android/content/Intent
    //   282: dup
    //   283: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   286: ldc_w 2041
    //   289: invokespecial 1915	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   292: astore_1
    //   293: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   296: aload_1
    //   297: invokevirtual 1947	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   300: pop
    //   301: invokestatic 1727	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   304: getstatic 1737	org/vidogram/messenger/NotificationCenter:audioPlayStateChanged	I
    //   307: iconst_1
    //   308: anewarray 4	java/lang/Object
    //   311: dup
    //   312: iconst_0
    //   313: aload_0
    //   314: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   317: invokevirtual 1165	org/vidogram/messenger/MessageObject:getId	()I
    //   320: invokestatic 1569	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   323: aastore
    //   324: invokevirtual 1734	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   327: iconst_1
    //   328: ireturn
    //   329: iconst_0
    //   330: istore 4
    //   332: goto -217 -> 115
    //   335: aload_1
    //   336: getfield 1050	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   339: invokestatic 1084	org/vidogram/messenger/FileLoader:getPathToMessage	(Lorg/vidogram/tgnet/TLRPC$Message;)Ljava/io/File;
    //   342: astore 6
    //   344: goto -144 -> 200
    //   347: new 1910	android/content/Intent
    //   350: dup
    //   351: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   354: ldc_w 2041
    //   357: invokespecial 1915	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   360: astore_1
    //   361: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   364: aload_1
    //   365: invokevirtual 2045	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   368: pop
    //   369: goto -68 -> 301
    //   372: aload_0
    //   373: iconst_0
    //   374: putfield 2035	org/vidogram/messenger/MediaController:downloadingCurrentMessage	Z
    //   377: aload_1
    //   378: invokevirtual 1067	org/vidogram/messenger/MessageObject:isMusic	()Z
    //   381: ifeq +7 -> 388
    //   384: aload_0
    //   385: invokespecial 2371	org/vidogram/messenger/MediaController:checkIsNextMusicFileDownloaded	()V
    //   388: aload_0
    //   389: aload 6
    //   391: invokevirtual 1605	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   394: invokespecial 2373	org/vidogram/messenger/MediaController:isOpusFile	(Ljava/lang/String;)I
    //   397: iconst_1
    //   398: if_icmpne +369 -> 767
    //   401: aload_0
    //   402: getfield 468	org/vidogram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   405: invokevirtual 951	java/util/ArrayList:clear	()V
    //   408: aload_0
    //   409: getfield 470	org/vidogram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   412: invokevirtual 951	java/util/ArrayList:clear	()V
    //   415: aload_0
    //   416: getfield 484	org/vidogram/messenger/MediaController:playerObjectSync	Ljava/lang/Object;
    //   419: astore 5
    //   421: aload 5
    //   423: monitorenter
    //   424: aload_0
    //   425: iconst_3
    //   426: putfield 462	org/vidogram/messenger/MediaController:ignoreFirstProgress	I
    //   429: new 2375	java/util/concurrent/Semaphore
    //   432: dup
    //   433: iconst_0
    //   434: invokespecial 2376	java/util/concurrent/Semaphore:<init>	(I)V
    //   437: astore 7
    //   439: iconst_1
    //   440: anewarray 1883	java/lang/Boolean
    //   443: astore 8
    //   445: aload_0
    //   446: getfield 625	org/vidogram/messenger/MediaController:fileDecodingQueue	Lorg/vidogram/messenger/DispatchQueue;
    //   449: new 28	org/vidogram/messenger/MediaController$13
    //   452: dup
    //   453: aload_0
    //   454: aload 8
    //   456: aload 6
    //   458: aload 7
    //   460: invokespecial 2379	org/vidogram/messenger/MediaController$13:<init>	(Lorg/vidogram/messenger/MediaController;[Ljava/lang/Boolean;Ljava/io/File;Ljava/util/concurrent/Semaphore;)V
    //   463: invokevirtual 1013	org/vidogram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   466: aload 7
    //   468: invokevirtual 2380	java/util/concurrent/Semaphore:acquire	()V
    //   471: aload 8
    //   473: iconst_0
    //   474: aaload
    //   475: invokevirtual 2110	java/lang/Boolean:booleanValue	()Z
    //   478: istore 4
    //   480: iload 4
    //   482: ifne +14 -> 496
    //   485: aload 5
    //   487: monitorexit
    //   488: iconst_0
    //   489: ireturn
    //   490: astore_1
    //   491: aload 5
    //   493: monitorexit
    //   494: aload_1
    //   495: athrow
    //   496: aload_0
    //   497: aload_0
    //   498: invokespecial 2382	org/vidogram/messenger/MediaController:getTotalPcmDuration	()J
    //   501: putfield 800	org/vidogram/messenger/MediaController:currentTotalPcmDuration	J
    //   504: aload_0
    //   505: getfield 985	org/vidogram/messenger/MediaController:useFrontSpeaker	Z
    //   508: ifeq +210 -> 718
    //   511: iconst_0
    //   512: istore_2
    //   513: aload_0
    //   514: new 526	android/media/AudioTrack
    //   517: dup
    //   518: iload_2
    //   519: ldc_w 524
    //   522: iconst_4
    //   523: iconst_2
    //   524: aload_0
    //   525: getfield 458	org/vidogram/messenger/MediaController:playerBufferSize	I
    //   528: iconst_1
    //   529: invokespecial 2385	android/media/AudioTrack:<init>	(IIIIII)V
    //   532: putfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   535: aload_0
    //   536: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   539: fconst_1
    //   540: fconst_1
    //   541: invokevirtual 1877	android/media/AudioTrack:setStereoVolume	(FF)I
    //   544: pop
    //   545: aload_0
    //   546: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   549: new 30	org/vidogram/messenger/MediaController$14
    //   552: dup
    //   553: aload_0
    //   554: invokespecial 2386	org/vidogram/messenger/MediaController$14:<init>	(Lorg/vidogram/messenger/MediaController;)V
    //   557: invokevirtual 2390	android/media/AudioTrack:setPlaybackPositionUpdateListener	(Landroid/media/AudioTrack$OnPlaybackPositionUpdateListener;)V
    //   560: aload_0
    //   561: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   564: invokevirtual 2393	android/media/AudioTrack:play	()V
    //   567: aload 5
    //   569: monitorexit
    //   570: aload_0
    //   571: aload_1
    //   572: invokespecial 2395	org/vidogram/messenger/MediaController:checkAudioFocus	(Lorg/vidogram/messenger/MessageObject;)V
    //   575: aload_0
    //   576: invokespecial 2238	org/vidogram/messenger/MediaController:setPlayerVolume	()V
    //   579: aload_0
    //   580: iconst_0
    //   581: putfield 450	org/vidogram/messenger/MediaController:isPaused	Z
    //   584: aload_0
    //   585: iconst_0
    //   586: putfield 456	org/vidogram/messenger/MediaController:lastProgress	I
    //   589: aload_0
    //   590: lconst_0
    //   591: putfield 796	org/vidogram/messenger/MediaController:lastPlayPcm	J
    //   594: aload_0
    //   595: aload_1
    //   596: putfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   599: aload_0
    //   600: getfield 430	org/vidogram/messenger/MediaController:raiseToSpeak	Z
    //   603: ifne +11 -> 614
    //   606: aload_0
    //   607: aload_0
    //   608: getfield 2047	org/vidogram/messenger/MediaController:raiseChat	Lorg/vidogram/ui/ChatActivity;
    //   611: invokevirtual 2366	org/vidogram/messenger/MediaController:startRaiseToEarSensors	(Lorg/vidogram/ui/ChatActivity;)V
    //   614: aload_0
    //   615: aload_0
    //   616: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   619: invokespecial 2397	org/vidogram/messenger/MediaController:startProgressTimer	(Lorg/vidogram/messenger/MessageObject;)V
    //   622: invokestatic 1727	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   625: getstatic 2400	org/vidogram/messenger/NotificationCenter:audioDidStarted	I
    //   628: iconst_1
    //   629: anewarray 4	java/lang/Object
    //   632: dup
    //   633: iconst_0
    //   634: aload_1
    //   635: aastore
    //   636: invokevirtual 1734	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   639: aload_0
    //   640: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   643: ifnull +396 -> 1039
    //   646: aload_0
    //   647: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   650: getfield 1719	org/vidogram/messenger/MessageObject:audioProgress	F
    //   653: fconst_0
    //   654: fcmpl
    //   655: ifeq +29 -> 684
    //   658: aload_0
    //   659: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   662: invokevirtual 2403	android/media/MediaPlayer:getDuration	()I
    //   665: i2f
    //   666: aload_0
    //   667: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   670: getfield 1719	org/vidogram/messenger/MessageObject:audioProgress	F
    //   673: fmul
    //   674: f2i
    //   675: istore_2
    //   676: aload_0
    //   677: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   680: iload_2
    //   681: invokevirtual 2405	android/media/MediaPlayer:seekTo	(I)V
    //   684: aload_0
    //   685: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   688: invokevirtual 1067	org/vidogram/messenger/MessageObject:isMusic	()Z
    //   691: ifeq +393 -> 1084
    //   694: new 1910	android/content/Intent
    //   697: dup
    //   698: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   701: ldc_w 2041
    //   704: invokespecial 1915	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   707: astore_1
    //   708: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   711: aload_1
    //   712: invokevirtual 1947	android/content/Context:startService	(Landroid/content/Intent;)Landroid/content/ComponentName;
    //   715: pop
    //   716: iconst_1
    //   717: ireturn
    //   718: iconst_3
    //   719: istore_2
    //   720: goto -207 -> 513
    //   723: astore_1
    //   724: aload_1
    //   725: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   728: aload_0
    //   729: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   732: ifnull +30 -> 762
    //   735: aload_0
    //   736: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   739: invokevirtual 1744	android/media/AudioTrack:release	()V
    //   742: aload_0
    //   743: aconst_null
    //   744: putfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   747: aload_0
    //   748: iconst_0
    //   749: putfield 450	org/vidogram/messenger/MediaController:isPaused	Z
    //   752: aload_0
    //   753: aconst_null
    //   754: putfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   757: aload_0
    //   758: iconst_0
    //   759: putfield 2035	org/vidogram/messenger/MediaController:downloadingCurrentMessage	Z
    //   762: aload 5
    //   764: monitorexit
    //   765: iconst_0
    //   766: ireturn
    //   767: aload_0
    //   768: new 1708	android/media/MediaPlayer
    //   771: dup
    //   772: invokespecial 2406	android/media/MediaPlayer:<init>	()V
    //   775: putfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   778: aload_0
    //   779: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   782: astore 5
    //   784: aload_0
    //   785: getfield 985	org/vidogram/messenger/MediaController:useFrontSpeaker	Z
    //   788: ifeq +162 -> 950
    //   791: iconst_0
    //   792: istore_2
    //   793: aload 5
    //   795: iload_2
    //   796: invokevirtual 2409	android/media/MediaPlayer:setAudioStreamType	(I)V
    //   799: aload_0
    //   800: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   803: aload 6
    //   805: invokevirtual 1605	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   808: invokevirtual 2410	android/media/MediaPlayer:setDataSource	(Ljava/lang/String;)V
    //   811: aload_0
    //   812: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   815: new 32	org/vidogram/messenger/MediaController$15
    //   818: dup
    //   819: aload_0
    //   820: aload_1
    //   821: invokespecial 2411	org/vidogram/messenger/MediaController$15:<init>	(Lorg/vidogram/messenger/MediaController;Lorg/vidogram/messenger/MessageObject;)V
    //   824: invokevirtual 2415	android/media/MediaPlayer:setOnCompletionListener	(Landroid/media/MediaPlayer$OnCompletionListener;)V
    //   827: aload_0
    //   828: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   831: invokevirtual 2418	android/media/MediaPlayer:prepare	()V
    //   834: aload_0
    //   835: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   838: invokevirtual 2419	android/media/MediaPlayer:start	()V
    //   841: aload_1
    //   842: invokevirtual 983	org/vidogram/messenger/MessageObject:isVoice	()Z
    //   845: ifeq +110 -> 955
    //   848: aload_0
    //   849: aconst_null
    //   850: putfield 2024	org/vidogram/messenger/MediaController:audioInfo	Lorg/vidogram/messenger/audioinfo/AudioInfo;
    //   853: aload_0
    //   854: getfield 468	org/vidogram/messenger/MediaController:playlist	Ljava/util/ArrayList;
    //   857: invokevirtual 951	java/util/ArrayList:clear	()V
    //   860: aload_0
    //   861: getfield 470	org/vidogram/messenger/MediaController:shuffledPlaylist	Ljava/util/ArrayList;
    //   864: invokevirtual 951	java/util/ArrayList:clear	()V
    //   867: goto -297 -> 570
    //   870: astore_1
    //   871: aload_1
    //   872: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   875: invokestatic 1727	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   878: astore_1
    //   879: getstatic 1737	org/vidogram/messenger/NotificationCenter:audioPlayStateChanged	I
    //   882: istore_3
    //   883: aload_0
    //   884: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   887: ifnull +90 -> 977
    //   890: aload_0
    //   891: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   894: invokevirtual 1165	org/vidogram/messenger/MessageObject:getId	()I
    //   897: istore_2
    //   898: aload_1
    //   899: iload_3
    //   900: iconst_1
    //   901: anewarray 4	java/lang/Object
    //   904: dup
    //   905: iconst_0
    //   906: iload_2
    //   907: invokestatic 1569	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   910: aastore
    //   911: invokevirtual 1734	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   914: aload_0
    //   915: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   918: ifnull -914 -> 4
    //   921: aload_0
    //   922: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   925: invokevirtual 1713	android/media/MediaPlayer:release	()V
    //   928: aload_0
    //   929: aconst_null
    //   930: putfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   933: aload_0
    //   934: iconst_0
    //   935: putfield 450	org/vidogram/messenger/MediaController:isPaused	Z
    //   938: aload_0
    //   939: aconst_null
    //   940: putfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   943: aload_0
    //   944: iconst_0
    //   945: putfield 2035	org/vidogram/messenger/MediaController:downloadingCurrentMessage	Z
    //   948: iconst_0
    //   949: ireturn
    //   950: iconst_3
    //   951: istore_2
    //   952: goto -159 -> 793
    //   955: aload_0
    //   956: aload 6
    //   958: invokestatic 2424	org/vidogram/messenger/audioinfo/AudioInfo:getAudioInfo	(Ljava/io/File;)Lorg/vidogram/messenger/audioinfo/AudioInfo;
    //   961: putfield 2024	org/vidogram/messenger/MediaController:audioInfo	Lorg/vidogram/messenger/audioinfo/AudioInfo;
    //   964: goto -394 -> 570
    //   967: astore 5
    //   969: aload 5
    //   971: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   974: goto -404 -> 570
    //   977: iconst_0
    //   978: istore_2
    //   979: goto -81 -> 898
    //   982: astore_1
    //   983: aload_0
    //   984: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   987: fconst_0
    //   988: putfield 1719	org/vidogram/messenger/MessageObject:audioProgress	F
    //   991: aload_0
    //   992: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   995: iconst_0
    //   996: putfield 1722	org/vidogram/messenger/MessageObject:audioProgressSec	I
    //   999: invokestatic 1727	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   1002: getstatic 1730	org/vidogram/messenger/NotificationCenter:audioProgressDidChanged	I
    //   1005: iconst_2
    //   1006: anewarray 4	java/lang/Object
    //   1009: dup
    //   1010: iconst_0
    //   1011: aload_0
    //   1012: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1015: invokevirtual 1165	org/vidogram/messenger/MessageObject:getId	()I
    //   1018: invokestatic 1569	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1021: aastore
    //   1022: dup
    //   1023: iconst_1
    //   1024: iconst_0
    //   1025: invokestatic 1569	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1028: aastore
    //   1029: invokevirtual 1734	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   1032: aload_1
    //   1033: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1036: goto -352 -> 684
    //   1039: aload_0
    //   1040: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   1043: ifnull -359 -> 684
    //   1046: aload_0
    //   1047: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1050: getfield 1719	org/vidogram/messenger/MessageObject:audioProgress	F
    //   1053: fconst_1
    //   1054: fcmpl
    //   1055: ifne +11 -> 1066
    //   1058: aload_0
    //   1059: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1062: fconst_0
    //   1063: putfield 1719	org/vidogram/messenger/MessageObject:audioProgress	F
    //   1066: aload_0
    //   1067: getfield 625	org/vidogram/messenger/MediaController:fileDecodingQueue	Lorg/vidogram/messenger/DispatchQueue;
    //   1070: new 34	org/vidogram/messenger/MediaController$16
    //   1073: dup
    //   1074: aload_0
    //   1075: invokespecial 2425	org/vidogram/messenger/MediaController$16:<init>	(Lorg/vidogram/messenger/MediaController;)V
    //   1078: invokevirtual 1013	org/vidogram/messenger/DispatchQueue:postRunnable	(Ljava/lang/Runnable;)V
    //   1081: goto -397 -> 684
    //   1084: new 1910	android/content/Intent
    //   1087: dup
    //   1088: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1091: ldc_w 2041
    //   1094: invokespecial 1915	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   1097: astore_1
    //   1098: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   1101: aload_1
    //   1102: invokevirtual 2045	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   1105: pop
    //   1106: goto -390 -> 716
    //   1109: aconst_null
    //   1110: astore 5
    //   1112: goto -921 -> 191
    //
    // Exception table:
    //   from	to	target	type
    //   424	480	490	finally
    //   485	488	490	finally
    //   491	494	490	finally
    //   496	511	490	finally
    //   513	567	490	finally
    //   567	570	490	finally
    //   724	762	490	finally
    //   762	765	490	finally
    //   424	480	723	java/lang/Exception
    //   496	511	723	java/lang/Exception
    //   513	567	723	java/lang/Exception
    //   767	791	870	java/lang/Exception
    //   793	867	870	java/lang/Exception
    //   969	974	870	java/lang/Exception
    //   955	964	967	java/lang/Exception
    //   646	684	982	java/lang/Exception
  }

  public void playMessageAtIndex(int paramInt)
  {
    if ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= this.playlist.size()))
      return;
    this.currentPlaylistNum = paramInt;
    this.playMusicAgain = true;
    playAudio((MessageObject)this.playlist.get(this.currentPlaylistNum));
  }

  public void playNextMessage()
  {
    playNextMessage(false);
  }

  public void playPreviousMessage()
  {
    ArrayList localArrayList;
    if (this.shuffleMusic)
    {
      localArrayList = this.shuffledPlaylist;
      if (!localArrayList.isEmpty())
        break label28;
    }
    label28: 
    do
    {
      return;
      localArrayList = this.playlist;
      break;
      MessageObject localMessageObject = (MessageObject)localArrayList.get(this.currentPlaylistNum);
      if (localMessageObject.audioProgressSec > 10)
      {
        getInstance().seekToProgress(localMessageObject, 0.0F);
        return;
      }
      this.currentPlaylistNum -= 1;
      if (this.currentPlaylistNum >= 0)
        continue;
      this.currentPlaylistNum = (localArrayList.size() - 1);
    }
    while ((this.currentPlaylistNum < 0) || (this.currentPlaylistNum >= localArrayList.size()));
    this.playMusicAgain = true;
    playAudio((MessageObject)localArrayList.get(this.currentPlaylistNum));
  }

  protected void processDownloadObjects(int paramInt, ArrayList<DownloadObject> paramArrayList)
  {
    if (paramArrayList.isEmpty())
      return;
    ArrayList localArrayList;
    if (paramInt == 1)
      localArrayList = this.photoDownloadQueue;
    while (true)
    {
      label19: int i = 0;
      label21: DownloadObject localDownloadObject;
      String str;
      if (i < paramArrayList.size())
      {
        localDownloadObject = (DownloadObject)paramArrayList.get(i);
        if (!(localDownloadObject.object instanceof TLRPC.Document))
          break label155;
        str = FileLoader.getAttachFileName((TLRPC.Document)localDownloadObject.object);
        label63: if (!this.downloadQueueKeys.containsKey(str))
          break label168;
      }
      label263: 
      while (true)
      {
        i += 1;
        break label21;
        break;
        if (paramInt == 2)
        {
          localArrayList = this.audioDownloadQueue;
          break label19;
        }
        if (paramInt == 4)
        {
          localArrayList = this.videoDownloadQueue;
          break label19;
        }
        if (paramInt == 8)
        {
          localArrayList = this.documentDownloadQueue;
          break label19;
        }
        if (paramInt == 16)
        {
          localArrayList = this.musicDownloadQueue;
          break label19;
        }
        if (paramInt != 32)
          break label265;
        localArrayList = this.gifDownloadQueue;
        break label19;
        label155: str = FileLoader.getAttachFileName(localDownloadObject.object);
        break label63;
        label168: if ((localDownloadObject.object instanceof TLRPC.PhotoSize))
        {
          FileLoader.getInstance().loadFile((TLRPC.PhotoSize)localDownloadObject.object, null, false);
          paramInt = 1;
        }
        while (true)
        {
          if (paramInt == 0)
            break label263;
          localArrayList.add(localDownloadObject);
          this.downloadQueueKeys.put(str, localDownloadObject);
          break;
          if ((localDownloadObject.object instanceof TLRPC.Document))
          {
            TLRPC.Document localDocument = (TLRPC.Document)localDownloadObject.object;
            FileLoader.getInstance().loadFile(localDocument, false, false);
            paramInt = 1;
            continue;
          }
          paramInt = 0;
        }
      }
      label265: localArrayList = null;
    }
  }

  public void processMediaObserver(Uri paramUri)
  {
    while (true)
    {
      ArrayList localArrayList;
      try
      {
        Point localPoint = AndroidUtilities.getRealScreenSize();
        paramUri = ApplicationLoader.applicationContext.getContentResolver().query(paramUri, this.mediaProjections, null, null, "date_added DESC LIMIT 1");
        localArrayList = new ArrayList();
        if (paramUri == null)
          break label333;
        if (paramUri.moveToNext())
        {
          String str1 = paramUri.getString(0);
          Object localObject = paramUri.getString(1);
          String str2 = paramUri.getString(2);
          long l = paramUri.getLong(3);
          String str3 = paramUri.getString(4);
          if (Build.VERSION.SDK_INT < 16)
            break label355;
          j = paramUri.getInt(5);
          i = paramUri.getInt(6);
          if (((str1 != null) && (str1.toLowerCase().contains("screenshot"))) || ((localObject != null) && (((String)localObject).toLowerCase().contains("screenshot"))) || ((str2 != null) && (str2.toLowerCase().contains("screenshot"))))
            continue;
          if (str3 == null)
            continue;
          boolean bool = str3.toLowerCase().contains("screenshot");
          if (!bool)
            continue;
          if (j == 0)
            continue;
          int k = i;
          if (i != 0)
            continue;
          try
          {
            localObject = new BitmapFactory.Options();
            ((BitmapFactory.Options)localObject).inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str1, (BitmapFactory.Options)localObject);
            j = ((BitmapFactory.Options)localObject).outWidth;
            k = ((BitmapFactory.Options)localObject).outHeight;
            if ((j > 0) && (k > 0) && ((j != localPoint.x) || (k != localPoint.y)) && ((k != localPoint.x) || (j != localPoint.y)))
              continue;
            localArrayList.add(Long.valueOf(l));
          }
          catch (Exception localException)
          {
            localArrayList.add(Long.valueOf(l));
          }
          continue;
        }
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
        return;
      }
      paramUri.close();
      label333: if (localArrayList.isEmpty())
        continue;
      AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
      {
        public void run()
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
          MediaController.this.checkScreenshots(this.val$screenshotDates);
        }
      });
      return;
      label355: int i = 0;
      int j = 0;
    }
  }

  public void removeLoadingFileObserver(FileDownloadProgressListener paramFileDownloadProgressListener)
  {
    if (this.listenerInProgress)
      this.deleteLaterArray.add(paramFileDownloadProgressListener);
    String str;
    do
    {
      return;
      str = (String)this.observersByTag.get(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()));
    }
    while (str == null);
    ArrayList localArrayList = (ArrayList)this.loadingFileObservers.get(str);
    if (localArrayList != null)
    {
      int j;
      for (int i = 0; i < localArrayList.size(); i = j + 1)
      {
        WeakReference localWeakReference = (WeakReference)localArrayList.get(i);
        if (localWeakReference.get() != null)
        {
          j = i;
          if (localWeakReference.get() != paramFileDownloadProgressListener)
            continue;
        }
        localArrayList.remove(i);
        j = i - 1;
      }
      if (localArrayList.isEmpty())
        this.loadingFileObservers.remove(str);
    }
    this.observersByTag.remove(Integer.valueOf(paramFileDownloadProgressListener.getObserverTag()));
  }

  public boolean resumeAudio(MessageObject paramMessageObject)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId())))
      return false;
    try
    {
      startProgressTimer(paramMessageObject);
      if (this.audioPlayer != null)
        this.audioPlayer.start();
      while (true)
      {
        checkAudioFocus(paramMessageObject);
        this.isPaused = false;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.audioPlayStateChanged, new Object[] { Integer.valueOf(this.playingMessageObject.getId()) });
        return true;
        if (this.audioTrackPlayer == null)
          continue;
        this.audioTrackPlayer.play();
        checkPlayerQueue();
      }
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e(paramMessageObject);
    }
    return false;
  }

  public void scheduleVideoConvert(MessageObject paramMessageObject)
  {
    scheduleVideoConvert(paramMessageObject, false);
  }

  public boolean scheduleVideoConvert(MessageObject paramMessageObject, boolean paramBoolean)
  {
    boolean bool = true;
    if ((paramBoolean) && (!this.videoConvertQueue.isEmpty()))
      paramBoolean = false;
    do
    {
      return paramBoolean;
      if (paramBoolean)
        new File(paramMessageObject.messageOwner.attachPath).delete();
      this.videoConvertQueue.add(paramMessageObject);
      paramBoolean = bool;
    }
    while (this.videoConvertQueue.size() != 1);
    startVideoConvertFromQueue();
    return true;
  }

  public boolean seekToProgress(MessageObject paramMessageObject, float paramFloat)
  {
    if (((this.audioTrackPlayer == null) && (this.audioPlayer == null)) || (paramMessageObject == null) || (this.playingMessageObject == null) || ((this.playingMessageObject != null) && (this.playingMessageObject.getId() != paramMessageObject.getId())))
      return false;
    try
    {
      if (this.audioPlayer != null)
      {
        int i = (int)(this.audioPlayer.getDuration() * paramFloat);
        this.audioPlayer.seekTo(i);
        this.lastProgress = i;
      }
      else if (this.audioTrackPlayer != null)
      {
        seekOpusPlayer(paramFloat);
      }
    }
    catch (Exception paramMessageObject)
    {
      FileLog.e(paramMessageObject);
      return false;
    }
    return true;
  }

  public void setAllowStartRecord(boolean paramBoolean)
  {
    this.allowStartRecord = paramBoolean;
  }

  public void setInputFieldHasText(boolean paramBoolean)
  {
    this.inputFieldHasText = paramBoolean;
  }

  public void setLastEncryptedChatParams(long paramLong1, long paramLong2, TLRPC.EncryptedChat paramEncryptedChat, ArrayList<Long> paramArrayList)
  {
    this.lastSecretChatEnterTime = paramLong1;
    this.lastSecretChatLeaveTime = paramLong2;
    this.lastSecretChat = paramEncryptedChat;
    this.lastSecretChatVisibleMessages = paramArrayList;
  }

  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject)
  {
    return setPlaylist(paramArrayList, paramMessageObject, true);
  }

  public boolean setPlaylist(ArrayList<MessageObject> paramArrayList, MessageObject paramMessageObject, boolean paramBoolean)
  {
    boolean bool2 = true;
    if (this.playingMessageObject == paramMessageObject)
      return playAudio(paramMessageObject);
    if (!paramBoolean)
    {
      bool1 = true;
      this.forceLoopCurrentPlaylist = bool1;
      if (this.playlist.isEmpty())
        break label114;
    }
    label114: for (boolean bool1 = bool2; ; bool1 = false)
    {
      this.playMusicAgain = bool1;
      this.playlist.clear();
      int i = paramArrayList.size() - 1;
      while (i >= 0)
      {
        MessageObject localMessageObject = (MessageObject)paramArrayList.get(i);
        if (localMessageObject.isMusic())
          this.playlist.add(localMessageObject);
        i -= 1;
      }
      bool1 = false;
      break;
    }
    this.currentPlaylistNum = this.playlist.indexOf(paramMessageObject);
    if (this.currentPlaylistNum == -1)
    {
      this.playlist.clear();
      this.shuffledPlaylist.clear();
      this.currentPlaylistNum = this.playlist.size();
      this.playlist.add(paramMessageObject);
    }
    if (paramMessageObject.isMusic())
    {
      if (this.shuffleMusic)
      {
        buildShuffledPlayList();
        this.currentPlaylistNum = 0;
      }
      if (paramBoolean)
        SharedMediaQuery.loadMusic(paramMessageObject.getDialogId(), ((MessageObject)this.playlist.get(0)).getId());
    }
    return playAudio(paramMessageObject);
  }

  public void setVoiceMessagesPlaylist(ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    this.voiceMessagesPlaylist = paramArrayList;
    if (this.voiceMessagesPlaylist != null)
    {
      this.voiceMessagesPlaylistUnread = paramBoolean;
      this.voiceMessagesPlaylistMap = new HashMap();
      int i = 0;
      while (i < this.voiceMessagesPlaylist.size())
      {
        paramArrayList = (MessageObject)this.voiceMessagesPlaylist.get(i);
        this.voiceMessagesPlaylistMap.put(Integer.valueOf(paramArrayList.getId()), paramArrayList);
        i += 1;
      }
    }
  }

  public void startMediaObserver()
  {
    ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
    this.startObserverToken += 1;
    try
    {
      if (this.internalObserver == null)
      {
        localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
        localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        localObject = new ExternalObserver();
        this.externalObserver = ((ExternalObserver)localObject);
        localContentResolver.registerContentObserver(localUri, false, (ContentObserver)localObject);
      }
    }
    catch (Exception localException2)
    {
      try
      {
        while (true)
        {
          ContentResolver localContentResolver;
          Uri localUri;
          Object localObject;
          if (this.externalObserver == null)
          {
            localContentResolver = ApplicationLoader.applicationContext.getContentResolver();
            localUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            localObject = new InternalObserver();
            this.internalObserver = ((InternalObserver)localObject);
            localContentResolver.registerContentObserver(localUri, false, (ContentObserver)localObject);
          }
          return;
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        FileLog.e(localException2);
      }
    }
  }

  public void startRaiseToEarSensors(ChatActivity paramChatActivity)
  {
    if ((paramChatActivity == null) || ((this.accelerometerSensor == null) && ((this.gravitySensor == null) || (this.linearAcceleration == null))) || (this.proximitySensor == null));
    do
    {
      return;
      this.raiseChat = paramChatActivity;
    }
    while (((!this.raiseToSpeak) && ((this.playingMessageObject == null) || (!this.playingMessageObject.isVoice()))) || (this.sensorsStarted));
    paramChatActivity = this.gravity;
    float[] arrayOfFloat = this.gravity;
    this.gravity[2] = 0.0F;
    arrayOfFloat[1] = 0.0F;
    paramChatActivity[0] = 0.0F;
    paramChatActivity = this.linearAcceleration;
    arrayOfFloat = this.linearAcceleration;
    this.linearAcceleration[2] = 0.0F;
    arrayOfFloat[1] = 0.0F;
    paramChatActivity[0] = 0.0F;
    paramChatActivity = this.gravityFast;
    arrayOfFloat = this.gravityFast;
    this.gravityFast[2] = 0.0F;
    arrayOfFloat[1] = 0.0F;
    paramChatActivity[0] = 0.0F;
    this.lastTimestamp = 0L;
    this.previousAccValue = 0.0F;
    this.raisedToTop = 0;
    this.countLess = 0;
    this.raisedToBack = 0;
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (MediaController.this.gravitySensor != null)
          MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.gravitySensor, 30000);
        if (MediaController.this.linearSensor != null)
          MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.linearSensor, 30000);
        if (MediaController.this.accelerometerSensor != null)
          MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.accelerometerSensor, 30000);
        MediaController.this.sensorManager.registerListener(MediaController.this, MediaController.this.proximitySensor, 3);
      }
    });
    this.sensorsStarted = true;
  }

  public void startRecording(long paramLong, MessageObject paramMessageObject)
  {
    int j = 0;
    int i = j;
    if (this.playingMessageObject != null)
    {
      i = j;
      if (isPlayingAudio(this.playingMessageObject))
      {
        i = j;
        if (!isAudioPaused())
        {
          i = 1;
          pauseAudio(this.playingMessageObject);
        }
      }
    }
    try
    {
      ((Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50L);
      DispatchQueue localDispatchQueue = this.recordQueue;
      paramMessageObject = new Runnable(paramLong, paramMessageObject)
      {
        public void run()
        {
          if (MediaController.this.audioRecorder != null)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1902(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
              }
            });
            return;
          }
          MediaController.access$2002(MediaController.this, new TLRPC.TL_document());
          MediaController.this.recordingAudio.dc_id = -2147483648;
          MediaController.this.recordingAudio.id = UserConfig.lastLocalId;
          MediaController.this.recordingAudio.user_id = UserConfig.getClientUserId();
          MediaController.this.recordingAudio.mime_type = "audio/ogg";
          MediaController.this.recordingAudio.thumb = new TLRPC.TL_photoSizeEmpty();
          MediaController.this.recordingAudio.thumb.type = "s";
          UserConfig.lastLocalId -= 1;
          UserConfig.saveConfig(false);
          MediaController.access$5202(MediaController.this, new File(FileLoader.getInstance().getDirectory(4), FileLoader.getAttachFileName(MediaController.this.recordingAudio)));
          try
          {
            if (MediaController.this.startRecord(MediaController.this.recordingAudioFile.getAbsolutePath()) == 0)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MediaController.access$1902(MediaController.this, null);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
                }
              });
              return;
            }
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
            MediaController.access$2002(MediaController.this, null);
            MediaController.this.stopRecord();
            MediaController.this.recordingAudioFile.delete();
            MediaController.access$5202(MediaController.this, null);
          }
          try
          {
            MediaController.this.audioRecorder.release();
            MediaController.access$002(MediaController.this, null);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1902(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStartError, new Object[0]);
              }
            });
            return;
            MediaController.access$002(MediaController.this, new AudioRecord(1, 16000, 16, 2, MediaController.this.recordBufferSize * 10));
            MediaController.access$1102(MediaController.this, System.currentTimeMillis());
            MediaController.access$702(MediaController.this, 0L);
            MediaController.access$302(MediaController.this, 0L);
            MediaController.access$5402(MediaController.this, this.val$dialog_id);
            MediaController.access$5502(MediaController.this, this.val$reply_to_msg);
            MediaController.this.fileBuffer.rewind();
            MediaController.this.audioRecorder.startRecording();
            MediaController.this.recordQueue.postRunnable(MediaController.this.recordRunnable);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MediaController.access$1902(MediaController.this, null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
              }
            });
            return;
          }
          catch (Exception localException2)
          {
            while (true)
              FileLog.e(localException2);
          }
        }
      };
      this.recordStartRunnable = paramMessageObject;
      if (i != 0)
      {
        paramLong = 500L;
        localDispatchQueue.postRunnable(paramMessageObject, paramLong);
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        FileLog.e(localException);
        continue;
        paramLong = 50L;
      }
    }
  }

  public void startRecordingIfFromSpeaker()
  {
    if ((!this.useFrontSpeaker) || (this.raiseChat == null) || (!this.allowStartRecord))
      return;
    this.raiseToEarRecord = true;
    startRecording(this.raiseChat.getDialogId(), null);
    this.ignoreOnPause = true;
  }

  // ERROR //
  public void stopAudio()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   4: ifnonnull +10 -> 14
    //   7: aload_0
    //   8: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   11: ifnull +10 -> 21
    //   14: aload_0
    //   15: getfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   18: ifnonnull +4 -> 22
    //   21: return
    //   22: aload_0
    //   23: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   26: astore_1
    //   27: aload_1
    //   28: ifnull +94 -> 122
    //   31: aload_0
    //   32: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   35: invokevirtual 1711	android/media/MediaPlayer:reset	()V
    //   38: aload_0
    //   39: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   42: invokevirtual 1712	android/media/MediaPlayer:stop	()V
    //   45: aload_0
    //   46: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   49: ifnull +97 -> 146
    //   52: aload_0
    //   53: getfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   56: invokevirtual 1713	android/media/MediaPlayer:release	()V
    //   59: aload_0
    //   60: aconst_null
    //   61: putfield 452	org/vidogram/messenger/MediaController:audioPlayer	Landroid/media/MediaPlayer;
    //   64: aload_0
    //   65: invokespecial 1716	org/vidogram/messenger/MediaController:stopProgressTimer	()V
    //   68: aload_0
    //   69: aconst_null
    //   70: putfield 869	org/vidogram/messenger/MediaController:playingMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   73: aload_0
    //   74: iconst_0
    //   75: putfield 2035	org/vidogram/messenger/MediaController:downloadingCurrentMessage	Z
    //   78: aload_0
    //   79: iconst_0
    //   80: putfield 450	org/vidogram/messenger/MediaController:isPaused	Z
    //   83: new 1910	android/content/Intent
    //   86: dup
    //   87: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   90: ldc_w 2041
    //   93: invokespecial 1915	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
    //   96: astore_1
    //   97: getstatic 562	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   100: aload_1
    //   101: invokevirtual 2045	android/content/Context:stopService	(Landroid/content/Intent;)Z
    //   104: pop
    //   105: return
    //   106: astore_1
    //   107: aload_1
    //   108: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   111: goto -73 -> 38
    //   114: astore_1
    //   115: aload_1
    //   116: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   119: goto -74 -> 45
    //   122: aload_0
    //   123: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   126: ifnull -81 -> 45
    //   129: aload_0
    //   130: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   133: invokevirtual 1740	android/media/AudioTrack:pause	()V
    //   136: aload_0
    //   137: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   140: invokevirtual 1743	android/media/AudioTrack:flush	()V
    //   143: goto -98 -> 45
    //   146: aload_0
    //   147: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   150: ifnull -86 -> 64
    //   153: aload_0
    //   154: getfield 484	org/vidogram/messenger/MediaController:playerObjectSync	Ljava/lang/Object;
    //   157: astore_1
    //   158: aload_1
    //   159: monitorenter
    //   160: aload_0
    //   161: getfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   164: invokevirtual 1744	android/media/AudioTrack:release	()V
    //   167: aload_0
    //   168: aconst_null
    //   169: putfield 454	org/vidogram/messenger/MediaController:audioTrackPlayer	Landroid/media/AudioTrack;
    //   172: aload_1
    //   173: monitorexit
    //   174: goto -110 -> 64
    //   177: astore_2
    //   178: aload_1
    //   179: monitorexit
    //   180: aload_2
    //   181: athrow
    //   182: astore_1
    //   183: aload_1
    //   184: invokestatic 556	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   187: goto -123 -> 64
    //
    // Exception table:
    //   from	to	target	type
    //   31	38	106	java/lang/Exception
    //   22	27	114	java/lang/Exception
    //   38	45	114	java/lang/Exception
    //   107	111	114	java/lang/Exception
    //   122	143	114	java/lang/Exception
    //   160	174	177	finally
    //   178	180	177	finally
    //   45	64	182	java/lang/Exception
    //   146	160	182	java/lang/Exception
    //   180	182	182	java/lang/Exception
  }

  public void stopMediaObserver()
  {
    if (this.stopMediaObserverRunnable == null)
      this.stopMediaObserverRunnable = new StopMediaObserverRunnable(null);
    this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
    ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, 5000L);
  }

  public void stopRaiseToEarSensors(ChatActivity paramChatActivity)
  {
    if (this.ignoreOnPause)
      this.ignoreOnPause = false;
    do
    {
      do
        return;
      while ((!this.sensorsStarted) || (this.ignoreOnPause) || ((this.accelerometerSensor == null) && ((this.gravitySensor == null) || (this.linearAcceleration == null))) || (this.proximitySensor == null) || (this.raiseChat != paramChatActivity));
      this.raiseChat = null;
      stopRecording(0);
      this.sensorsStarted = false;
      this.accelerometerVertical = false;
      this.proximityTouched = false;
      this.raiseToEarRecord = false;
      this.useFrontSpeaker = false;
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (MediaController.this.linearSensor != null)
            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.linearSensor);
          if (MediaController.this.gravitySensor != null)
            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.gravitySensor);
          if (MediaController.this.accelerometerSensor != null)
            MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.accelerometerSensor);
          MediaController.this.sensorManager.unregisterListener(MediaController.this, MediaController.this.proximitySensor);
        }
      });
    }
    while ((!this.proximityHasDifferentValues) || (this.proximityWakeLock == null) || (!this.proximityWakeLock.isHeld()));
    this.proximityWakeLock.release();
  }

  public void stopRecording(int paramInt)
  {
    if (this.recordStartRunnable != null)
    {
      this.recordQueue.cancelRunnable(this.recordStartRunnable);
      this.recordStartRunnable = null;
    }
    this.recordQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        if (MediaController.this.audioRecorder == null)
          return;
        try
        {
          MediaController.access$1202(MediaController.this, this.val$send);
          MediaController.this.audioRecorder.stop();
          if (this.val$send == 0)
            MediaController.this.stopRecordingInternal(0);
        }
        catch (Exception localException2)
        {
          try
          {
            do
            {
              ((Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50L);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
                }
              });
              return;
              localException1 = localException1;
              FileLog.e(localException1);
            }
            while (MediaController.this.recordingAudioFile == null);
            MediaController.this.recordingAudioFile.delete();
          }
          catch (Exception localException2)
          {
            while (true)
              FileLog.e(localException2);
          }
        }
      }
    });
  }

  public void toggleAutoplayGifs()
  {
    if (!this.autoplayGifs);
    for (boolean bool = true; ; bool = false)
    {
      this.autoplayGifs = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("autoplay_gif", this.autoplayGifs);
      localEditor.commit();
      return;
    }
  }

  public void toggleCustomTabs()
  {
    if (!this.customTabs);
    for (boolean bool = true; ; bool = false)
    {
      this.customTabs = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("custom_tabs", this.customTabs);
      localEditor.commit();
      return;
    }
  }

  public void toggleDirectShare()
  {
    if (!this.directShare);
    for (boolean bool = true; ; bool = false)
    {
      this.directShare = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("direct_share", this.directShare);
      localEditor.commit();
      return;
    }
  }

  public void toggleRepeatMode()
  {
    this.repeatMode += 1;
    if (this.repeatMode > 2)
      this.repeatMode = 0;
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
    localEditor.putInt("repeatMode", this.repeatMode);
    localEditor.commit();
  }

  public void toggleSaveToGallery()
  {
    if (!this.saveToGallery);
    for (boolean bool = true; ; bool = false)
    {
      this.saveToGallery = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("save_gallery", this.saveToGallery);
      localEditor.commit();
      checkSaveToGalleryFiles();
      return;
    }
  }

  public void toggleShuffleMusic()
  {
    boolean bool;
    if (!this.shuffleMusic)
    {
      bool = true;
      this.shuffleMusic = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("shuffleMusic", this.shuffleMusic);
      localEditor.commit();
      if (!this.shuffleMusic)
        break label73;
      buildShuffledPlayList();
      this.currentPlaylistNum = 0;
    }
    label73: 
    do
    {
      do
      {
        return;
        bool = false;
        break;
      }
      while (this.playingMessageObject == null);
      this.currentPlaylistNum = this.playlist.indexOf(this.playingMessageObject);
    }
    while (this.currentPlaylistNum != -1);
    this.playlist.clear();
    this.shuffledPlaylist.clear();
    cleanupPlayer(true, true);
  }

  public void toogleRaiseToSpeak()
  {
    if (!this.raiseToSpeak);
    for (boolean bool = true; ; bool = false)
    {
      this.raiseToSpeak = bool;
      SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
      localEditor.putBoolean("raise_to_speak", this.raiseToSpeak);
      localEditor.commit();
      return;
    }
  }

  public static class AlbumEntry
  {
    public int bucketId;
    public String bucketName;
    public MediaController.PhotoEntry coverPhoto;
    public boolean isVideo;
    public ArrayList<MediaController.PhotoEntry> photos = new ArrayList();
    public HashMap<Integer, MediaController.PhotoEntry> photosByIds = new HashMap();

    public AlbumEntry(int paramInt, String paramString, MediaController.PhotoEntry paramPhotoEntry, boolean paramBoolean)
    {
      this.bucketId = paramInt;
      this.bucketName = paramString;
      this.coverPhoto = paramPhotoEntry;
      this.isVideo = paramBoolean;
    }

    public void addPhoto(MediaController.PhotoEntry paramPhotoEntry)
    {
      this.photos.add(paramPhotoEntry);
      this.photosByIds.put(Integer.valueOf(paramPhotoEntry.imageId), paramPhotoEntry);
    }
  }

  private class AudioBuffer
  {
    ByteBuffer buffer;
    byte[] bufferBytes;
    int finished;
    long pcmOffset;
    int size;

    public AudioBuffer(int arg2)
    {
      int i;
      this.buffer = ByteBuffer.allocateDirect(i);
      this.bufferBytes = new byte[i];
    }
  }

  public static class AudioEntry
  {
    public String author;
    public int duration;
    public String genre;
    public long id;
    public MessageObject messageObject;
    public String path;
    public String title;
  }

  private class ExternalObserver extends ContentObserver
  {
    public ExternalObserver()
    {
      super();
    }

    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      MediaController.this.processMediaObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }
  }

  public static abstract interface FileDownloadProgressListener
  {
    public abstract int getObserverTag();

    public abstract void onFailedDownload(String paramString);

    public abstract void onProgressDownload(String paramString, float paramFloat);

    public abstract void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean);

    public abstract void onSuccessDownload(String paramString);
  }

  private class GalleryObserverExternal extends ContentObserver
  {
    public GalleryObserverExternal()
    {
      super();
    }

    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      if (MediaController.this.refreshGalleryRunnable != null)
        AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
      AndroidUtilities.runOnUIThread(MediaController.access$1402(MediaController.this, new Runnable()
      {
        public void run()
        {
          MediaController.access$1402(MediaController.this, null);
          MediaController.loadGalleryPhotosAlbums(0);
        }
      }), 2000L);
    }
  }

  private class GalleryObserverInternal extends ContentObserver
  {
    public GalleryObserverInternal()
    {
      super();
    }

    private void scheduleReloadRunnable()
    {
      AndroidUtilities.runOnUIThread(MediaController.access$1402(MediaController.this, new Runnable()
      {
        public void run()
        {
          if (PhotoViewer.getInstance().isVisible())
          {
            MediaController.GalleryObserverInternal.this.scheduleReloadRunnable();
            return;
          }
          MediaController.access$1402(MediaController.this, null);
          MediaController.loadGalleryPhotosAlbums(0);
        }
      }), 2000L);
    }

    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      if (MediaController.this.refreshGalleryRunnable != null)
        AndroidUtilities.cancelRunOnUIThread(MediaController.this.refreshGalleryRunnable);
      scheduleReloadRunnable();
    }
  }

  private class InternalObserver extends ContentObserver
  {
    public InternalObserver()
    {
      super();
    }

    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      MediaController.this.processMediaObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }
  }

  public static class PhotoEntry
  {
    public int bucketId;
    public CharSequence caption;
    public long dateTaken;
    public int imageId;
    public String imagePath;
    public boolean isVideo;
    public int orientation;
    public String path;
    public ArrayList<TLRPC.InputDocument> stickers = new ArrayList();
    public String thumbPath;

    public PhotoEntry(int paramInt1, int paramInt2, long paramLong, String paramString, int paramInt3, boolean paramBoolean)
    {
      this.bucketId = paramInt1;
      this.imageId = paramInt2;
      this.dateTaken = paramLong;
      this.path = paramString;
      this.orientation = paramInt3;
      this.isVideo = paramBoolean;
    }
  }

  public static class SearchImage
  {
    public CharSequence caption;
    public int date;
    public TLRPC.Document document;
    public int height;
    public String id;
    public String imagePath;
    public String imageUrl;
    public String localUrl;
    public int size;
    public ArrayList<TLRPC.InputDocument> stickers = new ArrayList();
    public String thumbPath;
    public String thumbUrl;
    public int type;
    public int width;
  }

  private final class StopMediaObserverRunnable
    implements Runnable
  {
    public int currentObserverToken = 0;

    private StopMediaObserverRunnable()
    {
    }

    public void run()
    {
      if (this.currentObserverToken == MediaController.this.startObserverToken);
      try
      {
        if (MediaController.this.internalObserver != null)
        {
          ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
          MediaController.access$1702(MediaController.this, null);
        }
      }
      catch (Exception localException2)
      {
        try
        {
          while (true)
          {
            if (MediaController.this.externalObserver != null)
            {
              ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
              MediaController.access$1802(MediaController.this, null);
            }
            return;
            localException1 = localException1;
            FileLog.e(localException1);
          }
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
        }
      }
    }
  }

  private static class VideoConvertRunnable
    implements Runnable
  {
    private MessageObject messageObject;

    private VideoConvertRunnable(MessageObject paramMessageObject)
    {
      this.messageObject = paramMessageObject;
    }

    public static void runConversion(MessageObject paramMessageObject)
    {
      new Thread(new Runnable(paramMessageObject)
      {
        public void run()
        {
          try
          {
            Thread localThread = new Thread(new MediaController.VideoConvertRunnable(this.val$obj, null), "VideoConvertRunnable");
            localThread.start();
            localThread.join();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      }).start();
    }

    public void run()
    {
      MediaController.getInstance().convertVideo(this.messageObject);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.MediaController
 * JD-Core Version:    0.6.0
 */