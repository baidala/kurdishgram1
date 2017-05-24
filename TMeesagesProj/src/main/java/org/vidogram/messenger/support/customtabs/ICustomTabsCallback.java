package org.vidogram.messenger.support.customtabs;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public abstract interface ICustomTabsCallback extends IInterface
{
  public abstract void extraCallback(String paramString, Bundle paramBundle);

  public abstract void onNavigationEvent(int paramInt, Bundle paramBundle);

  public static abstract class Stub extends Binder
    implements ICustomTabsCallback
  {
    private static final String DESCRIPTOR = "android.support.customtabs.ICustomTabsCallback";
    static final int TRANSACTION_extraCallback = 3;
    static final int TRANSACTION_onNavigationEvent = 2;

    public Stub()
    {
      attachInterface(this, "android.support.customtabs.ICustomTabsCallback");
    }

    public static ICustomTabsCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null)
        return null;
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.support.customtabs.ICustomTabsCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ICustomTabsCallback)))
        return (ICustomTabsCallback)localIInterface;
      return new Proxy(paramIBinder);
    }

    public IBinder asBinder()
    {
      return this;
    }

    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    {
      Object localObject = null;
      String str = null;
      switch (paramInt1)
      {
      default:
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 2:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsCallback");
        paramInt1 = paramParcel1.readInt();
        paramParcel2 = str;
        if (paramParcel1.readInt() != 0)
          paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        onNavigationEvent(paramInt1, paramParcel2);
        return true;
      case 3:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsCallback");
        str = paramParcel1.readString();
        paramParcel2 = localObject;
        if (paramParcel1.readInt() != 0)
          paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        extraCallback(str, paramParcel2);
        return true;
      case 1598968902:
      }
      paramParcel2.writeString("android.support.customtabs.ICustomTabsCallback");
      return true;
    }

    private static class Proxy
      implements ICustomTabsCallback
    {
      private IBinder mRemote;

      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }

      public IBinder asBinder()
      {
        return this.mRemote;
      }

      public void extraCallback(String paramString, Bundle paramBundle)
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
          localParcel.writeString(paramString);
          if (paramBundle != null)
          {
            localParcel.writeInt(1);
            paramBundle.writeToParcel(localParcel, 0);
          }
          while (true)
          {
            this.mRemote.transact(3, localParcel, null, 1);
            return;
            localParcel.writeInt(0);
          }
        }
        finally
        {
          localParcel.recycle();
        }
        throw paramString;
      }

      public String getInterfaceDescriptor()
      {
        return "android.support.customtabs.ICustomTabsCallback";
      }

      public void onNavigationEvent(int paramInt, Bundle paramBundle)
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.support.customtabs.ICustomTabsCallback");
          localParcel.writeInt(paramInt);
          if (paramBundle != null)
          {
            localParcel.writeInt(1);
            paramBundle.writeToParcel(localParcel, 0);
          }
          while (true)
          {
            this.mRemote.transact(2, localParcel, null, 1);
            return;
            localParcel.writeInt(0);
          }
        }
        finally
        {
          localParcel.recycle();
        }
        throw paramBundle;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.ICustomTabsCallback
 * JD-Core Version:    0.6.0
 */