# Causal-Memory-Checking-Java



## 可以优化的地方
- [x] index不一定要用原生的index，由于有一些invoke可以忽略，实际上可以自己赋值  
- [ ] v 的初始值
- [ ] CausalPast中的≤符号
- [ ] HBo的计算和检验可以多线程完成，一旦检测到失败就退出。

max = 500
单线程 325220 ms
优化 closure后 84116

- PO 不会有自环 `PO[i][i] =0`
- RF 不会有自环 `RF[i][i] =0`
- CO 是PO和RF并集的传递闭包， 若PO和RF都没有自环，则在CC的情况下，显然CO中`CO[i][i]`