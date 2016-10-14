/*
 * Nicholas Spurlock
 * 
 * This is a simple utility class used by the priority queue in ShortestPath to
 * prioritize WordNode objects by weight.
 */

package edu.unm.shortest_path;

import java.util.Comparator;

public class WordNodeComparator implements Comparator<WordNode>
{

  @Override
  public int compare(WordNode o1, WordNode o2)
  {
    if (o1.getWeight() < o2.getWeight())
    {
      return -1;
    }
    if (o1.getWeight() > o2.getWeight())
    {
      return 1;
    }
    return 0;
  }



}
