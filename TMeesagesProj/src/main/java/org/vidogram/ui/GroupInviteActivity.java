package org.vidogram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ExportedChatInvite;
import org.vidogram.tgnet.TLRPC.TL_channels_exportInvite;
import org.vidogram.tgnet.TLRPC.TL_chatInviteExported;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_messages_exportChatInvite;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.TextBlockCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class GroupInviteActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int chat_id;
  private int copyLinkRow;
  private EmptyTextProgressView emptyView;
  private TLRPC.ExportedChatInvite invite;
  private int linkInfoRow;
  private int linkRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loading;
  private int revokeLinkRow;
  private int rowCount;
  private int shadowRow;
  private int shareLinkRow;

  public GroupInviteActivity(int paramInt)
  {
    this.chat_id = paramInt;
  }

  private void generateLink(boolean paramBoolean)
  {
    this.loading = true;
    Object localObject;
    if (ChatObject.isChannel(this.chat_id))
    {
      localObject = new TLRPC.TL_channels_exportInvite();
      ((TLRPC.TL_channels_exportInvite)localObject).channel = MessagesController.getInputChannel(this.chat_id);
    }
    while (true)
    {
      int i = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(paramBoolean)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error == null)
              {
                GroupInviteActivity.access$202(GroupInviteActivity.this, (TLRPC.ExportedChatInvite)this.val$response);
                if (GroupInviteActivity.3.this.val$newRequest)
                {
                  if (GroupInviteActivity.this.getParentActivity() == null)
                    return;
                  AlertDialog.Builder localBuilder = new AlertDialog.Builder(GroupInviteActivity.this.getParentActivity());
                  localBuilder.setMessage(LocaleController.getString("RevokeAlertNewLink", 2131166362));
                  localBuilder.setTitle(LocaleController.getString("RevokeLink", 2131166364));
                  localBuilder.setNegativeButton(LocaleController.getString("OK", 2131166153), null);
                  GroupInviteActivity.this.showDialog(localBuilder.create());
                }
              }
              GroupInviteActivity.access$602(GroupInviteActivity.this, false);
              GroupInviteActivity.this.listAdapter.notifyDataSetChanged();
            }
          });
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
      return;
      localObject = new TLRPC.TL_messages_exportChatInvite();
      ((TLRPC.TL_messages_exportChatInvite)localObject).chat_id = this.chat_id;
    }
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("InviteLink", 2131165845));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          GroupInviteActivity.this.finishFragment();
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.showProgress();
    this.listView = new RecyclerListView(paramContext);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setEmptyView(this.emptyView);
    this.listView.setVerticalScrollBarEnabled(false);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if (GroupInviteActivity.this.getParentActivity() == null);
        do
          while (true)
          {
            return;
            if ((paramInt == GroupInviteActivity.this.copyLinkRow) || (paramInt == GroupInviteActivity.this.linkRow))
            {
              if (GroupInviteActivity.this.invite == null)
                continue;
              try
              {
                ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", GroupInviteActivity.this.invite.link));
                Toast.makeText(GroupInviteActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165909), 0).show();
                return;
              }
              catch (java.lang.Exception paramView)
              {
                FileLog.e(paramView);
                return;
              }
            }
            if (paramInt != GroupInviteActivity.this.shareLinkRow)
              break;
            if (GroupInviteActivity.this.invite == null)
              continue;
            try
            {
              paramView = new Intent("android.intent.action.SEND");
              paramView.setType("text/plain");
              paramView.putExtra("android.intent.extra.TEXT", GroupInviteActivity.this.invite.link);
              GroupInviteActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(paramView, LocaleController.getString("InviteToGroupByLink", 2131165848)), 500);
              return;
            }
            catch (java.lang.Exception paramView)
            {
              FileLog.e(paramView);
              return;
            }
          }
        while (paramInt != GroupInviteActivity.this.revokeLinkRow);
        paramView = new AlertDialog.Builder(GroupInviteActivity.this.getParentActivity());
        paramView.setMessage(LocaleController.getString("RevokeAlert", 2131166361));
        paramView.setTitle(LocaleController.getString("RevokeLink", 2131166364));
        paramView.setPositiveButton(LocaleController.getString("RevokeButton", 2131166363), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            GroupInviteActivity.this.generateLink(true);
          }
        });
        paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
        GroupInviteActivity.this.showDialog(paramView.create());
      }
    });
    return this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.chatInfoDidLoaded)
    {
      TLRPC.ChatFull localChatFull = (TLRPC.ChatFull)paramArrayOfObject[0];
      paramInt = ((Integer)paramArrayOfObject[1]).intValue();
      if ((localChatFull.id == this.chat_id) && (paramInt == this.classGuid))
      {
        this.invite = MessagesController.getInstance().getExportedInvite(this.chat_id);
        if ((this.invite instanceof TLRPC.TL_chatInviteExported))
          break label73;
        generateLink(false);
      }
    }
    label73: 
    do
    {
      return;
      this.loading = false;
    }
    while (this.listAdapter == null);
    this.listAdapter.notifyDataSetChanged();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, TextBlockCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { TextBlockCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
    this.loading = true;
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.linkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.linkInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.copyLinkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.revokeLinkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.shareLinkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.shadowRow = i;
    return true;
  }

  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getItemCount()
    {
      if (GroupInviteActivity.this.loading)
        return 0;
      return GroupInviteActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == GroupInviteActivity.this.copyLinkRow) || (paramInt == GroupInviteActivity.this.shareLinkRow) || (paramInt == GroupInviteActivity.this.revokeLinkRow));
      do
      {
        return 0;
        if ((paramInt == GroupInviteActivity.this.shadowRow) || (paramInt == GroupInviteActivity.this.linkInfoRow))
          return 1;
      }
      while (paramInt != GroupInviteActivity.this.linkRow);
      return 2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == GroupInviteActivity.this.revokeLinkRow) || (i == GroupInviteActivity.this.copyLinkRow) || (i == GroupInviteActivity.this.shareLinkRow) || (i == GroupInviteActivity.this.linkRow);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
        do
        {
          do
          {
            return;
            paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
            if (paramInt == GroupInviteActivity.this.copyLinkRow)
            {
              paramViewHolder.setText(LocaleController.getString("CopyLink", 2131165584), true);
              return;
            }
            if (paramInt != GroupInviteActivity.this.shareLinkRow)
              continue;
            paramViewHolder.setText(LocaleController.getString("ShareLink", 2131166452), false);
            return;
          }
          while (paramInt != GroupInviteActivity.this.revokeLinkRow);
          paramViewHolder.setText(LocaleController.getString("RevokeLink", 2131166364), true);
          return;
          paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
          if (paramInt != GroupInviteActivity.this.shadowRow)
            continue;
          paramViewHolder.setText("");
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
        }
        while (paramInt != GroupInviteActivity.this.linkInfoRow);
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
        if ((ChatObject.isChannel((TLRPC.Chat)localObject)) && (!((TLRPC.Chat)localObject).megagroup))
          paramViewHolder.setText(LocaleController.getString("ChannelLinkInfo", 2131165474));
        while (true)
        {
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
          return;
          paramViewHolder.setText(LocaleController.getString("LinkInfo", 2131165911));
        }
      case 2:
      }
      Object localObject = (TextBlockCell)paramViewHolder.itemView;
      if (GroupInviteActivity.this.invite != null);
      for (paramViewHolder = GroupInviteActivity.this.invite.link; ; paramViewHolder = "error")
      {
        ((TextBlockCell)localObject).setText(paramViewHolder, false);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextBlockCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      case 0:
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.GroupInviteActivity
 * JD-Core Version:    0.6.0
 */