package ru.spbau.mit;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by n_buga on 28.09.15.
 */

public class Collections {
    static public <T, E> Collection<E> map(Function1<T, E> func, Iterable<? extends T> collection) {
        Collection<E> newCollection = new LinkedList<>();
        for (T curElement : collection) {
            newCollection.add(func.apply(curElement));
        }
        return newCollection;
    }
    static public <T> Collection<T> filter(Predicate<T> pred, Iterable<? extends T> collection) {
        Collection<T> newCollection = new LinkedList<>();
        for (T curElement: collection) {
            if (pred.apply(curElement)) {
                newCollection.add(curElement);
            }
        }
        return newCollection;
    }
    static public <T> Collection<T> takeWhile(Predicate<T> pred, Iterable<? extends T> collection) {
        Collection<T> newCollection = new LinkedList<>();
        for (T curElement: collection) {
            if (pred.apply(curElement)) {
                newCollection.add(curElement);
            }
            else break;
        }
        return newCollection;
    }

    static public <T> Collection<T> takeUnless(Predicate<T> pred, Iterable<? extends T> collection) {
        Collection<T> newCollection = new LinkedList<>();
        for (T curElement: collection) {
            if (!pred.apply(curElement)) {
                newCollection.add(curElement);
            } else break;
        }
        return newCollection;
    }

    static public <T> T foldl(Function2<T, T, T> func, Iterable<? extends T> collection) {
        Iterator<T> curIterator = (Iterator<T>) collection.iterator();
        T curElement = curIterator.next();
        T prevElement = null;
        while (curIterator.hasNext()) {
            prevElement = curElement;
            curElement = curIterator.next();
            curElement = func.apply(prevElement, curElement);
        }
        return curElement;
    }
    static public <T> T foldr(Function2<T, T, T> func, Iterable<? extends T> collection) {
        LinkedList<T> llist = new LinkedList<>();
        for (T element: collection) {
            llist.addFirst(element);
        }
        T curElement = llist.get(0);
        for (int i = 1; i < llist.size(); i++) {
            curElement = func.apply(curElement, llist.get(i));
        }
        return curElement;
    }
}