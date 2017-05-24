package org.vidogram.VidogramUi.WebRTC.b;

import android.os.SystemClock;
import java.util.Locale;

public class c
{
  private long a;
  private int b;
  private double c;

  public static String a(double paramDouble)
  {
    if (paramDouble > 1000000.0D)
      return String.format(Locale.getDefault(), "%.2fMbps", new Object[] { Double.valueOf(1.0E-006D * paramDouble) });
    if (paramDouble > 1000.0D)
      return String.format(Locale.getDefault(), "%.0fKbps", new Object[] { Double.valueOf(0.001D * paramDouble) });
    return String.format(Locale.getDefault(), "%.0fbps", new Object[] { Double.valueOf(paramDouble) });
  }

  public String a()
  {
    return a(this.c);
  }

  void a(int paramInt)
  {
    long l = SystemClock.uptimeMillis();
    if ((this.a != 0L) && (paramInt > this.b))
      this.c = ((paramInt - this.b) * 8 / (l - this.a));
    this.b = paramInt;
    this.a = l;
  }

  public double b()
  {
    return this.c;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.b.c
 * JD-Core Version:    0.6.0
 */