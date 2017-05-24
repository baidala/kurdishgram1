package org.vidogram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.DialogObject;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.Adapters.DialogsSearchAdapter;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;

class DialogsActivity$6
  implements RecyclerListView.OnItemLongClickListener
{
  public boolean onItemClick(View paramView, int paramInt)
  {
    if ((DialogsActivity.access$700(this.this$0)) || ((DialogsActivity.access$000(this.this$0)) && (DialogsActivity.access$1200(this.this$0))) || (this.this$0.getParentActivity() == null))
    {
      if (((DialogsActivity.access$1200(this.this$0)) && (DialogsActivity.access$000(this.this$0))) || ((DialogsActivity.access$1600(this.this$0).isRecentSearchDisplayed()) && (DialogsActivity.access$200(this.this$0).getAdapter() == DialogsActivity.access$1600(this.this$0)) && (((DialogsActivity.access$1600(this.this$0).getItem(paramInt) instanceof String)) || (DialogsActivity.access$1600(this.this$0).isRecentSearchDisplayed()))))
      {
        paramView = new AlertDialog.Builder(this.this$0.getParentActivity());
        paramView.setTitle(LocaleController.getString("AppName", 2131165319));
        paramView.setMessage(LocaleController.getString("ClearSearch", 2131165555));
        paramView.setPositiveButton(LocaleController.getString("ClearButton", 2131165549).toUpperCase(), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            if (DialogsActivity.access$1600(DialogsActivity.6.this.this$0).isRecentSearchDisplayed())
            {
              DialogsActivity.access$1600(DialogsActivity.6.this.this$0).clearRecentSearch();
              return;
            }
            DialogsActivity.access$1600(DialogsActivity.6.this.this$0).clearRecentHashtags();
          }
        });
        paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
        this.this$0.showDialog(paramView.create());
        return true;
      }
      return false;
    }
    paramView = DialogsActivity.access$2700(this.this$0);
    if ((paramInt < 0) || (paramInt >= paramView.size()))
      return false;
    Object localObject3 = (TLRPC.TL_dialog)paramView.get(paramInt);
    DialogsActivity.access$2802(this.this$0, ((TLRPC.TL_dialog)localObject3).id);
    boolean bool4 = ((TLRPC.TL_dialog)localObject3).pinned;
    BottomSheet.Builder localBuilder = new BottomSheet.Builder(this.this$0.getParentActivity());
    paramInt = (int)DialogsActivity.access$2800(this.this$0);
    int i = (int)(DialogsActivity.access$2800(this.this$0) >> 32);
    Object localObject2;
    label382: Object localObject1;
    if (DialogObject.isChannel((TLRPC.TL_dialog)localObject3))
    {
      localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
      if (((TLRPC.TL_dialog)localObject3).pinned)
      {
        paramInt = 2130837668;
        if ((localObject2 == null) || (!((TLRPC.Chat)localObject2).megagroup))
          break label521;
        if ((!((TLRPC.TL_dialog)localObject3).pinned) && (!MessagesController.getInstance().canPinDialog(false)))
          break label504;
        if (!((TLRPC.TL_dialog)localObject3).pinned)
          break label493;
        paramView = LocaleController.getString("UnpinFromTop", 2131166537);
        localObject3 = LocaleController.getString("ClearHistoryCache", 2131165551);
        if ((localObject2 != null) && (((TLRPC.Chat)localObject2).creator))
          break label509;
      }
      label493: label504: label509: for (localObject1 = LocaleController.getString("LeaveMegaMenu", 2131165904); ; localObject1 = LocaleController.getString("DeleteMegaMenu", 2131165645))
      {
        paramView = new CharSequence[] { paramView, localObject3, localObject1 };
        localObject1 = new DialogInterface.OnClickListener(bool4, (TLRPC.Chat)localObject2)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            boolean bool = true;
            if (paramInt == 0)
            {
              paramDialogInterface = MessagesController.getInstance();
              long l = DialogsActivity.access$2800(DialogsActivity.6.this.this$0);
              if (!this.val$pinned);
              while (true)
              {
                if ((paramDialogInterface.pinDialog(l, bool, null, 0L)) && (!this.val$pinned))
                  DialogsActivity.access$200(DialogsActivity.6.this.this$0).smoothScrollToPosition(0);
                return;
                bool = false;
              }
            }
            paramDialogInterface = new AlertDialog.Builder(DialogsActivity.6.this.this$0.getParentActivity());
            paramDialogInterface.setTitle(LocaleController.getString("AppName", 2131165319));
            if (paramInt == 1)
            {
              if ((this.val$chat != null) && (this.val$chat.megagroup))
                paramDialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", 2131165339));
              while (true)
              {
                paramDialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    MessagesController.getInstance().deleteDialog(DialogsActivity.access$2800(DialogsActivity.6.this.this$0), 2);
                  }
                });
                paramDialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                DialogsActivity.6.this.this$0.showDialog(paramDialogInterface.create());
                return;
                paramDialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", 2131165338));
              }
            }
            if ((this.val$chat != null) && (this.val$chat.megagroup))
              if (!this.val$chat.creator)
                paramDialogInterface.setMessage(LocaleController.getString("MegaLeaveAlert", 2131165943));
            while (true)
            {
              paramDialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  MessagesController.getInstance().deleteUserFromChat((int)(-DialogsActivity.access$2800(DialogsActivity.6.this.this$0)), UserConfig.getCurrentUser(), null);
                  if (AndroidUtilities.isTablet())
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(DialogsActivity.access$2800(DialogsActivity.6.this.this$0)) });
                }
              });
              break;
              paramDialogInterface.setMessage(LocaleController.getString("MegaDeleteAlert", 2131165941));
              continue;
              if ((this.val$chat == null) || (!this.val$chat.creator))
              {
                paramDialogInterface.setMessage(LocaleController.getString("ChannelLeaveAlert", 2131165473));
                continue;
              }
              paramDialogInterface.setMessage(LocaleController.getString("ChannelDeleteAlert", 2131165463));
            }
          }
        };
        localBuilder.setItems(paramView, new int[] { paramInt, 2130837664, 2130837666 }, (DialogInterface.OnClickListener)localObject1);
        this.this$0.showDialog(localBuilder.create());
        return true;
        paramInt = 2130837667;
        break;
        paramView = LocaleController.getString("PinToTop", 2131166285);
        break label382;
        paramView = null;
        break label382;
      }
      label521: if ((((TLRPC.TL_dialog)localObject3).pinned) || (MessagesController.getInstance().canPinDialog(false)))
        if (((TLRPC.TL_dialog)localObject3).pinned)
        {
          paramView = LocaleController.getString("UnpinFromTop", 2131166537);
          label555: localObject3 = LocaleController.getString("ClearHistoryCache", 2131165551);
          if ((localObject2 != null) && (((TLRPC.Chat)localObject2).creator))
            break label624;
        }
      label624: for (localObject1 = LocaleController.getString("LeaveChannelMenu", 2131165902); ; localObject1 = LocaleController.getString("ChannelDeleteMenu", 2131165465))
      {
        paramView = new CharSequence[] { paramView, localObject3, localObject1 };
        break;
        paramView = LocaleController.getString("PinToTop", 2131166285);
        break label555;
        paramView = null;
        break label555;
      }
    }
    boolean bool1;
    label648: boolean bool2;
    label699: boolean bool3;
    if ((paramInt < 0) && (i != 1))
    {
      bool1 = true;
      localObject1 = null;
      paramView = (View)localObject1;
      if (!bool1)
      {
        paramView = (View)localObject1;
        if (paramInt > 0)
        {
          paramView = (View)localObject1;
          if (i != 1)
            paramView = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
        }
      }
      if ((paramView == null) || (!paramView.bot))
        break label865;
      bool2 = true;
      if (!((TLRPC.TL_dialog)localObject3).pinned)
      {
        paramView = MessagesController.getInstance();
        if (paramInt != 0)
          break label871;
        bool3 = true;
        label718: if (!paramView.canPinDialog(bool3))
          break label888;
      }
      if (!((TLRPC.TL_dialog)localObject3).pinned)
        break label877;
      paramView = LocaleController.getString("UnpinFromTop", 2131166537);
      label743: localObject2 = LocaleController.getString("ClearHistory", 2131165550);
      if (!bool1)
        break label893;
      localObject1 = LocaleController.getString("DeleteChat", 2131165637);
      label766: if (!((TLRPC.TL_dialog)localObject3).pinned)
        break label923;
      paramInt = 2130837668;
      label777: if (!bool1)
        break label929;
    }
    label923: label929: for (i = 2130837666; ; i = 2130837665)
    {
      localObject3 = new DialogInterface.OnClickListener(bool4, bool1, bool2)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          boolean bool = true;
          if (paramInt == 0)
          {
            paramDialogInterface = MessagesController.getInstance();
            long l = DialogsActivity.access$2800(DialogsActivity.6.this.this$0);
            if (!this.val$pinned);
            while (true)
            {
              if ((paramDialogInterface.pinDialog(l, bool, null, 0L)) && (!this.val$pinned))
                DialogsActivity.access$200(DialogsActivity.6.this.this$0).smoothScrollToPosition(0);
              return;
              bool = false;
            }
          }
          paramDialogInterface = new AlertDialog.Builder(DialogsActivity.6.this.this$0.getParentActivity());
          paramDialogInterface.setTitle(LocaleController.getString("AppName", 2131165319));
          if (paramInt == 1)
            paramDialogInterface.setMessage(LocaleController.getString("AreYouSureClearHistory", 2131165337));
          while (true)
          {
            paramDialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramInt)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                if (this.val$which != 1)
                {
                  if (DialogsActivity.6.3.this.val$isChat)
                  {
                    paramDialogInterface = MessagesController.getInstance().getChat(Integer.valueOf((int)(-DialogsActivity.access$2800(DialogsActivity.6.this.this$0))));
                    if ((paramDialogInterface != null) && (ChatObject.isNotInChat(paramDialogInterface)))
                      MessagesController.getInstance().deleteDialog(DialogsActivity.access$2800(DialogsActivity.6.this.this$0), 0);
                  }
                  while (true)
                  {
                    if (DialogsActivity.6.3.this.val$isBot)
                      MessagesController.getInstance().blockUser((int)DialogsActivity.access$2800(DialogsActivity.6.this.this$0));
                    if (AndroidUtilities.isTablet())
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(DialogsActivity.access$2800(DialogsActivity.6.this.this$0)) });
                    return;
                    MessagesController.getInstance().deleteUserFromChat((int)(-DialogsActivity.access$2800(DialogsActivity.6.this.this$0)), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                    continue;
                    MessagesController.getInstance().deleteDialog(DialogsActivity.access$2800(DialogsActivity.6.this.this$0), 0);
                  }
                }
                MessagesController.getInstance().deleteDialog(DialogsActivity.access$2800(DialogsActivity.6.this.this$0), 1);
              }
            });
            paramDialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            DialogsActivity.6.this.this$0.showDialog(paramDialogInterface.create());
            return;
            if (this.val$isChat)
            {
              paramDialogInterface.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", 2131165340));
              continue;
            }
            paramDialogInterface.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", 2131165344));
          }
        }
      };
      localBuilder.setItems(new CharSequence[] { paramView, localObject2, localObject1 }, new int[] { paramInt, 2130837664, i }, (DialogInterface.OnClickListener)localObject3);
      this.this$0.showDialog(localBuilder.create());
      break;
      bool1 = false;
      break label648;
      label865: bool2 = false;
      break label699;
      label871: bool3 = false;
      break label718;
      label877: paramView = LocaleController.getString("PinToTop", 2131166285);
      break label743;
      label888: paramView = null;
      break label743;
      label893: if (bool2)
      {
        localObject1 = LocaleController.getString("DeleteAndStop", 2131165635);
        break label766;
      }
      localObject1 = LocaleController.getString("Delete", 2131165628);
      break label766;
      paramInt = 2130837667;
      break label777;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.6
 * JD-Core Version:    0.6.0
 */