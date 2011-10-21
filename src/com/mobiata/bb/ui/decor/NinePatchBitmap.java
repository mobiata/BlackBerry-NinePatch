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

import java.util.Hashtable;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;

/*
 *        +---+---+---+
 *        | 0 | 1 | 2 |
 *        +---+---+---+
 *        | 3 | 4 | 5 |
 *        +---+---+---+
 *        | 6 | 7 | 8 |
 *        +---+---+---+
 */

/**
 * A NinePatchBitmap is a special image that can be stretched intelligently.  This implementation has been designed
 * around the nine-patch image format used in the Android operating system.  Please see
 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#nine-patch
 */
public class NinePatchBitmap {

	/**
	 * Flag that indicates the associated NinePatch graphic can be stretched horizontally using the shortcut method.
	 * The shortcut method duplicates the entire width of the horizontally stretchable portions of the original graphic
	 * in chunks, rather than pixel-by-pixel.
	 */
	public static final int H_SHORTCUT = 1;

	/**
	 * Flag that indicates the associated NinePatch graphic can be stretched vertically using the shortcut method.
	 * The shortcut method duplicates the entire height of the vertically stretchable portions of the original graphic
	 * in chunks, rather than pixel-by-pixel.
	 */
	public static final int V_SHORTCUT = 2;

	private static final Hashtable renderedBitmaps = new Hashtable();

	private Bitmap rawBitmap;
	private Bitmap bitmap;
	private Bitmap[] bitmapPatches;
	private XYRect stretchRect;
	private XYRect padRect;
	private int options;


	/**
	 * Create a NinePatchBitmap from a Bitmap.
	 * 
	 * @param bmp the bitmap
	 */
	public NinePatchBitmap(Bitmap bmp) {
		this(bmp, 0);
	}

	/**
	 * Create a NinePatchBitmap from a Bitmap.
	 * 
	 * @param bmp the bitmap
	 * @param options options (e.g. H_SHORTCUT and/or V_SHORTCUT)
	 */
	public NinePatchBitmap(Bitmap bmp, int options) {
		rawBitmap = bmp;
		this.options = options;
		calculatePatches();
		bitmap = copyBitmapSection(rawBitmap, 1, 1, rawBitmap.getWidth() - 2, rawBitmap.getHeight() - 2);

		bitmapPatches = new Bitmap[9];

		int[] colX = new int[] { 0, stretchRect.x, stretchRect.x + stretchRect.width };
		int[] colY = new int[] { 0, stretchRect.y, stretchRect.y + stretchRect.height };
		int[] colWidths = new int[] { stretchRect.x, stretchRect.width, bitmap.getWidth() - stretchRect.x - stretchRect.width };
		int[] rowHeights = new int[] { stretchRect.y, stretchRect.height, bitmap.getHeight() - stretchRect.y - stretchRect.height };

		bitmapPatches[0] = copyBitmapSection(bitmap, colX[0], colY[0], colWidths[0], rowHeights[0]);
		bitmapPatches[1] = copyBitmapSection(bitmap, colX[1], colY[0], colWidths[1], rowHeights[0]);
		bitmapPatches[2] = copyBitmapSection(bitmap, colX[2], colY[0], colWidths[2], rowHeights[0]);

		bitmapPatches[3] = copyBitmapSection(bitmap, colX[0], colY[1], colWidths[0], rowHeights[1]);
		bitmapPatches[4] = copyBitmapSection(bitmap, colX[1], colY[1], colWidths[1], rowHeights[1]);
		bitmapPatches[5] = copyBitmapSection(bitmap, colX[2], colY[1], colWidths[2], rowHeights[1]);

		bitmapPatches[6] = copyBitmapSection(bitmap, colX[0], colY[2], colWidths[0], rowHeights[2]);
		bitmapPatches[7] = copyBitmapSection(bitmap, colX[1], colY[2], colWidths[1], rowHeights[2]);
		bitmapPatches[8] = copyBitmapSection(bitmap, colX[2], colY[2], colWidths[2], rowHeights[2]);

		if (renderedBitmaps.get(rawBitmap) == null) {
			renderedBitmaps.put(rawBitmap, new Hashtable());
		}
	}

