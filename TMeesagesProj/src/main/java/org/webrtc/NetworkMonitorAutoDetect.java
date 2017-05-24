package org.webrtc;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest.Builder;
import android.net.wifi.WifiInfo;
import android.os.Build.VERSION;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetworkMonitorAutoDetect extends BroadcastReceiver
{
  static final long INVALID_NET_ID = -1L;
  private static final String TAG = "NetworkMonitorAutoDetect";
  private final ConnectivityManager.NetworkCallback allNetworkCallback;
  private ConnectionType connectionType;
  private ConnectivityManagerDelegate connectivityManagerDelegate;
  private final Context context;
  private final IntentFilter intentFilter;
  private boolean isRegistered;
  private final ConnectivityManager.NetworkCallback mobileNetworkCallback;
  private final Observer observer;
  private WifiManagerDelegate wifiManagerDelegate;
  private String wifiSSID;

  @SuppressLint({"NewApi"})
  public NetworkMonitorAutoDetect(Observer paramObserver, Context paramContext)
  {
    this.observer = paramObserver;
    this.context = paramContext;
    this.connectivityManagerDelegate = new ConnectivityManagerDelegate(paramContext);
    this.wifiManagerDelegate = new WifiManagerDelegate(paramContext);
    paramObserver = this.connectivityManagerDelegate.getNetworkState();
    this.connectionType = getConnectionType(paramObserver);
    this.wifiSSID = getWifiSSID(paramObserver);
    this.intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    registerReceiver();
    if (this.connectivityManagerDelegate.supportNetworkCallback())
    {
      paramObserver = new ConnectivityManager.NetworkCallback();
      try
      {
        this.connectivityManagerDelegate.requestMobileNetwork(paramObserver);
        this.mobileNetworkCallback = paramObserver;
        this.allNetworkCallback = new SimpleNetworkCallback(null);
        this.connectivityManagerDelegate.registerNetworkCallback(this.allNetworkCallback);
        return;
      }
      catch (java.lang.SecurityException paramObserver)
      {
        while (true)
        {
          Logging.w("NetworkMonitorAutoDetect", "Unable to obtain permission to request a cellular network.");
          paramObserver = null;
        }
      }
    }
    this.mobileNetworkCallback = null;
    this.allNetworkCallback = null;
  }

  private void connectionTypeChanged(NetworkState paramNetworkState)
  {
    ConnectionType localConnectionType = getConnectionType(paramNetworkState);
    paramNetworkState = getWifiSSID(paramNetworkState);
    if ((localConnectionType == this.connectionType) && (paramNetworkState.equals(this.wifiSSID)))
      return;
    this.connectionType = localConnectionType;
    this.wifiSSID = paramNetworkState;
    Logging.d("NetworkMonitorAutoDetect", "Network connectivity changed, type is: " + this.connectionType);
    this.observer.onConnectionTypeChanged(localConnectionType);
  }

  public static ConnectionType getConnectionType(NetworkState paramNetworkState)
  {
    if (!paramNetworkState.isConnected())
      return ConnectionType.CONNECTION_NONE;
    switch (paramNetworkState.getNetworkType())
    {
    case 2:
    case 3:
    case 4:
    case 5:
    case 8:
    default:
      return ConnectionType.CONNECTION_UNKNOWN;
    case 9:
      return ConnectionType.CONNECTION_ETHERNET;
    case 1:
      return ConnectionType.CONNECTION_WIFI;
    case 6:
      return ConnectionType.CONNECTION_4G;
    case 7:
      return ConnectionType.CONNECTION_BLUETOOTH;
    case 0:
    }
    switch (paramNetworkState.getNetworkSubType())
    {
    default:
      return ConnectionType.CONNECTION_UNKNOWN_CELLULAR;
    case 1:
    case 2:
    case 4:
    case 7:
    case 11:
      return ConnectionType.CONNECTION_2G;
    case 3:
    case 5:
    case 6:
    case 8:
    case 9:
    case 10:
    case 12:
    case 14:
    case 15:
      return ConnectionType.CONNECTION_3G;
    case 13:
    }
    return ConnectionType.CONNECTION_4G;
  }

  private String getWifiSSID(NetworkState paramNetworkState)
  {
    if (getConnectionType(paramNetworkState) != ConnectionType.CONNECTION_WIFI)
      return "";
    return this.wifiManagerDelegate.getWifiSSID();
  }

  @SuppressLint({"NewApi"})
  private static long networkToNetId(Network paramNetwork)
  {
    if (Build.VERSION.SDK_INT >= 23)
      return paramNetwork.getNetworkHandle();
    return Integer.parseInt(paramNetwork.toString());
  }

  private void registerReceiver()
  {
    if (this.isRegistered)
      return;
    this.isRegistered = true;
    this.context.registerReceiver(this, this.intentFilter);
  }

  private void unregisterReceiver()
  {
    if (!this.isRegistered)
      return;
    this.isRegistered = false;
    this.context.unregisterReceiver(this);
  }

  public void destroy()
  {
    if (this.allNetworkCallback != null)
      this.connectivityManagerDelegate.releaseCallback(this.allNetworkCallback);
    if (this.mobileNetworkCallback != null)
      this.connectivityManagerDelegate.releaseCallback(this.mobileNetworkCallback);
    unregisterReceiver();
  }

  List<NetworkInformation> getActiveNetworkList()
  {
    return this.connectivityManagerDelegate.getActiveNetworkList();
  }

  public NetworkState getCurrentNetworkState()
  {
    return this.connectivityManagerDelegate.getNetworkState();
  }

  public long getDefaultNetId()
  {
    return this.connectivityManagerDelegate.getDefaultNetId();
  }

  boolean isReceiverRegisteredForTesting()
  {
    return this.isRegistered;
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = getCurrentNetworkState();
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramIntent.getAction()))
      connectionTypeChanged(paramContext);
  }

  void setConnectivityManagerDelegateForTests(ConnectivityManagerDelegate paramConnectivityManagerDelegate)
  {
    this.connectivityManagerDelegate = paramConnectivityManagerDelegate;
  }

  void setWifiManagerDelegateForTests(WifiManagerDelegate paramWifiManagerDelegate)
  {
    this.wifiManagerDelegate = paramWifiManagerDelegate;
  }

  public static enum ConnectionType
  {
    static
    {
      CONNECTION_ETHERNET = new ConnectionType("CONNECTION_ETHERNET", 1);
      CONNECTION_WIFI = new ConnectionType("CONNECTION_WIFI", 2);
      CONNECTION_4G = new ConnectionType("CONNECTION_4G", 3);
      CONNECTION_3G = new ConnectionType("CONNECTION_3G", 4);
      CONNECTION_2G = new ConnectionType("CONNECTION_2G", 5);
      CONNECTION_UNKNOWN_CELLULAR = new ConnectionType("CONNECTION_UNKNOWN_CELLULAR", 6);
      CONNECTION_BLUETOOTH = new ConnectionType("CONNECTION_BLUETOOTH", 7);
      CONNECTION_NONE = new ConnectionType("CONNECTION_NONE", 8);
      $VALUES = new ConnectionType[] { CONNECTION_UNKNOWN, CONNECTION_ETHERNET, CONNECTION_WIFI, CONNECTION_4G, CONNECTION_3G, CONNECTION_2G, CONNECTION_UNKNOWN_CELLULAR, CONNECTION_BLUETOOTH, CONNECTION_NONE };
    }
  }

  static class ConnectivityManagerDelegate
  {
    private final ConnectivityManager connectivityManager;

    static
    {
      if (!NetworkMonitorAutoDetect.class.desiredAssertionStatus());
      for (boolean bool = true; ; bool = false)
      {
        $assertionsDisabled = bool;
        return;
      }
    }

    ConnectivityManagerDelegate()
    {
      this.connectivityManager = null;
    }

    ConnectivityManagerDelegate(Context paramContext)
    {
      this.connectivityManager = ((ConnectivityManager)paramContext.getSystemService("connectivity"));
    }

    @SuppressLint({"NewApi"})
    private NetworkMonitorAutoDetect.NetworkInformation networkToInfo(Network paramNetwork)
    {
      LinkProperties localLinkProperties = this.connectivityManager.getLinkProperties(paramNetwork);
      if (localLinkProperties == null)
      {
        Logging.w("NetworkMonitorAutoDetect", "Detected unknown network: " + paramNetwork.toString());
        return null;
      }
      if (localLinkProperties.getInterfaceName() == null)
      {
        Logging.w("NetworkMonitorAutoDetect", "Null interface name for network " + paramNetwork.toString());
        return null;
      }
      NetworkMonitorAutoDetect.NetworkState localNetworkState = getNetworkState(paramNetwork);
      NetworkMonitorAutoDetect.ConnectionType localConnectionType = NetworkMonitorAutoDetect.getConnectionType(localNetworkState);
      if (localConnectionType == NetworkMonitorAutoDetect.ConnectionType.CONNECTION_NONE)
      {
        Logging.d("NetworkMonitorAutoDetect", "Network " + paramNetwork.toString() + " is disconnected");
        return null;
      }
      if ((localConnectionType == NetworkMonitorAutoDetect.ConnectionType.CONNECTION_UNKNOWN) || (localConnectionType == NetworkMonitorAutoDetect.ConnectionType.CONNECTION_UNKNOWN_CELLULAR))
        Logging.d("NetworkMonitorAutoDetect", "Network " + paramNetwork.toString() + " connection type is " + localConnectionType + " because it has type " + localNetworkState.getNetworkType() + " and subtype " + localNetworkState.getNetworkSubType());
      return new NetworkMonitorAutoDetect.NetworkInformation(localLinkProperties.getInterfaceName(), localConnectionType, NetworkMonitorAutoDetect.access$000(paramNetwork), getIPAddresses(localLinkProperties));
    }

    List<NetworkMonitorAutoDetect.NetworkInformation> getActiveNetworkList()
    {
      Object localObject;
      if (!supportNetworkCallback())
      {
        localObject = null;
        return localObject;
      }
      ArrayList localArrayList = new ArrayList();
      Network[] arrayOfNetwork = getAllNetworks();
      int j = arrayOfNetwork.length;
      int i = 0;
      while (true)
      {
        localObject = localArrayList;
        if (i >= j)
          break;
        localObject = networkToInfo(arrayOfNetwork[i]);
        if (localObject != null)
          localArrayList.add(localObject);
        i += 1;
      }
    }

    @SuppressLint({"NewApi"})
    Network[] getAllNetworks()
    {
      if (this.connectivityManager == null)
        return new Network[0];
      return this.connectivityManager.getAllNetworks();
    }

    @SuppressLint({"NewApi"})
    long getDefaultNetId()
    {
      long l2;
      if (!supportNetworkCallback())
        l2 = -1L;
      NetworkInfo localNetworkInfo1;
      Network[] arrayOfNetwork;
      int j;
      int i;
      long l1;
      do
      {
        return l2;
        localNetworkInfo1 = this.connectivityManager.getActiveNetworkInfo();
        if (localNetworkInfo1 == null)
          return -1L;
        arrayOfNetwork = getAllNetworks();
        j = arrayOfNetwork.length;
        i = 0;
        l1 = -1L;
        l2 = l1;
      }
      while (i >= j);
      Network localNetwork = arrayOfNetwork[i];
      if (!hasInternetCapability(localNetwork))
        l2 = l1;
      while (true)
      {
        i += 1;
        l1 = l2;
        break;
        NetworkInfo localNetworkInfo2 = this.connectivityManager.getNetworkInfo(localNetwork);
        l2 = l1;
        if (localNetworkInfo2 == null)
          continue;
        l2 = l1;
        if (localNetworkInfo2.getType() != localNetworkInfo1.getType())
          continue;
        assert (l1 == -1L);
        l2 = NetworkMonitorAutoDetect.access$000(localNetwork);
      }
    }

    @SuppressLint({"NewApi"})
    NetworkMonitorAutoDetect.IPAddress[] getIPAddresses(LinkProperties paramLinkProperties)
    {
      NetworkMonitorAutoDetect.IPAddress[] arrayOfIPAddress = new NetworkMonitorAutoDetect.IPAddress[paramLinkProperties.getLinkAddresses().size()];
      paramLinkProperties = paramLinkProperties.getLinkAddresses().iterator();
      int i = 0;
      while (paramLinkProperties.hasNext())
      {
        arrayOfIPAddress[i] = new NetworkMonitorAutoDetect.IPAddress(((LinkAddress)paramLinkProperties.next()).getAddress().getAddress());
        i += 1;
      }
      return arrayOfIPAddress;
    }

    NetworkMonitorAutoDetect.NetworkState getNetworkState()
    {
      if (this.connectivityManager == null)
        return new NetworkMonitorAutoDetect.NetworkState(false, -1, -1);
      return getNetworkState(this.connectivityManager.getActiveNetworkInfo());
    }

    @SuppressLint({"NewApi"})
    NetworkMonitorAutoDetect.NetworkState getNetworkState(Network paramNetwork)
    {
      if (this.connectivityManager == null)
        return new NetworkMonitorAutoDetect.NetworkState(false, -1, -1);
      return getNetworkState(this.connectivityManager.getNetworkInfo(paramNetwork));
    }

    NetworkMonitorAutoDetect.NetworkState getNetworkState(NetworkInfo paramNetworkInfo)
    {
      if ((paramNetworkInfo == null) || (!paramNetworkInfo.isConnected()))
        return new NetworkMonitorAutoDetect.NetworkState(false, -1, -1);
      return new NetworkMonitorAutoDetect.NetworkState(true, paramNetworkInfo.getType(), paramNetworkInfo.getSubtype());
    }

    @SuppressLint({"NewApi"})
    boolean hasInternetCapability(Network paramNetwork)
    {
      if (this.connectivityManager == null);
      do
      {
        return false;
        paramNetwork = this.connectivityManager.getNetworkCapabilities(paramNetwork);
      }
      while ((paramNetwork == null) || (!paramNetwork.hasCapability(12)));
      return true;
    }

    @SuppressLint({"NewApi"})
    public void registerNetworkCallback(ConnectivityManager.NetworkCallback paramNetworkCallback)
    {
      this.connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().addCapability(12).build(), paramNetworkCallback);
    }

    @SuppressLint({"NewApi"})
    public void releaseCallback(ConnectivityManager.NetworkCallback paramNetworkCallback)
    {
      if (supportNetworkCallback())
      {
        Logging.d("NetworkMonitorAutoDetect", "Unregister network callback");
        this.connectivityManager.unregisterNetworkCallback(paramNetworkCallback);
      }
    }

    @SuppressLint({"NewApi"})
    public void requestMobileNetwork(ConnectivityManager.NetworkCallback paramNetworkCallback)
    {
      NetworkRequest.Builder localBuilder = new NetworkRequest.Builder();
      localBuilder.addCapability(12).addTransportType(0);
      this.connectivityManager.requestNetwork(localBuilder.build(), paramNetworkCallback);
    }

    public boolean supportNetworkCallback()
    {
      return (Build.VERSION.SDK_INT >= 21) && (this.connectivityManager != null);
    }
  }

  public static class IPAddress
  {
    public final byte[] address;

    public IPAddress(byte[] paramArrayOfByte)
    {
      this.address = paramArrayOfByte;
    }
  }

  public static class NetworkInformation
  {
    public final long handle;
    public final NetworkMonitorAutoDetect.IPAddress[] ipAddresses;
    public final String name;
    public final NetworkMonitorAutoDetect.ConnectionType type;

    public NetworkInformation(String paramString, NetworkMonitorAutoDetect.ConnectionType paramConnectionType, long paramLong, NetworkMonitorAutoDetect.IPAddress[] paramArrayOfIPAddress)
    {
      this.name = paramString;
      this.type = paramConnectionType;
      this.handle = paramLong;
      this.ipAddresses = paramArrayOfIPAddress;
    }
  }

  static class NetworkState
  {
    private final boolean connected;
    private final int subtype;
    private final int type;

    public NetworkState(boolean paramBoolean, int paramInt1, int paramInt2)
    {
      this.connected = paramBoolean;
      this.type = paramInt1;
      this.subtype = paramInt2;
    }

    public int getNetworkSubType()
    {
      return this.subtype;
    }

    public int getNetworkType()
    {
      return this.type;
    }

    public boolean isConnected()
    {
      return this.connected;
    }
  }

  public static abstract interface Observer
  {
    public abstract void onConnectionTypeChanged(NetworkMonitorAutoDetect.ConnectionType paramConnectionType);

    public abstract void onNetworkConnect(NetworkMonitorAutoDetect.NetworkInformation paramNetworkInformation);

    public abstract void onNetworkDisconnect(long paramLong);
  }

  @SuppressLint({"NewApi"})
  private class SimpleNetworkCallback extends ConnectivityManager.NetworkCallback
  {
    private SimpleNetworkCallback()
    {
    }

    private void onNetworkChanged(Network paramNetwork)
    {
      paramNetwork = NetworkMonitorAutoDetect.this.connectivityManagerDelegate.networkToInfo(paramNetwork);
      if (paramNetwork != null)
        NetworkMonitorAutoDetect.this.observer.onNetworkConnect(paramNetwork);
    }

    public void onAvailable(Network paramNetwork)
    {
      Logging.d("NetworkMonitorAutoDetect", "Network becomes available: " + paramNetwork.toString());
      onNetworkChanged(paramNetwork);
    }

    public void onCapabilitiesChanged(Network paramNetwork, NetworkCapabilities paramNetworkCapabilities)
    {
      Logging.d("NetworkMonitorAutoDetect", "capabilities changed: " + paramNetworkCapabilities.toString());
      onNetworkChanged(paramNetwork);
    }

    public void onLinkPropertiesChanged(Network paramNetwork, LinkProperties paramLinkProperties)
    {
      Logging.d("NetworkMonitorAutoDetect", "link properties changed: " + paramLinkProperties.toString());
      onNetworkChanged(paramNetwork);
    }

    public void onLosing(Network paramNetwork, int paramInt)
    {
      Logging.d("NetworkMonitorAutoDetect", "Network " + paramNetwork.toString() + " is about to lose in " + paramInt + "ms");
    }

    public void onLost(Network paramNetwork)
    {
      Logging.d("NetworkMonitorAutoDetect", "Network " + paramNetwork.toString() + " is disconnected");
      NetworkMonitorAutoDetect.this.observer.onNetworkDisconnect(NetworkMonitorAutoDetect.access$000(paramNetwork));
    }
  }

  static class WifiManagerDelegate
  {
    private final Context context;

    WifiManagerDelegate()
    {
      this.context = null;
    }

    WifiManagerDelegate(Context paramContext)
    {
      this.context = paramContext;
    }

    String getWifiSSID()
    {
      Object localObject = this.context.registerReceiver(null, new IntentFilter("android.net.wifi.STATE_CHANGE"));
      if (localObject != null)
      {
        localObject = (WifiInfo)((Intent)localObject).getParcelableExtra("wifiInfo");
        if (localObject != null)
        {
          localObject = ((WifiInfo)localObject).getSSID();
          if (localObject != null)
            return localObject;
        }
      }
      return (String)"";
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.NetworkMonitorAutoDetect
 * JD-Core Version:    0.6.0
 */