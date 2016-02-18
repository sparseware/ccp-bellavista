package com.sparseware.bellavista;

import com.appnativa.rare.ui.UIColor;
import com.appnativa.rare.ui.UIInsets;
import com.appnativa.rare.ui.UIStroke;
import com.appnativa.rare.ui.border.UILineBorder;
import com.appnativa.rare.ui.iPlatformGraphics;
/**
 * A custom border for text fields.
 * The border will look the same on all platforms.
 * 
 * @author Don DeCoteau
 */
public class TextFieldBorder extends UILineBorder {
  public TextFieldBorder() {
    super(UILineBorder.getDefaultLineColor());
    noTop = true;
  }

  @Override
  public void paint(iPlatformGraphics g, float x, float y, float width, float height, boolean last) {
    if (last != isPaintLast()) {
      return;
    }

    UIColor color = getPaintColor(g.getComponent());

    if (color.getAlpha() == 0) {
      return;
    }

    UIStroke stroke = g.getStroke();
    UIColor  c      = g.getColor();

    g.setColor(color);

    if (lineStroke != null) {
      g.setStroke(lineStroke);
    } else {
      g.setStrokeWidth(thickness);
    }

    if (insets == null) {
      insets = new UIInsets();
    }

    float    left   = x;
    float    top    = y;
    float    right  = x + width;
    float    bottom = y + height;
    UIInsets in     = calculateInsets(insets, false);

    top = bottom - (height / 3);

    if (in.left > 0) {
      g.fillRect(left, top, in.left, bottom - top);
    }

    if (in.bottom > 0) {
      g.fillRect(left, bottom - in.bottom, right - left, in.bottom);
    }

    if (in.right > 0) {
      g.fillRect(right - in.right, top, in.right, bottom - top);
    }

    g.setStroke(stroke);
    g.setColor(c);
  }

  @Override
  public boolean usesPath() {
    return false;
  }
}
