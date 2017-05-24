package org.vidogram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.f;
import android.support.v4.view.ab;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.LocaleController;
import org.vidogram.tgnet.ConnectionsManager;

public class IntroActivity extends Activity
{
  private ViewGroup bottomPages;
  private int[] icons;
  private boolean justCreated = false;
  private int lastPage = 0;
  private int[] messages;
  private boolean startPressed = false;
  private int[] titles;
  private ImageView topImage1;
  private ImageView topImage2;
  private ViewPager viewPager;

  protected void onCreate(Bundle paramBundle)
  {
    setTheme(2131361942);
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    Object localObject;
    if (AndroidUtilities.isTablet())
    {
      setContentView(2130903080);
      paramBundle = findViewById(2131558565);
      localObject = (BitmapDrawable)getResources().getDrawable(2130837662);
      ((BitmapDrawable)localObject).setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
      paramBundle.setBackgroundDrawable((Drawable)localObject);
      if (!LocaleController.isRTL)
        break label493;
      this.icons = new int[] { 2130837880, 2130837789, 2130837788, 2130837787, 2130837786, 2130837785, 2130837784 };
      this.titles = new int[] { 2131166807, 2131166805, 2131166803, 2131166801, 2131166799, 2131166797, 2131166795 };
    }
    for (this.messages = new int[] { 2131166806, 2131166804, 2131166802, 2131166800, 2131166798, 2131166796, 2131166794 }; ; this.messages = new int[] { 2131166794, 2131166796, 2131166798, 2131166800, 2131166802, 2131166804, 2131166806 })
    {
      this.viewPager = ((ViewPager)findViewById(2131558562));
      paramBundle = (TextView)findViewById(2131558563);
      paramBundle.setText(LocaleController.getString("StartMessaging", 2131166483).toUpperCase());
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject = new StateListAnimator();
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramBundle, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        ((StateListAnimator)localObject).addState(new int[] { 16842919 }, localObjectAnimator);
        localObjectAnimator = ObjectAnimator.ofFloat(paramBundle, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        ((StateListAnimator)localObject).addState(new int[0], localObjectAnimator);
        paramBundle.setStateListAnimator((StateListAnimator)localObject);
      }
      this.topImage1 = ((ImageView)findViewById(2131558560));
      this.topImage2 = ((ImageView)findViewById(2131558561));
      this.bottomPages = ((ViewGroup)findViewById(2131558564));
      this.topImage2.setVisibility(8);
      this.viewPager.setAdapter(new IntroAdapter(null));
      this.viewPager.setPageMargin(0);
      this.viewPager.setOffscreenPageLimit(1);
      this.viewPager.addOnPageChangeListener(new ViewPager.f()
      {
        public void onPageScrollStateChanged(int paramInt)
        {
          ImageView localImageView2;
          if (((paramInt == 0) || (paramInt == 2)) && (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()))
          {
            IntroActivity.access$102(IntroActivity.this, IntroActivity.this.viewPager.getCurrentItem());
            if (IntroActivity.this.topImage1.getVisibility() != 0)
              break label170;
            localImageView2 = IntroActivity.this.topImage1;
          }
          for (ImageView localImageView1 = IntroActivity.this.topImage2; ; localImageView1 = IntroActivity.this.topImage1)
          {
            localImageView1.bringToFront();
            localImageView1.setImageResource(IntroActivity.this.icons[IntroActivity.this.lastPage]);
            localImageView1.clearAnimation();
            localImageView2.clearAnimation();
            Animation localAnimation1 = AnimationUtils.loadAnimation(IntroActivity.this, 2130968587);
            localAnimation1.setAnimationListener(new Animation.AnimationListener(localImageView2)
            {
              public void onAnimationEnd(Animation paramAnimation)
              {
                this.val$fadeoutImage.setVisibility(8);
              }

              public void onAnimationRepeat(Animation paramAnimation)
              {
              }

              public void onAnimationStart(Animation paramAnimation)
              {
              }
            });
            Animation localAnimation2 = AnimationUtils.loadAnimation(IntroActivity.this, 2130968586);
            localAnimation2.setAnimationListener(new Animation.AnimationListener(localImageView1)
            {
              public void onAnimationEnd(Animation paramAnimation)
              {
              }

              public void onAnimationRepeat(Animation paramAnimation)
              {
              }

              public void onAnimationStart(Animation paramAnimation)
              {
                this.val$fadeinImage.setVisibility(0);
              }
            });
            localImageView2.startAnimation(localAnimation1);
            localImageView1.startAnimation(localAnimation2);
            return;
            label170: localImageView2 = IntroActivity.this.topImage2;
          }
        }

        public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
        {
        }

        public void onPageSelected(int paramInt)
        {
        }
      });
      paramBundle.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (IntroActivity.this.startPressed)
            return;
          IntroActivity.access$602(IntroActivity.this, true);
          paramView = new Intent(IntroActivity.this, LaunchActivity.class);
          paramView.putExtra("fromIntro", true);
          IntroActivity.this.startActivity(paramView);
          IntroActivity.this.finish();
        }
      });
      if (BuildVars.DEBUG_VERSION)
        paramBundle.setOnLongClickListener(new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramView)
          {
            ConnectionsManager.getInstance().switchBackend();
            return true;
          }
        });
      this.justCreated = true;
      return;
      setRequestedOrientation(1);
      setContentView(2130903079);
      break;
      label493: this.icons = new int[] { 2130837784, 2130837785, 2130837786, 2130837787, 2130837788, 2130837789, 2130837880 };
      this.titles = new int[] { 2131166795, 2131166797, 2131166799, 2131166801, 2131166803, 2131166805, 2131166807 };
    }
  }

  protected void onPause()
  {
    super.onPause();
    AndroidUtilities.unregisterUpdates();
  }

  protected void onResume()
  {
    super.onResume();
    if (this.justCreated)
    {
      if (!LocaleController.isRTL)
        break label46;
      this.viewPager.setCurrentItem(6);
    }
    for (this.lastPage = 6; ; this.lastPage = 0)
    {
      this.justCreated = false;
      AndroidUtilities.checkForCrashes(this);
      AndroidUtilities.checkForUpdates(this);
      return;
      label46: this.viewPager.setCurrentItem(0);
    }
  }

  private class IntroAdapter extends ab
  {
    private IntroAdapter()
    {
    }

    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      paramViewGroup.removeView((View)paramObject);
    }

    public int getCount()
    {
      return 7;
    }

    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      View localView = View.inflate(paramViewGroup.getContext(), 2130903081, null);
      TextView localTextView1 = (TextView)localView.findViewById(2131558566);
      TextView localTextView2 = (TextView)localView.findViewById(2131558567);
      paramViewGroup.addView(localView, 0);
      localTextView1.setText(IntroActivity.this.getString(IntroActivity.this.titles[paramInt]));
      localTextView2.setText(AndroidUtilities.replaceTags(IntroActivity.this.getString(IntroActivity.this.messages[paramInt])));
      return localView;
    }

    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView.equals(paramObject);
    }

    public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader)
    {
    }

    public Parcelable saveState()
    {
      return null;
    }

    public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      super.setPrimaryItem(paramViewGroup, paramInt, paramObject);
      int j = IntroActivity.this.bottomPages.getChildCount();
      int i = 0;
      if (i < j)
      {
        paramViewGroup = IntroActivity.this.bottomPages.getChildAt(i);
        if (i == paramInt)
          paramViewGroup.setBackgroundColor(-13851168);
        while (true)
        {
          i += 1;
          break;
          paramViewGroup.setBackgroundColor(-4473925);
        }
      }
    }

    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null)
        super.unregisterDataSetObserver(paramDataSetObserver);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.IntroActivity
 * JD-Core Version:    0.6.0
 */