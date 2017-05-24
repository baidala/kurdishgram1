package org.vidogram.messenger.exoplayer2.util;

public final class LibraryLoader
{
  private boolean isAvailable;
  private boolean loadAttempted;
  private String[] nativeLibraries;

  public LibraryLoader(String[] paramArrayOfString)
  {
    this.nativeLibraries = paramArrayOfString;
  }

  // ERROR //
  public boolean isAvailable()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 22	org/vidogram/messenger/exoplayer2/util/LibraryLoader:loadAttempted	Z
    //   6: ifeq +12 -> 18
    //   9: aload_0
    //   10: getfield 24	org/vidogram/messenger/exoplayer2/util/LibraryLoader:isAvailable	Z
    //   13: istore_3
    //   14: aload_0
    //   15: monitorexit
    //   16: iload_3
    //   17: ireturn
    //   18: aload_0
    //   19: iconst_1
    //   20: putfield 22	org/vidogram/messenger/exoplayer2/util/LibraryLoader:loadAttempted	Z
    //   23: aload_0
    //   24: getfield 16	org/vidogram/messenger/exoplayer2/util/LibraryLoader:nativeLibraries	[Ljava/lang/String;
    //   27: astore 4
    //   29: aload 4
    //   31: arraylength
    //   32: istore_2
    //   33: iconst_0
    //   34: istore_1
    //   35: iload_1
    //   36: iload_2
    //   37: if_icmpge +17 -> 54
    //   40: aload 4
    //   42: iload_1
    //   43: aaload
    //   44: invokestatic 30	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   47: iload_1
    //   48: iconst_1
    //   49: iadd
    //   50: istore_1
    //   51: goto -16 -> 35
    //   54: aload_0
    //   55: iconst_1
    //   56: putfield 24	org/vidogram/messenger/exoplayer2/util/LibraryLoader:isAvailable	Z
    //   59: aload_0
    //   60: getfield 24	org/vidogram/messenger/exoplayer2/util/LibraryLoader:isAvailable	Z
    //   63: istore_3
    //   64: goto -50 -> 14
    //   67: astore 4
    //   69: aload_0
    //   70: monitorexit
    //   71: aload 4
    //   73: athrow
    //   74: astore 4
    //   76: goto -17 -> 59
    //
    // Exception table:
    //   from	to	target	type
    //   2	14	67	finally
    //   18	23	67	finally
    //   23	33	67	finally
    //   40	47	67	finally
    //   54	59	67	finally
    //   59	64	67	finally
    //   23	33	74	java/lang/UnsatisfiedLinkError
    //   40	47	74	java/lang/UnsatisfiedLinkError
    //   54	59	74	java/lang/UnsatisfiedLinkError
  }

  public void setLibraries(String[] paramArrayOfString)
  {
    monitorenter;
    try
    {
      if (!this.loadAttempted);
      for (boolean bool = true; ; bool = false)
      {
        Assertions.checkState(bool, "Cannot set libraries after loading");
        this.nativeLibraries = paramArrayOfString;
        return;
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramArrayOfString;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.LibraryLoader
 * JD-Core Version:    0.6.0
 */