package org.webrtc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging
{
  private static final Logger fallbackLogger = Logger.getLogger("org.webrtc.Logging");
  private static volatile boolean loggingEnabled;
  private static volatile boolean nativeLibLoaded;
  private static volatile boolean tracingEnabled;

  static
  {
    try
    {
      System.loadLibrary("jingle_peerconnection_so");
      nativeLibLoaded = true;
      return;
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      fallbackLogger.setLevel(Level.ALL);
      fallbackLogger.log(Level.WARNING, "Failed to load jingle_peerconnection_so: ", localUnsatisfiedLinkError);
    }
  }

  public static void d(String paramString1, String paramString2)
  {
    log(Severity.LS_INFO, paramString1, paramString2);
  }

  public static void e(String paramString1, String paramString2)
  {
    log(Severity.LS_ERROR, paramString1, paramString2);
  }

  public static void e(String paramString1, String paramString2, Throwable paramThrowable)
  {
    log(Severity.LS_ERROR, paramString1, paramString2);
    log(Severity.LS_ERROR, paramString1, paramThrowable.toString());
    log(Severity.LS_ERROR, paramString1, getStackTraceString(paramThrowable));
  }

  public static void enableLogThreads()
  {
    if (!nativeLibLoaded)
    {
      fallbackLogger.log(Level.WARNING, "Cannot enable log thread because native lib not loaded.");
      return;
    }
    nativeEnableLogThreads();
  }

  public static void enableLogTimeStamps()
  {
    if (!nativeLibLoaded)
    {
      fallbackLogger.log(Level.WARNING, "Cannot enable log timestamps because native lib not loaded.");
      return;
    }
    nativeEnableLogTimeStamps();
  }

  public static void enableLogToDebugOutput(Severity paramSeverity)
  {
    monitorenter;
    try
    {
      if (!nativeLibLoaded)
        fallbackLogger.log(Level.WARNING, "Cannot enable logging because native lib not loaded.");
      while (true)
      {
        return;
        nativeEnableLogToDebugOutput(paramSeverity.ordinal());
        loggingEnabled = true;
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramSeverity;
  }

  public static void enableTracing(String paramString, EnumSet<TraceLevel> paramEnumSet)
  {
    monitorenter;
    try
    {
      if (!nativeLibLoaded)
        fallbackLogger.log(Level.WARNING, "Cannot enable tracing because native lib not loaded.");
      while (true)
      {
        return;
        if (tracingEnabled)
          continue;
        paramEnumSet = paramEnumSet.iterator();
        for (int i = 0; paramEnumSet.hasNext(); i = ((TraceLevel)paramEnumSet.next()).level | i);
        nativeEnableTracing(paramString, i);
        tracingEnabled = true;
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  private static String getStackTraceString(Throwable paramThrowable)
  {
    if (paramThrowable == null)
      return "";
    StringWriter localStringWriter = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(localStringWriter));
    return localStringWriter.toString();
  }

  public static void log(Severity paramSeverity, String paramString1, String paramString2)
  {
    if (loggingEnabled)
    {
      nativeLog(paramSeverity.ordinal(), paramString1, paramString2);
      return;
    }
    switch (1.$SwitchMap$org$webrtc$Logging$Severity[paramSeverity.ordinal()])
    {
    default:
      paramSeverity = Level.FINE;
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      fallbackLogger.log(paramSeverity, paramString1 + ": " + paramString2);
      return;
      paramSeverity = Level.SEVERE;
      continue;
      paramSeverity = Level.WARNING;
      continue;
      paramSeverity = Level.INFO;
    }
  }

  private static native void nativeEnableLogThreads();

  private static native void nativeEnableLogTimeStamps();

  private static native void nativeEnableLogToDebugOutput(int paramInt);

  private static native void nativeEnableTracing(String paramString, int paramInt);

  private static native void nativeLog(int paramInt, String paramString1, String paramString2);

  public static void v(String paramString1, String paramString2)
  {
    log(Severity.LS_VERBOSE, paramString1, paramString2);
  }

  public static void w(String paramString1, String paramString2)
  {
    log(Severity.LS_WARNING, paramString1, paramString2);
  }

  public static void w(String paramString1, String paramString2, Throwable paramThrowable)
  {
    log(Severity.LS_WARNING, paramString1, paramString2);
    log(Severity.LS_WARNING, paramString1, paramThrowable.toString());
    log(Severity.LS_WARNING, paramString1, getStackTraceString(paramThrowable));
  }

  public static enum Severity
  {
    static
    {
      LS_INFO = new Severity("LS_INFO", 2);
      LS_WARNING = new Severity("LS_WARNING", 3);
      LS_ERROR = new Severity("LS_ERROR", 4);
      LS_NONE = new Severity("LS_NONE", 5);
      $VALUES = new Severity[] { LS_SENSITIVE, LS_VERBOSE, LS_INFO, LS_WARNING, LS_ERROR, LS_NONE };
    }
  }

  public static enum TraceLevel
  {
    public final int level;

    static
    {
      TRACE_ERROR = new TraceLevel("TRACE_ERROR", 3, 4);
      TRACE_CRITICAL = new TraceLevel("TRACE_CRITICAL", 4, 8);
      TRACE_APICALL = new TraceLevel("TRACE_APICALL", 5, 16);
      TRACE_DEFAULT = new TraceLevel("TRACE_DEFAULT", 6, 255);
      TRACE_MODULECALL = new TraceLevel("TRACE_MODULECALL", 7, 32);
      TRACE_MEMORY = new TraceLevel("TRACE_MEMORY", 8, 256);
      TRACE_TIMER = new TraceLevel("TRACE_TIMER", 9, 512);
      TRACE_STREAM = new TraceLevel("TRACE_STREAM", 10, 1024);
      TRACE_DEBUG = new TraceLevel("TRACE_DEBUG", 11, 2048);
      TRACE_INFO = new TraceLevel("TRACE_INFO", 12, 4096);
      TRACE_TERSEINFO = new TraceLevel("TRACE_TERSEINFO", 13, 8192);
      TRACE_ALL = new TraceLevel("TRACE_ALL", 14, 65535);
      $VALUES = new TraceLevel[] { TRACE_NONE, TRACE_STATEINFO, TRACE_WARNING, TRACE_ERROR, TRACE_CRITICAL, TRACE_APICALL, TRACE_DEFAULT, TRACE_MODULECALL, TRACE_MEMORY, TRACE_TIMER, TRACE_STREAM, TRACE_DEBUG, TRACE_INFO, TRACE_TERSEINFO, TRACE_ALL };
    }

    private TraceLevel(int paramInt)
    {
      this.level = paramInt;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.Logging
 * JD-Core Version:    0.6.0
 */