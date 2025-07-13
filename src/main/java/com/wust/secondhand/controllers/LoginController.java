package com.wust.secondhand.controllers;

import com.wust.secondhand.Main;
import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.models.User;
import com.wust.secondhand.models.enums.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessage;

    @FXML
    protected void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorMessage.setText("用户名或密码不能为空！");
            return;
        }

        Optional<User> userOpt = DataManager.getInstance().authenticate(username, password);

        if (userOpt.isPresent()) {
            // 登录成功
            User user = userOpt.get();
            DataManager.getInstance().setCurrentUser(user);
            switchToMainView(user);
        } else {
            // 用户不存在，视为注册新用户
            User newUser = new User(username, password, UserRole.USER);
            DataManager.getInstance().addUser(newUser);
            DataManager.getInstance().setCurrentUser(newUser);
            switchToMainView(newUser);
        }
    }

    private void switchToMainView(User user) throws IOException {
        if (user.getRole() == UserRole.ADMIN) {
            Main.showAdminView();
        } else {
            Main.showUserView();
        }
    }
}