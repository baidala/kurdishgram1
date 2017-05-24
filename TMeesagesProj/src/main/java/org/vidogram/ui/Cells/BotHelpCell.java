package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.MeasureSpec;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LinkPath;
import org.vidogram.ui.Components.TypefaceSpan;

public class BotHelpCell extends View
{
  private BotHelpCellDelegate delegate;
  private int height;
  private String oldText;
  private ClickableSpan pressedLink;
  private StaticLayout textLayout;
  private int textX;
  private int textY;
  private LinkPath urlPath = new LinkPath();
  private int width;

  public BotHelpCell(Context paramContext)
  {
    super(paramContext);
  }

  private void resetPressedLink()
  {
    if (this.pressedLink != null)
      this.pressedLink = null;
    invalidate();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int j = (paramCanvas.getWidth() - this.width) / 2;
    int i = AndroidUtilities.dp(4.0F);
    Theme.chat_msgInMediaShadowDrawable.setBounds(j, i, this.width + j, this.height + i);
    Theme.chat_msgInMediaShadowDrawable.draw(paramCanvas);
    Theme.chat_msgInMediaDrawable.setBounds(j, i, this.width + j, this.height + i);
    Theme.chat_msgInMediaDrawable.draw(paramCanvas);
    Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
    Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
    paramCanvas.save();
    j += AndroidUtilities.dp(11.0F);
    this.textX = j;
    float f = j;
    i += AndroidUtilities.dp(11.0F);
    this.textY = i;
    paramCanvas.translate(f, i);
    if (this.pressedLink != null)
      paramCanvas.drawPath(this.urlPath, Theme.chat_urlPaint);
    if (this.textLayout != null)
      this.textLayout.draw(paramCanvas);
    paramCanvas.restore();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), this.height + AndroidUtilities.dp(8.0F));
  }

  // ERROR //
  public boolean onTouchEvent(android.view.MotionEvent paramMotionEvent)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 7
    //   3: aload_1
    //   4: invokevirtual 153	android/view/MotionEvent:getX	()F
    //   7: fstore_2
    //   8: aload_1
    //   9: invokevirtual 156	android/view/MotionEvent:getY	()F
    //   12: fstore_3
    //   13: aload_0
    //   14: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   17: ifnull +443 -> 460
    //   20: aload_1
    //   21: invokevirtual 159	android/view/MotionEvent:getAction	()I
    //   24: ifeq +18 -> 42
    //   27: aload_0
    //   28: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   31: ifnull +406 -> 437
    //   34: aload_1
    //   35: invokevirtual 159	android/view/MotionEvent:getAction	()I
    //   38: iconst_1
    //   39: if_icmpne +398 -> 437
    //   42: aload_1
    //   43: invokevirtual 159	android/view/MotionEvent:getAction	()I
    //   46: ifne +253 -> 299
    //   49: aload_0
    //   50: invokespecial 161	org/vidogram/ui/Cells/BotHelpCell:resetPressedLink	()V
    //   53: fload_2
    //   54: aload_0
    //   55: getfield 104	org/vidogram/ui/Cells/BotHelpCell:textX	I
    //   58: i2f
    //   59: fsub
    //   60: f2i
    //   61: istore 4
    //   63: fload_3
    //   64: aload_0
    //   65: getfield 106	org/vidogram/ui/Cells/BotHelpCell:textY	I
    //   68: i2f
    //   69: fsub
    //   70: f2i
    //   71: istore 5
    //   73: aload_0
    //   74: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   77: iload 5
    //   79: invokevirtual 164	android/text/StaticLayout:getLineForVertical	(I)I
    //   82: istore 5
    //   84: aload_0
    //   85: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   88: iload 5
    //   90: iload 4
    //   92: i2f
    //   93: invokevirtual 168	android/text/StaticLayout:getOffsetForHorizontal	(IF)I
    //   96: istore 6
    //   98: aload_0
    //   99: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   102: iload 5
    //   104: invokevirtual 172	android/text/StaticLayout:getLineLeft	(I)F
    //   107: fstore_2
    //   108: fload_2
    //   109: iload 4
    //   111: i2f
    //   112: fcmpg
    //   113: ifgt +162 -> 275
    //   116: aload_0
    //   117: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   120: iload 5
    //   122: invokevirtual 175	android/text/StaticLayout:getLineWidth	(I)F
    //   125: fload_2
    //   126: fadd
    //   127: iload 4
    //   129: i2f
    //   130: fcmpl
    //   131: iflt +144 -> 275
    //   134: aload_0
    //   135: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   138: invokevirtual 179	android/text/StaticLayout:getText	()Ljava/lang/CharSequence;
    //   141: checkcast 181	android/text/Spannable
    //   144: astore 8
    //   146: aload 8
    //   148: iload 6
    //   150: iload 6
    //   152: ldc 183
    //   154: invokeinterface 187 4 0
    //   159: checkcast 189	[Landroid/text/style/ClickableSpan;
    //   162: astore 9
    //   164: aload 9
    //   166: arraylength
    //   167: ifeq +101 -> 268
    //   170: aload_0
    //   171: invokespecial 161	org/vidogram/ui/Cells/BotHelpCell:resetPressedLink	()V
    //   174: aload_0
    //   175: aload 9
    //   177: iconst_0
    //   178: aaload
    //   179: putfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   182: aload 8
    //   184: aload_0
    //   185: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   188: invokeinterface 193 2 0
    //   193: istore 4
    //   195: aload_0
    //   196: getfield 33	org/vidogram/ui/Cells/BotHelpCell:urlPath	Lorg/vidogram/ui/Components/LinkPath;
    //   199: aload_0
    //   200: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   203: iload 4
    //   205: fconst_0
    //   206: invokevirtual 197	org/vidogram/ui/Components/LinkPath:setCurrentLayout	(Landroid/text/StaticLayout;IF)V
    //   209: aload_0
    //   210: getfield 120	org/vidogram/ui/Cells/BotHelpCell:textLayout	Landroid/text/StaticLayout;
    //   213: iload 4
    //   215: aload 8
    //   217: aload_0
    //   218: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   221: invokeinterface 200 2 0
    //   226: aload_0
    //   227: getfield 33	org/vidogram/ui/Cells/BotHelpCell:urlPath	Lorg/vidogram/ui/Components/LinkPath;
    //   230: invokevirtual 204	android/text/StaticLayout:getSelectionPath	(IILandroid/graphics/Path;)V
    //   233: iconst_1
    //   234: istore 4
    //   236: iload 4
    //   238: ifne +11 -> 249
    //   241: aload_0
    //   242: aload_1
    //   243: invokespecial 206	android/view/View:onTouchEvent	(Landroid/view/MotionEvent;)Z
    //   246: ifeq +6 -> 252
    //   249: iconst_1
    //   250: istore 7
    //   252: iload 7
    //   254: ireturn
    //   255: astore 8
    //   257: aload 8
    //   259: invokestatic 212	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   262: iconst_1
    //   263: istore 4
    //   265: goto -29 -> 236
    //   268: aload_0
    //   269: invokespecial 161	org/vidogram/ui/Cells/BotHelpCell:resetPressedLink	()V
    //   272: goto +188 -> 460
    //   275: aload_0
    //   276: invokespecial 161	org/vidogram/ui/Cells/BotHelpCell:resetPressedLink	()V
    //   279: goto +181 -> 460
    //   282: astore 8
    //   284: iconst_0
    //   285: istore 4
    //   287: aload_0
    //   288: invokespecial 161	org/vidogram/ui/Cells/BotHelpCell:resetPressedLink	()V
    //   291: aload 8
    //   293: invokestatic 212	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   296: goto -60 -> 236
    //   299: aload_0
    //   300: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   303: ifnull +157 -> 460
    //   306: aload_0
    //   307: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   310: instanceof 214
    //   313: ifeq +73 -> 386
    //   316: aload_0
    //   317: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   320: checkcast 214	org/vidogram/ui/Components/URLSpanNoUnderline
    //   323: invokevirtual 218	org/vidogram/ui/Components/URLSpanNoUnderline:getURL	()Ljava/lang/String;
    //   326: astore 8
    //   328: aload 8
    //   330: ldc 220
    //   332: invokevirtual 226	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   335: ifne +23 -> 358
    //   338: aload 8
    //   340: ldc 228
    //   342: invokevirtual 226	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   345: ifne +13 -> 358
    //   348: aload 8
    //   350: ldc 230
    //   352: invokevirtual 226	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   355: ifeq +21 -> 376
    //   358: aload_0
    //   359: getfield 232	org/vidogram/ui/Cells/BotHelpCell:delegate	Lorg/vidogram/ui/Cells/BotHelpCell$BotHelpCellDelegate;
    //   362: ifnull +14 -> 376
    //   365: aload_0
    //   366: getfield 232	org/vidogram/ui/Cells/BotHelpCell:delegate	Lorg/vidogram/ui/Cells/BotHelpCell$BotHelpCellDelegate;
    //   369: aload 8
    //   371: invokeinterface 236 2 0
    //   376: aload_0
    //   377: invokespecial 161	org/vidogram/ui/Cells/BotHelpCell:resetPressedLink	()V
    //   380: iconst_1
    //   381: istore 4
    //   383: goto -147 -> 236
    //   386: aload_0
    //   387: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   390: instanceof 238
    //   393: ifeq +33 -> 426
    //   396: aload_0
    //   397: invokevirtual 242	org/vidogram/ui/Cells/BotHelpCell:getContext	()Landroid/content/Context;
    //   400: aload_0
    //   401: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   404: checkcast 238	android/text/style/URLSpan
    //   407: invokevirtual 243	android/text/style/URLSpan:getURL	()Ljava/lang/String;
    //   410: invokestatic 249	org/vidogram/messenger/browser/Browser:openUrl	(Landroid/content/Context;Ljava/lang/String;)V
    //   413: goto -37 -> 376
    //   416: astore 8
    //   418: aload 8
    //   420: invokestatic 212	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   423: goto -47 -> 376
    //   426: aload_0
    //   427: getfield 37	org/vidogram/ui/Cells/BotHelpCell:pressedLink	Landroid/text/style/ClickableSpan;
    //   430: aload_0
    //   431: invokevirtual 253	android/text/style/ClickableSpan:onClick	(Landroid/view/View;)V
    //   434: goto -58 -> 376
    //   437: aload_1
    //   438: invokevirtual 159	android/view/MotionEvent:getAction	()I
    //   441: iconst_3
    //   442: if_icmpne +18 -> 460
    //   445: aload_0
    //   446: invokespecial 161	org/vidogram/ui/Cells/BotHelpCell:resetPressedLink	()V
    //   449: goto +11 -> 460
    //   452: astore 8
    //   454: iconst_1
    //   455: istore 4
    //   457: goto -170 -> 287
    //   460: iconst_0
    //   461: istore 4
    //   463: goto -227 -> 236
    //
    // Exception table:
    //   from	to	target	type
    //   182	233	255	java/lang/Exception
    //   53	108	282	java/lang/Exception
    //   116	182	282	java/lang/Exception
    //   268	272	282	java/lang/Exception
    //   275	279	282	java/lang/Exception
    //   306	358	416	java/lang/Exception
    //   358	376	416	java/lang/Exception
    //   386	413	416	java/lang/Exception
    //   426	434	416	java/lang/Exception
    //   257	262	452	java/lang/Exception
  }

  public void setDelegate(BotHelpCellDelegate paramBotHelpCellDelegate)
  {
    this.delegate = paramBotHelpCellDelegate;
  }

  public void setText(String paramString)
  {
    int k = 0;
    if ((paramString == null) || (paramString.length() == 0))
      setVisibility(8);
    do
      return;
    while ((paramString != null) && (this.oldText != null) && (paramString.equals(this.oldText)));
    this.oldText = paramString;
    setVisibility(0);
    if (AndroidUtilities.isTablet());
    SpannableStringBuilder localSpannableStringBuilder;
    String str;
    int j;
    for (int i = (int)(AndroidUtilities.getMinTabletSide() * 0.7F); ; i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F))
    {
      paramString = paramString.split("\n");
      localSpannableStringBuilder = new SpannableStringBuilder();
      str = LocaleController.getString("BotInfoTitle", 2131165393);
      localSpannableStringBuilder.append(str);
      localSpannableStringBuilder.append("\n\n");
      j = 0;
      while (j < paramString.length)
      {
        localSpannableStringBuilder.append(paramString[j].trim());
        if (j != paramString.length - 1)
          localSpannableStringBuilder.append("\n");
        j += 1;
      }
    }
    MessageObject.addLinks(false, localSpannableStringBuilder);
    localSpannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, str.length(), 33);
    Emoji.replaceEmoji(localSpannableStringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
    try
    {
      this.textLayout = new StaticLayout(localSpannableStringBuilder, Theme.chat_msgTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      this.width = 0;
      this.height = (this.textLayout.getHeight() + AndroidUtilities.dp(22.0F));
      int m = this.textLayout.getLineCount();
      j = k;
      while (j < m)
      {
        this.width = (int)Math.ceil(Math.max(this.width, this.textLayout.getLineWidth(j) + this.textLayout.getLineLeft(j)));
        j += 1;
      }
      if (this.width > i)
        this.width = i;
      this.width += AndroidUtilities.dp(22.0F);
      return;
    }
    catch (java.lang.Exception paramString)
    {
      while (true)
        FileLog.e("tmessage", paramString);
    }
  }

  public static abstract interface BotHelpCellDelegate
  {
    public abstract void didPressUrl(String paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.BotHelpCell
 * JD-Core Version:    0.6.0
 */