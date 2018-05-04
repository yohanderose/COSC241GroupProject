/* File: WordSalad.java - April 2018 */

import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Skeleton implementation of the WordSalad class.
 *
 * @author Quinn Thorsnes, Kelson Sadlier, Yohan de Rose
 */
public class WordSalad implements Iterable<String> {

    /**
     * Allows WordSalad to know what WordNode comes first in the LinkedList.
     */
    private WordNode first;
    /**
     * Allows WordSalad to know what WordNode comes last in the LinkedList.
     */
    private WordNode last;

    /**
     * Constructs an empty WordSalad object.
     */
    public WordSalad() {
        this.first = null;
        this.last = null;
    }

    /**
     * Constructs WordSalad object from String List object.
     * @param words a list of strings
     */
    public WordSalad(java.util.List<String> words) {
        for (String word : words) {
            addLast(word);
        }
    }

    /**
     * @param word
     */
    public void add(String word) {
        if (this.first == null) {
            this.first = new WordNode(word, null);
            this.last = this.first;
            return;
        }
        WordNode newFirst = new WordNode(word, this.first);
        this.first = newFirst;
    }

    /**
     * @param word
     */
    public void addLast(String word) {
        if (this.first == null) {
            add(word);
            return;
        }
        WordNode newLast = new WordNode(word, null);
        this.last.next = newLast;
        this.last = newLast;
    }

    /**
     *
     */
    private class WordNode {
        private String word;
        private WordNode next;

        private WordNode(String word, WordNode next) {
            this.word = word;
            this.next = next;
        }

    }

    /**
     * @return
     */
    public java.util.Iterator<String> iterator() {
        return new java.util.Iterator<String>() {
            private WordNode current = first;

            public boolean hasNext() {
                return current != null;
            }

            public String next() {
                String result = current.word;
                current = current.next;
                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * @return
     */
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        WordNode node = first;
        while (node != null) {
            result.append(node.word);
            result.append(node.next == null ? "" : ", ");
            node = node.next;
        }
        return result.toString() + "]";
    }


    // Method stubs to be completed for the assignment.
    // See the assignment description for specification of their behaviour.

    /**
     * Puts the first word in first block, 2nd in 2nd block, kth word in kth block,
     * wrap around blocks
     * like when dealing out cards
     *
     * @param k The number of blocks to distribute into
     * @return an array of WordSalads
     */
    public WordSalad[] distribute(int k) {
        // make use of modulo operator to determine which group to put a word in
        // remember that WordSalads are linked lists of Strings with add() and addLast() methods
        WordSalad[] distribution = new WordSalad[k];
        int wordCount = 0;
        int currIndex;
        for (int i = 0; i < k; i++) {
            distribution[i] = new WordSalad();
        }
        for (String word : this) {
            currIndex = wordCount % k;
            distribution[currIndex].addLast(word);
            wordCount++;
        }
        return distribution;
    }

    /**
     * This method divides the count of words in this into almost equal blocks and  populates an ArrayList with these values.
     * This is then used as the lengths for corresponding WordSalad objects in another array.
     * From the start of the original WordSalad object we fill each WordSalad block until its corresponding length is met, and continue until the whole array is full.
     * @param k used to divide original length into almost equal lengths
     * @return an array of WordSalad objects chopped into k pieces
     */
    public WordSalad[] chop(int k) {
        //establish lengths of each WordSalad item
        ArrayList<Integer> lengths = new ArrayList<>();
        Iterator<String> pointer = this.iterator();
        int count = 0;

        while (pointer.hasNext()) {
            pointer.next();             //remember to move iterator along
            count++;
        }
        int x = count / k;
        int r = count % k;

        for (int i = 0; i < k - r; i++) {
            lengths.add(x);       //add x (k-r) times
        }                     //while sum != words: add (count%sum)/r, r--

        int sum = 0;
        for (int i : lengths) {
            sum += i;
        }

        while (sum != count) {
            lengths.add(0, (count % sum) / r);
            r--; //might be r/2
            sum = 0;
            for (int i : lengths) {
                sum += i;
            }
        }

        //construct WordSalad array with reference to lengths
        WordSalad[] result = new WordSalad[k];
        for (int i = 0; i < result.length; i++) {
            result[i] = new WordSalad();
        }

        pointer = this.iterator();
        for (int i = 0; i < lengths.size(); i++) {
            int saladSize = 0;
            while (saladSize < lengths.get(i)) {
                result[i].addLast(pointer.next());
                saladSize++;
            }
        }
        return result;
    }

    /**
     * This method distributes the WordSalad object and keeps the first element of this result, merging the remaining elements so the words are ordered.
     * Then the distribute is called on these remaining, first element is kept, remainder ordered and so on until the original object is empty.
     * @param k count that each distribute call is passed
     * @return returns an array of WordSalad objects that are split
     */
    public WordSalad[] split(int k) {
        WordSalad[] currPass;
        WordSalad[] lastPass;
        WordSalad[] finalPass = new WordSalad[0];
        WordSalad saladLeft = this;
        int finalLength = 0;
        boolean splitting = true;
        while (splitting) {
            lastPass = finalPass;
            currPass = saladLeft.distribute(k);
            saladLeft = merge(Arrays.copyOfRange(currPass, 1, currPass.length));
            if (saladLeft.first == null) {
                splitting = false;
            }

            finalPass = new WordSalad[lastPass.length + 1];
            for (int i = 0; i < lastPass.length; i++) {
                finalPass[i] = lastPass[i];
            }

            finalPass[lastPass.length] = currPass[0];
        }
        return finalPass;
    }

    /**
     * @param blocks
     * @return
     */
    public static WordSalad merge(WordSalad[] blocks) {
        // The opposite of distribute
        WordSalad merged = new WordSalad();
        WordNode[] mergeNodes = new WordNode[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            mergeNodes[i] = blocks[i].first;
        }
        boolean merging = true;
        while (merging) {
            for (int i = 0; i < mergeNodes.length; i++) {
                if (mergeNodes[i] == null) {
                    merging = false;
                } else {
                    merged.addLast(mergeNodes[i].word);
                    mergeNodes[i] = mergeNodes[i].next;
                }
            }
        }
        return merged;
    }

    /**
     * @param blocks
     * @return
     */
    public static WordSalad join(WordSalad[] blocks) {
        // The opposite of Chop
        WordSalad joined = new WordSalad();
        for (WordSalad salad : blocks) {
            for (String word : salad) {
                joined.addLast(word);
            }
        }

        return joined;
    }

    /**
     * @param blocks
     * @param k
     * @return
     */
    public static WordSalad recombine(WordSalad[] blocks, int k) {
        // opposite of split... so... do split in reverse?
        WordSalad[] curToMerge = new WordSalad[1];
        WordSalad[] lastDist;
        WordSalad recombination = new WordSalad();
        int dists = 0;
        boolean recombining = true;
        while (recombining) {
            if (recombination.first != null) {
                lastDist = recombination.distribute(k - 1);
                curToMerge = new WordSalad[lastDist.length + 1];
                for (int i = 0; i < (curToMerge.length - 1); i++) {
                    curToMerge[i + 1] = lastDist[i];
                }

                if (lastDist.length == 2) {
                    curToMerge[2] = lastDist[1];
                }
            }
            dists++;
            if (dists >= blocks.length) {
                recombining = false;
            }
            curToMerge[0] = blocks[blocks.length - dists];
            recombination = merge(curToMerge);
        }


        return recombination;
    }

}
