# ArrayList

## 主要参数


* 初始化数组大小


    private static final int DEFAULT_CAPACITY = 10;

* 用来存数据的数组


    transient Object[] elementData;


* 构造函数


        public ArrayList(int initialCapacity) {
		//初始化
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }  }



* add方法

        public boolean add(E e) {
		//增加操作次数，检查数组大小
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;}
    
* 插入元素到某个位置



        public void add(int index, E element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1);  // Increments modCount!!
		//移动数组中的元素用系统提供的方法
		//参数对应含义：要复制的数组，复制开始位置，目标数组，目标开始位置，复制元素个数
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        elementData[index] = element;
        size++;
    }

* set方法

	比较简单
        public E set(int index, E element) {
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    


* remove


    public E remove(int index) {
        rangeCheck(index);

        modCount++;
        E oldValue = elementData(index);

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;}
    