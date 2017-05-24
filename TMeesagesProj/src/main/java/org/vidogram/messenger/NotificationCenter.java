package org.vidogram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;

public class NotificationCenter
{
  public static final int FileDidFailUpload;
  public static final int FileDidFailedLoad;
  public static final int FileDidLoaded;
  public static final int FileDidUpload;
  public static final int FileLoadProgressChanged;
  public static final int FileNewChunkAvailable;
  public static final int FilePreparingFailed;
  public static final int FilePreparingStarted;
  public static final int FileUploadProgressChanged;
  private static volatile NotificationCenter Instance;
  public static final int albumsDidLoaded;
  public static final int appDidLogout;
  public static final int archivedStickersCountDidLoaded;
  public static final int audioDidReset;
  public static final int audioDidSent;
  public static final int audioDidStarted;
  public static final int audioPlayStateChanged;
  public static final int audioProgressDidChanged;
  public static final int audioRouteChanged;
  public static final int blockedUsersDidLoaded;
  public static final int botInfoDidLoaded;
  public static final int botKeyboardDidLoaded;
  public static final int cameraInitied;
  public static final int chatDidCreated;
  public static final int chatDidFailCreate;
  public static final int chatInfoCantLoad;
  public static final int chatInfoDidLoaded;
  public static final int chatSearchResultsAvailable;
  public static final int closeChats;
  public static final int closeInCallActivity;
  public static final int closeOtherAppActivities;
  public static final int contactsDidLoaded;
  public static final int dialogPhotosLoaded;
  public static final int dialogsNeedReload;
  public static final int didCreatedNewDeleteTask;
  public static final int didEndedCall;
  public static final int didLoadedPinnedMessage;
  public static final int didLoadedReplyMessages;
  public static final int didReceiveCall;
  public static final int didReceiveSmsCode;
  public static final int didReceivedNewMessages;
  public static final int didReceivedWebpages;
  public static final int didReceivedWebpagesInUpdates;
  public static final int didRemovedTwoStepPassword;
  public static final int didReplacedPhotoInMemCache;
  public static final int didSetNewWallpapper;
  public static final int didSetPasscode;
  public static final int didSetTwoStepPassword;
  public static final int didStartedCall;
  public static final int didUpdatedConnectionState;
  public static final int didUpdatedMessagesViews;
  public static final int emojiDidLoaded;
  public static final int encryptedChatCreated;
  public static final int encryptedChatUpdated;
  public static final int featuredStickersDidLoaded;
  public static final int httpFileDidFailedLoad;
  public static final int httpFileDidLoaded;
  public static final int locationPermissionGranted;
  public static final int mainUserInfoChanged;
  public static final int mediaCountDidLoaded;
  public static final int mediaDidLoaded;
  public static final int messageReceivedByAck;
  public static final int messageReceivedByServer;
  public static final int messageSendError;
  public static final int messageThumbGenerated;
  public static final int messagesDeleted;
  public static final int messagesDidLoaded;
  public static final int messagesRead;
  public static final int messagesReadContent;
  public static final int messagesReadEncrypted;
  public static final int musicDidLoaded;
  public static final int needReloadArchivedStickers;
  public static final int needReloadRecentDialogsSearch;
  public static final int needShowAlert;
  public static final int newDraftReceived;
  public static final int newSessionReceived;
  public static final int notificationsSettingsUpdated;
  public static final int openedChatChanged;
  public static final int paymentFinished;
  public static final int peerSettingsDidLoaded;
  public static final int privacyRulesUpdated;
  public static final int pushMessagesUpdated;
  public static final int recentDocumentsDidLoaded;
  public static final int recentImagesDidLoaded;
  public static final int recordProgressChanged;
  public static final int recordStartError;
  public static final int recordStarted;
  public static final int recordStopped;
  public static final int reloadHints;
  public static final int reloadInlineHints;
  public static final int removeAllMessagesFromDialog;
  public static final int replaceMessagesObjects;
  public static final int screenStateChanged;
  public static final int screenshotTook;
  public static final int stickersDidLoaded;
  public static final int stopEncodingService;
  private static int totalEvents = 1;
  public static final int updateInterfaces;
  public static final int updateMessageMedia;
  public static final int userInfoDidLoaded;
  public static final int wallpapersDidLoaded;
  public static final int wasUnableToFindCurrentLocation;
  private SparseArray<ArrayList<Object>> addAfterBroadcast = new SparseArray();
  private int[] allowedNotifications;
  private boolean animationInProgress;
  private int broadcasting = 0;
  private ArrayList<DelayedPost> delayedPosts = new ArrayList(10);
  private SparseArray<ArrayList<Object>> observers = new SparseArray();
  private SparseArray<ArrayList<Object>> removeAfterBroadcast = new SparseArray();

