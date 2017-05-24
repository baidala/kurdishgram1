package org.vidogram.messenger.support.customtabs;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;

public abstract interface ICustomTabsService extends IInterface
{
  public abstract Bundle extraCommand(String paramString, Bundle paramBundle);

  public abstract boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList);

  public abstract boolean newSession(ICustomTabsCallback paramICustomTabsCallback);

  public abstract boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle);

  public abstract boolean warmup(long paramLong);

  public static abstract class Stub extends Binder
    implements ICustomTabsService
  {
    private static final String DESCRIPTOR = "android.support.customtabs.ICustomTabsService";
    static final int TRANSACTION_extraCommand = 5;
    static final int TRANSACTION_mayLaunchUrl = 4;
    static final int TRANSACTION_newSession = 3;
    static final int TRANSACTION_updateVisuals = 6;
    static final int TRANSACTION_warmup = 2;

    public Stub()
    {
      attachInterface(this, "android.support.customtabs.ICustomTabsService");
    }

    public static ICustomTabsService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null)
        return null;
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.support.customtabs.ICustomTabsService");
      if ((localIInterface != null) && ((localIInterface instanceof ICustomTabsService)))
        return (ICustomTabsService)localIInterface;
      return new Proxy(paramIBinder);
    }

    public IBinder asBinder()
    {
      return this;
    }

    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    {
      int j = 0;
      int k = 0;
      int i = 0;
      boolean bool;
      Object localObject;
      switch (paramInt1)
      {
      default:
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 2:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        bool = warmup(paramParcel1.readLong());
        paramParcel2.writeNoException();
        if (bool);
        for (paramInt1 = 1; ; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        bool = newSession(ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool)
          paramInt1 = 1;
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        ICustomTabsCallback localICustomTabsCallback = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0)
            break label260;
        }
        for (Bundle localBundle = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle = null)
        {
          bool = mayLaunchUrl(localICustomTabsCallback, (Uri)localObject, localBundle, paramParcel1.createTypedArrayList(Bundle.CREATOR));
          paramParcel2.writeNoException();
          paramInt1 = j;
          if (bool)
            paramInt1 = 1;
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
        }
      case 5:
        paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0);
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; paramParcel1 = null)
        {
          paramParcel1 = extraCommand((String)localObject, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null)
            break;
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 6:
        label260: paramParcel1.enforceInterface("android.support.customtabs.ICustomTabsService");
        localObject = ICustomTabsCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0);
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; paramParcel1 = null)
        {
          bool = updateVisuals((ICustomTabsCallback)localObject, paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = k;
          if (bool)
            paramInt1 = 1;
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 1598968902:
      }
      paramParcel2.writeString("android.support.customtabs.ICustomTabsService");
      return true;
    }

    private static class Proxy
      implements ICustomTabsService
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

      public Bundle extraCommand(String paramString, Bundle paramBundle)
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        while (true)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            localParcel1.writeString(paramString);
            if (paramBundle == null)
              continue;
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
            this.mRemote.transact(5, localParcel1, localParcel2, 0);
            localParcel2.readException();
            if (localParcel2.readInt() != 0)
            {
              paramString = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
              return paramString;
              localParcel1.writeInt(0);
              continue;
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          paramString = null;
        }
      }

      public String getInterfaceDescriptor()
      {
        return "android.support.customtabs.ICustomTabsService";
      }

      public boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
      {
        int j = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        while (true)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback == null)
              continue;
            paramICustomTabsCallback = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(paramICustomTabsCallback);
            if (paramUri == null)
              continue;
            localParcel1.writeInt(1);
            paramUri.writeToParcel(localParcel1, 0);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              localParcel1.writeTypedList(paramList);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i == 0)
                break label160;
              return j;
              paramICustomTabsCallback = null;
              continue;
              localParcel1.writeInt(0);
              continue;
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          localParcel1.writeInt(0);
          continue;
          label160: j = 0;
        }
      }

      public boolean newSession(ICustomTabsCallback paramICustomTabsCallback)
      {
        int j = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
          if (paramICustomTabsCallback != null);
          for (paramICustomTabsCallback = paramICustomTabsCallback.asBinder(); ; paramICustomTabsCallback = null)
          {
            localParcel1.writeStrongBinder(paramICustomTabsCallback);
            this.mRemote.transact(3, localParcel1, localParcel2, 0);
            localParcel2.readException();
            int i = localParcel2.readInt();
            if (i != 0)
              j = 1;
            return j;
          }
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
        throw paramICustomTabsCallback;
      }

      public boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
      {
        int j = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        while (true)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
            if (paramICustomTabsCallback == null)
              continue;
            paramICustomTabsCallback = paramICustomTabsCallback.asBinder();
            localParcel1.writeStrongBinder(paramICustomTabsCallback);
            if (paramBundle == null)
              continue;
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
            this.mRemote.transact(6, localParcel1, localParcel2, 0);
            localParcel2.readException();
            int i = localParcel2.readInt();
            if (i != 0)
            {
              return j;
              paramICustomTabsCallback = null;
              continue;
              localParcel1.writeInt(0);
              continue;
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          j = 0;
        }
      }

      public boolean warmup(long paramLong)
      {
        int j = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0)
            j = 1;
          return j;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
        throw localObject;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.ICustomTabsService
 * JD-Core Version:    0.6.0
 */