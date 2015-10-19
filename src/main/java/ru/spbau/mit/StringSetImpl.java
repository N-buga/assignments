package ru.spbau.mit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by n_buga on 22.09.15.
 */
public class StringSetImpl implements StreamSerializable, StringSet {

    private static class Vertex {
        private int countTermVertexLower;
        private boolean isTerminal;
        private Vertex[] links = new Vertex[256];
    }

    private static final int END_OF_FILE = 9;

    @Override
    public void serialize(OutputStream out) throws SerializationException {
        try {
            doSerializeTree(out);
        } catch (IOException e) {
            throw new SerializationException("serialize");
        }
    }

    private void doSerializeTree(OutputStream out) throws IOException {
        Vertex curVertex = vertexHead;
        StringBuilder currentPrefix = new StringBuilder();
        goRoundTree(out, curVertex, currentPrefix);
        out.write((char) END_OF_FILE);
    }

    private void goRoundTree(OutputStream out, Vertex curVertex, StringBuilder currentPrefix) throws IOException {
        if (curVertex == null) {
            return;
        }
        if (curVertex.isTerminal) {
            for (int i = 0; i < currentPrefix.length(); i++) {
                out.write(currentPrefix.charAt(i));
            }
            out.write('\n');
        }
        for (int i = 0; i < 256; i++) {
            if (curVertex.links[i] != null) {
                currentPrefix.append((char) i);
                goRoundTree(out, curVertex.links[i], currentPrefix);
                currentPrefix.deleteCharAt(currentPrefix.length() - 1);
            }
        }
    }

    @Override
    public void deserialize(InputStream in) throws SerializationException{
        try {
            doDeserialize(in);
        } catch (IOException e) {
            throw new SerializationException("deserialize");
        }
    }

    private void doDeserialize(InputStream in) throws IOException {
        vertexHead = new Vertex();
        char c = 0;
        int i = 0;
        while (i != END_OF_FILE) {
            StringBuilder curString = new StringBuilder();
            i = in.read();
            while ((c = (char) i) != '\n' && i != END_OF_FILE) {
                curString.append(c);
                i = in.read();
            }
            if (i == END_OF_FILE) {
                break;
            }
            add(curString.toString());
        }
    }

    private Vertex vertexHead = new Vertex();

    private Vertex nextForAdd(Vertex curVertex, char c) {
        curVertex =  curVertex.links[c];
        curVertex.countTermVertexLower++;
        return curVertex;
    }

    @Override
    public boolean add(String element){
        if (contains(element)) {
            return false;
        }
        Vertex currentVertex = vertexHead;
        currentVertex.countTermVertexLower++;
        for (char c: element.toCharArray()){
            if (currentVertex.links[c] == null) {
                Vertex newVertex = new Vertex();
                currentVertex.links[c] = newVertex;
            }
            currentVertex = nextForAdd(currentVertex, c);
        }
        currentVertex.isTerminal = true;
        return true;
    }

    @Override
    public boolean contains(String element) {
        Vertex curVertex = vertexHead;
        for (char c: element.toCharArray()) {
            if (curVertex.links[c] == null) {
                return false;
            }
            curVertex = curVertex.links[c];
        }
        return curVertex.isTerminal;
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        doRemove(vertexHead, element, 0);
        return true;
    }

    private void doRemove(Vertex curVertex, String element, int i) {
        curVertex.countTermVertexLower--;
        if (i == element.length()) {
            curVertex.isTerminal = false;
            return;
        }
        char c = element.charAt(i);
        if (curVertex.links[c].countTermVertexLower == 1) {
            curVertex.links[c] = null;
            return;
        }
        doRemove(curVertex.links[c], element, i + 1);
    }

    @Override
    public int size() {
        return vertexHead.countTermVertexLower;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        Vertex curVertex = vertexHead;
        for (char c: prefix.toCharArray()) {
            if (curVertex.links[c] == null) {
                return 0;
            }
            curVertex = curVertex.links[c];
        }
        return curVertex.countTermVertexLower;
    }
}
