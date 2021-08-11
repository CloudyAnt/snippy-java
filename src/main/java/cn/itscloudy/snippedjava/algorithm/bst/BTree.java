package cn.itscloudy.snippedjava.algorithm.bst;

import lombok.AllArgsConstructor;

public class BTree {
    private final int maxDegree;
    private final int maxValueIndex;
    private final int tmpValueIndex;
    private final int maxChildIndex;
    private final int tmpChildIndex;
    private final int overflowIndex;
    private final int splitMiddle;

    public Node top;

    public BTree(int maxDegree) {
        this.maxDegree = maxDegree;
        maxValueIndex = maxDegree - 2;
        tmpValueIndex = maxValueIndex + 1;
        maxChildIndex = maxDegree - 1;
        tmpChildIndex = maxChildIndex + 1;
        overflowIndex = tmpValueIndex / 2;
        splitMiddle = overflowIndex + 1;
    }

    public Node top() {
        return top;
    }

    public void insert(int i, String words) {
        if (top == null) {
            top = new Node(i, words);
        } else {
            top.insert(new Value(i, words));
            if (top.needSplit()) {
                Value newTopValue = top.pourOverflow();
                Node newNode = top.split();

                Node newTop = new Node(newTopValue);
                newTop.children[0] = top;
                newTop.children[1] = newNode;
                top = newTop;
            }
        }
    }

    public Value search(int i) {
        if (top == null) {
            return null;
        }
        return top.search(i);
    }

    protected class Node {
        protected final Value[] values = new Value[maxDegree];
        protected final Node[] children = new Node[maxDegree + 1];

        private Node(int i, String words) {
            values[0] = new Value(i, words);
        }

        private Node(Value value) {
            values[0] = value;
        }

        private void insert(Value v) {
            if (children[0] == null) {
                insertToLeaf(v);
                return;
            }

            // get child branch
            int i = 0;
            Node child = children[0];
            for (; i < maxChildIndex; i++) {
                if (values[i] == null || v.i < values[i].i) {
                    child = children[i];
                    break;
                }
            }
            // insert into child branch
            child.insert(v);
            if (child.needSplit()) {
                // pour overflow
                v = child.pourOverflow();
                Node newChild = child.split();

                int j = i;
                Value tmpValue = v;
                while (j < tmpValueIndex) {
                    Value tmp1 = values[j];
                    values[j] = tmpValue;
                    tmpValue = tmp1;
                    j++;
                }

                j = i + 1;
                Node tmpNode = newChild;
                while (j < tmpChildIndex) {
                    Node tmp1 = children[j];
                    children[j] = tmpNode;
                    tmpNode = tmp1;
                    j++;
                }
            }
        }

        /**
         * Insert into current leaf
         *
         * @param nv New value
         */
        private void insertToLeaf(Value nv) {
            if (values[maxValueIndex] != null) {
                // overflow
                this.values[tmpValueIndex] = nv;
                sortLeafValues();
                return;
            }

            int i = 0;
            for (; i <= tmpValueIndex; i++) {
                // finding the right index of nv
                if (values[i] == null) {
                    // insert to the end
                    values[i] = nv;
                    return;
                }
                if (values[i].i == nv.i) {
                    // skip same value
                    return;
                }
                if (values[i].i > nv.i) {
                    break;
                }
            }

            Value temp = values[i];
            values[i] = nv;
            for (i++; i <= tmpValueIndex; i++) {
                Value temp1 = values[i];
                values[i] = temp;
                if (temp1 == null) {
                    break;
                }
                temp = temp1;
            }
        }

        /**
         * Split current node and return the new node (right of parent)
         *
         * @return new node
         */
        private Node split() {
            Node right = new Node(values[splitMiddle]);
            values[splitMiddle] = null;
            for (int vi = splitMiddle + 1, rvi = 1; vi <= tmpValueIndex; vi++, rvi++) {
                right.values[rvi] = values[vi];
                right.children[rvi] = children[vi];
                values[vi] = null;
                children[vi] = null;
            }
            return right;
        }

        private Value pourOverflow() {
            Value overflow = values[overflowIndex];
            values[overflowIndex] = null;
            return overflow;
        }

        private void sortLeafValues() {
            for (int i = 1; i < values.length; i++) {
                for (int j = 0; j < values.length - i; j++) {
                    if (values[j].i > values[j + 1].i) {
                        Value temp = values[j];
                        values[j] = values[j + 1];
                        values[j + 1] = temp;
                    }
                }
            }
        }

        private boolean needSplit() {
            return values[tmpValueIndex] != null;
        }

        public Value search(int i) {
            int index = 0;
            for (; index < values.length; index++) {
                if (values[index] == null || values[index].i > i) {
                    break;
                }
                if (values[index].i == i) {
                    return values[index];
                }
            }
            if (children[index] == null) {
                return null;
            }
            return children[index].search(i);
        }
    }

    @AllArgsConstructor
    protected static class Value {
        protected final int i;
        protected final String words;
    }

    private static class Overflow<T> {
        private T value;
        private Node newNode;
    }

}
