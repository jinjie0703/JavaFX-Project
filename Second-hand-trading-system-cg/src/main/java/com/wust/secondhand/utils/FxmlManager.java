package com.wust.secondhand.utils;

import com.wust.secondhand.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class FxmlManager {

    public static Scene load(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
        return new Scene(fxmlLoader.load());
    }

    public static Scene load(String fxmlPath, double width, double height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
        return new Scene(fxmlLoader.load(), width, height);
    }
}