	/**
	 * Gets the unstretched bitmap (with the sizing bars removed).
	 * 
	 * @return the unstretched bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * Gets the padding rectangle.  The padding rectangle is the rectangle defined by the intersection of
	 * the horizontal and vertical padding regions.
	 * 
	 * @return the padding rectangle
	 */
	public XYRect getPadRect() {
		return padRect;
	}
	
	/**
	 * Gets the padding for this NinePatchBitmap.  Whereas the padding rectangle defines a rectangular
	 * region in the bitmap, the padding returns the number of pixels of padding on the four sides of
	 * the content.
	 * 
	 * @return the padding
	 */
	public XYEdges getPadding() {
		XYEdges pad = new XYEdges();

		pad.left = padRect.x;
		pad.top = padRect.y;
		pad.right = bitmap.getWidth() - padRect.width - padRect.x;
		pad.bottom = bitmap.getHeight() - padRect.height - padRect.y;
		
		return pad;
	}

	/**
	 * Gets a rendered version of the NinePatchBitmap at a specific size.
	 * 
	 * @param width the desired width
	 * @param height the desired height
	 * @return a version of this NinePatchBitmap rendered at the specified size
	 */
	public Bitmap getRenderedBitmap(int width, int height) {
		Hashtable rendered = (Hashtable) renderedBitmaps.get(rawBitmap);
		String renderedSize = width + "x" + height;
		
		Bitmap prerendered = (Bitmap) rendered.get(renderedSize);
		if (prerendered == null) {
			prerendered = render(width, height);
			rendered.put(renderedSize, prerendered);
		}
		
		return prerendered;
	}
	
	/**
	 * Draws the NinePatchBitmap into the specified rectangle using the specified graphics context.
	 * 
	 * @param g the graphics context to use
	 * @param rect the rectangle in which to draw
	 */
	public void draw(Graphics g, XYRect rect) {
		Bitmap prerendered = getRenderedBitmap(rect.width, rect.height);
		g.drawBitmap(rect.x, rect.y, rect.width, rect.height, prerendered, 0, 0);
	}

