package org.vidogram.messenger;

import itman.Vidofilm.d.e;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputEncryptedFile;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.vidogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_messageService;
import org.vidogram.tgnet.TLRPC.TL_photoCachedSize;
import org.vidogram.tgnet.TLRPC.TL_webDocument;
import org.vidogram.tgnet.TLRPC.WebPage;

public class FileLoader
{
  private static volatile FileLoader Instance = null;
  public static final int MEDIA_DIR_AUDIO = 1;
  public static final int MEDIA_DIR_CACHE = 4;
  public static final int MEDIA_DIR_DOCUMENT = 3;
  public static final int MEDIA_DIR_IMAGE = 0;
  public static final int MEDIA_DIR_VIDEO = 2;
  private LinkedList<FileLoadOperation> audioLoadOperationQueue = new LinkedList();
  private int currentAudioLoadOperationsCount = 0;
  private int currentLoadOperationsCount = 0;
  private int currentPhotoLoadOperationsCount = 0;
  private int currentUploadOperationsCount = 0;
  private int currentUploadSmallOperationsCount = 0;
  private FileLoaderDelegate delegate = null;
  private volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
  private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap();
  private LinkedList<FileLoadOperation> loadOperationQueue = new LinkedList();
  private HashMap<Integer, File> mediaDirs = null;
  private LinkedList<FileLoadOperation> photoLoadOperationQueue = new LinkedList();
  private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap();
  private ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap();
  private LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList();
  private HashMap<String, Long> uploadSizes = new HashMap();
  private LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList();

  private void cancelLoadFile(TLRPC.Document paramDocument, TLRPC.TL_webDocument paramTL_webDocument, TLRPC.FileLocation paramFileLocation, String paramString)
  {
    if ((paramFileLocation == null) && (paramDocument == null))
      return;
    this.fileLoaderQueue.postRunnable(new Runnable(paramFileLocation, paramString, paramDocument, paramTL_webDocument)
    {
      public void run()
      {
        Object localObject = null;
        if (this.val$location != null)
        {
          localObject = FileLoader.getAttachFileName(this.val$location, this.val$locationExt);
          if (localObject != null)
            break label62;
        }
        label62: 
        do
        {
          return;
          if (this.val$document != null)
          {
            localObject = FileLoader.getAttachFileName(this.val$document);
            break;
          }
          if (this.val$webDocument == null)
            break;
          localObject = FileLoader.getAttachFileName(this.val$webDocument);
          break;
          localObject = (FileLoadOperation)FileLoader.this.loadOperationPaths.remove(localObject);
        }
        while (localObject == null);
        if ((MessageObject.isVoiceDocument(this.val$document)) || (MessageObject.isVoiceWebDocument(this.val$webDocument)))
          if (!FileLoader.this.audioLoadOperationQueue.remove(localObject))
            FileLoader.access$1110(FileLoader.this);
        while (true)
        {
          ((FileLoadOperation)localObject).cancel();
          return;
          if (this.val$location != null)
          {
            if ((FileLoader.this.photoLoadOperationQueue.remove(localObject)) && (!MessageObject.isImageWebDocument(this.val$webDocument)))
              continue;
            FileLoader.access$1310(FileLoader.this);
            continue;
          }
          if (FileLoader.this.loadOperationQueue.remove(localObject))
            continue;
          FileLoader.access$1510(FileLoader.this);
        }
      }
    });
  }

