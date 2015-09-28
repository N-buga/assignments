package ru.spbau.mit;

import java.util.Objects;

/**
 * Created by n_buga on 28.09.15.
 */
abstract public class Predicate<T> {
    public final static Predicate ALWAYS_TRUE = new Predicate() {
        @Override
        public boolean apply (Object x) {
            return true;
        }
    };

    public final static Predicate ALWAYS_FALSE = new Predicate() {
        @Override
        public boolean apply (Object x) {
            return false;
        }
    };

    public abstract boolean apply (T x);
    public Predicate<T> or (final Predicate<? super T> pr2) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T x) {
                return (Predicate.this.apply(x) || pr2.apply(x));
            }
        };
    }
    public Predicate<T> and (final Predicate<? super T> pr2) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T x) {
                return (Predicate.this.apply(x) && pr2.apply(x));
            }
        };
    }
    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T x) {
                return !Predicate.this.apply(x);
            }
        };
    }
}
