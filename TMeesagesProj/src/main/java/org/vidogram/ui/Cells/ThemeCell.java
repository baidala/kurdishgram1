package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.Theme.ThemeInfo;
import org.vidogram.ui.Components.LayoutHelper;

public class ThemeCell extends FrameLayout
{
  private static byte[] bytes = new byte[1024];
  private ImageView checkImage;
  private Theme.ThemeInfo currentThemeInfo;
  private boolean needDivider;
  private ImageView optionsButton;
  private Paint paint;
  private TextView textView;

  public ThemeCell(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.paint = new Paint(1);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0F));
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    label147: float f1;
    label156: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL)
        break label383;
      i = 5;
      if (!LocaleController.isRTL)
        break label389;
      f1 = 101.0F;
      if (!LocaleController.isRTL)
        break label395;
      f2 = 60.0F;
      label165: addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, f1, 0.0F, f2, 0.0F));
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
      this.checkImage.setImageResource(2130838070);
      localObject = this.checkImage;
      if (!LocaleController.isRTL)
        break label401;
      i = 3;
      label244: addView((View)localObject, LayoutHelper.createFrame(19, 14.0F, i | 0x10, 55.0F, 0.0F, 55.0F, 0.0F));
      this.optionsButton = new ImageView(paramContext);
      this.optionsButton.setFocusable(false);
      this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
      this.optionsButton.setImageResource(2130837738);
      this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
      this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
      paramContext = this.optionsButton;
      if (!LocaleController.isRTL)
        break label407;
    }
    label389: label395: label401: label407: for (int i = j; ; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(48, 48, i | 0x30));
      return;
      i = 3;
      break;
      label383: i = 3;
      break label147;
      f1 = 60.0F;
      break label156;
      f2 = 101.0F;
      break label165;
      i = 5;
      break label244;
    }
  }

  public Theme.ThemeInfo getCurrentThemeInfo()
  {
    return this.currentThemeInfo;
  }

  public TextView getTextView()
  {
    return this.textView;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    int j = AndroidUtilities.dp(27.0F);
    int i = j;
    if (LocaleController.isRTL)
      i = getWidth() - j;
    paramCanvas.drawCircle(i, AndroidUtilities.dp(24.0F), AndroidUtilities.dp(11.0F), this.paint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
    }
  }

  public void setOnOptionsClick(View.OnClickListener paramOnClickListener)
  {
    this.optionsButton.setOnClickListener(paramOnClickListener);
  }

  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }

  // ERROR //
  public void setTheme(Theme.ThemeInfo paramThemeInfo, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 171	org/vidogram/ui/Cells/ThemeCell:currentThemeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   5: aload_1
    //   6: getfield 239	org/vidogram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
    //   9: astore 15
    //   11: aload 15
    //   13: astore 14
    //   15: aload 15
    //   17: ldc 241
    //   19: invokevirtual 247	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   22: ifeq +18 -> 40
    //   25: aload 15
    //   27: iconst_0
    //   28: aload 15
    //   30: bipush 46
    //   32: invokevirtual 250	java/lang/String:lastIndexOf	(I)I
    //   35: invokevirtual 254	java/lang/String:substring	(II)Ljava/lang/String;
    //   38: astore 14
    //   40: aload_0
    //   41: getfield 42	org/vidogram/ui/Cells/ThemeCell:textView	Landroid/widget/TextView;
    //   44: aload 14
    //   46: invokevirtual 258	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   49: aload_0
    //   50: iload_2
    //   51: putfield 177	org/vidogram/ui/Cells/ThemeCell:needDivider	Z
    //   54: aload_0
    //   55: getfield 113	org/vidogram/ui/Cells/ThemeCell:checkImage	Landroid/widget/ImageView;
    //   58: astore 14
    //   60: aload_1
    //   61: invokestatic 261	org/vidogram/ui/ActionBar/Theme:getCurrentTheme	()Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
    //   64: if_acmpne +229 -> 293
    //   67: iconst_0
    //   68: istore_3
    //   69: aload 14
    //   71: iload_3
    //   72: invokevirtual 264	android/widget/ImageView:setVisibility	(I)V
    //   75: iconst_0
    //   76: istore_3
    //   77: iconst_0
    //   78: istore 4
    //   80: aload_1
    //   81: getfield 267	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   84: ifnonnull +10 -> 94
    //   87: aload_1
    //   88: getfield 270	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   91: ifnull +183 -> 274
    //   94: aconst_null
    //   95: astore 14
    //   97: iconst_0
    //   98: istore 5
    //   100: aload_1
    //   101: getfield 270	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   104: ifnull +194 -> 298
    //   107: aload_1
    //   108: getfield 270	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   111: invokestatic 274	org/vidogram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
    //   114: astore_1
    //   115: new 276	java/io/FileInputStream
    //   118: dup
    //   119: aload_1
    //   120: invokespecial 279	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   123: astore_1
    //   124: iconst_0
    //   125: istore_3
    //   126: iconst_0
    //   127: istore 4
    //   129: iload_3
    //   130: istore 7
    //   132: aload_1
    //   133: getstatic 21	org/vidogram/ui/Cells/ThemeCell:bytes	[B
    //   136: invokevirtual 283	java/io/FileInputStream:read	([B)I
    //   139: istore 13
    //   141: iload 13
    //   143: iconst_m1
    //   144: if_icmpeq +480 -> 624
    //   147: iconst_0
    //   148: istore 8
    //   150: iconst_0
    //   151: istore 9
    //   153: iload 5
    //   155: istore 6
    //   157: iload 8
    //   159: iload 13
    //   161: if_icmpge +460 -> 621
    //   164: iload 9
    //   166: istore 12
    //   168: iload 4
    //   170: istore 11
    //   172: iload 6
    //   174: istore 10
    //   176: iload_3
    //   177: istore 7
    //   179: getstatic 21	org/vidogram/ui/Cells/ThemeCell:bytes	[B
    //   182: iload 8
    //   184: baload
    //   185: bipush 10
    //   187: if_icmpne +328 -> 515
    //   190: iload 4
    //   192: iconst_1
    //   193: iadd
    //   194: istore 4
    //   196: iload 8
    //   198: iload 9
    //   200: isub
    //   201: iconst_1
    //   202: iadd
    //   203: istore 10
    //   205: iload_3
    //   206: istore 7
    //   208: new 243	java/lang/String
    //   211: dup
    //   212: getstatic 21	org/vidogram/ui/Cells/ThemeCell:bytes	[B
    //   215: iload 9
    //   217: iload 10
    //   219: iconst_1
    //   220: isub
    //   221: ldc_w 285
    //   224: invokespecial 288	java/lang/String:<init>	([BIILjava/lang/String;)V
    //   227: astore 14
    //   229: iload_3
    //   230: istore 7
    //   232: aload 14
    //   234: ldc_w 290
    //   237: invokevirtual 293	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   240: istore_2
    //   241: iload_2
    //   242: ifeq +113 -> 355
    //   245: iload 5
    //   247: iload 6
    //   249: if_icmpeq +369 -> 618
    //   252: iload 4
    //   254: sipush 500
    //   257: if_icmplt +279 -> 536
    //   260: iload_3
    //   261: istore 4
    //   263: aload_1
    //   264: ifnull +10 -> 274
    //   267: aload_1
    //   268: invokevirtual 296	java/io/FileInputStream:close	()V
    //   271: iload_3
    //   272: istore 4
    //   274: iload 4
    //   276: ifne +16 -> 292
    //   279: aload_0
    //   280: getfield 37	org/vidogram/ui/Cells/ThemeCell:paint	Landroid/graphics/Paint;
    //   283: ldc_w 298
    //   286: invokestatic 301	org/vidogram/ui/ActionBar/Theme:getDefaultColor	(Ljava/lang/String;)I
    //   289: invokevirtual 304	android/graphics/Paint:setColor	(I)V
    //   292: return
    //   293: iconst_4
    //   294: istore_3
    //   295: goto -226 -> 69
    //   298: new 306	java/io/File
    //   301: dup
    //   302: aload_1
    //   303: getfield 267	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   306: invokespecial 309	java/io/File:<init>	(Ljava/lang/String;)V
    //   309: astore_1
    //   310: goto -195 -> 115
    //   313: astore 15
    //   315: aload 14
    //   317: astore_1
    //   318: aload 15
    //   320: astore 14
    //   322: aload 14
    //   324: invokestatic 315	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   327: iload_3
    //   328: istore 4
    //   330: aload_1
    //   331: ifnull -57 -> 274
    //   334: aload_1
    //   335: invokevirtual 296	java/io/FileInputStream:close	()V
    //   338: iload_3
    //   339: istore 4
    //   341: goto -67 -> 274
    //   344: astore_1
    //   345: aload_1
    //   346: invokestatic 315	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   349: iload_3
    //   350: istore 4
    //   352: goto -78 -> 274
    //   355: iload_3
    //   356: istore 7
    //   358: aload 14
    //   360: bipush 61
    //   362: invokevirtual 318	java/lang/String:indexOf	(I)I
    //   365: istore 11
    //   367: iload 11
    //   369: iconst_m1
    //   370: if_icmpeq +127 -> 497
    //   373: iload_3
    //   374: istore 7
    //   376: aload 14
    //   378: iconst_0
    //   379: iload 11
    //   381: invokevirtual 254	java/lang/String:substring	(II)Ljava/lang/String;
    //   384: ldc_w 298
    //   387: invokevirtual 322	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   390: ifeq +107 -> 497
    //   393: iload_3
    //   394: istore 7
    //   396: aload 14
    //   398: iload 11
    //   400: iconst_1
    //   401: iadd
    //   402: invokevirtual 325	java/lang/String:substring	(I)Ljava/lang/String;
    //   405: astore 14
    //   407: iload_3
    //   408: istore 7
    //   410: aload 14
    //   412: invokevirtual 328	java/lang/String:length	()I
    //   415: ifle +67 -> 482
    //   418: iload_3
    //   419: istore 7
    //   421: aload 14
    //   423: iconst_0
    //   424: invokevirtual 332	java/lang/String:charAt	(I)C
    //   427: istore 8
    //   429: iload 8
    //   431: bipush 35
    //   433: if_icmpne +49 -> 482
    //   436: iload_3
    //   437: istore 7
    //   439: aload 14
    //   441: invokestatic 337	android/graphics/Color:parseColor	(Ljava/lang/String;)I
    //   444: istore 8
    //   446: iload 8
    //   448: istore_3
    //   449: iconst_1
    //   450: istore 7
    //   452: aload_0
    //   453: getfield 37	org/vidogram/ui/Cells/ThemeCell:paint	Landroid/graphics/Paint;
    //   456: iload_3
    //   457: invokevirtual 304	android/graphics/Paint:setColor	(I)V
    //   460: iconst_1
    //   461: istore_3
    //   462: goto -217 -> 245
    //   465: astore 15
    //   467: iload_3
    //   468: istore 7
    //   470: aload 14
    //   472: invokestatic 343	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   475: invokevirtual 348	java/lang/Integer:intValue	()I
    //   478: istore_3
    //   479: goto -30 -> 449
    //   482: iload_3
    //   483: istore 7
    //   485: aload 14
    //   487: invokestatic 343	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   490: invokevirtual 348	java/lang/Integer:intValue	()I
    //   493: istore_3
    //   494: goto -45 -> 449
    //   497: iload 9
    //   499: iload 10
    //   501: iadd
    //   502: istore 12
    //   504: iload 6
    //   506: iload 10
    //   508: iadd
    //   509: istore 10
    //   511: iload 4
    //   513: istore 11
    //   515: iload 8
    //   517: iconst_1
    //   518: iadd
    //   519: istore 8
    //   521: iload 12
    //   523: istore 9
    //   525: iload 11
    //   527: istore 4
    //   529: iload 10
    //   531: istore 6
    //   533: goto -376 -> 157
    //   536: aload_1
    //   537: invokevirtual 352	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   540: iload 6
    //   542: i2l
    //   543: invokevirtual 358	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   546: pop
    //   547: iload_3
    //   548: ifeq +6 -> 554
    //   551: goto -291 -> 260
    //   554: iload 6
    //   556: istore 5
    //   558: goto -429 -> 129
    //   561: astore_1
    //   562: aload_1
    //   563: invokestatic 315	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   566: iload_3
    //   567: istore 4
    //   569: goto -295 -> 274
    //   572: astore 14
    //   574: aconst_null
    //   575: astore_1
    //   576: aload_1
    //   577: ifnull +7 -> 584
    //   580: aload_1
    //   581: invokevirtual 296	java/io/FileInputStream:close	()V
    //   584: aload 14
    //   586: athrow
    //   587: astore_1
    //   588: aload_1
    //   589: invokestatic 315	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   592: goto -8 -> 584
    //   595: astore 14
    //   597: goto -21 -> 576
    //   600: astore 14
    //   602: goto -26 -> 576
    //   605: astore 14
    //   607: iload 7
    //   609: istore_3
    //   610: goto -288 -> 322
    //   613: astore 14
    //   615: goto -293 -> 322
    //   618: goto -358 -> 260
    //   621: goto -376 -> 245
    //   624: goto -364 -> 260
    //
    // Exception table:
    //   from	to	target	type
    //   100	115	313	java/lang/Throwable
    //   115	124	313	java/lang/Throwable
    //   298	310	313	java/lang/Throwable
    //   334	338	344	java/lang/Exception
    //   439	446	465	java/lang/Exception
    //   267	271	561	java/lang/Exception
    //   100	115	572	finally
    //   115	124	572	finally
    //   298	310	572	finally
    //   580	584	587	java/lang/Exception
    //   132	141	595	finally
    //   179	190	595	finally
    //   208	229	595	finally
    //   232	241	595	finally
    //   358	367	595	finally
    //   376	393	595	finally
    //   396	407	595	finally
    //   410	418	595	finally
    //   421	429	595	finally
    //   439	446	595	finally
    //   452	460	595	finally
    //   470	479	595	finally
    //   485	494	595	finally
    //   536	547	595	finally
    //   322	327	600	finally
    //   132	141	605	java/lang/Throwable
    //   179	190	605	java/lang/Throwable
    //   208	229	605	java/lang/Throwable
    //   232	241	605	java/lang/Throwable
    //   358	367	605	java/lang/Throwable
    //   376	393	605	java/lang/Throwable
    //   396	407	605	java/lang/Throwable
    //   410	418	605	java/lang/Throwable
    //   421	429	605	java/lang/Throwable
    //   439	446	605	java/lang/Throwable
    //   452	460	605	java/lang/Throwable
    //   470	479	605	java/lang/Throwable
    //   485	494	605	java/lang/Throwable
    //   536	547	613	java/lang/Throwable
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ThemeCell
 * JD-Core Version:    0.6.0
 */