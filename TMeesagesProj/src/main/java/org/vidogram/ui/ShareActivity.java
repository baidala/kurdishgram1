package org.vidogram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.ui.Components.ShareAlert;

public class ShareActivity extends Activity
{
  private Dialog visibleDialog;

  protected void onCreate(Bundle paramBundle)
  {
    ApplicationLoader.postInitApplication();
    AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
    requestWindowFeature(1);
    setTheme(2131361951);
    super.onCreate(paramBundle);
    setContentView(new View(this), new ViewGroup.LayoutParams(-1, -1));
    paramBundle = getIntent();
    if ((paramBundle == null) || (!"android.intent.action.VIEW".equals(paramBundle.getAction())) || (paramBundle.getData() == null))
    {
      finish();
      return;
    }
    paramBundle = paramBundle.getData();
    Object localObject1 = paramBundle.getScheme();
    Object localObject2 = paramBundle.toString();
    paramBundle = paramBundle.getQueryParameter("hash");
    if ((!"tgb".equals(localObject1)) || (!((String)localObject2).toLowerCase().startsWith("tgb://share_game_score")) || (TextUtils.isEmpty(paramBundle)))
    {
      finish();
      return;
    }
    localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
    localObject2 = ((SharedPreferences)localObject1).getString(paramBundle + "_m", null);
    if (TextUtils.isEmpty((CharSequence)localObject2))
    {
      finish();
      return;
    }
    localObject2 = new SerializedData(Utilities.hexToBytes((String)localObject2));
    localObject2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((SerializedData)localObject2).readInt32(false), false);
    if (localObject2 == null)
    {
      finish();
      return;
    }
    paramBundle = ((SharedPreferences)localObject1).getString(paramBundle + "_link", null);
    localObject1 = new MessageObject((TLRPC.Message)localObject2, null, false);
    ((MessageObject)localObject1).messageOwner.with_my_score = true;
    try
    {
      localObject2 = new ArrayList();
      ((ArrayList)localObject2).add(localObject1);
      this.visibleDialog = new ShareAlert(this, (ArrayList)localObject2, null, false, paramBundle, true);
      this.visibleDialog.setCanceledOnTouchOutside(true);
      this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramDialogInterface)
        {
          if (!ShareActivity.this.isFinishing())
            ShareActivity.this.finish();
          ShareActivity.access$002(ShareActivity.this, null);
        }
      });
      this.visibleDialog.show();
      return;
    }
    catch (Exception paramBundle)
    {
      FileLog.e(paramBundle);
      finish();
    }
  }

  public void onPause()
  {
    super.onPause();
    try
    {
      if ((this.visibleDialog != null) && (this.visibleDialog.isShowing()))
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ShareActivity
 * JD-Core Version:    0.6.0
 */