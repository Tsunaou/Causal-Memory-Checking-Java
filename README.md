# Causal-Memory-Checking-Java



## 可以优化的地方
- [x] index不一定要用原生的index，由于有一些invoke可以忽略，实际上可以自己赋值  
- [ ] v 的初始值
- [ ] CausalPast中的≤符号
- [ ] HBo的计算和检验可以多线程完成，一旦检测到失败就退出。