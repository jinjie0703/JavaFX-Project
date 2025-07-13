package com.wust.secondhand.models;

import com.wust.secondhand.models.enums.ItemStatus;
import java.util.UUID;

public class Item {
    private String id; // 系统生成的唯一ID
    private String name;
    private int quantity;
    private String description;
    private String imagePath; // 图片的相对路径
    private String tradeLocation;
    private String contact;
    private String owner; // 提交物品的用户名
    private ItemStatus status;

    // 构造函数，自动生成ID和设置初始状态
    public Item(String name, int quantity, String description, String imagePath, String tradeLocation, String contact, String owner) {
        this.id = UUID.randomUUID().toString(); // 生成唯一ID
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.imagePath = imagePath;
        this.tradeLocation = tradeLocation;
        this.contact = contact;
        this.owner = owner;
        this.status = ItemStatus.PENDING; // 初始状态为“待审核”
    }

    // Getters and Setters...
    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
    public String getTradeLocation() { return tradeLocation; }
    public String getContact() { return contact; }
    public String getOwner() { return owner; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
}