package ru.spbau.mit;

public class SerializationException extends RuntimeException {
    String exceptionData;
    SerializationException(String doneData) {
        exceptionData = doneData;
    }

}
