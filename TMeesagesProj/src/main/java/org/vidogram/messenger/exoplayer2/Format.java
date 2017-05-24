package org.vidogram.messenger.exoplayer2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class Format
  implements Parcelable
{
  public static final Parcelable.Creator<Format> CREATOR = new Parcelable.Creator()
  {
    public Format createFromParcel(Parcel paramParcel)
    {
      return new Format(paramParcel);
    }

    public Format[] newArray(int paramInt)
    {
      return new Format[paramInt];
    }
  };
  public static final int NO_VALUE = -1;
  public static final long OFFSET_SAMPLE_RELATIVE = 9223372036854775807L;
  public final int accessibilityChannel;
  public final int bitrate;
  public final int channelCount;
  public final String codecs;
  public final String containerMimeType;
  public final DrmInitData drmInitData;
  public final int encoderDelay;
  public final int encoderPadding;
  public final float frameRate;
  private MediaFormat frameworkMediaFormat;
  private int hashCode;
  public final int height;
  public final String id;
  public final List<byte[]> initializationData;
  public final String language;
  public final int maxInputSize;
  public final Metadata metadata;
  public final int pcmEncoding;
  public final float pixelWidthHeightRatio;
  public final byte[] projectionData;
  public final int rotationDegrees;
  public final String sampleMimeType;
  public final int sampleRate;
  public final int selectionFlags;
  public final int stereoMode;
  public final long subsampleOffsetUs;
  public final int width;

  Format(Parcel paramParcel)
  {
    this.id = paramParcel.readString();
    this.containerMimeType = paramParcel.readString();
    this.sampleMimeType = paramParcel.readString();
    this.codecs = paramParcel.readString();
    this.bitrate = paramParcel.readInt();
    this.maxInputSize = paramParcel.readInt();
    this.width = paramParcel.readInt();
    this.height = paramParcel.readInt();
    this.frameRate = paramParcel.readFloat();
    this.rotationDegrees = paramParcel.readInt();
    this.pixelWidthHeightRatio = paramParcel.readFloat();
    int i;
    if (paramParcel.readInt() != 0)
    {
      i = 1;
      if (i == 0)
        break label247;
    }
    label247: for (byte[] arrayOfByte = paramParcel.createByteArray(); ; arrayOfByte = null)
    {
      this.projectionData = arrayOfByte;
      this.stereoMode = paramParcel.readInt();
      this.channelCount = paramParcel.readInt();
      this.sampleRate = paramParcel.readInt();
      this.pcmEncoding = paramParcel.readInt();
      this.encoderDelay = paramParcel.readInt();
      this.encoderPadding = paramParcel.readInt();
      this.selectionFlags = paramParcel.readInt();
      this.language = paramParcel.readString();
      this.accessibilityChannel = paramParcel.readInt();
      this.subsampleOffsetUs = paramParcel.readLong();
      int j = paramParcel.readInt();
      this.initializationData = new ArrayList(j);
      i = 0;
      while (i < j)
      {
        this.initializationData.add(paramParcel.createByteArray());
        i += 1;
      }
      i = 0;
      break;
    }
    this.drmInitData = ((DrmInitData)paramParcel.readParcelable(DrmInitData.class.getClassLoader()));
    this.metadata = ((Metadata)paramParcel.readParcelable(Metadata.class.getClassLoader()));
  }

  Format(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, int paramInt5, float paramFloat2, byte[] paramArrayOfByte, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, String paramString5, int paramInt13, long paramLong, List<byte[]> paramList, DrmInitData paramDrmInitData, Metadata paramMetadata)
  {
    this.id = paramString1;
    this.containerMimeType = paramString2;
    this.sampleMimeType = paramString3;
    this.codecs = paramString4;
    this.bitrate = paramInt1;
    this.maxInputSize = paramInt2;
    this.width = paramInt3;
    this.height = paramInt4;
    this.frameRate = paramFloat1;
    this.rotationDegrees = paramInt5;
    this.pixelWidthHeightRatio = paramFloat2;
    this.projectionData = paramArrayOfByte;
    this.stereoMode = paramInt6;
    this.channelCount = paramInt7;
    this.sampleRate = paramInt8;
    this.pcmEncoding = paramInt9;
    this.encoderDelay = paramInt10;
    this.encoderPadding = paramInt11;
    this.selectionFlags = paramInt12;
    this.language = paramString5;
    this.accessibilityChannel = paramInt13;
    this.subsampleOffsetUs = paramLong;
    paramString1 = paramList;
    if (paramList == null)
      paramString1 = Collections.emptyList();
    this.initializationData = paramString1;
    this.drmInitData = paramDrmInitData;
    this.metadata = paramMetadata;
  }

  public static Format createAudioContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, List<byte[]> paramList, int paramInt4, String paramString5)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, paramInt2, paramInt3, -1, -1, -1, paramInt4, paramString5, -1, 9223372036854775807L, paramList, null, null);
  }

  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, List<byte[]> paramList, DrmInitData paramDrmInitData, int paramInt8, String paramString4, Metadata paramMetadata)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt1, paramInt2, -1, -1, -1.0F, -1, -1.0F, null, -1, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramString4, -1, 9223372036854775807L, paramList, paramDrmInitData, paramMetadata);
  }

  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List<byte[]> paramList, DrmInitData paramDrmInitData, int paramInt6, String paramString4)
  {
    return createAudioSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, -1, -1, paramList, paramDrmInitData, paramInt6, paramString4, null);
  }

  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, List<byte[]> paramList, DrmInitData paramDrmInitData, int paramInt5, String paramString4)
  {
    return createAudioSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, -1, paramList, paramDrmInitData, paramInt5, paramString4);
  }

  public static Format createContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    return new Format(paramString1, paramString2, paramString4, paramString3, paramInt, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, -1, -1, -1, -1, -1, 0, null, -1, 9223372036854775807L, null, null, null);
  }

  public static Format createImageSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt, List<byte[]> paramList, String paramString4, DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, -1, -1, -1, -1, -1, 0, paramString4, -1, 9223372036854775807L, paramList, paramDrmInitData, null);
  }

  public static Format createSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt, DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, -1, -1, -1, -1, -1, 0, null, -1, 9223372036854775807L, null, paramDrmInitData, null);
  }

  public static Format createTextContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, String paramString5)
  {
    return createTextContainerFormat(paramString1, paramString2, paramString3, paramString4, paramInt1, paramInt2, paramString5, -1);
  }

  public static Format createTextContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, String paramString5, int paramInt3)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, -1, -1, -1, -1, -1, paramInt2, paramString5, paramInt3, 9223372036854775807L, null, null, null);
  }

  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, int paramInt3, DrmInitData paramDrmInitData)
  {
    return createTextSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4, paramInt3, paramDrmInitData, 9223372036854775807L);
  }

  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, int paramInt3, DrmInitData paramDrmInitData, long paramLong)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, -1, -1, -1, -1, -1, paramInt2, paramString4, paramInt3, paramLong, null, paramDrmInitData, null);
  }

  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, DrmInitData paramDrmInitData)
  {
    return createTextSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4, -1, paramDrmInitData, 9223372036854775807L);
  }

  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, DrmInitData paramDrmInitData, long paramLong)
  {
    return createTextSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4, -1, paramDrmInitData, paramLong);
  }

  public static Format createVideoContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, float paramFloat, List<byte[]> paramList)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramInt1, -1, paramInt2, paramInt3, paramFloat, -1, -1.0F, null, -1, -1, -1, -1, -1, -1, 0, null, -1, 9223372036854775807L, paramList, null, null);
  }

  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, List<byte[]> paramList, int paramInt5, float paramFloat2, DrmInitData paramDrmInitData)
  {
    return createVideoSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramList, paramInt5, paramFloat2, null, -1, paramDrmInitData);
  }

  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, List<byte[]> paramList, int paramInt5, float paramFloat2, byte[] paramArrayOfByte, int paramInt6, DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramInt5, paramFloat2, paramArrayOfByte, paramInt6, -1, -1, -1, -1, -1, 0, null, -1, 9223372036854775807L, paramList, paramDrmInitData, null);
  }

  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, List<byte[]> paramList, DrmInitData paramDrmInitData)
  {
    return createVideoSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat, paramList, -1, -1.0F, paramDrmInitData);
  }

  @TargetApi(16)
  private static void maybeSetFloatV16(MediaFormat paramMediaFormat, String paramString, float paramFloat)
  {
    if (paramFloat != -1.0F)
      paramMediaFormat.setFloat(paramString, paramFloat);
  }

  @TargetApi(16)
  private static void maybeSetIntegerV16(MediaFormat paramMediaFormat, String paramString, int paramInt)
  {
    if (paramInt != -1)
      paramMediaFormat.setInteger(paramString, paramInt);
  }

  @TargetApi(16)
  private static void maybeSetStringV16(MediaFormat paramMediaFormat, String paramString1, String paramString2)
  {
    if (paramString2 != null)
      paramMediaFormat.setString(paramString1, paramString2);
  }

  public Format copyWithContainerInfo(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3)
  {
    return new Format(paramString1, this.containerMimeType, this.sampleMimeType, paramString2, paramInt1, this.maxInputSize, paramInt2, paramInt3, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, paramInt4, paramString3, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
  }

  public Format copyWithDrmInitData(DrmInitData paramDrmInitData)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, paramDrmInitData, this.metadata);
  }

  public Format copyWithGaplessInfo(int paramInt1, int paramInt2)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.channelCount, this.sampleRate, this.pcmEncoding, paramInt1, paramInt2, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
  }

  public Format copyWithManifestFormatInfo(Format paramFormat, boolean paramBoolean)
  {
    String str3 = paramFormat.id;
    String str1;
    int i;
    label33: float f;
    label48: int j;
    int k;
    String str2;
    if (this.codecs == null)
    {
      str1 = paramFormat.codecs;
      if (this.bitrate != -1)
        break label200;
      i = paramFormat.bitrate;
      if (this.frameRate != -1.0F)
        break label209;
      f = paramFormat.frameRate;
      j = this.selectionFlags;
      k = paramFormat.selectionFlags;
      if (this.language != null)
        break label217;
      str2 = paramFormat.language;
      label73: if (((!paramBoolean) || (paramFormat.drmInitData == null)) && (this.drmInitData != null))
        break label226;
    }
    label200: label209: label217: label226: for (paramFormat = paramFormat.drmInitData; ; paramFormat = this.drmInitData)
    {
      return new Format(str3, this.containerMimeType, this.sampleMimeType, str1, i, this.maxInputSize, this.width, this.height, f, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, j | k, str2, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, paramFormat, this.metadata);
      str1 = this.codecs;
      break;
      i = this.bitrate;
      break label33;
      f = this.frameRate;
      break label48;
      str2 = this.language;
      break label73;
    }
  }

  public Format copyWithMaxInputSize(int paramInt)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, paramInt, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
  }

  public Format copyWithMetadata(Metadata paramMetadata)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, paramMetadata);
  }

  public Format copyWithSubsampleOffsetUs(long paramLong)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, paramLong, this.initializationData, this.drmInitData, this.metadata);
  }

  public int describeContents()
  {
    return 0;
  }

  public boolean equals(Object paramObject)
  {
    int k = 0;
    int j;
    if (this == paramObject)
      j = 1;
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  do
                                  {
                                    do
                                    {
                                      do
                                      {
                                        do
                                        {
                                          do
                                          {
                                            do
                                            {
                                              do
                                              {
                                                do
                                                {
                                                  do
                                                  {
                                                    do
                                                    {
                                                      do
                                                      {
                                                        do
                                                        {
                                                          return j;
                                                          j = k;
                                                        }
                                                        while (paramObject == null);
                                                        j = k;
                                                      }
                                                      while (getClass() != paramObject.getClass());
                                                      paramObject = (Format)paramObject;
                                                      j = k;
                                                    }
                                                    while (this.bitrate != paramObject.bitrate);
                                                    j = k;
                                                  }
                                                  while (this.maxInputSize != paramObject.maxInputSize);
                                                  j = k;
                                                }
                                                while (this.width != paramObject.width);
                                                j = k;
                                              }
                                              while (this.height != paramObject.height);
                                              j = k;
                                            }
                                            while (this.frameRate != paramObject.frameRate);
                                            j = k;
                                          }
                                          while (this.rotationDegrees != paramObject.rotationDegrees);
                                          j = k;
                                        }
                                        while (this.pixelWidthHeightRatio != paramObject.pixelWidthHeightRatio);
                                        j = k;
                                      }
                                      while (this.stereoMode != paramObject.stereoMode);
                                      j = k;
                                    }
                                    while (this.channelCount != paramObject.channelCount);
                                    j = k;
                                  }
                                  while (this.sampleRate != paramObject.sampleRate);
                                  j = k;
                                }
                                while (this.pcmEncoding != paramObject.pcmEncoding);
                                j = k;
                              }
                              while (this.encoderDelay != paramObject.encoderDelay);
                              j = k;
                            }
                            while (this.encoderPadding != paramObject.encoderPadding);
                            j = k;
                          }
                          while (this.subsampleOffsetUs != paramObject.subsampleOffsetUs);
                          j = k;
                        }
                        while (this.selectionFlags != paramObject.selectionFlags);
                        j = k;
                      }
                      while (!Util.areEqual(this.id, paramObject.id));
                      j = k;
                    }
                    while (!Util.areEqual(this.language, paramObject.language));
                    j = k;
                  }
                  while (this.accessibilityChannel != paramObject.accessibilityChannel);
                  j = k;
                }
                while (!Util.areEqual(this.containerMimeType, paramObject.containerMimeType));
                j = k;
              }
              while (!Util.areEqual(this.sampleMimeType, paramObject.sampleMimeType));
              j = k;
            }
            while (!Util.areEqual(this.codecs, paramObject.codecs));
            j = k;
          }
          while (!Util.areEqual(this.drmInitData, paramObject.drmInitData));
          j = k;
        }
        while (!Util.areEqual(this.metadata, paramObject.metadata));
        j = k;
      }
      while (!Arrays.equals(this.projectionData, paramObject.projectionData));
      j = k;
    }
    while (this.initializationData.size() != paramObject.initializationData.size());
    int i = 0;
    while (true)
    {
      if (i >= this.initializationData.size())
        break label482;
      j = k;
      if (!Arrays.equals((byte[])this.initializationData.get(i), (byte[])paramObject.initializationData.get(i)))
        break;
      i += 1;
    }
    label482: return true;
  }

  @SuppressLint({"InlinedApi"})
  @TargetApi(16)
  public final MediaFormat getFrameworkMediaFormatV16()
  {
    if (this.frameworkMediaFormat == null)
    {
      MediaFormat localMediaFormat = new MediaFormat();
      localMediaFormat.setString("mime", this.sampleMimeType);
      maybeSetStringV16(localMediaFormat, "language", this.language);
      maybeSetIntegerV16(localMediaFormat, "max-input-size", this.maxInputSize);
      maybeSetIntegerV16(localMediaFormat, "width", this.width);
      maybeSetIntegerV16(localMediaFormat, "height", this.height);
      maybeSetFloatV16(localMediaFormat, "frame-rate", this.frameRate);
      maybeSetIntegerV16(localMediaFormat, "rotation-degrees", this.rotationDegrees);
      maybeSetIntegerV16(localMediaFormat, "channel-count", this.channelCount);
      maybeSetIntegerV16(localMediaFormat, "sample-rate", this.sampleRate);
      maybeSetIntegerV16(localMediaFormat, "encoder-delay", this.encoderDelay);
      maybeSetIntegerV16(localMediaFormat, "encoder-padding", this.encoderPadding);
      int i = 0;
      while (i < this.initializationData.size())
      {
        localMediaFormat.setByteBuffer("csd-" + i, ByteBuffer.wrap((byte[])this.initializationData.get(i)));
        i += 1;
      }
      this.frameworkMediaFormat = localMediaFormat;
    }
    return this.frameworkMediaFormat;
  }

  public int getPixelCount()
  {
    if ((this.width == -1) || (this.height == -1))
      return -1;
    return this.width * this.height;
  }

  public int hashCode()
  {
    int i2 = 0;
    int i;
    int j;
    label28: int k;
    label37: int m;
    label47: int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    int n;
    label87: int i8;
    int i1;
    if (this.hashCode == 0)
    {
      if (this.id != null)
        break label194;
      i = 0;
      if (this.containerMimeType != null)
        break label205;
      j = 0;
      if (this.sampleMimeType != null)
        break label216;
      k = 0;
      if (this.codecs != null)
        break label227;
      m = 0;
      i3 = this.bitrate;
      i4 = this.width;
      i5 = this.height;
      i6 = this.channelCount;
      i7 = this.sampleRate;
      if (this.language != null)
        break label239;
      n = 0;
      i8 = this.accessibilityChannel;
      if (this.drmInitData != null)
        break label251;
      i1 = 0;
      label103: if (this.metadata != null)
        break label263;
    }
    while (true)
    {
      this.hashCode = ((i1 + ((n + ((((((m + (k + (j + (i + 527) * 31) * 31) * 31) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31) * 31 + i8) * 31) * 31 + i2);
      return this.hashCode;
      label194: i = this.id.hashCode();
      break;
      label205: j = this.containerMimeType.hashCode();
      break label28;
      label216: k = this.sampleMimeType.hashCode();
      break label37;
      label227: m = this.codecs.hashCode();
      break label47;
      label239: n = this.language.hashCode();
      break label87;
      label251: i1 = this.drmInitData.hashCode();
      break label103;
      label263: i2 = this.metadata.hashCode();
    }
  }

  public String toString()
  {
    return "Format(" + this.id + ", " + this.containerMimeType + ", " + this.sampleMimeType + ", " + this.bitrate + ", , " + this.language + ", [" + this.width + ", " + this.height + ", " + this.frameRate + "], [" + this.channelCount + ", " + this.sampleRate + "])";
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.containerMimeType);
    paramParcel.writeString(this.sampleMimeType);
    paramParcel.writeString(this.codecs);
    paramParcel.writeInt(this.bitrate);
    paramParcel.writeInt(this.maxInputSize);
    paramParcel.writeInt(this.width);
    paramParcel.writeInt(this.height);
    paramParcel.writeFloat(this.frameRate);
    paramParcel.writeInt(this.rotationDegrees);
    paramParcel.writeFloat(this.pixelWidthHeightRatio);
    if (this.projectionData != null);
    for (paramInt = 1; ; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      if (this.projectionData != null)
        paramParcel.writeByteArray(this.projectionData);
      paramParcel.writeInt(this.stereoMode);
      paramParcel.writeInt(this.channelCount);
      paramParcel.writeInt(this.sampleRate);
      paramParcel.writeInt(this.pcmEncoding);
      paramParcel.writeInt(this.encoderDelay);
      paramParcel.writeInt(this.encoderPadding);
      paramParcel.writeInt(this.selectionFlags);
      paramParcel.writeString(this.language);
      paramParcel.writeInt(this.accessibilityChannel);
      paramParcel.writeLong(this.subsampleOffsetUs);
      int i = this.initializationData.size();
      paramParcel.writeInt(i);
      paramInt = 0;
      while (paramInt < i)
      {
        paramParcel.writeByteArray((byte[])this.initializationData.get(paramInt));
        paramInt += 1;
      }
    }
    paramParcel.writeParcelable(this.drmInitData, 0);
    paramParcel.writeParcelable(this.metadata, 0);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.Format
 * JD-Core Version:    0.6.0
 */