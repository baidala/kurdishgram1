package org.vidogram.VidogramUi.a;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import itman.Vidofilm.a.b;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.BaseCell;
import org.vidogram.ui.Components.AvatarDrawable;

public class a extends BaseCell
{
  public boolean a = false;
  private Drawable b;
  private int c = AndroidUtilities.dp(16.0F);
  private TLRPC.User d = null;
  private b e;
  private int f;
  private int g = AndroidUtilities.dp(17.0F);
  private StaticLayout h;
  private int i;
  private StaticLayout j;
  private int k = AndroidUtilities.dp(40.0F);
  private int l;
  private StaticLayout m;
  private int n = AndroidUtilities.dp(10.0F);
  private int o;
  private int p = AndroidUtilities.dp(39.0F);
  private int q;
  private boolean r;
  private ImageReceiver s;
  private AvatarDrawable t;

  public a(Context paramContext)
  {
    super(paramContext);
    Theme.createDialogsResources(paramContext);
    this.s = new ImageReceiver(this);
    this.s.setRoundRadius(AndroidUtilities.dp(26.0F));
    this.t = new AvatarDrawable();
  }

  private TLRPC.User b(int paramInt)
  {
    Semaphore localSemaphore;
    if (paramInt != 0)
    {
      this.d = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
      if (this.d == null)
      {
        localSemaphore = new Semaphore(0);
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramInt, localSemaphore)
        {
          public void run()
          {
            a.a(a.this, MessagesStorage.getInstance().getUser(this.a));
            this.b.release();
          }
        });
      }
    }
    try
    {
      localSemaphore.acquire();
      if (this.d != null)
        MessagesController.getInstance().putUser(this.d, true);
      MessagesController.getInstance().loadPeerSettings(this.d, null);
      return this.d;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e("tmessages : " + localException);
    }
  }

  public void a()
  {
    TextPaint localTextPaint2 = Theme.dialogs_namePaint;
    Object localObject = Theme.dialogs_timePaint;
    TextPaint localTextPaint1 = Theme.dialogs_timePaint;
    LocaleController.getInstance();
    int i2 = (int)Math.ceil(((TextPaint)localObject).measureText(LocaleController.stringForMessageListDate(this.e.h())));
    LocaleController.getInstance();
    this.h = new StaticLayout(LocaleController.stringForMessageListDate(this.e.h()), (TextPaint)localObject, i2, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    if (!LocaleController.isRTL)
      this.f = (getMeasuredWidth() - AndroidUtilities.dp(15.0F) - i2);
    while (true)
    {
      label116: String str;
      label161: int i1;
      if (!LocaleController.isRTL)
      {
        this.i = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        if (Integer.parseInt(this.e.f()) != UserConfig.getCurrentUser().id)
          break label735;
        if (this.d == null)
          break label723;
        str = UserObject.getUserName(b(Integer.parseInt(this.e.b())));
        localObject = str;
        if (str.length() == 0)
          localObject = this.e.a();
        if (LocaleController.isRTL)
          break label797;
        i1 = getMeasuredWidth() - this.i - AndroidUtilities.dp(14.0F) - i2;
        label209: i1 = Math.max(AndroidUtilities.dp(12.0F), i1);
      }
      try
      {
        this.j = new StaticLayout(TextUtils.ellipsize(((String)localObject).replace('\n', ' '), localTextPaint2, i1 - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), localTextPaint2, i1, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        i2 = getMeasuredWidth();
        int i3 = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 16);
        if (!LocaleController.isRTL)
        {
          this.l = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
          if (AndroidUtilities.isTablet())
          {
            f1 = 13.0F;
            this.o = AndroidUtilities.dp(f1);
            this.s.setImageCoords(this.o, this.n, AndroidUtilities.dp(52.0F), AndroidUtilities.dp(52.0F));
            this.b = getResources().getDrawable(itman.Vidofilm.d.a.a(ApplicationLoader.applicationContext).a(this.e.g(), this.e.f(), UserConfig.getClientUserId()));
            if (LocaleController.isRTL)
              break label903;
            this.q = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            this.l = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + this.c);
            i2 = Math.max(AndroidUtilities.dp(12.0F), i2 - i3);
            localObject = TextUtils.ellipsize(String.format("%02d:%02d", new Object[] { Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(this.e.i())), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(this.e.i()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this.e.i()))) }), localTextPaint1, i2 - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END);
          }
        }
      }
      catch (Exception localException2)
      {
        try
        {
          float f1;
          while (true)
          {
            this.m = new StaticLayout((CharSequence)localObject, localTextPaint1, i2, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            if (!LocaleController.isRTL)
              break label955;
            if ((this.j != null) && (this.j.getLineCount() > 0))
            {
              f1 = this.j.getLineLeft(0);
              d1 = Math.ceil(this.j.getLineWidth(0));
              if ((f1 == 0.0F) && (d1 < i1))
                this.i = (int)(this.i + (i1 - d1));
            }
            if ((this.m != null) && (this.m.getLineCount() > 0) && (this.m.getLineLeft(0) == 0.0F))
            {
              d1 = Math.ceil(this.m.getLineWidth(0));
              if (d1 < i2)
              {
                double d2 = this.l;
                this.l = (int)(i2 - d1 + d2);
              }
            }
            return;
            this.f = AndroidUtilities.dp(15.0F);
            break;
            this.i = AndroidUtilities.dp(14.0F);
            break label116;
            label723: str = this.e.a();
            break label161;
            label735: if (this.d != null);
            for (str = UserObject.getUserName(b(Integer.parseInt(this.e.f()))); ; str = this.e.e())
            {
              localObject = str;
              if (str.length() != 0)
                break;
              localObject = this.e.e();
              break;
            }
            label797: i1 = getMeasuredWidth() - this.i - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - i2;
            this.i += i2;
            break label209;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
            continue;
            f1 = 9.0F;
          }
          this.l = AndroidUtilities.dp(16.0F);
          int i4 = getMeasuredWidth();
          if (AndroidUtilities.isTablet())
            f1 = 65.0F;
          while (true)
          {
            this.o = (i4 - AndroidUtilities.dp(f1));
            break;
            f1 = 61.0F;
          }
          label903: this.q = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - this.c);
          this.l = (AndroidUtilities.dp(12.0F) - this.c);
        }
        catch (Exception localException2)
        {
          double d1;
          label955: 
          do
          {
            do
            {
              while (true)
                FileLog.e("tmessages", localException2);
              if ((this.j == null) || (this.j.getLineCount() <= 0) || (this.j.getLineRight(0) != i1))
                continue;
              d1 = Math.ceil(this.j.getLineWidth(0));
              if (d1 >= i1)
                continue;
              this.i = (int)(this.i - (i1 - d1));
            }
            while ((this.m == null) || (this.m.getLineCount() <= 0) || (this.m.getLineRight(0) != i2));
            d1 = Math.ceil(this.m.getLineWidth(0));
          }
          while (d1 >= i2);
          this.l = (int)(this.l - (i2 - d1));
        }
      }
    }
  }

  public void a(int paramInt)
  {
    TLRPC.FileLocation localFileLocation;
    if (Integer.parseInt(this.e.f()) == UserConfig.getCurrentUser().id)
    {
      this.d = b(Integer.parseInt(this.e.b()));
      if (this.d == null)
        break label149;
      if (this.d.photo == null)
        break label144;
      localFileLocation = this.d.photo.photo_small;
      label65: this.t.setInfo(this.d);
    }
    while (true)
    {
      this.s.setImage(localFileLocation, "50_50", this.t, null, false);
      if ((getMeasuredWidth() != 0) || (getMeasuredHeight() != 0))
        a();
      while (true)
      {
        invalidate();
        return;
        this.d = b(Integer.parseInt(this.e.f()));
        break;
        requestLayout();
      }
      label144: localFileLocation = null;
      break label65;
      label149: localFileLocation = null;
    }
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.s.onAttachedToWindow();
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.s.onDetachedFromWindow();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.r)
      paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
    if (this.j != null)
    {
      paramCanvas.save();
      paramCanvas.translate(this.i, AndroidUtilities.dp(13.0F));
      this.j.draw(paramCanvas);
      paramCanvas.restore();
    }
    paramCanvas.save();
    paramCanvas.translate(this.f, this.g);
    this.h.draw(paramCanvas);
    paramCanvas.restore();
    if (this.m != null)
    {
      paramCanvas.save();
      paramCanvas.translate(this.l, this.k);
    }
    try
    {
      this.m.draw(paramCanvas);
      paramCanvas.restore();
      setDrawableBounds(this.b, this.q, this.p, this.c, this.c);
      this.b.draw(paramCanvas);
      if (this.a)
      {
        if (LocaleController.isRTL)
          paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
      }
      else
      {
        this.s.draw(paramCanvas);
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        FileLog.e("tmessages", localException);
        continue;
        paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
      }
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramBoolean)
      a();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    int i1 = AndroidUtilities.dp(72.0F);
    if (this.a);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i1);
      return;
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2)))
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    return super.onTouchEvent(paramMotionEvent);
  }

  public void setDialog(b paramb)
  {
    this.e = paramb;
    a(0);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.a.a
 * JD-Core Version:    0.6.0
 */