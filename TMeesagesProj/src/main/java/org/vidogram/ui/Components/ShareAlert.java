package org.vidogram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.SendMessagesHelper;
import org.vidogram.messenger.support.widget.GridLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.State;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_exportedMessageLink;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.BottomSheet;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.ShareDialogCell;
import org.vidogram.ui.DialogsActivity;

public class ShareAlert extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate
{
  protected ChatActivityEnterView chatActivityEnterView;
  private Switch checkBox;
  private TextView checkBoxTextView;
  SizeNotifierFrameLayout contentView;
  private boolean copyLinkOnEnd;
  private LinearLayout doneButton;
  private TextView doneButtonBadgeTextView;
  private TextView doneButtonTextView;
  private TLRPC.TL_exportedMessageLink exportedMessageLink;
  private FrameLayout frameLayout;
  private RecyclerListView gridView;
  private boolean isPublicChannel;
  private GridLayoutManager layoutManager;
  private String linkToCopy;
  private ShareDialogsAdapter listAdapter;
  private boolean loadingLink;
  private EditText nameTextView;
  private int scrollOffsetY;
  private ShareSearchAdapter searchAdapter;
  private EmptyTextProgressView searchEmptyView;
  private HashMap<Long, TLRPC.TL_dialog> selectedDialogs = new HashMap();
  private ArrayList<MessageObject> sendingMessageObject;
  private String sendingText;
  private View shadow;
  private Drawable shadowDrawable;
  private int topBeforeSwitch;

