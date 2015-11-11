package ru.spbau.mit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by n_buga on 11.11.15.
 */
public class TestCollectionsJava {

    @Test
    public void testCollectionsBool() {
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Predicate<Integer> pred = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
                return x > 5;
            }
        };

        Collection<Integer> curcoll = Collections.filter(pred, list);
        assertEquals(curcoll, Arrays.asList(6, 7, 8, 9));

        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + 5;
            }
        };
        curcoll = Collections.map(func1, list);
        assertEquals(curcoll, Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
    }

    @Test
    public void testCollectionsFor() {
        Collection<Integer> curcoll;

        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Predicate<Integer> pred = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
                return x > 5;
            }
        };

        List<Integer> list2 = asList(6, 4, 6, 8, 2, 5, 7, 10, 1);
        curcoll = Collections.takeWhile(pred, list2);

        assertEquals(curcoll, Arrays.asList(6));

        list2 = asList(3, 7, 6, 2, 7, 8);
        curcoll = Collections.takeUnless(pred, list2);
        assertEquals(curcoll, Arrays.asList(3));

        Function2<Integer, Integer, Integer> func2 = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer x, Integer y) {
                return x - y;
            }
        };

        Integer a = Collections.foldl(func2, 0, list);
        assertTrue(a == -45);

        a = Collections.foldr(func2, 0, list);
        assertTrue(a == -5);
    }
}
