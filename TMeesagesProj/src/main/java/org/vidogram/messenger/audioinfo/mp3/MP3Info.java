package org.vidogram.messenger.audioinfo.mp3;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vidogram.messenger.audioinfo.AudioInfo;

public class MP3Info extends AudioInfo
{
  static final Logger LOGGER = Logger.getLogger(MP3Info.class.getName());

  public MP3Info(InputStream paramInputStream, long paramLong)
  {
    this(paramInputStream, paramLong, Level.FINEST);
  }

  public MP3Info(InputStream paramInputStream, long paramLong, Level paramLevel)
  {
    this.brand = "MP3";
    this.version = "0";
    MP3Input localMP3Input = new MP3Input(paramInputStream);
    if (ID3v2Info.isID3v2StartPosition(localMP3Input))
    {
      ID3v2Info localID3v2Info = new ID3v2Info(localMP3Input, paramLevel);
      this.album = localID3v2Info.getAlbum();
      this.albumArtist = localID3v2Info.getAlbumArtist();
      this.artist = localID3v2Info.getArtist();
      this.comment = localID3v2Info.getComment();
      this.cover = localID3v2Info.getCover();
      this.smallCover = localID3v2Info.getSmallCover();
      this.compilation = localID3v2Info.isCompilation();
      this.composer = localID3v2Info.getComposer();
      this.copyright = localID3v2Info.getCopyright();
      this.disc = localID3v2Info.getDisc();
      this.discs = localID3v2Info.getDiscs();
      this.duration = localID3v2Info.getDuration();
      this.genre = localID3v2Info.getGenre();
      this.grouping = localID3v2Info.getGrouping();
      this.lyrics = localID3v2Info.getLyrics();
      this.title = localID3v2Info.getTitle();
      this.track = localID3v2Info.getTrack();
      this.tracks = localID3v2Info.getTracks();
      this.year = localID3v2Info.getYear();
    }
    if ((this.duration <= 0L) || (this.duration >= 3600000L));
    try
    {
      this.duration = calculateDuration(localMP3Input, paramLong, new StopReadCondition(paramLong)
      {
        final long stopPosition = this.val$fileLength - 128L;

        public boolean stopRead(MP3Input paramMP3Input)
        {
          return (paramMP3Input.getPosition() == this.stopPosition) && (ID3v1Info.isID3v1StartPosition(paramMP3Input));
        }
      });
      if (((this.title == null) || (this.album == null) || (this.artist == null)) && (localMP3Input.getPosition() <= paramLong - 128L))
      {
        localMP3Input.skipFully(paramLong - 128L - localMP3Input.getPosition());
        if (ID3v1Info.isID3v1StartPosition(paramInputStream))
        {
          paramInputStream = new ID3v1Info(paramInputStream);
          if (this.album == null)
            this.album = paramInputStream.getAlbum();
          if (this.artist == null)
            this.artist = paramInputStream.getArtist();
          if (this.comment == null)
            this.comment = paramInputStream.getComment();
          if (this.genre == null)
            this.genre = paramInputStream.getGenre();
          if (this.title == null)
            this.title = paramInputStream.getTitle();
          if (this.track == 0)
            this.track = paramInputStream.getTrack();
          if (this.year == 0)
            this.year = paramInputStream.getYear();
        }
      }
      return;
    }
    catch (MP3Exception localMP3Exception)
    {
      while (true)
      {
        if (!LOGGER.isLoggable(paramLevel))
          continue;
        LOGGER.log(paramLevel, "Could not determine MP3 duration", localMP3Exception);
      }
    }
  }

