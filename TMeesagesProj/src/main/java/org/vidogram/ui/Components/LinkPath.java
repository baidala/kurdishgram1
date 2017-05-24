package org.vidogram.ui.Components;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.text.StaticLayout;

public class LinkPath extends Path
{
  private StaticLayout currentLayout;
  private int currentLine;
  private float heightOffset;
  private float lastTop = -1.0F;

  public void addRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Path.Direction paramDirection)
  {
    float f1 = paramFloat2 + this.heightOffset;
    float f2 = paramFloat4 + this.heightOffset;
    if (this.lastTop == -1.0F)
      this.lastTop = f1;
    while (true)
    {
      paramFloat2 = this.currentLayout.getLineRight(this.currentLine);
      paramFloat4 = this.currentLayout.getLineLeft(this.currentLine);
      if (paramFloat1 < paramFloat2)
        break;
      return;
      if (this.lastTop == f1)
        continue;
      this.lastTop = f1;
      this.currentLine += 1;
    }
    if (paramFloat3 > paramFloat2);
    while (true)
    {
      if (paramFloat1 < paramFloat4)
        paramFloat1 = paramFloat4;
      while (true)
      {
        if (f2 != this.currentLayout.getHeight())
          paramFloat3 = this.currentLayout.getSpacingAdd();
        while (true)
        {
          super.addRect(paramFloat1, f1, paramFloat2, f2 - paramFloat3, paramDirection);
          return;
          paramFloat3 = 0.0F;
        }
      }
      paramFloat2 = paramFloat3;
    }
  }

  public void setCurrentLayout(StaticLayout paramStaticLayout, int paramInt, float paramFloat)
  {
    this.currentLayout = paramStaticLayout;
    this.currentLine = paramStaticLayout.getLineForOffset(paramInt);
    this.lastTop = -1.0F;
    this.heightOffset = paramFloat;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.LinkPath
 * JD-Core Version:    0.6.0
 */