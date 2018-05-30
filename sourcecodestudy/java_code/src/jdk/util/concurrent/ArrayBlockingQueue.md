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

* put

        public void put(E e) throws InterruptedException {
        checkNotNull(e);
		//这里使用final是一个小技巧，能够怎加效率
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    

**看了一下方法都比较简单，基本上都是对数组的基本操作，阻塞实现方式和LinkedBlockingQueue基本一致**
可以参考[LinkedBlockingQueue]()
## ArrayBlockingQueue与LinkedBlockingQueue的区别

* 保存数据方式不同
  
 ArrayBlockingQueue元素是放在一个数组当中，而LinkedBlockingQueue是以链表的形式存放元素

* 初始化方式
ArrayBlockingQueue因为是存放在数组当中我们知道数组初始化必须制定大小，而LinkedBlockingQueue可以不初始化大小其默认大小是最大整数，当然我们也可设置队列大小

* 锁的使用
从源码中可以看LinkedBlockingQueue使用两个可重入锁

	    private final ReentrantLock putLock = new ReentrantLock();
		private final ReentrantLock putLock = new ReentrantLock();`

而ArrayBlockingQueue只用了一个

	private final ReentrantLocklock = new ReentrantLock(fair);
主要原因是因为链表可以两端同时操作使用两个lock有助于增加效率，而数组无法同时操作