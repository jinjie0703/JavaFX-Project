package com.wust.secondhand.controllers;

import com.wust.secondhand.Main;
import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.models.User;
import com.wust.secondhand.models.enums.UserRole;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessage;

    // 这个方法用于处理登录按钮的点击事件
    // 1获取用户输入的用户名和密码；
    // 2检查用户名或密码是否为空，若为空则提示错误；
    // 3调用 DataManager.authenticate() 方法验证用户名和密码；
    // 4若验证通过，则设置当前用户并根据用户角色跳转到对应主界面（管理员或普通用户）；
    // 5若验证失败，则提示用户名或密码错误。
    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("用户名和密码不能为空！");
            return;
        }

        Optional<User> userOpt = DataManager.getInstance().authenticate(username, password);

        if (userOpt.isPresent()) {
            // 登录成功
            User user = userOpt.get();
            DataManager.getInstance().setCurrentUser(user);
            switchToMainView(user);
        } else {
            // 登录失败
            errorMessage.setText("用户名或密码错误！");
        }
    }

    // 这个方法用于处理“注册新用户”按钮的点击事件
    // 1使用FXMLLoader加载RegisterView.fxml界面文件；
    // 2创建新的Scene并设置到新创建的Stage上；
    // 3设置窗口标题为“新用户注册”，并设为应用模态（阻塞主窗口）；
    // 4显示注册窗口并等待用户操作完成。
    @FXML
    private void handleShowRegisterView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/wust/secondhand/fxml/RegisterView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("新用户注册");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // 根据用户角色切换到不同的主界面：如果是管理员（ADMIN），则显示管理员界面；否则显示普通用户界面
    private void switchToMainView(User user) throws IOException {
        if (user.getRole() == UserRole.ADMIN) {
            Main.showAdminView();
        } else {
            Main.showUserView();
        }
    }

    // 这段代码用于初始化登录界面的两个输入框事件：
    // 1当用户在用户名输入框按下回车时，自动将焦点转移到密码输入框；
    // 2当用户在密码输入框按下回车时，触发登录操作
    @FXML
    private void initialize() {
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> {
            try {
                handleLogin();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}