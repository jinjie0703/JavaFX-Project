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
        // 这两个方法的调用需要它们真实存在
        loadUsers();
        loadItems();
    }

    public static DataManager getInstance() { return instance; }

    // =================================================================
    // ↓↓↓↓↓ 下面就是您之前缺失的方法，现在已经全部补全 ↓↓↓↓↓
    // =================================================================

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

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

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

    public void reloadData() {
        // 清空并重新加载物品数据
        items.clear();
        loadItems();

        // 确保在 JavaFX 应用线程上更新界面
        javafx.application.Platform.runLater(() -> {
            System.out.println("数据已从文件重新加载！");
        });
    }

    public void saveItems() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            try (FileWriter writer = new FileWriter(ITEMS_FILE)) {
                gson.toJson(items, writer);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();
    }

    public boolean userExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public void addItem(Item item) {
        items.add(item);
        saveItems();
    }

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

    public void approveItem(Item item) {
        item.setStatus(ItemStatus.APPROVED);
        saveItems(); // 审核后立即保存到文件
    }

    public ObservableList<Item> getApprovedItems() {
        return items.filtered(item -> item.getStatus() == ItemStatus.APPROVED);
    }

    public ObservableList<Item> getItemsByUser(String username) {
        return items.filtered(item -> item.getOwner().equals(username));
    }

    // --- Getters and Setters ---
    public ObservableList<Item> getItems() { return items; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
}