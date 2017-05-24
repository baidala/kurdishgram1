package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_authorizations;
import org.vidogram.tgnet.TLRPC.TL_account_getAuthorizations;
import org.vidogram.tgnet.TLRPC.TL_account_resetAuthorization;
import org.vidogram.tgnet.TLRPC.TL_auth_resetAuthorizations;
import org.vidogram.tgnet.TLRPC.TL_authorization;
import org.vidogram.tgnet.TLRPC.TL_boolTrue;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.SessionCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class SessionsActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private TLRPC.TL_authorization currentSession = null;
  private int currentSessionRow;
  private int currentSessionSectionRow;
  private LinearLayout emptyLayout;
  private EmptyTextProgressView emptyView;
  private ImageView imageView;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loading;
  private int noOtherSessionsRow;
  private int otherSessionsEndRow;
  private int otherSessionsSectionRow;
  private int otherSessionsStartRow;
  private int otherSessionsTerminateDetail;
  private int rowCount;
  private ArrayList<TLRPC.TL_authorization> sessions = new ArrayList();
  private int terminateAllSessionsDetailRow;
  private int terminateAllSessionsRow;
  private TextView textView1;
  private TextView textView2;

  private void loadSessions(boolean paramBoolean)
  {
    if (this.loading)
      return;
    if (!paramBoolean)
      this.loading = true;
    TLRPC.TL_account_getAuthorizations localTL_account_getAuthorizations = new TLRPC.TL_account_getAuthorizations();
    int i = ConnectionsManager.getInstance().sendRequest(localTL_account_getAuthorizations, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            SessionsActivity.access$602(SessionsActivity.this, false);
            if (this.val$error == null)
            {
              SessionsActivity.this.sessions.clear();
              Iterator localIterator = ((TLRPC.TL_account_authorizations)this.val$response).authorizations.iterator();
              while (localIterator.hasNext())
              {
                TLRPC.TL_authorization localTL_authorization = (TLRPC.TL_authorization)localIterator.next();
                if ((localTL_authorization.flags & 0x1) != 0)
                {
                  SessionsActivity.access$702(SessionsActivity.this, localTL_authorization);
                  continue;
                }
                SessionsActivity.this.sessions.add(localTL_authorization);
              }
              SessionsActivity.this.updateRows();
            }
            if (SessionsActivity.this.listAdapter != null)
              SessionsActivity.this.listAdapter.notifyDataSetChanged();
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
  }

  private void updateRows()
  {
    this.rowCount = 0;
    if (this.currentSession != null)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.currentSessionSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.currentSessionRow = i;
      if (!this.sessions.isEmpty())
        break label132;
      if (this.currentSession == null)
        break label124;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label124: for (this.noOtherSessionsRow = i; ; this.noOtherSessionsRow = -1)
    {
      this.terminateAllSessionsRow = -1;
      this.terminateAllSessionsDetailRow = -1;
      this.otherSessionsSectionRow = -1;
      this.otherSessionsStartRow = -1;
      this.otherSessionsEndRow = -1;
      this.otherSessionsTerminateDetail = -1;
      return;
      this.currentSessionRow = -1;
      this.currentSessionSectionRow = -1;
      break;
    }
    label132: this.noOtherSessionsRow = -1;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.terminateAllSessionsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.terminateAllSessionsDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.otherSessionsSectionRow = i;
    this.otherSessionsStartRow = (this.otherSessionsSectionRow + 1);
    this.otherSessionsEndRow = (this.otherSessionsStartRow + this.sessions.size());
    this.rowCount += this.sessions.size();
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.otherSessionsTerminateDetail = i;
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SessionsTitle", 2131166436));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          SessionsActivity.this.finishFragment();
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.emptyLayout = new LinearLayout(paramContext);
    this.emptyLayout.setOrientation(1);
    this.emptyLayout.setGravity(17);
    this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, 2130837726, "windowBackgroundGrayShadow"));
    this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
    this.imageView = new ImageView(paramContext);
    this.imageView.setImageResource(2130837704);
    this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sessions_devicesImage"), PorterDuff.Mode.MULTIPLY));
    this.emptyLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2));
    this.textView1 = new TextView(paramContext);
    this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
    this.textView1.setGravity(17);
    this.textView1.setTextSize(1, 17.0F);
    this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView1.setText(LocaleController.getString("NoOtherSessions", 2131166037));
    this.emptyLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
    this.textView2 = new TextView(paramContext);
    this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
    this.textView2.setGravity(17);
    this.textView2.setTextSize(1, 17.0F);
    this.textView2.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
    this.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", 2131166038));
    this.emptyLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.showProgress();
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setEmptyView(this.emptyView);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if (paramInt == SessionsActivity.this.terminateAllSessionsRow)
          if (SessionsActivity.this.getParentActivity() != null);
        do
        {
          return;
          paramView = new AlertDialog.Builder(SessionsActivity.this.getParentActivity());
          paramView.setMessage(LocaleController.getString("AreYouSureSessions", 2131165349));
          paramView.setTitle(LocaleController.getString("AppName", 2131165319));
          paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              paramDialogInterface = new TLRPC.TL_auth_resetAuthorizations();
              ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate()
              {
                public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                  {
                    public void run()
                    {
                      if (SessionsActivity.this.getParentActivity() == null)
                        return;
                      if ((this.val$error == null) && ((this.val$response instanceof TLRPC.TL_boolTrue)))
                        Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllSessions", 2131166505), 0).show();
                      while (true)
                      {
                        SessionsActivity.this.finishFragment();
                        return;
                        Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", 2131166533), 0).show();
                      }
                    }
                  });
                  UserConfig.registeredForPush = false;
                  UserConfig.saveConfig(false);
                  MessagesController.getInstance().registerForPush(UserConfig.pushString);
                  ConnectionsManager.getInstance().setUserId(UserConfig.getClientUserId());
                }
              });
            }
          });
          paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          SessionsActivity.this.showDialog(paramView.create());
          return;
        }
        while ((paramInt < SessionsActivity.this.otherSessionsStartRow) || (paramInt >= SessionsActivity.this.otherSessionsEndRow));
        paramView = new AlertDialog.Builder(SessionsActivity.this.getParentActivity());
        paramView.setMessage(LocaleController.getString("TerminateSessionQuestion", 2131166507));
        paramView.setTitle(LocaleController.getString("AppName", 2131165319));
        paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramInt)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            if (SessionsActivity.this.getParentActivity() == null)
              return;
            paramDialogInterface = new AlertDialog(SessionsActivity.this.getParentActivity(), 1);
            paramDialogInterface.setMessage(LocaleController.getString("Loading", 2131165920));
            paramDialogInterface.setCanceledOnTouchOutside(false);
            paramDialogInterface.setCancelable(false);
            paramDialogInterface.show();
            TLRPC.TL_authorization localTL_authorization = (TLRPC.TL_authorization)SessionsActivity.this.sessions.get(this.val$position - SessionsActivity.this.otherSessionsStartRow);
            TLRPC.TL_account_resetAuthorization localTL_account_resetAuthorization = new TLRPC.TL_account_resetAuthorization();
            localTL_account_resetAuthorization.hash = localTL_authorization.hash;
            ConnectionsManager.getInstance().sendRequest(localTL_account_resetAuthorization, new RequestDelegate(paramDialogInterface, localTL_authorization)
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                {
                  public void run()
                  {
                    try
                    {
                      SessionsActivity.2.2.1.this.val$progressDialog.dismiss();
                      if (this.val$error == null)
                      {
                        SessionsActivity.this.sessions.remove(SessionsActivity.2.2.1.this.val$authorization);
                        SessionsActivity.this.updateRows();
                        if (SessionsActivity.this.listAdapter != null)
                          SessionsActivity.this.listAdapter.notifyDataSetChanged();
                      }
                      return;
                    }
                    catch (Exception localException)
                    {
                      while (true)
                        FileLog.e(localException);
                    }
                  }
                });
              }
            });
          }
        });
        paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
        SessionsActivity.this.showDialog(paramView.create());
      }
    });
    return this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.newSessionReceived)
      loadSessions(true);
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class, SessionCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "sessions_devicesImage"), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { SessionCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { SessionCell.class }, new String[] { "onlineTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { SessionCell.class }, new String[] { "onlineTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { SessionCell.class }, new String[] { "detailTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { SessionCell.class }, new String[] { "detailExTextView" }, null, null, null, "windowBackgroundWhiteGrayText3") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    loadSessions(false);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.newSessionReceived);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.newSessionReceived);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
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
      if (SessionsActivity.this.loading)
        return 0;
      return SessionsActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == SessionsActivity.this.terminateAllSessionsRow);
      do
      {
        return 0;
        if ((paramInt == SessionsActivity.this.terminateAllSessionsDetailRow) || (paramInt == SessionsActivity.this.otherSessionsTerminateDetail))
          return 1;
        if ((paramInt == SessionsActivity.this.currentSessionSectionRow) || (paramInt == SessionsActivity.this.otherSessionsSectionRow))
          return 2;
        if (paramInt == SessionsActivity.this.noOtherSessionsRow)
          return 3;
      }
      while ((paramInt != SessionsActivity.this.currentSessionRow) && ((paramInt < SessionsActivity.this.otherSessionsStartRow) || (paramInt >= SessionsActivity.this.otherSessionsEndRow)));
      return 4;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == SessionsActivity.this.terminateAllSessionsRow) || ((i >= SessionsActivity.this.otherSessionsStartRow) && (i < SessionsActivity.this.otherSessionsEndRow));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      boolean bool2 = false;
      switch (paramViewHolder.getItemViewType())
      {
      default:
        paramViewHolder = (SessionCell)paramViewHolder.itemView;
        if (paramInt != SessionsActivity.this.currentSessionRow)
          break;
        localTL_authorization = SessionsActivity.this.currentSession;
        bool1 = bool2;
        if (!SessionsActivity.this.sessions.isEmpty())
          bool1 = true;
        paramViewHolder.setSession(localTL_authorization, bool1);
      case 0:
      case 1:
      case 2:
      case 3:
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
              }
              while (paramInt != SessionsActivity.this.terminateAllSessionsRow);
              paramViewHolder.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
              paramViewHolder.setText(LocaleController.getString("TerminateAllSessions", 2131166505), false);
              return;
              paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
              if (paramInt != SessionsActivity.this.terminateAllSessionsDetailRow)
                continue;
              paramViewHolder.setText(LocaleController.getString("ClearOtherSessionsHelp", 2131165553));
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
              return;
            }
            while (paramInt != SessionsActivity.this.otherSessionsTerminateDetail);
            paramViewHolder.setText(LocaleController.getString("TerminateSessionInfo", 2131166506));
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
            return;
            paramViewHolder = (HeaderCell)paramViewHolder.itemView;
            if (paramInt != SessionsActivity.this.currentSessionSectionRow)
              continue;
            paramViewHolder.setText(LocaleController.getString("CurrentSession", 2131165598));
            return;
          }
          while (paramInt != SessionsActivity.this.otherSessionsSectionRow);
          paramViewHolder.setText(LocaleController.getString("OtherSessions", 2131166169));
          return;
          paramViewHolder = SessionsActivity.this.emptyLayout.getLayoutParams();
        }
        while (paramViewHolder == null);
        int i = AndroidUtilities.dp(220.0F);
        int j = AndroidUtilities.displaySize.y;
        int k = ActionBar.getCurrentActionBarHeight();
        int m = AndroidUtilities.dp(128.0F);
        if (Build.VERSION.SDK_INT >= 21);
        for (paramInt = AndroidUtilities.statusBarHeight; ; paramInt = 0)
        {
          paramViewHolder.height = Math.max(i, j - k - m - paramInt);
          SessionsActivity.this.emptyLayout.setLayoutParams(paramViewHolder);
          return;
        }
      }
      TLRPC.TL_authorization localTL_authorization = (TLRPC.TL_authorization)SessionsActivity.this.sessions.get(paramInt - SessionsActivity.this.otherSessionsStartRow);
      if (paramInt != SessionsActivity.this.otherSessionsEndRow - 1);
      while (true)
      {
        paramViewHolder.setSession(localTL_authorization, bool1);
        return;
        bool1 = false;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new SessionCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      case 0:
      case 1:
      case 2:
      case 3:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = SessionsActivity.this.emptyLayout;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.SessionsActivity
 * JD-Core Version:    0.6.0
 */