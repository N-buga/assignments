package ru.spbau.mit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
/**
 * Created by n_buga on 22.09.15.
 */
public class StringSetImpl implements StreamSerializable, StringSet {
    private static class Vertex {
        private int countTermVertexLower;
        private boolean isVertex;
        private Vertex[] links = new Vertex[256];
    }
    private static final int END_OF_FILE = 9;
    @Override
    public void serialize(OutputStream out) throws SerializationException {
        try {
            doSerialize(out);
        } catch (IOException e) {
            throw new SerializationException("serialize");
        }
    }
    private void doSerialize(OutputStream out) throws IOException {
        Vertex curVertex = vertexHead;
        StringBuilder curString = new StringBuilder();
        goRoundTree(out, curVertex, curString);
        out.write((char) END_OF_FILE);
    }
    private void goRoundTree(OutputStream out, Vertex curVertex, StringBuilder curString) throws IOException {
        if (curVertex == null) {
            return;
        }
        if (curVertex.isVertex) {
            for (int i = 0; i < curString.length(); i++) {
                out.write(curString.charAt(i));
            }
            out.write('\n');
        }
        for (int i = 0; i < 256; i++) {
            if (curVertex.links[i] != null) {
                curString.append((char)i);
                goRoundTree(out, curVertex.links[i], curString);
                curString.deleteCharAt(curString.length() - 1);
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
            while ((c = (char)i) != '\n' && i != END_OF_FILE) {
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
        if (this.contains(element)) {
            return false;
        }
        Vertex currentVertex = vertexHead;
        currentVertex.countTermVertexLower++;
        for (char c: element.toCharArray()){
            if (currentVertex.links[(int)c] != null) {
                currentVertex = nextForAdd(currentVertex, c);
            }
            else {
                Vertex newVertex = new Vertex();
                currentVertex.links[(int)c] = newVertex;
                currentVertex = nextForAdd(currentVertex, c);
            }
        }
        currentVertex.isVertex = true;
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
        return curVertex.isVertex;
    }

    private int helperRemove_Prefix(String element, String function) {
        Vertex curVertex = vertexHead;
        for (char c: element.toCharArray()){
            if (curVertex.links[c] == null) {
                return 0;
            }
            if (curVertex.countTermVertexLower > 1 && curVertex.links[c].countTermVertexLower == 1) {
                if (function == "remove") {
                    curVertex.countTermVertexLower--;
                    curVertex.links[c] = null;
                }
                return 1;
            }
            else {
                if (function == "remove") {
                    curVertex.countTermVertexLower--;
                }
                curVertex = curVertex.links[c];
            }
        }
        if (function == "remove") {
            if (curVertex.isVertex) {
                curVertex.isVertex = false;
                curVertex.countTermVertexLower--;
            } else {
                return 0;
            }
        }
        return curVertex.countTermVertexLower;
    }

    @Override
    public boolean remove(String element) {
        if (helperRemove_Prefix(element, "remove") == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        return helperRemove_Prefix(prefix, "howManyStartsWithPrefix");
    }

    /*    @Override
            public boolean remove(String element) {
                if (!contains(element)) {
                    return false;
                }
                Vertex curVertex = vertexHead;
                for (char c: element.toCharArray()){
                    if (curVertex.countTermVertexLower > 1 && curVertex.links[c].countTermVertexLower == 1) {
                        curVertex.countTermVertexLower--;
                        curVertex.links[c] = null;
                        return true;
                    }
                    else {
                        curVertex.countTermVertexLower--;
                        curVertex = curVertex.links[c];
                    }
                }
                curVertex.isVertex = false;
                curVertex.countTermVertexLower--;
                return true;
            }
        */
    @Override
    public int size() {
        return vertexHead.countTermVertexLower;
    }
/*    @Override
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
*/
}