  private void checkDownloadQueue(TLRPC.Document paramDocument, TLRPC.TL_webDocument paramTL_webDocument, TLRPC.FileLocation paramFileLocation, String paramString)
  {
    this.fileLoaderQueue.postRunnable(new Runnable(paramString, paramDocument, paramTL_webDocument, paramFileLocation)
    {
      public void run()
      {
        FileLoadOperation localFileLoadOperation = (FileLoadOperation)FileLoader.this.loadOperationPaths.remove(this.val$arg1);
        int i;
        if ((MessageObject.isVoiceDocument(this.val$document)) || (MessageObject.isVoiceWebDocument(this.val$webDocument)))
        {
          if (localFileLoadOperation != null)
          {
            if (localFileLoadOperation.wasStarted())
              FileLoader.access$1110(FileLoader.this);
          }
          else
          {
            if (FileLoader.this.audioLoadOperationQueue.isEmpty())
              return;
            if (!((FileLoadOperation)FileLoader.this.audioLoadOperationQueue.get(0)).isForceRequest())
              break label154;
          }
          label154: for (i = 3; ; i = 1)
          {
            if (FileLoader.this.currentAudioLoadOperationsCount >= i)
              return;
            localFileLoadOperation = (FileLoadOperation)FileLoader.this.audioLoadOperationQueue.poll();
            if ((localFileLoadOperation == null) || (!localFileLoadOperation.start()))
              break;
            FileLoader.access$1108(FileLoader.this);
            break;
            FileLoader.this.audioLoadOperationQueue.remove(localFileLoadOperation);
            break;
          }
        }
        else if ((this.val$location != null) || (MessageObject.isImageWebDocument(this.val$webDocument)))
        {
          if (localFileLoadOperation != null)
          {
            if (localFileLoadOperation.wasStarted())
              FileLoader.access$1310(FileLoader.this);
          }
          else
          {
            if (FileLoader.this.photoLoadOperationQueue.isEmpty())
              return;
            if (!((FileLoadOperation)FileLoader.this.photoLoadOperationQueue.get(0)).isForceRequest())
              break label292;
          }
          label292: for (i = 3; ; i = 1)
          {
            if (FileLoader.this.currentPhotoLoadOperationsCount >= i)
              return;
            localFileLoadOperation = (FileLoadOperation)FileLoader.this.photoLoadOperationQueue.poll();
            if ((localFileLoadOperation == null) || (!localFileLoadOperation.start()))
              break;
            FileLoader.access$1308(FileLoader.this);
            break;
            FileLoader.this.photoLoadOperationQueue.remove(localFileLoadOperation);
            break;
          }
        }
        else
        {
          if (localFileLoadOperation != null)
          {
            if (localFileLoadOperation.wasStarted())
              FileLoader.access$1510(FileLoader.this);
          }
          else
          {
            if (FileLoader.this.loadOperationQueue.isEmpty())
              return;
            if (!((FileLoadOperation)FileLoader.this.loadOperationQueue.get(0)).isForceRequest())
              break label413;
          }
          label413: for (i = 3; ; i = 1)
          {
            if (FileLoader.this.currentLoadOperationsCount >= i)
              return;
            localFileLoadOperation = (FileLoadOperation)FileLoader.this.loadOperationQueue.poll();
            if ((localFileLoadOperation == null) || (!localFileLoadOperation.start()))
              break;
            FileLoader.access$1508(FileLoader.this);
            break;
            FileLoader.this.loadOperationQueue.remove(localFileLoadOperation);
            break;
          }
        }
      }
    });
  }

  public static String getAttachFileName(TLObject paramTLObject)
  {
    return getAttachFileName(paramTLObject, null);
  }

  public static String getAttachFileName(TLObject paramTLObject, String paramString)
  {
    if ((paramTLObject instanceof TLRPC.Document))
    {
      TLRPC.Document localDocument = (TLRPC.Document)paramTLObject;
      paramString = null;
      int i;
      if (0 == 0)
      {
        paramTLObject = getDocumentFileName(localDocument);
        if (paramTLObject != null)
        {
          i = paramTLObject.lastIndexOf('.');
          if (i != -1);
        }
        else
        {
          paramString = "";
        }
      }
      else
      {
        paramTLObject = paramString;
        if (paramString.length() <= 1)
        {
          if (localDocument.mime_type == null)
            break label221;
          paramTLObject = localDocument.mime_type;
          switch (paramTLObject.hashCode())
          {
          default:
            label96: i = -1;
            switch (i)
            {
            default:
              label98: paramTLObject = "";
            case 0:
            case 1:
            }
          case 1331848029:
          case 187091926:
          }
        }
      }
      while (true)
      {
        if (localDocument.version != 0)
          break label257;
        if (paramTLObject.length() <= 1)
          break label227;
        return localDocument.dc_id + "_" + localDocument.id + paramTLObject;
        paramString = paramTLObject.substring(i);
        break;
        if (!paramTLObject.equals("video/mp4"))
          break label96;
        i = 0;
        break label98;
        if (!paramTLObject.equals("audio/ogg"))
          break label96;
        i = 1;
        break label98;
        paramTLObject = ".mp4";
        continue;
        paramTLObject = ".ogg";
        continue;
        label221: paramTLObject = "";
      }
      label227: return localDocument.dc_id + "_" + localDocument.id;
      label257: if (paramTLObject.length() > 1)
        return localDocument.dc_id + "_" + localDocument.id + "_" + localDocument.version + paramTLObject;
      return localDocument.dc_id + "_" + localDocument.id + "_" + localDocument.version;
    }
    if ((paramTLObject instanceof TLRPC.TL_webDocument))
    {
      paramTLObject = (TLRPC.TL_webDocument)paramTLObject;
      return Utilities.MD5(paramTLObject.url) + "." + ImageLoader.getHttpUrlExtension(paramTLObject.url, getExtensionByMime(paramTLObject.mime_type));
    }
    if ((paramTLObject instanceof TLRPC.PhotoSize))
    {
      paramTLObject = (TLRPC.PhotoSize)paramTLObject;
      if ((paramTLObject.location == null) || ((paramTLObject.location instanceof TLRPC.TL_fileLocationUnavailable)))
        return "";
      paramTLObject = new StringBuilder().append(paramTLObject.location.volume_id).append("_").append(paramTLObject.location.local_id).append(".");
      if (paramString != null);
      while (true)
      {
        return paramString;
        paramString = "jpg";
      }
    }
    if ((paramTLObject instanceof TLRPC.FileLocation))
    {
      if ((paramTLObject instanceof TLRPC.TL_fileLocationUnavailable))
        return "";
      paramTLObject = (TLRPC.FileLocation)paramTLObject;
      paramTLObject = new StringBuilder().append(paramTLObject.volume_id).append("_").append(paramTLObject.local_id).append(".");
      if (paramString != null);
      while (true)
      {
        return paramString;
        paramString = "jpg";
      }
    }
    return "";
  }

