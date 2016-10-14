/*
 * Nicholas Spurlock
 * 
 * ShortestPath contains the entry point for the program along with
 * static methods to process command line arguments and find shortest
 * paths.
*/


package edu.unm.shortest_path;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class ShortestPath
{
  private static final String ERROR_ARGS = "Incorrect number of arguments.  Arguments must contain a path to a dictionary followed by pairs of words";

  /*
   * Parameters:
   * 	Inputs:
   * 		String START: The word to start searching from
   * 		String END: the word to find the shortest path to
   * 		WordGraph graph: The graph of connected words to search through
   * 	Outputs:
   * 		None
   * Return Value:
   * 	The WordNode of the last word in the path
   * Description:
   * 	Using the start and end words and the generated graph, the shortest path
   * 	between the two words is found	
   * Algorithm:
   * 	Using the start word, the associated WordNode is retrieved from the graph.
   * 	The WordNode is added to a priority queue.  The list of connected words is
   * 	retrieved from the WordNode, and each word is weighted using the Levenshtein distance 
   * 	and the length of the path so far to that word.  All weighted words are added to the priority
   * 	queue.  The top of the queue is pulled and parsed in the same way until the queue is empty and their
   * 	is no path, or the end word is found.
   */
  public static WordNode findShortestPath(final String start, final String end, final WordGraph graph)
  {
    LinkedList<WordNode> removedList = new LinkedList<>(); //Used to track removed words so their values can be reset before a new word pair
    Comparator<WordNode> comparator = new WordNodeComparator();
    PriorityQueue<WordNode> queue = new PriorityQueue<>(comparator);
    
    WordNode current = graph.getNode(start);
    if (current == null || graph.getNode(end) == null)
    {
      WordNode endNode = graph.getNode(end);
      if (current == null && endNode == null)
      {
        System.out.format("%s and %s not found in dictionary. \n", start, end);
      }
      else if(endNode == null)
      {
        System.out.format("%s not found in dictionary. \n", end);
      }
      else
      {
        System.out.format("%s not found in dictionary. \n", start);
      }
      return null;
    }
    current.setSeen(true);
    queue.add(current);
    while (!queue.isEmpty())
    {
      current = queue.peek();
      if (current.getName().equalsIgnoreCase(end))
      {
        for(WordNode node: removedList)
        {
          node.setSeen(false);
        }
        for(WordNode node: queue)
        {
          node.setSeen(false);
        }
        current.addToPath(current.getName());
        return current;
      }
            
      updateQueue(current.getShorterList(), current, queue, end);
      updateQueue(current.getSameList(), current, queue, end);
      updateQueue(current.getLongerList(), current, queue, end);
      
      removedList.add(current);
      queue.remove(current);
    }
    return null;
  }
  
  /*
   * Parameters:
   * 	Inputs:
   * 		char[] s: the starting word
   * 		char[] t: the target word
   *  	Outputs:
   *  		none
   * Return Value:
   * 	The Levenshtein distance
   * Description:
   * 	An algorithm that determines a heurstic for the distance between two words.
   * 	Found and adapted from https://en.wikipedia.org/wiki/Levenshtein_distance
   * Algorithm:
   * 	Using a 2D matrix, the distance between any prefixes of the two given words is found.  
   * 	The last entry in the matrix is the distance between the two words
   */
  public static int getLevenshteinDistance(final char[] s, final char[] t)
  {
    //Default initializes to 0
    int[][] distArry = new int[s.length + 1][t.length + 1];
    int sLength = s.length +1;
    int tLength = t.length + 1;
    int i = 0;
    int subCost = 0;
    int insertion = 0;
    int substitution = 0;
    
    for (i = 1; i < sLength; i++)
    {
      distArry[i][0] = i;
    }
    
    for (i = 1; i < tLength; i++)
    {
      distArry[0][i] = i;
    }
    for (int j = 1; j < tLength; j++)
    {
      for (i = 1; i < sLength; i++)
      {
        subCost = (s[i - 1] == t[j - 1]) ? 0 : 1;
        
        int dist = distArry[i-1][j] + 1;
        insertion = distArry[i][j-1] + 1;
        substitution = distArry[i-1][j-1] + subCost;
        if (insertion < dist)
        {
          dist = insertion;
        }
        if (substitution < dist)
        {
          dist = substitution;
        }
        distArry[i][j] = dist;
      }

    }
    return distArry[sLength - 1][tLength - 1];
  }
  
  /*
   * Parameters:
   *   Inputs:
   *     List<WordNode> list: The list containing the nodes to be added
   *     WordNode current: The current word node
   *     PriorityQueue<WordNode> queue: The queue to be updated
   *     String END: The word to get to
   *   Outputs:
   *     None
   * Return Value:
   *   void
   * Description:
   *   Checks each 
   *   Looks at and adds every new node to the queue after calculating its weight.
   * Algorithm:
   *   For each node in the list, if it is new (not seen) its weight is calculated as the sum of its Levenshtein distance and path
   *   length up to this point.  It is added to the queue, inserted by weight, and marked as seen so as not to be added to the queue
   *   again.
   */
  private static void updateQueue(final List<WordNode> list, final WordNode current, final PriorityQueue<WordNode> queue, final String end)
  {
    int lWeight; //The Levenshtein distance between the two words
    int pathLength; //Number of step up to this point
    for(WordNode node: list)
    {
      if (!node.getSeen())
      { 
        node.setWordPath(new LinkedList<String>(current.getWordPath()));
        node.addToPath(current.getName());
        lWeight = getLevenshteinDistance(node.getName().toCharArray(), end.toCharArray());
        pathLength = node.getWordPath().size();
        node.setWeight(lWeight + pathLength);
        node.setSeen(true);
        queue.add(node);
      }
    }
  }
  
  /*
   * Parameters:
   * 	Inputs:
   * 		String[] args: The command line arguments
   *  	Outputs:
   *  		None
   * Return Value:
   * 	A list of command line args
   * Description:
   * 	Turns the String[] into a list of args for easier use
   * Algorithm:
   * 	Iterates over the list and adds each entry to a list
   */
  private static LinkedList<String> parseCommandArgs(String[] args)
  {
    LinkedList<String> argList = new LinkedList<>();
    if ((args.length - 1) % 2 != 0)
    {
      System.out.println(ERROR_ARGS);
      return null;
    }
    for (String s: args)
    {
      argList.add(s);
    }
    return argList;
  }
  
  public static void main(String[] args)
  {
    WordNode solution = null;
    String w1 = null;
    String w2 = null;
    LinkedList<String> argList = parseCommandArgs(args);
    if (argList == null)
    {
      return;
    }
    System.out.println("Graph generation started...");
    HashGraph graph = new HashGraph(12, argList.pop());
//    while (!argList.isEmpty())
//    {
//      w1 = argList.pop();
//      w2 = argList.pop();
//      solution = findShortestPath(w1, w2, graph);
//      if (solution == null)
//      {
//        System.out.format("NO POSSIBLE PATH: %s to %s\n", w1, w2);
//      }
//      else
//      {
//        for (String s: solution.getWordPath())
//        {
//          System.out.print(s + " ");
//        }
//        System.out.println();
//      }
//    }
  }

}
