package org.vidogram.ui.ActionBar;

import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.ChatBigEmptyView;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.ContextProgressView;
import org.vidogram.ui.Components.EditTextBoldCursor;
import org.vidogram.ui.Components.EditTextCaption;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.GroupCreateCheckBox;
import org.vidogram.ui.Components.GroupCreateSpan;
import org.vidogram.ui.Components.LetterDrawable;
import org.vidogram.ui.Components.LineProgressView;
import org.vidogram.ui.Components.NumberTextView;
import org.vidogram.ui.Components.RadialProgressView;
import org.vidogram.ui.Components.RadioButton;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.Switch;

public class ThemeDescription
{
  public static int FLAG_AB_AM_BACKGROUND;
  public static int FLAG_AB_AM_ITEMSCOLOR;
  public static int FLAG_AB_AM_SELECTORCOLOR;
  public static int FLAG_AB_AM_TOPBACKGROUND;
  public static int FLAG_AB_ITEMSCOLOR;
  public static int FLAG_AB_SEARCH;
  public static int FLAG_AB_SEARCHPLACEHOLDER;
  public static int FLAG_AB_SELECTORCOLOR;
  public static int FLAG_AB_SUBMENUBACKGROUND;
  public static int FLAG_AB_SUBMENUITEM;
  public static int FLAG_AB_SUBTITLECOLOR;
  public static int FLAG_AB_TITLECOLOR;
  public static int FLAG_BACKGROUND = 1;
  public static int FLAG_BACKGROUNDFILTER;
  public static int FLAG_CELLBACKGROUNDCOLOR;
  public static int FLAG_CHECKBOX;
  public static int FLAG_CHECKBOXCHECK;
  public static int FLAG_CHECKTAG;
  public static int FLAG_CURSORCOLOR;
  public static int FLAG_DRAWABLESELECTEDSTATE;
  public static int FLAG_FASTSCROLL;
  public static int FLAG_HINTTEXTCOLOR;
  public static int FLAG_IMAGECOLOR;
  public static int FLAG_LINKCOLOR = 2;
  public static int FLAG_LISTGLOWCOLOR;
  public static int FLAG_PROGRESSBAR;
  public static int FLAG_SECTIONS;
  public static int FLAG_SELECTOR;
  public static int FLAG_SELECTORWHITE;
  public static int FLAG_SERVICEBACKGROUND;
  public static int FLAG_TEXTCOLOR = 4;
  public static int FLAG_USEBACKGROUNDDRAWABLE;
  private HashMap<String, Field> cachedFields;
  private int changeFlags;
  private int currentColor;
  private String currentKey;
  private int defaultColor;
  private ThemeDescriptionDelegate delegate;
  private Drawable[] drawablesToUpdate;
  private Class[] listClasses;
  private String[] listClassesFieldName;
  private Paint[] paintToUpdate;
  private int previousColor;
  private boolean[] previousIsDefault = new boolean[1];
  private View viewToInvalidate;

  static
  {
    FLAG_IMAGECOLOR = 8;
    FLAG_CELLBACKGROUNDCOLOR = 16;
    FLAG_BACKGROUNDFILTER = 32;
    FLAG_AB_ITEMSCOLOR = 64;
    FLAG_AB_TITLECOLOR = 128;
    FLAG_AB_SELECTORCOLOR = 256;
    FLAG_AB_AM_ITEMSCOLOR = 512;
    FLAG_AB_SUBTITLECOLOR = 1024;
    FLAG_PROGRESSBAR = 2048;
    FLAG_SELECTOR = 4096;
    FLAG_CHECKBOX = 8192;
    FLAG_CHECKBOXCHECK = 16384;
    FLAG_LISTGLOWCOLOR = 32768;
    FLAG_DRAWABLESELECTEDSTATE = 65536;
    FLAG_USEBACKGROUNDDRAWABLE = 131072;
    FLAG_CHECKTAG = 262144;
    FLAG_SECTIONS = 524288;
    FLAG_AB_AM_BACKGROUND = 1048576;
    FLAG_AB_AM_TOPBACKGROUND = 2097152;
    FLAG_AB_AM_SELECTORCOLOR = 4194304;
    FLAG_HINTTEXTCOLOR = 8388608;
    FLAG_CURSORCOLOR = 16777216;
    FLAG_FASTSCROLL = 33554432;
    FLAG_AB_SEARCHPLACEHOLDER = 67108864;
    FLAG_AB_SEARCH = 134217728;
    FLAG_SELECTORWHITE = 268435456;
    FLAG_SERVICEBACKGROUND = 536870912;
    FLAG_AB_SUBMENUITEM = 1073741824;
    FLAG_AB_SUBMENUBACKGROUND = -2147483648;
  }

