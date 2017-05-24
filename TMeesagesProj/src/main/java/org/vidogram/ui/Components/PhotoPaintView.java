package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.google.android.gms.e.a.a;
import java.security.SecureRandom;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.Bitmaps;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.InputDocument;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.vidogram.tgnet.TLRPC.TL_inputDocument;
import org.vidogram.tgnet.TLRPC.TL_maskCoords;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarPopupWindow;
import org.vidogram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.vidogram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.Paint.Brush;
import org.vidogram.ui.Components.Paint.Brush.Elliptical;
import org.vidogram.ui.Components.Paint.Brush.Neon;
import org.vidogram.ui.Components.Paint.Brush.Radial;
import org.vidogram.ui.Components.Paint.Painting;
import org.vidogram.ui.Components.Paint.PhotoFace;
import org.vidogram.ui.Components.Paint.RenderView;
import org.vidogram.ui.Components.Paint.RenderView.RenderViewDelegate;
import org.vidogram.ui.Components.Paint.Swatch;
import org.vidogram.ui.Components.Paint.UndoStore;
import org.vidogram.ui.Components.Paint.UndoStore.UndoStoreDelegate;
import org.vidogram.ui.Components.Paint.Views.ColorPicker;
import org.vidogram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate;
import org.vidogram.ui.Components.Paint.Views.EditTextOutline;
import org.vidogram.ui.Components.Paint.Views.EntitiesContainerView;
import org.vidogram.ui.Components.Paint.Views.EntitiesContainerView.EntitiesContainerViewDelegate;
import org.vidogram.ui.Components.Paint.Views.EntityView;
import org.vidogram.ui.Components.Paint.Views.EntityView.EntityViewDelegate;
import org.vidogram.ui.Components.Paint.Views.StickerView;
import org.vidogram.ui.Components.Paint.Views.TextPaintView;
import org.vidogram.ui.PhotoViewer;

