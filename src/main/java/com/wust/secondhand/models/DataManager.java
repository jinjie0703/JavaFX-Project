package com.wust.secondhand.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wust.secondhand.models.enums.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataManager {
    private static DataManager instance = new DataManager();
    private static final String DATA_DIR = "src/main/resources/com/wust/secondhand/data/";
    private static final String ITEMS_FILE = DATA_DIR + "items.json";
    private static final String USERS_FILE = DATA_DIR + "users.json";

    private final ObservableList<Item> items;
    private final List<User> users;
    private User currentUser;

    private DataManager() {
        items = FXCollections.observableArrayList();
        users = new ArrayList<>();
        loadUsers();
        loadItems();
    }

    public static DataManager getInstance() { return instance; }

    // --- 用户管理 ---
    private void loadUsers() {
        // 如果用户文件不存在，创建一个默认的admin用户
        if (!Files.exists(Paths.get(USERS_FILE))) {
            users.add(new User("admin", "admin", UserRole.ADMIN));
            saveUsers();
            return;
        }
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            List<User> loadedUsers = new Gson().fromJson(reader, userListType);
            if (loadedUsers != null) {
                users.addAll(loadedUsers);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();
    }

    // --- 物品管理 ---
    private void loadItems() {
        try (FileReader reader = new FileReader(ITEMS_FILE)) {
            Type itemListType = new TypeToken<ArrayList<Item>>(){}.getType();
            List<Item> loadedItems = new Gson().fromJson(reader, itemListType);
            if (loadedItems != null) {
                items.addAll(loadedItems);
            }
        } catch (IOException e) { /* 文件可能尚不存在 */ }
    }

    public void saveItems() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            try (FileWriter writer = new FileWriter(ITEMS_FILE)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(items, writer);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public void addItem(Item item) {
        items.add(item);
        saveItems();
    }

    public void deleteItem(Item item) {
        items.remove(item);
        // 你也可以在这里添加删除关联图片文件的逻辑
        saveItems();
    }

    public void approveItem(Item item) {
        item.setStatus(com.wust.secondhand.models.enums.ItemStatus.APPROVED);
        // 更新列表以触发UI刷新
        int index = items.indexOf(item);
        if (index != -1) {
            items.set(index, item);
        }
        saveItems();
    }

    public ObservableList<Item> getItems() { return items; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
}