  static
  {
    int i = totalEvents;
    totalEvents = i + 1;
    didReceivedNewMessages = i;
    i = totalEvents;
    totalEvents = i + 1;
    updateInterfaces = i;
    i = totalEvents;
    totalEvents = i + 1;
    dialogsNeedReload = i;
    i = totalEvents;
    totalEvents = i + 1;
    closeChats = i;
    i = totalEvents;
    totalEvents = i + 1;
    messagesDeleted = i;
    i = totalEvents;
    totalEvents = i + 1;
    messagesRead = i;
    i = totalEvents;
    totalEvents = i + 1;
    messagesDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    messageReceivedByAck = i;
    i = totalEvents;
    totalEvents = i + 1;
    messageReceivedByServer = i;
    i = totalEvents;
    totalEvents = i + 1;
    messageSendError = i;
    i = totalEvents;
    totalEvents = i + 1;
    contactsDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    chatDidCreated = i;
    i = totalEvents;
    totalEvents = i + 1;
    chatDidFailCreate = i;
    i = totalEvents;
    totalEvents = i + 1;
    chatInfoDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    chatInfoCantLoad = i;
    i = totalEvents;
    totalEvents = i + 1;
    mediaDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    mediaCountDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    encryptedChatUpdated = i;
    i = totalEvents;
    totalEvents = i + 1;
    messagesReadEncrypted = i;
    i = totalEvents;
    totalEvents = i + 1;
    encryptedChatCreated = i;
    i = totalEvents;
    totalEvents = i + 1;
    dialogPhotosLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    removeAllMessagesFromDialog = i;
    i = totalEvents;
    totalEvents = i + 1;
    notificationsSettingsUpdated = i;
    i = totalEvents;
    totalEvents = i + 1;
    pushMessagesUpdated = i;
    i = totalEvents;
    totalEvents = i + 1;
    blockedUsersDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    openedChatChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    stopEncodingService = i;
    i = totalEvents;
    totalEvents = i + 1;
    didCreatedNewDeleteTask = i;
    i = totalEvents;
    totalEvents = i + 1;
    mainUserInfoChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    privacyRulesUpdated = i;
    i = totalEvents;
    totalEvents = i + 1;
    updateMessageMedia = i;
    i = totalEvents;
    totalEvents = i + 1;
    recentImagesDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    replaceMessagesObjects = i;
    i = totalEvents;
    totalEvents = i + 1;
    didSetPasscode = i;
    i = totalEvents;
    totalEvents = i + 1;
    didSetTwoStepPassword = i;
    i = totalEvents;
    totalEvents = i + 1;
    didRemovedTwoStepPassword = i;
    i = totalEvents;
    totalEvents = i + 1;
    screenStateChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    didLoadedReplyMessages = i;
    i = totalEvents;
    totalEvents = i + 1;
    didLoadedPinnedMessage = i;
    i = totalEvents;
    totalEvents = i + 1;
    newSessionReceived = i;
    i = totalEvents;
    totalEvents = i + 1;
    didReceivedWebpages = i;
    i = totalEvents;
    totalEvents = i + 1;
    didReceivedWebpagesInUpdates = i;
    i = totalEvents;
    totalEvents = i + 1;
    stickersDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    featuredStickersDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    didReplacedPhotoInMemCache = i;
    i = totalEvents;
    totalEvents = i + 1;
    messagesReadContent = i;
    i = totalEvents;
    totalEvents = i + 1;
    botInfoDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    userInfoDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    botKeyboardDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    chatSearchResultsAvailable = i;
    i = totalEvents;
    totalEvents = i + 1;
    musicDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    needShowAlert = i;
    i = totalEvents;
    totalEvents = i + 1;
    didUpdatedMessagesViews = i;
    i = totalEvents;
    totalEvents = i + 1;
    needReloadRecentDialogsSearch = i;
    i = totalEvents;
    totalEvents = i + 1;
    locationPermissionGranted = i;
    i = totalEvents;
    totalEvents = i + 1;
    peerSettingsDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    wasUnableToFindCurrentLocation = i;
    i = totalEvents;
    totalEvents = i + 1;
    reloadHints = i;
    i = totalEvents;
    totalEvents = i + 1;
    reloadInlineHints = i;
    i = totalEvents;
    totalEvents = i + 1;
    newDraftReceived = i;
    i = totalEvents;
    totalEvents = i + 1;
    recentDocumentsDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    cameraInitied = i;
    i = totalEvents;
    totalEvents = i + 1;
    needReloadArchivedStickers = i;
    i = totalEvents;
    totalEvents = i + 1;
    didSetNewWallpapper = i;
    i = totalEvents;
    totalEvents = i + 1;
    archivedStickersCountDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    paymentFinished = i;
    i = totalEvents;
    totalEvents = i + 1;
    httpFileDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    httpFileDidFailedLoad = i;
    i = totalEvents;
    totalEvents = i + 1;
    messageThumbGenerated = i;
    i = totalEvents;
    totalEvents = i + 1;
    wallpapersDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    closeOtherAppActivities = i;
    i = totalEvents;
    totalEvents = i + 1;
    didUpdatedConnectionState = i;
    i = totalEvents;
    totalEvents = i + 1;
    didReceiveSmsCode = i;
    i = totalEvents;
    totalEvents = i + 1;
    didReceiveCall = i;
    i = totalEvents;
    totalEvents = i + 1;
    emojiDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    appDidLogout = i;
    i = totalEvents;
    totalEvents = i + 1;
    FileDidUpload = i;
    i = totalEvents;
    totalEvents = i + 1;
    FileDidFailUpload = i;
    i = totalEvents;
    totalEvents = i + 1;
    FileUploadProgressChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    FileLoadProgressChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    FileDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    FileDidFailedLoad = i;
    i = totalEvents;
    totalEvents = i + 1;
    FilePreparingStarted = i;
    i = totalEvents;
    totalEvents = i + 1;
    FileNewChunkAvailable = i;
    i = totalEvents;
    totalEvents = i + 1;
    FilePreparingFailed = i;
    i = totalEvents;
    totalEvents = i + 1;
    audioProgressDidChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    audioDidReset = i;
    i = totalEvents;
    totalEvents = i + 1;
    audioPlayStateChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    recordProgressChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    recordStarted = i;
    i = totalEvents;
    totalEvents = i + 1;
    recordStartError = i;
    i = totalEvents;
    totalEvents = i + 1;
    recordStopped = i;
    i = totalEvents;
    totalEvents = i + 1;
    screenshotTook = i;
    i = totalEvents;
    totalEvents = i + 1;
    albumsDidLoaded = i;
    i = totalEvents;
    totalEvents = i + 1;
    audioDidSent = i;
    i = totalEvents;
    totalEvents = i + 1;
    audioDidStarted = i;
    i = totalEvents;
    totalEvents = i + 1;
    audioRouteChanged = i;
    i = totalEvents;
    totalEvents = i + 1;
    didStartedCall = i;
    i = totalEvents;
    totalEvents = i + 1;
    didEndedCall = i;
    i = totalEvents;
    totalEvents = i + 1;
    closeInCallActivity = i;
    Instance = null;
  }

