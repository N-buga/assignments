package ru.spbau.mit;

/**
 * Created by n_buga on 28.09.15.
 */

import static org.junit.Assert.*;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static java.util.Arrays.*;

public class TestFunctionalJava {
    @Test
    public void testFunction1() {
        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x*10;
            }
        };
        assertTrue(func1.apply(10) == 100);

        Function1<String, Integer> mylength = new Function1<String, Integer>() {
            @Override
            public Integer apply(String x) {
                return x.length();
            }
        };

        assertTrue(mylength.apply("as") == 2);

        assertTrue(func1.apply(mylength.apply("a")) == 10);
    }

    @Test
    public void testFunction2() {
        Function2<Integer, Integer, Integer> func2 = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer x, Integer y) {
                return x + y;
            }
        };

        assertTrue(func2.apply(11, 22) == 33);

        Function2<String, Character, String> addChar = new Function2<String, Character, String>() {
            @Override
            public String apply(String x, Character y) {
                return x + y;
            }
        };

        assertTrue(addChar.apply("you", 'r').equals("your"));
    }

    @Test
    public void testPredicate() {
        Predicate<Integer> pred0 = new Predicate<Integer>() {
            @Override
            public boolean apply (Integer x) {
                return x > 5;
            }
        };

        assertTrue(Predicate.ALWAYS_TRUE.apply("alala"));
        assertFalse(Predicate.ALWAYS_FALSE.apply("sdjj"));
        assertTrue(pred0.apply(10));
        assertFalse(pred0.apply(3));

    }

    @Test
    public void testCombinePredicate() {

        Predicate<Integer> pred0 = new Predicate<Integer>() {
            @Override
            public boolean apply (Integer x) {
                return x > 5;
            }
        };

        LinkedList<Integer> llist = new LinkedList<Integer>();
        for (int i = 0; i < 10; i++) {
            llist.add(i);
        }

        assertTrue(pred0.not().apply(5));
        assertFalse(pred0.not().apply(6));

        Predicate<Integer> pred1 = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
                return (x % 5 == 0);
            }
        };

        assertTrue(pred0.not().or(pred1).apply(5));
        assertTrue(pred0.not().or(pred1).apply(10));
        assertFalse(pred0.not().or(pred1).apply(6));

        assertTrue(pred0.not().and(pred1).apply(5));
        assertFalse(pred0.not().and(pred1).apply(4));
        assertFalse(pred0.not().and(pred1).apply(10));

        Predicate<Integer> pred2 = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
                assertFalse(true);
                return false;
            }
        };

        assertTrue(Predicate.ALWAYS_TRUE.or(pred2).apply(1));
        assertFalse(Predicate.ALWAYS_FALSE.and(pred2).apply(1));
    }

    @Test
    public void testCollectionsBool() {
        List<Integer> llist = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Predicate<Integer> pred = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
                return x > 5;
            }
        };

        Collection<Integer> curcoll = Collections.filter(pred, llist);
        assertEquals(curcoll, Arrays.asList(6, 7, 8, 9));

        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + 5;
            }
        };
        curcoll = Collections.map(func1, llist);
        assertEquals(curcoll, Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
    }

    @Test
    public void testCollectionsFor() {
        Collection<Integer> curcoll;

        List<Integer> llist = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Predicate<Integer> pred = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
                return x > 5;
            }
        };

        List<Integer> llist2 = asList(6, 4, 6, 8, 2, 5, 7, 10, 1);
        curcoll = Collections.takeWhile(pred, llist2);

        assertEquals(curcoll, Arrays.asList(6));

        llist2 = asList(3, 7, 6, 2, 7, 8);
        curcoll = Collections.takeUnless(pred, llist2);
        assertEquals(curcoll, Arrays.asList(3));

        Function2<Integer, Integer, Integer> func2 = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer x, Integer y) {
                return x - y;
            }
        };

        Integer a = Collections.foldl(func2, 0, llist);
        assertTrue(a == -45);

        a = Collections.foldr(func2, 0, llist);
        assertTrue(a == -5);
    }

    @Test
    public void testCombineFunction() {
        Function2<Integer, Integer, Integer> func2 = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer x, Integer y) {
                return x + y;
            }
        };

        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x*10;
            }
        };
        Function2<Integer, Integer, Integer> funcCompose = func2.compose(func1);
        assertTrue(50 == funcCompose.apply(2, 3));

        Function1<Integer, Integer> func3_1 = func2.bind1(10);
        Function1<Integer, Integer> func3_2 = func2.bind2(5);

        assertTrue(func3_1.apply(5) == 15);
        assertTrue(func3_2.apply(10) == 15);

        Function1<Integer, Function1<Integer, Integer>> func4 = func2.curry();

        assertTrue(9 == func4.apply(4).apply(5));

        Function1<Integer, Integer> func01 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + 5;
            }
        };

        assertTrue(15 == func1.compose(func01).apply(1));

    }
}
