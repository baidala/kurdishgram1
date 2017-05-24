package org.vidogram.VidogramUi.WebRTC.WebRTCUI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.concurrent.Semaphore;
import org.vidogram.VidogramUi.PermissionsActivity;
import org.vidogram.VidogramUi.WebRTC.a.a.b;
import org.vidogram.VidogramUi.WebRTC.c;
import org.vidogram.VidogramUi.WebRTC.c.a;
import org.vidogram.VidogramUi.WebRTC.e;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CubicBezierInterpolator;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.SizeNotifierFrameLayout;

public class CallActivity extends Activity
  implements b.a, org.vidogram.VidogramUi.WebRTC.a.b
{
  public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
  public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
  public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
  public static final String EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2";
  public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
  public static final String EXTRA_Callee_ID = "itman.Vidofilm.apprtc.Callee_ID";
  public static final String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
  public static final String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
  public static final String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
  public static final String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
  public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
  public static final String EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL";
  public static final String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
  public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
  public static final String EXTRA_ID = "org.appspot.apprtc.ID";
  public static final String EXTRA_Incoming_Call = "itman.Vidofilm.apprtc.Incoming_Call";
  public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
  public static final String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
  public static final String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
  public static final String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
  public static final String EXTRA_NOAUDIOPROCESSING_ENABLED = "org.appspot.apprtc.NOAUDIOPROCESSING";
  public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
  public static final String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
  public static final String EXTRA_PHONE_NUMBER = "itman.Vidofilm.apprtc.PHONE_NUMBER";
  public static final String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
  public static final String EXTRA_ROOMID = "itman.Vidofilm.apprtc.ROOMID";
  public static final String EXTRA_Room_Info = "itman.Vidofilm.apprtc.Room_Info";
  public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
  public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
  public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH = "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
  public static final String EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE";
  public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
  public static final String EXTRA_USE_VALUES_FROM_INTENT = "org.appspot.apprtc.USE_VALUES_FROM_INTENT";
  public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
  public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
  public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
  public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED = "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
  public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA";
  public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
  public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
  public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
  private static final String[] MANDATORY_PERMISSIONS = { "android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.RECORD_AUDIO", "android.permission.CAMERA" };
  public static int OVERLAY_PERMISSION_CODE = 9707;
  private ImageView blurOverlayView1;
  private ImageView blurOverlayView2;
  private Bitmap blurredPhoto1;
  private Bitmap blurredPhoto2;
  private TextView brandingText;
  private c callConnectionClient;
  private a callFragment;
  protected TLRPC.User currentUser;
  private long dialog_id;
  private TextView durationText;
  private AnimatorSet ellAnimator;
  private TextAlphaSpan[] ellSpans;
  private SizeNotifierFrameLayout fragmentView;
  private Handler handlerIncomingcall;
  private b incomingCallFragment;
  private Intent intent;
  private String lastStateText;
  private TextView nameText;
  private BackupImageView photoView;
  private Runnable stateRunnable;
  private TextView stateText;
  private TextView stateText2;
  private Animator textChangingAnim;
  private Runnable timeIncomingcall;

  private void answerCallMode()
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    localFragmentTransaction.remove(this.incomingCallFragment);
    localFragmentTransaction.add(2131558405, this.callFragment, "callFragment");
    localFragmentTransaction.commit();
  }

  private void callMode()
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    localFragmentTransaction.add(2131558405, this.callFragment, "callFragment");
    localFragmentTransaction.commit();
  }

  @SuppressLint({"ObjectAnimatorBinding"})
  private ObjectAnimator createAlphaAnimator(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramObject = ObjectAnimator.ofInt(paramObject, "alpha", new int[] { paramInt1, paramInt2 });
    paramObject.setDuration(paramInt4);
    paramObject.setStartDelay(paramInt3);
    paramObject.setInterpolator(CubicBezierInterpolator.DEFAULT);
    return paramObject;
  }

  private void getIncomingRoomInformation(String paramString, int paramInt)
  {
    itman.Vidofilm.a.m localm = new itman.Vidofilm.a.m();
    localm.b(itman.Vidofilm.b.a(getApplicationContext()).k());
    localm.a(paramString);
    if (localm.a() == null)
    {
      itman.Vidofilm.d.d.a(getApplicationContext()).a(true);
      getIncomingRoomInformation(paramString, paramInt - 1);
      return;
    }
    ((itman.Vidofilm.c.b)itman.Vidofilm.c.a.a().a(itman.Vidofilm.c.b.class)).a(localm).a(new e.d(paramInt, paramString)
    {
      public void onFailure(e.b<com.google.a.l> paramb, Throwable paramThrowable)
      {
        if (this.val$tryToSend < 3)
          CallActivity.this.getIncomingRoomInformation(this.val$roomId, this.val$tryToSend + 1);
        do
        {
          return;
          if (3 != this.val$tryToSend)
            continue;
          CallActivity.this.handlerIncomingcall.postDelayed(CallActivity.this.timeIncomingcall, 3000L);
          return;
        }
        while (this.val$tryToSend != 4);
        CallActivity.this.callConnectionClient.h();
      }

      public void onResponse(e.b<com.google.a.l> paramb, e.l<com.google.a.l> paraml)
      {
        if (paraml.b())
        {
          CallActivity.this.callConnectionClient.a(new e(CallActivity.this.getApplicationContext()).a(((com.google.a.l)paraml.c()).toString()));
          return;
        }
        if (this.val$tryToSend > 4)
        {
          CallActivity.this.callConnectionClient.h();
          return;
        }
        if (paraml.a() == 401)
        {
          itman.Vidofilm.d.d.a(CallActivity.this.getApplicationContext()).a(true);
          CallActivity.this.getIncomingRoomInformation(this.val$roomId, this.val$tryToSend + 1);
          return;
        }
        if (paraml.a() == 410)
        {
          CallActivity.this.callConnectionClient.h();
          return;
        }
        if (this.val$tryToSend < 3)
        {
          CallActivity.this.getIncomingRoomInformation(this.val$roomId, this.val$tryToSend + 1);
          return;
        }
        CallActivity.this.callConnectionClient.h();
      }
    });
  }

  private void inComingCallMode()
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    localFragmentTransaction.add(2131558405, this.incomingCallFragment, "incomingCallFragment");
    localFragmentTransaction.commit();
  }

  private void setStateTextAnimated(String paramString, boolean paramBoolean)
  {
    if (paramString.equals(this.lastStateText))
      return;
    this.lastStateText = paramString;
    if (this.textChangingAnim != null)
      this.textChangingAnim.cancel();
    float f;
    if (paramBoolean)
    {
      if (!this.ellAnimator.isRunning())
        this.ellAnimator.start();
      paramString = new SpannableStringBuilder(paramString.toUpperCase());
      Object localObject = this.ellSpans;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        localObject[i].setAlpha(0);
        i += 1;
      }
      localObject = new SpannableString("...");
      ((SpannableString)localObject).setSpan(this.ellSpans[0], 0, 1, 0);
      ((SpannableString)localObject).setSpan(this.ellSpans[1], 1, 2, 0);
      ((SpannableString)localObject).setSpan(this.ellSpans[2], 2, 3, 0);
      paramString.append((CharSequence)localObject);
      this.stateText2.setText(paramString);
      this.stateText2.setVisibility(0);
      paramString = this.stateText;
      if (!LocaleController.isRTL)
        break label572;
      f = this.stateText.getWidth();
      label200: paramString.setPivotX(f);
      this.stateText.setPivotY(this.stateText.getHeight() / 2);
      paramString = this.stateText2;
      if (!LocaleController.isRTL)
        break label577;
      f = this.stateText.getWidth();
    }
    while (true)
    {
      paramString.setPivotX(f);
      this.stateText2.setPivotY(this.stateText.getHeight() / 2);
      this.durationText = this.stateText2;
      paramString = new AnimatorSet();
      paramString.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.stateText2, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.stateText2, "translationY", new float[] { this.stateText.getHeight() / 2, 0.0F }), ObjectAnimator.ofFloat(this.stateText2, "scaleX", new float[] { 0.7F, 1.0F }), ObjectAnimator.ofFloat(this.stateText2, "scaleY", new float[] { 0.7F, 1.0F }), ObjectAnimator.ofFloat(this.stateText, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.stateText, "translationY", new float[] { 0.0F, -this.stateText.getHeight() / 2 }), ObjectAnimator.ofFloat(this.stateText, "scaleX", new float[] { 1.0F, 0.7F }), ObjectAnimator.ofFloat(this.stateText, "scaleY", new float[] { 1.0F, 0.7F }) });
      paramString.setDuration(200L);
      paramString.setInterpolator(CubicBezierInterpolator.DEFAULT);
      paramString.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          CallActivity.access$1502(CallActivity.this, null);
          CallActivity.this.stateText2.setVisibility(8);
          CallActivity.access$1202(CallActivity.this, CallActivity.this.stateText);
          CallActivity.this.stateText.setTranslationY(0.0F);
          CallActivity.this.stateText.setScaleX(1.0F);
          CallActivity.this.stateText.setScaleY(1.0F);
          CallActivity.this.stateText.setAlpha(1.0F);
          CallActivity.this.stateText.setText(CallActivity.this.stateText2.getText());
        }
      });
      this.textChangingAnim = paramString;
      paramString.start();
      return;
      if (this.ellAnimator.isRunning())
        this.ellAnimator.cancel();
      paramString = paramString.toUpperCase();
      break;
      label572: f = 0.0F;
      break label200;
      label577: f = 0.0F;
    }
  }

  private void startUpdatingCallDuration()
  {
    this.stateRunnable = new Runnable()
    {
      public void run()
      {
        if (((c.e()) && (CallActivity.this.callConnectionClient != null) && (CallActivity.this.isFinishing())) || (CallActivity.this.callConnectionClient.I() == 0L))
          return;
        long l = (System.currentTimeMillis() - CallActivity.this.callConnectionClient.I()) / 1000L;
        TextView localTextView;
        if (CallActivity.this.callConnectionClient.S() != c.a.i)
        {
          localTextView = CallActivity.this.durationText;
          if (l <= 3600L)
            break label163;
        }
        label163: for (String str = String.format("%d:%02d:%02d", new Object[] { Long.valueOf(l / 3600L), Long.valueOf(l % 3600L / 60L), Long.valueOf(l % 60L) }); ; str = String.format("%d:%02d", new Object[] { Long.valueOf(l / 60L), Long.valueOf(l % 60L) }))
        {
          localTextView.setText(str);
          CallActivity.this.durationText.postDelayed(this, 500L);
          return;
        }
      }
    };
    this.stateRunnable.run();
  }

  private void updateBlurredPhotos(Bitmap paramBitmap)
  {
    new Thread(new Runnable(paramBitmap)
    {
      public void run()
      {
        Bitmap localBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Object localObject1 = new Canvas(localBitmap);
        ((Canvas)localObject1).drawBitmap(this.val$src, null, new Rect(0, 0, 150, 150), new Paint(2));
        Utilities.blurBitmap(localBitmap, 3, 0, localBitmap.getWidth(), localBitmap.getHeight(), localBitmap.getRowBytes());
        Object localObject2 = android.support.v7.c.b.a(this.val$src).a();
        Paint localPaint = new Paint();
        localPaint.setColor(((android.support.v7.c.b)localObject2).a(-11242343) & 0xFFFFFF | 0x44000000);
        ((Canvas)localObject1).drawColor(637534208);
        ((Canvas)localObject1).drawRect(0.0F, 0.0F, ((Canvas)localObject1).getWidth(), ((Canvas)localObject1).getHeight(), localPaint);
        localObject1 = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        localObject2 = new Canvas((Bitmap)localObject1);
        ((Canvas)localObject2).drawBitmap(this.val$src, null, new Rect(0, 0, 50, 50), new Paint(2));
        Utilities.blurBitmap(localObject1, 3, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight(), ((Bitmap)localObject1).getRowBytes());
        localPaint.setAlpha(102);
        ((Canvas)localObject2).drawRect(0.0F, 0.0F, ((Canvas)localObject2).getWidth(), ((Canvas)localObject2).getHeight(), localPaint);
        CallActivity.access$602(CallActivity.this, localBitmap);
        CallActivity.access$702(CallActivity.this, (Bitmap)localObject1);
        CallActivity.this.runOnUiThread(new Runnable()
        {
          public void run()
          {
            CallActivity.this.blurOverlayView1.setImageBitmap(CallActivity.this.blurredPhoto1);
            CallActivity.this.blurOverlayView2.setImageBitmap(CallActivity.this.blurredPhoto2);
          }
        });
      }
    }).start();
  }

  public View createView()
  {
    this.nameText = new TextView(ApplicationLoader.applicationContext);
    this.photoView = new BackupImageView(ApplicationLoader.applicationContext)
    {
      private Drawable bottomGradient = getResources().getDrawable(2130837723);
      private Paint paint = new Paint();
      private Drawable topGradient = getResources().getDrawable(2130837724);

      protected void onDraw(Canvas paramCanvas)
      {
        super.onDraw(paramCanvas);
        this.paint.setColor(1275068416);
        paramCanvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), this.paint);
        this.topGradient.setBounds(0, 0, getWidth(), AndroidUtilities.dp(170.0F));
        this.topGradient.setAlpha(128);
        this.topGradient.draw(paramCanvas);
        this.bottomGradient.setBounds(0, getHeight() - AndroidUtilities.dp(220.0F), getWidth(), getHeight());
        this.bottomGradient.setAlpha(178);
        this.bottomGradient.draw(paramCanvas);
      }
    };
    int i = this.intent.getIntExtra("itman.Vidofilm.apprtc.Callee_ID", 0);
    Object localObject1;
    if (i != 0)
    {
      this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
      if (this.currentUser == null)
      {
        localObject1 = new Semaphore(0);
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(i, (Semaphore)localObject1)
        {
          public void run()
          {
            CallActivity.this.currentUser = MessagesStorage.getInstance().getUser(this.val$userId);
            this.val$semaphore.release();
          }
        });
      }
    }
    try
    {
      ((Semaphore)localObject1).acquire();
      if (this.currentUser != null)
        MessagesController.getInstance().putUser(this.currentUser, true);
      this.dialog_id = i;
      MessagesController.getInstance().loadPeerSettings(this.currentUser, null);
      this.fragmentView = new SizeNotifierFrameLayout(ApplicationLoader.applicationContext);
      this.fragmentView.setId(2131558405);
      this.fragmentView.addView(this.photoView);
      this.blurOverlayView1 = new ImageView(this);
      this.blurOverlayView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
      this.blurOverlayView1.setAlpha(0.0F);
      this.fragmentView.addView(this.blurOverlayView1);
      this.blurOverlayView2 = new ImageView(this);
      this.blurOverlayView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
      this.blurOverlayView2.setAlpha(0.0F);
      this.fragmentView.addView(this.blurOverlayView2);
      TextView localTextView = new TextView(this);
      localTextView.setTextColor(-855638017);
      localTextView.setText(LocaleController.getString("VidogramInCallBranding", 2131166810));
      localObject1 = getResources().getDrawable(2130837822).mutate();
      ((Drawable)localObject1).setAlpha(204);
      ((Drawable)localObject1).setBounds(0, 0, AndroidUtilities.dp(15.0F), AndroidUtilities.dp(15.0F));
      if (LocaleController.isRTL)
      {
        localObject3 = null;
        if (!LocaleController.isRTL)
          break label1146;
        localTextView.setCompoundDrawables((Drawable)localObject3, null, (Drawable)localObject1, null);
        localTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (!LocaleController.isRTL)
          break label1151;
        i = 5;
        localTextView.setGravity(i);
        localTextView.setCompoundDrawablePadding(AndroidUtilities.dp(5.0F));
        localTextView.setTextSize(1, 14.0F);
        this.fragmentView.addView(localTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 18.0F, 18.0F, 0.0F));
        this.stateText = new TextView(ApplicationLoader.applicationContext);
        this.fragmentView.addView(this.stateText, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 98.0F, 18.0F, 0.0F));
        this.stateText.setTextColor(-855638017);
        this.stateText.setSingleLine();
        this.stateText.setEllipsize(TextUtils.TruncateAt.END);
        this.stateText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stateText.setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
        this.stateText.setTextSize(1, 15.0F);
        localObject1 = this.stateText;
        if (!LocaleController.isRTL)
          break label1156;
        i = 5;
        ((TextView)localObject1).setGravity(i);
        this.durationText = this.stateText;
        this.stateText2 = new TextView(this);
        this.stateText2.setTextColor(-855638017);
        this.stateText2.setSingleLine();
        this.stateText2.setEllipsize(TextUtils.TruncateAt.END);
        this.stateText2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stateText2.setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
        this.stateText2.setTextSize(1, 15.0F);
        localObject1 = this.stateText2;
        if (!LocaleController.isRTL)
          break label1161;
        i = 5;
        ((TextView)localObject1).setGravity(i);
        this.stateText2.setVisibility(8);
        this.fragmentView.addView(this.stateText2, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 98.0F, 18.0F, 0.0F));
        this.ellSpans = new TextAlphaSpan[] { new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan() };
        this.fragmentView.addView(this.nameText, LayoutHelper.createFrame(-1, -2.0F, 51, 18.0F, 43.0F, 18.0F, 0.0F));
        this.nameText.setSingleLine();
        this.nameText.setTextColor(-1);
        this.nameText.setTextSize(1, 40.0F);
        this.nameText.setEllipsize(TextUtils.TruncateAt.END);
        localObject1 = this.nameText;
        if (!LocaleController.isRTL)
          break label1166;
        i = 5;
        ((TextView)localObject1).setGravity(i);
        this.nameText.setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
        this.nameText.setTypeface(Typeface.create("sans-serif-light", 0));
        if (this.currentUser == null)
          break label1171;
        this.nameText.setText(ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name));
        i = this.stateText.getPaint().getAlpha();
        this.ellAnimator = new AnimatorSet();
        this.ellAnimator.playTogether(new Animator[] { createAlphaAnimator(this.ellSpans[0], 0, i, 0, 300), createAlphaAnimator(this.ellSpans[1], 0, i, 150, 300), createAlphaAnimator(this.ellSpans[2], 0, i, 300, 300), createAlphaAnimator(this.ellSpans[0], i, 0, 1000, 400), createAlphaAnimator(this.ellSpans[1], i, 0, 1000, 400), createAlphaAnimator(this.ellSpans[2], i, 0, 1000, 400) });
        this.ellAnimator.addListener(new AnimatorListenerAdapter()
        {
          private Runnable restarter = new Runnable()
          {
            public void run()
            {
              if (!CallActivity.this.isFinishing())
                CallActivity.this.ellAnimator.start();
            }
          };

          public void onAnimationEnd(Animator paramAnimator)
          {
            if (!CallActivity.this.isFinishing())
              CallActivity.this.fragmentView.postDelayed(this.restarter, 300L);
          }
        });
        this.fragmentView.setClipChildren(false);
        setStateTextAnimated(LocaleController.getString("VoipRequesting", 2131166603), true);
        onStateChanged(this.callConnectionClient.S());
        return this.fragmentView;
      }
    }
    catch (Exception localObject2)
    {
      while (true)
      {
        FileLog.e("tmessages : " + localException);
        continue;
        Object localObject3 = localException;
        continue;
        label1146: Object localObject2 = null;
        continue;
        label1151: i = 3;
        continue;
        label1156: i = 3;
        continue;
        label1161: i = 3;
        continue;
        label1166: i = 3;
        continue;
        label1171: if ((this.intent.getStringExtra("itman.Vidofilm.apprtc.PHONE_NUMBER") != null) && (this.intent.getStringExtra("itman.Vidofilm.apprtc.PHONE_NUMBER").length() > 0))
        {
          this.nameText.setText(this.intent.getStringExtra("itman.Vidofilm.apprtc.PHONE_NUMBER"));
          continue;
        }
        this.nameText.setText("Unknown");
      }
    }
  }

  public void onAcceptCall()
  {
    this.callConnectionClient.G();
    answerCallMode();
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    try
    {
      if (paramInt1 == OVERLAY_PERMISSION_CODE)
        super.onBackPressed();
      return;
    }
    catch (Exception paramIntent)
    {
    }
  }

  public void onBackPressed()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (!Settings.canDrawOverlays(ApplicationLoader.applicationContext)))
    {
      new AlertDialog.Builder(this).setTitle(LocaleController.getString("AppName", 2131165319)).setMessage(LocaleController.getString("PermissionCallDrawAboveOtherApps", 2131166780)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", 2131166260), new DialogInterface.OnClickListener()
      {
        @TargetApi(23)
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          paramDialogInterface = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
          CallActivity.this.startActivityForResult(paramDialogInterface, CallActivity.OVERLAY_PERMISSION_CODE);
        }
      }).show();
      return;
    }
    super.onBackPressed();
  }

  public void onCalldisconnected()
  {
    if (this.stateRunnable != null)
      this.stateText.removeCallbacks(this.stateRunnable);
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if ((!c.e()) && (getIntent().getIntExtra("itman.Vidofilm.apprtc.Callee_ID", 0) == 0))
    {
      paramBundle = ApplicationLoader.applicationContext;
      Context localContext = ApplicationLoader.applicationContext;
      ((NotificationManager)paramBundle.getSystemService("notification")).cancel(911112);
      finish();
      return;
    }
    this.callConnectionClient = c.a(getIntent());
    this.callConnectionClient.a(this);
    paramBundle = new org.vidogram.VidogramUi.a(this);
    if ((Build.VERSION.SDK_INT >= 23) && (paramBundle.a(MANDATORY_PERMISSIONS)))
    {
      PermissionsActivity.a(this, false, MANDATORY_PERMISSIONS);
      setResult(0);
      this.callConnectionClient.h();
      finish();
      return;
    }
    this.callConnectionClient.f();
    requestWindowFeature(1);
    getWindow().addFlags(6816896);
    getWindow().getDecorView().setSystemUiVisibility(4102);
    this.intent = this.callConnectionClient.d();
    setContentView(createView());
    if ((this.currentUser != null) && (this.currentUser.photo != null))
    {
      this.photoView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
        {
          paramImageReceiver = paramImageReceiver.getBitmap();
          if (paramImageReceiver != null)
            CallActivity.this.updateBlurredPhotos(paramImageReceiver);
        }
      });
      this.photoView.setImage(this.currentUser.photo.photo_big, null, new ColorDrawable(-16777216));
    }
    while (true)
    {
      this.callFragment = new a();
      this.incomingCallFragment = new b();
      this.handlerIncomingcall = new Handler();
      this.callFragment.setArguments(this.intent.getExtras());
      this.incomingCallFragment.setArguments(this.intent.getExtras());
      if ((!this.intent.getBooleanExtra("itman.Vidofilm.apprtc.Incoming_Call", true)) || (this.callConnectionClient.Q()))
        break label415;
      inComingCallMode();
      paramBundle = this.intent.getStringExtra("itman.Vidofilm.apprtc.Room_Info");
      if ((paramBundle == null) || (!paramBundle.equals("-1")))
        break;
      this.fragmentView.post(new Runnable()
      {
        public void run()
        {
          CallActivity.access$302(CallActivity.this, new Runnable()
          {
            public void run()
            {
              CallActivity.this.getIncomingRoomInformation(CallActivity.this.intent.getStringExtra("itman.Vidofilm.apprtc.ROOMID"), 4);
            }
          });
          CallActivity.this.getIncomingRoomInformation(CallActivity.this.intent.getStringExtra("itman.Vidofilm.apprtc.ROOMID"), 0);
        }
      });
      return;
      this.photoView.setVisibility(8);
      this.fragmentView.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { -14994098, -14328963 }));
    }
    label415: callMode();
  }

  protected void onDestroy()
  {
    if (this.callConnectionClient != null)
      this.callConnectionClient.x();
    super.onDestroy();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    b localb = (b)getFragmentManager().findFragmentByTag("incomingCallFragment");
    if ((this.intent != null) && (this.intent.getBooleanExtra("itman.Vidofilm.apprtc.Incoming_Call", true)) && (localb != null))
    {
      this.callConnectionClient.H();
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public void onPause()
  {
    this.callConnectionClient.v();
    super.onPause();
  }

  public void onResume()
  {
    this.callConnectionClient.w();
    super.onResume();
  }

  public void onStart()
  {
    super.onStart();
    this.callConnectionClient.z();
  }

  public void onStateChanged(c.a parama)
  {
    if (this.stateText == null)
      return;
    if ((this.stateRunnable != null) && (parama != c.a.f))
    {
      this.stateText.removeCallbacks(this.stateRunnable);
      this.stateRunnable = null;
    }
    runOnUiThread(new Runnable(parama)
    {
      public void run()
      {
        switch (CallActivity.12.$SwitchMap$org$vidogram$VidogramUi$WebRTC$CallConnectionClient$CallConnectionState[this.val$callConnectionState.ordinal()])
        {
        default:
          return;
        case 1:
          CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", 2131166603), true);
          return;
        case 2:
          CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", 2131166579), false);
          return;
        case 3:
          if (CallActivity.this.callConnectionClient.R().b)
          {
            CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", 2131166608), true);
            return;
          }
          CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", 2131166589), false);
          return;
        case 4:
          CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", 2131166580), true);
          return;
        case 5:
          CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", 2131166604), true);
          return;
        case 6:
          CallActivity.this.setStateTextAnimated(LocaleController.getString("CallHold", 2131166762), true);
          return;
        case 7:
          CallActivity.this.setStateTextAnimated("0:00", false);
          CallActivity.this.startUpdatingCallDuration();
          return;
        case 8:
          CallActivity.this.setStateTextAnimated(LocaleController.getString("CallRejected", 2131166763), false);
          return;
        case 9:
          CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", 2131166578), false);
          return;
        case 10:
        }
        CallActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", 2131166587), true);
      }
    });
  }

  protected void onStop()
  {
    super.onStop();
    if ((Build.VERSION.SDK_INT >= 23) && (!Settings.canDrawOverlays(ApplicationLoader.applicationContext)));
    do
      return;
    while (this.callConnectionClient == null);
    this.callConnectionClient.y();
  }

  private class TextAlphaSpan extends CharacterStyle
  {
    private int alpha = 0;

    public TextAlphaSpan()
    {
    }

    public int getAlpha()
    {
      return this.alpha;
    }

    public void setAlpha(int paramInt)
    {
      this.alpha = paramInt;
      CallActivity.this.stateText.invalidate();
      CallActivity.this.stateText2.invalidate();
    }

    public void updateDrawState(TextPaint paramTextPaint)
    {
      paramTextPaint.setAlpha(this.alpha);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.WebRTCUI.CallActivity
 * JD-Core Version:    0.6.0
 */