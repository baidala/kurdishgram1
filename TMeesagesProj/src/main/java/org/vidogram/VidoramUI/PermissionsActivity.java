package org.vidogram.VidogramUi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PermissionsActivity extends Activity
{
  private a a;
  private boolean b;

  private void a()
  {
    String[] arrayOfString = b();
    if (c())
    {
      a(arrayOfString[1]);
      return;
    }
    a(arrayOfString);
  }

  public static void a(Activity paramActivity, boolean paramBoolean, String[] paramArrayOfString)
  {
    Intent localIntent = new Intent(paramActivity, PermissionsActivity.class);
    localIntent.putExtra("extraPermissions", paramArrayOfString);
    localIntent.putExtra("checkAudio", paramBoolean);
    android.support.v4.b.a.a(paramActivity, localIntent, null);
  }

  private void a(String paramString)
  {
    if (this.a.a(new String[] { paramString }))
    {
      b(new String[] { paramString });
      return;
    }
    d();
  }

  private void a(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt.length > 1)
    {
      int i = 0;
      while (i < paramArrayOfInt.length)
      {
        if (paramArrayOfInt[i] != 0);
        i += 1;
      }
    }
  }

  private void a(String[] paramArrayOfString)
  {
    if (this.a.a(paramArrayOfString))
    {
      b(paramArrayOfString);
      return;
    }
    d();
  }

  private void b(String[] paramArrayOfString)
  {
    android.support.v4.b.a.a(this, paramArrayOfString, 0);
  }

  private boolean b(int[] paramArrayOfInt)
  {
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfInt[i] == -1)
        return false;
      i += 1;
    }
    return true;
  }

  private String[] b()
  {
    return getIntent().getStringArrayExtra("extraPermissions");
  }

  private boolean c()
  {
    return getIntent().getBooleanExtra("checkAudio", false);
  }

  private void d()
  {
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if ((getIntent() == null) || (!getIntent().hasExtra("extraPermissions")))
      throw new RuntimeException("This Activity needs to be launched using the static startActivityForResult() method.");
    setContentView(2130903067);
    this.a = new a(this);
    this.b = true;
  }

  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramInt == 0) && (b(paramArrayOfInt)))
    {
      this.b = true;
      d();
      return;
    }
    this.b = false;
    a(paramArrayOfInt);
    finish();
  }

  protected void onResume()
  {
    super.onResume();
    if (this.b)
    {
      a();
      return;
    }
    this.b = true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.PermissionsActivity
 * JD-Core Version:    0.6.0
 */