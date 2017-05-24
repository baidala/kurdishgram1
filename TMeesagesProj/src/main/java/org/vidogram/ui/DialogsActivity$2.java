package org.vidogram.ui;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import itman.Vidofilm.tabLayout.CommonTabLayout;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessagesController;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.Adapters.DialogsAdapter;
import org.vidogram.ui.Adapters.DialogsSearchAdapter;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.RadialProgressView;
import org.vidogram.ui.Components.RecyclerListView;

class DialogsActivity$2 extends ActionBarMenuItem.ActionBarMenuItemSearchListener
{
  public boolean canCollapseSearch()
  {
    if (DialogsActivity.access$300(this.this$0) != null)
    {
      this.this$0.finishFragment();
      return false;
    }
    return true;
  }

  public void onSearchCollapse()
  {
    DialogsActivity.access$002(this.this$0, false);
    DialogsActivity.access$1202(this.this$0, false);
    if (DialogsActivity.access$200(this.this$0) != null)
    {
      DialogsActivity.access$400(this.this$0).setVisibility(8);
      if ((!MessagesController.getInstance().loadingDialogs) || (!MessagesController.getInstance().dialogs.isEmpty()))
        break label298;
      DialogsActivity.access$600(this.this$0).setVisibility(8);
      DialogsActivity.access$200(this.this$0).setEmptyView(DialogsActivity.access$500(this.this$0));
    }
    while (true)
    {
      if (!DialogsActivity.access$700(this.this$0))
      {
        DialogsActivity.access$800(this.this$0).setVisibility(0);
        DialogsActivity.access$1302(this.this$0, true);
        DialogsActivity.access$800(this.this$0).setTranslationY(AndroidUtilities.dp(100.0F));
        DialogsActivity.access$1400(this.this$0, false);
      }
      if (!DialogsActivity.access$900(this.this$0))
        DialogsActivity.access$1000(this.this$0).setVisibility(0);
      if (DialogsActivity.access$200(this.this$0).getAdapter() != DialogsActivity.access$1500(this.this$0))
      {
        DialogsActivity.access$200(this.this$0).setAdapter(DialogsActivity.access$1500(this.this$0));
        DialogsActivity.access$1500(this.this$0).notifyDataSetChanged();
      }
      if (DialogsActivity.access$1600(this.this$0) != null)
        DialogsActivity.access$1600(this.this$0).searchDialogs(null);
      DialogsActivity.access$1100(this.this$0);
      if (DialogsActivity.access$1700(this.this$0) != 0)
        break;
      DialogsActivity.access$100(this.this$0).setVisibility(0);
      DialogsActivity.access$200(this.this$0).setVisibility(8);
      DialogsActivity.access$600(this.this$0).setVisibility(8);
      DialogsActivity.access$800(this.this$0).setVisibility(8);
      return;
      label298: DialogsActivity.access$500(this.this$0).setVisibility(8);
      DialogsActivity.access$200(this.this$0).setEmptyView(DialogsActivity.access$600(this.this$0));
    }
    DialogsActivity.access$100(this.this$0).setVisibility(8);
  }

  public void onSearchExpand()
  {
    DialogsActivity.access$002(this.this$0, true);
    DialogsActivity.access$100(this.this$0).setVisibility(8);
    if (DialogsActivity.access$200(this.this$0) != null)
    {
      if (DialogsActivity.access$300(this.this$0) != null)
      {
        DialogsActivity.access$200(this.this$0).setEmptyView(DialogsActivity.access$400(this.this$0));
        DialogsActivity.access$500(this.this$0).setVisibility(8);
        DialogsActivity.access$600(this.this$0).setVisibility(8);
      }
      if (!DialogsActivity.access$700(this.this$0))
        DialogsActivity.access$800(this.this$0).setVisibility(8);
      if (!DialogsActivity.access$900(this.this$0))
        DialogsActivity.access$1000(this.this$0).setVisibility(8);
    }
    DialogsActivity.access$1100(this.this$0);
  }

  public void onTextChanged(EditText paramEditText)
  {
    paramEditText = paramEditText.getText().toString();
    if ((paramEditText.length() != 0) || ((DialogsActivity.access$1600(this.this$0) != null) && (DialogsActivity.access$1600(this.this$0).hasRecentRearch())))
    {
      DialogsActivity.access$1202(this.this$0, true);
      if ((DialogsActivity.access$1600(this.this$0) != null) && (DialogsActivity.access$200(this.this$0).getAdapter() != DialogsActivity.access$1600(this.this$0)))
      {
        DialogsActivity.access$200(this.this$0).setAdapter(DialogsActivity.access$1600(this.this$0));
        DialogsActivity.access$1600(this.this$0).notifyDataSetChanged();
      }
      if ((DialogsActivity.access$400(this.this$0) != null) && (DialogsActivity.access$200(this.this$0).getEmptyView() != DialogsActivity.access$400(this.this$0)))
      {
        DialogsActivity.access$600(this.this$0).setVisibility(8);
        DialogsActivity.access$500(this.this$0).setVisibility(8);
        DialogsActivity.access$400(this.this$0).showTextView();
        DialogsActivity.access$200(this.this$0).setEmptyView(DialogsActivity.access$400(this.this$0));
      }
    }
    if (DialogsActivity.access$1600(this.this$0) != null)
      DialogsActivity.access$1600(this.this$0).searchDialogs(paramEditText);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.2
 * JD-Core Version:    0.6.0
 */