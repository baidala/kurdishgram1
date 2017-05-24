package org.vidogram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.ContactsController.Contact;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.TL_contact;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.Cells.DividerCell;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.LetterSectionCell;
import org.vidogram.ui.Cells.TextCell;
import org.vidogram.ui.Cells.UserCell;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.SectionsAdapter;

public class ContactsAdapter extends RecyclerListView.SectionsAdapter
{
  private HashMap<Integer, ?> checkedMap;
  private HashMap<Integer, TLRPC.User> ignoreUsers;
  private boolean isAdmin;
  private Context mContext;
  private boolean needPhonebook;
  private int onlyUsers;
  private boolean scrolling;

  public ContactsAdapter(Context paramContext, int paramInt, boolean paramBoolean1, HashMap<Integer, TLRPC.User> paramHashMap, boolean paramBoolean2)
  {
    this.mContext = paramContext;
    this.onlyUsers = paramInt;
    this.needPhonebook = paramBoolean1;
    this.ignoreUsers = paramHashMap;
    this.isAdmin = paramBoolean2;
  }

  public int getCountForSection(int paramInt)
  {
    HashMap localHashMap;
    ArrayList localArrayList;
    label31: int i;
    if (this.onlyUsers == 2)
    {
      localHashMap = ContactsController.getInstance().usersMutualSectionsDict;
      if (this.onlyUsers != 2)
        break label107;
      localArrayList = ContactsController.getInstance().sortedUsersMutualSectionsArray;
      if ((this.onlyUsers == 0) || (this.isAdmin))
        break label118;
      if (paramInt >= localArrayList.size())
        break label196;
      i = ((ArrayList)localHashMap.get(localArrayList.get(paramInt))).size();
      if (paramInt == localArrayList.size() - 1)
      {
        paramInt = i;
        if (!this.needPhonebook);
      }
      else
      {
        paramInt = i + 1;
      }
    }
    label107: label118: 
    do
    {
      return paramInt;
      localHashMap = ContactsController.getInstance().usersSectionsDict;
      break;
      localArrayList = ContactsController.getInstance().sortedUsersSectionsArray;
      break label31;
      if (paramInt == 0)
      {
        if ((this.needPhonebook) || (this.isAdmin))
          return 2;
        return 4;
      }
      if (paramInt - 1 >= localArrayList.size())
        break label196;
      i = ((ArrayList)localHashMap.get(localArrayList.get(paramInt - 1))).size();
      if (paramInt - 1 != localArrayList.size() - 1)
        break label192;
      paramInt = i;
    }
    while (!this.needPhonebook);
    label192: return i + 1;
    label196: if (this.needPhonebook)
      return ContactsController.getInstance().phoneBookContacts.size();
    return 0;
  }

