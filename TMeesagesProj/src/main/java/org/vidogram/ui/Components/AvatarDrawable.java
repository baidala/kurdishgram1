package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.Theme;

public class AvatarDrawable extends Drawable
{
  private int color;
  private boolean drawBrodcast;
  private boolean drawPhoto;
  private boolean isProfile;
  private TextPaint namePaint = new TextPaint(1);
  private StringBuilder stringBuilder = new StringBuilder(5);
  private float textHeight;
  private StaticLayout textLayout;
  private float textLeft;
  private float textWidth;

  public AvatarDrawable()
  {
    this.namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.namePaint.setTextSize(AndroidUtilities.dp(18.0F));
  }

  public AvatarDrawable(TLRPC.Chat paramChat)
  {
    this(paramChat, false);
  }

  public AvatarDrawable(TLRPC.Chat paramChat, boolean paramBoolean)
  {
    this();
    this.isProfile = paramBoolean;
    int i;
    String str;
    if (paramChat != null)
    {
      i = paramChat.id;
      str = paramChat.title;
      if (paramChat.id >= 0)
        break label44;
    }
    label44: for (paramBoolean = true; ; paramBoolean = false)
    {
      setInfo(i, str, null, paramBoolean, null);
      return;
    }
  }

  public AvatarDrawable(TLRPC.User paramUser)
  {
    this(paramUser, false);
  }

  public AvatarDrawable(TLRPC.User paramUser, boolean paramBoolean)
  {
    this();
    this.isProfile = paramBoolean;
    if (paramUser != null)
      setInfo(paramUser.id, paramUser.first_name, paramUser.last_name, false, null);
  }

  public static int getButtonColorForId(int paramInt)
  {
    return Theme.getColor(Theme.keys_avatar_actionBarSelector[getColorIndex(paramInt)]);
  }

  public static int getColorForId(int paramInt)
  {
    return Theme.getColor(Theme.keys_avatar_background[getColorIndex(paramInt)]);
  }

