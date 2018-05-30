# LinkedBlockingQueue
阻塞队列，以链表的形式保存队列，遵循FIFO，这里主要关注点在如何实现阻塞这个功能

## 主要属性

* 初始化队列的大小，默认是Integer.MAX，建议根据实际情况设置初始值


    private final int capacity;

   
* 原子整型，标示队列中的数值数量


     /** Current number of elements */
    private final AtomicInteger count = new AtomicInteger();



* 可重入锁锁，实现生产者-消费者的锁模型，从字面上就可以确定其作用了，这里的ReentrantLock以及Condition特新及使用，需要去了解一下，这里就不详细说了

    
    /** Lock held by take, poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();



    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();




* Condition对象：实现锁上多个对象等待且公平队列操作即遵循FIFO规则，也是通过这个来实现阻塞以及超时功能


    /**  这个也是ReentrantLock 中的Condition的一个标识，标识队列中的元素不满 */
    private final Condition notFull = putLock.newCondition();

    /** 这个是有ReentrantLock 中的Condition一个标识队列中有元素非空标志，用于通知消费者队列中有数据了 */
    private final Condition notEmpty = takeLock.newCondition();

## 主要方法


* put方法


     public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        // Note: convention in all put/take/etc is to preset local var
        // holding count negative to indicate failure unless set.
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            /*
             * Note that count is used in wait guard even though it is
             * not protected by lock. This works because count can
             * only decrease at this point (all other puts are shut
             * out by lock), and we (or some other waiting put) are
             * signalled if it ever changes from capacity. Similarly
             * for all other uses of count in other wait guards.
             */
			//如果队列已经满了，则需要等待，这里使用了cndition
			//这里用while循环，如果被唤醒后发现队列任然是空则继续等待
            while (count.get() == capacity) {
                notFull.await();
            }
			//当队列有空位时，将恩queue添加到队尾
            enqueue(node);
			//元素数量加一
            c = count.getAndIncrement();
			//判断队列是否已满，未满唤醒一个被阻塞的线程
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
			//如果offer前队列为空，则唤醒notEmpty上的等待线程  			
            signalNotEmpty();
    }


* take方法



    public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
			//如果队列为空，则等待
            while (count.get() == 0) {
                notEmpty.await();
            }
			//跳出循环说明队列有数据
            x = dequeue();
			//原子减一
            c = count.getAndDecrement();
            if (c > 1)//如果队列有元素唤醒等待的线程
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)//如果队列满则唤醒一个想要获取元素的项城
            signalNotFull();
        return x;
    }

* offer
添加一个元素如果添加失败放回false，这里也可以设置超时时间


    public boolean offer(E e, long timeout, TimeUnit unit)
        throws InterruptedException {

        if (e == null) throw new NullPointerException();
        long nanos = unit.toNanos(timeout);
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() == capacity) {
                if (nanos <= 0)
                    return false;
				//设置超时等待时间，使用Condition的特新来控制等待时间
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(new Node<E>(e));
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
        return true;
    }


* poll方法
和offer一样，取数据如果没有返回null

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x = null;
        int c = -1;
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        if (c == capacity)
            signalNotFull();
        return x;
    }

* peek这个就不贴代码了
取出数据并删除这个数据

* （之前队列为空）添加数据后调用signalNotEmpty()方法唤醒等待取数据的线程；（之前队列已满）取数据后调用signalNotFull()唤醒等待插入数据的线程。这种唤醒模式可节省线程等待时间。？