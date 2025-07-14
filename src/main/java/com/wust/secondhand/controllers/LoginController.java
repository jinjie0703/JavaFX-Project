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

    private void switchToMainView(User user) throws IOException {
        if (user.getRole() == UserRole.ADMIN) {
            Main.showAdminView();
        } else {
            Main.showUserView();
        }
    }

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