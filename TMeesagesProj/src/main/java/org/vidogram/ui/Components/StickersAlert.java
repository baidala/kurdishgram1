package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.support.widget.GridLayoutManager;
import org.vidogram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.State;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputPhoto;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetID;
import org.vidogram.tgnet.TLRPC.TL_inputStickeredMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import org.vidogram.tgnet.TLRPC.Vector;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.EmptyCell;
import org.vidogram.ui.Cells.FeaturedStickerSetInfoCell;
import org.vidogram.ui.Cells.StickerEmojiCell;
import org.vidogram.ui.StickerPreviewViewer;

public class StickersAlert extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate
{
  private GridAdapter adapter;
  private StickersAlertDelegate delegate;
  private FrameLayout emptyView;
  private RecyclerListView gridView;
  private boolean ignoreLayout;
  private TLRPC.InputStickerSet inputStickerSet;
  private StickersAlertInstallDelegate installDelegate;
  private int itemSize;
  private GridLayoutManager layoutManager;
  private Activity parentActivity;
  private BaseFragment parentFragment;
  private PickerBottomLayout pickerBottomLayout;
  private TextView previewSendButton;
  private View previewSendButtonShadow;
  private int reqId;
  private int scrollOffsetY;
  private TLRPC.Document selectedSticker;
  private View[] shadow = new View[2];
  private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
  private Drawable shadowDrawable;
  private boolean showEmoji;
  private TextView stickerEmojiTextView;
  private BackupImageView stickerImageView;
  private FrameLayout stickerPreviewLayout;
  private TLRPC.TL_messages_stickerSet stickerSet;
  private ArrayList<TLRPC.StickerSetCovered> stickerSetCovereds;
  private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
  private TextView titleTextView;

