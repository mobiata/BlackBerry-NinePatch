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

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;

import com.mobiata.bb.ui.decor.NinePatchBitmap;

/**
 * A NinePatchHRField draws a horizontal rule across the entire width of its Manager
 * at the specified height, using a NinePatchBitmap to render the rule.
 */
public class NinePatchHRField extends HRField {

	protected NinePatchBitmap ninePatch;
	
	public NinePatchHRField(NinePatchBitmap npbmp) {
		super(npbmp.getBitmap().getHeight(), 0);
		ninePatch = npbmp;
	}
	
	protected void paint(Graphics g) {
		ninePatch.draw(g, new XYRect(0, 0, getWidth(), getHeight()));
	}
	
}
