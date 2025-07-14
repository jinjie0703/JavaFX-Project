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
        if (nameField.getText().isBlank() || quantityField.getText().isBlank() ||
                locationField.getText().isBlank() || contactField.getText().isBlank()) {
            showAlert("错误", "所有字段均为必填项！");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("错误", "数量必须是一个有效的数字！");
            return;
        }

        // 调用我们修改后的 copyImageToStorage 方法
        String imagePath = copyImageToStorage(selectedImageFile);

        Item newItem = new Item(
                nameField.getText(),
                quantity,
                descriptionArea.getText(),
                imagePath,
                locationField.getText(),
                contactField.getText(),
                DataManager.getInstance().getCurrentUser().getUsername()
        );

        DataManager.getInstance().addItem(newItem);

        ((Stage) nameField.getScene().getWindow()).close();
    }

    /**
     * 【核心修改】将图片复制到以当前用户名命名的专属文件夹中。
     * @param imageFile 用户选择的图片文件
     * @return 保存到JSON中的相对路径，例如 "images/abc/12345.jpg"
     */
    private String copyImageToStorage(File imageFile) {
        if (imageFile == null) {
            return "";
        }

        try {
            // 1. 获取当前用户名
            String username = DataManager.getInstance().getCurrentUser().getUsername();

            // 2. 构建以用户名为基础的目标文件夹路径
            Path userImageDir = Paths.get("src/main/resources/com/wust/secondhand/images/" + username);

            // 3. 确保这个文件夹存在，如果不存在，则创建它（包括父目录）
            Files.createDirectories(userImageDir);

            // 4. 构建新的文件名和最终的目标路径
            String newFileName = System.currentTimeMillis() + "_" + imageFile.getName();
            Path targetPath = userImageDir.resolve(newFileName);

            // 5. 复制文件
            Files.copy(imageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 6. 返回包含了用户名文件夹的相对路径
            return "images/" + username + "/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            // 如果在复制过程中发生任何错误，仍然返回默认图片路径
            return "";
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}