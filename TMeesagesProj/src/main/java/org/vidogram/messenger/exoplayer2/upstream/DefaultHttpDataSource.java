package org.vidogram.messenger.exoplayer2.upstream;

import android.net.Uri;
import android.util.Log;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Predicate;
import org.vidogram.messenger.exoplayer2.util.Util;

public class DefaultHttpDataSource
  implements HttpDataSource
{
  private static final Pattern CONTENT_RANGE_HEADER = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
  public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8000;
  public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8000;
  private static final long MAX_BYTES_TO_DRAIN = 2048L;
  private static final int MAX_REDIRECTS = 20;
  private static final String TAG = "DefaultHttpDataSource";
  private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference();
  private final boolean allowCrossProtocolRedirects;
  private long bytesRead;
  private long bytesSkipped;
  private long bytesToRead;
  private long bytesToSkip;
  private final int connectTimeoutMillis;
  private HttpURLConnection connection;
  private final Predicate<String> contentTypePredicate;
  private DataSpec dataSpec;
  private InputStream inputStream;
  private final TransferListener<? super DefaultHttpDataSource> listener;
  private boolean opened;
  private final int readTimeoutMillis;
  private final HashMap<String, String> requestProperties;
  private final String userAgent;

  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate)
  {
    this(paramString, paramPredicate, null);
  }

  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener<? super DefaultHttpDataSource> paramTransferListener)
  {
    this(paramString, paramPredicate, paramTransferListener, 8000, 8000);
  }

  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener<? super DefaultHttpDataSource> paramTransferListener, int paramInt1, int paramInt2)
  {
    this(paramString, paramPredicate, paramTransferListener, paramInt1, paramInt2, false);
  }

  public DefaultHttpDataSource(String paramString, Predicate<String> paramPredicate, TransferListener<? super DefaultHttpDataSource> paramTransferListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.userAgent = Assertions.checkNotEmpty(paramString);
    this.contentTypePredicate = paramPredicate;
    this.listener = paramTransferListener;
    this.requestProperties = new HashMap();
    this.connectTimeoutMillis = paramInt1;
    this.readTimeoutMillis = paramInt2;
    this.allowCrossProtocolRedirects = paramBoolean;
  }

  private void closeConnectionQuietly()
  {
    if (this.connection != null);
    try
    {
      this.connection.disconnect();
      this.connection = null;
      return;
    }
    catch (Exception localException)
    {
      while (true)
        Log.e("DefaultHttpDataSource", "Unexpected error while disconnecting", localException);
    }
  }

  // ERROR //
  private static long getContentLength(HttpURLConnection paramHttpURLConnection)
  {
    // Byte code:
    //   0: ldc2_w 132
    //   3: lstore_3
    //   4: aload_0
    //   5: ldc 135
    //   7: invokevirtual 138	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   10: astore 7
    //   12: lload_3
    //   13: lstore_1
    //   14: aload 7
    //   16: invokestatic 144	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   19: ifne +9 -> 28
    //   22: aload 7
    //   24: invokestatic 150	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   27: lstore_1
    //   28: aload_0
    //   29: ldc 152
    //   31: invokevirtual 138	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   34: astore_0
    //   35: lload_1
    //   36: lstore_3
    //   37: aload_0
    //   38: invokestatic 144	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   41: ifne +60 -> 101
    //   44: getstatic 62	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:CONTENT_RANGE_HEADER	Ljava/util/regex/Pattern;
    //   47: aload_0
    //   48: invokevirtual 156	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
    //   51: astore 8
    //   53: lload_1
    //   54: lstore_3
    //   55: aload 8
    //   57: invokevirtual 162	java/util/regex/Matcher:find	()Z
    //   60: ifeq +41 -> 101
    //   63: aload 8
    //   65: iconst_2
    //   66: invokevirtual 166	java/util/regex/Matcher:group	(I)Ljava/lang/String;
    //   69: invokestatic 150	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   72: lstore_3
    //   73: aload 8
    //   75: iconst_1
    //   76: invokevirtual 166	java/util/regex/Matcher:group	(I)Ljava/lang/String;
    //   79: invokestatic 150	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   82: lstore 5
    //   84: lload_3
    //   85: lload 5
    //   87: lsub
    //   88: lconst_1
    //   89: ladd
    //   90: lstore 5
    //   92: lload_1
    //   93: lconst_0
    //   94: lcmp
    //   95: ifge +46 -> 141
    //   98: lload 5
    //   100: lstore_3
    //   101: lload_3
    //   102: lreturn
    //   103: astore 8
    //   105: ldc 22
    //   107: new 168	java/lang/StringBuilder
    //   110: dup
    //   111: invokespecial 169	java/lang/StringBuilder:<init>	()V
    //   114: ldc 171
    //   116: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: aload 7
    //   121: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: ldc 177
    //   126: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: invokevirtual 181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   132: invokestatic 184	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   135: pop
    //   136: lload_3
    //   137: lstore_1
    //   138: goto -110 -> 28
    //   141: lload_1
    //   142: lstore_3
    //   143: lload_1
    //   144: lload 5
    //   146: lcmp
    //   147: ifeq -46 -> 101
    //   150: ldc 22
    //   152: new 168	java/lang/StringBuilder
    //   155: dup
    //   156: invokespecial 169	java/lang/StringBuilder:<init>	()V
    //   159: ldc 186
    //   161: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: aload 7
    //   166: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: ldc 188
    //   171: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: aload_0
    //   175: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: ldc 177
    //   180: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: invokevirtual 181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   186: invokestatic 191	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   189: pop
    //   190: lload_1
    //   191: lload 5
    //   193: invokestatic 197	java/lang/Math:max	(JJ)J
    //   196: lstore_3
    //   197: lload_3
    //   198: lreturn
    //   199: astore 7
    //   201: ldc 22
    //   203: new 168	java/lang/StringBuilder
    //   206: dup
    //   207: invokespecial 169	java/lang/StringBuilder:<init>	()V
    //   210: ldc 199
    //   212: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: aload_0
    //   216: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   219: ldc 177
    //   221: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   224: invokevirtual 181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   227: invokestatic 184	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   230: pop
    //   231: lload_1
    //   232: lreturn
    //
    // Exception table:
    //   from	to	target	type
    //   22	28	103	java/lang/NumberFormatException
    //   63	84	199	java/lang/NumberFormatException
    //   150	197	199	java/lang/NumberFormatException
  }

  private static URL handleRedirect(URL paramURL, String paramString)
  {
    if (paramString == null)
      throw new ProtocolException("Null location redirect");
    paramURL = new URL(paramURL, paramString);
    paramString = paramURL.getProtocol();
    if ((!"https".equals(paramString)) && (!"http".equals(paramString)))
      throw new ProtocolException("Unsupported protocol redirect: " + paramString);
    return paramURL;
  }

  private HttpURLConnection makeConnection(URL arg1, byte[] paramArrayOfByte, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2)
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)???.openConnection();
    localHttpURLConnection.setConnectTimeout(this.connectTimeoutMillis);
    localHttpURLConnection.setReadTimeout(this.readTimeoutMillis);
    Object localObject;
    synchronized (this.requestProperties)
    {
      localObject = this.requestProperties.entrySet().iterator();
      if (((Iterator)localObject).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
        localHttpURLConnection.setRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
      }
    }
    monitorexit;
    if ((paramLong1 != 0L) || (paramLong2 != -1L))
    {
      localObject = "bytes=" + paramLong1 + "-";
      ??? = (URL)localObject;
      if (paramLong2 != -1L)
        ??? = (String)localObject + (paramLong1 + paramLong2 - 1L);
      localHttpURLConnection.setRequestProperty("Range", ???);
    }
    localHttpURLConnection.setRequestProperty("User-Agent", this.userAgent);
    if (!paramBoolean1)
      localHttpURLConnection.setRequestProperty("Accept-Encoding", "identity");
    localHttpURLConnection.setInstanceFollowRedirects(paramBoolean2);
    if (paramArrayOfByte != null)
      paramBoolean1 = true;
    while (true)
    {
      localHttpURLConnection.setDoOutput(paramBoolean1);
      if (paramArrayOfByte == null)
        break;
      localHttpURLConnection.setRequestMethod("POST");
      if (paramArrayOfByte.length == 0)
      {
        localHttpURLConnection.connect();
        return localHttpURLConnection;
        paramBoolean1 = false;
        continue;
      }
      localHttpURLConnection.setFixedLengthStreamingMode(paramArrayOfByte.length);
      localHttpURLConnection.connect();
      ??? = localHttpURLConnection.getOutputStream();
      ???.write(paramArrayOfByte);
      ???.close();
      return localHttpURLConnection;
    }
    localHttpURLConnection.connect();
    return (HttpURLConnection)localHttpURLConnection;
  }

  private HttpURLConnection makeConnection(DataSpec paramDataSpec)
  {
    Object localObject = new URL(paramDataSpec.uri.toString());
    byte[] arrayOfByte = paramDataSpec.postBody;
    long l1 = paramDataSpec.position;
    long l2 = paramDataSpec.length;
    if ((paramDataSpec.flags & 0x1) != 0);
    for (boolean bool = true; !this.allowCrossProtocolRedirects; bool = false)
      return makeConnection((URL)localObject, arrayOfByte, l1, l2, bool, true);
    int i = 0;
    paramDataSpec = (DataSpec)localObject;
    int j;
    while (true)
    {
      j = i + 1;
      if (i > 20)
        break;
      localObject = makeConnection(paramDataSpec, arrayOfByte, l1, l2, bool, false);
      i = ((HttpURLConnection)localObject).getResponseCode();
      if ((i == 300) || (i == 301) || (i == 302) || (i == 303) || ((arrayOfByte == null) && ((i == 307) || (i == 308))))
      {
        arrayOfByte = null;
        String str = ((HttpURLConnection)localObject).getHeaderField("Location");
        ((HttpURLConnection)localObject).disconnect();
        paramDataSpec = handleRedirect(paramDataSpec, str);
        i = j;
        continue;
      }
      return localObject;
    }
    throw new NoRouteToHostException("Too many redirects: " + j);
  }

  private static void maybeTerminateInputStream(HttpURLConnection paramHttpURLConnection, long paramLong)
  {
    if ((Util.SDK_INT != 19) && (Util.SDK_INT != 20))
      return;
    do
      try
      {
        paramHttpURLConnection = paramHttpURLConnection.getInputStream();
        if (paramLong != -1L)
          continue;
        if (paramHttpURLConnection.read() == -1)
          break;
        Object localObject = paramHttpURLConnection.getClass().getName();
        if ((!((String)localObject).equals("com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream")) && (!((String)localObject).equals("com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream")))
          break;
        localObject = paramHttpURLConnection.getClass().getSuperclass().getDeclaredMethod("unexpectedEndOfInput", new Class[0]);
        ((Method)localObject).setAccessible(true);
        ((Method)localObject).invoke(paramHttpURLConnection, new Object[0]);
        return;
      }
      catch (Exception paramHttpURLConnection)
      {
        return;
      }
    while (paramLong > 2048L);
  }

  private int readInternal(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int j = -1;
    int i;
    if (paramInt2 == 0)
      i = 0;
    while (true)
    {
      return i;
      i = paramInt2;
      if (this.bytesToRead != -1L)
      {
        long l = this.bytesToRead - this.bytesRead;
        i = j;
        if (l == 0L)
          continue;
        i = (int)Math.min(paramInt2, l);
      }
      paramInt1 = this.inputStream.read(paramArrayOfByte, paramInt1, i);
      if (paramInt1 != -1)
        break;
      i = j;
      if (this.bytesToRead == -1L)
        continue;
      throw new EOFException();
    }
    this.bytesRead += paramInt1;
    if (this.listener != null)
      this.listener.onBytesTransferred(this, paramInt1);
    return paramInt1;
  }

  private void skipInternal()
  {
    if (this.bytesSkipped == this.bytesToSkip)
      return;
    byte[] arrayOfByte2 = (byte[])skipBufferReference.getAndSet(null);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2 == null)
      arrayOfByte1 = new byte[4096];
    while (this.bytesSkipped != this.bytesToSkip)
    {
      int i = (int)Math.min(this.bytesToSkip - this.bytesSkipped, arrayOfByte1.length);
      i = this.inputStream.read(arrayOfByte1, 0, i);
      if (Thread.interrupted())
        throw new InterruptedIOException();
      if (i == -1)
        throw new EOFException();
      this.bytesSkipped += i;
      if (this.listener == null)
        continue;
      this.listener.onBytesTransferred(this, i);
    }
    skipBufferReference.set(arrayOfByte1);
  }

  protected final long bytesRead()
  {
    return this.bytesRead;
  }

  protected final long bytesRemaining()
  {
    if (this.bytesToRead == -1L)
      return this.bytesToRead;
    return this.bytesToRead - this.bytesRead;
  }

  protected final long bytesSkipped()
  {
    return this.bytesSkipped;
  }

  public void clearAllRequestProperties()
  {
    synchronized (this.requestProperties)
    {
      this.requestProperties.clear();
      return;
    }
  }

  public void clearRequestProperty(String paramString)
  {
    Assertions.checkNotNull(paramString);
    synchronized (this.requestProperties)
    {
      this.requestProperties.remove(paramString);
      return;
    }
  }

  public void close()
  {
    try
    {
      if (this.inputStream != null)
        maybeTerminateInputStream(this.connection, bytesRemaining());
      try
      {
        this.inputStream.close();
        this.inputStream = null;
        closeConnectionQuietly();
        if (this.opened)
        {
          this.opened = false;
          if (this.listener != null)
            this.listener.onTransferEnd(this);
        }
        return;
      }
      catch (IOException localIOException)
      {
        throw new HttpDataSource.HttpDataSourceException(localIOException, this.dataSpec, 3);
      }
    }
    finally
    {
      this.inputStream = null;
      closeConnectionQuietly();
      if (this.opened)
      {
        this.opened = false;
        if (this.listener != null)
          this.listener.onTransferEnd(this);
      }
    }
    throw localObject;
  }

  protected final HttpURLConnection getConnection()
  {
    return this.connection;
  }

  public Map<String, List<String>> getResponseHeaders()
  {
    if (this.connection == null)
      return null;
    return this.connection.getHeaderFields();
  }

  public Uri getUri()
  {
    if (this.connection == null)
      return null;
    return Uri.parse(this.connection.getURL().toString());
  }

  // ERROR //
  public long open(DataSpec paramDataSpec)
  {
    // Byte code:
    //   0: lconst_0
    //   1: lstore 5
    //   3: aload_0
    //   4: aload_1
    //   5: putfield 482	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:dataSpec	Lorg/vidogram/messenger/exoplayer2/upstream/DataSpec;
    //   8: aload_0
    //   9: lconst_0
    //   10: putfield 413	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesRead	J
    //   13: aload_0
    //   14: lconst_0
    //   15: putfield 432	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesSkipped	J
    //   18: aload_0
    //   19: aload_0
    //   20: aload_1
    //   21: invokespecial 508	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:makeConnection	(Lorg/vidogram/messenger/exoplayer2/upstream/DataSpec;)Ljava/net/HttpURLConnection;
    //   24: putfield 114	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   27: aload_0
    //   28: getfield 114	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   31: invokevirtual 348	java/net/HttpURLConnection:getResponseCode	()I
    //   34: istore_2
    //   35: iload_2
    //   36: sipush 200
    //   39: if_icmplt +10 -> 49
    //   42: iload_2
    //   43: sipush 299
    //   46: if_icmple +134 -> 180
    //   49: aload_0
    //   50: getfield 114	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   53: invokevirtual 492	java/net/HttpURLConnection:getHeaderFields	()Ljava/util/Map;
    //   56: astore 7
    //   58: aload_0
    //   59: invokespecial 473	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   62: new 510	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$InvalidResponseCodeException
    //   65: dup
    //   66: iload_2
    //   67: aload 7
    //   69: aload_1
    //   70: invokespecial 513	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$InvalidResponseCodeException:<init>	(ILjava/util/Map;Lorg/vidogram/messenger/exoplayer2/upstream/DataSpec;)V
    //   73: astore_1
    //   74: iload_2
    //   75: sipush 416
    //   78: if_icmpne +16 -> 94
    //   81: aload_1
    //   82: new 515	org/vidogram/messenger/exoplayer2/upstream/DataSourceException
    //   85: dup
    //   86: iconst_0
    //   87: invokespecial 517	org/vidogram/messenger/exoplayer2/upstream/DataSourceException:<init>	(I)V
    //   90: invokevirtual 521	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$InvalidResponseCodeException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   93: pop
    //   94: aload_1
    //   95: athrow
    //   96: astore 7
    //   98: new 480	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException
    //   101: dup
    //   102: new 168	java/lang/StringBuilder
    //   105: dup
    //   106: invokespecial 169	java/lang/StringBuilder:<init>	()V
    //   109: ldc_w 523
    //   112: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: aload_1
    //   116: getfield 325	org/vidogram/messenger/exoplayer2/upstream/DataSpec:uri	Landroid/net/Uri;
    //   119: invokevirtual 328	android/net/Uri:toString	()Ljava/lang/String;
    //   122: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: invokevirtual 181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   128: aload 7
    //   130: aload_1
    //   131: iconst_1
    //   132: invokespecial 526	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/lang/String;Ljava/io/IOException;Lorg/vidogram/messenger/exoplayer2/upstream/DataSpec;I)V
    //   135: athrow
    //   136: astore 7
    //   138: aload_0
    //   139: invokespecial 473	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   142: new 480	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException
    //   145: dup
    //   146: new 168	java/lang/StringBuilder
    //   149: dup
    //   150: invokespecial 169	java/lang/StringBuilder:<init>	()V
    //   153: ldc_w 523
    //   156: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: aload_1
    //   160: getfield 325	org/vidogram/messenger/exoplayer2/upstream/DataSpec:uri	Landroid/net/Uri;
    //   163: invokevirtual 328	android/net/Uri:toString	()Ljava/lang/String;
    //   166: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: invokevirtual 181	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   172: aload 7
    //   174: aload_1
    //   175: iconst_1
    //   176: invokespecial 526	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/lang/String;Ljava/io/IOException;Lorg/vidogram/messenger/exoplayer2/upstream/DataSpec;I)V
    //   179: athrow
    //   180: aload_0
    //   181: getfield 114	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   184: invokevirtual 529	java/net/HttpURLConnection:getContentType	()Ljava/lang/String;
    //   187: astore 7
    //   189: aload_0
    //   190: getfield 95	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:contentTypePredicate	Lorg/vidogram/messenger/exoplayer2/util/Predicate;
    //   193: ifnull +32 -> 225
    //   196: aload_0
    //   197: getfield 95	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:contentTypePredicate	Lorg/vidogram/messenger/exoplayer2/util/Predicate;
    //   200: aload 7
    //   202: invokeinterface 534 2 0
    //   207: ifne +18 -> 225
    //   210: aload_0
    //   211: invokespecial 473	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   214: new 536	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$InvalidContentTypeException
    //   217: dup
    //   218: aload 7
    //   220: aload_1
    //   221: invokespecial 539	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$InvalidContentTypeException:<init>	(Ljava/lang/String;Lorg/vidogram/messenger/exoplayer2/upstream/DataSpec;)V
    //   224: athrow
    //   225: lload 5
    //   227: lstore_3
    //   228: iload_2
    //   229: sipush 200
    //   232: if_icmpne +20 -> 252
    //   235: lload 5
    //   237: lstore_3
    //   238: aload_1
    //   239: getfield 336	org/vidogram/messenger/exoplayer2/upstream/DataSpec:position	J
    //   242: lconst_0
    //   243: lcmp
    //   244: ifeq +8 -> 252
    //   247: aload_1
    //   248: getfield 336	org/vidogram/messenger/exoplayer2/upstream/DataSpec:position	J
    //   251: lstore_3
    //   252: aload_0
    //   253: lload_3
    //   254: putfield 434	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesToSkip	J
    //   257: aload_1
    //   258: getfield 342	org/vidogram/messenger/exoplayer2/upstream/DataSpec:flags	I
    //   261: iconst_1
    //   262: iand
    //   263: ifne +99 -> 362
    //   266: aload_1
    //   267: getfield 339	org/vidogram/messenger/exoplayer2/upstream/DataSpec:length	J
    //   270: ldc2_w 132
    //   273: lcmp
    //   274: ifeq +50 -> 324
    //   277: aload_0
    //   278: aload_1
    //   279: getfield 339	org/vidogram/messenger/exoplayer2/upstream/DataSpec:length	J
    //   282: putfield 411	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   285: aload_0
    //   286: aload_0
    //   287: getfield 114	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   290: invokevirtual 371	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   293: putfield 418	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:inputStream	Ljava/io/InputStream;
    //   296: aload_0
    //   297: iconst_1
    //   298: putfield 475	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:opened	Z
    //   301: aload_0
    //   302: getfield 97	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:listener	Lorg/vidogram/messenger/exoplayer2/upstream/TransferListener;
    //   305: ifnull +14 -> 319
    //   308: aload_0
    //   309: getfield 97	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:listener	Lorg/vidogram/messenger/exoplayer2/upstream/TransferListener;
    //   312: aload_0
    //   313: aload_1
    //   314: invokeinterface 543 3 0
    //   319: aload_0
    //   320: getfield 411	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   323: lreturn
    //   324: aload_0
    //   325: getfield 114	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:connection	Ljava/net/HttpURLConnection;
    //   328: invokestatic 545	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:getContentLength	(Ljava/net/HttpURLConnection;)J
    //   331: lstore_3
    //   332: lload_3
    //   333: ldc2_w 132
    //   336: lcmp
    //   337: ifeq +18 -> 355
    //   340: lload_3
    //   341: aload_0
    //   342: getfield 434	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesToSkip	J
    //   345: lsub
    //   346: lstore_3
    //   347: aload_0
    //   348: lload_3
    //   349: putfield 411	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   352: goto -67 -> 285
    //   355: ldc2_w 132
    //   358: lstore_3
    //   359: goto -12 -> 347
    //   362: aload_0
    //   363: aload_1
    //   364: getfield 339	org/vidogram/messenger/exoplayer2/upstream/DataSpec:length	J
    //   367: putfield 411	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:bytesToRead	J
    //   370: goto -85 -> 285
    //   373: astore 7
    //   375: aload_0
    //   376: invokespecial 473	org/vidogram/messenger/exoplayer2/upstream/DefaultHttpDataSource:closeConnectionQuietly	()V
    //   379: new 480	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException
    //   382: dup
    //   383: aload 7
    //   385: aload_1
    //   386: iconst_1
    //   387: invokespecial 485	org/vidogram/messenger/exoplayer2/upstream/HttpDataSource$HttpDataSourceException:<init>	(Ljava/io/IOException;Lorg/vidogram/messenger/exoplayer2/upstream/DataSpec;I)V
    //   390: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   18	27	96	java/io/IOException
    //   27	35	136	java/io/IOException
    //   285	296	373	java/io/IOException
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      skipInternal();
      paramInt1 = readInternal(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
    }
    throw new HttpDataSource.HttpDataSourceException(paramArrayOfByte, this.dataSpec, 2);
  }

  public void setRequestProperty(String paramString1, String paramString2)
  {
    Assertions.checkNotNull(paramString1);
    Assertions.checkNotNull(paramString2);
    synchronized (this.requestProperties)
    {
      this.requestProperties.put(paramString1, paramString2);
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DefaultHttpDataSource
 * JD-Core Version:    0.6.0
 */