  public StickersAlert(Context paramContext, TLRPC.Photo paramPhoto)
  {
    super(paramContext, false);
    this.parentActivity = ((Activity)paramContext);
    TLRPC.TL_messages_getAttachedStickers localTL_messages_getAttachedStickers = new TLRPC.TL_messages_getAttachedStickers();
    TLRPC.TL_inputStickeredMediaPhoto localTL_inputStickeredMediaPhoto = new TLRPC.TL_inputStickeredMediaPhoto();
    localTL_inputStickeredMediaPhoto.id = new TLRPC.TL_inputPhoto();
    localTL_inputStickeredMediaPhoto.id.id = paramPhoto.id;
    localTL_inputStickeredMediaPhoto.id.access_hash = paramPhoto.access_hash;
    localTL_messages_getAttachedStickers.media = localTL_inputStickeredMediaPhoto;
    this.reqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_getAttachedStickers, new RequestDelegate(localTL_messages_getAttachedStickers)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            StickersAlert.access$002(StickersAlert.this, 0);
            if (this.val$error == null)
            {
              Object localObject = (TLRPC.Vector)this.val$response;
              if (((TLRPC.Vector)localObject).objects.isEmpty())
              {
                StickersAlert.this.dismiss();
                return;
              }
              if (((TLRPC.Vector)localObject).objects.size() == 1)
              {
                localObject = (TLRPC.StickerSetCovered)((TLRPC.Vector)localObject).objects.get(0);
                StickersAlert.access$102(StickersAlert.this, new TLRPC.TL_inputStickerSetID());
                StickersAlert.this.inputStickerSet.id = ((TLRPC.StickerSetCovered)localObject).set.id;
                StickersAlert.this.inputStickerSet.access_hash = ((TLRPC.StickerSetCovered)localObject).set.access_hash;
                StickersAlert.this.loadStickerSet();
                return;
              }
              StickersAlert.access$302(StickersAlert.this, new ArrayList());
              int i = 0;
              while (i < ((TLRPC.Vector)localObject).objects.size())
              {
                StickersAlert.this.stickerSetCovereds.add((TLRPC.StickerSetCovered)((TLRPC.Vector)localObject).objects.get(i));
                i += 1;
              }
              StickersAlert.this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
              StickersAlert.this.titleTextView.setVisibility(8);
              StickersAlert.this.shadow[0].setVisibility(8);
              StickersAlert.this.adapter.notifyDataSetChanged();
              return;
            }
            AlertsCreator.processError(this.val$error, StickersAlert.this.parentFragment, StickersAlert.1.this.val$req, new Object[0]);
            StickersAlert.this.dismiss();
          }
        });
      }
    });
    init(paramContext);
  }

  public StickersAlert(Context paramContext, BaseFragment paramBaseFragment, TLRPC.InputStickerSet paramInputStickerSet, TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet, StickersAlertDelegate paramStickersAlertDelegate)
  {
    super(paramContext, false);
    this.delegate = paramStickersAlertDelegate;
    this.inputStickerSet = paramInputStickerSet;
    this.stickerSet = paramTL_messages_stickerSet;
    this.parentFragment = paramBaseFragment;
    loadStickerSet();
    init(paramContext);
  }

  private void hidePreview()
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.stickerPreviewLayout, "alpha", new float[] { 0.0F }) });
    localAnimatorSet.setDuration(200L);
    localAnimatorSet.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        StickersAlert.this.stickerPreviewLayout.setVisibility(8);
      }
    });
    localAnimatorSet.start();
  }

  private void init(Context paramContext)
  {
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838062).mutate();
    this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
    this.containerView = new FrameLayout(paramContext)
    {
      private int lastNotifyWidth;

      protected void onDraw(Canvas paramCanvas)
      {
        StickersAlert.this.shadowDrawable.setBounds(0, StickersAlert.this.scrollOffsetY - StickersAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        StickersAlert.this.shadowDrawable.draw(paramCanvas);
      }

      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        if ((paramMotionEvent.getAction() == 0) && (StickersAlert.this.scrollOffsetY != 0) && (paramMotionEvent.getY() < StickersAlert.this.scrollOffsetY))
        {
          StickersAlert.this.dismiss();
          return true;
        }
        return super.onInterceptTouchEvent(paramMotionEvent);
      }

      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        if (this.lastNotifyWidth != paramInt3 - paramInt1)
        {
          this.lastNotifyWidth = (paramInt3 - paramInt1);
          if ((StickersAlert.this.adapter != null) && (StickersAlert.this.stickerSetCovereds != null))
            StickersAlert.this.adapter.notifyDataSetChanged();
        }
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        StickersAlert.this.updateLayout();
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        paramInt2 = View.MeasureSpec.getSize(paramInt2);
        int j = paramInt2;
        if (Build.VERSION.SDK_INT >= 21)
          j = paramInt2 - AndroidUtilities.statusBarHeight;
        getMeasuredWidth();
        StickersAlert.access$1402(StickersAlert.this, (View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(36.0F)) / 5);
        int k;
        if (StickersAlert.this.stickerSetCovereds != null)
        {
          k = AndroidUtilities.dp(56.0F) + AndroidUtilities.dp(60.0F) * StickersAlert.this.stickerSetCovereds.size() + StickersAlert.GridAdapter.access$1500(StickersAlert.this.adapter) * AndroidUtilities.dp(82.0F);
          if (k >= j / 5 * 3.2D)
            break label322;
        }
        label322: for (int i = 0; ; i = j / 5 * 2)
        {
          paramInt2 = i;
          if (i != 0)
          {
            paramInt2 = i;
            if (k < j)
              paramInt2 = i - (j - k);
          }
          i = paramInt2;
          if (paramInt2 == 0)
            i = StickersAlert.backgroundPaddingTop;
          paramInt2 = i;
          if (StickersAlert.this.stickerSetCovereds != null)
            paramInt2 = i + AndroidUtilities.dp(8.0F);
          if (StickersAlert.this.gridView.getPaddingTop() != paramInt2)
          {
            StickersAlert.access$1802(StickersAlert.this, true);
            StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0F), paramInt2, AndroidUtilities.dp(10.0F), 0);
            StickersAlert.this.emptyView.setPadding(0, paramInt2, 0, 0);
            StickersAlert.access$1802(StickersAlert.this, false);
          }
          super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(Math.min(k, j), 1073741824));
          return;
          i = AndroidUtilities.dp(96.0F);
          if (StickersAlert.this.stickerSet != null);
          for (paramInt2 = (int)Math.ceil(StickersAlert.this.stickerSet.documents.size() / 5.0F); ; paramInt2 = 0)
          {
            k = Math.max(3, paramInt2) * AndroidUtilities.dp(82.0F) + i + StickersAlert.backgroundPaddingTop;
            break;
          }
        }
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        return (!StickersAlert.this.isDismissed()) && (super.onTouchEvent(paramMotionEvent));
      }

      public void requestLayout()
      {
        if (StickersAlert.this.ignoreLayout)
          return;
        super.requestLayout();
      }
    };
    this.containerView.setWillNotDraw(false);
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.titleTextView = new TextView(paramContext);
    this.titleTextView.setLines(1);
    this.titleTextView.setSingleLine(true);
    this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
    this.titleTextView.setTextSize(1, 20.0F);
    this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    this.titleTextView.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    this.titleTextView.setGravity(16);
    this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-1, 48));
    this.titleTextView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    this.shadow[0] = new View(paramContext);
    this.shadow[0].setBackgroundResource(2130837728);
    this.shadow[0].setAlpha(0.0F);
    this.shadow[0].setVisibility(4);
    this.shadow[0].setTag(Integer.valueOf(1));
    this.containerView.addView(this.shadow[0], LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
    this.gridView = new RecyclerListView(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        int i = 0;
        boolean bool = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramMotionEvent, StickersAlert.this.gridView, 0, null);
        if ((super.onInterceptTouchEvent(paramMotionEvent)) || (bool))
          i = 1;
        return i;
      }

      public void requestLayout()
      {
        if (StickersAlert.this.ignoreLayout)
          return;
        super.requestLayout();
      }
    };
    this.gridView.setTag(Integer.valueOf(14));
    Object localObject1 = this.gridView;
    Object localObject2 = new GridLayoutManager(getContext(), 5);
    this.layoutManager = ((GridLayoutManager)localObject2);
    ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
    this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
    {
      public int getSpanSize(int paramInt)
      {
        if (((StickersAlert.this.stickerSetCovereds != null) && ((StickersAlert.GridAdapter.access$2300(StickersAlert.this.adapter).get(Integer.valueOf(paramInt)) instanceof Integer))) || (paramInt == StickersAlert.GridAdapter.access$2400(StickersAlert.this.adapter)))
          return StickersAlert.GridAdapter.access$2500(StickersAlert.this.adapter);
        return 1;
      }
    });
    localObject1 = this.gridView;
    localObject2 = new GridAdapter(paramContext);
    this.adapter = ((GridAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    this.gridView.setVerticalScrollBarEnabled(false);
    this.gridView.addItemDecoration(new RecyclerView.ItemDecoration()
    {
      public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
      {
        paramRect.left = 0;
        paramRect.right = 0;
        paramRect.bottom = 0;
        paramRect.top = 0;
      }
    });
    this.gridView.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.gridView.setClipToPadding(false);
    this.gridView.setEnabled(true);
    this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
    this.gridView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return StickerPreviewViewer.getInstance().onTouch(paramMotionEvent, StickersAlert.this.gridView, 0, StickersAlert.this.stickersOnItemClickListener, null);
      }
    });
    this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
      {
        StickersAlert.this.updateLayout();
      }
    });
    this.stickersOnItemClickListener = new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if (StickersAlert.this.stickerSetCovereds != null)
        {
          paramView = (TLRPC.StickerSetCovered)StickersAlert.GridAdapter.access$2700(StickersAlert.this.adapter).get(Integer.valueOf(paramInt));
          if (paramView != null)
          {
            StickersAlert.this.dismiss();
            TLRPC.TL_inputStickerSetID localTL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
            localTL_inputStickerSetID.access_hash = paramView.set.access_hash;
            localTL_inputStickerSetID.id = paramView.set.id;
            new StickersAlert(StickersAlert.this.parentActivity, StickersAlert.this.parentFragment, localTL_inputStickerSetID, null, null).show();
          }
        }
        do
          return;
        while ((StickersAlert.this.stickerSet == null) || (paramInt < 0) || (paramInt >= StickersAlert.this.stickerSet.documents.size()));
        StickersAlert.access$2902(StickersAlert.this, (TLRPC.Document)StickersAlert.this.stickerSet.documents.get(paramInt));
        paramInt = 0;
        if (paramInt < StickersAlert.this.selectedSticker.attributes.size())
        {
          paramView = (TLRPC.DocumentAttribute)StickersAlert.this.selectedSticker.attributes.get(paramInt);
          if ((paramView instanceof TLRPC.TL_documentAttributeSticker))
          {
            if ((paramView.alt == null) || (paramView.alt.length() <= 0))
              break label451;
            StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(paramView.alt, StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0F), false));
          }
        }
        label451: for (paramInt = 1; ; paramInt = 0)
        {
          if (paramInt == 0)
            StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0F), false));
          StickersAlert.this.stickerImageView.getImageReceiver().setImage(StickersAlert.this.selectedSticker, null, StickersAlert.this.selectedSticker.thumb.location, null, "webp", true);
          paramView = (FrameLayout.LayoutParams)StickersAlert.this.stickerPreviewLayout.getLayoutParams();
          paramView.topMargin = StickersAlert.this.scrollOffsetY;
          StickersAlert.this.stickerPreviewLayout.setLayoutParams(paramView);
          StickersAlert.this.stickerPreviewLayout.setVisibility(0);
          paramView = new AnimatorSet();
          paramView.playTogether(new Animator[] { ObjectAnimator.ofFloat(StickersAlert.access$3200(StickersAlert.this), "alpha", new float[] { 0.0F, 1.0F }) });
          paramView.setDuration(200L);
          paramView.start();
          return;
          paramInt += 1;
          break;
        }
      }
    };
    this.gridView.setOnItemClickListener(this.stickersOnItemClickListener);
    this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 48.0F));
    this.emptyView = new FrameLayout(paramContext)
    {
      public void requestLayout()
      {
        if (StickersAlert.this.ignoreLayout)
          return;
        super.requestLayout();
      }
    };
    this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
    this.gridView.setEmptyView(this.emptyView);
    this.emptyView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    localObject1 = new RadialProgressView(paramContext);
    this.emptyView.addView((View)localObject1, LayoutHelper.createFrame(-2, -2, 17));
    this.shadow[1] = new View(paramContext);
    this.shadow[1].setBackgroundResource(2130837729);
    this.containerView.addView(this.shadow[1], LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    this.pickerBottomLayout = new PickerBottomLayout(paramContext, false);
    this.pickerBottomLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
    this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
    this.pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    this.pickerBottomLayout.cancelButton.setTextColor(Theme.getColor("dialogTextBlue2"));
    this.pickerBottomLayout.cancelButton.setText(LocaleController.getString("Close", 2131165556).toUpperCase());
    this.pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        StickersAlert.this.dismiss();
      }
    });
    this.pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    this.pickerBottomLayout.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5F), Theme.getColor("dialogBadgeBackground")));
    this.stickerPreviewLayout = new FrameLayout(paramContext);
    this.stickerPreviewLayout.setBackgroundColor(Theme.getColor("dialogBackground") & 0xDFFFFFFF);
    this.stickerPreviewLayout.setVisibility(8);
    this.stickerPreviewLayout.setSoundEffectsEnabled(false);
    this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0F));
    this.stickerPreviewLayout.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        StickersAlert.this.hidePreview();
      }
    });
    localObject1 = new ImageView(paramContext);
    ((ImageView)localObject1).setImageResource(2130837937);
    ((ImageView)localObject1).setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextGray3"), PorterDuff.Mode.MULTIPLY));
    ((ImageView)localObject1).setScaleType(ImageView.ScaleType.CENTER);
    this.stickerPreviewLayout.addView((View)localObject1, LayoutHelper.createFrame(48, 48, 53));
    ((ImageView)localObject1).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        StickersAlert.this.hidePreview();
      }
    });
    this.stickerImageView = new BackupImageView(paramContext);
    this.stickerImageView.setAspectFit(true);
    this.stickerPreviewLayout.addView(this.stickerImageView);
    this.stickerEmojiTextView = new TextView(paramContext);
    this.stickerEmojiTextView.setTextSize(1, 30.0F);
    this.stickerEmojiTextView.setGravity(85);
    this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
    this.previewSendButton = new TextView(paramContext);
    this.previewSendButton.setTextSize(1, 14.0F);
    this.previewSendButton.setTextColor(Theme.getColor("dialogTextBlue2"));
    this.previewSendButton.setGravity(17);
    this.previewSendButton.setBackgroundColor(Theme.getColor("dialogBackground"));
    this.previewSendButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
    this.previewSendButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
    this.previewSendButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        StickersAlert.this.delegate.onStickerSelected(StickersAlert.this.selectedSticker);
        StickersAlert.this.dismiss();
      }
    });
    this.previewSendButtonShadow = new View(paramContext);
    this.previewSendButtonShadow.setBackgroundResource(2130837729);
    this.stickerPreviewLayout.addView(this.previewSendButtonShadow, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    updateFields();
    updateSendButton();
    this.adapter.notifyDataSetChanged();
  }

  private void loadStickerSet()
  {
    if (this.inputStickerSet != null)
    {
      if ((this.stickerSet == null) && (this.inputStickerSet.short_name != null))
        this.stickerSet = StickersQuery.getStickerSetByName(this.inputStickerSet.short_name);
      if (this.stickerSet == null)
        this.stickerSet = StickersQuery.getStickerSetById(Long.valueOf(this.inputStickerSet.id));
      if (this.stickerSet == null)
      {
        TLRPC.TL_messages_getStickerSet localTL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
        localTL_messages_getStickerSet.stickerset = this.inputStickerSet;
        ConnectionsManager.getInstance().sendRequest(localTL_messages_getStickerSet, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
            {
              public void run()
              {
                StickersAlert.access$002(StickersAlert.this, 0);
                if (this.val$error == null)
                {
                  StickersAlert.access$902(StickersAlert.this, (TLRPC.TL_messages_stickerSet)this.val$response);
                  StickersAlert localStickersAlert = StickersAlert.this;
                  if (!StickersAlert.this.stickerSet.set.masks);
                  for (boolean bool = true; ; bool = false)
                  {
                    StickersAlert.access$1002(localStickersAlert, bool);
                    StickersAlert.this.updateSendButton();
                    StickersAlert.this.updateFields();
                    StickersAlert.this.adapter.notifyDataSetChanged();
                    return;
                  }
                }
                Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersNotFound", 2131165287), 0).show();
                StickersAlert.this.dismiss();
              }
            });
          }
        });
      }
    }
    else if (this.stickerSet != null)
    {
      if (this.stickerSet.set.masks)
        break label154;
    }
    label154: for (boolean bool = true; ; bool = false)
    {
      this.showEmoji = bool;
      return;
      if (this.adapter == null)
        break;
      updateSendButton();
      updateFields();
      this.adapter.notifyDataSetChanged();
      break;
    }
  }

  private void runShadowAnimation(int paramInt, boolean paramBoolean)
  {
    if (this.stickerSetCovereds != null);
    do
      return;
    while (((!paramBoolean) || (this.shadow[paramInt].getTag() == null)) && ((paramBoolean) || (this.shadow[paramInt].getTag() != null)));
    View localView = this.shadow[paramInt];
    Object localObject;
    float f;
    if (paramBoolean)
    {
      localObject = null;
      localView.setTag(localObject);
      if (paramBoolean)
        this.shadow[paramInt].setVisibility(0);
      if (this.shadowAnimation[paramInt] != null)
        this.shadowAnimation[paramInt].cancel();
      this.shadowAnimation[paramInt] = new AnimatorSet();
      localObject = this.shadowAnimation[paramInt];
      localView = this.shadow[paramInt];
      if (!paramBoolean)
        break label207;
      f = 1.0F;
    }
    while (true)
    {
      ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(localView, "alpha", new float[] { f }) });
      this.shadowAnimation[paramInt].setDuration(150L);
      this.shadowAnimation[paramInt].addListener(new AnimatorListenerAdapter(paramInt, paramBoolean)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((StickersAlert.this.shadowAnimation[this.val$num] != null) && (StickersAlert.this.shadowAnimation[this.val$num].equals(paramAnimator)))
            StickersAlert.this.shadowAnimation[this.val$num] = null;
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((StickersAlert.this.shadowAnimation[this.val$num] != null) && (StickersAlert.this.shadowAnimation[this.val$num].equals(paramAnimator)))
          {
            if (!this.val$show)
              StickersAlert.this.shadow[this.val$num].setVisibility(4);
            StickersAlert.this.shadowAnimation[this.val$num] = null;
          }
        }
      });
      this.shadowAnimation[paramInt].start();
      return;
      localObject = Integer.valueOf(1);
      break;
      label207: f = 0.0F;
    }
  }

  private void setRightButton(View.OnClickListener paramOnClickListener, String paramString, int paramInt, boolean paramBoolean)
  {
    if (paramString == null)
    {
      this.pickerBottomLayout.doneButton.setVisibility(8);
      return;
    }
    this.pickerBottomLayout.doneButton.setVisibility(0);
    if (paramBoolean)
    {
      this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(0);
      this.pickerBottomLayout.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(this.stickerSet.documents.size()) }));
    }
    while (true)
    {
      this.pickerBottomLayout.doneButtonTextView.setTextColor(paramInt);
      this.pickerBottomLayout.doneButtonTextView.setText(paramString.toUpperCase());
      this.pickerBottomLayout.doneButton.setOnClickListener(paramOnClickListener);
      return;
      this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
    }
  }

  private void updateFields()
  {
    if (this.titleTextView == null)
      return;
    if (this.stickerSet != null)
    {
      this.titleTextView.setText(this.stickerSet.set.title);
      String str;
      if ((this.stickerSet.set == null) || (!StickersQuery.isStickerPackInstalled(this.stickerSet.set.id)))
      {
        17 local17 = new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            StickersAlert.this.dismiss();
            if (StickersAlert.this.installDelegate != null)
              StickersAlert.this.installDelegate.onStickerSetInstalled();
            paramView = new TLRPC.TL_messages_installStickerSet();
            paramView.stickerset = StickersAlert.this.inputStickerSet;
            ConnectionsManager.getInstance().sendRequest(paramView, new RequestDelegate()
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                {
                  public void run()
                  {
                    try
                    {
                      if (this.val$error == null)
                      {
                        if (StickersAlert.this.stickerSet.set.masks)
                          Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddMasksInstalled", 2131165280), 0).show();
                        while (true)
                        {
                          if ((this.val$response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive))
                          {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[0]);
                            if ((StickersAlert.this.parentFragment != null) && (StickersAlert.this.parentFragment.getParentActivity() != null))
                            {
                              StickersArchiveAlert localStickersArchiveAlert = new StickersArchiveAlert(StickersAlert.this.parentFragment.getParentActivity(), StickersAlert.this.parentFragment, ((TLRPC.TL_messages_stickerSetInstallResultArchive)this.val$response).sets);
                              StickersAlert.this.parentFragment.showDialog(localStickersArchiveAlert.create());
                            }
                          }
                          if (!StickersAlert.this.stickerSet.set.masks)
                            break;
                          i = 1;
                          StickersQuery.loadStickers(i, false, true);
                          return;
                          Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersInstalled", 2131165286), 0).show();
                        }
                      }
                    }
                    catch (Exception localException)
                    {
                      while (true)
                      {
                        FileLog.e(localException);
                        continue;
                        Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("ErrorOccurred", 2131165701), 0).show();
                        continue;
                        int i = 0;
                      }
                    }
                  }
                });
              }
            });
          }
        };
        if ((this.stickerSet != null) && (this.stickerSet.set.masks))
        {
          str = LocaleController.getString("AddMasks", 2131165279);
          setRightButton(local17, str, Theme.getColor("dialogTextBlue2"), true);
        }
      }
      while (true)
      {
        this.adapter.notifyDataSetChanged();
        return;
        str = LocaleController.getString("AddStickers", 2131165285);
        break;
        if (this.stickerSet.set.official)
        {
          setRightButton(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              if (StickersAlert.this.installDelegate != null)
                StickersAlert.this.installDelegate.onStickerSetUninstalled();
              StickersAlert.this.dismiss();
              StickersQuery.removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 1, StickersAlert.this.parentFragment, true);
            }
          }
          , LocaleController.getString("StickersRemove", 2131166487), Theme.getColor("dialogTextRed"), false);
          continue;
        }
        setRightButton(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (StickersAlert.this.installDelegate != null)
              StickersAlert.this.installDelegate.onStickerSetUninstalled();
            StickersAlert.this.dismiss();
            StickersQuery.removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 0, StickersAlert.this.parentFragment, true);
          }
        }
        , LocaleController.getString("StickersRemove", 2131166488), Theme.getColor("dialogTextRed"), false);
      }
    }
    setRightButton(null, null, Theme.getColor("dialogTextRed"), false);
  }

  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    Object localObject;
    int i;
    if (this.gridView.getChildCount() <= 0)
    {
      localObject = this.gridView;
      i = this.gridView.getPaddingTop();
      this.scrollOffsetY = i;
      ((RecyclerListView)localObject).setTopGlowOffset(i);
      if (this.stickerSetCovereds == null)
      {
        this.titleTextView.setTranslationY(this.scrollOffsetY);
        this.shadow[0].setTranslationY(this.scrollOffsetY);
      }
      this.containerView.invalidate();
    }
    while (true)
    {
      return;
      localObject = this.gridView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.gridView.findContainingViewHolder((View)localObject);
      i = ((View)localObject).getTop();
      if ((i >= 0) && (localHolder != null) && (localHolder.getAdapterPosition() == 0))
        runShadowAnimation(0, false);
      while (this.scrollOffsetY != i)
      {
        localObject = this.gridView;
        this.scrollOffsetY = i;
        ((RecyclerListView)localObject).setTopGlowOffset(i);
        if (this.stickerSetCovereds == null)
        {
          this.titleTextView.setTranslationY(this.scrollOffsetY);
          this.shadow[0].setTranslationY(this.scrollOffsetY);
        }
        this.containerView.invalidate();
        return;
        runShadowAnimation(0, true);
        i = 0;
      }
    }
  }

  private void updateSendButton()
  {
    int i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2 / AndroidUtilities.density);
    if ((this.delegate != null) && ((this.stickerSet == null) || (!this.stickerSet.set.masks)))
    {
      this.previewSendButton.setText(LocaleController.getString("SendSticker", 2131166421).toUpperCase());
      this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(i, i, 17, 0.0F, 0.0F, 0.0F, 30.0F));
      this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(i, i, 17, 0.0F, 0.0F, 0.0F, 30.0F));
      this.previewSendButton.setVisibility(0);
      this.previewSendButtonShadow.setVisibility(0);
      return;
    }
    this.previewSendButton.setText(LocaleController.getString("Close", 2131165556).toUpperCase());
    this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(i, i, 17));
    this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(i, i, 17));
    this.previewSendButton.setVisibility(8);
    this.previewSendButtonShadow.setVisibility(8);
  }

  protected boolean canDismissWithSwipe()
  {
    return false;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.emojiDidLoaded)
    {
      if (this.gridView != null)
      {
        int i = this.gridView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          this.gridView.getChildAt(paramInt).invalidate();
          paramInt += 1;
        }
      }
      if (StickerPreviewViewer.getInstance().isVisible())
        StickerPreviewViewer.getInstance().close();
      StickerPreviewViewer.getInstance().reset();
    }
  }

  public void dismiss()
  {
    super.dismiss();
    if (this.reqId != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
      this.reqId = 0;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
  }

  public void setInstallDelegate(StickersAlertInstallDelegate paramStickersAlertInstallDelegate)
  {
    this.installDelegate = paramStickersAlertInstallDelegate;
  }

  private class GridAdapter extends RecyclerListView.SelectionAdapter
  {
    private HashMap<Integer, Object> cache = new HashMap();
    private Context context;
    private HashMap<Integer, TLRPC.StickerSetCovered> positionsToSets = new HashMap();
    private int stickersPerRow;
    private int stickersRowCount;
    private int totalItems;

    public GridAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    public int getItemCount()
    {
      return this.totalItems;
    }

    public int getItemViewType(int paramInt)
    {
      if (StickersAlert.this.stickerSetCovereds != null)
      {
        Object localObject = this.cache.get(Integer.valueOf(paramInt));
        if (localObject == null)
          break label37;
        if (!(localObject instanceof TLRPC.Document));
      }
      else
      {
        return 0;
      }
      return 2;
      label37: return 1;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void notifyDataSetChanged()
    {
      int i = 0;
      if (StickersAlert.this.stickerSetCovereds != null)
      {
        int j = StickersAlert.this.gridView.getMeasuredWidth();
        i = j;
        if (j == 0)
          i = AndroidUtilities.displaySize.x;
        this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
        StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
        this.cache.clear();
        this.positionsToSets.clear();
        this.totalItems = 0;
        this.stickersRowCount = 0;
        i = 0;
        if (i < StickersAlert.this.stickerSetCovereds.size())
        {
          TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)StickersAlert.this.stickerSetCovereds.get(i);
          if ((localStickerSetCovered.covers.isEmpty()) && (localStickerSetCovered.cover == null));
          while (true)
          {
            i += 1;
            break;
            this.stickersRowCount = (int)(this.stickersRowCount + Math.ceil(StickersAlert.this.stickerSetCovereds.size() / this.stickersPerRow));
            this.positionsToSets.put(Integer.valueOf(this.totalItems), localStickerSetCovered);
            HashMap localHashMap = this.cache;
            j = this.totalItems;
            this.totalItems = (j + 1);
            localHashMap.put(Integer.valueOf(j), Integer.valueOf(i));
            j = this.totalItems / this.stickersPerRow;
            int k;
            if (!localStickerSetCovered.covers.isEmpty())
            {
              k = (int)Math.ceil(localStickerSetCovered.covers.size() / this.stickersPerRow);
              j = 0;
              while (j < localStickerSetCovered.covers.size())
              {
                this.cache.put(Integer.valueOf(this.totalItems + j), localStickerSetCovered.covers.get(j));
                j += 1;
              }
              j = k;
            }
            while (true)
            {
              k = 0;
              while (k < this.stickersPerRow * j)
              {
                this.positionsToSets.put(Integer.valueOf(this.totalItems + k), localStickerSetCovered);
                k += 1;
              }
              j = 1;
              this.cache.put(Integer.valueOf(this.totalItems), localStickerSetCovered.cover);
            }
            this.totalItems += j * this.stickersPerRow;
          }
        }
      }
      else
      {
        if (StickersAlert.this.stickerSet != null)
          i = StickersAlert.this.stickerSet.documents.size();
        this.totalItems = i;
      }
      super.notifyDataSetChanged();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (StickersAlert.this.stickerSetCovereds != null)
      {
        switch (paramViewHolder.getItemViewType())
        {
        default:
          return;
        case 0:
          localObject = (TLRPC.Document)this.cache.get(Integer.valueOf(paramInt));
          ((StickerEmojiCell)paramViewHolder.itemView).setSticker((TLRPC.Document)localObject, false);
          return;
        case 1:
          ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(82.0F));
          return;
        case 2:
        }
        Object localObject = (TLRPC.StickerSetCovered)StickersAlert.this.stickerSetCovereds.get(((Integer)this.cache.get(Integer.valueOf(paramInt))).intValue());
        ((FeaturedStickerSetInfoCell)paramViewHolder.itemView).setStickerSet((TLRPC.StickerSetCovered)localObject, false);
        return;
      }
      ((StickerEmojiCell)paramViewHolder.itemView).setSticker((TLRPC.Document)StickersAlert.this.stickerSet.documents.get(paramInt), StickersAlert.this.showEmoji);
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
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new StickerEmojiCell(this.context)
        {
          public void onMeasure(int paramInt1, int paramInt2)
          {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0F), 1073741824));
          }
        };
        continue;
        paramViewGroup = new EmptyCell(this.context);
        continue;
        paramViewGroup = new FeaturedStickerSetInfoCell(this.context, 8);
      }
    }
  }

  public static abstract interface StickersAlertDelegate
  {
    public abstract void onStickerSelected(TLRPC.Document paramDocument);
  }

  public static abstract interface StickersAlertInstallDelegate
  {
    public abstract void onStickerSetInstalled();

    public abstract void onStickerSetUninstalled();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.StickersAlert
 * JD-Core Version:    0.6.0
 */