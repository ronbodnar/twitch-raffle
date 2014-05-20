/**
 * Copyright (c) 2014 Ron Bodnar.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.aeternaly.raffle.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageConstants {

	private BufferedImage[] images;
	
	public static String IMAGE_DIRECTORY = Constants.SETTINGS_DIRECTORY + "images" + File.separator;

	public static final int FAVICON = 0, REFRESH_ICON = 1;
	
	public static final String FAVICON_URL = IMAGE_DIRECTORY + "favicon.png";
	
	public static final String REFRESH_ICON_URL = IMAGE_DIRECTORY + "refresh-icon.png";

	public ImageConstants() {
		try {
			images = new BufferedImage[2];
			images[FAVICON] = ImageIO.read(new URL("http://i.imgur.com/oujpZZm.png"));//new File(FAVICON_URL));
			images[REFRESH_ICON] = ImageIO.read(new URL("http://i.imgur.com/M25KZh8.png"));//File(REFRESH_ICON_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage(int index) {
		return images[index];
	}
	
	public BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);

		Graphics2D graphics = (Graphics2D) bufferedImage.createGraphics();
		graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();

		return bufferedImage;
	}

}