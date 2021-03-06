package org.vidogram.ui.Components.Paint;

import android.graphics.PointF;

public class Point
{
  public boolean edge;
  public double x;
  public double y;
  public double z;

  public Point(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.x = paramDouble1;
    this.y = paramDouble2;
    this.z = paramDouble3;
  }

  public Point(Point paramPoint)
  {
    this.x = paramPoint.x;
    this.y = paramPoint.y;
    this.z = paramPoint.z;
  }

  private double getMagnitude()
  {
    return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
  }

  Point add(Point paramPoint)
  {
    return new Point(this.x + paramPoint.x, this.y + paramPoint.y, this.z + paramPoint.z);
  }

  void alteringAddMultiplication(Point paramPoint, double paramDouble)
  {
    this.x += paramPoint.x * paramDouble;
    this.y += paramPoint.y * paramDouble;
    this.z += paramPoint.z * paramDouble;
  }

  public boolean equals(Object paramObject)
  {
    int i = 1;
    if (paramObject == null);
    do
    {
      return false;
      if (paramObject == this)
        return true;
    }
    while (!(paramObject instanceof Point));
    paramObject = (Point)paramObject;
    if ((this.x == paramObject.x) && (this.y == paramObject.y) && (this.z == paramObject.z));
    while (true)
    {
      return i;
      i = 0;
    }
  }

  float getDistanceTo(Point paramPoint)
  {
    return (float)Math.sqrt(Math.pow(this.x - paramPoint.x, 2.0D) + Math.pow(this.y - paramPoint.y, 2.0D) + Math.pow(this.z - paramPoint.z, 2.0D));
  }

  Point getNormalized()
  {
    return multiplyByScalar(1.0D / getMagnitude());
  }

  Point multiplyAndAdd(double paramDouble, Point paramPoint)
  {
    return new Point(this.x * paramDouble + paramPoint.x, this.y * paramDouble + paramPoint.y, this.z * paramDouble + paramPoint.z);
  }

  Point multiplyByScalar(double paramDouble)
  {
    return new Point(this.x * paramDouble, this.y * paramDouble, this.z * paramDouble);
  }

  Point multiplySum(Point paramPoint, double paramDouble)
  {
    return new Point((this.x + paramPoint.x) * paramDouble, (this.y + paramPoint.y) * paramDouble, (this.z + paramPoint.z) * paramDouble);
  }

  Point substract(Point paramPoint)
  {
    return new Point(this.x - paramPoint.x, this.y - paramPoint.y, this.z - paramPoint.z);
  }

  PointF toPointF()
  {
    return new PointF((float)this.x, (float)this.y);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.Paint.Point
 * JD-Core Version:    0.6.0
 */