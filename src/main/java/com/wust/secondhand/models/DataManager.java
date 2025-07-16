package com.wust.secondhand.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wust.secondhand.models.enums.ItemStatus;
import com.wust.secondhand.models.enums.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataManager {
    private static final DataManager instance = new DataManager();
    private static final String DATA_DIR = "src/main/resources/com/wust/secondhand/data/";
    private static final String ITEMS_FILE = DATA_DIR + "items.json";
    private static final String USERS_FILE = DATA_DIR + "users.json";

    private final ObservableList<Item> items;
    private final List<User> users;
    private User currentUser;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private DataManager() {
        items = FXCollections.observableArrayList(item ->
                new javafx.beans.Observable[]{ item.statusProperty() }
        );

        users = new ArrayList<>();
        loadUsers();
        loadItems();
    }

    public static DataManager getInstance() { return instance; }

    /** 初始化数据管理器，加载用户和物品数据。
     * 如果用户文件不存在，则创建一个默认的管理员用户。
     * 物品数据从 JSON 文件加载，并确保每个物品的状态正确映射到枚举。
     */
    private void loadUsers() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            if (!Files.exists(Paths.get(USERS_FILE))) {
                users.add(new User("admin", "admin", UserRole.ADMIN));
                saveUsers();
                return;
            }
            try (FileReader reader = new FileReader(USERS_FILE)) {
                Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
                List<User> loadedUsers = gson.fromJson(reader, userListType);
                if (loadedUsers != null) {
                    users.addAll(loadedUsers);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** 保存用户数据到 JSON 文件。
     * 使用 Gson 库将用户列表序列化为 JSON 格式并写入文件。
     * 如果发生 IO 异常，则打印错误信息。
     */
    private void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** 加载物品数据从 JSON 文件。
     * 使用 Gson 库将 JSON 文件反序列化为物品列表，并添加到 ObservableList 中。
     * 确保每个物品的状态字段正确映射到枚举类型，如果状态为 null，则设置为默认值 PENDING。
     * 如果发生 IO 异常，则打印错误信息。
     */
    private void loadItems() {
        try (FileReader reader = new FileReader(ITEMS_FILE)) {
            Type itemListType = new TypeToken<ArrayList<Item>>(){}.getType();
            List<Item> loadedItems = gson.fromJson(reader, itemListType);
            if (loadedItems != null) {
                loadedItems.forEach(item -> {
                    // 确保 status 字段正确映射到枚举
                    if (item.getStatus() == null) {
                        item.setStatus(ItemStatus.PENDING); // 默认值
                    }
                    item.statusProperty(); // 确保初始化 statusProperty
                });
                items.addAll(loadedItems);
                System.out.println("加载的物品数据: " + loadedItems); // 调试输出
            }
        } catch (IOException e) {
            System.err.println("加载物品数据时出错: " + e.getMessage()); // 错误日志
        }
    }

    /** 重新加载物品数据。
     * 清空当前物品列表，并从文件中重新加载物品数据。
     * 使用 JavaFX 的 Platform.runLater 确保在 JavaFX 应用线程上更新界面。
     */
    public void reloadData() {
        // 清空并重新加载物品数据
        items.clear();
        loadItems();

        // 确保在 JavaFX 应用线程上更新界面
        javafx.application.Platform.runLater(() -> {
            System.out.println("数据已从文件重新加载！");
        });
    }

    /** 保存物品数据到 JSON 文件。
     * 使用 Gson 库将 ObservableList 中的物品列表序列化为 JSON 格式并写入文件。
     * 如果发生 IO 异常，则打印错误信息。
     */
    public void saveItems() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            try (FileWriter writer = new FileWriter(ITEMS_FILE)) {
                gson.toJson(items, writer);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** 添加新用户到用户列表。
     * 将新用户添加到用户列表中，并调用 saveUsers() 方法保存到文件。
     * @param user 要添加的用户对象
     */
    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    /** 验证用户登录信息。
     * 根据用户名和密码在用户列表中查找匹配的用户。
     * 如果找到匹配的用户，则返回 Optional 包含该用户，否则返回空 Optional。
     * @param username 用户名
     * @param password 密码
     * @return Optional<User> 包含匹配的用户或空
     */
    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();
    }

    /** 检查用户名是否已存在。
     * 在用户列表中查找是否有用户的用户名与给定的用户名匹配。
     * @param username 要检查的用户名
     * @return true 如果用户存在，false 如果不存在
     */
    public boolean userExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    /** 添加新物品到物品列表。
     * 将新物品添加到 ObservableList 中，并调用 saveItems() 方法保存到文件。
     * @param item 要添加的物品对象
     */
    public void addItem(Item item) {
        items.add(item);
        saveItems();
    }

    /** 删除物品及其关联的图片文件。
     * 从物品列表中删除指定的物品，并尝试删除与该物品关联的图片文件。
     * 如果图片路径不为空且文件存在，则删除该文件。
     * @param item 要删除的物品对象
     */
    public void deleteItem(Item item) {
        String imagePathString = item.getImagePath();
        if (imagePathString != null && !imagePathString.isEmpty()) {
            try {
                java.nio.file.Path imageFileToDelete = java.nio.file.Paths.get("src/main/resources/com/wust/secondhand/" + imagePathString);
                java.nio.file.Files.deleteIfExists(imageFileToDelete);
                System.out.println("成功删除了关联图片: " + imageFileToDelete);
            } catch (Exception e) {
                System.err.println("删除图片文件时���错: " + imagePathString);
                e.printStackTrace();
            }
        }
        items.remove(item);
        saveItems();
    }

    /** 审核物品，将其状态设置为 APPROVED。
     * 更新物品的状态为 APPROVED，并调用 saveItems() 方法保存到文件。
     * @param item 要审核的物品对象
     */
    public void approveItem(Item item) {
        item.setStatus(ItemStatus.APPROVED);
        saveItems(); // 审核后立即保存到文件
    }

    /** 获取当前用户的角色。
     * 如果当前用户为 null，则返回 UserRole.GUEST。
     * @return 当前用户的角色
     */
    public ObservableList<Item> getApprovedItems() {
        return items.filtered(item -> item.getStatus() == ItemStatus.APPROVED);
    }

    /** 获取当前用户的角色。
     * 如果当前用户为 null，则返回 UserRole.GUEST。
     * @return 当前用户的角色
     */
    public ObservableList<Item> getItemsByUser(String username) {
        return items.filtered(item -> item.getOwner().equals(username));
    }

    /** 获取所有待审核的物品。
     * 返回一个 ObservableList，其中包含所有状态为 PENDING 的物品。
     * @return 待审核的物品列表
     */
    public ObservableList<Item> getItems() { return items; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
}