package org.vidogram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class DrawerProfileCell extends FrameLayout
{
  private BackupImageView avatarImageView;
  private Drawable cloudDrawable;
  private CloudView cloudView;
  private Integer currentColor;
  private Rect destRect = new Rect();
  private int lastCloudColor;
  private TextView nameTextView;
  private Paint paint = new Paint();
  private TextView phoneTextView;
  private ImageView shadowView;
  private Rect srcRect = new Rect();

  public DrawerProfileCell(Context paramContext)
  {
    super(paramContext);
    this.cloudDrawable = paramContext.getResources().getDrawable(2130837673);
    Drawable localDrawable = this.cloudDrawable;
    int i = Theme.getColor("chats_menuCloud");
    this.lastCloudColor = i;
    localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
    this.shadowView = new ImageView(paramContext);
    this.shadowView.setVisibility(4);
    this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
    this.shadowView.setImageResource(2130837650);
    addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0F));
    addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0F, 83, 16.0F, 0.0F, 0.0F, 67.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextSize(1, 15.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(3);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 76.0F, 28.0F));
    this.phoneTextView = new TextView(paramContext);
    this.phoneTextView.setTextSize(1, 13.0F);
    this.phoneTextView.setLines(1);
    this.phoneTextView.setMaxLines(1);
    this.phoneTextView.setSingleLine(true);
    this.phoneTextView.setGravity(3);
    addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0F, 83, 16.0F, 0.0F, 76.0F, 9.0F));
    this.cloudView = new CloudView(paramContext);
    addView(this.cloudView, LayoutHelper.createFrame(61, 61, 85));
  }

  public void invalidate()
  {
    super.invalidate();
    this.cloudView.invalidate();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    Object localObject = Theme.getCachedWallpaper();
    int i;
    if (Theme.hasThemeKey("chats_menuTopShadow"))
    {
      i = Theme.getColor("chats_menuTopShadow");
      if ((this.currentColor == null) || (this.currentColor.intValue() != i))
      {
        this.currentColor = Integer.valueOf(i);
        this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      }
      this.nameTextView.setTextColor(Theme.getColor("chats_menuName"));
      if ((!Theme.isCustomTheme()) || (localObject == null))
        break label317;
      this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhone"));
      this.shadowView.setVisibility(0);
      if (!(localObject instanceof ColorDrawable))
        break label155;
      ((Drawable)localObject).setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
      ((Drawable)localObject).draw(paramCanvas);
    }
    label155: 
    do
    {
      return;
      i = Theme.getServiceMessageColor() | 0xFF000000;
      break;
    }
    while (!(localObject instanceof BitmapDrawable));
    localObject = ((BitmapDrawable)localObject).getBitmap();
    float f1 = getMeasuredWidth() / ((Bitmap)localObject).getWidth();
    float f2 = getMeasuredHeight() / ((Bitmap)localObject).getHeight();
    if (f1 < f2)
      f1 = f2;
    while (true)
    {
      i = (int)(getMeasuredWidth() / f1);
      int j = (int)(getMeasuredHeight() / f1);
      int k = (((Bitmap)localObject).getWidth() - i) / 2;
      int m = (((Bitmap)localObject).getHeight() - j) / 2;
      this.srcRect.set(k, m, i + k, j + m);
      this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
      try
      {
        paramCanvas.drawBitmap((Bitmap)localObject, this.srcRect, this.destRect, this.paint);
        return;
      }
      catch (java.lang.Throwable paramCanvas)
      {
        FileLog.e(paramCanvas);
        return;
      }
    }
    label317: this.shadowView.setVisibility(4);
    this.phoneTextView.setTextColor(Theme.getColor("chats_menuPhoneCats"));
    super.onDraw(paramCanvas);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F) + AndroidUtilities.statusBarHeight, 1073741824));
      return;
    }
    try
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0F), 1073741824));
      return;
    }
    catch (Exception localException)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(148.0F));
      FileLog.e(localException);
    }
  }

  public void setUser(TLRPC.User paramUser)
  {
    if (paramUser == null)
      return;
    TLRPC.FileLocation localFileLocation = null;
    if (paramUser.photo != null)
      localFileLocation = paramUser.photo.photo_small;
    this.nameTextView.setText(UserObject.getUserName(paramUser));
    this.phoneTextView.setText(b.a().e("+" + paramUser.phone));
    paramUser = new AvatarDrawable(paramUser);
    paramUser.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
    this.avatarImageView.setImage(localFileLocation, "50_50", paramUser);
  }

  private class CloudView extends View
  {
    private Paint paint = new Paint(1);

    public CloudView(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      if ((Theme.isCustomTheme()) && (Theme.getCachedWallpaper() != null))
        this.paint.setColor(Theme.getServiceMessageColor());
      while (true)
      {
        int i = Theme.getColor("chats_menuCloud");
        if (DrawerProfileCell.this.lastCloudColor != i)
          DrawerProfileCell.this.cloudDrawable.setColorFilter(new PorterDuffColorFilter(DrawerProfileCell.access$002(DrawerProfileCell.this, Theme.getColor("chats_menuCloud")), PorterDuff.Mode.MULTIPLY));
        paramCanvas.drawCircle(getMeasuredWidth() / 2.0F, getMeasuredHeight() / 2.0F, AndroidUtilities.dp(34.0F) / 2.0F, this.paint);
        i = (getMeasuredWidth() - AndroidUtilities.dp(33.0F)) / 2;
        int j = (getMeasuredHeight() - AndroidUtilities.dp(33.0F)) / 2;
        DrawerProfileCell.this.cloudDrawable.setBounds(i, j, AndroidUtilities.dp(33.0F) + i, AndroidUtilities.dp(33.0F) + j);
        DrawerProfileCell.this.cloudDrawable.draw(paramCanvas);
        return;
        this.paint.setColor(Theme.getColor("chats_menuCloudBackgroundCats"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.DrawerProfileCell
 * JD-Core Version:    0.6.0
 */