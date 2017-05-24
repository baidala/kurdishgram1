package org.vidogram.messenger;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class AuthenticatorService extends Service
{
  private static Authenticator authenticator = null;

  protected Authenticator getAuthenticator()
  {
    if (authenticator == null)
      authenticator = new Authenticator(this);
    return authenticator;
  }

  public IBinder onBind(Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.accounts.AccountAuthenticator"))
      return getAuthenticator().getIBinder();
    return null;
  }

  private static class Authenticator extends AbstractAccountAuthenticator
  {
    private final Context context;

    public Authenticator(Context paramContext)
    {
      super();
      this.context = paramContext;
    }

    public Bundle addAccount(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
    {
      return null;
    }

    public Bundle confirmCredentials(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
    {
      return null;
    }

    public Bundle editProperties(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString)
    {
      return null;
    }

    public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount)
    {
      return super.getAccountRemovalAllowed(paramAccountAuthenticatorResponse, paramAccount);
    }

    public Bundle getAuthToken(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
    {
      return null;
    }

    public String getAuthTokenLabel(String paramString)
    {
      return null;
    }

    public Bundle hasFeatures(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String[] paramArrayOfString)
    {
      return null;
    }

    public Bundle updateCredentials(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
    {
      return null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.AuthenticatorService
 * JD-Core Version:    0.6.0
 */