	/**
	 * Renders the NinePatchBitmap at the specified size.
	 * 
	 * @param width the desired width
	 * @param height the desired height
	 * @return a rendering of this NinePatchBitmap at the specified size
	 */
	private Bitmap render(int width, int height) {
		Bitmap bmp = new Bitmap(width, height);

		// patch 0 - copy to the upper left corner
		if (bitmapPatches[0].getWidth() > 0 && bitmapPatches[0].getHeight() > 0) {
			copyBitmapSectionToBitmap(bmp, 0, 0, bitmapPatches[0].getWidth(), bitmapPatches[0].getHeight(), bitmapPatches[0], 0, 0);
		}

		// patch 1 - stretch across top
		int widthToFill = width - bitmapPatches[0].getWidth() - bitmapPatches[2].getWidth();
		int[] pixelWidths = new int[bitmapPatches[1].getWidth()];
		int x = bitmapPatches[0].getWidth();
		if (widthToFill > 0) {
			int widthPerPixel = widthToFill / bitmapPatches[1].getWidth();
			int remainWidth = widthToFill - (widthPerPixel * bitmapPatches[1].getWidth());
			int skip = (pixelWidths.length - remainWidth) / 2;
			boolean needExtra = (pixelWidths.length - remainWidth) != (skip * 2);
			for (int i = 0; i < pixelWidths.length; i++) {
				pixelWidths[i] = widthPerPixel;
				if (i >= skip && i < (needExtra ? (pixelWidths.length - (skip+1)) : (pixelWidths.length - skip))) {
					pixelWidths[i]++;
				}
			}

			if (bitmapPatches[1].getHeight() > 0) {
				if ((options & H_SHORTCUT) == H_SHORTCUT) {
					int maxX = width - bitmapPatches[2].getWidth();
					while (x < maxX) {
						int w = bitmapPatches[1].getWidth();
						if (x+w > maxX)
							w = maxX - x;
						copyBitmapSectionToBitmap(bmp, x, 0, w, bitmapPatches[1].getHeight(), bitmapPatches[1], 0, 0);
						x += w;
					}
				} else {
					for (int i = 0; i < pixelWidths.length && x < width; i++) {
						for (int j = 0; j < pixelWidths[i] && x < width; j++) {
							copyBitmapSectionToBitmap(bmp, x++, 0, 1, bitmapPatches[1].getHeight(), bitmapPatches[1], i, 0);
						}
					}
				}
			}
		}

		// patch 2 - copy to upper right corner
		x = width - bitmapPatches[2].getWidth();
		if (x < 0) x = 0;
		if (bitmapPatches[2].getWidth() > 0 && bitmapPatches[2].getHeight() > 0) {
			copyBitmapSectionToBitmap(bmp, x, 0, bitmapPatches[2].getWidth(), bitmapPatches[2].getHeight(), bitmapPatches[2], 0, 0);
		}

		// patch 3 - stretch down left edge
		int heightToFill = height - bitmapPatches[0].getHeight() - bitmapPatches[6].getHeight();
		int[] pixelHeights = new int[bitmapPatches[3].getHeight()];
		int y = bitmapPatches[0].getHeight();
		if (heightToFill > 0) {
			int heightPerPixel = heightToFill / bitmapPatches[3].getHeight();
			int remainHeight = heightToFill - (heightPerPixel * bitmapPatches[3].getHeight());
			int skip = (pixelHeights.length - remainHeight) / 2;
			boolean needExtra = (pixelHeights.length - remainHeight) != (skip * 2);
			for (int i = 0; i < pixelHeights.length; i++) {
				pixelHeights[i] = heightPerPixel;
				if (i >= skip && i < (needExtra ? (pixelHeights.length - (skip+1)) : (pixelHeights.length - skip))) {
					pixelHeights[i]++;
				}
			}

			if (bitmapPatches[3].getWidth() > 0) {
				if ((options & V_SHORTCUT) == V_SHORTCUT) {
					int maxY = height - bitmapPatches[6].getHeight();
					while (y < maxY) {
						int h = bitmapPatches[3].getHeight();
						if (y+h > maxY) 
							h = maxY - y;
						copyBitmapSectionToBitmap(bmp, 0, y, bitmapPatches[3].getWidth(), h, bitmapPatches[3], 0, 0);
						y += h;
					}
				} else {
					for (int i = 0; i < pixelHeights.length && y < height; i++) {
						for (int j = 0; j < pixelHeights[i] && y < height; j++) {
							copyBitmapSectionToBitmap(bmp, 0, y++, bitmapPatches[3].getWidth(), 1, bitmapPatches[3], 0, i);
						}
					}
				}
			}
		}

		// patch 4 - stretch across middle and down middle
		if (widthToFill > 0 && heightToFill > 0) {
			y = bitmapPatches[1].getHeight();
			if ((options & (H_SHORTCUT | V_SHORTCUT)) == (H_SHORTCUT | V_SHORTCUT)) {
				int maxX = width - bitmapPatches[5].getWidth();
				int maxY = height - bitmapPatches[7].getHeight();
				while (y < maxY) {
					int h = bitmapPatches[4].getHeight();
					if (y+h > maxY)
						h = maxY - y;
					x = bitmapPatches[3].getWidth();
					while (x < maxX) {
						int w = bitmapPatches[4].getWidth();
						if (x+w > maxX)
							w = maxX - x;
						copyBitmapSectionToBitmap(bmp, x, y, w, h, bitmapPatches[4], 0, 0);
						x += w;
					}
					y += h;
				}
			} else if ((options & H_SHORTCUT) == H_SHORTCUT) {
				int maxX = width - bitmapPatches[5].getWidth();
				for (int i = 0; i < pixelHeights.length && y < height; i++) {
					for (int j = 0; j < pixelHeights[i] && y < height; j++) {
						x = bitmapPatches[3].getWidth();
						while (x < maxX) {
							int w = bitmapPatches[4].getWidth();
							if (x+w > maxX)
								w = maxX - x;
							copyBitmapSectionToBitmap(bmp, x, y, w, 1, bitmapPatches[4], 0, i);
							x += w;
						}
						y++;
					}
				}
			} else if ((options & V_SHORTCUT) == V_SHORTCUT) {
				int maxY = height - bitmapPatches[7].getHeight();
				x = bitmapPatches[3].getWidth();
				for (int k = 0; k < pixelWidths.length && x < width; k++) {
					for (int l = 0; l < pixelWidths[k] && x < width; l++) {
						y = bitmapPatches[1].getHeight();
						while (y < maxY) {
							int h = bitmapPatches[4].getHeight();
							if (y+h > maxY)
								h = maxY - y;
							copyBitmapSectionToBitmap(bmp, x, y, 1, h, bitmapPatches[4], k, 0);
							y += h;
						}
						x++;
					}
				}
			} else {
				for (int i = 0; i < pixelHeights.length && y < height; i++) {
					for (int j = 0; j < pixelHeights[i] && y < height; j++) {
						x = bitmapPatches[3].getWidth();
						for (int k = 0; k < pixelWidths.length && x < width; k++) {
							for (int l = 0; l < pixelWidths[k] && x < width; l++) {
								copyBitmapSectionToBitmap(bmp, x++, y, 1, 1, bitmapPatches[4], k, i);
							}
						}
						y++;
					}
				}
			}
		}

		// patch 5 - stretch down right edge
		if (heightToFill > 0 && bitmapPatches[5].getWidth() > 0) {
			y = bitmapPatches[2].getHeight();
			x = width - bitmapPatches[5].getWidth();
			if (x < 0) x = 0;
			if ((options & V_SHORTCUT) == V_SHORTCUT) {
				int maxY = height - bitmapPatches[8].getHeight();
				while (y < maxY) {
					int h = bitmapPatches[5].getHeight();
					if (y+h > maxY) 
						h = maxY - y;
					copyBitmapSectionToBitmap(bmp, x, y, bitmapPatches[5].getWidth(), h, bitmapPatches[5], 0, 0);
					y += h;
				}
			} else {
				for (int i = 0; i < pixelHeights.length && y < height; i++) {
					for (int j = 0; j < pixelHeights[i] && y < height; j++) {
						copyBitmapSectionToBitmap(bmp, x, y++, bitmapPatches[5].getWidth(), 1, bitmapPatches[5], 0, i);
					}
				}
			}
		}

		// patch 6 - copy to lower left corner
		y = height - bitmapPatches[6].getHeight();
		if (y < 0) y = 0;
		if (bitmapPatches[6].getWidth() > 0 && bitmapPatches[6].getHeight() > 0)
			copyBitmapSectionToBitmap(bmp, 0, y, bitmapPatches[6].getWidth(), bitmapPatches[6].getHeight(), bitmapPatches[6], 0, 0);

		// patch 7 - stretch across bottom
		if (widthToFill > 0 && bitmapPatches[7].getHeight() > 0) {
			y = height - bitmapPatches[7].getHeight();
			if (y < 0) y = 0;
			x = bitmapPatches[6].getWidth();
			if ((options & H_SHORTCUT) == H_SHORTCUT) {
				int maxX = width - bitmapPatches[8].getWidth();
				while (x < maxX) {
					int w = bitmapPatches[7].getWidth();
					if (x+w > maxX)
						w = maxX - x;
					copyBitmapSectionToBitmap(bmp, x, y, w, bitmapPatches[7].getHeight(), bitmapPatches[7], 0, 0);
					x += w;
				}
			} else {
				for (int i = 0; i < pixelWidths.length && x < width; i++) {
					for (int j = 0; j < pixelWidths[i] && x < width; j++) {
						copyBitmapSectionToBitmap(bmp, x++, y, 1, bitmapPatches[7].getHeight(), bitmapPatches[7], i, 0);
					}
				}
			}
		}

		// patch 8 - copy to lower right corner
		if (bitmapPatches[8].getWidth() > 0 && bitmapPatches[8].getHeight() > 0) {
			x = width - bitmapPatches[8].getWidth();
			if (x < 0) x = 0;
			y = height - bitmapPatches[8].getHeight();
			if (y < 0) y = 0;
			copyBitmapSectionToBitmap(bmp, x, y, bitmapPatches[8].getWidth(), bitmapPatches[8].getHeight(), bitmapPatches[8], 0, 0);
		}

		return bmp;
	}

