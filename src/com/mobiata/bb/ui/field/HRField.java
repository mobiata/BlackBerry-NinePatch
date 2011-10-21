/**
 * Copyright (c) 2011 Mobiata, LLC.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without 
 * restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * This License shall be included in all copies or substantial 
 * portions of the Software.
 * 
 * The name(s) of the above copyright holders shall not be used 
 * in advertising or otherwise to promote the sale, use or other 
 * dealings in this Software without prior written authorization.
 * 
 */
package com.mobiata.bb.ui.field;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

/**
 * An HRField draws a horizontal rule that extends across the entire width of its Manager
 * at a specified height and using a specified color.
 */
public class HRField extends Field {

	protected int fHeight;
	
	protected Integer fColor;
	
	public HRField() {
		this(1);
	}
	
	/**
	 * Creates an HRField with a specified height and using the default color.
	 * 
	 * @param height the height in pixels
	 */
	public HRField(int height) {
		fHeight = height;
		fColor = null;
	}
	
	/**
	 * Creates an HRField with a specified height and color.  Note that the color includes
	 * an alpha value specified in the most significant bits (e.g. 0xAARRGGBB).
	 * 
	 * @param height the height in pixels
	 * @param color the color to use, including alpha
	 */
	public HRField(int height, int color) {
		fHeight = height;
		fColor = new Integer(color);
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		return fHeight;
	}
	
	protected void layout(int width, int height) {
		setExtent(width, Math.min(fHeight, height));
	}

	protected void paint(Graphics g) {
		int priorAlpha = g.getGlobalAlpha();
		int priorColor = g.getColor();
		
		if (fColor != null) {
			int clr = fColor.intValue();
			int alpha = (clr >> 24) & 0xFF;

			g.setGlobalAlpha(alpha);
			g.setColor(clr);
		}
		
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (fColor != null) {
			g.setColor(priorColor);
			g.setGlobalAlpha(priorAlpha);
		}
	}

}
