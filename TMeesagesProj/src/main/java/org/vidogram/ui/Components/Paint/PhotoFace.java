package org.vidogram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.PointF;
import com.google.android.gms.e.a.a;
import com.google.android.gms.e.a.c;
import java.util.Iterator;
import java.util.List;
import org.vidogram.ui.Components.Point;
import org.vidogram.ui.Components.Size;

public class PhotoFace
{
  private float angle;
  private Point chinPoint;
  private Point eyesCenterPoint;
  private float eyesDistance;
  private Point foreheadPoint;
  private Point mouthPoint;
  private float width;

  public PhotoFace(a parama, Bitmap paramBitmap, Size paramSize, boolean paramBoolean)
  {
    Object localObject4 = parama.a();
    Object localObject1 = null;
    Object localObject3 = null;
    Object localObject2 = null;
    parama = null;
    Iterator localIterator = ((List)localObject4).iterator();
    if (localIterator.hasNext())
    {
      localObject4 = (c)localIterator.next();
      PointF localPointF = ((c)localObject4).a();
      switch (((c)localObject4).b())
      {
      case 6:
      case 7:
      case 8:
      case 9:
      default:
        localObject4 = localObject2;
        localObject2 = localObject3;
        localObject3 = localObject1;
        localObject1 = localObject4;
      case 4:
      case 10:
      case 5:
      case 11:
      }
      while (true)
      {
        localObject4 = localObject3;
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject4;
        break;
        localObject4 = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
        localObject1 = localObject2;
        localObject2 = localObject3;
        localObject3 = localObject4;
        continue;
        localObject4 = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject4;
        continue;
        localObject4 = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
        localObject2 = localObject3;
        localObject3 = localObject1;
        localObject1 = localObject4;
        continue;
        parama = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
        localObject4 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
        localObject3 = localObject4;
      }
    }
    float f1;
    float f2;
    float f3;
    float f4;
    float f5;
    if ((localObject1 != null) && (localObject3 != null))
    {
      this.eyesCenterPoint = new Point(0.5F * localObject1.x + 0.5F * localObject3.x, 0.5F * localObject1.y + 0.5F * localObject3.y);
      this.eyesDistance = (float)Math.hypot(localObject3.x - localObject1.x, localObject3.y - localObject1.y);
      this.angle = (float)Math.toDegrees(Math.atan2(localObject3.y - localObject1.y, localObject3.x - localObject1.x) + 3.141592653589793D);
      this.width = (this.eyesDistance * 2.35F);
      f1 = 0.8F * this.eyesDistance;
      f2 = (float)Math.toRadians(this.angle - 90.0F);
      f3 = this.eyesCenterPoint.x;
      f4 = (float)Math.cos(f2);
      f5 = this.eyesCenterPoint.y;
      this.foreheadPoint = new Point(f3 + f4 * f1, f1 * (float)Math.sin(f2) + f5);
    }
    if ((localObject2 != null) && (parama != null))
    {
      f1 = localObject2.x;
      f2 = parama.x;
      f3 = localObject2.y;
      this.mouthPoint = new Point(0.5F * f1 + 0.5F * f2, parama.y * 0.5F + f3 * 0.5F);
      f1 = 0.7F * this.eyesDistance;
      f2 = (float)Math.toRadians(this.angle + 90.0F);
      f3 = this.mouthPoint.x;
      f4 = (float)Math.cos(f2);
      f5 = this.mouthPoint.y;
      this.chinPoint = new Point(f3 + f4 * f1, f1 * (float)Math.sin(f2) + f5);
    }
  }

  private Point transposePoint(PointF paramPointF, Bitmap paramBitmap, Size paramSize, boolean paramBoolean)
  {
    float f1;
    float f2;
    if (paramBoolean)
    {
      f1 = paramBitmap.getHeight();
      if (!paramBoolean)
        break label66;
      f2 = paramBitmap.getWidth();
    }
    while (true)
    {
      return new Point(paramSize.width * paramPointF.x / f1, paramSize.height * paramPointF.y / f2);
      f1 = paramBitmap.getWidth();
      break;
      label66: f2 = paramBitmap.getHeight();
    }
  }

  public float getAngle()
  {
    return this.angle;
  }

  public Point getPointForAnchor(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return null;
    case 0:
      return this.foreheadPoint;
    case 1:
      return this.eyesCenterPoint;
    case 2:
      return this.mouthPoint;
    case 3:
    }
    return this.chinPoint;
  }

  public float getWidthForAnchor(int paramInt)
  {
    if (paramInt == 1)
      return this.eyesDistance;
    return this.width;
  }

  public boolean isSufficient()
  {
    return this.eyesCenterPoint != null;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.Paint.PhotoFace
 * JD-Core Version:    0.6.0
 */