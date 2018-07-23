package me.creese.photo.cut.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/**
 * Created by andrew_pc on 04.10.2016.
 */
public class ControllerForSample {

    private static BufferedImage img;
    @FXML
    Canvas imgc;

    public static void setImg(BufferedImage img) {
        ControllerForSample.img = img;
    }


    @FXML
    public void initialize() {
        imgc.setWidth(img.getWidth());
        imgc.setHeight(img.getHeight());
        ImageView image1 = new ImageView(SwingFXUtils.toFXImage(img, null));
        imgc.getGraphicsContext2D().drawImage(image1.getImage(),0,0);
    }

}
