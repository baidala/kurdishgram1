package org.vidogram.messenger.exoplayer2.drm;

import B;
import android.annotation.TargetApi;
import android.net.Uri;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.vidogram.messenger.exoplayer2.C;
import org.vidogram.messenger.exoplayer2.upstream.DataSourceInputStream;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.upstream.HttpDataSource;
import org.vidogram.messenger.exoplayer2.upstream.HttpDataSource.Factory;
import org.vidogram.messenger.exoplayer2.util.Util;

@TargetApi(18)
public final class HttpMediaDrmCallback
  implements MediaDrmCallback
{
  private static final Map<String, String> PLAYREADY_KEY_REQUEST_PROPERTIES = new HashMap();
  private final HttpDataSource.Factory dataSourceFactory;
  private final String defaultUrl;
  private final Map<String, String> keyRequestProperties;

  static
  {
    PLAYREADY_KEY_REQUEST_PROPERTIES.put("Content-Type", "text/xml");
    PLAYREADY_KEY_REQUEST_PROPERTIES.put("SOAPAction", "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
  }

  public HttpMediaDrmCallback(String paramString, HttpDataSource.Factory paramFactory)
  {
    this(paramString, paramFactory, null);
  }

  public HttpMediaDrmCallback(String paramString, HttpDataSource.Factory paramFactory, Map<String, String> paramMap)
  {
    this.dataSourceFactory = paramFactory;
    this.defaultUrl = paramString;
    this.keyRequestProperties = paramMap;
  }

  private byte[] executePost(String paramString, byte[] paramArrayOfByte, Map<String, String> paramMap)
  {
    HttpDataSource localHttpDataSource = this.dataSourceFactory.createDataSource();
    if (paramMap != null)
    {
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        localHttpDataSource.setRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
      }
    }
    paramString = new DataSourceInputStream(localHttpDataSource, new DataSpec(Uri.parse(paramString), paramArrayOfByte, 0L, 0L, -1L, null, 1));
    try
    {
      paramArrayOfByte = Util.toByteArray(paramString);
      return paramArrayOfByte;
    }
    finally
    {
      paramString.close();
    }
    throw paramArrayOfByte;
  }

  public byte[] executeKeyRequest(UUID paramUUID, ExoMediaDrm.KeyRequest paramKeyRequest)
  {
    Object localObject2 = paramKeyRequest.getDefaultUrl();
    Object localObject1 = localObject2;
    if (TextUtils.isEmpty((CharSequence)localObject2))
      localObject1 = this.defaultUrl;
    localObject2 = new HashMap();
    ((Map)localObject2).put("Content-Type", "application/octet-stream");
    if (C.PLAYREADY_UUID.equals(paramUUID))
      ((Map)localObject2).putAll(PLAYREADY_KEY_REQUEST_PROPERTIES);
    if (this.keyRequestProperties != null)
      ((Map)localObject2).putAll(this.keyRequestProperties);
    return (B)(B)executePost((String)localObject1, paramKeyRequest.getData(), (Map)localObject2);
  }

  public byte[] executeProvisionRequest(UUID paramUUID, ExoMediaDrm.ProvisionRequest paramProvisionRequest)
  {
    return executePost(paramProvisionRequest.getDefaultUrl() + "&signedRequest=" + new String(paramProvisionRequest.getData()), new byte[0], null);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.HttpMediaDrmCallback
 * JD-Core Version:    0.6.0
 */