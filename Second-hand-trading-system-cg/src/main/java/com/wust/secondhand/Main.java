package com.wust.secondhand;

import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.utils.FxmlManager; // 导入 FxmlManager
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Path dataDir = Paths.get("src/main/resources/com/wust/secondhand/data");
        Files.createDirectories(dataDir);
        DataManager.getInstance();
        showLoginView();
    }

    @Override
    public void stop() {
        DataManager.getInstance().saveItems();
    }

    public static void showLoginView() throws IOException {
        Scene scene = FxmlManager.load("fxml/LoginView.fxml", 380, 280);
        primaryStage.setTitle("登录 - 武科大二手交易");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAdminView() throws IOException {
        Scene scene = FxmlManager.load("fxml/AdminMainView.fxml", 1000, 600);
        primaryStage.setTitle("管理员后台");
        primaryStage.setScene(scene);
    }

    public static void showUserView() throws IOException {
        Scene scene = FxmlManager.load("fxml/UserMainView.fxml", 1000, 600);
        primaryStage.setTitle("武科大二手市场");
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}