  public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> paramArrayList, int paramInt)
  {
    return getClosestPhotoSizeWithSize(paramArrayList, paramInt, false);
  }

  public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> paramArrayList, int paramInt, boolean paramBoolean)
  {
    TLRPC.PhotoSize localPhotoSize = null;
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramArrayList != null)
    {
      if (paramArrayList.isEmpty())
        localObject2 = localObject1;
    }
    else
      return localObject2;
    int m = 0;
    int i = 0;
    localObject1 = localPhotoSize;
    int j;
    while (true)
    {
      localObject2 = localObject1;
      if (m >= paramArrayList.size())
        break;
      localPhotoSize = (TLRPC.PhotoSize)paramArrayList.get(m);
      if (localPhotoSize != null)
        break label89;
      j = i;
      localObject2 = localObject1;
      m += 1;
      localObject1 = localObject2;
      i = j;
    }
    label89: if (paramBoolean)
      if (localPhotoSize.h >= localPhotoSize.w)
      {
        j = localPhotoSize.w;
        label113: if ((localObject1 != null) && ((paramInt <= 100) || (((TLRPC.PhotoSize)localObject1).location == null) || (((TLRPC.PhotoSize)localObject1).location.dc_id != -2147483648)) && (!(localPhotoSize instanceof TLRPC.TL_photoCachedSize)) && ((paramInt <= i) || (i >= j)))
          break label300;
        i = j;
        localObject1 = localPhotoSize;
      }
    label300: 
    while (true)
    {
      localObject2 = localObject1;
      j = i;
      break;
      j = localPhotoSize.h;
      break label113;
      if (localPhotoSize.w >= localPhotoSize.h);
      for (int k = localPhotoSize.w; ; k = localPhotoSize.h)
      {
        if ((localObject1 != null) && ((paramInt <= 100) || (((TLRPC.PhotoSize)localObject1).location == null) || (((TLRPC.PhotoSize)localObject1).location.dc_id != -2147483648)) && (!(localPhotoSize instanceof TLRPC.TL_photoCachedSize)))
        {
          localObject2 = localObject1;
          j = i;
          if (k > paramInt)
            break;
          localObject2 = localObject1;
          j = i;
          if (i >= k)
            break;
        }
        localObject2 = localPhotoSize;
        j = k;
        break;
      }
    }
  }

  public static String getDocumentExtension(TLRPC.Document paramDocument)
  {
    Object localObject = getDocumentFileName(paramDocument);
    int i = ((String)localObject).lastIndexOf('.');
    String str = null;
    if (i != -1)
      str = ((String)localObject).substring(i + 1);
    if (str != null)
    {
      localObject = str;
      if (str.length() != 0);
    }
    else
    {
      localObject = paramDocument.mime_type;
    }
    paramDocument = (TLRPC.Document)localObject;
    if (localObject == null)
      paramDocument = "";
    return (String)paramDocument.toUpperCase();
  }

  public static String getDocumentFileName(TLRPC.Document paramDocument)
  {
    if (paramDocument != null)
    {
      if (paramDocument.file_name != null)
        return paramDocument.file_name;
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeFilename))
          return localDocumentAttribute.file_name;
        i += 1;
      }
    }
    return "";
  }

  public static String getExtensionByMime(String paramString)
  {
    int i = paramString.indexOf('/');
    if (i != -1)
      return paramString.substring(i + 1);
    return "";
  }

  public static String getFileExtension(File paramFile)
  {
    paramFile = paramFile.getName();
    try
    {
      paramFile = paramFile.substring(paramFile.lastIndexOf('.') + 1);
      return paramFile;
    }
    catch (Exception paramFile)
    {
    }
    return "";
  }

  public static FileLoader getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        FileLoader localFileLoader = Instance;
        localObject1 = localFileLoader;
        if (localFileLoader == null)
        {
          localObject1 = new FileLoader();
          Instance = (FileLoader)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (FileLoader)localObject2;
  }

  public static String getMessageFileName(TLRPC.Message paramMessage)
  {
    if (paramMessage == null)
      return "";
    if ((paramMessage instanceof TLRPC.TL_messageService))
    {
      if (paramMessage.action.photo != null)
      {
        paramMessage = paramMessage.action.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null)
            return getAttachFileName(paramMessage);
        }
      }
    }
    else
    {
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
        return getAttachFileName(paramMessage.media.document);
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        paramMessage = paramMessage.media.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null)
            return getAttachFileName(paramMessage);
        }
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      {
        if (paramMessage.media.webpage.photo != null)
        {
          paramMessage = paramMessage.media.webpage.photo.sizes;
          if (paramMessage.size() > 0)
          {
            paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
            if (paramMessage != null)
              return getAttachFileName(paramMessage);
          }
        }
        else
        {
          if (paramMessage.media.webpage.document != null)
            return getAttachFileName(paramMessage.media.webpage.document);
          if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
            return getAttachFileName(((TLRPC.TL_messageMediaInvoice)paramMessage.media).photo);
        }
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
      {
        paramMessage = ((TLRPC.TL_messageMediaInvoice)paramMessage.media).photo;
        if (paramMessage != null)
          return Utilities.MD5(paramMessage.url) + "." + ImageLoader.getHttpUrlExtension(paramMessage.url, getExtensionByMime(paramMessage.mime_type));
      }
    }
    return "";
  }

  public static File getPathToAttach(TLObject paramTLObject)
  {
    return getPathToAttach(paramTLObject, null, false);
  }

  public static File getPathToAttach(TLObject paramTLObject, String paramString, boolean paramBoolean)
  {
    Object localObject;
    if (paramBoolean)
      localObject = getInstance().getDirectory(4);
    while (true)
    {
      if (localObject == null)
      {
        return new File("");
        if ((paramTLObject instanceof TLRPC.Document))
        {
          localObject = (TLRPC.Document)paramTLObject;
          if (((TLRPC.Document)localObject).key != null)
            localObject = getInstance().getDirectory(4);
          while (true)
          {
            break;
            if (MessageObject.isVoiceDocument((TLRPC.Document)localObject))
            {
              localObject = getInstance().getDirectory(1);
              continue;
            }
            if (MessageObject.isVideoDocument((TLRPC.Document)localObject))
            {
              localObject = getInstance().getDirectory(2);
              continue;
            }
            localObject = getInstance().getDirectory(3);
          }
        }
        if ((paramTLObject instanceof TLRPC.PhotoSize))
        {
          localObject = (TLRPC.PhotoSize)paramTLObject;
          if ((((TLRPC.PhotoSize)localObject).location == null) || (((TLRPC.PhotoSize)localObject).location.key != null) || ((((TLRPC.PhotoSize)localObject).location.volume_id == -2147483648L) && (((TLRPC.PhotoSize)localObject).location.local_id < 0)) || (((TLRPC.PhotoSize)localObject).size < 0));
          for (localObject = getInstance().getDirectory(4); ; localObject = getInstance().getDirectory(0))
            break;
        }
        if ((paramTLObject instanceof TLRPC.FileLocation))
        {
          localObject = (TLRPC.FileLocation)paramTLObject;
          if ((((TLRPC.FileLocation)localObject).key != null) || ((((TLRPC.FileLocation)localObject).volume_id == -2147483648L) && (((TLRPC.FileLocation)localObject).local_id < 0)));
          for (localObject = getInstance().getDirectory(4); ; localObject = getInstance().getDirectory(0))
            break;
        }
        if ((paramTLObject instanceof TLRPC.TL_webDocument))
        {
          localObject = (TLRPC.TL_webDocument)paramTLObject;
          if (((TLRPC.TL_webDocument)localObject).mime_type.startsWith("image/"))
          {
            localObject = getInstance().getDirectory(0);
            continue;
          }
          if (((TLRPC.TL_webDocument)localObject).mime_type.startsWith("audio/"))
          {
            localObject = getInstance().getDirectory(1);
            continue;
          }
          if (((TLRPC.TL_webDocument)localObject).mime_type.startsWith("video/"))
          {
            localObject = getInstance().getDirectory(2);
            continue;
          }
          localObject = getInstance().getDirectory(3);
          continue;
        }
      }
      else
      {
        return new File((File)localObject, getAttachFileName(paramTLObject, paramString));
      }
      localObject = null;
    }
  }

  public static File getPathToAttach(TLObject paramTLObject, boolean paramBoolean)
  {
    return getPathToAttach(paramTLObject, null, paramBoolean);
  }

  public static File getPathToMessage(TLRPC.Message paramMessage)
  {
    if (paramMessage == null)
      return new File("");
    if ((paramMessage instanceof TLRPC.TL_messageService))
    {
      if (paramMessage.action.photo != null)
      {
        paramMessage = paramMessage.action.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null)
            return getPathToAttach(paramMessage);
        }
      }
    }
    else
    {
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
        return getPathToAttach(paramMessage.media.document);
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        paramMessage = paramMessage.media.photo.sizes;
        if (paramMessage.size() > 0)
        {
          paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
          if (paramMessage != null)
            return getPathToAttach(paramMessage);
        }
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      {
        if (paramMessage.media.webpage.document != null)
          return getPathToAttach(paramMessage.media.webpage.document);
        if (paramMessage.media.webpage.photo != null)
        {
          paramMessage = paramMessage.media.webpage.photo.sizes;
          if (paramMessage.size() > 0)
          {
            paramMessage = getClosestPhotoSizeWithSize(paramMessage, AndroidUtilities.getPhotoSize());
            if (paramMessage != null)
              return getPathToAttach(paramMessage);
          }
        }
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
      {
        return getPathToAttach(((TLRPC.TL_messageMediaInvoice)paramMessage.media).photo, true);
      }
    }
    return new File("");
  }

  private void loadFile(TLRPC.Document paramDocument, TLRPC.TL_webDocument paramTL_webDocument, TLRPC.FileLocation paramFileLocation, String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.fileLoaderQueue.postRunnable(new Runnable(paramFileLocation, paramString, paramDocument, paramTL_webDocument, paramBoolean1, paramInt, paramBoolean2)
    {
      public void run()
      {
        int j = 3;
        String str;
        if (this.val$location != null)
          str = FileLoader.getAttachFileName(this.val$location, this.val$locationExt);
        while (true)
        {
          if ((str == null) || (str.contains("-2147483648")));
          Object localObject2;
          Object localObject1;
          int i;
          label205: 
          while (true)
          {
            return;
            if (this.val$document != null)
            {
              str = FileLoader.getAttachFileName(this.val$document);
              break;
            }
            if (this.val$webDocument == null)
              break label656;
            str = FileLoader.getAttachFileName(this.val$webDocument);
            break;
            localObject2 = (FileLoadOperation)FileLoader.this.loadOperationPaths.get(str);
            if (localObject2 == null)
              break label207;
            if (!this.val$force)
              continue;
            ((FileLoadOperation)localObject2).setForceRequest(true);
            if ((MessageObject.isVoiceDocument(this.val$document)) || (MessageObject.isVoiceWebDocument(this.val$webDocument)))
              localObject1 = FileLoader.this.audioLoadOperationQueue;
            while (true)
            {
              if (localObject1 == null)
                break label205;
              i = ((LinkedList)localObject1).indexOf(localObject2);
              if (i <= 0)
                break;
              ((LinkedList)localObject1).remove(i);
              ((LinkedList)localObject1).add(0, localObject2);
              return;
              if ((this.val$location != null) || (MessageObject.isImageWebDocument(this.val$webDocument)))
              {
                localObject1 = FileLoader.this.photoLoadOperationQueue;
                continue;
              }
              localObject1 = FileLoader.this.loadOperationQueue;
            }
          }
          label207: File localFile = FileLoader.this.getDirectory(4);
          if (this.val$location != null)
          {
            localObject1 = new FileLoadOperation(this.val$location, this.val$locationExt, this.val$locationSize);
            i = 0;
          }
          while (true)
          {
            label246: if (!this.val$cacheOnly);
            for (localObject2 = FileLoader.this.getDirectory(i); ; localObject2 = localFile)
            {
              ((FileLoadOperation)localObject1).setPaths((File)localObject2, localFile);
              if (i == 2)
                e.a(ApplicationLoader.applicationContext).d();
              ((FileLoadOperation)localObject1).setDelegate(new FileLoadOperation.FileLoadOperationDelegate(str, i)
              {
                public void didChangedLoadProgress(FileLoadOperation paramFileLoadOperation, float paramFloat)
                {
                  if (FileLoader.this.delegate != null)
                    FileLoader.this.delegate.fileLoadProgressChanged(this.val$finalFileName, paramFloat);
                }

                public void didFailedLoadingFile(FileLoadOperation paramFileLoadOperation, int paramInt)
                {
                  FileLoader.this.checkDownloadQueue(FileLoader.6.this.val$document, FileLoader.6.this.val$webDocument, FileLoader.6.this.val$location, this.val$finalFileName);
                  if (FileLoader.this.delegate != null)
                    FileLoader.this.delegate.fileDidFailedLoad(this.val$finalFileName, paramInt);
                }

                public void didFinishLoadingFile(FileLoadOperation paramFileLoadOperation, File paramFile)
                {
                  if (FileLoader.this.delegate != null)
                    FileLoader.this.delegate.fileDidLoaded(this.val$finalFileName, paramFile, this.val$finalType);
                  FileLoader.this.checkDownloadQueue(FileLoader.6.this.val$document, FileLoader.6.this.val$webDocument, FileLoader.6.this.val$location, this.val$finalFileName);
                }
              });
              FileLoader.this.loadOperationPaths.put(str, localObject1);
              if (this.val$force);
              while (true)
              {
                if (i != 1)
                  break label514;
                if (FileLoader.this.currentAudioLoadOperationsCount >= j)
                  break label481;
                if (!((FileLoadOperation)localObject1).start())
                  break;
                FileLoader.access$1108(FileLoader.this);
                return;
                if (this.val$document != null)
                {
                  localObject1 = new FileLoadOperation(this.val$document);
                  if (MessageObject.isVoiceDocument(this.val$document))
                  {
                    i = 1;
                    break label246;
                  }
                  if (MessageObject.isVideoDocument(this.val$document))
                  {
                    i = 2;
                    break label246;
                  }
                  i = 3;
                  break label246;
                }
                if (this.val$webDocument == null)
                  break label648;
                localObject1 = new FileLoadOperation(this.val$webDocument);
                if (MessageObject.isVoiceWebDocument(this.val$webDocument))
                {
                  i = 1;
                  break label246;
                }
                if (MessageObject.isVideoWebDocument(this.val$webDocument))
                {
                  i = 2;
                  break label246;
                }
                if (MessageObject.isImageWebDocument(this.val$webDocument))
                {
                  i = 0;
                  break label246;
                }
                i = 3;
                break label246;
                j = 1;
              }
              label481: if (this.val$force)
              {
                FileLoader.this.audioLoadOperationQueue.add(0, localObject1);
                return;
              }
              FileLoader.this.audioLoadOperationQueue.add(localObject1);
              return;
              label514: if (this.val$location != null)
              {
                if (FileLoader.this.currentPhotoLoadOperationsCount < j)
                {
                  if (!((FileLoadOperation)localObject1).start())
                    break;
                  FileLoader.access$1308(FileLoader.this);
                  return;
                }
                if (this.val$force)
                {
                  FileLoader.this.photoLoadOperationQueue.add(0, localObject1);
                  return;
                }
                FileLoader.this.photoLoadOperationQueue.add(localObject1);
                return;
              }
              if (FileLoader.this.currentLoadOperationsCount < j)
              {
                if (!((FileLoadOperation)localObject1).start())
                  break;
                FileLoader.access$1508(FileLoader.this);
                return;
              }
              if (this.val$force)
              {
                FileLoader.this.loadOperationQueue.add(0, localObject1);
                return;
              }
              FileLoader.this.loadOperationQueue.add(localObject1);
              return;
            }
            label648: localObject1 = localObject2;
            i = 4;
          }
          label656: str = null;
        }
      }
    });
  }

  public void cancelLoadFile(TLRPC.Document paramDocument)
  {
    cancelLoadFile(paramDocument, null, null, null);
  }

  public void cancelLoadFile(TLRPC.FileLocation paramFileLocation, String paramString)
  {
    cancelLoadFile(null, null, paramFileLocation, paramString);
  }

  public void cancelLoadFile(TLRPC.PhotoSize paramPhotoSize)
  {
    cancelLoadFile(null, null, paramPhotoSize.location, null);
  }

  public void cancelLoadFile(TLRPC.TL_webDocument paramTL_webDocument)
  {
    cancelLoadFile(null, paramTL_webDocument, null, null);
  }

  public void cancelUploadFile(String paramString, boolean paramBoolean)
  {
    this.fileLoaderQueue.postRunnable(new Runnable(paramBoolean, paramString)
    {
      public void run()
      {
        if (!this.val$enc);
        for (FileUploadOperation localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPaths.get(this.val$location); ; localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPathsEnc.get(this.val$location))
        {
          FileLoader.this.uploadSizes.remove(this.val$location);
          if (localFileUploadOperation != null)
          {
            FileLoader.this.uploadOperationPathsEnc.remove(this.val$location);
            FileLoader.this.uploadOperationQueue.remove(localFileUploadOperation);
            FileLoader.this.uploadSmallOperationQueue.remove(localFileUploadOperation);
            localFileUploadOperation.cancel();
          }
          return;
        }
      }
    });
  }

  public File checkDirectory(int paramInt)
  {
    return (File)this.mediaDirs.get(Integer.valueOf(paramInt));
  }

  public void checkUploadNewDataAvailable(String paramString, boolean paramBoolean, long paramLong)
  {
    this.fileLoaderQueue.postRunnable(new Runnable(paramBoolean, paramString, paramLong)
    {
      public void run()
      {
        FileUploadOperation localFileUploadOperation;
        if (this.val$encrypted)
        {
          localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPathsEnc.get(this.val$location);
          if (localFileUploadOperation == null)
            break label59;
          localFileUploadOperation.checkNewDataAvailable(this.val$finalSize);
        }
        label59: 
        do
        {
          return;
          localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationPaths.get(this.val$location);
          break;
        }
        while (this.val$finalSize == 0L);
        FileLoader.this.uploadSizes.put(this.val$location, Long.valueOf(this.val$finalSize));
      }
    });
  }

  public void deleteFiles(ArrayList<File> paramArrayList, int paramInt)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    this.fileLoaderQueue.postRunnable(new Runnable(paramArrayList, paramInt)
    {
      public void run()
      {
        int i = 0;
        while (true)
          if (i < this.val$files.size())
          {
            File localFile = (File)this.val$files.get(i);
            if (localFile.exists());
            try
            {
              if (!localFile.delete())
                localFile.deleteOnExit();
            }
            catch (Exception localException2)
            {
              try
              {
                while (true)
                {
                  localFile = new File(localFile.getParentFile(), "q_" + localFile.getName());
                  if ((localFile.exists()) && (!localFile.delete()))
                    localFile.deleteOnExit();
                  i += 1;
                  break;
                  localException2 = localException2;
                  FileLog.e(localException2);
                }
              }
              catch (Exception localException1)
              {
                while (true)
                  FileLog.e(localException1);
              }
            }
          }
        if (this.val$type == 2)
          ImageLoader.getInstance().clearMemory();
      }
    });
  }

  public File getDirectory(int paramInt)
  {
    File localFile2 = (File)this.mediaDirs.get(Integer.valueOf(paramInt));
    File localFile1 = localFile2;
    if (localFile2 == null)
    {
      localFile1 = localFile2;
      if (paramInt != 4)
        localFile1 = (File)this.mediaDirs.get(Integer.valueOf(4));
    }
    try
    {
      if (!localFile1.isDirectory())
        localFile1.mkdirs();
      return localFile1;
    }
    catch (Exception localException)
    {
    }
    return localFile1;
  }

  public boolean isLoadingFile(String paramString)
  {
    Semaphore localSemaphore = new Semaphore(0);
    Boolean[] arrayOfBoolean = new Boolean[1];
    this.fileLoaderQueue.postRunnable(new Runnable(arrayOfBoolean, paramString, localSemaphore)
    {
      public void run()
      {
        this.val$result[0] = Boolean.valueOf(FileLoader.access$900(FileLoader.this).containsKey(this.val$fileName));
        this.val$semaphore.release();
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0].booleanValue();
    }
    catch (Exception paramString)
    {
      while (true)
        FileLog.e(paramString);
    }
  }

  public void loadFile(TLRPC.Document paramDocument, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean2) || ((paramDocument != null) && (paramDocument.key != null)));
    for (paramBoolean2 = true; ; paramBoolean2 = false)
    {
      loadFile(paramDocument, null, null, null, 0, paramBoolean1, paramBoolean2);
      return;
    }
  }

  public void loadFile(TLRPC.FileLocation paramFileLocation, String paramString, int paramInt, boolean paramBoolean)
  {
    if ((paramBoolean) || (paramInt == 0) || ((paramFileLocation != null) && (paramFileLocation.key != null)));
    for (paramBoolean = true; ; paramBoolean = false)
    {
      loadFile(null, null, paramFileLocation, paramString, paramInt, true, paramBoolean);
      return;
    }
  }

  public void loadFile(TLRPC.PhotoSize paramPhotoSize, String paramString, boolean paramBoolean)
  {
    TLRPC.FileLocation localFileLocation = paramPhotoSize.location;
    int i = paramPhotoSize.size;
    if ((paramBoolean) || ((paramPhotoSize != null) && (paramPhotoSize.size == 0)) || (paramPhotoSize.location.key != null));
    for (paramBoolean = true; ; paramBoolean = false)
    {
      loadFile(null, null, localFileLocation, paramString, i, false, paramBoolean);
      return;
    }
  }

  public void loadFile(TLRPC.TL_webDocument paramTL_webDocument, boolean paramBoolean1, boolean paramBoolean2)
  {
    loadFile(null, paramTL_webDocument, null, null, 0, paramBoolean1, paramBoolean2);
  }

  public void setDelegate(FileLoaderDelegate paramFileLoaderDelegate)
  {
    this.delegate = paramFileLoaderDelegate;
  }

  public void setMediaDirs(HashMap<Integer, File> paramHashMap)
  {
    this.mediaDirs = paramHashMap;
  }

  public void uploadFile(String paramString, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    uploadFile(paramString, paramBoolean1, paramBoolean2, 0, paramInt);
  }

  public void uploadFile(String paramString, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    if (paramString == null)
      return;
    this.fileLoaderQueue.postRunnable(new Runnable(paramBoolean1, paramString, paramInt1, paramInt2, paramBoolean2)
    {
      public void run()
      {
        if (this.val$encrypted)
        {
          if (!FileLoader.this.uploadOperationPathsEnc.containsKey(this.val$location));
        }
        else
          do
            return;
          while (FileLoader.this.uploadOperationPaths.containsKey(this.val$location));
        int i = this.val$estimatedSize;
        if ((i != 0) && ((Long)FileLoader.this.uploadSizes.get(this.val$location) != null))
        {
          i = 0;
          FileLoader.this.uploadSizes.remove(this.val$location);
        }
        while (true)
        {
          FileUploadOperation localFileUploadOperation = new FileUploadOperation(this.val$location, this.val$encrypted, i, this.val$type);
          if (this.val$encrypted)
            FileLoader.this.uploadOperationPathsEnc.put(this.val$location, localFileUploadOperation);
          while (true)
          {
            localFileUploadOperation.setDelegate(new FileUploadOperation.FileUploadOperationDelegate()
            {
              public void didChangedUploadProgress(FileUploadOperation paramFileUploadOperation, float paramFloat)
              {
                if (FileLoader.this.delegate != null)
                  FileLoader.this.delegate.fileUploadProgressChanged(FileLoader.3.this.val$location, paramFloat, FileLoader.3.this.val$encrypted);
              }

              public void didFailedUploadingFile(FileUploadOperation paramFileUploadOperation)
              {
                FileLoader.this.fileLoaderQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    FileUploadOperation localFileUploadOperation;
                    if (FileLoader.3.this.val$encrypted)
                    {
                      FileLoader.this.uploadOperationPathsEnc.remove(FileLoader.3.this.val$location);
                      if (FileLoader.this.delegate != null)
                        FileLoader.this.delegate.fileDidFailedUpload(FileLoader.3.this.val$location, FileLoader.3.this.val$encrypted);
                      if (!FileLoader.3.this.val$small)
                        break label211;
                      FileLoader.access$510(FileLoader.this);
                      if (FileLoader.this.currentUploadSmallOperationsCount < 1)
                      {
                        localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadSmallOperationQueue.poll();
                        if (localFileUploadOperation != null)
                        {
                          FileLoader.access$508(FileLoader.this);
                          localFileUploadOperation.start();
                        }
                      }
                    }
                    label211: 
                    do
                    {
                      do
                      {
                        return;
                        FileLoader.this.uploadOperationPaths.remove(FileLoader.3.this.val$location);
                        break;
                        FileLoader.access$610(FileLoader.this);
                      }
                      while (FileLoader.this.currentUploadOperationsCount >= 1);
                      localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationQueue.poll();
                    }
                    while (localFileUploadOperation == null);
                    FileLoader.access$608(FileLoader.this);
                    localFileUploadOperation.start();
                  }
                });
              }

              public void didFinishUploadingFile(FileUploadOperation paramFileUploadOperation, TLRPC.InputFile paramInputFile, TLRPC.InputEncryptedFile paramInputEncryptedFile, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
              {
                FileLoader.this.fileLoaderQueue.postRunnable(new Runnable(paramInputFile, paramInputEncryptedFile, paramArrayOfByte1, paramArrayOfByte2, paramFileUploadOperation)
                {
                  public void run()
                  {
                    FileUploadOperation localFileUploadOperation;
                    if (FileLoader.3.this.val$encrypted)
                    {
                      FileLoader.this.uploadOperationPathsEnc.remove(FileLoader.3.this.val$location);
                      if (!FileLoader.3.this.val$small)
                        break label224;
                      FileLoader.access$510(FileLoader.this);
                      if (FileLoader.this.currentUploadSmallOperationsCount < 1)
                      {
                        localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadSmallOperationQueue.poll();
                        if (localFileUploadOperation != null)
                        {
                          FileLoader.access$508(FileLoader.this);
                          localFileUploadOperation.start();
                        }
                      }
                    }
                    while (true)
                    {
                      if (FileLoader.this.delegate != null)
                        FileLoader.this.delegate.fileDidUploaded(FileLoader.3.this.val$location, this.val$inputFile, this.val$inputEncryptedFile, this.val$key, this.val$iv, this.val$operation.getTotalFileSize());
                      return;
                      FileLoader.this.uploadOperationPaths.remove(FileLoader.3.this.val$location);
                      break;
                      label224: FileLoader.access$610(FileLoader.this);
                      if (FileLoader.this.currentUploadOperationsCount >= 1)
                        continue;
                      localFileUploadOperation = (FileUploadOperation)FileLoader.this.uploadOperationQueue.poll();
                      if (localFileUploadOperation == null)
                        continue;
                      FileLoader.access$608(FileLoader.this);
                      localFileUploadOperation.start();
                    }
                  }
                });
              }
            });
            if (!this.val$small)
              break;
            if (FileLoader.this.currentUploadSmallOperationsCount < 1)
            {
              FileLoader.access$508(FileLoader.this);
              localFileUploadOperation.start();
              return;
              FileLoader.this.uploadOperationPaths.put(this.val$location, localFileUploadOperation);
              continue;
            }
            FileLoader.this.uploadSmallOperationQueue.add(localFileUploadOperation);
            return;
          }
          if (FileLoader.this.currentUploadOperationsCount < 1)
          {
            FileLoader.access$608(FileLoader.this);
            localFileUploadOperation.start();
            return;
          }
          FileLoader.this.uploadOperationQueue.add(localFileUploadOperation);
          return;
        }
      }
    });
  }

  public static abstract interface FileLoaderDelegate
  {
    public abstract void fileDidFailedLoad(String paramString, int paramInt);

    public abstract void fileDidFailedUpload(String paramString, boolean paramBoolean);

    public abstract void fileDidLoaded(String paramString, File paramFile, int paramInt);

    public abstract void fileDidUploaded(String paramString, TLRPC.InputFile paramInputFile, TLRPC.InputEncryptedFile paramInputEncryptedFile, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long paramLong);

    public abstract void fileLoadProgressChanged(String paramString, float paramFloat);

    public abstract void fileUploadProgressChanged(String paramString, float paramFloat, boolean paramBoolean);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.FileLoader
 * JD-Core Version:    0.6.0
 */