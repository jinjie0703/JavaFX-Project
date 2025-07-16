package com.wust.secondhand.controllers;

import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.models.Item;
import com.wust.secondhand.models.enums.ItemStatus;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdminMainViewController {

    @FXML private TableView<Item> pendingTable;
    @FXML private TableColumn<Item, String> pendingNameCol;
    @FXML private TableColumn<Item, String> pendingOwnerCol;

    @FXML private TableView<Item> approvedTable;
    @FXML private TableColumn<Item, String> approvedNameCol;
    @FXML private TableColumn<Item, String> approvedOwnerCol;

    @FXML private GridPane detailsPane;
    @FXML private Label idLabel, nameLabel, quantityLabel, contactLabel;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView imageView;
    @FXML private Label dealLabel;
    @FXML private Label campusLabel;
    @FXML private Label locationLabel; // 新增：交易地点字段

    private final DataManager dataManager = DataManager.getInstance();

    // 方法用于初始化管理员界面的表格和事件监听器
    // 1配置“待审核”表格列，绑定name和owner属性，并显示状态为PENDING的商品。
    // 2配置“已上架”表格列，同样绑定属性，并显示状态为APPROVED的商品。
    // 3为两个表格添加选中项变化监听器，当选中某商品时显示其详细信息。
    // 4设置鼠标点击事件：点击一个表格时自动清除另一个表格的选中项。
    // 5初始隐藏商品详情面板。
    @FXML
    public void initialize() {
        // 配置待审核列表
        pendingNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        pendingOwnerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        // FilteredList 会自动监听源列表（和其内部属性）的变化
        pendingTable.setItems(dataManager.getItems().filtered(p -> p.getStatus() == ItemStatus.PENDING));

        // 配置已上架列表
        approvedNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        approvedOwnerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        approvedTable.setItems(dataManager.getItems().filtered(p -> p.getStatus() == ItemStatus.APPROVED));

        // 为两个表格添加选择监听器
        addSelectionListener(pendingTable);
        addSelectionListener(approvedTable);

        // 点击某个表格时，清空另一个表格的选择
        pendingTable.setOnMouseClicked(event -> approvedTable.getSelectionModel().clearSelection());
        approvedTable.setOnMouseClicked(event -> pendingTable.getSelectionModel().clearSelection());

        // 初始状态下，隐藏详情面板
        detailsPane.setVisible(false);
    }

    // 处理手动刷新数据的按钮点击事件
    // 1打印刷新日志；
    // 2调用 dataManager.reloadData() 从文件重新加载所有商品数据；
    // 3更新“待审批”和“已批准”表格，分别显示对应状态的商品；
    // 4弹出提示框告知用户刷新成功。
    @FXML
    private void handleRefreshData() {
        System.out.println("手动刷新数据...");
        dataManager.reloadData();

        pendingTable.setItems(dataManager.getItems().filtered(p -> p.getStatus() == ItemStatus.PENDING));
        approvedTable.setItems(dataManager.getItems().filtered(p -> p.getStatus() == ItemStatus.APPROVED));

        showAlert(Alert.AlertType.INFORMATION, "刷新成功", "数据已从文件重新加载！");
    }

    // 添加表格选择监听器
    // 1获取表格的选择模型；
    // 2监听选中项的变化；
    // 3若新选中项不为空，则展示该项的详细信息。
    private void addSelectionListener(TableView<Item> table) {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showItemDetails(newSelection);
            }
        });
    }

    // 显示选中商品的详细信息
    // 1显示详情面板（detailsPane.setVisible(true)）；
    // 2设置各标签和文本区域的内容（ID、名称、数量、联系方式、描述）；
    // 3加载并显示图片：构造资源路径，尝试获取输入流并设置图像，若失败则清空图片并输出错误信息
    private void showItemDetails(Item item) {
        detailsPane.setVisible(true);
        idLabel.setText(item.getId());
        nameLabel.setText(item.getName());
        quantityLabel.setText(String.valueOf(item.getQuantity()));
        contactLabel.setText(item.getContact());
        descriptionArea.setText(item.getDescription());
        dealLabel.setText(item.getTradeType() != null ? item.getTradeType() : "出售");
        campusLabel.setText(item.getCampus() != null ? item.getCampus() : "黄家湖校区");
        locationLabel.setText(item.getTradeLocation() != null ? item.getTradeLocation() : "未指定"); // 修正方法调用为 getTradeLocation()
        try {
            System.out.println("图片路径: " + item.getImagePath()); // 调试日志
            String absolutePath = "src/main/resources/com/wust/secondhand/" + item.getImagePath();
            File imageFile = new File(absolutePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                System.err.println("图片文件未找到: " + absolutePath);
                imageView.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("加载图片时出错: " + e.getMessage());
            imageView.setImage(null);
        }
    }

    // 处理审核按钮点击事件
    // 1从“待审核”表格中获取用户选中的商品（Item）；
    // 2如果选中了商品，则调用 dataManager.approveItem(selectedItem) 方法审核该商品；
    // 3如果未选中任何商品，则弹出警告提示用户先选择一个物品。
    @FXML
    private void handleApprove() {
        Item selectedItem = pendingTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dataManager.approveItem(selectedItem);
        } else {
            showAlert(Alert.AlertType.WARNING, "操作提示", "请先在“待审核”列表中选择一个物品。");
        }
    }

    // 处理删除按钮点击事件
    // 1从“已上架”表格中获取用户选中的物品；
    // 2如果有选中物品，则调用 dataManager.deleteItem(selectedItem) 删除该物品，并清空详情面板；
    // 3如果未选中任何物品，则弹出提示框，提醒用户先选择一个物品
    @FXML
    private void handleDelete() {
        Item selectedItem = approvedTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dataManager.deleteItem(selectedItem);
            clearDetailsPane();
        } else {
            showAlert(Alert.AlertType.WARNING, "操作提示", "请先在“已上架”列表中选择一个物品。");
        }
    }

    // 处理注销按钮点击事件
    // 1获取当前界面的窗口（Stage）并关闭它；
    // 2调用 Main.showLoginView() 重新显示登录窗口；
    // 3如果跳转过程中发生异常，捕获并弹出错误提示框。
    @FXML
    private void handleLogout() {
        try {
            // 获取当前按钮所在的窗口 (Stage)
            Stage currentStage = (Stage) pendingTable.getScene().getWindow();
            // 关闭当前窗口
            currentStage.close();
            // 调用 Main 类中的静态方法，重新显示登录窗口
            com.wust.secondhand.Main.showLoginView();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "返回登录页面时发生错误！");
        }
    }

    // 清除详情面板的内容
    // 1隐藏 detailsPane（详情信息区域）；
    // 2将各个标签文本设置为 "..." 表示无内容；
    // 3清空描述文本框 descriptionArea；
    // 4移除图片视图 imageView 中的图片。
    private void clearDetailsPane() {
        detailsPane.setVisible(false);
        idLabel.setText("...");
        nameLabel.setText("...");
        quantityLabel.setText("...");
        contactLabel.setText("...");
        descriptionArea.clear();
        imageView.setImage(null);
        dealLabel.setText("..."); // 清空交易类型
        campusLabel.setText("..."); // 清空校区
        locationLabel.setText("..."); // 新增：清空交易地点
    }

    // 显示提示框
    // 1创建指定类型的 Alert 对话框（如信息、警告等）；
    // 2设置对话框标题和内容消息；
    // 3不显示 header text；
    // 4弹出对话框并等待用户响应。
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}