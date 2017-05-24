package org.vidogram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.InputEncryptedFile;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.TL_boolTrue;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputEncryptedFileBigUploaded;
import org.vidogram.tgnet.TLRPC.TL_inputEncryptedFileUploaded;
import org.vidogram.tgnet.TLRPC.TL_inputFile;
import org.vidogram.tgnet.TLRPC.TL_inputFileBig;
import org.vidogram.tgnet.TLRPC.TL_upload_saveBigFilePart;
import org.vidogram.tgnet.TLRPC.TL_upload_saveFilePart;

public class FileUploadOperation
{
  private HashMap<Integer, UploadCachedResult> cachedResults = new HashMap();
  private long currentFileId;
  private int currentPartNum;
  private int currentType;
  private int currentUploadRequetsCount;
  private FileUploadOperationDelegate delegate;
  private int estimatedSize;
  private String fileKey;
  private int fingerprint;
  private ArrayList<byte[]> freeRequestIvs;
  private boolean isBigFile;
  private boolean isEncrypted;
  private boolean isLastPart = false;
  private byte[] iv;
  private byte[] ivChange;
  private byte[] key;
  private int lastSavedPartNum;
  private final int maxRequestsCount = 8;
  private MessageDigest mdEnc;
  private SharedPreferences preferences;
  private byte[] readBuffer;
  private long readBytesCount;
  private int requestNum;
  private HashMap<Integer, Integer> requestTokens = new HashMap();
  private int saveInfoTimes;
  private boolean started;
  private int state;
  private FileInputStream stream;
  private long totalFileSize;
  private int totalPartsCount;
  private int uploadChunkSize = 131072;
  private int uploadStartTime;
  private long uploadedBytesCount;
  private String uploadingFilePath;

  public FileUploadOperation(String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.uploadingFilePath = paramString;
    this.isEncrypted = paramBoolean;
    this.estimatedSize = paramInt1;
    this.currentType = paramInt2;
  }

