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

    static public <T, R> R foldl(Function2<R, T, R> func, R init, Iterable<? extends T> collection) {
        Iterator<T> curIterator = (Iterator<T>) collection.iterator();
        R answer = init;
        T curElement = null;
        while (curIterator.hasNext()) {
            curElement = curIterator.next();
            answer = func.apply(answer, curElement);
        }
        return answer;
    }

    static public <T, R> R foldr(Function2<T, R, R> func, R init, Iterable<? extends T> collection) {
        LinkedList<T> llist = new LinkedList<>();
        for (T element: collection) {
            llist.addFirst(element);
        }
        R answer = init;
        for (int i = 0; i < llist.size(); i++) {
            answer = func.apply(llist.get(i), answer);
        }
        return answer;
    }

}