	/**
	 * Copies a portion of a bitmap into a second bitmap.
	 * 
	 * @param dst the destination bitmap
	 * @param dstXOffset the destination x offset
	 * @param dstYOffset the destination y offset
	 * @param width the width to copy
	 * @param height the height to copy
	 * @param src the source bitmap
	 * @param srcXOffset the source x offset
	 * @param srcYOffset the source y offset
	 */
	private void copyBitmapSectionToBitmap(Bitmap dst, int dstXOffset, int dstYOffset, int width, int height, Bitmap src, int srcXOffset, int srcYOffset) {
		int[] raw = new int[width*height];
		src.getARGB(raw, 0, width, srcXOffset, srcYOffset, width, height);
		dst.setARGB(raw, 0, width, dstXOffset, dstYOffset, width, height);
	}

	/**
	 * Creates a bitmap that is a copy of a portion of an existing bitmap.
	 * 
	 * @param src the source bitmap
	 * @param x the x offset
	 * @param y the y offset
	 * @param width the width to copy
	 * @param height the height to copy
	 * @return a new Bitmap that is a copy of the specified portion of the specified bitmap
	 */
	private Bitmap copyBitmapSection(Bitmap src, int x, int y, int width, int height) {
		if (x < 0 || y < 0 || (x+width) > src.getWidth() || (y+height) > src.getHeight()) {
			throw new IllegalArgumentException("must not specify a rectangle that falls outside of the source bitmap");
		}

		Bitmap dst = new Bitmap(width, height);

		int[] raw = new int[width*height];
		src.getARGB(raw, 0, width, x, y, width, height);
		dst.setARGB(raw, 0, width, 0, 0, width, height);

		return dst;
	}

