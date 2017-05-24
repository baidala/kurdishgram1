package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.TextColorThemeCell;
import org.vidogram.ui.LaunchActivity;

public class ThemeEditorView
{

  @SuppressLint({"StaticFieldLeak"})
  private static volatile ThemeEditorView Instance = null;
  private ArrayList<ThemeDescription> currentThemeDesription;
  private int currentThemeDesriptionPosition;
  private String currentThemeName;
  private DecelerateInterpolator decelerateInterpolator;
  private EditorAlert editorAlert;
  private final int editorHeight = AndroidUtilities.dp(54.0F);
  private final int editorWidth = AndroidUtilities.dp(54.0F);
  private boolean hidden;
  private Activity parentActivity;
  private SharedPreferences preferences;
  private WallpaperUpdater wallpaperUpdater;
  private WindowManager.LayoutParams windowLayoutParams;
  private WindowManager windowManager;
  private FrameLayout windowView;

  private void animateToBoundsMaybe()
  {
    int i = getSideCoord(true, 0, 0.0F, this.editorWidth);
    int n = getSideCoord(true, 1, 0.0F, this.editorWidth);
    int j = getSideCoord(false, 0, 0.0F, this.editorHeight);
    int k = getSideCoord(false, 1, 0.0F, this.editorHeight);
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject1 = null;
    SharedPreferences.Editor localEditor = this.preferences.edit();
    int m = AndroidUtilities.dp(20.0F);
    if ((Math.abs(i - this.windowLayoutParams.x) <= m) || ((this.windowLayoutParams.x < 0) && (this.windowLayoutParams.x > -this.editorWidth / 4)))
    {
      if (0 == 0)
        localObject1 = new ArrayList();
      localEditor.putInt("sidex", 0);
      if (this.windowView.getAlpha() != 1.0F)
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
      ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { i }));
      i = 0;
      localObject2 = localObject1;
      if (i == 0)
      {
        if ((Math.abs(j - this.windowLayoutParams.y) > m) && (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()))
          break label694;
        localObject2 = localObject1;
        if (localObject1 == null)
          localObject2 = new ArrayList();
        localEditor.putInt("sidey", 0);
        ((ArrayList)localObject2).add(ObjectAnimator.ofInt(this, "y", new int[] { j }));
        localObject1 = localObject2;
      }
    }
    while (true)
    {
      localEditor.commit();
      localObject2 = localObject1;
      if (localObject2 != null)
      {
        if (this.decelerateInterpolator == null)
          this.decelerateInterpolator = new DecelerateInterpolator();
        localObject1 = new AnimatorSet();
        ((AnimatorSet)localObject1).setInterpolator(this.decelerateInterpolator);
        ((AnimatorSet)localObject1).setDuration(150L);
        if (i != 0)
        {
          ((ArrayList)localObject2).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F }));
          ((AnimatorSet)localObject1).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              Theme.saveCurrentTheme(ThemeEditorView.this.currentThemeName, true);
              ThemeEditorView.this.destroy();
            }
          });
        }
        ((AnimatorSet)localObject1).playTogether((Collection)localObject2);
        ((AnimatorSet)localObject1).start();
      }
      return;
      if ((Math.abs(n - this.windowLayoutParams.x) <= m) || ((this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.editorWidth) && (this.windowLayoutParams.x < AndroidUtilities.displaySize.x - this.editorWidth / 4 * 3)))
      {
        localObject1 = localObject3;
        if (0 == 0)
          localObject1 = new ArrayList();
        localEditor.putInt("sidex", 1);
        if (this.windowView.getAlpha() != 1.0F)
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
        ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { n }));
        i = 0;
        break;
      }
      if (this.windowView.getAlpha() != 1.0F)
      {
        localObject1 = localObject4;
        if (0 == 0)
          localObject1 = new ArrayList();
        if (this.windowLayoutParams.x < 0)
          ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { -this.editorWidth }));
        while (true)
        {
          i = 1;
          break;
          ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { AndroidUtilities.displaySize.x }));
        }
      }
      localEditor.putFloat("px", (this.windowLayoutParams.x - i) / (n - i));
      localEditor.putInt("sidex", 2);
      i = 0;
      localObject1 = localObject2;
      break;
      label694: if (Math.abs(k - this.windowLayoutParams.y) <= m)
      {
        localObject2 = localObject1;
        if (localObject1 == null)
          localObject2 = new ArrayList();
        localEditor.putInt("sidey", 1);
        ((ArrayList)localObject2).add(ObjectAnimator.ofInt(this, "y", new int[] { k }));
        localObject1 = localObject2;
        continue;
      }
      localEditor.putFloat("py", (this.windowLayoutParams.y - j) / (k - j));
      localEditor.putInt("sidey", 2);
    }
  }

  public static ThemeEditorView getInstance()
  {
    return Instance;
  }

  private static int getSideCoord(boolean paramBoolean, int paramInt1, float paramFloat, int paramInt2)
  {
    if (paramBoolean)
    {
      paramInt2 = AndroidUtilities.displaySize.x - paramInt2;
      if (paramInt1 != 0)
        break label54;
      paramInt1 = AndroidUtilities.dp(10.0F);
    }
    while (true)
    {
      paramInt2 = paramInt1;
      if (!paramBoolean)
        paramInt2 = paramInt1 + ActionBar.getCurrentActionBarHeight();
      return paramInt2;
      paramInt2 = AndroidUtilities.displaySize.y - paramInt2 - ActionBar.getCurrentActionBarHeight();
      break;
      label54: if (paramInt1 == 1)
      {
        paramInt1 = paramInt2 - AndroidUtilities.dp(10.0F);
        continue;
      }
      paramInt1 = Math.round((paramInt2 - AndroidUtilities.dp(20.0F)) * paramFloat) + AndroidUtilities.dp(10.0F);
    }
  }

  private void hide()
  {
    if (this.parentActivity == null)
      return;
    try
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[] { 1.0F, 0.0F }) });
      localAnimatorSet.setInterpolator(this.decelerateInterpolator);
      localAnimatorSet.setDuration(150L);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if (ThemeEditorView.this.windowView != null)
            ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
        }
      });
      localAnimatorSet.start();
      this.hidden = true;
      return;
    }
    catch (Exception localException)
    {
    }
  }

  private void show()
  {
    if (this.parentActivity == null)
      return;
    try
    {
      this.windowManager.addView(this.windowView, this.windowLayoutParams);
      this.hidden = false;
      showWithAnimation();
      return;
    }
    catch (Exception localException)
    {
    }
  }

  private void showWithAnimation()
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[] { 0.0F, 1.0F }) });
    localAnimatorSet.setInterpolator(this.decelerateInterpolator);
    localAnimatorSet.setDuration(150L);
    localAnimatorSet.start();
  }

  public void close()
  {
    try
    {
      this.windowManager.removeView(this.windowView);
      label13: this.parentActivity = null;
      return;
    }
    catch (Exception localException)
    {
      break label13;
    }
  }

  public void destroy()
  {
    this.wallpaperUpdater.cleanup();
    if ((this.parentActivity == null) || (this.windowView == null))
      return;
    try
    {
      this.windowManager.removeViewImmediate(this.windowView);
      this.windowView = null;
    }
    catch (Exception localException2)
    {
      try
      {
        while (true)
        {
          if (this.editorAlert != null)
          {
            this.editorAlert.dismiss();
            this.editorAlert = null;
          }
          this.parentActivity = null;
          Instance = null;
          return;
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        while (true)
          FileLog.e(localException2);
      }
    }
  }

  public int getX()
  {
    return this.windowLayoutParams.x;
  }

  public int getY()
  {
    return this.windowLayoutParams.y;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (this.wallpaperUpdater != null)
      this.wallpaperUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onConfigurationChanged()
  {
    int i = this.preferences.getInt("sidex", 1);
    int j = this.preferences.getInt("sidey", 0);
    float f1 = this.preferences.getFloat("px", 0.0F);
    float f2 = this.preferences.getFloat("py", 0.0F);
    this.windowLayoutParams.x = getSideCoord(true, i, f1, this.editorWidth);
    this.windowLayoutParams.y = getSideCoord(false, j, f2, this.editorHeight);
    try
    {
      if (this.windowView.getParent() != null)
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  @Keep
  public void setX(int paramInt)
  {
    this.windowLayoutParams.x = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }

  @Keep
  public void setY(int paramInt)
  {
    this.windowLayoutParams.y = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }

  public void show(Activity paramActivity, String paramString)
  {
    if (Instance != null)
      Instance.destroy();
    this.hidden = false;
    this.currentThemeName = paramString;
    this.windowView = new FrameLayout(paramActivity)
    {
      private boolean dragging;
      private float startX;
      private float startY;

      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        return true;
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        float f2 = paramMotionEvent.getRawX();
        float f3 = paramMotionEvent.getRawY();
        float f1;
        int i;
        if (paramMotionEvent.getAction() == 0)
        {
          this.startX = f2;
          this.startY = f3;
          if (this.dragging)
          {
            if (paramMotionEvent.getAction() != 2)
              break label708;
            f1 = this.startX;
            float f4 = this.startY;
            paramMotionEvent = ThemeEditorView.this.windowLayoutParams;
            paramMotionEvent.x = (int)(f2 - f1 + paramMotionEvent.x);
            paramMotionEvent = ThemeEditorView.this.windowLayoutParams;
            paramMotionEvent.y = (int)(f3 - f4 + paramMotionEvent.y);
            i = ThemeEditorView.this.editorWidth / 2;
            if (ThemeEditorView.this.windowLayoutParams.x >= -i)
              break label506;
            ThemeEditorView.this.windowLayoutParams.x = (-i);
            label141: if (ThemeEditorView.this.windowLayoutParams.x >= 0)
              break label572;
            f1 = ThemeEditorView.this.windowLayoutParams.x / i * 0.5F + 1.0F;
          }
        }
        while (true)
        {
          label175: if (ThemeEditorView.this.windowView.getAlpha() != f1)
            ThemeEditorView.this.windowView.setAlpha(f1);
          if (ThemeEditorView.this.windowLayoutParams.y < -0)
          {
            ThemeEditorView.this.windowLayoutParams.y = (-0);
            label228: ThemeEditorView.this.windowManager.updateViewLayout(ThemeEditorView.this.windowView, ThemeEditorView.this.windowLayoutParams);
            this.startX = f2;
            this.startY = f3;
          }
          label506: 
          do
          {
            return true;
            if ((paramMotionEvent.getAction() == 2) && (!this.dragging))
            {
              if ((Math.abs(this.startX - f2) < AndroidUtilities.getPixelsInCM(0.3F, true)) && (Math.abs(this.startY - f3) < AndroidUtilities.getPixelsInCM(0.3F, false)))
                break;
              this.dragging = true;
              this.startX = f2;
              this.startY = f3;
              break;
            }
            if ((paramMotionEvent.getAction() != 1) || (this.dragging) || (ThemeEditorView.this.editorAlert != null))
              break;
            Object localObject = ((LaunchActivity)ThemeEditorView.this.parentActivity).getActionBarLayout();
            if (((ActionBarLayout)localObject).fragmentsStack.isEmpty())
              break;
            localObject = ((BaseFragment)((ActionBarLayout)localObject).fragmentsStack.get(((ActionBarLayout)localObject).fragmentsStack.size() - 1)).getThemeDescriptions();
            if (localObject == null)
              break;
            ThemeEditorView.access$2402(ThemeEditorView.this, new ThemeEditorView.EditorAlert(ThemeEditorView.this, ThemeEditorView.this.parentActivity, localObject));
            ThemeEditorView.this.editorAlert.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
              public void onDismiss(DialogInterface paramDialogInterface)
              {
              }
            });
            ThemeEditorView.this.editorAlert.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
              public void onDismiss(DialogInterface paramDialogInterface)
              {
                ThemeEditorView.access$2402(ThemeEditorView.this, null);
                ThemeEditorView.this.show();
              }
            });
            ThemeEditorView.this.editorAlert.show();
            ThemeEditorView.this.hide();
            break;
            if (ThemeEditorView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width + i)
              break label141;
            ThemeEditorView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width + i);
            break label141;
            if (ThemeEditorView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width)
              break label730;
            f1 = 1.0F - (ThemeEditorView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x + ThemeEditorView.this.windowLayoutParams.width) / i * 0.5F;
            break label175;
            if (ThemeEditorView.this.windowLayoutParams.y <= AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height + 0)
              break label228;
            ThemeEditorView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height + 0);
            break label228;
          }
          while (paramMotionEvent.getAction() != 1);
          label572: label708: this.dragging = false;
          ThemeEditorView.this.animateToBoundsMaybe();
          return true;
          label730: f1 = 1.0F;
        }
      }
    };
    this.windowView.setBackgroundResource(2130838086);
    this.windowManager = ((WindowManager)paramActivity.getSystemService("window"));
    this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
    int i = this.preferences.getInt("sidex", 1);
    int j = this.preferences.getInt("sidey", 0);
    float f1 = this.preferences.getFloat("px", 0.0F);
    float f2 = this.preferences.getFloat("py", 0.0F);
    try
    {
      this.windowLayoutParams = new WindowManager.LayoutParams();
      this.windowLayoutParams.width = this.editorWidth;
      this.windowLayoutParams.height = this.editorHeight;
      this.windowLayoutParams.x = getSideCoord(true, i, f1, this.editorWidth);
      this.windowLayoutParams.y = getSideCoord(false, j, f2, this.editorHeight);
      this.windowLayoutParams.format = -3;
      this.windowLayoutParams.gravity = 51;
      this.windowLayoutParams.type = 99;
      this.windowLayoutParams.flags = 16777736;
      this.windowManager.addView(this.windowView, this.windowLayoutParams);
      this.wallpaperUpdater = new WallpaperUpdater(paramActivity, new WallpaperUpdater.WallpaperUpdaterDelegate(paramString)
      {
        public void didSelectWallpaper(File paramFile, Bitmap paramBitmap)
        {
          Theme.setThemeWallpaper(this.val$themeName, paramBitmap, paramFile);
        }

        public void needOpenColorPicker()
        {
          int i = 0;
          while (i < ThemeEditorView.this.currentThemeDesription.size())
          {
            ThemeDescription localThemeDescription = (ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i);
            localThemeDescription.startEditing();
            if (i == 0)
              ThemeEditorView.EditorAlert.access$900(ThemeEditorView.this.editorAlert).setColor(localThemeDescription.getCurrentColor());
            i += 1;
          }
          ThemeEditorView.EditorAlert.access$1700(ThemeEditorView.this.editorAlert, true);
        }
      });
      Instance = this;
      this.parentActivity = paramActivity;
      showWithAnimation();
      return;
    }
    catch (Exception paramActivity)
    {
      FileLog.e(paramActivity);
    }
  }

  public class EditorAlert extends BottomSheet
  {
    private boolean animationInProgress;
    private FrameLayout bottomLayout;
    private FrameLayout bottomSaveLayout;
    private TextView cancelButton;
    private AnimatorSet colorChangeAnimation;
    private ColorPicker colorPicker;
    private TextView defaultButtom;
    private boolean ignoreTextChange;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int previousScrollPosition;
    private TextView saveButton;
    private int scrollOffsetY;
    private View shadow;
    private Drawable shadowDrawable;
    private boolean startedColorChange;
    private int topBeforeSwitch;

    public EditorAlert(Context paramArrayOfThemeDescription, ThemeDescription[] arg3)
    {
      super(true);
      this.shadowDrawable = paramArrayOfThemeDescription.getResources().getDrawable(2130838062).mutate();
      this.containerView = new FrameLayout(paramArrayOfThemeDescription, ThemeEditorView.this)
      {
        private boolean ignoreLayout = false;

        protected void onDraw(Canvas paramCanvas)
        {
          ThemeEditorView.EditorAlert.this.shadowDrawable.setBounds(0, ThemeEditorView.EditorAlert.this.scrollOffsetY - ThemeEditorView.EditorAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
          ThemeEditorView.EditorAlert.this.shadowDrawable.draw(paramCanvas);
        }

        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          if ((paramMotionEvent.getAction() == 0) && (ThemeEditorView.EditorAlert.this.scrollOffsetY != 0) && (paramMotionEvent.getY() < ThemeEditorView.EditorAlert.this.scrollOffsetY))
          {
            ThemeEditorView.EditorAlert.this.dismiss();
            return true;
          }
          return super.onInterceptTouchEvent(paramMotionEvent);
        }

        protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        {
          super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
          ThemeEditorView.EditorAlert.this.updateLayout();
        }

        protected void onMeasure(int paramInt1, int paramInt2)
        {
          int j = View.MeasureSpec.getSize(paramInt1);
          int i = View.MeasureSpec.getSize(paramInt2);
          paramInt2 = i;
          if (Build.VERSION.SDK_INT >= 21)
            paramInt2 = i - AndroidUtilities.statusBarHeight;
          i = paramInt2 - Math.min(j, paramInt2);
          if (ThemeEditorView.EditorAlert.this.listView.getPaddingTop() != i)
          {
            this.ignoreLayout = true;
            ThemeEditorView.EditorAlert.this.listView.getPaddingTop();
            ThemeEditorView.EditorAlert.this.listView.setPadding(0, i, 0, AndroidUtilities.dp(48.0F));
            if (ThemeEditorView.EditorAlert.this.colorPicker.getVisibility() == 0)
            {
              ThemeEditorView.EditorAlert.access$702(ThemeEditorView.EditorAlert.this, ThemeEditorView.EditorAlert.this.listView.getPaddingTop());
              ThemeEditorView.EditorAlert.this.listView.setTopGlowOffset(ThemeEditorView.EditorAlert.this.scrollOffsetY);
              ThemeEditorView.EditorAlert.this.colorPicker.setTranslationY(ThemeEditorView.EditorAlert.this.scrollOffsetY);
              ThemeEditorView.EditorAlert.access$1002(ThemeEditorView.EditorAlert.this, 0);
            }
            this.ignoreLayout = false;
          }
          super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824));
        }

        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          return (!ThemeEditorView.EditorAlert.this.isDismissed()) && (super.onTouchEvent(paramMotionEvent));
        }

        public void requestLayout()
        {
          if (this.ignoreLayout)
            return;
          super.requestLayout();
        }
      };
      this.containerView.setWillNotDraw(false);
      this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
      this.listView = new RecyclerListView(paramArrayOfThemeDescription);
      this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0F));
      this.listView.setClipToPadding(false);
      RecyclerListView localRecyclerListView = this.listView;
      LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getContext());
      this.layoutManager = localLinearLayoutManager;
      localRecyclerListView.setLayoutManager(localLinearLayoutManager);
      this.listView.setHorizontalScrollBarEnabled(false);
      this.listView.setVerticalScrollBarEnabled(false);
      this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      localRecyclerListView = this.listView;
      Object localObject = new ListAdapter(paramArrayOfThemeDescription, localObject);
      this.listAdapter = ((ListAdapter)localObject);
      localRecyclerListView.setAdapter((RecyclerView.Adapter)localObject);
      this.listView.setGlowColor(-657673);
      this.listView.setItemAnimator(null);
      this.listView.setLayoutAnimation(null);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener(ThemeEditorView.this)
      {
        public void onItemClick(View paramView, int paramInt)
        {
          ThemeEditorView.access$202(ThemeEditorView.this, ThemeEditorView.EditorAlert.this.listAdapter.getItem(paramInt));
          ThemeEditorView.access$1502(ThemeEditorView.this, paramInt);
          paramInt = 0;
          while (paramInt < ThemeEditorView.this.currentThemeDesription.size())
          {
            paramView = (ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(paramInt);
            if (paramView.getCurrentKey().equals("chat_wallpaper"))
            {
              ThemeEditorView.this.wallpaperUpdater.showAlert(true);
              return;
            }
            paramView.startEditing();
            if (paramInt == 0)
              ThemeEditorView.EditorAlert.this.colorPicker.setColor(paramView.getCurrentColor());
            paramInt += 1;
          }
          ThemeEditorView.EditorAlert.this.setColorPickerVisible(true);
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener(ThemeEditorView.this)
      {
        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          ThemeEditorView.EditorAlert.this.updateLayout();
        }
      });
      this.colorPicker = new ColorPicker(paramArrayOfThemeDescription);
      this.colorPicker.setVisibility(8);
      this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
      this.shadow = new View(paramArrayOfThemeDescription);
      this.shadow.setBackgroundResource(2130837729);
      this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      this.bottomSaveLayout = new FrameLayout(paramArrayOfThemeDescription);
      this.bottomSaveLayout.setBackgroundColor(-1);
      this.containerView.addView(this.bottomSaveLayout, LayoutHelper.createFrame(-1, 48, 83));
      localObject = new TextView(paramArrayOfThemeDescription);
      ((TextView)localObject).setTextSize(1, 14.0F);
      ((TextView)localObject).setTextColor(-15095832);
      ((TextView)localObject).setGravity(17);
      ((TextView)localObject).setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      ((TextView)localObject).setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      ((TextView)localObject).setText(LocaleController.getString("CloseEditor", 2131165557).toUpperCase());
      ((TextView)localObject).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.bottomSaveLayout.addView((View)localObject, LayoutHelper.createFrame(-2, -1, 51));
      ((TextView)localObject).setOnClickListener(new View.OnClickListener(ThemeEditorView.this)
      {
        public void onClick(View paramView)
        {
          ThemeEditorView.EditorAlert.this.dismiss();
        }
      });
      localObject = new TextView(paramArrayOfThemeDescription);
      ((TextView)localObject).setTextSize(1, 14.0F);
      ((TextView)localObject).setTextColor(-15095832);
      ((TextView)localObject).setGravity(17);
      ((TextView)localObject).setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      ((TextView)localObject).setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      ((TextView)localObject).setText(LocaleController.getString("SaveTheme", 2131166372).toUpperCase());
      ((TextView)localObject).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.bottomSaveLayout.addView((View)localObject, LayoutHelper.createFrame(-2, -1, 53));
      ((TextView)localObject).setOnClickListener(new View.OnClickListener(ThemeEditorView.this)
      {
        public void onClick(View paramView)
        {
          Theme.saveCurrentTheme(ThemeEditorView.this.currentThemeName, true);
          ThemeEditorView.EditorAlert.this.setOnDismissListener(null);
          ThemeEditorView.EditorAlert.this.dismiss();
          ThemeEditorView.this.close();
        }
      });
      this.bottomLayout = new FrameLayout(paramArrayOfThemeDescription);
      this.bottomLayout.setVisibility(8);
      this.bottomLayout.setBackgroundColor(-1);
      this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
      this.cancelButton = new TextView(paramArrayOfThemeDescription);
      this.cancelButton.setTextSize(1, 14.0F);
      this.cancelButton.setTextColor(-15095832);
      this.cancelButton.setGravity(17);
      this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      this.cancelButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      this.cancelButton.setText(LocaleController.getString("Cancel", 2131165427).toUpperCase());
      this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.bottomLayout.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
      this.cancelButton.setOnClickListener(new View.OnClickListener(ThemeEditorView.this)
      {
        public void onClick(View paramView)
        {
          int i = 0;
          while (i < ThemeEditorView.this.currentThemeDesription.size())
          {
            ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i)).setPreviousColor();
            i += 1;
          }
          ThemeEditorView.EditorAlert.this.setColorPickerVisible(false);
        }
      });
      localObject = new LinearLayout(paramArrayOfThemeDescription);
      ((LinearLayout)localObject).setOrientation(0);
      this.bottomLayout.addView((View)localObject, LayoutHelper.createFrame(-2, -1, 53));
      this.defaultButtom = new TextView(paramArrayOfThemeDescription);
      this.defaultButtom.setTextSize(1, 14.0F);
      this.defaultButtom.setTextColor(-15095832);
      this.defaultButtom.setGravity(17);
      this.defaultButtom.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      this.defaultButtom.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      this.defaultButtom.setText(LocaleController.getString("Default", 2131165626).toUpperCase());
      this.defaultButtom.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((LinearLayout)localObject).addView(this.defaultButtom, LayoutHelper.createFrame(-2, -1, 51));
      this.defaultButtom.setOnClickListener(new View.OnClickListener(ThemeEditorView.this)
      {
        public void onClick(View paramView)
        {
          int i = 0;
          while (i < ThemeEditorView.this.currentThemeDesription.size())
          {
            ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i)).setDefaultColor();
            i += 1;
          }
          ThemeEditorView.EditorAlert.this.setColorPickerVisible(false);
        }
      });
      paramArrayOfThemeDescription = new TextView(paramArrayOfThemeDescription);
      paramArrayOfThemeDescription.setTextSize(1, 14.0F);
      paramArrayOfThemeDescription.setTextColor(-15095832);
      paramArrayOfThemeDescription.setGravity(17);
      paramArrayOfThemeDescription.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      paramArrayOfThemeDescription.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramArrayOfThemeDescription.setText(LocaleController.getString("Save", 2131166371).toUpperCase());
      paramArrayOfThemeDescription.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((LinearLayout)localObject).addView(paramArrayOfThemeDescription, LayoutHelper.createFrame(-2, -1, 51));
      paramArrayOfThemeDescription.setOnClickListener(new View.OnClickListener(ThemeEditorView.this)
      {
        public void onClick(View paramView)
        {
          ThemeEditorView.EditorAlert.this.setColorPickerVisible(false);
        }
      });
    }

    private int getCurrentTop()
    {
      if (this.listView.getChildCount() != 0)
      {
        View localView = this.listView.getChildAt(0);
        RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
        if (localHolder != null)
        {
          int j = this.listView.getPaddingTop();
          if ((localHolder.getAdapterPosition() == 0) && (localView.getTop() >= 0));
          for (int i = localView.getTop(); ; i = 0)
            return j - i;
        }
      }
      return -1000;
    }

    private void setColorPickerVisible(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.animationInProgress = true;
        this.colorPicker.setVisibility(0);
        this.bottomLayout.setVisibility(0);
        this.colorPicker.setAlpha(0.0F);
        this.bottomLayout.setAlpha(0.0F);
        localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.listView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofInt(this, "scrollOffsetY", new int[] { this.listView.getPaddingTop() }) });
        localAnimatorSet.setDuration(150L);
        localAnimatorSet.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            ThemeEditorView.EditorAlert.this.listView.setVisibility(4);
            ThemeEditorView.EditorAlert.this.bottomSaveLayout.setVisibility(4);
            ThemeEditorView.EditorAlert.access$2102(ThemeEditorView.EditorAlert.this, false);
          }
        });
        localAnimatorSet.start();
        this.previousScrollPosition = this.scrollOffsetY;
        return;
      }
      if (ThemeEditorView.this.parentActivity != null)
        ((LaunchActivity)ThemeEditorView.this.parentActivity).rebuildAllFragments(false);
      Theme.saveCurrentTheme(ThemeEditorView.this.currentThemeName, false);
      AndroidUtilities.hideKeyboard(getCurrentFocus());
      this.animationInProgress = true;
      this.listView.setVisibility(0);
      this.bottomSaveLayout.setVisibility(0);
      this.listView.setAlpha(0.0F);
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.listView, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[] { 1.0F }), ObjectAnimator.ofInt(this, "scrollOffsetY", new int[] { this.previousScrollPosition }) });
      localAnimatorSet.setDuration(150L);
      localAnimatorSet.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          ThemeEditorView.EditorAlert.this.colorPicker.setVisibility(8);
          ThemeEditorView.EditorAlert.this.bottomLayout.setVisibility(8);
          ThemeEditorView.EditorAlert.access$2102(ThemeEditorView.EditorAlert.this, false);
        }
      });
      localAnimatorSet.start();
      this.listAdapter.notifyItemChanged(ThemeEditorView.this.currentThemeDesriptionPosition);
    }

    @SuppressLint({"NewApi"})
    private void updateLayout()
    {
      if ((this.listView.getChildCount() <= 0) || (this.listView.getVisibility() != 0) || (this.animationInProgress));
      label118: label121: 
      while (true)
      {
        return;
        View localView = this.listView.getChildAt(0);
        RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
        int i;
        if ((this.listView.getVisibility() != 0) || (this.animationInProgress))
        {
          i = this.listView.getPaddingTop();
          if ((i <= 0) || (localHolder == null) || (localHolder.getAdapterPosition() != 0))
            break label118;
        }
        while (true)
        {
          if (this.scrollOffsetY == i)
            break label121;
          setScrollOffsetY(i);
          return;
          i = localView.getTop() - AndroidUtilities.dp(8.0F);
          break;
          i = 0;
        }
      }
    }

    protected boolean canDismissWithSwipe()
    {
      return false;
    }

    public int getScrollOffsetY()
    {
      return this.scrollOffsetY;
    }

    @Keep
    public void setScrollOffsetY(int paramInt)
    {
      RecyclerListView localRecyclerListView = this.listView;
      this.scrollOffsetY = paramInt;
      localRecyclerListView.setTopGlowOffset(paramInt);
      this.colorPicker.setTranslationY(this.scrollOffsetY);
      this.containerView.invalidate();
    }

    private class ColorPicker extends FrameLayout
    {
      private float alpha = 1.0F;
      private LinearGradient alphaGradient;
      private boolean alphaPressed;
      private Drawable circleDrawable;
      private Paint circlePaint;
      private boolean circlePressed;
      private EditText[] colorEditText = new EditText[4];
      private LinearGradient colorGradient;
      private float[] colorHSV = { 0.0F, 0.0F, 1.0F };
      private boolean colorPressed;
      private Bitmap colorWheelBitmap;
      private Paint colorWheelPaint;
      private int colorWheelRadius;
      private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
      private float[] hsvTemp = new float[3];
      private LinearLayout linearLayout;
      private final int paramValueSliderWidth = AndroidUtilities.dp(20.0F);
      private Paint valueSliderPaint;

      public ColorPicker(Context arg2)
      {
        super();
        setWillNotDraw(false);
        this.circlePaint = new Paint(1);
        this.circleDrawable = localContext.getResources().getDrawable(2130837882).mutate();
        this.colorWheelPaint = new Paint();
        this.colorWheelPaint.setAntiAlias(true);
        this.colorWheelPaint.setDither(true);
        this.valueSliderPaint = new Paint();
        this.valueSliderPaint.setAntiAlias(true);
        this.valueSliderPaint.setDither(true);
        this.linearLayout = new LinearLayout(localContext);
        this.linearLayout.setOrientation(0);
        addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 49));
        int i = 0;
        if (i < 4)
        {
          this.colorEditText[i] = new EditText(localContext);
          this.colorEditText[i].setInputType(2);
          this.colorEditText[i].setTextColor(-14606047);
          AndroidUtilities.clearCursorDrawable(this.colorEditText[i]);
          this.colorEditText[i].setTextSize(1, 18.0F);
          this.colorEditText[i].setBackgroundDrawable(Theme.createEditTextDrawable(localContext, true));
          this.colorEditText[i].setMaxLines(1);
          this.colorEditText[i].setTag(Integer.valueOf(i));
          this.colorEditText[i].setGravity(17);
          label333: Object localObject;
          int j;
          label352: EditText localEditText;
          float f;
          if (i == 0)
          {
            this.colorEditText[i].setHint("red");
            localObject = this.colorEditText[i];
            if (i != 3)
              break label545;
            j = 6;
            ((EditText)localObject).setImeOptions(j | 0x10000000);
            localObject = new InputFilter.LengthFilter(3);
            this.colorEditText[i].setFilters(new InputFilter[] { localObject });
            localObject = this.linearLayout;
            localEditText = this.colorEditText[i];
            if (i == 3)
              break label551;
            f = 16.0F;
          }
          while (true)
          {
            ((LinearLayout)localObject).addView(localEditText, LayoutHelper.createLinear(55, 36, 0.0F, 0.0F, f, 0.0F));
            this.colorEditText[i].addTextChangedListener(new TextWatcher(ThemeEditorView.EditorAlert.this, i)
            {
              public void afterTextChanged(Editable paramEditable)
              {
                int i = 255;
                if (ThemeEditorView.EditorAlert.this.ignoreTextChange)
                  return;
                ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, true);
                int j = Utilities.parseInt(paramEditable.toString()).intValue();
                if (j < 0)
                {
                  ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[this.val$num].setText("" + 0);
                  ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[this.val$num].setSelection(ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[this.val$num].length());
                  i = 0;
                }
                while (true)
                {
                  j = ThemeEditorView.EditorAlert.ColorPicker.this.getColor();
                  if (this.val$num == 2)
                    i = i & 0xFF | j & 0xFFFFFF00;
                  while (true)
                  {
                    label139: ThemeEditorView.EditorAlert.ColorPicker.this.setColor(i);
                    i = 0;
                    while (true)
                      if (i < ThemeEditorView.this.currentThemeDesription.size())
                      {
                        ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i)).setColor(ThemeEditorView.EditorAlert.ColorPicker.this.getColor(), false);
                        i += 1;
                        continue;
                        if (j <= 255)
                          break label375;
                        ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[this.val$num].setText("" + 255);
                        ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[this.val$num].setSelection(ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[this.val$num].length());
                        break;
                        if (this.val$num == 1)
                        {
                          i = (i & 0xFF) << 8 | j & 0xFFFF00FF;
                          break label139;
                        }
                        if (this.val$num == 0)
                        {
                          i = (i & 0xFF) << 16 | j & 0xFF00FFFF;
                          break label139;
                        }
                        if (this.val$num != 3)
                          break label370;
                        i = (i & 0xFF) << 24 | j & 0xFFFFFF;
                        break label139;
                      }
                    ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, false);
                    return;
                    label370: i = j;
                  }
                  label375: i = j;
                }
              }

              public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
              {
              }

              public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
              {
              }
            });
            this.colorEditText[i].setOnEditorActionListener(new TextView.OnEditorActionListener(ThemeEditorView.EditorAlert.this)
            {
              public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
              {
                if (paramInt == 6)
                {
                  AndroidUtilities.hideKeyboard(paramTextView);
                  return true;
                }
                return false;
              }
            });
            i += 1;
            break;
            if (i == 1)
            {
              this.colorEditText[i].setHint("green");
              break label333;
            }
            if (i == 2)
            {
              this.colorEditText[i].setHint("blue");
              break label333;
            }
            if (i != 3)
              break label333;
            this.colorEditText[i].setHint("alpha");
            break label333;
            label545: j = 5;
            break label352;
            label551: f = 0.0F;
          }
        }
      }

      private Bitmap createColorWheelBitmap(int paramInt1, int paramInt2)
      {
        Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
        Object localObject = new int[13];
        float[] arrayOfFloat = new float[3];
        float[] tmp23_21 = arrayOfFloat;
        tmp23_21[0] = 0.0F;
        float[] tmp27_23 = tmp23_21;
        tmp27_23[1] = 1.0F;
        float[] tmp31_27 = tmp27_23;
        tmp31_27[2] = 1.0F;
        tmp31_27;
        int i = 0;
        while (i < localObject.length)
        {
          arrayOfFloat[0] = ((i * 30 + 180) % 360);
          localObject[i] = Color.HSVToColor(arrayOfFloat);
          i += 1;
        }
        localObject[12] = localObject[0];
        localObject = new ComposeShader(new SweepGradient(paramInt1 / 2, paramInt2 / 2, localObject, null), new RadialGradient(paramInt1 / 2, paramInt2 / 2, this.colorWheelRadius, -1, 16777215, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER);
        this.colorWheelPaint.setShader((Shader)localObject);
        new Canvas(localBitmap).drawCircle(paramInt1 / 2, paramInt2 / 2, this.colorWheelRadius, this.colorWheelPaint);
        return (Bitmap)localBitmap;
      }

      private void drawPointerArrow(Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3)
      {
        int i = AndroidUtilities.dp(13.0F);
        this.circleDrawable.setBounds(paramInt1 - i, paramInt2 - i, paramInt1 + i, i + paramInt2);
        this.circleDrawable.draw(paramCanvas);
        this.circlePaint.setColor(-1);
        paramCanvas.drawCircle(paramInt1, paramInt2, AndroidUtilities.dp(11.0F), this.circlePaint);
        this.circlePaint.setColor(paramInt3);
        paramCanvas.drawCircle(paramInt1, paramInt2, AndroidUtilities.dp(9.0F), this.circlePaint);
      }

      private void startColorChange(boolean paramBoolean)
      {
        if (ThemeEditorView.EditorAlert.this.startedColorChange == paramBoolean)
          return;
        if (ThemeEditorView.EditorAlert.this.colorChangeAnimation != null)
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.cancel();
        ThemeEditorView.EditorAlert.access$302(ThemeEditorView.EditorAlert.this, paramBoolean);
        ThemeEditorView.EditorAlert.access$402(ThemeEditorView.EditorAlert.this, new AnimatorSet());
        AnimatorSet localAnimatorSet = ThemeEditorView.EditorAlert.this.colorChangeAnimation;
        Object localObject = ThemeEditorView.EditorAlert.this.backDrawable;
        int i;
        ViewGroup localViewGroup;
        float f;
        if (paramBoolean)
        {
          i = 0;
          localObject = ObjectAnimator.ofInt(localObject, "alpha", new int[] { i });
          localViewGroup = ThemeEditorView.EditorAlert.this.containerView;
          if (!paramBoolean)
            break label189;
          f = 0.2F;
        }
        while (true)
        {
          localAnimatorSet.playTogether(new Animator[] { localObject, ObjectAnimator.ofFloat(localViewGroup, "alpha", new float[] { f }) });
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.setDuration(150L);
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.start();
          return;
          i = 51;
          break;
          label189: f = 1.0F;
        }
      }

      public int getColor()
      {
        return Color.HSVToColor(this.colorHSV) & 0xFFFFFF | (int)(this.alpha * 255.0F) << 24;
      }

      protected void onDraw(Canvas paramCanvas)
      {
        int j = getWidth() / 2 - this.paramValueSliderWidth * 2;
        int i = getHeight() / 2 - AndroidUtilities.dp(8.0F);
        paramCanvas.drawBitmap(this.colorWheelBitmap, j - this.colorWheelRadius, i - this.colorWheelRadius, null);
        float f1 = (float)Math.toRadians(this.colorHSV[0]);
        int k = (int)(-Math.cos(f1) * this.colorHSV[1] * this.colorWheelRadius);
        int m = (int)(-Math.sin(f1) * this.colorHSV[1] * this.colorWheelRadius);
        f1 = this.colorWheelRadius;
        this.hsvTemp[0] = this.colorHSV[0];
        this.hsvTemp[1] = this.colorHSV[1];
        this.hsvTemp[2] = 1.0F;
        drawPointerArrow(paramCanvas, k + j, m + i, Color.HSVToColor(this.hsvTemp));
        int n = j + this.colorWheelRadius + this.paramValueSliderWidth;
        i -= this.colorWheelRadius;
        j = AndroidUtilities.dp(9.0F);
        k = this.colorWheelRadius * 2;
        float f2;
        float f3;
        float f4;
        int i2;
        Shader.TileMode localTileMode;
        if (this.colorGradient == null)
        {
          f1 = n;
          f2 = i;
          f3 = n + j;
          f4 = i + k;
          i2 = Color.HSVToColor(this.hsvTemp);
          localTileMode = Shader.TileMode.CLAMP;
          this.colorGradient = new LinearGradient(f1, f2, f3, f4, new int[] { -16777216, i2 }, null, localTileMode);
        }
        this.valueSliderPaint.setShader(this.colorGradient);
        paramCanvas.drawRect(n, i, n + j, i + k, this.valueSliderPaint);
        drawPointerArrow(paramCanvas, j / 2 + n, (int)(i + this.colorHSV[2] * k), Color.HSVToColor(this.colorHSV));
        int i1;
        n += this.paramValueSliderWidth * 2;
        if (this.alphaGradient == null)
        {
          i2 = Color.HSVToColor(this.hsvTemp);
          f1 = i1;
          f2 = i;
          f3 = i1 + j;
          f4 = i + k;
          localTileMode = Shader.TileMode.CLAMP;
          this.alphaGradient = new LinearGradient(f1, f2, f3, f4, new int[] { i2, i2 & 0xFFFFFF }, null, localTileMode);
        }
        this.valueSliderPaint.setShader(this.alphaGradient);
        paramCanvas.drawRect(i1, i, i1 + j, i + k, this.valueSliderPaint);
        drawPointerArrow(paramCanvas, j / 2 + i1, (int)(i + (1.0F - this.alpha) * k), Color.HSVToColor(this.colorHSV) & 0xFFFFFF | (int)(255.0F * this.alpha) << 24);
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        int i = Math.min(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt2));
        measureChild(this.linearLayout, paramInt1, paramInt2);
        setMeasuredDimension(i, i);
      }

      protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        this.colorWheelRadius = Math.max(1, paramInt1 / 2 - this.paramValueSliderWidth * 2 - AndroidUtilities.dp(20.0F));
        this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelRadius * 2, this.colorWheelRadius * 2);
        this.colorGradient = null;
        this.alphaGradient = null;
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        switch (paramMotionEvent.getAction())
        {
        default:
        case 0:
        case 2:
        case 1:
        }
        while (true)
        {
          return super.onTouchEvent(paramMotionEvent);
          int i = (int)paramMotionEvent.getX();
          int j = (int)paramMotionEvent.getY();
          int k = getWidth() / 2 - this.paramValueSliderWidth * 2;
          int m = getHeight() / 2 - AndroidUtilities.dp(8.0F);
          int n = i - k;
          int i1 = j - m;
          double d2 = Math.sqrt(n * n + i1 * i1);
          if ((this.circlePressed) || ((!this.alphaPressed) && (!this.colorPressed) && (d2 <= this.colorWheelRadius)))
          {
            double d1 = d2;
            if (d2 > this.colorWheelRadius)
              d1 = this.colorWheelRadius;
            this.circlePressed = true;
            this.colorHSV[0] = (float)(Math.toDegrees(Math.atan2(i1, n)) + 180.0D);
            this.colorHSV[1] = Math.max(0.0F, Math.min(1.0F, (float)(d1 / this.colorWheelRadius)));
            this.colorGradient = null;
            this.alphaGradient = null;
          }
          float f2;
          float f1;
          if ((this.colorPressed) || ((!this.circlePressed) && (!this.alphaPressed) && (i >= this.colorWheelRadius + k + this.paramValueSliderWidth) && (i <= this.colorWheelRadius + k + this.paramValueSliderWidth * 2) && (j >= m - this.colorWheelRadius) && (j <= this.colorWheelRadius + m)))
          {
            f2 = (j - (m - this.colorWheelRadius)) / (this.colorWheelRadius * 2.0F);
            if (f2 < 0.0F)
            {
              f1 = 0.0F;
              this.colorHSV[2] = f1;
              this.colorPressed = true;
            }
          }
          else if ((this.alphaPressed) || ((!this.circlePressed) && (!this.colorPressed) && (i >= this.colorWheelRadius + k + this.paramValueSliderWidth * 3) && (i <= this.colorWheelRadius + k + this.paramValueSliderWidth * 4) && (j >= m - this.colorWheelRadius) && (j <= this.colorWheelRadius + m)))
          {
            this.alpha = (1.0F - (j - (m - this.colorWheelRadius)) / (this.colorWheelRadius * 2.0F));
            if (this.alpha >= 0.0F)
              break label580;
            this.alpha = 0.0F;
          }
          while (true)
          {
            this.alphaPressed = true;
            if ((!this.alphaPressed) && (!this.colorPressed) && (!this.circlePressed))
              break label815;
            startColorChange(true);
            j = getColor();
            i = 0;
            while (i < ThemeEditorView.this.currentThemeDesription.size())
            {
              ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i)).setColor(j, false);
              i += 1;
            }
            f1 = f2;
            if (f2 <= 1.0F)
              break;
            f1 = 1.0F;
            break;
            label580: if (this.alpha <= 1.0F)
              continue;
            this.alpha = 1.0F;
          }
          i = Color.red(j);
          k = Color.green(j);
          m = Color.blue(j);
          j = Color.alpha(j);
          if (!ThemeEditorView.EditorAlert.this.ignoreTextChange)
          {
            ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, true);
            this.colorEditText[0].setText("" + i);
            this.colorEditText[1].setText("" + k);
            this.colorEditText[2].setText("" + m);
            this.colorEditText[3].setText("" + j);
            i = 0;
            while (i < 4)
            {
              this.colorEditText[i].setSelection(this.colorEditText[i].length());
              i += 1;
            }
            ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, false);
          }
          invalidate();
          label815: return true;
          this.alphaPressed = false;
          this.colorPressed = false;
          this.circlePressed = false;
          startColorChange(false);
        }
      }

      public void setColor(int paramInt)
      {
        int i = Color.red(paramInt);
        int k = Color.green(paramInt);
        int m = Color.blue(paramInt);
        int j = Color.alpha(paramInt);
        if (!ThemeEditorView.EditorAlert.this.ignoreTextChange)
        {
          ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, true);
          this.colorEditText[0].setText("" + i);
          this.colorEditText[1].setText("" + k);
          this.colorEditText[2].setText("" + m);
          this.colorEditText[3].setText("" + j);
          i = 0;
          while (i < 4)
          {
            this.colorEditText[i].setSelection(this.colorEditText[i].length());
            i += 1;
          }
          ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, false);
        }
        this.alphaGradient = null;
        this.colorGradient = null;
        this.alpha = (j / 255.0F);
        Color.colorToHSV(paramInt, this.colorHSV);
        invalidate();
      }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter
    {
      private Context context;
      private int currentCount;
      private ArrayList<ArrayList<ThemeDescription>> items = new ArrayList();
      private HashMap<String, ArrayList<ThemeDescription>> itemsMap = new HashMap();

      public ListAdapter(Context paramArrayOfThemeDescription, ThemeDescription[] arg3)
      {
        this.context = paramArrayOfThemeDescription;
        int i = 0;
        Object localObject1;
        while (i < localObject1.length)
        {
          Object localObject2 = localObject1[i];
          String str = localObject2.getCurrentKey();
          paramArrayOfThemeDescription = (ArrayList)this.itemsMap.get(str);
          this$1 = paramArrayOfThemeDescription;
          if (paramArrayOfThemeDescription == null)
          {
            this$1 = new ArrayList();
            this.itemsMap.put(str, ThemeEditorView.EditorAlert.this);
            this.items.add(ThemeEditorView.EditorAlert.this);
          }
          ThemeEditorView.EditorAlert.this.add(localObject2);
          i += 1;
        }
      }

      public ArrayList<ThemeDescription> getItem(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= this.items.size()))
          return null;
        return (ArrayList)this.items.get(paramInt);
      }

      public int getItemCount()
      {
        return this.items.size();
      }

      public int getItemViewType(int paramInt)
      {
        return 0;
      }

      public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
      {
        return true;
      }

      public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
      {
        ThemeDescription localThemeDescription = (ThemeDescription)((ArrayList)this.items.get(paramInt)).get(0);
        if (localThemeDescription.getCurrentKey().equals("chat_wallpaper"));
        for (paramInt = 0; ; paramInt = localThemeDescription.getSetColor())
        {
          ((TextColorThemeCell)paramViewHolder.itemView).setTextAndColor(localThemeDescription.getTitle(), paramInt);
          return;
        }
      }

      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
      {
        paramViewGroup = new TextColorThemeCell(this.context);
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.ThemeEditorView
 * JD-Core Version:    0.6.0
 */