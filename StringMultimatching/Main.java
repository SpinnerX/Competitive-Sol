import java.util.*;

// Just some NOTES for CS-185

/**
 * 
 * NOTED On May 10th, 2023, we optimized the search function by adding in a pre-caching into the searchPatterns()!!!!! (IN-PROGRESS)
 * 
 * ALSO NOTE: THIS IS THE OPTIMIZATION VERSION OF THE AHO-CORASICK ALGORITHM IMPLEMENTATION!
 */

/**
 * Part 1
 * 1.) Implementing a ternary-searching trie (Context)
 *  - Best option for storing characters in a heirarchical tree structure
 * Node - basically has a character its associated with.
 * 
 * Optimization inlook
 * - This structure is faster when searching for larger collections of character strings
 * 
 * Running time of Aho-Corasick is able to search in length of O(N)
 * 
 * Trie implementation - O(m + n)
 * 
 * 
 * NOTE: (Just some random thing I learned)
 * - The trie allows the process of searching up the patterns a lot faster in parallel
 * 
 * Aho Corasick (without the tree)
 * - Can be slower in O(N) because the pre-processing step of constructing the trie helps speed up the process.
 * 
 * 
 */


 /**
  * Part 2
  * Using the Trie Data structure
  */

  /**
   * Example, showing the TrieNode data structure if we have three paths
   * paths = (a-b-c, a-b-d, and e-f-g)
   * 
   | (Root)
  |
  |
  |
  V
  (a) - (b) - (c)
  |       |
  |       V
  |       d
  |
  V
  e - f
      |
      V
      g

   */
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>(); // Contains characters to the nodes used in the Trie structure. Storing diff paths of the node.
    boolean isEndOfPattern; // indicating when we are at the end of the pattern
    List<Integer> patternIndices = new ArrayList<>(); // storing the indices of the patterns that end at this node.
    TrieNode fail; // referencing to the node that represents the longest prefix of the current nodes path that is also a suffix of some other path
}

class Trie{
    public TrieNode root = null;

    public Trie(){
        root = new TrieNode();
    }

    /**
     * build the failure function by checking if the children of the nodes
     */
    public void buildFailureFunction() {
        Queue<TrieNode> queue = new LinkedList<>();
        
        // Assign the fail node to the root, for assigning the loop.
        // Doing BFS, so were initializing the queue this way, and then do BFS.
        for (TrieNode node : root.children.values()) {
            node.fail = root;
            queue.add(node);
        }
        
        while (!queue.isEmpty()) {
            TrieNode node = queue.remove();
            
            // Traversing through the hash table this way.
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                char c = entry.getKey();
                
                
                TrieNode child = entry.getValue();
                TrieNode fail = node.fail;
                
                // Checking if we have not reached the node and have not found this corresponding character as part of the chilcdren of the current node we're at
                while (fail != root && !fail.children.containsKey(c)) fail = fail.fail;
                
                if (fail.children.containsKey(c)) fail = fail.children.get(c);

                child.fail = fail;
                child.patternIndices.addAll(fail.patternIndices); 
                queue.add(child);
            }
        }
    }

    // Adds a specific pattern to the trie then iterates over those characters as well adding a new Node for each character that doesn't exist.
    // failure var used to indicate when we are at the end of the pattern.
    public void addPattern(String pattern, int index) {
        TrieNode node = root;

        for (char c : pattern.toCharArray()) {
            
            // Checking if the node children of this current node is in the hashtable if not, we basically do children[m] = new TrieNode
            if (!node.children.containsKey(c)) node.children.put(c, new TrieNode());
            
            // Checking if the node children of this current node is in the hashtable if not, we basically do children[m] = new TrieNode

            node = node.children.get(c);
        }

        node.isEndOfPattern = true;
        node.patternIndices.add(index);
    }

    public List<List<Integer>> searchPatterns(String text, String[] patterns) {
        List<List<Integer> > patternIndices = new ArrayList<>();

        for (int i = 0; i < patterns.length; i++) patternIndices.add(new ArrayList<>());
        
        // checking if the string to check for a specific pattern is null/empty return that pattern;
        if (text == null || text.isEmpty() || patterns == null || patterns.length == 0) return patternIndices;

        root = new TrieNode();

        for (int i = 0; i < patterns.length; i++) addPattern(patterns[i], i);
        
        buildFailureFunction();
        
        TrieNode node = root;

        // Help find all instances of a given set of patterns, if part of a loop which iterates through each character in the text.
        // Optimization NOTE: Adding this while-loop helps keep track of the transition of the characters.
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            TrieNode last = null;

            while(node != root && !node.children.containsKey(c)){
                if(last == null) last = node;
                node = node.fail;
            }
            
            // node = node.children.getOrDefault(c, root);
            // if(last != null) last.children.getOrDefault(c, node);
            // if(last != null) last.children.put(c, node);

            // checks if the current node is not the root node and if the node does not have a child with the current character. If
            // both of these conditions are true, then the code sets the node to the failure node. The failure node points to the 
            // node which was last successfully matched and is used to continue the search for the given patterns.
            /*
            while (node != root && !node.children.containsKey(c)) node = node.fail;*/
            TrieNode tempNode = null;
            if ((tempNode = node.children.get(c)) != null) node = tempNode;

            /*
            for (int index : node.patternIndices) patternIndices.get(index).add(i - patterns[index].length() + 1);
            */
            
            // node = node.children.getOrDefault(c, root);
            if(last != null) last.children.put(c, node);

            // if (node.children.containsKey(c)) node = node.children.get(c);

            // NOTE: index - patternTypeCharIndex + 1 // this basically addds the specific pattern to our patternIndices
            for (int index : node.patternIndices) patternIndices.get(index).add(i - patterns[index].length() + 1);
        }

        return patternIndices;
    }
};

