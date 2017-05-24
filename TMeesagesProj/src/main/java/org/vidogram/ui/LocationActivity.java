package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.b;
import com.google.android.gms.maps.c;
import com.google.android.gms.maps.c.a;
import com.google.android.gms.maps.d;
import com.google.android.gms.maps.f;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.GeoPoint;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.TL_geoPoint;
import org.vidogram.tgnet.TLRPC.TL_messageFwdHeader;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGeo;
import org.vidogram.tgnet.TLRPC.TL_messageMediaVenue;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;
import org.vidogram.ui.Adapters.LocationActivityAdapter;
import org.vidogram.ui.Adapters.LocationActivitySearchAdapter;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.LocationCell;
import org.vidogram.ui.Cells.LocationLoadingCell;
import org.vidogram.ui.Cells.LocationPoweredCell;
import org.vidogram.ui.Cells.SendLocationCell;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.MapPlaceholderDrawable;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;

public class LocationActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int map_list_menu_hybrid = 4;
  private static final int map_list_menu_map = 2;
  private static final int map_list_menu_satellite = 3;
  private static final int share = 1;
  private LocationActivityAdapter adapter;
  private AnimatorSet animatorSet;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImageView;
  private FrameLayout bottomView;
  private boolean checkPermission = true;
  private com.google.android.gms.maps.model.e circleOptions;
  private LocationActivityDelegate delegate;
  private TextView distanceTextView;
  private EmptyTextProgressView emptyView;
  private boolean firstWas = false;
  private c googleMap;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private ImageView locationButton;
  private d mapView;
  private FrameLayout mapViewClip;
  private boolean mapsInitialized;
  private ImageView markerImageView;
  private int markerTop;
  private ImageView markerXImageView;
  private MessageObject messageObject;
  private Location myLocation;
  private TextView nameTextView;
  private boolean onResumeCalled;
  private int overScrollHeight = AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(66.0F);
  private ImageView routeButton;
  private LocationActivitySearchAdapter searchAdapter;
  private RecyclerListView searchListView;
  private boolean searchWas;
  private boolean searching;
  private Location userLocation;
  private boolean userLocationMoved = false;
  private boolean wasResults;

  private void fixLayoutInternal(boolean paramBoolean)
  {
    if (this.listView != null)
      if (!this.actionBar.getOccupyStatusBar())
        break label40;
    int j;
    label40: for (int i = AndroidUtilities.statusBarHeight; ; i = 0)
    {
      i = ActionBar.getCurrentActionBarHeight() + i;
      j = this.fragmentView.getMeasuredHeight();
      if (j != 0)
        break;
      return;
    }
    this.overScrollHeight = (j - AndroidUtilities.dp(66.0F) - i);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    localLayoutParams.topMargin = i;
    this.listView.setLayoutParams(localLayoutParams);
    localLayoutParams = (FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams();
    localLayoutParams.topMargin = i;
    localLayoutParams.height = this.overScrollHeight;
    this.mapViewClip.setLayoutParams(localLayoutParams);
    localLayoutParams = (FrameLayout.LayoutParams)this.searchListView.getLayoutParams();
    localLayoutParams.topMargin = i;
    this.searchListView.setLayoutParams(localLayoutParams);
    this.adapter.setOverScrollHeight(this.overScrollHeight);
    localLayoutParams = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
    if (localLayoutParams != null)
    {
      localLayoutParams.height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
      if (this.googleMap != null)
        this.googleMap.a(0, 0, 0, AndroidUtilities.dp(10.0F));
      this.mapView.setLayoutParams(localLayoutParams);
    }
    this.adapter.notifyDataSetChanged();
    if (paramBoolean)
    {
      this.layoutManager.scrollToPositionWithOffset(0, -(int)(AndroidUtilities.dp(56.0F) * 2.5F + AndroidUtilities.dp(102.0F)));
      updateClipView(this.layoutManager.findFirstVisibleItemPosition());
      this.listView.post(new Runnable()
      {
        public void run()
        {
          LocationActivity.this.layoutManager.scrollToPositionWithOffset(0, -(int)(AndroidUtilities.dp(56.0F) * 2.5F + AndroidUtilities.dp(102.0F)));
          LocationActivity.this.updateClipView(LocationActivity.this.layoutManager.findFirstVisibleItemPosition());
        }
      });
      return;
    }
    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
  }

  private Location getLastLocation()
  {
    LocationManager localLocationManager = (LocationManager)ApplicationLoader.applicationContext.getSystemService("location");
    List localList = localLocationManager.getProviders(true);
    int i = localList.size();
    Location localLocation = null;
    i -= 1;
    while (i >= 0)
    {
      localLocation = localLocationManager.getLastKnownLocation((String)localList.get(i));
      if (localLocation != null)
        return localLocation;
      i -= 1;
    }
    return localLocation;
  }

  // ERROR //
  private void onMapInit()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 165	org/vidogram/ui/LocationActivity:messageObject	Lorg/vidogram/messenger/MessageObject;
    //   12: ifnull +157 -> 169
    //   15: new 400	com/google/android/gms/maps/model/LatLng
    //   18: dup
    //   19: aload_0
    //   20: getfield 280	org/vidogram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   23: invokevirtual 406	android/location/Location:getLatitude	()D
    //   26: aload_0
    //   27: getfield 280	org/vidogram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   30: invokevirtual 409	android/location/Location:getLongitude	()D
    //   33: invokespecial 412	com/google/android/gms/maps/model/LatLng:<init>	(DD)V
    //   36: astore_1
    //   37: aload_0
    //   38: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   41: new 414	com/google/android/gms/maps/model/i
    //   44: dup
    //   45: invokespecial 415	com/google/android/gms/maps/model/i:<init>	()V
    //   48: aload_1
    //   49: invokevirtual 418	com/google/android/gms/maps/model/i:a	(Lcom/google/android/gms/maps/model/LatLng;)Lcom/google/android/gms/maps/model/i;
    //   52: ldc_w 419
    //   55: invokestatic 424	com/google/android/gms/maps/model/b:a	(I)Lcom/google/android/gms/maps/model/a;
    //   58: invokevirtual 427	com/google/android/gms/maps/model/i:a	(Lcom/google/android/gms/maps/model/a;)Lcom/google/android/gms/maps/model/i;
    //   61: invokevirtual 430	com/google/android/gms/maps/c:a	(Lcom/google/android/gms/maps/model/i;)Lcom/google/android/gms/maps/model/h;
    //   64: pop
    //   65: aload_1
    //   66: aload_0
    //   67: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   70: invokevirtual 434	com/google/android/gms/maps/c:b	()F
    //   73: ldc_w 435
    //   76: fsub
    //   77: invokestatic 440	com/google/android/gms/maps/b:a	(Lcom/google/android/gms/maps/model/LatLng;F)Lcom/google/android/gms/maps/a;
    //   80: astore_1
    //   81: aload_0
    //   82: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   85: aload_1
    //   86: invokevirtual 443	com/google/android/gms/maps/c:a	(Lcom/google/android/gms/maps/a;)V
    //   89: aload_0
    //   90: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   93: iconst_1
    //   94: invokevirtual 445	com/google/android/gms/maps/c:a	(Z)V
    //   97: aload_0
    //   98: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   101: invokevirtual 449	com/google/android/gms/maps/c:c	()Lcom/google/android/gms/maps/g;
    //   104: iconst_0
    //   105: invokevirtual 453	com/google/android/gms/maps/g:c	(Z)V
    //   108: aload_0
    //   109: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   112: invokevirtual 449	com/google/android/gms/maps/c:c	()Lcom/google/android/gms/maps/g;
    //   115: iconst_0
    //   116: invokevirtual 454	com/google/android/gms/maps/g:a	(Z)V
    //   119: aload_0
    //   120: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   123: invokevirtual 449	com/google/android/gms/maps/c:c	()Lcom/google/android/gms/maps/g;
    //   126: iconst_0
    //   127: invokevirtual 456	com/google/android/gms/maps/g:b	(Z)V
    //   130: aload_0
    //   131: getfield 159	org/vidogram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/c;
    //   134: new 30	org/vidogram/ui/LocationActivity$18
    //   137: dup
    //   138: aload_0
    //   139: invokespecial 457	org/vidogram/ui/LocationActivity$18:<init>	(Lorg/vidogram/ui/LocationActivity;)V
    //   142: invokevirtual 460	com/google/android/gms/maps/c:a	(Lcom/google/android/gms/maps/c$a;)V
    //   145: aload_0
    //   146: invokespecial 462	org/vidogram/ui/LocationActivity:getLastLocation	()Landroid/location/Location;
    //   149: astore_1
    //   150: aload_0
    //   151: aload_1
    //   152: putfield 196	org/vidogram/ui/LocationActivity:myLocation	Landroid/location/Location;
    //   155: aload_0
    //   156: aload_1
    //   157: invokespecial 250	org/vidogram/ui/LocationActivity:positionMarker	(Landroid/location/Location;)V
    //   160: return
    //   161: astore_2
    //   162: aload_2
    //   163: invokestatic 468	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   166: goto -101 -> 65
    //   169: aload_0
    //   170: new 402	android/location/Location
    //   173: dup
    //   174: ldc_w 470
    //   177: invokespecial 473	android/location/Location:<init>	(Ljava/lang/String;)V
    //   180: putfield 280	org/vidogram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   183: aload_0
    //   184: getfield 280	org/vidogram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   187: ldc2_w 474
    //   190: invokevirtual 479	android/location/Location:setLatitude	(D)V
    //   193: aload_0
    //   194: getfield 280	org/vidogram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   197: ldc2_w 480
    //   200: invokevirtual 484	android/location/Location:setLongitude	(D)V
    //   203: goto -114 -> 89
    //   206: astore_1
    //   207: aload_1
    //   208: invokestatic 468	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   211: goto -114 -> 97
    //
    // Exception table:
    //   from	to	target	type
    //   37	65	161	java/lang/Exception
    //   89	97	206	java/lang/Exception
  }

  private void positionMarker(Location paramLocation)
  {
    if (paramLocation == null);
    LatLng localLatLng;
    do
    {
      do
        while (true)
        {
          return;
          this.myLocation = new Location(paramLocation);
          if (this.messageObject == null)
            break;
          if ((this.userLocation == null) || (this.distanceTextView == null))
            continue;
          float f = paramLocation.distanceTo(this.userLocation);
          if (f < 1000.0F)
          {
            this.distanceTextView.setText(String.format("%d %s", new Object[] { Integer.valueOf((int)f), LocaleController.getString("MetersAway", 2131165969) }));
            return;
          }
          this.distanceTextView.setText(String.format("%.2f %s", new Object[] { Float.valueOf(f / 1000.0F), LocaleController.getString("KMetersAway", 2131165864) }));
          return;
        }
      while (this.googleMap == null);
      localLatLng = new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude());
      if (this.adapter == null)
        continue;
      this.adapter.searchGooglePlacesWithQuery(null, this.myLocation);
      this.adapter.setGpsLocation(this.myLocation);
    }
    while (this.userLocationMoved);
    this.userLocation = new Location(paramLocation);
    if (this.firstWas)
    {
      paramLocation = b.a(localLatLng);
      this.googleMap.b(paramLocation);
      return;
    }
    this.firstWas = true;
    paramLocation = b.a(localLatLng, this.googleMap.b() - 4.0F);
    this.googleMap.a(paramLocation);
  }

  private void showPermissionAlert(boolean paramBoolean)
  {
    if (getParentActivity() == null)
      return;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    if (paramBoolean)
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocationPosition", 2131166259));
    while (true)
    {
      localBuilder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166260), new DialogInterface.OnClickListener()
      {
        @TargetApi(9)
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          if (LocationActivity.this.getParentActivity() == null)
            return;
          try
          {
            paramDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            paramDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            LocationActivity.this.getParentActivity().startActivity(paramDialogInterface);
            return;
          }
          catch (Exception paramDialogInterface)
          {
            FileLog.e(paramDialogInterface);
          }
        }
      });
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      showDialog(localBuilder.create());
      return;
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocation", 2131166258));
    }
  }

  private void updateClipView(int paramInt)
  {
    if (paramInt == -1);
    label296: 
    while (true)
    {
      return;
      Object localObject = this.listView.getChildAt(0);
      if (localObject == null)
        continue;
      int k;
      label42: int j;
      if (paramInt == 0)
      {
        paramInt = ((View)localObject).getTop();
        k = this.overScrollHeight;
        if (paramInt < 0)
        {
          int i = paramInt;
          i += k;
        }
      }
      while (true)
      {
        if ((FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams() == null)
          break label296;
        if (j <= 0)
          if (this.mapView.getVisibility() == 0)
          {
            this.mapView.setVisibility(4);
            this.mapViewClip.setVisibility(4);
          }
        while (true)
        {
          this.mapViewClip.setTranslationY(Math.min(0, paramInt));
          this.mapView.setTranslationY(Math.max(0, -paramInt / 2));
          localObject = this.markerImageView;
          k = -paramInt - AndroidUtilities.dp(42.0F) + j / 2;
          this.markerTop = k;
          ((ImageView)localObject).setTranslationY(k);
          this.markerXImageView.setTranslationY(-paramInt - AndroidUtilities.dp(7.0F) + j / 2);
          localObject = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
          if ((localObject == null) || (((FrameLayout.LayoutParams)localObject).height == this.overScrollHeight + AndroidUtilities.dp(10.0F)))
            break;
          ((FrameLayout.LayoutParams)localObject).height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
          if (this.googleMap != null)
            this.googleMap.a(0, 0, 0, AndroidUtilities.dp(10.0F));
          this.mapView.setLayoutParams((ViewGroup.LayoutParams)localObject);
          return;
          j = 0;
          break label42;
          if (this.mapView.getVisibility() != 4)
            continue;
          this.mapView.setVisibility(0);
          this.mapViewClip.setVisibility(0);
        }
        paramInt = 0;
        j = 0;
      }
    }
  }

  private void updateSearchInterface()
  {
    if (this.adapter != null)
      this.adapter.notifyDataSetChanged();
  }

  private void updateUserData()
  {
    int i;
    String str;
    Object localObject;
    if ((this.messageObject != null) && (this.avatarImageView != null))
    {
      i = this.messageObject.messageOwner.from_id;
      if (this.messageObject.isForwarded())
      {
        if (this.messageObject.messageOwner.fwd_from.channel_id == 0)
          break label161;
        i = -this.messageObject.messageOwner.fwd_from.channel_id;
      }
      str = "";
      this.avatarDrawable = null;
      if (i <= 0)
        break label178;
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
      if (localObject == null)
        break label249;
      if (((TLRPC.User)localObject).photo == null)
        break label254;
    }
    label161: label178: label244: label249: label254: for (TLRPC.FileLocation localFileLocation = ((TLRPC.User)localObject).photo.photo_small; ; localFileLocation = null)
    {
      this.avatarDrawable = new AvatarDrawable((TLRPC.User)localObject);
      str = UserObject.getUserName((TLRPC.User)localObject);
      while (true)
      {
        if (this.avatarDrawable != null)
        {
          this.avatarImageView.setImage(localFileLocation, null, this.avatarDrawable);
          this.nameTextView.setText(str);
          return;
          i = this.messageObject.messageOwner.fwd_from.from_id;
          break;
          localObject = MessagesController.getInstance().getChat(Integer.valueOf(-i));
          if (localObject == null)
            break label249;
          if (((TLRPC.Chat)localObject).photo == null)
            break label244;
        }
        for (localFileLocation = ((TLRPC.Chat)localObject).photo.photo_small; ; localFileLocation = null)
        {
          this.avatarDrawable = new AvatarDrawable((TLRPC.Chat)localObject);
          str = ((TLRPC.Chat)localObject).title;
          break;
          this.avatarImageView.setImageDrawable(null);
          return;
        }
        localFileLocation = null;
      }
    }
  }

  public View createView(Context paramContext)
  {
    int i = 3;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    if (AndroidUtilities.isTablet())
      this.actionBar.setOccupyStatusBar(false);
    Object localObject1 = this.actionBar;
    boolean bool;
    label199: label209: FrameLayout localFrameLayout;
    Object localObject2;
    if (this.messageObject != null)
    {
      bool = true;
      ((ActionBar)localObject1).setAddToContainer(bool);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            LocationActivity.this.finishFragment();
          do
            while (true)
            {
              return;
              if (paramInt == 2)
              {
                if (LocationActivity.this.googleMap == null)
                  continue;
                LocationActivity.this.googleMap.a(1);
                return;
              }
              if (paramInt == 3)
              {
                if (LocationActivity.this.googleMap == null)
                  continue;
                LocationActivity.this.googleMap.a(2);
                return;
              }
              if (paramInt != 4)
                break;
              if (LocationActivity.this.googleMap == null)
                continue;
              LocationActivity.this.googleMap.a(4);
              return;
            }
          while (paramInt != 1);
          try
          {
            double d1 = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
            double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
            LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + d1 + "," + d2 + "?q=" + d1 + "," + d2)));
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
      localObject1 = this.actionBar.createMenu();
      if (this.messageObject == null)
        break label1713;
      if ((this.messageObject.messageOwner.media.title == null) || (this.messageObject.messageOwner.media.title.length() <= 0))
        break label1694;
      this.actionBar.setTitle(this.messageObject.messageOwner.media.title);
      if ((this.messageObject.messageOwner.media.address != null) && (this.messageObject.messageOwner.media.address.length() > 0))
        this.actionBar.setSubtitle(this.messageObject.messageOwner.media.address);
      ((ActionBarMenu)localObject1).addItem(1, 2130838058);
      localObject1 = ((ActionBarMenu)localObject1).addItem(0, 2130837738);
      ((ActionBarMenuItem)localObject1).addSubItem(2, LocaleController.getString("Map", 2131165935));
      ((ActionBarMenuItem)localObject1).addSubItem(3, LocaleController.getString("Satellite", 2131166369));
      ((ActionBarMenuItem)localObject1).addSubItem(4, LocaleController.getString("Hybrid", 2131165824));
      this.fragmentView = new FrameLayout(paramContext)
      {
        private boolean first = true;

        protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        {
          super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
          if (paramBoolean)
          {
            LocationActivity.this.fixLayoutInternal(this.first);
            this.first = false;
          }
        }
      };
      localFrameLayout = (FrameLayout)this.fragmentView;
      this.locationButton = new ImageView(paramContext);
      localObject1 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
      if (Build.VERSION.SDK_INT >= 21)
        break label2654;
      localObject2 = paramContext.getResources().getDrawable(2130837718).mutate();
      ((Drawable)localObject2).setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
      localObject1 = new CombinedDrawable((Drawable)localObject2, (Drawable)localObject1, 0, 0);
      ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
    }
    label780: label788: label798: label942: label952: label962: label1093: label1103: label1113: label2654: 
    while (true)
    {
      this.locationButton.setBackgroundDrawable((Drawable)localObject1);
      this.locationButton.setImageResource(2130837958);
      this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
      this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject1 = new StateListAnimator();
        localObject2 = ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
        localObject2 = ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
        this.locationButton.setStateListAnimator((StateListAnimator)localObject1);
        this.locationButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramView, Outline paramOutline)
          {
            paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      int j;
      float f1;
      float f2;
      if (this.messageObject != null)
      {
        this.mapView = new d(paramContext);
        localFrameLayout.setBackgroundDrawable(new MapPlaceholderDrawable());
        new Thread(new Runnable(this.mapView)
        {
          public void run()
          {
            try
            {
              this.val$map.onCreate(null);
              label8: AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((LocationActivity.this.mapView != null) && (LocationActivity.this.getParentActivity() != null));
                  try
                  {
                    LocationActivity.5.this.val$map.onCreate(null);
                    com.google.android.gms.maps.e.a(LocationActivity.this.getParentActivity());
                    LocationActivity.this.mapView.getMapAsync(new f()
                    {
                      public void onMapReady(c paramc)
                      {
                        LocationActivity.access$002(LocationActivity.this, paramc);
                        LocationActivity.this.googleMap.a(0, 0, 0, AndroidUtilities.dp(10.0F));
                        LocationActivity.this.onMapInit();
                      }
                    });
                    LocationActivity.access$1302(LocationActivity.this, true);
                    if (LocationActivity.this.onResumeCalled)
                      LocationActivity.this.mapView.onResume();
                    return;
                  }
                  catch (Exception localException)
                  {
                    FileLog.e(localException);
                  }
                }
              });
              return;
            }
            catch (Exception localException)
            {
              break label8;
            }
          }
        }).start();
        this.bottomView = new FrameLayout(paramContext);
        localObject1 = paramContext.getResources().getDrawable(2130837899);
        ((Drawable)localObject1).setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
        this.bottomView.setBackgroundDrawable((Drawable)localObject1);
        localFrameLayout.addView(this.bottomView, LayoutHelper.createFrame(-1, 60, 83));
        this.bottomView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (LocationActivity.this.userLocation != null)
            {
              paramView = new LatLng(LocationActivity.this.userLocation.getLatitude(), LocationActivity.this.userLocation.getLongitude());
              if (LocationActivity.this.googleMap != null)
              {
                paramView = b.a(paramView, LocationActivity.this.googleMap.b() - 4.0F);
                LocationActivity.this.googleMap.b(paramView);
              }
            }
          }
        });
        this.avatarImageView = new BackupImageView(paramContext);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0F));
        localObject1 = this.bottomView;
        localObject2 = this.avatarImageView;
        if (LocaleController.isRTL)
        {
          j = 5;
          if (!LocaleController.isRTL)
            break label1777;
          f1 = 0.0F;
          if (!LocaleController.isRTL)
            break label1784;
          f2 = 12.0F;
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(40, 40.0F, j | 0x30, f1, 12.0F, f2, 0.0F));
          this.nameTextView = new TextView(paramContext);
          this.nameTextView.setTextSize(1, 16.0F);
          this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          this.nameTextView.setMaxLines(1);
          this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
          this.nameTextView.setSingleLine(true);
          localObject1 = this.nameTextView;
          if (!LocaleController.isRTL)
            break label1789;
          j = 5;
          ((TextView)localObject1).setGravity(j);
          localObject1 = this.bottomView;
          localObject2 = this.nameTextView;
          if (!LocaleController.isRTL)
            break label1795;
          j = 5;
          if (!LocaleController.isRTL)
            break label1801;
          f1 = 12.0F;
          if (!LocaleController.isRTL)
            break label1808;
          f2 = 72.0F;
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, j | 0x30, f1, 10.0F, f2, 0.0F));
          this.distanceTextView = new TextView(paramContext);
          this.distanceTextView.setTextSize(1, 14.0F);
          this.distanceTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
          this.distanceTextView.setMaxLines(1);
          this.distanceTextView.setEllipsize(TextUtils.TruncateAt.END);
          this.distanceTextView.setSingleLine(true);
          localObject1 = this.distanceTextView;
          if (!LocaleController.isRTL)
            break label1815;
          j = 5;
          ((TextView)localObject1).setGravity(j);
          localObject1 = this.bottomView;
          localObject2 = this.distanceTextView;
          if (!LocaleController.isRTL)
            break label1821;
          j = 5;
          if (!LocaleController.isRTL)
            break label1827;
          f1 = 12.0F;
          if (!LocaleController.isRTL)
            break label1834;
          f2 = 72.0F;
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, j | 0x30, f1, 33.0F, f2, 0.0F));
          this.userLocation = new Location("network");
          this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
          this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
          this.routeButton = new ImageView(paramContext);
          localObject1 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
          if (Build.VERSION.SDK_INT >= 21)
            break label2648;
          paramContext = paramContext.getResources().getDrawable(2130837717).mutate();
          paramContext.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
          paramContext = new CombinedDrawable(paramContext, (Drawable)localObject1, 0, 0);
          paramContext.setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
        }
      }
      while (true)
      {
        this.routeButton.setBackgroundDrawable(paramContext);
        this.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.routeButton.setImageResource(2130837959);
        this.routeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21)
        {
          paramContext = new StateListAnimator();
          localObject1 = ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
          paramContext.addState(new int[] { 16842919 }, (Animator)localObject1);
          localObject1 = ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
          paramContext.addState(new int[0], (Animator)localObject1);
          this.routeButton.setStateListAnimator(paramContext);
          this.routeButton.setOutlineProvider(new ViewOutlineProvider()
          {
            @SuppressLint({"NewApi"})
            public void getOutline(View paramView, Outline paramOutline)
            {
              paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
            }
          });
        }
        paramContext = this.routeButton;
        label1511: label1523: int k;
        label1532: float f3;
        if (Build.VERSION.SDK_INT >= 21)
        {
          j = 56;
          if (Build.VERSION.SDK_INT < 21)
            break label1848;
          f1 = 56.0F;
          if (!LocaleController.isRTL)
            break label1855;
          k = 3;
          if (!LocaleController.isRTL)
            break label1861;
          f2 = 14.0F;
          if (!LocaleController.isRTL)
            break label1866;
          f3 = 0.0F;
          localFrameLayout.addView(paramContext, LayoutHelper.createFrame(j, f1, k | 0x50, f2, 0.0F, f3, 28.0F));
          this.routeButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              if (Build.VERSION.SDK_INT >= 23)
              {
                paramView = LocationActivity.this.getParentActivity();
                if ((paramView != null) && (paramView.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0))
                  LocationActivity.this.showPermissionAlert(true);
              }
              do
                return;
              while (LocationActivity.this.myLocation == null);
              try
              {
                paramView = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[] { Double.valueOf(LocationActivity.access$1600(LocationActivity.this).getLatitude()), Double.valueOf(LocationActivity.access$1600(LocationActivity.this).getLongitude()), Double.valueOf(LocationActivity.access$100(LocationActivity.this).messageOwner.media.geo.lat), Double.valueOf(LocationActivity.access$100(LocationActivity.this).messageOwner.media.geo._long) })));
                LocationActivity.this.getParentActivity().startActivity(paramView);
                return;
              }
              catch (Exception paramView)
              {
                FileLog.e(paramView);
              }
            }
          });
          paramContext = this.locationButton;
          if (Build.VERSION.SDK_INT < 21)
            break label1874;
          j = 56;
          if (Build.VERSION.SDK_INT < 21)
            break label1881;
          f1 = 56.0F;
          if (!LocaleController.isRTL)
            break label1888;
          if (!LocaleController.isRTL)
            break label1894;
          f2 = 14.0F;
          label1635: if (!LocaleController.isRTL)
            break label1899;
          f3 = 0.0F;
        }
        while (true)
        {
          localFrameLayout.addView(paramContext, LayoutHelper.createFrame(j, f1, i | 0x50, f2, 0.0F, f3, 100.0F));
          this.locationButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              if (Build.VERSION.SDK_INT >= 23)
              {
                paramView = LocationActivity.this.getParentActivity();
                if ((paramView != null) && (paramView.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0))
                  LocationActivity.this.showPermissionAlert(true);
              }
              do
                return;
              while ((LocationActivity.this.myLocation == null) || (LocationActivity.this.googleMap == null));
              LocationActivity.this.googleMap.b(b.a(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude()), LocationActivity.this.googleMap.b() - 4.0F));
            }
          });
          return this.fragmentView;
          bool = false;
          break;
          this.actionBar.setTitle(LocaleController.getString("ChatLocation", 2131165536));
          break label199;
          this.actionBar.setTitle(LocaleController.getString("ShareLocation", 2131166453));
          ((ActionBarMenu)localObject1).addItem(0, 2130837741).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
          {
            public void onSearchCollapse()
            {
              LocationActivity.access$202(LocationActivity.this, false);
              LocationActivity.access$702(LocationActivity.this, false);
              LocationActivity.this.searchListView.setEmptyView(null);
              LocationActivity.this.listView.setVisibility(0);
              LocationActivity.this.mapViewClip.setVisibility(0);
              LocationActivity.this.searchListView.setVisibility(8);
              LocationActivity.this.emptyView.setVisibility(8);
              LocationActivity.this.searchAdapter.searchDelayed(null, null);
            }

            public void onSearchExpand()
            {
              LocationActivity.access$202(LocationActivity.this, true);
              LocationActivity.this.listView.setVisibility(8);
              LocationActivity.this.mapViewClip.setVisibility(8);
              LocationActivity.this.searchListView.setVisibility(0);
              LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyView);
            }

            public void onTextChanged(EditText paramEditText)
            {
              if (LocationActivity.this.searchAdapter == null)
                return;
              paramEditText = paramEditText.getText().toString();
              if (paramEditText.length() != 0)
                LocationActivity.access$702(LocationActivity.this, true);
              LocationActivity.this.searchAdapter.searchDelayed(paramEditText, LocationActivity.this.userLocation);
            }
          }).getSearchField().setHint(LocaleController.getString("Search", 2131166381));
          break label209;
          j = 3;
          break label780;
          label1777: f1 = 12.0F;
          break label788;
          label1784: f2 = 0.0F;
          break label798;
          label1789: j = 3;
          break label914;
          j = 3;
          break label942;
          f1 = 72.0F;
          break label952;
          f2 = 12.0F;
          break label962;
          j = 3;
          break label1065;
          j = 3;
          break label1093;
          f1 = 72.0F;
          break label1103;
          f2 = 12.0F;
          break label1113;
          j = 60;
          break label1511;
          f1 = 60.0F;
          break label1523;
          k = 5;
          break label1532;
          f2 = 0.0F;
          break label1542;
          f3 = 14.0F;
          break label1551;
          j = 60;
          break label1607;
          f1 = 60.0F;
          break label1619;
          label1888: i = 5;
          break label1625;
          label1894: f2 = 0.0F;
          break label1635;
          label1899: f3 = 14.0F;
        }
        this.searchWas = false;
        this.searching = false;
        this.mapViewClip = new FrameLayout(paramContext);
        this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
        if (this.adapter != null)
          this.adapter.destroy();
        if (this.searchAdapter != null)
          this.searchAdapter.destroy();
        this.listView = new RecyclerListView(paramContext);
        localObject1 = this.listView;
        localObject2 = new LocationActivityAdapter(paramContext);
        this.adapter = ((LocationActivityAdapter)localObject2);
        ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
        this.listView.setVerticalScrollBarEnabled(false);
        localObject1 = this.listView;
        localObject2 = new LinearLayoutManager(paramContext, 1, false);
        this.layoutManager = ((LinearLayoutManager)localObject2);
        ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
        localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
          {
            if (LocationActivity.this.adapter.getItemCount() == 0);
            do
            {
              return;
              paramInt1 = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
            }
            while (paramInt1 == -1);
            LocationActivity.this.updateClipView(paramInt1);
          }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
        {
          public void onItemClick(View paramView, int paramInt)
          {
            if (paramInt == 1)
            {
              if ((LocationActivity.this.delegate != null) && (LocationActivity.this.userLocation != null))
              {
                paramView = new TLRPC.TL_messageMediaGeo();
                paramView.geo = new TLRPC.TL_geoPoint();
                paramView.geo.lat = LocationActivity.this.userLocation.getLatitude();
                paramView.geo._long = LocationActivity.this.userLocation.getLongitude();
                LocationActivity.this.delegate.didSelectLocation(paramView);
              }
              LocationActivity.this.finishFragment();
              return;
            }
            paramView = LocationActivity.this.adapter.getItem(paramInt);
            if ((paramView != null) && (LocationActivity.this.delegate != null))
              LocationActivity.this.delegate.didSelectLocation(paramView);
            LocationActivity.this.finishFragment();
          }
        });
        this.adapter.setDelegate(new BaseLocationAdapter.BaseLocationAdapterDelegate()
        {
          public void didLoadedSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> paramArrayList)
          {
            if ((!LocationActivity.this.wasResults) && (!paramArrayList.isEmpty()))
              LocationActivity.access$2102(LocationActivity.this, true);
          }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        localFrameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new d(paramContext)
        {
          public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
          {
            if (paramMotionEvent.getAction() == 0)
            {
              if (LocationActivity.this.animatorSet != null)
                LocationActivity.this.animatorSet.cancel();
              LocationActivity.access$2202(LocationActivity.this, new AnimatorSet());
              LocationActivity.this.animatorSet.setDuration(200L);
              LocationActivity.this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(LocationActivity.access$2300(LocationActivity.this), "translationY", new float[] { LocationActivity.access$2400(LocationActivity.this) + -AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofFloat(LocationActivity.access$2500(LocationActivity.this), "alpha", new float[] { 1.0F }) });
              LocationActivity.this.animatorSet.start();
            }
            while (true)
            {
              if (paramMotionEvent.getAction() == 2)
              {
                if (!LocationActivity.this.userLocationMoved)
                {
                  AnimatorSet localAnimatorSet = new AnimatorSet();
                  localAnimatorSet.setDuration(200L);
                  localAnimatorSet.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[] { 1.0F }));
                  localAnimatorSet.start();
                  LocationActivity.access$2602(LocationActivity.this, true);
                }
                if ((LocationActivity.this.googleMap != null) && (LocationActivity.this.userLocation != null))
                {
                  LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.a().a.a);
                  LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.a().a.b);
                }
                LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
              }
              return super.onInterceptTouchEvent(paramMotionEvent);
              if (paramMotionEvent.getAction() != 1)
                continue;
              if (LocationActivity.this.animatorSet != null)
                LocationActivity.this.animatorSet.cancel();
              LocationActivity.access$2202(LocationActivity.this, new AnimatorSet());
              LocationActivity.this.animatorSet.setDuration(200L);
              LocationActivity.this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(LocationActivity.access$2300(LocationActivity.this), "translationY", new float[] { LocationActivity.access$2400(LocationActivity.this) }), ObjectAnimator.ofFloat(LocationActivity.access$2500(LocationActivity.this), "alpha", new float[] { 0.0F }) });
              LocationActivity.this.animatorSet.start();
            }
          }
        };
        new Thread(new Runnable(this.mapView)
        {
          public void run()
          {
            try
            {
              this.val$map.onCreate(null);
              label8: AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((LocationActivity.this.mapView != null) && (LocationActivity.this.getParentActivity() != null));
                  try
                  {
                    LocationActivity.14.this.val$map.onCreate(null);
                    com.google.android.gms.maps.e.a(LocationActivity.this.getParentActivity());
                    LocationActivity.this.mapView.getMapAsync(new f()
                    {
                      public void onMapReady(c paramc)
                      {
                        LocationActivity.access$002(LocationActivity.this, paramc);
                        LocationActivity.this.googleMap.a(0, 0, 0, AndroidUtilities.dp(10.0F));
                        LocationActivity.this.onMapInit();
                      }
                    });
                    LocationActivity.access$1302(LocationActivity.this, true);
                    if (LocationActivity.this.onResumeCalled)
                      LocationActivity.this.mapView.onResume();
                    return;
                  }
                  catch (Exception localException)
                  {
                    FileLog.e(localException);
                  }
                }
              });
              return;
            }
            catch (Exception localException)
            {
              break label8;
            }
          }
        }).start();
        localObject1 = new View(paramContext);
        ((View)localObject1).setBackgroundResource(2130837729);
        this.mapViewClip.addView((View)localObject1, LayoutHelper.createFrame(-1, 3, 83));
        this.markerImageView = new ImageView(paramContext);
        this.markerImageView.setImageResource(2130837905);
        this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(24, 42, 49));
        this.markerXImageView = new ImageView(paramContext);
        this.markerXImageView.setAlpha(0.0F);
        this.markerXImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_markerX"), PorterDuff.Mode.MULTIPLY));
        this.markerXImageView.setImageResource(2130838028);
        this.mapViewClip.addView(this.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
        localObject1 = this.mapViewClip;
        localObject2 = this.locationButton;
        if (Build.VERSION.SDK_INT >= 21)
        {
          j = 56;
          if (Build.VERSION.SDK_INT < 21)
            break label2622;
          f1 = 56.0F;
          if (!LocaleController.isRTL)
            break label2629;
          if (!LocaleController.isRTL)
            break label2635;
          f2 = 14.0F;
          if (!LocaleController.isRTL)
            break label2640;
          f3 = 0.0F;
        }
        while (true)
        {
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(j, f1, i | 0x50, f2, 0.0F, f3, 14.0F));
          this.locationButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              if (Build.VERSION.SDK_INT >= 23)
              {
                paramView = LocationActivity.this.getParentActivity();
                if ((paramView != null) && (paramView.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0))
                  LocationActivity.this.showPermissionAlert(false);
              }
              do
                return;
              while ((LocationActivity.this.myLocation == null) || (LocationActivity.this.googleMap == null));
              paramView = new AnimatorSet();
              paramView.setDuration(200L);
              paramView.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[] { 0.0F }));
              paramView.start();
              LocationActivity.this.adapter.setCustomLocation(null);
              LocationActivity.access$2602(LocationActivity.this, false);
              LocationActivity.this.googleMap.b(b.a(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude())));
            }
          });
          this.locationButton.setAlpha(0.0F);
          this.emptyView = new EmptyTextProgressView(paramContext);
          this.emptyView.setText(LocaleController.getString("NoResult", 2131166045));
          this.emptyView.setShowAtCenter(true);
          this.emptyView.setVisibility(8);
          localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
          this.searchListView = new RecyclerListView(paramContext);
          this.searchListView.setVisibility(8);
          this.searchListView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
          localObject1 = this.searchListView;
          paramContext = new LocationActivitySearchAdapter(paramContext);
          this.searchAdapter = paramContext;
          ((RecyclerListView)localObject1).setAdapter(paramContext);
          localFrameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
          this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener()
          {
            public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
            {
              if ((paramInt == 1) && (LocationActivity.this.searching) && (LocationActivity.this.searchWas))
                AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
            }
          });
          this.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
          {
            public void onItemClick(View paramView, int paramInt)
            {
              paramView = LocationActivity.this.searchAdapter.getItem(paramInt);
              if ((paramView != null) && (LocationActivity.this.delegate != null))
                LocationActivity.this.delegate.didSelectLocation(paramView);
              LocationActivity.this.finishFragment();
            }
          });
          localFrameLayout.addView(this.actionBar);
          break;
          j = 60;
          break label2347;
          f1 = 60.0F;
          break label2359;
          i = 5;
          break label2365;
          f2 = 0.0F;
          break label2375;
          f3 = 14.0F;
        }
        paramContext = (Context)localObject1;
      }
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramArrayOfObject[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0))
        updateUserData();
    }
    do
    {
      return;
      if (paramInt != NotificationCenter.closeChats)
        continue;
      removeSelfFromStack();
      return;
    }
    while ((paramInt != NotificationCenter.locationPermissionGranted) || (this.googleMap == null));
    try
    {
      this.googleMap.a(true);
      return;
    }
    catch (Exception paramArrayOfObject)
    {
      FileLog.e(paramArrayOfObject);
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    21 local21 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        LocationActivity.this.updateUserData();
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon"), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground"), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground"), new ThemeDescription(this.bottomView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.distanceTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"), new ThemeDescription(this.markerXImageView, 0, null, null, null, null, "location_markerX"), new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, local21, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundPink"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { SendLocationCell.class }, new String[] { "imageView" }, null, null, null, "location_sendLocationIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { SendLocationCell.class }, new String[] { "imageView" }, null, null, null, "location_sendLocationBackground"), new ThemeDescription(this.listView, 0, new Class[] { SendLocationCell.class }, new String[] { "titleTextView" }, null, null, null, "windowBackgroundWhiteBlueText7"), new ThemeDescription(this.listView, 0, new Class[] { SendLocationCell.class }, new String[] { "accurateTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { LocationCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { LocationCell.class }, new String[] { "addressTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { LocationCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.searchListView, 0, new Class[] { LocationCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.searchListView, 0, new Class[] { LocationCell.class }, new String[] { "addressTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationLoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { LocationLoadingCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationPoweredCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationPoweredCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationPoweredCell.class }, new String[] { "textView2" }, null, null, null, "windowBackgroundWhiteGrayText3") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.swipeBackEnabled = false;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
    if (this.messageObject != null)
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    try
    {
      if (this.mapView != null)
        this.mapView.onDestroy();
      if (this.adapter != null)
        this.adapter.destroy();
      if (this.searchAdapter != null)
        this.searchAdapter.destroy();
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void onLowMemory()
  {
    super.onLowMemory();
    if ((this.mapView != null) && (this.mapsInitialized))
      this.mapView.onLowMemory();
  }

  public void onPause()
  {
    super.onPause();
    if ((this.mapView != null) && (this.mapsInitialized));
    try
    {
      this.mapView.onPause();
      this.onResumeCalled = false;
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void onResume()
  {
    super.onResume();
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    if ((this.mapView != null) && (this.mapsInitialized));
    try
    {
      this.mapView.onResume();
      this.onResumeCalled = true;
      if (this.googleMap == null);
    }
    catch (Throwable localException)
    {
      try
      {
        this.googleMap.a(true);
        updateUserData();
        fixLayoutInternal(true);
        if ((this.checkPermission) && (Build.VERSION.SDK_INT >= 23))
        {
          Activity localActivity = getParentActivity();
          if (localActivity != null)
          {
            this.checkPermission = false;
            if (localActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)
              localActivity.requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
          }
        }
        return;
        localThrowable = localThrowable;
        FileLog.e(localThrowable);
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1);
    try
    {
      if ((this.mapView.getParent() instanceof ViewGroup))
        ((ViewGroup)this.mapView.getParent()).removeView(this.mapView);
      if (this.mapViewClip != null)
      {
        this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0F), 51));
        updateClipView(this.layoutManager.findFirstVisibleItemPosition());
        return;
      }
    }
    catch (Exception localException)
    {
      do
        while (true)
          FileLog.e(localException);
      while (this.fragmentView == null);
      ((FrameLayout)this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
    }
  }

  public void setDelegate(LocationActivityDelegate paramLocationActivityDelegate)
  {
    this.delegate = paramLocationActivityDelegate;
  }

  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.messageObject = paramMessageObject;
  }

  public static abstract interface LocationActivityDelegate
  {
    public abstract void didSelectLocation(TLRPC.MessageMedia paramMessageMedia);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.LocationActivity
 * JD-Core Version:    0.6.0
 */