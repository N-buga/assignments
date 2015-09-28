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
import java.util.Collection;
import java.util.Iterator;
import static java.util.Arrays.*;
import java.util.LinkedList;
import java.util.List;

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
    public void testCollections() {
        LinkedList<Integer> llist = new LinkedList<Integer>();
        for (int i = 0; i < 10; i++)
            llist.add(i);

        Predicate<Integer> pred = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
                return x > 5;
            }
        };

        Collection<Integer> curcoll = Collections.filter(pred, llist);
        Iterator<Integer> iter = curcoll.iterator();
        System.out.print("1:");
        while (iter.hasNext()) {
            System.out.print(iter.next());
            System.out.print(' ');
        }
        System.out.print('\n');

        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + 5;
            }
        };

        curcoll = Collections.map(func1, llist);
        iter = curcoll.iterator();
        System.out.print("2:");
        while (iter.hasNext()) {
            System.out.print(iter.next());
            System.out.print(' ');
        }
        System.out.print('\n');

        List<Integer> llist2 = asList(6, 4, 6, 8, 2, 5, 7, 10, 1);
        curcoll = Collections.takeWhile(pred, llist2);
        iter = curcoll.iterator();
        System.out.print("3:");
        while (iter.hasNext()) {
            System.out.print(iter.next());
            System.out.print(' ');
        }
        System.out.print("\n");

        llist2 = asList(3, 7, 6, 2, 7, 8);
        curcoll = Collections.takeUnless(pred, llist2);
        iter = curcoll.iterator();
        System.out.print("4:");
        while (iter.hasNext()) {
            System.out.print(iter.next());
            System.out.print(' ');
        }
        System.out.print('\n');

        Function2<Integer, Integer, Integer> func2 = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer x, Integer y) {
                return x - y;
            }
        };

        Integer a = Collections.foldl(func2, llist);
        assertTrue(a == -45);

        a = Collections.foldr(func2, llist);
        System.out.print(a);
    }

    @Test
    public void CombineTest() {
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

        Function1<Integer, Integer> func3 = func2.bind1(10);

        Function1<Integer, Function1<Integer, Integer>> func4 = func2.curry();

        assertTrue(9 == func4.apply(4).apply(5));

        assertTrue(func3.apply(5) == 15);

        Function1<Integer, Integer> func01 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + 5;
            }
        };

        assertTrue(15 == func1.compose(func01).apply(1));

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

    }
}
