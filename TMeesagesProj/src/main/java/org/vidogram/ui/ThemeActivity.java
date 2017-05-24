package org.vidogram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.Theme.ThemeInfo;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Cells.ThemeCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment
{
  private ListAdapter listAdapter;
  private RecyclerListView listView;

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setTitle(LocaleController.getString("Theme", 2131166510));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          ThemeActivity.this.finishFragment();
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.fragmentView = localFrameLayout;
    this.listView = new RecyclerListView(paramContext);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setAdapter(this.listAdapter);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if (paramInt == 0)
          if (ThemeActivity.this.getParentActivity() != null);
        do
        {
          return;
          paramView = new EditText(ThemeActivity.this.getParentActivity());
          paramView.setBackgroundDrawable(Theme.createEditTextDrawable(ThemeActivity.this.getParentActivity(), true));
          Object localObject = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("NewTheme", 2131166018));
          ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
            }
          });
          LinearLayout localLinearLayout = new LinearLayout(ThemeActivity.this.getParentActivity());
          localLinearLayout.setOrientation(1);
          ((AlertDialog.Builder)localObject).setView(localLinearLayout);
          TextView localTextView = new TextView(ThemeActivity.this.getParentActivity());
          localTextView.setText(LocaleController.formatString("EnterThemeName", 2131165699, new Object[0]));
          localTextView.setTextSize(16.0F);
          localTextView.setPadding(AndroidUtilities.dp(23.0F), AndroidUtilities.dp(12.0F), AndroidUtilities.dp(23.0F), AndroidUtilities.dp(6.0F));
          localTextView.setTextColor(Theme.getColor("dialogTextBlack"));
          localLinearLayout.addView(localTextView, LayoutHelper.createLinear(-1, -2));
          paramView.setTextSize(1, 16.0F);
          paramView.setTextColor(Theme.getColor("dialogTextBlack"));
          paramView.setMaxLines(1);
          paramView.setLines(1);
          paramView.setInputType(16385);
          paramView.setGravity(51);
          paramView.setSingleLine(true);
          paramView.setImeOptions(6);
          AndroidUtilities.clearCursorDrawable(paramView);
          paramView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
          localLinearLayout.addView(paramView, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
          paramView.setOnEditorActionListener(new TextView.OnEditorActionListener()
          {
            public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
            {
              AndroidUtilities.hideKeyboard(paramTextView);
              return false;
            }
          });
          localObject = ((AlertDialog.Builder)localObject).create();
          ((AlertDialog)localObject).setOnShowListener(new DialogInterface.OnShowListener(paramView)
          {
            public void onShow(DialogInterface paramDialogInterface)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ThemeActivity.2.3.this.val$editText.requestFocus();
                  AndroidUtilities.showKeyboard(ThemeActivity.2.3.this.val$editText);
                }
              });
            }
          });
          ThemeActivity.this.showDialog((Dialog)localObject);
          ((AlertDialog)localObject).getButton(-1).setOnClickListener(new View.OnClickListener(paramView, (AlertDialog)localObject)
          {
            public void onClick(View paramView)
            {
              if (this.val$editText.length() == 0)
              {
                paramView = (Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator");
                if (paramView != null)
                  paramView.vibrate(200L);
                AndroidUtilities.shakeView(this.val$editText, 2.0F, 0);
              }
              do
              {
                return;
                paramView = new ThemeEditorView();
                String str = this.val$editText.getText().toString() + ".attheme";
                paramView.show(ThemeActivity.this.getParentActivity(), str);
                Theme.saveCurrentTheme(str, true);
                ThemeActivity.this.listAdapter.notifyDataSetChanged();
                this.val$alertDialog.dismiss();
                paramView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
              }
              while (paramView.getBoolean("themehint", false));
              paramView.edit().putBoolean("themehint", true).commit();
              try
              {
                Toast.makeText(ThemeActivity.this.getParentActivity(), LocaleController.getString("CreateNewThemeHelp", 2131165591), 1).show();
                return;
              }
              catch (java.lang.Exception paramView)
              {
                FileLog.e(paramView);
              }
            }
          });
          return;
          paramInt -= 2;
        }
        while ((paramInt < 0) || (paramInt >= Theme.themes.size()));
        Theme.applyTheme((Theme.ThemeInfo)Theme.themes.get(paramInt));
        if (ThemeActivity.this.parentLayout != null)
          ThemeActivity.this.parentLayout.rebuildAllFragmentViews(false);
        ThemeActivity.this.finishFragment();
      }
    });
    return this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { ThemeCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { ThemeCell.class }, new String[] { "checkImage" }, null, null, null, "featuredStickers_addedIcon"), new ThemeDescription(this.listView, 0, new Class[] { ThemeCell.class }, new String[] { "optionsButton" }, null, null, null, "stickers_menu"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText") };
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
      return Theme.themes.size() + 3;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == 0)
        return 1;
      if (paramInt == 1)
        return 2;
      if (paramInt == Theme.themes.size() + 2)
        return 3;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      return (i == 0) || (i == 1);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Theme.ThemeInfo localThemeInfo;
      if (paramViewHolder.getItemViewType() == 0)
      {
        paramInt -= 2;
        localThemeInfo = (Theme.ThemeInfo)Theme.themes.get(paramInt);
        paramViewHolder = (ThemeCell)paramViewHolder.itemView;
        if (paramInt == Theme.themes.size() - 1)
          break label53;
      }
      label53: for (boolean bool = true; ; bool = false)
      {
        paramViewHolder.setTheme(localThemeInfo, bool);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new ShadowSectionCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
      case 0:
      case 1:
      case 2:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ThemeCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((ThemeCell)paramViewGroup).setOnOptionsClick(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            Theme.ThemeInfo localThemeInfo = ((ThemeCell)paramView.getParent()).getCurrentThemeInfo();
            if (ThemeActivity.this.getParentActivity() == null)
              return;
            BottomSheet.Builder localBuilder = new BottomSheet.Builder(ThemeActivity.this.getParentActivity());
            if (localThemeInfo.pathToFile == null)
            {
              paramView = new CharSequence[1];
              paramView[0] = LocaleController.getString("ShareFile", 2131166451);
            }
            while (true)
            {
              localBuilder.setItems(paramView, new DialogInterface.OnClickListener(localThemeInfo)
              {
                // ERROR //
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  // Byte code:
                  //   0: iload_2
                  //   1: ifne +380 -> 381
                  //   4: aload_0
                  //   5: getfield 28	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
                  //   8: getfield 41	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
                  //   11: ifnonnull +265 -> 276
                  //   14: aload_0
                  //   15: getfield 28	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
                  //   18: getfield 44	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
                  //   21: ifnonnull +255 -> 276
                  //   24: new 46	java/lang/StringBuilder
                  //   27: dup
                  //   28: invokespecial 47	java/lang/StringBuilder:<init>	()V
                  //   31: astore 5
                  //   33: invokestatic 53	org/vidogram/ui/ActionBar/Theme:getDefaultColors	()Ljava/util/HashMap;
                  //   36: invokevirtual 59	java/util/HashMap:entrySet	()Ljava/util/Set;
                  //   39: invokeinterface 65 1 0
                  //   44: astore_1
                  //   45: aload_1
                  //   46: invokeinterface 71 1 0
                  //   51: ifeq +53 -> 104
                  //   54: aload_1
                  //   55: invokeinterface 75 1 0
                  //   60: checkcast 77	java/util/Map$Entry
                  //   63: astore 4
                  //   65: aload 5
                  //   67: aload 4
                  //   69: invokeinterface 80 1 0
                  //   74: checkcast 82	java/lang/String
                  //   77: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                  //   80: ldc 88
                  //   82: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                  //   85: aload 4
                  //   87: invokeinterface 91 1 0
                  //   92: invokevirtual 94	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
                  //   95: ldc 96
                  //   97: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                  //   100: pop
                  //   101: goto -56 -> 45
                  //   104: new 98	java/io/File
                  //   107: dup
                  //   108: invokestatic 104	org/vidogram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
                  //   111: ldc 106
                  //   113: invokespecial 109	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
                  //   116: astore 6
                  //   118: new 111	java/io/FileOutputStream
                  //   121: dup
                  //   122: aload 6
                  //   124: invokespecial 114	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
                  //   127: astore 4
                  //   129: aload 4
                  //   131: astore_1
                  //   132: aload 4
                  //   134: aload 5
                  //   136: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
                  //   139: invokevirtual 122	java/lang/String:getBytes	()[B
                  //   142: invokevirtual 126	java/io/FileOutputStream:write	([B)V
                  //   145: aload 6
                  //   147: astore_1
                  //   148: aload 4
                  //   150: ifnull +11 -> 161
                  //   153: aload 4
                  //   155: invokevirtual 129	java/io/FileOutputStream:close	()V
                  //   158: aload 6
                  //   160: astore_1
                  //   161: new 98	java/io/File
                  //   164: dup
                  //   165: invokestatic 135	org/vidogram/messenger/FileLoader:getInstance	()Lorg/vidogram/messenger/FileLoader;
                  //   168: iconst_4
                  //   169: invokevirtual 139	org/vidogram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
                  //   172: aload_1
                  //   173: invokevirtual 142	java/io/File:getName	()Ljava/lang/String;
                  //   176: invokespecial 109	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
                  //   179: astore 4
                  //   181: aload_1
                  //   182: aload 4
                  //   184: invokestatic 148	org/vidogram/messenger/AndroidUtilities:copyFile	(Ljava/io/File;Ljava/io/File;)Z
                  //   187: istore_3
                  //   188: iload_3
                  //   189: ifne +129 -> 318
                  //   192: return
                  //   193: astore_1
                  //   194: ldc 150
                  //   196: aload_1
                  //   197: invokestatic 156	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
                  //   200: aload 6
                  //   202: astore_1
                  //   203: goto -42 -> 161
                  //   206: astore 5
                  //   208: aconst_null
                  //   209: astore 4
                  //   211: aload 4
                  //   213: astore_1
                  //   214: aload 5
                  //   216: invokestatic 159	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
                  //   219: aload 6
                  //   221: astore_1
                  //   222: aload 4
                  //   224: ifnull -63 -> 161
                  //   227: aload 4
                  //   229: invokevirtual 129	java/io/FileOutputStream:close	()V
                  //   232: aload 6
                  //   234: astore_1
                  //   235: goto -74 -> 161
                  //   238: astore_1
                  //   239: ldc 150
                  //   241: aload_1
                  //   242: invokestatic 156	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
                  //   245: aload 6
                  //   247: astore_1
                  //   248: goto -87 -> 161
                  //   251: astore 4
                  //   253: aconst_null
                  //   254: astore_1
                  //   255: aload_1
                  //   256: ifnull +7 -> 263
                  //   259: aload_1
                  //   260: invokevirtual 129	java/io/FileOutputStream:close	()V
                  //   263: aload 4
                  //   265: athrow
                  //   266: astore_1
                  //   267: ldc 150
                  //   269: aload_1
                  //   270: invokestatic 156	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
                  //   273: goto -10 -> 263
                  //   276: aload_0
                  //   277: getfield 28	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
                  //   280: getfield 44	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
                  //   283: ifnull +17 -> 300
                  //   286: aload_0
                  //   287: getfield 28	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
                  //   290: getfield 44	org/vidogram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
                  //   293: invokestatic 163	org/vidogram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
                  //   296: astore_1
                  //   297: goto -136 -> 161
                  //   300: new 98	java/io/File
                  //   303: dup
                  //   304: aload_0
                  //   305: getfield 28	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
                  //   308: getfield 41	org/vidogram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
                  //   311: invokespecial 166	java/io/File:<init>	(Ljava/lang/String;)V
                  //   314: astore_1
                  //   315: goto -154 -> 161
                  //   318: new 168	android/content/Intent
                  //   321: dup
                  //   322: ldc 170
                  //   324: invokespecial 171	android/content/Intent:<init>	(Ljava/lang/String;)V
                  //   327: astore_1
                  //   328: aload_1
                  //   329: ldc 173
                  //   331: invokevirtual 177	android/content/Intent:setType	(Ljava/lang/String;)Landroid/content/Intent;
                  //   334: pop
                  //   335: aload_1
                  //   336: ldc 179
                  //   338: aload 4
                  //   340: invokestatic 185	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
                  //   343: invokevirtual 189	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
                  //   346: pop
                  //   347: aload_0
                  //   348: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   351: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   354: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   357: aload_1
                  //   358: ldc 199
                  //   360: ldc 200
                  //   362: invokestatic 206	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                  //   365: invokestatic 210	android/content/Intent:createChooser	(Landroid/content/Intent;Ljava/lang/CharSequence;)Landroid/content/Intent;
                  //   368: sipush 500
                  //   371: invokevirtual 214	org/vidogram/ui/ThemeActivity:startActivityForResult	(Landroid/content/Intent;I)V
                  //   374: return
                  //   375: astore_1
                  //   376: aload_1
                  //   377: invokestatic 159	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
                  //   380: return
                  //   381: iload_2
                  //   382: iconst_1
                  //   383: if_icmpne +90 -> 473
                  //   386: aload_0
                  //   387: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   390: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   393: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   396: invokestatic 218	org/vidogram/ui/ThemeActivity:access$300	(Lorg/vidogram/ui/ThemeActivity;)Lorg/vidogram/ui/ActionBar/ActionBarLayout;
                  //   399: ifnull -207 -> 192
                  //   402: aload_0
                  //   403: getfield 28	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
                  //   406: invokestatic 222	org/vidogram/ui/ActionBar/Theme:applyTheme	(Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;)V
                  //   409: aload_0
                  //   410: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   413: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   416: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   419: invokestatic 225	org/vidogram/ui/ThemeActivity:access$400	(Lorg/vidogram/ui/ThemeActivity;)Lorg/vidogram/ui/ActionBar/ActionBarLayout;
                  //   422: iconst_1
                  //   423: invokevirtual 231	org/vidogram/ui/ActionBar/ActionBarLayout:rebuildAllFragmentViews	(Z)V
                  //   426: aload_0
                  //   427: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   430: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   433: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   436: invokestatic 234	org/vidogram/ui/ThemeActivity:access$500	(Lorg/vidogram/ui/ThemeActivity;)Lorg/vidogram/ui/ActionBar/ActionBarLayout;
                  //   439: invokevirtual 237	org/vidogram/ui/ActionBar/ActionBarLayout:showLastFragment	()V
                  //   442: new 239	org/vidogram/ui/Components/ThemeEditorView
                  //   445: dup
                  //   446: invokespecial 240	org/vidogram/ui/Components/ThemeEditorView:<init>	()V
                  //   449: aload_0
                  //   450: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   453: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   456: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   459: invokevirtual 244	org/vidogram/ui/ThemeActivity:getParentActivity	()Landroid/app/Activity;
                  //   462: aload_0
                  //   463: getfield 28	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/vidogram/ui/ActionBar/Theme$ThemeInfo;
                  //   466: getfield 247	org/vidogram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
                  //   469: invokevirtual 251	org/vidogram/ui/Components/ThemeEditorView:show	(Landroid/app/Activity;Ljava/lang/String;)V
                  //   472: return
                  //   473: aload_0
                  //   474: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   477: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   480: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   483: invokevirtual 244	org/vidogram/ui/ThemeActivity:getParentActivity	()Landroid/app/Activity;
                  //   486: ifnull -294 -> 192
                  //   489: new 253	org/vidogram/ui/ActionBar/AlertDialog$Builder
                  //   492: dup
                  //   493: aload_0
                  //   494: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   497: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   500: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   503: invokevirtual 244	org/vidogram/ui/ThemeActivity:getParentActivity	()Landroid/app/Activity;
                  //   506: invokespecial 256	org/vidogram/ui/ActionBar/AlertDialog$Builder:<init>	(Landroid/content/Context;)V
                  //   509: astore_1
                  //   510: aload_1
                  //   511: ldc_w 258
                  //   514: ldc_w 259
                  //   517: invokestatic 206	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                  //   520: invokevirtual 263	org/vidogram/ui/ActionBar/AlertDialog$Builder:setMessage	(Ljava/lang/CharSequence;)Lorg/vidogram/ui/ActionBar/AlertDialog$Builder;
                  //   523: pop
                  //   524: aload_1
                  //   525: ldc_w 265
                  //   528: ldc_w 266
                  //   531: invokestatic 206	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                  //   534: invokevirtual 269	org/vidogram/ui/ActionBar/AlertDialog$Builder:setTitle	(Ljava/lang/CharSequence;)Lorg/vidogram/ui/ActionBar/AlertDialog$Builder;
                  //   537: pop
                  //   538: aload_1
                  //   539: ldc_w 271
                  //   542: ldc_w 272
                  //   545: invokestatic 206	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                  //   548: new 18	org/vidogram/ui/ThemeActivity$ListAdapter$1$1$1
                  //   551: dup
                  //   552: aload_0
                  //   553: invokespecial 275	org/vidogram/ui/ThemeActivity$ListAdapter$1$1$1:<init>	(Lorg/vidogram/ui/ThemeActivity$ListAdapter$1$1;)V
                  //   556: invokevirtual 279	org/vidogram/ui/ActionBar/AlertDialog$Builder:setPositiveButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/vidogram/ui/ActionBar/AlertDialog$Builder;
                  //   559: pop
                  //   560: aload_1
                  //   561: ldc_w 281
                  //   564: ldc_w 282
                  //   567: invokestatic 206	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                  //   570: aconst_null
                  //   571: invokevirtual 285	org/vidogram/ui/ActionBar/AlertDialog$Builder:setNegativeButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/vidogram/ui/ActionBar/AlertDialog$Builder;
                  //   574: pop
                  //   575: aload_0
                  //   576: getfield 26	org/vidogram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/vidogram/ui/ThemeActivity$ListAdapter$1;
                  //   579: getfield 193	org/vidogram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/vidogram/ui/ThemeActivity$ListAdapter;
                  //   582: getfield 197	org/vidogram/ui/ThemeActivity$ListAdapter:this$0	Lorg/vidogram/ui/ThemeActivity;
                  //   585: aload_1
                  //   586: invokevirtual 289	org/vidogram/ui/ActionBar/AlertDialog$Builder:create	()Lorg/vidogram/ui/ActionBar/AlertDialog;
                  //   589: invokevirtual 293	org/vidogram/ui/ThemeActivity:showDialog	(Landroid/app/Dialog;)Landroid/app/Dialog;
                  //   592: pop
                  //   593: return
                  //   594: astore 4
                  //   596: goto -341 -> 255
                  //   599: astore 5
                  //   601: goto -390 -> 211
                  //
                  // Exception table:
                  //   from	to	target	type
                  //   153	158	193	java/lang/Exception
                  //   118	129	206	java/lang/Exception
                  //   227	232	238	java/lang/Exception
                  //   118	129	251	finally
                  //   259	263	266	java/lang/Exception
                  //   181	188	375	java/lang/Exception
                  //   318	374	375	java/lang/Exception
                  //   132	145	594	finally
                  //   214	219	594	finally
                  //   132	145	599	java/lang/Exception
                }
              });
              ThemeActivity.this.showDialog(localBuilder.create());
              return;
              paramView = new CharSequence[3];
              paramView[0] = LocaleController.getString("ShareFile", 2131166451);
              paramView[1] = LocaleController.getString("Edit", 2131165663);
              paramView[2] = LocaleController.getString("Delete", 2131165628);
            }
          }
        });
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        ((TextSettingsCell)paramViewGroup).setText(LocaleController.getString("CreateNewTheme", 2131165590), false);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        ((TextInfoPrivacyCell)paramViewGroup).setText(LocaleController.getString("CreateNewThemeInfo", 2131165592));
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ThemeActivity
 * JD-Core Version:    0.6.0
 */