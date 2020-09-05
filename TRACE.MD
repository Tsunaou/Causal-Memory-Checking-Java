
## 20200817 mongo-causal-register-wc-:majority-rc-:majority-ti-600-sd-2-cry-100-wn-200-rn-200-cpk-10-no-nemesis
### WriteCOInitRead and WriteHBInitRead
**majority, majority, 200 write, 200 read and 10 clients per key, no nemesis**

:type :ok, :f :write, :value [7 13],  :process 78, :time 1170613920,  :position 6861918774639984646, :link nil, :index 4
2020-08-17 19:49:31,509{GMT}    :type :ok, :f :write, :value [7 118], :process 78, :time 7092429661,  :position 6861918800409788429, :link 6861918791819853827, :index 246
2020-08-17 19:49:32,480{GMT}    :type :ok, :f :read,  :value [7 118], :process 74, :time 8063030485,  :position 6861918804704755713, :link 6861918787524886534, :index 263
2020-08-17 19:50:32,729{GMT}    :type :ok, :f :read,  :value [7 0],   :process 74, :time 66691192608, :position 6861919053812858894, :link 6861919049517891585, :index 3700


## 20200817 mongo-causal-register-wc-:majority-rc-:majority-ti-600-sd-2-cry-100-wn-200-rn-200-cpk-25-node-failure
### WriteCOInitRead and WriteHBInitRead
**majority, majority, 200 write, 200 read and 25 clients per key, node-failure**

:type :ok, :f :write, :value [4 93], :process 238, :time 77500911824, :position 6861896964796055557, :link nil, :index 1352
:type :ok, :f :read, :value [4 93], :process 139, :time 77495038138, :position 6861896969091022850, :link 6861896956206120967, :index 1328
:type :ok, :f :write, :value [4 114], :process 139, :time 81461541304, :position 6861896990565859337, :link 6861896981975924754, :index 1557
:type :ok, :f :read, :value [4 114], :process 245, :time 81713755272, :position 6861896990565859343, :link 6861896990565859330, :index 1583
:type :ok, :f :read,  :value [4 0],  :process 245, :time 85813590122, :position 6861897007745728532, :link 6861896990565859343, :index 1691


## 20200818 mongo-causal-register-wc-:w1-rc-:local-ti-600-sd-2-cry-100-wn-200-rn-200-cpk-5-node-failure
### CyclicCF
### CyclicHB
### WriteCORead



## 20200818 mongo-causal-register-wc-:w1-rc-:local-ti-600-sd-2-cry-100-wn-200-rn-200-cpk-10-node-failure
---2000----------------------------------------------------
### CyclicCF
### CyclicHB
### WriteCORead

---4000----------------------------------------------------
### WriteCOInitRead
### CyclicCF
### CyclicHB
### WriteCORead
### WriteHBInitRead
