package org.vidogram.messenger;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputFileLocation;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_documentEncrypted;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.vidogram.tgnet.TLRPC.TL_fileLocation;
import org.vidogram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.vidogram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.vidogram.tgnet.TLRPC.TL_inputFileLocation;
import org.vidogram.tgnet.TLRPC.TL_inputWebFileLocation;
import org.vidogram.tgnet.TLRPC.TL_upload_file;
import org.vidogram.tgnet.TLRPC.TL_upload_getFile;
import org.vidogram.tgnet.TLRPC.TL_upload_getWebFile;
import org.vidogram.tgnet.TLRPC.TL_upload_webFile;
import org.vidogram.tgnet.TLRPC.TL_webDocument;

public class FileLoadOperation
{
  private static final int bigFileSizeFrom = 1048576;
  private static final int downloadChunkSize = 32768;
  private static final int downloadChunkSizeBig = 131072;
  private static final int maxDownloadRequests = 4;
  private static final int maxDownloadRequestsBig = 2;
  private static final int stateDownloading = 1;
  private static final int stateFailed = 2;
  private static final int stateFinished = 3;
  private static final int stateIdle = 0;
  private int bytesCountPadding;
  private File cacheFileFinal;
  private File cacheFileTemp;
  private File cacheIvTemp;
  private int currentDownloadChunkSize;
  private int currentMaxDownloadRequests;
  private int currentType;
  private int datacenter_id;
  private ArrayList<RequestInfo> delayedRequestInfos;
  private FileLoadOperationDelegate delegate;
  private int downloadedBytes;
  private String ext;
  private RandomAccessFile fileOutputStream;
  private RandomAccessFile fiv;
  private boolean isForceRequest;
  private byte[] iv;
  private byte[] key;
  private TLRPC.InputFileLocation location;
  private int nextDownloadOffset;
  private int renameRetryCount;
  private ArrayList<RequestInfo> requestInfos;
  private int requestsCount;
  private boolean started;
  private volatile int state = 0;
  private File storePath;
  private File tempPath;
  private int totalBytesCount;
  private TLRPC.TL_inputWebFileLocation webLocation;

  public FileLoadOperation(TLRPC.Document paramDocument)
  {
    while (true)
    {
      int j;
      try
      {
        if (!(paramDocument instanceof TLRPC.TL_documentEncrypted))
          continue;
        this.location = new TLRPC.TL_inputEncryptedFileLocation();
        this.location.id = paramDocument.id;
        this.location.access_hash = paramDocument.access_hash;
        this.datacenter_id = paramDocument.dc_id;
        this.iv = new byte[32];
        System.arraycopy(paramDocument.iv, 0, this.iv, 0, this.iv.length);
        this.key = paramDocument.key;
        this.totalBytesCount = paramDocument.size;
        if ((this.key == null) || (this.totalBytesCount % 16 == 0))
          continue;
        this.bytesCountPadding = (16 - this.totalBytesCount % 16);
        this.totalBytesCount += this.bytesCountPadding;
        this.ext = FileLoader.getDocumentFileName(paramDocument);
        if (this.ext == null)
          continue;
        j = this.ext.lastIndexOf('.');
        if (j == -1)
        {
          this.ext = "";
          if (!"audio/ogg".equals(paramDocument.mime_type))
            break label337;
          this.currentType = 50331648;
          if (this.ext.length() > 1)
            break;
          if (paramDocument.mime_type == null)
            break label409;
          paramDocument = paramDocument.mime_type;
          switch (paramDocument.hashCode())
          {
          case 1331848029:
            this.ext = "";
            return;
            if (!(paramDocument instanceof TLRPC.TL_document))
              continue;
            this.location = new TLRPC.TL_inputDocumentFileLocation();
            this.location.id = paramDocument.id;
            this.location.access_hash = paramDocument.access_hash;
            this.datacenter_id = paramDocument.dc_id;
            continue;
          case 187091926:
          }
        }
      }
      catch (Exception paramDocument)
      {
        FileLog.e(paramDocument);
        onFail(true, 0);
        return;
      }
      this.ext = this.ext.substring(j);
      continue;
      label337: if ("video/mp4".equals(paramDocument.mime_type))
      {
        this.currentType = 33554432;
        continue;
      }
      this.currentType = 67108864;
      continue;
      if (paramDocument.equals("video/mp4"))
      {
        i = 0;
        break label416;
        if (paramDocument.equals("audio/ogg"))
        {
          i = 1;
          break label416;
          this.ext = ".mp4";
          return;
          this.ext = ".ogg";
          return;
          label409: this.ext = "";
          return;
        }
      }
      label416: switch (i)
      {
      case 0:
      case 1:
      }
    }
  }

