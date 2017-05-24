package org.vidogram.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.f;
import android.support.v4.view.ab;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.KeyboardButton;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.vidogram.tgnet.TLRPC.TL_message;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_peerUser;
import org.vidogram.tgnet.TLRPC.TL_photo;
import org.vidogram.tgnet.TLRPC.TL_photoSize;
import org.vidogram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.vidogram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.ActionBar.BackDrawable;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.MenuDrawable;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.Theme.ThemeInfo;
import org.vidogram.ui.Cells.ChatActionCell;
import org.vidogram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.vidogram.ui.Cells.ChatMessageCell;
import org.vidogram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.vidogram.ui.Cells.DialogCell;
import org.vidogram.ui.Cells.DialogCell.CustomDialog;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.SizeNotifierFrameLayout;

public class ThemePreviewActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ActionBar actionBar2;
  private boolean applied;
  private Theme.ThemeInfo applyingTheme;
  private DialogsAdapter dialogsAdapter;
  private View dotsContainer;
  private ImageView floatingButton;
  private RecyclerListView listView;
  private RecyclerListView listView2;
  private MessagesAdapter messagesAdapter;
  private FrameLayout page1;
  private SizeNotifierFrameLayout page2;
  private File themeFile;

  public ThemePreviewActivity(File paramFile, Theme.ThemeInfo paramThemeInfo)
  {
    this.swipeBackEnabled = false;
    this.applyingTheme = paramThemeInfo;
    this.themeFile = paramFile;
  }

  public View createView(Context paramContext)
  {
    this.page1 = new FrameLayout(paramContext);
    this.actionBar.createMenu().addItem(0, 2130837741).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public boolean canCollapseSearch()
      {
        return true;
      }

      public void onSearchCollapse()
      {
      }

      public void onSearchExpand()
      {
      }

      public void onTextChanged(EditText paramEditText)
      {
      }
    }).getSearchField().setHint(LocaleController.getString("Search", 2131166381));
    this.actionBar.setBackButtonDrawable(new MenuDrawable());
    this.actionBar.setAddToContainer(false);
    this.actionBar.setTitle(LocaleController.getString("ThemePreview", 2131166511));
    this.page1 = new FrameLayout(paramContext)
    {
      protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
      {
        boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
        if ((paramView == ThemePreviewActivity.this.actionBar) && (ThemePreviewActivity.this.parentLayout != null))
        {
          paramView = ThemePreviewActivity.this.parentLayout;
          if (ThemePreviewActivity.this.actionBar.getVisibility() != 0)
            break label73;
        }
        label73: for (int i = ThemePreviewActivity.this.actionBar.getMeasuredHeight(); ; i = 0)
        {
          paramView.drawHeaderShadow(paramCanvas, i);
          return bool;
        }
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        int j = View.MeasureSpec.getSize(paramInt1);
        int i = View.MeasureSpec.getSize(paramInt2);
        setMeasuredDimension(j, i);
        measureChildWithMargins(ThemePreviewActivity.this.actionBar, paramInt1, 0, paramInt2, 0);
        int k = ThemePreviewActivity.this.actionBar.getMeasuredHeight();
        if (ThemePreviewActivity.this.actionBar.getVisibility() == 0)
          i -= k;
        while (true)
        {
          ((FrameLayout.LayoutParams)ThemePreviewActivity.this.listView.getLayoutParams()).topMargin = k;
          ThemePreviewActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
          measureChildWithMargins(ThemePreviewActivity.this.floatingButton, paramInt1, 0, paramInt2, 0);
          return;
        }
      }
    };
    this.page1.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(true);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    Object localObject1 = this.listView;
    int i;
    Object localObject2;
    if (LocaleController.isRTL)
    {
      i = 1;
      ((RecyclerListView)localObject1).setVerticalScrollbarPosition(i);
      this.page1.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      this.floatingButton = new ImageView(paramContext);
      this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
      localObject1 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
      if (Build.VERSION.SDK_INT >= 21)
        break label1343;
      localObject2 = paramContext.getResources().getDrawable(2130837717).mutate();
      ((Drawable)localObject2).setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
      localObject1 = new CombinedDrawable((Drawable)localObject2, (Drawable)localObject1, 0, 0);
      ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
    }
    label538: label549: label558: label568: label1337: label1343: 
    while (true)
    {
      this.floatingButton.setBackgroundDrawable((Drawable)localObject1);
      this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
      this.floatingButton.setImageResource(2130837716);
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject1 = new StateListAnimator();
        localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
        localObject2 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
        this.floatingButton.setStateListAnimator((StateListAnimator)localObject1);
        this.floatingButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramView, Outline paramOutline)
          {
            paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      localObject1 = this.page1;
      localObject2 = this.floatingButton;
      float f1;
      int j;
      float f2;
      float f3;
      if (Build.VERSION.SDK_INT >= 21)
      {
        i = 56;
        if (Build.VERSION.SDK_INT < 21)
          break label1311;
        f1 = 56.0F;
        if (!LocaleController.isRTL)
          break label1318;
        j = 3;
        if (!LocaleController.isRTL)
          break label1324;
        f2 = 14.0F;
        if (!LocaleController.isRTL)
          break label1329;
        f3 = 0.0F;
        label577: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(i, f1, j | 0x50, f2, 0.0F, f3, 14.0F));
        this.dialogsAdapter = new DialogsAdapter(paramContext);
        this.listView.setAdapter(this.dialogsAdapter);
        this.page2 = new SizeNotifierFrameLayout(paramContext)
        {
          protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
          {
            boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
            if ((paramView == ThemePreviewActivity.this.actionBar2) && (ThemePreviewActivity.this.parentLayout != null))
            {
              paramView = ThemePreviewActivity.this.parentLayout;
              if (ThemePreviewActivity.this.actionBar2.getVisibility() != 0)
                break label73;
            }
            label73: for (int i = ThemePreviewActivity.this.actionBar2.getMeasuredHeight(); ; i = 0)
            {
              paramView.drawHeaderShadow(paramCanvas, i);
              return bool;
            }
          }

          protected void onMeasure(int paramInt1, int paramInt2)
          {
            int j = View.MeasureSpec.getSize(paramInt1);
            int i = View.MeasureSpec.getSize(paramInt2);
            setMeasuredDimension(j, i);
            measureChildWithMargins(ThemePreviewActivity.this.actionBar2, paramInt1, 0, paramInt2, 0);
            paramInt2 = ThemePreviewActivity.this.actionBar2.getMeasuredHeight();
            if (ThemePreviewActivity.this.actionBar2.getVisibility() == 0);
            for (paramInt1 = i - paramInt2; ; paramInt1 = i)
            {
              ((FrameLayout.LayoutParams)ThemePreviewActivity.this.listView2.getLayoutParams()).topMargin = paramInt2;
              ThemePreviewActivity.this.listView2.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824));
              return;
            }
          }
        };
        this.page2.setBackgroundImage(Theme.getCachedWallpaper());
        this.actionBar2 = createActionBar(paramContext);
        this.actionBar2.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar2.setTitle("Reinhardt");
        this.actionBar2.setSubtitle(LocaleController.formatDateOnline(System.currentTimeMillis() / 1000L - 3600L));
        this.page2.addView(this.actionBar2, LayoutHelper.createFrame(-1, -2.0F));
        this.listView2 = new RecyclerListView(paramContext);
        this.listView2.setVerticalScrollBarEnabled(true);
        this.listView2.setItemAnimator(null);
        this.listView2.setLayoutAnimation(null);
        this.listView2.setPadding(0, AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F));
        this.listView2.setClipToPadding(false);
        this.listView2.setLayoutManager(new LinearLayoutManager(paramContext, 1, true));
        localObject1 = this.listView2;
        if (!LocaleController.isRTL)
          break label1337;
      }
      for (i = 1; ; i = 2)
      {
        ((RecyclerListView)localObject1).setVerticalScrollbarPosition(i);
        this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1, 51));
        this.messagesAdapter = new MessagesAdapter(paramContext);
        this.listView2.setAdapter(this.messagesAdapter);
        this.fragmentView = new FrameLayout(paramContext);
        localObject2 = (FrameLayout)this.fragmentView;
        ViewPager localViewPager = new ViewPager(paramContext);
        localViewPager.addOnPageChangeListener(new ViewPager.f()
        {
          public void onPageScrollStateChanged(int paramInt)
          {
          }

          public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
          {
          }

          public void onPageSelected(int paramInt)
          {
            ThemePreviewActivity.this.dotsContainer.invalidate();
          }
        });
        localViewPager.setAdapter(new ab()
        {
          public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
          {
            paramViewGroup.removeView((View)paramObject);
          }

          public int getCount()
          {
            return 2;
          }

          public int getItemPosition(Object paramObject)
          {
            return -1;
          }

          public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
          {
            if (paramInt == 0);
            for (Object localObject = ThemePreviewActivity.this.page1; ; localObject = ThemePreviewActivity.this.page2)
            {
              paramViewGroup.addView((View)localObject);
              return localObject;
            }
          }

          public boolean isViewFromObject(View paramView, Object paramObject)
          {
            return paramObject == paramView;
          }

          public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
          {
            if (paramDataSetObserver != null)
              super.unregisterDataSetObserver(paramDataSetObserver);
          }
        });
        AndroidUtilities.setViewPagerEdgeEffectColor(localViewPager, Theme.getColor("actionBarDefault"));
        ((FrameLayout)localObject2).addView(localViewPager, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
        localObject1 = new View(paramContext);
        ((View)localObject1).setBackgroundResource(2130837729);
        ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
        localObject1 = new FrameLayout(paramContext);
        ((FrameLayout)localObject1).setBackgroundColor(-1);
        ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-1, 48, 83));
        this.dotsContainer = new View(paramContext, localViewPager)
        {
          private Paint paint = new Paint(1);

          protected void onDraw(Canvas paramCanvas)
          {
            int k = this.val$viewPager.getCurrentItem();
            int i = 0;
            if (i < 2)
            {
              Paint localPaint = this.paint;
              if (i == k);
              for (int j = -6710887; ; j = -3355444)
              {
                localPaint.setColor(j);
                paramCanvas.drawCircle(AndroidUtilities.dp(i * 15 + 3), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(3.0F), this.paint);
                i += 1;
                break;
              }
            }
          }
        };
        ((FrameLayout)localObject1).addView(this.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
        localObject2 = new TextView(paramContext);
        ((TextView)localObject2).setTextSize(1, 14.0F);
        ((TextView)localObject2).setTextColor(-15095832);
        ((TextView)localObject2).setGravity(17);
        ((TextView)localObject2).setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
        ((TextView)localObject2).setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
        ((TextView)localObject2).setText(LocaleController.getString("Cancel", 2131165427).toUpperCase());
        ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -1, 51));
        ((TextView)localObject2).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            Theme.applyPreviousTheme();
            ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false);
            ThemePreviewActivity.this.finishFragment();
          }
        });
        paramContext = new TextView(paramContext);
        paramContext.setTextSize(1, 14.0F);
        paramContext.setTextColor(-15095832);
        paramContext.setGravity(17);
        paramContext.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
        paramContext.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
        paramContext.setText(LocaleController.getString("ApplyTheme", 2131165322).toUpperCase());
        paramContext.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        ((FrameLayout)localObject1).addView(paramContext, LayoutHelper.createFrame(-2, -1, 53));
        paramContext.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            ThemePreviewActivity.access$1802(ThemePreviewActivity.this, true);
            ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false);
            Theme.applyThemeFile(ThemePreviewActivity.this.themeFile, ThemePreviewActivity.this.applyingTheme.name, false);
            ThemePreviewActivity.this.finishFragment();
          }
        });
        return this.fragmentView;
        i = 2;
        break;
        i = 60;
        break label538;
        f1 = 60.0F;
        break label549;
        j = 5;
        break label558;
        f2 = 0.0F;
        break label568;
        f3 = 14.0F;
        break label577;
      }
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if ((paramInt != NotificationCenter.emojiDidLoaded) || (this.listView == null));
    while (true)
    {
      return;
      int i = this.listView.getChildCount();
      paramInt = 0;
      while (paramInt < i)
      {
        paramArrayOfObject = this.listView.getChildAt(paramInt);
        if ((paramArrayOfObject instanceof DialogCell))
          ((DialogCell)paramArrayOfObject).update(0);
        paramInt += 1;
      }
    }
  }

  public boolean onBackPressed()
  {
    Theme.applyPreviousTheme();
    this.parentLayout.rebuildAllFragmentViews(false);
    return super.onBackPressed();
  }

  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    super.onFragmentDestroy();
  }

  public void onResume()
  {
    super.onResume();
    if (this.dialogsAdapter != null)
      this.dialogsAdapter.notifyDataSetChanged();
    if (this.messagesAdapter != null)
      this.messagesAdapter.notifyDataSetChanged();
  }

  public class DialogsAdapter extends RecyclerListView.SelectionAdapter
  {
    private ArrayList<DialogCell.CustomDialog> dialogs;
    private Context mContext;

    public DialogsAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
      this.dialogs = new ArrayList();
      int i = (int)(System.currentTimeMillis() / 1000L);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Eva Summer";
      ThemePreviewActivity.this.message = "Reminds me of a Chinese prove...";
      ThemePreviewActivity.this.id = 0;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = true;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = i;
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = true;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Alexandra Smith";
      ThemePreviewActivity.this.message = "Reminds me of a Chinese prove...";
      ThemePreviewActivity.this.id = 1;
      ThemePreviewActivity.this.unread_count = 2;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 3600);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Make Apple";
      ThemePreviewActivity.this.message = "🤷‍♂️ Sticker";
      ThemePreviewActivity.this.id = 2;
      ThemePreviewActivity.this.unread_count = 3;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = true;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 7200);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = true;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Paul Newman";
      ThemePreviewActivity.this.message = "Any ideas?";
      ThemePreviewActivity.this.id = 3;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 2;
      ThemePreviewActivity.this.date = (i - 10800);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Old Pirates";
      ThemePreviewActivity.this.message = "Yo-ho-ho!";
      ThemePreviewActivity.this.id = 4;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 1;
      ThemePreviewActivity.this.date = (i - 14400);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = true;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Kate Bright";
      ThemePreviewActivity.this.message = "Hola!";
      ThemePreviewActivity.this.id = 5;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 18000);
      ThemePreviewActivity.this.verified = false;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Nick K";
      ThemePreviewActivity.this.message = "These are not the droids you are looking for";
      ThemePreviewActivity.this.id = 6;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 21600);
      ThemePreviewActivity.this.verified = true;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
      this$1 = new DialogCell.CustomDialog();
      ThemePreviewActivity.this.name = "Adler Toberg";
      ThemePreviewActivity.this.message = "Did someone say peanut butter?";
      ThemePreviewActivity.this.id = 0;
      ThemePreviewActivity.this.unread_count = 0;
      ThemePreviewActivity.this.pinned = false;
      ThemePreviewActivity.this.muted = false;
      ThemePreviewActivity.this.type = 0;
      ThemePreviewActivity.this.date = (i - 25200);
      ThemePreviewActivity.this.verified = true;
      ThemePreviewActivity.this.isMedia = false;
      ThemePreviewActivity.this.sent = false;
      this.dialogs.add(ThemePreviewActivity.this);
    }

    public int getItemCount()
    {
      return this.dialogs.size() + 1;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == this.dialogs.size())
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getItemViewType() != 1;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramViewHolder.getItemViewType() == 0)
      {
        paramViewHolder = (DialogCell)paramViewHolder.itemView;
        if (paramInt == getItemCount() - 1)
          break label48;
      }
      label48: for (boolean bool = true; ; bool = false)
      {
        paramViewHolder.useSeparator = bool;
        paramViewHolder.setDialog((DialogCell.CustomDialog)this.dialogs.get(paramInt));
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      if (paramInt == 0)
        paramViewGroup = new DialogCell(this.mContext);
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        if (paramInt != 1)
          continue;
        paramViewGroup = new LoadingCell(this.mContext);
      }
    }
  }

  public class MessagesAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    private ArrayList<MessageObject> messages;

    public MessagesAdapter(Context arg2)
    {
      this.mContext = ((Context)localObject);
      this.messages = new ArrayList();
      int i = (int)(System.currentTimeMillis() / 1000L) - 3600;
      this$1 = new TLRPC.TL_message();
      ThemePreviewActivity.this.message = "Reinhardt, we need to find you some new tunes 🎶.";
      ThemePreviewActivity.this.date = (i + 60);
      ThemePreviewActivity.this.dialog_id = 1L;
      ThemePreviewActivity.this.flags = 259;
      ThemePreviewActivity.this.from_id = UserConfig.getClientUserId();
      ThemePreviewActivity.this.id = 1;
      ThemePreviewActivity.this.media = new TLRPC.TL_messageMediaEmpty();
      ThemePreviewActivity.this.out = true;
      ThemePreviewActivity.this.to_id = new TLRPC.TL_peerUser();
      ThemePreviewActivity.this.to_id.user_id = 0;
      this$1 = new MessageObject(ThemePreviewActivity.this, null, true);
      Object localObject = new TLRPC.TL_message();
      ((TLRPC.Message)localObject).message = "I can't even take you seriously right now.";
      ((TLRPC.Message)localObject).date = (i + 960);
      ((TLRPC.Message)localObject).dialog_id = 1L;
      ((TLRPC.Message)localObject).flags = 259;
      ((TLRPC.Message)localObject).from_id = UserConfig.getClientUserId();
      ((TLRPC.Message)localObject).id = 1;
      ((TLRPC.Message)localObject).media = new TLRPC.TL_messageMediaEmpty();
      ((TLRPC.Message)localObject).out = true;
      ((TLRPC.Message)localObject).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject).to_id.user_id = 0;
      this.messages.add(new MessageObject((TLRPC.Message)localObject, null, true));
      localObject = new TLRPC.TL_message();
      ((TLRPC.Message)localObject).date = (i + 130);
      ((TLRPC.Message)localObject).dialog_id = 1L;
      ((TLRPC.Message)localObject).flags = 259;
      ((TLRPC.Message)localObject).from_id = 0;
      ((TLRPC.Message)localObject).id = 5;
      ((TLRPC.Message)localObject).media = new TLRPC.TL_messageMediaDocument();
      ((TLRPC.Message)localObject).media.document = new TLRPC.TL_document();
      ((TLRPC.Message)localObject).media.document.mime_type = "audio/mp4";
      ((TLRPC.Message)localObject).media.document.thumb = new TLRPC.TL_photoSizeEmpty();
      ((TLRPC.Message)localObject).media.document.thumb.type = "s";
      TLRPC.TL_documentAttributeAudio localTL_documentAttributeAudio = new TLRPC.TL_documentAttributeAudio();
      localTL_documentAttributeAudio.duration = 243;
      localTL_documentAttributeAudio.performer = "David Hasselhoff";
      localTL_documentAttributeAudio.title = "True Survivor";
      ((TLRPC.Message)localObject).media.document.attributes.add(localTL_documentAttributeAudio);
      ((TLRPC.Message)localObject).out = false;
      ((TLRPC.Message)localObject).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject).to_id.user_id = UserConfig.getClientUserId();
      this.messages.add(new MessageObject((TLRPC.Message)localObject, null, true));
      localObject = new TLRPC.TL_message();
      ((TLRPC.Message)localObject).message = "Ah, you kids today with techno music! You should enjoy the classics, like Hasselhoff!";
      ((TLRPC.Message)localObject).date = (i + 60);
      ((TLRPC.Message)localObject).dialog_id = 1L;
      ((TLRPC.Message)localObject).flags = 265;
      ((TLRPC.Message)localObject).from_id = 0;
      ((TLRPC.Message)localObject).id = 1;
      ((TLRPC.Message)localObject).reply_to_msg_id = 5;
      ((TLRPC.Message)localObject).media = new TLRPC.TL_messageMediaEmpty();
      ((TLRPC.Message)localObject).out = false;
      ((TLRPC.Message)localObject).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject).to_id.user_id = UserConfig.getClientUserId();
      localObject = new MessageObject((TLRPC.Message)localObject, null, true);
      ((MessageObject)localObject).customReplyName = "Lucio";
      ((MessageObject)localObject).replyMessageObject = ThemePreviewActivity.this;
      this.messages.add(localObject);
      localObject = new TLRPC.TL_message();
      ((TLRPC.Message)localObject).date = (i + 120);
      ((TLRPC.Message)localObject).dialog_id = 1L;
      ((TLRPC.Message)localObject).flags = 259;
      ((TLRPC.Message)localObject).from_id = UserConfig.getClientUserId();
      ((TLRPC.Message)localObject).id = 1;
      ((TLRPC.Message)localObject).media = new TLRPC.TL_messageMediaDocument();
      ((TLRPC.Message)localObject).media.document = new TLRPC.TL_document();
      ((TLRPC.Message)localObject).media.document.mime_type = "audio/ogg";
      ((TLRPC.Message)localObject).media.document.thumb = new TLRPC.TL_photoSizeEmpty();
      ((TLRPC.Message)localObject).media.document.thumb.type = "s";
      localTL_documentAttributeAudio = new TLRPC.TL_documentAttributeAudio();
      localTL_documentAttributeAudio.flags = 1028;
      localTL_documentAttributeAudio.duration = 3;
      localTL_documentAttributeAudio.voice = true;
      localTL_documentAttributeAudio.waveform = new byte[] { 0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1 };
      ((TLRPC.Message)localObject).media.document.attributes.add(localTL_documentAttributeAudio);
      ((TLRPC.Message)localObject).out = true;
      ((TLRPC.Message)localObject).to_id = new TLRPC.TL_peerUser();
      ((TLRPC.Message)localObject).to_id.user_id = 0;
      localObject = new MessageObject((TLRPC.Message)localObject, null, true);
      ((MessageObject)localObject).audioProgressSec = 1;
      ((MessageObject)localObject).audioProgress = 0.3F;
      ((MessageObject)localObject).useCustomPhoto = true;
      this.messages.add(localObject);
      this.messages.add(ThemePreviewActivity.this);
      this$1 = new TLRPC.TL_message();
      ThemePreviewActivity.this.date = (i + 10);
      ThemePreviewActivity.this.dialog_id = 1L;
      ThemePreviewActivity.this.flags = 257;
      ThemePreviewActivity.this.from_id = 0;
      ThemePreviewActivity.this.id = 1;
      ThemePreviewActivity.this.media = new TLRPC.TL_messageMediaPhoto();
      ThemePreviewActivity.this.media.photo = new TLRPC.TL_photo();
      ThemePreviewActivity.this.media.photo.has_stickers = false;
      ThemePreviewActivity.this.media.photo.id = 1L;
      ThemePreviewActivity.this.media.photo.access_hash = 0L;
      ThemePreviewActivity.this.media.photo.date = i;
      localObject = new TLRPC.TL_photoSize();
      ((TLRPC.TL_photoSize)localObject).size = 0;
      ((TLRPC.TL_photoSize)localObject).w = 500;
      ((TLRPC.TL_photoSize)localObject).h = 302;
      ((TLRPC.TL_photoSize)localObject).type = "s";
      ((TLRPC.TL_photoSize)localObject).location = new TLRPC.TL_fileLocationUnavailable();
      ThemePreviewActivity.this.media.photo.sizes.add(localObject);
      ThemePreviewActivity.this.media.caption = "Bring it on! I LIVE for this!";
      ThemePreviewActivity.this.out = false;
      ThemePreviewActivity.this.to_id = new TLRPC.TL_peerUser();
      ThemePreviewActivity.this.to_id.user_id = UserConfig.getClientUserId();
      this$1 = new MessageObject(ThemePreviewActivity.this, null, true);
      ThemePreviewActivity.this.useCustomPhoto = true;
      this.messages.add(ThemePreviewActivity.this);
      this$1 = new TLRPC.Message();
      ThemePreviewActivity.this.message = LocaleController.formatDateChat(i);
      ThemePreviewActivity.this.id = 0;
      ThemePreviewActivity.this.date = i;
      this$1 = new MessageObject(ThemePreviewActivity.this, null, false);
      ThemePreviewActivity.this.type = 10;
      ThemePreviewActivity.this.contentType = 1;
      ThemePreviewActivity.this.isDateObject = true;
      this.messages.add(ThemePreviewActivity.this);
    }

    public int getItemCount()
    {
      return this.messages.size();
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.messages.size()))
        return ((MessageObject)this.messages.get(paramInt)).contentType;
      return 4;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool3 = false;
      MessageObject localMessageObject1 = (MessageObject)this.messages.get(paramInt);
      Object localObject = paramViewHolder.itemView;
      boolean bool1;
      if ((localObject instanceof ChatMessageCell))
      {
        localObject = (ChatMessageCell)localObject;
        ((ChatMessageCell)localObject).isChat = false;
        int i = getItemViewType(paramInt - 1);
        int j = getItemViewType(paramInt + 1);
        if ((!(localMessageObject1.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) && (i == paramViewHolder.getItemViewType()))
        {
          MessageObject localMessageObject2 = (MessageObject)this.messages.get(paramInt - 1);
          if ((localMessageObject2.isOutOwner() == localMessageObject1.isOutOwner()) && (Math.abs(localMessageObject2.messageOwner.date - localMessageObject1.messageOwner.date) <= 300))
          {
            bool1 = true;
            boolean bool2 = bool3;
            if (j == paramViewHolder.getItemViewType())
            {
              paramViewHolder = (MessageObject)this.messages.get(paramInt + 1);
              bool2 = bool3;
              if (!(paramViewHolder.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup))
              {
                bool2 = bool3;
                if (paramViewHolder.isOutOwner() == localMessageObject1.isOutOwner())
                {
                  bool2 = bool3;
                  if (Math.abs(paramViewHolder.messageOwner.date - localMessageObject1.messageOwner.date) <= 300)
                    bool2 = true;
                }
              }
            }
            ((ChatMessageCell)localObject).setFullyDraw(true);
            ((ChatMessageCell)localObject).setMessageObject(localMessageObject1, bool1, bool2);
          }
        }
      }
      do
      {
        return;
        bool1 = false;
        break;
        bool1 = false;
        break;
      }
      while (!(localObject instanceof ChatActionCell));
      paramViewHolder = (ChatActionCell)localObject;
      paramViewHolder.setMessageObject(localMessageObject1);
      paramViewHolder.setAlpha(1.0F);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      if (paramInt == 0)
      {
        paramViewGroup = new ChatMessageCell(this.mContext);
        ((ChatMessageCell)paramViewGroup).setDelegate(new ChatMessageCell.ChatMessageCellDelegate()
        {
          public boolean canPerformActions()
          {
            return false;
          }

          public void didLongPressed(ChatMessageCell paramChatMessageCell)
          {
          }

          public void didPressedBotButton(ChatMessageCell paramChatMessageCell, TLRPC.KeyboardButton paramKeyboardButton)
          {
          }

          public void didPressedCancelSendButton(ChatMessageCell paramChatMessageCell)
          {
          }

          public void didPressedChannelAvatar(ChatMessageCell paramChatMessageCell, TLRPC.Chat paramChat, int paramInt)
          {
          }

          public void didPressedImage(ChatMessageCell paramChatMessageCell)
          {
          }

          public void didPressedInstantButton(ChatMessageCell paramChatMessageCell)
          {
          }

          public void didPressedOther(ChatMessageCell paramChatMessageCell)
          {
          }

          public void didPressedReplyMessage(ChatMessageCell paramChatMessageCell, int paramInt)
          {
          }

          public void didPressedShare(ChatMessageCell paramChatMessageCell)
          {
          }

          public void didPressedUrl(MessageObject paramMessageObject, CharacterStyle paramCharacterStyle, boolean paramBoolean)
          {
          }

          public void didPressedUserAvatar(ChatMessageCell paramChatMessageCell, TLRPC.User paramUser)
          {
          }

          public void didPressedViaBot(ChatMessageCell paramChatMessageCell, String paramString)
          {
          }

          public void needOpenWebView(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
          {
          }

          public boolean needPlayAudio(MessageObject paramMessageObject)
          {
            return false;
          }
        });
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        if (paramInt != 1)
          continue;
        paramViewGroup = new ChatActionCell(this.mContext);
        ((ChatActionCell)paramViewGroup).setDelegate(new ChatActionCell.ChatActionCellDelegate()
        {
          public void didClickedImage(ChatActionCell paramChatActionCell)
          {
          }

          public void didLongPressed(ChatActionCell paramChatActionCell)
          {
          }

          public void didPressedBotButton(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
          {
          }

          public void didPressedReplyMessage(ChatActionCell paramChatActionCell, int paramInt)
          {
          }

          public void needOpenUserProfile(int paramInt)
          {
          }
        });
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ThemePreviewActivity
 * JD-Core Version:    0.6.0
 */