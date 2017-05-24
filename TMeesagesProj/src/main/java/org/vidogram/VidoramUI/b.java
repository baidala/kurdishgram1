package org.vidogram.VidogramUi;

import android.content.Context;
import com.google.firebase.crash.FirebaseCrash;
import e.m;
import itman.Vidofilm.a.q;
import itman.Vidofilm.a.u;
import itman.Vidofilm.c.a;
import itman.Vidofilm.e.c;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.vidogram.VidogramUi.FCM.VidogramMessagingService;

public class b
{
  private static volatile b b = null;
  private Context a;

  public b(Context paramContext)
  {
    this.a = paramContext;
  }

  public static b a(Context paramContext)
  {
    Object localObject = b;
    if (localObject == null)
    {
      monitorenter;
      try
      {
        b localb = b;
        localObject = localb;
        if (localb == null)
        {
          localObject = new b(paramContext);
          b = (b)localObject;
        }
        return localObject;
      }
      finally
      {
        monitorexit;
      }
    }
    return (b)localObject;
  }

  private void a(ArrayList<u> paramArrayList)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      u localu = (u)paramArrayList.next();
      if ((localu.b() == null) || (itman.Vidofilm.e.e.a(this.a).a(localu.b()) != null))
        continue;
      VidogramMessagingService.a(Integer.parseInt(localu.b()), false);
    }
  }

  public u a(String paramString)
  {
    return itman.Vidofilm.e.e.a(this.a).a(paramString);
  }

  public void a()
  {
    if (itman.Vidofilm.b.a(this.a).l() == null)
      return;
    q localq = new q();
    localq.a(null);
    localq.c(null);
    localq.b(itman.Vidofilm.b.a(this.a).l());
    localq.b(null);
    localq.a(itman.Vidofilm.b.a(this.a).k());
    if (localq.a() == null)
    {
      itman.Vidofilm.d.d.a(this.a).a(true);
      return;
    }
    ((itman.Vidofilm.c.b)a.a().a(itman.Vidofilm.c.b.class)).a(localq).a(new e.d()
    {
      public void onFailure(e.b<itman.Vidofilm.a.l> paramb, Throwable paramThrowable)
      {
      }

      public void onResponse(e.b<itman.Vidofilm.a.l> paramb, e.l<itman.Vidofilm.a.l> paraml)
      {
        try
        {
          if (paraml.b())
          {
            b.a(b.this, ((itman.Vidofilm.a.l)paraml.c()).a());
            itman.Vidofilm.b.a(b.a(b.this)).d(((itman.Vidofilm.a.l)paraml.c()).c());
            b.this.a(((itman.Vidofilm.a.l)paraml.c()).a(), ((itman.Vidofilm.a.l)paraml.c()).b());
            return;
          }
          if (paraml.a() == 401)
          {
            itman.Vidofilm.d.d.a(b.a(b.this)).a(true);
            return;
          }
        }
        catch (Exception paramb)
        {
        }
      }
    });
  }

  public void a(ArrayList<u> paramArrayList, ArrayList<String> paramArrayList1)
  {
    itman.Vidofilm.e.e.a(this.a).a(paramArrayList, false);
    itman.Vidofilm.e.e.a(this.a).a(paramArrayList1);
  }

  public void a(ArrayList<u> paramArrayList1, ArrayList<String> paramArrayList, ArrayList<u> paramArrayList2)
  {
    try
    {
      c.a(this.a).a(new ArrayList(), paramArrayList, new ArrayList());
      label25: q localq = new q();
      localq.a(paramArrayList1);
      localq.c(paramArrayList);
      localq.b(itman.Vidofilm.b.a(this.a).l());
      localq.b(paramArrayList2);
      localq.a(itman.Vidofilm.b.a(this.a).k());
      if (localq.a() == null)
      {
        itman.Vidofilm.d.d.a(this.a).a(true);
        int i = 0;
        try
        {
          c.a(this.a).a(paramArrayList1, paramArrayList, paramArrayList2);
          if (i != 0)
            itman.Vidofilm.b.a(this.a).d("-1");
          return;
        }
        catch (Exception paramArrayList1)
        {
          while (true)
          {
            FirebaseCrash.a(paramArrayList1);
            i = 1;
          }
        }
      }
      ((itman.Vidofilm.c.b)a.a().a(itman.Vidofilm.c.b.class)).a(localq).a(new e.d(paramArrayList1, paramArrayList, paramArrayList2)
      {
        public void onFailure(e.b<itman.Vidofilm.a.l> paramb, Throwable paramThrowable)
        {
          int i = 0;
          try
          {
            c.a(b.a(b.this)).a(this.a, this.b, this.c);
            if (i != 0)
              itman.Vidofilm.b.a(b.a(b.this)).d("-1");
            return;
          }
          catch (Exception paramb)
          {
            while (true)
            {
              FirebaseCrash.a(paramb);
              i = 1;
            }
          }
        }

        public void onResponse(e.b<itman.Vidofilm.a.l> paramb, e.l<itman.Vidofilm.a.l> paraml)
        {
          try
          {
            if (paraml.b())
            {
              if (itman.Vidofilm.b.a(b.a(b.this)).l() != null)
                b.a(b.this, ((itman.Vidofilm.a.l)paraml.c()).a());
              itman.Vidofilm.b.a(b.a(b.this)).d(((itman.Vidofilm.a.l)paraml.c()).c());
              b.this.a(((itman.Vidofilm.a.l)paraml.c()).a(), ((itman.Vidofilm.a.l)paraml.c()).b());
              c.a(b.a(b.this)).e();
              c.a(b.a(b.this)).c();
              return;
            }
            if (paraml.a() == 401)
            {
              itman.Vidofilm.d.d.a(b.a(b.this)).a(true);
              return;
            }
          }
          catch (Exception paramb)
          {
            return;
          }
          int i = 0;
          try
          {
            c.a(b.a(b.this)).a(this.a, this.b, this.c);
            if (i != 0)
            {
              itman.Vidofilm.b.a(b.a(b.this)).d("-1");
              return;
            }
          }
          catch (Exception paramb)
          {
            while (true)
            {
              FirebaseCrash.a(paramb);
              i = 1;
            }
          }
        }
      });
      return;
    }
    catch (Exception localException)
    {
      break label25;
    }
  }

  public ArrayList<u> b()
  {
    c localc = c.a(this.a);
    c.a(this.a);
    return localc.a(c.a);
  }

  public ArrayList<u> c()
  {
    c localc = c.a(this.a);
    c.a(this.a);
    return localc.a(c.b);
  }

  public ArrayList<String> d()
  {
    return c.a(this.a).a();
  }

  public HashMap<String, String> e()
  {
    return c.a(this.a).b();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.b
 * JD-Core Version:    0.6.0
 */