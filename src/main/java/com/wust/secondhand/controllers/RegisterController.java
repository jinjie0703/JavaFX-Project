package com.wust.secondhand.controllers;

import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.models.User;
import com.wust.secondhand.models.enums.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    private final DataManager dataManager = DataManager.getInstance();

    // 这个方法用于处理注册按钮的点击事件
    // 1获取用户输入的用户名和密码；
    // 2校验输入是否为空，密码是否一致；
    // 3调用 dataManager.userExists(username) 检查用户名是否已存在；
    // 4若全部校验通过，调用 dataManager.addUser(...) 添加新用户；
    // 5最终显示注册成功信息，并设置文字颜色为绿色。
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("用户名和密码不能为空！");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("两次输入的密码不一致！");
            return;
        }

        if (dataManager.userExists(username)) {
            messageLabel.setText("用户名已存在！");
            return;
        }

        dataManager.addUser(new User(username, password, UserRole.USER));
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("注册成功！");
    }
}
