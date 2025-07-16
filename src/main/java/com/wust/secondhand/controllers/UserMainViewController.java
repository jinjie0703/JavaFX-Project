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
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

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
    @FXML private TableColumn<Item, String> marketDelCol;
    @FXML private TableColumn<Item, String> marketCampusCol;

    // --- “我的发布” Tab 组件 ---
    @FXML private TableView<Item> myItemsTable;
    @FXML private TableColumn<Item, String> myNameCol;
    @FXML private TableColumn<Item, ItemStatus> myStatusCol;
    @FXML private TableColumn<Item, String> myDescCol;
    @FXML private TableColumn<Item, String> myDealCol;
    @FXML private TableColumn<Item, String> myCampusCol;


    // --- 详情区域组件 ---
    @FXML private Label detailsNameLabel;
    @FXML private Label detailsDescLabel;
    @FXML private Label detailsContactLabel;
    @FXML private Label detailsOwnerLabel;
    @FXML private Label detailsStatusLabel;
    // 新增属性
    @FXML private Label detailsIdLabel;
    @FXML private Label detailsQuantityLabel;
    @FXML private Label detailsLocationLabel;
    @FXML private ImageView detailsImageView;
    @FXML private Label detailsTradeTypeLabel;
    @FXML private Label detailsCampusLabel;

    private final DataManager dataManager = DataManager.getInstance();
    private String currentUsername;

    // 日志记录器
    private static final Logger logger = Logger.getLogger(UserMainViewController.class.getName());

    /** 方法用于初始化用户主界面，其功能如下：
     1获取当前登录用户名并显示欢迎信息；
     2配置“浏览市场”和“我的发布”两个表格的数据显示；
     3为两个表格添加选中项监听器，当选中某条数据时自动在右侧详情区域展示该数据内容。
     */
    @FXML
    public void initialize() {
        // 1. ��取当前用户名
        this.currentUsername = dataManager.getCurrentUser().getUsername();
        welcomeLabel.setText("欢迎, " + currentUsername + "!");

        // 2. 配置“浏览市场”的表格和搜索逻辑
        setupMarketTable();

        // 3. 配置“我的发布”的表格
        setupMyItemsTable();

        // 4. 为两个表格都添加选中项变化监听器，自动更新右侧详情栏
        marketItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showDetails(newVal));
        myItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showDetails(newVal));
    }

    /** 该方法用于配置“浏览市场”表格，功能如下：
     1将表格各列绑定到Item类的对应属性（名称、描述、联系方式、发布者）。
     2从数据管理器中获取所有商品，并过滤出状态为APPROVED（已上架）的商品展示在表格中。
     */
    private void setupMarketTable() {
        marketNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        marketDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        marketContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        marketOwnerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));
        marketDelCol.setCellValueFactory(new PropertyValueFactory<>("tradeType"));
        marketCampusCol.setCellValueFactory(new PropertyValueFactory<>("campus"));

        // 创建一个只包含“已上架”物品的过滤列表
        FilteredList<Item> filteredMarketItems = new FilteredList<>(dataManager.getItems(), p -> p.getStatus() == ItemStatus.APPROVED);
        marketItemsTable.setItems(filteredMarketItems);
    }

    /** 该方法用于配置“我的发布”表格，功能如下：
     1为表格的列设置数据绑定，分别显示物品名称、状态和描述；
     2使用 FilteredList 过滤出当前用户发布的商品（根据 owner 判断）；
     3将过滤后的列表设置为表格的数据源。
     */
    private void setupMyItemsTable() {
        myNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        myStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        myDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        myDealCol.setCellValueFactory(new PropertyValueFactory<>("tradeType"));
        myCampusCol.setCellValueFactory(new PropertyValueFactory<>("campus"));

        // 创建一个只包含当前用户发布的物品的过滤列表
        FilteredList<Item> myItems = new FilteredList<>(dataManager.getItems(), p -> p.getOwner().equals(currentUsername));
        myItemsTable.setItems(myItems);
    }

    /** 用户在搜索框输入关键字后，对“浏览市场”和“我的发布”两个表格中的商品信���进行过滤显示的功能。
     1获取用户输入的搜索关键词并转为小写；
     2如果关键词为空，则显示所有已批准的商品和当前用户的发布商品；
     3否则根据关键词对商品名称、描述或联系方式进行匹配过滤，并更新表格内容。
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            marketItemsTable.setItems(dataManager.getApprovedItems());
            myItemsTable.setItems(dataManager.getItemsByUser(currentUsername));
            return;
        }

        marketItemsTable.setItems(dataManager.getApprovedItems().filtered(item ->
                item.getName().toLowerCase().contains(keyword) ||
                        item.getDescription().toLowerCase().contains(keyword) ||
                        item.getContact().toLowerCase().contains(keyword)
        ));

        myItemsTable.setItems(dataManager.getItemsByUser(currentUsername).filtered(item ->
                item.getName().toLowerCase().contains(keyword) ||
                        item.getDescription().toLowerCase().contains(keyword)
        ));
    }

    /** 该方法用于清空搜索框并重置两个表格视图的数据：
     1 searchField.clear();：清空搜索输入框的内容；
     2 marketItemsTable.setItems(dataManager.getApprovedItems());：将“浏览市场”表格的数据设置为所有已批准的商品；
     3 myItemsTable.setItems(dataManager.getItemsByUser(currentUsername));：将“我的发布”表格的数据设置为当前用户发布的商品。
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        marketItemsTable.setItems(dataManager.getApprovedItems());
        myItemsTable.setItems(dataManager.getItemsByUser(currentUsername));
    }

    /** 用户删除自己发布的物品功能：
     1从“我的发布”表格中获取选中的物品；
     2若未选择，弹出警告提示；
     3弹出确认对话框防止误删；
     4用户确认后，调用 dataManager.deleteItem(selectedItem) 删除该物品；
     5因使用响应式列表，删除后界面会自动更新
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

    /** 该方法用于处理用户点击“发布新物品”按钮的事件：
     1使用FXMLLoader加载SubmitItemView.fxml界面文件；
     2创建新的Scene并设置到新创建的Stage上；
     3设置窗口标题为“发布我的二手物品”，并设为应用模态（阻塞主窗口）；
     4显示注册窗口并等待用户操作完成。
     */
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
            logger.severe("Error occurred: " + e.getMessage());
        }
    }

    /** 该方法用于处理用户点击“注销”按钮的事件：
     1获取当前界面的窗口（Stage）并关闭它；
     2调用 Main.showLoginView() 重新显示登录窗口；
     3如果跳转过程中发生异常，捕获并弹出错误提示框。
     */
    @FXML
    private void handleLogout() {
        try {
            // 获取当前按钮所在的窗口 (Stage)
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            // 关闭当前窗口
            currentStage.close();

            // 调用 Main 类中的静态方法，重新显示登录窗口
            com.wust.secondhand.Main.showLoginView();
        } catch (IOException e) {
            logger.severe("Error occurred: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "错误", "返回登录页面时发生错误！");
        }
    }


    /** 该方法用于在界面右侧的详情区域显示传入的 Item
     * 对象的详细信息，包括ID、名称、数量、描述、联系方式、发布者、状态、交易地点以及图片路径
     * 若 item 为 null，则清空所有详情栏内容
     具体逻辑如下
     1参数判断：如果 item == null，则将所有标签文本设为空，图片设为 null
     2信息展示：否则，调用 item 的各个 getter 方法获取属性值，并设置到对应的 UI 标签中
     3图片加载
     4如果 imagePath 不为空，则拼���资源路径并尝试从类路径中读取图片
     5成功读取则显示图片，失败则设为 null，防止异常中断程序
     */
    private void showDetails(Item item) {
        if (item == null) {
            detailsIdLabel.setText("ID: ");
            detailsNameLabel.setText("名称: ");
            detailsQuantityLabel.setText("数量: ");
            detailsDescLabel.setText("描述: ");
            detailsContactLabel.setText("联系方式: ");
            detailsOwnerLabel.setText("发布者: ");
            detailsStatusLabel.setText("状态: ");
            detailsLocationLabel.setText("交易地点: ");
            detailsTradeTypeLabel.setText("交易类型: ");
            detailsCampusLabel.setText("校区: ");
            detailsImageView.setImage(null);
            return;
        }
        detailsIdLabel.setText("ID: " + item.getId());
        detailsNameLabel.setText("名称: " + item.getName());
        detailsQuantityLabel.setText("数量: " + item.getQuantity());
        detailsDescLabel.setText("描述: " + item.getDescription());
        detailsContactLabel.setText("联系方式: " + item.getContact());
        detailsOwnerLabel.setText("发布者: " + item.getOwner());
        detailsStatusLabel.setText("状态: " + item.getStatus().toString());
        detailsLocationLabel.setText("交易地点: " + item.getTradeLocation());
        detailsTradeTypeLabel.setText("交易类型: " + item.getTradeType());
        detailsCampusLabel.setText("校区: " + item.getCampus());
        // 图片显示逻辑
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            String resourcePath = "/com/wust/secondhand/" + item.getImagePath();
            try (java.io.InputStream stream = getClass().getResourceAsStream(resourcePath)) {
                if (stream != null) {
                    detailsImageView.setImage(new javafx.scene.image.Image(stream));
                } else {
                    detailsImageView.setImage(null);
                }
            } catch (Exception e) {
                detailsImageView.setImage(null);
            }
        } else {
            detailsImageView.setImage(null);
        }
    }

    /** 该方法用于显示一个警告或错误提示框，功能如下：
     1创建一个新的 Alert 对象，设置类型为 alertType（如警告或错误）；
     2设置标题为 title，内容文本为 message；
     3显示并等���用户点击确定按钮。
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
