package org.vidogram.ui;

import android.os.Bundle;
import android.view.View;
import itman.Vidofilm.a.b;
import org.vidogram.VidogramUi.a.c;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.Adapters.DialogsAdapter;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;

class DialogsActivity$17
  implements RecyclerListView.OnItemClickListener
{
  public void onItemClick(View paramView, int paramInt)
  {
    try
    {
      paramView = ((c)DialogsActivity.access$100(this.this$0).getAdapter()).a(paramInt);
      Bundle localBundle = new Bundle();
      Object localObject;
      if (paramView.f().equals(UserConfig.getCurrentUser().id + ""))
      {
        localObject = paramView.b();
        paramView = paramView.a();
      }
      while (true)
      {
        localBundle.putInt("user_id", Integer.parseInt((String)localObject));
        RecyclerView.Adapter localAdapter = DialogsActivity.access$100(this.this$0).getAdapter();
        if (!AndroidUtilities.isTablet())
          break;
        if ((DialogsActivity.access$2500(this.this$0) == Integer.parseInt((String)localObject)) && (localAdapter != DialogsActivity.access$1600(this.this$0)))
        {
          return;
          localObject = paramView.f();
          paramView = paramView.e();
          continue;
        }
        if (DialogsActivity.access$1500(this.this$0) == null)
          break;
        DialogsActivity.access$1500(this.this$0).setOpenedDialogId(DialogsActivity.access$2502(this.this$0, Integer.parseInt((String)localObject)));
        DialogsActivity.access$2600(this.this$0, 512);
      }
      if ((MessagesController.checkCanOpenChat(localBundle, this.this$0)) && (!this.this$0.presentFragment(new ProfileActivity(localBundle))) && (MessagesController.getInstance().getUser(Integer.valueOf(Integer.parseInt((String)localObject))) == null))
      {
        localObject = new Bundle();
        ((Bundle)localObject).putString("phone", paramView);
        this.this$0.presentFragment(new NewContactActivity((Bundle)localObject));
        return;
      }
    }
    catch (java.lang.Exception paramView)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.17
 * JD-Core Version:    0.6.0
 */