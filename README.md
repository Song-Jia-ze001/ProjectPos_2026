# MiniMart POS

小型超市收银系统，作业用。Java 8，窗口是 Swing 写的。

## 功能

- 收银：输入商品编号或点下面目录，累计金额，收现金，找零，打小票
- 退货：选要退的商品，算出应退金额，库存加回去
- 同一件商品再点一次，数量会加在一起，不会多出一行
- 商品按钮上能看到剩余库存，卖出或退货后会跟着变
- 商品数据放在 `data/catalog.csv`

## 运行

先装好 JDK 8，在本目录打开命令行：

```
compile.bat
java -cp bin pos.Main
```

`compile.bat` 会编译，再跑一遍合计和找零的测试。

也可以自己编译：

```
javac -encoding UTF-8 -d bin src/pos/domain/*.java src/pos/patterns/*.java src/pos/ui/*.java src/pos/Main.java src/pos/test/*.java
java -cp bin pos.test.SaleTest
java -cp bin pos.Main
```

窗口打开后先收银，再切到 Return 退货。

## 目录

```
data/catalog.csv          商品编号、名称、单价、库存
src/pos/Main.java         入口
src/pos/ui/POSFrame.java  收银窗口
src/pos/domain/           商店、收银机、销售、退货
src/pos/patterns/         付款工厂、小票打印
src/pos/test/SaleTest.java 合计和找零测试
compile.bat               编译并跑测试
```

## 商品

`catalog.csv` 现在有水果、饮料、日用品这些，比如：

| 编号 | 名称 | 单价 | 库存 |
|------|------|------|------|
| 1001 | Apple | 3.50 | 80 |
| 1003 | Milk 1L | 6.80 | 40 |
| 1008 | Mineral Water | 2.00 | 100 |
| 2001 | Shampoo | 18.50 | 20 |

要加商品的话，按 `itemId,name,price,stock` 往 csv 后面加一行就行。