  public Object getItem(int paramInt1, int paramInt2)
  {
    Object localObject;
    if (this.onlyUsers == 2)
    {
      localObject = ContactsController.getInstance().usersMutualSectionsDict;
      if (this.onlyUsers != 2)
        break label107;
    }
    label107: for (ArrayList localArrayList = ContactsController.getInstance().sortedUsersMutualSectionsArray; ; localArrayList = ContactsController.getInstance().sortedUsersSectionsArray)
    {
      if ((this.onlyUsers == 0) || (this.isAdmin))
        break label120;
      if (paramInt1 >= localArrayList.size())
        break label118;
      localObject = (ArrayList)((HashMap)localObject).get(localArrayList.get(paramInt1));
      if (paramInt2 >= ((ArrayList)localObject).size())
        break label118;
      return MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)((ArrayList)localObject).get(paramInt2)).user_id));
      localObject = ContactsController.getInstance().usersSectionsDict;
      break;
    }
    label118: return null;
    label120: if (paramInt1 == 0)
      return null;
    if (paramInt1 - 1 < localArrayList.size())
    {
      localObject = (ArrayList)((HashMap)localObject).get(localArrayList.get(paramInt1 - 1));
      if (paramInt2 < ((ArrayList)localObject).size())
        return MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)((ArrayList)localObject).get(paramInt2)).user_id));
      return null;
    }
    if (this.needPhonebook)
      return ContactsController.getInstance().phoneBookContacts.get(paramInt2);
    return null;
  }

  public int getItemViewType(int paramInt1, int paramInt2)
  {
    int i = 0;
    HashMap localHashMap;
    if (this.onlyUsers == 2)
    {
      localHashMap = ContactsController.getInstance().usersMutualSectionsDict;
      if (this.onlyUsers != 2)
        break label82;
    }
    label82: for (ArrayList localArrayList = ContactsController.getInstance().sortedUsersMutualSectionsArray; ; localArrayList = ContactsController.getInstance().sortedUsersSectionsArray)
    {
      if ((this.onlyUsers == 0) || (this.isAdmin))
        break label95;
      if (paramInt2 >= ((ArrayList)localHashMap.get(localArrayList.get(paramInt1))).size())
        break label93;
      return 0;
      localHashMap = ContactsController.getInstance().usersSectionsDict;
      break;
    }
    label93: return 3;
    label95: if (paramInt1 == 0)
    {
      if (((!this.needPhonebook) && (!this.isAdmin)) || ((paramInt2 == 1) || (paramInt2 == 3)))
        return 2;
    }
    else if (paramInt1 - 1 < localArrayList.size())
    {
      if (paramInt2 < ((ArrayList)localHashMap.get(localArrayList.get(paramInt1 - 1))).size());
      for (paramInt1 = i; ; paramInt1 = 3)
        return paramInt1;
    }
    return 1;
  }

  public String getLetter(int paramInt)
  {
    if (this.onlyUsers == 2);
    for (ArrayList localArrayList = ContactsController.getInstance().sortedUsersMutualSectionsArray; ; localArrayList = ContactsController.getInstance().sortedUsersSectionsArray)
    {
      int i = getSectionForPosition(paramInt);
      paramInt = i;
      if (i == -1)
        paramInt = localArrayList.size() - 1;
      if ((paramInt <= 0) || (paramInt > localArrayList.size()))
        break;
      return (String)localArrayList.get(paramInt - 1);
    }
    return null;
  }

  public int getPositionForScrollProgress(float paramFloat)
  {
    return (int)(getItemCount() * paramFloat);
  }

  public int getSectionCount()
  {
    if (this.onlyUsers == 2);
    for (ArrayList localArrayList = ContactsController.getInstance().sortedUsersMutualSectionsArray; ; localArrayList = ContactsController.getInstance().sortedUsersSectionsArray)
    {
      int j = localArrayList.size();
      int i = j;
      if (this.onlyUsers == 0)
        i = j + 1;
      j = i;
      if (this.isAdmin)
        j = i + 1;
      i = j;
      if (this.needPhonebook)
        i = j + 1;
      return i;
    }
  }

  public View getSectionHeaderView(int paramInt, View paramView)
  {
    Object localObject;
    if (this.onlyUsers == 2)
    {
      localObject = ContactsController.getInstance().usersMutualSectionsDict;
      if (this.onlyUsers != 2)
        break label99;
      localObject = ContactsController.getInstance().sortedUsersMutualSectionsArray;
      label30: if (paramView != null)
        break label167;
      paramView = new LetterSectionCell(this.mContext);
    }
    label167: 
    while (true)
    {
      LetterSectionCell localLetterSectionCell = (LetterSectionCell)paramView;
      if ((this.onlyUsers != 0) && (!this.isAdmin))
      {
        if (paramInt < ((ArrayList)localObject).size())
        {
          localLetterSectionCell.setLetter((String)((ArrayList)localObject).get(paramInt));
          return paramView;
          localObject = ContactsController.getInstance().usersSectionsDict;
          break;
          label99: localObject = ContactsController.getInstance().sortedUsersSectionsArray;
          break label30;
        }
        localLetterSectionCell.setLetter("");
        return paramView;
      }
      if (paramInt == 0)
      {
        localLetterSectionCell.setLetter("");
        return paramView;
      }
      if (paramInt - 1 < ((ArrayList)localObject).size())
      {
        localLetterSectionCell.setLetter((String)((ArrayList)localObject).get(paramInt - 1));
        return paramView;
      }
      localLetterSectionCell.setLetter("");
      return paramView;
    }
  }

  public boolean isEnabled(int paramInt1, int paramInt2)
  {
    int j = 1;
    HashMap localHashMap;
    ArrayList localArrayList;
    label35: int i;
    if (this.onlyUsers == 2)
    {
      localHashMap = ContactsController.getInstance().usersMutualSectionsDict;
      if (this.onlyUsers != 2)
        break label85;
      localArrayList = ContactsController.getInstance().sortedUsersMutualSectionsArray;
      if ((this.onlyUsers == 0) || (this.isAdmin))
        break label101;
      if (paramInt2 >= ((ArrayList)localHashMap.get(localArrayList.get(paramInt1))).size())
        break label96;
      i = 1;
    }
    label85: label96: label101: 
    do
    {
      do
      {
        do
        {
          do
          {
            while (true)
            {
              return i;
              localHashMap = ContactsController.getInstance().usersSectionsDict;
              break;
              localArrayList = ContactsController.getInstance().sortedUsersSectionsArray;
              break label35;
              i = 0;
            }
            if (paramInt1 != 0)
              break label139;
            if ((!this.needPhonebook) && (!this.isAdmin))
              break label129;
            i = j;
          }
          while (paramInt2 != 1);
          return false;
          i = j;
        }
        while (paramInt2 != 3);
        return false;
        i = j;
      }
      while (paramInt1 - 1 >= localArrayList.size());
      i = j;
    }
    while (paramInt2 < ((ArrayList)localHashMap.get(localArrayList.get(paramInt1 - 1))).size());
    label129: label139: return false;
  }

  public void onBindViewHolder(int paramInt1, int paramInt2, RecyclerView.ViewHolder paramViewHolder)
  {
    boolean bool1 = true;
    switch (paramViewHolder.getItemViewType())
    {
    default:
    case 0:
    case 1:
    }
    while (true)
    {
      return;
      UserCell localUserCell = (UserCell)paramViewHolder.itemView;
      label53: label69: int i;
      label86: boolean bool2;
      if (this.onlyUsers == 2)
      {
        paramViewHolder = ContactsController.getInstance().usersMutualSectionsDict;
        if (this.onlyUsers != 2)
          break label214;
        localObject = ContactsController.getInstance().sortedUsersMutualSectionsArray;
        if ((this.onlyUsers == 0) || (this.isAdmin))
          break label225;
        i = 0;
        paramViewHolder = (ArrayList)paramViewHolder.get(((ArrayList)localObject).get(paramInt1 - i));
        paramViewHolder = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)paramViewHolder.get(paramInt2)).user_id));
        localUserCell.setData(paramViewHolder, null, null, 0);
        if (this.checkedMap != null)
        {
          bool2 = this.checkedMap.containsKey(Integer.valueOf(paramViewHolder.id));
          if (this.scrolling)
            break label231;
        }
      }
      while (true)
      {
        localUserCell.setChecked(bool2, bool1);
        if (this.ignoreUsers == null)
          break;
        if (!this.ignoreUsers.containsKey(Integer.valueOf(paramViewHolder.id)))
          break label237;
        localUserCell.setAlpha(0.5F);
        return;
        paramViewHolder = ContactsController.getInstance().usersSectionsDict;
        break label53;
        label214: localObject = ContactsController.getInstance().sortedUsersSectionsArray;
        break label69;
        label225: i = 1;
        break label86;
        label231: bool1 = false;
      }
      label237: localUserCell.setAlpha(1.0F);
      return;
      paramViewHolder = (TextCell)paramViewHolder.itemView;
      if (paramInt1 != 0)
        break;
      if (this.needPhonebook)
      {
        paramViewHolder.setTextAndIcon(LocaleController.getString("InviteFriends", 2131165844), 2130837916);
        return;
      }
      if (this.isAdmin)
      {
        paramViewHolder.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", 2131165848), 2130837916);
        return;
      }
      if (paramInt2 == 0)
      {
        paramViewHolder.setTextAndIcon(LocaleController.getString("NewGroup", 2131166009), 2130837917);
        return;
      }
      if (paramInt2 == 1)
      {
        paramViewHolder.setTextAndIcon(LocaleController.getString("NewSecretChat", 2131166017), 2130837918);
        return;
      }
      if (paramInt2 != 2)
        continue;
      paramViewHolder.setTextAndIcon(LocaleController.getString("NewChannel", 2131166007), 2130837912);
      return;
    }
    Object localObject = (ContactsController.Contact)ContactsController.getInstance().phoneBookContacts.get(paramInt2);
    if ((((ContactsController.Contact)localObject).first_name != null) && (((ContactsController.Contact)localObject).last_name != null))
    {
      paramViewHolder.setText(((ContactsController.Contact)localObject).first_name + " " + ((ContactsController.Contact)localObject).last_name);
      return;
    }
    if ((((ContactsController.Contact)localObject).first_name != null) && (((ContactsController.Contact)localObject).last_name == null))
    {
      paramViewHolder.setText(((ContactsController.Contact)localObject).first_name);
      return;
    }
    paramViewHolder.setText(((ContactsController.Contact)localObject).last_name);
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    float f2 = 72.0F;
    float f1;
    switch (paramInt)
    {
    default:
      paramViewGroup = new DividerCell(this.mContext);
      if (!LocaleController.isRTL)
        break;
      f1 = 28.0F;
      paramInt = AndroidUtilities.dp(f1);
      if (LocaleController.isRTL)
        f1 = f2;
    case 0:
    case 1:
    case 2:
    }
    while (true)
    {
      paramViewGroup.setPadding(paramInt, 0, AndroidUtilities.dp(f1), 0);
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 58, 1, false);
        continue;
        paramViewGroup = new TextCell(this.mContext);
        continue;
        paramViewGroup = new GraySectionCell(this.mContext);
        ((GraySectionCell)paramViewGroup).setText(LocaleController.getString("Contacts", 2131165574).toUpperCase());
      }
      f1 = 72.0F;
      break;
      f1 = 28.0F;
    }
  }

  public void setCheckedMap(HashMap<Integer, ?> paramHashMap)
  {
    this.checkedMap = paramHashMap;
  }

  public void setIsScrolling(boolean paramBoolean)
  {
    this.scrolling = paramBoolean;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.ContactsAdapter
 * JD-Core Version:    0.6.0
 */