  public static int getColorIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < 8))
      return paramInt;
    return Math.abs(paramInt % Theme.keys_avatar_background.length);
  }

  public static int getIconColorForId(int paramInt)
  {
    return Theme.getColor(Theme.keys_avatar_actionBarIcon[getColorIndex(paramInt)]);
  }

  public static int getNameColorForId(int paramInt)
  {
    return Theme.getColor(Theme.keys_avatar_nameInMessage[getColorIndex(paramInt)]);
  }

  public static int getProfileBackColorForId(int paramInt)
  {
    return Theme.getColor(Theme.keys_avatar_backgroundActionBar[getColorIndex(paramInt)]);
  }

  public static int getProfileColorForId(int paramInt)
  {
    return Theme.getColor(Theme.keys_avatar_backgroundInProfile[getColorIndex(paramInt)]);
  }

  public static int getProfileTextColorForId(int paramInt)
  {
    return Theme.getColor(Theme.keys_avatar_subtitleInProfile[getColorIndex(paramInt)]);
  }

  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    if (localRect == null)
      return;
    int i = localRect.width();
    this.namePaint.setColor(Theme.getColor("avatar_text"));
    Theme.avatar_backgroundPaint.setColor(this.color);
    paramCanvas.save();
    paramCanvas.translate(localRect.left, localRect.top);
    paramCanvas.drawCircle(i / 2, i / 2, i / 2, Theme.avatar_backgroundPaint);
    int j;
    if ((this.drawBrodcast) && (Theme.avatar_broadcastDrawable != null))
    {
      j = (i - Theme.avatar_broadcastDrawable.getIntrinsicWidth()) / 2;
      i = (i - Theme.avatar_broadcastDrawable.getIntrinsicHeight()) / 2;
      Theme.avatar_broadcastDrawable.setBounds(j, i, Theme.avatar_broadcastDrawable.getIntrinsicWidth() + j, Theme.avatar_broadcastDrawable.getIntrinsicHeight() + i);
      Theme.avatar_broadcastDrawable.draw(paramCanvas);
    }
    while (true)
    {
      paramCanvas.restore();
      return;
      if (this.textLayout != null)
      {
        paramCanvas.translate((i - this.textWidth) / 2.0F - this.textLeft, (i - this.textHeight) / 2.0F);
        this.textLayout.draw(paramCanvas);
        continue;
      }
      if ((!this.drawPhoto) || (Theme.avatar_photoDrawable == null))
        continue;
      j = (i - Theme.avatar_photoDrawable.getIntrinsicWidth()) / 2;
      i = (i - Theme.avatar_photoDrawable.getIntrinsicHeight()) / 2;
      Theme.avatar_photoDrawable.setBounds(j, i, Theme.avatar_photoDrawable.getIntrinsicWidth() + j, Theme.avatar_photoDrawable.getIntrinsicHeight() + i);
      Theme.avatar_photoDrawable.draw(paramCanvas);
    }
  }

  public int getColor()
  {
    return this.color;
  }

  public int getIntrinsicHeight()
  {
    return 0;
  }

  public int getIntrinsicWidth()
  {
    return 0;
  }

  public int getOpacity()
  {
    return -2;
  }

  public void setAlpha(int paramInt)
  {
  }

  public void setColor(int paramInt)
  {
    this.color = paramInt;
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
  }

  public void setDrawPhoto(boolean paramBoolean)
  {
    this.drawPhoto = paramBoolean;
  }

  public void setInfo(int paramInt, String paramString1, String paramString2, boolean paramBoolean)
  {
    setInfo(paramInt, paramString1, paramString2, paramBoolean, null);
  }

  public void setInfo(int paramInt, String paramString1, String paramString2, boolean paramBoolean, String paramString3)
  {
    if (this.isProfile)
      this.color = getProfileColorForId(paramInt);
    while (true)
    {
      this.drawBrodcast = paramBoolean;
      String str2;
      String str1;
      if (paramString1 != null)
      {
        str2 = paramString1;
        str1 = paramString2;
        if (paramString1.length() != 0);
      }
      else
      {
        str1 = null;
        str2 = paramString2;
      }
      this.stringBuilder.setLength(0);
      if (paramString3 != null)
      {
        this.stringBuilder.append(paramString3);
        label67: if (this.stringBuilder.length() <= 0)
          break;
        paramString1 = this.stringBuilder.toString().toUpperCase();
      }
      else
      {
        try
        {
          this.textLayout = new StaticLayout(paramString1, this.namePaint, AndroidUtilities.dp(100.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          if (this.textLayout.getLineCount() > 0)
          {
            this.textLeft = this.textLayout.getLineLeft(0);
            this.textWidth = this.textLayout.getLineWidth(0);
            this.textHeight = this.textLayout.getLineBottom(0);
          }
          return;
          this.color = getColorForId(paramInt);
          continue;
          if ((str2 != null) && (str2.length() > 0))
            this.stringBuilder.appendCodePoint(str2.codePointAt(0));
          if ((str1 != null) && (str1.length() > 0))
          {
            paramInt = str1.length() - 1;
            paramString1 = null;
            while (true)
            {
              if ((paramInt < 0) || ((paramString1 != null) && (str1.charAt(paramInt) == ' ')))
              {
                if (Build.VERSION.SDK_INT >= 16)
                  this.stringBuilder.append("‌");
                this.stringBuilder.appendCodePoint(paramString1.intValue());
                break;
              }
              paramString1 = Integer.valueOf(str1.codePointAt(paramInt));
              paramInt -= 1;
            }
          }
          if ((str2 == null) || (str2.length() <= 0))
            break label67;
          paramInt = str2.length() - 1;
          while (paramInt >= 0)
          {
            if ((str2.charAt(paramInt) == ' ') && (paramInt != str2.length() - 1) && (str2.charAt(paramInt + 1) != ' '))
            {
              if (Build.VERSION.SDK_INT >= 16)
                this.stringBuilder.append("‌");
              this.stringBuilder.appendCodePoint(str2.codePointAt(paramInt + 1));
              break;
            }
            paramInt -= 1;
          }
        }
        catch (java.lang.Exception paramString1)
        {
          FileLog.e(paramString1);
          return;
        }
      }
    }
    this.textLayout = null;
  }

  public void setInfo(TLRPC.Chat paramChat)
  {
    int i;
    String str;
    if (paramChat != null)
    {
      i = paramChat.id;
      str = paramChat.title;
      if (paramChat.id >= 0)
        break label35;
    }
    label35: for (boolean bool = true; ; bool = false)
    {
      setInfo(i, str, null, bool, null);
      return;
    }
  }

  public void setInfo(TLRPC.User paramUser)
  {
    if (paramUser != null)
      setInfo(paramUser.id, paramUser.first_name, paramUser.last_name, false, null);
  }

  public void setProfile(boolean paramBoolean)
  {
    this.isProfile = paramBoolean;
  }

  public void setTextSize(int paramInt)
  {
    this.namePaint.setTextSize(paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.AvatarDrawable
 * JD-Core Version:    0.6.0
 */