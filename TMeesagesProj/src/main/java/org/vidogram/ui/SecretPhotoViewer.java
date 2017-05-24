package org.vidogram.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.PhotoSize;

public class SecretPhotoViewer
  implements NotificationCenter.NotificationCenterDelegate
{

  @SuppressLint({"StaticFieldLeak"})
  private static volatile SecretPhotoViewer Instance = null;
  private ImageReceiver centerImage = new ImageReceiver();
  private FrameLayoutDrawer containerView;
  private MessageObject currentMessageObject = null;
  private boolean isVisible = false;
  private Activity parentActivity;
  private SecretDeleteTimer secretDeleteTimer;
  private WindowManager.LayoutParams windowLayoutParams;
  private FrameLayout windowView;

  public static SecretPhotoViewer getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        SecretPhotoViewer localSecretPhotoViewer = Instance;
        localObject1 = localSecretPhotoViewer;
        if (localSecretPhotoViewer == null)
        {
          localObject1 = new SecretPhotoViewer();
          Instance = (SecretPhotoViewer)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (SecretPhotoViewer)localObject2;
  }

  private void onDraw(Canvas paramCanvas)
  {
    paramCanvas.save();
    paramCanvas.translate(this.containerView.getWidth() / 2, this.containerView.getHeight() / 2);
    Bitmap localBitmap = this.centerImage.getBitmap();
    int j;
    int i;
    float f1;
    if (localBitmap != null)
    {
      j = localBitmap.getWidth();
      i = localBitmap.getHeight();
      f1 = this.containerView.getWidth() / j;
      float f2 = this.containerView.getHeight() / i;
      if (f1 <= f2)
        break label142;
      f1 = f2;
    }
    label142: 
    while (true)
    {
      j = (int)(j * f1);
      i = (int)(f1 * i);
      this.centerImage.setImageCoords(-j / 2, -i / 2, j, i);
      this.centerImage.draw(paramCanvas);
      paramCanvas.restore();
      return;
    }
  }

  public void closePhoto()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
    if (this.parentActivity == null);
    while (true)
    {
      return;
      this.currentMessageObject = null;
      this.isVisible = false;
      AndroidUtilities.unlockOrientation(this.parentActivity);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          SecretPhotoViewer.this.centerImage.setImageBitmap((Bitmap)null);
        }
      });
      try
      {
        if (this.windowView.getParent() == null)
          continue;
        ((WindowManager)this.parentActivity.getSystemService("window")).removeView(this.windowView);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
  }

  public void destroyPhotoViewer()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
    this.isVisible = false;
    this.currentMessageObject = null;
    if ((this.parentActivity == null) || (this.windowView == null))
      return;
    try
    {
      if (this.windowView.getParent() != null)
        ((WindowManager)this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
      this.windowView = null;
      Instance = null;
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.messagesDeleted)
      if (this.currentMessageObject == null)
        break label14;
    while (true)
    {
      label14: return;
      if ((((Integer)paramArrayOfObject[1]).intValue() != 0) || (!((ArrayList)paramArrayOfObject[0]).contains(Integer.valueOf(this.currentMessageObject.getId()))))
        continue;
      closePhoto();
      return;
      if ((paramInt != NotificationCenter.didCreatedNewDeleteTask) || (this.currentMessageObject == null) || (this.secretDeleteTimer == null))
        break;
      paramArrayOfObject = (SparseArray)paramArrayOfObject[0];
      paramInt = 0;
      while (paramInt < paramArrayOfObject.size())
      {
        int i = paramArrayOfObject.keyAt(paramInt);
        Iterator localIterator = ((ArrayList)paramArrayOfObject.get(i)).iterator();
        while (localIterator.hasNext())
        {
          Integer localInteger = (Integer)localIterator.next();
          if (this.currentMessageObject.getId() != localInteger.intValue())
            continue;
          this.currentMessageObject.messageOwner.destroyTime = i;
          this.secretDeleteTimer.invalidate();
          return;
        }
        paramInt += 1;
      }
    }
  }

  public boolean isVisible()
  {
    return this.isVisible;
  }

  public void openPhoto(MessageObject paramMessageObject)
  {
    if ((this.parentActivity == null) || (paramMessageObject == null) || (paramMessageObject.messageOwner.media == null) || (paramMessageObject.messageOwner.media.photo == null))
      return;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
    TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
    int j = localPhotoSize.size;
    int i = j;
    if (j == 0)
      i = -1;
    BitmapDrawable localBitmapDrawable2 = ImageLoader.getInstance().getImageFromMemory(localPhotoSize.location, null, null);
    File localFile;
    Object localObject;
    if (localBitmapDrawable2 == null)
    {
      localFile = FileLoader.getPathToAttach(localPhotoSize);
      if (Build.VERSION.SDK_INT >= 21)
        break label374;
      localObject = new BitmapFactory.Options();
      ((BitmapFactory.Options)localObject).inDither = true;
      ((BitmapFactory.Options)localObject).inPreferredConfig = Bitmap.Config.ARGB_8888;
      ((BitmapFactory.Options)localObject).inPurgeable = true;
      ((BitmapFactory.Options)localObject).inSampleSize = 1;
      ((BitmapFactory.Options)localObject).inMutable = true;
    }
    while (true)
    {
      try
      {
        localObject = BitmapFactory.decodeFile(localFile.getAbsolutePath(), (BitmapFactory.Options)localObject);
        if (localObject != null)
        {
          localObject = new BitmapDrawable((Bitmap)localObject);
          ImageLoader.getInstance().putImageToCache((BitmapDrawable)localObject, localPhotoSize.location.volume_id + "_" + localPhotoSize.location.local_id);
          if (localObject == null)
            continue;
          this.centerImage.setImageBitmap((Drawable)localObject);
          this.currentMessageObject = paramMessageObject;
          AndroidUtilities.lockOrientation(this.parentActivity);
        }
      }
      catch (Throwable localBitmapDrawable1)
      {
        try
        {
          if (this.windowView.getParent() == null)
            continue;
          ((WindowManager)this.parentActivity.getSystemService("window")).removeView(this.windowView);
          ((WindowManager)this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
          this.secretDeleteTimer.invalidate();
          this.isVisible = true;
          return;
          localThrowable = localThrowable;
          FileLog.e(localThrowable);
          localBitmapDrawable1 = null;
          continue;
          this.centerImage.setImage(localPhotoSize.location, null, null, i, null, false);
        }
        catch (Exception paramMessageObject)
        {
          FileLog.e(paramMessageObject);
          continue;
        }
      }
      BitmapDrawable localBitmapDrawable1 = localBitmapDrawable2;
      continue;
      label374: localBitmapDrawable1 = null;
    }
  }

  public void setParentActivity(Activity paramActivity)
  {
    if (this.parentActivity == paramActivity)
      return;
    this.parentActivity = paramActivity;
    this.windowView = new FrameLayout(paramActivity);
    this.windowView.setBackgroundColor(-16777216);
    this.windowView.setFocusable(true);
    this.windowView.setFocusableInTouchMode(true);
    if (Build.VERSION.SDK_INT >= 23)
      this.windowView.setFitsSystemWindows(true);
    this.containerView = new FrameLayoutDrawer(paramActivity);
    this.containerView.setFocusable(false);
    this.windowView.addView(this.containerView);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.containerView.getLayoutParams();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    localLayoutParams.gravity = 51;
    this.containerView.setLayoutParams(localLayoutParams);
    this.containerView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 6) || (paramMotionEvent.getAction() == 3))
          SecretPhotoViewer.this.closePhoto();
        return true;
      }
    });
    this.secretDeleteTimer = new SecretDeleteTimer(paramActivity);
    this.containerView.addView(this.secretDeleteTimer);
    paramActivity = (FrameLayout.LayoutParams)this.secretDeleteTimer.getLayoutParams();
    paramActivity.gravity = 53;
    paramActivity.width = AndroidUtilities.dp(100.0F);
    paramActivity.height = AndroidUtilities.dp(32.0F);
    paramActivity.rightMargin = AndroidUtilities.dp(19.0F);
    paramActivity.topMargin = AndroidUtilities.dp(19.0F);
    this.secretDeleteTimer.setLayoutParams(paramActivity);
    this.windowLayoutParams = new WindowManager.LayoutParams();
    this.windowLayoutParams.height = -1;
    this.windowLayoutParams.format = -3;
    this.windowLayoutParams.width = -1;
    this.windowLayoutParams.gravity = 48;
    this.windowLayoutParams.type = 99;
    this.windowLayoutParams.flags = 8;
    this.centerImage.setParentView(this.containerView);
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
      SecretPhotoViewer.getInstance().onDraw(paramCanvas);
    }
  }

  private class SecretDeleteTimer extends FrameLayout
  {
    private String currentInfoString;
    private Paint deleteProgressPaint;
    private RectF deleteProgressRect = new RectF();
    private Drawable drawable = null;
    private StaticLayout infoLayout = null;
    private TextPaint infoPaint = null;
    private int infoWidth;

    public SecretDeleteTimer(Context arg2)
    {
      super();
      setWillNotDraw(false);
      this.infoPaint = new TextPaint(1);
      this.infoPaint.setTextSize(AndroidUtilities.dp(15.0F));
      this.infoPaint.setColor(-1);
      this.deleteProgressPaint = new Paint(1);
      this.deleteProgressPaint.setColor(-1644826);
      this.drawable = getResources().getDrawable(2130837671);
    }

    private void updateSecretTimeText()
    {
      if (SecretPhotoViewer.this.currentMessageObject == null);
      String str;
      do
      {
        return;
        str = SecretPhotoViewer.this.currentMessageObject.getSecretTimeString();
      }
      while ((str == null) || ((this.currentInfoString != null) && (this.currentInfoString.equals(str))));
      this.currentInfoString = str;
      this.infoWidth = (int)Math.ceil(this.infoPaint.measureText(this.currentInfoString));
      this.infoLayout = new StaticLayout(TextUtils.ellipsize(this.currentInfoString, this.infoPaint, this.infoWidth, TextUtils.TruncateAt.END), this.infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      invalidate();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if ((SecretPhotoViewer.this.currentMessageObject == null) || (SecretPhotoViewer.this.currentMessageObject.messageOwner.destroyTime == 0));
      do
      {
        return;
        if (this.drawable != null)
        {
          this.drawable.setBounds(getMeasuredWidth() - AndroidUtilities.dp(32.0F), 0, getMeasuredWidth(), AndroidUtilities.dp(32.0F));
          this.drawable.draw(paramCanvas);
        }
        long l1 = System.currentTimeMillis();
        long l2 = ConnectionsManager.getInstance().getTimeDifference() * 1000;
        float f = (float)Math.max(0L, SecretPhotoViewer.this.currentMessageObject.messageOwner.destroyTime * 1000L - (l1 + l2)) / (SecretPhotoViewer.this.currentMessageObject.messageOwner.ttl * 1000.0F);
        paramCanvas.drawArc(this.deleteProgressRect, -90.0F, -360.0F * f, true, this.deleteProgressPaint);
        if (f != 0.0F)
        {
          int i = AndroidUtilities.dp(2.0F);
          invalidate((int)this.deleteProgressRect.left - i, (int)this.deleteProgressRect.top - i, (int)this.deleteProgressRect.right + i * 2, i * 2 + (int)this.deleteProgressRect.bottom);
        }
        updateSecretTimeText();
      }
      while (this.infoLayout == null);
      paramCanvas.save();
      paramCanvas.translate(getMeasuredWidth() - AndroidUtilities.dp(38.0F) - this.infoWidth, AndroidUtilities.dp(7.0F));
      this.infoLayout.draw(paramCanvas);
      paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      this.deleteProgressRect.set(getMeasuredWidth() - AndroidUtilities.dp(30.0F), AndroidUtilities.dp(2.0F), getMeasuredWidth() - AndroidUtilities.dp(2.0F), AndroidUtilities.dp(30.0F));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.SecretPhotoViewer
 * JD-Core Version:    0.6.0
 */