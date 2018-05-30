# LinkedList

## 主要参数

* 队首元素队尾元素

      transient Node<E> first;
	  transient Node<E> last;


* 内部类

	链表节点元素


    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

*  add(int index, E element)方法


    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
			//添加元素到队尾，下面有介绍
            linkLast(element);
        else
            linkBefore(element, node(index));
    }
	

* add(int index, E element)添加元素到指定位置


    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));//node方法返回某个位置的元素，下面有解析
    }


* linkLast方法
将元素添加到队尾


     void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
		//替换队尾元素
        last = newNode;
        if (l == null)
		//如果原来队列为空放到队首中
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;}
    

* node


    Node<E> node(int index) {
        // assert isElementIndex(index);
		//如果下表在链表前半段从前面开始，如果在后半段从后面开始，不得不说大师不会放过任何一定点的优化机会
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }