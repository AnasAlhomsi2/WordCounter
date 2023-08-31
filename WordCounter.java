import java.util.Comparator;

import components.map.Map;
import components.map.Map.Pair;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Simple HelloWorld program (clear of Checkstyle and SpotBugs warnings).
 *
 * @author Anas Alhomsi
 */
public final class WordCounter {

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        String word = "" + text.charAt(position);
        int index = position + 1;

        if (separators.contains(text.charAt(position))) {
            while (index < text.length()
                    && separators.contains(text.charAt(index))) {
                word += text.charAt(index);
                index++;
            }
        } else {
            while (index < text.length()
                    && !separators.contains(text.charAt(index))) {
                word += text.charAt(index);
                index++;
            }
        }
        return word;
    }

    /**
     * Comparing String to sort them alphabetically.
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str1.toLowerCase().compareTo(str2.toLowerCase());
        }
    }

    /**
     * This method stores all the elements of the queue in a map to count the
     * duplicates.
     *
     * @param q
     *            stores all the words in alphabetical order
     * @return This method returns a map of words and counter
     */
    public static Map<String, Integer> map(Queue<String> q) {
        Map<String, Integer> wordsAndCount = new Map1L<>();
        for (String x : q) {
            if (!wordsAndCount.hasKey(x)) {
                wordsAndCount.add(x, 1);
            } else {
                int value = wordsAndCount.value(x) + 1;
                wordsAndCount.replaceValue(x, value);
            }
        }
        return wordsAndCount;
    }

    /**
     * This method call in NextWordOrSeparator method to store all the words and
     * duplicates in a queue.
     *
     * @param text
     *            stores all the words given by the user.
     * @param separators
     *            stores all the separators that I added in the main method
     * @return This method returns a queue of all the words with duplicates
     */
    public static Queue<String> storeInQueue(String text,
            Set<Character> separators) {
        SimpleWriter out = new SimpleWriter1L();
        Queue<String> s = new Queue1L<>();
        int x = 0;
        int position = 0;
        String word = "";
        while (position < text.length()) {
            word = nextWordOrSeparator(text, position, separators);
            if (!separators.contains(word.charAt(0))) {
                s.enqueue(word);
            }
            position += word.length();
        }

        return s;
    }

    /**
     * Prints out the html page.
     *
     * @param fileName
     *            contains the name of the file entered by the user.
     * @param map
     *            contains the map with each word and how many times it was
     *            repeated in a text.
     */
    public static void outputFile(String fileName, Map<String, Integer> map) {
        SimpleWriter out = new SimpleWriter1L(fileName);

        out.println("<html>");
        out.println("<head>");
        out.print("<title>");
        out.print("<Words Counted in " + fileName + ">");
        out.println("</title>");
        out.println("</head>");
        out.println("<body>");
        out.print("<h2>");
        out.print("<Words Counted in " + fileName + ">");
        out.println("</h2>");
        out.println("<hr />");
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<th>Words</th>");
        out.println("<th>Counts</th>");
        out.println("</tr>");
        Queue<String> q = new Queue1L<>();

        for (Pair<String, Integer> x : map) {
            String key = x.key();
            q.enqueue(key);
        }
//      Calling the comparator that we created to sort the words in alphabetical order
        Comparator<String> compare = new StringLT();
        q.sort(compare);

        //Now we need to print all the words and their counter
        for (String x : q) {
            out.println("<tr>");
            out.println("<td>" + x + "</td>");
            out.println("<td>" + map.value(x) + "</td>");
            out.println("</tr>");
        }

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleWriter out = new SimpleWriter1L();
        SimpleReader in = new SimpleReader1L();

        //Prompts the user to enter an input file
        out.print("Input file Name: ");
        String inputName = in.nextLine();
        SimpleReader readInput = new SimpleReader1L(inputName);

        //Reading the userInput file and storing it in a string
        String strText = "";
        while (!readInput.atEOS()) {
            strText = strText + " " + readInput.nextLine();
        }

        //Prompts the user to enter an output file
        out.print("Output file Name: ");
        String outputName = in.nextLine();

        //Set of separators
        Set<Character> separator = new Set1L<>();
        separator.add(' ');
        separator.add(',');
        separator.add('.');
        separator.add('-');
        separator.add('!');

        //Calling storeInQueue method
        Queue<String> queueWords = new Queue1L();
        queueWords = storeInQueue(strText.substring(1, strText.length()),
                separator);

        //Calling outputFile method to create the output file in html
        outputFile(outputName, map(queueWords));
        out.close();
    }

}
