package ru.spbau.mit;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by n_buga on 28.09.15.
 */

public class Collections {
    static public <T, E, C extends Iterable<T>> Collection<E> map(Function1<T, E> func, C collection) {
        Iterator<T> curIterator = collection.iterator();
        Collection<E> newCollection = new LinkedList<>();
        T curElement;
        while (curIterator.hasNext()) {
            curElement = curIterator.next();
            newCollection.add(func.apply(curElement));
        }
        return newCollection;
    }
    static public <T, C extends Iterable<T>> Collection<T> filter(Predicate<T> pred, C collection) {
        Iterator<T> curIterator = collection.iterator();
        Collection<T> newCollection = new LinkedList<>();
        T curElement;
        while (curIterator.hasNext()) {
            curElement = curIterator.next();
            if (pred.apply(curElement)) {
                newCollection.add(curElement);
            }
        }
        return newCollection;
    }
    static public <T, C extends Iterable<T>> Collection<T> takeWhile(Predicate<T> pred, C collection) {
        Iterator<T> curIterator = collection.iterator();
        Collection<T> newCollection = new LinkedList<>();
        T curElement;
        while (curIterator.hasNext()) {
            curElement = curIterator.next();
            if (pred.apply(curElement)) {
                newCollection.add(curElement);
            }
            else break;
        }
        return newCollection;
    }

    static public <T, C extends Iterable<T>> Collection<T> takeUnless(Predicate<T> pred, C collection) {
        Iterator<T> curIterator = collection.iterator();
        Collection<T> newCollection = new LinkedList<>();
        T curElement;
        while (curIterator.hasNext()) {
            curElement = curIterator.next();
            if (!pred.apply(curElement)) {
                newCollection.add(curElement);
            } else break;
        }
        return newCollection;
    }

    static public <T, C extends Iterable<T>> T foldl(Function2<T, T, T> func, C collection) {
        Iterator<T> curIterator = collection.iterator();
        T curElement = curIterator.next();
        T prevElement;
        while (curIterator.hasNext()) {
            prevElement = curElement;
            curElement = curIterator.next();
            curElement = func.apply(prevElement, curElement);
        }
        return curElement;
    }
    static public <T, C extends Iterable<T>> T foldr(Function2<T, T, T> func, C collection) {
        Iterator<T> curIterator = collection.iterator();
        LinkedList<T> llist = new LinkedList<>();
        while (curIterator.hasNext()) {
            llist.addFirst(curIterator.next());
        }
        T curElement = llist.get(0);
        for (int i = 1; i < llist.size(); i++) {
            curElement = func.apply(curElement, llist.get(i));
        }
        return curElement;
    }
}