package org.vidogram.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.Cells.ContextLinkCell;
import org.vidogram.ui.Cells.StickerCell;
import org.vidogram.ui.Cells.StickerEmojiCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;

public class StickerPreviewViewer
{

  @SuppressLint({"StaticFieldLeak"})
  private static volatile StickerPreviewViewer Instance = null;
  private static TextPaint textPaint;
  private ColorDrawable backgroundDrawable = new ColorDrawable(1895825408);
  private ImageReceiver centerImage = new ImageReceiver();
  private FrameLayoutDrawer containerView;
  private TLRPC.InputStickerSet currentSet;
  private TLRPC.Document currentSticker;
  private View currentStickerPreviewCell;
  private StickerPreviewViewerDelegate delegate;
  private boolean isVisible = false;
  private int keyboardHeight = AndroidUtilities.dp(200.0F);
  private long lastUpdateTime;
  private Runnable openStickerPreviewRunnable;
  private Activity parentActivity;
  private float showProgress;
  private Runnable showSheetRunnable = new Runnable()
  {
    public void run()
    {
      if ((StickerPreviewViewer.this.parentActivity == null) || (StickerPreviewViewer.this.currentSet == null))
        return;
      BottomSheet.Builder localBuilder = new BottomSheet.Builder(StickerPreviewViewer.this.parentActivity);
      String str1 = LocaleController.getString("SendStickerPreview", 2131166422);
      String str2 = LocaleController.formatString("ViewPackPreview", 2131166572, new Object[0]);
      1 local1 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          if (StickerPreviewViewer.this.parentActivity == null);
          do
          {
            do
              return;
            while (StickerPreviewViewer.this.delegate == null);
            if (paramInt != 0)
              continue;
            StickerPreviewViewer.this.delegate.sentSticker(StickerPreviewViewer.this.currentSticker);
            return;
          }
          while (paramInt != 1);
          StickerPreviewViewer.this.delegate.openSet(StickerPreviewViewer.this.currentSet);
        }
      };
      localBuilder.setItems(new CharSequence[] { str1, str2 }, local1);
      StickerPreviewViewer.access$502(StickerPreviewViewer.this, localBuilder.create());
      StickerPreviewViewer.this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramDialogInterface)
        {
          StickerPreviewViewer.access$502(StickerPreviewViewer.this, null);
          StickerPreviewViewer.this.close();
        }
      });
      StickerPreviewViewer.this.visibleDialog.show();
    }
  };
  private int startX;
  private int startY;
  private StaticLayout stickerEmojiLayout;
  private Dialog visibleDialog;
  private WindowManager.LayoutParams windowLayoutParams;
  private FrameLayout windowView;

  public static StickerPreviewViewer getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        StickerPreviewViewer localStickerPreviewViewer = Instance;
        localObject1 = localStickerPreviewViewer;
        if (localStickerPreviewViewer == null)
        {
          localObject1 = new StickerPreviewViewer();
          Instance = (StickerPreviewViewer)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (StickerPreviewViewer)localObject2;
  }

  @SuppressLint({"DrawAllocation"})
  private void onDraw(Canvas paramCanvas)
  {
    if ((this.containerView == null) || (this.backgroundDrawable == null));
    while (true)
    {
      return;
      this.backgroundDrawable.setAlpha((int)(180.0F * this.showProgress));
      this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
      this.backgroundDrawable.draw(paramCanvas);
      paramCanvas.save();
      int i = (int)(Math.min(this.containerView.getWidth(), this.containerView.getHeight()) / 1.8F);
      paramCanvas.translate(this.containerView.getWidth() / 2, Math.max(i / 2 + AndroidUtilities.statusBarHeight, (this.containerView.getHeight() - this.keyboardHeight) / 2));
      if (this.centerImage.getBitmap() != null)
      {
        float f = this.showProgress * 0.8F / 0.8F;
        i = (int)(i * f);
        this.centerImage.setAlpha(this.showProgress);
        this.centerImage.setImageCoords(-i / 2, -i / 2, i, i);
        this.centerImage.draw(paramCanvas);
      }
      if (this.stickerEmojiLayout != null)
      {
        paramCanvas.translate(-AndroidUtilities.dp(50.0F), -this.centerImage.getImageHeight() / 2 - AndroidUtilities.dp(30.0F));
        this.stickerEmojiLayout.draw(paramCanvas);
      }
      paramCanvas.restore();
      if (this.isVisible)
      {
        if (this.showProgress == 1.0F)
          continue;
        l1 = System.currentTimeMillis();
        l2 = this.lastUpdateTime;
        this.lastUpdateTime = l1;
        this.showProgress += (float)(l1 - l2) / 120.0F;
        this.containerView.invalidate();
        if (this.showProgress <= 1.0F)
          continue;
        this.showProgress = 1.0F;
        return;
      }
      if (this.showProgress == 0.0F)
        continue;
      long l1 = System.currentTimeMillis();
      long l2 = this.lastUpdateTime;
      this.lastUpdateTime = l1;
      this.showProgress -= (float)(l1 - l2) / 120.0F;
      this.containerView.invalidate();
      if (this.showProgress < 0.0F)
        this.showProgress = 0.0F;
      if (this.showProgress != 0.0F)
        continue;
      AndroidUtilities.unlockOrientation(this.parentActivity);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          StickerPreviewViewer.this.centerImage.setImageBitmap((Bitmap)null);
        }
      });
      try
      {
        if (this.windowView.getParent() == null)
          continue;
        ((WindowManager)this.parentActivity.getSystemService("window")).removeView(this.windowView);
        return;
      }
      catch (Exception paramCanvas)
      {
        FileLog.e(paramCanvas);
      }
    }
  }

  public void close()
  {
    if ((this.parentActivity == null) || (this.visibleDialog != null))
      return;
    AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
    this.showProgress = 1.0F;
    this.lastUpdateTime = System.currentTimeMillis();
    this.containerView.invalidate();
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      this.currentSticker = null;
      this.currentSet = null;
      this.delegate = null;
      this.isVisible = false;
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void destroy()
  {
    this.isVisible = false;
    this.delegate = null;
    this.currentSticker = null;
    this.currentSet = null;
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
      if ((this.parentActivity == null) || (this.windowView == null))
        return;
    }
    catch (Exception localException1)
    {
      while (true)
        FileLog.e(localException1);
    }
    try
    {
      if (this.windowView.getParent() != null)
        ((WindowManager)this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
      this.windowView = null;
      Instance = null;
      return;
    }
    catch (Exception localException2)
    {
      while (true)
        FileLog.e(localException2);
    }
  }

  public boolean isVisible()
  {
    return this.isVisible;
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent, View paramView, int paramInt, StickerPreviewViewerDelegate paramStickerPreviewViewerDelegate)
  {
    this.delegate = paramStickerPreviewViewerDelegate;
    int k;
    int m;
    int i;
    if (paramMotionEvent.getAction() == 0)
    {
      k = (int)paramMotionEvent.getX();
      m = (int)paramMotionEvent.getY();
      if (!(paramView instanceof AbsListView))
        break label78;
      i = ((AbsListView)paramView).getChildCount();
    }
    while (true)
    {
      int j = 0;
      label46: if (j < i)
      {
        paramMotionEvent = null;
        if (!(paramView instanceof AbsListView))
          break label97;
        paramMotionEvent = ((AbsListView)paramView).getChildAt(j);
        label72: if (paramMotionEvent != null)
          break label117;
      }
      while (true)
      {
        return false;
        label78: if (!(paramView instanceof RecyclerListView))
          break label308;
        i = ((RecyclerListView)paramView).getChildCount();
        break;
        label97: if (!(paramView instanceof RecyclerListView))
          break label72;
        paramMotionEvent = ((RecyclerListView)paramView).getChildAt(j);
        break label72;
        label117: int n = paramMotionEvent.getTop();
        int i1 = paramMotionEvent.getBottom();
        int i2 = paramMotionEvent.getLeft();
        int i3 = paramMotionEvent.getRight();
        if ((n > m) || (i1 < m) || (i2 > k) || (i3 < k))
        {
          j += 1;
          break label46;
        }
        boolean bool;
        if ((paramMotionEvent instanceof StickerEmojiCell))
          bool = ((StickerEmojiCell)paramMotionEvent).showingBitmap();
        while (bool)
        {
          this.startX = k;
          this.startY = m;
          this.currentStickerPreviewCell = paramMotionEvent;
          this.openStickerPreviewRunnable = new Runnable(paramView, paramInt)
          {
            public void run()
            {
              if (StickerPreviewViewer.this.openStickerPreviewRunnable == null);
              do
              {
                return;
                if ((this.val$listView instanceof AbsListView))
                {
                  ((AbsListView)this.val$listView).setOnItemClickListener(null);
                  ((AbsListView)this.val$listView).requestDisallowInterceptTouchEvent(true);
                }
                while (true)
                {
                  StickerPreviewViewer.access$602(StickerPreviewViewer.this, null);
                  StickerPreviewViewer.this.setParentActivity((Activity)this.val$listView.getContext());
                  StickerPreviewViewer.this.setKeyboardHeight(this.val$height);
                  if (!(StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerEmojiCell))
                    break;
                  StickerPreviewViewer.this.open(((StickerEmojiCell)StickerPreviewViewer.this.currentStickerPreviewCell).getSticker(), ((StickerEmojiCell)StickerPreviewViewer.this.currentStickerPreviewCell).isRecent());
                  ((StickerEmojiCell)StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                  return;
                  if (!(this.val$listView instanceof RecyclerListView))
                    continue;
                  ((RecyclerListView)this.val$listView).setOnItemClickListener(null);
                  ((RecyclerListView)this.val$listView).requestDisallowInterceptTouchEvent(true);
                }
                if (!(StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerCell))
                  continue;
                StickerPreviewViewer.this.open(((StickerCell)StickerPreviewViewer.this.currentStickerPreviewCell).getSticker(), false);
                ((StickerCell)StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                return;
              }
              while (!(StickerPreviewViewer.this.currentStickerPreviewCell instanceof ContextLinkCell));
              StickerPreviewViewer.this.open(((ContextLinkCell)StickerPreviewViewer.this.currentStickerPreviewCell).getDocument(), false);
              ((ContextLinkCell)StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
            }
          };
          AndroidUtilities.runOnUIThread(this.openStickerPreviewRunnable, 200L);
          return true;
          if ((paramMotionEvent instanceof StickerCell))
          {
            bool = ((StickerCell)paramMotionEvent).showingBitmap();
            continue;
          }
          if ((paramMotionEvent instanceof ContextLinkCell))
          {
            paramStickerPreviewViewerDelegate = (ContextLinkCell)paramMotionEvent;
            if ((paramStickerPreviewViewerDelegate.isSticker()) && (paramStickerPreviewViewerDelegate.showingBitmap()))
            {
              bool = true;
              continue;
            }
            bool = false;
            continue;
          }
          bool = false;
        }
      }
      label308: i = 0;
    }
  }

  public boolean onTouch(MotionEvent paramMotionEvent, View paramView, int paramInt, Object paramObject, StickerPreviewViewerDelegate paramStickerPreviewViewerDelegate)
  {
    this.delegate = paramStickerPreviewViewerDelegate;
    if ((this.openStickerPreviewRunnable != null) || (isVisible()))
    {
      if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3) && (paramMotionEvent.getAction() != 6))
        break label178;
      AndroidUtilities.runOnUIThread(new Runnable(paramView, paramObject)
      {
        public void run()
        {
          if ((this.val$listView instanceof AbsListView))
            ((AbsListView)this.val$listView).setOnItemClickListener((AdapterView.OnItemClickListener)this.val$listener);
          do
            return;
          while (!(this.val$listView instanceof RecyclerListView));
          ((RecyclerListView)this.val$listView).setOnItemClickListener((RecyclerListView.OnItemClickListener)this.val$listener);
        }
      }
      , 150L);
      if (this.openStickerPreviewRunnable == null)
        break label83;
      AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
      this.openStickerPreviewRunnable = null;
    }
    label83: 
    do
    {
      do
      {
        do
          return false;
        while (!isVisible());
        close();
      }
      while (this.currentStickerPreviewCell == null);
      if ((this.currentStickerPreviewCell instanceof StickerEmojiCell))
        ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(false);
      while (true)
      {
        this.currentStickerPreviewCell = null;
        break;
        if ((this.currentStickerPreviewCell instanceof StickerCell))
        {
          ((StickerCell)this.currentStickerPreviewCell).setScaled(false);
          continue;
        }
        if (!(this.currentStickerPreviewCell instanceof ContextLinkCell))
          continue;
        ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(false);
      }
    }
    while (paramMotionEvent.getAction() == 0);
    label178: int k;
    int m;
    int i;
    if (isVisible())
      if (paramMotionEvent.getAction() == 2)
      {
        k = (int)paramMotionEvent.getX();
        m = (int)paramMotionEvent.getY();
        if ((paramView instanceof AbsListView))
          i = ((AbsListView)paramView).getChildCount();
      }
    while (true)
    {
      int j = 0;
      while (true)
      {
        if (j >= i)
          break label391;
        paramMotionEvent = null;
        if ((paramView instanceof AbsListView))
          paramMotionEvent = ((AbsListView)paramView).getChildAt(j);
        while (true)
        {
          if (paramMotionEvent != null)
            break label304;
          return false;
          if (!(paramView instanceof RecyclerListView))
            break label710;
          i = ((RecyclerListView)paramView).getChildCount();
          break;
          if (!(paramView instanceof RecyclerListView))
            continue;
          paramMotionEvent = ((RecyclerListView)paramView).getChildAt(j);
        }
        label304: int n = paramMotionEvent.getTop();
        int i1 = paramMotionEvent.getBottom();
        int i2 = paramMotionEvent.getLeft();
        int i3 = paramMotionEvent.getRight();
        if ((n <= m) && (i1 >= m) && (i2 <= k) && (i3 >= k))
          break label365;
        j += 1;
      }
      label365: boolean bool = false;
      if ((paramMotionEvent instanceof StickerEmojiCell))
        bool = true;
      while ((!bool) || (paramMotionEvent == this.currentStickerPreviewCell))
      {
        label391: return true;
        if ((paramMotionEvent instanceof StickerCell))
        {
          bool = true;
          continue;
        }
        if (!(paramMotionEvent instanceof ContextLinkCell))
          continue;
        bool = ((ContextLinkCell)paramMotionEvent).isSticker();
      }
      if ((this.currentStickerPreviewCell instanceof StickerEmojiCell))
      {
        ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(false);
        this.currentStickerPreviewCell = paramMotionEvent;
        setKeyboardHeight(paramInt);
        if (!(this.currentStickerPreviewCell instanceof StickerEmojiCell))
          break label551;
        open(((StickerEmojiCell)this.currentStickerPreviewCell).getSticker(), ((StickerEmojiCell)this.currentStickerPreviewCell).isRecent());
        ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(true);
      }
      while (true)
      {
        return true;
        if ((this.currentStickerPreviewCell instanceof StickerCell))
        {
          ((StickerCell)this.currentStickerPreviewCell).setScaled(false);
          break;
        }
        if (!(this.currentStickerPreviewCell instanceof ContextLinkCell))
          break;
        ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(false);
        break;
        label551: if ((this.currentStickerPreviewCell instanceof StickerCell))
        {
          open(((StickerCell)this.currentStickerPreviewCell).getSticker(), false);
          ((StickerCell)this.currentStickerPreviewCell).setScaled(true);
          continue;
        }
        if (!(this.currentStickerPreviewCell instanceof ContextLinkCell))
          continue;
        open(((ContextLinkCell)this.currentStickerPreviewCell).getDocument(), false);
        ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(true);
      }
      if (this.openStickerPreviewRunnable == null)
        break;
      if (paramMotionEvent.getAction() == 2)
      {
        if (Math.hypot(this.startX - paramMotionEvent.getX(), this.startY - paramMotionEvent.getY()) <= AndroidUtilities.dp(10.0F))
          break;
        AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
        this.openStickerPreviewRunnable = null;
        break;
      }
      AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
      this.openStickerPreviewRunnable = null;
      break;
      label710: i = 0;
    }
  }

  public void open(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    if ((this.parentActivity == null) || (paramDocument == null))
      return;
    if (textPaint == null)
    {
      textPaint = new TextPaint(1);
      textPaint.setTextSize(AndroidUtilities.dp(24.0F));
    }
    int i;
    label48: Object localObject1;
    label95: Object localObject2;
    if (paramBoolean)
    {
      i = 0;
      if (i < paramDocument.attributes.size())
      {
        localObject1 = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (((localObject1 instanceof TLRPC.TL_documentAttributeSticker)) && (((TLRPC.DocumentAttribute)localObject1).stickerset != null))
        {
          localObject1 = ((TLRPC.DocumentAttribute)localObject1).stickerset;
          localObject2 = localObject1;
          if (localObject1 != null)
          {
            localObject2 = localObject1;
            if (this.currentSet == localObject1);
          }
        }
      }
    }
    while (true)
    {
      try
      {
        if (this.visibleDialog == null)
          continue;
        this.visibleDialog.setOnDismissListener(null);
        this.visibleDialog.dismiss();
        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 2000L);
        localObject2 = localObject1;
        this.currentSet = localObject2;
        this.centerImage.setImage(paramDocument, null, paramDocument.thumb.location, null, "webp", true);
        this.stickerEmojiLayout = null;
        i = 0;
        if (i >= paramDocument.attributes.size())
          continue;
        localObject1 = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((!(localObject1 instanceof TLRPC.TL_documentAttributeSticker)) || (TextUtils.isEmpty(((TLRPC.DocumentAttribute)localObject1).alt)))
          continue;
        this.stickerEmojiLayout = new StaticLayout(Emoji.replaceEmoji(((TLRPC.DocumentAttribute)localObject1).alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0F), false), textPaint, AndroidUtilities.dp(100.0F), Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
        this.currentSticker = paramDocument;
        this.containerView.invalidate();
        if (this.isVisible)
          break;
        AndroidUtilities.lockOrientation(this.parentActivity);
      }
      catch (Exception localException)
      {
        try
        {
          if (this.windowView.getParent() == null)
            continue;
          ((WindowManager)this.parentActivity.getSystemService("window")).removeView(this.windowView);
          ((WindowManager)this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
          this.isVisible = true;
          this.showProgress = 0.0F;
          this.lastUpdateTime = System.currentTimeMillis();
          return;
          i += 1;
          break label48;
          localException = localException;
          FileLog.e(localException);
          continue;
          i += 1;
        }
        catch (Exception paramDocument)
        {
          FileLog.e(paramDocument);
          continue;
        }
      }
      localObject1 = null;
      break label95;
      Object localObject3 = null;
    }
  }

  public void reset()
  {
    if (this.openStickerPreviewRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
      this.openStickerPreviewRunnable = null;
    }
    if (this.currentStickerPreviewCell != null)
    {
      if (!(this.currentStickerPreviewCell instanceof StickerEmojiCell))
        break label53;
      ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(false);
    }
    while (true)
    {
      this.currentStickerPreviewCell = null;
      return;
      label53: if ((this.currentStickerPreviewCell instanceof StickerCell))
      {
        ((StickerCell)this.currentStickerPreviewCell).setScaled(false);
        continue;
      }
      if (!(this.currentStickerPreviewCell instanceof ContextLinkCell))
        continue;
      ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(false);
    }
  }

  public void setDelegate(StickerPreviewViewerDelegate paramStickerPreviewViewerDelegate)
  {
    this.delegate = paramStickerPreviewViewerDelegate;
  }

  public void setKeyboardHeight(int paramInt)
  {
    this.keyboardHeight = paramInt;
  }

  public void setParentActivity(Activity paramActivity)
  {
    if (this.parentActivity == paramActivity)
      return;
    this.parentActivity = paramActivity;
    this.windowView = new FrameLayout(paramActivity);
    this.windowView.setFocusable(true);
    this.windowView.setFocusableInTouchMode(true);
    if (Build.VERSION.SDK_INT >= 23)
      this.windowView.setFitsSystemWindows(true);
    this.containerView = new FrameLayoutDrawer(paramActivity);
    this.containerView.setFocusable(false);
    this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
    this.containerView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 6) || (paramMotionEvent.getAction() == 3))
          StickerPreviewViewer.this.close();
        return true;
      }
    });
    this.windowLayoutParams = new WindowManager.LayoutParams();
    this.windowLayoutParams.height = -1;
    this.windowLayoutParams.format = -3;
    this.windowLayoutParams.width = -1;
    this.windowLayoutParams.gravity = 48;
    this.windowLayoutParams.type = 99;
    if (Build.VERSION.SDK_INT >= 21);
    for (this.windowLayoutParams.flags = -2147483640; ; this.windowLayoutParams.flags = 8)
    {
      this.centerImage.setAspectFit(true);
      this.centerImage.setInvalidateAll(true);
      this.centerImage.setParentView(this.containerView);
      return;
    }
  }

  private class FrameLayoutDrawer extends FrameLayout
  {
    public FrameLayoutDrawer(Context arg2)
    {
      super();
      setWillNotDraw(false);
    }

    protected void onDraw(Canvas paramCanvas)
    {
      StickerPreviewViewer.getInstance().onDraw(paramCanvas);
    }
  }

  public static abstract interface StickerPreviewViewerDelegate
  {
    public abstract void openSet(TLRPC.InputStickerSet paramInputStickerSet);

    public abstract void sentSticker(TLRPC.Document paramDocument);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.StickerPreviewViewer
 * JD-Core Version:    0.6.0
 */