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
import java.util.logging.Logger;

// 这个类用于处理提交新物品的界面逻辑
public class SubmitItemController {
    @FXML private TextField nameField, quantityField, locationField, contactField;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView imagePreview;
    @FXML private ChoiceBox<String> tradeTypeChoiceBox;
    @FXML private ChoiceBox<String> campusChoiceBox;

    private File selectedImageFile;
    private static final Logger logger = Logger.getLogger(SubmitItemController.class.getName());

    /** 该方法实现了选择图片文件的功能：
     1创建文件选择对话框，设置标题和图片格式过滤器；
     2显示对话框并获取用户选择的文件；
     3若选择成功，更新界面显示文件名和图片预览。
    */
    @FXML
    public void initialize() {
        // 可选：确保默认值已设置，如果 FXML 中未处理
        // 例如，如果你希望“出售”和“黄家湖校区”在加载时被明确设置：
        // if (dealChoiceBox.getValue() == null) {
        //     dealChoiceBox.setValue("出售");
        // }
        // if (campusChoiceBox.getValue() == null) {
        //     campusChoiceBox.setValue("黄家湖校区");
        // }
    }
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择物品图片");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        selectedImageFile = fileChooser.showOpenDialog(nameField.getScene().getWindow());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    /** 该方法处理提交按钮的点击事件：
     1检查表单中名称、数量、位置、联系方式是否为空，若为空则弹出错误提示。
     2尝试将数量字段转换为整数，若失败则提示“数量必须是一个有效的数字”。
     3调用 copyImageToStorage() 方法将选中的图片复制到用户专属文件夹，并获取保存路径。
     4使用表单数据和当前用户名创建一个新的 Item 对象。
     5将新商品添加到数据管理器中。
     6关闭当前窗口。
    */
    @FXML
    private void handleSubmit() {
        if (nameField.getText().isBlank() || quantityField.getText().isBlank() ||
                locationField.getText().isBlank() || contactField.getText().isBlank()) {
            showAlert("所有字段均为必填项！");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("数量必须是一个有效的数字！");
            return;
        }
        String dealType = tradeTypeChoiceBox.getValue();
        String campus = campusChoiceBox.getValue();

        String imagePath = copyImageToStorage(selectedImageFile);

        Item newItem = new Item(
                nameField.getText(),
                quantity,
                descriptionArea.getText(),
                imagePath,
                locationField.getText(),
                contactField.getText(),
                DataManager.getInstance().getCurrentUser().getUsername(),
                dealType,
                campus
        );

        DataManager.getInstance().addItem(newItem);

        ((Stage) nameField.getScene().getWindow()).close();
    }

    /** 该方法将选中的图片复制到用户专属的存储文件夹中：
     1获取当前用户名；
     2构建以用户名为基础的目标文件夹路径；
     3确保这个文件夹存在，如果不存在，则创建它（包括父目录）；
     4构建新的文件名和最终的目标路径；
     5复制文件到目标路径；
     6返回包含了用户名文件夹的相对路径。
     如果在复制过程中发生任何错误，仍然返回默认图片路径。
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
            logger.severe("Error occurred: " + e.getMessage());
            return "";
        }
    }

    /** 该方法用于显示错误提示框：
     1创建一个新的 Alert 对象，设置类型为 ERROR；
     2设置标题、头部文本和内容文本；
     3显示并等待用户关闭提示框。
    */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}