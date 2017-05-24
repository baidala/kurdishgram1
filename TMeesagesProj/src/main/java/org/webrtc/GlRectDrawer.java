package org.webrtc;

import android.opengl.GLES20;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

public class GlRectDrawer
  implements RendererCommon.GlDrawer
{
  private static final FloatBuffer FULL_RECTANGLE_BUF = GlUtil.createFloatBuffer(new float[] { -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F });
  private static final FloatBuffer FULL_RECTANGLE_TEX_BUF = GlUtil.createFloatBuffer(new float[] { 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F });
  private static final String OES_FRAGMENT_SHADER_STRING = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 interp_tc;\n\nuniform samplerExternalOES oes_tex;\n\nvoid main() {\n  gl_FragColor = texture2D(oes_tex, interp_tc);\n}\n";
  private static final String RGB_FRAGMENT_SHADER_STRING = "precision mediump float;\nvarying vec2 interp_tc;\n\nuniform sampler2D rgb_tex;\n\nvoid main() {\n  gl_FragColor = texture2D(rgb_tex, interp_tc);\n}\n";
  private static final String VERTEX_SHADER_STRING = "varying vec2 interp_tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\n\nuniform mat4 texMatrix;\n\nvoid main() {\n    gl_Position = in_pos;\n    interp_tc = (texMatrix * in_tc).xy;\n}\n";
  private static final String YUV_FRAGMENT_SHADER_STRING = "precision mediump float;\nvarying vec2 interp_tc;\n\nuniform sampler2D y_tex;\nuniform sampler2D u_tex;\nuniform sampler2D v_tex;\n\nvoid main() {\n  float y = texture2D(y_tex, interp_tc).r;\n  float u = texture2D(u_tex, interp_tc).r - 0.5;\n  float v = texture2D(v_tex, interp_tc).r - 0.5;\n  gl_FragColor = vec4(y + 1.403 * v,                       y - 0.344 * u - 0.714 * v,                       y + 1.77 * u, 1);\n}\n";
  private final Map<String, Shader> shaders = new IdentityHashMap();

  private void drawRectangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    GLES20.glViewport(paramInt1, paramInt2, paramInt3, paramInt4);
    GLES20.glDrawArrays(5, 0, 4);
  }

  private void prepareShader(String paramString, float[] paramArrayOfFloat)
  {
    if (this.shaders.containsKey(paramString))
    {
      paramString = (Shader)this.shaders.get(paramString);
      paramString.glShader.useProgram();
      GLES20.glUniformMatrix4fv(paramString.texMatrixLocation, 1, false, paramArrayOfFloat, 0);
      return;
    }
    Shader localShader = new Shader(paramString);
    this.shaders.put(paramString, localShader);
    localShader.glShader.useProgram();
    if (paramString == "precision mediump float;\nvarying vec2 interp_tc;\n\nuniform sampler2D y_tex;\nuniform sampler2D u_tex;\nuniform sampler2D v_tex;\n\nvoid main() {\n  float y = texture2D(y_tex, interp_tc).r;\n  float u = texture2D(u_tex, interp_tc).r - 0.5;\n  float v = texture2D(v_tex, interp_tc).r - 0.5;\n  gl_FragColor = vec4(y + 1.403 * v,                       y - 0.344 * u - 0.714 * v,                       y + 1.77 * u, 1);\n}\n")
    {
      GLES20.glUniform1i(localShader.glShader.getUniformLocation("y_tex"), 0);
      GLES20.glUniform1i(localShader.glShader.getUniformLocation("u_tex"), 1);
      GLES20.glUniform1i(localShader.glShader.getUniformLocation("v_tex"), 2);
    }
    while (true)
    {
      GlUtil.checkNoGLES2Error("Initialize fragment shader uniform values.");
      localShader.glShader.setVertexAttribArray("in_pos", 2, FULL_RECTANGLE_BUF);
      localShader.glShader.setVertexAttribArray("in_tc", 2, FULL_RECTANGLE_TEX_BUF);
      paramString = localShader;
      break;
      if (paramString == "precision mediump float;\nvarying vec2 interp_tc;\n\nuniform sampler2D rgb_tex;\n\nvoid main() {\n  gl_FragColor = texture2D(rgb_tex, interp_tc);\n}\n")
      {
        GLES20.glUniform1i(localShader.glShader.getUniformLocation("rgb_tex"), 0);
        continue;
      }
      if (paramString != "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 interp_tc;\n\nuniform samplerExternalOES oes_tex;\n\nvoid main() {\n  gl_FragColor = texture2D(oes_tex, interp_tc);\n}\n")
        break label199;
      GLES20.glUniform1i(localShader.glShader.getUniformLocation("oes_tex"), 0);
    }
    label199: throw new IllegalStateException("Unknown fragment shader: " + paramString);
  }

  public void drawOes(int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    prepareShader("#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 interp_tc;\n\nuniform samplerExternalOES oes_tex;\n\nvoid main() {\n  gl_FragColor = texture2D(oes_tex, interp_tc);\n}\n", paramArrayOfFloat);
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(36197, paramInt1);
    drawRectangle(paramInt4, paramInt5, paramInt6, paramInt7);
    GLES20.glBindTexture(36197, 0);
  }

  public void drawRgb(int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    prepareShader("precision mediump float;\nvarying vec2 interp_tc;\n\nuniform sampler2D rgb_tex;\n\nvoid main() {\n  gl_FragColor = texture2D(rgb_tex, interp_tc);\n}\n", paramArrayOfFloat);
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(3553, paramInt1);
    drawRectangle(paramInt4, paramInt5, paramInt6, paramInt7);
    GLES20.glBindTexture(3553, 0);
  }

  public void drawYuv(int[] paramArrayOfInt, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    prepareShader("precision mediump float;\nvarying vec2 interp_tc;\n\nuniform sampler2D y_tex;\nuniform sampler2D u_tex;\nuniform sampler2D v_tex;\n\nvoid main() {\n  float y = texture2D(y_tex, interp_tc).r;\n  float u = texture2D(u_tex, interp_tc).r - 0.5;\n  float v = texture2D(v_tex, interp_tc).r - 0.5;\n  gl_FragColor = vec4(y + 1.403 * v,                       y - 0.344 * u - 0.714 * v,                       y + 1.77 * u, 1);\n}\n", paramArrayOfFloat);
    paramInt1 = 0;
    while (paramInt1 < 3)
    {
      GLES20.glActiveTexture(33984 + paramInt1);
      GLES20.glBindTexture(3553, paramArrayOfInt[paramInt1]);
      paramInt1 += 1;
    }
    drawRectangle(paramInt3, paramInt4, paramInt5, paramInt6);
    paramInt1 = 0;
    while (paramInt1 < 3)
    {
      GLES20.glActiveTexture(33984 + paramInt1);
      GLES20.glBindTexture(3553, 0);
      paramInt1 += 1;
    }
  }

  public void release()
  {
    Iterator localIterator = this.shaders.values().iterator();
    while (localIterator.hasNext())
      ((Shader)localIterator.next()).glShader.release();
    this.shaders.clear();
  }

  private static class Shader
  {
    public final GlShader glShader;
    public final int texMatrixLocation;

    public Shader(String paramString)
    {
      this.glShader = new GlShader("varying vec2 interp_tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\n\nuniform mat4 texMatrix;\n\nvoid main() {\n    gl_Position = in_pos;\n    interp_tc = (texMatrix * in_tc).xy;\n}\n", paramString);
      this.texMatrixLocation = this.glShader.getUniformLocation("texMatrix");
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.GlRectDrawer
 * JD-Core Version:    0.6.0
 */