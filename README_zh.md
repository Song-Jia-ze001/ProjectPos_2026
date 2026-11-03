# ProjectPos_2026 — MiniMart 超市收银系统

软件工程实践作业：用 Scrum 与面向对象分析设计（Larman）实现的小型超市收银系统。

英文说明见 [README.md](README.md)。

## 功能

- **收银（Process Sale）**：录入商品、累计金额、现金支付、找零、小票、库存减少
- **退货（Handle Returns）**：登记退货商品、退款合计、退货单、库存恢复

## 如何运行

需要 Java 8。在本目录执行：

```
compile.bat
java -cp bin pos.Main
```

或：

```
javac -encoding UTF-8 -d bin src/pos/domain/*.java src/pos/patterns/*.java src/pos/ui/*.java src/pos/Main.java src/pos/test/*.java
java -cp bin pos.test.SaleTest
java -cp bin pos.Main
```

## 设计

分层架构：`pos.ui` → `pos.domain` → `pos.patterns` / 技术服务。

GRASP：Register 是 Controller；Sale 是 SalesLineItem 的 Creator，也是 `getTotal` 的 Information Expert。

GoF：`PaymentFactory`（Simple Factory），`ReceiptPrinterAdapter`（Adapter）。

## 仓库约定

远程仓库名：`ProjectPos_2026`。每人使用独立分支；完成后合并到 `main`；**不要删除分支**。
