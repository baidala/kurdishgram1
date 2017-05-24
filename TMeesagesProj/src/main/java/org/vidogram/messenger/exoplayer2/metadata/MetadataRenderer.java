package org.vidogram.messenger.exoplayer2.metadata;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.nio.ByteBuffer;
import org.vidogram.messenger.exoplayer2.BaseRenderer;
import org.vidogram.messenger.exoplayer2.ExoPlaybackException;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class MetadataRenderer extends BaseRenderer
  implements Handler.Callback
{
  private static final int MSG_INVOKE_RENDERER = 0;
  private final DecoderInputBuffer buffer;
  private final FormatHolder formatHolder;
  private boolean inputStreamEnded;
  private final MetadataDecoder metadataDecoder;
  private final Output output;
  private final Handler outputHandler;
  private Metadata pendingMetadata;
  private long pendingMetadataTimestamp;

  public MetadataRenderer(Output paramOutput, Looper paramLooper, MetadataDecoder paramMetadataDecoder)
  {
    super(4);
    this.output = ((Output)Assertions.checkNotNull(paramOutput));
    if (paramLooper == null);
    for (paramOutput = null; ; paramOutput = new Handler(paramLooper, this))
    {
      this.outputHandler = paramOutput;
      this.metadataDecoder = ((MetadataDecoder)Assertions.checkNotNull(paramMetadataDecoder));
      this.formatHolder = new FormatHolder();
      this.buffer = new DecoderInputBuffer(1);
      return;
    }
  }

  private void invokeRenderer(Metadata paramMetadata)
  {
    if (this.outputHandler != null)
    {
      this.outputHandler.obtainMessage(0, paramMetadata).sendToTarget();
      return;
    }
    invokeRendererInternal(paramMetadata);
  }

  private void invokeRendererInternal(Metadata paramMetadata)
  {
    this.output.onMetadata(paramMetadata);
  }

  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default:
      return false;
    case 0:
    }
    invokeRendererInternal((Metadata)paramMessage.obj);
    return true;
  }

  public boolean isEnded()
  {
    return this.inputStreamEnded;
  }

  public boolean isReady()
  {
    return true;
  }

  protected void onDisabled()
  {
    this.pendingMetadata = null;
    super.onDisabled();
  }

  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    this.pendingMetadata = null;
    this.inputStreamEnded = false;
  }

  public void render(long paramLong1, long paramLong2)
  {
    if ((!this.inputStreamEnded) && (this.pendingMetadata == null))
    {
      this.buffer.clear();
      if (readSource(this.formatHolder, this.buffer) == -4)
      {
        if (!this.buffer.isEndOfStream())
          break label83;
        this.inputStreamEnded = true;
      }
    }
    while (true)
    {
      if ((this.pendingMetadata != null) && (this.pendingMetadataTimestamp <= paramLong1))
      {
        invokeRenderer(this.pendingMetadata);
        this.pendingMetadata = null;
      }
      return;
      label83: this.pendingMetadataTimestamp = this.buffer.timeUs;
      try
      {
        this.buffer.flip();
        ByteBuffer localByteBuffer = this.buffer.data;
        this.pendingMetadata = this.metadataDecoder.decode(localByteBuffer.array(), localByteBuffer.limit());
      }
      catch (MetadataDecoderException localMetadataDecoderException)
      {
      }
    }
    throw ExoPlaybackException.createForRenderer(localMetadataDecoderException, getIndex());
  }

  public int supportsFormat(Format paramFormat)
  {
    if (this.metadataDecoder.canDecode(paramFormat.sampleMimeType))
      return 3;
    return 0;
  }

  public static abstract interface Output
  {
    public abstract void onMetadata(Metadata paramMetadata);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.MetadataRenderer
 * JD-Core Version:    0.6.0
 */