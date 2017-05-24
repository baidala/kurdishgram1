package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import java.io.File;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.FileDownloadProgressListener;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.BaseCell;

public class PopupAudioView extends BaseCell
  implements MediaController.FileDownloadProgressListener, SeekBar.SeekBarDelegate
{
  private int TAG;
  private int buttonPressed = 0;
  private int buttonState = 0;
  private int buttonX;
  private int buttonY;
  protected MessageObject currentMessageObject;
  private String lastTimeString = null;
  private ProgressView progressView;
  private SeekBar seekBar;
  private int seekBarX;
  private int seekBarY;
  private StaticLayout timeLayout;
  private TextPaint timePaint = new TextPaint(1);
  int timeWidth = 0;
  private int timeX;
  private boolean wasLayout = false;

  public PopupAudioView(Context paramContext)
  {
    super(paramContext);
    this.timePaint.setTextSize(AndroidUtilities.dp(16.0F));
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.seekBar = new SeekBar(getContext());
    this.seekBar.setDelegate(this);
    this.progressView = new ProgressView();
  }

  private void didPressedButton()
  {
    if (this.buttonState == 0)
    {
      boolean bool = MediaController.getInstance().playAudio(this.currentMessageObject);
      if ((!this.currentMessageObject.isOut()) && (this.currentMessageObject.isContentUnread()) && (this.currentMessageObject.messageOwner.to_id.channel_id == 0))
      {
        MessagesController.getInstance().markMessageContentAsRead(this.currentMessageObject);
        this.currentMessageObject.setContentIsRead();
      }
      if (bool)
      {
        this.buttonState = 1;
        invalidate();
      }
    }
    do
    {
      while (true)
      {
        return;
        if (this.buttonState != 1)
          break;
        if (!MediaController.getInstance().pauseAudio(this.currentMessageObject))
          continue;
        this.buttonState = 0;
        invalidate();
        return;
      }
      if (this.buttonState != 2)
        continue;
      FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
      this.buttonState = 4;
      invalidate();
      return;
    }
    while (this.buttonState != 3);
    FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.getDocument());
    this.buttonState = 2;
    invalidate();
  }

  public void downloadAudioIfNeed()
  {
    if (this.buttonState == 2)
    {
      FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
      this.buttonState = 3;
      invalidate();
    }
  }

  public final MessageObject getMessageObject()
  {
    return this.currentMessageObject;
  }

  public int getObserverTag()
  {
    return this.TAG;
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    MediaController.getInstance().removeLoadingFileObserver(this);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentMessageObject == null);
    do
    {
      return;
      if (!this.wasLayout)
      {
        requestLayout();
        return;
      }
      setDrawableBounds(Theme.chat_msgInMediaDrawable, 0, 0, getMeasuredWidth(), getMeasuredHeight());
      Theme.chat_msgInMediaDrawable.draw(paramCanvas);
    }
    while (this.currentMessageObject == null);
    paramCanvas.save();
    if ((this.buttonState == 0) || (this.buttonState == 1))
    {
      paramCanvas.translate(this.seekBarX, this.seekBarY);
      this.seekBar.draw(paramCanvas);
    }
    while (true)
    {
      paramCanvas.restore();
      int i = this.buttonState;
      this.timePaint.setColor(-6182221);
      Drawable localDrawable = Theme.chat_fileStatesDrawable[(i + 5)][this.buttonPressed];
      int j = AndroidUtilities.dp(36.0F);
      i = (j - localDrawable.getIntrinsicWidth()) / 2;
      j = (j - localDrawable.getIntrinsicHeight()) / 2;
      setDrawableBounds(localDrawable, i + this.buttonX, j + this.buttonY);
      localDrawable.draw(paramCanvas);
      paramCanvas.save();
      paramCanvas.translate(this.timeX, AndroidUtilities.dp(18.0F));
      this.timeLayout.draw(paramCanvas);
      paramCanvas.restore();
      return;
      paramCanvas.translate(this.seekBarX + AndroidUtilities.dp(12.0F), this.seekBarY);
      this.progressView.draw(paramCanvas);
    }
  }

  public void onFailedDownload(String paramString)
  {
    updateButtonState();
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.currentMessageObject == null)
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    do
    {
      return;
      this.seekBarX = AndroidUtilities.dp(54.0F);
      this.buttonX = AndroidUtilities.dp(10.0F);
      this.timeX = (getMeasuredWidth() - this.timeWidth - AndroidUtilities.dp(16.0F));
      this.seekBar.setSize(getMeasuredWidth() - AndroidUtilities.dp(70.0F) - this.timeWidth, AndroidUtilities.dp(30.0F));
      this.progressView.width = (getMeasuredWidth() - AndroidUtilities.dp(94.0F) - this.timeWidth);
      this.progressView.height = AndroidUtilities.dp(30.0F);
      this.seekBarY = AndroidUtilities.dp(13.0F);
      this.buttonY = AndroidUtilities.dp(10.0F);
      updateProgress();
    }
    while ((!paramBoolean) && (this.wasLayout));
    this.wasLayout = true;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(56.0F));
  }

  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.progressView.setProgress(paramFloat);
    if (this.buttonState != 3)
      updateButtonState();
    invalidate();
  }

  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean)
  {
  }

  public void onSeekBarDrag(float paramFloat)
  {
    if (this.currentMessageObject == null)
      return;
    this.currentMessageObject.audioProgress = paramFloat;
    MediaController.getInstance().seekToProgress(this.currentMessageObject, paramFloat);
  }

  public void onSuccessDownload(String paramString)
  {
    updateButtonState();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    boolean bool2 = this.seekBar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX, paramMotionEvent.getY() - this.seekBarY);
    if (bool2)
    {
      if (paramMotionEvent.getAction() == 0)
        getParent().requestDisallowInterceptTouchEvent(true);
      invalidate();
      return bool2;
    }
    int i = AndroidUtilities.dp(36.0F);
    boolean bool1;
    if (paramMotionEvent.getAction() == 0)
    {
      bool1 = bool2;
      if (f1 >= this.buttonX)
      {
        bool1 = bool2;
        if (f1 <= this.buttonX + i)
        {
          bool1 = bool2;
          if (f2 >= this.buttonY)
          {
            bool1 = bool2;
            if (f2 <= this.buttonY + i)
            {
              this.buttonPressed = 1;
              invalidate();
              bool1 = true;
            }
          }
        }
      }
    }
    while (true)
    {
      bool2 = bool1;
      if (bool1)
        break;
      return super.onTouchEvent(paramMotionEvent);
      bool1 = bool2;
      if (this.buttonPressed != 1)
        continue;
      if (paramMotionEvent.getAction() == 1)
      {
        this.buttonPressed = 0;
        playSoundEffect(0);
        didPressedButton();
        invalidate();
        bool1 = bool2;
        continue;
      }
      if (paramMotionEvent.getAction() == 3)
      {
        this.buttonPressed = 0;
        invalidate();
        bool1 = bool2;
        continue;
      }
      bool1 = bool2;
      if (paramMotionEvent.getAction() != 2)
        continue;
      if ((f1 >= this.buttonX) && (f1 <= this.buttonX + i) && (f2 >= this.buttonY))
      {
        bool1 = bool2;
        if (f2 <= this.buttonY + i)
          continue;
      }
      this.buttonPressed = 0;
      invalidate();
      bool1 = bool2;
    }
  }

  public void setMessageObject(MessageObject paramMessageObject)
  {
    if (this.currentMessageObject != paramMessageObject)
    {
      this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
      this.progressView.setProgressColors(-2497813, -7944712);
      this.currentMessageObject = paramMessageObject;
      this.wasLayout = false;
      requestLayout();
    }
    updateButtonState();
  }

  public void updateButtonState()
  {
    Object localObject = this.currentMessageObject.getFileName();
    if (FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists())
    {
      MediaController.getInstance().removeLoadingFileObserver(this);
      boolean bool = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
      if ((!bool) || ((bool) && (MediaController.getInstance().isAudioPaused())))
      {
        this.buttonState = 0;
        this.progressView.setProgress(0.0F);
      }
    }
    while (true)
    {
      updateProgress();
      return;
      this.buttonState = 1;
      break;
      MediaController.getInstance().addLoadingFileObserver((String)localObject, this);
      if (!FileLoader.getInstance().isLoadingFile((String)localObject))
      {
        this.buttonState = 2;
        this.progressView.setProgress(0.0F);
        continue;
      }
      this.buttonState = 3;
      localObject = ImageLoader.getInstance().getFileProgress((String)localObject);
      if (localObject != null)
      {
        this.progressView.setProgress(((Float)localObject).floatValue());
        continue;
      }
      this.progressView.setProgress(0.0F);
    }
  }

  public void updateProgress()
  {
    if (this.currentMessageObject == null)
      return;
    if (!this.seekBar.isDragging())
      this.seekBar.setProgress(this.currentMessageObject.audioProgress);
    int i;
    Object localObject;
    if (!MediaController.getInstance().isPlayingAudio(this.currentMessageObject))
    {
      i = 0;
      if (i >= this.currentMessageObject.getDocument().attributes.size())
        break label216;
      localObject = (TLRPC.DocumentAttribute)this.currentMessageObject.getDocument().attributes.get(i);
      if ((localObject instanceof TLRPC.TL_documentAttributeAudio))
        i = ((TLRPC.DocumentAttribute)localObject).duration;
    }
    while (true)
    {
      localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60) });
      if ((this.lastTimeString == null) || ((this.lastTimeString != null) && (!this.lastTimeString.equals(localObject))))
      {
        this.timeWidth = (int)Math.ceil(this.timePaint.measureText((String)localObject));
        this.timeLayout = new StaticLayout((CharSequence)localObject, this.timePaint, this.timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      invalidate();
      return;
      i += 1;
      break;
      i = this.currentMessageObject.audioProgressSec;
      continue;
      label216: i = 0;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PopupAudioView
 * JD-Core Version:    0.6.0
 */