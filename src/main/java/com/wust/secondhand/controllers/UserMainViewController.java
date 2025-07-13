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

public class UserMainViewController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, String> nameCol;
    @FXML private TableColumn<Item, String> descCol;
    @FXML private TableColumn<Item, String> contactCol;
    @FXML private TableColumn<Item, String> locationCol;

    private DataManager dataManager = DataManager.getInstance();

    @FXML
    public void initialize() {
        // 设置欢迎语
        welcomeLabel.setText("欢迎, " + dataManager.getCurrentUser().getUsername() + "!");

        // 设置表格列
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("tradeLocation"));

        // 只显示审核通过的物品
        FilteredList<Item> approvedItems = new FilteredList<>(dataManager.getItems(), p -> p.getStatus() == ItemStatus.APPROVED);
        itemsTable.setItems(approvedItems);

        // 设置双击查看详情
        itemsTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Item rowData = row.getItem();
                    showItemDetails(rowData);
                }
            });
            return row;
        });
    }

    @FXML
    protected void handleSubmitNewItem() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/wust/secondhand/fxml/SubmitItemView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("发布我的二手物品");
            stage.initModality(Modality.APPLICATION_MODAL); // 阻塞父窗口
            stage.setScene(scene);
            stage.showAndWait(); // 等待该窗口关闭

            // 刷新表格，虽然FilteredList会自动更新，但强制刷新可以确保万无一失
            itemsTable.refresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}