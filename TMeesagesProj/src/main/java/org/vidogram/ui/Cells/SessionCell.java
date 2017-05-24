package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.tgnet.TLRPC.TL_authorization;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class SessionCell extends FrameLayout
{
  private TextView detailExTextView;
  private TextView detailTextView;
  private TextView nameTextView;
  boolean needDivider;
  private TextView onlineTextView;

  public SessionCell(Context paramContext)
  {
    super(paramContext);
    Object localObject = new LinearLayout(paramContext);
    ((LinearLayout)localObject).setOrientation(0);
    ((LinearLayout)localObject).setWeightSum(1.0F);
    if (LocaleController.isRTL)
    {
      i = 5;
      addView((View)localObject, LayoutHelper.createFrame(-1, 30.0F, i | 0x30, 17.0F, 11.0F, 11.0F, 0.0F));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setLines(1);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      TextView localTextView = this.nameTextView;
      if (!LocaleController.isRTL)
        break label503;
      i = 5;
      label148: localTextView.setGravity(i | 0x30);
      this.onlineTextView = new TextView(paramContext);
      this.onlineTextView.setTextSize(1, 14.0F);
      localTextView = this.onlineTextView;
      if (!LocaleController.isRTL)
        break label508;
      i = 3;
      label193: localTextView.setGravity(i | 0x30);
      if (!LocaleController.isRTL)
        break label513;
      ((LinearLayout)localObject).addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 51, 0, 2, 0, 0));
      ((LinearLayout)localObject).addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0F, 53, 10, 0, 0, 0));
      label249: this.detailTextView = new TextView(paramContext);
      this.detailTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.detailTextView.setTextSize(1, 14.0F);
      this.detailTextView.setLines(1);
      this.detailTextView.setMaxLines(1);
      this.detailTextView.setSingleLine(true);
      this.detailTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.detailTextView;
      if (!LocaleController.isRTL)
        break label557;
      i = 5;
      label330: ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.detailTextView;
      if (!LocaleController.isRTL)
        break label562;
      i = 5;
      label351: addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, 17.0F, 36.0F, 17.0F, 0.0F));
      this.detailExTextView = new TextView(paramContext);
      this.detailExTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      this.detailExTextView.setTextSize(1, 14.0F);
      this.detailExTextView.setLines(1);
      this.detailExTextView.setMaxLines(1);
      this.detailExTextView.setSingleLine(true);
      this.detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
      paramContext = this.detailExTextView;
      if (!LocaleController.isRTL)
        break label567;
      i = 5;
      label454: paramContext.setGravity(i | 0x30);
      paramContext = this.detailExTextView;
      if (!LocaleController.isRTL)
        break label572;
    }
    label513: label557: label562: label567: label572: for (int i = 5; ; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, 17.0F, 59.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
      label503: i = 3;
      break label148;
      label508: i = 5;
      break label193;
      ((LinearLayout)localObject).addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0F, 51, 0, 0, 10, 0));
      ((LinearLayout)localObject).addView(this.onlineTextView, LayoutHelper.createLinear(-2, -1, 53, 0, 2, 0, 0));
      break label249;
      i = 3;
      break label330;
      i = 3;
      break label351;
      i = 3;
      break label454;
    }
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    int i = AndroidUtilities.dp(90.0F);
    if (this.needDivider);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
    }
  }

  public void setSession(TLRPC.TL_authorization paramTL_authorization, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.nameTextView.setText(String.format(Locale.US, "%s %s", new Object[] { paramTL_authorization.app_name, paramTL_authorization.app_version }));
    if ((paramTL_authorization.flags & 0x1) != 0)
    {
      setTag("windowBackgroundWhiteValueText");
      this.onlineTextView.setText(LocaleController.getString("Online", 2131166155));
      this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
    }
    while (true)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (paramTL_authorization.ip.length() != 0)
        localStringBuilder.append(paramTL_authorization.ip);
      if (paramTL_authorization.country.length() != 0)
      {
        if (localStringBuilder.length() != 0)
          localStringBuilder.append(" ");
        localStringBuilder.append("— ");
        localStringBuilder.append(paramTL_authorization.country);
      }
      this.detailExTextView.setText(localStringBuilder);
      localStringBuilder = new StringBuilder();
      if (paramTL_authorization.device_model.length() != 0)
        localStringBuilder.append(paramTL_authorization.device_model);
      if ((paramTL_authorization.system_version.length() != 0) || (paramTL_authorization.platform.length() != 0))
      {
        if (localStringBuilder.length() != 0)
          localStringBuilder.append(", ");
        if (paramTL_authorization.platform.length() != 0)
          localStringBuilder.append(paramTL_authorization.platform);
        if (paramTL_authorization.system_version.length() != 0)
        {
          if (paramTL_authorization.platform.length() != 0)
            localStringBuilder.append(" ");
          localStringBuilder.append(paramTL_authorization.system_version);
        }
      }
      if ((paramTL_authorization.flags & 0x2) == 0)
      {
        if (localStringBuilder.length() != 0)
          localStringBuilder.append(", ");
        localStringBuilder.append(LocaleController.getString("UnofficialApp", 2131166536));
        localStringBuilder.append(" (ID: ");
        localStringBuilder.append(paramTL_authorization.api_id);
        localStringBuilder.append(")");
      }
      this.detailTextView.setText(localStringBuilder);
      return;
      setTag("windowBackgroundWhiteGrayText3");
      this.onlineTextView.setText(LocaleController.stringForMessageListDate(paramTL_authorization.date_active));
      this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.SessionCell
 * JD-Core Version:    0.6.0
 */