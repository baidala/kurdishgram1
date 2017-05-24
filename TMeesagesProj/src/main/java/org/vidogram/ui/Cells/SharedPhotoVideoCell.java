package org.vidogram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.MessageObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.PhotoViewer;

public class SharedPhotoVideoCell extends FrameLayout
{
  private SharedPhotoVideoCellDelegate delegate;
  private int[] indeces = new int[6];
  private boolean isFirst;
  private int itemsCount;
  private MessageObject[] messageObjects = new MessageObject[6];
  private PhotoVideoView[] photoVideoViews = new PhotoVideoView[6];

  public SharedPhotoVideoCell(Context paramContext)
  {
    super(paramContext);
    int i = 0;
    while (i < 6)
    {
      this.photoVideoViews[i] = new PhotoVideoView(paramContext);
      addView(this.photoVideoViews[i]);
      this.photoVideoViews[i].setVisibility(4);
      this.photoVideoViews[i].setTag(Integer.valueOf(i));
      this.photoVideoViews[i].setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (SharedPhotoVideoCell.this.delegate != null)
          {
            int i = ((Integer)paramView.getTag()).intValue();
            SharedPhotoVideoCell.this.delegate.didClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[i], SharedPhotoVideoCell.this.messageObjects[i], i);
          }
        }
      });
      this.photoVideoViews[i].setOnLongClickListener(new View.OnLongClickListener()
      {
        public boolean onLongClick(View paramView)
        {
          if (SharedPhotoVideoCell.this.delegate != null)
          {
            int i = ((Integer)paramView.getTag()).intValue();
            return SharedPhotoVideoCell.this.delegate.didLongClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[i], SharedPhotoVideoCell.this.messageObjects[i], i);
          }
          return false;
        }
      });
      i += 1;
    }
  }

  public BackupImageView getImageView(int paramInt)
  {
    if (paramInt >= this.itemsCount)
      return null;
    return this.photoVideoViews[paramInt].imageView;
  }

  public MessageObject getMessageObject(int paramInt)
  {
    if (paramInt >= this.itemsCount)
      return null;
    return this.messageObjects[paramInt];
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int k = 0;
    label35: FrameLayout.LayoutParams localLayoutParams;
    if (AndroidUtilities.isTablet())
    {
      paramInt2 = (AndroidUtilities.dp(490.0F) - (this.itemsCount + 1) * AndroidUtilities.dp(4.0F)) / this.itemsCount;
      i = 0;
      if (i >= this.itemsCount)
        break label169;
      localLayoutParams = (FrameLayout.LayoutParams)this.photoVideoViews[i].getLayoutParams();
      if (!this.isFirst)
        break label159;
    }
    label159: for (int j = 0; ; j = AndroidUtilities.dp(4.0F))
    {
      localLayoutParams.topMargin = j;
      localLayoutParams.leftMargin = ((AndroidUtilities.dp(4.0F) + paramInt2) * i + AndroidUtilities.dp(4.0F));
      localLayoutParams.width = paramInt2;
      localLayoutParams.height = paramInt2;
      localLayoutParams.gravity = 51;
      this.photoVideoViews[i].setLayoutParams(localLayoutParams);
      i += 1;
      break label35;
      paramInt2 = (AndroidUtilities.displaySize.x - (this.itemsCount + 1) * AndroidUtilities.dp(4.0F)) / this.itemsCount;
      break;
    }
    label169: if (this.isFirst);
    for (int i = k; ; i = AndroidUtilities.dp(4.0F))
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(i + paramInt2, 1073741824));
      return;
    }
  }

  public void setChecked(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.photoVideoViews[paramInt].setChecked(paramBoolean1, paramBoolean2);
  }

  public void setDelegate(SharedPhotoVideoCellDelegate paramSharedPhotoVideoCellDelegate)
  {
    this.delegate = paramSharedPhotoVideoCellDelegate;
  }

  public void setIsFirst(boolean paramBoolean)
  {
    this.isFirst = paramBoolean;
  }

  public void setItem(int paramInt1, int paramInt2, MessageObject paramMessageObject)
  {
    this.messageObjects[paramInt1] = paramMessageObject;
    this.indeces[paramInt1] = paramInt2;
    PhotoVideoView localPhotoVideoView;
    Object localObject;
    boolean bool;
    if (paramMessageObject != null)
    {
      this.photoVideoViews[paramInt1].setVisibility(0);
      localPhotoVideoView = this.photoVideoViews[paramInt1];
      localPhotoVideoView.imageView.getImageReceiver().setParentMessageObject(paramMessageObject);
      localObject = localPhotoVideoView.imageView.getImageReceiver();
      if (!PhotoViewer.getInstance().isShowingImage(paramMessageObject))
      {
        bool = true;
        ((ImageReceiver)localObject).setVisible(bool, false);
        if (!paramMessageObject.isVideo())
          break label259;
        localPhotoVideoView.videoInfoContainer.setVisibility(0);
        paramInt1 = 0;
        label97: if (paramInt1 >= paramMessageObject.getDocument().attributes.size())
          break label396;
        localObject = (TLRPC.DocumentAttribute)paramMessageObject.getDocument().attributes.get(paramInt1);
        if (!(localObject instanceof TLRPC.TL_documentAttributeVideo))
          break label240;
      }
    }
    label259: label396: for (paramInt1 = ((TLRPC.DocumentAttribute)localObject).duration; ; paramInt1 = 0)
    {
      paramInt2 = paramInt1 / 60;
      localPhotoVideoView.videoTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt1 - paramInt2 * 60) }));
      if (paramMessageObject.getDocument().thumb != null)
      {
        paramMessageObject = paramMessageObject.getDocument().thumb.location;
        localPhotoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(2130838005), null, paramMessageObject, "b", null, 0);
        return;
        bool = false;
        break;
        label240: paramInt1 += 1;
        break label97;
      }
      localPhotoVideoView.imageView.setImageResource(2130838005);
      return;
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessageObject.messageOwner.media.photo != null) && (!paramMessageObject.photoThumbs.isEmpty()))
      {
        localPhotoVideoView.videoInfoContainer.setVisibility(4);
        paramMessageObject = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs, 80);
        localPhotoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(2130838005), null, paramMessageObject.location, "b", null, 0);
        return;
      }
      localPhotoVideoView.videoInfoContainer.setVisibility(4);
      localPhotoVideoView.imageView.setImageResource(2130838005);
      return;
      this.photoVideoViews[paramInt1].clearAnimation();
      this.photoVideoViews[paramInt1].setVisibility(4);
      this.messageObjects[paramInt1] = null;
      return;
    }
  }

  public void setItemsCount(int paramInt)
  {
    int i = 0;
    if (i < this.photoVideoViews.length)
    {
      this.photoVideoViews[i].clearAnimation();
      PhotoVideoView localPhotoVideoView = this.photoVideoViews[i];
      if (i < paramInt);
      for (int j = 0; ; j = 4)
      {
        localPhotoVideoView.setVisibility(j);
        i += 1;
        break;
      }
    }
    this.itemsCount = paramInt;
  }

  public void updateCheckboxColor()
  {
    int i = 0;
    while (i < 6)
    {
      this.photoVideoViews[i].checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
      i += 1;
    }
  }

  private class PhotoVideoView extends FrameLayout
  {
    private AnimatorSet animator;
    private CheckBox checkBox;
    private FrameLayout container;
    private BackupImageView imageView;
    private View selector;
    private LinearLayout videoInfoContainer;
    private TextView videoTextView;

    public PhotoVideoView(Context arg2)
    {
      super();
      this.container = new FrameLayout(localContext);
      addView(this.container, LayoutHelper.createFrame(-1, -1.0F));
      this.imageView = new BackupImageView(localContext);
      this.imageView.getImageReceiver().setNeedsQualityThumb(true);
      this.imageView.getImageReceiver().setShouldGenerateQualityThumb(true);
      this.container.addView(this.imageView, LayoutHelper.createFrame(-1, -1.0F));
      this.videoInfoContainer = new LinearLayout(localContext);
      this.videoInfoContainer.setOrientation(0);
      this.videoInfoContainer.setBackgroundResource(2130838015);
      this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0F), 0, AndroidUtilities.dp(3.0F), 0);
      this.videoInfoContainer.setGravity(16);
      this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
      this$1 = new ImageView(localContext);
      SharedPhotoVideoCell.this.setImageResource(2130837866);
      this.videoInfoContainer.addView(SharedPhotoVideoCell.this, LayoutHelper.createLinear(-2, -2));
      this.videoTextView = new TextView(localContext);
      this.videoTextView.setTextColor(-1);
      this.videoTextView.setTextSize(1, 12.0F);
      this.videoTextView.setGravity(16);
      this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createLinear(-2, -2, 16, 4, 0, 0, 1));
      this.selector = new View(localContext);
      this.selector.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      addView(this.selector, LayoutHelper.createFrame(-1, -1.0F));
      this.checkBox = new CheckBox(localContext, 2130838041);
      this.checkBox.setVisibility(4);
      this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
      addView(this.checkBox, LayoutHelper.createFrame(22, 22.0F, 53, 0.0F, 2.0F, 2.0F, 0.0F));
    }

    public void clearAnimation()
    {
      super.clearAnimation();
      if (this.animator != null)
      {
        this.animator.cancel();
        this.animator = null;
      }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (Build.VERSION.SDK_INT >= 21)
        this.selector.drawableHotspotChanged(paramMotionEvent.getX(), paramMotionEvent.getY());
      return super.onTouchEvent(paramMotionEvent);
    }

    public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = -657931;
      float f1 = 0.85F;
      if (this.checkBox.getVisibility() != 0)
        this.checkBox.setVisibility(0);
      this.checkBox.setChecked(paramBoolean1, paramBoolean2);
      if (this.animator != null)
      {
        this.animator.cancel();
        this.animator = null;
      }
      Object localObject1;
      float f2;
      if (paramBoolean2)
      {
        if (paramBoolean1)
          setBackgroundColor(-657931);
        this.animator = new AnimatorSet();
        localObject1 = this.animator;
        Object localObject2 = this.container;
        FrameLayout localFrameLayout;
        if (paramBoolean1)
        {
          f2 = 0.85F;
          localObject2 = ObjectAnimator.ofFloat(localObject2, "scaleX", new float[] { f2 });
          localFrameLayout = this.container;
          if (!paramBoolean1)
            break label197;
        }
        while (true)
        {
          ((AnimatorSet)localObject1).playTogether(new Animator[] { localObject2, ObjectAnimator.ofFloat(localFrameLayout, "scaleY", new float[] { f1 }) });
          this.animator.setDuration(200L);
          this.animator.addListener(new AnimatorListenerAdapter(paramBoolean1)
          {
            public void onAnimationCancel(Animator paramAnimator)
            {
              if ((SharedPhotoVideoCell.PhotoVideoView.this.animator != null) && (SharedPhotoVideoCell.PhotoVideoView.this.animator.equals(paramAnimator)))
                SharedPhotoVideoCell.PhotoVideoView.access$002(SharedPhotoVideoCell.PhotoVideoView.this, null);
            }

            public void onAnimationEnd(Animator paramAnimator)
            {
              if ((SharedPhotoVideoCell.PhotoVideoView.this.animator != null) && (SharedPhotoVideoCell.PhotoVideoView.this.animator.equals(paramAnimator)))
              {
                SharedPhotoVideoCell.PhotoVideoView.access$002(SharedPhotoVideoCell.PhotoVideoView.this, null);
                if (!this.val$checked)
                  SharedPhotoVideoCell.PhotoVideoView.this.setBackgroundColor(0);
              }
            }
          });
          this.animator.start();
          return;
          f2 = 1.0F;
          break;
          label197: f1 = 1.0F;
        }
      }
      if (paramBoolean1)
      {
        setBackgroundColor(i);
        localObject1 = this.container;
        if (!paramBoolean1)
          break label256;
        f2 = 0.85F;
        label226: ((FrameLayout)localObject1).setScaleX(f2);
        localObject1 = this.container;
        if (!paramBoolean1)
          break label262;
      }
      while (true)
      {
        ((FrameLayout)localObject1).setScaleY(f1);
        return;
        i = 0;
        break;
        label256: f2 = 1.0F;
        break label226;
        label262: f1 = 1.0F;
      }
    }
  }

  public static abstract interface SharedPhotoVideoCellDelegate
  {
    public abstract void didClickItem(SharedPhotoVideoCell paramSharedPhotoVideoCell, int paramInt1, MessageObject paramMessageObject, int paramInt2);

    public abstract boolean didLongClickItem(SharedPhotoVideoCell paramSharedPhotoVideoCell, int paramInt1, MessageObject paramMessageObject, int paramInt2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.SharedPhotoVideoCell
 * JD-Core Version:    0.6.0
 */