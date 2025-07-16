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
    private String tradeType; // 新增字段：交易类型
    private String campus;
    // --- 这个字段是JavaFX的属性，用于UI绑定 ---
    private transient ObjectProperty<ItemStatus> statusProperty;

    /**
     * 这个无参构造函数是必需的，以便在反序列化后进行数据同步。
     */
    public Item() {}

    public Item(String name, int quantity, String description, String imagePath, String tradeLocation, String contact, String owner, String tradeType, String campus) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.imagePath = imagePath;
        this.tradeLocation = tradeLocation;
        this.contact = contact;
        this.owner = owner;
        this.status = ItemStatus.PENDING; // 初始化普通的status字段
        this.tradeType = tradeType;
        this.campus = campus;
        // 注意：这里我们不再初始化 statusProperty
    }

    /** 确保 statusProperty 已经初始化。
     * 这个方法会在第一次访问 statusProperty 时被调用，
     * 并且会添加一个监听器来同步普通的 status 字段。
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

    /**
     * 获取当前物品的状态。
     * 如果 statusProperty 尚未初始化，则会自动初始化它。
     * @return 当前物品的状态
     */
    public ItemStatus getStatus() {
        ensureStatusPropertyInitialized();
        return statusProperty.get();
    }

    /**
     * 设置物品的状态。
     * 如果 statusProperty 尚未初始化，则会自动初始化它。
     * @param newStatus 新的物品状态
     */
    public void setStatus(ItemStatus newStatus) {
        ensureStatusPropertyInitialized();
        this.statusProperty.set(newStatus);
    }

    /** 获取 JavaFX 的状态属性。
     * 这个方法确保 statusProperty 已经被初始化，
     * 并返回一个可以绑定到 UI 的属性。
     * @return JavaFX 的状态属性
     */
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
    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }
    public String getCampus() {return campus;}
    public void setCampus(String campus) {this.campus = campus;}
}