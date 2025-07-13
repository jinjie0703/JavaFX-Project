package com.wust.secondhand.controllers;

import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.models.Item;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SubmitItemController {
    @FXML private TextField nameField, quantityField, locationField, contactField;
    @FXML private TextArea descriptionArea;
    @FXML private Label imagePathLabel;
    @FXML private ImageView imageView;

    private File selectedImageFile;

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择物品图片");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        selectedImageFile = fileChooser.showOpenDialog(nameField.getScene().getWindow());

        if (selectedImageFile != null) {
            imagePathLabel.setText(selectedImageFile.getName());
            Image image = new Image(selectedImageFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void handleSubmit() {
        // 数据验证
        if (nameField.getText().isEmpty() || quantityField.getText().isEmpty()) {
            // ... 显示错误提示 ...
            return;
        }

        String imagePath = copyImageToStorage(selectedImageFile);

        Item newItem = new Item(
                nameField.getText(),
                Integer.parseInt(quantityField.getText()),
                descriptionArea.getText(),
                imagePath,
                locationField.getText(),
                contactField.getText(),
                DataManager.getInstance().getCurrentUser().getUsername()
        );

        DataManager.getInstance().addItem(newItem);

        // 关闭窗口
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private String copyImageToStorage(File imageFile) {
        if (imageFile == null) return "images/default.png"; // 如果未选择图片，返回一个默认图片路径

        try {
            Path targetDir = Paths.get("src/main/resources/com/wust/secondhand/images/");
            Files.createDirectories(targetDir); // 确保目录存在

            String newFileName = System.currentTimeMillis() + "_" + imageFile.getName();
            Path targetPath = targetDir.resolve(newFileName);

            Files.copy(imageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "images/" + newFileName; // 返回相对路径
        } catch (IOException e) {
            e.printStackTrace();
            return "images/default.png"; // 出错时也返回默认图片
        }
    }
}