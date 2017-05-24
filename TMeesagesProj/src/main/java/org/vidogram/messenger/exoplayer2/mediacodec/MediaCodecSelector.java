package org.vidogram.messenger.exoplayer2.mediacodec;

public abstract interface MediaCodecSelector
{
  public static final MediaCodecSelector DEFAULT = new MediaCodecSelector()
  {
    public MediaCodecInfo getDecoderInfo(String paramString, boolean paramBoolean)
    {
      return MediaCodecUtil.getDecoderInfo(paramString, paramBoolean);
    }

    public MediaCodecInfo getPassthroughDecoderInfo()
    {
      return MediaCodecUtil.getPassthroughDecoderInfo();
    }
  };

  public abstract MediaCodecInfo getDecoderInfo(String paramString, boolean paramBoolean);

  public abstract MediaCodecInfo getPassthroughDecoderInfo();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.mediacodec.MediaCodecSelector
 * JD-Core Version:    0.6.0
 */