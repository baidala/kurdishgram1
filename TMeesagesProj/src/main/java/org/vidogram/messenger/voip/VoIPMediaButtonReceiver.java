package org.vidogram.messenger.voip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class VoIPMediaButtonReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ((!"android.intent.action.MEDIA_BUTTON".equals(paramIntent.getAction())) || (VoIPService.getSharedInstance() == null))
      return;
    paramContext = (KeyEvent)paramIntent.getParcelableExtra("android.intent.extra.KEY_EVENT");
    VoIPService.getSharedInstance().onMediaButtonEvent(paramContext);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.voip.VoIPMediaButtonReceiver
 * JD-Core Version:    0.6.0
 */