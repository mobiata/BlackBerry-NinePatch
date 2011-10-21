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
package com.mobiata.bb.ui.decor;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;

/**
 * A NinePatchBackground is an implementation of the standard Background interface that
 * uses a NinePatchBitmap to render the background. 
 *
 */
public class NinePatchBackground extends Background {

	private NinePatchBitmap ninePatch;
	
	public NinePatchBackground(NinePatchBitmap bmp) {
		ninePatch = bmp;
	}
	
	public void draw(Graphics g, XYRect rect) {
		ninePatch.draw(g, rect);
	}

	public boolean isTransparent() {
		return ninePatch.getBitmap().hasAlpha();
	}
	
	public NinePatchBitmap getNinePatch() {
		return ninePatch;
	}

}
