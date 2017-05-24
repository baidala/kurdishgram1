package org.vidogram.VidogramUi.WebRTC.b;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.webrtc.StatsReport;
import org.webrtc.StatsReport.Value;

public class g
{
  private c a = new c();
  private c b = new c();
  private c c = new c();
  private c d = new c();
  private c e = new c();
  private c f = new c();
  private String g;
  private double h;
  private int i;
  private int j;
  private String k;
  private String l;
  private String m;

  @d(a="googRetransmitBitrate", b="bwe")
  private String n;

  @d(a="googTransmitBitrate", b="bwe")
  private String o;

  public static void a(Map<String, String> paramMap, String paramString, g paramg)
  {
    Field[] arrayOfField = g.class.getDeclaredFields();
    int i2 = arrayOfField.length;
    int i1 = 0;
    while (i1 < i2)
    {
      Field localField = arrayOfField[i1];
      d locald = (d)localField.getAnnotation(d.class);
      if (locald != null)
      {
        String str = (String)paramMap.get(locald.a());
        if ((str != null) && (locald.b().equals(paramString)))
          e.a(localField, paramg, str);
      }
      i1 += 1;
    }
  }

  private Map<String, String> b(StatsReport paramStatsReport)
  {
    HashMap localHashMap = new HashMap();
    paramStatsReport = paramStatsReport.values;
    int i2 = paramStatsReport.length;
    int i1 = 0;
    while (i1 < i2)
    {
      Object localObject = paramStatsReport[i1];
      localHashMap.put(localObject.name, localObject.value);
      i1 += 1;
    }
    return localHashMap;
  }

  private void c(StatsReport paramStatsReport)
  {
    paramStatsReport = b(paramStatsReport);
    if ("true".equals((String)paramStatsReport.get("googActiveConnection")))
    {
      a(paramStatsReport, "connextion", this);
      int i1 = Integer.valueOf((String)paramStatsReport.get("bytesReceived")).intValue();
      this.i = i1;
      this.c.a(i1);
      this.g = this.c.a();
      i1 = Integer.valueOf((String)paramStatsReport.get("bytesSent")).intValue();
      this.j = i1;
      this.d.a(i1);
      this.k = this.d.a();
      this.h = this.d.b();
    }
  }

  public double a()
  {
    return this.h;
  }

  public void a(StatsReport paramStatsReport)
  {
    String str1 = paramStatsReport.type;
    String str2 = paramStatsReport.id;
    if ((str1.equals("ssrc")) && (str2.contains("ssrc")));
    do
      return;
    while ((str2.equals("bweforvideo")) || (!str1.equals("googCandidatePair")));
    c(paramStatsReport);
  }

  public String toString()
  {
    return "\n\n\n\n\n\nRTCStatsReport{connectionReceivedBitrate='" + this.g + '\'' + ",\n connectionSendBitrate='" + this.k + '\'' + ",\n\n availableReceiveBandwidth='" + this.l + '\'' + ",\n availableSendBandwidth='" + this.m + '\'' + ",\n\n retransmitBitrate='" + this.n + '\'' + ",\n transmitBitrate='" + this.o + '\'' + '}';
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.b.g
 * JD-Core Version:    0.6.0
 */