# MiniMart POS 超市收银

软件工程实践作业，小型超市收银系统。英文目录名还是 `ProjectPos_2026`。

## 功能

- 收银：录入商品、累计金额、现金支付、找零、小票、库存减少
- 退货：登记退货、退款合计、退货单、库存加回去
- 同一商品重复录入会把数量叠到一行
- 目录按钮上显示剩余库存

## 怎么运行

需要 Java 8，在项目根目录执行：

```
compile.bat
java -cp bin pos.Main
```

或者：

```
javac -encoding UTF-8 -d bin src/pos/domain/*.java src/pos/patterns/*.java src/pos/ui/*.java src/pos/Main.java src/pos/test/*.java
java -cp bin pos.test.SaleTest
java -cp bin pos.Main
```

先收银，再退货。

## 代码放哪

- `src/pos/ui` 窗口，只负责显示和点按钮
- `src/pos/domain` 商店、收银机、销售、退货
- `src/pos/patterns` 付款工厂、小票打印
- `data/catalog.csv` 商品表

Register 接着界面过来的操作。Sale 自己算合计，也负责创建销售明细。
