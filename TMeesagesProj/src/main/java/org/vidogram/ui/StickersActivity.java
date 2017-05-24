package org.vidogram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.messenger.support.widget.helper.ItemTouchHelper;
import org.vidogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_messages_reorderStickerSets;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSet;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.StickerSetCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.StickersAlert;
import org.vidogram.ui.Components.URLSpanNoUnderline;

public class StickersActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int archivedInfoRow;
  private int archivedRow;
  private int currentType;
  private int featuredInfoRow;
  private int featuredRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int masksInfoRow;
  private int masksRow;
  private boolean needReorder;
  private int rowCount;
  private int stickersEndRow;
  private int stickersShadowRow;
  private int stickersStartRow;

  public StickersActivity(int paramInt)
  {
    this.currentType = paramInt;
  }

  private void sendReorder()
  {
    if (!this.needReorder)
      return;
    StickersQuery.calcNewHash(this.currentType);
    this.needReorder = false;
    TLRPC.TL_messages_reorderStickerSets localTL_messages_reorderStickerSets = new TLRPC.TL_messages_reorderStickerSets();
    if (this.currentType == 1);
    for (boolean bool = true; ; bool = false)
    {
      localTL_messages_reorderStickerSets.masks = bool;
      ArrayList localArrayList = StickersQuery.getStickerSets(this.currentType);
      int i = 0;
      while (i < localArrayList.size())
      {
        localTL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet)localArrayList.get(i)).set.id));
        i += 1;
      }
    }
    ConnectionsManager.getInstance().sendRequest(localTL_messages_reorderStickerSets, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(this.currentType) });
  }

  private void updateRows()
  {
    this.rowCount = 0;
    int i;
    if (this.currentType == 0)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.featuredRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.featuredInfoRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.masksRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.masksInfoRow = i;
      if (StickersQuery.getArchivedStickersCount(this.currentType) == 0)
        break label230;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.archivedInfoRow = i;
      label124: ArrayList localArrayList = StickersQuery.getStickerSets(this.currentType);
      if (localArrayList.isEmpty())
        break label243;
      this.stickersStartRow = this.rowCount;
      this.stickersEndRow = (this.rowCount + localArrayList.size());
      i = this.rowCount;
      this.rowCount = (localArrayList.size() + i);
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.stickersShadowRow = i; ; this.stickersShadowRow = -1)
    {
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
      return;
      this.featuredRow = -1;
      this.featuredInfoRow = -1;
      this.masksRow = -1;
      this.masksInfoRow = -1;
      break;
      label230: this.archivedRow = -1;
      this.archivedInfoRow = -1;
      break label124;
      label243: this.stickersStartRow = -1;
      this.stickersEndRow = -1;
    }
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.currentType == 0)
      this.actionBar.setTitle(LocaleController.getString("Stickers", 2131166485));
    while (true)
    {
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            StickersActivity.this.finishFragment();
        }
      });
      this.listAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setFocusable(true);
      this.listView.setTag(Integer.valueOf(7));
      paramContext = new LinearLayoutManager(paramContext);
      paramContext.setOrientation(1);
      this.listView.setLayoutManager(paramContext);
      new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow) && (StickersActivity.this.getParentActivity() != null))
          {
            StickersActivity.this.sendReorder();
            paramView = (TLRPC.TL_messages_stickerSet)StickersQuery.getStickerSets(StickersActivity.this.currentType).get(paramInt - StickersActivity.this.stickersStartRow);
            ArrayList localArrayList = paramView.documents;
            if ((localArrayList != null) && (!localArrayList.isEmpty()));
          }
          do
          {
            return;
            StickersActivity.this.showDialog(new StickersAlert(StickersActivity.this.getParentActivity(), StickersActivity.this, null, paramView, null));
            return;
            if (paramInt == StickersActivity.this.featuredRow)
            {
              StickersActivity.this.sendReorder();
              StickersActivity.this.presentFragment(new FeaturedStickersActivity());
              return;
            }
            if (paramInt != StickersActivity.this.archivedRow)
              continue;
            StickersActivity.this.sendReorder();
            StickersActivity.this.presentFragment(new ArchivedStickersActivity(StickersActivity.this.currentType));
            return;
          }
          while (paramInt != StickersActivity.this.masksRow);
          StickersActivity.this.presentFragment(new StickersActivity(1));
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("Masks", 2131165936));
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.stickersDidLoaded)
      if (((Integer)paramArrayOfObject[0]).intValue() == this.currentType)
        updateRows();
    do
      while (true)
      {
        return;
        if (paramInt != NotificationCenter.featuredStickersDidLoaded)
          break;
        if (this.listAdapter == null)
          continue;
        this.listAdapter.notifyItemChanged(0);
        return;
      }
    while ((paramInt != NotificationCenter.archivedStickersCountDidLoaded) || (((Integer)paramArrayOfObject[0]).intValue() != this.currentType));
    updateRows();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { StickerSetCell.class, TextSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteLinkText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { StickerSetCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { StickerSetCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[] { StickerSetCell.class }, new String[] { "optionsButton" }, null, null, null, "stickers_menuSelector"), new ThemeDescription(this.listView, 0, new Class[] { StickerSetCell.class }, new String[] { "optionsButton" }, null, null, null, "stickers_menu") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    StickersQuery.checkStickers(this.currentType);
    if (this.currentType == 0)
      StickersQuery.checkFeaturedStickers();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.archivedStickersCountDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    updateRows();
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.archivedStickersCountDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    sendReorder();
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

    private void processSelectionOption(int paramInt, TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
    {
      int i = 2;
      Object localObject;
      if (paramInt == 0)
      {
        localObject = StickersActivity.this.getParentActivity();
        TLRPC.StickerSet localStickerSet = paramTL_messages_stickerSet.set;
        paramInt = i;
        if (!paramTL_messages_stickerSet.set.archived)
          paramInt = 1;
        StickersQuery.removeStickersSet((Context)localObject, localStickerSet, paramInt, StickersActivity.this, true);
      }
      do
      {
        return;
        if (paramInt == 1)
        {
          StickersQuery.removeStickersSet(StickersActivity.this.getParentActivity(), paramTL_messages_stickerSet.set, 0, StickersActivity.this, true);
          return;
        }
        if (paramInt != 2)
          continue;
        try
        {
          localObject = new Intent("android.intent.action.SEND");
          ((Intent)localObject).setType("text/plain");
          ((Intent)localObject).putExtra("android.intent.extra.TEXT", String.format(Locale.US, "https://" + MessagesController.getInstance().linkPrefix + "/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name }));
          StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject, LocaleController.getString("StickersShare", 2131166490)), 500);
          return;
        }
        catch (Exception paramTL_messages_stickerSet)
        {
          FileLog.e(paramTL_messages_stickerSet);
          return;
        }
      }
      while (paramInt != 3);
      try
      {
        ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(Locale.US, "https://" + MessagesController.getInstance().linkPrefix + "/addstickers/%s", new Object[] { paramTL_messages_stickerSet.set.short_name })));
        Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165909), 0).show();
        return;
      }
      catch (Exception paramTL_messages_stickerSet)
      {
        FileLog.e(paramTL_messages_stickerSet);
      }
    }

    public int getItemCount()
    {
      return StickersActivity.this.rowCount;
    }

    public long getItemId(int paramInt)
    {
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow))
        return ((TLRPC.TL_messages_stickerSet)StickersQuery.getStickerSets(StickersActivity.this.currentType).get(paramInt - StickersActivity.this.stickersStartRow)).set.id;
      if ((paramInt == StickersActivity.this.archivedRow) || (paramInt == StickersActivity.this.archivedInfoRow) || (paramInt == StickersActivity.this.featuredRow) || (paramInt == StickersActivity.this.featuredInfoRow) || (paramInt == StickersActivity.this.masksRow) || (paramInt == StickersActivity.this.masksInfoRow))
        return -2147483648L;
      return paramInt;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= StickersActivity.this.stickersStartRow) && (paramInt < StickersActivity.this.stickersEndRow));
      do
      {
        return 0;
        if ((paramInt == StickersActivity.this.featuredInfoRow) || (paramInt == StickersActivity.this.archivedInfoRow) || (paramInt == StickersActivity.this.masksInfoRow))
          return 1;
        if ((paramInt == StickersActivity.this.featuredRow) || (paramInt == StickersActivity.this.archivedRow) || (paramInt == StickersActivity.this.masksRow))
          return 2;
      }
      while (paramInt != StickersActivity.this.stickersShadowRow);
      return 3;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      return (i == 0) || (i == 2);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = true;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      case 2:
      }
      do
      {
        Object localObject1;
        do
        {
          return;
          localObject1 = StickersQuery.getStickerSets(StickersActivity.this.currentType);
          paramInt -= StickersActivity.this.stickersStartRow;
          paramViewHolder = (StickerSetCell)paramViewHolder.itemView;
          Object localObject2 = (TLRPC.TL_messages_stickerSet)((ArrayList)localObject1).get(paramInt);
          if (paramInt != ((ArrayList)localObject1).size() - 1);
          while (true)
          {
            paramViewHolder.setStickersSet((TLRPC.TL_messages_stickerSet)localObject2, bool);
            return;
            bool = false;
          }
          if (paramInt == StickersActivity.this.featuredInfoRow)
          {
            localObject1 = LocaleController.getString("FeaturedStickersInfo", 2131165708);
            paramInt = ((String)localObject1).indexOf("@stickers");
            if (paramInt != -1)
              try
              {
                localObject2 = new SpannableStringBuilder((CharSequence)localObject1);
                ((SpannableStringBuilder)localObject2).setSpan(new URLSpanNoUnderline("@stickers")
                {
                  public void onClick(View paramView)
                  {
                    MessagesController.openByUserName("stickers", StickersActivity.this, 1);
                  }
                }
                , paramInt, "@stickers".length() + paramInt, 18);
                ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject2);
                return;
              }
              catch (Exception localException)
              {
                FileLog.e(localException);
                ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject1);
                return;
              }
            ((TextInfoPrivacyCell)paramViewHolder.itemView).setText((CharSequence)localObject1);
            return;
          }
          if (paramInt != StickersActivity.this.archivedInfoRow)
            continue;
          if (StickersActivity.this.currentType == 0)
          {
            ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedStickersInfo", 2131165333));
            return;
          }
          ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedMasksInfo", 2131165328));
          return;
        }
        while (paramInt != StickersActivity.this.masksInfoRow);
        ((TextInfoPrivacyCell)paramViewHolder.itemView).setText(LocaleController.getString("MasksInfo", 2131165937));
        return;
        if (paramInt == StickersActivity.this.featuredRow)
        {
          paramInt = StickersQuery.getUnreadStickerSets().size();
          localObject1 = (TextSettingsCell)paramViewHolder.itemView;
          String str = LocaleController.getString("FeaturedStickers", 2131165707);
          if (paramInt != 0);
          for (paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(paramInt) }); ; paramViewHolder = "")
          {
            ((TextSettingsCell)localObject1).setTextAndValue(str, paramViewHolder, false);
            return;
          }
        }
        if (paramInt != StickersActivity.this.archivedRow)
          continue;
        if (StickersActivity.this.currentType == 0)
        {
          ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", 2131165329), false);
          return;
        }
        ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", 2131165324), false);
        return;
      }
      while (paramInt != StickersActivity.this.masksRow);
      ((TextSettingsCell)paramViewHolder.itemView).setText(LocaleController.getString("Masks", 2131165936), true);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new StickerSetCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((StickerSetCell)paramViewGroup).setOnOptionsClick(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            StickersActivity.this.sendReorder();
            TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = ((StickerSetCell)paramView.getParent()).getStickersSet();
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(StickersActivity.this.getParentActivity());
            localBuilder.setTitle(localTL_messages_stickerSet.set.title);
            CharSequence[] arrayOfCharSequence;
            if (StickersActivity.this.currentType == 0)
              if (localTL_messages_stickerSet.set.official)
              {
                paramView = new int[1];
                paramView[0] = 0;
                arrayOfCharSequence = new CharSequence[1];
                arrayOfCharSequence[0] = LocaleController.getString("StickersHide", 2131166487);
              }
            while (true)
            {
              localBuilder.setItems(arrayOfCharSequence, new DialogInterface.OnClickListener(paramView, localTL_messages_stickerSet)
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  StickersActivity.ListAdapter.this.processSelectionOption(this.val$options[paramInt], this.val$stickerSet);
                }
              });
              StickersActivity.this.showDialog(localBuilder.create());
              return;
              paramView = new int[4];
              View tmp138_137 = paramView;
              tmp138_137[0] = 0;
              View tmp142_138 = tmp138_137;
              tmp142_138[1] = 1;
              View tmp146_142 = tmp142_138;
              tmp146_142[2] = 2;
              View tmp150_146 = tmp146_142;
              tmp150_146[3] = 3;
              tmp150_146;
              arrayOfCharSequence = new CharSequence[4];
              arrayOfCharSequence[0] = LocaleController.getString("StickersHide", 2131166487);
              arrayOfCharSequence[1] = LocaleController.getString("StickersRemove", 2131166488);
              arrayOfCharSequence[2] = LocaleController.getString("StickersShare", 2131166490);
              arrayOfCharSequence[3] = LocaleController.getString("StickersCopy", 2131166486);
              continue;
              if (localTL_messages_stickerSet.set.official)
              {
                paramView = new int[1];
                paramView[0] = 0;
                arrayOfCharSequence = new CharSequence[1];
                arrayOfCharSequence[0] = LocaleController.getString("StickersRemove", 2131166487);
                continue;
              }
              paramView = new int[4];
              View tmp244_243 = paramView;
              tmp244_243[0] = 0;
              View tmp248_244 = tmp244_243;
              tmp248_244[1] = 1;
              View tmp252_248 = tmp248_244;
              tmp252_248[2] = 2;
              View tmp256_252 = tmp252_248;
              tmp256_252[3] = 3;
              tmp256_252;
              arrayOfCharSequence = new CharSequence[4];
              arrayOfCharSequence[0] = LocaleController.getString("StickersHide", 2131166487);
              arrayOfCharSequence[1] = LocaleController.getString("StickersRemove", 2131166488);
              arrayOfCharSequence[2] = LocaleController.getString("StickersShare", 2131166490);
              arrayOfCharSequence[3] = LocaleController.getString("StickersCopy", 2131166486);
            }
          }
        });
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
      }
    }

    public void swapElements(int paramInt1, int paramInt2)
    {
      if (paramInt1 != paramInt2)
        StickersActivity.access$1502(StickersActivity.this, true);
      ArrayList localArrayList = StickersQuery.getStickerSets(StickersActivity.this.currentType);
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList.get(paramInt1 - StickersActivity.this.stickersStartRow);
      localArrayList.set(paramInt1 - StickersActivity.this.stickersStartRow, localArrayList.get(paramInt2 - StickersActivity.this.stickersStartRow));
      localArrayList.set(paramInt2 - StickersActivity.this.stickersStartRow, localTL_messages_stickerSet);
      notifyItemMoved(paramInt1, paramInt2);
    }
  }

  public class TouchHelperCallback extends ItemTouchHelper.Callback
  {
    public TouchHelperCallback()
    {
    }

    public void clearView(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      super.clearView(paramRecyclerView, paramViewHolder);
      paramViewHolder.itemView.setPressed(false);
    }

    public int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() != 0)
        return makeMovementFlags(0, 0);
      return makeMovementFlags(3, 0);
    }

    public boolean isLongPressDragEnabled()
    {
      return true;
    }

    public void onChildDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      super.onChildDraw(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }

    public boolean onMove(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      if (paramViewHolder1.getItemViewType() != paramViewHolder2.getItemViewType())
        return false;
      StickersActivity.this.listAdapter.swapElements(paramViewHolder1.getAdapterPosition(), paramViewHolder2.getAdapterPosition());
      return true;
    }

    public void onSelectedChanged(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramInt != 0)
      {
        StickersActivity.this.listView.cancelClickRunnables(false);
        paramViewHolder.itemView.setPressed(true);
      }
      super.onSelectedChanged(paramViewHolder, paramInt);
    }

    public void onSwiped(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.StickersActivity
 * JD-Core Version:    0.6.0
 */