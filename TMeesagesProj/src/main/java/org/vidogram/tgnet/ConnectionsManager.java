package org.vidogram.tgnet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.StatsController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;

public class ConnectionsManager
{
  public static final int ConnectionStateConnected = 3;
  public static final int ConnectionStateConnecting = 1;
  public static final int ConnectionStateUpdating = 4;
  public static final int ConnectionStateWaitingForNetwork = 2;
  public static final int ConnectionTypeDownload = 2;
  public static final int ConnectionTypeDownload2 = 65538;
  public static final int ConnectionTypeGeneric = 1;
  public static final int ConnectionTypePush = 8;
  public static final int ConnectionTypeUpload = 4;
  public static final int ConnectionTypeUpload2 = 65540;
  public static final int DEFAULT_DATACENTER_ID = 2147483647;
  public static final int FileTypeAudio = 50331648;
  public static final int FileTypeFile = 67108864;
  public static final int FileTypePhoto = 16777216;
  public static final int FileTypeVideo = 33554432;
  private static volatile ConnectionsManager Instance = null;
  public static final int RequestFlagCanCompress = 4;
  public static final int RequestFlagEnableUnauthorized = 1;
  public static final int RequestFlagFailOnServerErrors = 2;
  public static final int RequestFlagForceDownload = 32;
  public static final int RequestFlagInvokeAfter = 64;
  public static final int RequestFlagNeedQuickAck = 128;
  public static final int RequestFlagTryDifferentDc = 16;
  public static final int RequestFlagWithoutLogin = 8;
  private boolean appPaused = true;
  private int appResumeCount;
  private int connectionState = native_getConnectionState();
  private boolean isUpdating;
  private int lastClassGuid = 1;
  private long lastPauseTime = System.currentTimeMillis();
  private AtomicInteger lastRequestToken = new AtomicInteger(1);
  private PowerManager.WakeLock wakeLock;

