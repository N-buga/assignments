package ru.spbau.mit;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.util.*;
import java.io.*;

public class Main {
    static class Vertex {
        char b;
        int termVertexLower;
        boolean termVertex;
        Vertex[] links = new Vertex[256];
        Vertex(){
            termVertexLower = 0;
            b = 0;
        }
    }

    static class StringStorage implements StreamSerializable, StringSet {
        @Override
        public void serialize(OutputStream out) throws  IOException {
            Vertex curVertex = vertexArrayList.get(0);
            int countStringOut = 0;
            char lastChar = 0;
            Vector<Vertex> vertexVector = new Vector<Vertex>();
//           while (countStringOut < curVertex.termVertexLower){
                int i = 0;
                while (curVertex != vertexArrayList.get(0) || i < 255) {
                    if (i == 0 && curVertex.termVertex){ //the fist time we are in the vertex, write if it is a term vertex
                        int j;
                        countStringOut++;
                        for (j = 1; j < vertexVector.size(); j++) {
                            out.write(vertexVector.get(j).b);
                        }
                        out.write(curVertex.b);
                        out.write('\n');
                    }
                    if (i == 255 || (curVertex.termVertex && curVertex.termVertexLower == 1)){ //delete last element of deq, cause we walk around all subtree
                        i = (char)((int)curVertex.b + 1);
                        curVertex = vertexVector.get(vertexVector.size() - 1);
                        vertexVector.remove(vertexVector.size() - 1);
                        continue;
                    }
                    if (curVertex.links[(char) i] != null) {
                        vertexVector.add(curVertex);
                        curVertex = curVertex.links[(char)i];
                        i = 0;
                        continue;
                    }
                    i++;
                }
        //    }
        }
/*        public void serialize(OutputStream out) throws IOException {
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
*/
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
            currentVertex.termVertexLower++;
            int i;
            boolean ans = false;
            for (i = 0; i < element.length(); i++){
                if (currentVertex.links[(int)element.charAt(i)] != null) {
                    currentVertex = currentVertex.links[element.charAt(i)];
                    currentVertex.b = element.charAt(i);
                    currentVertex.termVertexLower++;
                }
                else {
                    ans = true;
                    Vertex newVertex = new Vertex();
                    vertexArrayList.add(newVertex);
                    currentVertex.links[(int)element.charAt(i)] = newVertex;
                    currentVertex = currentVertex.links[element.charAt(i)];
                    currentVertex.b = element.charAt(i);
                    currentVertex.termVertexLower++;
                }
            }
            currentVertex.termVertex = true;
            return ans;
        }

        @Override
        public boolean contains(String element) {
            Vertex curVertex = vertexArrayList.get(0);
            int i;
            for (i = 0; i < element.length(); i++) {
                if (curVertex.links[element.charAt(i)] == null)
                    return false;
                curVertex = curVertex.links[element.charAt(i)];
            }
            return true;
        }

        @Override
        public boolean remove(String element) {
            if (!this.contains(element))
                    return false;
            Vertex curVertex = vertexArrayList.get(0);
            for (int i = 0; i < element.length(); i++){
                if (curVertex.termVertexLower == 2 && curVertex.links[element.charAt(i)].termVertexLower == 1) {
                    curVertex.termVertexLower--;
                    curVertex.links[element.charAt(i)] = null;
                    break;
                }
                else {
                    curVertex.termVertexLower--;
                    curVertex = curVertex.links[element.charAt(i)];
                }
            }
            return true;
        }

        @Override
        public int size() {
            return vertexArrayList.get(0).termVertexLower;
        }

        @Override
        public int howManyStartsWithPrefix(String prefix) {
            int i;
            Vertex curVertex = vertexArrayList.get(0);
            for (i = 0; i < prefix.length(); i++) {
                if (curVertex.links[prefix.charAt(i)] == null)
                    return 0;
                curVertex = curVertex.links[prefix.charAt(i)];
            }
            return curVertex.termVertexLower;
        }
    }
    public static void main (String[ ] args) throws IOException {
/*        StringStorage stringStorage = new StringStorage();

        stringStorage.add("alpha");
        stringStorage.add("betta");
        stringStorage.add("alens");

        OutputStream outputStream = new FileOutputStream(new File("./src/main/java/ru/spbau/mit/file"));
        stringStorage.serialize(outputStream);

        outputStream.flush();
        outputStream.close();

        outputStream = new FileOutputStream(new File("./src/main/java/ru/spbau/mit/file.out"));

        stringStorage.add("gamma");

        System.out.print(stringStorage.size());
        System.out.print(stringStorage.contains("alpha"));
        System.out.print(stringStorage.contains("gamma"));
        System.out.print(stringStorage.contains("alenso"));

        stringStorage.serialize(outputStream);

        outputStream.flush();
        outputStream.close();

        InputStream inputStream = new FileInputStream(new File("./src/main/java/ru/spbau/mit/file"));

        stringStorage.deserialize(inputStream);

        inputStream.close();

        System.out.print(stringStorage.size());
        System.out.print(stringStorage.contains("gamma"));
        System.out.print(stringStorage.contains("betta"));

        outputStream = new FileOutputStream(new File("./src/main/java/ru/spbau/mit/file2.out"));

        stringStorage.serialize(outputStream);

        outputStream.flush();
        outputStream.close();

        System.out.print('\n');

        System.out.print(stringStorage.howManyStartsWithPrefix("al"));

        System.out.print(stringStorage.remove("alenso"));
        System.out.print(stringStorage.remove("alens"));
        System.out.print(stringStorage.remove("alens"));

        System.out.print(stringStorage.howManyStartsWithPrefix("al"));

*/
    }
}