  public FileLoadOperation(TLRPC.FileLocation paramFileLocation, String paramString, int paramInt)
  {
    if ((paramFileLocation instanceof TLRPC.TL_fileEncryptedLocation))
    {
      this.location = new TLRPC.TL_inputEncryptedFileLocation();
      this.location.id = paramFileLocation.volume_id;
      this.location.volume_id = paramFileLocation.volume_id;
      this.location.access_hash = paramFileLocation.secret;
      this.location.local_id = paramFileLocation.local_id;
      this.iv = new byte[32];
      System.arraycopy(paramFileLocation.iv, 0, this.iv, 0, this.iv.length);
      this.key = paramFileLocation.key;
      this.datacenter_id = paramFileLocation.dc_id;
      this.currentType = 16777216;
      this.totalBytesCount = paramInt;
      if (paramString == null)
        break label196;
    }
    while (true)
    {
      this.ext = paramString;
      return;
      if (!(paramFileLocation instanceof TLRPC.TL_fileLocation))
        break;
      this.location = new TLRPC.TL_inputFileLocation();
      this.location.volume_id = paramFileLocation.volume_id;
      this.location.secret = paramFileLocation.secret;
      this.location.local_id = paramFileLocation.local_id;
      this.datacenter_id = paramFileLocation.dc_id;
      break;
      label196: paramString = "jpg";
    }
  }

  public FileLoadOperation(TLRPC.TL_webDocument paramTL_webDocument)
  {
    this.webLocation = new TLRPC.TL_inputWebFileLocation();
    this.webLocation.url = paramTL_webDocument.url;
    this.webLocation.access_hash = paramTL_webDocument.access_hash;
    this.totalBytesCount = paramTL_webDocument.size;
    this.datacenter_id = paramTL_webDocument.dc_id;
    String str = FileLoader.getExtensionByMime(paramTL_webDocument.mime_type);
    if (paramTL_webDocument.mime_type.startsWith("image/"))
      this.currentType = 16777216;
    while (true)
    {
      this.ext = ImageLoader.getHttpUrlExtension(paramTL_webDocument.url, str);
      return;
      if (paramTL_webDocument.mime_type.equals("audio/ogg"))
      {
        this.currentType = 50331648;
        continue;
      }
      if (paramTL_webDocument.mime_type.startsWith("video/"))
      {
        this.currentType = 33554432;
        continue;
      }
      this.currentType = 67108864;
    }
  }

