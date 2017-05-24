package org.webrtc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MediaConstraints
{
  public final List<KeyValuePair> mandatory = new LinkedList();
  public final List<KeyValuePair> optional = new LinkedList();

  private static String stringifyKeyValuePairList(List<KeyValuePair> paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder("[");
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      KeyValuePair localKeyValuePair = (KeyValuePair)paramList.next();
      if (localStringBuilder.length() > 1)
        localStringBuilder.append(", ");
      localStringBuilder.append(localKeyValuePair.toString());
    }
    return "]";
  }

  public String toString()
  {
    return "mandatory: " + stringifyKeyValuePairList(this.mandatory) + ", optional: " + stringifyKeyValuePairList(this.optional);
  }

  public static class KeyValuePair
  {
    private final String key;
    private final String value;

    public KeyValuePair(String paramString1, String paramString2)
    {
      this.key = paramString1;
      this.value = paramString2;
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
          return false;
        paramObject = (KeyValuePair)paramObject;
      }
      while ((this.key.equals(paramObject.key)) && (this.value.equals(paramObject.value)));
      return false;
    }

    public String getKey()
    {
      return this.key;
    }

    public String getValue()
    {
      return this.value;
    }

    public int hashCode()
    {
      return this.key.hashCode() + this.value.hashCode();
    }

    public String toString()
    {
      return this.key + ": " + this.value;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.MediaConstraints
 * JD-Core Version:    0.6.0
 */