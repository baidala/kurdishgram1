package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.widget.EditText;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class HintEditText extends EditText
{
  private String hintText;
  private float numberSize;
  private Paint paint = new Paint();
  private Rect rect = new Rect();
  private float spaceSize;
  private float textOffset;

  public HintEditText(Context paramContext)
  {
    super(paramContext);
    this.paint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
  }

  public String getHintText()
  {
    return this.hintText;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((this.hintText != null) && (length() < this.hintText.length()))
    {
      int j = getMeasuredHeight() / 2;
      float f = this.textOffset;
      int i = length();
      if (i < this.hintText.length())
      {
        if (this.hintText.charAt(i) == ' ')
          f += this.spaceSize;
        while (true)
        {
          i += 1;
          break;
          this.rect.set((int)f + AndroidUtilities.dp(1.0F), j, (int)(this.numberSize + f) - AndroidUtilities.dp(1.0F), AndroidUtilities.dp(2.0F) + j);
          paramCanvas.drawRect(this.rect, this.paint);
          f += this.numberSize;
        }
      }
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    onTextChange();
  }

  public void onTextChange()
  {
    float f;
    if (length() > 0)
      f = getPaint().measureText(getText(), 0, length());
    while (true)
    {
      this.textOffset = f;
      this.spaceSize = getPaint().measureText(" ");
      this.numberSize = getPaint().measureText("1");
      invalidate();
      return;
      f = 0.0F;
    }
  }

  public void setHintText(String paramString)
  {
    this.hintText = paramString;
    onTextChange();
    setText(getText());
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.HintEditText
 * JD-Core Version:    0.6.0
 */