package labrom.litlbro.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;


public class ObservableListProxy<T> extends Observable implements List<T> {
    private final List<T> list;
    private boolean hold;
    
    public ObservableListProxy() {
        this.list = new ArrayList<T>();
    }
    
    public ObservableListProxy(List<T> l) {
        this.list = l;
    }
    
    public void holdNotify() {
        hold = true;
    }
    

    public void forceNotify() {
        hold = false;
        setChanged();
        notifyObservers();
    }
    
    @Override
    public void notifyObservers() {
        if(!hold)
            super.notifyObservers();
    }
    
    
    @Override
    public boolean add(T object) {
        boolean add = list.add(object);
        if(add) {
            setChanged();
            notifyObservers();
        }
        return add;
    }

    @Override
    public void add(int location, T object) {
        list.add(location, object);
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean addAll(Collection<? extends T> arg0) {
        boolean addAll = list.addAll(arg0);
        if(addAll) {
            setChanged();
            notifyObservers();
        }
        return addAll;
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends T> arg1) {
        boolean addAll = list.addAll(arg0, arg1);
        if(addAll) {
            setChanged();
            notifyObservers();
        }
        return addAll;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return list.containsAll(arg0);
    }

    @Override
    public T get(int location) {
        return list.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return list.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int location) {
        return list.listIterator(location);
    }

    @Override
    public T remove(int location) {
        T remove = list.remove(location);
        if(remove != null) {
            setChanged();
            notifyObservers();
        }
        return remove;
    }

    @Override
    public boolean remove(Object object) {
        boolean remove = list.remove(object);
        if(remove) {
            setChanged();
            notifyObservers();
        }
        return remove;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        boolean removeAll = list.removeAll(arg0);
        if(removeAll) {
            setChanged();
            notifyObservers();
        }
        return removeAll;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        boolean retainAll = list.retainAll(arg0);
        if(retainAll) {
            setChanged();
            notifyObservers();
        }
        return retainAll;
    }

    @Override
    public T set(int location, T object) {
        T set = list.set(location, object);
        setChanged();
        notifyObservers();
        return set;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public List<T> subList(int start, int end) {
        return new ObservableListProxy<T>(list.subList(start, end));
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @SuppressWarnings("hiding")
    @Override
    public <T> T[] toArray(T[] array) {
        return list.toArray(array);
    }

    @Override
    public String toString() {
        return String.valueOf(list);
    }

}