class Main{

    // This just basically prints out the patternIndices
    /**
     * 
     * Outputting formats!
     * 
     * 1.) output consist of n lines where it shows n number of patterns
     * 2.) the I'th lines contains the positions of all the occurrences of the i'th pattern in text, from first to last seperated by space...
     * 3.) Position listed in ascending order
     * 
     * @param patternIndices
     */

     /**
      * 1) The output consists of 6 lines, since there are 6 patterns.
        2) The first line contains the positions of all the occurrences of the first pattern in the text, from first to last, separated by a single space (2 4).
        3) The second line contains the positions of all the occurrences of the second pattern in the text, from first to last, separated by a single space (5).
        4) The third line contains the positions of all the occurrences of the third pattern in the text, from first to last, separated by a single space (7).
        5) The fourth line contains the positions of all the occurrences of the fourth pattern in the text, from first to last, separated by a single space (1 3 5 7).
        6) The fifth line contains the positions of all the occurrences of the fifth pattern in the text, from first to last, separated by a single space (1 3 5 7).
        7) The sixth line contains the positions of all the occurrences of the sixth pattern in the text, from first to last, separated by a single space (1 3 5 7 9 12).
      * @param patternIndices
      */
    static void print(List<List<Integer>> patternIndices){
        for (List<Integer> indices : patternIndices) {
            
            String[] indicesStr = new String[indices.size()];

            // basically formats the output so we can join the string with string args.
            for (int i = 0; i < indices.size(); i++) {
                indicesStr[i] = indices.get(i).toString();
            }

            System.out.println(String.join(" ", indicesStr));
        }
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        Trie trie = new Trie(); // create a trie


        while(scanner.hasNextLine()){
            String s = scanner.nextLine();
            if(s.isEmpty()) break; // checking for empty strings.

            int n = Integer.parseInt(s);
            String patterns[] = new String[n];

            for(int i = 0; i < n; i++) {
                patterns[i] = scanner.nextLine();
            }

            String text = scanner.nextLine();
            List<List<Integer>> patternIndices = trie.searchPatterns(text, patterns);
            print(patternIndices);
        }



        scanner.close();
    }
}
