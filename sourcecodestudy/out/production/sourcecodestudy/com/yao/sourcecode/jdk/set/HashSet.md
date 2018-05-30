# HashSet

Hashset内部原理与HashTable基本一致，只是保存数据的时候会把值当作key，value就是一个统一的一个值
从下面的add方法中可以看出来，每次添加新值时都会把其作为key存入Node中。

    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }