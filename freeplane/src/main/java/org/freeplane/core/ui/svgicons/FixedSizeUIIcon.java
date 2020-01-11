package org.freeplane.core.ui.svgicons;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import org.freeplane.core.util.LogUtils;

public class FixedSizeUIIcon implements Icon {

    private final int width;
    private final int height;
    private final URL url;
    private boolean failure = false;
    

    public FixedSizeUIIcon(URL url, int width, int height) {
        super();
        this.url = url;
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (isValid()) {
            Image image = createImage();
            if(image != null)
                g.drawImage(image, x, y, c);
        }
    }

    private boolean isValid() {
        return width >= 0 && height >= 0 && failure == false;
    }

    private Image createImage() {
        try {
            if(url.getPath().endsWith(".svg")) 
                return new SVGIconCreator(url).setHeight(height).setWidth(width).loadImage();
            else
                return ImageIO.read(url).getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            LogUtils.severe(e);
            failure = true;
            return null;
        }
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    public static FixedSizeUIIcon withHeigth(URL url, int heightInPixel) {
        return new FixedSizeUIIcon(url, heightInPixel, heightInPixel);
    }

    public FixedSizeUIIcon withProportionalWidth() {
        if (! isValid()) 
            return this;
        try {
            if(url.getPath().endsWith(".svg")) {
                Dimension size = new SVGIconCreator(url).getSize();
                return new FixedSizeUIIcon(url, height * size.width / size.height , height);
            } else {
                BufferedImage image = ImageIO.read(url);
                image.getHeight();
                return new FixedSizeUIIcon(url, height * image.getWidth() / image.getHeight() , height);
            }
        } catch (Exception e) {
            LogUtils.severe(e);
            failure = true;
            return null;
        }
    }

}