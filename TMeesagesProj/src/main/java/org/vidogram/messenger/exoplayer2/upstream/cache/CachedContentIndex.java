package org.vidogram.messenger.exoplayer2.upstream.cache;

import android.util.SparseArray;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.AtomicFile;
import org.vidogram.messenger.exoplayer2.util.ReusableBufferedOutputStream;

final class CachedContentIndex
{
  public static final String FILE_NAME = "cached_content_index.exi";
  private static final int FLAG_ENCRYPTED_INDEX = 1;
  private static final int VERSION = 1;
  private final AtomicFile atomicFile;
  private ReusableBufferedOutputStream bufferedOutputStream;
  private boolean changed;
  private final Cipher cipher;
  private final SparseArray<String> idToKey;
  private final HashMap<String, CachedContent> keyToContent;
  private final SecretKeySpec secretKeySpec;

  public CachedContentIndex(File paramFile)
  {
    this(paramFile, null);
  }

  public CachedContentIndex(File paramFile, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null);
    try
    {
      this.cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      this.secretKeySpec = new SecretKeySpec(paramArrayOfByte, "AES");
      this.keyToContent = new HashMap();
      this.idToKey = new SparseArray();
      this.atomicFile = new AtomicFile(new File(paramFile, "cached_content_index.exi"));
      return;
    }
    catch (java.security.NoSuchAlgorithmException paramFile)
    {
      while (true)
      {
        throw new IllegalStateException(paramFile);
        this.cipher = null;
        this.secretKeySpec = null;
      }
    }
    catch (javax.crypto.NoSuchPaddingException paramFile)
    {
      label76: break label76;
    }
  }

  private CachedContent addNew(String paramString, long paramLong)
  {
    paramString = new CachedContent(getNewId(this.idToKey), paramString, paramLong);
    addNew(paramString);
    return paramString;
  }

  public static int getNewId(SparseArray<String> paramSparseArray)
  {
    int k = paramSparseArray.size();
    int i;
    int j;
    if (k == 0)
    {
      i = 0;
      j = i;
      if (i < 0)
        i = 0;
    }
    while (true)
    {
      j = i;
      if (i < k)
      {
        if (i != paramSparseArray.keyAt(i))
          j = i;
      }
      else
      {
        return j;
        i = paramSparseArray.keyAt(k - 1) + 1;
        break;
      }
      i += 1;
    }
  }

  // ERROR //
  private boolean readFile()
  {
    // Byte code:
    //   0: new 120	java/io/BufferedInputStream
    //   3: dup
    //   4: aload_0
    //   5: getfield 81	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:atomicFile	Lorg/vidogram/messenger/exoplayer2/util/AtomicFile;
    //   8: invokevirtual 124	org/vidogram/messenger/exoplayer2/util/AtomicFile:openRead	()Ljava/io/InputStream;
    //   11: invokespecial 127	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   14: astore 8
    //   16: new 129	java/io/DataInputStream
    //   19: dup
    //   20: aload 8
    //   22: invokespecial 130	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   25: astore 6
    //   27: aload 6
    //   29: astore 7
    //   31: aload 6
    //   33: invokevirtual 133	java/io/DataInputStream:readInt	()I
    //   36: istore_1
    //   37: iload_1
    //   38: iconst_1
    //   39: if_icmpeq +15 -> 54
    //   42: aload 6
    //   44: ifnull +8 -> 52
    //   47: aload 6
    //   49: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   52: iconst_0
    //   53: ireturn
    //   54: aload 6
    //   56: astore 5
    //   58: aload 6
    //   60: astore 7
    //   62: aload 6
    //   64: invokevirtual 133	java/io/DataInputStream:readInt	()I
    //   67: iconst_1
    //   68: iand
    //   69: ifeq +111 -> 180
    //   72: aload 6
    //   74: astore 7
    //   76: aload_0
    //   77: getfield 51	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   80: astore 5
    //   82: aload 5
    //   84: ifnonnull +15 -> 99
    //   87: aload 6
    //   89: ifnull -37 -> 52
    //   92: aload 6
    //   94: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   97: iconst_0
    //   98: ireturn
    //   99: aload 6
    //   101: astore 7
    //   103: bipush 16
    //   105: newarray byte
    //   107: astore 5
    //   109: aload 6
    //   111: astore 7
    //   113: aload 6
    //   115: aload 5
    //   117: invokevirtual 143	java/io/DataInputStream:read	([B)I
    //   120: pop
    //   121: aload 6
    //   123: astore 7
    //   125: new 145	javax/crypto/spec/IvParameterSpec
    //   128: dup
    //   129: aload 5
    //   131: invokespecial 148	javax/crypto/spec/IvParameterSpec:<init>	([B)V
    //   134: astore 5
    //   136: aload 6
    //   138: astore 7
    //   140: aload_0
    //   141: getfield 51	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   144: iconst_2
    //   145: aload_0
    //   146: getfield 60	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:secretKeySpec	Ljavax/crypto/spec/SecretKeySpec;
    //   149: aload 5
    //   151: invokevirtual 152	javax/crypto/Cipher:init	(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V
    //   154: aload 6
    //   156: astore 7
    //   158: new 129	java/io/DataInputStream
    //   161: dup
    //   162: new 154	javax/crypto/CipherInputStream
    //   165: dup
    //   166: aload 8
    //   168: aload_0
    //   169: getfield 51	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   172: invokespecial 157	javax/crypto/CipherInputStream:<init>	(Ljava/io/InputStream;Ljavax/crypto/Cipher;)V
    //   175: invokespecial 130	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   178: astore 5
    //   180: aload 5
    //   182: astore 7
    //   184: aload 5
    //   186: invokevirtual 133	java/io/DataInputStream:readInt	()I
    //   189: istore_3
    //   190: iconst_0
    //   191: istore_2
    //   192: iconst_0
    //   193: istore_1
    //   194: iload_2
    //   195: iload_3
    //   196: if_icmpge +85 -> 281
    //   199: aload 5
    //   201: astore 7
    //   203: new 90	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContent
    //   206: dup
    //   207: aload 5
    //   209: invokespecial 160	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContent:<init>	(Ljava/io/DataInputStream;)V
    //   212: astore 6
    //   214: aload 5
    //   216: astore 7
    //   218: aload_0
    //   219: aload 6
    //   221: invokevirtual 100	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:addNew	(Lorg/vidogram/messenger/exoplayer2/upstream/cache/CachedContent;)V
    //   224: aload 5
    //   226: astore 7
    //   228: aload 6
    //   230: invokevirtual 163	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContent:headerHashCode	()I
    //   233: istore 4
    //   235: iload_1
    //   236: iload 4
    //   238: iadd
    //   239: istore_1
    //   240: iload_2
    //   241: iconst_1
    //   242: iadd
    //   243: istore_2
    //   244: goto -50 -> 194
    //   247: astore 5
    //   249: aload 6
    //   251: astore 7
    //   253: new 83	java/lang/IllegalStateException
    //   256: dup
    //   257: aload 5
    //   259: invokespecial 86	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
    //   262: athrow
    //   263: astore 5
    //   265: aload 6
    //   267: astore 5
    //   269: aload 5
    //   271: ifnull -219 -> 52
    //   274: aload 5
    //   276: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   279: iconst_0
    //   280: ireturn
    //   281: aload 5
    //   283: astore 7
    //   285: aload 5
    //   287: invokevirtual 133	java/io/DataInputStream:readInt	()I
    //   290: istore_2
    //   291: iload_2
    //   292: iload_1
    //   293: if_icmpeq +15 -> 308
    //   296: aload 5
    //   298: ifnull -246 -> 52
    //   301: aload 5
    //   303: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   306: iconst_0
    //   307: ireturn
    //   308: aload 5
    //   310: ifnull +8 -> 318
    //   313: aload 5
    //   315: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   318: iconst_1
    //   319: ireturn
    //   320: astore 5
    //   322: aconst_null
    //   323: astore 7
    //   325: aload 7
    //   327: ifnull +8 -> 335
    //   330: aload 7
    //   332: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   335: aload 5
    //   337: athrow
    //   338: astore 5
    //   340: goto -15 -> 325
    //   343: astore 5
    //   345: aconst_null
    //   346: astore 5
    //   348: goto -79 -> 269
    //   351: astore 6
    //   353: goto -84 -> 269
    //   356: astore 5
    //   358: goto -109 -> 249
    //
    // Exception table:
    //   from	to	target	type
    //   140	154	247	java/security/InvalidAlgorithmParameterException
    //   31	37	263	java/io/IOException
    //   62	72	263	java/io/IOException
    //   76	82	263	java/io/IOException
    //   103	109	263	java/io/IOException
    //   113	121	263	java/io/IOException
    //   125	136	263	java/io/IOException
    //   140	154	263	java/io/IOException
    //   158	180	263	java/io/IOException
    //   253	263	263	java/io/IOException
    //   0	27	320	finally
    //   31	37	338	finally
    //   62	72	338	finally
    //   76	82	338	finally
    //   103	109	338	finally
    //   113	121	338	finally
    //   125	136	338	finally
    //   140	154	338	finally
    //   158	180	338	finally
    //   184	190	338	finally
    //   203	214	338	finally
    //   218	224	338	finally
    //   228	235	338	finally
    //   253	263	338	finally
    //   285	291	338	finally
    //   0	27	343	java/io/IOException
    //   184	190	351	java/io/IOException
    //   203	214	351	java/io/IOException
    //   218	224	351	java/io/IOException
    //   228	235	351	java/io/IOException
    //   285	291	351	java/io/IOException
    //   140	154	356	java/security/InvalidKeyException
  }

  // ERROR //
  private void writeFile()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore_3
    //   5: aload_3
    //   6: astore 4
    //   8: aload 5
    //   10: astore_2
    //   11: aload_0
    //   12: getfield 81	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:atomicFile	Lorg/vidogram/messenger/exoplayer2/util/AtomicFile;
    //   15: invokevirtual 168	org/vidogram/messenger/exoplayer2/util/AtomicFile:startWrite	()Ljava/io/OutputStream;
    //   18: astore 6
    //   20: aload_3
    //   21: astore 4
    //   23: aload 5
    //   25: astore_2
    //   26: aload_0
    //   27: getfield 170	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   30: ifnonnull +234 -> 264
    //   33: aload_3
    //   34: astore 4
    //   36: aload 5
    //   38: astore_2
    //   39: aload_0
    //   40: new 172	org/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream
    //   43: dup
    //   44: aload 6
    //   46: invokespecial 175	org/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   49: putfield 170	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   52: aload_3
    //   53: astore 4
    //   55: aload 5
    //   57: astore_2
    //   58: new 177	java/io/DataOutputStream
    //   61: dup
    //   62: aload_0
    //   63: getfield 170	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   66: invokespecial 178	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   69: astore_3
    //   70: aload_3
    //   71: iconst_1
    //   72: invokevirtual 182	java/io/DataOutputStream:writeInt	(I)V
    //   75: aload_0
    //   76: getfield 51	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   79: ifnull +229 -> 308
    //   82: iconst_1
    //   83: istore_1
    //   84: aload_3
    //   85: iload_1
    //   86: invokevirtual 182	java/io/DataOutputStream:writeInt	(I)V
    //   89: aload_0
    //   90: getfield 51	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   93: ifnull +276 -> 369
    //   96: bipush 16
    //   98: newarray byte
    //   100: astore_2
    //   101: new 184	java/util/Random
    //   104: dup
    //   105: invokespecial 185	java/util/Random:<init>	()V
    //   108: aload_2
    //   109: invokevirtual 188	java/util/Random:nextBytes	([B)V
    //   112: aload_3
    //   113: aload_2
    //   114: invokevirtual 191	java/io/DataOutputStream:write	([B)V
    //   117: new 145	javax/crypto/spec/IvParameterSpec
    //   120: dup
    //   121: aload_2
    //   122: invokespecial 148	javax/crypto/spec/IvParameterSpec:<init>	([B)V
    //   125: astore_2
    //   126: aload_0
    //   127: getfield 51	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   130: iconst_1
    //   131: aload_0
    //   132: getfield 60	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:secretKeySpec	Ljavax/crypto/spec/SecretKeySpec;
    //   135: aload_2
    //   136: invokevirtual 152	javax/crypto/Cipher:init	(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V
    //   139: aload_3
    //   140: invokevirtual 194	java/io/DataOutputStream:flush	()V
    //   143: new 177	java/io/DataOutputStream
    //   146: dup
    //   147: new 196	javax/crypto/CipherOutputStream
    //   150: dup
    //   151: aload_0
    //   152: getfield 170	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   155: aload_0
    //   156: getfield 51	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   159: invokespecial 199	javax/crypto/CipherOutputStream:<init>	(Ljava/io/OutputStream;Ljavax/crypto/Cipher;)V
    //   162: invokespecial 178	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   165: astore_2
    //   166: aload_2
    //   167: astore_3
    //   168: aload_3
    //   169: astore 4
    //   171: aload_3
    //   172: astore_2
    //   173: aload_3
    //   174: aload_0
    //   175: getfield 65	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:keyToContent	Ljava/util/HashMap;
    //   178: invokevirtual 200	java/util/HashMap:size	()I
    //   181: invokevirtual 182	java/io/DataOutputStream:writeInt	(I)V
    //   184: aload_3
    //   185: astore 4
    //   187: aload_3
    //   188: astore_2
    //   189: aload_0
    //   190: getfield 65	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:keyToContent	Ljava/util/HashMap;
    //   193: invokevirtual 204	java/util/HashMap:values	()Ljava/util/Collection;
    //   196: invokeinterface 210 1 0
    //   201: astore 5
    //   203: iconst_0
    //   204: istore_1
    //   205: aload_3
    //   206: astore 4
    //   208: aload_3
    //   209: astore_2
    //   210: aload 5
    //   212: invokeinterface 215 1 0
    //   217: ifeq +116 -> 333
    //   220: aload_3
    //   221: astore 4
    //   223: aload_3
    //   224: astore_2
    //   225: aload 5
    //   227: invokeinterface 219 1 0
    //   232: checkcast 90	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContent
    //   235: astore 6
    //   237: aload_3
    //   238: astore 4
    //   240: aload_3
    //   241: astore_2
    //   242: aload 6
    //   244: aload_3
    //   245: invokevirtual 223	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContent:writeToStream	(Ljava/io/DataOutputStream;)V
    //   248: aload_3
    //   249: astore 4
    //   251: aload_3
    //   252: astore_2
    //   253: aload 6
    //   255: invokevirtual 163	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContent:headerHashCode	()I
    //   258: iload_1
    //   259: iadd
    //   260: istore_1
    //   261: goto -56 -> 205
    //   264: aload_3
    //   265: astore 4
    //   267: aload 5
    //   269: astore_2
    //   270: aload_0
    //   271: getfield 170	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   274: aload 6
    //   276: invokevirtual 226	org/vidogram/messenger/exoplayer2/util/ReusableBufferedOutputStream:reset	(Ljava/io/OutputStream;)V
    //   279: goto -227 -> 52
    //   282: astore_3
    //   283: aload 4
    //   285: astore_2
    //   286: new 228	org/vidogram/messenger/exoplayer2/upstream/cache/Cache$CacheException
    //   289: dup
    //   290: aload_3
    //   291: invokespecial 231	org/vidogram/messenger/exoplayer2/upstream/cache/Cache$CacheException:<init>	(Ljava/io/IOException;)V
    //   294: athrow
    //   295: astore 4
    //   297: aload_2
    //   298: astore_3
    //   299: aload 4
    //   301: astore_2
    //   302: aload_3
    //   303: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   306: aload_2
    //   307: athrow
    //   308: iconst_0
    //   309: istore_1
    //   310: goto -226 -> 84
    //   313: astore_2
    //   314: new 83	java/lang/IllegalStateException
    //   317: dup
    //   318: aload_2
    //   319: invokespecial 86	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
    //   322: athrow
    //   323: astore 4
    //   325: aload_3
    //   326: astore_2
    //   327: aload 4
    //   329: astore_3
    //   330: goto -44 -> 286
    //   333: aload_3
    //   334: astore 4
    //   336: aload_3
    //   337: astore_2
    //   338: aload_3
    //   339: iload_1
    //   340: invokevirtual 182	java/io/DataOutputStream:writeInt	(I)V
    //   343: aload_3
    //   344: astore 4
    //   346: aload_3
    //   347: astore_2
    //   348: aload_0
    //   349: getfield 81	org/vidogram/messenger/exoplayer2/upstream/cache/CachedContentIndex:atomicFile	Lorg/vidogram/messenger/exoplayer2/util/AtomicFile;
    //   352: aload_3
    //   353: invokevirtual 234	org/vidogram/messenger/exoplayer2/util/AtomicFile:endWrite	(Ljava/io/OutputStream;)V
    //   356: aload_3
    //   357: invokestatic 139	org/vidogram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   360: return
    //   361: astore_2
    //   362: goto -60 -> 302
    //   365: astore_2
    //   366: goto -52 -> 314
    //   369: goto -201 -> 168
    //
    // Exception table:
    //   from	to	target	type
    //   11	20	282	java/io/IOException
    //   26	33	282	java/io/IOException
    //   39	52	282	java/io/IOException
    //   58	70	282	java/io/IOException
    //   173	184	282	java/io/IOException
    //   189	203	282	java/io/IOException
    //   210	220	282	java/io/IOException
    //   225	237	282	java/io/IOException
    //   242	248	282	java/io/IOException
    //   253	261	282	java/io/IOException
    //   270	279	282	java/io/IOException
    //   338	343	282	java/io/IOException
    //   348	356	282	java/io/IOException
    //   11	20	295	finally
    //   26	33	295	finally
    //   39	52	295	finally
    //   58	70	295	finally
    //   173	184	295	finally
    //   189	203	295	finally
    //   210	220	295	finally
    //   225	237	295	finally
    //   242	248	295	finally
    //   253	261	295	finally
    //   270	279	295	finally
    //   286	295	295	finally
    //   338	343	295	finally
    //   348	356	295	finally
    //   126	139	313	java/security/InvalidAlgorithmParameterException
    //   70	82	323	java/io/IOException
    //   84	126	323	java/io/IOException
    //   126	139	323	java/io/IOException
    //   139	166	323	java/io/IOException
    //   314	323	323	java/io/IOException
    //   70	82	361	finally
    //   84	126	361	finally
    //   126	139	361	finally
    //   139	166	361	finally
    //   314	323	361	finally
    //   126	139	365	java/security/InvalidKeyException
  }

  public CachedContent add(String paramString)
  {
    CachedContent localCachedContent2 = (CachedContent)this.keyToContent.get(paramString);
    CachedContent localCachedContent1 = localCachedContent2;
    if (localCachedContent2 == null)
      localCachedContent1 = addNew(paramString, -1L);
    return localCachedContent1;
  }

  void addNew(CachedContent paramCachedContent)
  {
    this.keyToContent.put(paramCachedContent.key, paramCachedContent);
    this.idToKey.put(paramCachedContent.id, paramCachedContent.key);
    this.changed = true;
  }

  public int assignIdForKey(String paramString)
  {
    return add(paramString).id;
  }

  public CachedContent get(String paramString)
  {
    return (CachedContent)this.keyToContent.get(paramString);
  }

  public Collection<CachedContent> getAll()
  {
    return this.keyToContent.values();
  }

  public long getContentLength(String paramString)
  {
    paramString = get(paramString);
    if (paramString == null)
      return -1L;
    return paramString.getLength();
  }

  public String getKeyForId(int paramInt)
  {
    return (String)this.idToKey.get(paramInt);
  }

  public Set<String> getKeys()
  {
    return this.keyToContent.keySet();
  }

  public void load()
  {
    if (!this.changed);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      if (!readFile())
      {
        this.atomicFile.delete();
        this.keyToContent.clear();
        this.idToKey.clear();
      }
      return;
    }
  }

  public void removeEmpty()
  {
    Object localObject = new LinkedList();
    Iterator localIterator = this.keyToContent.values().iterator();
    while (localIterator.hasNext())
    {
      CachedContent localCachedContent = (CachedContent)localIterator.next();
      if (!localCachedContent.isEmpty())
        continue;
      ((LinkedList)localObject).add(localCachedContent.key);
    }
    localObject = ((LinkedList)localObject).iterator();
    while (((Iterator)localObject).hasNext())
      removeEmpty((String)((Iterator)localObject).next());
  }

  public void removeEmpty(String paramString)
  {
    paramString = (CachedContent)this.keyToContent.remove(paramString);
    if (paramString != null)
    {
      Assertions.checkState(paramString.isEmpty());
      this.idToKey.remove(paramString.id);
      this.changed = true;
    }
  }

  public void setContentLength(String paramString, long paramLong)
  {
    CachedContent localCachedContent = get(paramString);
    if (localCachedContent != null)
    {
      if (localCachedContent.getLength() != paramLong)
      {
        localCachedContent.setLength(paramLong);
        this.changed = true;
      }
      return;
    }
    addNew(paramString, paramLong);
  }

  public void store()
  {
    if (!this.changed)
      return;
    writeFile();
    this.changed = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.CachedContentIndex
 * JD-Core Version:    0.6.0
 */