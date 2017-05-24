package org.vidogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.LinkPath;

public class AboutLinkCell extends FrameLayout
{
  private AboutLinkCellDelegate delegate;
  private ImageView imageView;
  private String oldText;
  private ClickableSpan pressedLink;
  private SpannableStringBuilder stringBuilder;
  private StaticLayout textLayout;
  private int textX;
  private int textY;
  private LinkPath urlPath = new LinkPath();

  public AboutLinkCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new ImageView(paramContext);
    this.imageView.setScaleType(ImageView.ScaleType.CENTER);
    this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
    paramContext = this.imageView;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label125;
      f1 = 0.0F;
      label85: if (!LocaleController.isRTL)
        break label131;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 5.0F, f2, 0.0F));
      setWillNotDraw(false);
      return;
      i = 3;
      break;
      label125: f1 = 16.0F;
      break label85;
      label131: f2 = 0.0F;
    }
  }

  private void resetPressedLink()
  {
    if (this.pressedLink != null)
      this.pressedLink = null;
    invalidate();
  }

  public ImageView getImageView()
  {
    return this.imageView;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.save();
    float f;
    if (LocaleController.isRTL)
      f = 16.0F;
    while (true)
    {
      int i = AndroidUtilities.dp(f);
      this.textX = i;
      f = i;
      i = AndroidUtilities.dp(8.0F);
      this.textY = i;
      paramCanvas.translate(f, i);
      if (this.pressedLink != null)
        paramCanvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
      try
      {
        if (this.textLayout != null)
          this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
        return;
        f = 71.0F;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }

  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.textLayout = new StaticLayout(this.stringBuilder, Theme.profile_aboutTextPaint, View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(87.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(this.textLayout.getHeight() + AndroidUtilities.dp(16.0F), 1073741824));
  }

  // ERROR //
  public boolean onTouchEvent(android.view.MotionEvent paramMotionEvent)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 7
    //   3: aload_1
    //   4: invokevirtual 201	android/view/MotionEvent:getX	()F
    //   7: fstore_2
    //   8: aload_1
    //   9: invokevirtual 204	android/view/MotionEvent:getY	()F
    //   12: fstore_3
    //   13: aload_0
    //   14: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   17: ifnull +446 -> 463
    //   20: aload_1
    //   21: invokevirtual 207	android/view/MotionEvent:getAction	()I
    //   24: ifeq +18 -> 42
    //   27: aload_0
    //   28: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   31: ifnull +409 -> 440
    //   34: aload_1
    //   35: invokevirtual 207	android/view/MotionEvent:getAction	()I
    //   38: iconst_1
    //   39: if_icmpne +401 -> 440
    //   42: aload_1
    //   43: invokevirtual 207	android/view/MotionEvent:getAction	()I
    //   46: ifne +253 -> 299
    //   49: aload_0
    //   50: invokespecial 209	org/vidogram/ui/Cells/AboutLinkCell:resetPressedLink	()V
    //   53: fload_2
    //   54: aload_0
    //   55: getfield 123	org/vidogram/ui/Cells/AboutLinkCell:textX	I
    //   58: i2f
    //   59: fsub
    //   60: f2i
    //   61: istore 4
    //   63: fload_3
    //   64: aload_0
    //   65: getfield 126	org/vidogram/ui/Cells/AboutLinkCell:textY	I
    //   68: i2f
    //   69: fsub
    //   70: f2i
    //   71: istore 5
    //   73: aload_0
    //   74: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   77: iload 5
    //   79: invokevirtual 212	android/text/StaticLayout:getLineForVertical	(I)I
    //   82: istore 5
    //   84: aload_0
    //   85: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   88: iload 5
    //   90: iload 4
    //   92: i2f
    //   93: invokevirtual 216	android/text/StaticLayout:getOffsetForHorizontal	(IF)I
    //   96: istore 6
    //   98: aload_0
    //   99: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   102: iload 5
    //   104: invokevirtual 220	android/text/StaticLayout:getLineLeft	(I)F
    //   107: fstore_2
    //   108: fload_2
    //   109: iload 4
    //   111: i2f
    //   112: fcmpg
    //   113: ifgt +162 -> 275
    //   116: aload_0
    //   117: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   120: iload 5
    //   122: invokevirtual 223	android/text/StaticLayout:getLineWidth	(I)F
    //   125: fload_2
    //   126: fadd
    //   127: iload 4
    //   129: i2f
    //   130: fcmpl
    //   131: iflt +144 -> 275
    //   134: aload_0
    //   135: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   138: invokevirtual 227	android/text/StaticLayout:getText	()Ljava/lang/CharSequence;
    //   141: checkcast 229	android/text/Spannable
    //   144: astore 8
    //   146: aload 8
    //   148: iload 6
    //   150: iload 6
    //   152: ldc 231
    //   154: invokeinterface 235 4 0
    //   159: checkcast 237	[Landroid/text/style/ClickableSpan;
    //   162: astore 9
    //   164: aload 9
    //   166: arraylength
    //   167: ifeq +101 -> 268
    //   170: aload_0
    //   171: invokespecial 209	org/vidogram/ui/Cells/AboutLinkCell:resetPressedLink	()V
    //   174: aload_0
    //   175: aload 9
    //   177: iconst_0
    //   178: aaload
    //   179: putfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   182: aload 8
    //   184: aload_0
    //   185: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   188: invokeinterface 241 2 0
    //   193: istore 4
    //   195: aload_0
    //   196: getfield 36	org/vidogram/ui/Cells/AboutLinkCell:urlPath	Lorg/vidogram/ui/Components/LinkPath;
    //   199: aload_0
    //   200: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   203: iload 4
    //   205: fconst_0
    //   206: invokevirtual 245	org/vidogram/ui/Components/LinkPath:setCurrentLayout	(Landroid/text/StaticLayout;IF)V
    //   209: aload_0
    //   210: getfield 140	org/vidogram/ui/Cells/AboutLinkCell:textLayout	Landroid/text/StaticLayout;
    //   213: iload 4
    //   215: aload 8
    //   217: aload_0
    //   218: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   221: invokeinterface 248 2 0
    //   226: aload_0
    //   227: getfield 36	org/vidogram/ui/Cells/AboutLinkCell:urlPath	Lorg/vidogram/ui/Components/LinkPath;
    //   230: invokevirtual 252	android/text/StaticLayout:getSelectionPath	(IILandroid/graphics/Path;)V
    //   233: iconst_1
    //   234: istore 4
    //   236: iload 4
    //   238: ifne +11 -> 249
    //   241: aload_0
    //   242: aload_1
    //   243: invokespecial 254	android/widget/FrameLayout:onTouchEvent	(Landroid/view/MotionEvent;)Z
    //   246: ifeq +6 -> 252
    //   249: iconst_1
    //   250: istore 7
    //   252: iload 7
    //   254: ireturn
    //   255: astore 8
    //   257: aload 8
    //   259: invokestatic 155	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   262: iconst_1
    //   263: istore 4
    //   265: goto -29 -> 236
    //   268: aload_0
    //   269: invokespecial 209	org/vidogram/ui/Cells/AboutLinkCell:resetPressedLink	()V
    //   272: goto +191 -> 463
    //   275: aload_0
    //   276: invokespecial 209	org/vidogram/ui/Cells/AboutLinkCell:resetPressedLink	()V
    //   279: goto +184 -> 463
    //   282: astore 8
    //   284: iconst_0
    //   285: istore 4
    //   287: aload_0
    //   288: invokespecial 209	org/vidogram/ui/Cells/AboutLinkCell:resetPressedLink	()V
    //   291: aload 8
    //   293: invokestatic 155	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   296: goto -60 -> 236
    //   299: aload_0
    //   300: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   303: ifnull +160 -> 463
    //   306: aload_0
    //   307: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   310: instanceof 256
    //   313: ifeq +76 -> 389
    //   316: aload_0
    //   317: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   320: checkcast 256	org/vidogram/ui/Components/URLSpanNoUnderline
    //   323: invokevirtual 260	org/vidogram/ui/Components/URLSpanNoUnderline:getURL	()Ljava/lang/String;
    //   326: astore 8
    //   328: aload 8
    //   330: ldc_w 262
    //   333: invokevirtual 268	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   336: ifne +25 -> 361
    //   339: aload 8
    //   341: ldc_w 270
    //   344: invokevirtual 268	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   347: ifne +14 -> 361
    //   350: aload 8
    //   352: ldc_w 272
    //   355: invokevirtual 268	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   358: ifeq +21 -> 379
    //   361: aload_0
    //   362: getfield 274	org/vidogram/ui/Cells/AboutLinkCell:delegate	Lorg/vidogram/ui/Cells/AboutLinkCell$AboutLinkCellDelegate;
    //   365: ifnull +14 -> 379
    //   368: aload_0
    //   369: getfield 274	org/vidogram/ui/Cells/AboutLinkCell:delegate	Lorg/vidogram/ui/Cells/AboutLinkCell$AboutLinkCellDelegate;
    //   372: aload 8
    //   374: invokeinterface 278 2 0
    //   379: aload_0
    //   380: invokespecial 209	org/vidogram/ui/Cells/AboutLinkCell:resetPressedLink	()V
    //   383: iconst_1
    //   384: istore 4
    //   386: goto -150 -> 236
    //   389: aload_0
    //   390: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   393: instanceof 280
    //   396: ifeq +33 -> 429
    //   399: aload_0
    //   400: invokevirtual 284	org/vidogram/ui/Cells/AboutLinkCell:getContext	()Landroid/content/Context;
    //   403: aload_0
    //   404: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   407: checkcast 280	android/text/style/URLSpan
    //   410: invokevirtual 285	android/text/style/URLSpan:getURL	()Ljava/lang/String;
    //   413: invokestatic 291	org/vidogram/messenger/browser/Browser:openUrl	(Landroid/content/Context;Ljava/lang/String;)V
    //   416: goto -37 -> 379
    //   419: astore 8
    //   421: aload 8
    //   423: invokestatic 155	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   426: goto -47 -> 379
    //   429: aload_0
    //   430: getfield 100	org/vidogram/ui/Cells/AboutLinkCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   433: aload_0
    //   434: invokevirtual 295	android/text/style/ClickableSpan:onClick	(Landroid/view/View;)V
    //   437: goto -58 -> 379
    //   440: aload_1
    //   441: invokevirtual 207	android/view/MotionEvent:getAction	()I
    //   444: iconst_3
    //   445: if_icmpne +18 -> 463
    //   448: aload_0
    //   449: invokespecial 209	org/vidogram/ui/Cells/AboutLinkCell:resetPressedLink	()V
    //   452: goto +11 -> 463
    //   455: astore 8
    //   457: iconst_1
    //   458: istore 4
    //   460: goto -173 -> 287
    //   463: iconst_0
    //   464: istore 4
    //   466: goto -230 -> 236
    //
    // Exception table:
    //   from	to	target	type
    //   182	233	255	java/lang/Exception
    //   53	108	282	java/lang/Exception
    //   116	182	282	java/lang/Exception
    //   268	272	282	java/lang/Exception
    //   275	279	282	java/lang/Exception
    //   306	361	419	java/lang/Exception
    //   361	379	419	java/lang/Exception
    //   389	416	419	java/lang/Exception
    //   429	437	419	java/lang/Exception
    //   257	262	455	java/lang/Exception
  }

  public void setDelegate(AboutLinkCellDelegate paramAboutLinkCellDelegate)
  {
    this.delegate = paramAboutLinkCellDelegate;
  }

  public void setTextAndIcon(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.length() == 0))
      setVisibility(8);
    do
      return;
    while ((paramString != null) && (this.oldText != null) && (paramString.equals(this.oldText)));
    this.oldText = paramString;
    this.stringBuilder = new SpannableStringBuilder(this.oldText);
    MessageObject.addLinks(false, this.stringBuilder, false);
    Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
    requestLayout();
    if (paramInt == 0)
    {
      this.imageView.setImageDrawable(null);
      return;
    }
    this.imageView.setImageResource(paramInt);
  }

  public static abstract interface AboutLinkCellDelegate
  {
    public abstract void didPressUrl(String paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.AboutLinkCell
 * JD-Core Version:    0.6.0
 */