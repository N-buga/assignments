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
        private int termVertexLower;
        private boolean termVertex;
        private Vertex[] links = new Vertex[256];
    }

    private static final int ENDOFFILE = 9;

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
        out.write((char) ENDOFFILE);
    }

    private void goRoundTree(OutputStream out, Vertex curVertex, StringBuilder curString) throws IOException {
        if (curVertex == null) {
            return;
        }
        if (curVertex.termVertex) {
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
        while (i != ENDOFFILE) {
            StringBuilder curString = new StringBuilder();
            i = in.read();
            while ((c = (char)i) != '\n' && i != ENDOFFILE) {
                curString.append(c);
                i = in.read();
            }
            if (i == ENDOFFILE) {
                break;
            }
            add(curString.toString());
        }
    }

    private Vertex vertexHead = new Vertex();

    @Override
    public boolean add(String element){
        if (this.contains(element)) {
            return false;
        }
        Vertex currentVertex = vertexHead;
        currentVertex.termVertexLower++;
        for (char c: element.toCharArray()){
            if (currentVertex.links[(int)c] != null) {
                currentVertex = currentVertex.links[c];
                currentVertex.termVertexLower++;
            }
            else {
                Vertex newVertex = new Vertex();
                currentVertex.links[(int)c] = newVertex;
                currentVertex = currentVertex.links[c];
                currentVertex.termVertexLower++;
            }
        }
        currentVertex.termVertex = true;
        return true;
    }

    @Override
    public boolean contains(String element) {
        Vertex curVertex = vertexHead;
        int i;
        for (i = 0; i < element.length(); i++) {
            if (curVertex.links[element.charAt(i)] == null) {
                return false;
            }
            curVertex = curVertex.links[element.charAt(i)];
        }
        return curVertex.termVertex;
    }

    @Override
    public boolean remove(String element) {
        if (!this.contains(element)) {
            return false;
        }
        Vertex curVertex = vertexHead;
        for (int i = 0; i < element.length(); i++){
            if (curVertex.termVertexLower > 1 && curVertex.links[element.charAt(i)].termVertexLower == 1) {
                curVertex.termVertexLower--;
                curVertex.links[element.charAt(i)] = null;
                return true;
            }
            else {
                curVertex.termVertexLower--;
                curVertex = curVertex.links[element.charAt(i)];
            }
        }
        curVertex.termVertex = false;
        curVertex.termVertexLower--;
        return true;
    }

    @Override
    public int size() {
        return vertexHead.termVertexLower;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        int i;
        Vertex curVertex = vertexHead;
        for (i = 0; i < prefix.length(); i++) {
            if (curVertex.links[prefix.charAt(i)] == null) {
                return 0;
            }
            curVertex = curVertex.links[prefix.charAt(i)];
        }
        return curVertex.termVertexLower;
    }
}