  public ShareAlert(Context paramContext, ArrayList<MessageObject> paramArrayList, String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
  {
    super(paramContext, true);
    this.shadowDrawable = paramContext.getResources().getDrawable(2130838062).mutate();
    this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
    this.linkToCopy = paramString2;
    this.sendingMessageObject = paramArrayList;
    this.searchAdapter = new ShareSearchAdapter(paramContext);
    this.isPublicChannel = paramBoolean1;
    this.sendingText = paramString1;
    if (paramBoolean1)
    {
      this.loadingLink = true;
      paramString1 = new TLRPC.TL_channels_exportMessageLink();
      paramString1.id = ((MessageObject)paramArrayList.get(0)).getId();
      paramString1.channel = MessagesController.getInputChannel(((MessageObject)paramArrayList.get(0)).messageOwner.to_id.channel_id);
      ConnectionsManager.getInstance().sendRequest(paramString1, new RequestDelegate(paramContext)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              if (this.val$response != null)
              {
                ShareAlert.access$002(ShareAlert.this, (TLRPC.TL_exportedMessageLink)this.val$response);
                if (ShareAlert.this.copyLinkOnEnd)
                  ShareAlert.this.copyLink(ShareAlert.1.this.val$context);
              }
              ShareAlert.access$302(ShareAlert.this, false);
            }
          });
        }
      });
    }
    this.containerView = new FrameLayout(paramContext)
    {
      private boolean ignoreLayout = false;

      protected void onDraw(Canvas paramCanvas)
      {
        ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        ShareAlert.this.shadowDrawable.draw(paramCanvas);
      }

      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        if ((paramMotionEvent.getAction() == 0) && (ShareAlert.this.scrollOffsetY != 0) && (paramMotionEvent.getY() < ShareAlert.this.scrollOffsetY))
        {
          ShareAlert.this.dismiss();
          return true;
        }
        return super.onInterceptTouchEvent(paramMotionEvent);
      }

      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        ShareAlert.this.updateLayout();
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        int i = View.MeasureSpec.getSize(paramInt2);
        paramInt2 = i;
        if (Build.VERSION.SDK_INT >= 21)
          paramInt2 = i - AndroidUtilities.statusBarHeight;
        int j = Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount());
        i = AndroidUtilities.dp(48.0F);
        j = Math.max(3, (int)Math.ceil(j / 4.0F));
        int k = AndroidUtilities.dp(100.0F);
        j = ShareAlert.backgroundPaddingTop + (j * k + i);
        if (j < paramInt2);
        for (i = 0; ; i = paramInt2 - paramInt2 / 5 * 3 + AndroidUtilities.dp(8.0F))
        {
          if (ShareAlert.this.gridView.getPaddingTop() != i)
          {
            this.ignoreLayout = true;
            ShareAlert.this.gridView.setPadding(0, i, 0, AndroidUtilities.dp(8.0F));
            this.ignoreLayout = false;
          }
          super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(Math.min(j, paramInt2), 1073741824));
          return;
        }
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        return (!ShareAlert.this.isDismissed()) && (super.onTouchEvent(paramMotionEvent));
      }

      public void requestLayout()
      {
        if (this.ignoreLayout)
          return;
        super.requestLayout();
      }
    };
    this.containerView.setWillNotDraw(false);
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.frameLayout = new FrameLayout(paramContext);
    this.frameLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
    this.frameLayout.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    this.doneButton = new LinearLayout(paramContext);
    this.doneButton.setOrientation(0);
    this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
    this.doneButton.setPadding(AndroidUtilities.dp(21.0F), 0, AndroidUtilities.dp(21.0F), 0);
    this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, 48, 53));
    this.doneButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        Object localObject;
        if (((MessageObject)ShareAlert.this.sendingMessageObject.get(0)).messageOwner.media.caption != null)
        {
          localObject = ((MessageObject)ShareAlert.this.sendingMessageObject.get(0)).messageOwner.media;
          if (ShareAlert.this.chatActivityEnterView.getFieldText() == null)
          {
            paramView = null;
            ((TLRPC.MessageMedia)localObject).caption = paramView;
            ((MessageObject)ShareAlert.this.sendingMessageObject.get(0)).caption = ShareAlert.this.chatActivityEnterView.getFieldText();
            label94: if ((!ShareAlert.this.selectedDialogs.isEmpty()) || ((!ShareAlert.this.isPublicChannel) && (ShareAlert.this.linkToCopy == null)))
              break label384;
            if ((ShareAlert.this.linkToCopy != null) || (!ShareAlert.this.loadingLink))
              break label367;
            ShareAlert.access$102(ShareAlert.this, true);
            Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", 2131165920), 0).show();
          }
        }
        while (true)
        {
          ShareAlert.this.dismiss();
          return;
          paramView = ShareAlert.this.chatActivityEnterView.getFieldText().toString();
          break;
          if ((((MessageObject)ShareAlert.this.sendingMessageObject.get(0)).messageOwner.message == null) || (((MessageObject)ShareAlert.this.sendingMessageObject.get(0)).messageOwner.message.length() <= 0))
            break label94;
          localObject = ((MessageObject)ShareAlert.this.sendingMessageObject.get(0)).messageOwner;
          if (ShareAlert.this.chatActivityEnterView.getFieldText() == null)
          {
            paramView = null;
            label286: ((TLRPC.Message)localObject).message = paramView;
            localObject = (MessageObject)ShareAlert.this.sendingMessageObject.get(0);
            if (ShareAlert.this.chatActivityEnterView.getFieldText() != null)
              break label348;
          }
          label348: for (paramView = null; ; paramView = ShareAlert.this.chatActivityEnterView.getFieldText().toString())
          {
            ((MessageObject)localObject).messageText = paramView;
            break;
            paramView = ShareAlert.this.chatActivityEnterView.getFieldText().toString();
            break label286;
          }
          label367: ShareAlert.this.copyLink(ShareAlert.this.getContext());
        }
        label384: if (ShareAlert.this.sendingMessageObject != null)
        {
          paramView = ShareAlert.this.selectedDialogs.entrySet().iterator();
          while (paramView.hasNext())
          {
            localObject = (Map.Entry)paramView.next();
            if (!ShareAlert.this.checkBox.isChecked())
            {
              Iterator localIterator = ShareAlert.this.sendingMessageObject.iterator();
              while (localIterator.hasNext())
              {
                MessageObject localMessageObject = (MessageObject)localIterator.next();
                SendMessagesHelper.getInstance().forwardFromMyName(localMessageObject, ((Long)((Map.Entry)localObject).getKey()).longValue());
              }
              continue;
            }
            SendMessagesHelper.getInstance().sendMessage(ShareAlert.this.sendingMessageObject, ((Long)((Map.Entry)localObject).getKey()).longValue());
          }
        }
        if (ShareAlert.this.sendingText != null)
        {
          paramView = ShareAlert.this.selectedDialogs.entrySet().iterator();
          while (paramView.hasNext())
          {
            localObject = (Map.Entry)paramView.next();
            SendMessagesHelper.getInstance().sendMessage(ShareAlert.this.sendingText, ((Long)((Map.Entry)localObject).getKey()).longValue(), null, null, true, null, null, null);
          }
        }
        ShareAlert.this.dismiss();
      }
    });
    this.doneButtonBadgeTextView = new TextView(paramContext);
    this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButtonBadgeTextView.setTextSize(1, 13.0F);
    this.doneButtonBadgeTextView.setTextColor(Theme.getColor("dialogBadgeText"));
    this.doneButtonBadgeTextView.setGravity(17);
    this.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5F), Theme.getColor("dialogBadgeBackground")));
    this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0F));
    this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
    this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
    this.doneButtonTextView = new TextView(paramContext);
    this.doneButtonTextView.setTextSize(1, 14.0F);
    this.doneButtonTextView.setGravity(17);
    this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
    this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    this.checkBox = new Switch(paramContext);
    this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
      {
        paramCompoundButton = ShareAlert.this.chatActivityEnterView;
        if (ShareAlert.this.checkBox.isChecked());
        for (int i = 8; ; i = 0)
        {
          paramCompoundButton.setVisibility(i);
          if (ShareAlert.this.chatActivityEnterView.getFocusedChild() != null)
            ShareAlert.this.chatActivityEnterView.getFocusedChild().clearFocus();
          ShareAlert.this.chatActivityEnterView.closeKeyboard();
          return;
        }
      }
    });
    this.checkBox.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
      }
    });
    this.checkBoxTextView = new TextView(paramContext);
    this.checkBoxTextView.setText(LocaleController.getString("QouteForward", 2131166784));
    this.checkBoxTextView.setTextSize(1, 11.0F);
    this.frameLayout.addView(this.checkBoxTextView, LayoutHelper.createFrame(-2, 24.0F, 51, 9.0F, 5.0F, 0.0F, 0.0F));
    this.frameLayout.addView(this.checkBox, LayoutHelper.createFrame(-2, 24.0F, 83, 5.0F, 24.0F, 10.0F, 0.0F));
    this.contentView = new SizeNotifierFrameLayout(paramContext)
    {
      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        int n = getChildCount();
        int k;
        if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F))
          k = ShareAlert.this.chatActivityEnterView.getEmojiPadding();
        while (true)
        {
          int m = 0;
          View localView;
          while (true)
          {
            if (m >= n)
              break label445;
            localView = getChildAt(m);
            if (localView.getVisibility() == 8)
            {
              m += 1;
              continue;
              k = 0;
              break;
            }
          }
          FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
          int i1 = localView.getMeasuredWidth();
          int i2 = localView.getMeasuredHeight();
          int j = localLayoutParams.gravity;
          int i = j;
          if (j == -1)
            i = 51;
          switch (i & 0x7 & 0x7)
          {
          default:
            j = localLayoutParams.leftMargin;
            label159: switch (i & 0x70)
            {
            default:
              i = localLayoutParams.topMargin;
              label207: if (ShareAlert.this.chatActivityEnterView.isPopupView(localView))
              {
                if (k == 0)
                  break label437;
                getMeasuredHeight();
              }
            case 48:
            case 16:
            case 80:
            }
          case 1:
          case 5:
          }
          while (true)
          {
            i = getChildAt(1).getMeasuredHeight() + getChildAt(0).getMeasuredHeight();
            localView.layout(j, i, i1 + j, i + i2);
            ShareAlert.this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F + i2 / AndroidUtilities.density, 0.0F, 0.0F));
            ShareAlert.this.shadow.setLayoutParams(LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 48.0F + i2 / AndroidUtilities.density, 0.0F, 0.0F));
            break;
            j = (paramInt3 - paramInt1 - i1) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
            break label159;
            j = paramInt3 - i1 - localLayoutParams.rightMargin;
            break label159;
            i = localLayoutParams.topMargin;
            break label207;
            i = (paramInt4 - k - paramInt2 - i2) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
            break label207;
            i = paramInt4 - k - paramInt2 - i2 - localLayoutParams.bottomMargin;
            break label207;
            label437: getMeasuredHeight();
          }
        }
        label445: notifyHeightChanged();
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        View.MeasureSpec.getMode(paramInt1);
        View.MeasureSpec.getMode(paramInt2);
        int j = View.MeasureSpec.getSize(paramInt1);
        setMeasuredDimension(j, View.MeasureSpec.getSize(paramInt2));
        int k = getChildCount();
        int i = 0;
        if (i < k)
        {
          View localView = getChildAt(i);
          if (localView.getVisibility() == 8);
          while (true)
          {
            i += 1;
            break;
            if (ShareAlert.this.chatActivityEnterView.isPopupView(localView))
            {
              localView.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, 1073741824));
              continue;
            }
            if (ShareAlert.this.chatActivityEnterView.isRecordCircle(localView))
            {
              measureChildWithMargins(localView, paramInt1, 0, paramInt2, 0);
              continue;
            }
            localView.measure(paramInt1, paramInt2);
          }
        }
      }
    };
    this.chatActivityEnterView = new ChatActivityEnterView((Activity)paramContext, this.contentView, null, false);
    this.contentView.addView(this.chatActivityEnterView, this.contentView.getChildCount() - 1, LayoutHelper.createFrame(-1, -1, 51));
    paramArrayList = this.chatActivityEnterView;
    paramString1 = (MessageObject)this.sendingMessageObject.get(0);
    if (!((MessageObject)this.sendingMessageObject.get(0)).isMediaEmpty());
    for (paramBoolean1 = true; ; paramBoolean1 = false)
    {
      paramArrayList.setEditingMessageObject(paramString1, paramBoolean1);
      this.chatActivityEnterView.showEditDoneProgress(false, true);
      this.chatActivityEnterView.doneButtonImage.setVisibility(8);
      paramArrayList = new ImageView(paramContext);
      paramArrayList.setImageResource(2130837741);
      paramArrayList.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
      paramArrayList.setScaleType(ImageView.ScaleType.CENTER);
      paramArrayList.setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
      this.frameLayout.addView(paramArrayList, LayoutHelper.createFrame(48, 48.0F, 51, 52.0F, 0.0F, 0.0F, 0.0F));
      this.nameTextView = new EditText(paramContext);
      this.nameTextView.setHint(LocaleController.getString("ShareSendTo", 2131166455));
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setGravity(19);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setBackgroundDrawable(null);
      this.nameTextView.setHintTextColor(Theme.getColor("dialogTextHint"));
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16385);
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.nameTextView.setTextColor(Theme.getColor("dialogTextBlack"));
      this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -1.0F, 51, 100.0F, 2.0F, 96.0F, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
          paramEditable = ShareAlert.this.nameTextView.getText().toString();
          if (paramEditable.length() != 0)
          {
            if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter)
            {
              ShareAlert.access$2002(ShareAlert.this, ShareAlert.this.getCurrentTop());
              ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
              ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
            if (ShareAlert.this.searchEmptyView != null)
              ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", 2131166045));
          }
          while (true)
          {
            if (ShareAlert.this.searchAdapter != null)
              ShareAlert.this.searchAdapter.searchDialogs(paramEditable);
            return;
            if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter)
              continue;
            int i = ShareAlert.this.getCurrentTop();
            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", 2131166025));
            ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
            ShareAlert.this.listAdapter.notifyDataSetChanged();
            if (i <= 0)
              continue;
            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -i);
          }
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }
      });
      this.gridView = new RecyclerListView(paramContext);
      this.gridView.setTag(Integer.valueOf(13));
      this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      this.gridView.setClipToPadding(false);
      paramArrayList = this.gridView;
      paramString1 = new GridLayoutManager(getContext(), 4);
      this.layoutManager = paramString1;
      paramArrayList.setLayoutManager(paramString1);
      this.gridView.setHorizontalScrollBarEnabled(false);
      this.gridView.setVerticalScrollBarEnabled(false);
      this.gridView.addItemDecoration(new RecyclerView.ItemDecoration()
      {
        public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
        {
          int j = 0;
          paramView = (RecyclerListView.Holder)paramRecyclerView.getChildViewHolder(paramView);
          if (paramView != null)
          {
            int k = paramView.getAdapterPosition();
            if (k % 4 == 0)
            {
              i = 0;
              paramRect.left = i;
              if (k % 4 != 3)
                break label67;
            }
            label67: for (int i = j; ; i = AndroidUtilities.dp(4.0F))
            {
              paramRect.right = i;
              return;
              i = AndroidUtilities.dp(4.0F);
              break;
            }
          }
          paramRect.left = AndroidUtilities.dp(4.0F);
          paramRect.right = AndroidUtilities.dp(4.0F);
        }
      });
      this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
      paramArrayList = this.gridView;
      paramString1 = new ShareDialogsAdapter(paramContext);
      this.listAdapter = paramString1;
      paramArrayList.setAdapter(paramString1);
      this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
      this.gridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if (paramInt < 0)
            return;
          TLRPC.TL_dialog localTL_dialog;
          if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter)
          {
            localTL_dialog = ShareAlert.this.listAdapter.getItem(paramInt);
            label37: if (localTL_dialog == null)
              break label111;
            paramView = (ShareDialogCell)paramView;
            if (!ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localTL_dialog.id)))
              break label113;
            ShareAlert.this.selectedDialogs.remove(Long.valueOf(localTL_dialog.id));
            paramView.setChecked(false, true);
          }
          while (true)
          {
            ShareAlert.this.updateSelectedCount();
            return;
            localTL_dialog = ShareAlert.this.searchAdapter.getItem(paramInt);
            break label37;
            label111: break;
            label113: ShareAlert.this.selectedDialogs.put(Long.valueOf(localTL_dialog.id), localTL_dialog);
            paramView.setChecked(true, true);
          }
        }
      });
      this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          ShareAlert.this.updateLayout();
        }
      });
      this.searchEmptyView = new EmptyTextProgressView(paramContext);
      this.searchEmptyView.setShowAtCenter(true);
      this.searchEmptyView.showTextView();
      this.searchEmptyView.setText(LocaleController.getString("NoChats", 2131166025));
      this.gridView.setEmptyView(this.searchEmptyView);
      this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 96.0F, 0.0F, 0.0F));
      this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
      this.containerView.addView(this.contentView, LayoutHelper.createFrame(-1, -2.0F, 53, 0.0F, 48.0F, 0.0F, 0.0F));
      this.shadow = new View(paramContext);
      this.shadow.setBackgroundResource(2130837728);
      this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
      updateSelectedCount();
      if (!DialogsActivity.dialogsLoaded)
      {
        MessagesController.getInstance().loadDialogs(0, 100, true);
        ContactsController.getInstance().checkInviteText();
        DialogsActivity.dialogsLoaded = true;
      }
      if (this.listAdapter.dialogs.isEmpty())
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
      return;
    }
  }

  private void copyLink(Context paramContext)
  {
    if ((this.exportedMessageLink == null) && (this.linkToCopy == null))
      return;
    while (true)
    {
      try
      {
        ClipboardManager localClipboardManager = (ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard");
        if (this.linkToCopy != null)
        {
          str = this.linkToCopy;
          localClipboardManager.setPrimaryClip(ClipData.newPlainText("label", str));
          Toast.makeText(paramContext, LocaleController.getString("LinkCopied", 2131165909), 0).show();
          return;
        }
      }
      catch (Exception paramContext)
      {
        FileLog.e(paramContext);
        return;
      }
      String str = this.exportedMessageLink.link;
    }
  }

  private int getCurrentTop()
  {
    if (this.gridView.getChildCount() != 0)
    {
      View localView = this.gridView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.gridView.findContainingViewHolder(localView);
      if (localHolder != null)
      {
        int j = this.gridView.getPaddingTop();
        if ((localHolder.getAdapterPosition() == 0) && (localView.getTop() >= 0));
        for (int i = localView.getTop(); ; i = 0)
          return j - i;
      }
    }
    return -1000;
  }

  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    if (this.gridView.getChildCount() <= 0);
    while (true)
    {
      return;
      Object localObject = this.gridView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.gridView.findContainingViewHolder((View)localObject);
      int i = ((View)localObject).getTop() - AndroidUtilities.dp(8.0F);
      if ((i > 0) && (localHolder != null) && (localHolder.getAdapterPosition() == 0));
      while (this.scrollOffsetY != i)
      {
        localObject = this.gridView;
        this.scrollOffsetY = i;
        ((RecyclerListView)localObject).setTopGlowOffset(i);
        this.frameLayout.setTranslationY(this.scrollOffsetY);
        this.contentView.setTranslationY(this.scrollOffsetY);
        this.shadow.setTranslationY(this.scrollOffsetY);
        this.searchEmptyView.setTranslationY(this.scrollOffsetY);
        this.containerView.invalidate();
        return;
        i = 0;
      }
    }
  }

  protected boolean canDismissWithSwipe()
  {
    return false;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.dialogsNeedReload)
    {
      if (this.listAdapter != null)
        this.listAdapter.fetchDialogs();
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
    }
  }

  public void dismiss()
  {
    super.dismiss();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
  }

  public void updateSelectedCount()
  {
    if (this.selectedDialogs.isEmpty())
    {
      this.doneButtonBadgeTextView.setVisibility(8);
      if ((!this.isPublicChannel) && (this.linkToCopy == null))
      {
        this.doneButtonTextView.setTextColor(Theme.getColor("dialogTextGray4"));
        this.doneButton.setEnabled(false);
        this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166409).toUpperCase());
        return;
      }
      this.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
      this.doneButton.setEnabled(true);
      this.doneButtonTextView.setText(LocaleController.getString("CopyLink", 2131165584).toUpperCase());
      return;
    }
    this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(this.selectedDialogs.size()) }));
    this.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue3"));
    this.doneButton.setEnabled(true);
    this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166409).toUpperCase());
  }

  private class ShareDialogsAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context context;
    private int currentCount;
    private ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();

    public ShareDialogsAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
      fetchDialogs();
    }

    public void fetchDialogs()
    {
      this.dialogs.clear();
      int i = 0;
      if (i < MessagesController.getInstance().dialogsServerOnly.size())
      {
        TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
        int j = (int)localTL_dialog.id;
        int k = (int)(localTL_dialog.id >> 32);
        if ((j != 0) && (k != 1))
        {
          if (j <= 0)
            break label84;
          this.dialogs.add(localTL_dialog);
        }
        while (true)
        {
          i += 1;
          break;
          label84: TLRPC.Chat localChat = MessagesController.getInstance().getChat(Integer.valueOf(-j));
          if ((localChat == null) || (ChatObject.isNotInChat(localChat)) || ((ChatObject.isChannel(localChat)) && (!localChat.creator) && (!localChat.editor) && (!localChat.megagroup)))
            continue;
          this.dialogs.add(localTL_dialog);
        }
      }
      notifyDataSetChanged();
    }

    public TLRPC.TL_dialog getItem(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= this.dialogs.size()))
        return null;
      return (TLRPC.TL_dialog)this.dialogs.get(paramInt);
    }

    public int getItemCount()
    {
      return this.dialogs.size();
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (ShareDialogCell)paramViewHolder.itemView;
      TLRPC.TL_dialog localTL_dialog = getItem(paramInt);
      paramViewHolder.setDialog((int)localTL_dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localTL_dialog.id)), null);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ShareDialogCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      return new RecyclerListView.Holder(paramViewGroup);
    }
  }

  public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context context;
    private int lastReqId;
    private int lastSearchId = 0;
    private String lastSearchText;
    private int reqId = 0;
    private ArrayList<DialogSearchResult> searchResult = new ArrayList();
    private Timer searchTimer;

    public ShareSearchAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    private void searchDialogsInternal(String paramString, int paramInt)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramString, paramInt)
      {
        public void run()
        {
          Object localObject3;
          int i;
          String[] arrayOfString;
          Object localObject4;
          int j;
          HashMap localHashMap;
          Object localObject5;
          int k;
          int m;
          while (true)
          {
            try
            {
              localObject3 = this.val$query.trim().toLowerCase();
              if (((String)localObject3).length() != 0)
                continue;
              ShareAlert.ShareSearchAdapter.access$2502(ShareAlert.ShareSearchAdapter.this, -1);
              ShareAlert.ShareSearchAdapter.this.updateSearchResults(new ArrayList(), ShareAlert.ShareSearchAdapter.this.lastSearchId);
              return;
              Object localObject1 = LocaleController.getInstance().getTranslitString((String)localObject3);
              if (((String)localObject3).equals(localObject1))
                break label1618;
              if (((String)localObject1).length() != 0)
                break label1615;
              break label1618;
              arrayOfString = new String[i + 1];
              arrayOfString[0] = localObject3;
              if (localObject1 == null)
                continue;
              arrayOfString[1] = localObject1;
              localObject1 = new ArrayList();
              localObject4 = new ArrayList();
              i = 0;
              j = 0;
              localHashMap = new HashMap();
              localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
              if (!((SQLiteCursor)localObject3).next())
                break;
              long l = ((SQLiteCursor)localObject3).longValue(0);
              localObject5 = new ShareAlert.ShareSearchAdapter.DialogSearchResult(ShareAlert.ShareSearchAdapter.this, null);
              ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject5).date = ((SQLiteCursor)localObject3).intValue(1);
              localHashMap.put(Long.valueOf(l), localObject5);
              k = (int)l;
              m = (int)(l >> 32);
              if ((k == 0) || (m == 1))
                continue;
              if (k <= 0)
                break label273;
              if (((ArrayList)localObject1).contains(Integer.valueOf(k)))
                continue;
              ((ArrayList)localObject1).add(Integer.valueOf(k));
              continue;
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              return;
            }
            label268: i = 0;
            continue;
            label273: m = -k;
            if (((ArrayList)localObject4).contains(Integer.valueOf(m)))
              continue;
            ((ArrayList)localObject4).add(Integer.valueOf(-k));
          }
          ((SQLiteCursor)localObject3).dispose();
          label356: String str1;
          Object localObject2;
          label395: label418: label427: String str2;
          if (!localException.isEmpty())
          {
            localObject5 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[] { TextUtils.join(",", localException) }), new Object[0]);
            i = j;
            if (((SQLiteCursor)localObject5).next())
            {
              str1 = ((SQLiteCursor)localObject5).stringValue(2);
              localObject2 = LocaleController.getInstance().getTranslitString(str1);
              if (!str1.equals(localObject2))
                break label1612;
              localObject2 = null;
              j = str1.lastIndexOf(";;;");
              if (j == -1)
                break label1606;
              localObject3 = str1.substring(j + 3);
              m = arrayOfString.length;
              k = 0;
              j = 0;
              if (k < m)
              {
                str2 = arrayOfString[k];
                if ((str1.startsWith(str2)) || (str1.contains(" " + str2)))
                  break label1631;
                if (localObject2 != null)
                {
                  if (((String)localObject2).startsWith(str2))
                    break label1631;
                  if (((String)localObject2).contains(" " + str2))
                  {
                    break label1631;
                    label523: if (j == 0)
                      break label1639;
                    localObject3 = ((SQLiteCursor)localObject5).byteBufferValue(0);
                    if (localObject3 == null)
                      break label1600;
                    localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                    ((NativeByteBuffer)localObject3).reuse();
                    localObject3 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(((TLRPC.User)localObject2).id));
                    if (((TLRPC.User)localObject2).status != null)
                      ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject5).intValue(1);
                    if (j != 1)
                      break label673;
                  }
                }
                label673: for (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2); ; ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
                {
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).dialog.id = ((TLRPC.User)localObject2).id;
                  i += 1;
                  break label1636;
                  if ((localObject3 == null) || (!((String)localObject3).startsWith(str2)))
                    break label1603;
                  j = 2;
                  break;
                }
              }
            }
            else
            {
              ((SQLiteCursor)localObject5).dispose();
            }
          }
          else
          {
            j = i;
            if (!((ArrayList)localObject4).isEmpty())
            {
              localObject4 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject4) }), new Object[0]);
              label783: 
              while (((SQLiteCursor)localObject4).next())
              {
                localObject5 = ((SQLiteCursor)localObject4).stringValue(1);
                localObject3 = LocaleController.getInstance().getTranslitString((String)localObject5);
                localObject2 = localObject3;
                if (!((String)localObject5).equals(localObject3))
                  break label1646;
                localObject2 = null;
                break label1646;
                label829: if (j >= arrayOfString.length)
                  break label1659;
                localObject3 = arrayOfString[j];
                if ((!((String)localObject5).startsWith((String)localObject3)) && (!((String)localObject5).contains(" " + (String)localObject3)) && ((localObject2 == null) || ((!((String)localObject2).startsWith((String)localObject3)) && (!((String)localObject2).contains(" " + (String)localObject3)))))
                  break label1654;
                localObject5 = ((SQLiteCursor)localObject4).byteBufferValue(0);
                if (localObject5 == null)
                  continue;
                localObject2 = TLRPC.Chat.TLdeserialize((AbstractSerializedData)localObject5, ((NativeByteBuffer)localObject5).readInt32(false), false);
                ((NativeByteBuffer)localObject5).reuse();
                if ((localObject2 == null) || (ChatObject.isNotInChat((TLRPC.Chat)localObject2)) || ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (!((TLRPC.Chat)localObject2).creator) && (!((TLRPC.Chat)localObject2).editor) && (!((TLRPC.Chat)localObject2).megagroup)))
                  break label1597;
                localObject5 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(-((TLRPC.Chat)localObject2).id));
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject5).name = AndroidUtilities.generateSearchName(((TLRPC.Chat)localObject2).title, null, (String)localObject3);
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject5).object = ((TLObject)localObject2);
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject5).dialog.id = (-((TLRPC.Chat)localObject2).id);
                i += 1;
                break label1651;
              }
              ((SQLiteCursor)localObject4).dispose();
              j = i;
            }
            localObject4 = new ArrayList(j);
            localObject2 = localHashMap.values().iterator();
            while (((Iterator)localObject2).hasNext())
            {
              localObject3 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)((Iterator)localObject2).next();
              if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object == null) || (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name == null))
                continue;
              ((ArrayList)localObject4).add(localObject3);
            }
            localObject5 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
          }
          label1666: label1673: 
          while (true)
          {
            label1161: if (((SQLiteCursor)localObject5).next())
            {
              if (localHashMap.containsKey(Long.valueOf(((SQLiteCursor)localObject5).intValue(3))))
                continue;
              str1 = ((SQLiteCursor)localObject5).stringValue(2);
              localObject2 = LocaleController.getInstance().getTranslitString(str1);
              if (!str1.equals(localObject2))
                break label1594;
              localObject2 = null;
              label1218: i = str1.lastIndexOf(";;;");
              if (i == -1)
                break label1588;
              localObject3 = str1.substring(i + 3);
              label1241: m = arrayOfString.length;
              k = 0;
              j = 0;
            }
            while (true)
            {
              if (j >= m)
                break label1673;
              str2 = arrayOfString[j];
              if ((!str1.startsWith(str2)) && (!str1.contains(" " + str2)))
                if (localObject2 != null)
                {
                  if (((String)localObject2).startsWith(str2))
                    break label1661;
                  if (((String)localObject2).contains(" " + str2))
                    break label1661;
                }
              while (true)
              {
                label1346: if (i == 0)
                  break label1666;
                localObject3 = ((SQLiteCursor)localObject5).byteBufferValue(0);
                if (localObject3 == null)
                  break label1161;
                localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                ((NativeByteBuffer)localObject3).reuse();
                localObject3 = new ShareAlert.ShareSearchAdapter.DialogSearchResult(ShareAlert.ShareSearchAdapter.this, null);
                if (((TLRPC.User)localObject2).status != null)
                  ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject5).intValue(1);
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).dialog.id = ((TLRPC.User)localObject2).id;
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                if (i == 1);
                for (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2); ; ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
                {
                  ((ArrayList)localObject4).add(localObject3);
                  break;
                  i = k;
                  if (localObject3 == null)
                    break label1346;
                  i = k;
                  if (!((String)localObject3).startsWith(str2))
                    break label1346;
                  i = 2;
                  break label1346;
                }
                ((SQLiteCursor)localObject5).dispose();
                Collections.sort((List)localObject4, new Comparator()
                {
                  public int compare(ShareAlert.ShareSearchAdapter.DialogSearchResult paramDialogSearchResult1, ShareAlert.ShareSearchAdapter.DialogSearchResult paramDialogSearchResult2)
                  {
                    if (paramDialogSearchResult1.date < paramDialogSearchResult2.date)
                      return 1;
                    if (paramDialogSearchResult1.date > paramDialogSearchResult2.date)
                      return -1;
                    return 0;
                  }
                });
                ShareAlert.ShareSearchAdapter.this.updateSearchResults((ArrayList)localObject4, this.val$searchId);
                return;
                label1588: localObject3 = null;
                break label1241;
                label1594: break label1218;
                label1597: break label1651;
                label1600: break label1636;
                label1603: break label523;
                label1606: localObject3 = null;
                break label418;
                label1612: break label395;
                label1615: break label1621;
                label1618: localObject2 = null;
                label1621: if (localObject2 == null)
                  break label268;
                i = 1;
                break;
                label1631: j = 1;
                break label523;
                label1636: break label356;
                label1639: k += 1;
                break label427;
                label1646: j = 0;
                break label829;
                label1651: break label783;
                label1654: j += 1;
                break label829;
                label1659: break label783;
                label1661: i = 1;
              }
              j += 1;
              k = i;
            }
          }
        }
      });
    }

    private void updateSearchResults(ArrayList<DialogSearchResult> paramArrayList, int paramInt)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramInt, paramArrayList)
      {
        public void run()
        {
          int j = 1;
          if (this.val$searchId != ShareAlert.ShareSearchAdapter.this.lastSearchId)
            return;
          int i = 0;
          if (i < this.val$result.size())
          {
            Object localObject = (ShareAlert.ShareSearchAdapter.DialogSearchResult)this.val$result.get(i);
            if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object instanceof TLRPC.User))
            {
              localObject = (TLRPC.User)((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object;
              MessagesController.getInstance().putUser((TLRPC.User)localObject, true);
            }
            while (true)
            {
              i += 1;
              break;
              if (!(((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object instanceof TLRPC.Chat))
                continue;
              localObject = (TLRPC.Chat)((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object;
              MessagesController.getInstance().putChat((TLRPC.Chat)localObject, true);
            }
          }
          if ((!ShareAlert.ShareSearchAdapter.this.searchResult.isEmpty()) && (this.val$result.isEmpty()))
          {
            i = 1;
            label130: if ((!ShareAlert.ShareSearchAdapter.this.searchResult.isEmpty()) || (!this.val$result.isEmpty()))
              break label263;
          }
          while (true)
          {
            if (i != 0)
              ShareAlert.access$2002(ShareAlert.this, ShareAlert.this.getCurrentTop());
            ShareAlert.ShareSearchAdapter.access$2802(ShareAlert.ShareSearchAdapter.this, this.val$result);
            ShareAlert.ShareSearchAdapter.this.notifyDataSetChanged();
            if ((j != 0) || (i != 0) || (ShareAlert.this.topBeforeSwitch <= 0))
              break;
            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
            ShareAlert.access$2002(ShareAlert.this, -1000);
            return;
            i = 0;
            break label130;
            label263: j = 0;
          }
        }
      });
    }

    public TLRPC.TL_dialog getItem(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= this.searchResult.size()))
        return null;
      return ((DialogSearchResult)this.searchResult.get(paramInt)).dialog;
    }

    public int getItemCount()
    {
      return this.searchResult.size();
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (ShareDialogCell)paramViewHolder.itemView;
      DialogSearchResult localDialogSearchResult = (DialogSearchResult)this.searchResult.get(paramInt);
      paramViewHolder.setDialog((int)localDialogSearchResult.dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localDialogSearchResult.dialog.id)), localDialogSearchResult.name);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ShareDialogCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      return new RecyclerListView.Holder(paramViewGroup);
    }

    public void searchDialogs(String paramString)
    {
      if ((paramString != null) && (this.lastSearchText != null) && (paramString.equals(this.lastSearchText)))
        return;
      this.lastSearchText = paramString;
      try
      {
        if (this.searchTimer != null)
        {
          this.searchTimer.cancel();
          this.searchTimer = null;
        }
        if ((paramString == null) || (paramString.length() == 0))
        {
          this.searchResult.clear();
          ShareAlert.access$2002(ShareAlert.this, ShareAlert.this.getCurrentTop());
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
        int i = this.lastSearchId + 1;
        this.lastSearchId = i;
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask(paramString, i)
        {
          public void run()
          {
            try
            {
              cancel();
              ShareAlert.ShareSearchAdapter.this.searchTimer.cancel();
              ShareAlert.ShareSearchAdapter.access$2902(ShareAlert.ShareSearchAdapter.this, null);
              ShareAlert.ShareSearchAdapter.this.searchDialogsInternal(this.val$query, this.val$searchId);
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

    private class DialogSearchResult
    {
      public int date;
      public TLRPC.TL_dialog dialog = new TLRPC.TL_dialog();
      public CharSequence name;
      public TLObject object;

      private DialogSearchResult()
      {
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.ShareAlert
 * JD-Core Version:    0.6.0
 */