# 二手交易系统

## 项目简介
二手交易系统是一个基于 JavaFX 的桌面应用程序，旨在为用户提供一个方便的二手商品交易平台。用户可以注册、登录、发布商品、浏览商品以及管理商品信息。

## 项目结构
```
LICENSE
pom.xml
README.md
src/
    main/
        java/
            com/
                wust/
                    secondhand/
                        Main.java
                        controllers/
                            AdminMainViewController.java
                            LoginController.java
                            RegisterController.java
                            SubmitItemController.java
                            UserMainViewController.java
                        models/
                            DataManager.java
                            Item.java
                            User.java
                            enums/
                                ItemStatus.java
                                UserRole.java
                        utils/
                            FxmlManager.java
        resources/
            com/
                wust/
                    secondhand/
                        css/
                            components/
                                button.css
                            views/
                                AdminMain.css
                                Login.css
                                Register.css
                                SubmitItem.css
                                UserMain.css
                            base.css
                        data/
                            items.json
                            users.json
                        fxml/
                            AdminMainView.fxml
                            LoginView.fxml
                            RegisterView.fxml
                            SubmitItemView.fxml
                            UserMainView.fxml
                        image/
                            login_bg.png
                            register_bg.png
                            usermain.jpg
                        images/
                            users1/
                              ...
                            
```

### 主要模块
- **controllers**: 包含所有的控制器类，用于处理用户交互逻辑。
- **models**: 定义了数据模型，例如用户和商品。
- **utils**: 包含工具类，例如 FxmlManager。
- **resources**: 包含应用程序的资源文件，例如 FXML 文件和 JSON 数据文件。

## 环境要求
- JDK 11 或更高版本
- Maven 3.6 或更高版本

## 构建与运行
1. 克隆项目到本地：
   ```bash
   git clone https://github.com/jinjie0703/Second-hand-trading-system.git
   ```
2. 使用 Maven 构建项目：
   ```bash
   mvn clean install
   ```
3. 运行主类：
   ```bash
   java -cp target/classes com.wust.secondhand.Main
   ```

## 功能概述
- 用户注册与登录
- 发布商品
- 浏览商品
- 管理商品信息

## 文件说明
- `Main.java`: 应用程序的入口。
- `controllers/`: 包含各个视图的控制器。
- `models/`: 定义了用户和商品的模型。
- `resources/`: 包含 FXML 文件和 JSON 数据文件。

## 许可证
本项目使用 MIT 许可证，详情请参阅 LICENSE 文件。
