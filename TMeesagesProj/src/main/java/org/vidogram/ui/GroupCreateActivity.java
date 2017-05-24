package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.TL_contact;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Adapters.SearchAdapterHelper;
import org.vidogram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.vidogram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.vidogram.ui.Cells.GroupCreateSectionCell;
import org.vidogram.ui.Cells.GroupCreateUserCell;
import org.vidogram.ui.Components.EditTextBoldCursor;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.GroupCreateDividerItemDecoration;
import org.vidogram.ui.Components.GroupCreateSpan;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.FastScrollAdapter;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;

public class GroupCreateActivity extends BaseFragment
  implements View.OnClickListener, NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private GroupCreateAdapter adapter;
  private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
  private int chatId;
  private int chatType = 0;
  private int containerHeight;
  private GroupCreateSpan currentDeletingSpan;
  private AnimatorSet currentDoneButtonAnimation;
  private GroupCreateActivityDelegate delegate;
  private View doneButton;
  private boolean doneButtonVisible;
  private EditTextBoldCursor editText;
  private EmptyTextProgressView emptyView;
  private int fieldY;
  private boolean ignoreScrollEvent;
  private boolean isAlwaysShare;
  private boolean isGroup;
  private boolean isNeverShare;
  private GroupCreateDividerItemDecoration itemDecoration;
  private RecyclerListView listView;
  private int maxCount = 5000;
  private ScrollView scrollView;
  private boolean searchWas;
  private boolean searching;
  private HashMap<Integer, GroupCreateSpan> selectedContacts = new HashMap();
  private SpansContainer spansContainer;

  public GroupCreateActivity()
  {
  }

  public GroupCreateActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatType = paramBundle.getInt("chatType", 0);
    this.isAlwaysShare = paramBundle.getBoolean("isAlwaysShare", false);
    this.isNeverShare = paramBundle.getBoolean("isNeverShare", false);
    this.isGroup = paramBundle.getBoolean("isGroup", false);
    this.chatId = paramBundle.getInt("chatId");
    if (this.chatType == 0);
    for (int i = MessagesController.getInstance().maxMegagroupCount; ; i = MessagesController.getInstance().maxBroadcastCount)
    {
      this.maxCount = i;
      return;
    }
  }

  private void checkVisibleRows()
  {
    int j = this.listView.getChildCount();
    int i = 0;
    while (i < j)
    {
      Object localObject = this.listView.getChildAt(i);
      if ((localObject instanceof GroupCreateUserCell))
      {
        localObject = (GroupCreateUserCell)localObject;
        TLRPC.User localUser = ((GroupCreateUserCell)localObject).getUser();
        if (localUser != null)
          ((GroupCreateUserCell)localObject).setChecked(this.selectedContacts.containsKey(Integer.valueOf(localUser.id)), true);
      }
      i += 1;
    }
  }

  private void closeSearch()
  {
    this.searching = false;
    this.searchWas = false;
    this.itemDecoration.setSearching(false);
    this.adapter.setSearching(false);
    this.adapter.searchDialogs(null);
    this.listView.setFastScrollVisible(true);
    this.listView.setVerticalScrollBarEnabled(false);
    this.emptyView.setText(LocaleController.getString("NoContacts", 2131166027));
  }

  private boolean onDonePressed()
  {
    Object localObject1;
    Object localObject2;
    if (this.chatType == 2)
    {
      localObject1 = new ArrayList();
      localObject2 = this.selectedContacts.keySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = (Integer)((Iterator)localObject2).next();
        localObject3 = MessagesController.getInputUser(MessagesController.getInstance().getUser((Integer)localObject3));
        if (localObject3 == null)
          continue;
        ((ArrayList)localObject1).add(localObject3);
      }
      MessagesController.getInstance().addUsersToChannel(this.chatId, (ArrayList)localObject1, null);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      localObject1 = new Bundle();
      ((Bundle)localObject1).putInt("chat_id", this.chatId);
      presentFragment(new ChatActivity((Bundle)localObject1), true);
    }
    while (true)
    {
      return true;
      if ((!this.doneButtonVisible) || (this.selectedContacts.isEmpty()))
        return false;
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).addAll(this.selectedContacts.keySet());
      if ((this.isAlwaysShare) || (this.isNeverShare))
      {
        if (this.delegate != null)
          this.delegate.didSelectUsers((ArrayList)localObject1);
        finishFragment();
        continue;
      }
      localObject2 = new Bundle();
      ((Bundle)localObject2).putIntegerArrayList("result", (ArrayList)localObject1);
      ((Bundle)localObject2).putInt("chatType", this.chatType);
      presentFragment(new GroupCreateFinalActivity((Bundle)localObject2));
    }
  }

  private void updateHint()
  {
    if ((!this.isAlwaysShare) && (!this.isNeverShare))
    {
      if (this.chatType == 2)
        this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
    }
    else if (this.chatType != 2)
    {
      if ((!this.doneButtonVisible) || (!this.allSpans.isEmpty()))
        break label279;
      if (this.currentDoneButtonAnimation != null)
        this.currentDoneButtonAnimation.cancel();
      this.currentDoneButtonAnimation = new AnimatorSet();
      this.currentDoneButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[] { 0.0F }) });
      this.currentDoneButtonAnimation.setDuration(180L);
      this.currentDoneButtonAnimation.start();
      this.doneButtonVisible = false;
    }
    label279: 
    do
    {
      return;
      if (this.selectedContacts.isEmpty())
      {
        this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", 2131165952, new Object[] { LocaleController.formatPluralString("Members", this.maxCount) }));
        break;
      }
      this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", 2131165951, new Object[] { Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount) }));
      break;
    }
    while ((this.doneButtonVisible) || (this.allSpans.isEmpty()));
    if (this.currentDoneButtonAnimation != null)
      this.currentDoneButtonAnimation.cancel();
    this.currentDoneButtonAnimation = new AnimatorSet();
    this.currentDoneButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[] { 1.0F }) });
    this.currentDoneButtonAnimation.setDuration(180L);
    this.currentDoneButtonAnimation.start();
    this.doneButtonVisible = true;
  }

  public View createView(Context paramContext)
  {
    int j = 1;
    this.searching = false;
    this.searchWas = false;
    this.allSpans.clear();
    this.selectedContacts.clear();
    this.currentDeletingSpan = null;
    boolean bool;
    label90: Object localObject1;
    Object localObject2;
    if (this.chatType == 2)
    {
      bool = true;
      this.doneButtonVisible = bool;
      this.actionBar.setBackButtonImage(2130837732);
      this.actionBar.setAllowOverlayTitle(true);
      if (this.chatType != 2)
        break label754;
      this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", 2131165444));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            GroupCreateActivity.this.finishFragment();
          do
            return;
          while (paramInt != 1);
          GroupCreateActivity.this.onDonePressed();
        }
      });
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
      if (this.chatType != 2)
      {
        this.doneButton.setScaleX(0.0F);
        this.doneButton.setScaleY(0.0F);
        this.doneButton.setAlpha(0.0F);
      }
      this.fragmentView = new ViewGroup(paramContext)
      {
        protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
        {
          boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
          if ((paramView == GroupCreateActivity.this.listView) || (paramView == GroupCreateActivity.this.emptyView))
            GroupCreateActivity.this.parentLayout.drawHeaderShadow(paramCanvas, GroupCreateActivity.this.scrollView.getMeasuredHeight());
          return bool;
        }

        protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        {
          GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
          GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
          GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
        }

        protected void onMeasure(int paramInt1, int paramInt2)
        {
          int i = View.MeasureSpec.getSize(paramInt1);
          paramInt2 = View.MeasureSpec.getSize(paramInt2);
          setMeasuredDimension(i, paramInt2);
          if ((AndroidUtilities.isTablet()) || (paramInt2 > i));
          for (paramInt1 = AndroidUtilities.dp(144.0F); ; paramInt1 = AndroidUtilities.dp(56.0F))
          {
            GroupCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt1, -2147483648));
            GroupCreateActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), 1073741824));
            GroupCreateActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), 1073741824));
            return;
          }
        }
      };
      localObject1 = (ViewGroup)this.fragmentView;
      this.scrollView = new ScrollView(paramContext)
      {
        public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
        {
          if (GroupCreateActivity.this.ignoreScrollEvent)
          {
            GroupCreateActivity.access$302(GroupCreateActivity.this, false);
            return false;
          }
          paramRect.offset(paramView.getLeft() - paramView.getScrollX(), paramView.getTop() - paramView.getScrollY());
          paramRect.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0F);
          paramRect.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0F);
          return super.requestChildRectangleOnScreen(paramView, paramRect, paramBoolean);
        }
      };
      this.scrollView.setVerticalScrollBarEnabled(false);
      AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
      ((ViewGroup)localObject1).addView(this.scrollView);
      this.spansContainer = new SpansContainer(paramContext);
      this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0F));
      this.editText = new EditTextBoldCursor(paramContext)
      {
        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          if (GroupCreateActivity.this.currentDeletingSpan != null)
          {
            GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
            GroupCreateActivity.access$1502(GroupCreateActivity.this, null);
          }
          return super.onTouchEvent(paramMotionEvent);
        }
      };
      this.editText.setTextSize(1, 18.0F);
      this.editText.setHintColor(Theme.getColor("groupcreate_hintText"));
      this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.editText.setCursorColor(Theme.getColor("groupcreate_cursor"));
      this.editText.setInputType(655536);
      this.editText.setSingleLine(true);
      this.editText.setBackgroundDrawable(null);
      this.editText.setVerticalScrollBarEnabled(false);
      this.editText.setHorizontalScrollBarEnabled(false);
      this.editText.setTextIsSelectable(false);
      this.editText.setPadding(0, 0, 0, 0);
      this.editText.setImeOptions(268435462);
      localObject2 = this.editText;
      if (!LocaleController.isRTL)
        break label906;
      i = 5;
      label405: ((EditTextBoldCursor)localObject2).setGravity(i | 0x10);
      this.spansContainer.addView(this.editText);
      if (this.chatType != 2)
        break label911;
      this.editText.setHintText(LocaleController.getString("AddMutual", 2131165282));
      label449: this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
        {
          return false;
        }

        public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          return false;
        }

        public void onDestroyActionMode(ActionMode paramActionMode)
        {
        }

        public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          return false;
        }
      });
      this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          return (paramInt == 6) && (GroupCreateActivity.this.onDonePressed());
        }
      });
      this.editText.setOnKeyListener(new View.OnKeyListener()
      {
        public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
        {
          if ((paramInt == 67) && (paramKeyEvent.getAction() == 1) && (GroupCreateActivity.this.editText.length() == 0) && (!GroupCreateActivity.this.allSpans.isEmpty()))
          {
            GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan)GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
            GroupCreateActivity.this.updateHint();
            GroupCreateActivity.this.checkVisibleRows();
            return true;
          }
          return false;
        }
      });
      this.editText.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
          if (GroupCreateActivity.this.editText.length() != 0)
          {
            GroupCreateActivity.access$1902(GroupCreateActivity.this, true);
            GroupCreateActivity.access$2002(GroupCreateActivity.this, true);
            GroupCreateActivity.this.adapter.setSearching(true);
            GroupCreateActivity.this.itemDecoration.setSearching(true);
            GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
            GroupCreateActivity.this.listView.setFastScrollVisible(false);
            GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
            GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", 2131166045));
            return;
          }
          GroupCreateActivity.this.closeSearch();
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }
      });
      this.emptyView = new EmptyTextProgressView(paramContext);
      if (!ContactsController.getInstance().isLoadingContacts())
        break label1034;
      this.emptyView.showProgress();
      label537: this.emptyView.setShowAtCenter(true);
      this.emptyView.setText(LocaleController.getString("NoContacts", 2131166027));
      ((ViewGroup)localObject1).addView(this.emptyView);
      localObject2 = new LinearLayoutManager(paramContext, 1, false);
      this.listView = new RecyclerListView(paramContext);
      this.listView.setFastScrollEnabled();
      this.listView.setEmptyView(this.emptyView);
      RecyclerListView localRecyclerListView = this.listView;
      paramContext = new GroupCreateAdapter(paramContext);
      this.adapter = paramContext;
      localRecyclerListView.setAdapter(paramContext);
      this.listView.setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.listView.setVerticalScrollBarEnabled(false);
      paramContext = this.listView;
      if (!LocaleController.isRTL)
        break label1044;
    }
    label906: label911: label1044: for (int i = j; ; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      paramContext = this.listView;
      localObject2 = new GroupCreateDividerItemDecoration();
      this.itemDecoration = ((GroupCreateDividerItemDecoration)localObject2);
      paramContext.addItemDecoration((RecyclerView.ItemDecoration)localObject2);
      ((ViewGroup)localObject1).addView(this.listView);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          boolean bool2 = false;
          if (!(paramView instanceof GroupCreateUserCell));
          label349: label366: 
          while (true)
          {
            return;
            paramView = (GroupCreateUserCell)paramView;
            Object localObject = paramView.getUser();
            if (localObject == null)
              continue;
            boolean bool3 = GroupCreateActivity.this.selectedContacts.containsKey(Integer.valueOf(((TLRPC.User)localObject).id));
            if (bool3)
            {
              localObject = (GroupCreateSpan)GroupCreateActivity.this.selectedContacts.get(Integer.valueOf(((TLRPC.User)localObject).id));
              GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan)localObject);
              GroupCreateActivity.this.updateHint();
              if ((!GroupCreateActivity.this.searching) && (!GroupCreateActivity.this.searchWas))
                break label349;
              AndroidUtilities.showKeyboard(GroupCreateActivity.this.editText);
            }
            while (true)
            {
              if (GroupCreateActivity.this.editText.length() <= 0)
                break label366;
              GroupCreateActivity.this.editText.setText(null);
              return;
              if ((GroupCreateActivity.this.maxCount != 0) && (GroupCreateActivity.this.selectedContacts.size() == GroupCreateActivity.this.maxCount))
                break;
              if ((GroupCreateActivity.this.chatType == 0) && (GroupCreateActivity.this.selectedContacts.size() == MessagesController.getInstance().maxGroupCount))
              {
                paramView = new AlertDialog.Builder(GroupCreateActivity.this.getParentActivity());
                paramView.setTitle(LocaleController.getString("AppName", 2131165319));
                paramView.setMessage(LocaleController.getString("SoftUserLimitAlert", 2131166476));
                paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
                GroupCreateActivity.this.showDialog(paramView.create());
                return;
              }
              MessagesController localMessagesController = MessagesController.getInstance();
              if (!GroupCreateActivity.this.searching);
              for (boolean bool1 = true; ; bool1 = false)
              {
                localMessagesController.putUser((TLRPC.User)localObject, bool1);
                localObject = new GroupCreateSpan(GroupCreateActivity.this.editText.getContext(), (TLRPC.User)localObject);
                GroupCreateActivity.this.spansContainer.addSpan((GroupCreateSpan)localObject);
                ((GroupCreateSpan)localObject).setOnClickListener(GroupCreateActivity.this);
                break;
              }
              bool1 = bool2;
              if (!bool3)
                bool1 = true;
              paramView.setChecked(bool1, true);
            }
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
        {
          if (paramInt == 1)
            AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
        }
      });
      updateHint();
      return this.fragmentView;
      bool = false;
      break;
      label754: if (this.isAlwaysShare)
      {
        if (this.isGroup)
        {
          this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", 2131165301));
          break label90;
        }
        this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", 2131165305));
        break label90;
      }
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.actionBar.setTitle(LocaleController.getString("NeverAllow", 2131166001));
          break label90;
        }
        this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", 2131166005));
        break label90;
      }
      localObject2 = this.actionBar;
      if (this.chatType == 0);
      for (localObject1 = LocaleController.getString("NewGroup", 2131166009); ; localObject1 = LocaleController.getString("NewBroadcastList", 2131166006))
      {
        ((ActionBar)localObject2).setTitle((CharSequence)localObject1);
        break;
      }
      i = 3;
      break label405;
      if (this.isAlwaysShare)
      {
        if (this.isGroup)
        {
          this.editText.setHintText(LocaleController.getString("AlwaysAllowPlaceholder", 2131165302));
          break label449;
        }
        this.editText.setHintText(LocaleController.getString("AlwaysShareWithPlaceholder", 2131165304));
        break label449;
      }
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.editText.setHintText(LocaleController.getString("NeverAllowPlaceholder", 2131166002));
          break label449;
        }
        this.editText.setHintText(LocaleController.getString("NeverShareWithPlaceholder", 2131166004));
        break label449;
      }
      this.editText.setHintText(LocaleController.getString("SendMessageTo", 2131166417));
      break label449;
      this.emptyView.showTextView();
      break label537;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    if (paramInt == NotificationCenter.contactsDidLoaded)
    {
      if (this.emptyView != null)
        this.emptyView.showTextView();
      if (this.adapter != null)
        this.adapter.notifyDataSetChanged();
    }
    do
      while (true)
      {
        return;
        if (paramInt != NotificationCenter.updateInterfaces)
          break;
        if (this.listView == null)
          continue;
        int j = ((Integer)paramArrayOfObject[0]).intValue();
        int k = this.listView.getChildCount();
        paramInt = i;
        if ((j & 0x2) == 0)
        {
          paramInt = i;
          if ((j & 0x1) == 0)
          {
            if ((j & 0x4) == 0)
              continue;
            paramInt = i;
          }
        }
        while (paramInt < k)
        {
          paramArrayOfObject = this.listView.getChildAt(paramInt);
          if ((paramArrayOfObject instanceof GroupCreateUserCell))
            ((GroupCreateUserCell)paramArrayOfObject).update(j);
          paramInt += 1;
        }
      }
    while (paramInt != NotificationCenter.chatDidCreated);
    removeSelfFromStack();
  }

  public int getContainerHeight()
  {
    return this.containerHeight;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    11 local11 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = GroupCreateActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = GroupCreateActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof GroupCreateUserCell))
            ((GroupCreateUserCell)localView).update(0);
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GroupCreateSectionCell.class }, null, null, null, "graySection");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, 0, new Class[] { GroupCreateSectionCell.class }, new String[] { "drawable" }, null, null, null, "groupcreate_sectionShadow");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateSectionCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "checkBox" }, null, null, null, "groupcreate_checkbox");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "checkBox" }, null, null, null, "groupcreate_checkboxCheck");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_onlineText");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_offlineText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    return (ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localObject1, localObject2, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, new ThemeDescription(localRecyclerListView, 0, new Class[] { GroupCreateUserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundPink"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "avatar_backgroundGroupCreateSpanBlue"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "groupcreate_spanBackground"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "groupcreate_spanText"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "avatar_backgroundBlue") };
  }

  public void onClick(View paramView)
  {
    paramView = (GroupCreateSpan)paramView;
    if (paramView.isDeleting())
    {
      this.currentDeletingSpan = null;
      this.spansContainer.removeSpan(paramView);
      updateHint();
      checkVisibleRows();
      return;
    }
    if (this.currentDeletingSpan != null)
      this.currentDeletingSpan.cancelDeleteAnimation();
    this.currentDeletingSpan = paramView;
    paramView.startDeleteAnimation();
  }

  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
  }

  public void onResume()
  {
    super.onResume();
    if (this.editText != null)
      this.editText.requestFocus();
  }

  public void setContainerHeight(int paramInt)
  {
    this.containerHeight = paramInt;
    if (this.spansContainer != null)
      this.spansContainer.requestLayout();
  }

  public void setDelegate(GroupCreateActivityDelegate paramGroupCreateActivityDelegate)
  {
    this.delegate = paramGroupCreateActivityDelegate;
  }

  public static abstract interface GroupCreateActivityDelegate
  {
    public abstract void didSelectUsers(ArrayList<Integer> paramArrayList);
  }

  public class GroupCreateAdapter extends RecyclerListView.FastScrollAdapter
  {
    private ArrayList<TLRPC.User> contacts = new ArrayList();
    private Context context;
    private SearchAdapterHelper searchAdapterHelper;
    private ArrayList<TLRPC.User> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;
    private boolean searching;

    public GroupCreateAdapter(Context arg2)
    {
      this.context = localArrayList;
      ArrayList localArrayList = ContactsController.getInstance().contacts;
      int i = 0;
      if (i < localArrayList.size())
      {
        TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localArrayList.get(i)).user_id));
        if ((localUser == null) || (localUser.self) || (localUser.deleted));
        while (true)
        {
          i += 1;
          break;
          this.contacts.add(localUser);
        }
      }
      this.searchAdapterHelper = new SearchAdapterHelper();
      this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate(GroupCreateActivity.this)
      {
        public void onDataSetChanged()
        {
          GroupCreateActivity.GroupCreateAdapter.this.notifyDataSetChanged();
        }

        public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramHashMap)
        {
        }
      });
    }

    private void updateSearchResults(ArrayList<TLRPC.User> paramArrayList, ArrayList<CharSequence> paramArrayList1)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramArrayList, paramArrayList1)
      {
        public void run()
        {
          GroupCreateActivity.GroupCreateAdapter.access$3002(GroupCreateActivity.GroupCreateAdapter.this, this.val$users);
          GroupCreateActivity.GroupCreateAdapter.access$3102(GroupCreateActivity.GroupCreateAdapter.this, this.val$names);
          GroupCreateActivity.GroupCreateAdapter.this.notifyDataSetChanged();
        }
      });
    }

    public int getItemCount()
    {
      if (this.searching)
      {
        int j = this.searchResult.size();
        int k = this.searchAdapterHelper.getGlobalSearch().size();
        int i = j;
        if (k != 0)
          i = j + (k + 1);
        return i;
      }
      return this.contacts.size();
    }

    public int getItemViewType(int paramInt)
    {
      int j = 1;
      int i = j;
      if (this.searching)
      {
        i = j;
        if (paramInt == this.searchResult.size())
          i = 0;
      }
      return i;
    }

    public String getLetter(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= this.contacts.size()))
        return null;
      TLRPC.User localUser = (TLRPC.User)this.contacts.get(paramInt);
      if (localUser == null)
        return null;
      if (LocaleController.nameDisplayOrder == 1)
      {
        if (!TextUtils.isEmpty(localUser.first_name))
          return localUser.first_name.substring(0, 1).toUpperCase();
        if (!TextUtils.isEmpty(localUser.last_name))
          return localUser.last_name.substring(0, 1).toUpperCase();
      }
      else
      {
        if (!TextUtils.isEmpty(localUser.last_name))
          return localUser.last_name.substring(0, 1).toUpperCase();
        if (!TextUtils.isEmpty(localUser.first_name))
          return localUser.first_name.substring(0, 1).toUpperCase();
      }
      return "";
    }

    public int getPositionForScrollProgress(float paramFloat)
    {
      return (int)(getItemCount() * paramFloat);
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Object localObject2 = null;
      Object localObject4 = null;
      GroupCreateUserCell localGroupCreateUserCell;
      int i;
      int j;
      Object localObject3;
      Object localObject1;
      switch (paramViewHolder.getItemViewType())
      {
      default:
        localGroupCreateUserCell = (GroupCreateUserCell)paramViewHolder.itemView;
        if (this.searching)
        {
          i = this.searchResult.size();
          j = this.searchAdapterHelper.getGlobalSearch().size();
          if ((paramInt >= 0) && (paramInt < i))
          {
            localObject2 = (TLRPC.User)this.searchResult.get(paramInt);
            if (localObject2 == null)
              break label420;
            if (paramInt >= i)
              break label304;
            localObject3 = (CharSequence)this.searchResultNames.get(paramInt);
            paramViewHolder = (RecyclerView.ViewHolder)localObject3;
            localObject1 = localObject4;
            if (localObject3 == null)
              break;
            paramViewHolder = (RecyclerView.ViewHolder)localObject3;
            localObject1 = localObject4;
            if (TextUtils.isEmpty(((TLRPC.User)localObject2).username))
              break;
            paramViewHolder = (RecyclerView.ViewHolder)localObject3;
            localObject1 = localObject4;
            if (!((CharSequence)localObject3).toString().startsWith("@" + ((TLRPC.User)localObject2).username))
              break;
            localObject1 = localObject3;
            paramViewHolder = null;
          }
        }
      case 0:
      }
      while (true)
      {
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
        label200: localGroupCreateUserCell.setUser((TLRPC.User)localObject1, paramViewHolder, (CharSequence)localObject2);
        localGroupCreateUserCell.setChecked(GroupCreateActivity.this.selectedContacts.containsKey(Integer.valueOf(((TLRPC.User)localObject1).id)), false);
        do
        {
          return;
          paramViewHolder = (GroupCreateSectionCell)paramViewHolder.itemView;
        }
        while (!this.searching);
        paramViewHolder.setText(LocaleController.getString("GlobalSearch", 2131165793));
        return;
        if ((paramInt > i) && (paramInt <= j + i))
        {
          localObject2 = (TLRPC.User)this.searchAdapterHelper.getGlobalSearch().get(paramInt - i - 1);
          break;
        }
        localObject2 = null;
        break;
        label304: if ((paramInt > i) && (!TextUtils.isEmpty(((TLRPC.User)localObject2).username)))
        {
          paramViewHolder = this.searchAdapterHelper.getLastFoundUsername();
          if (paramViewHolder.startsWith("@"))
            paramViewHolder = paramViewHolder.substring(1);
          while (true)
          {
            try
            {
              localObject1 = new SpannableStringBuilder(null);
              ((SpannableStringBuilder)localObject1).setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), 0, paramViewHolder.length(), 33);
              paramViewHolder = null;
            }
            catch (Exception paramViewHolder)
            {
              localObject1 = ((TLRPC.User)localObject2).username;
              paramViewHolder = null;
            }
            break;
            localObject1 = (TLRPC.User)this.contacts.get(paramInt);
            paramViewHolder = null;
            break label200;
          }
        }
        label420: paramViewHolder = null;
        localObject1 = localObject4;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
      case 0:
      }
      for (paramViewGroup = new GroupCreateUserCell(this.context, true); ; paramViewGroup = new GroupCreateSectionCell(this.context))
        return new RecyclerListView.Holder(paramViewGroup);
    }

    public void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() == 1)
        ((GroupCreateUserCell)paramViewHolder.itemView).recycle();
    }

    public void searchDialogs(String paramString)
    {
      try
      {
        if (this.searchTimer != null)
          this.searchTimer.cancel();
        if (paramString == null)
        {
          this.searchResult.clear();
          this.searchResultNames.clear();
          this.searchAdapterHelper.queryServerSearch(null, false, false, false);
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask(paramString)
        {
          public void run()
          {
            try
            {
              GroupCreateActivity.GroupCreateAdapter.this.searchTimer.cancel();
              GroupCreateActivity.GroupCreateAdapter.access$2602(GroupCreateActivity.GroupCreateAdapter.this, null);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  GroupCreateActivity.GroupCreateAdapter.this.searchAdapterHelper.queryServerSearch(GroupCreateActivity.GroupCreateAdapter.2.this.val$query, false, false, false);
                  Utilities.searchQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      String str2 = GroupCreateActivity.GroupCreateAdapter.2.this.val$query.trim().toLowerCase();
                      if (str2.length() == 0)
                      {
                        GroupCreateActivity.GroupCreateAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                        return;
                      }
                      String str1 = LocaleController.getInstance().getTranslitString(str2);
                      if ((str2.equals(str1)) || (str1.length() == 0))
                        str1 = null;
                      while (true)
                      {
                        int i;
                        String[] arrayOfString;
                        ArrayList localArrayList1;
                        ArrayList localArrayList2;
                        int j;
                        label137: TLRPC.User localUser;
                        String str3;
                        int n;
                        int m;
                        int k;
                        if (str1 != null)
                        {
                          i = 1;
                          arrayOfString = new String[i + 1];
                          arrayOfString[0] = str2;
                          if (str1 != null)
                            arrayOfString[1] = str1;
                          localArrayList1 = new ArrayList();
                          localArrayList2 = new ArrayList();
                          j = 0;
                          if (j < GroupCreateActivity.GroupCreateAdapter.this.contacts.size())
                          {
                            localUser = (TLRPC.User)GroupCreateActivity.GroupCreateAdapter.this.contacts.get(j);
                            str3 = ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase();
                            str2 = LocaleController.getInstance().getTranslitString(str3);
                            str1 = str2;
                            if (str3.equals(str2))
                              str1 = null;
                            n = arrayOfString.length;
                            m = 0;
                            k = 0;
                          }
                        }
                        else
                        {
                          while (true)
                          {
                            if (k < n)
                            {
                              str2 = arrayOfString[k];
                              if ((!str3.startsWith(str2)) && (!str3.contains(" " + str2)) && ((str1 == null) || ((!str1.startsWith(str2)) && (!str1.contains(" " + str2)))))
                                break label379;
                              i = 1;
                              label329: if (i == 0)
                                break label467;
                              if (i != 1)
                                break label411;
                              localArrayList2.add(AndroidUtilities.generateSearchName(localUser.first_name, localUser.last_name, str2));
                            }
                            while (true)
                            {
                              localArrayList1.add(localUser);
                              j += 1;
                              break label137;
                              i = 0;
                              break;
                              label379: i = m;
                              if (localUser.username == null)
                                break label329;
                              i = m;
                              if (!localUser.username.startsWith(str2))
                                break label329;
                              i = 2;
                              break label329;
                              label411: localArrayList2.add(AndroidUtilities.generateSearchName("@" + localUser.username, null, "@" + str2));
                            }
                            label467: k += 1;
                            m = i;
                          }
                        }
                        GroupCreateActivity.GroupCreateAdapter.this.updateSearchResults(localArrayList1, localArrayList2);
                        return;
                      }
                    }
                  });
                }
              });
              return;
            }
            catch (Exception localException)
            {
              while (true)
                FileLog.e(localException);
            }
          }
        }
        , 200L, 300L);
      }
    }

    public void setSearching(boolean paramBoolean)
    {
      if (this.searching == paramBoolean)
        return;
      this.searching = paramBoolean;
      notifyDataSetChanged();
    }
  }

  private class SpansContainer extends ViewGroup
  {
    private View addingSpan;
    private boolean animationStarted;
    private ArrayList<Animator> animators = new ArrayList();
    private AnimatorSet currentAnimation;
    private View removingSpan;

    public SpansContainer(Context arg2)
    {
      super();
    }

    public void addSpan(GroupCreateSpan paramGroupCreateSpan)
    {
      GroupCreateActivity.this.allSpans.add(paramGroupCreateSpan);
      GroupCreateActivity.this.selectedContacts.put(Integer.valueOf(paramGroupCreateSpan.getUid()), paramGroupCreateSpan);
      GroupCreateActivity.this.editText.setHintVisible(false);
      if (this.currentAnimation != null)
      {
        this.currentAnimation.setupEndValues();
        this.currentAnimation.cancel();
      }
      this.animationStarted = false;
      this.currentAnimation = new AnimatorSet();
      this.currentAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          GroupCreateActivity.SpansContainer.access$602(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$702(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$802(GroupCreateActivity.SpansContainer.this, false);
          GroupCreateActivity.this.editText.setAllowDrawCursor(true);
        }
      });
      this.currentAnimation.setDuration(150L);
      this.addingSpan = paramGroupCreateSpan;
      this.animators.clear();
      this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[] { 0.01F, 1.0F }));
      this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[] { 0.01F, 1.0F }));
      this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[] { 0.0F, 1.0F }));
      addView(paramGroupCreateSpan);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt2 = getChildCount();
      paramInt1 = 0;
      while (paramInt1 < paramInt2)
      {
        View localView = getChildAt(paramInt1);
        localView.layout(0, 0, localView.getMeasuredWidth(), localView.getMeasuredHeight());
        paramInt1 += 1;
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i4 = getChildCount();
      int i2 = View.MeasureSpec.getSize(paramInt1);
      int i3 = i2 - AndroidUtilities.dp(32.0F);
      paramInt2 = AndroidUtilities.dp(12.0F);
      int j = 0;
      paramInt1 = AndroidUtilities.dp(12.0F);
      int i = 0;
      int i1 = 0;
      int n;
      while (i1 < i4)
      {
        View localView = getChildAt(i1);
        if (!(localView instanceof GroupCreateSpan))
        {
          n = j;
          j = paramInt1;
          paramInt1 = i;
          k = paramInt2;
          i1 += 1;
          paramInt2 = k;
          i = paramInt1;
          paramInt1 = j;
          j = n;
          continue;
        }
        localView.measure(View.MeasureSpec.makeMeasureSpec(i2, -2147483648), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0F), 1073741824));
        int k = paramInt2;
        n = i;
        if (localView != this.removingSpan)
        {
          k = paramInt2;
          n = i;
          if (localView.getMeasuredWidth() + i > i3)
          {
            k = paramInt2 + (localView.getMeasuredHeight() + AndroidUtilities.dp(12.0F));
            n = 0;
          }
        }
        paramInt2 = paramInt1;
        i = j;
        if (localView.getMeasuredWidth() + j > i3)
        {
          paramInt2 = paramInt1 + (localView.getMeasuredHeight() + AndroidUtilities.dp(12.0F));
          i = 0;
        }
        paramInt1 = AndroidUtilities.dp(16.0F) + n;
        if (!this.animationStarted)
        {
          if (localView != this.removingSpan)
            break label298;
          localView.setTranslationX(AndroidUtilities.dp(16.0F) + i);
          localView.setTranslationY(paramInt2);
        }
        while (true)
        {
          paramInt1 = n;
          if (localView != this.removingSpan)
            paramInt1 = n + (localView.getMeasuredWidth() + AndroidUtilities.dp(9.0F));
          n = i + (localView.getMeasuredWidth() + AndroidUtilities.dp(9.0F));
          j = paramInt2;
          break;
          label298: if (this.removingSpan != null)
          {
            if (localView.getTranslationX() != paramInt1)
              this.animators.add(ObjectAnimator.ofFloat(localView, "translationX", new float[] { paramInt1 }));
            if (localView.getTranslationY() == k)
              continue;
            this.animators.add(ObjectAnimator.ofFloat(localView, "translationY", new float[] { k }));
            continue;
          }
          localView.setTranslationX(paramInt1);
          localView.setTranslationY(k);
        }
      }
      if (AndroidUtilities.isTablet())
      {
        i1 = AndroidUtilities.dp(366.0F) / 3;
        int m = paramInt2;
        n = i;
        if (i3 - i < i1)
        {
          n = 0;
          m = paramInt2 + AndroidUtilities.dp(44.0F);
        }
        paramInt2 = paramInt1;
        if (i3 - j < i1)
          paramInt2 = paramInt1 + AndroidUtilities.dp(44.0F);
        GroupCreateActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(i3 - n, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0F), 1073741824));
        if (this.animationStarted)
          break label792;
        paramInt1 = AndroidUtilities.dp(44.0F);
        i = n + AndroidUtilities.dp(16.0F);
        GroupCreateActivity.access$102(GroupCreateActivity.this, m);
        if (this.currentAnimation == null)
          break label748;
        paramInt1 = m + AndroidUtilities.dp(44.0F);
        if (GroupCreateActivity.this.containerHeight != paramInt1)
          this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[] { paramInt1 }));
        if (GroupCreateActivity.this.editText.getTranslationX() != i)
          this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[] { i }));
        if (GroupCreateActivity.this.editText.getTranslationY() != GroupCreateActivity.this.fieldY)
          this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", new float[] { GroupCreateActivity.access$100(GroupCreateActivity.this) }));
        GroupCreateActivity.this.editText.setAllowDrawCursor(false);
        this.currentAnimation.playTogether(this.animators);
        this.currentAnimation.start();
        this.animationStarted = true;
      }
      while (true)
      {
        setMeasuredDimension(i2, GroupCreateActivity.this.containerHeight);
        return;
        i1 = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0F)) / 3;
        break;
        label748: GroupCreateActivity.access$202(GroupCreateActivity.this, paramInt2 + paramInt1);
        GroupCreateActivity.this.editText.setTranslationX(i);
        GroupCreateActivity.this.editText.setTranslationY(GroupCreateActivity.this.fieldY);
        continue;
        label792: if ((this.currentAnimation == null) || (GroupCreateActivity.this.ignoreScrollEvent) || (this.removingSpan != null))
          continue;
        GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
      }
    }

    public void removeSpan(GroupCreateSpan paramGroupCreateSpan)
    {
      GroupCreateActivity.access$302(GroupCreateActivity.this, true);
      GroupCreateActivity.this.selectedContacts.remove(Integer.valueOf(paramGroupCreateSpan.getUid()));
      GroupCreateActivity.this.allSpans.remove(paramGroupCreateSpan);
      paramGroupCreateSpan.setOnClickListener(null);
      if (this.currentAnimation != null)
      {
        this.currentAnimation.setupEndValues();
        this.currentAnimation.cancel();
      }
      this.animationStarted = false;
      this.currentAnimation = new AnimatorSet();
      this.currentAnimation.addListener(new AnimatorListenerAdapter(paramGroupCreateSpan)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          GroupCreateActivity.SpansContainer.this.removeView(this.val$span);
          GroupCreateActivity.SpansContainer.access$902(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$702(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$802(GroupCreateActivity.SpansContainer.this, false);
          GroupCreateActivity.this.editText.setAllowDrawCursor(true);
          if (GroupCreateActivity.this.allSpans.isEmpty())
            GroupCreateActivity.this.editText.setHintVisible(true);
        }
      });
      this.currentAnimation.setDuration(150L);
      this.removingSpan = paramGroupCreateSpan;
      this.animators.clear();
      this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[] { 1.0F, 0.01F }));
      this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[] { 1.0F, 0.01F }));
      this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[] { 1.0F, 0.0F }));
      requestLayout();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.GroupCreateActivity
 * JD-Core Version:    0.6.0
 */