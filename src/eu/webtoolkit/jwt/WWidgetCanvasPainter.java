/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

import java.util.List;

class WWidgetCanvasPainter extends WWidgetPainter {
	public WWidgetCanvasPainter(WPaintedWidget widget) {
		super(widget);
	}

	public WPaintDevice getCreatePaintDevice() {
		return new WCanvasPaintDevice(new WLength(this.widget_.renderWidth_),
				new WLength(this.widget_.renderHeight_));
	}

	public void createContents(DomElement result, WPaintDevice device) {
		String wstr = String.valueOf(this.widget_.renderWidth_);
		String hstr = String.valueOf(this.widget_.renderHeight_);
		result.setProperty(Property.PropertyStylePosition, "relative");
		result.setProperty(Property.PropertyStyleOverflowX, "hidden");
		DomElement canvas = DomElement
				.createNew(DomElementType.DomElement_CANVAS);
		canvas.setId('c' + this.widget_.getId());
		canvas.setAttribute("width", wstr);
		canvas.setAttribute("height", hstr);
		result.addChild(canvas);
		DomElement text = DomElement.createNew(DomElementType.DomElement_DIV);
		text.setId('t' + this.widget_.getId());
		text.setProperty(Property.PropertyStylePosition, "absolute");
		text.setProperty(Property.PropertyStyleZIndex, "1");
		text.setProperty(Property.PropertyStyleTop, "0px");
		text.setProperty(Property.PropertyStyleLeft, "0px");
		WCanvasPaintDevice canvasDevice = ((device) instanceof WCanvasPaintDevice ? (WCanvasPaintDevice) (device)
				: null);
		canvasDevice.render("c" + this.widget_.getId(), text);
		result.addChild(text);
	}

	public void updateContents(List<DomElement> result, WPaintDevice device) {
		WCanvasPaintDevice canvasDevice = ((device) instanceof WCanvasPaintDevice ? (WCanvasPaintDevice) (device)
				: null);
		if (this.widget_.sizeChanged_) {
			DomElement canvas = DomElement.getForUpdate('c' + this.widget_
					.getId(), DomElementType.DomElement_CANVAS);
			canvas.setAttribute("width", String
					.valueOf(this.widget_.renderWidth_));
			canvas.setAttribute("height", String
					.valueOf(this.widget_.renderHeight_));
			result.add(canvas);
			this.widget_.sizeChanged_ = false;
		}
		DomElement text = DomElement.getForUpdate('t' + this.widget_.getId(),
				DomElementType.DomElement_DIV);
		text.removeAllChildren();
		canvasDevice.render('c' + this.widget_.getId(), text);
		result.add(text);
	}
}
