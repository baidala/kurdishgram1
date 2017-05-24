package org.vidogram.VidogramUi.WebRTC;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
import android.util.Log;
import org.webrtc.ThreadUtils.ThreadChecker;

public class b
  implements SensorEventListener
{
  private final ThreadUtils.ThreadChecker a = new ThreadUtils.ThreadChecker();
  private final Runnable b;
  private final SensorManager c;
  private Sensor d = null;
  private boolean e = false;

  private b(Context paramContext, Runnable paramRunnable)
  {
    this.b = paramRunnable;
    this.c = ((SensorManager)paramContext.getSystemService("sensor"));
  }

  static b a(Context paramContext, Runnable paramRunnable)
  {
    return new b(paramContext, paramRunnable);
  }

  private boolean d()
  {
    if (this.d != null)
      return true;
    this.d = this.c.getDefaultSensor(8);
    if (this.d == null)
      return false;
    e();
    return true;
  }

  private void e()
  {
    if (this.d == null)
      return;
    StringBuilder localStringBuilder = new StringBuilder("Proximity sensor: ");
    localStringBuilder.append("name=").append(this.d.getName());
    localStringBuilder.append(", vendor: ").append(this.d.getVendor());
    localStringBuilder.append(", power: ").append(this.d.getPower());
    localStringBuilder.append(", resolution: ").append(this.d.getResolution());
    localStringBuilder.append(", max range: ").append(this.d.getMaximumRange());
    if (Build.VERSION.SDK_INT >= 9)
      localStringBuilder.append(", min delay: ").append(this.d.getMinDelay());
    if (Build.VERSION.SDK_INT >= 20)
      localStringBuilder.append(", type: ").append(this.d.getStringType());
    if (Build.VERSION.SDK_INT >= 21)
    {
      localStringBuilder.append(", max delay: ").append(this.d.getMaxDelay());
      localStringBuilder.append(", reporting mode: ").append(this.d.getReportingMode());
      localStringBuilder.append(", isWakeUpSensor: ").append(this.d.isWakeUpSensor());
    }
    Log.d("AppRTCProximitySensor", localStringBuilder.toString());
  }

  public boolean a()
  {
    this.a.checkIsOnValidThread();
    if (!d())
      return false;
    this.c.registerListener(this, this.d, 3);
    return true;
  }

  public void b()
  {
    try
    {
      this.a.checkIsOnValidThread();
      try
      {
        label7: if (this.d == null)
          return;
        this.c.unregisterListener(this, this.d);
        return;
      }
      catch (Exception localException1)
      {
        return;
      }
    }
    catch (Exception localException2)
    {
      break label7;
    }
  }

  public boolean c()
  {
    this.a.checkIsOnValidThread();
    return this.e;
  }

  public final void onAccuracyChanged(Sensor paramSensor, int paramInt)
  {
    this.a.checkIsOnValidThread();
    if (paramInt == 0)
      Log.e("AppRTCProximitySensor", "The values returned by this sensor cannot be trusted");
  }

  public final void onSensorChanged(SensorEvent paramSensorEvent)
  {
    this.a.checkIsOnValidThread();
    if (paramSensorEvent.values[0] < this.d.getMaximumRange())
      Log.d("AppRTCProximitySensor", "Proximity sensor => NEAR state");
    for (this.e = true; ; this.e = false)
    {
      if (this.b != null)
        this.b.run();
      return;
      Log.d("AppRTCProximitySensor", "Proximity sensor => FAR state");
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.b
 * JD-Core Version:    0.6.0
 */