  public ConnectionsManager()
  {
    try
    {
      this.wakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
      this.wakeLock.setReferenceCounted(false);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  private void checkConnection()
  {
    native_setUseIpv6(useIpv6Address());
    native_setNetworkAvailable(isNetworkOnline(), getCurrentNetworkType());
  }

  public static int getCurrentNetworkType()
  {
    if (isConnectedOrConnectingToWiFi())
      return 1;
    if (isRoaming())
      return 2;
    return 0;
  }

  public static ConnectionsManager getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        ConnectionsManager localConnectionsManager = Instance;
        localObject1 = localConnectionsManager;
        if (localConnectionsManager == null)
        {
          localObject1 = new ConnectionsManager();
          Instance = (ConnectionsManager)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (ConnectionsManager)localObject2;
  }

  public static boolean isConnectedOrConnectingToWiFi()
  {
    try
    {
      Object localObject = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
      NetworkInfo.State localState = ((NetworkInfo)localObject).getState();
      if (localObject != null)
        if ((localState != NetworkInfo.State.CONNECTED) && (localState != NetworkInfo.State.CONNECTING))
        {
          localObject = NetworkInfo.State.SUSPENDED;
          if (localState != localObject);
        }
        else
        {
          return true;
        }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return false;
  }

  public static boolean isConnectedToWiFi()
  {
    try
    {
      Object localObject = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
      if (localObject != null)
      {
        localObject = ((NetworkInfo)localObject).getState();
        NetworkInfo.State localState = NetworkInfo.State.CONNECTED;
        if (localObject == localState)
          return true;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return false;
  }

  public static boolean isNetworkOnline()
  {
    try
    {
      Object localObject = (ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity");
      NetworkInfo localNetworkInfo = ((ConnectivityManager)localObject).getActiveNetworkInfo();
      if ((localNetworkInfo != null) && ((localNetworkInfo.isConnectedOrConnecting()) || (localNetworkInfo.isAvailable())))
        break label87;
      localNetworkInfo = ((ConnectivityManager)localObject).getNetworkInfo(0);
      if ((localNetworkInfo != null) && (localNetworkInfo.isConnectedOrConnecting()))
        return true;
      localObject = ((ConnectivityManager)localObject).getNetworkInfo(1);
      if (localObject != null)
      {
        boolean bool = ((NetworkInfo)localObject).isConnectedOrConnecting();
        if (bool)
          return true;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
      return true;
    }
    return false;
    label87: return true;
  }

  public static boolean isRoaming()
  {
    try
    {
      NetworkInfo localNetworkInfo = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
      if (localNetworkInfo != null)
      {
        boolean bool = localNetworkInfo.isRoaming();
        return bool;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return false;
  }

  public static native void native_applyDatacenterAddress(int paramInt1, String paramString, int paramInt2);

  public static native void native_bindRequestToGuid(int paramInt1, int paramInt2);

  public static native void native_cancelRequest(int paramInt, boolean paramBoolean);

  public static native void native_cancelRequestsForGuid(int paramInt);

  public static native void native_cleanUp();

  public static native int native_getConnectionState();

  public static native int native_getCurrentTime();

  public static native long native_getCurrentTimeMillis();

  public static native int native_getTimeDifference();

  public static native void native_init(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, int paramInt5);

  public static native void native_pauseNetwork();

  public static native void native_resumeNetwork(boolean paramBoolean);

  public static native void native_sendRequest(int paramInt1, RequestDelegateInternal paramRequestDelegateInternal, QuickAckDelegate paramQuickAckDelegate, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5);

  public static native void native_setJava(boolean paramBoolean);

  public static native void native_setNetworkAvailable(boolean paramBoolean, int paramInt);

  public static native void native_setPushConnectionEnabled(boolean paramBoolean);

  public static native void native_setUseIpv6(boolean paramBoolean);

  public static native void native_setUserId(int paramInt);

  public static native void native_switchBackend();

  public static native void native_updateDcSettings();

  public static void onBytesReceived(int paramInt1, int paramInt2)
  {
    try
    {
      StatsController.getInstance().incrementReceivedBytesCount(paramInt2, 6, paramInt1);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public static void onBytesSent(int paramInt1, int paramInt2)
  {
    try
    {
      StatsController.getInstance().incrementSentBytesCount(paramInt2, 6, paramInt1);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public static void onConnectionStateChanged(int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInt)
    {
      public void run()
      {
        ConnectionsManager.access$202(ConnectionsManager.getInstance(), this.val$state);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
      }
    });
  }

  public static void onInternalPushReceived()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        try
        {
          if (!ConnectionsManager.getInstance().wakeLock.isHeld())
          {
            ConnectionsManager.getInstance().wakeLock.acquire(10000L);
            FileLog.d("acquire wakelock");
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public static void onLogout()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (UserConfig.getClientUserId() != 0)
        {
          UserConfig.clearConfig();
          MessagesController.getInstance().performLogout(false);
        }
      }
    });
  }

  public static void onSessionCreated()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().getDifference();
      }
    });
  }

  public static void onUnparsedMessageReceived(int paramInt)
  {
    try
    {
      Object localObject = NativeByteBuffer.wrap(paramInt);
      ((NativeByteBuffer)localObject).reused = true;
      localObject = TLClassStore.Instance().TLdeserialize((NativeByteBuffer)localObject, ((NativeByteBuffer)localObject).readInt32(true), true);
      if ((localObject instanceof TLRPC.Updates))
      {
        FileLog.d("java received " + localObject);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (ConnectionsManager.getInstance().wakeLock.isHeld())
            {
              FileLog.d("release wakelock");
              ConnectionsManager.getInstance().wakeLock.release();
            }
          }
        });
        Utilities.stageQueue.postRunnable(new Runnable((TLObject)localObject)
        {
          public void run()
          {
            MessagesController.getInstance().processUpdates((TLRPC.Updates)this.val$message, false);
          }
        });
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public static void onUpdate()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().updateTimerProc();
      }
    });
  }

  public static void onUpdateConfig(int paramInt)
  {
    try
    {
      Object localObject = NativeByteBuffer.wrap(paramInt);
      ((NativeByteBuffer)localObject).reused = true;
      localObject = TLRPC.TL_config.TLdeserialize((AbstractSerializedData)localObject, ((NativeByteBuffer)localObject).readInt32(true), true);
      if (localObject != null)
        Utilities.stageQueue.postRunnable(new Runnable((TLRPC.TL_config)localObject)
        {
          public void run()
          {
            MessagesController.getInstance().updateConfig(this.val$message);
          }
        });
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  @SuppressLint({"NewApi"})
  protected static boolean useIpv6Address()
  {
    int i1 = 1;
    if (Build.VERSION.SDK_INT < 19)
    {
      i1 = 0;
      return i1;
    }
    Object localObject;
    int i;
    label112: InetAddress localInetAddress;
    if (BuildVars.DEBUG_VERSION)
      try
      {
        Enumeration localEnumeration1 = NetworkInterface.getNetworkInterfaces();
        while (localEnumeration1.hasMoreElements())
        {
          localObject = (NetworkInterface)localEnumeration1.nextElement();
          if ((!((NetworkInterface)localObject).isUp()) || (((NetworkInterface)localObject).isLoopback()) || (((NetworkInterface)localObject).getInterfaceAddresses().isEmpty()))
            continue;
          FileLog.e("valid interface: " + localObject);
          localObject = ((NetworkInterface)localObject).getInterfaceAddresses();
          i = 0;
          if (i >= ((List)localObject).size())
            continue;
          localInetAddress = ((InterfaceAddress)((List)localObject).get(i)).getAddress();
          if (BuildVars.DEBUG_VERSION)
            FileLog.e("address: " + localInetAddress.getHostAddress());
          if ((localInetAddress.isLinkLocalAddress()) || (localInetAddress.isLoopbackAddress()) || (localInetAddress.isMulticastAddress()) || (!BuildVars.DEBUG_VERSION))
            break label428;
          FileLog.e("address is good");
        }
      }
      catch (Throwable localThrowable1)
      {
        FileLog.e(localThrowable1);
      }
    while (true)
    {
      try
      {
        Enumeration localEnumeration2 = NetworkInterface.getNetworkInterfaces();
        int n = 0;
        m = 0;
        if (!localEnumeration2.hasMoreElements())
          continue;
        localObject = (NetworkInterface)localEnumeration2.nextElement();
        if ((!((NetworkInterface)localObject).isUp()) || (((NetworkInterface)localObject).isLoopback()))
          continue;
        localObject = ((NetworkInterface)localObject).getInterfaceAddresses();
        k = 0;
        i = m;
        j = n;
        n = j;
        m = i;
        if (k >= ((List)localObject).size())
          continue;
        localInetAddress = ((InterfaceAddress)((List)localObject).get(k)).getAddress();
        if ((localInetAddress.isLinkLocalAddress()) || (localInetAddress.isLoopbackAddress()))
          continue;
        if (!localInetAddress.isMulticastAddress())
          continue;
        m = i;
        i = j;
        j = m;
        break label435;
        if (!(localInetAddress instanceof Inet6Address))
          continue;
        m = 1;
        j = i;
        i = m;
        break label435;
        if (!(localInetAddress instanceof Inet4Address))
          continue;
        boolean bool = localInetAddress.getHostAddress().startsWith("192.0.0.");
        if (bool)
          continue;
        i = j;
        j = 1;
        break label435;
        if ((m == 0) && (n != 0))
          break;
        return false;
      }
      catch (Throwable localThrowable2)
      {
        FileLog.e(localThrowable2);
        continue;
        m = i;
        i = j;
        j = m;
      }
      label428: i += 1;
      break label112;
      label435: int m = k + 1;
      int k = j;
      int j = i;
      i = k;
      k = m;
    }
  }

  public void applyCountryPortNumber(String paramString)
  {
  }

  public void applyDatacenterAddress(int paramInt1, String paramString, int paramInt2)
  {
    native_applyDatacenterAddress(paramInt1, paramString, paramInt2);
  }

  public void bindRequestToGuid(int paramInt1, int paramInt2)
  {
    native_bindRequestToGuid(paramInt1, paramInt2);
  }

  public void cancelRequest(int paramInt, boolean paramBoolean)
  {
    native_cancelRequest(paramInt, paramBoolean);
  }

  public void cancelRequestsForGuid(int paramInt)
  {
    native_cancelRequestsForGuid(paramInt);
  }

  public void cleanup()
  {
    native_cleanUp();
  }

  public int generateClassGuid()
  {
    int i = this.lastClassGuid;
    this.lastClassGuid = (i + 1);
    return i;
  }

  public int getConnectionState()
  {
    if ((this.connectionState == 3) && (this.isUpdating))
      return 4;
    return this.connectionState;
  }

  public int getCurrentTime()
  {
    return native_getCurrentTime();
  }

  public long getCurrentTimeMillis()
  {
    return native_getCurrentTimeMillis();
  }

  public long getPauseTime()
  {
    return this.lastPauseTime;
  }

  public int getTimeDifference()
  {
    return native_getTimeDifference();
  }

  public void init(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt4, boolean paramBoolean)
  {
    native_init(paramInt1, paramInt2, paramInt3, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramInt4, paramBoolean, isNetworkOnline(), getCurrentNetworkType());
    checkConnection();
    paramString1 = new BroadcastReceiver()
    {
      public void onReceive(Context paramContext, Intent paramIntent)
      {
        ConnectionsManager.this.checkConnection();
      }
    };
    paramString2 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    ApplicationLoader.applicationContext.registerReceiver(paramString1, paramString2);
  }

  public void resumeNetworkMaybe()
  {
    native_resumeNetwork(true);
  }

  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, 0);
  }

  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, int paramInt)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, paramInt, 2147483647, 1, true);
  }

  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, int paramInt1, int paramInt2)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, paramInt1, 2147483647, paramInt2, true);
  }

  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, QuickAckDelegate paramQuickAckDelegate, int paramInt)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, paramQuickAckDelegate, paramInt, 2147483647, 1, true);
  }

  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, QuickAckDelegate paramQuickAckDelegate, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = this.lastRequestToken.getAndIncrement();
    Utilities.stageQueue.postRunnable(new Runnable(paramTLObject, i, paramRequestDelegate, paramQuickAckDelegate, paramInt1, paramInt2, paramInt3, paramBoolean)
    {
      public void run()
      {
        FileLog.d("send request " + this.val$object + " with token = " + this.val$requestToken);
        try
        {
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(this.val$object.getObjectSize());
          this.val$object.serializeToStream(localNativeByteBuffer);
          this.val$object.freeResources();
          ConnectionsManager.native_sendRequest(localNativeByteBuffer.address, new RequestDelegateInternal()
          {
            public void run(int paramInt1, int paramInt2, String paramString, int paramInt3)
            {
              Object localObject = null;
              if (paramInt1 != 0);
              while (true)
              {
                try
                {
                  paramString = NativeByteBuffer.wrap(paramInt1);
                  paramString.reused = true;
                  paramString = ConnectionsManager.1.this.val$object.deserializeResponse(paramString, paramString.readInt32(true), true);
                  if (paramString == null)
                    continue;
                  paramString.networkType = paramInt3;
                  FileLog.d("java received " + paramString + " error = " + localObject);
                  Utilities.stageQueue.postRunnable(new Runnable(paramString, (TLRPC.TL_error)localObject)
                  {
                    public void run()
                    {
                      ConnectionsManager.1.this.val$onComplete.run(this.val$finalResponse, this.val$finalError);
                      if (this.val$finalResponse != null)
                        this.val$finalResponse.freeResources();
                    }
                  });
                  return;
                  if (paramString != null)
                  {
                    localObject = new TLRPC.TL_error();
                    ((TLRPC.TL_error)localObject).code = paramInt2;
                    ((TLRPC.TL_error)localObject).text = paramString;
                    FileLog.e(ConnectionsManager.1.this.val$object + " got error " + ((TLRPC.TL_error)localObject).code + " " + ((TLRPC.TL_error)localObject).text);
                    paramString = null;
                    continue;
                  }
                }
                catch (Exception paramString)
                {
                  FileLog.e(paramString);
                  return;
                }
                paramString = null;
              }
            }
          }
          , this.val$onQuickAck, this.val$flags, this.val$datacenterId, this.val$connetionType, this.val$immediate, this.val$requestToken);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
    return i;
  }

  public void setAppPaused(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean2)
    {
      this.appPaused = paramBoolean1;
      FileLog.d("app paused = " + paramBoolean1);
      if (!paramBoolean1)
        break label111;
      this.appResumeCount -= 1;
      FileLog.d("app resume count " + this.appResumeCount);
      if (this.appResumeCount < 0)
        this.appResumeCount = 0;
    }
    if (this.appResumeCount == 0)
    {
      if (this.lastPauseTime == 0L)
        this.lastPauseTime = System.currentTimeMillis();
      native_pauseNetwork();
    }
    label111: 
    do
    {
      return;
      this.appResumeCount += 1;
      break;
    }
    while (this.appPaused);
    FileLog.e("reset app pause time");
    if ((this.lastPauseTime != 0L) && (System.currentTimeMillis() - this.lastPauseTime > 5000L))
      ContactsController.getInstance().checkContacts();
    this.lastPauseTime = 0L;
    native_resumeNetwork(false);
  }

  public void setIsUpdating(boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramBoolean)
    {
      public void run()
      {
        if (ConnectionsManager.this.isUpdating == this.val$value);
        do
        {
          return;
          ConnectionsManager.access$302(ConnectionsManager.this, this.val$value);
        }
        while (ConnectionsManager.this.connectionState != 3);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
      }
    });
  }

  public void setPushConnectionEnabled(boolean paramBoolean)
  {
    native_setPushConnectionEnabled(paramBoolean);
  }

  public void setUserId(int paramInt)
  {
    native_setUserId(paramInt);
  }

  public void switchBackend()
  {
    native_switchBackend();
  }

  public void updateDcSettings()
  {
    native_updateDcSettings();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.tgnet.ConnectionsManager
 * JD-Core Version:    0.6.0
 */