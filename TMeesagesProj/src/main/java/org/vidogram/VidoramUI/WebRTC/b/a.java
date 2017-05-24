package org.vidogram.VidogramUi.WebRTC.b;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

public class a
{
  private final String a;
  private final String b;
  private final String c;
  private final a d;
  private String e;

  public a(String paramString1, String paramString2, String paramString3, a parama)
  {
    this.a = paramString1;
    this.b = paramString2;
    this.c = paramString3;
    this.d = parama;
  }

  private static String a(InputStream paramInputStream)
  {
    paramInputStream = new Scanner(paramInputStream).useDelimiter("\\A");
    if (paramInputStream.hasNext())
      return paramInputStream.next();
    return "";
  }

  private void b()
  {
    for (int i = 1; ; i = 0)
      try
      {
        localHttpURLConnection = (HttpURLConnection)new URL(this.b).openConnection();
        byte[] arrayOfByte = new byte[0];
        if (this.c != null)
          arrayOfByte = this.c.getBytes("UTF-8");
        localHttpURLConnection.setRequestMethod(this.a);
        localHttpURLConnection.setUseCaches(false);
        localHttpURLConnection.setDoInput(true);
        localHttpURLConnection.setConnectTimeout(8000);
        localHttpURLConnection.setReadTimeout(8000);
        localHttpURLConnection.addRequestProperty("origin", "https://appr.tc");
        if (!this.a.equals("POST"))
          continue;
        localHttpURLConnection.setDoOutput(true);
        localHttpURLConnection.setFixedLengthStreamingMode(arrayOfByte.length);
        if (this.e == null)
          localHttpURLConnection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        while (true)
        {
          if ((i != 0) && (arrayOfByte.length > 0))
          {
            localObject = localHttpURLConnection.getOutputStream();
            ((OutputStream)localObject).write(arrayOfByte);
            ((OutputStream)localObject).close();
          }
          if (localHttpURLConnection.getResponseCode() == 200)
            break;
          this.d.a("Non-200 response to " + this.a + " to URL: " + this.b + " : " + localHttpURLConnection.getHeaderField(null));
          localHttpURLConnection.disconnect();
          return;
          localHttpURLConnection.setRequestProperty("Content-Type", this.e);
        }
      }
      catch (SocketTimeoutException localInputStream)
      {
        HttpURLConnection localHttpURLConnection;
        this.d.a("HTTP " + this.a + " to " + this.b + " timeout");
        return;
        InputStream localInputStream = localHttpURLConnection.getInputStream();
        Object localObject = a(localInputStream);
        localInputStream.close();
        localHttpURLConnection.disconnect();
        this.d.b((String)localObject);
        return;
      }
      catch (IOException localIOException)
      {
        this.d.a("HTTP " + this.a + " to " + this.b + " error: " + localIOException.getMessage());
        return;
      }
  }

  public void a()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        a.a(a.this);
      }
    }).start();
  }

  public static abstract interface a
  {
    public abstract void a(String paramString);

    public abstract void b(String paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.b.a
 * JD-Core Version:    0.6.0
 */