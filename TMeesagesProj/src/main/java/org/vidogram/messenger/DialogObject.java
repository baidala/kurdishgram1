package org.vidogram.messenger;

import org.vidogram.tgnet.TLRPC.TL_dialog;

public class DialogObject
{
  public static boolean isChannel(TLRPC.TL_dialog paramTL_dialog)
  {
    return (paramTL_dialog != null) && ((paramTL_dialog.flags & 0x1) != 0);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.DialogObject
 * JD-Core Version:    0.6.0
 */