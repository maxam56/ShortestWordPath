/*
 * Nicholas Spurlock
 * 
 * This class contains functionality for creating a graph of words and word connections within one degree.
 * Using an ascii text file as a dictionary, it reads and connects every word to every other word within one legal move.
 */

package edu.unm.shortest_path;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordGraph
{
  private final int MAX_WORD_LENGTH;
  private final int MAX_LIST_SIZE; //A list size max used to avoid thousands of list copies cause by list expansion. Trading memory for time.
  private ArrayList<ArrayList<WordNode>> graph;
 
  public WordGraph(final int maxWordLength, final String dictionaryPath)
  {
    this.MAX_WORD_LENGTH = maxWordLength;
    this.MAX_LIST_SIZE = 30000;
    graph = new ArrayList<ArrayList<WordNode>>(MAX_WORD_LENGTH + 1);
    initGraph();
    generateGraph(dictionaryPath);
  }
  
  /*
   * Parameters:
   * 	Inputs:
   * 		String name: The name of the WordNode to be returned
   * 	Outputs:
   * 		None
   * Return Value:
   * 	WordNode n
   * Description:
   * 	Returns the WordNode associated with name
   * Algorithm:
   * 	Searches HashMap by key name
   */
  public WordNode getNode(final String name)
  {
    for (WordNode n: graph.get(name.length()))
    {
      if (n.getName().equalsIgnoreCase(name))
      {
        //Insures we start an empty list, not necessary for weights as they are recalculated each time
        n.getWordPath().clear();
        return n;
      }
    }
    return null;
  }
  
  /*
   * Parameters:
   * 	Inputs:
   * 		None
   *  	Outputs:
   *  		Printed graph of connected words by length
   * Return Value:
   * 	None
   * Description:
   * 	Prints generated graph of connected words
   * Algorithm:
   * 	Iterates through each (key, value) pair and utilizes WordNode.printEdges to print connected words
   */
  @SuppressWarnings("unused")
  private void printGraph()
  {
    for (ArrayList<WordNode> list: graph)
    {
      for (WordNode node: list)
      {
        System.out.println("Name: " + node.getName());
        System.out.println("Edges:");
        node.printEdges();
        System.out.println("-----------------------------");
        System.out.println("List Size: " + list.size());
      }
      System.out.println("###############################");
    }
  }
  
  /*
   * Parameters:
   * 	Inputs:
   * 		String newWord: Word most recently read in from dictionary
   * 		String oldWord: Existing word in graph map
   * 		int diff: The difference in the length of oldWord and newWord
   * 	Outputs:
   * 		None
   * Return Value:
   * 		boolean: true if the two words are connected by one move, false if not
   * Description:
   * 		Takes two words and determines if they are connected by one legal move
   * Algorithm:
   * 		Based on difference in length, the words are checked for a connection of one legal move.
   * 		If diff is 0, then if more than one character is different between the words then false is returned
   * 		If diff is +/-1 then the words are checked char by char.  If an unmatched char is found, the longer
   * 		word index is offset by one and the search continues.  If another is found, false is returned.
   */
  private boolean isConnected(final String newWord, final String oldWord, final int diff)
  {
    if(diff == 0) //Same length, replace letters.  If there is only one char diff return true
    {
      int count = 0;
      for (int i = 0; i < oldWord.length(); i++)
      {
        if (oldWord.charAt(i) != newWord.charAt(i))
        {
          count++;
          if (count > 1)
          {
            return false;
          }
        }
      }
      return true;
    }
    else //words diff lengths
    {
      int count = 0;
      String longer;
      String shorter;
      if (diff == -1) //newWord is shorter
      {
        longer = oldWord;
        shorter = newWord;
      }
      else
      {
        longer = newWord;
        shorter = oldWord;
      }
      for (int i = 0; i < longer.length(); i++)
      {
        if ((i - count) < shorter.length() && longer.charAt(i) != shorter.charAt(i - count))
        {
          count++;
          if (count > 1) //More than one letter difference, not within one legal move
          {
            return false;
          }
        }
      }
      
    }
    return true;
  }
  
  /*
   * Parameters:
   * 	Inputs: 
   * 		None
   * 	Outputs:
   * 		None
   * Return Value:
   * 		void
   * Description:
   * 		Initializes the lists used in the graph
   * Algorithm:
   * 		Adds entry for each word length and initializes the list
   */		
  private void initGraph()
  {
    graph = new ArrayList<ArrayList<WordNode>>(MAX_WORD_LENGTH + 1);
    //Initialize all lists in the graph from length 0 to max length + 1. The additional spot allows the use of word lengths and indices
    for( int i = 0; i < MAX_WORD_LENGTH + 1; i++)
    {
      graph.add(new ArrayList<WordNode>(MAX_LIST_SIZE));
    }
  }
  
  /*
   * Parameters:
   * 	Inputs:
   * 		String path: The path to the dictionary
   * 	Outputs:
   * 		None
   * Return Value:
   * 		void
   * Description:
   * 		Reads through a text file of words(dictionary) it generates a graph of words connected by one legal move.
   * Algorithm:
   * 		For each word in the dictionary a WordNode is created and lists of words one letter less, the same length, and one letter more
   * 		are retrieved from a HashMap.  Each list is iterated over and each new word from the dictionary is checked against
   * 		every word from the lists.  If a connection is found between the words they are added to the appropriate list of each
   * 		WordNode.  The new WordNode is then added to the HashMap
   */
  private void generateGraph(final String path)
  {
    //TimerThread thread = new TimerThread("TimerThread");
    
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
        int length = word.length();
        WordNode newNode = new WordNode(word, length);
        for (int i = length - 1; i < length + 2; i++)
        {
          //Single letter word, no smaller-length list so skip
          if (i < 1 || i > MAX_WORD_LENGTH) 
          {
            continue;
          }
          for (WordNode node: graph.get(i))
          {
            //Check if there is an edge between the new word and existing words
            if (isConnected(word, node.getName(), (length - i)))
            {
              if (i == length)
              {
                newNode.getSameList().add(node);
                node.getSameList().add(newNode);
              }
              else if(i < length)
              {
                newNode.getShorterList().add(node);
                node.getLongerList().add(newNode);
              }
              else
              {
                newNode.getShorterList().add(node);
                node.getLongerList().add(newNode);
              }
            }
          }
        }
        graph.get(length).add(newNode);
      }
      fin.close();
    } 
    catch (IOException e)
    {
      System.err.println("Failed to open dictionary at " + path + ".");
      e.printStackTrace();
    }

  }
}

  