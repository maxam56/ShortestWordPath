package edu.unm.shortest_path;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;



public class HashGraph
{
  private final int MAX_WORD_LENGTH;
  private final int MAX_LIST_SIZE; //A list size max used to avoid thousands of list copies cause by list expansion. Trading memory for time.
  private HashSet<String> dictionary;
  private HashMap<String, ArrayList<WordNode>> graph;
 
  public HashGraph(final int maxWordLength, final String dictionaryPath)
  {
    this.MAX_WORD_LENGTH = maxWordLength;
    this.MAX_LIST_SIZE = 30000;
    this.dictionary = new HashSet<>();
    this.graph = new HashMap<>();
    //initGraph();
    generateGraph(dictionaryPath);
  }
  
  /*
   * Parameters:
   *  Inputs:
   *    String name: The name of the WordNode to be returned
   *  Outputs:
   *    None
   * Return Value:
   *  WordNode n
   * Description:
   *  Returns the WordNode associated with name
   * Algorithm:
   *  Searches HashMap by key name
   */
//  public WordNode getNode(final String name)
//  {
//    for (WordNode n: graph.get(name.length()))
//    {
//      if (n.getName().equalsIgnoreCase(name))
//      {
//        //Insures we start an empty list, not necessary for weights as they are recalculated each time
//        n.getWordPath().clear();
//        return n;
//      }
//    }
//    return null;
//  }
//  
  /*
   * Parameters:
   *  Inputs:
   *    None
   *    Outputs:
   *      Printed graph of connected words by length
   * Return Value:
   *  None
   * Description:
   *  Prints generated graph of connected words
   * Algorithm:
   *  Iterates through each (key, value) pair and utilizes WordNode.printEdges to print connected words
   */
  @SuppressWarnings("unused")
  private void printGraph()
  {
    Iterator<String> it = dictionary.iterator();
    while (it.hasNext())
    {
      String word = it.next();
      System.out.println("Word: " + word);
      for (WordNode node: graph.get(word))
      {
        node.printEdges();
      }
    }
  }
  
  /*
   * Parameters:
   *  Inputs:
   *    String newWord: Word most recently read in from dictionary
   *    String oldWord: Existing word in graph map
   *    int diff: The difference in the length of oldWord and newWord
   *  Outputs:
   *    None
   * Return Value:
   *    boolean: true if the two words are connected by one move, false if not
   * Description:
   *    Takes two words and determines if they are connected by one legal move
   * Algorithm:
   *    Based on difference in length, the words are checked for a connection of one legal move.
   *    If diff is 0, then if more than one character is different between the words then false is returned
   *    If diff is +/-1 then the words are checked char by char.  If an unmatched char is found, the longer
   *    word index is offset by one and the search continues.  If another is found, false is returned.
   */
  private void isConnected(final String word)
  {
    StringBuilder sb;
    char[] cWord = word.toCharArray();
    graph.put(word, new ArrayList<WordNode>());
    for (int i = 0; i < word.length(); i++)
    {
      char temp = cWord[i];
      for (int j = 97; j < 123; j++)
      {
        
        cWord[i] = (char)j;
        if (dictionary.contains(String.valueOf(cWord)))
        {
          System.out.println("Added " + String.valueOf(cWord));
          if (!graph.containsKey(word))
          {
            graph.get(word).add(new WordNode(cWord.toString(), cWord.length));
          }
  
        }
        
      }
      cWord[i] = temp;
    }
    for (int i = 0; i < word.length() + 1; i++)
    {
      sb = new StringBuilder(word);
      for (int j = 97; j < 123; j++)
      {
        if (dictionary.contains(sb.insert(i, (char)j).toString()))
        {
          graph.get(word).add(new WordNode(sb.toString(), sb.length()));
        }
        sb.deleteCharAt(i);
        if (i < word.length())
        {
          char temp = sb.charAt(i);
          if (dictionary.contains(sb.deleteCharAt(i).toString()))
          {
            graph.get(word).add(new WordNode(sb.toString(), sb.length()));
          }
          sb.insert(i, temp);        }
        
      }
    }
  }
  
  /*
   * Parameters:
   *  Inputs: 
   *    None
   *  Outputs:
   *    None
   * Return Value:
   *    void
   * Description:
   *    Initializes the lists used in the graph
   * Algorithm:
   *    Adds entry for each word length and initializes the list
   */   
//  private void initGraph()
//  {
//    graph = new ArrayList<ArrayList<WordNode>>(MAX_WORD_LENGTH + 1);
//    //Initialize all lists in the graph from length 0 to max length + 1. The additional spot allows the use of word lengths and indices
//    for( int i = 0; i < MAX_WORD_LENGTH + 1; i++)
//    {
//      graph.add(new ArrayList<WordNode>(MAX_LIST_SIZE));
//    }
//  }
  
  /*
   * Parameters:
   *  Inputs:
   *    String path: The path to the dictionary
   *  Outputs:
   *    None
   * Return Value:
   *    void
   * Description:
   *    Reads through a text file of words(dictionary) it generates a graph of words connected by one legal move.
   * Algorithm:
   *    For each word in the dictionary a WordNode is created and lists of words one letter less, the same length, and one letter more
   *    are retrieved from a HashMap.  Each list is iterated over and each new word from the dictionary is checked against
   *    every word from the lists.  If a connection is found between the words they are added to the appropriate list of each
   *    WordNode.  The new WordNode is then added to the HashMap
   */
  private void generateGraph(final String path)
  {
    //TimerThread thread = new TimerThread("TimerThread");
    double beforeMills = System.currentTimeMillis();
    FileInputStream fin = null;
    try
    {
      String word = null;
      fin = new FileInputStream(path);
      InputStreamReader reader = new InputStreamReader(fin);
      BufferedReader buffReader = new BufferedReader(reader);
      
      //Read each word until we've read them all
      while ((word = buffReader.readLine()) != null)
      {
        dictionary.add(word);
      }
      
      java.util.Iterator<String> it = dictionary.iterator();
      while(it.hasNext())
      {
        isConnected(it.next());
      }
      double end = System.currentTimeMillis() - beforeMills;
      System.out.println(end);
      printGraph();
      fin.close();
    } 
    catch (IOException e)
    {
      System.err.println("Failed to open dictionary at " + path + ".");
      e.printStackTrace();
    }

  }
}
