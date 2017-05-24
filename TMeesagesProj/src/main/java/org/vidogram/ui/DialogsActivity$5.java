package org.vidogram.ui;

import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.InputChannel;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.Adapters.DialogsAdapter;
import org.vidogram.ui.Adapters.DialogsSearchAdapter;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;

class DialogsActivity$5
  implements RecyclerListView.OnItemClickListener
{
  public void onItemClick(View paramView, int paramInt)
  {
    if ((DialogsActivity.access$200(this.this$0) == null) || (DialogsActivity.access$200(this.this$0).getAdapter() == null));
    label564: label825: label855: label858: 
    while (true)
    {
      return;
      paramView = DialogsActivity.access$200(this.this$0).getAdapter();
      Object localObject1;
      long l1;
      if (paramView == DialogsActivity.access$1500(this.this$0))
      {
        localObject1 = DialogsActivity.access$1500(this.this$0).getItem(paramInt);
        if (localObject1 == null)
          continue;
        l1 = ((TLRPC.TL_dialog)localObject1).id;
        paramInt = 0;
      }
      while (true)
      {
        if (l1 == 0L)
          break label858;
        Object localObject2;
        if (DialogsActivity.access$700(this.this$0))
        {
          DialogsActivity.access$2200(this.this$0, l1, true, false);
          return;
          if (paramView == DialogsActivity.access$1600(this.this$0))
          {
            localObject1 = DialogsActivity.access$1600(this.this$0).getItem(paramInt);
            long l2;
            if ((localObject1 instanceof TLRPC.User))
            {
              l2 = ((TLRPC.User)localObject1).id;
              if (DialogsActivity.access$1600(this.this$0).isGlobalSearch(paramInt))
              {
                localObject2 = new ArrayList();
                ((ArrayList)localObject2).add((TLRPC.User)localObject1);
                MessagesController.getInstance().putUsers((ArrayList)localObject2, false);
                MessagesStorage.getInstance().putUsersAndChats((ArrayList)localObject2, null, false, true);
              }
              l1 = l2;
              if (DialogsActivity.access$700(this.this$0))
                break label855;
              DialogsActivity.access$1600(this.this$0).putRecentSearch(l2, (TLRPC.User)localObject1);
              paramInt = 0;
              l1 = l2;
              continue;
            }
            if ((localObject1 instanceof TLRPC.Chat))
            {
              if (DialogsActivity.access$1600(this.this$0).isGlobalSearch(paramInt))
              {
                localObject2 = new ArrayList();
                ((ArrayList)localObject2).add((TLRPC.Chat)localObject1);
                MessagesController.getInstance().putChats((ArrayList)localObject2, false);
                MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject2, false, true);
              }
              if (((TLRPC.Chat)localObject1).id > 0)
                l2 = -((TLRPC.Chat)localObject1).id;
              while (true)
              {
                l1 = l2;
                if (DialogsActivity.access$700(this.this$0))
                  break label855;
                DialogsActivity.access$1600(this.this$0).putRecentSearch(l2, (TLRPC.Chat)localObject1);
                paramInt = 0;
                l1 = l2;
                break;
                l2 = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject1).id);
              }
            }
            if ((localObject1 instanceof TLRPC.EncryptedChat))
            {
              l2 = ((TLRPC.EncryptedChat)localObject1).id << 32;
              l1 = l2;
              if (DialogsActivity.access$700(this.this$0))
                break label855;
              DialogsActivity.access$1600(this.this$0).putRecentSearch(l2, (TLRPC.EncryptedChat)localObject1);
              paramInt = 0;
              l1 = l2;
              continue;
            }
            if ((localObject1 instanceof MessageObject))
            {
              localObject1 = (MessageObject)localObject1;
              l1 = ((MessageObject)localObject1).getDialogId();
              paramInt = ((MessageObject)localObject1).getId();
              DialogsActivity.access$1600(this.this$0).addHashtagsFromMessage(DialogsActivity.access$1600(this.this$0).getLastSearchString());
              continue;
            }
            if ((localObject1 instanceof String))
              DialogsActivity.access$2100(this.this$0).openSearchField((String)localObject1);
          }
          paramInt = 0;
          l1 = 0L;
          continue;
        }
        else
        {
          localObject1 = new Bundle();
          int j = (int)l1;
          int i = (int)(l1 >> 32);
          if (j != 0)
            if (i == 1)
            {
              ((Bundle)localObject1).putInt("chat_id", j);
              if (paramInt == 0)
                break label802;
              ((Bundle)localObject1).putInt("message_id", paramInt);
            }
          while (true)
          {
            if (AndroidUtilities.isTablet())
            {
              if ((DialogsActivity.access$2500(this.this$0) == l1) && (paramView != DialogsActivity.access$1600(this.this$0)))
                break;
              if (DialogsActivity.access$1500(this.this$0) != null)
              {
                DialogsActivity.access$1500(this.this$0).setOpenedDialogId(DialogsActivity.access$2502(this.this$0, l1));
                DialogsActivity.access$2600(this.this$0, 512);
              }
            }
            if (DialogsActivity.access$300(this.this$0) == null)
              break label825;
            if (!MessagesController.checkCanOpenChat((Bundle)localObject1, this.this$0))
              break;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.this$0.presentFragment(new ChatActivity((Bundle)localObject1));
            return;
            if (j > 0)
            {
              ((Bundle)localObject1).putInt("user_id", j);
              break label564;
            }
            if (j >= 0)
              break label564;
            i = j;
            if (paramInt != 0)
            {
              localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-j));
              i = j;
              if (localObject2 != null)
              {
                i = j;
                if (((TLRPC.Chat)localObject2).migrated_to != null)
                {
                  ((Bundle)localObject1).putInt("migrated_to", j);
                  i = -((TLRPC.Chat)localObject2).migrated_to.channel_id;
                }
              }
            }
            ((Bundle)localObject1).putInt("chat_id", -i);
            break label564;
            ((Bundle)localObject1).putInt("enc_id", i);
            break label564;
            if (DialogsActivity.access$2300(this.this$0) == null)
              continue;
            DialogsActivity.access$2400(this.this$0).closeSearchField();
          }
          if (!MessagesController.checkCanOpenChat((Bundle)localObject1, this.this$0))
            break;
          this.this$0.presentFragment(new ChatActivity((Bundle)localObject1));
          return;
        }
        paramInt = 0;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.5
 * JD-Core Version:    0.6.0
 */