  // ERROR //
  private void cleanup()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 292	org/vidogram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   4: astore_2
    //   5: aload_2
    //   6: ifnull +25 -> 31
    //   9: aload_0
    //   10: getfield 292	org/vidogram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   13: invokevirtual 298	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   16: invokevirtual 303	java/nio/channels/FileChannel:close	()V
    //   19: aload_0
    //   20: getfield 292	org/vidogram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   23: invokevirtual 304	java/io/RandomAccessFile:close	()V
    //   26: aload_0
    //   27: aconst_null
    //   28: putfield 292	org/vidogram/messenger/FileLoadOperation:fileOutputStream	Ljava/io/RandomAccessFile;
    //   31: aload_0
    //   32: getfield 306	org/vidogram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   35: ifnull +15 -> 50
    //   38: aload_0
    //   39: getfield 306	org/vidogram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   42: invokevirtual 304	java/io/RandomAccessFile:close	()V
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 306	org/vidogram/messenger/FileLoadOperation:fiv	Ljava/io/RandomAccessFile;
    //   50: aload_0
    //   51: getfield 308	org/vidogram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   54: ifnull +113 -> 167
    //   57: iconst_0
    //   58: istore_1
    //   59: iload_1
    //   60: aload_0
    //   61: getfield 308	org/vidogram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   64: invokevirtual 312	java/util/ArrayList:size	()I
    //   67: if_icmpge +93 -> 160
    //   70: aload_0
    //   71: getfield 308	org/vidogram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   74: iload_1
    //   75: invokevirtual 316	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   78: checkcast 19	org/vidogram/messenger/FileLoadOperation$RequestInfo
    //   81: astore_2
    //   82: aload_2
    //   83: invokestatic 320	org/vidogram/messenger/FileLoadOperation$RequestInfo:access$800	(Lorg/vidogram/messenger/FileLoadOperation$RequestInfo;)Lorg/vidogram/tgnet/TLRPC$TL_upload_file;
    //   86: ifnull +49 -> 135
    //   89: aload_2
    //   90: invokestatic 320	org/vidogram/messenger/FileLoadOperation$RequestInfo:access$800	(Lorg/vidogram/messenger/FileLoadOperation$RequestInfo;)Lorg/vidogram/tgnet/TLRPC$TL_upload_file;
    //   93: iconst_0
    //   94: putfield 325	org/vidogram/tgnet/TLRPC$TL_upload_file:disableFree	Z
    //   97: aload_2
    //   98: invokestatic 320	org/vidogram/messenger/FileLoadOperation$RequestInfo:access$800	(Lorg/vidogram/messenger/FileLoadOperation$RequestInfo;)Lorg/vidogram/tgnet/TLRPC$TL_upload_file;
    //   101: invokevirtual 328	org/vidogram/tgnet/TLRPC$TL_upload_file:freeResources	()V
    //   104: iload_1
    //   105: iconst_1
    //   106: iadd
    //   107: istore_1
    //   108: goto -49 -> 59
    //   111: astore_2
    //   112: aload_2
    //   113: invokestatic 175	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   116: goto -97 -> 19
    //   119: astore_2
    //   120: aload_2
    //   121: invokestatic 175	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   124: goto -93 -> 31
    //   127: astore_2
    //   128: aload_2
    //   129: invokestatic 175	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   132: goto -82 -> 50
    //   135: aload_2
    //   136: invokestatic 332	org/vidogram/messenger/FileLoadOperation$RequestInfo:access$900	(Lorg/vidogram/messenger/FileLoadOperation$RequestInfo;)Lorg/vidogram/tgnet/TLRPC$TL_upload_webFile;
    //   139: ifnull -35 -> 104
    //   142: aload_2
    //   143: invokestatic 332	org/vidogram/messenger/FileLoadOperation$RequestInfo:access$900	(Lorg/vidogram/messenger/FileLoadOperation$RequestInfo;)Lorg/vidogram/tgnet/TLRPC$TL_upload_webFile;
    //   146: iconst_0
    //   147: putfield 335	org/vidogram/tgnet/TLRPC$TL_upload_webFile:disableFree	Z
    //   150: aload_2
    //   151: invokestatic 332	org/vidogram/messenger/FileLoadOperation$RequestInfo:access$900	(Lorg/vidogram/messenger/FileLoadOperation$RequestInfo;)Lorg/vidogram/tgnet/TLRPC$TL_upload_webFile;
    //   154: invokevirtual 336	org/vidogram/tgnet/TLRPC$TL_upload_webFile:freeResources	()V
    //   157: goto -53 -> 104
    //   160: aload_0
    //   161: getfield 308	org/vidogram/messenger/FileLoadOperation:delayedRequestInfos	Ljava/util/ArrayList;
    //   164: invokevirtual 339	java/util/ArrayList:clear	()V
    //   167: return
    //
    // Exception table:
    //   from	to	target	type
    //   9	19	111	java/lang/Exception
    //   0	5	119	java/lang/Exception
    //   19	31	119	java/lang/Exception
    //   112	116	119	java/lang/Exception
    //   31	50	127	java/lang/Exception
  }

  private void onFail(boolean paramBoolean, int paramInt)
  {
    cleanup();
    this.state = 2;
    if (paramBoolean)
    {
      Utilities.stageQueue.postRunnable(new Runnable(paramInt)
      {
        public void run()
        {
          FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, this.val$reason);
        }
      });
      return;
    }
    this.delegate.didFailedLoadingFile(this, paramInt);
  }

  private void onFinishLoadingFile(boolean paramBoolean)
  {
    if (this.state != 1);
    do
    {
      do
      {
        return;
        this.state = 3;
        cleanup();
        if (this.cacheIvTemp != null)
        {
          this.cacheIvTemp.delete();
          this.cacheIvTemp = null;
        }
        if ((this.cacheFileTemp != null) && (!this.cacheFileTemp.renameTo(this.cacheFileFinal)))
        {
          if (BuildVars.DEBUG_VERSION)
            FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
          this.renameRetryCount += 1;
          if (this.renameRetryCount < 3)
          {
            this.state = 1;
            Utilities.stageQueue.postRunnable(new Runnable(paramBoolean)
            {
              public void run()
              {
                try
                {
                  FileLoadOperation.this.onFinishLoadingFile(this.val$increment);
                  return;
                }
                catch (Exception localException)
                {
                  FileLoadOperation.this.onFail(false, 0);
                }
              }
            }
            , 200L);
            return;
          }
          this.cacheFileFinal = this.cacheFileTemp;
        }
        if (BuildVars.DEBUG_VERSION)
          FileLog.e("finished downloading file to " + this.cacheFileFinal);
        this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
      }
      while (!paramBoolean);
      if (this.currentType == 50331648)
      {
        StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
        return;
      }
      if (this.currentType == 33554432)
      {
        StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
        return;
      }
      if (this.currentType != 16777216)
        continue;
      StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
      return;
    }
    while (this.currentType != 67108864);
    StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
  }

  private void processRequestResult(RequestInfo paramRequestInfo, TLRPC.TL_error paramTL_error)
  {
    Object localObject = null;
    this.requestInfos.remove(paramRequestInfo);
    int i;
    if (paramTL_error == null)
    {
      try
      {
        if (this.downloadedBytes != paramRequestInfo.offset)
        {
          if (this.state != 1)
            break label784;
          this.delayedRequestInfos.add(paramRequestInfo);
          if (paramRequestInfo.response != null)
          {
            paramRequestInfo.response.disableFree = true;
            return;
          }
          paramRequestInfo.responseWeb.disableFree = true;
          return;
        }
      }
      catch (Exception paramRequestInfo)
      {
        onFail(false, 0);
        FileLog.e(paramRequestInfo);
        return;
      }
      if (paramRequestInfo.response != null);
      for (paramRequestInfo = paramRequestInfo.response.bytes; (paramRequestInfo == null) || (paramRequestInfo.limit() == 0); paramRequestInfo = paramRequestInfo.responseWeb.bytes)
      {
        onFinishLoadingFile(true);
        return;
      }
      i = paramRequestInfo.limit();
      this.downloadedBytes += i;
      if (i != this.currentDownloadChunkSize)
        break label785;
      if ((this.totalBytesCount != this.downloadedBytes) && (this.downloadedBytes % this.currentDownloadChunkSize == 0))
        break label796;
      if (this.totalBytesCount <= 0)
        break label785;
      if (this.totalBytesCount > this.downloadedBytes)
        break label796;
      break label785;
      if (this.key != null)
      {
        Utilities.aesIgeEncryption(paramRequestInfo.buffer, this.key, this.iv, false, true, 0, paramRequestInfo.limit());
        if ((i != 0) && (this.bytesCountPadding != 0))
          paramRequestInfo.limit(paramRequestInfo.limit() - this.bytesCountPadding);
      }
      if (this.fileOutputStream != null)
        this.fileOutputStream.getChannel().write(paramRequestInfo.buffer);
      if (this.fiv != null)
      {
        this.fiv.seek(0L);
        this.fiv.write(this.iv);
      }
      if ((this.totalBytesCount <= 0) || (this.state != 1))
        break label790;
      this.delegate.didChangedLoadProgress(this, Math.min(1.0F, this.downloadedBytes / this.totalBytesCount));
      break label790;
    }
    while (true)
    {
      if (j < this.delayedRequestInfos.size())
      {
        paramRequestInfo = (RequestInfo)this.delayedRequestInfos.get(j);
        if (this.downloadedBytes != paramRequestInfo.offset)
          break label801;
        this.delayedRequestInfos.remove(j);
        processRequestResult(paramRequestInfo, null);
        if (paramRequestInfo.response == null)
          break label419;
        paramRequestInfo.response.disableFree = false;
        paramRequestInfo.response.freeResources();
      }
      while (i != 0)
      {
        onFinishLoadingFile(true);
        return;
        label419: paramRequestInfo.responseWeb.disableFree = false;
        paramRequestInfo.responseWeb.freeResources();
      }
      startDownloadRequest();
      return;
      if (paramTL_error.text.contains("FILE_MIGRATE_"))
      {
        paramRequestInfo = new Scanner(paramTL_error.text.replace("FILE_MIGRATE_", ""));
        paramRequestInfo.useDelimiter("");
      }
      try
      {
        i = paramRequestInfo.nextInt();
        paramRequestInfo = Integer.valueOf(i);
        if (paramRequestInfo == null)
        {
          onFail(false, 0);
          return;
        }
        this.datacenter_id = paramRequestInfo.intValue();
        this.nextDownloadOffset = 0;
        startDownloadRequest();
        return;
        if (paramTL_error.text.contains("OFFSET_INVALID"))
        {
          if (this.downloadedBytes % this.currentDownloadChunkSize == 0)
            try
            {
              onFinishLoadingFile(true);
              return;
            }
            catch (Exception paramRequestInfo)
            {
              FileLog.e(paramRequestInfo);
              onFail(false, 0);
              return;
            }
          onFail(false, 0);
          return;
        }
        if (paramTL_error.text.contains("RETRY_LIMIT"))
        {
          onFail(false, 2);
          return;
        }
        if (this.location != null)
          FileLog.e("" + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
        while (true)
        {
          onFail(false, 0);
          return;
          if (this.webLocation == null)
            continue;
          FileLog.e("" + this.webLocation + " id = " + this.webLocation.url + " access_hash = " + this.webLocation.access_hash);
        }
      }
      catch (Exception paramRequestInfo)
      {
        while (true)
          paramRequestInfo = localObject;
      }
      label784: return;
      label785: i = 1;
      break;
      label790: int j = 0;
      continue;
      label796: i = 0;
      break;
      label801: j += 1;
    }
  }

  private void startDownloadRequest()
  {
    if ((this.state != 1) || ((this.totalBytesCount > 0) && (this.nextDownloadOffset >= this.totalBytesCount)) || (this.requestInfos.size() + this.delayedRequestInfos.size() >= this.currentMaxDownloadRequests))
      return;
    if (this.totalBytesCount > 0);
    for (int j = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size()); ; j = 1)
    {
      int k = 0;
      label75: boolean bool;
      label138: Object localObject;
      int i;
      label196: RequestInfo localRequestInfo;
      ConnectionsManager localConnectionsManager;
      5 local5;
      if ((k < j) && ((this.totalBytesCount <= 0) || (this.nextDownloadOffset < this.totalBytesCount)))
      {
        if ((this.totalBytesCount > 0) && (k != j - 1) && ((this.totalBytesCount <= 0) || (this.nextDownloadOffset + this.currentDownloadChunkSize < this.totalBytesCount)))
          break label309;
        bool = true;
        if (this.webLocation == null)
          break label322;
        localObject = new TLRPC.TL_upload_getWebFile();
        ((TLRPC.TL_upload_getWebFile)localObject).location = this.webLocation;
        m = this.nextDownloadOffset;
        ((TLRPC.TL_upload_getWebFile)localObject).offset = m;
        ((TLRPC.TL_upload_getWebFile)localObject).limit = this.currentDownloadChunkSize;
        if (this.requestsCount % 2 != 0)
          break label315;
        i = 2;
        this.nextDownloadOffset += this.currentDownloadChunkSize;
        localRequestInfo = new RequestInfo(null);
        this.requestInfos.add(localRequestInfo);
        RequestInfo.access$1002(localRequestInfo, m);
        localConnectionsManager = ConnectionsManager.getInstance();
        local5 = new RequestDelegate(localRequestInfo)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if ((paramTLObject instanceof TLRPC.TL_upload_file))
            {
              FileLoadOperation.RequestInfo.access$802(this.val$requestInfo, (TLRPC.TL_upload_file)paramTLObject);
              if (paramTLObject != null)
              {
                if (FileLoadOperation.this.currentType != 50331648)
                  break label81;
                StatsController.getInstance().incrementReceivedBytesCount(paramTLObject.networkType, 3, paramTLObject.getObjectSize() + 4);
              }
            }
            while (true)
            {
              FileLoadOperation.this.processRequestResult(this.val$requestInfo, paramTL_error);
              return;
              FileLoadOperation.RequestInfo.access$902(this.val$requestInfo, (TLRPC.TL_upload_webFile)paramTLObject);
              break;
              label81: if (FileLoadOperation.this.currentType == 33554432)
              {
                StatsController.getInstance().incrementReceivedBytesCount(paramTLObject.networkType, 2, paramTLObject.getObjectSize() + 4);
                continue;
              }
              if (FileLoadOperation.this.currentType == 16777216)
              {
                StatsController.getInstance().incrementReceivedBytesCount(paramTLObject.networkType, 4, paramTLObject.getObjectSize() + 4);
                continue;
              }
              if (FileLoadOperation.this.currentType != 67108864)
                continue;
              StatsController.getInstance().incrementReceivedBytesCount(paramTLObject.networkType, 5, paramTLObject.getObjectSize() + 4);
            }
          }
        };
        if (!this.isForceRequest)
          break label383;
      }
      label309: label315: label322: label383: for (int m = 32; ; m = 0)
      {
        RequestInfo.access$702(localRequestInfo, localConnectionsManager.sendRequest((TLObject)localObject, local5, null, m | 0x2, this.datacenter_id, i, bool));
        this.requestsCount += 1;
        k += 1;
        break label75;
        break;
        bool = false;
        break label138;
        i = 65538;
        break label196;
        localObject = new TLRPC.TL_upload_getFile();
        ((TLRPC.TL_upload_getFile)localObject).location = this.location;
        m = this.nextDownloadOffset;
        ((TLRPC.TL_upload_getFile)localObject).offset = m;
        ((TLRPC.TL_upload_getFile)localObject).limit = this.currentDownloadChunkSize;
        if (this.requestsCount % 2 == 0);
        for (i = 2; ; i = 65538)
          break;
      }
    }
  }

  public void cancel()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if ((FileLoadOperation.this.state == 3) || (FileLoadOperation.this.state == 2))
          return;
        if (FileLoadOperation.this.requestInfos != null)
        {
          int i = 0;
          while (i < FileLoadOperation.this.requestInfos.size())
          {
            FileLoadOperation.RequestInfo localRequestInfo = (FileLoadOperation.RequestInfo)FileLoadOperation.this.requestInfos.get(i);
            if (FileLoadOperation.RequestInfo.access$700(localRequestInfo) != 0)
              ConnectionsManager.getInstance().cancelRequest(FileLoadOperation.RequestInfo.access$700(localRequestInfo), true);
            i += 1;
          }
        }
        FileLoadOperation.this.onFail(false, 1);
      }
    });
  }

  public int getCurrentType()
  {
    return this.currentType;
  }

  public String getFileName()
  {
    if (this.location != null)
      return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
    return Utilities.MD5(this.webLocation.url) + "." + this.ext;
  }

  public boolean isForceRequest()
  {
    return this.isForceRequest;
  }

  public void setDelegate(FileLoadOperationDelegate paramFileLoadOperationDelegate)
  {
    this.delegate = paramFileLoadOperationDelegate;
  }

  public void setForceRequest(boolean paramBoolean)
  {
    this.isForceRequest = paramBoolean;
  }

  public void setPaths(File paramFile1, File paramFile2)
  {
    this.storePath = paramFile1;
    this.tempPath = paramFile2;
  }

  public boolean start()
  {
    if (this.state != 0)
      return false;
    if ((this.location == null) && (this.webLocation == null))
    {
      onFail(true, 0);
      return false;
    }
    String str1 = null;
    String str4;
    String str5;
    String str2;
    String str3;
    if (this.webLocation != null)
    {
      String str6 = Utilities.MD5(this.webLocation.url);
      str4 = str6 + ".temp";
      str5 = str6 + "." + this.ext;
      str2 = str4;
      str3 = str5;
      if (this.key != null)
      {
        str1 = str6 + ".iv";
        str3 = str5;
        str2 = str4;
      }
    }
    while (true)
    {
      int i;
      if (this.totalBytesCount >= 1048576)
      {
        i = 131072;
        label164: this.currentDownloadChunkSize = i;
        if (this.totalBytesCount < 1048576)
          break label939;
        i = 2;
        label180: this.currentMaxDownloadRequests = i;
        this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
        this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
        this.state = 1;
        this.cacheFileFinal = new File(this.storePath, str3);
        if ((this.cacheFileFinal.exists()) && (this.totalBytesCount != 0) && (this.totalBytesCount != this.cacheFileFinal.length()))
          this.cacheFileFinal.delete();
        if (this.cacheFileFinal.exists())
          break label998;
        this.cacheFileTemp = new File(this.tempPath, str2);
        if (this.cacheFileTemp.exists())
        {
          this.downloadedBytes = (int)this.cacheFileTemp.length();
          i = this.downloadedBytes / this.currentDownloadChunkSize * this.currentDownloadChunkSize;
          this.downloadedBytes = i;
          this.nextDownloadOffset = i;
        }
        if (BuildVars.DEBUG_VERSION)
          FileLog.d("start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
        if (str1 != null)
          this.cacheIvTemp = new File(this.tempPath, str1);
      }
      try
      {
        this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
        long l = this.cacheIvTemp.length();
        if ((l > 0L) && (l % 32L == 0L))
          this.fiv.read(this.iv, 0, 32);
      }
      catch (Exception localException2)
      {
        try
        {
          while (true)
          {
            this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
            if (this.downloadedBytes != 0)
              this.fileOutputStream.seek(this.downloadedBytes);
            if (this.fileOutputStream != null)
              break label977;
            onFail(true, 0);
            return false;
            if ((this.location.volume_id != 0L) && (this.location.local_id != 0))
            {
              if ((this.datacenter_id == -2147483648) || (this.location.volume_id == -2147483648L) || (this.datacenter_id == 0))
              {
                onFail(true, 0);
                return false;
              }
              str4 = this.location.volume_id + "_" + this.location.local_id + ".temp";
              str5 = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
              str2 = str4;
              str3 = str5;
              if (this.key == null)
                break;
              str1 = this.location.volume_id + "_" + this.location.local_id + ".iv";
              str2 = str4;
              str3 = str5;
              break;
            }
            if ((this.datacenter_id == 0) || (this.location.id == 0L))
            {
              onFail(true, 0);
              return false;
            }
            str4 = this.datacenter_id + "_" + this.location.id + ".temp";
            str5 = this.datacenter_id + "_" + this.location.id + this.ext;
            str2 = str4;
            str3 = str5;
            if (this.key == null)
              break;
            str1 = this.datacenter_id + "_" + this.location.id + ".iv";
            str2 = str4;
            str3 = str5;
            break;
            i = 32768;
            break label164;
            label939: i = 4;
            break label180;
            this.downloadedBytes = 0;
            continue;
            localException1 = localException1;
            FileLog.e(localException1);
            this.downloadedBytes = 0;
          }
        }
        catch (Exception localException2)
        {
          while (true)
            FileLog.e(localException2);
          label977: this.started = true;
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if ((FileLoadOperation.this.totalBytesCount != 0) && (FileLoadOperation.this.downloadedBytes == FileLoadOperation.this.totalBytesCount))
                try
                {
                  FileLoadOperation.this.onFinishLoadingFile(false);
                  return;
                }
                catch (Exception localException)
                {
                  FileLoadOperation.this.onFail(true, 0);
                  return;
                }
              FileLoadOperation.this.startDownloadRequest();
            }
          });
        }
      }
    }
    while (true)
    {
      return true;
      label998: this.started = true;
      try
      {
        onFinishLoadingFile(false);
      }
      catch (Exception localException3)
      {
        onFail(true, 0);
      }
    }
  }

  public boolean wasStarted()
  {
    return this.started;
  }

  public static abstract interface FileLoadOperationDelegate
  {
    public abstract void didChangedLoadProgress(FileLoadOperation paramFileLoadOperation, float paramFloat);

    public abstract void didFailedLoadingFile(FileLoadOperation paramFileLoadOperation, int paramInt);

    public abstract void didFinishLoadingFile(FileLoadOperation paramFileLoadOperation, File paramFile);
  }

  private static class RequestInfo
  {
    private int offset;
    private int requestToken;
    private TLRPC.TL_upload_file response;
    private TLRPC.TL_upload_webFile responseWeb;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.FileLoadOperation
 * JD-Core Version:    0.6.0
 */