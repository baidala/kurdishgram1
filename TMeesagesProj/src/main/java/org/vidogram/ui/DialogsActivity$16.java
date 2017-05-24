package org.vidogram.ui;

import android.view.View;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Adapters.DialogsSearchAdapter;
import org.vidogram.ui.Cells.DialogCell;
import org.vidogram.ui.Cells.HintDialogCell;
import org.vidogram.ui.Cells.ProfileSearchCell;
import org.vidogram.ui.Components.RecyclerListView;

class DialogsActivity$16
  implements ThemeDescription.ThemeDescriptionDelegate
{
  public void didSetColor(int paramInt)
  {
    int i = 0;
    int j = DialogsActivity.access$200(this.this$0).getChildCount();
    paramInt = 0;
    if (paramInt < j)
    {
      localObject = DialogsActivity.access$200(this.this$0).getChildAt(paramInt);
      if ((localObject instanceof ProfileSearchCell))
        ((ProfileSearchCell)localObject).update(0);
      while (true)
      {
        paramInt += 1;
        break;
        if (!(localObject instanceof DialogCell))
          continue;
        ((DialogCell)localObject).update(0);
      }
    }
    Object localObject = DialogsActivity.access$1600(this.this$0).getInnerListView();
    if (localObject != null)
    {
      j = ((RecyclerListView)localObject).getChildCount();
      paramInt = i;
      while (paramInt < j)
      {
        View localView = ((RecyclerListView)localObject).getChildAt(paramInt);
        if ((localView instanceof HintDialogCell))
          ((HintDialogCell)localView).update();
        paramInt += 1;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.16
 * JD-Core Version:    0.6.0
 */