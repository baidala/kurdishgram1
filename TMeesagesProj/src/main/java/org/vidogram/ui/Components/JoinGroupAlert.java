package org.vidogram.ui.Components;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatInvite;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.vidogram.tgnet.TLRPC.Updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.JoinSheetUserCell;
import org.vidogram.ui.ChatActivity;

public class JoinGroupAlert extends BottomSheet
{
  private TLRPC.ChatInvite chatInvite;
  private BaseFragment fragment;
  private String hash;

  public JoinGroupAlert(Context paramContext, TLRPC.ChatInvite paramChatInvite, String paramString, BaseFragment paramBaseFragment)
  {
    super(paramContext, false);
    setApplyBottomPadding(false);
    setApplyTopPadding(false);
    this.fragment = paramBaseFragment;
    this.chatInvite = paramChatInvite;
    this.hash = paramString;
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setOrientation(1);
    localLinearLayout.setClickable(true);
    setCustomView(localLinearLayout);
    paramBaseFragment = null;
    paramString = null;
    AvatarDrawable localAvatarDrawable;
    int i;
    if (paramChatInvite.chat != null)
    {
      localAvatarDrawable = new AvatarDrawable(paramChatInvite.chat);
      if (this.chatInvite.chat.photo != null)
        paramString = this.chatInvite.chat.photo.photo_small;
      paramBaseFragment = paramChatInvite.chat.title;
      i = paramChatInvite.chat.participants_count;
      BackupImageView localBackupImageView = new BackupImageView(paramContext);
      localBackupImageView.setRoundRadius(AndroidUtilities.dp(35.0F));
      localBackupImageView.setImage(paramString, "50_50", localAvatarDrawable);
      localLinearLayout.addView(localBackupImageView, LayoutHelper.createLinear(70, 70, 49, 0, 12, 0, 0));
      paramString = new TextView(paramContext);
      paramString.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString.setTextSize(1, 17.0F);
      paramString.setTextColor(Theme.getColor("dialogTextBlack"));
      paramString.setText(paramBaseFragment);
      paramString.setSingleLine(true);
      paramString.setEllipsize(TextUtils.TruncateAt.END);
      if (i <= 0)
        break label701;
    }
    label701: for (int j = 0; ; j = 10)
    {
      localLinearLayout.addView(paramString, LayoutHelper.createLinear(-2, -2, 49, 10, 10, 10, j));
      if (i > 0)
      {
        paramString = new TextView(paramContext);
        paramString.setTextSize(1, 14.0F);
        paramString.setTextColor(Theme.getColor("dialogTextGray3"));
        paramString.setSingleLine(true);
        paramString.setEllipsize(TextUtils.TruncateAt.END);
        paramString.setText(LocaleController.formatPluralString("Members", i));
        localLinearLayout.addView(paramString, LayoutHelper.createLinear(-2, -2, 49, 10, 4, 10, 10));
      }
      if (!paramChatInvite.participants.isEmpty())
      {
        paramChatInvite = new RecyclerListView(paramContext);
        paramChatInvite.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
        paramChatInvite.setNestedScrollingEnabled(false);
        paramChatInvite.setClipToPadding(false);
        paramChatInvite.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        paramChatInvite.setHorizontalScrollBarEnabled(false);
        paramChatInvite.setVerticalScrollBarEnabled(false);
        paramChatInvite.setAdapter(new UsersAdapter(paramContext));
        paramChatInvite.setGlowColor(Theme.getColor("dialogScrollGlow"));
        localLinearLayout.addView(paramChatInvite, LayoutHelper.createLinear(-2, 90, 49, 0, 0, 0, 0));
      }
      paramChatInvite = new View(paramContext);
      paramChatInvite.setBackgroundResource(2130837729);
      localLinearLayout.addView(paramChatInvite, LayoutHelper.createLinear(-1, 3));
      paramContext = new PickerBottomLayout(paramContext, false);
      localLinearLayout.addView(paramContext, LayoutHelper.createFrame(-1, 48, 83));
      paramContext.cancelButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramContext.cancelButton.setTextColor(Theme.getColor("dialogTextBlue2"));
      paramContext.cancelButton.setText(LocaleController.getString("Cancel", 2131165427).toUpperCase());
      paramContext.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          JoinGroupAlert.this.dismiss();
        }
      });
      paramContext.doneButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramContext.doneButton.setVisibility(0);
      paramContext.doneButtonBadgeTextView.setVisibility(8);
      paramContext.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
      paramContext.doneButtonTextView.setText(LocaleController.getString("JoinGroup", 2131165859));
      paramContext.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          JoinGroupAlert.this.dismiss();
          paramView = new TLRPC.TL_messages_importChatInvite();
          paramView.hash = JoinGroupAlert.this.hash;
          ConnectionsManager.getInstance().sendRequest(paramView, new RequestDelegate(paramView)
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              if (paramTL_error == null)
              {
                TLRPC.Updates localUpdates = (TLRPC.Updates)paramTLObject;
                MessagesController.getInstance().processUpdates(localUpdates, false);
              }
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
              {
                public void run()
                {
                  if ((JoinGroupAlert.this.fragment == null) || (JoinGroupAlert.this.fragment.getParentActivity() == null));
                  while (true)
                  {
                    return;
                    if (this.val$error != null)
                      break;
                    Object localObject2 = (TLRPC.Updates)this.val$response;
                    if (((TLRPC.Updates)localObject2).chats.isEmpty())
                      continue;
                    Object localObject1 = (TLRPC.Chat)((TLRPC.Updates)localObject2).chats.get(0);
                    ((TLRPC.Chat)localObject1).left = false;
                    ((TLRPC.Chat)localObject1).kicked = false;
                    MessagesController.getInstance().putUsers(((TLRPC.Updates)localObject2).users, false);
                    MessagesController.getInstance().putChats(((TLRPC.Updates)localObject2).chats, false);
                    localObject2 = new Bundle();
                    ((Bundle)localObject2).putInt("chat_id", ((TLRPC.Chat)localObject1).id);
                    if (!MessagesController.checkCanOpenChat((Bundle)localObject2, JoinGroupAlert.this.fragment))
                      continue;
                    localObject1 = new ChatActivity((Bundle)localObject2);
                    JoinGroupAlert.this.fragment.presentFragment((BaseFragment)localObject1, JoinGroupAlert.this.fragment instanceof ChatActivity);
                    return;
                  }
                  AlertsCreator.processError(this.val$error, JoinGroupAlert.this.fragment, JoinGroupAlert.2.1.this.val$req, new Object[0]);
                }
              });
            }
          }
          , 2);
        }
      });
      return;
      localAvatarDrawable = new AvatarDrawable();
      localAvatarDrawable.setInfo(0, paramChatInvite.title, null, false);
      paramString = paramBaseFragment;
      if (this.chatInvite.photo != null)
        paramString = this.chatInvite.photo.photo_small;
      paramBaseFragment = paramChatInvite.title;
      i = paramChatInvite.participants_count;
      break;
    }
  }

  private class UsersAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context context;

    public UsersAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    public int getItemCount()
    {
      int k = JoinGroupAlert.this.chatInvite.participants.size();
      if (JoinGroupAlert.this.chatInvite.chat != null);
      for (int i = JoinGroupAlert.this.chatInvite.chat.participants_count; ; i = JoinGroupAlert.this.chatInvite.participants_count)
      {
        int j = k;
        if (k != i)
          j = k + 1;
        return j;
      }
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (JoinSheetUserCell)paramViewHolder.itemView;
      if (paramInt < JoinGroupAlert.this.chatInvite.participants.size())
      {
        paramViewHolder.setUser((TLRPC.User)JoinGroupAlert.this.chatInvite.participants.get(paramInt));
        return;
      }
      if (JoinGroupAlert.this.chatInvite.chat != null);
      for (paramInt = JoinGroupAlert.this.chatInvite.chat.participants_count; ; paramInt = JoinGroupAlert.this.chatInvite.participants_count)
      {
        paramViewHolder.setCount(paramInt - JoinGroupAlert.this.chatInvite.participants.size());
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new JoinSheetUserCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(100.0F), AndroidUtilities.dp(90.0F)));
      return new RecyclerListView.Holder(paramViewGroup);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.JoinGroupAlert
 * JD-Core Version:    0.6.0
 */