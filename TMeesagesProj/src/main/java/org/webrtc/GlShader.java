package org.webrtc;

import android.opengl.GLES20;
import java.nio.FloatBuffer;

public class GlShader
{
  private static final String TAG = "GlShader";
  private int program;

  public GlShader(String paramString1, String paramString2)
  {
    int i = compileShader(35633, paramString1);
    int j = compileShader(35632, paramString2);
    this.program = GLES20.glCreateProgram();
    if (this.program == 0)
      throw new RuntimeException("glCreateProgram() failed. GLES20 error: " + GLES20.glGetError());
    GLES20.glAttachShader(this.program, i);
    GLES20.glAttachShader(this.program, j);
    GLES20.glLinkProgram(this.program);
    paramString1 = new int[1];
    paramString1[0] = 0;
    GLES20.glGetProgramiv(this.program, 35714, paramString1, 0);
    if (paramString1[0] != 1)
    {
      Logging.e("GlShader", "Could not link program: " + GLES20.glGetProgramInfoLog(this.program));
      throw new RuntimeException(GLES20.glGetProgramInfoLog(this.program));
    }
    GLES20.glDeleteShader(i);
    GLES20.glDeleteShader(j);
    GlUtil.checkNoGLES2Error("Creating GlShader");
  }

  private static int compileShader(int paramInt, String paramString)
  {
    int i = GLES20.glCreateShader(paramInt);
    if (i == 0)
      throw new RuntimeException("glCreateShader() failed. GLES20 error: " + GLES20.glGetError());
    GLES20.glShaderSource(i, paramString);
    GLES20.glCompileShader(i);
    paramString = new int[1];
    paramString[0] = 0;
    GLES20.glGetShaderiv(i, 35713, paramString, 0);
    if (paramString[0] != 1)
    {
      Logging.e("GlShader", "Could not compile shader " + paramInt + ":" + GLES20.glGetShaderInfoLog(i));
      throw new RuntimeException(GLES20.glGetShaderInfoLog(i));
    }
    GlUtil.checkNoGLES2Error("compileShader");
    return i;
  }

  public int getAttribLocation(String paramString)
  {
    if (this.program == -1)
      throw new RuntimeException("The program has been released");
    int i = GLES20.glGetAttribLocation(this.program, paramString);
    if (i < 0)
      throw new RuntimeException("Could not locate '" + paramString + "' in program");
    return i;
  }

  public int getUniformLocation(String paramString)
  {
    if (this.program == -1)
      throw new RuntimeException("The program has been released");
    int i = GLES20.glGetUniformLocation(this.program, paramString);
    if (i < 0)
      throw new RuntimeException("Could not locate uniform '" + paramString + "' in program");
    return i;
  }

  public void release()
  {
    Logging.d("GlShader", "Deleting shader.");
    if (this.program != -1)
    {
      GLES20.glDeleteProgram(this.program);
      this.program = -1;
    }
  }

  public void setVertexAttribArray(String paramString, int paramInt, FloatBuffer paramFloatBuffer)
  {
    if (this.program == -1)
      throw new RuntimeException("The program has been released");
    int i = getAttribLocation(paramString);
    GLES20.glEnableVertexAttribArray(i);
    GLES20.glVertexAttribPointer(i, paramInt, 5126, false, 0, paramFloatBuffer);
    GlUtil.checkNoGLES2Error("setVertexAttribArray");
  }

  public void useProgram()
  {
    if (this.program == -1)
      throw new RuntimeException("The program has been released");
    GLES20.glUseProgram(this.program);
    GlUtil.checkNoGLES2Error("glUseProgram");
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.GlShader
 * JD-Core Version:    0.6.0
 */