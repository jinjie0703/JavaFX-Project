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
                loadedItems.forEach(Item::statusProperty);
                items.addAll(loadedItems);
            }
        } catch (IOException e) { /* 文件可能尚不存在 */ }
    }

    public void reloadData() {
        // 在重新加载前，确保清空操作在JavaFX应用线程上执行
        javafx.application.Platform.runLater(() -> {
            items.clear();
            // 注意：我们通常不需要清空users列表，因为它在程序生命周期内一般不应从外部改变
            // users.clear();
        });

        // 从�����件重新加载物品数据
        // 因为loadItems会先清空再添加，所以上面的clear()主要是为了视觉上的即时响应
        loadItems();

        System.out.println("数据已从文件重新加载！");
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
        if (imagePathString != null && !imagePathString.isEmpty() && !imagePathString.equals("images/default.png")) {
            try {
                java.nio.file.Path imageFileToDelete = java.nio.file.Paths.get("src/main/resources/com/wust/secondhand/" + imagePathString);
                java.nio.file.Files.deleteIfExists(imageFileToDelete);
                System.out.println("成功删除了关联图片: " + imageFileToDelete);
            } catch (Exception e) {
                System.err.println("删除图片文件时出错: " + imagePathString);
                e.printStackTrace();
            }
        }
        items.remove(item);
        saveItems();
    }

    public void approveItem(Item item) {
        item.setStatus(ItemStatus.APPROVED);
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