# Vector
* 这个集合和ArrayList内部实现基本一致，但是大部分的公开方法都用synchronized加锁同步了
## 主要参数

* 保存元素数组

    protected Object[] elementData;


* 元素个数
	
    protected int elementCount;

* 每次扩容大小，默认10
	每次扩容在原有基础上增加，如果该值小雨等于0,则扩容为原来两倍
    protected int capacityIncrement;

* 构造函数

    
    public Vector(int initialCapacity, int capacityIncrement) {
    super();
    if (initialCapacity < 0)
    throw new IllegalArgumentException("Illegal Capacity: "+
       initialCapacity);
	//初始化数组大小
    this.elementData = new Object[initialCapacity];
    this.capacityIncrement = capacityIncrement;
    }


* 扩容方法grow


    private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
	//在原有大小基础上扩容capacityIncrement，如果小等于0之间扩容为原来两倍	
    int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
     capacityIncrement : oldCapacity);
    if (newCapacity - minCapacity < 0)
    newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
    newCapacity = hugeCapacity(minCapacity);
    elementData = Arrays.copyOf(elementData, newCapacity);
    }


# Vector与ArratList区别

* 线程安全与非线程安全

	从源码中可以看出，Vector大部分方法都会加上sychronized同步机制，这样就可以办证线程安全但同时其效率也会大幅度下降

* 扩容方式不同

ArrayList的扩容方法

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }


Vector的扩容方法

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                         capacityIncrement : oldCapacity);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

从代码上可以看出来，ArrayList扩容是直接增加一倍，而Vector是有个增量**capacityIncrement**的如果大于0就扩容capacityIncrement大小不然扩容一倍