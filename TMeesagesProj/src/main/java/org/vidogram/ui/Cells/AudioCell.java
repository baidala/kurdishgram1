package org.vidogram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.AudioEntry;
import org.vidogram.messenger.MessageObject;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.LayoutHelper;

public class AudioCell extends FrameLayout
{
  private MediaController.AudioEntry audioEntry;
  private TextView authorTextView;
  private CheckBox checkBox;
  private AudioCellDelegate delegate;
  private TextView genreTextView;
  private boolean needDivider;
  private ImageView playButton;
  private TextView timeTextView;
  private TextView titleTextView;

  public AudioCell(Context paramContext)
  {
    super(paramContext);
    this.playButton = new ImageView(paramContext);
    Object localObject = this.playButton;
    int i;
    float f1;
    label43: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label794;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label800;
      f2 = 13.0F;
      label52: addView((View)localObject, LayoutHelper.createFrame(46, 46.0F, i | 0x30, f1, 13.0F, f2, 0.0F));
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (AudioCell.this.audioEntry != null)
          {
            if ((!MediaController.getInstance().isPlayingAudio(AudioCell.this.audioEntry.messageObject)) || (MediaController.getInstance().isAudioPaused()))
              break label64;
            MediaController.getInstance().pauseAudio(AudioCell.this.audioEntry.messageObject);
            AudioCell.this.setPlayDrawable(false);
          }
          label64: 
          do
          {
            do
            {
              return;
              paramView = new ArrayList();
              paramView.add(AudioCell.this.audioEntry.messageObject);
            }
            while (!MediaController.getInstance().setPlaylist(paramView, AudioCell.this.audioEntry.messageObject));
            AudioCell.this.setPlayDrawable(true);
          }
          while (AudioCell.this.delegate == null);
          AudioCell.this.delegate.startedPlayingAudio(AudioCell.this.audioEntry.messageObject);
        }
      });
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.titleTextView.setTextSize(1, 16.0F);
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.titleTextView.setLines(1);
      this.titleTextView.setMaxLines(1);
      this.titleTextView.setSingleLine(true);
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.titleTextView;
      if (!LocaleController.isRTL)
        break label805;
      i = 5;
      label185: ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.titleTextView;
      if (!LocaleController.isRTL)
        break label811;
      i = 5;
      label210: if (!LocaleController.isRTL)
        break label817;
      f1 = 50.0F;
      label219: if (!LocaleController.isRTL)
        break label823;
      f2 = 72.0F;
      label228: addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 7.0F, f2, 0.0F));
      this.genreTextView = new TextView(paramContext);
      this.genreTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.genreTextView.setTextSize(1, 14.0F);
      this.genreTextView.setLines(1);
      this.genreTextView.setMaxLines(1);
      this.genreTextView.setSingleLine(true);
      this.genreTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.genreTextView;
      if (!LocaleController.isRTL)
        break label829;
      i = 5;
      label333: ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.genreTextView;
      if (!LocaleController.isRTL)
        break label835;
      i = 5;
      label358: if (!LocaleController.isRTL)
        break label841;
      f1 = 50.0F;
      label367: if (!LocaleController.isRTL)
        break label847;
      f2 = 72.0F;
      label376: addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 28.0F, f2, 0.0F));
      this.authorTextView = new TextView(paramContext);
      this.authorTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.authorTextView.setTextSize(1, 14.0F);
      this.authorTextView.setLines(1);
      this.authorTextView.setMaxLines(1);
      this.authorTextView.setSingleLine(true);
      this.authorTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.authorTextView;
      if (!LocaleController.isRTL)
        break label853;
      i = 5;
      label481: ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.authorTextView;
      if (!LocaleController.isRTL)
        break label859;
      i = 5;
      label506: if (!LocaleController.isRTL)
        break label865;
      f1 = 50.0F;
      label515: if (!LocaleController.isRTL)
        break label871;
      f2 = 72.0F;
      label524: addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 44.0F, f2, 0.0F));
      this.timeTextView = new TextView(paramContext);
      this.timeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      this.timeTextView.setTextSize(1, 13.0F);
      this.timeTextView.setLines(1);
      this.timeTextView.setMaxLines(1);
      this.timeTextView.setSingleLine(true);
      this.timeTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.timeTextView;
      if (!LocaleController.isRTL)
        break label877;
      i = 3;
      label629: ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.timeTextView;
      if (!LocaleController.isRTL)
        break label883;
      i = 3;
      label654: if (!LocaleController.isRTL)
        break label889;
      f1 = 18.0F;
      label663: if (!LocaleController.isRTL)
        break label894;
      f2 = 0.0F;
      label671: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 11.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, 2130838041);
      this.checkBox.setVisibility(0);
      this.checkBox.setColor(Theme.getColor("musicPicker_checkbox"), Theme.getColor("musicPicker_checkboxCheck"));
      paramContext = this.checkBox;
      if (!LocaleController.isRTL)
        break label900;
      i = j;
      label748: if (!LocaleController.isRTL)
        break label906;
      f1 = 18.0F;
      label757: if (!LocaleController.isRTL)
        break label911;
      f2 = 0.0F;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 39.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      label794: f1 = 13.0F;
      break label43;
      label800: f2 = 0.0F;
      break label52;
      label805: i = 3;
      break label185;
      label811: i = 3;
      break label210;
      label817: f1 = 72.0F;
      break label219;
      label823: f2 = 50.0F;
      break label228;
      label829: i = 3;
      break label333;
      label835: i = 3;
      break label358;
      label841: f1 = 72.0F;
      break label367;
      label847: f2 = 50.0F;
      break label376;
      label853: i = 3;
      break label481;
      label859: i = 3;
      break label506;
      label865: f1 = 72.0F;
      break label515;
      label871: f2 = 50.0F;
      break label524;
      label877: i = 5;
      break label629;
      label883: i = 5;
      break label654;
      label889: f1 = 0.0F;
      break label663;
      label894: f2 = 18.0F;
      break label671;
      label900: i = 5;
      break label748;
      label906: f1 = 0.0F;
      break label757;
      label911: f2 = 18.0F;
    }
  }

  private void setPlayDrawable(boolean paramBoolean)
  {
    Object localObject1 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(46.0F), Theme.getColor("musicPicker_buttonBackground"), Theme.getColor("musicPicker_buttonBackground"));
    Object localObject2 = getResources();
    if (paramBoolean);
    for (int i = 2130837621; ; i = 2130837622)
    {
      localObject2 = ((Resources)localObject2).getDrawable(i);
      ((Drawable)localObject2).setColorFilter(new PorterDuffColorFilter(Theme.getColor("musicPicker_buttonIcon"), PorterDuff.Mode.MULTIPLY));
      localObject1 = new CombinedDrawable((Drawable)localObject1, (Drawable)localObject2);
      ((CombinedDrawable)localObject1).setCustomSize(AndroidUtilities.dp(46.0F), AndroidUtilities.dp(46.0F));
      this.playButton.setBackgroundDrawable((Drawable)localObject1);
      return;
    }
  }

  public MediaController.AudioEntry getAudioEntry()
  {
    return this.audioEntry;
  }

  public TextView getAuthorTextView()
  {
    return this.authorTextView;
  }

  public CheckBox getCheckBox()
  {
    return this.checkBox;
  }

  public TextView getGenreTextView()
  {
    return this.genreTextView;
  }

  public ImageView getPlayButton()
  {
    return this.playButton;
  }

  public TextView getTimeTextView()
  {
    return this.timeTextView;
  }

  public TextView getTitleTextView()
  {
    return this.titleTextView;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth(), getHeight() - 1, Theme.dividerPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    int i = AndroidUtilities.dp(72.0F);
    if (this.needDivider);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
    }
  }

  public void setAudio(MediaController.AudioEntry paramAudioEntry, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool2 = true;
    this.audioEntry = paramAudioEntry;
    this.titleTextView.setText(this.audioEntry.title);
    this.genreTextView.setText(this.audioEntry.genre);
    this.authorTextView.setText(this.audioEntry.author);
    this.timeTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(this.audioEntry.duration / 60), Integer.valueOf(this.audioEntry.duration % 60) }));
    boolean bool1;
    if ((MediaController.getInstance().isPlayingAudio(this.audioEntry.messageObject)) && (!MediaController.getInstance().isAudioPaused()))
    {
      bool1 = true;
      setPlayDrawable(bool1);
      this.needDivider = paramBoolean1;
      if (paramBoolean1)
        break label166;
    }
    label166: for (paramBoolean1 = bool2; ; paramBoolean1 = false)
    {
      setWillNotDraw(paramBoolean1);
      this.checkBox.setChecked(paramBoolean2, false);
      return;
      bool1 = false;
      break;
    }
  }

  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean, true);
  }

  public void setDelegate(AudioCellDelegate paramAudioCellDelegate)
  {
    this.delegate = paramAudioCellDelegate;
  }

  public static abstract interface AudioCellDelegate
  {
    public abstract void startedPlayingAudio(MessageObject paramMessageObject);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.AudioCell
 * JD-Core Version:    0.6.0
 */