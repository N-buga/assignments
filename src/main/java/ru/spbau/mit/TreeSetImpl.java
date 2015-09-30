package ru.spbau.mit;

import java.util.*;


public class TreeSetImpl<E> extends AbstractSet<E> {
    final static Random random = new Random(100000);

    private Comparator<E> cur_comparator;
    private Node headNode = null;

    private class MyPair<X> {
        X first;
        X second;
        MyPair(X x1, X x2) {
            first = x1;
            second = x2;
        }
    }

    private MyPair<Node<E>> split(Node<E> curNode, E e) {
        int resOfCompare = cur_comparator.compare(curNode.element, e);
        MyPair<Node<E>> pairNodes;
        if (resOfCompare == 1) {
            if (curNode.left != null) {
                pairNodes = split(curNode.left, e);
                curNode.left = pairNodes.second;
                if (pairNodes.second != null)
                    pairNodes.second.pred = curNode;
                curNode.count();
                pairNodes.second = curNode;
                return pairNodes;
            } else {
                return new MyPair(null, curNode);
            }
        } else {
            if (curNode.right != null) {
                pairNodes = split(curNode.right, e);
                curNode.right = pairNodes.first;
                if (pairNodes.first != null)
                    pairNodes.first.pred = curNode;
                pairNodes.first = curNode;
                curNode.count();
                return pairNodes;
            }
            else {
                return new MyPair<>(curNode, null);
            }
        }
    }

    private Node<E> merge(Node<E> node1, Node<E> node2) {
        if (node1 == null) return node2;
        if (node2 == null) return node1;
        if (node1.y > node2.y) {
            node1.right = merge(node1.right, node2);
            if (node1.right != null)
                node1.right.pred = node1;
            node1.count();
            return node1;
        } else {
            node2.left = merge(node1, node2.left);
            if (node2.left != null)
                node2.left.pred = node2;
            node2.count();
            return node2;
        }
    }

    private class myIterator implements Iterator<E> {
        private Node<E> curNode;
        private Node<E> lastNode = null;

        private myIterator() {
            curNode = headNode;
            if (headNode != null)
                while (curNode.left != null)
                    curNode = curNode.left;
        }

        @Override
        public boolean hasNext() {
            if (curNode != null)
                return true;
            else
                return false;
        }

        @Override
        public E next() {
            if (curNode == null)
                throw new NoSuchElementException();
            lastNode = curNode;
            curNode = nextNoChange(curNode);
            return lastNode.element;
        }

        @Override
        public void remove() {
            if (lastNode == null)
                throw new IllegalStateException();
            else removeNode(lastNode);
        }
    };

    private Node nextNoChange(Node<E> curNode) {
        Node tempNode = curNode;
        if (curNode == null)
            return null;
        if (tempNode.right == null)
        {
            while (tempNode != headNode) {
                if (tempNode.pred.left == tempNode) {
                    tempNode = tempNode.pred;
                    return tempNode;
                } else {
                    tempNode = tempNode.pred;
                }
            }
            return null;
        }
        else {
            tempNode = tempNode.right;
            while (tempNode.left != null)
                tempNode = tempNode.left;
            return tempNode;
        }
    }


    private void removeNode(Node<E> lastNode) {
        MyPair<Node<E>> pairs1 = split(headNode, lastNode.element);
        Node curNode = pairs1.first;
        while (curNode.right != null) {
            curNode = curNode.right;
        }
        if (curNode == pairs1.first)
            headNode = merge(curNode.left, pairs1.second);
        else {
            curNode.pred.right = curNode.left;
            if (curNode.left != null) {
                curNode.left.pred = curNode.pred;
            }
            curNode = curNode.pred;
            while (curNode != null) {
                curNode.nodesBelow--;
                curNode = curNode.pred;
            }
            merge(pairs1.first, pairs1.second);
        }
    }

    private static class Node<E> {
        private Node left;
        private Node right;
        private int nodesBelow;
        private E element;
        private int y;
        private Node pred = null;
        private void count() {
            nodesBelow = 0;
            if (right != null) nodesBelow += right.nodesBelow + 1;
            if (left != null) nodesBelow += left.nodesBelow + 1;
        }
        private Node() {
            y = random.nextInt();
        }
    }

    public TreeSetImpl(Comparator<E> comparator) {
        cur_comparator = comparator;
    }

    @Override
    public int size() {
        if (headNode == null)
            return 0;
        return headNode.nodesBelow + 1;
    }

    @Override
    public Iterator<E> iterator() {
        return new myIterator();
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) return false;
        if (headNode == null) {
            headNode = new Node();
            headNode.element = e;
            return true;
        }
        MyPair<Node<E>> pairNodes = split(headNode, e);
        Node<E> newNode = new Node<E>();
        newNode.element = e;
        headNode = merge(merge(pairNodes.first, newNode), pairNodes.second);
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (headNode == null) return false;
        MyPair<Node<E>> pairNodes = split(headNode, (E) o);
        Node<E> curNode = pairNodes.first;
        if (curNode == null) {
            headNode = pairNodes.second;
            return false;
        }
        while (curNode.right != null) {
            curNode = curNode.right;
        }
        headNode = merge(pairNodes.first, pairNodes.second);
        if (cur_comparator.compare(curNode.element, (E) o) == 0) {
            return true;
        } else {
            return false;
        }
     }

    @Override
    public boolean remove(Object o) {
        if (!contains(o))
            return false;
        MyPair<Node<E>> pairNodes = split(headNode, (E) o);
        Node<E> curNode = pairNodes.first;
        while (curNode.right != null) {
            curNode = curNode.right;
        }
        headNode = merge(pairNodes.first, pairNodes.second);
        removeNode(curNode);
        return true;
    }
}
