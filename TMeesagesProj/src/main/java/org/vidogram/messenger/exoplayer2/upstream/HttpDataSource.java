package org.vidogram.messenger.exoplayer2.upstream;

import android.text.TextUtils;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import org.vidogram.messenger.exoplayer2.util.Predicate;
import org.vidogram.messenger.exoplayer2.util.Util;

public abstract interface HttpDataSource extends DataSource
{
  public static final Predicate<String> REJECT_PAYWALL_TYPES = new Predicate()
  {
    public boolean evaluate(String paramString)
    {
      paramString = Util.toLowerInvariant(paramString);
      return (!TextUtils.isEmpty(paramString)) && ((!paramString.contains("text")) || (paramString.contains("text/vtt"))) && (!paramString.contains("html")) && (!paramString.contains("xml"));
    }
  };

  public abstract void clearAllRequestProperties();

  public abstract void clearRequestProperty(String paramString);

  public abstract void close();

  public abstract Map<String, List<String>> getResponseHeaders();

  public abstract long open(DataSpec paramDataSpec);

  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract void setRequestProperty(String paramString1, String paramString2);

  public static abstract interface Factory extends DataSource.Factory
  {
    public abstract HttpDataSource createDataSource();
  }

  public static class HttpDataSourceException extends IOException
  {
    public static final int TYPE_CLOSE = 3;
    public static final int TYPE_OPEN = 1;
    public static final int TYPE_READ = 2;
    public final DataSpec dataSpec;
    public final int type;

    public HttpDataSourceException(IOException paramIOException, DataSpec paramDataSpec, int paramInt)
    {
      super();
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }

    public HttpDataSourceException(String paramString, IOException paramIOException, DataSpec paramDataSpec, int paramInt)
    {
      super(paramIOException);
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }

    public HttpDataSourceException(String paramString, DataSpec paramDataSpec, int paramInt)
    {
      super();
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }

    public HttpDataSourceException(DataSpec paramDataSpec, int paramInt)
    {
      this.dataSpec = paramDataSpec;
      this.type = paramInt;
    }

    @Retention(RetentionPolicy.SOURCE)
    public static @interface Type
    {
    }
  }

  public static final class InvalidContentTypeException extends HttpDataSource.HttpDataSourceException
  {
    public final String contentType;

    public InvalidContentTypeException(String paramString, DataSpec paramDataSpec)
    {
      super(paramDataSpec, 1);
      this.contentType = paramString;
    }
  }

  public static final class InvalidResponseCodeException extends HttpDataSource.HttpDataSourceException
  {
    public final Map<String, List<String>> headerFields;
    public final int responseCode;

    public InvalidResponseCodeException(int paramInt, Map<String, List<String>> paramMap, DataSpec paramDataSpec)
    {
      super(paramDataSpec, 1);
      this.responseCode = paramInt;
      this.headerFields = paramMap;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.HttpDataSource
 * JD-Core Version:    0.6.0
 */