  long calculateDuration(MP3Input paramMP3Input, long paramLong, StopReadCondition paramStopReadCondition)
  {
    MP3Frame localMP3Frame = readFirstFrame(paramMP3Input, paramStopReadCondition);
    if (localMP3Frame != null)
    {
      int i = localMP3Frame.getNumberOfFrames();
      if (i > 0)
        return localMP3Frame.getHeader().getTotalDuration(i * localMP3Frame.getSize());
      i = 1;
      long l3 = paramMP3Input.getPosition();
      long l4 = localMP3Frame.getSize();
      long l1 = localMP3Frame.getSize();
      int k = localMP3Frame.getHeader().getBitrate();
      long l2 = k;
      int j = 0;
      int m = 10000 / localMP3Frame.getHeader().getDuration();
      while (true)
      {
        if ((i == m) && (j == 0) && (paramLong > 0L))
          return localMP3Frame.getHeader().getTotalDuration(paramLong - (l3 - l4));
        localMP3Frame = readNextFrame(paramMP3Input, paramStopReadCondition, localMP3Frame);
        if (localMP3Frame == null)
          return i * (l1 * 1000L) * 8L / l2;
        int n = localMP3Frame.getHeader().getBitrate();
        if (n != k)
          j = 1;
        l2 += n;
        l1 += localMP3Frame.getSize();
        i += 1;
      }
    }
    throw new MP3Exception("No audio frame");
  }

