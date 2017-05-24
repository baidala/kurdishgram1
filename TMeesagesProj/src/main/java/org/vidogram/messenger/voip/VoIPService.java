package org.vidogram.messenger.voip;

import B;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.b.aa;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.StatsController;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.PhoneCall;
import org.vidogram.tgnet.TLRPC.TL_dataJSON;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_messages_dhConfig;
import org.vidogram.tgnet.TLRPC.TL_messages_getDhConfig;
import org.vidogram.tgnet.TLRPC.TL_phoneCall;
import org.vidogram.tgnet.TLRPC.TL_phoneCallAccepted;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonDisconnect;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonHangup;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscarded;
import org.vidogram.tgnet.TLRPC.TL_phoneCallProtocol;
import org.vidogram.tgnet.TLRPC.TL_phoneConnection;
import org.vidogram.tgnet.TLRPC.TL_phone_acceptCall;
import org.vidogram.tgnet.TLRPC.TL_phone_confirmCall;
import org.vidogram.tgnet.TLRPC.TL_phone_discardCall;
import org.vidogram.tgnet.TLRPC.TL_phone_getCallConfig;
import org.vidogram.tgnet.TLRPC.TL_phone_phoneCall;
import org.vidogram.tgnet.TLRPC.TL_phone_receivedCall;
import org.vidogram.tgnet.TLRPC.TL_phone_requestCall;
import org.vidogram.tgnet.TLRPC.TL_phone_saveCallDebug;
import org.vidogram.tgnet.TLRPC.TL_updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.messages_DhConfig;
import org.vidogram.ui.VoIPActivity;
import org.vidogram.ui.VoIPFeedbackActivity;
import org.vidogram.ui.VoIPPermissionActivity;