	/**
	 * Inspects a NinePatch-formatted image and calculates the stretch and pad
	 * rectangles.  Note that the lines defining the stretch and pad rectangles
	 * must be pure black (#000000) and completely opaque (alpha 100%) in order
	 * for this method to work properly.
	 */
	private void calculatePatches() {
		stretchRect = new XYRect();
		padRect = new XYRect();

		int height = rawBitmap.getHeight();
		int width = rawBitmap.getWidth();

		int[] raw = new int[width];
		rawBitmap.getARGB(raw, 0, width, 0, 0, width, 1);
		boolean started = false;
		for (int i = 0; i < raw.length; i++) {
			if (!started && (raw[i] == 0xFF000000)) {
				started = true;
				stretchRect.x = i;
			} else if (started && (raw[i] != 0xFF000000)) {
				stretchRect.width = i - stretchRect.x;
				break;
			}
		}

		rawBitmap.getARGB(raw, 0, width, 0, height-1, width, 1);
		started = false;
		for (int i = 0; i < raw.length; i++) {
			if (!started && (raw[i] == 0xFF000000)) {
				started = true;
				padRect.x = i;
			} else if (started && (raw[i] != 0xFF000000)) {
				padRect.width = i - padRect.x;
				break;
			}
		}

		raw = new int[height];
		started = false;
		rawBitmap.getARGB(raw, 0, 1, 0, 0, 1, height);
		for (int i = 0; i < raw.length; i++) {
			if (!started && (raw[i] == 0xFF000000)) {
				started = true;
				stretchRect.y = i;
			} else if (started && (raw[i] != 0xFF000000)) {
				stretchRect.height = i - stretchRect.y;
				break;
			}
		}

		started = false;
		rawBitmap.getARGB(raw, 0, 1, width-1, 0, 1, height);
		for (int i = 0; i < raw.length; i++) {
			if (!started && (raw[i] == 0xFF000000)) {
				started = true;
				padRect.y = i;
			} else if (started && (raw[i] != 0xFF000000)) {
				padRect.height = i - padRect.y;
				break;
			}
		}


		// recalculate to take into account the 1px border that will go away
		stretchRect.x -= 1;
		stretchRect.y -= 1;
		padRect.x -= 1;
		padRect.y -= 1;
	}
}
