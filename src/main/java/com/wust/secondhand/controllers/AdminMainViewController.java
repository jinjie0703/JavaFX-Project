package com.wust.secondhand.controllers;

import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.models.Item;
import com.wust.secondhand.models.enums.ItemStatus;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AdminMainViewController {

    // --- 表格组件 ---
    @FXML private TableView<Item> pendingTable;
    @FXML private TableColumn<Item, String> pendingNameCol;
    @FXML private TableColumn<Item, String> pendingOwnerCol;

    @FXML private TableView<Item> approvedTable;
    @FXML private TableColumn<Item, String> approvedNameCol;
    @FXML private TableColumn<Item, String> approvedOwnerCol;

    // --- 新增的详情区域组件 ---
    @FXML private GridPane detailsPane;
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label quantityLabel;
    @FXML private Label contactLabel;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView imageView;

    private final DataManager dataManager = DataManager.getInstance();

    @FXML
    public void initialize() {
        // 1. 配置表格
        setupTables();

        // 2. 为两个表格添加选择监听器
        addSelectionListener(pendingTable);
        addSelectionListener(approvedTable);

        // 3. 为了更好的用户体验，当点击一个表格时，清空另一个表格的选择
        pendingTable.setOnMouseClicked(event -> approvedTable.getSelectionModel().clearSelection());
        approvedTable.setOnMouseClicked(event -> pendingTable.getSelectionModel().clearSelection());

        // 4. 初始状态下，隐藏详情面板
        detailsPane.setVisible(false);
    }

    private void setupTables() {
        // 配置待审核列表
        pendingNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        pendingOwnerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        FilteredList<Item> pendingItems = new FilteredList<>(dataManager.getItems(), p -> p.getStatus() == ItemStatus.PENDING);
        pendingTable.setItems(pendingItems);

        // 配置已上架列表
        approvedNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        approvedOwnerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        FilteredList<Item> approvedItems = new FilteredList<>(dataManager.getItems(), p -> p.getStatus() == ItemStatus.APPROVED);
        approvedTable.setItems(approvedItems);

        // 刷新表格以响应数据模型的变化
        dataManager.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Item> c) -> {
            pendingTable.refresh();
            approvedTable.refresh();
        });
    }

    /**
     * 为表格添加监听器，当选择项改变时，调用 showItemDetails 方法
     */
    private void addSelectionListener(TableView<Item> table) {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showItemDetails(newSelection);
            }
        });
    }

    /**
     * 将选中 Item 的数据显示在右侧的详情面板上
     */
    private void showItemDetails(Item item) {
        detailsPane.setVisible(true); // 显示详情面板

        idLabel.setText(item.getId());
        nameLabel.setText(item.getName());
        quantityLabel.setText(String.valueOf(item.getQuantity()));
        contactLabel.setText(item.getContact());
        descriptionArea.setText(item.getDescription());

        // --- 核心改动：使用资源流的方式加载图片 ---
        try {
            // getResourceAsStream 会从编译后的 classpath 中寻找资源
            // 路径必须以'/'开头，代表从 classpath 的根目录开始查找
            String resourcePath = "/com/wust/secondhand/" + item.getImagePath();
            java.io.InputStream stream = getClass().getResourceAsStream(resourcePath);

            if (stream != null) {
                Image image = new Image(stream);
                imageView.setImage(image);
            } else {
                // 如果资源流为null，说明在classpath中找不到该文件
                imageView.setImage(null);
                System.err.println("图片资源未找到: " + resourcePath);
            }
        } catch (Exception e) {
            // 捕获其他可能的异常
            imageView.setImage(null);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApprove() {
        Item selectedItem = pendingTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dataManager.approveItem(selectedItem);
            clearDetailsPane(); // 审核后清空详情
        } else {
            showAlert("请先在“待审核”列表中选择一个物品。");
        }
    }

    @FXML
    private void handleDelete() {
        // 现在可以从任一表格删除，但我们的按钮只放在了已上架列表下
        Item selectedItem = approvedTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dataManager.deleteItem(selectedItem);
            clearDetailsPane(); // 删除后清空详情
        } else {
            showAlert("请先在“已上架”列表中选择一个物品。");
        }
    }

    /**
     * 清空详情面板并隐藏
     */
    private void clearDetailsPane() {
        detailsPane.setVisible(false);
        idLabel.setText("...");
        nameLabel.setText("...");
        quantityLabel.setText("...");
        contactLabel.setText("...");
        descriptionArea.clear();
        imageView.setImage(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("操作提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}