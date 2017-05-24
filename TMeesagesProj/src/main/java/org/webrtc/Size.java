package org.webrtc;

public class Size
{
  public int height;
  public int width;

  public Size(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Size));
    do
    {
      return false;
      paramObject = (Size)paramObject;
    }
    while ((this.width != paramObject.width) || (this.height != paramObject.height));
    return true;
  }

  public int hashCode()
  {
    return 65537 * this.width + 1 + this.height;
  }

  public String toString()
  {
    return this.width + "x" + this.height;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.Size
 * JD-Core Version:    0.6.0
 */