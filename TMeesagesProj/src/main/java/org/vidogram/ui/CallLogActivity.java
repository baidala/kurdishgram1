package org.vidogram.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterPhoneCalls;
import org.vidogram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_messages_search;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.messages_Messages;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Cells.LocationCell;
import org.vidogram.ui.Cells.ProfileSearchCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.voip.VoIPHelper;

public class CallLogActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int TYPE_IN = 1;
  private static final int TYPE_MISSED = 2;
  private static final int TYPE_OUT = 0;
  private View.OnClickListener callBtnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramView)
    {
      paramView = (CallLogActivity.CallLogRow)paramView.getTag();
      VoIPHelper.startCall(CallLogActivity.access$102(CallLogActivity.this, paramView.user), CallLogActivity.this.getParentActivity(), null);
    }
  };
  private ArrayList<CallLogRow> calls = new ArrayList();
  private EmptyTextProgressView emptyView;
  private boolean endReached;
  private boolean firstLoaded;
  private ImageView floatingButton;
  private boolean floatingHidden;
  private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
  private Drawable greenDrawable;
  private Drawable greenDrawable2;
  private ImageSpan iconIn;
  private ImageSpan iconMissed;
  private ImageSpan iconOut;
  private TLRPC.User lastCallUser;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private boolean loading;
  private int prevPosition;
  private int prevTop;
  private Drawable redDrawable;
  private boolean scrollUpdated;

  private void confirmAndDelete(CallLogRow paramCallLogRow)
  {
    if (getParentActivity() == null)
      return;
    new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", 2131165319)).setMessage(LocaleController.getString("ConfirmDeleteCallLog", 2131165570)).setPositiveButton(LocaleController.getString("Delete", 2131165628), new DialogInterface.OnClickListener(paramCallLogRow)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface = new ArrayList();
        Iterator localIterator = this.val$row.calls.iterator();
        while (localIterator.hasNext())
          paramDialogInterface.add(Integer.valueOf(((TLRPC.Message)localIterator.next()).id));
        MessagesController.getInstance().deleteMessages(paramDialogInterface, null, null, 0, false);
      }
    }).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null).show().setCanceledOnTouchOutside(true);
  }

  private void getCalls(int paramInt1, int paramInt2)
  {
    if (this.loading)
      return;
    this.loading = true;
    if ((this.emptyView != null) && (!this.firstLoaded))
      this.emptyView.showProgress();
    if (this.listViewAdapter != null)
      this.listViewAdapter.notifyDataSetChanged();
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.limit = paramInt2;
    localTL_messages_search.peer = new TLRPC.TL_inputPeerEmpty();
    localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhoneCalls();
    localTL_messages_search.q = "";
    localTL_messages_search.max_id = paramInt1;
    paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            if (this.val$error == null)
            {
              SparseArray localSparseArray = new SparseArray();
              TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)this.val$response;
              CallLogActivity.access$602(CallLogActivity.this, localmessages_Messages.messages.isEmpty());
              int i = 0;
              Object localObject1;
              while (i < localmessages_Messages.users.size())
              {
                localObject1 = (TLRPC.User)localmessages_Messages.users.get(i);
                localSparseArray.put(((TLRPC.User)localObject1).id, localObject1);
                i += 1;
              }
              if (CallLogActivity.this.calls.size() > 0)
                localObject1 = (CallLogActivity.CallLogRow)CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1);
              while (true)
              {
                int j = 0;
                TLRPC.Message localMessage;
                while (true)
                {
                  if (j >= localmessages_Messages.messages.size())
                    break label411;
                  localMessage = (TLRPC.Message)localmessages_Messages.messages.get(j);
                  if (localMessage.action == null)
                  {
                    j += 1;
                    continue;
                    localObject1 = null;
                    break;
                  }
                }
                label202: Object localObject2;
                int k;
                if (localMessage.from_id == UserConfig.getClientUserId())
                {
                  i = 0;
                  localObject2 = localMessage.action.reason;
                  k = i;
                  if (i == 1)
                    if (!(localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonMissed))
                    {
                      k = i;
                      if (!(localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonBusy));
                    }
                    else
                    {
                      k = 2;
                    }
                  if (localMessage.from_id != UserConfig.getClientUserId())
                    break label402;
                }
                label402: for (i = localMessage.to_id.user_id; ; i = localMessage.from_id)
                {
                  if ((localObject1 != null) && (((CallLogActivity.CallLogRow)localObject1).user.id == i))
                  {
                    localObject2 = localObject1;
                    if (((CallLogActivity.CallLogRow)localObject1).type == k);
                  }
                  else
                  {
                    if ((localObject1 != null) && (!CallLogActivity.this.calls.contains(localObject1)))
                      CallLogActivity.this.calls.add(localObject1);
                    localObject2 = new CallLogActivity.CallLogRow(CallLogActivity.this, null);
                    ((CallLogActivity.CallLogRow)localObject2).calls = new ArrayList();
                    ((CallLogActivity.CallLogRow)localObject2).user = ((TLRPC.User)localSparseArray.get(i));
                    ((CallLogActivity.CallLogRow)localObject2).type = k;
                  }
                  ((CallLogActivity.CallLogRow)localObject2).calls.add(localMessage);
                  localObject1 = localObject2;
                  break;
                  i = 1;
                  break label202;
                }
              }
              label411: if ((localObject1 != null) && (((CallLogActivity.CallLogRow)localObject1).calls.size() > 0) && (!CallLogActivity.this.calls.contains(localObject1)))
                CallLogActivity.this.calls.add(localObject1);
            }
            while (true)
            {
              CallLogActivity.access$702(CallLogActivity.this, false);
              CallLogActivity.access$1402(CallLogActivity.this, true);
              if (CallLogActivity.this.emptyView != null)
                CallLogActivity.this.emptyView.showTextView();
              if (CallLogActivity.this.listViewAdapter != null)
                CallLogActivity.this.listViewAdapter.notifyDataSetChanged();
              return;
              CallLogActivity.access$602(CallLogActivity.this, true);
            }
          }
        });
      }
    }
    , 2);
    ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, this.classGuid);
  }

  private void hideFloatingButton(boolean paramBoolean)
  {
    if (this.floatingHidden == paramBoolean)
      return;
    this.floatingHidden = paramBoolean;
    Object localObject = this.floatingButton;
    float f;
    ImageView localImageView;
    if (this.floatingHidden)
    {
      f = AndroidUtilities.dp(100.0F);
      localObject = ObjectAnimator.ofFloat(localObject, "translationY", new float[] { f }).setDuration(300L);
      ((ObjectAnimator)localObject).setInterpolator(this.floatingInterpolator);
      localImageView = this.floatingButton;
      if (paramBoolean)
        break label91;
    }
    label91: for (paramBoolean = true; ; paramBoolean = false)
    {
      localImageView.setClickable(paramBoolean);
      ((ObjectAnimator)localObject).start();
      return;
      f = 0.0F;
      break;
    }
  }

  public View createView(Context paramContext)
  {
    this.greenDrawable = getParentActivity().getResources().getDrawable(2130837754).mutate();
    this.greenDrawable.setBounds(0, 0, this.greenDrawable.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
    this.greenDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
    this.iconOut = new ImageSpan(this.greenDrawable, 0);
    this.greenDrawable2 = getParentActivity().getResources().getDrawable(2130837755).mutate();
    this.greenDrawable2.setBounds(0, 0, this.greenDrawable2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
    this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
    this.iconIn = new ImageSpan(this.greenDrawable2, 0);
    this.redDrawable = getParentActivity().getResources().getDrawable(2130837755).mutate();
    this.redDrawable.setBounds(0, 0, this.redDrawable.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
    this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedRedIcon"), PorterDuff.Mode.MULTIPLY));
    this.iconMissed = new ImageSpan(this.redDrawable, 0);
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Calls", 2131165424));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          CallLogActivity.this.finishFragment();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoCallLog", 2131166024));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    Object localObject1 = this.listView;
    Object localObject2 = new LinearLayoutManager(paramContext, 1, false);
    this.layoutManager = ((LinearLayoutManager)localObject2);
    ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
    localObject1 = this.listView;
    localObject2 = new ListAdapter(paramContext);
    this.listViewAdapter = ((ListAdapter)localObject2);
    ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
    localObject1 = this.listView;
    int i;
    if (LocaleController.isRTL)
    {
      i = 1;
      ((RecyclerListView)localObject1).setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if ((paramInt < 0) || (paramInt >= CallLogActivity.this.calls.size()))
            return;
          paramView = (CallLogActivity.CallLogRow)CallLogActivity.this.calls.get(paramInt);
          Bundle localBundle = new Bundle();
          localBundle.putInt("user_id", paramView.user.id);
          localBundle.putInt("message_id", ((TLRPC.Message)paramView.calls.get(0)).id);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          CallLogActivity.this.presentFragment(new ChatActivity(localBundle), true);
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          if ((paramInt < 0) || (paramInt >= CallLogActivity.this.calls.size()))
            return false;
          Object localObject = (CallLogActivity.CallLogRow)CallLogActivity.this.calls.get(paramInt);
          paramView = new AlertDialog.Builder(CallLogActivity.this.getParentActivity()).setTitle(LocaleController.getString("Calls", 2131165424));
          String str = LocaleController.getString("Delete", 2131165628);
          localObject = new DialogInterface.OnClickListener((CallLogActivity.CallLogRow)localObject)
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              CallLogActivity.this.confirmAndDelete(this.val$row);
            }
          };
          paramView.setItems(new String[] { str }, (DialogInterface.OnClickListener)localObject).show();
          return true;
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          boolean bool1 = false;
          int i = 0;
          int j = CallLogActivity.this.layoutManager.findFirstVisibleItemPosition();
          if (j == -1)
          {
            paramInt1 = 0;
            if (paramInt1 > 0)
            {
              paramInt2 = CallLogActivity.this.listViewAdapter.getItemCount();
              if ((!CallLogActivity.this.endReached) && (!CallLogActivity.this.loading) && (!CallLogActivity.this.calls.isEmpty()) && (paramInt1 + j >= paramInt2 - 5))
              {
                CallLogActivity.CallLogRow localCallLogRow = (CallLogActivity.CallLogRow)CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1);
                CallLogActivity.this.getCalls(((TLRPC.Message)localCallLogRow.calls.get(localCallLogRow.calls.size() - 1)).id, 100);
              }
            }
            if (CallLogActivity.this.floatingButton.getVisibility() != 8)
            {
              paramRecyclerView = paramRecyclerView.getChildAt(0);
              if (paramRecyclerView == null)
                break label341;
            }
          }
          label341: for (paramInt2 = paramRecyclerView.getTop(); ; paramInt2 = 0)
          {
            if (CallLogActivity.this.prevPosition == j)
            {
              int k = CallLogActivity.this.prevTop;
              if (paramInt2 < CallLogActivity.this.prevTop)
              {
                bool1 = true;
                label213: paramInt1 = i;
                bool2 = bool1;
                if (Math.abs(k - paramInt2) > 1)
                  paramInt1 = 1;
              }
            }
            for (boolean bool2 = bool1; ; bool2 = bool1)
            {
              if ((paramInt1 != 0) && (CallLogActivity.this.scrollUpdated))
                CallLogActivity.this.hideFloatingButton(bool2);
              CallLogActivity.access$1002(CallLogActivity.this, j);
              CallLogActivity.access$1102(CallLogActivity.this, paramInt2);
              CallLogActivity.access$1202(CallLogActivity.this, true);
              return;
              paramInt1 = Math.abs(CallLogActivity.this.layoutManager.findLastVisibleItemPosition() - j) + 1;
              break;
              bool1 = false;
              break label213;
              if (j > CallLogActivity.this.prevPosition)
                bool1 = true;
              paramInt1 = 1;
            }
          }
        }
      });
      if (!this.loading)
        break label964;
      this.emptyView.showProgress();
      label554: this.floatingButton = new ImageView(paramContext);
      this.floatingButton.setVisibility(0);
      this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
      localObject1 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
      if (Build.VERSION.SDK_INT >= 21)
        break label1007;
      paramContext = paramContext.getResources().getDrawable(2130837717).mutate();
      paramContext.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
      paramContext = new CombinedDrawable(paramContext, (Drawable)localObject1, 0, 0);
      paramContext.setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
    }
    while (true)
    {
      this.floatingButton.setBackgroundDrawable(paramContext);
      this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
      this.floatingButton.setImageResource(2130837758);
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramContext = new StateListAnimator();
        localObject1 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        paramContext.addState(new int[] { 16842919 }, (Animator)localObject1);
        localObject1 = ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        paramContext.addState(new int[0], (Animator)localObject1);
        this.floatingButton.setStateListAnimator(paramContext);
        this.floatingButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramView, Outline paramOutline)
          {
            paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      paramContext = this.floatingButton;
      label874: float f1;
      label886: int j;
      label895: float f2;
      label905: float f3;
      if (Build.VERSION.SDK_INT >= 21)
      {
        i = 56;
        if (Build.VERSION.SDK_INT < 21)
          break label981;
        f1 = 56.0F;
        if (!LocaleController.isRTL)
          break label988;
        j = 3;
        if (!LocaleController.isRTL)
          break label994;
        f2 = 14.0F;
        if (!LocaleController.isRTL)
          break label999;
        f3 = 0.0F;
      }
      while (true)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(i, f1, j | 0x50, f2, 0.0F, f3, 14.0F));
        this.floatingButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            paramView = new Bundle();
            paramView.putBoolean("destroyAfterSelect", true);
            paramView.putBoolean("returnAsResult", true);
            paramView.putBoolean("onlyUsers", true);
            paramView = new ContactsActivity(paramView);
            paramView.setDelegate(new ContactsActivity.ContactsActivityDelegate()
            {
              public void didSelectContact(TLRPC.User paramUser, String paramString)
              {
                VoIPHelper.startCall(paramUser, CallLogActivity.this.getParentActivity(), null);
              }
            });
            CallLogActivity.this.presentFragment(paramView);
          }
        });
        return this.fragmentView;
        i = 2;
        break;
        label964: this.emptyView.showTextView();
        break label554;
        i = 60;
        break label874;
        label981: f1 = 60.0F;
        break label886;
        label988: j = 5;
        break label895;
        label994: f2 = 0.0F;
        break label905;
        label999: f3 = 14.0F;
      }
      label1007: paramContext = (Context)localObject1;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    Object localObject1;
    label97: label113: Object localObject2;
    if ((paramInt == NotificationCenter.didReceivedNewMessages) && (this.firstLoaded))
    {
      paramArrayOfObject = ((ArrayList)paramArrayOfObject[1]).iterator();
      while (true)
        if (paramArrayOfObject.hasNext())
        {
          localObject1 = (MessageObject)paramArrayOfObject.next();
          if ((((MessageObject)localObject1).messageOwner.action == null) || (!(((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall)))
            continue;
          if (((MessageObject)localObject1).messageOwner.from_id != UserConfig.getClientUserId())
            break;
          i = ((MessageObject)localObject1).messageOwner.to_id.user_id;
          if (((MessageObject)localObject1).messageOwner.from_id == UserConfig.getClientUserId())
          {
            paramInt = 0;
            localObject2 = ((MessageObject)localObject1).messageOwner.action.reason;
            if ((paramInt != 1) || ((!(localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) && (!(localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonBusy))))
              break label477;
            paramInt = 2;
          }
        }
    }
    label477: 
    while (true)
    {
      if (this.calls.size() > 0)
      {
        localObject2 = (CallLogRow)this.calls.get(0);
        if ((((CallLogRow)localObject2).user.id == i) && (((CallLogRow)localObject2).type == paramInt))
        {
          ((CallLogRow)localObject2).calls.add(0, ((MessageObject)localObject1).messageOwner);
          this.listViewAdapter.notifyItemChanged(0);
          break;
          i = ((MessageObject)localObject1).messageOwner.from_id;
          break label97;
          paramInt = 1;
          break label113;
        }
      }
      localObject2 = new CallLogRow(null);
      ((CallLogRow)localObject2).calls = new ArrayList();
      ((CallLogRow)localObject2).calls.add(((MessageObject)localObject1).messageOwner);
      ((CallLogRow)localObject2).user = MessagesController.getInstance().getUser(Integer.valueOf(i));
      ((CallLogRow)localObject2).type = paramInt;
      this.calls.add(0, localObject2);
      this.listViewAdapter.notifyItemInserted(0);
      break;
      if ((paramInt == NotificationCenter.messagesDeleted) && (this.firstLoaded))
      {
        paramArrayOfObject = (ArrayList)paramArrayOfObject[0];
        localObject1 = this.calls.iterator();
        paramInt = i;
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (CallLogRow)((Iterator)localObject1).next();
          Iterator localIterator = ((CallLogRow)localObject2).calls.iterator();
          i = paramInt;
          while (localIterator.hasNext())
          {
            if (!paramArrayOfObject.contains(Integer.valueOf(((TLRPC.Message)localIterator.next()).id)))
              continue;
            localIterator.remove();
            i = 1;
          }
          paramInt = i;
          if (((CallLogRow)localObject2).calls.size() != 0)
            continue;
          ((Iterator)localObject1).remove();
          paramInt = i;
        }
        if ((paramInt != 0) && (this.listViewAdapter != null))
          this.listViewAdapter.notifyDataSetChanged();
      }
      return;
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    Object localObject7 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = CallLogActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = CallLogActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof ProfileSearchCell))
            ((ProfileSearchCell)localView).update(0);
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { LocationCell.class, CustomCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground");
    Object localObject3 = this.listView;
    Object localObject4 = Theme.dialogs_verifiedCheckDrawable;
    localObject3 = new ThemeDescription((View)localObject3, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject4 }, null, "chats_verifiedCheck");
    localObject4 = this.listView;
    Object localObject5 = Theme.dialogs_verifiedDrawable;
    localObject4 = new ThemeDescription((View)localObject4, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject5 }, null, "chats_verifiedBackground");
    localObject5 = this.listView;
    Object localObject6 = Theme.dialogs_offlinePaint;
    localObject5 = new ThemeDescription((View)localObject5, 0, new Class[] { ProfileSearchCell.class }, (Paint)localObject6, null, null, "windowBackgroundWhiteGrayText3");
    localObject6 = this.listView;
    Object localObject8 = Theme.dialogs_onlinePaint;
    localObject6 = new ThemeDescription((View)localObject6, 0, new Class[] { ProfileSearchCell.class }, (Paint)localObject8, null, null, "windowBackgroundWhiteBlueText3");
    localObject8 = this.listView;
    Object localObject9 = Theme.dialogs_namePaint;
    localObject8 = new ThemeDescription((View)localObject8, 0, new Class[] { ProfileSearchCell.class }, (Paint)localObject9, null, null, "chats_name");
    localObject9 = this.listView;
    Object localObject10 = Theme.avatar_photoDrawable;
    Object localObject11 = Theme.avatar_broadcastDrawable;
    localObject9 = new ThemeDescription((View)localObject9, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject10, localObject11 }, null, "avatar_text");
    localObject10 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject7, "avatar_backgroundRed");
    localObject11 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject7, "avatar_backgroundOrange");
    ThemeDescription localThemeDescription16 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject7, "avatar_backgroundViolet");
    ThemeDescription localThemeDescription17 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject7, "avatar_backgroundGreen");
    ThemeDescription localThemeDescription18 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject7, "avatar_backgroundCyan");
    ThemeDescription localThemeDescription19 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject7, "avatar_backgroundBlue");
    localObject7 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject7, "avatar_backgroundPink");
    Object localObject12 = this.listView;
    Object localObject13 = this.greenDrawable;
    Drawable localDrawable1 = this.greenDrawable2;
    Drawable localDrawable2 = Theme.chat_msgCallUpRedDrawable;
    Drawable localDrawable3 = Theme.chat_msgCallDownRedDrawable;
    localObject12 = new ThemeDescription((View)localObject12, 0, new Class[] { View.class }, null, new Drawable[] { localObject13, localDrawable1, localDrawable2, localDrawable3 }, null, "calls_callReceivedGreenIcon");
    localObject13 = this.listView;
    localDrawable1 = this.redDrawable;
    localDrawable2 = Theme.chat_msgCallUpGreenDrawable;
    localDrawable3 = Theme.chat_msgCallDownGreenDrawable;
    return (ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localObject1, localObject2, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localObject3, localObject4, localObject5, localObject6, localObject8, localObject9, localObject10, localObject11, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localObject7, localObject12, new ThemeDescription((View)localObject13, 0, new Class[] { View.class }, null, new Drawable[] { localDrawable1, localDrawable2, localDrawable3 }, null, "calls_callReceivedRedIcon") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    getCalls(0, 50);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
  }

  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 101)
    {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
        VoIPHelper.startCall(this.lastCallUser, getParentActivity(), null);
    }
    else
      return;
    VoIPHelper.permissionDenied(getParentActivity(), null);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null)
      this.listViewAdapter.notifyDataSetChanged();
  }

  private class CallLogRow
  {
    public List<TLRPC.Message> calls;
    public int type;
    public TLRPC.User user;

    private CallLogRow()
    {
    }
  }

  private class CustomCell extends FrameLayout
  {
    public CustomCell(Context arg2)
    {
      super();
    }
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getItemCount()
    {
      int j = CallLogActivity.this.calls.size();
      int i = j;
      if (!CallLogActivity.this.calls.isEmpty())
      {
        i = j;
        if (!CallLogActivity.this.endReached)
          i = j + 1;
      }
      return i;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt < CallLogActivity.this.calls.size())
        return 0;
      if ((!CallLogActivity.this.endReached) && (paramInt == CallLogActivity.this.calls.size()))
        return 1;
      return 2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getAdapterPosition() != CallLogActivity.this.calls.size();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      CallLogActivity.ViewItem localViewItem;
      ProfileSearchCell localProfileSearchCell;
      CallLogActivity.CallLogRow localCallLogRow;
      Object localObject;
      label152: if (paramViewHolder.getItemViewType() == 0)
      {
        localViewItem = (CallLogActivity.ViewItem)paramViewHolder.itemView.getTag();
        localProfileSearchCell = localViewItem.cell;
        localCallLogRow = (CallLogActivity.CallLogRow)CallLogActivity.this.calls.get(paramInt);
        localObject = (TLRPC.Message)localCallLogRow.calls.get(0);
        if (!LocaleController.isRTL)
          break label212;
        paramViewHolder = "‫";
        if (localCallLogRow.calls.size() != 1)
          break label218;
        localObject = new SpannableString(paramViewHolder + "  " + LocaleController.formatDateCallLog(((TLRPC.Message)localObject).date));
        label121: switch (localCallLogRow.type)
        {
        default:
          localProfileSearchCell.setData(localCallLogRow.user, null, null, (CharSequence)localObject, false);
          if ((paramInt == CallLogActivity.this.calls.size() - 1) && (CallLogActivity.this.endReached))
            break;
        case 0:
        case 1:
        case 2:
        }
      }
      for (boolean bool = true; ; bool = false)
      {
        localProfileSearchCell.useSeparator = bool;
        localViewItem.button.setTag(localCallLogRow);
        return;
        label212: paramViewHolder = "";
        break;
        label218: localObject = new SpannableString(String.format(paramViewHolder + "  (%d) %s", new Object[] { Integer.valueOf(localCallLogRow.calls.size()), LocaleController.formatDateCallLog(((TLRPC.Message)localObject).date) }));
        break label121;
        ((SpannableString)localObject).setSpan(CallLogActivity.this.iconOut, paramViewHolder.length(), paramViewHolder.length() + 1, 0);
        break label152;
        ((SpannableString)localObject).setSpan(CallLogActivity.this.iconIn, paramViewHolder.length(), paramViewHolder.length() + 1, 0);
        break label152;
        ((SpannableString)localObject).setSpan(CallLogActivity.this.iconMissed, paramViewHolder.length(), paramViewHolder.length() + 1, 0);
        break label152;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
      case 0:
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new CallLogActivity.CustomCell(CallLogActivity.this, this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ProfileSearchCell localProfileSearchCell = new ProfileSearchCell(this.mContext);
        localProfileSearchCell.setPaddingRight(AndroidUtilities.dp(32.0F));
        paramViewGroup.addView(localProfileSearchCell);
        ImageView localImageView = new ImageView(this.mContext);
        localImageView.setImageResource(2130838035);
        localImageView.setAlpha(214);
        localImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        localImageView.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
        localImageView.setScaleType(ImageView.ScaleType.CENTER);
        localImageView.setOnClickListener(CallLogActivity.this.callBtnClickListener);
        if (LocaleController.isRTL);
        for (paramInt = 3; ; paramInt = 5)
        {
          paramViewGroup.addView(localImageView, LayoutHelper.createFrame(48, 48.0F, paramInt | 0x10, 8.0F, 0.0F, 8.0F, 0.0F));
          paramViewGroup.setTag(new CallLogActivity.ViewItem(CallLogActivity.this, localImageView, localProfileSearchCell));
          break;
        }
        paramViewGroup = new LoadingCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }

  private class ViewItem
  {
    public ImageView button;
    public ProfileSearchCell cell;

    public ViewItem(ImageView paramProfileSearchCell, ProfileSearchCell arg3)
    {
      this.button = paramProfileSearchCell;
      Object localObject;
      this.cell = localObject;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.CallLogActivity
 * JD-Core Version:    0.6.0
 */