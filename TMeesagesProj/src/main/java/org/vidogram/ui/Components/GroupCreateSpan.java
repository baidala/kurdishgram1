package org.vidogram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.Theme;

public class GroupCreateSpan extends View
{
  private static Paint backPaint;
  private static TextPaint textPaint = new TextPaint(1);
  private AvatarDrawable avatarDrawable;
  private int[] colors = new int[6];
  private Drawable deleteDrawable = getResources().getDrawable(2130837703);
  private boolean deleting;
  private ImageReceiver imageReceiver;
  private long lastUpdateTime;
  private StaticLayout nameLayout;
  private float progress;
  private RectF rect = new RectF();
  private int textWidth;
  private float textX;
  private int uid;

  static
  {
    backPaint = new Paint(1);
  }

  public GroupCreateSpan(Context paramContext, TLRPC.User paramUser)
  {
    super(paramContext);
    textPaint.setTextSize(AndroidUtilities.dp(14.0F));
    this.avatarDrawable = new AvatarDrawable();
    this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0F));
    this.avatarDrawable.setInfo(paramUser);
    this.imageReceiver = new ImageReceiver();
    this.imageReceiver.setRoundRadius(AndroidUtilities.dp(16.0F));
    this.imageReceiver.setParentView(this);
    this.imageReceiver.setImageCoords(0, 0, AndroidUtilities.dp(32.0F), AndroidUtilities.dp(32.0F));
    this.uid = paramUser.id;
    if (AndroidUtilities.isTablet())
    {
      int i = AndroidUtilities.dp(366.0F) / 2;
      this.nameLayout = new StaticLayout(TextUtils.ellipsize(UserObject.getFirstName(paramUser).replace('\n', ' '), textPaint, i, TextUtils.TruncateAt.END), textPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.nameLayout.getLineCount() > 0)
      {
        this.textWidth = (int)Math.ceil(this.nameLayout.getLineWidth(0));
        this.textX = (-this.nameLayout.getLineLeft(0));
      }
      if (paramUser.photo == null)
        break label304;
    }
    label304: for (paramContext = paramUser.photo.photo_small; ; paramContext = null)
    {
      this.imageReceiver.setImage(paramContext, null, "50_50", this.avatarDrawable, null, null, 0, null, true);
      updateColors();
      return;
      int j = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0F)) / 2;
      break;
    }
  }

  public void cancelDeleteAnimation()
  {
    if (!this.deleting)
      return;
    this.deleting = false;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }

  public int getUid()
  {
    return this.uid;
  }

  public boolean isDeleting()
  {
    return this.deleting;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    long l1;
    if (((this.deleting) && (this.progress != 1.0F)) || ((!this.deleting) && (this.progress != 0.0F)))
    {
      long l2 = System.currentTimeMillis() - this.lastUpdateTime;
      if (l2 >= 0L)
      {
        l1 = l2;
        if (l2 <= 17L);
      }
      else
      {
        l1 = 17L;
      }
      if (!this.deleting)
        break label444;
      float f = this.progress;
      this.progress = ((float)l1 / 120.0F + f);
      if (this.progress >= 1.0F)
        this.progress = 1.0F;
    }
    while (true)
    {
      invalidate();
      paramCanvas.save();
      this.rect.set(0.0F, 0.0F, getMeasuredWidth(), AndroidUtilities.dp(32.0F));
      backPaint.setColor(Color.argb(255, this.colors[0] + (int)((this.colors[1] - this.colors[0]) * this.progress), this.colors[2] + (int)((this.colors[3] - this.colors[2]) * this.progress), this.colors[4] + (int)((this.colors[5] - this.colors[4]) * this.progress)));
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), backPaint);
      this.imageReceiver.draw(paramCanvas);
      if (this.progress != 0.0F)
      {
        backPaint.setColor(this.avatarDrawable.getColor());
        backPaint.setAlpha((int)(255.0F * this.progress));
        paramCanvas.drawCircle(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), backPaint);
        paramCanvas.save();
        paramCanvas.rotate(45.0F * (1.0F - this.progress), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F));
        this.deleteDrawable.setBounds(AndroidUtilities.dp(11.0F), AndroidUtilities.dp(11.0F), AndroidUtilities.dp(21.0F), AndroidUtilities.dp(21.0F));
        this.deleteDrawable.setAlpha((int)(255.0F * this.progress));
        this.deleteDrawable.draw(paramCanvas);
        paramCanvas.restore();
      }
      paramCanvas.translate(this.textX + AndroidUtilities.dp(41.0F), AndroidUtilities.dp(8.0F));
      this.nameLayout.draw(paramCanvas);
      paramCanvas.restore();
      return;
      label444: this.progress -= (float)l1 / 120.0F;
      if (this.progress >= 0.0F)
        continue;
      this.progress = 0.0F;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(AndroidUtilities.dp(57.0F) + this.textWidth, AndroidUtilities.dp(32.0F));
  }

  public void startDeleteAnimation()
  {
    if (this.deleting)
      return;
    this.deleting = true;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }

  public void updateColors()
  {
    int i = Theme.getColor("avatar_backgroundGroupCreateSpanBlue");
    int j = Theme.getColor("groupcreate_spanBackground");
    int k = Theme.getColor("groupcreate_spanText");
    this.colors[0] = Color.red(j);
    this.colors[1] = Color.red(i);
    this.colors[2] = Color.green(j);
    this.colors[3] = Color.green(i);
    this.colors[4] = Color.blue(j);
    this.colors[5] = Color.blue(i);
    textPaint.setColor(k);
    this.deleteDrawable.setColorFilter(new PorterDuffColorFilter(k, PorterDuff.Mode.MULTIPLY));
    backPaint.setColor(j);
    this.avatarDrawable.setColor(AvatarDrawable.getColorForId(5));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.GroupCreateSpan
 * JD-Core Version:    0.6.0
 */