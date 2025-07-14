package com.wust.secondhand.models;

import com.wust.secondhand.models.enums.ItemStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.UUID;

public class Item {
    // --- 这些字段都给Gson处理 ---
    private String id;
    private String name;
    private int quantity;
    private String description;
    private String imagePath;
    private String tradeLocation;
    private String contact;
    private String owner;
    private ItemStatus status; // 保留一个普通的status字段，专门用于JSON存取

    // --- 这个字段用 transient 关键字告诉Gson忽略它 ---
    private transient ObjectProperty<ItemStatus> statusProperty;

    /**
     * 这个无参构造函数是必需的，以便在反序列化后进行数据同步。
     */
    public Item() {}

    public Item(String name, int quantity, String description, String imagePath, String tradeLocation, String contact, String owner) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.imagePath = imagePath;
        this.tradeLocation = tradeLocation;
        this.contact = contact;
        this.owner = owner;
        this.status = ItemStatus.PENDING; // 初始化普通的status字段
        // 注意：这里我们不再初始化 statusProperty
    }

    /**
     * 关键方法：确保JavaFX属性在使用前被正确初始化。
     * 这个方法会在需要时（比如调用getStatus()或setStatus()）才进行初始化，这叫“懒加载”。
     */
    private void ensureStatusPropertyInitialized() {
        if (statusProperty == null) {
            statusProperty = new SimpleObjectProperty<>(this.status);

            // 关键步骤：添加监听器，当JavaFX属性变化时，自动更新那个普通的status字段
            statusProperty.addListener((obs, oldVal, newVal) -> {
                this.status = newVal;
            });
        }
    }

    // --- 修改后的 Getter 和 Setter ---
    public ItemStatus getStatus() {
        ensureStatusPropertyInitialized();
        return statusProperty.get();
    }

    public void setStatus(ItemStatus newStatus) {
        ensureStatusPropertyInitialized();
        this.statusProperty.set(newStatus);
    }

    public ObjectProperty<ItemStatus> statusProperty() {
        ensureStatusPropertyInitialized();
        return statusProperty;
    }

    // --- 其他字段的普通Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
    public String getTradeLocation() { return tradeLocation; }
    public String getContact() { return contact; }
    public String getOwner() { return owner; }
}