  // ERROR //
  MP3Frame readFirstFrame(MP3Input paramMP3Input, StopReadCondition paramStopReadCondition)
  {
    // Byte code:
    //   0: aload_2
    //   1: aload_1
    //   2: invokeinterface 278 2 0
    //   7: ifeq +58 -> 65
    //   10: iconst_m1
    //   11: istore_3
    //   12: iconst_0
    //   13: istore 4
    //   15: iload_3
    //   16: iconst_m1
    //   17: if_icmpeq +46 -> 63
    //   20: iload 4
    //   22: sipush 255
    //   25: if_icmpne +364 -> 389
    //   28: iload_3
    //   29: sipush 224
    //   32: iand
    //   33: sipush 224
    //   36: if_icmpne +353 -> 389
    //   39: aload_1
    //   40: iconst_2
    //   41: invokevirtual 282	org/vidogram/messenger/audioinfo/mp3/MP3Input:mark	(I)V
    //   44: aload_2
    //   45: aload_1
    //   46: invokeinterface 278 2 0
    //   51: ifeq +22 -> 73
    //   54: iconst_m1
    //   55: istore 4
    //   57: iload 4
    //   59: iconst_m1
    //   60: if_icmpne +22 -> 82
    //   63: aconst_null
    //   64: areturn
    //   65: aload_1
    //   66: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   69: istore_3
    //   70: goto -58 -> 12
    //   73: aload_1
    //   74: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   77: istore 4
    //   79: goto -22 -> 57
    //   82: aload_2
    //   83: aload_1
    //   84: invokeinterface 278 2 0
    //   89: ifeq +140 -> 229
    //   92: iconst_m1
    //   93: istore 5
    //   95: iload 5
    //   97: iconst_m1
    //   98: if_icmpeq -35 -> 63
    //   101: new 250	org/vidogram/messenger/audioinfo/mp3/MP3Frame$Header
    //   104: dup
    //   105: iload_3
    //   106: iload 4
    //   108: iload 5
    //   110: invokespecial 288	org/vidogram/messenger/audioinfo/mp3/MP3Frame$Header:<init>	(III)V
    //   113: astore 7
    //   115: aload 7
    //   117: ifnull +268 -> 385
    //   120: aload_1
    //   121: invokevirtual 291	org/vidogram/messenger/audioinfo/mp3/MP3Input:reset	()V
    //   124: aload_1
    //   125: aload 7
    //   127: invokevirtual 294	org/vidogram/messenger/audioinfo/mp3/MP3Frame$Header:getFrameSize	()I
    //   130: iconst_2
    //   131: iadd
    //   132: invokevirtual 282	org/vidogram/messenger/audioinfo/mp3/MP3Input:mark	(I)V
    //   135: aload 7
    //   137: invokevirtual 294	org/vidogram/messenger/audioinfo/mp3/MP3Frame$Header:getFrameSize	()I
    //   140: newarray byte
    //   142: astore 8
    //   144: aload 8
    //   146: iconst_0
    //   147: iconst_m1
    //   148: bastore
    //   149: aload 8
    //   151: iconst_1
    //   152: iload_3
    //   153: i2b
    //   154: bastore
    //   155: aload_1
    //   156: aload 8
    //   158: iconst_2
    //   159: aload 8
    //   161: arraylength
    //   162: iconst_2
    //   163: isub
    //   164: invokevirtual 298	org/vidogram/messenger/audioinfo/mp3/MP3Input:readFully	([BII)V
    //   167: new 237	org/vidogram/messenger/audioinfo/mp3/MP3Frame
    //   170: dup
    //   171: aload 7
    //   173: aload 8
    //   175: invokespecial 301	org/vidogram/messenger/audioinfo/mp3/MP3Frame:<init>	(Lorg/vidogram/messenger/audioinfo/mp3/MP3Frame$Header;[B)V
    //   178: astore 9
    //   180: aload 9
    //   182: invokevirtual 304	org/vidogram/messenger/audioinfo/mp3/MP3Frame:isChecksumError	()Z
    //   185: ifne +200 -> 385
    //   188: aload_2
    //   189: aload_1
    //   190: invokeinterface 278 2 0
    //   195: ifeq +51 -> 246
    //   198: iconst_m1
    //   199: istore 5
    //   201: aload_2
    //   202: aload_1
    //   203: invokeinterface 278 2 0
    //   208: ifeq +47 -> 255
    //   211: iconst_m1
    //   212: istore 4
    //   214: iload 5
    //   216: iconst_m1
    //   217: if_icmpeq +9 -> 226
    //   220: iload 4
    //   222: iconst_m1
    //   223: if_icmpne +41 -> 264
    //   226: aload 9
    //   228: areturn
    //   229: aload_1
    //   230: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   233: istore 5
    //   235: goto -140 -> 95
    //   238: astore 7
    //   240: aconst_null
    //   241: astore 7
    //   243: goto -128 -> 115
    //   246: aload_1
    //   247: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   250: istore 5
    //   252: goto -51 -> 201
    //   255: aload_1
    //   256: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   259: istore 4
    //   261: goto -47 -> 214
    //   264: iload 5
    //   266: sipush 255
    //   269: if_icmpne +116 -> 385
    //   272: iload 4
    //   274: sipush 254
    //   277: iand
    //   278: iload_3
    //   279: sipush 254
    //   282: iand
    //   283: if_icmpne +102 -> 385
    //   286: aload_2
    //   287: aload_1
    //   288: invokeinterface 278 2 0
    //   293: ifeq +34 -> 327
    //   296: iconst_m1
    //   297: istore 5
    //   299: aload_2
    //   300: aload_1
    //   301: invokeinterface 278 2 0
    //   306: ifeq +30 -> 336
    //   309: iconst_m1
    //   310: istore 6
    //   312: iload 5
    //   314: iconst_m1
    //   315: if_icmpeq +9 -> 324
    //   318: iload 6
    //   320: iconst_m1
    //   321: if_icmpne +24 -> 345
    //   324: aload 9
    //   326: areturn
    //   327: aload_1
    //   328: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   331: istore 5
    //   333: goto -34 -> 299
    //   336: aload_1
    //   337: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   340: istore 6
    //   342: goto -30 -> 312
    //   345: new 250	org/vidogram/messenger/audioinfo/mp3/MP3Frame$Header
    //   348: dup
    //   349: iload 4
    //   351: iload 5
    //   353: iload 6
    //   355: invokespecial 288	org/vidogram/messenger/audioinfo/mp3/MP3Frame$Header:<init>	(III)V
    //   358: aload 7
    //   360: invokevirtual 308	org/vidogram/messenger/audioinfo/mp3/MP3Frame$Header:isCompatible	(Lorg/vidogram/messenger/audioinfo/mp3/MP3Frame$Header;)Z
    //   363: ifeq +22 -> 385
    //   366: aload_1
    //   367: invokevirtual 291	org/vidogram/messenger/audioinfo/mp3/MP3Input:reset	()V
    //   370: aload_1
    //   371: aload 8
    //   373: arraylength
    //   374: iconst_2
    //   375: isub
    //   376: i2l
    //   377: invokevirtual 208	org/vidogram/messenger/audioinfo/mp3/MP3Input:skipFully	(J)V
    //   380: aload 9
    //   382: areturn
    //   383: astore 7
    //   385: aload_1
    //   386: invokevirtual 291	org/vidogram/messenger/audioinfo/mp3/MP3Input:reset	()V
    //   389: aload_2
    //   390: aload_1
    //   391: invokeinterface 278 2 0
    //   396: ifeq +19 -> 415
    //   399: iconst_m1
    //   400: istore 4
    //   402: iload 4
    //   404: istore 5
    //   406: iload_3
    //   407: istore 4
    //   409: iload 5
    //   411: istore_3
    //   412: goto -397 -> 15
    //   415: aload_1
    //   416: invokevirtual 285	org/vidogram/messenger/audioinfo/mp3/MP3Input:read	()I
    //   419: istore 4
    //   421: goto -19 -> 402
    //   424: astore_1
    //   425: goto -362 -> 63
    //
    // Exception table:
    //   from	to	target	type
    //   101	115	238	org/vidogram/messenger/audioinfo/mp3/MP3Exception
    //   345	380	383	org/vidogram/messenger/audioinfo/mp3/MP3Exception
    //   155	167	424	java/io/EOFException
  }

