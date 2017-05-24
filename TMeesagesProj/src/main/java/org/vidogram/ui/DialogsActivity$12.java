package org.vidogram.ui;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.ui.Adapters.DialogsSearchAdapter;

class DialogsActivity$12 extends RecyclerView.OnScrollListener
{
  public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
  {
    if ((paramInt == 1) && (DialogsActivity.access$000(this.this$0)) && (DialogsActivity.access$1200(this.this$0)))
      AndroidUtilities.hideKeyboard(this.this$0.getParentActivity().getCurrentFocus());
  }

  public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    paramInt1 = 0;
    int i = DialogsActivity.access$3100(this.this$0).findFirstVisibleItemPosition();
    paramInt2 = Math.abs(DialogsActivity.access$3100(this.this$0).findLastVisibleItemPosition() - i) + 1;
    int j = paramRecyclerView.getAdapter().getItemCount();
    if ((DialogsActivity.access$000(this.this$0)) && (DialogsActivity.access$1200(this.this$0)))
      if ((paramInt2 > 0) && (DialogsActivity.access$3100(this.this$0).findLastVisibleItemPosition() == j - 1) && (!DialogsActivity.access$1600(this.this$0).isMessagesSearchEndReached()))
        DialogsActivity.access$1600(this.this$0).loadMoreSearchMessages();
    boolean bool1;
    while (true)
    {
      return;
      if ((paramInt2 > 0) && (DialogsActivity.access$3100(this.this$0).findLastVisibleItemPosition() >= DialogsActivity.access$2700(this.this$0).size() - 10))
      {
        if (MessagesController.getInstance().dialogsEndReached)
          break;
        bool1 = true;
        if ((bool1) || (!MessagesController.getInstance().serverDialogsEndReached))
          MessagesController.getInstance().loadDialogs(-1, 100, bool1);
      }
      if (DialogsActivity.access$800(this.this$0).getVisibility() == 8)
        continue;
      paramRecyclerView = paramRecyclerView.getChildAt(0);
      if (paramRecyclerView == null)
        break label355;
    }
    label355: for (paramInt2 = paramRecyclerView.getTop(); ; paramInt2 = 0)
    {
      if (DialogsActivity.access$3200(this.this$0) == i)
      {
        j = DialogsActivity.access$3300(this.this$0);
        if (paramInt2 < DialogsActivity.access$3300(this.this$0))
        {
          bool1 = true;
          label242: bool2 = bool1;
          if (Math.abs(j - paramInt2) > 1)
            paramInt1 = 1;
        }
      }
      for (bool2 = bool1; ; bool2 = bool1)
      {
        if ((paramInt1 != 0) && (DialogsActivity.access$3400(this.this$0)))
          DialogsActivity.access$1400(this.this$0, bool2);
        DialogsActivity.access$3202(this.this$0, i);
        DialogsActivity.access$3302(this.this$0, paramInt2);
        DialogsActivity.access$3402(this.this$0, true);
        return;
        bool1 = false;
        break;
        bool1 = false;
        break label242;
        bool1 = bool2;
        if (i > DialogsActivity.access$3200(this.this$0))
          bool1 = true;
        paramInt1 = 1;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.12
 * JD-Core Version:    0.6.0
 */