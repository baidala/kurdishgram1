package org.vidogram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.query.SearchQuery;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.Adapters.DialogsAdapter;
import org.vidogram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
import org.vidogram.ui.Components.EmptyTextProgressView;

class DialogsActivity$13
  implements DialogsSearchAdapter.DialogsSearchAdapterDelegate
{
  public void didPressedOnSubDialog(int paramInt)
  {
    if (DialogsActivity.access$700(this.this$0))
      DialogsActivity.access$2200(this.this$0, paramInt, true, false);
    Bundle localBundle;
    label168: 
    do
    {
      return;
      localBundle = new Bundle();
      if (paramInt > 0)
        localBundle.putInt("user_id", paramInt);
      while (true)
      {
        if (DialogsActivity.access$3500(this.this$0) != null)
          DialogsActivity.access$3600(this.this$0).closeSearchField();
        if ((AndroidUtilities.isTablet()) && (DialogsActivity.access$1500(this.this$0) != null))
        {
          DialogsActivity.access$1500(this.this$0).setOpenedDialogId(DialogsActivity.access$2502(this.this$0, paramInt));
          DialogsActivity.access$2600(this.this$0, 512);
        }
        if (DialogsActivity.access$300(this.this$0) == null)
          break label168;
        if (!MessagesController.checkCanOpenChat(localBundle, this.this$0))
          break;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        this.this$0.presentFragment(new ChatActivity(localBundle));
        return;
        localBundle.putInt("chat_id", -paramInt);
      }
    }
    while (!MessagesController.checkCanOpenChat(localBundle, this.this$0));
    this.this$0.presentFragment(new ChatActivity(localBundle));
  }

  public void needRemoveHint(int paramInt)
  {
    if (this.this$0.getParentActivity() == null);
    TLRPC.User localUser;
    do
    {
      return;
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
    }
    while (localUser == null);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.this$0.getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    localBuilder.setMessage(LocaleController.formatString("ChatHintsDelete", 2131165535, new Object[] { ContactsController.formatName(localUser.first_name, localUser.last_name) }));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramInt)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        SearchQuery.removePeer(this.val$did);
      }
    });
    localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
    this.this$0.showDialog(localBuilder.create());
  }

  public void searchStateChanged(boolean paramBoolean)
  {
    if ((DialogsActivity.access$000(this.this$0)) && (DialogsActivity.access$1200(this.this$0)) && (DialogsActivity.access$400(this.this$0) != null))
    {
      if (paramBoolean)
        DialogsActivity.access$400(this.this$0).showProgress();
    }
    else
      return;
    DialogsActivity.access$400(this.this$0).showTextView();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.13
 * JD-Core Version:    0.6.0
 */