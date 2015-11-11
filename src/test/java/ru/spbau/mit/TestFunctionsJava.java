package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by n_buga on 11.11.15.
 */
public class TestFunctionsJava {

    @Test
    public void testFunction1() {
        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x * 10;
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
