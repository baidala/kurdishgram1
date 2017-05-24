package org.vidogram.VidogramUi.a;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.h;
import android.view.View;
import com.google.firebase.crash.FirebaseCrash;
import itman.Vidofilm.e.a;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;

public class b
{
  public c a;
  private RecyclerListView b;
  private BroadcastReceiver c;
  private LinearLayoutManager d;

  private void a(Context paramContext)
  {
    this.c = new BroadcastReceiver()
    {
      public void onReceive(Context paramContext, Intent paramIntent)
      {
        try
        {
          if (b.b(b.this).getAdapter() != null)
          {
            if (b.this.a.getItemCount() == 0)
            {
              b.this.a.a();
              b.b(b.this).setAdapter(b.this.a);
              return;
            }
            b.this.a.a();
            b.this.a.notifyDataSetChanged();
            return;
          }
        }
        catch (java.lang.Exception paramContext)
        {
          FirebaseCrash.a(paramContext);
          return;
        }
        b.this.a.a();
        b.b(b.this).setAdapter(b.this.a);
      }
    };
    h.a(paramContext).a(this.c, new IntentFilter("loadHistoryComplete"));
  }

  private void b(Context paramContext)
  {
    Intent localIntent = new Intent("loadHistoryComplete");
    h.a(paramContext).a(localIntent);
  }

  public RecyclerListView a(Context paramContext, String paramString)
  {
    int i = 1;
    this.b = new RecyclerListView(paramContext);
    this.b.setVerticalScrollBarEnabled(true);
    this.b.setItemAnimator(null);
    this.b.setInstantClick(true);
    this.b.setLayoutAnimation(null);
    this.d = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.d.setOrientation(1);
    this.b.setLayoutManager(this.d);
    RecyclerListView localRecyclerListView = this.b;
    if (LocaleController.isRTL);
    while (true)
    {
      localRecyclerListView.setVerticalScrollbarPosition(i);
      this.a = new c(paramContext, paramString);
      this.b.setAdapter(this.a);
      this.b.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
        }
      });
      this.b.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
        {
        }

        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          paramInt1 = b.a(b.this).findFirstVisibleItemPosition();
          paramInt1 = Math.abs(b.a(b.this).findLastVisibleItemPosition() - paramInt1);
          paramRecyclerView.getAdapter().getItemCount();
          if ((paramInt1 + 1 > 0) && (b.a(b.this).findLastVisibleItemPosition() >= b.this.a.getItemCount() - 10));
        }
      });
      this.b.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener(paramContext)
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          paramView = new BottomSheet.Builder(this.a);
          String str1 = LocaleController.getString("ClearOneHistory", 2131166765);
          String str2 = LocaleController.getString("ClearUserHistories", 2131166766);
          1 local1 = new DialogInterface.OnClickListener(paramInt)
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              paramDialogInterface = new AlertDialog.Builder(b.4.this.a);
              paramDialogInterface.setTitle(LocaleController.getString("AppName", 2131165319));
              if (paramInt == 0)
              {
                paramDialogInterface.setMessage(LocaleController.getString("AreYouSureClearOneHistory", 2131166755));
                paramDialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    a.a(b.4.this.a).a(b.this.a.a(b.4.1.this.a).d());
                    b.a(b.this, b.4.this.a);
                  }
                });
              }
              while (true)
              {
                paramDialogInterface.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                paramDialogInterface.create();
                paramDialogInterface.show();
                return;
                paramDialogInterface.setMessage(LocaleController.getString("AreYouSureClearUserHistory", 2131166756));
                paramDialogInterface.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    paramDialogInterface = b.this.a.a(b.4.1.this.a).b();
                    if (paramDialogInterface.equals(UserConfig.getCurrentUser().id + ""))
                      a.a(b.4.this.a).b(b.this.a.a(b.4.1.this.a).f());
                    while (true)
                    {
                      b.a(b.this, b.4.this.a);
                      return;
                      a.a(b.4.this.a).b(paramDialogInterface);
                    }
                  }
                });
              }
            }
          };
          paramView.setItems(new CharSequence[] { str1, str2 }, new int[] { 2130837665, 2130837664 }, local1);
          paramView.create();
          paramView.show();
          return true;
        }
      });
      a(paramContext);
      return this.b;
      i = 2;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.a.b
 * JD-Core Version:    0.6.0
 */