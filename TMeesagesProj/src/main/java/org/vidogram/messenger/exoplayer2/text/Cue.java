package org.vidogram.messenger.exoplayer2.text;

import android.text.Layout.Alignment;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Cue
{
  public static final int ANCHOR_TYPE_END = 2;
  public static final int ANCHOR_TYPE_MIDDLE = 1;
  public static final int ANCHOR_TYPE_START = 0;
  public static final float DIMEN_UNSET = 1.4E-45F;
  public static final int LINE_TYPE_FRACTION = 0;
  public static final int LINE_TYPE_NUMBER = 1;
  public static final int TYPE_UNSET = -2147483648;
  public final float line;
  public final int lineAnchor;
  public final int lineType;
  public final float position;
  public final int positionAnchor;
  public final float size;
  public final CharSequence text;
  public final Layout.Alignment textAlignment;

  public Cue(CharSequence paramCharSequence)
  {
    this(paramCharSequence, null, 1.4E-45F, -2147483648, -2147483648, 1.4E-45F, -2147483648, 1.4E-45F);
  }

  public Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3)
  {
    this.text = paramCharSequence;
    this.textAlignment = paramAlignment;
    this.line = paramFloat1;
    this.lineType = paramInt1;
    this.lineAnchor = paramInt2;
    this.position = paramFloat2;
    this.positionAnchor = paramInt3;
    this.size = paramFloat3;
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface AnchorType
  {
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface LineType
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.Cue
 * JD-Core Version:    0.6.0
 */