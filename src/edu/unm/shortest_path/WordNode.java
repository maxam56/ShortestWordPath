/*
 * Nicholas Spurlock
 * 
 * This class encapsulates the data required to track a word and its connected words, as well as metrics about the word
 * and its connections.  It is grouped by word length with other words and maintained in the WordGraph class.
 */

package edu.unm.shortest_path;

import java.util.ArrayList;
import java.util.LinkedList;

public class WordNode
{
  private final int LENGTH;
  private final String NAME;
  
  private ArrayList<WordNode> sameList;
  private ArrayList<WordNode> longerList;
  private ArrayList<WordNode> shorterList;
  private LinkedList<String> wordPath;
  
  private boolean seen; //Denotes whether a word has been seen before to prevent another addition to the queue
  private int weight; //Sum of Levenshtein distance and path length
 
  public WordNode(String name, int length)
  {
    this.LENGTH = length;
    this.NAME = name;
    sameList = new ArrayList<>();
    longerList = new ArrayList<>();
    shorterList = new ArrayList<>();
    wordPath = new LinkedList<>();
    seen = false;
    weight = 0;
  }
  
  public void addToPath(final String s)
  {
    this.wordPath.add(s);
  }
  public int getLength()
  {
    return this.LENGTH;
  }
  public ArrayList<WordNode> getLongerList()
  {
    return this.longerList;
  }
  public String getName()
  {
    return this.NAME;
  }
  public ArrayList<WordNode> getSameList()
  {
    return this.sameList;
  }
  public boolean getSeen()
  {
    return this.seen;
  }
  public ArrayList<WordNode> getShorterList()
  {
    return this.shorterList;
  }
  public LinkedList<String> getWordPath()
  {
    return this.wordPath;
  }
  public int getWeight()
  {
    return this.weight;
  }
  public void printEdges()
  {
    for (WordNode node: shorterList)
    {
      System.out.println(node.getName());
    }
    for (WordNode node: sameList)
    {
      System.out.println(node.getName());
    }
    for (WordNode node: longerList)
    {
      System.out.println(node.getName());
    }
  }
  public void setSeen(final boolean s)
  {
    this.seen = s;
  }
  public void setWeight(final int w)
  {
    this.weight = w;
  }
  public void setWordPath(final LinkedList<String> list)
  {
    this.wordPath = list;
  }
}