@SuppressLint({"NewApi"})
public class PhotoPaintView extends FrameLayout
  implements EntityView.EntityViewDelegate
{
  private static final int gallery_menu_done = 1;
  private static final int gallery_menu_undo = 2;
  private ActionBar actionBar;
  private Bitmap bitmapToEdit;
  private Brush[] brushes = { new Brush.Radial(), new Brush.Elliptical(), new Brush.Neon() };
  private TextView cancelTextView;
  private ColorPicker colorPicker;
  private Animator colorPickerAnimator;
  int currentBrush;
  private EntityView currentEntityView;
  private FrameLayout curtainView;
  private FrameLayout dimView;
  private ActionBarMenuItem doneItem;
  private TextView doneTextView;
  private Point editedTextPosition;
  private float editedTextRotation;
  private float editedTextScale;
  private boolean editingText;
  private EntitiesContainerView entitiesView;
  private ArrayList<PhotoFace> faces;
  private String initialText;
  private int orientation;
  private ImageView paintButton;
  private Size paintingSize;
  private boolean pickingSticker;
  private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
  private Rect popupRect;
  private ActionBarPopupWindow popupWindow;
  private DispatchQueue queue = new DispatchQueue("Paint");
  private RenderView renderView;
  private boolean selectedStroke = true;
  private FrameLayout selectionContainerView;
  private StickerMasksView stickersView;
  private FrameLayout textDimView;
  private FrameLayout toolsView;
  private ActionBarMenuItem undoItem;
  private UndoStore undoStore;

  public PhotoPaintView(Context paramContext, Bitmap paramBitmap, int paramInt)
  {
    super(paramContext);
    this.bitmapToEdit = paramBitmap;
    this.orientation = paramInt;
    this.undoStore = new UndoStore();
    this.undoStore.setDelegate(new UndoStore.UndoStoreDelegate()
    {
      public void historyChanged()
      {
        PhotoPaintView.this.setMenuItemEnabled(PhotoPaintView.this.undoStore.canUndo());
      }
    });
    this.curtainView = new FrameLayout(paramContext);
    this.curtainView.setBackgroundColor(-16777216);
    this.curtainView.setVisibility(4);
    addView(this.curtainView);
    this.renderView = new RenderView(paramContext, new Painting(getPaintingSize()), paramBitmap, this.orientation);
    this.renderView.setDelegate(new RenderView.RenderViewDelegate()
    {
      public void onBeganDrawing()
      {
        PhotoPaintView.this.setColorPickerVisibilityFade(false);
        if (PhotoPaintView.this.currentEntityView != null)
          PhotoPaintView.this.selectEntity(null);
      }

      public void onFinishedDrawing(boolean paramBoolean)
      {
        if (paramBoolean)
          PhotoPaintView.this.setColorPickerVisibilityFade(true);
        PhotoPaintView.this.setMenuItemEnabled(PhotoPaintView.this.undoStore.canUndo());
      }

      public boolean shouldDraw()
      {
        if (PhotoPaintView.this.currentEntityView == null);
        for (int i = 1; ; i = 0)
        {
          if (i == 0)
            PhotoPaintView.this.selectEntity(null);
          return i;
        }
      }
    });
    this.renderView.setUndoStore(this.undoStore);
    this.renderView.setQueue(this.queue);
    this.renderView.setVisibility(4);
    this.renderView.setBrush(this.brushes[0]);
    addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
    this.entitiesView = new EntitiesContainerView(paramContext, new EntitiesContainerView.EntitiesContainerViewDelegate()
    {
      public void onEntityDeselect()
      {
        PhotoPaintView.this.selectEntity(null);
      }

      public EntityView onSelectedEntityRequest()
      {
        return PhotoPaintView.this.currentEntityView;
      }

      public boolean shouldReceiveTouches()
      {
        return PhotoPaintView.this.textDimView.getVisibility() != 0;
      }
    });
    this.entitiesView.setPivotX(0.0F);
    this.entitiesView.setPivotY(0.0F);
    addView(this.entitiesView);
    this.dimView = new FrameLayout(paramContext);
    this.dimView.setAlpha(0.0F);
    this.dimView.setBackgroundColor(1711276032);
    this.dimView.setVisibility(8);
    addView(this.dimView);
    this.textDimView = new FrameLayout(paramContext);
    this.textDimView.setAlpha(0.0F);
    this.textDimView.setBackgroundColor(1711276032);
    this.textDimView.setVisibility(8);
    this.textDimView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoPaintView.this.closeTextEnter(true);
      }
    });
    this.selectionContainerView = new FrameLayout(paramContext)
    {
      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        return false;
      }
    };
    addView(this.selectionContainerView);
    this.colorPicker = new ColorPicker(paramContext);
    addView(this.colorPicker);
    this.colorPicker.setDelegate(new ColorPicker.ColorPickerDelegate()
    {
      public void onBeganColorPicking()
      {
        if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView))
          PhotoPaintView.this.setDimVisibility(true);
      }

      public void onColorValueChanged()
      {
        PhotoPaintView.this.setCurrentSwatch(PhotoPaintView.this.colorPicker.getSwatch(), false);
      }

      public void onFinishedColorPicking()
      {
        PhotoPaintView.this.setCurrentSwatch(PhotoPaintView.this.colorPicker.getSwatch(), false);
        if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView))
          PhotoPaintView.this.setDimVisibility(false);
      }

      public void onSettingsPressed()
      {
        if (PhotoPaintView.this.currentEntityView != null)
        {
          if ((PhotoPaintView.this.currentEntityView instanceof StickerView))
            PhotoPaintView.this.mirrorSticker();
          do
            return;
          while (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView));
          PhotoPaintView.this.showTextSettings();
          return;
        }
        PhotoPaintView.this.showBrushSettings();
      }
    });
    this.toolsView = new FrameLayout(paramContext);
    this.toolsView.setBackgroundColor(-16777216);
    addView(this.toolsView, LayoutHelper.createFrame(-1, 48, 83));
    this.cancelTextView = new TextView(paramContext);
    this.cancelTextView.setTextSize(1, 14.0F);
    this.cancelTextView.setTextColor(-1);
    this.cancelTextView.setGravity(17);
    this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
    this.cancelTextView.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
    this.cancelTextView.setText(LocaleController.getString("Cancel", 2131165427).toUpperCase());
    this.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.toolsView.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
    this.doneTextView = new TextView(paramContext);
    this.doneTextView.setTextSize(1, 14.0F);
    this.doneTextView.setTextColor(-11420173);
    this.doneTextView.setGravity(17);
    this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
    this.doneTextView.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
    this.doneTextView.setText(LocaleController.getString("Done", 2131165661).toUpperCase());
    this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.toolsView.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
    paramBitmap = new ImageView(paramContext);
    paramBitmap.setScaleType(ImageView.ScaleType.CENTER);
    paramBitmap.setImageResource(2130838006);
    paramBitmap.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
    this.toolsView.addView(paramBitmap, LayoutHelper.createFrame(54, -1.0F, 17, 0.0F, 0.0F, 56.0F, 0.0F));
    paramBitmap.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoPaintView.this.openStickersView();
      }
    });
    this.paintButton = new ImageView(paramContext);
    this.paintButton.setScaleType(ImageView.ScaleType.CENTER);
    this.paintButton.setImageResource(2130838001);
    this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
    this.toolsView.addView(this.paintButton, LayoutHelper.createFrame(54, -1, 17));
    this.paintButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoPaintView.this.selectEntity(null);
      }
    });
    paramBitmap = new ImageView(paramContext);
    paramBitmap.setScaleType(ImageView.ScaleType.CENTER);
    paramBitmap.setImageResource(2130838004);
    paramBitmap.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
    this.toolsView.addView(paramBitmap, LayoutHelper.createFrame(54, -1.0F, 17, 56.0F, 0.0F, 0.0F, 0.0F));
    paramBitmap.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoPaintView.this.createText();
      }
    });
    this.actionBar = new ActionBar(paramContext);
    this.actionBar.setBackgroundColor(2130706432);
    paramContext = this.actionBar;
    if (Build.VERSION.SDK_INT >= 21);
    for (boolean bool = true; ; bool = false)
    {
      paramContext.setOccupyStatusBar(bool);
      this.actionBar.setTitleColor(-1);
      this.actionBar.setItemsBackgroundColor(1090519039, false);
      this.actionBar.setBackButtonImage(2130837732);
      this.actionBar.setTitle(LocaleController.getString("PaintDraw", 2131166186));
      addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0F));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public boolean canOpenMenu()
        {
          return false;
        }

        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            PhotoPaintView.this.cancelTextView.callOnClick();
          do
          {
            return;
            if (paramInt != 1)
              continue;
            PhotoPaintView.this.closeTextEnter(true);
            return;
          }
          while (paramInt != 2);
          PhotoPaintView.this.undoStore.undo();
        }
      });
      paramContext = this.actionBar.createMenu();
      this.undoItem = paramContext.addItem(2, 2130838010, AndroidUtilities.dp(56.0F));
      setMenuItemEnabled(false);
      this.doneItem = paramContext.addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
      this.doneItem.setVisibility(8);
      setCurrentSwatch(this.colorPicker.getSwatch(), false);
      updateSettingsButton();
      return;
    }
  }

  private int baseFontSize()
  {
    return (int)(getPaintingSize().width / 9.0F);
  }

  private Size baseStickerSize()
  {
    float f = (float)Math.floor(getPaintingSize().width * 0.5D);
    return new Size(f, f);
  }

  private FrameLayout buttonForBrush(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    FrameLayout localFrameLayout = new FrameLayout(getContext());
    localFrameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    localFrameLayout.setOnClickListener(new View.OnClickListener(paramInt1)
    {
      public void onClick(View paramView)
      {
        PhotoPaintView.this.setBrush(this.val$brush);
        if ((PhotoPaintView.this.popupWindow != null) && (PhotoPaintView.this.popupWindow.isShowing()))
          PhotoPaintView.this.popupWindow.dismiss(true);
      }
    });
    ImageView localImageView = new ImageView(getContext());
    localImageView.setImageResource(paramInt2);
    localFrameLayout.addView(localImageView, LayoutHelper.createFrame(165, 44.0F, 19, 46.0F, 0.0F, 8.0F, 0.0F));
    if (paramBoolean)
    {
      localImageView = new ImageView(getContext());
      localImageView.setImageResource(2130837735);
      localImageView.setScaleType(ImageView.ScaleType.CENTER);
      localFrameLayout.addView(localImageView, LayoutHelper.createFrame(50, -1.0F));
    }
    return localFrameLayout;
  }

  private FrameLayout buttonForText(boolean paramBoolean1, String paramString, boolean paramBoolean2)
  {
    int j = -16777216;
    22 local22 = new FrameLayout(getContext())
    {
      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        return true;
      }
    };
    local22.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    local22.setOnClickListener(new View.OnClickListener(paramBoolean1)
    {
      public void onClick(View paramView)
      {
        PhotoPaintView.this.setStroke(this.val$stroke);
        if ((PhotoPaintView.this.popupWindow != null) && (PhotoPaintView.this.popupWindow.isShowing()))
          PhotoPaintView.this.popupWindow.dismiss(true);
      }
    });
    EditTextOutline localEditTextOutline = new EditTextOutline(getContext());
    localEditTextOutline.setBackgroundColor(0);
    localEditTextOutline.setEnabled(false);
    localEditTextOutline.setStrokeWidth(AndroidUtilities.dp(3.0F));
    if (paramBoolean1)
    {
      i = -1;
      localEditTextOutline.setTextColor(i);
      if (!paramBoolean1)
        break label232;
    }
    label232: for (int i = j; ; i = 0)
    {
      localEditTextOutline.setStrokeColor(i);
      localEditTextOutline.setPadding(AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(2.0F), 0);
      localEditTextOutline.setTextSize(1, 18.0F);
      localEditTextOutline.setTypeface(null, 1);
      localEditTextOutline.setTag(Boolean.valueOf(paramBoolean1));
      localEditTextOutline.setText(paramString);
      local22.addView(localEditTextOutline, LayoutHelper.createFrame(-2, -2.0F, 19, 46.0F, 0.0F, 16.0F, 0.0F));
      if (paramBoolean2)
      {
        paramString = new ImageView(getContext());
        paramString.setImageResource(2130837735);
        paramString.setScaleType(ImageView.ScaleType.CENTER);
        local22.addView(paramString, LayoutHelper.createFrame(50, -1.0F));
      }
      return local22;
      i = -16777216;
      break;
    }
  }

  private StickerPosition calculateStickerPosition(TLRPC.Document paramDocument)
  {
    int i = 0;
    if (i < paramDocument.attributes.size())
    {
      localObject1 = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
      if (!(localObject1 instanceof TLRPC.TL_documentAttributeSticker));
    }
    for (Object localObject1 = ((TLRPC.DocumentAttribute)localObject1).mask_coords; ; localObject1 = null)
    {
      Object localObject2 = new StickerPosition(centerPositionForEntity(), 0.75F, 0.0F);
      if ((localObject1 == null) || (this.faces == null) || (this.faces.size() == 0))
      {
        return localObject2;
        i += 1;
        break;
      }
      i = ((TLRPC.TL_maskCoords)localObject1).n;
      paramDocument = getRandomFaceWithVacantAnchor(i, paramDocument.id, (TLRPC.TL_maskCoords)localObject1);
      if (paramDocument == null)
        return localObject2;
      localObject2 = paramDocument.getPointForAnchor(i);
      float f6 = paramDocument.getWidthForAnchor(i);
      float f1 = paramDocument.getAngle();
      float f2 = (float)(f6 / baseStickerSize().width * ((TLRPC.TL_maskCoords)localObject1).zoom);
      float f7 = (float)Math.toRadians(f1);
      float f3 = (float)(Math.sin(1.570796326794897D - f7) * f6 * ((TLRPC.TL_maskCoords)localObject1).x);
      float f4 = (float)(Math.cos(1.570796326794897D - f7) * f6 * ((TLRPC.TL_maskCoords)localObject1).x);
      float f5 = (float)(Math.cos(f7 + 1.570796326794897D) * f6 * ((TLRPC.TL_maskCoords)localObject1).y);
      f6 = (float)(Math.sin(f7 + 1.570796326794897D) * f6 * ((TLRPC.TL_maskCoords)localObject1).y);
      return new StickerPosition(new Point(((Point)localObject2).x + f3 + f5, ((Point)localObject2).y + f4 + f6), f2, f1);
    }
  }

  private Point centerPositionForEntity()
  {
    Size localSize = getPaintingSize();
    return new Point(localSize.width / 2.0F, localSize.height / 2.0F);
  }

  private void closeStickersView()
  {
    if ((this.stickersView == null) || (this.stickersView.getVisibility() != 0))
      return;
    this.pickingSticker = false;
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[] { 1.0F, 0.0F });
    localObjectAnimator.setDuration(200L);
    localObjectAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        PhotoPaintView.this.stickersView.setVisibility(8);
      }
    });
    localObjectAnimator.start();
    this.undoItem.setVisibility(0);
    this.actionBar.setTitle(LocaleController.getString("PaintDraw", 2131166186));
  }

  private void createSticker(TLRPC.Document paramDocument)
  {
    StickerPosition localStickerPosition = calculateStickerPosition(paramDocument);
    paramDocument = new StickerView(getContext(), localStickerPosition.position, localStickerPosition.angle, localStickerPosition.scale, baseStickerSize(), paramDocument);
    paramDocument.setDelegate(this);
    this.entitiesView.addView(paramDocument);
    registerRemovalUndo(paramDocument);
    selectEntity(paramDocument);
  }

  private void createText()
  {
    Swatch localSwatch = this.colorPicker.getSwatch();
    Object localObject = new Swatch(-1, 1.0F, localSwatch.brushWeight);
    localSwatch = new Swatch(-16777216, 0.85F, localSwatch.brushWeight);
    if (this.selectedStroke)
      localObject = localSwatch;
    while (true)
    {
      setCurrentSwatch((Swatch)localObject, true);
      localObject = new TextPaintView(getContext(), startPositionRelativeToEntity(null), baseFontSize(), "", this.colorPicker.getSwatch(), this.selectedStroke);
      ((TextPaintView)localObject).setDelegate(this);
      ((TextPaintView)localObject).setMaxWidth((int)(getPaintingSize().width - 20.0F));
      this.entitiesView.addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F));
      registerRemovalUndo((EntityView)localObject);
      selectEntity((EntityView)localObject);
      editSelectedTextEntity();
      return;
    }
  }

  private void detectFaces()
  {
    this.queue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        while (true)
        {
          int i;
          try
          {
            com.google.android.gms.e.a.b localb = new com.google.android.gms.e.a.b.a(PhotoPaintView.this.getContext()).b(1).a(1).a(false).a();
            localObject1 = localb;
            localObject3 = localb;
            if (localb.b())
              continue;
            localObject1 = localb;
            localObject3 = localb;
            FileLog.e("face detection is not operational");
            return;
            localObject1 = localb;
            localObject3 = localb;
            Object localObject4 = new com.google.android.gms.e.b.a().a(PhotoPaintView.this.bitmapToEdit).a(PhotoPaintView.this.getFrameRotation()).a();
            localObject1 = localb;
            localObject3 = localb;
            ArrayList localArrayList;
            try
            {
              localObject4 = localb.a((com.google.android.gms.e.b)localObject4);
              localObject1 = localb;
              localObject3 = localb;
              localArrayList = new ArrayList();
              localObject1 = localb;
              localObject3 = localb;
              Size localSize = PhotoPaintView.this.getPaintingSize();
              i = 0;
              localObject1 = localb;
              localObject3 = localb;
              if (i >= ((SparseArray)localObject4).size())
                continue;
              localObject1 = localb;
              localObject3 = localb;
              PhotoFace localPhotoFace = new PhotoFace((a)((SparseArray)localObject4).get(((SparseArray)localObject4).keyAt(i)), PhotoPaintView.this.bitmapToEdit, localSize, PhotoPaintView.this.isSidewardOrientation());
              localObject1 = localb;
              localObject3 = localb;
              if (!localPhotoFace.isSufficient())
                break label327;
              localObject1 = localb;
              localObject3 = localb;
              localArrayList.add(localPhotoFace);
            }
            catch (Throwable localThrowable)
            {
              localObject1 = localb;
              localObject3 = localb;
              FileLog.e(localThrowable);
            }
            return;
            localObject1 = localb;
            localObject3 = localb;
            PhotoPaintView.access$3902(PhotoPaintView.this, localArrayList);
            return;
          }
          catch (Exception localException)
          {
            localObject3 = localObject1;
            FileLog.e(localException);
            return;
          }
          finally
          {
            if (localObject3 == null)
              continue;
            localObject3.a();
          }
          label327: i += 1;
        }
      }
    });
  }

  private void duplicateSelectedEntity()
  {
    if (this.currentEntityView == null)
      return;
    Object localObject = null;
    Point localPoint = startPositionRelativeToEntity(this.currentEntityView);
    if ((this.currentEntityView instanceof StickerView))
    {
      localObject = new StickerView(getContext(), (StickerView)this.currentEntityView, localPoint);
      ((StickerView)localObject).setDelegate(this);
      this.entitiesView.addView((View)localObject);
    }
    while (true)
    {
      registerRemovalUndo((EntityView)localObject);
      selectEntity((EntityView)localObject);
      updateSettingsButton();
      return;
      if (!(this.currentEntityView instanceof TextPaintView))
        continue;
      localObject = new TextPaintView(getContext(), (TextPaintView)this.currentEntityView, localPoint);
      ((TextPaintView)localObject).setDelegate(this);
      ((TextPaintView)localObject).setMaxWidth((int)(getPaintingSize().width - 20.0F));
      this.entitiesView.addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F));
    }
  }

  private void editSelectedTextEntity()
  {
    if ((!(this.currentEntityView instanceof TextPaintView)) || (this.editingText))
      return;
    this.curtainView.setVisibility(0);
    TextPaintView localTextPaintView = (TextPaintView)this.currentEntityView;
    this.initialText = localTextPaintView.getText();
    this.editingText = true;
    this.editedTextPosition = localTextPaintView.getPosition();
    this.editedTextRotation = localTextPaintView.getRotation();
    this.editedTextScale = localTextPaintView.getScale();
    localTextPaintView.setPosition(centerPositionForEntity());
    localTextPaintView.setRotation(0.0F);
    localTextPaintView.setScale(1.0F);
    this.undoItem.setVisibility(8);
    this.doneItem.setVisibility(0);
    this.actionBar.setTitle(LocaleController.getString("PaintText", 2131166192));
    this.toolsView.setVisibility(8);
    setColorPickerVisibilitySlide(false);
    setTextDimVisibility(true, localTextPaintView);
    localTextPaintView.beginEditing();
    ((InputMethodManager)ApplicationLoader.applicationContext.getSystemService("input_method")).toggleSoftInputFromWindow(localTextPaintView.getFocusedView().getWindowToken(), 2, 0);
  }

  private int getFrameRotation()
  {
    switch (this.orientation)
    {
    default:
      return 0;
    case 90:
      return 1;
    case 180:
      return 2;
    case 270:
    }
    return 3;
  }

  private Size getPaintingSize()
  {
    if (this.paintingSize != null)
      return this.paintingSize;
    float f1;
    float f2;
    if (isSidewardOrientation())
    {
      f1 = this.bitmapToEdit.getHeight();
      if (!isSidewardOrientation())
        break label132;
      f2 = this.bitmapToEdit.getWidth();
    }
    while (true)
    {
      Size localSize = new Size(f1, f2);
      localSize.width = 1280.0F;
      localSize.height = (float)Math.floor(localSize.width * f2 / f1);
      if (localSize.height > 1280.0F)
      {
        localSize.height = 1280.0F;
        localSize.width = (float)Math.floor(f1 * localSize.height / f2);
      }
      this.paintingSize = localSize;
      return localSize;
      f1 = this.bitmapToEdit.getWidth();
      break;
      label132: f2 = this.bitmapToEdit.getHeight();
    }
  }

  private PhotoFace getRandomFaceWithVacantAnchor(int paramInt, long paramLong, TLRPC.TL_maskCoords paramTL_maskCoords)
  {
    Object localObject;
    if ((paramInt < 0) || (paramInt > 3) || (this.faces.isEmpty()))
    {
      localObject = null;
      return localObject;
    }
    int k = this.faces.size();
    int j = Utilities.random.nextInt(k);
    int i = k;
    while (true)
    {
      if (i <= 0)
        break label102;
      PhotoFace localPhotoFace = (PhotoFace)this.faces.get(j);
      localObject = localPhotoFace;
      if (!isFaceAnchorOccupied(localPhotoFace, paramInt, paramLong, paramTL_maskCoords))
        break;
      j = (j + 1) % k;
      i -= 1;
    }
    label102: return null;
  }

  private boolean hasChanges()
  {
    return (this.undoStore.canUndo()) || (this.entitiesView.entitiesCount() > 0);
  }

  private boolean isFaceAnchorOccupied(PhotoFace paramPhotoFace, int paramInt, long paramLong, TLRPC.TL_maskCoords paramTL_maskCoords)
  {
    int k = 0;
    paramTL_maskCoords = paramPhotoFace.getPointForAnchor(paramInt);
    int j;
    if (paramTL_maskCoords == null)
      j = 1;
    float f1;
    int i;
    do
    {
      return j;
      f1 = paramPhotoFace.getWidthForAnchor(0);
      i = 0;
      j = k;
    }
    while (i >= this.entitiesView.getChildCount());
    paramPhotoFace = this.entitiesView.getChildAt(i);
    if (!(paramPhotoFace instanceof StickerView));
    float f2;
    do
    {
      do
      {
        i += 1;
        break;
        paramPhotoFace = (StickerView)paramPhotoFace;
      }
      while (paramPhotoFace.getAnchor() != paramInt);
      Point localPoint = paramPhotoFace.getPosition();
      f2 = (float)Math.hypot(localPoint.x - paramTL_maskCoords.x, localPoint.y - paramTL_maskCoords.y);
    }
    while (((paramLong != paramPhotoFace.getSticker().id) && (this.faces.size() <= 1)) || (f2 >= f1 * 1.1F));
    return true;
  }

  private boolean isSidewardOrientation()
  {
    return (this.orientation % 360 == 90) || (this.orientation % 360 == 270);
  }

  private void mirrorSticker()
  {
    if ((this.currentEntityView instanceof StickerView))
      ((StickerView)this.currentEntityView).mirror();
  }

  private void openStickersView()
  {
    if ((this.stickersView != null) && (this.stickersView.getVisibility() == 0))
      return;
    this.pickingSticker = true;
    if (this.stickersView == null)
    {
      this.stickersView = new StickerMasksView(getContext());
      this.stickersView.setListener(new StickerMasksView.Listener()
      {
        public void onStickerSelected(TLRPC.Document paramDocument)
        {
          PhotoPaintView.this.closeStickersView();
          PhotoPaintView.this.createSticker(paramDocument);
        }

        public void onTypeChanged()
        {
          PhotoPaintView.this.updateStickersTitle();
        }
      });
      addView(this.stickersView, LayoutHelper.createFrame(-1, -1, 51));
    }
    this.stickersView.setVisibility(0);
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[] { 0.0F, 1.0F });
    localObjectAnimator.setDuration(200L);
    localObjectAnimator.start();
    this.undoItem.setVisibility(8);
    updateStickersTitle();
  }

  private void registerRemovalUndo(EntityView paramEntityView)
  {
    this.undoStore.registerUndo(paramEntityView.getUUID(), new Runnable(paramEntityView)
    {
      public void run()
      {
        PhotoPaintView.this.removeEntity(this.val$entityView);
      }
    });
  }

  private void removeEntity(EntityView paramEntityView)
  {
    if (paramEntityView == this.currentEntityView)
    {
      this.currentEntityView.deselect();
      this.currentEntityView = null;
      updateSettingsButton();
    }
    this.entitiesView.removeView(paramEntityView);
    this.undoStore.unregisterUndo(paramEntityView.getUUID());
  }

  private boolean selectEntity(EntityView paramEntityView)
  {
    int j = 1;
    int i = 0;
    if (this.currentEntityView != null)
    {
      if (this.currentEntityView == paramEntityView)
      {
        if (!this.editingText)
          showMenuForEntity(this.currentEntityView);
        return true;
      }
      this.currentEntityView.deselect();
      i = 1;
    }
    this.currentEntityView = paramEntityView;
    if (this.currentEntityView != null)
    {
      this.currentEntityView.select(this.selectionContainerView);
      this.entitiesView.bringViewToFront(this.currentEntityView);
      i = j;
      if ((this.currentEntityView instanceof TextPaintView))
      {
        setCurrentSwatch(((TextPaintView)this.currentEntityView).getSwatch(), true);
        i = j;
      }
    }
    while (true)
    {
      updateSettingsButton();
      return i;
    }
  }

  private void setBrush(int paramInt)
  {
    RenderView localRenderView = this.renderView;
    Brush[] arrayOfBrush = this.brushes;
    this.currentBrush = paramInt;
    localRenderView.setBrush(arrayOfBrush[paramInt]);
  }

  private void setColorPickerVisibilityFade(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.colorPickerAnimator = ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[] { this.colorPicker.getAlpha(), 1.0F });
      this.colorPickerAnimator.setStartDelay(200L);
      this.colorPickerAnimator.setDuration(200L);
      this.colorPickerAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if (PhotoPaintView.this.colorPickerAnimator != null)
            PhotoPaintView.access$1502(PhotoPaintView.this, null);
        }
      });
      this.colorPickerAnimator.start();
      return;
    }
    if (this.colorPickerAnimator != null)
    {
      this.colorPickerAnimator.cancel();
      this.colorPickerAnimator = null;
    }
    this.colorPicker.setAlpha(0.0F);
  }

  private void setColorPickerVisibilitySlide(boolean paramBoolean)
  {
    float f2 = 0.0F;
    Object localObject = this.colorPicker;
    float f1;
    if (paramBoolean)
    {
      f1 = AndroidUtilities.dp(60.0F);
      if (!paramBoolean)
        break label65;
    }
    while (true)
    {
      localObject = ObjectAnimator.ofFloat(localObject, "translationX", new float[] { f1, f2 });
      ((Animator)localObject).setDuration(200L);
      ((Animator)localObject).start();
      return;
      f1 = 0.0F;
      break;
      label65: f2 = AndroidUtilities.dp(60.0F);
    }
  }

  private void setCurrentSwatch(Swatch paramSwatch, boolean paramBoolean)
  {
    this.renderView.setColor(paramSwatch.color);
    this.renderView.setBrushSize(paramSwatch.brushWeight);
    if (paramBoolean)
      this.colorPicker.setSwatch(paramSwatch);
    if ((this.currentEntityView instanceof TextPaintView))
      ((TextPaintView)this.currentEntityView).setSwatch(paramSwatch);
  }

  private void setDimVisibility(boolean paramBoolean)
  {
    if (paramBoolean)
      this.dimView.setVisibility(0);
    for (ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[] { 0.0F, 1.0F }); ; localObjectAnimator = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[] { 1.0F, 0.0F }))
    {
      localObjectAnimator.addListener(new AnimatorListenerAdapter(paramBoolean)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if (!this.val$visible)
            PhotoPaintView.this.dimView.setVisibility(8);
        }
      });
      localObjectAnimator.setDuration(200L);
      localObjectAnimator.start();
      return;
    }
  }

  private void setMenuItemEnabled(boolean paramBoolean)
  {
    ActionBarMenuItem localActionBarMenuItem = this.undoItem;
    float f;
    if (paramBoolean)
      f = 1.0F;
    while (true)
    {
      localActionBarMenuItem.setAlpha(f);
      this.undoItem.setEnabled(paramBoolean);
      return;
      f = 0.3F;
    }
  }

  private void setStroke(boolean paramBoolean)
  {
    this.selectedStroke = paramBoolean;
    Swatch localSwatch;
    if ((this.currentEntityView instanceof TextPaintView))
    {
      localSwatch = this.colorPicker.getSwatch();
      if ((!paramBoolean) || (localSwatch.color != -1))
        break label68;
      setCurrentSwatch(new Swatch(-16777216, 0.85F, localSwatch.brushWeight), true);
    }
    while (true)
    {
      ((TextPaintView)this.currentEntityView).setStroke(paramBoolean);
      return;
      label68: if ((paramBoolean) || (localSwatch.color != -16777216))
        continue;
      setCurrentSwatch(new Swatch(-1, 1.0F, localSwatch.brushWeight), true);
    }
  }

  private void setTextDimVisibility(boolean paramBoolean, EntityView paramEntityView)
  {
    if ((paramBoolean) && (paramEntityView != null))
    {
      ViewGroup localViewGroup = (ViewGroup)paramEntityView.getParent();
      if (this.textDimView.getParent() != null)
        ((EntitiesContainerView)this.textDimView.getParent()).removeView(this.textDimView);
      localViewGroup.addView(this.textDimView, localViewGroup.indexOfChild(paramEntityView));
    }
    boolean bool;
    if (!paramBoolean)
    {
      bool = true;
      paramEntityView.setSelectionVisibility(bool);
      if (!paramBoolean)
        break label135;
      this.textDimView.setVisibility(0);
    }
    label135: for (paramEntityView = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[] { 0.0F, 1.0F }); ; paramEntityView = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[] { 1.0F, 0.0F }))
    {
      paramEntityView.addListener(new AnimatorListenerAdapter(paramBoolean)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if (!this.val$visible)
          {
            PhotoPaintView.this.textDimView.setVisibility(8);
            if (PhotoPaintView.this.textDimView.getParent() != null)
              ((EntitiesContainerView)PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
          }
        }
      });
      paramEntityView.setDuration(200L);
      paramEntityView.start();
      return;
      bool = false;
      break;
    }
  }

  private void showBrushSettings()
  {
    showPopup(new Runnable()
    {
      public void run()
      {
        boolean bool2 = true;
        Object localObject = PhotoPaintView.this;
        LinearLayout.LayoutParams localLayoutParams;
        if (PhotoPaintView.this.currentBrush == 0)
        {
          bool1 = true;
          localObject = ((PhotoPaintView)localObject).buttonForBrush(0, 2130837992, bool1);
          PhotoPaintView.this.popupLayout.addView((View)localObject);
          localLayoutParams = (LinearLayout.LayoutParams)((View)localObject).getLayoutParams();
          localLayoutParams.width = -1;
          localLayoutParams.height = AndroidUtilities.dp(52.0F);
          ((View)localObject).setLayoutParams(localLayoutParams);
          localObject = PhotoPaintView.this;
          if (PhotoPaintView.this.currentBrush != 1)
            break label214;
          bool1 = true;
          label88: localObject = ((PhotoPaintView)localObject).buttonForBrush(1, 2130837988, bool1);
          PhotoPaintView.this.popupLayout.addView((View)localObject);
          localLayoutParams = (LinearLayout.LayoutParams)((View)localObject).getLayoutParams();
          localLayoutParams.width = -1;
          localLayoutParams.height = AndroidUtilities.dp(52.0F);
          ((View)localObject).setLayoutParams(localLayoutParams);
          localObject = PhotoPaintView.this;
          if (PhotoPaintView.this.currentBrush != 2)
            break label219;
        }
        label214: label219: for (boolean bool1 = bool2; ; bool1 = false)
        {
          localObject = ((PhotoPaintView)localObject).buttonForBrush(2, 2130837990, bool1);
          PhotoPaintView.this.popupLayout.addView((View)localObject);
          localLayoutParams = (LinearLayout.LayoutParams)((View)localObject).getLayoutParams();
          localLayoutParams.width = -1;
          localLayoutParams.height = AndroidUtilities.dp(52.0F);
          ((View)localObject).setLayoutParams(localLayoutParams);
          return;
          bool1 = false;
          break;
          bool1 = false;
          break label88;
        }
      }
    }
    , this, 85, 0, AndroidUtilities.dp(48.0F));
  }

  private void showMenuForEntity(EntityView paramEntityView)
  {
    int i = (int)((paramEntityView.getPosition().x - this.entitiesView.getWidth() / 2) * this.entitiesView.getScaleX());
    int j = (int)((paramEntityView.getPosition().y - paramEntityView.getHeight() * paramEntityView.getScale() / 2.0F - this.entitiesView.getHeight() / 2) * this.entitiesView.getScaleY());
    int k = AndroidUtilities.dp(32.0F);
    showPopup(new Runnable(paramEntityView)
    {
      public void run()
      {
        LinearLayout localLinearLayout = new LinearLayout(PhotoPaintView.this.getContext());
        localLinearLayout.setOrientation(0);
        Object localObject = new TextView(PhotoPaintView.this.getContext());
        ((TextView)localObject).setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        ((TextView)localObject).setBackgroundDrawable(Theme.getSelectorDrawable(false));
        ((TextView)localObject).setGravity(16);
        ((TextView)localObject).setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(14.0F), 0);
        ((TextView)localObject).setTextSize(1, 18.0F);
        ((TextView)localObject).setTag(Integer.valueOf(0));
        ((TextView)localObject).setText(LocaleController.getString("PaintDelete", 2131166185));
        ((TextView)localObject).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            PhotoPaintView.this.removeEntity(PhotoPaintView.19.this.val$entityView);
            if ((PhotoPaintView.this.popupWindow != null) && (PhotoPaintView.this.popupWindow.isShowing()))
              PhotoPaintView.this.popupWindow.dismiss(true);
          }
        });
        localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, 48));
        if ((this.val$entityView instanceof TextPaintView))
        {
          localObject = new TextView(PhotoPaintView.this.getContext());
          ((TextView)localObject).setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
          ((TextView)localObject).setBackgroundDrawable(Theme.getSelectorDrawable(false));
          ((TextView)localObject).setGravity(16);
          ((TextView)localObject).setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
          ((TextView)localObject).setTextSize(1, 18.0F);
          ((TextView)localObject).setTag(Integer.valueOf(1));
          ((TextView)localObject).setText(LocaleController.getString("PaintEdit", 2131166188));
          ((TextView)localObject).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              PhotoPaintView.this.editSelectedTextEntity();
              if ((PhotoPaintView.this.popupWindow != null) && (PhotoPaintView.this.popupWindow.isShowing()))
                PhotoPaintView.this.popupWindow.dismiss(true);
            }
          });
          localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, 48));
        }
        localObject = new TextView(PhotoPaintView.this.getContext());
        ((TextView)localObject).setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        ((TextView)localObject).setBackgroundDrawable(Theme.getSelectorDrawable(false));
        ((TextView)localObject).setGravity(16);
        ((TextView)localObject).setPadding(AndroidUtilities.dp(14.0F), 0, AndroidUtilities.dp(16.0F), 0);
        ((TextView)localObject).setTextSize(1, 18.0F);
        ((TextView)localObject).setTag(Integer.valueOf(2));
        ((TextView)localObject).setText(LocaleController.getString("PaintDuplicate", 2131166187));
        ((TextView)localObject).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            PhotoPaintView.this.duplicateSelectedEntity();
            if ((PhotoPaintView.this.popupWindow != null) && (PhotoPaintView.this.popupWindow.isShowing()))
              PhotoPaintView.this.popupWindow.dismiss(true);
          }
        });
        localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, 48));
        PhotoPaintView.this.popupLayout.addView(localLinearLayout);
        localObject = (LinearLayout.LayoutParams)localLinearLayout.getLayoutParams();
        ((LinearLayout.LayoutParams)localObject).width = -2;
        ((LinearLayout.LayoutParams)localObject).height = -2;
        localLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
    }
    , paramEntityView, 17, i, j - k);
  }

  private void showPopup(Runnable paramRunnable, View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.popupWindow != null) && (this.popupWindow.isShowing()))
    {
      this.popupWindow.dismiss();
      return;
    }
    if (this.popupLayout == null)
    {
      this.popupRect = new Rect();
      this.popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
      this.popupLayout.setAnimationEnabled(false);
      this.popupLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          if ((paramMotionEvent.getActionMasked() == 0) && (PhotoPaintView.this.popupWindow != null) && (PhotoPaintView.this.popupWindow.isShowing()))
          {
            paramView.getHitRect(PhotoPaintView.this.popupRect);
            if (!PhotoPaintView.this.popupRect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
              PhotoPaintView.this.popupWindow.dismiss();
          }
          return false;
        }
      });
      this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener()
      {
        public void onDispatchKeyEvent(KeyEvent paramKeyEvent)
        {
          if ((paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getRepeatCount() == 0) && (PhotoPaintView.this.popupWindow != null) && (PhotoPaintView.this.popupWindow.isShowing()))
            PhotoPaintView.this.popupWindow.dismiss();
        }
      });
      this.popupLayout.setShowedFromBotton(true);
    }
    this.popupLayout.removeInnerViews();
    paramRunnable.run();
    if (this.popupWindow == null)
    {
      this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
      this.popupWindow.setAnimationEnabled(false);
      this.popupWindow.setAnimationStyle(2131362023);
      this.popupWindow.setOutsideTouchable(true);
      this.popupWindow.setClippingEnabled(true);
      this.popupWindow.setInputMethodMode(2);
      this.popupWindow.setSoftInputMode(0);
      this.popupWindow.getContentView().setFocusableInTouchMode(true);
      this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
      {
        public void onDismiss()
        {
          PhotoPaintView.this.popupLayout.removeInnerViews();
        }
      });
    }
    this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0F), -2147483648), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0F), -2147483648));
    this.popupWindow.setFocusable(true);
    this.popupWindow.showAtLocation(paramView, paramInt1, paramInt2, paramInt3);
    this.popupWindow.startAnimation();
  }

  private void showTextSettings()
  {
    showPopup(new Runnable()
    {
      public void run()
      {
        Object localObject1 = PhotoPaintView.this.buttonForText(true, LocaleController.getString("PaintOutlined", 2131166189), PhotoPaintView.this.selectedStroke);
        PhotoPaintView.this.popupLayout.addView((View)localObject1);
        Object localObject2 = (LinearLayout.LayoutParams)((View)localObject1).getLayoutParams();
        ((LinearLayout.LayoutParams)localObject2).width = -1;
        ((LinearLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(48.0F);
        ((View)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject1 = PhotoPaintView.this;
        localObject2 = LocaleController.getString("PaintRegular", 2131166190);
        if (!PhotoPaintView.this.selectedStroke);
        for (boolean bool = true; ; bool = false)
        {
          localObject1 = ((PhotoPaintView)localObject1).buttonForText(false, (String)localObject2, bool);
          PhotoPaintView.this.popupLayout.addView((View)localObject1);
          localObject2 = (LinearLayout.LayoutParams)((View)localObject1).getLayoutParams();
          ((LinearLayout.LayoutParams)localObject2).width = -1;
          ((LinearLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(48.0F);
          ((View)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
          return;
        }
      }
    }
    , this, 85, 0, AndroidUtilities.dp(48.0F));
  }

  private Point startPositionRelativeToEntity(EntityView paramEntityView)
  {
    Object localObject;
    if (paramEntityView != null)
    {
      paramEntityView = paramEntityView.getPosition();
      localObject = new Point(paramEntityView.x + 200.0F, paramEntityView.y + 200.0F);
      return localObject;
    }
    for (paramEntityView = centerPositionForEntity(); ; paramEntityView = new Point(paramEntityView.x + 200.0F, paramEntityView.y + 200.0F))
    {
      int i = 0;
      int j = 0;
      if (i < this.entitiesView.getChildCount())
      {
        localObject = this.entitiesView.getChildAt(i);
        if (!(localObject instanceof EntityView));
        while (true)
        {
          i += 1;
          break;
          localObject = ((EntityView)localObject).getPosition();
          if ((float)Math.sqrt(Math.pow(((Point)localObject).x - paramEntityView.x, 2.0D) + Math.pow(((Point)localObject).y - paramEntityView.y, 2.0D)) >= 100.0F)
            continue;
          j = 1;
        }
      }
      localObject = paramEntityView;
      if (j == 0)
        break;
    }
  }

  private void updateSettingsButton()
  {
    int i = 2130838003;
    if (this.currentEntityView != null)
      if ((this.currentEntityView instanceof StickerView))
      {
        i = 2130837999;
        this.paintButton.setImageResource(2130838001);
      }
    while (true)
    {
      this.colorPicker.setSettingsButtonImage(i);
      return;
      if (!(this.currentEntityView instanceof TextPaintView))
        break;
      i = 2130838000;
      break;
      this.paintButton.setImageResource(2130838002);
    }
  }

  private void updateStickersTitle()
  {
    if ((this.stickersView == null) || (this.stickersView.getVisibility() != 0))
      return;
    switch (this.stickersView.getCurrentType())
    {
    default:
      return;
    case 0:
      this.actionBar.setTitle(LocaleController.getString("PaintStickers", 2131166191));
      return;
    case 1:
    }
    this.actionBar.setTitle(LocaleController.getString("Masks", 2131165936));
  }

  public boolean allowInteraction(EntityView paramEntityView)
  {
    return !this.editingText;
  }

  public void closeTextEnter(boolean paramBoolean)
  {
    if ((!this.editingText) || (!(this.currentEntityView instanceof TextPaintView)))
      return;
    TextPaintView localTextPaintView = (TextPaintView)this.currentEntityView;
    this.undoItem.setVisibility(0);
    this.doneItem.setVisibility(8);
    this.actionBar.setTitle(LocaleController.getString("PaintDraw", 2131166186));
    this.toolsView.setVisibility(0);
    setColorPickerVisibilitySlide(true);
    AndroidUtilities.hideKeyboard(localTextPaintView.getFocusedView());
    localTextPaintView.getFocusedView().clearFocus();
    localTextPaintView.endEditing();
    if (!paramBoolean)
      localTextPaintView.setText(this.initialText);
    if (localTextPaintView.getText().trim().length() == 0)
    {
      this.entitiesView.removeView(localTextPaintView);
      selectEntity(null);
    }
    while (true)
    {
      setTextDimVisibility(false, localTextPaintView);
      this.editingText = false;
      this.initialText = null;
      this.curtainView.setVisibility(8);
      return;
      localTextPaintView.setPosition(this.editedTextPosition);
      localTextPaintView.setRotation(this.editedTextRotation);
      localTextPaintView.setScale(this.editedTextScale);
      this.editedTextPosition = null;
      this.editedTextRotation = 0.0F;
      this.editedTextScale = 0.0F;
    }
  }

  public ActionBar getActionBar()
  {
    return this.actionBar;
  }

  public Bitmap getBitmap()
  {
    Bitmap localBitmap = this.renderView.getResultBitmap();
    if ((localBitmap != null) && (this.entitiesView.entitiesCount() > 0))
    {
      Canvas localCanvas1 = new Canvas(localBitmap);
      int i = 0;
      if (i < this.entitiesView.getChildCount())
      {
        View localView = this.entitiesView.getChildAt(i);
        localCanvas1.save();
        Object localObject;
        Canvas localCanvas2;
        if ((localView instanceof EntityView))
        {
          localObject = (EntityView)localView;
          localCanvas1.translate(((EntityView)localObject).getPosition().x, ((EntityView)localObject).getPosition().y);
          localCanvas1.scale(localView.getScaleX(), localView.getScaleY());
          localCanvas1.rotate(localView.getRotation());
          localCanvas1.translate(-((EntityView)localObject).getWidth() / 2, -((EntityView)localObject).getHeight() / 2);
          if (!(localView instanceof TextPaintView))
            break label242;
          localObject = Bitmaps.createBitmap(localView.getWidth(), localView.getHeight(), Bitmap.Config.ARGB_8888);
          localCanvas2 = new Canvas((Bitmap)localObject);
          localView.draw(localCanvas2);
          localCanvas1.drawBitmap((Bitmap)localObject, null, new Rect(0, 0, ((Bitmap)localObject).getWidth(), ((Bitmap)localObject).getHeight()), null);
        }
        while (true)
        {
          try
          {
            localCanvas2.setBitmap(null);
            ((Bitmap)localObject).recycle();
            localCanvas1.restore();
            i += 1;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            continue;
          }
          label242: localException.draw(localCanvas1);
        }
      }
    }
    return (Bitmap)localBitmap;
  }

  public TextView getCancelTextView()
  {
    return this.cancelTextView;
  }

  public ColorPicker getColorPicker()
  {
    return this.colorPicker;
  }

  public TextView getDoneTextView()
  {
    return this.doneTextView;
  }

  public ArrayList<TLRPC.InputDocument> getMasks()
  {
    ArrayList localArrayList = null;
    int j = this.entitiesView.getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject = this.entitiesView.getChildAt(i);
      if (!(localObject instanceof StickerView))
        break label105;
      localObject = ((StickerView)localObject).getSticker();
      if (localArrayList != null)
        break label102;
      localArrayList = new ArrayList();
      label57: TLRPC.TL_inputDocument localTL_inputDocument = new TLRPC.TL_inputDocument();
      localTL_inputDocument.id = ((TLRPC.Document)localObject).id;
      localTL_inputDocument.access_hash = ((TLRPC.Document)localObject).access_hash;
      localArrayList.add(localTL_inputDocument);
    }
    label102: label105: 
    while (true)
    {
      i += 1;
      break;
      return localArrayList;
      break label57;
    }
  }

  public FrameLayout getToolsView()
  {
    return this.toolsView;
  }

  public void init()
  {
    this.renderView.setVisibility(0);
    detectFaces();
  }

  public void maybeShowDismissalAlert(PhotoViewer paramPhotoViewer, Activity paramActivity, Runnable paramRunnable)
  {
    if (this.editingText)
      closeTextEnter(false);
    while (true)
    {
      return;
      if (this.pickingSticker)
      {
        closeStickersView();
        return;
      }
      if (!hasChanges())
        break;
      if (paramActivity == null)
        continue;
      paramActivity = new AlertDialog.Builder(paramActivity);
      paramActivity.setMessage(LocaleController.getString("DiscardChanges", 2131165659));
      paramActivity.setTitle(LocaleController.getString("AppName", 2131165319));
      paramActivity.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramRunnable)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          this.val$okRunnable.run();
        }
      });
      paramActivity.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
      paramPhotoViewer.showAlertDialog(paramActivity);
      return;
    }
    paramRunnable.run();
  }

  public void onBeganEntityDragging(EntityView paramEntityView)
  {
    setColorPickerVisibilityFade(false);
  }

  public boolean onEntityLongClicked(EntityView paramEntityView)
  {
    showMenuForEntity(paramEntityView);
    return true;
  }

  public boolean onEntitySelected(EntityView paramEntityView)
  {
    return selectEntity(paramEntityView);
  }

  public void onFinishedEntityDragging(EntityView paramEntityView)
  {
    setColorPickerVisibilityFade(true);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    int i = ActionBar.getCurrentActionBarHeight();
    paramInt3 = this.actionBar.getMeasuredHeight();
    this.actionBar.layout(0, 0, this.actionBar.getMeasuredWidth(), paramInt3);
    paramInt4 = AndroidUtilities.displaySize.y - i - AndroidUtilities.dp(48.0F);
    float f1;
    float f2;
    label101: float f3;
    if (this.bitmapToEdit != null)
      if (isSidewardOrientation())
      {
        f1 = this.bitmapToEdit.getHeight();
        if (!isSidewardOrientation())
          break label494;
        f2 = this.bitmapToEdit.getWidth();
        f3 = paramInt1;
        if ((float)Math.floor(f3 * f2 / f1) <= paramInt4)
          break label528;
        f1 = (float)Math.floor(f1 * paramInt4 / f2);
      }
    while (true)
    {
      i = (int)Math.ceil((paramInt1 - this.renderView.getMeasuredWidth()) / 2);
      int j = (paramInt2 - paramInt3 - AndroidUtilities.dp(48.0F) - this.renderView.getMeasuredHeight()) / 2 + paramInt3;
      this.renderView.layout(i, j, this.renderView.getMeasuredWidth() + i, this.renderView.getMeasuredHeight() + j);
      f1 /= this.paintingSize.width;
      this.entitiesView.setScaleX(f1);
      this.entitiesView.setScaleY(f1);
      this.entitiesView.layout(i, j, this.entitiesView.getMeasuredWidth() + i, this.entitiesView.getMeasuredHeight() + j);
      this.dimView.layout(0, paramInt3, this.dimView.getMeasuredWidth(), this.dimView.getMeasuredHeight() + paramInt3);
      this.selectionContainerView.layout(0, paramInt3, this.selectionContainerView.getMeasuredWidth(), this.selectionContainerView.getMeasuredHeight() + paramInt3);
      this.colorPicker.layout(0, paramInt3, this.colorPicker.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + paramInt3);
      this.toolsView.layout(0, paramInt2 - this.toolsView.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), paramInt2);
      this.curtainView.layout(0, 0, paramInt1, paramInt4);
      if (this.stickersView != null)
        this.stickersView.layout(0, paramInt3, this.stickersView.getMeasuredWidth(), this.stickersView.getMeasuredHeight() + paramInt3);
      if (this.currentEntityView != null)
      {
        this.currentEntityView.updateSelectionView();
        this.currentEntityView.setOffset(this.entitiesView.getLeft() - this.selectionContainerView.getLeft(), this.entitiesView.getTop() - this.selectionContainerView.getTop());
      }
      return;
      f1 = this.bitmapToEdit.getWidth();
      break;
      label494: f2 = this.bitmapToEdit.getHeight();
      break label101;
      f1 = paramInt1;
      f2 = paramInt2 - i - AndroidUtilities.dp(48.0F);
      break label101;
      label528: f1 = f3;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    setMeasuredDimension(i, paramInt2);
    this.actionBar.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2, -2147483648));
    int j = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
    int k = j - AndroidUtilities.dp(48.0F);
    float f2;
    float f1;
    label96: float f3;
    float f4;
    if (this.bitmapToEdit != null)
      if (isSidewardOrientation())
      {
        f2 = this.bitmapToEdit.getHeight();
        if (!isSidewardOrientation())
          break label317;
        f1 = this.bitmapToEdit.getWidth();
        f3 = i;
        f4 = (float)Math.floor(f3 * f1 / f2);
        if (f4 <= k)
          break label351;
        f3 = k;
        f2 = (float)Math.floor(f2 * f3 / f1);
        f1 = f3;
      }
    while (true)
    {
      this.renderView.measure(View.MeasureSpec.makeMeasureSpec((int)f2, 1073741824), View.MeasureSpec.makeMeasureSpec((int)f1, 1073741824));
      this.entitiesView.measure(View.MeasureSpec.makeMeasureSpec((int)this.paintingSize.width, 1073741824), View.MeasureSpec.makeMeasureSpec((int)this.paintingSize.height, 1073741824));
      this.dimView.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(k, -2147483648));
      this.selectionContainerView.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(k, 1073741824));
      this.colorPicker.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(k, 1073741824));
      this.toolsView.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), 1073741824));
      if (this.stickersView != null)
        this.stickersView.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(j, 1073741824));
      return;
      f2 = this.bitmapToEdit.getWidth();
      break;
      label317: f1 = this.bitmapToEdit.getHeight();
      break label96;
      f2 = i;
      f1 = paramInt2 - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0F);
      break label96;
      label351: f1 = f4;
      f2 = f3;
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getY() > this.actionBar.getHeight())
    {
      if (this.currentEntityView != null)
      {
        if (this.editingText)
          closeTextEnter(true);
      }
      else
        return true;
      selectEntity(null);
      return true;
    }
    return false;
  }

  public void shutdown()
  {
    this.renderView.shutdown();
    this.entitiesView.setVisibility(8);
    this.selectionContainerView.setVisibility(8);
    this.queue.postRunnable(new Runnable()
    {
      public void run()
      {
        Looper localLooper = Looper.myLooper();
        if (localLooper != null)
          localLooper.quit();
      }
    });
  }

  private class StickerPosition
  {
    private float angle;
    private Point position;
    private float scale;

    StickerPosition(Point paramFloat1, float paramFloat2, float arg4)
    {
      this.position = paramFloat1;
      this.scale = paramFloat2;
      Object localObject;
      this.angle = localObject;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PhotoPaintView
 * JD-Core Version:    0.6.0
 */