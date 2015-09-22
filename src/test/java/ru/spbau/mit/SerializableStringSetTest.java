package ru.spbau.mit;


import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SerializableStringSetTest {

    @Test
    public void testSimple() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.contains("abc"));
        assertEquals(1, stringSet.size());
        assertEquals(1, stringSet.howManyStartsWithPrefix("abc"));
    }

    @Test
    public void testSimpleSerialization() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("bcde"));
        assertTrue(stringSet.add("bcd"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) stringSet).serialize(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        StringSet newStringSet = instance();
        ((StreamSerializable) newStringSet).deserialize(inputStream);

        assertTrue(newStringSet.contains("bcde"));
        assertTrue(newStringSet.contains("bcd"));

        assertFalse(newStringSet.contains("cd"));
        assertTrue(newStringSet.size() == 2);
        assertTrue(newStringSet.howManyStartsWithPrefix("") == 2);

        assertTrue(newStringSet.howManyStartsWithPrefix(" ") == 0);

        assertTrue(newStringSet.remove("bcd"));
        assertFalse(newStringSet.contains("bcd"));

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        ((StreamSerializable) newStringSet).serialize(outputStream2);

        assertFalse(newStringSet.contains("bcd"));
        assertFalse(newStringSet.size() == 3);

        assertTrue(newStringSet.add("cd"));
        assertTrue(newStringSet.add("bc"));
        assertFalse(newStringSet.add("bcde"));
        assertTrue(newStringSet.add("bcda"));

        assertFalse(newStringSet.contains("bcd"));
        assertTrue(newStringSet.contains("bc"));

        assertTrue(newStringSet.howManyStartsWithPrefix("b") == 3);
    }


    @Test(expected=SerializationException.class)
    public void testSimpleSerializationFails() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.add("cde"));

        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Fail");
            }
        };

        ((StreamSerializable) stringSet).serialize(outputStream);
    }

    public static StringSet instance() {
        try {
            return (StringSet) Class.forName("ru.spbau.mit.StringSetImpl").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Error while class loading");
    }
}

