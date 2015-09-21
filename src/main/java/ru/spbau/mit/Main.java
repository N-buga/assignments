package ru.spbau.mit;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.util.*;
import java.io.*;

public class Main {
    static class Vertex {
        char b;
        boolean termVertex;
        Vertex[] links = new Vertex[256];
        Vertex(){
        }
    }

    static class StringStorage implements StreamSerializable, StringSet {
        @Override
        public void serialize(OutputStream out) throws IOException {
            Vertex curVertex = vertexArrayList.get(0);
            String curString = new String();
            goRoundTree(out, curVertex, curString);
        }

        public void goRoundTree(OutputStream out, Vertex curVertex, String curString) throws IOException {
            if (curVertex == null)
                return;
            if (curVertex.termVertex == true) {
                int i;
                for (i = 0; i < curString.length(); i++) {
                    out.write(curString.charAt(i));
                }
                out.write('\n');
            }
            int i;
            for (i = 0; i < 256; i++) {
                if (curVertex.links[i] != null)
                    goRoundTree(out, curVertex.links[i], curString + (char)i);
            }
        }

        @Override
        public void deserialize(InputStream in) throws IOException {
            vertexArrayList = null;
            vertexArrayList = new ArrayList<Vertex>();
            vertexArrayList.add(new Vertex());
            char c = 0;
            while (c != 65535) {
                String curString = "";
                while ((c = (char) in.read()) != '\n' && c != 65535) {
                    curString = curString + c;
                }
                if (c == 65535)
                    break;
//                System.out.print(curString);
  //              System.out.print('%');
                this.add(curString);
            }
//            System.out.print('&');
        }

        ArrayList<Vertex> vertexArrayList = null;
        StringStorage() {
            vertexArrayList = new ArrayList<Vertex>();
            vertexArrayList.add(new Vertex());
        }

        @Override
        public boolean add(String element){
            Vertex currentVertex = vertexArrayList.get(0);
            int i;
            for (i = 0; i < element.length(); i++){
                if (currentVertex.links[(int)element.charAt(i)] != null) {
                    currentVertex.b = element.charAt(i);
                    currentVertex = currentVertex.links[element.charAt(i)];
                }
                else {
                    Vertex newVertex = new Vertex();
                    vertexArrayList.add(newVertex);
                    currentVertex.b = element.charAt(i);
                    currentVertex.links[(int)element.charAt(i)] = newVertex;
                    currentVertex = currentVertex.links[element.charAt(i)];
                }
            }
            currentVertex.termVertex = true;
            return true;
        }

        @Override
        public boolean contains(String element) {
            return false;
        }

        @Override
        public boolean remove(String element) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public int howManyStartsWithPrefix(String prefix) {
            return 0;
        }
    }
    public static void main (String[ ] args) throws IOException {
        StringStorage stringStorage = new StringStorage();

        stringStorage.add("alpha");
        stringStorage.add("betta");
        stringStorage.add("alens");

        OutputStream outputStream = new FileOutputStream(new File("./src/main/java/ru/spbau/mit/file"));
        stringStorage.serialize(outputStream);

        outputStream.flush();
        outputStream.close();

        outputStream = new FileOutputStream(new File("./src/main/java/ru/spbau/mit/file.out"));

        stringStorage.add("gamma");

        stringStorage.serialize(outputStream);

        outputStream.flush();
        outputStream.close();

        InputStream inputStream = new FileInputStream(new File("./src/main/java/ru/spbau/mit/file"));

        stringStorage.deserialize(inputStream);

        inputStream.close();

        outputStream = new FileOutputStream(new File("./src/main/java/ru/spbau/mit/file2.out"));

        stringStorage.serialize(outputStream);

        outputStream.flush();
        outputStream.close();
    }
}
