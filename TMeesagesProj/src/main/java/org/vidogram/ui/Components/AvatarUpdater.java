package org.vidogram.ui.Components;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.MediaController.PhotoEntry;
import org.vidogram.messenger.MediaController.SearchImage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputDocument;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.LaunchActivity;
import org.vidogram.ui.PhotoAlbumPickerActivity;
import org.vidogram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.vidogram.ui.PhotoCropActivity;
import org.vidogram.ui.PhotoCropActivity.PhotoEditActivityDelegate;
import org.vidogram.ui.PhotoViewer;
import org.vidogram.ui.PhotoViewer.EmptyPhotoViewerProvider;

public class AvatarUpdater
  implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate
{
  private TLRPC.PhotoSize bigPhoto;
  private boolean clearAfterUpdate = false;
  public String currentPicturePath;
  public AvatarUpdaterDelegate delegate;
  public BaseFragment parentFragment = null;
  File picturePath = null;
  public boolean returnOnly = false;
  private TLRPC.PhotoSize smallPhoto;
  public String uploadingAvatar = null;

  private void processBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap == null);
    while (true)
    {
      return;
      this.smallPhoto = ImageLoader.scaleAndSaveImage(paramBitmap, 100.0F, 100.0F, 80, false);
      this.bigPhoto = ImageLoader.scaleAndSaveImage(paramBitmap, 800.0F, 800.0F, 80, false, 320, 320);
      paramBitmap.recycle();
      if ((this.bigPhoto == null) || (this.smallPhoto == null))
        continue;
      if (!this.returnOnly)
        break;
      if (this.delegate == null)
        continue;
      this.delegate.didUploadedPhoto(null, this.smallPhoto, this.bigPhoto);
      return;
    }
    UserConfig.saveConfig(false);
    this.uploadingAvatar = (FileLoader.getInstance().getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg");
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
    FileLoader.getInstance().uploadFile(this.uploadingAvatar, false, true, 16777216);
  }

  private void startCrop(String paramString, Uri paramUri)
  {
    while (true)
    {
      Object localObject;
      try
      {
        LaunchActivity localLaunchActivity = (LaunchActivity)this.parentFragment.getParentActivity();
        if (localLaunchActivity == null)
          return;
        localObject = new Bundle();
        if (paramString != null)
        {
          ((Bundle)localObject).putString("photoPath", paramString);
          localObject = new PhotoCropActivity((Bundle)localObject);
          ((PhotoCropActivity)localObject).setDelegate(this);
          localLaunchActivity.presentFragment((BaseFragment)localObject);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        processBitmap(ImageLoader.loadBitmap(paramString, paramUri, 800.0F, 800.0F, true));
        return;
      }
      if (paramUri == null)
        continue;
      ((Bundle)localObject).putParcelable("photoUri", paramUri);
    }
  }

  public void clear()
  {
    if (this.uploadingAvatar != null)
    {
      this.clearAfterUpdate = true;
      return;
    }
    this.parentFragment = null;
    this.delegate = null;
  }

  public void didFinishEdit(Bitmap paramBitmap)
  {
    processBitmap(paramBitmap);
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.FileDidUpload)
    {
      String str = (String)paramArrayOfObject[0];
      if ((this.uploadingAvatar != null) && (str.equals(this.uploadingAvatar)))
      {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailUpload);
        if (this.delegate != null)
          this.delegate.didUploadedPhoto((TLRPC.InputFile)paramArrayOfObject[1], this.smallPhoto, this.bigPhoto);
        this.uploadingAvatar = null;
        if (this.clearAfterUpdate)
        {
          this.parentFragment = null;
          this.delegate = null;
        }
      }
    }
    do
    {
      do
      {
        do
          return;
        while (paramInt != NotificationCenter.FileDidFailUpload);
        paramArrayOfObject = (String)paramArrayOfObject[0];
      }
      while ((this.uploadingAvatar == null) || (!paramArrayOfObject.equals(this.uploadingAvatar)));
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidUpload);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailUpload);
      this.uploadingAvatar = null;
    }
    while (!this.clearAfterUpdate);
    this.parentFragment = null;
    this.delegate = null;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 == -1)
    {
      if (paramInt1 != 13)
        break label174;
      PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
    }
    label174: 
    do
      try
      {
        paramInt1 = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
        switch (paramInt1)
        {
        case 4:
        case 5:
        case 7:
        default:
          paramInt1 = 0;
        case 6:
        case 3:
        case 8:
        }
        while (true)
        {
          paramIntent = new ArrayList();
          paramIntent.add(new MediaController.PhotoEntry(0, 0, 0L, this.currentPicturePath, paramInt1, false));
          PhotoViewer.getInstance().openPhotoForSelect(paramIntent, 0, 1, new PhotoViewer.EmptyPhotoViewerProvider(paramIntent)
          {
            public boolean allowCaption()
            {
              return false;
            }

            public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
            {
              paramVideoEditedInfo = (MediaController.PhotoEntry)this.val$arrayList.get(0);
              if (paramVideoEditedInfo.imagePath != null)
                paramVideoEditedInfo = paramVideoEditedInfo.imagePath;
              while (true)
              {
                paramVideoEditedInfo = ImageLoader.loadBitmap(paramVideoEditedInfo, null, 800.0F, 800.0F, true);
                AvatarUpdater.this.processBitmap(paramVideoEditedInfo);
                return;
                if (paramVideoEditedInfo.path != null)
                {
                  paramVideoEditedInfo = paramVideoEditedInfo.path;
                  continue;
                }
                paramVideoEditedInfo = null;
              }
            }
          }
          , null);
          AndroidUtilities.addMediaToGallery(this.currentPicturePath);
          this.currentPicturePath = null;
          return;
          paramInt1 = 90;
          continue;
          paramInt1 = 180;
          continue;
          paramInt1 = 270;
        }
      }
      catch (Exception paramIntent)
      {
        while (true)
        {
          FileLog.e(paramIntent);
          paramInt1 = 0;
        }
      }
    while ((paramInt1 != 14) || (paramIntent == null) || (paramIntent.getData() == null));
    startCrop(null, paramIntent.getData());
  }

  public void openCamera()
  {
    try
    {
      Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
      File localFile = AndroidUtilities.generatePicturePath();
      if (localFile != null)
      {
        if (Build.VERSION.SDK_INT < 24)
          break label80;
        localIntent.putExtra("output", FileProvider.a(this.parentFragment.getParentActivity(), "org.vidogram.messenger.provider", localFile));
        localIntent.addFlags(2);
        localIntent.addFlags(1);
      }
      while (true)
      {
        this.currentPicturePath = localFile.getAbsolutePath();
        this.parentFragment.startActivityForResult(localIntent, 13);
        return;
        label80: localIntent.putExtra("output", Uri.fromFile(localFile));
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void openGallery()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (this.parentFragment != null) && (this.parentFragment.getParentActivity() != null) && (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
    {
      this.parentFragment.getParentActivity().requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 4);
      return;
    }
    PhotoAlbumPickerActivity localPhotoAlbumPickerActivity = new PhotoAlbumPickerActivity(true, false, false, null);
    localPhotoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate()
    {
      public void didSelectPhotos(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayList<ArrayList<TLRPC.InputDocument>> paramArrayList, ArrayList<MediaController.SearchImage> paramArrayList3)
      {
        if (!paramArrayList1.isEmpty())
        {
          paramArrayList1 = ImageLoader.loadBitmap((String)paramArrayList1.get(0), null, 800.0F, 800.0F, true);
          AvatarUpdater.this.processBitmap(paramArrayList1);
        }
      }

      public void didSelectVideo(String paramString1, VideoEditedInfo paramVideoEditedInfo, long paramLong1, long paramLong2, String paramString2)
      {
      }

      public void startPhotoSelectActivity()
      {
        try
        {
          Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
          localIntent.setType("image/*");
          AvatarUpdater.this.parentFragment.startActivityForResult(localIntent, 14);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
    this.parentFragment.presentFragment(localPhotoAlbumPickerActivity);
  }

  public static abstract interface AvatarUpdaterDelegate
  {
    public abstract void didUploadedPhoto(TLRPC.InputFile paramInputFile, TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.AvatarUpdater
 * JD-Core Version:    0.6.0
 */