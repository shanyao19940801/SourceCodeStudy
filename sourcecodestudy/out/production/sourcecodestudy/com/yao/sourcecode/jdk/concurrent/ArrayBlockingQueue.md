# ArrayBlockingQueue

数组阻塞队列

## 主要属性

* 用于存放元素的数组，使用fianl说明一经初始化后就不能再进行修改


	`final Object[] items;`

* 几个整型属性

    /** 下一次获取数据时的数据下标 */
    int takeIndex;

    /** 下次插入数据时的下标 */
    int putIndex;

    /** 队列中元素个数 */
    int count;

* 可重入锁，实现生产者-消费者的锁模型
	

	`final ReentrantLock lock;`

* lock中的codition对象


    /** 标识队列不为空 */
    private final Condition notEmpty;

    /** 标识队列不为空 */
    private final Condition notFull;


## 构造函数

与LinkedBlockedQueue不同，ArrayBlockedQueue必须要初始化大小，所以在创建对象实例时必须要赋予初始值
	
	//capacity 初始大小
    public ArrayBlockingQueue(int capacity) {
        this(capacity, false);
    }

	//fair 如果是fair则遵循FIFO原则，如果false则确定顺序
    public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }

## 主要方法



