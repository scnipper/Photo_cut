package me.creese.photo.cut.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainController {

    @FXML
    Pane root;
    @FXML
    TextField savePath;
    @FXML
    TextField saveField;

    @FXML
    Button sampleBtn;
    @FXML
    TextField parts;
    @FXML
    Label errLabel;
    @FXML
    ProgressIndicator progr;
    @FXML
    ComboBox comboParts;
    @FXML
    Button okBtn;

    private BufferedImage image;
    private Stage form2;
    private int k;
    private BufferedImage resizedImage;
    private Graphics2D graph;
    private int partsCount = 2;
    private ArrayList<Rectangle> tmpRectangles;
    private ArrayList<Rectangle> rects;
    private String fileName;
    private String typeFile;
    private Alert alert;


    @FXML
    public void initialize() {
        tmpRectangles = new ArrayList<>();
        rects = new ArrayList<>();
        ObservableList<Integer> tmp = FXCollections.observableArrayList();
        int a = 2;
        for (int i = 0; i < 10; i++) {
            tmp.add(a);
            a += 2;
        }
        comboParts.setItems(tmp);
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
    }

    public void click(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор изображения");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All Images (*.*)", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        fileName = file.getName().split("[*.*]")[0];
        typeFile = file.getName().split("[*.*]")[1];
        typeFile.toLowerCase();

        savePath.setText(file.getAbsolutePath());
        progr.setProgress(0);
        loadImage();
        checkToValid();
    }

    private void showSampleForm() {
        Parent root2 = null;
        try {
            root2 = FXMLLoader.load(getClass().getResource("../fxmls/show_sample.fxml"));
        } catch (IOException e) {
            //e.printStackTrace();
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        form2 = new Stage();
        form2.setTitle("Пример");

        form2.setScene(new Scene(root2));
        form2.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("../icon.png")));

    }

    private void loadImage() {

        try {
            image = ImageIO.read(new File(savePath.getText()));
        } catch (IOException e) {
            //e.printStackTrace();

            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        k = 3;
        try {
            if (image.getHeight() > 1000) k = 6;
        } catch (NullPointerException e) {
            alert.setContentText(fileName + "." + typeFile + " - не поддерживаемый тип файла.");
            alert.showAndWait();
            savePath.setText("");
        }


        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        resizedImage = new BufferedImage(image.getWidth() / k, image.getHeight() / k, type);


    }

    private void checkToValid() {
        if (comboParts.getValue() != null) sampleBtn.setDisable(false);
        if (comboParts.getValue() != null && savePath.getText() != "" && saveField.getText() != "")
            okBtn.setDisable(false);
    }

    public void save(ActionEvent actionEvent) {
        DirectoryChooser directory = new DirectoryChooser();
        directory.setTitle("Выбор папки для сохранения");

        File file = directory.showDialog(root.getScene().getWindow());
        saveField.setText(file.getAbsolutePath());
        checkToValid();
    }

    public void clickOnSample(ActionEvent actionEvent) {

        //
        ControllerForSample.setImg(resizedImage);
        showSampleForm();
        form2.show();
        form2.setHeight(resizedImage.getHeight() + 20);
        form2.setWidth(resizedImage.getWidth() + 20);
        form2.setMinWidth(resizedImage.getWidth() + 20);
        form2.setMinHeight(resizedImage.getHeight() + 20);
        //graph.dispose();
        checkToValid();
    }

    private void saveCutImage() {
        tmpRectangles.clear();
        BufferedImage tmp;
        int st = 0;
        int sx = 0;
        int half = rects.size() / 2;
        double step = 1 / (double) (rects.size());

        double progress = 0;

        for (int i = 0; i < rects.size(); i++) {


            tmp = new BufferedImage((int) rects.get(i).getWidth(), (int) rects.get(i).getHeight(), image.getType());
            if (i > half - 1) {
                half *= 2;
                sx = 0;
                st++;
            }

            sx++;
            for (int y = (int) rects.get(i).getY(); y < rects.get(i).getHeight() * (st + 1); y++) {
                for (int x = (int) rects.get(i).getX(); x < rects.get(i).getWidth() * sx; x++) {
                    tmp.setRGB((int) (x - (rects.get(i).getWidth() * (sx - 1))), (int) (y - rects.get(i).getHeight() * st), image.getRGB(x, y));
                }
            }
            try {
                ImageIO.write(tmp, typeFile, new File(saveField.getText() + "/part_" + (i + 1) + "." + typeFile));
            } catch (IOException e) {
                e.printStackTrace();
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } catch (NullPointerException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        alert.setContentText("Укажите путь сохранения.");
                        alert.showAndWait();
                    }
                });

            }
            tmp = null;
            progress += step;
            progr.setProgress(progress);
            if (i == rects.size() - 1) progr.setProgress(1);
        }
    }

    public void okDoIt(ActionEvent actionEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveCutImage();
            }
        }).start();

    }

    public void comboClick(ActionEvent actionEvent) {
        partsCount = (Integer) comboParts.getValue();



        rects.clear();
        graph = resizedImage.createGraphics();

        graph.drawImage(image, 0, 0, image.getWidth() / k, image.getHeight() / k, null);
        graph.setColor(new Color(0, 255, 0));
        int tmpHeight = resizedImage.getHeight();
        int tmpWidth = resizedImage.getWidth();

        //partsCount /= 2;
        int halfCount = partsCount / 2;
        int _x = 0;
        int _y = 0;
        int perWidth = resizedImage.getWidth() / halfCount;
        int perHeight = resizedImage.getHeight() / 2;
        for (int i = 0; i < partsCount; i++) {
            tmpRectangles.add(new Rectangle(_x * perWidth, perHeight * _y, perWidth, perHeight));
            graph.drawRect((int) tmpRectangles.get(tmpRectangles.size() - 1).getX(), (int) tmpRectangles.get(tmpRectangles.size() - 1).getY(), (int) tmpRectangles.get(tmpRectangles.size() - 1).getWidth(),
                    (int) tmpRectangles.get(tmpRectangles.size() - 1).getHeight());
            _x++;
            if (i == halfCount - 1) {
                _x = 0;
                _y++;
            }

        }
        perWidth = image.getWidth() / halfCount;
        perHeight = image.getHeight() / 2;
        _x = 0;
        _y = 0;
        for (int i = 0; i < partsCount; i++) {
            rects.add(new Rectangle(_x * perWidth, perHeight * _y, perWidth, perHeight));
            graph.drawRect((int) rects.get(rects.size() - 1).getX(), (int) rects.get(rects.size() - 1).getY(), (int) rects.get(rects.size() - 1).getWidth(),
                    (int) rects.get(rects.size() - 1).getHeight());
            _x++;
            if (i == halfCount - 1) {
                _x = 0;
                _y++;
            }

        }
        checkToValid();
    }
}
