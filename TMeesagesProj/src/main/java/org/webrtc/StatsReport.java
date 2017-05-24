package org.webrtc;

public class StatsReport
{
  public final String id;
  public final double timestamp;
  public final String type;
  public final Value[] values;

  public StatsReport(String paramString1, String paramString2, double paramDouble, Value[] paramArrayOfValue)
  {
    this.id = paramString1;
    this.type = paramString2;
    this.timestamp = paramDouble;
    this.values = paramArrayOfValue;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("id: ").append(this.id).append(", type: ").append(this.type).append(", timestamp: ").append(this.timestamp).append(", values: ");
    int i = 0;
    while (i < this.values.length)
    {
      localStringBuilder.append(this.values[i].toString()).append(", ");
      i += 1;
    }
    return localStringBuilder.toString();
  }

  public static class Value
  {
    public final String name;
    public final String value;

    public Value(String paramString1, String paramString2)
    {
      this.name = paramString1;
      this.value = paramString2;
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[").append(this.name).append(": ").append(this.value).append("]");
      return localStringBuilder.toString();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.StatsReport
 * JD-Core Version:    0.6.0
 */