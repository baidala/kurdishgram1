package org.vidogram.messenger.exoplayer2.source.hls.playlist;

import java.util.Collections;
import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;

public final class HlsMasterPlaylist extends HlsPlaylist
{
  public final List<HlsUrl> audios;
  public final Format muxedAudioFormat;
  public final Format muxedCaptionFormat;
  public final List<HlsUrl> subtitles;
  public final List<HlsUrl> variants;

  public HlsMasterPlaylist(String paramString, List<HlsUrl> paramList1, List<HlsUrl> paramList2, List<HlsUrl> paramList3, Format paramFormat1, Format paramFormat2)
  {
    super(paramString, 0);
    this.variants = Collections.unmodifiableList(paramList1);
    this.audios = Collections.unmodifiableList(paramList2);
    this.subtitles = Collections.unmodifiableList(paramList3);
    this.muxedAudioFormat = paramFormat1;
    this.muxedCaptionFormat = paramFormat2;
  }

  public static HlsMasterPlaylist createSingleVariantMasterPlaylist(String paramString)
  {
    paramString = Collections.singletonList(HlsUrl.createMediaPlaylistHlsUrl(paramString));
    List localList = Collections.emptyList();
    return new HlsMasterPlaylist(null, paramString, localList, localList, null, null);
  }

  public static final class HlsUrl
  {
    public final Format audioFormat;
    public final Format format;
    public final String name;
    public final Format[] textFormats;
    public final String url;
    public final Format videoFormat;

    public HlsUrl(String paramString1, String paramString2, Format paramFormat1, Format paramFormat2, Format paramFormat3, Format[] paramArrayOfFormat)
    {
      this.name = paramString1;
      this.url = paramString2;
      this.format = paramFormat1;
      this.videoFormat = paramFormat2;
      this.audioFormat = paramFormat3;
      this.textFormats = paramArrayOfFormat;
    }

    public static HlsUrl createMediaPlaylistHlsUrl(String paramString)
    {
      return new HlsUrl(null, paramString, Format.createContainerFormat("0", "application/x-mpegURL", null, null, -1), null, null, null);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist
 * JD-Core Version:    0.6.0
 */