  MP3Frame readNextFrame(MP3Input paramMP3Input, StopReadCondition paramStopReadCondition, MP3Frame paramMP3Frame)
  {
    paramMP3Frame = paramMP3Frame.getHeader();
    paramMP3Input.mark(4);
    int i;
    if (paramStopReadCondition.stopRead(paramMP3Input))
    {
      i = -1;
      if (!paramStopReadCondition.stopRead(paramMP3Input))
        break label59;
    }
    label59: for (int j = -1; ; j = paramMP3Input.read())
    {
      if ((i != -1) && (j != -1))
        break label68;
      return null;
      i = paramMP3Input.read();
      break;
    }
    label68: if ((i == 255) && ((j & 0xE0) == 224))
    {
      int k;
      if (paramStopReadCondition.stopRead(paramMP3Input))
      {
        k = -1;
        if (!paramStopReadCondition.stopRead(paramMP3Input))
          break label137;
      }
      for (int m = -1; ; m = paramMP3Input.read())
      {
        if ((k != -1) && (m != -1))
          break label146;
        return null;
        k = paramMP3Input.read();
        break;
      }
      try
      {
        paramStopReadCondition = new MP3Frame.Header(j, k, m);
        if ((paramStopReadCondition != null) && (paramStopReadCondition.isCompatible(paramMP3Frame)))
        {
          paramMP3Frame = new byte[paramStopReadCondition.getFrameSize()];
          paramMP3Frame[0] = (byte)i;
          paramMP3Frame[1] = (byte)j;
          paramMP3Frame[2] = (byte)k;
          paramMP3Frame[3] = (byte)m;
        }
      }
      catch (MP3Exception paramStopReadCondition)
      {
        try
        {
          paramMP3Input.readFully(paramMP3Frame, 4, paramMP3Frame.length - 4);
          return new MP3Frame(paramStopReadCondition, paramMP3Frame);
          paramStopReadCondition = paramStopReadCondition;
          paramStopReadCondition = null;
        }
        catch (java.io.EOFException paramMP3Input)
        {
          return null;
        }
      }
    }
    label137: label146: paramMP3Input.reset();
    return null;
  }

  static abstract interface StopReadCondition
  {
    public abstract boolean stopRead(MP3Input paramMP3Input);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.MP3Info
 * JD-Core Version:    0.6.0
 */