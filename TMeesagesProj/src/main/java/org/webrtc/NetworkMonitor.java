package org.webrtc;

import android.content.Context;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetworkMonitor
{
  private static final String TAG = "NetworkMonitor";
  private static NetworkMonitor instance;
  private final Context applicationContext;
  private NetworkMonitorAutoDetect autoDetector;
  private NetworkMonitorAutoDetect.ConnectionType currentConnectionType = NetworkMonitorAutoDetect.ConnectionType.CONNECTION_UNKNOWN;
  private final ArrayList<Long> nativeNetworkObservers;
  private final ArrayList<NetworkObserver> networkObservers;

  private NetworkMonitor(Context paramContext)
  {
    boolean bool;
    if (paramContext != null)
    {
      bool = true;
      assertIsTrue(bool);
      if (paramContext.getApplicationContext() != null)
        break label61;
    }
    while (true)
    {
      this.applicationContext = paramContext;
      this.nativeNetworkObservers = new ArrayList();
      this.networkObservers = new ArrayList();
      return;
      bool = false;
      break;
      label61: paramContext = paramContext.getApplicationContext();
    }
  }

  public static void addNetworkObserver(NetworkObserver paramNetworkObserver)
  {
    getInstance().addNetworkObserverInternal(paramNetworkObserver);
  }

  private void addNetworkObserverInternal(NetworkObserver paramNetworkObserver)
  {
    this.networkObservers.add(paramNetworkObserver);
  }

  private static int androidSdkInt()
  {
    return Build.VERSION.SDK_INT;
  }

  private static void assertIsTrue(boolean paramBoolean)
  {
    if (!paramBoolean)
      throw new AssertionError("Expected to be true");
  }

  private void destroyAutoDetector()
  {
    if (this.autoDetector != null)
    {
      this.autoDetector.destroy();
      this.autoDetector = null;
    }
  }

  public static NetworkMonitorAutoDetect getAutoDetectorForTest()
  {
    return getInstance().autoDetector;
  }

  private NetworkMonitorAutoDetect.ConnectionType getCurrentConnectionType()
  {
    return this.currentConnectionType;
  }

  private long getCurrentDefaultNetId()
  {
    if (this.autoDetector == null)
      return -1L;
    return this.autoDetector.getDefaultNetId();
  }

  public static NetworkMonitor getInstance()
  {
    return instance;
  }

  public static NetworkMonitor init(Context paramContext)
  {
    if (!isInitialized())
      instance = new NetworkMonitor(paramContext);
    return instance;
  }

  public static boolean isInitialized()
  {
    return instance != null;
  }

  public static boolean isOnline()
  {
    return getInstance().getCurrentConnectionType() != NetworkMonitorAutoDetect.ConnectionType.CONNECTION_NONE;
  }

  private native void nativeNotifyConnectionTypeChanged(long paramLong);

  private native void nativeNotifyOfActiveNetworkList(long paramLong, NetworkMonitorAutoDetect.NetworkInformation[] paramArrayOfNetworkInformation);

  private native void nativeNotifyOfNetworkConnect(long paramLong, NetworkMonitorAutoDetect.NetworkInformation paramNetworkInformation);

  private native void nativeNotifyOfNetworkDisconnect(long paramLong1, long paramLong2);

  private void notifyObserversOfConnectionTypeChange(NetworkMonitorAutoDetect.ConnectionType paramConnectionType)
  {
    Iterator localIterator = this.nativeNetworkObservers.iterator();
    while (localIterator.hasNext())
      nativeNotifyConnectionTypeChanged(((Long)localIterator.next()).longValue());
    localIterator = this.networkObservers.iterator();
    while (localIterator.hasNext())
      ((NetworkObserver)localIterator.next()).onConnectionTypeChanged(paramConnectionType);
  }

  private void notifyObserversOfNetworkConnect(NetworkMonitorAutoDetect.NetworkInformation paramNetworkInformation)
  {
    Iterator localIterator = this.nativeNetworkObservers.iterator();
    while (localIterator.hasNext())
      nativeNotifyOfNetworkConnect(((Long)localIterator.next()).longValue(), paramNetworkInformation);
  }

  private void notifyObserversOfNetworkDisconnect(long paramLong)
  {
    Iterator localIterator = this.nativeNetworkObservers.iterator();
    while (localIterator.hasNext())
      nativeNotifyOfNetworkDisconnect(((Long)localIterator.next()).longValue(), paramLong);
  }

  public static void removeNetworkObserver(NetworkObserver paramNetworkObserver)
  {
    getInstance().removeNetworkObserverInternal(paramNetworkObserver);
  }

  private void removeNetworkObserverInternal(NetworkObserver paramNetworkObserver)
  {
    this.networkObservers.remove(paramNetworkObserver);
  }

  static void resetInstanceForTests(Context paramContext)
  {
    instance = new NetworkMonitor(paramContext);
  }

  public static void setAutoDetectConnectivityState(boolean paramBoolean)
  {
    getInstance().setAutoDetectConnectivityStateInternal(paramBoolean);
  }

  private void setAutoDetectConnectivityStateInternal(boolean paramBoolean)
  {
    if (!paramBoolean)
      destroyAutoDetector();
    do
      return;
    while (this.autoDetector != null);
    this.autoDetector = new NetworkMonitorAutoDetect(new NetworkMonitorAutoDetect.Observer()
    {
      public void onConnectionTypeChanged(NetworkMonitorAutoDetect.ConnectionType paramConnectionType)
      {
        NetworkMonitor.this.updateCurrentConnectionType(paramConnectionType);
      }

      public void onNetworkConnect(NetworkMonitorAutoDetect.NetworkInformation paramNetworkInformation)
      {
        NetworkMonitor.this.notifyObserversOfNetworkConnect(paramNetworkInformation);
      }

      public void onNetworkDisconnect(long paramLong)
      {
        NetworkMonitor.this.notifyObserversOfNetworkDisconnect(paramLong);
      }
    }
    , this.applicationContext);
    updateCurrentConnectionType(NetworkMonitorAutoDetect.getConnectionType(this.autoDetector.getCurrentNetworkState()));
    updateActiveNetworkList();
  }

  private void startMonitoring(long paramLong)
  {
    Logging.d("NetworkMonitor", "Start monitoring from native observer " + paramLong);
    this.nativeNetworkObservers.add(Long.valueOf(paramLong));
    setAutoDetectConnectivityStateInternal(true);
  }

  private void stopMonitoring(long paramLong)
  {
    Logging.d("NetworkMonitor", "Stop monitoring from native observer " + paramLong);
    setAutoDetectConnectivityStateInternal(false);
    this.nativeNetworkObservers.remove(Long.valueOf(paramLong));
  }

  private void updateActiveNetworkList()
  {
    Object localObject = this.autoDetector.getActiveNetworkList();
    if ((localObject == null) || (((List)localObject).size() == 0));
    while (true)
    {
      return;
      localObject = (NetworkMonitorAutoDetect.NetworkInformation[])((List)localObject).toArray(new NetworkMonitorAutoDetect.NetworkInformation[((List)localObject).size()]);
      Iterator localIterator = this.nativeNetworkObservers.iterator();
      while (localIterator.hasNext())
        nativeNotifyOfActiveNetworkList(((Long)localIterator.next()).longValue(), localObject);
    }
  }

  private void updateCurrentConnectionType(NetworkMonitorAutoDetect.ConnectionType paramConnectionType)
  {
    this.currentConnectionType = paramConnectionType;
    notifyObserversOfConnectionTypeChange(paramConnectionType);
  }

  public static abstract interface NetworkObserver
  {
    public abstract void onConnectionTypeChanged(NetworkMonitorAutoDetect.ConnectionType paramConnectionType);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.NetworkMonitor
 * JD-Core Version:    0.6.0
 */