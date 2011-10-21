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
package com.mobiata.bb.ui.manager;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import com.mobiata.bb.ui.decor.NinePatchBitmap;

/**
 * A Manager that uses a NinePatchBitmap as its background and places
 * all children at the top left corner after taking into consideration
 * the padding set by the NinePatchBitmap.  You will probably want to 
 * put just a single child inside of this manager that is another manager
 * (e.g. a VerticalFieldManager).
 */
public class NinePatchFieldManager extends Manager {
    protected int topInset;
    protected int rightInset;
    protected int bottomInset;
    protected int leftInset;

    /**
     * Creates a NinePatchFieldManager.
     * 
     * @param bitmap the NinePatchBitmap to be used as the background of this manager
     * @param style the style 
     */
	public NinePatchFieldManager(NinePatchBitmap bitmap, long style) {
		super(style);
		setBackground(new NinePatchBackground(bitmap));
	}
	
	/**
	 * Set the NinePatchBackground to be used by this manager.
	 * @param background the NinePatchBackground to be used by this manager
	 */
	public void setBackground(NinePatchBackground background) {
		super.setBackground(background);
		setInsets(background.getNinePatch());
	}
	
	/**
	 * Set the NinePatchBackground to be used by this manager for a specific visual state
	 * @param visual the visual state
	 * @param background the NinePatchBackground to be used by this manager
	 */
	public void setBackground(int visual, NinePatchBackground background) {
		super.setBackground(visual, background);
		setInsets(background.getNinePatch());
	}
	
	/**
	 * Sets the padding for this manager based on the specified NinePatchBitmap.
	 * @param ninePatch
	 */
	private void setInsets(NinePatchBitmap ninePatch) {
		XYEdges padding = ninePatch.getPadding();
		topInset = padding.top;
		rightInset = padding.right;
		bottomInset = padding.bottom;
		leftInset = padding.left;
	}

    public int getPreferredWidth() {
        int result = 0;
        for (int x = 0; x < this.getFieldCount(); ++x)
            result = Math.max(result, this.getPreferredWidthOfChild(this.getField(x)));
        return result + leftInset + rightInset;
    }

    public int getPreferredHeight() {
        int result = 0;
        for (int x = 0; x < this.getFieldCount(); ++x)
            result = Math.max(result, this.getPreferredHeightOfChild(this.getField(x)));
        return result + topInset + bottomInset;
    }

	protected void sublayout(int layoutWidth, int layoutHeight) {
        final int innerWidth = layoutWidth - leftInset - rightInset;
        final int innerHeight = layoutHeight - topInset - bottomInset;

        int childWidth = 0;
        int childHeight = 0;
        for (int x = 0; x < this.getFieldCount(); ++x) {
            Field theField = this.getField(x);
            this.setPositionChild(theField, leftInset, topInset);
            this.layoutChild(theField, innerWidth, innerHeight);
            childWidth = Math.max(childWidth, theField.getWidth());
            childHeight = Math.max(childHeight, theField.getHeight());
        }

        if ((getStyle() & USE_ALL_WIDTH) == 0)
            layoutWidth = childWidth + leftInset + rightInset;

        if ((getStyle() & USE_ALL_HEIGHT) == 0)
            layoutHeight = childHeight + topInset + bottomInset;

        this.setExtent(layoutWidth, layoutHeight);
	}
}
