/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.tool;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.ImageIcon;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ImageConverter {

    public static ImageView convertToImageView(ImageIcon icon) throws JEVisException {
        java.awt.image.BufferedImage bi = convertToBufferedImage(icon);
        javafx.scene.image.Image fi = convertToFxImage(bi);

        return new ImageView(fi);
    }

    public static ImageView convertToImageView(ImageIcon icon, double w, double h) throws JEVisException {
        java.awt.image.BufferedImage bi = convertToBufferedImage(icon);
        javafx.scene.image.Image fi = convertToFxImage(bi);
        ImageView iv = new ImageView(fi);
        iv.fitHeightProperty().setValue(h);
        iv.fitWidthProperty().setValue(w);
        iv.setSmooth(true);
        return iv;
    }

    public static java.awt.image.BufferedImage convertToBufferedImage(ImageIcon icon) {
        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;

    }

    public static javafx.scene.image.Image convertToFxImage(java.awt.image.BufferedImage awtImage) {
        if (Image.impl_isExternalFormatSupported(BufferedImage.class)) {
            return javafx.scene.image.Image.impl_fromExternalImage(awtImage);
        } else {
            return null;
        }
    }

    public static java.awt.image.BufferedImage convertToAwtImage(javafx.scene.image.Image fxImage) {
        if (Image.impl_isExternalFormatSupported(BufferedImage.class)) {
            java.awt.image.BufferedImage awtImage = new BufferedImage((int) fxImage.getWidth(), (int) fxImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            return (BufferedImage) fxImage.impl_toExternalImage(awtImage);
        } else {
            return null;
        }
    }
}
