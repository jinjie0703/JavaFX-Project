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

    /**
     * 应用程序入口点，初始化数据目录并显示登录界面。
     * @param stage 主舞台
     * @throws IOException 如果加载FXML文件失败
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Path dataDir = Paths.get("src/main/resources/com/wust/secondhand/data");
        Files.createDirectories(dataDir);
        DataManager.getInstance();
        showLoginView();
    }

    /**
     * 应用程序停止时保存数据。
     * 这里调用 DataManager 的 saveItems 方法来保存物品数据。
     */
    @Override
    public void stop() {
        DataManager.getInstance().saveItems();
    }

    /**
     * 显示登录界面。
     * @throws IOException 如果加载FXML文件失败
     */
    public static void showLoginView() throws IOException {
        Scene scene = FxmlManager.load("/com/wust/secondhand/fxml/LoginView.fxml", 380, 280);
        primaryStage.setTitle("登录 - 武科大二手交易");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 显示注册界面。
     * @throws IOException 如果加载FXML文件失败
     */
    public static void showAdminView() throws IOException {
        Scene scene = FxmlManager.load("/com/wust/secondhand/fxml/AdminMainView.fxml", 1000, 600);
        primaryStage.setTitle("管理员后台");
        primaryStage.setScene(scene);
        primaryStage.setX(300); // 设置窗口左上角X坐标
        primaryStage.setY(150); // 设置窗口左上角Y坐标
        primaryStage.show();
    }

    /**
     * 显示用户主界面。
     * @throws IOException 如果加载FXML文件失败
     */
    public static void showUserView() throws IOException {
        Scene scene = FxmlManager.load("/com/wust/secondhand/fxml/UserMainView.fxml", 1080, 600);
        primaryStage.setTitle("武科大二手市场");
        primaryStage.setScene(scene);
        primaryStage.setX(350); // 设置窗口左上角X坐标
        primaryStage.setY(150); // 设置窗口左上角Y坐标
        primaryStage.show();
    }

    /**
     程序的入口点。
     */
    public static void main(String[] args) {
        launch(args);
    }
}