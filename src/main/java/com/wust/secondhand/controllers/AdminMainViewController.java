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

    private final DataManager dataManager = DataManager.getInstance();

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

        // 点击一个表格时，清空另一个表格的选择
        pendingTable.setOnMouseClicked(event -> approvedTable.getSelectionModel().clearSelection());
        approvedTable.setOnMouseClicked(event -> pendingTable.getSelectionModel().clearSelection());

        // 初始状态下，隐藏详情面板
        detailsPane.setVisible(false);
    }
    @FXML
    private void handleRefreshData() {
        System.out.println("手动刷新数据...");
        dataManager.reloadData();
        showAlert(Alert.AlertType.INFORMATION, "刷新成功", "数据已从文件重新加载！");
    }

    // 其他方法（addSelectionListener, showItemDetails, handleApprove, handleDelete,等）保持不变...
    // 【此处省略了其他方法的代码，它们不需要修改】

    private void addSelectionListener(TableView<Item> table) {
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showItemDetails(newSelection);
            }
        });
    }

    private void showItemDetails(Item item) {
        detailsPane.setVisible(true);
        idLabel.setText(item.getId());
        nameLabel.setText(item.getName());
        quantityLabel.setText(String.valueOf(item.getQuantity()));
        contactLabel.setText(item.getContact());
        descriptionArea.setText(item.getDescription());
        try {
            String resourcePath = "/com/wust/secondhand/" + item.getImagePath();
            java.io.InputStream stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                imageView.setImage(new Image(stream));
            } else {
                imageView.setImage(null);
                System.err.println("图片资源未找到: " + resourcePath);
            }
        } catch (Exception e) {
            imageView.setImage(null);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApprove() {
        Item selectedItem = pendingTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dataManager.approveItem(selectedItem);
        } else {
            // ↓↓↓ 修改这里 ↓↓↓
            showAlert(Alert.AlertType.WARNING, "操作提示", "请先在“待审核”列表中选择一个物品。");
        }
    }

    @FXML
    private void handleDelete() {
        Item selectedItem = approvedTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            dataManager.deleteItem(selectedItem);
            clearDetailsPane();
        } else {
            // ↓↓↓ 修改这里 ↓↓↓
            showAlert(Alert.AlertType.WARNING, "操作提示", "请先在“已上架”列表中选择一个物品。");
        }
    }

    private void clearDetailsPane() {
        detailsPane.setVisible(false);
        idLabel.setText("...");
        nameLabel.setText("...");
        quantityLabel.setText("...");
        contactLabel.setText("...");
        descriptionArea.clear();
        imageView.setImage(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // 我们通常不使用 header text
        alert.setContentText(message);
        alert.showAndWait();
    }
}