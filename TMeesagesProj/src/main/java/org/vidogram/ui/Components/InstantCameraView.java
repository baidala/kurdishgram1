package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.MediaController.PhotoEntry;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.messenger.camera.CameraController;
import org.vidogram.messenger.camera.CameraController.VideoTakeCallback;
import org.vidogram.messenger.camera.CameraView;
import org.vidogram.messenger.camera.CameraView.CameraViewDelegate;
import org.vidogram.ui.ChatActivity;

public class InstantCameraView extends FrameLayout
{
  private View actionBar;
  private AnimatorSet animatorSet;
  private ChatActivity baseFragment;
  private FrameLayout cameraContainer;
  private File cameraFile;
  private CameraView cameraView;
  private boolean deviceHasGoodCamera;
  private int[] position = new int[2];
  private long recordStartTime;
  private boolean recording;
  private boolean requestingPermissions;
  private Runnable timerRunnable = new Runnable()
  {
    public void run()
    {
      if (!InstantCameraView.this.recording)
        return;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, new Object[] { Long.valueOf(System.currentTimeMillis() - InstantCameraView.access$100(InstantCameraView.this)), Double.valueOf(0.0D) });
      AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50L);
    }
  };

  public InstantCameraView(Context paramContext, ChatActivity paramChatActivity, View paramView)
  {
    super(paramContext);
    this.actionBar = paramView;
    setBackgroundColor(2130706432);
    this.baseFragment = paramChatActivity;
    int i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.cameraContainer = new FrameLayout(paramContext);
      if (!AndroidUtilities.isTablet())
        break label201;
      i = AndroidUtilities.dp(100.0F);
      label73: paramContext = new FrameLayout.LayoutParams(i, i, 17);
      paramContext.bottomMargin = AndroidUtilities.dp(48.0F);
      addView(this.cameraContainer, paramContext);
      if (Build.VERSION.SDK_INT < 21)
        break label223;
      this.cameraContainer.setOutlineProvider(new ViewOutlineProvider(i)
      {
        @TargetApi(21)
        public void getOutline(View paramView, Outline paramOutline)
        {
          paramOutline.setOval(0, 0, this.val$size, this.val$size);
        }
      });
      this.cameraContainer.setClipToOutline(true);
    }
    while (true)
    {
      setVisibility(8);
      return;
      paramChatActivity = new Path();
      paramView = new Paint(1);
      paramView.setColor(-16777216);
      paramView.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      this.cameraContainer = new FrameLayout(paramContext, paramChatActivity, paramView)
      {
        protected void dispatchDraw(Canvas paramCanvas)
        {
          super.dispatchDraw(paramCanvas);
          paramCanvas.drawPath(this.val$path, this.val$paint);
        }

        protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        {
          super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
          this.val$path.reset();
          this.val$path.addCircle(paramInt1 / 2, paramInt2 / 2, paramInt1 / 2, Path.Direction.CW);
          this.val$path.toggleInverseFillType();
        }
      };
      break;
      label201: i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2;
      break label73;
      label223: this.cameraContainer.setLayerType(2, null);
    }
  }

  public void cancel()
  {
    if ((this.cameraView == null) || (this.cameraFile == null))
      return;
    this.recording = false;
    AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
    CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), true);
    this.cameraFile.delete();
    this.cameraFile = null;
    startAnimation(false);
  }

  public void checkCamera(boolean paramBoolean)
  {
    if (this.baseFragment == null);
    while (true)
    {
      return;
      boolean bool = this.deviceHasGoodCamera;
      if (Build.VERSION.SDK_INT >= 23)
        if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0)
        {
          if (paramBoolean)
            this.baseFragment.getParentActivity().requestPermissions(new String[] { "android.permission.CAMERA" }, 17);
          this.deviceHasGoodCamera = false;
        }
      while ((this.deviceHasGoodCamera) && (this.baseFragment != null))
      {
        showCamera();
        return;
        CameraController.getInstance().initCamera();
        this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
        continue;
        if (Build.VERSION.SDK_INT < 16)
          continue;
        CameraController.getInstance().initCamera();
        this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
      }
    }
  }

  public FrameLayout getCameraContainer()
  {
    return this.cameraContainer;
  }

  public Rect getCameraRect()
  {
    this.cameraContainer.getLocationOnScreen(this.position);
    return new Rect(this.position[0], this.position[1], this.cameraContainer.getWidth(), this.cameraContainer.getHeight());
  }

  public void hideCamera(boolean paramBoolean)
  {
    if (this.cameraView == null)
      return;
    this.cameraView.destroy(paramBoolean, null);
    this.cameraContainer.removeView(this.cameraView);
    this.cameraView = null;
  }

  public void send()
  {
    if ((this.cameraView == null) || (this.cameraFile == null))
      return;
    this.recording = false;
    AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
    CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
  }

  public void setAlpha(float paramFloat)
  {
    ((ColorDrawable)getBackground()).setAlpha((int)(127.0F * paramFloat));
  }

  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    this.actionBar.setVisibility(paramInt);
    setAlpha(0.0F);
    this.actionBar.setAlpha(0.0F);
    this.cameraContainer.setAlpha(0.0F);
    this.cameraContainer.setScaleX(0.1F);
    this.cameraContainer.setScaleY(0.1F);
    if (this.cameraContainer.getMeasuredWidth() != 0)
    {
      this.cameraContainer.setPivotX(this.cameraContainer.getMeasuredWidth() / 2);
      this.cameraContainer.setPivotY(this.cameraContainer.getMeasuredHeight() / 2);
    }
  }

  @TargetApi(16)
  public void showCamera()
  {
    if (this.cameraView != null)
      return;
    setVisibility(0);
    this.cameraView = new CameraView(getContext(), true);
    this.cameraView.setMirror(true);
    this.cameraContainer.addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0F));
    this.cameraView.setDelegate(new CameraView.CameraViewDelegate()
    {
      public void onCameraCreated(Camera paramCamera)
      {
      }

      public void onCameraInit()
      {
        if ((Build.VERSION.SDK_INT >= 23) && (InstantCameraView.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
        {
          InstantCameraView.access$402(InstantCameraView.this, true);
          InstantCameraView.this.baseFragment.getParentActivity().requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 21);
          return;
        }
        try
        {
          ((Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50L);
          AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
          InstantCameraView.access$502(InstantCameraView.this, AndroidUtilities.generateVideoPath());
          CameraController.getInstance().recordVideo(InstantCameraView.this.cameraView.getCameraSession(), InstantCameraView.this.cameraFile, new CameraController.VideoTakeCallback()
          {
            public void onFinishVideoRecording(Bitmap paramBitmap)
            {
              if ((InstantCameraView.this.cameraFile == null) || (InstantCameraView.this.baseFragment == null))
                return;
              AndroidUtilities.addMediaToGallery(InstantCameraView.this.cameraFile.getAbsolutePath());
              paramBitmap = new VideoEditedInfo();
              paramBitmap.bitrate = -1;
              paramBitmap.originalPath = InstantCameraView.this.cameraFile.getAbsolutePath();
              paramBitmap.endTime = -1L;
              paramBitmap.startTime = -1L;
              paramBitmap.estimatedSize = InstantCameraView.this.cameraFile.length();
              InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0L, InstantCameraView.this.cameraFile.getAbsolutePath(), 0, true), null);
            }
          }
          , new Runnable()
          {
            public void run()
            {
              InstantCameraView.access$002(InstantCameraView.this, true);
              InstantCameraView.access$102(InstantCameraView.this, System.currentTimeMillis());
              AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
              InstantCameraView.this.startAnimation(true);
            }
          }
          , true);
          return;
        }
        catch (Exception localException)
        {
          while (true)
            FileLog.e(localException);
        }
      }
    });
  }

  public void startAnimation(boolean paramBoolean)
  {
    float f3 = 1.0F;
    float f2 = 0.0F;
    if (this.animatorSet != null)
      this.animatorSet.cancel();
    this.animatorSet = new AnimatorSet();
    AnimatorSet localAnimatorSet = this.animatorSet;
    Object localObject1 = this.actionBar;
    float f1;
    label71: ObjectAnimator localObjectAnimator;
    Object localObject2;
    label99: Object localObject3;
    label128: Object localObject4;
    label158: FrameLayout localFrameLayout;
    if (paramBoolean)
    {
      f1 = 1.0F;
      localObject1 = ObjectAnimator.ofFloat(localObject1, "alpha", new float[] { f1 });
      if (!paramBoolean)
        break label311;
      f1 = 1.0F;
      localObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", new float[] { f1 });
      localObject2 = this.cameraContainer;
      if (!paramBoolean)
        break label316;
      f1 = 1.0F;
      localObject2 = ObjectAnimator.ofFloat(localObject2, "alpha", new float[] { f1 });
      localObject3 = this.cameraContainer;
      if (!paramBoolean)
        break label321;
      f1 = 1.0F;
      localObject3 = ObjectAnimator.ofFloat(localObject3, "scaleX", new float[] { f1 });
      localObject4 = this.cameraContainer;
      if (!paramBoolean)
        break label328;
      f1 = f3;
      localObject4 = ObjectAnimator.ofFloat(localObject4, "scaleY", new float[] { f1 });
      localFrameLayout = this.cameraContainer;
      if (!paramBoolean)
        break label335;
      f1 = getMeasuredHeight() / 2;
      label193: if (!paramBoolean)
        break label340;
    }
    while (true)
    {
      localAnimatorSet.playTogether(new Animator[] { localObject1, localObjectAnimator, localObject2, localObject3, localObject4, ObjectAnimator.ofFloat(localFrameLayout, "translationY", new float[] { f1, f2 }) });
      if (!paramBoolean)
        this.animatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if (paramAnimator.equals(InstantCameraView.this.animatorSet))
            {
              InstantCameraView.this.hideCamera(true);
              InstantCameraView.this.setVisibility(8);
            }
          }
        });
      this.animatorSet.setDuration(180L);
      this.animatorSet.setInterpolator(new DecelerateInterpolator());
      this.animatorSet.start();
      return;
      f1 = 0.0F;
      break;
      label311: f1 = 0.0F;
      break label71;
      label316: f1 = 0.0F;
      break label99;
      label321: f1 = 0.1F;
      break label128;
      label328: f1 = 0.1F;
      break label158;
      label335: f1 = 0.0F;
      break label193;
      label340: f2 = getMeasuredHeight() / 2;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.InstantCameraView
 * JD-Core Version:    0.6.0
 */