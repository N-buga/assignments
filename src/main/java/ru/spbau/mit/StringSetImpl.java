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
        private char b;
        private int termVertexLower;
        private boolean termVertex;
        private Vertex[] links = new Vertex[256];
    }

    private static final int EndOfFile = 9;

    @Override
    public void serialize(OutputStream out) throws SerializationException {
        Vertex curVertex = vertexHead;
        StringBuilder curString = new StringBuilder();
        try {
            goRoundTree(out, curVertex, curString);
            out.write((char) EndOfFile);
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private void goRoundTree(OutputStream out, Vertex curVertex, StringBuilder curString) throws IOException {
        if (curVertex == null)
            return;
        if (curVertex.termVertex) {
            for (int i = 0; i < curString.length(); i++) {
                out.write(curString.charAt(i));
            }
            out.write('\n');
        }
        int i;
        for (i = 0; i < 256; i++) {
            if (curVertex.links[i] != null) {
                curString.append((char)i);
                goRoundTree(out, curVertex.links[i], curString);
                curString.deleteCharAt(curString.length() - 1);
            }
        }
    }

    @Override
    public void deserialize(InputStream in) throws SerializationException{
        vertexHead = new Vertex();
        char c = 0;
        int i = 0;
        while (i != EndOfFile) { //65535
            StringBuilder curString = new StringBuilder();
            try {
                i = in.read();
                while ((c = (char)i) != '\n' && i != EndOfFile) {
                    curString.append(c);
                    i = in.read();
                }
            } catch (IOException e) {
                throw new SerializationException("deserialize");
            }
            if (i == EndOfFile)
                break;
            add(curString.toString());
        }
    }

    private Vertex vertexHead = new Vertex();

    @Override
    public boolean add(String element){
        if (this.contains(element))
            return false;
        Vertex currentVertex = vertexHead;
        currentVertex.termVertexLower++;
        for (int i = 0; i < element.length(); i++){
            if (currentVertex.links[(int)element.charAt(i)] != null) {
                currentVertex = currentVertex.links[element.charAt(i)];
                currentVertex.termVertexLower++;
            }
            else {
                Vertex newVertex = new Vertex();
                currentVertex.links[(int)element.charAt(i)] = newVertex;
                currentVertex = currentVertex.links[element.charAt(i)];
                currentVertex.b = element.charAt(i);
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
            if (curVertex.links[element.charAt(i)] == null)
                return false;
            curVertex = curVertex.links[element.charAt(i)];
        }
        return curVertex.termVertex;
    }

    @Override
    public boolean remove(String element) {
        if (!this.contains(element))
            return false;
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
            if (curVertex.links[prefix.charAt(i)] == null)
                return 0;
            curVertex = curVertex.links[prefix.charAt(i)];
        }
        return curVertex.termVertexLower;
    }
}
