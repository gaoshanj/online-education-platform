/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : online_edu

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 07/05/2026 14:13:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色：user/assistant',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_chat_message
-- ----------------------------
INSERT INTO `ai_chat_message` VALUES (39, 10, 'user', 'Spring是什么？', '2026-04-04 22:32:30', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (40, 10, 'assistant', 'Spring 是一个**开源的 Java 应用框架**，由 Rod Johnson 于 2003 年提出并创建，旨在简化企业级 Java 应用（尤其是 J2EE）的开发，解决传统 Java 开发中配置繁琐、耦合度高、测试困难等问题。\n\n### 核心特点：\n✅ **轻量级（Lightweight）**：非侵入式设计，不强制依赖 Spring API，POJO 即可开发。  \n✅ **控制反转（IoC / Inversion of Control）**：通过容器管理对象的生命周期和依赖关系，降低组件耦合。  \n✅ **面向切面编程（AOP / Aspect-Oriented Programming）**：将横切关注点（如日志、事务、安全）与业务逻辑解耦。  \n✅ **模块化设计**：提供多个可选模块（如 Spring MVC、Spring Data、Spring Security、Spring Boot 等），按需使用。  \n✅ **强大的生态与整合能力**：无缝集成数据库（JDBC、JPA、MyBatis）、消息队列、缓存、Web 框架等。\n\n### 常见子项目/模块：\n- **Spring Core**：IoC 容器基础（BeanFactory、ApplicationContext）。  \n- **Spring AOP**：实现切面编程支持。  \n- **Spring MVC**：基于 Servlet 的 Web 框架，用于构建 RESTful 或传统 Web 应用。  \n- **Spring Data**：统一数据访问抽象（简化 JPA、Redis、MongoDB 等操作）。  \n- **Spring Security**：强大而灵活的身份认证与授权框架。  \n- **Spring Boot**（重要！）：Spring 的“开箱即用”增强版，自动配置 + 内嵌服务器 + 起步依赖，极大提升开发效率（常被误认为是 Spring 的替代品，实为 Spring 的演进和封装）。\n\n🔹 简单说：  \n> **Spring 是 Java 开发的“基础设施层”——它不直接写业务，但让写业务变得更简单、更规范、更可维护。**\n\n需要我帮你对比 Spring 和 Spring Boot？或者用一个例子说明 IoC 是怎么工作的？😊', '2026-04-04 22:32:42', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (41, 11, 'user', '面向对象的三大特性是什么？', '2026-04-04 22:33:09', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (42, 11, 'assistant', '面向对象编程（OOP）的三大核心特性是：\n\n1. **封装（Encapsulation）**  \n   - 将数据（属性）和操作数据的方法（行为）绑定在一起，形成一个独立的单元——类（Class）。  \n   - 通过访问控制（如 `private`、`protected`、`public`）隐藏内部实现细节，只暴露必要的接口，提高安全性与可维护性。  \n   ✅ 例如：类中将字段设为 `private`，通过 `public` 的 getter/setter 方法访问。\n\n2. **继承（Inheritance）**  \n   - 允许一个类（子类/派生类）基于另一个类（父类/基类）来定义，从而复用父类的属性和方法，并可扩展或重写。  \n   - 体现“is-a”关系（如 `Dog` is a `Animal`），支持代码复用和层次化建模。  \n   ⚠️ 注意：并非所有语言都支持多继承（如 Java 只支持单继承，但可通过接口实现多重行为继承）。\n\n3. **多态（Polymorphism）**  \n   - 同一操作作用于不同对象，可以有不同的解释和执行结果。  \n   - 常见形式包括：  \n     - **编译时多态**（方法重载，Overloading）  \n     - **运行时多态**（方法重写 + 父类引用指向子类对象，如 `Animal a = new Dog(); a.speak();`）  \n   ✅ 核心依赖：继承 + 方法重写 + 向上转型 + 动态绑定（虚函数机制）。\n\n💡 补充说明：有些资料会提到“抽象（Abstraction）”作为第四特性，它强调关注对象的核心特征而忽略细节（常通过抽象类、接口实现），但传统 OOP 三大支柱仍公认是 **封装、继承、多态**。\n\n需要我用代码示例说明其中某一项吗？ 😊', '2026-04-04 22:33:20', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (43, 11, 'user', '介绍一下封装的概念', '2026-04-22 16:50:29', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (44, 10, 'user', 'spring和Springboot的区别', '2026-04-22 16:52:27', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (45, 12, 'user', '你好', '2026-04-22 16:54:49', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (46, 12, 'assistant', '你好！很高兴见到你～😊  \n有什么问题或需要帮助的地方吗？无论是学习、知识答疑、还是其他方面，我都很乐意为你提供支持！', '2026-04-22 16:54:51', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (47, 12, 'user', 'spring是什么', '2026-04-22 16:55:09', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (48, 12, 'assistant', '你好！😊  \n**Spring** 是一个开源的 **Java 应用框架（Framework）**，由 Rod Johnson 于 2003 年提出并创建，目前由 Pivotal（现属 VMware）主导维护。它被广泛用于构建企业级、可扩展、松耦合的 Java 应用程序（尤其是 Web 应用和微服务）。\n\n---\n\n### ✅ Spring 的核心目标：\n- **简化 Java 开发**：减少样板代码（如 JDBC、事务管理等）；\n- **促进松耦合设计**：通过依赖注入（DI）和面向切面编程（AOP）解耦组件；\n- **提供统一的编程模型**：整合各种技术（数据库、Web、安全、消息、测试等）。\n\n---\n\n### 🔑 Spring 的几大核心特性：\n\n| 特性 | 简要说明 |\n|------|----------|\n| **IoC（控制反转）容器** | 将对象的创建和依赖关系交给 Spring 容器管理，开发者只需声明“需要什么”，不用手动 `new` 对象。 |\n| **DI（依赖注入）** | IoC 的实现方式，如通过构造器、Setter 方法或字段注入依赖（如 `@Autowired`）。 |\n| **AOP（面向切面编程）** | 将横切关注点（如日志、事务、权限）与业务逻辑分离，提高模块化程度（如 `@Transactional`）。 |\n| **Spring MVC** | 基于 Servlet 的轻量级 Web 框架，用于构建 RESTful 或传统 Web 应用。 |\n| **Spring Data** | 统一数据访问层（支持 JPA、Redis、MongoDB、JDBC 等），大幅简化 DAO 层代码。 |\n| **Spring Boot（重要衍生）** ⭐ | **不是 Spring 的子项目，而是“约定优于配置”的快速开发脚手架**，自动配置 + 内嵌服务器（Tomcat）+ Starter 依赖，让 Spring 应用“开箱即用”。 |\n| **Spring Cloud** | 构建分布式/微服务系统的工具集（服务发现、配置中心、熔断、网关等）。 |\n\n---\n\n### 🌟 举个简单例子（Spring Boot）：\n```java\n@SpringBootApplication\npublic class MyApp {\n    public static void main(String[] args) {\n        SpringApplication.run(MyApp.class, args);\n    }\n}\n\n@RestController\nclass HelloController {\n    @GetMapping(\"/hello\")\n    String hello() {\n        return \"Hello from Spring!\";\n    }\n}\n```\n✅ 运行后，直接访问 `http://localhost:8080/hello` 就能看到结果 —— **无需 XML 配置、无需部署 WAR、无需手动配 Tomcat！**\n\n---\n\n如果你是初学者，建议从 **Spring Boot** 入门（它极大降低了 Spring 的学习门槛）；  \n如果是面试或进阶，需要深入理解 **IoC/AOP 原理、Bean 生命周期、事务管理、Spring MVC 流程** 等。\n\n需要我帮你：\n- 对比 Spring 和 Spring Boot？\n- 解释某个核心概念（比如 Bean 是什么？@Autowired 怎么工作？）？\n- 搭建一个入门小项目？\n- 或准备面试题？\n\n欢迎随时告诉我 😊', '2026-04-22 16:55:32', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (49, 13, 'user', '你好', '2026-04-22 17:04:34', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (50, 13, 'assistant', '你好！很高兴见到你～😊  \n有什么问题、疑惑，或者需要帮助的地方吗？比如学习上的难题、知识点讲解、作业辅导，还是其他方面？我随时乐意帮忙！', '2026-04-22 17:04:36', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (51, 14, 'user', '你好', '2026-04-22 17:05:05', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (52, 14, 'assistant', '你好！很高兴见到你～😊  \n请问有什么问题我可以帮你解答吗？无论是学习上的疑问、知识点梳理，还是作业辅导、考试准备，我都很乐意帮忙！', '2026-04-22 17:05:07', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (53, 15, 'user', '你好', '2026-04-22 17:28:00', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (54, 15, 'assistant', '你好！很高兴见到你～😊  \n有什么问题或需要帮助的地方吗？比如学习上的疑问、知识点讲解、作业辅导，或者想了解某个学科的内容，我都很乐意帮忙！', '2026-04-22 17:28:01', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (55, 16, 'user', '你好', '2026-04-22 17:28:34', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (56, 16, 'assistant', '你好！很高兴见到你～😊  \n有什么问题、疑惑，或者需要帮助的地方吗？比如学习上的难题、知识点讲解、作业辅导，还是其他方面？我随时乐意帮忙！', '2026-04-22 17:28:35', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (57, 17, 'user', '你好', '2026-04-22 17:28:59', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (58, 17, 'assistant', '你好！很高兴见到你～😊  \n请问有什么问题我可以帮你解答吗？无论是学习上的疑问、知识点梳理，还是作业辅导、考试准备，我都很乐意帮忙！', '2026-04-22 17:29:00', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (59, 11, 'user', '介绍一下封装的概念', '2026-04-22 17:29:16', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (60, 11, 'assistant', '当然可以！😊\n\n**封装（Encapsulation）** 是面向对象编程（OOP）的**首要和基础特性**，它的核心思想是：\n\n> ✅ **“把数据（属性）和操作数据的方法（行为）捆绑在一起，并对外隐藏内部实现细节，仅提供受控的访问接口。”**\n\n类比理解 🌟：  \n就像一台智能手机——你不需要知道屏幕怎么发光、芯片如何运算（内部细节），只需通过触摸屏、按钮（公开接口）就能打电话、拍照。厂商把复杂逻辑“封装”起来了，既保护了核心设计，又提升了易用性与安全性。\n\n---\n\n### 封装的三大关键要素：\n\n| 要素 | 说明 | 示例（以 Java 为例） |\n|------|------|------------------------|\n| **1. 属性私有化（数据隐藏）** | 将字段（field）声明为 `private`，防止外部直接访问或随意修改 | `private String name; private int age;` |\n| **2. 提供公共访问方法（Getter/Setter）** | 用 `public` 方法控制对私有属性的读写，可加入校验逻辑 | `public String getName() { return name; }`<br>`public void setAge(int age) { if (age > 0) this.age = age; }` |\n| **3. 行为与数据统一在类中** | 方法直接操作本类的属性，体现“高内聚”——相关功能集中管理 | `public void introduce() { System.out.println(\"我是\" + name); }` |\n\n---\n\n### ✅ 封装的好处：\n- **安全性**：防止非法/错误的数据赋值（如年龄设为 -5）；\n- **可维护性**：内部实现变更（如把 `age` 改成由出生年份计算）不影响外部调用；\n- **可复用性**：封装好的类可被多个模块安全复用；\n- **降低耦合**：调用者只依赖接口，不依赖实现，便于系统解耦。\n\n---\n\n### ❌ 不封装的反例（坏代码）：\n```java\nclass Person {\n    public String name; // ❌ 外部可随意修改，无法校验\n    public int age;\n}\n// 使用时：\nPerson p = new Person();\np.age = -100; // 合法但语义错误！\n```\n\n✅ 正确封装后：\n```java\nclass Person {\n    private String name;\n    private int age;\n\n    public void setAge(int age) {\n        if (age < 0 || age > 150) {\n            throw new IllegalArgumentException(\"年龄必须在0~150之间\");\n        }\n        this.age = age;\n    }\n    // 其他 getter/setter...\n}\n```\n\n---\n\n💡 小贴士：  \n封装 ≠ 单纯加 `private`！真正的封装是**有意识的设计**：思考哪些该暴露、哪些该隐藏、哪些需要校验或触发联动逻辑（如设置姓名时自动更新昵称）。\n\n需要我用 Python 或 C++ 的语法再演示一次封装吗？或者帮你设计一个带封装的实际小例子（比如银行账户类）？ 😊', '2026-04-22 17:29:32', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (61, 18, 'user', 'spring是什么', '2026-04-24 01:12:35', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (62, 18, 'assistant', 'Spring 是一个**开源的 Java 应用框架**，主要用于简化企业级（尤其是 Web）应用程序的开发。它由 Rod Johnson 于 2003 年提出并创建，现由 Pivotal（后被 VMware 收购，现属 Broadcom）维护，是 Java 生态中最主流、最成熟的轻量级框架之一。\n\n✅ **核心定位**：  \nSpring 不是一个“全栈框架”，而是一个**分层、模块化、非侵入式的开发平台**，旨在解决传统 Java EE 开发中配置繁琐、耦合度高、测试困难等问题。\n\n---\n\n🔹 **Spring 的几个关键特性/概念**：\n\n1. **IoC（控制反转，Inversion of Control）容器**  \n   - Spring 管理对象（Bean）的生命周期和依赖关系。\n   - 开发者不再手动 `new` 对象，而是通过配置（XML 或注解）让 Spring 自动装配依赖 → 实现**松耦合**。\n\n2. **AOP（面向切面编程，Aspect-Oriented Programming）**  \n   - 将横切关注点（如日志、事务、安全、监控）与业务逻辑分离，提高代码复用性和可维护性。\n\n3. **丰富的模块生态**（Spring Framework 5.x+）：\n   - `spring-core`：基础工具类（如资源加载、类型转换）\n   - `spring-beans` & `spring-context`：IoC 容器核心\n   - `spring-aop`：AOP 支持\n   - `spring-jdbc` / `spring-orm`：数据库访问封装（简化 JDBC、集成 MyBatis/Hibernate）\n   - `spring-web` / `spring-webmvc`：Web 开发支持（如 MVC 模式、REST 接口）\n   - `spring-tx`：声明式事务管理\n\n4. **Spring Boot（重要延伸）**  \n   - 基于 Spring Framework 的“约定优于配置”快速开发脚手架。\n   - 内嵌 Tomcat、自动配置、起步依赖（Starter）、Actuator 监控等，极大提升开发效率。\n   - ✅ 当前绝大多数新 Spring 项目都基于 Spring Boot 构建。\n\n5. **Spring Cloud（微服务生态）**  \n   - 提供服务发现（Eureka/Nacos）、配置中心（Config/Apollo）、熔断限流（Hystrix/Sentinel）、网关（Gateway）等微服务解决方案。\n\n---\n\n📌 简单类比：  \n> 如果把开发一个 Java Web 应用比作盖一栋楼，  \n> - 传统方式：你得自己打地基、砌墙、装水电、做消防……事无巨细；  \n> - Spring 就像一个成熟的建筑公司，提供标准化地基（IoC）、模块化建材（AOP）、智能布线方案（事务/安全），让你专注设计“房间功能”（业务逻辑）；  \n> - Spring Boot 是“精装交付样板间”，开箱即用；  \n> - Spring Cloud 则是帮你建“小区+物业系统”，管理多栋楼（微服务）之间的协作。\n\n---\n\n✅ 总结一句话：  \n> **Spring 是一套以 IoC 和 AOP 为核心、高度模块化、生态丰富、支撑从单体到微服务全场景的 Java 企业级开发框架体系。**\n\n需要我为你画个 Spring 生态图、对比 Spring Boot 和传统 Spring，或带你写一个最简 Spring Boot “Hello World” 示例吗？ 😊', '2026-04-24 01:12:54', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (63, 19, 'user', '你好', '2026-04-24 01:15:08', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (64, 19, 'assistant', '你好！很高兴见到你～😊  \n有什么问题、疑惑，或者需要帮助的地方吗？比如学习上的难题、知识点讲解、作业辅导，还是其他方面？我随时乐意帮忙！', '2026-04-24 01:15:09', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (65, 19, 'user', 'spiring是什么', '2026-04-24 01:15:20', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (66, 19, 'assistant', '你好！你提到的 **“spiring”** 可能是拼写错误，常见的正确拼写是：\n\n✅ **Spring**（注意首字母大写时通常指框架，小写时是英文单词）\n\n下面分两种情况为你解释：\n\n---\n\n🔹 1. **Spring（软件框架）——最可能的含义**  \n**Spring** 是一个非常流行、开源的 **Java 应用开发框架**，由 Rod Johnson 于 2003 年提出，现由 Pivotal（现属 VMware）维护。它主要用于构建企业级 Java 应用（尤其是后端服务）。\n\n📌 核心特点包括：\n- ✅ **IoC（控制反转）容器**：管理对象的创建与依赖关系（即“谁来创建对象、谁来注入依赖”，由 Spring 容器负责）；\n- ✅ **AOP（面向切面编程）**：便于统一处理日志、事务、安全等横切关注点；\n- ✅ **Spring MVC**：基于 Servlet 的 Web 框架，用于构建 Web 应用；\n- ✅ **Spring Boot**（Spring 的“升级版”）：约定优于配置，快速启动独立运行的 Spring 应用（内嵌 Tomcat，自动配置）；\n- ✅ **Spring Cloud**：用于构建分布式微服务系统（服务发现、配置中心、熔断等）。\n\n💡 简单说：**Spring 是 Java 开发者的“瑞士军刀”——让写企业级应用更简单、规范、可维护。**\n\n---\n\n🔹 2. **spiring（拼写错误或生僻词）？**  \n- 英文中没有标准单词 *spiring*；  \n- 可能是 **spring** 的笔误（比如键盘上 `s` 和 `d` 相邻，误打成 *spiring*）；  \n- 极少数情况下，可能是某公司/项目/缩写的自定义名称（但无通用含义），需结合上下文判断。\n\n---\n\n✅ 建议你确认一下：\n- 是不是想了解 **Spring 框架**？（比如在学 Java、准备面试、搭建后台服务？）  \n- 或者你看到的是某个特定场景中的 “spiring”（如文档、代码、截图）？欢迎补充，我可以帮你进一步分析！\n\n需要我为你简要介绍 **Spring Boot 入门示例** 或 **IoC/AOP 是什么** 吗？😊', '2026-04-24 01:15:32', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (67, 20, 'user', 'Spring是什么', '2026-04-24 16:53:23', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (68, 20, 'assistant', 'Spring 是一个**开源的 Java 应用框架**，由 Rod Johnson 于 2003 年左右提出并创建，旨在简化企业级 Java 应用（尤其是 J2EE/Java EE）的开发。它已经成为 Java 生态中最主流、最广泛使用的轻量级框架之一。\n\n✅ **核心定位**：  \nSpring 不是一个“大而全”的容器，而是一个**分层、模块化、非侵入式**的框架集合，提供全面的基础设施支持，让开发者能更专注于业务逻辑，而不是重复造轮子（如对象创建、事务管理、数据库连接等）。\n\n---\n\n🔹 **Spring 的核心特性包括**：\n\n1. **IoC（控制反转）容器**  \n   - 管理对象（Bean）的生命周期和依赖关系。  \n   - 开发者不再手动 `new` 对象，而是通过配置（XML/注解/Java Config）交由 Spring 容器自动装配（即「依赖注入 DI」）。  \n   → 实现松耦合、易测试、高内聚。\n\n2. **AOP（面向切面编程）支持**  \n   - 将横切关注点（如日志、事务、安全、监控）与业务逻辑分离。  \n   - 通过代理机制在不修改源码的前提下动态织入功能。\n\n3. **一站式集成能力**  \n   Spring 并非“只做一件事”，而是提供丰富模块，可按需选用：\n   - `Spring JDBC` / `Spring ORM`：简化数据库访问（整合 MyBatis、Hibernate 等）  \n   - `Spring MVC`：强大的 Web 层框架（基于 Servlet，实现 RESTful 接口）  \n   - `Spring Boot`（现代主流）：约定优于配置，快速构建独立、生产级 Spring 应用（内嵌 Tomcat、自动配置、起步依赖）  \n   - `Spring Data`：统一数据访问抽象（JPA、Redis、MongoDB 等）  \n   - `Spring Security`：认证与授权框架  \n   - `Spring Cloud`：微服务架构解决方案（服务发现、配置中心、网关、熔断等）\n\n---\n\n💡 **一句话总结**：  \n> **Spring 是一个以 IoC 和 AOP 为核心，为 Java 应用提供全方位基础设施支持的轻量级、模块化、可扩展的开源框架体系。**\n\n📌 补充说明：  \n- 传统 Spring（Spring Framework）需要较多配置；  \n- 如今绝大多数项目使用 **Spring Boot**（它是 Spring 生态的“加速器”），极大降低了入门门槛和开发复杂度。\n\n需要我为你对比 Spring Framework 和 Spring Boot？或者带你写一个最简 Spring Boot “Hello World” 示例？😊', '2026-04-24 16:53:38', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (69, 21, 'user', '简单说一下SpringBoot', '2026-04-24 16:55:27', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (70, 21, 'assistant', 'Spring Boot 是一个基于 Spring 框架的**快速开发框架**，它的核心目标是：**简化 Spring 应用的初始搭建和开发过程**。\n\n简单来说，它有以下几个关键特点：\n\n✅ **开箱即用（Auto-Configuration）**  \n根据项目依赖自动配置 Spring 和第三方库（如数据库、Web、安全等），无需大量 XML 或 Java 配置。\n\n✅ **内嵌服务器（如 Tomcat、Jetty）**  \n打包成一个可执行的 JAR 文件，直接运行 `java -jar xxx.jar` 就能启动 Web 应用，无需部署到外部容器。\n\n✅ **起步依赖（Starter Dependencies）**  \n提供预定义的依赖组合（如 `spring-boot-starter-web`），一键引入常用功能所需的所有依赖，避免版本冲突。\n\n✅ **约定优于配置（Convention over Configuration）**  \n默认配置合理，开发者只需关注业务逻辑；需要自定义时，通过 `application.properties` 或 `application.yml` 简单修改即可。\n\n✅ **生产就绪（Actuator）**  \n内置监控、健康检查、指标收集等生产级功能，便于运维和问题排查。\n\n🎯 一句话总结：  \n**Spring Boot = Spring + 自动配置 + 内嵌服务器 + 简化依赖 + 快速开发**\n\n适合构建微服务、REST API、后台管理系统等各类 Java Web 应用。✨\n\n需要我帮你快速搭建一个 Hello World 示例吗？ 😊', '2026-04-24 16:55:34', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (71, 22, 'user', 'Spring是什么？', '2026-04-24 17:11:48', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (72, 22, 'assistant', 'Spring 是一个**开源的 Java 应用框架**，由 Rod Johnson 于 2003 年提出并创建，旨在简化企业级 Java 应用（尤其是 J2EE）的开发，解决传统 Java 开发中配置繁琐、耦合度高、测试困难等问题。\n\n✅ **核心特点：**  \n1. **轻量级 & 非侵入式**  \n   - 不强制继承特定类或实现特定接口，对现有代码干扰小。  \n   - 应用可脱离 Spring 环境运行（如直接用 `main` 方法启动）。\n\n2. **控制反转（IoC, Inversion of Control）**  \n   - 将对象的创建和依赖关系管理交给 Spring 容器（如 `ApplicationContext`），而非由程序员手动 `new`。  \n   - 实现方式主要是**依赖注入（DI, Dependency Injection）**：通过构造器、Setter 或字段自动注入依赖。\n\n3. **面向切面编程（AOP, Aspect-Oriented Programming）**  \n   - 将横切关注点（如日志、事务、安全、监控）与业务逻辑解耦，通过“切面”统一处理，提升模块化和可维护性。\n\n4. **丰富的模块生态（Spring Framework 5+）**  \n   - `spring-core`：基础工具类（如类型转换、资源加载）  \n   - `spring-beans` / `spring-context`：IoC 容器核心  \n   - `spring-aop`：AOP 支持  \n   - `spring-jdbc` / `spring-orm`：数据库访问支持（集成 MyBatis、Hibernate 等）  \n   - `spring-web` / `spring-webmvc`：Web 开发（MVC 框架）  \n   - `spring-tx`：声明式事务管理  \n\n5. **与 Spring Boot 的关系**  \n   - Spring Framework 是底层核心；  \n   - **Spring Boot 是基于 Spring 的“快速开发脚手架”**，提供自动配置、起步依赖（Starter）、内嵌服务器等，极大简化 Spring 应用的搭建和部署。\n\n🔍 简单类比：  \n> Spring 就像一个智能管家：你只管写业务逻辑（比如“下单”），它帮你自动准备好数据库连接、事务控制、日志记录、HTTP 接口等一切配套设施，并确保各部分松耦合、易测试、好扩展。\n\n💡 一句话总结：  \n**Spring 是一个以 IoC 和 AOP 为核心，用于构建松耦合、可测试、可维护的 Java 企业级应用的综合性框架。**\n\n需要我为你对比 Spring 和 Spring Boot？或者演示一个最简 Spring Bean 示例？ 😊', '2026-04-24 17:12:02', NULL, 1);
INSERT INTO `ai_chat_message` VALUES (73, 23, 'user', '简要说明Spring的作用', '2026-04-24 17:22:24', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (74, 23, 'assistant', 'Spring 是一个开源的 Java 应用框架，其核心作用是**简化企业级 Java 应用的开发与管理**。简要来说，它的主要作用包括：\n\n1. **控制反转（IoC）**：  \n   将对象的创建和依赖关系交由 Spring 容器管理，降低组件间的耦合度，提高可测试性和可维护性。\n\n2. **面向切面编程（AOP）**：  \n   支持横切关注点（如日志、事务、安全）的模块化，避免重复代码，增强业务逻辑的纯粹性。\n\n3. **一站式集成能力**：  \n   提供对数据库（JDBC、ORM 如 MyBatis/Hibernate）、Web（Spring MVC、Spring WebFlux）、消息队列、缓存、安全（Spring Security）等技术的统一、简洁的封装与整合。\n\n4. **轻量、非侵入式**：  \n   不强制依赖特定 API，POJO（普通 Java 对象）即可开发，便于迁移和测试。\n\n5. **强大的生态支持（Spring Boot / Spring Cloud）**：  \n   基于 Spring 的扩展框架大幅简化配置、部署和微服务开发，成为现代 Java 开发的事实标准。\n\n✅ 总结一句话：  \n**Spring 是一个以 IoC 和 AOP 为核心，致力于降低 Java 应用开发复杂度、提升可维护性与扩展性的综合性开发框架。**', '2026-04-24 17:22:32', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (75, 24, 'user', '简要介绍一下', '2026-04-25 00:41:51', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (76, 24, 'assistant', '您好！您提到“简要介绍一下”，但还没有说明具体想了解什么内容（比如某个概念、人物、技术、课程、平台，还是其他主题）。😊\n\n请您补充一下，例如：\n- 想了解“人工智能”？\n- 还是“Python编程”？\n- 或者是“在线教育平台‘学而思网校’”？\n- 又或者是某个历史事件、科学原理、学习方法等？\n\n我会立刻为您简洁、清晰地介绍！', '2026-04-25 00:41:55', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (77, 25, 'user', '简要介绍一下Spring Boot', '2026-04-25 00:42:25', NULL, 0);
INSERT INTO `ai_chat_message` VALUES (78, 25, 'assistant', 'Spring Boot 是 Spring 生态中一个**用于快速开发、简化配置的开源框架**，其核心目标是“**约定优于配置**”（Convention over Configuration），帮助开发者以极简的方式构建独立、生产级的 Spring 应用。\n\n✅ 主要特点包括：\n\n- **自动配置（Auto-configuration）**：根据项目依赖（如添加了 `spring-boot-starter-web`）自动配置 Spring 和第三方库，大幅减少 XML 或 Java 配置。\n- **起步依赖（Starters）**：提供预定义的依赖集合（如 `spring-boot-starter-data-jpa`），一键引入常用功能所需的所有依赖及版本管理。\n- **内嵌服务器**：默认集成 Tomcat/Jetty/Undertow，无需部署到外部容器，直接运行 `main()` 方法即可启动 Web 应用。\n- **Actuator 监控**：提供生产级监控端点（如 `/actuator/health`, `/actuator/metrics`），便于运维与诊断。\n- **无代码生成、无 XML 配置**：基于 Java 注解和少量 `application.properties`/`application.yml` 即可完成配置。\n- **易于打包与部署**：支持打包为可执行 JAR（含所有依赖），一键运行（`java -jar xxx.jar`）。\n\n💡 简单说：  \n> Spring Boot ≠ 新框架，而是 Spring 的“加速器”——它让 Spring 应用从“准备环境 → 配置 → 编码 → 部署”的繁琐流程，变成“写代码 → 运行”两步搞定。\n\n适合快速构建微服务、REST API、后台管理系统等各类 Java 应用。  \n官网：https://spring.io/projects/spring-boot  \n\n需要我帮你演示一个最简 Hello World 示例吗？ 😊', '2026-04-25 00:42:36', NULL, 0);

-- ----------------------------
-- Table structure for ai_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_session`;
CREATE TABLE `ai_chat_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '新对话' COMMENT '会话标题',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI聊天会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_chat_session
-- ----------------------------
INSERT INTO `ai_chat_session` VALUES (10, 4, 'Spring是什么？', '2026-04-04 22:32:30', '2026-04-24 00:52:09', 1);
INSERT INTO `ai_chat_session` VALUES (11, 4, '面向对象的特性有哪些？', '2026-04-04 22:33:09', '2026-04-22 17:29:32', 0);
INSERT INTO `ai_chat_session` VALUES (12, 4, '你好', '2026-04-22 16:54:49', '2026-04-22 21:22:24', 1);
INSERT INTO `ai_chat_session` VALUES (13, 4, '你好', '2026-04-22 17:04:34', '2026-04-22 17:36:34', 1);
INSERT INTO `ai_chat_session` VALUES (14, 4, '你好', '2026-04-22 17:05:05', '2026-04-22 21:22:27', 1);
INSERT INTO `ai_chat_session` VALUES (15, 4, '你好', '2026-04-22 17:28:00', '2026-04-22 17:36:31', 1);
INSERT INTO `ai_chat_session` VALUES (16, 4, '你好', '2026-04-22 17:28:34', '2026-04-22 17:36:29', 1);
INSERT INTO `ai_chat_session` VALUES (17, 4, '你好', '2026-04-22 17:28:59', '2026-04-22 17:36:27', 1);
INSERT INTO `ai_chat_session` VALUES (18, 4, 'spring是什么', '2026-04-24 01:12:35', '2026-04-24 01:16:00', 1);
INSERT INTO `ai_chat_session` VALUES (19, 4, '你好', '2026-04-24 01:15:08', '2026-04-24 01:15:58', 1);
INSERT INTO `ai_chat_session` VALUES (20, 4, 'Spring是什么', '2026-04-24 16:53:23', '2026-04-24 16:55:55', 1);
INSERT INTO `ai_chat_session` VALUES (21, 4, '简单说一下SpringBoot', '2026-04-24 16:55:27', '2026-04-24 16:56:06', 1);
INSERT INTO `ai_chat_session` VALUES (22, 4, 'Spring是什么？', '2026-04-24 17:11:48', '2026-04-24 17:17:59', 1);
INSERT INTO `ai_chat_session` VALUES (23, 4, '简要说明Spring的作用', '2026-04-24 17:22:24', '2026-04-24 17:22:32', 0);
INSERT INTO `ai_chat_session` VALUES (24, 4, '简要介绍一下', '2026-04-25 00:41:51', '2026-04-25 00:41:55', 0);
INSERT INTO `ai_chat_session` VALUES (25, 4, '简要介绍一下Spring Boot', '2026-04-25 00:42:25', '2026-04-25 00:42:36', 0);

-- ----------------------------
-- Table structure for answer
-- ----------------------------
DROP TABLE IF EXISTS `answer`;
CREATE TABLE `answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回答ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `answer_id` bigint NULL DEFAULT 0 COMMENT '回复的回答id(一级回答的id，为0表示该记录是回答)',
  `user_id` bigint NOT NULL COMMENT '回答用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回答内容',
  `is_teacher` tinyint NULL DEFAULT 0 COMMENT '是否为讲师回答：0-否，1-是',
  `target_reply_id` bigint NULL DEFAULT 0 COMMENT '回复的目标回复id',
  `target_user_id` bigint NULL DEFAULT 0 COMMENT '回复的目标用户id',
  `reply_times` int NOT NULL DEFAULT 0 COMMENT '评论数量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_answer_id`(`answer_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_is_teacher`(`is_teacher` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '回答评论表（一级为回答，二级为评论）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answer
-- ----------------------------
INSERT INTO `answer` VALUES (1, 1, 0, 102, '我也是零基础，学起来还可以', 0, 0, 0, 2, 3, '2026-04-04 22:20:04', '2026-04-24 17:05:29', 0);
INSERT INTO `answer` VALUES (2, 1, 0, 3, '讲师讲得很细，可以跟上', 1, 0, 0, 1, 5, '2026-04-04 22:20:04', '2026-04-04 22:23:38', 1);
INSERT INTO `answer` VALUES (3, 2, 0, 104, '有一个完整的项目实战', 0, 0, 0, 1, 2, '2026-04-04 22:20:04', '2026-04-24 17:05:45', 0);
INSERT INTO `answer` VALUES (4, 2, 0, 3, '实战部分挺不错的', 0, 0, 0, 0, 1, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer` VALUES (5, 3, 0, 2, '大概两个月可以学完', 1, 0, 0, 1, 4, '2026-04-04 22:20:04', '2026-04-04 22:21:08', 1);
INSERT INTO `answer` VALUES (6, 4, 0, 3, '可以提供源码下载', 0, 0, 0, 0, 2, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer` VALUES (7, 1, 1, 1, '谢谢你的反馈！', 0, 1, 2, 0, 1, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer` VALUES (8, 1, 1, 3, '确实讲得很清楚', 0, 1, 2, 0, 0, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer` VALUES (9, 1, 2, 2, '老师讲得很好', 0, 2, 3, 0, 2, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer` VALUES (10, 2, 3, 2, '项目难度大吗？', 0, 3, 1, 0, 0, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer` VALUES (11, 3, 5, 1, '每天学习多久？', 0, 5, 2, 0, 1, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer` VALUES (24, 1, 0, 4, 'hlthelgel', 0, 0, 0, 0, 0, '2026-04-24 16:50:11', '2026-04-24 17:04:31', 1);
INSERT INTO `answer` VALUES (25, 1, 0, 4, 'alhelge', 0, 0, 0, 0, 0, '2026-04-24 16:52:58', '2026-04-24 17:04:30', 1);
INSERT INTO `answer` VALUES (26, 1, 0, 4, 'jlgjelgje', 0, 0, 0, 0, 0, '2026-04-24 17:02:30', '2026-04-24 17:04:28', 1);
INSERT INTO `answer` VALUES (27, 1, 0, 4, 'hlgjelgje', 0, 0, 0, 0, 0, '2026-04-24 17:11:22', '2026-04-24 17:11:22', 0);
INSERT INTO `answer` VALUES (28, 1, 0, 4, '换了个了换个', 0, 0, 0, 0, 0, '2026-04-24 17:21:57', '2026-04-24 17:21:57', 0);
INSERT INTO `answer` VALUES (35, 17, 0, 2, '测试数据', 1, 0, 0, 0, 0, '2026-04-24 18:23:12', '2026-04-24 19:27:35', 1);

-- ----------------------------
-- Table structure for answer_like
-- ----------------------------
DROP TABLE IF EXISTS `answer_like`;
CREATE TABLE `answer_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `answer_id` bigint NOT NULL COMMENT '回答/评论ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_answer`(`user_id` ASC, `answer_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_answer_id`(`answer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '回答点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answer_like
-- ----------------------------
INSERT INTO `answer_like` VALUES (17, 1, 1, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (18, 2, 1, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (19, 3, 1, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (20, 1, 2, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (21, 2, 2, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (22, 3, 3, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (23, 1, 5, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (24, 2, 7, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);
INSERT INTO `answer_like` VALUES (25, 3, 9, '2026-04-04 22:20:04', '2026-04-04 22:20:04', 0);

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片地址',
  `course_id` bigint NULL DEFAULT NULL COMMENT '关联课程ID（点击跳转）',
  `sort` int NULL DEFAULT 0 COMMENT '排序（值越小越靠前）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '首页轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, 'JavaWeb', 'http://localhost:8088/files/image/2026/03/13/10a3deec61f04e97ae747874563bf40f_1773403667232.jpg', 103, 0, 0, '2026-03-04 15:47:16', '2026-04-24 19:06:41', 0);
INSERT INTO `banner` VALUES (2, 'SpringBoot', 'http://localhost:8088/files/image/2026/03/13/f724b4c6ce364200b254e81738605457_1773403544336.jpg', 100, 3, 0, '2026-03-10 20:15:41', '2026-04-24 19:07:29', 0);
INSERT INTO `banner` VALUES (3, 'test', 'http://localhost:8088/files/image/2026/03/13/beedec2946084b479e1606b0bae5da19_1773403787253.jpg', 115, 2, 1, '2026-03-13 00:57:59', '2026-04-24 19:07:31', 1);
INSERT INTO `banner` VALUES (4, 'Vue入门', 'http://localhost:8088/files/image/2026/03/13/f1932e75049647bb80f4fd0b10b27cb2_1773403800269.jpg', 1, 1, 1, '2026-03-13 14:55:44', '2026-03-13 20:10:05', 0);
INSERT INTO `banner` VALUES (5, 'ceshi ', 'http://localhost:8088/files/image/2026/04/24/39ea5a925319423ba4d11709b1820ad6_1777028792150.jpg', 1, 0, 1, '2026-04-24 19:06:37', '2026-04-24 19:06:37', 0);
INSERT INTO `banner` VALUES (6, ' 测试数据', 'http://localhost:8088/files/image/2026/04/24/a22348ffea9843ca8dcf94c01fd6dbce_1777028841097.jpg', 100, 0, 1, '2026-04-24 19:07:26', '2026-04-24 19:07:26', 0);

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `teacher_id` bigint NOT NULL COMMENT '讲师ID',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程封面图',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程简介',
  `level` tinyint NULL DEFAULT 1 COMMENT '课程难度：1-入门，2-初级，3-中级，4-高级',
  `status` tinyint NULL DEFAULT 0 COMMENT '课程状态：0-草稿，1-待审核，2-已发布，3-审核拒绝，4-已下架',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核拒绝原因',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `student_count` int NULL DEFAULT 0 COMMENT '学习人数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `hot_score` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '热度分数（定时任务计算：view×1 + student×10 + like×3 + favorite×5  + avg_score×review×1.5 + question×4）',
  `is_recommend` tinyint NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
  `avg_score` decimal(3, 1) NULL DEFAULT 0.0 COMMENT '平均评分（冗余，由评价写入时维护）',
  `review_count` int NULL DEFAULT 0 COMMENT '评价总数（冗余）',
  `good_review_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '好评率：4星及以上占比×100（冗余）',
  `latest_review_time` datetime NULL DEFAULT NULL COMMENT '最新评价时间（冗余）',
  `question_count` int NULL DEFAULT 0 COMMENT '问题总数（冗余，由提问/删除时维护）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE,
  INDEX `idx_student_count`(`student_count` ASC) USING BTREE,
  INDEX `idx_is_hot`(`hot_score` ASC) USING BTREE,
  INDEX `idx_is_recommend`(`is_recommend` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'Java从入门到精通1', 120, 2, 'https://images.unsplash.com/photo-1571260899304-425eee4c7efc', '本课程旨在帮助学习者全面、系统地掌握 Java 编程语言，从最基础的语法规则入手，逐步深入到面向对象编程思想以及常见开发技巧。课程内容涵盖变量与数据类型、流程控制、数组、方法、类与对象、继承与多态、异常处理、集合框架等核心知识点。\r\n\r\n在打牢基础的同时，课程还将引导学员理解 Java 的核心设计思想，并结合实际案例进行讲解，帮助学习者建立清晰的编程思维。通过循序渐进的讲解方式，即使是零基础学员也可以逐步掌握编程技能。\r\n\r\n此外，课程还包含一定的实战练习与综合案例，帮助学员将理论知识应用到实际开发中，提高代码编写能力与问题分析能力。完成本课程后，学习者将具备独立进行基础 Java 程序开发的能力，并为后续学习 Web 开发、框架技术（如 Spring Boot）等打下坚实基础。\r\n', NULL, 2, NULL, 57, 1, 1, 1, 98.15, 1, 4.7, 3, 100.00, '2026-04-04 22:26:29', 1, '2026-02-26 14:20:57', '2026-05-07 13:40:09', 0);
INSERT INTO `course` VALUES (2, 'Spring Boot实战开发1', 121, 2, 'https://picsum.photos/seed/java/400/300', '深入学习Spring Boot框架，掌握企业级应用开发技能', NULL, 4, NULL, 10, 157, 78, 45, 1.00, 1, 4.0, 3, 66.67, '2026-04-04 22:26:29', 0, '2026-02-26 14:20:57', '2026-04-24 14:28:25', 1);
INSERT INTO `course` VALUES (3, 'MySQL数据库优化', 130, 2, 'https://picsum.photos/seed/java/400/300', 'MySQL性能优化实战，索引优化、查询优化、架构优化', NULL, 0, NULL, 9, 90, 42, 28, 0.00, 1, 4.3, 3, 100.00, '2026-04-04 22:26:29', 0, '2026-02-26 14:20:57', '2026-04-24 14:28:26', 1);
INSERT INTO `course` VALUES (4, 'Python基础', 128, 2, 'https://picsum.photos/seed/java/400/300', '使用Python进行数据分析，掌握Pandas、NumPy等工具', NULL, 4, NULL, 5, 247, 113, 68, 4433.00, 1, 4.0, 3, 66.67, '2026-04-04 22:26:29', 0, '2026-02-26 14:20:57', '2026-04-24 14:28:33', 1);
INSERT INTO `course` VALUES (5, 'Vue3从零开始', 125, 2, 'https://picsum.photos/seed/java/400/300', 'Vue3全家桶开发实战，包含Vue Router、Pinia等', NULL, 4, NULL, 12, 0, 0, 0, 3277.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:20:57', '2026-04-24 14:28:30', 1);
INSERT INTO `course` VALUES (6, 'React Hooks实战', 111, 2, 'https://picsum.photos/seed/java/400/300', '深入理解React Hooks，构建现代化Web应用', NULL, 0, NULL, 120, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:20:57', '2026-04-23 23:52:31', 1);
INSERT INTO `course` VALUES (10, 'Java 入门', 120, 2, 'https://picsum.photos/seed/java/400/300', '全面系统地学习Java编程，从基础语法到高级特性，适合零基础学员', NULL, 3, '不合格', 4, 0, 0, 0, 0.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:25:09', '2026-04-23 23:52:38', 1);
INSERT INTO `course` VALUES (11, 'Spring Boot 实战2', 121, 2, 'https://picsum.photos/seed/java/400/300', '全面系统地学习Java编程，从基础语法到高级特性，适合零基础学员', NULL, 0, NULL, 11, 0, 0, 0, 0.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:25:09', '2026-04-23 23:52:36', 1);
INSERT INTO `course` VALUES (12, 'MySQL 基础', 130, 2, 'https://picsum.photos/seed/java/400/300', NULL, NULL, 4, NULL, 7, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:25:09', '2026-04-23 23:52:24', 1);
INSERT INTO `course` VALUES (13, '前端基础', 111, 2, 'https://picsum.photos/seed/java/400/300', NULL, NULL, 0, NULL, 1, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:25:09', '2026-04-23 23:52:25', 1);
INSERT INTO `course` VALUES (14, 'Redis 实战', 100, 2, 'https://picsum.photos/seed/java/400/300', NULL, NULL, 0, NULL, 19, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:25:09', '2026-04-23 23:52:27', 1);
INSERT INTO `course` VALUES (15, '微服务架构', 122, 2, 'https://picsum.photos/seed/java/400/300', NULL, NULL, 0, NULL, 19, 0, 0, 0, 0.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-02-26 14:25:09', '2026-04-23 23:52:28', 1);
INSERT INTO `course` VALUES (16, 'Java从入门到精通3', 120, 2, 'http://localhost:8088/files/image/2026/04/24/fa4e86872e804276811c7b5b59d7922c_1776967654315.bmp', '全面系统地学习Java编程，从基础语法到高级特性，适合零基础学员', NULL, 3, 'test', 2, 0, 0, 0, 20.00, 0, 4.0, 3, 66.67, '2026-04-04 22:26:29', 0, '2026-03-02 21:44:25', '2026-04-24 18:51:16', 0);
INSERT INTO `course` VALUES (100, 'Spring Boot实战开发3', 121, 2, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3', '从零开始学习Spring Boot开发企业级项目', NULL, 2, NULL, 7, 1, 1, 1, 25.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:40:51', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (101, 'Vue3前端开发入门', 125, 2, 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f', '系统学习Vue3框架与组件开发', NULL, 3, 'test', 5, 0, 0, 0, 24.35, 1, 4.3, 3, 100.00, '2026-04-04 22:26:29', 0, '2026-03-13 19:40:51', '2026-04-24 18:51:14', 0);
INSERT INTO `course` VALUES (102, 'MySQL数据库基础', 130, 2, 'https://images.unsplash.com/photo-1501504905252-473c47e087f8', '掌握MySQL数据库设计与SQL编写', NULL, 2, NULL, 5, 5, 0, 0, 55.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:40:51', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (103, 'Python数据分析实战', 128, 2, 'https://images.unsplash.com/photo-1555066931-4365d14bab8c', '使用Python进行数据分析与可视化', NULL, 4, NULL, 8, 5, 0, 0, 76.00, 0, 4.0, 3, 66.67, '2026-04-04 22:26:29', 0, '2026-03-13 19:40:51', '2026-04-24 20:46:55', 0);
INSERT INTO `course` VALUES (104, '高等数学基础课程', 140, 2, 'https://images.unsplash.com/photo-1501504905252-473c47e087f8', '大学高等数学核心知识讲解', NULL, 3, 'test', 7, 0, 0, 0, 1450.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:40:51', '2026-04-24 18:51:19', 0);
INSERT INTO `course` VALUES (105, 'UI设计入门', 180, 2, 'https://picsum.photos/seed/java/400/300', '从零学习UI界面设计与用户体验', NULL, 2, NULL, 9, 0, 0, 0, 9.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:40:51', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (106, '英语口语提升课程', 173, 2, 'https://images.unsplash.com/photo-1588702547923-7093a6c3ba33', '提高英语日常交流能力', NULL, 3, 'test', 13, 0, 0, 0, 1751.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:40:51', '2026-04-24 18:51:21', 0);
INSERT INTO `course` VALUES (107, 'React前端开发实战', 126, 2, 'https://images.unsplash.com/photo-1498050108023-c5249f4df085', '系统学习React框架开发', NULL, 2, NULL, 6, 5, 0, 0, 56.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (108, 'Java数据结构与算法', 138, 2, 'https://images.unsplash.com/photo-1519389950473-47ba0277781c', '数据结构与算法核心知识', NULL, 2, NULL, 15, 5, 0, 0, 65.00, 1, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (109, '机器学习入门', 133, 2, 'https://images.unsplash.com/photo-1555066931-4365d14bab8c', '机器学习基础理论与实践', NULL, 2, NULL, 2, 0, 0, 0, 2.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (110, 'Photoshop设计基础', 185, 2, 'https://images.unsplash.com/photo-1571260899304-425eee4c7efc', '学习Photoshop设计技巧', NULL, 2, NULL, 10, 0, 0, 0, 10.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (111, 'Figma UI设计实战', 183, 2, 'https://images.unsplash.com/photo-1498050108023-c5249f4df085', 'Figma界面设计实战', NULL, 2, NULL, 6, 0, 0, 0, 6.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (112, '英语写作提升', 174, 2, 'https://images.unsplash.com/photo-1519389950473-47ba0277781c', '提升英语写作能力', NULL, 2, NULL, 8, 0, 0, 0, 8.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 15:00:00', 0);
INSERT INTO `course` VALUES (113, '概率论基础课程', 149, 2, 'https://images.unsplash.com/photo-1501504905252-473c47e087f8', '概率论核心知识讲解', NULL, 4, NULL, 10, 0, 0, 0, 1200.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 14:43:30', 0);
INSERT INTO `course` VALUES (114, '创业思维训练', 195, 2, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3', '培养创业思维与商业模式', NULL, 4, NULL, 9, 0, 0, 0, 1100.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-03-13 19:44:09', '2026-04-24 14:43:22', 0);
INSERT INTO `course` VALUES (115, 'Vue', 100, 2, 'http://localhost:8088/files/image/2026/04/05/0f9074eceb7d4606ac7e29a8b9b52d85_1775369492550.jpg', '零基础学Vue', 1, 2, NULL, 1, 0, 0, 0, 1.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-04-05 14:12:25', '2026-04-24 18:01:15', 0);
INSERT INTO `course` VALUES (116, 'test', 103, 2, 'http://localhost:8088/files/image/2026/04/24/3c8b7c949644481c9292a52ba5098211_1776962009275.jpg', 'test', 1, 0, NULL, 0, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-04-24 00:33:32', '2026-04-24 18:01:10', 1);
INSERT INTO `course` VALUES (117, '测试数据', 100, 2, 'http://localhost:8088/files/image/2026/04/24/cfbd6412755c4090aca9ff0b2acb20bd_1777022983996.jpg', '测试数据', 1, 0, NULL, 0, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-04-24 17:29:54', '2026-04-24 18:01:09', 1);
INSERT INTO `course` VALUES (118, '测试数据', 110, 2, 'http://localhost:8088/files/image/2026/04/24/cb6641073c694c01a6d3d1ed4bb150ca_1777024365815.jpg', '测试数据', 1, 0, NULL, 0, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-04-24 17:52:51', '2026-04-24 18:01:11', 1);
INSERT INTO `course` VALUES (119, '测试数据', 110, 2, 'http://localhost:8088/files/image/2026/04/24/6ed089c7808c42bc80009541de5bc517_1777024761524.jpg', '测试数据', 1, 0, NULL, 0, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-04-24 17:59:26', '2026-04-24 18:01:08', 1);
INSERT INTO `course` VALUES (120, '测试数据', 100, 2, 'http://localhost:8088/files/image/2026/04/24/571bf2238d854f8882705da5ab6aa177_1777024958871.jpg', '测试数据', 1, 0, NULL, 0, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-04-24 18:02:43', '2026-04-24 18:02:43', 0);
INSERT INTO `course` VALUES (121, '测试数据', 110, 2, 'http://localhost:8088/files/image/2026/04/24/e48973d371064de093aa072e2a9e2a49_1777025050820.jpg', '测试数据', 1, 3, '不符合', 0, 0, 0, 0, 0.00, 0, 0.0, 0, 0.00, NULL, 0, '2026-04-24 18:04:16', '2026-04-24 20:19:29', 0);

-- ----------------------------
-- Table structure for course_category
-- ----------------------------
DROP TABLE IF EXISTS `course_category`;
CREATE TABLE `course_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID（0表示一级分类）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标URL',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 268 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_category
-- ----------------------------
INSERT INTO `course_category` VALUES (100, '计算机', 0, 2, '', '计算机技术相关课程', '2026-03-13 19:31:11', '2026-04-24 20:29:05', 0);
INSERT INTO `course_category` VALUES (101, '数学', 0, 2, NULL, '数学基础与应用', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (102, '文学', 0, 3, NULL, '文学阅读与写作', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (103, '外语', 0, 4, NULL, '语言学习课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (104, '设计', 0, 5, NULL, '视觉设计与创意', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (105, '商业', 0, 6, NULL, '商业与管理课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (110, '后端开发', 100, 1, NULL, '服务器端开发技术', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (111, '前端开发', 100, 2, NULL, 'Web前端开发技术', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (112, 'Python开发', 100, 3, NULL, 'Python相关技术', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (113, '数据库', 100, 4, NULL, '数据库技术', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (114, '人工智能', 100, 5, NULL, 'AI与机器学习', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (115, '计算机基础', 100, 6, NULL, '计算机基础知识', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (120, 'Java开发', 110, 1, NULL, 'Java语言开发', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (121, 'Spring Boot', 110, 2, NULL, 'Spring Boot框架', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (122, '微服务架构', 110, 3, NULL, 'Spring Cloud与微服务', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (123, 'HTML/CSS', 111, 1, NULL, '网页基础开发', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (124, 'JavaScript', 111, 2, NULL, 'JS核心编程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (125, 'Vue开发', 111, 3, NULL, 'Vue框架开发', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (126, 'React开发', 111, 4, NULL, 'React框架', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (127, 'Python基础', 112, 1, NULL, 'Python编程入门', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (128, '数据分析', 112, 2, NULL, 'Python数据分析', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (129, '爬虫开发', 112, 3, NULL, 'Python爬虫', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (130, 'MySQL', 113, 1, NULL, 'MySQL数据库', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (131, '数据库设计', 113, 2, NULL, '数据库建模', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (132, 'SQL性能调优', 113, 3, NULL, 'SQL性能提升', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (133, '机器学习', 114, 1, NULL, '机器学习基础', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (134, '深度学习', 114, 2, NULL, '神经网络与深度学习', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (135, '计算机视觉', 114, 3, NULL, 'CV视觉技术', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (136, '计算机网络', 115, 1, NULL, '网络原理', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (137, '操作系统', 115, 2, NULL, '操作系统基础', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (138, '数据结构', 115, 3, NULL, '数据结构算法', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (140, '高等数学', 101, 1, NULL, '微积分课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (141, '线性代数', 101, 2, NULL, '矩阵与向量', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (142, '概率统计', 101, 3, NULL, '概率与统计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (143, '数学建模', 101, 4, NULL, '数学建模', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (144, '极限与连续', 140, 1, NULL, '极限概念', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (145, '导数应用', 140, 2, NULL, '导数与微分', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (146, '积分应用', 140, 3, NULL, '积分计算', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (147, '矩阵运算', 141, 1, NULL, '矩阵基础', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (148, '特征值', 141, 2, NULL, '特征值与特征向量', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (149, '概率论基础', 142, 1, NULL, '概率基本概念', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (150, '统计学基础', 142, 2, NULL, '统计分析', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (151, '数学建模竞赛', 143, 1, NULL, '数学建模竞赛', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (152, '算法建模', 143, 2, NULL, '算法应用', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (160, '中国文学', 102, 1, NULL, '中国文学', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (161, '外国文学', 102, 2, NULL, '外国文学', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (162, '写作', 102, 3, NULL, '写作课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (163, '古代文学', 160, 1, NULL, '诗词与古文', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (164, '现代文学', 160, 2, NULL, '现代文学', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (165, '欧美文学', 161, 1, NULL, '欧美文学作品', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (166, '日本文学', 161, 2, NULL, '日本文学', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (167, '小说写作', 162, 1, NULL, '小说创作', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (168, '公文写作', 162, 2, NULL, '公文写作', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (170, '英语', 103, 1, NULL, '英语学习', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (171, '日语', 103, 2, NULL, '日语学习', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (172, '韩语', 103, 3, NULL, '韩语学习', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (173, '英语口语', 170, 1, NULL, '口语提升', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (174, '英语写作', 170, 2, NULL, '写作训练', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (175, '雅思英语', 170, 3, NULL, 'IELTS考试', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (176, '日语N5', 171, 1, NULL, '日语入门', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (177, '日语N2', 171, 2, NULL, '日语进阶', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (178, '韩语入门', 172, 1, NULL, '韩语基础', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (179, '韩语TOPIK', 172, 2, NULL, '韩语考试', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (180, 'UI设计', 104, 1, NULL, 'UI界面设计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (181, '平面设计', 104, 2, NULL, '视觉设计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (182, '三维设计', 104, 3, NULL, '3D设计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (183, 'Figma设计', 180, 1, NULL, 'Figma工具', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (184, '用户体验设计', 180, 2, NULL, 'UX设计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (185, 'Photoshop', 181, 1, NULL, 'PS设计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (186, 'Illustrator', 181, 2, NULL, 'AI设计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (187, '3D建模', 182, 1, NULL, '建模技术', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (188, '三维动画', 182, 2, NULL, '动画设计', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (190, '市场营销', 105, 1, NULL, '营销课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (191, '创业管理', 105, 2, NULL, '创业课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (192, '财务管理', 105, 3, NULL, '财务课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (193, '品牌营销', 190, 1, NULL, '品牌推广', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (194, '新媒体运营', 190, 2, NULL, '短视频运营', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (195, '创业思维', 191, 1, NULL, '创业课程', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (196, '商业模式', 191, 2, NULL, '商业模式', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (197, '财务报表', 192, 1, NULL, '财务报表', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (198, '成本管理', 192, 2, NULL, '成本控制', '2026-03-13 19:31:11', '2026-03-13 19:31:11', 0);
INSERT INTO `course_category` VALUES (265, 'test', 0, 1, '', '', '2026-04-24 20:39:22', '2026-04-24 20:39:39', 1);
INSERT INTO `course_category` VALUES (266, 'test01', 265, 1, '', '', '2026-04-24 20:39:27', '2026-04-24 20:39:27', 0);
INSERT INTO `course_category` VALUES (267, 'test03', 266, 1, '', '', '2026-04-24 20:39:32', '2026-04-24 20:39:32', 0);

-- ----------------------------
-- Table structure for course_chapter
-- ----------------------------
DROP TABLE IF EXISTS `course_chapter`;
CREATE TABLE `course_chapter`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `chapter_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父章节ID（0表示一级章节）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频URL',
  `video_duration` int NULL DEFAULT 0 COMMENT '视频时长（秒）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 181 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程章节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_chapter
-- ----------------------------
INSERT INTO `course_chapter` VALUES (100, 100, '第一章 Spring Boot基础', 0, 1, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (101, 100, '课程介绍', 100, 1, 'http://localhost:8088/files/video/2026/04/05/335040_tiny.mp4', 27, '2026-03-13 19:40:51', '2026-04-22 18:30:56', 0);
INSERT INTO `course_chapter` VALUES (102, 100, 'Spring Boot项目创建', 100, 2, 'http://localhost:8088/files/video/2026/04/05/204565-924698132_tiny.mp4', 33, '2026-03-13 19:40:51', '2026-04-22 18:31:04', 0);
INSERT INTO `course_chapter` VALUES (103, 100, '第二章 Web开发', 0, 2, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (104, 100, 'Controller开发', 103, 1, 'http://localhost:8088/files/video/2026/04/05/33194-396036988_tiny.mp4', 27, '2026-03-13 19:40:51', '2026-04-22 21:14:45', 0);
INSERT INTO `course_chapter` VALUES (105, 100, 'REST接口设计', 103, 2, 'http://localhost:8088/files/video/2026/04/05/335040_tiny.mp4', 33, '2026-03-13 19:40:51', '2026-04-22 21:14:57', 0);
INSERT INTO `course_chapter` VALUES (106, 101, '第一章 Vue3基础', 0, 1, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (107, 101, 'Vue3介绍', 106, 1, 'http://localhost:8088/files/video/2026/04/05/335040_tiny.mp4', 500, '2026-03-13 19:40:51', '2026-04-22 18:29:08', 0);
INSERT INTO `course_chapter` VALUES (108, 101, 'Composition API', 106, 2, 'http://localhost:8088/files/video/2026/04/05/204565-924698132_tiny.mp4', 850, '2026-03-13 19:40:51', '2026-04-22 18:29:14', 0);
INSERT INTO `course_chapter` VALUES (109, 101, '第二章 组件开发', 0, 2, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (110, 101, '组件基础', 109, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 900, '2026-03-13 19:40:51', '2026-03-13 19:55:23', 0);
INSERT INTO `course_chapter` VALUES (111, 101, '组件通信', 109, 2, 'http://localhost:8088/files/video/2026/04/05/33194-396036988_tiny.mp4', 760, '2026-03-13 19:40:51', '2026-04-22 18:29:18', 0);
INSERT INTO `course_chapter` VALUES (112, 102, '第一章 MySQL基础', 0, 1, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (113, 102, '数据库概念', 112, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 600, '2026-03-13 19:40:51', '2026-03-13 19:55:25', 0);
INSERT INTO `course_chapter` VALUES (114, 102, 'SQL基础语句', 112, 2, 'http://localhost:8088/files/video/2026/04/05/33194-396036988_tiny.mp4', 820, '2026-03-13 19:40:51', '2026-04-22 18:29:21', 0);
INSERT INTO `course_chapter` VALUES (115, 102, '第二章 数据库设计', 0, 2, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (116, 102, 'ER模型', 115, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 700, '2026-03-13 19:40:51', '2026-03-13 19:55:28', 0);
INSERT INTO `course_chapter` VALUES (117, 102, '表设计规范', 115, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946ahttp://localhost:8088/files/video/2026/04/05/33194-396036988_tiny.mp4994340c93f08c40_1773136973147.mp4', 650, '2026-03-13 19:40:51', '2026-04-22 18:29:22', 0);
INSERT INTO `course_chapter` VALUES (118, 103, '第一章 Python基础', 0, 1, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (119, 103, 'Python环境安装', 118, 1, 'http://localhost:8088/files/video/2026/04/05/204565-924698132_tiny.mp4', 400, '2026-03-13 19:40:51', '2026-04-22 18:29:29', 0);
INSERT INTO `course_chapter` VALUES (120, 103, 'Python语法基础', 118, 2, 'http://localhost:8088/files/video/2026/04/05/33194-396036988_tiny.mp4', 800, '2026-03-13 19:40:51', '2026-04-22 18:29:26', 0);
INSERT INTO `course_chapter` VALUES (121, 103, '第二章 数据分析', 0, 2, NULL, 0, '2026-03-13 19:40:51', '2026-03-13 19:40:51', 0);
INSERT INTO `course_chapter` VALUES (122, 103, 'Pandas基础', 121, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 900, '2026-03-13 19:40:51', '2026-03-13 19:55:35', 0);
INSERT INTO `course_chapter` VALUES (123, 103, '数据可视化', 121, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 850, '2026-03-13 19:40:51', '2026-03-13 19:55:35', 0);
INSERT INTO `course_chapter` VALUES (124, 107, '第一章 React基础', 0, 1, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (125, 107, 'React介绍', 124, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 600, '2026-03-13 19:44:09', '2026-03-13 19:55:38', 0);
INSERT INTO `course_chapter` VALUES (126, 107, 'JSX语法', 124, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 700, '2026-03-13 19:44:09', '2026-03-13 19:55:39', 0);
INSERT INTO `course_chapter` VALUES (127, 107, '第二章 React组件', 0, 2, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (128, 107, '函数组件', 127, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 800, '2026-03-13 19:44:09', '2026-03-13 19:55:41', 0);
INSERT INTO `course_chapter` VALUES (129, 107, '组件状态', 127, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 850, '2026-03-13 19:44:09', '2026-03-13 19:55:42', 0);
INSERT INTO `course_chapter` VALUES (130, 108, '第一章 数据结构', 0, 1, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (131, 108, '数组与链表', 130, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 900, '2026-03-13 19:44:09', '2026-03-13 19:55:42', 0);
INSERT INTO `course_chapter` VALUES (132, 108, '栈与队列', 130, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 850, '2026-03-13 19:44:09', '2026-03-13 19:55:43', 0);
INSERT INTO `course_chapter` VALUES (133, 108, '第二章 算法基础', 0, 2, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (134, 108, '排序算法', 133, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 920, '2026-03-13 19:44:09', '2026-03-13 19:55:44', 0);
INSERT INTO `course_chapter` VALUES (135, 108, '搜索算法', 133, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 880, '2026-03-13 19:44:09', '2026-03-13 19:55:45', 0);
INSERT INTO `course_chapter` VALUES (136, 109, '第一章 机器学习基础', 0, 1, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (137, 109, '机器学习概念', 136, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 700, '2026-03-13 19:44:09', '2026-03-13 19:55:47', 0);
INSERT INTO `course_chapter` VALUES (138, 109, '监督学习', 136, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 850, '2026-03-13 19:44:09', '2026-03-13 19:55:48', 0);
INSERT INTO `course_chapter` VALUES (139, 109, '第二章 常见算法', 0, 2, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (140, 109, '线性回归', 139, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 900, '2026-03-13 19:44:09', '2026-03-13 19:55:54', 0);
INSERT INTO `course_chapter` VALUES (141, 109, '决策树', 139, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 870, '2026-03-13 19:44:09', '2026-03-13 19:55:56', 0);
INSERT INTO `course_chapter` VALUES (142, 110, '第一章 Photoshop基础', 0, 1, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (143, 110, '界面介绍', 142, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 500, '2026-03-13 19:44:09', '2026-03-13 19:55:56', 0);
INSERT INTO `course_chapter` VALUES (144, 110, '基础工具', 142, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 650, '2026-03-13 19:44:09', '2026-03-13 19:55:57', 0);
INSERT INTO `course_chapter` VALUES (145, 110, '第二章 图像处理', 0, 2, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (146, 110, '图层操作', 145, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 700, '2026-03-13 19:44:09', '2026-03-13 19:55:58', 0);
INSERT INTO `course_chapter` VALUES (147, 110, '滤镜效果', 145, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 720, '2026-03-13 19:44:09', '2026-03-13 19:55:59', 0);
INSERT INTO `course_chapter` VALUES (148, 111, '第一章 Figma基础', 0, 1, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (149, 111, '界面介绍', 148, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 550, '2026-03-13 19:44:09', '2026-03-13 19:56:00', 0);
INSERT INTO `course_chapter` VALUES (150, 111, '组件设计', 148, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 780, '2026-03-13 19:44:09', '2026-03-13 19:56:01', 0);
INSERT INTO `course_chapter` VALUES (151, 111, '第二章 UI设计实践', 0, 2, NULL, 0, '2026-03-13 19:44:09', '2026-03-13 19:44:09', 0);
INSERT INTO `course_chapter` VALUES (152, 111, '页面布局', 151, 1, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 820, '2026-03-13 19:44:09', '2026-03-13 19:56:01', 0);
INSERT INTO `course_chapter` VALUES (153, 111, '设计规范', 151, 2, 'http://localhost:8088/files/video/2026/03/10/9411641e6cad4946a994340c93f08c40_1773136973147.mp4', 760, '2026-03-13 19:44:09', '2026-03-13 19:56:03', 0);
INSERT INTO `course_chapter` VALUES (154, 115, '第一章 Vue3基础', 0, 0, '', 0, '2026-04-05 14:12:40', '2026-04-05 14:15:38', 0);
INSERT INTO `course_chapter` VALUES (155, 115, 'Vue3介绍', 154, 0, 'http://localhost:8088/files/video/2026/04/05/132dee2623f64144a72c0ada0be5d630_1775369597755.mp4', 16, '2026-04-05 14:13:19', '2026-04-05 14:15:47', 0);
INSERT INTO `course_chapter` VALUES (156, 115, 'Composition API', 154, 0, 'http://localhost:8088/files/video/2026/04/05/857a441ff9ad40119aacc0d6385e1780_1775369677177.mp4', 58, '2026-04-05 14:14:38', '2026-04-05 14:15:54', 0);
INSERT INTO `course_chapter` VALUES (157, 115, '第二章 组件开发', 0, 0, '', 0, '2026-04-05 14:16:02', '2026-04-05 14:16:02', 0);
INSERT INTO `course_chapter` VALUES (158, 115, '组件基础', 157, 0, 'http://localhost:8088/files/video/2026/04/05/1c9632a6ee994b4cbbd95504d0e8b342_1775369770949.mp4', 25, '2026-04-05 14:16:12', '2026-04-05 14:16:12', 0);
INSERT INTO `course_chapter` VALUES (159, 115, '组件通信', 157, 0, 'http://localhost:8088/files/video/2026/04/05/59377d81548e413ea98ae9e67cf19f8e_1775369785805.mp4', 2, '2026-04-05 14:16:27', '2026-04-05 14:16:27', 0);
INSERT INTO `course_chapter` VALUES (160, 1, '开发环境', 0, 1, '', 0, '2026-04-23 23:56:44', '2026-04-23 23:59:06', 0);
INSERT INTO `course_chapter` VALUES (161, 1, 'JDK概述', 160, 1, 'http://localhost:8088/files/video/2026/04/23/1053a5c2f0c14fd98ce7041c1acf5348_1776959873914.mp4', 39, '2026-04-23 23:57:56', '2026-04-23 23:58:30', 0);
INSERT INTO `course_chapter` VALUES (162, 1, 'Java概述', 160, 0, 'http://localhost:8088/files/video/2026/04/23/4a7e20124dd6435e803c6e4d4d5c7fd4_1776959901883.mp4', 33, '2026-04-23 23:58:23', '2026-04-23 23:59:48', 1);
INSERT INTO `course_chapter` VALUES (163, 1, 'Java概述', 0, 0, '', 0, '2026-04-23 23:59:01', '2026-04-23 23:59:01', 0);
INSERT INTO `course_chapter` VALUES (164, 1, 'Java概述', 163, 0, 'http://localhost:8088/files/video/2026/04/23/5d12020c8659449493fd7461629d3b8d_1776959984294.mp4', 33, '2026-04-23 23:59:45', '2026-04-23 23:59:45', 0);
INSERT INTO `course_chapter` VALUES (165, 1, 'Java使用场景', 163, 0, 'http://localhost:8088/files/video/2026/04/24/66aef7d90a464945bc7d97dbbc45d7a7_1776960032264.mp4', 27, '2026-04-24 00:00:33', '2026-04-24 00:00:33', 0);
INSERT INTO `course_chapter` VALUES (166, 1, 'JDK安装', 160, 2, 'http://localhost:8088/files/video/2026/04/24/a0cddea8bf61421a8ae87ca38e2b7051_1776960056350.mp4', 27, '2026-04-24 00:00:57', '2026-04-24 00:01:07', 0);
INSERT INTO `course_chapter` VALUES (167, 1, 'IDEA安装', 160, 3, 'http://localhost:8088/files/video/2026/04/24/93b19b6c76dd48f2ba5c56e09f654a3b_1776960077042.mp4', 33, '2026-04-24 00:01:18', '2026-04-24 00:01:22', 0);
INSERT INTO `course_chapter` VALUES (168, 116, 'test', 0, 0, '', 0, '2026-04-24 00:33:36', '2026-04-24 00:33:36', 0);
INSERT INTO `course_chapter` VALUES (169, 117, '测试数据', 0, 0, '', 0, '2026-04-24 17:30:03', '2026-04-24 17:30:03', 0);
INSERT INTO `course_chapter` VALUES (170, 117, '测试数据', 169, 0, 'http://localhost:8088/files/video/2026/04/24/5457db34cc554a65b7dc7e52dd6b8b8f_1777023016275.mp4', 33, '2026-04-24 17:30:18', '2026-04-24 17:30:18', 0);
INSERT INTO `course_chapter` VALUES (171, 117, '测试数据2', 169, 0, 'http://localhost:8088/files/video/2026/04/24/52867e7da66c4270bcbf6e04c413df7c_1777023029957.mp4', 27, '2026-04-24 17:30:31', '2026-04-24 17:30:31', 0);
INSERT INTO `course_chapter` VALUES (172, 118, '测试数据', 0, 0, '', 0, '2026-04-24 17:53:02', '2026-04-24 17:53:02', 0);
INSERT INTO `course_chapter` VALUES (173, 118, '测试数据1', 172, 0, 'http://localhost:8088/files/video/2026/04/24/30f5fbf1b6d04a05baa9dc655c183c02_1777024394586.mp4', 33, '2026-04-24 17:53:15', '2026-04-24 17:53:15', 0);
INSERT INTO `course_chapter` VALUES (174, 118, 'test', 172, 0, 'http://localhost:8088/files/video/2026/04/24/2b3d74a6fcb54d468d4763f2fb3c2163_1777024411385.mp4', 27, '2026-04-24 17:53:33', '2026-04-24 17:53:33', 0);
INSERT INTO `course_chapter` VALUES (175, 119, '测试数据', 0, 0, '', 0, '2026-04-24 17:59:35', '2026-04-24 17:59:35', 0);
INSERT INTO `course_chapter` VALUES (176, 119, '测试数据1', 175, 0, 'http://localhost:8088/files/video/2026/04/24/ee0eab649a85493e8736d7298675f2ff_1777024788349.mp4', 33, '2026-04-24 17:59:49', '2026-04-24 17:59:49', 0);
INSERT INTO `course_chapter` VALUES (177, 121, '测试数据', 0, 0, '', 0, '2026-04-24 18:04:23', '2026-04-24 18:04:23', 0);
INSERT INTO `course_chapter` VALUES (178, 121, '测试数据1', 177, 0, 'http://localhost:8088/files/video/2026/04/24/fa00c2fbb74c403385ab3f9eb15c64d2_1777025073986.mp4', 33, '2026-04-24 18:04:35', '2026-04-24 18:04:35', 0);
INSERT INTO `course_chapter` VALUES (179, 16, '测试数据', 0, 0, '', 0, '2026-04-24 18:08:20', '2026-04-24 18:08:20', 0);
INSERT INTO `course_chapter` VALUES (180, 16, '测试数据1', 179, 0, 'http://localhost:8088/files/video/2026/04/24/5bef750be01442219c8623e1c2f8e855_1777025309071.mp4', 33, '2026-04-24 18:08:30', '2026-04-24 18:08:30', 0);

-- ----------------------------
-- Table structure for course_enrollment
-- ----------------------------
DROP TABLE IF EXISTS `course_enrollment`;
CREATE TABLE `course_enrollment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `enroll_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `last_learn_time` datetime NULL DEFAULT NULL COMMENT '最后学习时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_enroll_time`(`enroll_time` ASC) USING BTREE,
  INDEX `idx_last_learn_time`(`last_learn_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_enrollment
-- ----------------------------
INSERT INTO `course_enrollment` VALUES (29, 4, 100, '2026-04-04 22:03:31', '2026-04-24 01:11:31', '2026-04-04 22:03:31', '2026-04-24 01:11:31', 0);
INSERT INTO `course_enrollment` VALUES (70, 103, 102, '2026-03-23 09:41:56', '2026-03-30 12:54:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (71, 103, 103, '2026-03-31 20:24:56', '2026-04-04 11:59:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (72, 103, 107, '2026-03-27 07:52:56', NULL, '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (73, 103, 108, '2026-04-03 05:09:56', '2026-04-03 08:56:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (74, 104, 102, '2026-03-29 08:31:56', '2026-03-26 10:44:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (75, 104, 103, '2026-04-04 12:53:56', '2026-04-02 14:01:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (76, 104, 107, '2026-03-25 01:55:56', NULL, '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (77, 104, 108, '2026-03-30 17:20:56', '2026-03-29 08:34:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (78, 105, 102, '2026-03-21 11:46:56', '2026-03-24 07:58:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (79, 105, 103, '2026-03-30 04:37:56', NULL, '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (80, 105, 107, '2026-04-01 23:02:56', '2026-04-04 09:24:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (81, 105, 108, '2026-03-26 06:14:56', '2026-03-28 11:39:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (82, 106, 102, '2026-03-28 03:59:56', NULL, '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (83, 106, 103, '2026-03-24 10:22:56', '2026-03-27 07:49:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (84, 106, 107, '2026-04-03 00:31:56', '2026-04-01 08:55:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (85, 106, 108, '2026-03-22 06:03:56', NULL, '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (86, 107, 102, '2026-03-30 21:36:56', '2026-04-04 12:50:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (87, 107, 103, '2026-03-27 02:27:56', NULL, '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (88, 107, 107, '2026-04-04 09:01:56', '2026-04-05 09:44:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (89, 107, 108, '2026-03-24 18:19:56', '2026-03-30 06:53:56', '2026-04-05 15:04:56', '2026-04-05 15:04:56', 0);
INSERT INTO `course_enrollment` VALUES (90, 4, 1, '2026-04-24 00:26:22', '2026-05-07 13:40:09', '2026-04-24 00:26:22', '2026-05-07 13:40:09', 0);

-- ----------------------------
-- Table structure for course_favorite
-- ----------------------------
DROP TABLE IF EXISTS `course_favorite`;
CREATE TABLE `course_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_favorite
-- ----------------------------
INSERT INTO `course_favorite` VALUES (25, 4, 100, '2026-04-04 22:03:33', '2026-04-24 00:26:12', 0);
INSERT INTO `course_favorite` VALUES (26, 4, 1, '2026-04-22 17:36:10', '2026-04-24 16:39:30', 0);

-- ----------------------------
-- Table structure for course_like
-- ----------------------------
DROP TABLE IF EXISTS `course_like`;
CREATE TABLE `course_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_like
-- ----------------------------
INSERT INTO `course_like` VALUES (25, 4, 100, '2026-04-04 22:03:33', '2026-04-24 00:26:11', 0);
INSERT INTO `course_like` VALUES (26, 4, 1, '2026-04-22 17:36:09', '2026-04-24 16:39:29', 0);

-- ----------------------------
-- Table structure for course_review
-- ----------------------------
DROP TABLE IF EXISTS `course_review`;
CREATE TABLE `course_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `user_id` bigint NOT NULL COMMENT '评价用户ID',
  `rating` tinyint NOT NULL COMMENT '评分 1-5',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价内容',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除（用户/管理员删除时物理DELETE）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_like_count`(`like_count` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_review
-- ----------------------------
INSERT INTO `course_review` VALUES (1, 1, 105, 5, '课程讲得非常清晰，收获很大', 2, '2026-04-04 22:26:29', '2026-04-24 00:31:32', 0);
INSERT INTO `course_review` VALUES (2, 1, 104, 4, '内容不错，就是节奏稍微快了一点', 1, '2026-04-04 22:26:29', '2026-04-24 00:31:29', 0);
INSERT INTO `course_review` VALUES (3, 1, 103, 5, '非常推荐这门课程，讲解深入浅出', 0, '2026-04-04 22:26:29', '2026-04-24 00:11:22', 0);
INSERT INTO `course_review` VALUES (4, 16, 104, 4, '整体不错，案例比较实用', 1, '2026-04-04 22:26:29', '2026-04-24 00:31:44', 0);
INSERT INTO `course_review` VALUES (5, 16, 105, 3, '基础部分很好，高级部分稍微简单', 0, '2026-04-04 22:26:29', '2026-04-24 00:31:52', 0);
INSERT INTO `course_review` VALUES (6, 16, 103, 5, '老师讲解非常清楚，很适合入门', 2, '2026-04-04 22:26:29', '2026-04-24 00:30:16', 0);
INSERT INTO `course_review` VALUES (7, 101, 104, 5, '项目实战部分特别好', 1, '2026-04-04 22:26:29', '2026-04-24 00:31:49', 0);
INSERT INTO `course_review` VALUES (8, 101, 105, 4, '课程内容丰富，但有些地方可以更详细', 0, '2026-04-04 22:26:29', '2026-04-24 00:31:54', 0);
INSERT INTO `course_review` VALUES (9, 101, 103, 4, '总体不错，适合进阶学习', 1, '2026-04-04 22:26:29', '2026-04-24 00:30:18', 0);
INSERT INTO `course_review` VALUES (10, 103, 104, 3, '内容还可以，但深度一般', 0, '2026-04-04 22:26:29', '2026-04-24 00:31:50', 0);
INSERT INTO `course_review` VALUES (11, 103, 105, 5, '非常好的课程，五星推荐', 2, '2026-04-04 22:26:29', '2026-04-24 00:31:55', 0);
INSERT INTO `course_review` VALUES (12, 103, 103, 4, '讲得比较清楚，适合新手', 1, '2026-04-04 22:26:29', '2026-04-24 00:30:22', 0);

-- ----------------------------
-- Table structure for course_review_like
-- ----------------------------
DROP TABLE IF EXISTS `course_review_like`;
CREATE TABLE `course_review_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `review_id` bigint NOT NULL COMMENT '评价ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-已点赞，1-已取消',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_review`(`user_id` ASC, `review_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_review_id`(`review_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程评价点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_review_like
-- ----------------------------
INSERT INTO `course_review_like` VALUES (16, 2, 1, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (17, 3, 1, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (18, 1, 2, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (19, 2, 4, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (20, 1, 6, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (21, 2, 6, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (22, 3, 7, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (23, 1, 9, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (24, 1, 11, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (25, 3, 11, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);
INSERT INTO `course_review_like` VALUES (26, 2, 12, '2026-04-04 22:26:29', '2026-04-04 22:26:29', 0);

-- ----------------------------
-- Table structure for learning_progress
-- ----------------------------
DROP TABLE IF EXISTS `learning_progress`;
CREATE TABLE `learning_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `last_position` int NULL DEFAULT 0 COMMENT '最后播放位置（秒）',
  `max_position` int NULL DEFAULT 0 COMMENT '历史最高播放位置（秒）',
  `duration` int NULL DEFAULT 0 COMMENT '已学习时长（秒）',
  `is_completed` tinyint NULL DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course_chapter`(`user_id` ASC, `course_id` ASC, `chapter_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_chapter_id`(`chapter_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_progress
-- ----------------------------
INSERT INTO `learning_progress` VALUES (26, 4, 100, 101, 0, 24, 190, 0, '2026-04-04 22:03:44', '2026-04-25 00:33:55', 0);
INSERT INTO `learning_progress` VALUES (27, 4, 100, 102, 9, 9, 220, 0, '2026-04-04 22:06:22', '2026-04-25 00:33:56', 0);
INSERT INTO `learning_progress` VALUES (28, 103, 102, 1001, 120, 300, 300, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (29, 103, 103, 1002, 500, 600, 600, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (30, 103, 107, 1003, 60, 200, 200, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (31, 103, 108, 1004, 800, 800, 800, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (32, 104, 102, 1001, 50, 200, 200, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (33, 104, 103, 1002, 600, 600, 600, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (34, 104, 107, 1003, 0, 100, 100, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (35, 104, 108, 1004, 300, 500, 500, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (36, 105, 102, 1001, 400, 400, 400, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (37, 105, 103, 1002, 100, 300, 300, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (38, 105, 107, 1003, 700, 700, 700, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (39, 105, 108, 1004, 200, 400, 400, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (40, 106, 102, 1001, 0, 100, 100, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (41, 106, 103, 1002, 250, 500, 500, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (42, 106, 107, 1003, 800, 800, 800, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (43, 106, 108, 1004, 100, 300, 300, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (44, 107, 102, 1001, 600, 600, 600, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (45, 107, 103, 1002, 100, 200, 200, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (46, 107, 107, 1003, 300, 500, 500, 0, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (47, 107, 108, 1004, 900, 900, 900, 1, '2026-04-05 14:53:13', '2026-04-05 14:53:13', 0);
INSERT INTO `learning_progress` VALUES (48, 4, 100, 105, 0, 0, 10, 0, '2026-04-22 18:28:04', '2026-04-25 00:33:56', 0);
INSERT INTO `learning_progress` VALUES (49, 4, 100, 104, 2, 2, 30, 0, '2026-04-22 18:28:25', '2026-04-25 00:33:58', 0);
INSERT INTO `learning_progress` VALUES (50, 4, 1, 164, 20, 20, 340, 1, '2026-04-24 00:26:34', '2026-04-25 00:37:48', 0);
INSERT INTO `learning_progress` VALUES (51, 4, 1, 165, 4, 18, 400, 0, '2026-04-24 13:20:10', '2026-04-25 00:38:02', 0);
INSERT INTO `learning_progress` VALUES (52, 4, 1, 161, 23, 23, 290, 0, '2026-04-24 16:02:13', '2026-05-07 13:40:09', 0);
INSERT INTO `learning_progress` VALUES (53, 4, 1, 166, 10, 10, 40, 1, '2026-04-25 00:32:27', '2026-04-25 00:33:52', 0);
INSERT INTO `learning_progress` VALUES (54, 4, 1, 167, 7, 7, 20, 1, '2026-04-25 00:33:35', '2026-04-25 00:34:01', 0);

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `user_id` bigint NOT NULL COMMENT '提问用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题内容',
  `latest_answer_id` bigint NULL DEFAULT NULL COMMENT '最新的一个回答的id',
  `answer_count` int NULL DEFAULT 0 COMMENT '问题下的回答数量，不包括评论',
  `has_teacher_answer` tinyint NULL DEFAULT 0 COMMENT '是否有讲师回答：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_has_teacher_answer`(`has_teacher_answer` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '问答表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 1, 105, '这门课适合零基础吗？', '完全没有基础可以学习吗？', 30, 4, 1, '2026-04-04 22:20:04', '2026-04-24 18:13:25', 0);
INSERT INTO `question` VALUES (2, 1, 103, '课程有项目实战吗？', '有没有完整项目案例？', 23, 2, 0, '2026-04-04 22:20:04', '2026-04-24 18:13:11', 0);
INSERT INTO `question` VALUES (3, 2, 4, '学习周期多久？', '大概需要多久可以学完？', NULL, 0, 0, '2026-04-04 22:20:04', '2026-04-23 23:34:44', 1);
INSERT INTO `question` VALUES (4, 3, 1, '是否提供源码？', '课程中的案例是否提供源码下载？', 6, 1, 0, '2026-04-04 22:20:04', '2026-04-23 23:34:42', 1);
INSERT INTO `question` VALUES (12, 1, 4, 'soegjegj', 'gjelgjelge', NULL, 0, 0, '2026-04-24 16:47:11', '2026-04-24 16:48:11', 1);
INSERT INTO `question` VALUES (13, 1, 4, 'ljslfjelgje', 'jlgjelgegege', NULL, 0, 0, '2026-04-24 16:50:02', '2026-04-24 17:03:38', 1);
INSERT INTO `question` VALUES (14, 1, 4, 'hlgeglehgle', 'ghelgjlegjle', NULL, 0, 0, '2026-04-24 16:52:48', '2026-04-24 17:03:37', 1);
INSERT INTO `question` VALUES (15, 1, 4, 'jlgjelgej', 'lgjelgjelge', NULL, 0, 0, '2026-04-24 17:02:21', '2026-04-24 17:03:35', 1);
INSERT INTO `question` VALUES (16, 1, 4, 'hgelgjlege', 'jlejglejge', NULL, 0, 0, '2026-04-24 17:11:15', '2026-04-24 17:16:17', 1);
INSERT INTO `question` VALUES (17, 1, 4, 'hfleglehgle', 'fjelgjeljgel', 35, 0, 1, '2026-04-24 17:21:50', '2026-04-24 19:27:39', 1);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码（STUDENT/TEACHER/ADMIN）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '管理员', 'ADMIN', '系统管理员', '2026-02-26 14:16:40', '2026-02-26 14:16:40', 0);
INSERT INTO `role` VALUES (2, '讲师', 'TEACHER', '课程讲师', '2026-02-26 14:16:40', '2026-02-26 14:16:40', 0);
INSERT INTO `role` VALUES (3, '学生', 'STUDENT', '普通学生', '2026-02-26 14:16:40', '2026-02-26 14:16:40', 0);

-- ----------------------------
-- Table structure for teacher_application
-- ----------------------------
DROP TABLE IF EXISTS `teacher_application`;
CREATE TABLE `teacher_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `education` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学历',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
  `teaching_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教学经验描述',
  `certificate_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资质证书URL（多个用逗号分隔）',
  `status` tinyint NULL DEFAULT NULL COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝，3-修改待审核，4.已取消讲师资格',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `auditor_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '讲师申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher_application
-- ----------------------------
INSERT INTO `teacher_application` VALUES (1, 2, 'test', '440885918930535322', '18930535322', '18930535322@qq.com', '硕士', 'test', 'test', 'http://localhost:8088/files/image/2026/03/12/9ff730ab949240f8b025849d9557f65a_1773245957823.png,http://localhost:8088/files/image/2026/03/12/b9246ddb487f4d5a83fc63fe866a2e32_1773246006385.png', 1, '', 1, '2026-03-10 21:50:24', '2026-03-04 18:24:49', '2026-03-16 16:18:52', 0);
INSERT INTO `teacher_application` VALUES (3, 4, '测试数据', '440883190359305646', '18930704853', '', '本科', '测试数据', '测试数据', 'http://localhost:8088/files/image/2026/04/22/37838f42615c48708ad090a678d57c47_1776850674784.jpg', 1, NULL, 1, '2026-04-22 17:39:33', '2026-04-22 17:38:54', '2026-04-24 00:21:45', 1);
INSERT INTO `teacher_application` VALUES (4, 4, '测试数据', '440883190359305646', '18930704853', '18930535389@qq.com', '硕士', '测试数据', '测试数据', 'http://localhost:8088/files/image/2026/04/24/d8b8d892d4e847aabec27d607e410a0c_1776961363977.jpg', 0, NULL, NULL, NULL, '2026-04-24 00:22:48', '2026-04-24 00:23:17', 1);
INSERT INTO `teacher_application` VALUES (5, 4, '测试数据', '440883190359305646', '18930704853', '18930535389@qq.com', '本科', '测试数据', '测试数据', 'http://localhost:8088/files/image/2026/04/24/51339e8a1b5c41ef895569ecbabbfb27_1776961435577.jpg', 0, NULL, NULL, NULL, '2026-04-24 00:23:57', '2026-04-24 00:24:13', 1);
INSERT INTO `teacher_application` VALUES (6, 4, 'test', '540053058358305038', '18930704853', '2894794034@qq.com', '本科', 'test', 'test', 'http://localhost:8088/files/image/2026/04/24/fe449222bab04dd1a57b64cdec24082a_1777017968893.jpg', 0, NULL, NULL, NULL, '2026-04-24 16:06:13', '2026-04-24 16:06:57', 1);
INSERT INTO `teacher_application` VALUES (7, 4, '测试数据', '537438358305385333', '18930704853', '2894794034@qq.com', '硕士', '测试数据', '测试数据', 'http://localhost:8088/files/image/2026/04/24/a01c4b7deab74d1b879a5ac6b09049ce_1777018052922.jpg', 0, NULL, NULL, NULL, '2026-04-24 16:07:34', '2026-04-24 16:07:41', 1);
INSERT INTO `teacher_application` VALUES (8, 4, '测试数据', '275030830740840705', '18930704853', '2894794034@qq.com', '本科', 'jlgjegle', 'test\n', 'http://localhost:8088/files/image/2026/04/24/485e0c9a6abc494baad52ddd3c80bc00_1777022681506.jpg', 1, NULL, 1, '2026-04-24 20:19:45', '2026-04-24 17:24:47', '2026-04-24 20:19:45', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-保密，1-男，2-女',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'http://localhost:8088/files/image/2026/03/01/eb3a49aeb06143ccbfe08f01d8299bed_1772372950585.jpg', 'admin@qq.com', '', 1, 1, '2026-02-26 14:16:40', '2026-04-24 00:18:20', 0);
INSERT INTO `user` VALUES (2, 'teacher01', 'e10adc3949ba59abbe56e057f20f883e', '张老师', 'https://api.dicebear.com/7.x/avataaars/svg?seed=teacher01', 'teacher01@qq.com', '', 1, 1, '2026-02-26 14:16:40', '2026-04-04 23:52:27', 0);
INSERT INTO `user` VALUES (4, 'user100', 'e10adc3949ba59abbe56e057f20f883e', 'student01', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student01', 'student01@qq.com', '', 1, 1, '2026-02-26 14:16:40', '2026-04-24 00:21:16', 0);
INSERT INTO `user` VALUES (103, 'user103', 'e10adc3949ba59abbe56e057f20f883e', 'student02', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student02', 'user103@test.com', NULL, 0, 1, '2026-04-05 14:53:13', '2026-04-24 19:38:49', 1);
INSERT INTO `user` VALUES (104, 'user104', 'e10adc3949ba59abbe56e057f20f883e', 'student03', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student03', 'user104@test.com', NULL, 0, 0, '2026-04-05 14:53:13', '2026-04-24 19:38:52', 0);
INSERT INTO `user` VALUES (105, 'user105', 'e10adc3949ba59abbe56e057f20f883e', 'student03', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student04', 'user105@test.com', NULL, 0, 1, '2026-04-05 14:53:13', '2026-04-24 00:31:21', 0);
INSERT INTO `user` VALUES (106, 'user106', '123456', 'student04', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student05', 'user106@test.com', NULL, 0, 1, '2026-04-05 14:53:13', '2026-04-05 14:58:00', 0);
INSERT INTO `user` VALUES (107, 'user107', '123456', 'student05', 'https://api.dicebear.com/7.x/avataaars/svg?seed=student06', 'user107@test.com', NULL, 0, 1, '2026-04-05 14:53:13', '2026-04-05 14:58:04', 0);
INSERT INTO `user` VALUES (110, 'test', 'e10adc3949ba59abbe56e057f20f883e', 'test', NULL, '2739543850@qq.com', NULL, 0, 1, '2026-04-24 19:38:39', '2026-04-24 19:38:39', 0);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (19, 2, 2, '2026-03-13 17:38:11');
INSERT INTO `user_role` VALUES (22, 2, 3, '2026-03-16 16:18:45');
INSERT INTO `user_role` VALUES (25, 104, 3, '2026-04-22 18:53:57');
INSERT INTO `user_role` VALUES (26, 105, 3, '2026-04-22 18:54:03');
INSERT INTO `user_role` VALUES (27, 106, 3, '2026-04-22 18:54:07');
INSERT INTO `user_role` VALUES (28, 107, 3, '2026-04-22 18:54:12');
INSERT INTO `user_role` VALUES (31, 1, 1, '2026-04-24 00:18:57');
INSERT INTO `user_role` VALUES (32, 4, 3, '2026-04-24 00:19:10');
INSERT INTO `user_role` VALUES (34, 108, 3, '2026-04-24 19:31:06');
INSERT INTO `user_role` VALUES (35, 110, 3, '2026-04-24 19:38:39');
INSERT INTO `user_role` VALUES (36, 103, 3, '2026-04-24 19:38:46');
INSERT INTO `user_role` VALUES (37, 103, 2, '2026-04-24 19:38:46');
INSERT INTO `user_role` VALUES (38, 4, 2, '2026-04-24 20:19:45');

SET FOREIGN_KEY_CHECKS = 1;
