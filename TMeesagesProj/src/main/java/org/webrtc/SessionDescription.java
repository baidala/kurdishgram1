package org.webrtc;

public class SessionDescription
{
  public final String description;
  public final Type type;

  public SessionDescription(Type paramType, String paramString)
  {
    this.type = paramType;
    this.description = paramString;
  }

  public static enum Type
  {
    static
    {
      ANSWER = new Type("ANSWER", 2);
      $VALUES = new Type[] { OFFER, PRANSWER, ANSWER };
    }

    public static Type fromCanonicalForm(String paramString)
    {
      return (Type)valueOf(Type.class, paramString.toUpperCase());
    }

    public String canonicalForm()
    {
      return name().toLowerCase();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.SessionDescription
 * JD-Core Version:    0.6.0
 */