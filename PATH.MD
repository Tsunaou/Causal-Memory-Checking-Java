# 
w1 is :type :ok, :f :write, :value [4 162], :process 21, :time 6251519642, :position 6861880648215298091, :link 6861880643920330774, :index 434
w2 is :type :ok, :f :write, :value [4 195], :process 20, :time 7670171586, :position 6861880656805232649, :link 6861880648215298095, :index 544
r1 is :type :ok, :f :read, :value [4 162], :process 20, :time 35356517538, :position 6861880777064316933, :link nil, :index 1469
k1 is :type :ok, :f :read, :value [4 162], :process 20, :time 6366564769, :position 6861880648215298095, :link 6861880648215298067, :index 443

CO.isCO(w1, w2) && CO.isCO(w2, r1) && RF.isRF(w1, r1)
CO.isCO(w1, w2) 究竟成立吗？
i=434,j=544k=443

P20  R(4,162) W(4,195) R(4,162)
P21  W(4,162)

w1 {:type :ok, :f :write, :value [4 162], :process 21, :time 6251519642, :position 6861880648215298091, :link 6861880643920330774, :index 885}
w2 {:type :ok, :f :write, :value [4 195], :process 20, :time 7670171586, :position 6861880656805232649, :link 6861880648215298095, :index 1116}
r1 {:type :ok, :f :read, :value [4 162], :process 120, :time 35356517538, :position 6861880777064316933, :link nil, :index 3188}
r2 {:type :ok, :f :read, :value [4 162], :process 20, :time 6366564769, :position 6861880648215298095, :link 6861880648215298067, :index 902}


CO.isCO(w1, w2) && CO.isCO(w2, r1) && RF.isRF(w1, r1)
w1 is :type :ok, :f :write, :value [12 4], :process 60, :time 7276613307, :position 6862041279992168471, :link 6862041275697201202, :index 556
r2 is :type :ok, :f :read, :value [12 4], :process 61, :time 7346787736, :position 6862041279992168478, :link 6862041275697201166, :index 567
w2 is :type :ok, :f :write, :value [12 144], :process 61, :time 7638473982, :position 6862041279992168494, :link 6862041279992168478, :index 598
r1 is :type :ok, :f :read, :value [12 4], :process 61, :time 35657895357, :position 6862041400251252749, :link 6862041322941841410, :index 1019

:type :ok, :f :write, :value [7 13],  :process 78, :time 1170613920,  :position 6861918774639984646, :link nil, :index 4
:type :ok, :f :write, :value [7 118], :process 78, :time 7092429661,  :position 6861918800409788429, :link 6861918791819853827, :index 246
:type :ok, :f :read,  :value [7 118], :process 74, :time 8063030485,  :position 6861918804704755713, :link 6861918787524886534, :index 263
:type :ok, :f :read,  :value [7 0],   :process 74, :time 66691192608, :position 6861919053812858894, :link 6861919049517891585, :index 3700
