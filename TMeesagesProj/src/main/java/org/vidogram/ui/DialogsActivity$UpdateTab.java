package org.vidogram.ui;

import android.os.AsyncTask;
import org.vidogram.messenger.MessagesController;

class DialogsActivity$UpdateTab extends AsyncTask<String, Void, Void>
{
  private DialogsActivity$UpdateTab(DialogsActivity paramDialogsActivity)
  {
  }

  protected Void doInBackground(String[] paramArrayOfString)
  {
    try
    {
      MessagesController.getInstance().sortDialogs(null);
      return null;
    }
    catch (java.lang.Exception paramArrayOfString)
    {
    }
    return null;
  }

  protected void onPostExecute(Void paramVoid)
  {
    DialogsActivity.access$3800(this.this$0);
  }

  protected void onPreExecute()
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.UpdateTab
 * JD-Core Version:    0.6.0
 */