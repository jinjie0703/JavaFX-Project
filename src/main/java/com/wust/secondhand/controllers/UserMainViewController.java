package com.wust.secondhand.controllers;

import com.wust.secondhand.models.DataManager;
import com.wust.secondhand.models.Item;
import com.wust.secondhand.models.enums.ItemStatus;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class UserMainViewController {

    // --- 通用组件 ---
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;

    // --- “浏览市场” Tab 组件 ---
    @FXML private TableView<Item> marketItemsTable;
    @FXML private TableColumn<Item, String> marketNameCol;
    @FXML private TableColumn<Item, String> marketDescCol;
    @FXML private TableColumn<Item, String> marketContactCol;
    @FXML private TableColumn<Item, String> marketOwnerCol;

    // --- “我的发布” Tab 组件 ---
    @FXML private TableView<Item> myItemsTable;
    @FXML private TableColumn<Item, String> myNameCol;
    @FXML private TableColumn<Item, ItemStatus> myStatusCol;
    @FXML private TableColumn<Item, String> myDescCol;

    private final DataManager dataManager = DataManager.getInstance();
    private String currentUsername;
    private FilteredList<Item> filteredMarketItems; // 用于搜索的过滤列表

    @FXML
    public void initialize() {
        // 1. 获取当前用户名
        this.currentUsername = dataManager.getCurrentUser().getUsername();
        welcomeLabel.setText("欢迎, " + currentUsername + "!");

        // 2. 配置“浏览市场”的表格和搜索逻辑
        setupMarketTable();

        // 3. 配置“我的发布”的表格
        setupMyItemsTable();

        // 4. 为两个表格都添加双击查看详情的功能
        addDoubleClickDetailsListener(marketItemsTable);
        addDoubleClickDetailsListener(myItemsTable);
    }

    /**
     * 配置“浏览市场”的表格
     */
    private void setupMarketTable() {
        marketNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        marketDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        marketContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        marketOwnerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));

        // 创建一个只包含“已上架”物品的过滤列表
        filteredMarketItems = new FilteredList<>(dataManager.getItems(), p -> p.getStatus() == ItemStatus.APPROVED);
        marketItemsTable.setItems(filteredMarketItems);
    }

    /**
     * 配置“我的发布”的表格
     */
    private void setupMyItemsTable() {
        myNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        myStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        myDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // 创建一个只包含当前用户发布的物品的过滤列表
        FilteredList<Item> myItems = new FilteredList<>(dataManager.getItems(), p -> p.getOwner().equals(currentUsername));
        myItemsTable.setItems(myItems);
    }

    /**
     * 核心功能：处理搜索
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            // 如果关键字为空，重置过滤器，显示所有已上架物品
            filteredMarketItems.setPredicate(p -> p.getStatus() == ItemStatus.APPROVED);
            return;
        }

        // 设置新的过滤条件
        filteredMarketItems.setPredicate(item ->
                item.getStatus() == ItemStatus.APPROVED && (
                        item.getName().toLowerCase().contains(keyword) ||
                                item.getDescription().toLowerCase().contains(keyword) ||
                                item.getContact().toLowerCase().contains(keyword)
                )
        );
    }

    /**
     * 清空搜索框并重置列表
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        filteredMarketItems.setPredicate(p -> p.getStatus() == ItemStatus.APPROVED);
    }

    /**
     * 核心功能：删除用户自己的物品
     */
    @FXML
    private void handleDeleteMyItem() {
        Item selectedItem = myItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "操作提示", "请先在“我的发布”列表中选择一个您想删除的物品。");
            return;
        }

        // 增加一个确认对话框，防止误删
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("确认删除");
        confirmation.setHeaderText("您确定要删除物品“" + selectedItem.getName() + "”吗？");
        confirmation.setContentText("此操作将永久删除该物品的记录和图片，通常在交易完成后进行。");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 用户点击了“确定”
            dataManager.deleteItem(selectedItem);
            // 因为列表是响应式的，界面会自动更新，我们不需要做任何事！
        }
    }

    // --- 下面是辅助方法，与之前类似 ---

    @FXML
    protected void handleSubmitNewItem() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/wust/secondhand/fxml/SubmitItemView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("发布我的二手物品");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addDoubleClickDetailsListener(TableView<Item> table) {
        table.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showItemDetails(row.getItem());
                }
            });
            return row;
        });
    }

    private void showItemDetails(Item item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("物品详情");
        alert.setHeaderText(item.getName() + " (由 " + item.getOwner() + " 发布)");
        String content = "描述: " + item.getDescription() + "\n" +
                "数量: " + item.getQuantity() + "\n" +
                "交易地点: " + item.getTradeLocation() + "\n" +
                "联系方式: " + item.getContact() + "\n" +
                "图片路径: " + item.getImagePath();
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}