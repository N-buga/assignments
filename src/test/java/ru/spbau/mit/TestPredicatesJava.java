package ru.spbau.mit;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by n_buga on 11.11.15.
 */
public class TestPredicatesJava {

    @Test
    public void testPredicate() {
        Predicate<Integer> pred0 = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer x) {
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
            public boolean apply(Integer x) {
                return x > 5;
            }
        };

        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        for (int i = 0; i < 10; i++) {
            linkedList.add(i);
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
                fail();
                return false;
            }
        };

        assertTrue(Predicate.ALWAYS_TRUE.or(pred2).apply(1));
        assertFalse(Predicate.ALWAYS_FALSE.and(pred2).apply(1));
    }
}