  public static NotificationCenter getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        NotificationCenter localNotificationCenter = Instance;
        localObject1 = localNotificationCenter;
        if (localNotificationCenter == null)
        {
          localObject1 = new NotificationCenter();
          Instance = (NotificationCenter)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (NotificationCenter)localObject2;
  }

  public void addObserver(Object paramObject, int paramInt)
  {
    if ((BuildVars.DEBUG_VERSION) && (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()))
      throw new RuntimeException("addObserver allowed only from MAIN thread");
    Object localObject2;
    Object localObject1;
    if (this.broadcasting != 0)
    {
      localObject2 = (ArrayList)this.addAfterBroadcast.get(paramInt);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayList();
        this.addAfterBroadcast.put(paramInt, localObject1);
      }
      ((ArrayList)localObject1).add(paramObject);
    }
    do
    {
      return;
      localObject2 = (ArrayList)this.observers.get(paramInt);
      localObject1 = localObject2;
      if (localObject2 != null)
        continue;
      localObject2 = this.observers;
      localObject1 = new ArrayList();
      ((SparseArray)localObject2).put(paramInt, localObject1);
    }
    while (((ArrayList)localObject1).contains(paramObject));
    ((ArrayList)localObject1).add(paramObject);
  }

  public boolean isAnimationInProgress()
  {
    return this.animationInProgress;
  }

  public void postNotificationName(int paramInt, Object[] paramArrayOfObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    int i;
    if (this.allowedNotifications != null)
      i = 0;
    while (true)
    {
      bool1 = bool2;
      if (i < this.allowedNotifications.length)
      {
        if (this.allowedNotifications[i] == paramInt)
          bool1 = true;
      }
      else
      {
        postNotificationNameInternal(paramInt, bool1, paramArrayOfObject);
        return;
      }
      i += 1;
    }
  }

  public void postNotificationNameInternal(int paramInt, boolean paramBoolean, Object[] paramArrayOfObject)
  {
    if ((BuildVars.DEBUG_VERSION) && (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()))
      throw new RuntimeException("postNotificationName allowed only from MAIN thread");
    Object localObject;
    if ((!paramBoolean) && (this.animationInProgress))
    {
      localObject = new DelayedPost(paramInt, paramArrayOfObject, null);
      this.delayedPosts.add(localObject);
      if (BuildVars.DEBUG_VERSION)
        FileLog.e("delay post notification " + paramInt + " with args count = " + paramArrayOfObject.length);
    }
    int i;
    int j;
    do
    {
      do
      {
        return;
        this.broadcasting += 1;
        localObject = (ArrayList)this.observers.get(paramInt);
        if ((localObject != null) && (!((ArrayList)localObject).isEmpty()))
        {
          i = 0;
          while (i < ((ArrayList)localObject).size())
          {
            ((NotificationCenterDelegate)((ArrayList)localObject).get(i)).didReceivedNotification(paramInt, paramArrayOfObject);
            i += 1;
          }
        }
        this.broadcasting -= 1;
      }
      while (this.broadcasting != 0);
      if (this.removeAfterBroadcast.size() == 0)
        continue;
      paramInt = 0;
      while (paramInt < this.removeAfterBroadcast.size())
      {
        j = this.removeAfterBroadcast.keyAt(paramInt);
        paramArrayOfObject = (ArrayList)this.removeAfterBroadcast.get(j);
        i = 0;
        while (i < paramArrayOfObject.size())
        {
          removeObserver(paramArrayOfObject.get(i), j);
          i += 1;
        }
        paramInt += 1;
      }
      this.removeAfterBroadcast.clear();
    }
    while (this.addAfterBroadcast.size() == 0);
    paramInt = 0;
    while (paramInt < this.addAfterBroadcast.size())
    {
      j = this.addAfterBroadcast.keyAt(paramInt);
      paramArrayOfObject = (ArrayList)this.addAfterBroadcast.get(j);
      i = 0;
      while (i < paramArrayOfObject.size())
      {
        addObserver(paramArrayOfObject.get(i), j);
        i += 1;
      }
      paramInt += 1;
    }
    this.addAfterBroadcast.clear();
  }

  public void removeObserver(Object paramObject, int paramInt)
  {
    if ((BuildVars.DEBUG_VERSION) && (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()))
      throw new RuntimeException("removeObserver allowed only from MAIN thread");
    ArrayList localArrayList1;
    if (this.broadcasting != 0)
    {
      ArrayList localArrayList2 = (ArrayList)this.removeAfterBroadcast.get(paramInt);
      localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.removeAfterBroadcast.put(paramInt, localArrayList1);
      }
      localArrayList1.add(paramObject);
    }
    do
    {
      return;
      localArrayList1 = (ArrayList)this.observers.get(paramInt);
    }
    while (localArrayList1 == null);
    localArrayList1.remove(paramObject);
  }

  public void setAllowedNotificationsDutingAnimation(int[] paramArrayOfInt)
  {
    this.allowedNotifications = paramArrayOfInt;
  }

  public void setAnimationInProgress(boolean paramBoolean)
  {
    this.animationInProgress = paramBoolean;
    if ((!this.animationInProgress) && (!this.delayedPosts.isEmpty()))
    {
      Iterator localIterator = this.delayedPosts.iterator();
      while (localIterator.hasNext())
      {
        DelayedPost localDelayedPost = (DelayedPost)localIterator.next();
        postNotificationNameInternal(localDelayedPost.id, true, localDelayedPost.args);
      }
      this.delayedPosts.clear();
    }
  }

  private class DelayedPost
  {
    private Object[] args;
    private int id;

    private DelayedPost(int paramArrayOfObject, Object[] arg3)
    {
      this.id = paramArrayOfObject;
      Object localObject;
      this.args = localObject;
    }
  }

  public static abstract interface NotificationCenterDelegate
  {
    public abstract void didReceivedNotification(int paramInt, Object[] paramArrayOfObject);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.NotificationCenter
 * JD-Core Version:    0.6.0
 */