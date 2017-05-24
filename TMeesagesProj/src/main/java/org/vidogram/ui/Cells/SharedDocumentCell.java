package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.FileDownloadProgressListener;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.LineProgressView;

public class SharedDocumentCell extends FrameLayout
  implements MediaController.FileDownloadProgressListener
{
  private int TAG = MediaController.getInstance().generateObserverTag();
  private CheckBox checkBox;
  private TextView dateTextView;
  private TextView extTextView;
  private int[] icons = { 2130837906, 2130837907, 2130837910, 2130837911 };
  private boolean loaded;
  private boolean loading;
  private MessageObject message;
  private TextView nameTextView;
  private boolean needDivider;
  private ImageView placeholderImageView;
  private LineProgressView progressView;
  private ImageView statusImageView;
  private BackupImageView thumbImageView;

  public SharedDocumentCell(Context paramContext)
  {
    super(paramContext);
    this.placeholderImageView = new ImageView(paramContext);
    Object localObject = this.placeholderImageView;
    int i;
    float f1;
    label77: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label920;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label926;
      f2 = 12.0F;
      label86: addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.extTextView = new TextView(paramContext);
      this.extTextView.setTextColor(Theme.getColor("files_iconText"));
      this.extTextView.setTextSize(1, 14.0F);
      this.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.extTextView.setLines(1);
      this.extTextView.setMaxLines(1);
      this.extTextView.setSingleLine(true);
      this.extTextView.setGravity(17);
      this.extTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.extTextView;
      if (!LocaleController.isRTL)
        break label931;
      i = 5;
      label213: if (!LocaleController.isRTL)
        break label937;
      f1 = 0.0F;
      label221: if (!LocaleController.isRTL)
        break label943;
      f2 = 16.0F;
      label230: addView((View)localObject, LayoutHelper.createFrame(32, -2.0F, i | 0x30, f1, 22.0F, f2, 0.0F));
      this.thumbImageView = new BackupImageView(paramContext);
      localObject = this.thumbImageView;
      if (!LocaleController.isRTL)
        break label948;
      i = 5;
      label280: if (!LocaleController.isRTL)
        break label954;
      f1 = 0.0F;
      label288: if (!LocaleController.isRTL)
        break label960;
      f2 = 12.0F;
      label297: addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.thumbImageView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate()
      {
        public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
        {
          int j = 4;
          paramImageReceiver = SharedDocumentCell.this.extTextView;
          if (paramBoolean1)
          {
            i = 4;
            paramImageReceiver.setVisibility(i);
            paramImageReceiver = SharedDocumentCell.this.placeholderImageView;
            if (!paramBoolean1)
              break label53;
          }
          label53: for (int i = j; ; i = 0)
          {
            paramImageReceiver.setVisibility(i);
            return;
            i = 0;
            break;
          }
        }
      });
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setLines(1);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label965;
      i = 5;
      label433: ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label971;
      i = 5;
      label458: if (!LocaleController.isRTL)
        break label977;
      f1 = 8.0F;
      label467: if (!LocaleController.isRTL)
        break label983;
      f2 = 72.0F;
      label476: addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 5.0F, f2, 0.0F));
      this.statusImageView = new ImageView(paramContext);
      this.statusImageView.setVisibility(4);
      this.statusImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sharedMedia_startStopLoadIcon"), PorterDuff.Mode.MULTIPLY));
      localObject = this.statusImageView;
      if (!LocaleController.isRTL)
        break label989;
      i = 5;
      label555: if (!LocaleController.isRTL)
        break label995;
      f1 = 8.0F;
      label564: if (!LocaleController.isRTL)
        break label1001;
      f2 = 72.0F;
      label573: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 35.0F, f2, 0.0F));
      this.dateTextView = new TextView(paramContext);
      this.dateTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      this.dateTextView.setTextSize(1, 14.0F);
      this.dateTextView.setLines(1);
      this.dateTextView.setMaxLines(1);
      this.dateTextView.setSingleLine(true);
      this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.dateTextView;
      if (!LocaleController.isRTL)
        break label1007;
      i = 5;
      label679: ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.dateTextView;
      if (!LocaleController.isRTL)
        break label1013;
      i = 5;
      label704: if (!LocaleController.isRTL)
        break label1019;
      f1 = 8.0F;
      label713: if (!LocaleController.isRTL)
        break label1025;
      f2 = 72.0F;
      label722: addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 30.0F, f2, 0.0F));
      this.progressView = new LineProgressView(paramContext);
      this.progressView.setProgressColor(Theme.getColor("sharedMedia_startStopLoadIcon"));
      localObject = this.progressView;
      if (!LocaleController.isRTL)
        break label1031;
      i = 5;
      label783: if (!LocaleController.isRTL)
        break label1037;
      f1 = 0.0F;
      label791: if (!LocaleController.isRTL)
        break label1043;
      f2 = 72.0F;
      label800: addView((View)localObject, LayoutHelper.createFrame(-1, 2.0F, i | 0x30, f1, 54.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, 2130838041);
      this.checkBox.setVisibility(4);
      this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
      paramContext = this.checkBox;
      if (!LocaleController.isRTL)
        break label1048;
      i = 5;
      label874: if (!LocaleController.isRTL)
        break label1054;
      f1 = 0.0F;
      label882: if (!LocaleController.isRTL)
        break label1060;
      f2 = 34.0F;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 30.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      label920: f1 = 12.0F;
      break label77;
      label926: f2 = 0.0F;
      break label86;
      label931: i = 3;
      break label213;
      label937: f1 = 16.0F;
      break label221;
      label943: f2 = 0.0F;
      break label230;
      label948: i = 3;
      break label280;
      label954: f1 = 12.0F;
      break label288;
      label960: f2 = 0.0F;
      break label297;
      label965: i = 3;
      break label433;
      label971: i = 3;
      break label458;
      label977: f1 = 72.0F;
      break label467;
      label983: f2 = 8.0F;
      break label476;
      label989: i = 3;
      break label555;
      label995: f1 = 72.0F;
      break label564;
      label1001: f2 = 8.0F;
      break label573;
      label1007: i = 3;
      break label679;
      label1013: i = 3;
      break label704;
      label1019: f1 = 72.0F;
      break label713;
      label1025: f2 = 8.0F;
      break label722;
      label1031: i = 3;
      break label783;
      label1037: f1 = 72.0F;
      break label791;
      label1043: f2 = 0.0F;
      break label800;
      label1048: i = 3;
      break label874;
      label1054: f1 = 34.0F;
      break label882;
      label1060: f2 = 0.0F;
    }
  }

  private int getThumbForNameOrMime(String paramString1, String paramString2)
  {
    int i;
    if ((paramString1 != null) && (paramString1.length() != 0))
      if ((paramString1.contains(".doc")) || (paramString1.contains(".txt")) || (paramString1.contains(".psd")))
        i = 0;
    while (true)
    {
      int j = i;
      if (i == -1)
      {
        i = paramString1.lastIndexOf('.');
        if (i != -1)
          break label212;
        paramString2 = "";
        label63: if (paramString2.length() == 0)
          break label223;
      }
      label212: label223: for (j = paramString2.charAt(0) % this.icons.length; ; j = paramString1.charAt(0) % this.icons.length)
      {
        return this.icons[j];
        if ((paramString1.contains(".xls")) || (paramString1.contains(".csv")))
        {
          i = 1;
          break;
        }
        if ((paramString1.contains(".pdf")) || (paramString1.contains(".ppt")) || (paramString1.contains(".key")))
        {
          i = 2;
          break;
        }
        if ((!paramString1.contains(".zip")) && (!paramString1.contains(".rar")) && (!paramString1.contains(".ai")) && (!paramString1.contains(".mp3")) && (!paramString1.contains(".mov")) && (!paramString1.contains(".avi")))
          break label246;
        i = 3;
        break;
        paramString2 = paramString1.substring(i + 1);
        break label63;
      }
      return this.icons[0];
      label246: i = -1;
    }
  }

  public MessageObject getMessage()
  {
    return this.message;
  }

  public int getObserverTag()
  {
    return this.TAG;
  }

  public boolean isLoaded()
  {
    return this.loaded;
  }

  public boolean isLoading()
  {
    return this.loading;
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    MediaController.getInstance().removeLoadingFileObserver(this);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
  }

  public void onFailedDownload(String paramString)
  {
    updateFileExistIcon();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    int i = AndroidUtilities.dp(56.0F);
    if (this.needDivider);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
    }
  }

  public void onProgressDownload(String paramString, float paramFloat)
  {
    if (this.progressView.getVisibility() != 0)
      updateFileExistIcon();
    this.progressView.setProgress(paramFloat, true);
  }

  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean)
  {
  }

  public void onSuccessDownload(String paramString)
  {
    this.progressView.setProgress(1.0F, true);
    updateFileExistIcon();
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox.getVisibility() != 0)
      this.checkBox.setVisibility(0);
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setDocument(MessageObject paramMessageObject, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.message = paramMessageObject;
    this.loaded = false;
    this.loading = false;
    Object localObject3;
    int i;
    Object localObject1;
    label65: Object localObject2;
    if ((paramMessageObject != null) && (paramMessageObject.getDocument() != null))
      if (paramMessageObject.isMusic())
        if (paramMessageObject.type == 0)
        {
          localObject3 = paramMessageObject.messageOwner.media.webpage.document;
          i = 0;
          localObject1 = null;
          localObject2 = localObject1;
          if (i >= ((TLRPC.Document)localObject3).attributes.size())
            break label198;
          localObject2 = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject3).attributes.get(i);
          if ((!(localObject2 instanceof TLRPC.TL_documentAttributeAudio)) || (((((TLRPC.DocumentAttribute)localObject2).performer == null) || (((TLRPC.DocumentAttribute)localObject2).performer.length() == 0)) && ((((TLRPC.DocumentAttribute)localObject2).title == null) || (((TLRPC.DocumentAttribute)localObject2).title.length() == 0))))
            break label583;
          localObject1 = paramMessageObject.getMusicAuthor() + " - " + paramMessageObject.getMusicTitle();
        }
    label198: label583: 
    while (true)
    {
      i += 1;
      break label65;
      localObject3 = paramMessageObject.messageOwner.media.document;
      break;
      localObject2 = null;
      localObject3 = FileLoader.getDocumentFileName(paramMessageObject.getDocument());
      localObject1 = localObject2;
      if (localObject2 == null)
        localObject1 = localObject3;
      this.nameTextView.setText((CharSequence)localObject1);
      this.placeholderImageView.setVisibility(0);
      this.extTextView.setVisibility(0);
      this.placeholderImageView.setImageResource(getThumbForNameOrMime((String)localObject3, paramMessageObject.getDocument().mime_type));
      localObject2 = this.extTextView;
      i = ((String)localObject3).lastIndexOf('.');
      if (i == -1)
      {
        localObject1 = "";
        ((TextView)localObject2).setText((CharSequence)localObject1);
        if ((!(paramMessageObject.getDocument().thumb instanceof TLRPC.TL_photoSizeEmpty)) && (paramMessageObject.getDocument().thumb != null))
          break label481;
        this.thumbImageView.setVisibility(4);
        this.thumbImageView.setImageBitmap(null);
        label334: long l = paramMessageObject.messageOwner.date * 1000L;
        this.dateTextView.setText(String.format("%s, %s", new Object[] { AndroidUtilities.formatFileSize(paramMessageObject.getDocument().size), LocaleController.formatString("formatDateAtTime", 2131166662, new Object[] { LocaleController.getInstance().formatterYear.format(new Date(l)), LocaleController.getInstance().formatterDay.format(new Date(l)) }) }));
        if (this.needDivider)
          break label578;
      }
      for (paramBoolean = true; ; paramBoolean = false)
      {
        setWillNotDraw(paramBoolean);
        this.progressView.setProgress(0.0F, false);
        updateFileExistIcon();
        return;
        localObject1 = ((String)localObject3).substring(i + 1).toLowerCase();
        break;
        label481: this.thumbImageView.setVisibility(0);
        this.thumbImageView.setImage(paramMessageObject.getDocument().thumb.location, "40_40", (Drawable)null);
        break label334;
        this.nameTextView.setText("");
        this.extTextView.setText("");
        this.dateTextView.setText("");
        this.placeholderImageView.setVisibility(0);
        this.extTextView.setVisibility(0);
        this.thumbImageView.setVisibility(4);
        this.thumbImageView.setImageBitmap(null);
        break label437;
      }
    }
  }

  public void setTextAndValueAndTypeAndThumb(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    this.nameTextView.setText(paramString1);
    this.dateTextView.setText(paramString2);
    if (paramString3 != null)
    {
      this.extTextView.setVisibility(0);
      this.extTextView.setText(paramString3);
      if (paramInt != 0)
        break label110;
      this.placeholderImageView.setImageResource(getThumbForNameOrMime(paramString1, paramString3));
      this.placeholderImageView.setVisibility(0);
      label62: if ((paramString4 == null) && (paramInt == 0))
        break label165;
      if (paramString4 == null)
        break label121;
      this.thumbImageView.setImage(paramString4, "40_40", null);
    }
    while (true)
    {
      this.thumbImageView.setVisibility(0);
      return;
      this.extTextView.setVisibility(4);
      break;
      label110: this.placeholderImageView.setVisibility(4);
      break label62;
      label121: paramString1 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), paramInt);
      Theme.setCombinedDrawableColor(paramString1, Theme.getColor("files_folderIconBackground"), false);
      Theme.setCombinedDrawableColor(paramString1, Theme.getColor("files_folderIcon"), true);
      this.thumbImageView.setImageDrawable(paramString1);
    }
    label165: this.thumbImageView.setImageBitmap(null);
    this.thumbImageView.setVisibility(4);
  }

  public void updateFileExistIcon()
  {
    if ((this.message != null) && (this.message.messageOwner.media != null))
    {
      Object localObject2 = null;
      Object localObject1;
      if ((this.message.messageOwner.attachPath != null) && (this.message.messageOwner.attachPath.length() != 0))
      {
        localObject1 = localObject2;
        if (new File(this.message.messageOwner.attachPath).exists());
      }
      else
      {
        localObject1 = localObject2;
        if (!FileLoader.getPathToMessage(this.message.messageOwner).exists())
          localObject1 = FileLoader.getAttachFileName(this.message.getDocument());
      }
      this.loaded = false;
      if (localObject1 == null)
      {
        this.statusImageView.setVisibility(4);
        this.dateTextView.setPadding(0, 0, 0, 0);
        this.loading = false;
        this.loaded = true;
        MediaController.getInstance().removeLoadingFileObserver(this);
        return;
      }
      MediaController.getInstance().addLoadingFileObserver((String)localObject1, this);
      this.loading = FileLoader.getInstance().isLoadingFile((String)localObject1);
      this.statusImageView.setVisibility(0);
      localObject2 = this.statusImageView;
      int i;
      if (this.loading)
      {
        i = 2130837909;
        ((ImageView)localObject2).setImageResource(i);
        localObject2 = this.dateTextView;
        if (!LocaleController.isRTL)
          break label296;
        i = 0;
        label218: if (!LocaleController.isRTL)
          break label305;
      }
      label296: label305: for (int j = AndroidUtilities.dp(14.0F); ; j = 0)
      {
        ((TextView)localObject2).setPadding(i, 0, j, 0);
        if (!this.loading)
          break label310;
        this.progressView.setVisibility(0);
        localObject2 = ImageLoader.getInstance().getFileProgress((String)localObject1);
        localObject1 = localObject2;
        if (localObject2 == null)
          localObject1 = Float.valueOf(0.0F);
        this.progressView.setProgress(((Float)localObject1).floatValue(), false);
        return;
        i = 2130837908;
        break;
        i = AndroidUtilities.dp(14.0F);
        break label218;
      }
      label310: this.progressView.setVisibility(4);
      return;
    }
    this.loading = false;
    this.loaded = true;
    this.progressView.setVisibility(4);
    this.progressView.setProgress(0.0F, false);
    this.statusImageView.setVisibility(4);
    this.dateTextView.setPadding(0, 0, 0, 0);
    MediaController.getInstance().removeLoadingFileObserver(this);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.SharedDocumentCell
 * JD-Core Version:    0.6.0
 */