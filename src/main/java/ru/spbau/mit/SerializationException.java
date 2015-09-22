package ru.spbau.mit;

public class SerializationException extends RuntimeException {
    SerializationException() {}
    String exceptionData;
    SerializationException(String doneData) {
        exceptionData = doneData;
    }

}
