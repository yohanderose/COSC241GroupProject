package week09;

import java.io.*;
import java.util.*;

/**
 * Application class for the COSC241 assignment.
 *
 * See http://cs.otago.ac.nz/cosc241/pdf/asgn.pdf
 *
 * @author Iain Hewson
 */
public class SaladApp {

    /**
     *  A collection of WordSalad objects used to hold the result of
     *  calling some WordSalad methods, and as a parameter to other
     *  WordSalad methods.
     */
    private WordSalad[] multi;

    /**
     *  A WordSalad object used to hold the result of calling some
     *  WordSalad methods, and as a parameter to other WordSalad
     *  methods.
     */
    private WordSalad single;

    /**
     *  Entry point of the program.  Creates a SaladApp instance,
     *  preloads it with some words, and then calls the handleLine
     *  method to deal with input read from stdin.
     *
     * @param args The command line arguments are not used.
     */
    public static void main(String[] args) {
        SaladApp app = new SaladApp();
        app.load("words.txt");
        System.err.println(app.info());
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            app.handleLine(input.nextLine());
        }
    }

    /**
     *  Returns a message outlining commands and current state.
     *
     * @return current state of application and what the commands are.
     */
    private String info() {
        return "\nEnter commands to manipulate the current WordSalads:\n\n" +
            "Distribute N - distribute WS into N blocks\n" +
            "Merge        - merge multiple WS blocks into a single WS\n" +
            "Chop N       - chop WS into N blocks\n" +
            "Join         - join multiple WS blocks into a single WS\n" +
            "Split N      - split WS into multiple blocks using parameter N\n" +
            "Recombine N  - recombine multiple WS blocks using parameter N\n" +
            "Load FILE    - loads the contents of FILE into WS\n" +
            "Write FILE   - writes the current WS to FILE\n" +
            "\nSingle: " + single + "\nMulti: " + Arrays.deepToString(multi);
    }

    /**
     *  Manipulates the single WordSalad (WS) and the array of
     *  multiple WordSalads (MULT) according to commands read from the
     *  input parameter as follows:
     *
     * <pre>
     *  Distribute N - distribute WS into N salads storing in MULT
     *  Merge        - merge salads from MULT and store in WS
     *  Chop N       - chop WS into N salads storing in MULT
     *  Join         - join salads from MULT and store in WS
     *  Split N      - split WS and store in MULT using parameter N
     *  Recombine N  - recombine MULT salads into WS using parameter N
     *  Load FILE    - loads the contents of FILE into the single WS
     *  Write FILE   - writes the contents of the single WS to FILE
     * </pre>
     * 
     *  Note: No error checking is done, so for example calling chop,
     *  distribute, or split when the single WordSalad is null will
     *  cause an exception.
     *
     * @param input the source to read commands from.
     */
    public void handleLine(String input) {
        Scanner s = new Scanner(input);
        if (s.hasNext()) {
            String command = s.next().toLowerCase();
            switch (command) {
                case "distribute": case "d":
                    if (s.hasNextInt()) {
                        multi = single.distribute(s.nextInt());
                        single = null;
                    }
                    break;
                case "merge": case "m":
                    single = WordSalad.merge(multi);
                    multi = null;
                    break;
                case "chop": case "c":
                    if (s.hasNextInt()) {
                        multi = single.chop(s.nextInt());
                        single = null;
                    }
                    break;
                case "join": case "j":
                    single = WordSalad.join(multi);
                    multi = null;
                    break;
                case "split": case "s":
                    if (s.hasNextInt()) {
                        multi = single.split(s.nextInt());
                        single = null;
                    }
                    break;
                case "recombine": case "r":
                    if (s.hasNextInt()) {
                        single = WordSalad.recombine(multi, s.nextInt());
                        multi = null;
                    }
                    break;
                case "load": case "l":
                    if (s.hasNext()) {
                        load(s.next());
                    }
                    break;
                case "write": case "w":
                    if (s.hasNext()) {
                        write(s.next());
                    }
                    break;
                default:
                    System.err.println(info());
                    return;
            }
        }
        System.out.println("Single: " + single);
        System.out.println("Multi: " + Arrays.deepToString(multi));
    }

    /**
     *  Load words from a file into the single WordSalad.
     *
     * @param filename the name of the file to read words from.
     */
    private void load(String filename) {
        List<String> wordList = new LinkedList<>();
        try {
            Scanner words = new Scanner(new File(filename));
            while (words.hasNext()) {
                wordList.add(words.next());
            }
            single = new WordSalad(wordList);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *  Write the contents of the single WordSalad to a file on one
     *  line separated by spaces.
     *
     * @param filename the name of the file to write to.
     */
    private void write(String filename) {
        try {
            FileWriter output = new FileWriter(new File(filename));
            Iterator<String> i = single.iterator();
            while (i.hasNext()) {
                output.write(i.next() + (i.hasNext() ? " " : "\n"));
            }
            output.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
