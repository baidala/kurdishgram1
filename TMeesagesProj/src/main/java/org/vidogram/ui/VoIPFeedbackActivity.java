package org.vidogram.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_phone_setCallRating;
import org.vidogram.tgnet.TLRPC.TL_updates;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.BetterRatingView;
import org.vidogram.ui.Components.BetterRatingView.OnRatingChangeListener;
import org.vidogram.ui.Components.LayoutHelper;

public class VoIPFeedbackActivity extends Activity
{
  public void finish()
  {
    super.finish();
    overridePendingTransition(0, 0);
  }

  protected void onCreate(Bundle paramBundle)
  {
    getWindow().addFlags(524288);
    super.onCreate(paramBundle);
    overridePendingTransition(0, 0);
    setContentView(new View(this));
    Object localObject = new LinearLayout(this);
    ((LinearLayout)localObject).setOrientation(1);
    int i = AndroidUtilities.dp(16.0F);
    ((LinearLayout)localObject).setPadding(i, i, i, i);
    paramBundle = new TextView(this);
    paramBundle.setTextSize(2, 16.0F);
    paramBundle.setTextColor(Theme.getColor("dialogTextBlack"));
    paramBundle.setGravity(17);
    paramBundle.setText(LocaleController.getString("VoipRateCallAlert", 2131166602));
    ((LinearLayout)localObject).addView(paramBundle);
    paramBundle = new BetterRatingView(this);
    ((LinearLayout)localObject).addView(paramBundle, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
    EditText localEditText = new EditText(this);
    localEditText.setHint(LocaleController.getString("VoipFeedbackCommentHint", 2131166586));
    localEditText.setInputType(147457);
    localEditText.setVisibility(8);
    localEditText.setTextColor(Theme.getColor("dialogTextBlack"));
    localEditText.setHintTextColor(Theme.getColor("dialogTextHint"));
    localEditText.setBackgroundDrawable(Theme.createEditTextDrawable(this, true));
    ((LinearLayout)localObject).addView(localEditText, LayoutHelper.createLinear(-1, -2, 1, 0, 16, 0, 0));
    localObject = new AlertDialog.Builder(this).setTitle(LocaleController.getString("AppName", 2131165319)).setView((View)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramBundle, localEditText)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface = new TLRPC.TL_phone_setCallRating();
        paramDialogInterface.rating = this.val$bar.getRating();
        if (paramDialogInterface.rating < 5);
        for (paramDialogInterface.comment = this.val$commentBox.getText().toString(); ; paramDialogInterface.comment = "")
        {
          paramDialogInterface.peer = new TLRPC.TL_inputPhoneCall();
          paramDialogInterface.peer.access_hash = VoIPFeedbackActivity.this.getIntent().getLongExtra("call_access_hash", 0L);
          paramDialogInterface.peer.id = VoIPFeedbackActivity.this.getIntent().getLongExtra("call_id", 0L);
          ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              if ((paramTLObject instanceof TLRPC.TL_updates))
              {
                paramTLObject = (TLRPC.TL_updates)paramTLObject;
                MessagesController.getInstance().processUpdates(paramTLObject, false);
              }
            }
          });
          VoIPFeedbackActivity.this.finish();
          return;
        }
      }
    }).setNegativeButton(LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        VoIPFeedbackActivity.this.finish();
      }
    }).show();
    ((AlertDialog)localObject).setCanceledOnTouchOutside(true);
    ((AlertDialog)localObject).setOnCancelListener(new DialogInterface.OnCancelListener()
    {
      public void onCancel(DialogInterface paramDialogInterface)
      {
        VoIPFeedbackActivity.this.finish();
      }
    });
    localObject = ((AlertDialog)localObject).getButton(-1);
    ((View)localObject).setEnabled(false);
    paramBundle.setOnRatingChangeListener(new BetterRatingView.OnRatingChangeListener((View)localObject, localEditText)
    {
      public void onRatingChanged(int paramInt)
      {
        Object localObject = this.val$btn;
        boolean bool;
        if (paramInt > 0)
        {
          bool = true;
          ((View)localObject).setEnabled(bool);
          localObject = this.val$commentBox;
          if ((paramInt >= 5) || (paramInt <= 0))
            break label79;
        }
        label79: for (paramInt = 0; ; paramInt = 8)
        {
          ((EditText)localObject).setVisibility(paramInt);
          if (this.val$commentBox.getVisibility() == 8)
            ((InputMethodManager)VoIPFeedbackActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(this.val$commentBox.getWindowToken(), 0);
          return;
          bool = false;
          break;
        }
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.VoIPFeedbackActivity
 * JD-Core Version:    0.6.0
 */