  public ThemeDescription(View paramView, int paramInt, Class[] paramArrayOfClass, Paint paramPaint, Drawable[] paramArrayOfDrawable, ThemeDescriptionDelegate paramThemeDescriptionDelegate, String paramString)
  {
    this.currentKey = paramString;
    if (paramPaint != null)
      this.paintToUpdate = new Paint[] { paramPaint };
    this.drawablesToUpdate = paramArrayOfDrawable;
    this.viewToInvalidate = paramView;
    this.changeFlags = paramInt;
    this.listClasses = paramArrayOfClass;
    this.delegate = paramThemeDescriptionDelegate;
  }

  public ThemeDescription(View paramView, int paramInt, Class[] paramArrayOfClass, Paint[] paramArrayOfPaint, Drawable[] paramArrayOfDrawable, ThemeDescriptionDelegate paramThemeDescriptionDelegate, String paramString, Object paramObject)
  {
    this.currentKey = paramString;
    this.paintToUpdate = paramArrayOfPaint;
    this.drawablesToUpdate = paramArrayOfDrawable;
    this.viewToInvalidate = paramView;
    this.changeFlags = paramInt;
    this.listClasses = paramArrayOfClass;
    this.delegate = paramThemeDescriptionDelegate;
  }

  public ThemeDescription(View paramView, int paramInt, Class[] paramArrayOfClass, String[] paramArrayOfString, Paint[] paramArrayOfPaint, Drawable[] paramArrayOfDrawable, ThemeDescriptionDelegate paramThemeDescriptionDelegate, String paramString)
  {
    this.currentKey = paramString;
    this.paintToUpdate = paramArrayOfPaint;
    this.drawablesToUpdate = paramArrayOfDrawable;
    this.viewToInvalidate = paramView;
    this.changeFlags = paramInt;
    this.listClasses = paramArrayOfClass;
    this.listClassesFieldName = paramArrayOfString;
    this.delegate = paramThemeDescriptionDelegate;
    this.cachedFields = new HashMap();
  }

