# ConcurrentHashMap

## 主要参数
正数的时候类似于HashMap中阀值<br>
负数时有两种情况：<br>

    private transient volatile int sizeCtl;  

*  -1 table[] 正在初始化
*  -N 有N-1个线程正在进行扩容操作
    

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

* table[]的初始值以及默认加载因子

        static final int MOVED     = -1; // hash for forwarding nodes
        static final int TREEBIN   = -2; // hash for roots of trees
* 上面两个用于判断Node的如果Node的hash=-1表示该Node是一个forwardNode节点，该节点是一个扩容时的零时节点
* 当hash=-2时表示该节点是一个树形节点

        static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K,V> next;
* Node内部类，这里只是一小部分，看一下key和val使用**volatile**关键词修饰，确保了同步可见性

## 主要方法

* 构造方法

        public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        if (initialCapacity < concurrencyLevel)   // Use at least as many bins
            initialCapacity = concurrencyLevel;   // as estimated threads
        long size = (long)(1.0 + (long)initialCapacity / loadFactor);
        int cap = (size >= (long)MAXIMUM_CAPACITY) ?
            MAXIMUM_CAPACITY : tableSizeFor((int)size);
        this.sizeCtl = cap;
    }



* put方法	

	
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();//初始化table
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
				//无锁原子操作，将新的Node放到table[i]中，成功退出自旋
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   
            }
            //hash值为-1链表正在进行扩容操作，加入其中帮助扩容
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
				//对要添加的那个Node节点加锁
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
						//如果节点的hash值<0，则节点上的数据可能是红黑树的形式
                        if (fh >= 0) {
							
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
				    //当链表长度超过一定值改为红黑树存储
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }


* initTable方法


    private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
			//有其他线程正在初始化，等一下
            if ((sc = sizeCtl) < 0)
                Thread.yield(); // lost initialization race; just spin
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
    

## ConcurrentHashMap与HashTable区别

* 同步：从源码中可以看到两者的同步方式有很大的不同，ConcurrentHashMap同步机制是直接在方法上使用synchronized关键字加锁对象就是HashTable；二ConcurrentHashMap是在hash桶的某个节点Node上加锁，锁的粒度小了很多，而且ConcurrentHash大量使用了CASE算法。到这里两者的优缺点也就大概明白了。HashTable效率较低但是更安全更准确，ConcuurentHashMap效率更高但获取的数据可能并不是最新的数据。貌似回答了下面的问题？


## 为什么不能用ConcurrentHashTable取代HashTable？
*  这里涉及到一致性问题，ConcurrentHashTable是弱一致性二HashTable是强一致性，要研究内存模型了，我当前也是有点迷糊，记得在《多线程核心技术》中看到过，可能要重新去读一遍了。


###我曾七次鄙视自己的灵魂
* 第一次，当它本可进取时，却故作谦卑；
第二次，当它在空虚时，用爱欲来填充；
第三次，在困难和容易之间，它选择了容易；
第四次，它犯了错，却借由别人也会犯错来宽慰自己；
第五次，它自由软弱，却把它认为是生命的坚韧；
第六次，当它鄙夷一张丑恶的嘴脸时，却不知那正是自己面具中的一副；
第七次，它侧身于生活的污泥中，虽不甘心，却又畏首畏尾。