  private void cleanup()
  {
    if (this.preferences == null)
      this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
    this.preferences.edit().remove(this.fileKey + "_time").remove(this.fileKey + "_size").remove(this.fileKey + "_uploaded").remove(this.fileKey + "_id").remove(this.fileKey + "_iv").remove(this.fileKey + "_key").remove(this.fileKey + "_ivc").commit();
    try
    {
      if (this.stream != null)
      {
        this.stream.close();
        this.stream = null;
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  private void startUploadRequest()
  {
    int m = 1;
    if (this.state != 1)
      return;
    int i;
    while (true)
    {
      try
      {
        this.started = true;
        if (this.stream != null)
          break label1289;
        if (!this.isEncrypted)
          continue;
        this.freeRequestIvs = new ArrayList(8);
        i = 0;
        if (i >= 8)
          continue;
        this.freeRequestIvs.add(new byte[32]);
        i += 1;
        continue;
        File localFile = new File(this.uploadingFilePath);
        this.stream = new FileInputStream(localFile);
        if (this.estimatedSize == 0)
          continue;
        this.totalFileSize = this.estimatedSize;
        if (this.totalFileSize > 10485760L)
        {
          this.isBigFile = true;
          this.uploadChunkSize = (int)Math.max(128L, (this.totalFileSize + 3072000L - 1L) / 3072000L);
          if (1024 % this.uploadChunkSize == 0)
            break label245;
          i = 64;
          if (this.uploadChunkSize <= i)
            break;
          i *= 2;
          continue;
          this.totalFileSize = localFile.length();
          continue;
        }
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        this.delegate.didFailedUploadingFile(this);
        cleanup();
        return;
      }
      try
      {
        this.mdEnc = MessageDigest.getInstance("MD5");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        FileLog.e(localNoSuchAlgorithmException);
      }
    }
    this.uploadChunkSize = i;
    label245: this.uploadChunkSize *= 1024;
    this.totalPartsCount = ((int)(this.totalFileSize + this.uploadChunkSize - 1L) / this.uploadChunkSize);
    this.readBuffer = new byte[this.uploadChunkSize];
    Object localObject3 = new StringBuilder().append(this.uploadingFilePath);
    Object localObject1;
    label317: long l;
    int j;
    label662: int k;
    if (this.isEncrypted)
    {
      localObject1 = "enc";
      this.fileKey = Utilities.MD5((String)localObject1);
      l = this.preferences.getLong(this.fileKey + "_size", 0L);
      this.uploadStartTime = (int)(System.currentTimeMillis() / 1000L);
      if ((this.estimatedSize != 0) || (l != this.totalFileSize))
        break label1820;
      this.currentFileId = this.preferences.getLong(this.fileKey + "_id", 0L);
      j = this.preferences.getInt(this.fileKey + "_time", 0);
      l = this.preferences.getLong(this.fileKey + "_uploaded", 0L);
      if (this.isEncrypted)
      {
        localObject1 = this.preferences.getString(this.fileKey + "_iv", null);
        localObject3 = this.preferences.getString(this.fileKey + "_key", null);
        if ((localObject1 == null) || (localObject3 == null))
          break label1800;
        this.key = Utilities.hexToBytes((String)localObject3);
        this.iv = Utilities.hexToBytes((String)localObject1);
        if ((this.key == null) || (this.iv == null) || (this.key.length != 32) || (this.iv.length != 32))
          break label1795;
        this.ivChange = new byte[32];
        System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
        i = 0;
        if ((i != 0) || (j == 0))
          break label1815;
        if ((this.isBigFile) && (j < this.uploadStartTime - 86400))
        {
          k = 0;
          j = i;
          if (k == 0)
            break label1055;
          if (l <= 0L)
            break label1810;
          this.readBytesCount = l;
          this.currentPartNum = (int)(l / this.uploadChunkSize);
          if (!this.isBigFile)
            k = 0;
        }
        else
        {
          while (true)
          {
            j = i;
            if (k >= this.readBytesCount / this.uploadChunkSize)
              break label1055;
            int n = this.stream.read(this.readBuffer);
            if ((!this.isEncrypted) || (n % 16 == 0))
              break label1777;
            j = 16 - n % 16 + 0;
            label789: localObject1 = new NativeByteBuffer(n + j);
            if ((n != this.uploadChunkSize) || (this.totalPartsCount == this.currentPartNum + 1))
              this.isLastPart = true;
            ((NativeByteBuffer)localObject1).writeBytes(this.readBuffer, 0, n);
            if (this.isEncrypted)
            {
              m = 0;
              while (true)
                if (m < j)
                {
                  ((NativeByteBuffer)localObject1).writeByte(0);
                  m += 1;
                  continue;
                  k = j;
                  if (this.isBigFile)
                    break;
                  k = j;
                  if (j >= this.uploadStartTime - 5400.0F)
                    break;
                  k = 0;
                  break;
                }
              Utilities.aesIgeEncryption(((NativeByteBuffer)localObject1).buffer, this.key, this.ivChange, true, true, 0, j + n);
            }
            ((NativeByteBuffer)localObject1).rewind();
            this.mdEnc.update(((NativeByteBuffer)localObject1).buffer);
            ((NativeByteBuffer)localObject1).reuse();
            k += 1;
          }
        }
        this.stream.skip(l);
        j = i;
        if (this.isEncrypted)
        {
          localObject1 = this.preferences.getString(this.fileKey + "_ivc", null);
          if (localObject1 == null)
            break label1250;
          this.ivChange = Utilities.hexToBytes((String)localObject1);
          if ((this.ivChange != null) && (this.ivChange.length == 32))
            break label1774;
          this.readBytesCount = 0L;
          this.currentPartNum = 0;
          i = m;
          break label1805;
        }
      }
    }
    while (true)
    {
      label1055: if (j != 0)
      {
        if (this.isEncrypted)
        {
          this.iv = new byte[32];
          this.key = new byte[32];
          this.ivChange = new byte[32];
          Utilities.random.nextBytes(this.iv);
          Utilities.random.nextBytes(this.key);
          System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
        }
        this.currentFileId = Utilities.random.nextLong();
        if (this.estimatedSize == 0)
          storeFileUploadInfo();
      }
      boolean bool = this.isEncrypted;
      if (bool)
      {
        try
        {
          localObject1 = MessageDigest.getInstance("MD5");
          localObject3 = new byte[64];
          System.arraycopy(this.key, 0, localObject3, 0, 32);
          System.arraycopy(this.iv, 0, localObject3, 32, 32);
          localObject1 = ((MessageDigest)localObject1).digest(localObject3);
          i = 0;
          while (i < 4)
          {
            this.fingerprint |= ((localObject1[i] ^ localObject1[(i + 4)]) & 0xFF) << i * 8;
            i += 1;
            continue;
            label1250: this.readBytesCount = 0L;
            this.currentPartNum = 0;
            i = m;
          }
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
        }
      }
      else
      {
        this.uploadedBytesCount = this.readBytesCount;
        this.lastSavedPartNum = this.currentPartNum;
        label1289: if (this.estimatedSize != 0)
        {
          l = this.stream.getChannel().size();
          if (this.readBytesCount + this.uploadChunkSize > l)
            break;
        }
        k = this.stream.read(this.readBuffer);
        if (k == -1)
          break;
        if ((this.isEncrypted) && (k % 16 != 0));
        for (i = 16 - k % 16 + 0; ; i = 0)
        {
          Object localObject4 = new NativeByteBuffer(k + i);
          if ((k != this.uploadChunkSize) || ((this.estimatedSize == 0) && (this.totalPartsCount == this.currentPartNum + 1)))
            this.isLastPart = true;
          ((NativeByteBuffer)localObject4).writeBytes(this.readBuffer, 0, k);
          if (this.isEncrypted)
          {
            j = 0;
            while (j < i)
            {
              ((NativeByteBuffer)localObject4).writeByte(0);
              j += 1;
            }
            Utilities.aesIgeEncryption(((NativeByteBuffer)localObject4).buffer, this.key, this.ivChange, true, true, 0, i + k);
            localObject2 = (byte[])this.freeRequestIvs.get(0);
            System.arraycopy(this.ivChange, 0, localObject2, 0, 32);
            this.freeRequestIvs.remove(0);
            ((NativeByteBuffer)localObject4).rewind();
            if (!this.isBigFile)
              this.mdEnc.update(((NativeByteBuffer)localObject4).buffer);
            if (!this.isBigFile)
              break label1725;
            localObject3 = new TLRPC.TL_upload_saveBigFilePart();
            ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_part = this.currentPartNum;
            ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_id = this.currentFileId;
            if (this.estimatedSize == 0)
              break label1713;
            ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_total_parts = -1;
            label1576: ((TLRPC.TL_upload_saveBigFilePart)localObject3).bytes = ((NativeByteBuffer)localObject4);
            label1583: this.readBytesCount += k;
            this.currentUploadRequetsCount += 1;
            j = this.requestNum;
            this.requestNum = (j + 1);
            l = this.readBytesCount;
            i = this.currentPartNum;
            this.currentPartNum = (i + 1);
            m = ((TLObject)localObject3).getObjectSize();
            localObject4 = ConnectionsManager.getInstance();
            localObject2 = new RequestDelegate(m + 4, localObject2, j, k, i, l)
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                int i;
                if (paramTLObject != null)
                {
                  i = paramTLObject.networkType;
                  if (FileUploadOperation.this.currentType != 50331648)
                    break label319;
                  StatsController.getInstance().incrementSentBytesCount(i, 3, this.val$requestSize);
                  label34: if (this.val$currentRequestIv != null)
                    FileUploadOperation.this.freeRequestIvs.add(this.val$currentRequestIv);
                  FileUploadOperation.this.requestTokens.remove(Integer.valueOf(this.val$requestNumFinal));
                  if (!(paramTLObject instanceof TLRPC.TL_boolTrue))
                    break label1070;
                  FileUploadOperation.access$1202(FileUploadOperation.this, FileUploadOperation.this.uploadedBytesCount + this.val$currentRequestBytes);
                  FileUploadOperation.this.delegate.didChangedUploadProgress(FileUploadOperation.this, (float)FileUploadOperation.this.uploadedBytesCount / (float)FileUploadOperation.this.totalFileSize);
                  FileUploadOperation.access$810(FileUploadOperation.this);
                  if ((!FileUploadOperation.this.isLastPart) || (FileUploadOperation.this.currentUploadRequetsCount != 0) || (FileUploadOperation.this.state != 1))
                    break label616;
                  FileUploadOperation.access$1502(FileUploadOperation.this, 3);
                  if (FileUploadOperation.this.key != null)
                    break label463;
                  if (!FileUploadOperation.this.isBigFile)
                    break label403;
                  paramTLObject = new TLRPC.TL_inputFileBig();
                  label211: paramTLObject.parts = FileUploadOperation.this.currentPartNum;
                  paramTLObject.id = FileUploadOperation.this.currentFileId;
                  paramTLObject.name = FileUploadOperation.this.uploadingFilePath.substring(FileUploadOperation.this.uploadingFilePath.lastIndexOf("/") + 1);
                  FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, paramTLObject, null, null, null);
                  FileUploadOperation.this.cleanup();
                  label288: if (FileUploadOperation.this.currentType != 50331648)
                    break label998;
                  StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
                }
                label319: label463: label616: 
                do
                {
                  return;
                  i = ConnectionsManager.getCurrentNetworkType();
                  break;
                  if (FileUploadOperation.this.currentType == 33554432)
                  {
                    StatsController.getInstance().incrementSentBytesCount(i, 2, this.val$requestSize);
                    break label34;
                  }
                  if (FileUploadOperation.this.currentType == 16777216)
                  {
                    StatsController.getInstance().incrementSentBytesCount(i, 4, this.val$requestSize);
                    break label34;
                  }
                  if (FileUploadOperation.this.currentType != 67108864)
                    break label34;
                  StatsController.getInstance().incrementSentBytesCount(i, 5, this.val$requestSize);
                  break label34;
                  paramTLObject = new TLRPC.TL_inputFile();
                  paramTLObject.md5_checksum = String.format(Locale.US, "%32s", new Object[] { new BigInteger(1, FileUploadOperation.this.mdEnc.digest()).toString(16) }).replace(' ', '0');
                  break label211;
                  if (FileUploadOperation.this.isBigFile)
                    paramTLObject = new TLRPC.TL_inputEncryptedFileBigUploaded();
                  while (true)
                  {
                    paramTLObject.parts = FileUploadOperation.this.currentPartNum;
                    paramTLObject.id = FileUploadOperation.this.currentFileId;
                    paramTLObject.key_fingerprint = FileUploadOperation.this.fingerprint;
                    FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, null, paramTLObject, FileUploadOperation.this.key, FileUploadOperation.this.iv);
                    FileUploadOperation.this.cleanup();
                    break;
                    paramTLObject = new TLRPC.TL_inputEncryptedFileUploaded();
                    paramTLObject.md5_checksum = String.format(Locale.US, "%32s", new Object[] { new BigInteger(1, FileUploadOperation.this.mdEnc.digest()).toString(16) }).replace(' ', '0');
                  }
                  if (FileUploadOperation.this.currentUploadRequetsCount >= 8)
                    break label288;
                  if (FileUploadOperation.this.estimatedSize == 0)
                  {
                    if (FileUploadOperation.this.saveInfoTimes >= 4)
                      FileUploadOperation.access$2502(FileUploadOperation.this, 0);
                    if (this.val$currentRequestPartNum != FileUploadOperation.this.lastSavedPartNum)
                      break label923;
                    FileUploadOperation.access$2608(FileUploadOperation.this);
                    long l = this.val$currentRequestBytesOffset;
                    paramTLObject = this.val$currentRequestIv;
                    while (true)
                    {
                      paramTL_error = (FileUploadOperation.UploadCachedResult)FileUploadOperation.this.cachedResults.get(Integer.valueOf(FileUploadOperation.this.lastSavedPartNum));
                      if (paramTL_error == null)
                        break;
                      l = FileUploadOperation.UploadCachedResult.access$2800(paramTL_error);
                      paramTLObject = FileUploadOperation.UploadCachedResult.access$2900(paramTL_error);
                      FileUploadOperation.this.cachedResults.remove(Integer.valueOf(FileUploadOperation.this.lastSavedPartNum));
                      FileUploadOperation.access$2608(FileUploadOperation.this);
                    }
                    if (((FileUploadOperation.this.isBigFile) && (l % 1048576L == 0L)) || ((!FileUploadOperation.this.isBigFile) && (FileUploadOperation.this.saveInfoTimes == 0)))
                    {
                      paramTL_error = FileUploadOperation.this.preferences.edit();
                      paramTL_error.putLong(FileUploadOperation.this.fileKey + "_uploaded", l);
                      if (FileUploadOperation.this.isEncrypted)
                        paramTL_error.putString(FileUploadOperation.this.fileKey + "_ivc", Utilities.bytesToHex(paramTLObject));
                      paramTL_error.commit();
                    }
                  }
                  while (true)
                  {
                    FileUploadOperation.access$2508(FileUploadOperation.this);
                    FileUploadOperation.this.startUploadRequest();
                    break;
                    paramTLObject = new FileUploadOperation.UploadCachedResult(FileUploadOperation.this, null);
                    FileUploadOperation.UploadCachedResult.access$2802(paramTLObject, this.val$currentRequestBytesOffset);
                    if (this.val$currentRequestIv != null)
                    {
                      FileUploadOperation.UploadCachedResult.access$2902(paramTLObject, new byte[32]);
                      System.arraycopy(this.val$currentRequestIv, 0, FileUploadOperation.UploadCachedResult.access$2900(paramTLObject), 0, 32);
                    }
                    FileUploadOperation.this.cachedResults.put(Integer.valueOf(this.val$currentRequestPartNum), paramTLObject);
                  }
                  if (FileUploadOperation.this.currentType == 33554432)
                  {
                    StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
                    return;
                  }
                  if (FileUploadOperation.this.currentType != 16777216)
                    continue;
                  StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
                  return;
                }
                while (FileUploadOperation.this.currentType != 67108864);
                label403: label923: label998: StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
                return;
                label1070: FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
                FileUploadOperation.this.cleanup();
              }
            };
            if (this.currentUploadRequetsCount % 2 != 0)
              break label1762;
          }
          label1713: label1725: label1762: for (i = 4; ; i = 65540)
          {
            i = ((ConnectionsManager)localObject4).sendRequest((TLObject)localObject3, (RequestDelegate)localObject2, 0, i);
            this.requestTokens.put(Integer.valueOf(j), Integer.valueOf(i));
            return;
            localObject2 = null;
            break;
            ((TLRPC.TL_upload_saveBigFilePart)localObject3).file_total_parts = this.totalPartsCount;
            break label1576;
            localObject3 = new TLRPC.TL_upload_saveFilePart();
            ((TLRPC.TL_upload_saveFilePart)localObject3).file_part = this.currentPartNum;
            ((TLRPC.TL_upload_saveFilePart)localObject3).file_id = this.currentFileId;
            ((TLRPC.TL_upload_saveFilePart)localObject3).bytes = ((NativeByteBuffer)localObject4);
            break label1583;
          }
        }
        label1774: break label1805;
        label1777: j = 0;
        break label789;
        i = 0;
        break label662;
        Object localObject2 = "";
        break label317;
        label1795: i = 1;
        break label662;
        label1800: i = 1;
        break label662;
      }
      label1805: j = i;
      continue;
      label1810: j = 1;
      continue;
      label1815: j = 1;
      continue;
      label1820: j = 1;
    }
  }

  private void storeFileUploadInfo()
  {
    SharedPreferences.Editor localEditor = this.preferences.edit();
    localEditor.putInt(this.fileKey + "_time", this.uploadStartTime);
    localEditor.putLong(this.fileKey + "_size", this.totalFileSize);
    localEditor.putLong(this.fileKey + "_id", this.currentFileId);
    localEditor.remove(this.fileKey + "_uploaded");
    if (this.isEncrypted)
    {
      localEditor.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
      localEditor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
      localEditor.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
    }
    localEditor.commit();
  }

  public void cancel()
  {
    if (this.state == 3)
      return;
    this.state = 2;
    Iterator localIterator = this.requestTokens.values().iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      ConnectionsManager.getInstance().cancelRequest(localInteger.intValue(), true);
    }
    this.delegate.didFailedUploadingFile(this);
    cleanup();
  }

  protected void checkNewDataAvailable(long paramLong)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramLong)
    {
      public void run()
      {
        if ((FileUploadOperation.this.estimatedSize != 0) && (this.val$finalSize != 0L))
        {
          FileUploadOperation.access$202(FileUploadOperation.this, 0);
          FileUploadOperation.access$302(FileUploadOperation.this, this.val$finalSize);
          FileUploadOperation.access$402(FileUploadOperation.this, (int)(FileUploadOperation.this.totalFileSize + FileUploadOperation.this.uploadChunkSize - 1L) / FileUploadOperation.this.uploadChunkSize);
          if (FileUploadOperation.this.started)
            FileUploadOperation.this.storeFileUploadInfo();
        }
        if (FileUploadOperation.this.currentUploadRequetsCount < 8)
          FileUploadOperation.this.startUploadRequest();
      }
    });
  }

  public long getTotalFileSize()
  {
    return this.totalFileSize;
  }

  public void setDelegate(FileUploadOperationDelegate paramFileUploadOperationDelegate)
  {
    this.delegate = paramFileUploadOperationDelegate;
  }

  public void start()
  {
    if (this.state != 0)
      return;
    this.state = 1;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        FileUploadOperation.access$002(FileUploadOperation.this, ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0));
        while (i < 8)
        {
          FileUploadOperation.this.startUploadRequest();
          i += 1;
        }
      }
    });
  }

  public static abstract interface FileUploadOperationDelegate
  {
    public abstract void didChangedUploadProgress(FileUploadOperation paramFileUploadOperation, float paramFloat);

    public abstract void didFailedUploadingFile(FileUploadOperation paramFileUploadOperation);

    public abstract void didFinishUploadingFile(FileUploadOperation paramFileUploadOperation, TLRPC.InputFile paramInputFile, TLRPC.InputEncryptedFile paramInputEncryptedFile, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  }

  private class UploadCachedResult
  {
    private long bytesOffset;
    private byte[] iv;

    private UploadCachedResult()
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.FileUploadOperation
 * JD-Core Version:    0.6.0
 */