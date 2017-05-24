package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class MusicPlayerReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.intent.action.MEDIA_BUTTON"))
      if (paramIntent.getExtras() != null);
    do
    {
      do
      {
        return;
        paramContext = (KeyEvent)paramIntent.getExtras().get("android.intent.extra.KEY_EVENT");
      }
      while ((paramContext == null) || (paramContext.getAction() != 0));
      switch (paramContext.getKeyCode())
      {
      case 86:
      default:
        return;
      case 79:
      case 85:
        if (MediaController.getInstance().isAudioPaused())
        {
          MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
          return;
        }
        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
        return;
      case 126:
        MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
        return;
      case 127:
        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
        return;
      case 87:
        MediaController.getInstance().playNextMessage();
        return;
      case 88:
      }
      MediaController.getInstance().playPreviousMessage();
      return;
      if (paramIntent.getAction().equals("org.telegram.android.musicplayer.play"))
      {
        MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
        return;
      }
      if ((paramIntent.getAction().equals("org.telegram.android.musicplayer.pause")) || (paramIntent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")))
      {
        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
        return;
      }
      if (paramIntent.getAction().equals("org.telegram.android.musicplayer.next"))
      {
        MediaController.getInstance().playNextMessage();
        return;
      }
      if (!paramIntent.getAction().equals("org.telegram.android.musicplayer.close"))
        continue;
      MediaController.getInstance().cleanupPlayer(true, true);
      return;
    }
    while (!paramIntent.getAction().equals("org.telegram.android.musicplayer.previous"));
    MediaController.getInstance().playPreviousMessage();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.MusicPlayerReceiver
 * JD-Core Version:    0.6.0
 */