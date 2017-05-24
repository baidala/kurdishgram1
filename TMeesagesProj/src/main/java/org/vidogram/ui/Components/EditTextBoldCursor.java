package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;

public class EditTextBoldCursor extends EditText
{
  private static Method getVerticalOffsetMethod;
  private static Field mCursorDrawableField;
  private static Field mCursorDrawableResField;
  private static Field mEditor;
  private static Field mScrollYField;
  private static Field mShowCursorField;
  private boolean allowDrawCursor = true;
  private int cursorSize;
  private Object editor;
  private GradientDrawable gradientDrawable;
  private float hintAlpha = 1.0F;
  private int hintColor;
  private StaticLayout hintLayout;
  private boolean hintVisible = true;
  private int ignoreBottomCount;
  private int ignoreTopCount;
  private long lastUpdateTime;
  private float lineSpacingExtra;
  private Drawable[] mCursorDrawable;
  private Rect rect = new Rect();
  private int scrollY;

  public EditTextBoldCursor(Context paramContext)
  {
    super(paramContext);
    if (mCursorDrawableField == null);
    try
    {
      mScrollYField = View.class.getDeclaredField("mScrollY");
      mScrollYField.setAccessible(true);
      mCursorDrawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
      mCursorDrawableResField.setAccessible(true);
      mEditor = TextView.class.getDeclaredField("mEditor");
      mEditor.setAccessible(true);
      paramContext = Class.forName("android.widget.Editor");
      mShowCursorField = paramContext.getDeclaredField("mShowCursor");
      mShowCursorField.setAccessible(true);
      mCursorDrawableField = paramContext.getDeclaredField("mCursorDrawable");
      mCursorDrawableField.setAccessible(true);
      getVerticalOffsetMethod = TextView.class.getDeclaredMethod("getVerticalOffset", new Class[] { Boolean.TYPE });
      getVerticalOffsetMethod.setAccessible(true);
      try
      {
        label153: this.gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { -11230757, -11230757 });
        this.editor = mEditor.get(this);
        this.mCursorDrawable = ((Drawable[])(Drawable[])mCursorDrawableField.get(this.editor));
        mCursorDrawableResField.set(this, Integer.valueOf(2130837710));
        this.cursorSize = AndroidUtilities.dp(24.0F);
        return;
      }
      catch (java.lang.Exception paramContext)
      {
        while (true)
          FileLog.e(paramContext);
      }
    }
    catch (java.lang.Throwable paramContext)
    {
      break label153;
    }
  }

  public int getExtendedPaddingBottom()
  {
    if (this.ignoreBottomCount != 0)
    {
      this.ignoreBottomCount -= 1;
      if (this.scrollY != 2147483647)
        return -this.scrollY;
      return 0;
    }
    return super.getExtendedPaddingBottom();
  }

  public int getExtendedPaddingTop()
  {
    if (this.ignoreTopCount != 0)
    {
      this.ignoreTopCount -= 1;
      return 0;
    }
    return super.getExtendedPaddingTop();
  }

  // ERROR //
  protected void onDraw(android.graphics.Canvas paramCanvas)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 181	org/vidogram/ui/Components/EditTextBoldCursor:getExtendedPaddingTop	()I
    //   4: istore_3
    //   5: aload_0
    //   6: ldc 171
    //   8: putfield 170	org/vidogram/ui/Components/EditTextBoldCursor:scrollY	I
    //   11: aload_0
    //   12: getstatic 71	org/vidogram/ui/Components/EditTextBoldCursor:mScrollYField	Ljava/lang/reflect/Field;
    //   15: aload_0
    //   16: invokevirtual 185	java/lang/reflect/Field:getInt	(Ljava/lang/Object;)I
    //   19: putfield 170	org/vidogram/ui/Components/EditTextBoldCursor:scrollY	I
    //   22: getstatic 71	org/vidogram/ui/Components/EditTextBoldCursor:mScrollYField	Ljava/lang/reflect/Field;
    //   25: aload_0
    //   26: iconst_0
    //   27: invokestatic 144	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   30: invokevirtual 148	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   33: aload_0
    //   34: iconst_1
    //   35: putfield 176	org/vidogram/ui/Components/EditTextBoldCursor:ignoreTopCount	I
    //   38: aload_0
    //   39: iconst_1
    //   40: putfield 168	org/vidogram/ui/Components/EditTextBoldCursor:ignoreBottomCount	I
    //   43: aload_1
    //   44: invokevirtual 190	android/graphics/Canvas:save	()I
    //   47: pop
    //   48: aload_1
    //   49: fconst_0
    //   50: iload_3
    //   51: i2f
    //   52: invokevirtual 194	android/graphics/Canvas:translate	(FF)V
    //   55: aload_0
    //   56: aload_1
    //   57: invokespecial 196	android/widget/EditText:onDraw	(Landroid/graphics/Canvas;)V
    //   60: aload_0
    //   61: getfield 170	org/vidogram/ui/Components/EditTextBoldCursor:scrollY	I
    //   64: ldc 171
    //   66: if_icmpeq +17 -> 83
    //   69: getstatic 71	org/vidogram/ui/Components/EditTextBoldCursor:mScrollYField	Ljava/lang/reflect/Field;
    //   72: aload_0
    //   73: aload_0
    //   74: getfield 170	org/vidogram/ui/Components/EditTextBoldCursor:scrollY	I
    //   77: invokestatic 144	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   80: invokevirtual 148	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   83: aload_1
    //   84: invokevirtual 199	android/graphics/Canvas:restore	()V
    //   87: aload_0
    //   88: invokevirtual 202	org/vidogram/ui/Components/EditTextBoldCursor:length	()I
    //   91: ifne +224 -> 315
    //   94: aload_0
    //   95: getfield 204	org/vidogram/ui/Components/EditTextBoldCursor:hintLayout	Landroid/text/StaticLayout;
    //   98: ifnull +217 -> 315
    //   101: aload_0
    //   102: getfield 53	org/vidogram/ui/Components/EditTextBoldCursor:hintVisible	Z
    //   105: ifne +12 -> 117
    //   108: aload_0
    //   109: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   112: fconst_0
    //   113: fcmpl
    //   114: ifeq +201 -> 315
    //   117: aload_0
    //   118: getfield 53	org/vidogram/ui/Components/EditTextBoldCursor:hintVisible	Z
    //   121: ifeq +12 -> 133
    //   124: aload_0
    //   125: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   128: fconst_1
    //   129: fcmpl
    //   130: ifne +19 -> 149
    //   133: aload_0
    //   134: getfield 53	org/vidogram/ui/Components/EditTextBoldCursor:hintVisible	Z
    //   137: ifne +99 -> 236
    //   140: aload_0
    //   141: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   144: fconst_0
    //   145: fcmpl
    //   146: ifeq +90 -> 236
    //   149: invokestatic 210	java/lang/System:currentTimeMillis	()J
    //   152: lstore 9
    //   154: lload 9
    //   156: aload_0
    //   157: getfield 212	org/vidogram/ui/Components/EditTextBoldCursor:lastUpdateTime	J
    //   160: lsub
    //   161: lstore 7
    //   163: lload 7
    //   165: lconst_0
    //   166: lcmp
    //   167: iflt +16 -> 183
    //   170: lload 7
    //   172: lstore 5
    //   174: lload 7
    //   176: ldc2_w 213
    //   179: lcmp
    //   180: ifle +8 -> 188
    //   183: ldc2_w 213
    //   186: lstore 5
    //   188: aload_0
    //   189: lload 9
    //   191: putfield 212	org/vidogram/ui/Components/EditTextBoldCursor:lastUpdateTime	J
    //   194: aload_0
    //   195: getfield 53	org/vidogram/ui/Components/EditTextBoldCursor:hintVisible	Z
    //   198: ifeq +430 -> 628
    //   201: aload_0
    //   202: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   205: fstore_2
    //   206: aload_0
    //   207: lload 5
    //   209: l2f
    //   210: ldc 215
    //   212: fdiv
    //   213: fload_2
    //   214: fadd
    //   215: putfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   218: aload_0
    //   219: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   222: fconst_1
    //   223: fcmpl
    //   224: ifle +8 -> 232
    //   227: aload_0
    //   228: fconst_1
    //   229: putfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   232: aload_0
    //   233: invokevirtual 218	org/vidogram/ui/Components/EditTextBoldCursor:invalidate	()V
    //   236: aload_0
    //   237: invokevirtual 222	org/vidogram/ui/Components/EditTextBoldCursor:getPaint	()Landroid/text/TextPaint;
    //   240: invokevirtual 227	android/text/TextPaint:getColor	()I
    //   243: istore_3
    //   244: aload_0
    //   245: invokevirtual 222	org/vidogram/ui/Components/EditTextBoldCursor:getPaint	()Landroid/text/TextPaint;
    //   248: aload_0
    //   249: getfield 229	org/vidogram/ui/Components/EditTextBoldCursor:hintColor	I
    //   252: invokevirtual 233	android/text/TextPaint:setColor	(I)V
    //   255: aload_0
    //   256: invokevirtual 222	org/vidogram/ui/Components/EditTextBoldCursor:getPaint	()Landroid/text/TextPaint;
    //   259: ldc 234
    //   261: aload_0
    //   262: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   265: fmul
    //   266: f2i
    //   267: invokevirtual 237	android/text/TextPaint:setAlpha	(I)V
    //   270: aload_1
    //   271: invokevirtual 190	android/graphics/Canvas:save	()I
    //   274: pop
    //   275: aload_1
    //   276: fconst_0
    //   277: aload_0
    //   278: invokevirtual 240	org/vidogram/ui/Components/EditTextBoldCursor:getMeasuredHeight	()I
    //   281: aload_0
    //   282: getfield 204	org/vidogram/ui/Components/EditTextBoldCursor:hintLayout	Landroid/text/StaticLayout;
    //   285: invokevirtual 245	android/text/StaticLayout:getHeight	()I
    //   288: isub
    //   289: i2f
    //   290: fconst_2
    //   291: fdiv
    //   292: invokevirtual 194	android/graphics/Canvas:translate	(FF)V
    //   295: aload_0
    //   296: getfield 204	org/vidogram/ui/Components/EditTextBoldCursor:hintLayout	Landroid/text/StaticLayout;
    //   299: aload_1
    //   300: invokevirtual 248	android/text/StaticLayout:draw	(Landroid/graphics/Canvas;)V
    //   303: aload_0
    //   304: invokevirtual 222	org/vidogram/ui/Components/EditTextBoldCursor:getPaint	()Landroid/text/TextPaint;
    //   307: iload_3
    //   308: invokevirtual 233	android/text/TextPaint:setColor	(I)V
    //   311: aload_1
    //   312: invokevirtual 199	android/graphics/Canvas:restore	()V
    //   315: aload_0
    //   316: getfield 57	org/vidogram/ui/Components/EditTextBoldCursor:allowDrawCursor	Z
    //   319: ifeq +308 -> 627
    //   322: getstatic 96	org/vidogram/ui/Components/EditTextBoldCursor:mShowCursorField	Ljava/lang/reflect/Field;
    //   325: ifnull +302 -> 627
    //   328: aload_0
    //   329: getfield 137	org/vidogram/ui/Components/EditTextBoldCursor:mCursorDrawable	[Landroid/graphics/drawable/Drawable;
    //   332: ifnull +295 -> 627
    //   335: aload_0
    //   336: getfield 137	org/vidogram/ui/Components/EditTextBoldCursor:mCursorDrawable	[Landroid/graphics/drawable/Drawable;
    //   339: iconst_0
    //   340: aaload
    //   341: ifnull +286 -> 627
    //   344: getstatic 96	org/vidogram/ui/Components/EditTextBoldCursor:mShowCursorField	Ljava/lang/reflect/Field;
    //   347: aload_0
    //   348: getfield 134	org/vidogram/ui/Components/EditTextBoldCursor:editor	Ljava/lang/Object;
    //   351: invokevirtual 252	java/lang/reflect/Field:getLong	(Ljava/lang/Object;)J
    //   354: lstore 5
    //   356: invokestatic 257	android/os/SystemClock:uptimeMillis	()J
    //   359: lload 5
    //   361: lsub
    //   362: ldc2_w 258
    //   365: lrem
    //   366: ldc2_w 260
    //   369: lcmp
    //   370: ifge +290 -> 660
    //   373: iconst_1
    //   374: istore_3
    //   375: iload_3
    //   376: ifeq +251 -> 627
    //   379: aload_1
    //   380: invokevirtual 190	android/graphics/Canvas:save	()I
    //   383: pop
    //   384: aload_0
    //   385: invokevirtual 264	org/vidogram/ui/Components/EditTextBoldCursor:getGravity	()I
    //   388: bipush 112
    //   390: iand
    //   391: bipush 48
    //   393: if_icmpeq +289 -> 682
    //   396: getstatic 111	org/vidogram/ui/Components/EditTextBoldCursor:getVerticalOffsetMethod	Ljava/lang/reflect/Method;
    //   399: aload_0
    //   400: iconst_1
    //   401: anewarray 266	java/lang/Object
    //   404: dup
    //   405: iconst_0
    //   406: iconst_1
    //   407: invokestatic 269	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   410: aastore
    //   411: invokevirtual 273	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   414: checkcast 140	java/lang/Integer
    //   417: invokevirtual 276	java/lang/Integer:intValue	()I
    //   420: istore_3
    //   421: aload_1
    //   422: aload_0
    //   423: invokevirtual 279	org/vidogram/ui/Components/EditTextBoldCursor:getPaddingLeft	()I
    //   426: i2f
    //   427: iload_3
    //   428: aload_0
    //   429: invokevirtual 181	org/vidogram/ui/Components/EditTextBoldCursor:getExtendedPaddingTop	()I
    //   432: iadd
    //   433: i2f
    //   434: invokevirtual 194	android/graphics/Canvas:translate	(FF)V
    //   437: aload_0
    //   438: invokevirtual 283	org/vidogram/ui/Components/EditTextBoldCursor:getLayout	()Landroid/text/Layout;
    //   441: astore 11
    //   443: aload 11
    //   445: aload_0
    //   446: invokevirtual 286	org/vidogram/ui/Components/EditTextBoldCursor:getSelectionStart	()I
    //   449: invokevirtual 292	android/text/Layout:getLineForOffset	(I)I
    //   452: istore_3
    //   453: aload 11
    //   455: invokevirtual 295	android/text/Layout:getLineCount	()I
    //   458: istore 4
    //   460: aload_0
    //   461: getfield 137	org/vidogram/ui/Components/EditTextBoldCursor:mCursorDrawable	[Landroid/graphics/drawable/Drawable;
    //   464: iconst_0
    //   465: aaload
    //   466: invokevirtual 301	android/graphics/drawable/Drawable:getBounds	()Landroid/graphics/Rect;
    //   469: astore 11
    //   471: aload_0
    //   472: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   475: aload 11
    //   477: getfield 304	android/graphics/Rect:left	I
    //   480: putfield 304	android/graphics/Rect:left	I
    //   483: aload_0
    //   484: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   487: aload 11
    //   489: getfield 304	android/graphics/Rect:left	I
    //   492: fconst_2
    //   493: invokestatic 155	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   496: iadd
    //   497: putfield 307	android/graphics/Rect:right	I
    //   500: aload_0
    //   501: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   504: aload 11
    //   506: getfield 310	android/graphics/Rect:bottom	I
    //   509: putfield 310	android/graphics/Rect:bottom	I
    //   512: aload_0
    //   513: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   516: aload 11
    //   518: getfield 313	android/graphics/Rect:top	I
    //   521: putfield 313	android/graphics/Rect:top	I
    //   524: aload_0
    //   525: getfield 315	org/vidogram/ui/Components/EditTextBoldCursor:lineSpacingExtra	F
    //   528: fconst_0
    //   529: fcmpl
    //   530: ifeq +34 -> 564
    //   533: iload_3
    //   534: iload 4
    //   536: iconst_1
    //   537: isub
    //   538: if_icmpge +26 -> 564
    //   541: aload_0
    //   542: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   545: astore 11
    //   547: aload 11
    //   549: aload 11
    //   551: getfield 310	android/graphics/Rect:bottom	I
    //   554: i2f
    //   555: aload_0
    //   556: getfield 315	org/vidogram/ui/Components/EditTextBoldCursor:lineSpacingExtra	F
    //   559: fsub
    //   560: f2i
    //   561: putfield 310	android/graphics/Rect:bottom	I
    //   564: aload_0
    //   565: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   568: aload_0
    //   569: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   572: invokevirtual 318	android/graphics/Rect:centerY	()I
    //   575: aload_0
    //   576: getfield 157	org/vidogram/ui/Components/EditTextBoldCursor:cursorSize	I
    //   579: iconst_2
    //   580: idiv
    //   581: isub
    //   582: putfield 313	android/graphics/Rect:top	I
    //   585: aload_0
    //   586: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   589: aload_0
    //   590: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   593: getfield 313	android/graphics/Rect:top	I
    //   596: aload_0
    //   597: getfield 157	org/vidogram/ui/Components/EditTextBoldCursor:cursorSize	I
    //   600: iadd
    //   601: putfield 310	android/graphics/Rect:bottom	I
    //   604: aload_0
    //   605: getfield 128	org/vidogram/ui/Components/EditTextBoldCursor:gradientDrawable	Landroid/graphics/drawable/GradientDrawable;
    //   608: aload_0
    //   609: getfield 51	org/vidogram/ui/Components/EditTextBoldCursor:rect	Landroid/graphics/Rect;
    //   612: invokevirtual 322	android/graphics/drawable/GradientDrawable:setBounds	(Landroid/graphics/Rect;)V
    //   615: aload_0
    //   616: getfield 128	org/vidogram/ui/Components/EditTextBoldCursor:gradientDrawable	Landroid/graphics/drawable/GradientDrawable;
    //   619: aload_1
    //   620: invokevirtual 323	android/graphics/drawable/GradientDrawable:draw	(Landroid/graphics/Canvas;)V
    //   623: aload_1
    //   624: invokevirtual 199	android/graphics/Canvas:restore	()V
    //   627: return
    //   628: aload_0
    //   629: aload_0
    //   630: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   633: lload 5
    //   635: l2f
    //   636: ldc 215
    //   638: fdiv
    //   639: fsub
    //   640: putfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   643: aload_0
    //   644: getfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   647: fconst_0
    //   648: fcmpg
    //   649: ifge -417 -> 232
    //   652: aload_0
    //   653: fconst_0
    //   654: putfield 55	org/vidogram/ui/Components/EditTextBoldCursor:hintAlpha	F
    //   657: goto -425 -> 232
    //   660: iconst_0
    //   661: istore_3
    //   662: goto -287 -> 375
    //   665: astore 11
    //   667: goto -607 -> 60
    //   670: astore_1
    //   671: return
    //   672: astore 11
    //   674: goto -591 -> 83
    //   677: astore 11
    //   679: goto -646 -> 33
    //   682: iconst_0
    //   683: istore_3
    //   684: goto -263 -> 421
    //
    // Exception table:
    //   from	to	target	type
    //   55	60	665	java/lang/Exception
    //   315	373	670	java/lang/Throwable
    //   379	421	670	java/lang/Throwable
    //   421	533	670	java/lang/Throwable
    //   541	564	670	java/lang/Throwable
    //   564	627	670	java/lang/Throwable
    //   69	83	672	java/lang/Exception
    //   11	33	677	java/lang/Exception
  }

  public void setAllowDrawCursor(boolean paramBoolean)
  {
    this.allowDrawCursor = paramBoolean;
  }

  public void setCursorColor(int paramInt)
  {
    this.gradientDrawable.setColor(paramInt);
    invalidate();
  }

  public void setCursorSize(int paramInt)
  {
    this.cursorSize = paramInt;
  }

  public void setHintColor(int paramInt)
  {
    this.hintColor = paramInt;
    invalidate();
  }

  public void setHintText(String paramString)
  {
    this.hintLayout = new StaticLayout(paramString, getPaint(), AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
  }

  public void setHintVisible(boolean paramBoolean)
  {
    if (this.hintVisible == paramBoolean)
      return;
    this.lastUpdateTime = System.currentTimeMillis();
    this.hintVisible = paramBoolean;
    invalidate();
  }

  public void setLineSpacing(float paramFloat1, float paramFloat2)
  {
    super.setLineSpacing(paramFloat1, paramFloat2);
    this.lineSpacingExtra = paramFloat1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.EditTextBoldCursor
 * JD-Core Version:    0.6.0
 */