public class VoIPService extends Service
  implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate, VoIPController.ConnectionStateListener
{
  public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
  private static final int CALL_MAX_LAYER = 65;
  private static final int CALL_MIN_LAYER = 65;
  public static final int DISCARD_REASON_DISCONNECT = 2;
  public static final int DISCARD_REASON_HANGUP = 1;
  public static final int DISCARD_REASON_LINE_BUSY = 4;
  public static final int DISCARD_REASON_MISSED = 3;
  private static final int ID_INCOMING_CALL_NOTIFICATION = 202;
  private static final int ID_ONGOING_CALL_NOTIFICATION = 201;
  private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
  public static final int STATE_BUSY = 12;
  public static final int STATE_ENDED = 6;
  public static final int STATE_ESTABLISHED = 3;
  public static final int STATE_EXCHANGING_KEYS = 7;
  public static final int STATE_FAILED = 4;
  public static final int STATE_HANGING_UP = 5;
  public static final int STATE_REQUESTING = 9;
  public static final int STATE_RINGING = 11;
  public static final int STATE_WAITING = 8;
  public static final int STATE_WAITING_INCOMING = 10;
  public static final int STATE_WAIT_INIT = 1;
  public static final int STATE_WAIT_INIT_ACK = 2;
  private static final String TAG = "tg-voip-service";
  public static TLRPC.PhoneCall callIShouldHavePutIntoIntent;
  private static VoIPService sharedInstance;
  private byte[] a_or_b;
  private byte[] authKey;
  private BluetoothAdapter btAdapter;
  private TLRPC.PhoneCall call;
  private int callReqId;
  private VoIPController controller;
  private boolean controllerStarted;
  private PowerManager.WakeLock cpuWakelock;
  private int currentState = 0;
  private boolean endCallAfterRequest = false;
  private int endHash;
  private byte[] g_a;
  private byte[] g_a_hash;
  private boolean haveAudioFocus;
  private boolean isBtHeadsetConnected;
  private boolean isHeadsetPlugged;
  private boolean isOutgoing;
  private boolean isProximityNear;
  private long keyFingerprint;
  private int lastError;
  private long lastKnownDuration = 0L;
  private NetworkInfo lastNetInfo;
  private Boolean mHasEarpiece = null;
  private boolean micMute;
  private boolean needPlayEndSound;
  private boolean needSendDebugLog = false;
  private Notification ongoingCallNotification;
  private ArrayList<TLRPC.PhoneCall> pendingUpdates = new ArrayList();
  private boolean playingSound;
  private VoIPController.Stats prevStats = new VoIPController.Stats();
  private PowerManager.WakeLock proximityWakelock;
  private BroadcastReceiver receiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      if ("android.intent.action.HEADSET_PLUG".equals(paramIntent.getAction()))
      {
        paramContext = VoIPService.this;
        if (paramIntent.getIntExtra("state", 0) == 1)
        {
          VoIPService.access$002(paramContext, bool1);
          if ((VoIPService.this.isHeadsetPlugged) && (VoIPService.this.proximityWakelock != null) && (VoIPService.this.proximityWakelock.isHeld()))
            VoIPService.this.proximityWakelock.release();
          VoIPService.access$202(VoIPService.this, false);
        }
      }
      label510: 
      do
      {
        do
          while (true)
          {
            return;
            bool1 = false;
            break;
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramIntent.getAction()))
            {
              VoIPService.this.updateNetworkType();
              return;
            }
            if ((VoIPService.this.getPackageName() + ".END_CALL").equals(paramIntent.getAction()))
            {
              if (paramIntent.getIntExtra("end_hash", 0) != VoIPService.this.endHash)
                continue;
              VoIPService.this.stopForeground(true);
              VoIPService.this.hangUp();
              return;
            }
            if ((VoIPService.this.getPackageName() + ".DECLINE_CALL").equals(paramIntent.getAction()))
            {
              if (paramIntent.getIntExtra("end_hash", 0) != VoIPService.this.endHash)
                continue;
              VoIPService.this.stopForeground(true);
              VoIPService.this.declineIncomingCall(4, null);
              return;
            }
            if ((VoIPService.this.getPackageName() + ".ANSWER_CALL").equals(paramIntent.getAction()))
            {
              if (paramIntent.getIntExtra("end_hash", 0) != VoIPService.this.endHash)
                continue;
              VoIPService.this.showNotification();
              if ((Build.VERSION.SDK_INT >= 23) && (VoIPService.this.checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
                try
                {
                  PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, VoIPPermissionActivity.class).addFlags(268435456), 0).send();
                  return;
                }
                catch (Exception paramContext)
                {
                  FileLog.e("Error starting permission activity", paramContext);
                  return;
                }
              VoIPService.this.acceptIncomingCall();
              try
              {
                PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, VoIPActivity.class).addFlags(805306368), 0).send();
                return;
              }
              catch (Exception paramContext)
              {
                FileLog.e("Error starting incall activity", paramContext);
                return;
              }
            }
            if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction()))
            {
              paramContext = VoIPService.this;
              if (paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) == 2);
              for (bool1 = bool2; ; bool1 = false)
              {
                paramContext.updateBluetoothHeadsetState(bool1);
                return;
              }
            }
            if (!"android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(paramIntent.getAction()))
              break label510;
            paramContext = VoIPService.this.stateListeners.iterator();
            while (paramContext.hasNext())
              ((VoIPService.StateListener)paramContext.next()).onAudioSettingsChanged();
          }
        while (!"android.intent.action.PHONE_STATE".equals(paramIntent.getAction()));
        paramContext = paramIntent.getStringExtra("state");
      }
      while (!TelephonyManager.EXTRA_STATE_OFFHOOK.equals(paramContext));
      VoIPService.this.hangUp();
    }
  };
  private MediaPlayer ringtonePlayer;
  private SoundPool soundPool;
  private int spBusyId;
  private int spConnectingId;
  private int spEndId;
  private int spFailedID;
  private int spPlayID;
  private int spRingbackID;
  private ArrayList<StateListener> stateListeners = new ArrayList();
  private VoIPController.Stats stats = new VoIPController.Stats();
  private Runnable timeoutRunnable;
  private TLRPC.User user;
  private int userID;
  private Vibrator vibrator;

  private void acknowledgeCallAndStartRinging()
  {
    if ((this.call instanceof TLRPC.TL_phoneCallDiscarded))
    {
      FileLog.w("Call " + this.call.id + " was discarded before the service started, stopping");
      stopSelf();
      return;
    }
    TLRPC.TL_phone_receivedCall localTL_phone_receivedCall = new TLRPC.TL_phone_receivedCall();
    localTL_phone_receivedCall.peer = new TLRPC.TL_inputPhoneCall();
    localTL_phone_receivedCall.peer.id = this.call.id;
    localTL_phone_receivedCall.peer.access_hash = this.call.access_hash;
    ConnectionsManager.getInstance().sendRequest(localTL_phone_receivedCall, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTLObject, paramTL_error)
        {
          public void run()
          {
            if (VoIPService.sharedInstance == null)
              return;
            FileLog.w("receivedCall response = " + this.val$response);
            if (this.val$error != null)
            {
              FileLog.e("error on receivedCall: " + this.val$error);
              VoIPService.this.stopSelf();
              return;
            }
            VoIPService.this.startRinging();
          }
        });
      }
    }
    , 2);
  }

  private void callEnded()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Call ");
    long l;
    if (this.call != null)
      l = this.call.id;
    while (true)
    {
      FileLog.d(l + " ended");
      dispatchStateChanged(6);
      if (this.needPlayEndSound)
      {
        this.playingSound = true;
        this.soundPool.play(this.spEndId, 1.0F, 1.0F, 0, 0, 1.0F);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            VoIPService.this.soundPool.release();
            if (VoIPService.this.isBtHeadsetConnected)
              ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio")).stopBluetoothSco();
          }
        }
        , 1000L);
      }
      if (this.timeoutRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
        this.timeoutRunnable = null;
      }
      stopSelf();
      return;
      l = 0L;
    }
  }

  private void callFailed()
  {
    if ((this.controller != null) && (this.controllerStarted));
    for (int i = this.controller.getLastError(); ; i = 0)
    {
      callFailed(i);
      return;
    }
  }

  private void callFailed(int paramInt)
  {
    long l;
    try
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Call ");
      if (this.call == null)
        break label282;
      l = this.call.id;
      throw new Exception(l + " failed with error code " + paramInt);
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
      this.lastError = paramInt;
      if (this.call == null)
        break label223;
    }
    FileLog.d("Discarding failed call");
    TLRPC.TL_phone_discardCall localTL_phone_discardCall = new TLRPC.TL_phone_discardCall();
    localTL_phone_discardCall.peer = new TLRPC.TL_inputPhoneCall();
    localTL_phone_discardCall.peer.access_hash = this.call.access_hash;
    localTL_phone_discardCall.peer.id = this.call.id;
    int i;
    if ((this.controller != null) && (this.controllerStarted))
    {
      i = (int)(this.controller.getCallDuration() / 1000L);
      label160: localTL_phone_discardCall.duration = i;
      if ((this.controller == null) || (!this.controllerStarted))
        break label292;
      l = this.controller.getPreferredRelayID();
    }
    while (true)
    {
      localTL_phone_discardCall.connection_id = l;
      localTL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
      ConnectionsManager.getInstance().sendRequest(localTL_phone_discardCall, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error != null)
          {
            FileLog.e("error on phone.discardCall: " + paramTL_error);
            return;
          }
          FileLog.d("phone.discardCall " + paramTLObject);
        }
      });
      label223: dispatchStateChanged(4);
      if ((paramInt != -3) && (this.soundPool != null))
      {
        this.playingSound = true;
        this.soundPool.play(this.spFailedID, 1.0F, 1.0F, 0, 0, 1.0F);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            VoIPService.this.soundPool.release();
            if (VoIPService.this.isBtHeadsetConnected)
              ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio")).stopBluetoothSco();
          }
        }
        , 1000L);
      }
      stopSelf();
      return;
      label282: l = 0L;
      break;
      i = 0;
      break label160;
      label292: l = 0L;
    }
  }

  private void configureDeviceForCall()
  {
    this.needPlayEndSound = true;
    Object localObject = (AudioManager)getSystemService("audio");
    ((AudioManager)localObject).setMode(3);
    ((AudioManager)localObject).setSpeakerphoneOn(false);
    ((AudioManager)localObject).requestAudioFocus(this, 0, 1);
    localObject = (SensorManager)getSystemService("sensor");
    Sensor localSensor = ((SensorManager)localObject).getDefaultSensor(8);
    if (localSensor != null);
    try
    {
      this.proximityWakelock = ((PowerManager)getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
      ((SensorManager)localObject).registerListener(this, localSensor, 3);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("Error initializing proximity sensor", localException);
    }
  }

  private void dispatchStateChanged(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder().append("== Call ");
    long l;
    if (this.call != null)
      l = this.call.id;
    while (true)
    {
      FileLog.d(l + " state changed to " + paramInt + " ==");
      this.currentState = paramInt;
      int i = 0;
      while (i < this.stateListeners.size())
      {
        ((StateListener)this.stateListeners.get(i)).onStateChanged(paramInt);
        i += 1;
      }
      l = 0L;
    }
  }

  private void dumpCallObject()
  {
    try
    {
      Field[] arrayOfField = TLRPC.PhoneCall.class.getFields();
      int j = arrayOfField.length;
      int i = 0;
      while (i < j)
      {
        Field localField = arrayOfField[i];
        FileLog.d(localField.getName() + " = " + localField.get(this.call));
        i += 1;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public static VoIPService getSharedInstance()
  {
    return sharedInstance;
  }

  private int getStatsNetworkType()
  {
    int j = 1;
    int i = j;
    if (this.lastNetInfo != null)
    {
      i = j;
      if (this.lastNetInfo.getType() == 0)
      {
        if (!this.lastNetInfo.isRoaming())
          break label37;
        i = 2;
      }
    }
    return i;
    label37: return 0;
  }

  private void initiateActualEncryptedCall()
  {
    if (this.timeoutRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
      this.timeoutRunnable = null;
    }
    try
    {
      FileLog.d("InitCall: keyID=" + this.keyFingerprint);
      this.controller.setEncryptionKey(this.authKey, this.isOutgoing);
      TLRPC.TL_phoneConnection[] arrayOfTL_phoneConnection = new TLRPC.TL_phoneConnection[this.call.alternative_connections.size() + 1];
      arrayOfTL_phoneConnection[0] = this.call.connection;
      int i = 0;
      while (i < this.call.alternative_connections.size())
      {
        arrayOfTL_phoneConnection[(i + 1)] = ((TLRPC.TL_phoneConnection)this.call.alternative_connections.get(i));
        i += 1;
      }
      this.controller.setRemoteEndpoints(arrayOfTL_phoneConnection, this.call.protocol.udp_p2p);
      this.controller.start();
      updateNetworkType();
      this.controller.connect();
      this.controllerStarted = true;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (VoIPService.this.controller == null)
            return;
          VoIPService.this.updateStats();
          AndroidUtilities.runOnUIThread(this, 5000L);
        }
      }
      , 5000L);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("error starting call", localException);
      callFailed();
    }
  }

  private void processAcceptedCall()
  {
    dispatchStateChanged(7);
    Object localObject1 = new BigInteger(1, MessagesStorage.secretPBytes);
    Object localObject2 = new BigInteger(1, this.call.g_b);
    if (!Utilities.isGoodGaAndGb((BigInteger)localObject2, (BigInteger)localObject1))
    {
      FileLog.w("stopping VoIP service, bad Ga and Gb");
      callFailed();
      return;
    }
    localObject1 = ((BigInteger)localObject2).modPow(new BigInteger(1, this.a_or_b), (BigInteger)localObject1).toByteArray();
    if (localObject1.length > 256)
    {
      localObject2 = new byte[256];
      System.arraycopy(localObject1, localObject1.length - 256, localObject2, 0, 256);
      localObject1 = localObject2;
    }
    while (true)
    {
      localObject2 = Utilities.computeSHA1(localObject1);
      byte[] arrayOfByte = new byte[8];
      System.arraycopy(localObject2, localObject2.length - 8, arrayOfByte, 0, 8);
      long l = Utilities.bytesToLong(arrayOfByte);
      this.authKey = ((B)localObject1);
      this.keyFingerprint = l;
      localObject1 = new TLRPC.TL_phone_confirmCall();
      ((TLRPC.TL_phone_confirmCall)localObject1).g_a = this.g_a;
      ((TLRPC.TL_phone_confirmCall)localObject1).key_fingerprint = l;
      ((TLRPC.TL_phone_confirmCall)localObject1).peer = new TLRPC.TL_inputPhoneCall();
      ((TLRPC.TL_phone_confirmCall)localObject1).peer.id = this.call.id;
      ((TLRPC.TL_phone_confirmCall)localObject1).peer.access_hash = this.call.access_hash;
      ((TLRPC.TL_phone_confirmCall)localObject1).protocol = new TLRPC.TL_phoneCallProtocol();
      ((TLRPC.TL_phone_confirmCall)localObject1).protocol.max_layer = 65;
      ((TLRPC.TL_phone_confirmCall)localObject1).protocol.min_layer = 65;
      localObject2 = ((TLRPC.TL_phone_confirmCall)localObject1).protocol;
      ((TLRPC.TL_phone_confirmCall)localObject1).protocol.udp_reflector = true;
      ((TLRPC.TL_phoneCallProtocol)localObject2).udp_p2p = true;
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error != null)
              {
                VoIPService.this.callFailed();
                return;
              }
              VoIPService.access$1202(VoIPService.this, ((TLRPC.TL_phone_phoneCall)this.val$response).phone_call);
              VoIPService.this.initiateActualEncryptedCall();
            }
          });
        }
      });
      return;
      if (localObject1.length < 256)
      {
        localObject2 = new byte[256];
        System.arraycopy(localObject1, 0, localObject2, 256 - localObject1.length, localObject1.length);
        int i = 0;
        while (i < 256 - localObject1.length)
        {
          localObject1[i] = 0;
          i += 1;
        }
        localObject1 = localObject2;
        continue;
      }
    }
  }

  private void showIncomingNotification()
  {
    Object localObject2 = new Intent(this, VoIPActivity.class);
    ((Intent)localObject2).addFlags(805306368);
    Notification.Builder localBuilder = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipInCallBranding", 2131166588)).setContentText(ContactsController.formatName(this.user.first_name, this.user.last_name)).setSmallIcon(2130837965).setContentIntent(PendingIntent.getActivity(this, 0, (Intent)localObject2, 0));
    Intent localIntent;
    Object localObject1;
    if (Build.VERSION.SDK_INT >= 16)
    {
      this.endHash = Utilities.random.nextInt();
      localIntent = new Intent();
      localIntent.setAction(getPackageName() + ".DECLINE_CALL");
      localIntent.putExtra("end_hash", this.endHash);
      localObject1 = LocaleController.getString("VoipDeclineCall", 2131166581);
      if (Build.VERSION.SDK_INT < 24)
        break label564;
      localObject1 = new SpannableString((CharSequence)localObject1);
      ((SpannableString)localObject1).setSpan(new ForegroundColorSpan(-769226), 0, ((CharSequence)localObject1).length(), 0);
    }
    label555: label564: 
    while (true)
    {
      localBuilder.addAction(2130837752, (CharSequence)localObject1, PendingIntent.getBroadcast(this, 0, localIntent, 134217728));
      localIntent = new Intent();
      localIntent.setAction(getPackageName() + ".ANSWER_CALL");
      localIntent.putExtra("end_hash", this.endHash);
      localObject1 = LocaleController.getString("VoipAnswerCall", 2131166574);
      if (Build.VERSION.SDK_INT >= 24)
      {
        localObject1 = new SpannableString((CharSequence)localObject1);
        ((SpannableString)localObject1).setSpan(new ForegroundColorSpan(-16733696), 0, ((CharSequence)localObject1).length(), 0);
      }
      while (true)
      {
        localBuilder.addAction(2130837758, (CharSequence)localObject1, PendingIntent.getBroadcast(this, 0, localIntent, 134217728));
        localBuilder.setPriority(2);
        if (Build.VERSION.SDK_INT >= 17)
          localBuilder.setShowWhen(false);
        if (Build.VERSION.SDK_INT >= 21)
        {
          localBuilder.setColor(-13851168);
          localBuilder.setVibrate(new long[0]);
          localBuilder.setCategory("call");
          localBuilder.setFullScreenIntent(PendingIntent.getActivity(this, 0, (Intent)localObject2, 0), true);
        }
        if (this.user.photo != null)
        {
          localObject1 = this.user.photo.photo_small;
          if (localObject1 != null)
          {
            localObject2 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject1, null, "50_50");
            if (localObject2 == null)
              break label484;
            localBuilder.setLargeIcon(((BitmapDrawable)localObject2).getBitmap());
          }
        }
        startForeground(202, localBuilder.getNotification());
        return;
        while (true)
        {
          label484: float f;
          try
          {
            f = 160.0F / AndroidUtilities.dp(50.0F);
            localObject2 = new BitmapFactory.Options();
            if (f >= 1.0F)
              break label555;
            i = 1;
            ((BitmapFactory.Options)localObject2).inSampleSize = i;
            localObject1 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject1, true).toString(), (BitmapFactory.Options)localObject2);
            if (localObject1 == null)
              break;
            localBuilder.setLargeIcon((Bitmap)localObject1);
          }
          catch (Throwable localThrowable)
          {
            FileLog.e(localThrowable);
          }
          break;
          int i = (int)f;
        }
      }
    }
  }

  private void showNotification()
  {
    int i = 1;
    Object localObject1 = new Intent(this, VoIPActivity.class);
    ((Intent)localObject1).addFlags(805306368);
    localObject1 = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipOutgoingCall", 2131166599)).setContentText(ContactsController.formatName(this.user.first_name, this.user.last_name)).setSmallIcon(2130837965).setContentIntent(PendingIntent.getActivity(this, 0, (Intent)localObject1, 0));
    Object localObject2;
    if (Build.VERSION.SDK_INT >= 16)
    {
      localObject2 = new Intent();
      ((Intent)localObject2).setAction(getPackageName() + ".END_CALL");
      int j = Utilities.random.nextInt();
      this.endHash = j;
      ((Intent)localObject2).putExtra("end_hash", j);
      ((Notification.Builder)localObject1).addAction(2130837752, LocaleController.getString("VoipEndCall", 2131166582), PendingIntent.getBroadcast(this, 0, (Intent)localObject2, 134217728));
      ((Notification.Builder)localObject1).setPriority(2);
    }
    if (Build.VERSION.SDK_INT >= 17)
      ((Notification.Builder)localObject1).setShowWhen(false);
    if (Build.VERSION.SDK_INT >= 21)
      ((Notification.Builder)localObject1).setColor(-13851168);
    Object localObject3;
    if (this.user.photo != null)
    {
      localObject2 = this.user.photo.photo_small;
      if (localObject2 != null)
      {
        localObject3 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject2, null, "50_50");
        if (localObject3 == null)
          break label296;
        ((Notification.Builder)localObject1).setLargeIcon(((BitmapDrawable)localObject3).getBitmap());
      }
    }
    this.ongoingCallNotification = ((Notification.Builder)localObject1).getNotification();
    startForeground(201, this.ongoingCallNotification);
    return;
    while (true)
    {
      label296: float f;
      try
      {
        f = 160.0F / AndroidUtilities.dp(50.0F);
        localObject3 = new BitmapFactory.Options();
        if (f >= 1.0F)
          break label371;
        ((BitmapFactory.Options)localObject3).inSampleSize = i;
        localObject2 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject2, true).toString(), (BitmapFactory.Options)localObject3);
        if (localObject2 == null)
          break;
        ((Notification.Builder)localObject1).setLargeIcon((Bitmap)localObject2);
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
      }
      break;
      label371: i = (int)f;
    }
  }

  private void startConnectingSound()
  {
    if (this.spPlayID != 0)
      this.soundPool.stop(this.spPlayID);
    this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0F, 1.0F, 0, -1, 1.0F);
    if (this.spPlayID == 0)
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (VoIPService.sharedInstance == null);
          do
          {
            return;
            if (VoIPService.this.spPlayID != 0)
              continue;
            VoIPService.access$2902(VoIPService.this, VoIPService.this.soundPool.play(VoIPService.this.spConnectingId, 1.0F, 1.0F, 0, -1, 1.0F));
          }
          while (VoIPService.this.spPlayID != 0);
          AndroidUtilities.runOnUIThread(this, 100L);
        }
      }
      , 100L);
  }

  private void startOutgoingCall()
  {
    configureDeviceForCall();
    showNotification();
    startConnectingSound();
    dispatchStateChanged(9);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
      }
    });
    Object localObject = new byte[256];
    Utilities.random.nextBytes(localObject);
    localObject = new TLRPC.TL_messages_getDhConfig();
    ((TLRPC.TL_messages_getDhConfig)localObject).random_length = 256;
    ((TLRPC.TL_messages_getDhConfig)localObject).version = MessagesStorage.lastSecretVersion;
    this.callReqId = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        VoIPService.access$802(VoIPService.this, 0);
        byte[] arrayOfByte;
        if (paramTL_error == null)
        {
          paramTL_error = (TLRPC.messages_DhConfig)paramTLObject;
          if ((paramTLObject instanceof TLRPC.TL_messages_dhConfig))
          {
            if (!Utilities.isGoodPrime(paramTL_error.p, paramTL_error.g))
            {
              VoIPService.this.callFailed();
              return;
            }
            MessagesStorage.secretPBytes = paramTL_error.p;
            MessagesStorage.secretG = paramTL_error.g;
            MessagesStorage.lastSecretVersion = paramTL_error.version;
            MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
          }
          arrayOfByte = new byte[256];
          int i = 0;
          while (i < 256)
          {
            arrayOfByte[i] = (byte)((byte)(int)(Utilities.random.nextDouble() * 256.0D) ^ paramTL_error.random[i]);
            i += 1;
          }
          paramTLObject = BigInteger.valueOf(MessagesStorage.secretG).modPow(new BigInteger(1, arrayOfByte), new BigInteger(1, MessagesStorage.secretPBytes)).toByteArray();
          if (paramTLObject.length <= 256)
            break label342;
          paramTL_error = new byte[256];
          System.arraycopy(paramTLObject, 1, paramTL_error, 0, 256);
          paramTLObject = paramTL_error;
        }
        label342: 
        while (true)
        {
          paramTL_error = new TLRPC.TL_phone_requestCall();
          paramTL_error.user_id = MessagesController.getInputUser(VoIPService.this.user);
          paramTL_error.protocol = new TLRPC.TL_phoneCallProtocol();
          TLRPC.TL_phoneCallProtocol localTL_phoneCallProtocol = paramTL_error.protocol;
          paramTL_error.protocol.udp_reflector = true;
          localTL_phoneCallProtocol.udp_p2p = true;
          paramTL_error.protocol.min_layer = 65;
          paramTL_error.protocol.max_layer = 65;
          VoIPService.access$1102(VoIPService.this, paramTLObject);
          paramTL_error.g_a_hash = Utilities.computeSHA256(paramTLObject, 0, paramTLObject.length);
          paramTL_error.random_id = Utilities.random.nextInt();
          ConnectionsManager.getInstance().sendRequest(paramTL_error, new RequestDelegate(arrayOfByte)
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
              {
                public void run()
                {
                  if (this.val$error == null)
                  {
                    VoIPService.access$1202(VoIPService.this, ((TLRPC.TL_phone_phoneCall)this.val$response).phone_call);
                    VoIPService.access$1302(VoIPService.this, VoIPService.6.1.this.val$salt);
                    VoIPService.this.dispatchStateChanged(8);
                    if (VoIPService.this.endCallAfterRequest)
                    {
                      VoIPService.this.hangUp();
                      return;
                    }
                    if ((VoIPService.this.pendingUpdates.size() > 0) && (VoIPService.this.call != null))
                    {
                      Iterator localIterator = VoIPService.this.pendingUpdates.iterator();
                      while (localIterator.hasNext())
                      {
                        TLRPC.PhoneCall localPhoneCall = (TLRPC.PhoneCall)localIterator.next();
                        VoIPService.this.onCallUpdated(localPhoneCall);
                      }
                      VoIPService.this.pendingUpdates.clear();
                    }
                    VoIPService.access$1702(VoIPService.this, new Runnable()
                    {
                      public void run()
                      {
                        VoIPService.access$1702(VoIPService.this, null);
                        TLRPC.TL_phone_discardCall localTL_phone_discardCall = new TLRPC.TL_phone_discardCall();
                        localTL_phone_discardCall.peer = new TLRPC.TL_inputPhoneCall();
                        localTL_phone_discardCall.peer.access_hash = VoIPService.this.call.access_hash;
                        localTL_phone_discardCall.peer.id = VoIPService.this.call.id;
                        localTL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
                        ConnectionsManager.getInstance().sendRequest(localTL_phone_discardCall, new RequestDelegate()
                        {
                          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                          {
                            if (paramTL_error != null)
                              FileLog.e("error on phone.discardCall: " + paramTL_error);
                            while (true)
                            {
                              AndroidUtilities.runOnUIThread(new Runnable()
                              {
                                public void run()
                                {
                                  VoIPService.this.callFailed();
                                }
                              });
                              return;
                              FileLog.d("phone.discardCall " + paramTLObject);
                            }
                          }
                        }
                        , 2);
                      }
                    });
                    AndroidUtilities.runOnUIThread(VoIPService.this.timeoutRunnable, MessagesController.getInstance().callReceiveTimeout);
                    return;
                  }
                  if ((this.val$error.code == 400) && ("PARTICIPANT_VERSION_OUTDATED".equals(this.val$error.text)))
                  {
                    VoIPService.this.callFailed(-1);
                    return;
                  }
                  if ((this.val$error.code == 403) && ("USER_PRIVACY_RESTRICTED".equals(this.val$error.text)))
                  {
                    VoIPService.this.callFailed(-2);
                    return;
                  }
                  if (this.val$error.code == 406)
                  {
                    VoIPService.this.callFailed(-3);
                    return;
                  }
                  FileLog.e("Error on phone.requestCall: " + this.val$error);
                  VoIPService.this.callFailed();
                }
              });
            }
          }
          , 2);
          return;
          FileLog.e("Error on getDhConfig " + paramTL_error);
          VoIPService.this.callFailed();
          return;
        }
      }
    }
    , 2);
  }

  private void startRatingActivity()
  {
    try
    {
      PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.call.id).putExtra("call_access_hash", this.call.access_hash).addFlags(805306368), 0).send();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("Error starting incall activity", localException);
    }
  }

  private void startRinging()
  {
    FileLog.d("starting ringing for call " + this.call.id);
    dispatchStateChanged(10);
    SharedPreferences localSharedPreferences = getSharedPreferences("Notifications", 0);
    this.ringtonePlayer = new MediaPlayer();
    this.ringtonePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
    {
      public void onPrepared(MediaPlayer paramMediaPlayer)
      {
        VoIPService.this.ringtonePlayer.start();
      }
    });
    this.ringtonePlayer.setLooping(true);
    this.ringtonePlayer.setAudioStreamType(2);
    try
    {
      if (localSharedPreferences.getBoolean("custom_" + this.user.id, false));
      for (Object localObject = localSharedPreferences.getString("ringtone_path_" + this.user.id, RingtoneManager.getDefaultUri(1).toString()); ; localObject = localSharedPreferences.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString()))
      {
        this.ringtonePlayer.setDataSource(this, Uri.parse((String)localObject));
        this.ringtonePlayer.prepareAsync();
        localObject = (AudioManager)getSystemService("audio");
        if (!localSharedPreferences.getBoolean("custom_" + this.user.id, false))
          break;
        i = localSharedPreferences.getInt("calls_vibrate_" + this.user.id, 0);
        if (((i != 2) && (i != 4) && ((((AudioManager)localObject).getRingerMode() == 1) || (((AudioManager)localObject).getRingerMode() == 2))) || ((i == 4) && (((AudioManager)localObject).getRingerMode() == 1)))
        {
          this.vibrator = ((Vibrator)getSystemService("vibrator"));
          l = 1000L;
          if (i != 1)
            break label479;
          l = 1000L / 2L;
          this.vibrator.vibrate(new long[] { 0L, l, 1000L }, 0);
        }
        if ((Build.VERSION.SDK_INT < 21) || (((KeyguardManager)getSystemService("keyguard")).inKeyguardRestrictedInputMode()) || (!aa.a(this).a()))
          break label495;
        showIncomingNotification();
        FileLog.d("Showing incoming call notification");
        return;
      }
    }
    catch (Exception localException2)
    {
      while (true)
      {
        FileLog.e(localException1);
        if (this.ringtonePlayer == null)
          continue;
        this.ringtonePlayer.release();
        this.ringtonePlayer = null;
        continue;
        int i = localSharedPreferences.getInt("vibrate_calls", 0);
        continue;
        label479: if (i != 3)
          continue;
        long l = 1000L * 2L;
      }
      label495: FileLog.d("Starting incall activity for incoming call");
      try
      {
        PendingIntent.getActivity(this, 12345, new Intent(this, VoIPActivity.class).addFlags(268435456), 0).send();
        return;
      }
      catch (Exception localException2)
      {
        FileLog.e("Error starting incall activity", localException2);
      }
    }
  }

  private void updateBluetoothHeadsetState(boolean paramBoolean)
  {
    if (paramBoolean == this.isBtHeadsetConnected)
      return;
    this.isBtHeadsetConnected = paramBoolean;
    Object localObject = (AudioManager)getSystemService("audio");
    if (paramBoolean)
      ((AudioManager)localObject).startBluetoothSco();
    while (true)
    {
      localObject = this.stateListeners.iterator();
      while (((Iterator)localObject).hasNext())
        ((StateListener)((Iterator)localObject).next()).onAudioSettingsChanged();
      break;
      ((AudioManager)localObject).stopBluetoothSco();
    }
  }

  private void updateNetworkType()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    this.lastNetInfo = localNetworkInfo;
    int j = 0;
    int i = j;
    if (localNetworkInfo != null)
      switch (localNetworkInfo.getType())
      {
      default:
        i = j;
      case 0:
      case 1:
      case 9:
      }
    while (true)
    {
      if (this.controller != null)
        this.controller.setNetworkType(i);
      return;
      switch (localNetworkInfo.getSubtype())
      {
      case 4:
      case 11:
      case 14:
      default:
        i = 11;
        break;
      case 1:
        i = 1;
        break;
      case 2:
      case 7:
        i = 2;
        break;
      case 3:
      case 5:
        i = 3;
        break;
      case 6:
      case 8:
      case 9:
      case 10:
      case 12:
      case 15:
        i = 4;
        break;
      case 13:
        i = 5;
        continue;
        i = 6;
        continue;
        i = 7;
      }
    }
  }

  private void updateStats()
  {
    this.controller.getStats(this.stats);
    long l1 = this.stats.bytesSentWifi - this.prevStats.bytesSentWifi;
    long l2 = this.stats.bytesRecvdWifi - this.prevStats.bytesRecvdWifi;
    long l3 = this.stats.bytesSentMobile - this.prevStats.bytesSentMobile;
    long l4 = this.stats.bytesRecvdMobile - this.prevStats.bytesRecvdMobile;
    Object localObject = this.stats;
    this.stats = this.prevStats;
    this.prevStats = ((VoIPController.Stats)localObject);
    if (l1 > 0L)
      StatsController.getInstance().incrementSentBytesCount(1, 0, l1);
    if (l2 > 0L)
      StatsController.getInstance().incrementReceivedBytesCount(1, 0, l2);
    if (l3 > 0L)
    {
      localObject = StatsController.getInstance();
      if ((this.lastNetInfo != null) && (this.lastNetInfo.isRoaming()))
      {
        i = 2;
        ((StatsController)localObject).incrementSentBytesCount(i, 0, l3);
      }
    }
    else if (l4 > 0L)
    {
      localObject = StatsController.getInstance();
      if ((this.lastNetInfo == null) || (!this.lastNetInfo.isRoaming()))
        break label216;
    }
    label216: for (int i = 2; ; i = 0)
    {
      ((StatsController)localObject).incrementReceivedBytesCount(i, 0, l4);
      return;
      i = 0;
      break;
    }
  }

  public void acceptIncomingCall()
  {
    stopRinging();
    showNotification();
    configureDeviceForCall();
    startConnectingSound();
    dispatchStateChanged(7);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
      }
    });
    TLRPC.TL_messages_getDhConfig localTL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
    localTL_messages_getDhConfig.random_length = 256;
    localTL_messages_getDhConfig.version = MessagesStorage.lastSecretVersion;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getDhConfig, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          paramTL_error = (TLRPC.messages_DhConfig)paramTLObject;
          if ((paramTLObject instanceof TLRPC.TL_messages_dhConfig))
          {
            if (!Utilities.isGoodPrime(paramTL_error.p, paramTL_error.g))
            {
              FileLog.e("stopping VoIP service, bad prime");
              VoIPService.this.callFailed();
              return;
            }
            MessagesStorage.secretPBytes = paramTL_error.p;
            MessagesStorage.secretG = paramTL_error.g;
            MessagesStorage.lastSecretVersion = paramTL_error.version;
            MessagesStorage.getInstance().saveSecretParams(MessagesStorage.lastSecretVersion, MessagesStorage.secretG, MessagesStorage.secretPBytes);
          }
          paramTLObject = new byte[256];
          int i = 0;
          while (i < 256)
          {
            paramTLObject[i] = (byte)((byte)(int)(Utilities.random.nextDouble() * 256.0D) ^ paramTL_error.random[i]);
            i += 1;
          }
          VoIPService.access$1302(VoIPService.this, paramTLObject);
          paramTL_error = BigInteger.valueOf(MessagesStorage.secretG);
          BigInteger localBigInteger = new BigInteger(1, MessagesStorage.secretPBytes);
          paramTLObject = paramTL_error.modPow(new BigInteger(1, paramTLObject), localBigInteger);
          VoIPService.access$2202(VoIPService.this, VoIPService.this.call.g_a_hash);
          paramTLObject = paramTLObject.toByteArray();
          if (paramTLObject.length <= 256)
            break label350;
          paramTL_error = new byte[256];
          System.arraycopy(paramTLObject, 1, paramTL_error, 0, 256);
          paramTLObject = paramTL_error;
        }
        label350: 
        while (true)
        {
          paramTL_error = new TLRPC.TL_phone_acceptCall();
          paramTL_error.g_b = paramTLObject;
          paramTL_error.peer = new TLRPC.TL_inputPhoneCall();
          paramTL_error.peer.id = VoIPService.this.call.id;
          paramTL_error.peer.access_hash = VoIPService.this.call.access_hash;
          paramTL_error.protocol = new TLRPC.TL_phoneCallProtocol();
          paramTLObject = paramTL_error.protocol;
          paramTL_error.protocol.udp_reflector = true;
          paramTLObject.udp_p2p = true;
          paramTL_error.protocol.min_layer = 65;
          paramTL_error.protocol.max_layer = 65;
          ConnectionsManager.getInstance().sendRequest(paramTL_error, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
              {
                public void run()
                {
                  if (this.val$error == null)
                  {
                    FileLog.w("accept call ok! " + this.val$response);
                    VoIPService.access$1202(VoIPService.this, ((TLRPC.TL_phone_phoneCall)this.val$response).phone_call);
                    if ((VoIPService.this.call instanceof TLRPC.TL_phoneCallDiscarded))
                      VoIPService.this.onCallUpdated(VoIPService.this.call);
                    return;
                  }
                  FileLog.e("Error on phone.acceptCall: " + this.val$error);
                  VoIPService.this.callFailed();
                }
              });
            }
          }
          , 2);
          return;
          VoIPService.this.callFailed();
          return;
        }
      }
    });
  }

  public void debugCtl(int paramInt1, int paramInt2)
  {
    if (this.controller != null)
      this.controller.debugCtl(paramInt1, paramInt2);
  }

  public void declineIncomingCall()
  {
    declineIncomingCall(1, null);
  }

  public void declineIncomingCall(int paramInt, Runnable paramRunnable)
  {
    boolean bool = false;
    if (this.currentState == 9)
      this.endCallAfterRequest = true;
    while (true)
    {
      do
        return;
      while ((this.currentState == 5) || (this.currentState == 6));
      dispatchStateChanged(5);
      if (this.call != null)
        break;
      if (paramRunnable != null)
        paramRunnable.run();
      callEnded();
      if (this.callReqId == 0)
        continue;
      ConnectionsManager.getInstance().cancelRequest(this.callReqId, false);
      this.callReqId = 0;
      return;
    }
    TLRPC.TL_phone_discardCall localTL_phone_discardCall = new TLRPC.TL_phone_discardCall();
    localTL_phone_discardCall.peer = new TLRPC.TL_inputPhoneCall();
    localTL_phone_discardCall.peer.access_hash = this.call.access_hash;
    localTL_phone_discardCall.peer.id = this.call.id;
    int i;
    long l;
    if ((this.controller != null) && (this.controllerStarted))
    {
      i = (int)(this.controller.getCallDuration() / 1000L);
      localTL_phone_discardCall.duration = i;
      if ((this.controller == null) || (!this.controllerStarted))
        break label332;
      l = this.controller.getPreferredRelayID();
      label192: localTL_phone_discardCall.connection_id = l;
      switch (paramInt)
      {
      default:
        localTL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonHangup();
      case 2:
      case 3:
      case 4:
      }
    }
    while (true)
    {
      if (ConnectionsManager.getInstance().getConnectionState() != 3)
        bool = true;
      if (bool)
      {
        if (paramRunnable != null)
          paramRunnable.run();
        callEnded();
      }
      11 local11 = new Runnable(paramRunnable)
      {
        private boolean done = false;

        public void run()
        {
          if (this.done)
            return;
          this.done = true;
          if (this.val$onDone != null)
            this.val$onDone.run();
          VoIPService.this.callEnded();
        }
      };
      AndroidUtilities.runOnUIThread(local11, (int)(VoIPServerConfig.getDouble("hangup_ui_timeout", 5.0D) * 1000.0D));
      ConnectionsManager.getInstance().sendRequest(localTL_phone_discardCall, new RequestDelegate(bool, local11, paramRunnable)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error != null)
            FileLog.e("error on phone.discardCall: " + paramTL_error);
          while (true)
          {
            if (!this.val$wasNotConnected)
            {
              AndroidUtilities.cancelRunOnUIThread(this.val$stopper);
              if (this.val$onDone != null)
                this.val$onDone.run();
            }
            return;
            if ((paramTLObject instanceof TLRPC.TL_updates))
            {
              paramTL_error = (TLRPC.TL_updates)paramTLObject;
              MessagesController.getInstance().processUpdates(paramTL_error, false);
            }
            FileLog.d("phone.discardCall " + paramTLObject);
          }
        }
      }
      , 2);
      return;
      i = 0;
      break;
      label332: l = 0L;
      break label192;
      localTL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
      continue;
      localTL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
      continue;
      localTL_phone_discardCall.reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.appDidLogout)
      callEnded();
  }

  public long getCallDuration()
  {
    if ((!this.controllerStarted) || (this.controller == null))
      return this.lastKnownDuration;
    long l = this.controller.getCallDuration();
    this.lastKnownDuration = l;
    return l;
  }

  public int getCallState()
  {
    return this.currentState;
  }

  public String getDebugString()
  {
    return this.controller.getDebugString();
  }

  public byte[] getEncryptionKey()
  {
    return this.authKey;
  }

  public byte[] getGA()
  {
    return this.g_a;
  }

  public int getLastError()
  {
    return this.lastError;
  }

  public TLRPC.User getUser()
  {
    return this.user;
  }

  public void hangUp()
  {
    if ((this.currentState == 11) || ((this.currentState == 8) && (this.isOutgoing)));
    for (int i = 3; ; i = 1)
    {
      declineIncomingCall(i, null);
      return;
    }
  }

  public void hangUp(Runnable paramRunnable)
  {
    if ((this.currentState == 11) || ((this.currentState == 8) && (this.isOutgoing)));
    for (int i = 3; ; i = 1)
    {
      declineIncomingCall(i, paramRunnable);
      return;
    }
  }

  public boolean hasEarpiece()
  {
    if (((TelephonyManager)getSystemService("phone")).getPhoneType() != 0)
      return true;
    if (this.mHasEarpiece != null)
      return this.mHasEarpiece.booleanValue();
    try
    {
      AudioManager localAudioManager = (AudioManager)getSystemService("audio");
      Method localMethod = AudioManager.class.getMethod("getDevicesForStream", new Class[] { Integer.TYPE });
      int i = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt(null);
      if ((((Integer)localMethod.invoke(localAudioManager, new Object[] { Integer.valueOf(0) })).intValue() & i) == i);
      for (this.mHasEarpiece = Boolean.TRUE; ; this.mHasEarpiece = Boolean.FALSE)
        return this.mHasEarpiece.booleanValue();
    }
    catch (Throwable localThrowable)
    {
      while (true)
      {
        FileLog.e("Error while checking earpiece! ", localThrowable);
        this.mHasEarpiece = Boolean.TRUE;
      }
    }
  }

  public boolean isBluetoothHeadsetConnected()
  {
    return this.isBtHeadsetConnected;
  }

  public boolean isMicMute()
  {
    return this.micMute;
  }

  public boolean isOutgoing()
  {
    return this.isOutgoing;
  }

  public void onAccuracyChanged(Sensor paramSensor, int paramInt)
  {
  }

  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == 1)
    {
      this.haveAudioFocus = true;
      return;
    }
    this.haveAudioFocus = false;
  }

  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }

  public void onCallUpdated(TLRPC.PhoneCall paramPhoneCall)
  {
    label16: if (this.call == null)
    {
      this.pendingUpdates.add(paramPhoneCall);
      break label16;
    }
    while (true)
    {
      return;
      if (paramPhoneCall == null)
        continue;
      if (paramPhoneCall.id != this.call.id)
      {
        if (!BuildVars.DEBUG_VERSION)
          break;
        FileLog.w("onCallUpdated called with wrong call id (got " + paramPhoneCall.id + ", expected " + this.call.id + ")");
        return;
      }
      if (paramPhoneCall.access_hash == 0L)
        paramPhoneCall.access_hash = this.call.access_hash;
      if (BuildVars.DEBUG_VERSION)
      {
        FileLog.d("Call updated: " + paramPhoneCall);
        dumpCallObject();
      }
      this.call = paramPhoneCall;
      if (!(paramPhoneCall instanceof TLRPC.TL_phoneCallDiscarded))
        break label245;
      this.needSendDebugLog = paramPhoneCall.need_debug;
      FileLog.d("call discarded, stopping service");
      if ((paramPhoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))
      {
        dispatchStateChanged(12);
        this.playingSound = true;
        this.soundPool.play(this.spBusyId, 1.0F, 1.0F, 0, -1, 1.0F);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            VoIPService.this.soundPool.release();
            if (VoIPService.this.isBtHeadsetConnected)
              ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio")).stopBluetoothSco();
          }
        }
        , 2500L);
        stopSelf();
      }
      while (paramPhoneCall.need_rating)
      {
        startRatingActivity();
        return;
        callEnded();
      }
    }
    label245: Object localObject1;
    Object localObject2;
    if (((paramPhoneCall instanceof TLRPC.TL_phoneCall)) && (this.authKey == null))
    {
      if (paramPhoneCall.g_a_or_b == null)
      {
        FileLog.w("stopping VoIP service, Ga == null");
        callFailed();
        return;
      }
      if (!Arrays.equals(this.g_a_hash, Utilities.computeSHA256(paramPhoneCall.g_a_or_b, 0, paramPhoneCall.g_a_or_b.length)))
      {
        FileLog.w("stopping VoIP service, Ga hash doesn't match");
        callFailed();
        return;
      }
      this.g_a = paramPhoneCall.g_a_or_b;
      localObject1 = new BigInteger(1, paramPhoneCall.g_a_or_b);
      localObject2 = new BigInteger(1, MessagesStorage.secretPBytes);
      if (!Utilities.isGoodGaAndGb((BigInteger)localObject1, (BigInteger)localObject2))
      {
        FileLog.w("stopping VoIP service, bad Ga and Gb (accepting)");
        callFailed();
        return;
      }
      localObject1 = ((BigInteger)localObject1).modPow(new BigInteger(1, this.a_or_b), (BigInteger)localObject2).toByteArray();
      if (localObject1.length > 256)
      {
        localObject2 = new byte[256];
        System.arraycopy(localObject1, localObject1.length - 256, localObject2, 0, 256);
        localObject1 = localObject2;
      }
    }
    while (true)
    {
      localObject2 = Utilities.computeSHA1(localObject1);
      byte[] arrayOfByte = new byte[8];
      System.arraycopy(localObject2, localObject2.length - 8, arrayOfByte, 0, 8);
      this.authKey = ((B)localObject1);
      this.keyFingerprint = Utilities.bytesToLong(arrayOfByte);
      if (this.keyFingerprint != paramPhoneCall.key_fingerprint)
      {
        FileLog.w("key fingerprints don't match");
        callFailed();
        return;
        if (localObject1.length < 256)
        {
          localObject2 = new byte[256];
          System.arraycopy(localObject1, 0, localObject2, 256 - localObject1.length, localObject1.length);
          int i = 0;
          while (i < 256 - localObject1.length)
          {
            localObject1[i] = 0;
            i += 1;
          }
          localObject1 = localObject2;
          continue;
        }
      }
      else
      {
        initiateActualEncryptedCall();
        return;
        if (((paramPhoneCall instanceof TLRPC.TL_phoneCallAccepted)) && (this.authKey == null))
        {
          processAcceptedCall();
          return;
        }
        if ((this.currentState != 8) || (paramPhoneCall.receive_date == 0))
          break;
        dispatchStateChanged(11);
        FileLog.d("!!!!!! CALL RECEIVED");
        if (this.spPlayID != 0)
          this.soundPool.stop(this.spPlayID);
        this.spPlayID = this.soundPool.play(this.spRingbackID, 1.0F, 1.0F, 0, -1, 1.0F);
        if (this.timeoutRunnable != null)
        {
          AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
          this.timeoutRunnable = null;
        }
        this.timeoutRunnable = new Runnable()
        {
          public void run()
          {
            VoIPService.access$1702(VoIPService.this, null);
            VoIPService.this.declineIncomingCall(3, null);
          }
        };
        AndroidUtilities.runOnUIThread(this.timeoutRunnable, MessagesController.getInstance().callRingTimeout);
        return;
      }
    }
  }

  public void onConnectionStateChanged(int paramInt)
  {
    if (paramInt == 4)
    {
      callFailed();
      return;
    }
    if (paramInt == 3)
    {
      if (this.spPlayID != 0)
      {
        this.soundPool.stop(this.spPlayID);
        this.spPlayID = 0;
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (VoIPService.this.controller == null)
            return;
          int j = 1;
          int i = j;
          if (VoIPService.this.lastNetInfo != null)
          {
            i = j;
            if (VoIPService.this.lastNetInfo.getType() == 0)
              if (!VoIPService.this.lastNetInfo.isRoaming())
                break label71;
          }
          label71: for (i = 2; ; i = 0)
          {
            StatsController.getInstance().incrementTotalCallsTime(i, 5);
            AndroidUtilities.runOnUIThread(this, 5000L);
            return;
          }
        }
      }
      , 5000L);
      if (!this.isOutgoing)
        break label77;
      StatsController.getInstance().incrementSentItemsCount(getStatsNetworkType(), 0, 1);
    }
    while (true)
    {
      dispatchStateChanged(paramInt);
      return;
      label77: StatsController.getInstance().incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
    }
  }

  public void onCreate()
  {
    super.onCreate();
    FileLog.d("=============== VoIPService STARTING ===============");
    AudioManager localAudioManager = (AudioManager)getSystemService("audio");
    Object localObject1;
    if ((Build.VERSION.SDK_INT >= 17) && (localAudioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") != null))
    {
      VoIPController.setNativeBufferSize(Integer.parseInt(localAudioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
      localObject1 = getSharedPreferences("mainconfig", 0);
      VoIPServerConfig.setConfig(((SharedPreferences)localObject1).getString("voip_server_config", "{}"));
      if (System.currentTimeMillis() - ((SharedPreferences)localObject1).getLong("voip_server_config_updated", 0L) > 86400000L)
        ConnectionsManager.getInstance().sendRequest(new TLRPC.TL_phone_getCallConfig(), new RequestDelegate((SharedPreferences)localObject1)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (paramTL_error == null)
            {
              paramTLObject = ((TLRPC.TL_dataJSON)paramTLObject).data;
              VoIPServerConfig.setConfig(paramTLObject);
              this.val$preferences.edit().putString("voip_server_config", paramTLObject).putLong("voip_server_config_updated", System.currentTimeMillis()).apply();
            }
          }
        });
    }
    while (true)
    {
      try
      {
        this.controller = new VoIPController();
        this.controller.setConnectionStateListener(this);
        this.controller.setConfig(MessagesController.getInstance().callPacketTimeout / 1000.0D, MessagesController.getInstance().callConnectTimeout / 1000.0D, ((SharedPreferences)localObject1).getInt("VoipDataSaving", 0));
        this.cpuWakelock = ((PowerManager)getSystemService("power")).newWakeLock(1, "telegram-voip");
        this.cpuWakelock.acquire();
        if (!localAudioManager.isBluetoothScoAvailableOffCall())
          break label596;
        localObject1 = BluetoothAdapter.getDefaultAdapter();
        this.btAdapter = ((BluetoothAdapter)localObject1);
        localObject1 = new IntentFilter();
        ((IntentFilter)localObject1).addAction("android.net.conn.CONNECTIVITY_CHANGE");
        ((IntentFilter)localObject1).addAction("android.intent.action.HEADSET_PLUG");
        if (this.btAdapter == null)
          continue;
        ((IntentFilter)localObject1).addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        ((IntentFilter)localObject1).addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
        ((IntentFilter)localObject1).addAction("android.intent.action.PHONE_STATE");
        ((IntentFilter)localObject1).addAction(getPackageName() + ".END_CALL");
        ((IntentFilter)localObject1).addAction(getPackageName() + ".DECLINE_CALL");
        ((IntentFilter)localObject1).addAction(getPackageName() + ".ANSWER_CALL");
        registerReceiver(this.receiver, (IntentFilter)localObject1);
        ConnectionsManager.getInstance().setAppPaused(false, false);
        this.soundPool = new SoundPool(1, 0, 0);
        this.spConnectingId = this.soundPool.load(this, 2131099651, 1);
        this.spRingbackID = this.soundPool.load(this, 2131099654, 1);
        this.spFailedID = this.soundPool.load(this, 2131099653, 1);
        this.spEndId = this.soundPool.load(this, 2131099652, 1);
        this.spBusyId = this.soundPool.load(this, 2131099650, 1);
        localAudioManager.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
        if ((this.btAdapter == null) || (!this.btAdapter.isEnabled()))
          break label606;
        int i = this.btAdapter.getProfileConnectionState(1);
        if (i != 2)
          break label601;
        bool = true;
        updateBluetoothHeadsetState(bool);
        if (i != 2)
          continue;
        localAudioManager.setBluetoothScoOn(true);
        localObject1 = this.stateListeners.iterator();
        if (!((Iterator)localObject1).hasNext())
          break label606;
        ((StateListener)((Iterator)localObject1).next()).onAudioSettingsChanged();
        continue;
      }
      catch (Exception localException)
      {
        FileLog.e("error initializing voip controller", localException);
        callFailed();
        return;
      }
      VoIPController.setNativeBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
      break;
      label596: Object localObject2 = null;
      continue;
      label601: boolean bool = false;
    }
    label606: NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
  }

  public void onDestroy()
  {
    FileLog.d("=============== VoIPService STOPPING ===============");
    stopForeground(true);
    stopRinging();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
    Object localObject = (SensorManager)getSystemService("sensor");
    if (((SensorManager)localObject).getDefaultSensor(8) != null)
      ((SensorManager)localObject).unregisterListener(this);
    if ((this.proximityWakelock != null) && (this.proximityWakelock.isHeld()))
      this.proximityWakelock.release();
    unregisterReceiver(this.receiver);
    if (this.timeoutRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
      this.timeoutRunnable = null;
    }
    super.onDestroy();
    sharedInstance = null;
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didEndedCall, new Object[0]);
      }
    });
    if ((this.controller != null) && (this.controllerStarted))
    {
      this.lastKnownDuration = this.controller.getCallDuration();
      updateStats();
      StatsController.getInstance().incrementTotalCallsTime(getStatsNetworkType(), (int)(this.lastKnownDuration / 1000L) % 5);
      if (this.needSendDebugLog)
      {
        localObject = this.controller.getDebugLog();
        TLRPC.TL_phone_saveCallDebug localTL_phone_saveCallDebug = new TLRPC.TL_phone_saveCallDebug();
        localTL_phone_saveCallDebug.debug = new TLRPC.TL_dataJSON();
        localTL_phone_saveCallDebug.debug.data = ((String)localObject);
        localTL_phone_saveCallDebug.peer = new TLRPC.TL_inputPhoneCall();
        localTL_phone_saveCallDebug.peer.access_hash = this.call.access_hash;
        localTL_phone_saveCallDebug.peer.id = this.call.id;
        ConnectionsManager.getInstance().sendRequest(localTL_phone_saveCallDebug, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            FileLog.d("Sent debug logs, response=" + paramTLObject);
          }
        });
      }
      this.controller.release();
      this.controller = null;
    }
    this.cpuWakelock.release();
    localObject = (AudioManager)getSystemService("audio");
    if ((this.isBtHeadsetConnected) && (!this.playingSound))
      ((AudioManager)localObject).stopBluetoothSco();
    ((AudioManager)localObject).setMode(0);
    ((AudioManager)localObject).unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
    if (this.haveAudioFocus)
      ((AudioManager)localObject).abandonAudioFocus(this);
    if (!this.playingSound)
      this.soundPool.release();
    ConnectionsManager.getInstance().setAppPaused(true, false);
  }

  void onMediaButtonEvent(KeyEvent paramKeyEvent)
  {
    boolean bool = true;
    if ((paramKeyEvent.getKeyCode() == 79) && (paramKeyEvent.getAction() == 1))
    {
      if (this.currentState == 10)
        acceptIncomingCall();
    }
    else
      return;
    if (!isMicMute());
    while (true)
    {
      setMicMute(bool);
      paramKeyEvent = this.stateListeners.iterator();
      while (paramKeyEvent.hasNext())
        ((StateListener)paramKeyEvent.next()).onAudioSettingsChanged();
      break;
      bool = false;
    }
  }

  @SuppressLint({"NewApi"})
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (paramSensorEvent.sensor.getType() == 8)
    {
      AudioManager localAudioManager = (AudioManager)getSystemService("audio");
      if ((!this.isHeadsetPlugged) && (!localAudioManager.isSpeakerphoneOn()) && ((!isBluetoothHeadsetConnected()) || (!localAudioManager.isBluetoothScoOn())))
        break label52;
    }
    while (true)
    {
      return;
      label52: if (paramSensorEvent.values[0] < Math.min(paramSensorEvent.sensor.getMaximumRange(), 3.0F));
      for (boolean bool = true; bool != this.isProximityNear; bool = false)
      {
        FileLog.d("proximity " + bool);
        this.isProximityNear = bool;
        try
        {
          if (!this.isProximityNear)
            break label139;
          this.proximityWakelock.acquire();
          return;
        }
        catch (Exception paramSensorEvent)
        {
          FileLog.e(paramSensorEvent);
          return;
        }
      }
    }
    label139: this.proximityWakelock.release(1);
  }

  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    if (sharedInstance != null)
    {
      FileLog.e("Tried to start the VoIP service when it's already started");
      return 2;
    }
    this.userID = paramIntent.getIntExtra("user_id", 0);
    this.isOutgoing = paramIntent.getBooleanExtra("is_outgoing", false);
    this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.userID));
    if (this.user == null)
    {
      FileLog.w("VoIPService: user==null");
      stopSelf();
      return 2;
    }
    if (this.isOutgoing)
    {
      startOutgoingCall();
      if (paramIntent.getBooleanExtra("start_incall_activity", false))
        startActivity(new Intent(this, VoIPActivity.class).addFlags(268435456));
    }
    while (true)
    {
      sharedInstance = this;
      return 2;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
      this.call = callIShouldHavePutIntoIntent;
      callIShouldHavePutIntoIntent = null;
      acknowledgeCallAndStartRinging();
    }
  }

  public void onUIForegroundStateChanged(boolean paramBoolean)
  {
    if (this.currentState == 10)
    {
      if (paramBoolean)
        stopForeground(true);
    }
    else
      return;
    if (!((KeyguardManager)getSystemService("keyguard")).inKeyguardRestrictedInputMode())
    {
      showIncomingNotification();
      return;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        Intent localIntent = new Intent(VoIPService.this, VoIPActivity.class);
        localIntent.addFlags(805306368);
        try
        {
          PendingIntent.getActivity(VoIPService.this, 0, localIntent, 0).send();
          return;
        }
        catch (PendingIntent.CanceledException localCanceledException)
        {
          FileLog.e("error restarting activity", localCanceledException);
        }
      }
    }
    , 500L);
  }

  public void registerStateListener(StateListener paramStateListener)
  {
    this.stateListeners.add(paramStateListener);
    if (this.currentState != 0)
      paramStateListener.onStateChanged(this.currentState);
  }

  public void setMicMute(boolean paramBoolean)
  {
    VoIPController localVoIPController = this.controller;
    this.micMute = paramBoolean;
    localVoIPController.setMicMute(paramBoolean);
  }

  public void stopRinging()
  {
    if (this.ringtonePlayer != null)
    {
      this.ringtonePlayer.stop();
      this.ringtonePlayer.release();
      this.ringtonePlayer = null;
    }
    if (this.vibrator != null)
    {
      this.vibrator.cancel();
      this.vibrator = null;
    }
  }

  public void unregisterStateListener(StateListener paramStateListener)
  {
    this.stateListeners.remove(paramStateListener);
  }

  public static abstract interface StateListener
  {
    public abstract void onAudioSettingsChanged();

    public abstract void onStateChanged(int paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.voip.VoIPService
 * JD-Core Version:    0.6.0
 */