  private void processViewColor(View paramView, int paramInt)
  {
    int j = 0;
    Object localObject3;
    label139: int i;
    label141: label310: label319: label360: label497: Object localObject2;
    if (j < this.listClasses.length)
    {
      Object localObject1;
      if (this.listClasses[j].isInstance(paramView))
      {
        paramView.invalidate();
        if (((this.changeFlags & FLAG_CHECKTAG) != 0) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || (!this.currentKey.equals(paramView.getTag()))))
          break label447;
        paramView.invalidate();
        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0)
          break label360;
        localObject3 = paramView.getBackground();
        if (localObject3 != null)
        {
          if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0)
            break label319;
          if ((localObject3 instanceof CombinedDrawable))
          {
            localObject1 = ((CombinedDrawable)localObject3).getBackground();
            if ((localObject1 instanceof ColorDrawable))
              ((ColorDrawable)localObject1).setColor(paramInt);
          }
        }
        i = 1;
        if (this.listClassesFieldName == null)
          break label1378;
      }
      while (true)
      {
        try
        {
          String str = this.listClasses[j] + "_" + this.listClassesFieldName[j];
          localObject3 = (Field)this.cachedFields.get(str);
          localObject1 = localObject3;
          if (localObject3 != null)
            continue;
          localObject3 = this.listClasses[j].getDeclaredField(this.listClassesFieldName[j]);
          localObject1 = localObject3;
          if (localObject3 == null)
            continue;
          ((Field)localObject3).setAccessible(true);
          this.cachedFields.put(str, localObject3);
          localObject1 = localObject3;
          if (localObject1 == null)
            continue;
          localObject3 = ((Field)localObject1).get(paramView);
          if (localObject3 == null)
            continue;
          if ((i != 0) || (!(localObject3 instanceof View)))
            continue;
          bool = this.currentKey.equals(((View)localObject3).getTag());
          if (bool)
            continue;
          j += 1;
          break;
          localObject1 = localObject3;
          if (!(localObject3 instanceof CombinedDrawable))
            continue;
          localObject1 = ((CombinedDrawable)localObject3).getIcon();
          ((Drawable)localObject1).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          break label139;
          if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0)
            continue;
          paramView.setBackgroundColor(paramInt);
          i = 1;
          break label141;
          if ((this.changeFlags & FLAG_TEXTCOLOR) == 0)
            continue;
          if (!(paramView instanceof TextView))
            break label1399;
          ((TextView)paramView).setTextColor(paramInt);
          i = 1;
          break label141;
          if ((this.changeFlags & FLAG_SERVICEBACKGROUND) == 0)
            break label1399;
          localObject1 = paramView.getBackground();
          if (localObject1 == null)
            continue;
          ((Drawable)localObject1).setColorFilter(Theme.colorFilter);
          i = 1;
          break label141;
          label447: i = 0;
          break label141;
          if (!(localObject3 instanceof View))
            continue;
          ((View)localObject3).invalidate();
          if (((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) == 0) || (!(localObject3 instanceof View)))
            break label1396;
          localObject3 = ((View)localObject3).getBackground();
          if (((this.changeFlags & FLAG_BACKGROUND) == 0) || (!(localObject3 instanceof View)))
            continue;
          ((View)localObject3).setBackgroundColor(paramInt);
          continue;
        }
        catch (Throwable localObject2)
        {
          FileLog.e(localThrowable);
          continue;
          if (!(localObject3 instanceof Switch))
            continue;
          ((Switch)localObject3).checkColorFilters();
          continue;
          if (!(localObject3 instanceof EditTextCaption))
            continue;
          if ((this.changeFlags & FLAG_HINTTEXTCOLOR) == 0)
            continue;
          ((EditTextCaption)localObject3).setHintColor(paramInt);
          ((EditTextCaption)localObject3).setHintTextColor(paramInt);
          continue;
          ((EditTextCaption)localObject3).setTextColor(paramInt);
          continue;
          if (!(localObject3 instanceof SimpleTextView))
            continue;
          if ((this.changeFlags & FLAG_LINKCOLOR) == 0)
            continue;
          ((SimpleTextView)localObject3).setLinkTextColor(paramInt);
          continue;
          ((SimpleTextView)localObject3).setTextColor(paramInt);
          continue;
          if (!(localObject3 instanceof TextView))
            continue;
          if ((this.changeFlags & FLAG_IMAGECOLOR) == 0)
            continue;
          localObject2 = ((TextView)localObject3).getCompoundDrawables();
          if (localObject2 == null)
            continue;
          i = 0;
          if (i >= localObject2.length)
            continue;
          localObject2[i].setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          i += 1;
          continue;
          if ((this.changeFlags & FLAG_LINKCOLOR) == 0)
            continue;
          ((TextView)localObject3).getPaint().linkColor = paramInt;
          ((TextView)localObject3).invalidate();
          continue;
          ((TextView)localObject3).setTextColor(paramInt);
          continue;
          if (!(localObject3 instanceof ImageView))
            continue;
          ((ImageView)localObject3).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          continue;
          if (!(localObject3 instanceof BackupImageView))
            continue;
          localObject2 = ((BackupImageView)localObject3).getImageReceiver().getStaticThumb();
          if (!(localObject2 instanceof CombinedDrawable))
            continue;
          if ((this.changeFlags & FLAG_BACKGROUNDFILTER) == 0)
            continue;
          ((CombinedDrawable)localObject2).getBackground().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          continue;
          ((CombinedDrawable)localObject2).getIcon().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          continue;
          if (localObject2 == null)
            continue;
          ((Drawable)localObject2).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          continue;
          if (!(localObject3 instanceof Drawable))
            break label1108;
        }
        if ((localObject3 instanceof LetterDrawable))
        {
          if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0)
          {
            ((LetterDrawable)localObject3).setBackgroundColor(paramInt);
            continue;
          }
          ((LetterDrawable)localObject3).setColor(paramInt);
          continue;
        }
        if (!(localObject3 instanceof CombinedDrawable))
          break label1030;
        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0)
        {
          ((CombinedDrawable)localObject3).getBackground().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
          continue;
        }
        ((CombinedDrawable)localObject3).getIcon().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      }
      label1030: if (((localObject3 instanceof StateListDrawable)) || ((Build.VERSION.SDK_INT >= 21) && ((localObject3 instanceof RippleDrawable))))
      {
        localObject2 = (Drawable)localObject3;
        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0)
          break label1404;
      }
    }
    label1108: label1378: label1396: label1399: label1404: for (boolean bool = true; ; bool = false)
    {
      Theme.setSelectorDrawableColor((Drawable)localObject2, paramInt, bool);
      break label310;
      ((Drawable)localObject3).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      break label310;
      if ((localObject3 instanceof CheckBox))
      {
        if ((this.changeFlags & FLAG_CHECKBOX) != 0)
        {
          ((CheckBox)localObject3).setBackgroundColor(paramInt);
          break label310;
        }
        if ((this.changeFlags & FLAG_CHECKBOXCHECK) == 0)
          break label310;
        ((CheckBox)localObject3).setCheckColor(paramInt);
        break label310;
      }
      if ((localObject3 instanceof GroupCreateCheckBox))
      {
        ((GroupCreateCheckBox)localObject3).updateColors();
        break label310;
      }
      if ((localObject3 instanceof Integer))
      {
        ((Field)localObject2).set(paramView, Integer.valueOf(paramInt));
        break label310;
      }
      if ((localObject3 instanceof RadioButton))
      {
        if ((this.changeFlags & FLAG_CHECKBOX) != 0)
        {
          ((RadioButton)localObject3).setBackgroundColor(paramInt);
          ((RadioButton)localObject3).invalidate();
          break label310;
        }
        if ((this.changeFlags & FLAG_CHECKBOXCHECK) == 0)
          break label310;
        ((RadioButton)localObject3).setCheckedColor(paramInt);
        ((RadioButton)localObject3).invalidate();
        break label310;
      }
      if ((localObject3 instanceof TextPaint))
      {
        if ((this.changeFlags & FLAG_LINKCOLOR) != 0)
        {
          ((TextPaint)localObject3).linkColor = paramInt;
          break label310;
        }
        ((TextPaint)localObject3).setColor(paramInt);
        break label310;
      }
      if ((localObject3 instanceof LineProgressView))
      {
        if ((this.changeFlags & FLAG_PROGRESSBAR) != 0)
        {
          ((LineProgressView)localObject3).setProgressColor(paramInt);
          break label310;
        }
        ((LineProgressView)localObject3).setBackColor(paramInt);
        break label310;
      }
      if (!(localObject3 instanceof Paint))
        break label310;
      ((Paint)localObject3).setColor(paramInt);
      break label310;
      if (!(paramView instanceof GroupCreateSpan))
        break label310;
      ((GroupCreateSpan)paramView).updateColors();
      break label310;
      return;
      break label497;
      i = 1;
      break;
    }
  }

  public int getCurrentColor()
  {
    return this.currentColor;
  }

  public String getCurrentKey()
  {
    return this.currentKey;
  }

  public int getSetColor()
  {
    return Theme.getColor(this.currentKey);
  }

  public String getTitle()
  {
    return this.currentKey;
  }

  public void setColor(int paramInt, boolean paramBoolean)
  {
    int j = 0;
    Theme.setColor(this.currentKey, paramInt, paramBoolean);
    int i;
    if (this.paintToUpdate != null)
    {
      i = 0;
      if (i < this.paintToUpdate.length)
      {
        if (((this.changeFlags & FLAG_LINKCOLOR) != 0) && ((this.paintToUpdate[i] instanceof TextPaint)))
          ((TextPaint)this.paintToUpdate[i]).linkColor = paramInt;
        while (true)
        {
          i += 1;
          break;
          this.paintToUpdate[i].setColor(paramInt);
        }
      }
    }
    if (this.drawablesToUpdate != null)
    {
      i = 0;
      if (i < this.drawablesToUpdate.length)
      {
        if (this.drawablesToUpdate[i] == null);
        while (true)
        {
          i += 1;
          break;
          if ((this.drawablesToUpdate[i] instanceof CombinedDrawable))
          {
            if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0)
            {
              ((CombinedDrawable)this.drawablesToUpdate[i]).getBackground().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
              continue;
            }
            ((CombinedDrawable)this.drawablesToUpdate[i]).getIcon().setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
            continue;
          }
          if ((this.drawablesToUpdate[i] instanceof AvatarDrawable))
          {
            ((AvatarDrawable)this.drawablesToUpdate[i]).setColor(paramInt);
            continue;
          }
          this.drawablesToUpdate[i].setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
        }
      }
    }
    Object localObject;
    if ((this.viewToInvalidate != null) && (this.listClasses == null) && (this.listClassesFieldName == null) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || (((this.changeFlags & FLAG_CHECKTAG) != 0) && (this.currentKey.equals(this.viewToInvalidate.getTag())))))
    {
      if ((this.changeFlags & FLAG_BACKGROUND) != 0)
        this.viewToInvalidate.setBackgroundColor(paramInt);
      if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0)
      {
        localObject = this.viewToInvalidate.getBackground();
        if (!(localObject instanceof CombinedDrawable))
          break label1738;
        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0)
          break label1240;
        localObject = ((CombinedDrawable)localObject).getBackground();
      }
    }
    label422: label429: label840: label1738: 
    while (true)
    {
      if (localObject != null)
      {
        if ((!(localObject instanceof StateListDrawable)) && ((Build.VERSION.SDK_INT < 21) || (!(localObject instanceof RippleDrawable))))
          break label1258;
        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0)
        {
          paramBoolean = true;
          Theme.setSelectorDrawableColor((Drawable)localObject, paramInt, paramBoolean);
        }
      }
      else
      {
        if ((this.viewToInvalidate instanceof ActionBar))
        {
          if ((this.changeFlags & FLAG_AB_ITEMSCOLOR) != 0)
            ((ActionBar)this.viewToInvalidate).setItemsColor(paramInt, false);
          if ((this.changeFlags & FLAG_AB_TITLECOLOR) != 0)
            ((ActionBar)this.viewToInvalidate).setTitleColor(paramInt);
          if ((this.changeFlags & FLAG_AB_SELECTORCOLOR) != 0)
            ((ActionBar)this.viewToInvalidate).setItemsBackgroundColor(paramInt, false);
          if ((this.changeFlags & FLAG_AB_AM_SELECTORCOLOR) != 0)
            ((ActionBar)this.viewToInvalidate).setItemsBackgroundColor(paramInt, true);
          if ((this.changeFlags & FLAG_AB_AM_ITEMSCOLOR) != 0)
            ((ActionBar)this.viewToInvalidate).setItemsColor(paramInt, true);
          if ((this.changeFlags & FLAG_AB_SUBTITLECOLOR) != 0)
            ((ActionBar)this.viewToInvalidate).setSubtitleColor(paramInt);
          if ((this.changeFlags & FLAG_AB_AM_BACKGROUND) != 0)
            ((ActionBar)this.viewToInvalidate).setActionModeColor(paramInt);
          if ((this.changeFlags & FLAG_AB_AM_TOPBACKGROUND) != 0)
            ((ActionBar)this.viewToInvalidate).setActionModeTopColor(paramInt);
          if ((this.changeFlags & FLAG_AB_SEARCHPLACEHOLDER) != 0)
            ((ActionBar)this.viewToInvalidate).setSearchTextColor(paramInt, true);
          if ((this.changeFlags & FLAG_AB_SEARCH) != 0)
            ((ActionBar)this.viewToInvalidate).setSearchTextColor(paramInt, false);
          if ((this.changeFlags & FLAG_AB_SUBMENUITEM) != 0)
            ((ActionBar)this.viewToInvalidate).setPopupItemsColor(paramInt);
          if ((this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0)
            ((ActionBar)this.viewToInvalidate).setPopupBackgroundColor(paramInt);
        }
        if ((this.viewToInvalidate instanceof EmptyTextProgressView))
        {
          if ((this.changeFlags & FLAG_TEXTCOLOR) == 0)
            break label1277;
          ((EmptyTextProgressView)this.viewToInvalidate).setTextColor(paramInt);
        }
        label741: if (!(this.viewToInvalidate instanceof RadialProgressView))
          break label1302;
        ((RadialProgressView)this.viewToInvalidate).setProgressColor(paramInt);
        label762: if (((this.changeFlags & FLAG_TEXTCOLOR) != 0) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || ((this.viewToInvalidate != null) && ((this.changeFlags & FLAG_CHECKTAG) != 0) && (this.currentKey.equals(this.viewToInvalidate.getTag())))))
        {
          if (!(this.viewToInvalidate instanceof TextView))
            break label1374;
          ((TextView)this.viewToInvalidate).setTextColor(paramInt);
        }
        if (((this.changeFlags & FLAG_CURSORCOLOR) != 0) && ((this.viewToInvalidate instanceof EditTextBoldCursor)))
          ((EditTextBoldCursor)this.viewToInvalidate).setCursorColor(paramInt);
        if ((this.changeFlags & FLAG_HINTTEXTCOLOR) != 0)
        {
          if (!(this.viewToInvalidate instanceof EditTextBoldCursor))
            break label1446;
          ((EditTextBoldCursor)this.viewToInvalidate).setHintColor(paramInt);
        }
        if ((this.viewToInvalidate != null) && ((this.changeFlags & FLAG_SERVICEBACKGROUND) != 0))
        {
          localObject = this.viewToInvalidate.getBackground();
          if (localObject != null)
            ((Drawable)localObject).setColorFilter(Theme.colorFilter);
        }
        if (((this.changeFlags & FLAG_IMAGECOLOR) != 0) && (((this.changeFlags & FLAG_CHECKTAG) == 0) || (((this.changeFlags & FLAG_CHECKTAG) != 0) && (this.currentKey.equals(this.viewToInvalidate.getTag())))))
        {
          if (!(this.viewToInvalidate instanceof ImageView))
            break label1499;
          if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) == 0)
            break label1475;
          localObject = ((ImageView)this.viewToInvalidate).getDrawable();
          if (((localObject instanceof StateListDrawable)) || ((Build.VERSION.SDK_INT >= 21) && ((localObject instanceof RippleDrawable))))
          {
            if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) == 0)
              break label1470;
            paramBoolean = true;
            Theme.setSelectorDrawableColor((Drawable)localObject, paramInt, paramBoolean);
          }
        }
      }
      while (true)
      {
        if (((this.viewToInvalidate instanceof ScrollView)) && ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0))
          AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView)this.viewToInvalidate, paramInt);
        if (!(this.viewToInvalidate instanceof RecyclerListView))
          break label1635;
        localObject = (RecyclerListView)this.viewToInvalidate;
        if (((this.changeFlags & FLAG_SELECTOR) != 0) && (this.currentKey.equals("listSelectorSDK21")))
          ((RecyclerListView)localObject).setListSelectorColor(paramInt);
        if ((this.changeFlags & FLAG_FASTSCROLL) != 0)
          ((RecyclerListView)localObject).updateFastScrollColors();
        if ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0)
          ((RecyclerListView)localObject).setGlowColor(paramInt);
        if ((this.changeFlags & FLAG_SECTIONS) == 0)
          break label1575;
        localArrayList = ((RecyclerListView)localObject).getHeaders();
        if (localArrayList == null)
          break label1512;
        i = 0;
        while (i < localArrayList.size())
        {
          processViewColor((View)localArrayList.get(i), paramInt);
          i += 1;
        }
        label1240: localObject = ((CombinedDrawable)localObject).getIcon();
        break;
        paramBoolean = false;
        break label422;
        label1258: ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
        break label429;
        label1277: if ((this.changeFlags & FLAG_PROGRESSBAR) == 0)
          break label741;
        ((EmptyTextProgressView)this.viewToInvalidate).setProgressBarColor(paramInt);
        break label741;
        if ((this.viewToInvalidate instanceof LineProgressView))
        {
          if ((this.changeFlags & FLAG_PROGRESSBAR) != 0)
          {
            ((LineProgressView)this.viewToInvalidate).setProgressColor(paramInt);
            break label762;
          }
          ((LineProgressView)this.viewToInvalidate).setBackColor(paramInt);
          break label762;
        }
        if (!(this.viewToInvalidate instanceof ContextProgressView))
          break label762;
        ((ContextProgressView)this.viewToInvalidate).updateColors();
        break label762;
        label1374: if ((this.viewToInvalidate instanceof NumberTextView))
        {
          ((NumberTextView)this.viewToInvalidate).setTextColor(paramInt);
          break label840;
        }
        if ((this.viewToInvalidate instanceof SimpleTextView))
        {
          ((SimpleTextView)this.viewToInvalidate).setTextColor(paramInt);
          break label840;
        }
        if (!(this.viewToInvalidate instanceof ChatBigEmptyView))
          break label840;
        ((ChatBigEmptyView)this.viewToInvalidate).setTextColor(paramInt);
        break label840;
        if (!(this.viewToInvalidate instanceof EditText))
          break label904;
        ((EditText)this.viewToInvalidate).setHintTextColor(paramInt);
        break label904;
        paramBoolean = false;
        break label1064;
        ((ImageView)this.viewToInvalidate).setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
        continue;
        label1499: if (!(this.viewToInvalidate instanceof BackupImageView))
          continue;
      }
      label1512: ArrayList localArrayList = ((RecyclerListView)localObject).getHeadersCache();
      if (localArrayList != null)
      {
        i = 0;
        while (i < localArrayList.size())
        {
          processViewColor((View)localArrayList.get(i), paramInt);
          i += 1;
        }
      }
      localObject = ((RecyclerListView)localObject).getPinnedHeader();
      if (localObject != null)
        processViewColor((View)localObject, paramInt);
      while (this.listClasses != null)
      {
        if ((this.viewToInvalidate instanceof ViewGroup))
        {
          localObject = (ViewGroup)this.viewToInvalidate;
          int k = ((ViewGroup)localObject).getChildCount();
          i = j;
          while (true)
            if (i < k)
            {
              processViewColor(((ViewGroup)localObject).getChildAt(i), paramInt);
              i += 1;
              continue;
              label1635: if (this.viewToInvalidate == null)
                break;
              if ((this.changeFlags & FLAG_SELECTOR) != 0)
              {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                break;
              }
              if ((this.changeFlags & FLAG_SELECTORWHITE) == 0)
                break;
              this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
              break;
            }
        }
        processViewColor(this.viewToInvalidate, paramInt);
      }
      this.currentColor = paramInt;
      if (this.delegate != null)
        this.delegate.didSetColor(paramInt);
      if (this.viewToInvalidate != null)
        this.viewToInvalidate.invalidate();
      return;
    }
  }

  public void setDefaultColor()
  {
    setColor(Theme.getDefaultColor(this.currentKey), true);
  }

  public void setPreviousColor()
  {
    setColor(this.previousColor, this.previousIsDefault[0]);
  }

  public void startEditing()
  {
    int i = Theme.getColor(this.currentKey, this.previousIsDefault);
    this.previousColor = i;
    this.currentColor = i;
  }

  public static abstract interface ThemeDescriptionDelegate
  {
    public abstract void didSetColor(int paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ActionBar.ThemeDescription
 * JD-Core Version:    0.6.0
 */