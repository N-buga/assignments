package ru.spbau.mit;

/**
 * Created by n_buga on 28.09.15.
 */
abstract public class Function2<T1, T2, R> {
    public abstract R apply (T1 x, T2 y);
    public <E> Function2<T1, T2, E> compose(final Function1<? super R, E> g) {
        return new Function2<T1, T2, E>() {
            @Override
            public E apply (T1 x, T2 y) {
                return g.apply(Function2.this.apply(x, y));
            }
        };
    }
    public Function1<T2, R> bind1(final T1 x) {
        return new Function1<T2, R>() {
            @Override
            public R apply(T2 y) {
                return Function2.this.apply(x, y);
            }
        };
    }
    public Function1<T1, R> bind2(final T2 y) {
        return new Function1<T1, R>() {
            @Override
            public R apply(T1 x) {
                return Function2.this.apply(x, y);
            }
        };
    }
    public Function1<T2, Function1<T1, R>> curry() {
        return new Function1<T2, Function1<T1, R>>() {
            @Override
            public Function1<T1, R> apply(final T2 y) {
                return new Function1<T1, R>() {
                    @Override
                    public R apply(T1 x) {
                        return Function2.this.apply(x, y);
                    }
                };
            }
        };
    }
}
