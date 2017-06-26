/**
 * 
 */
package edu.cnm.deepdive.cards;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Creates a Hand object drawn from a deck of cards.
 * 
 * @author Sky Link
 *
 */
public class Hand implements Comparable<Hand> {
  
  private final Deck deck;
  private final int size;
  private final ArrayList<Card> cards;
  /**
   * Constructor for Hand.
   * 
   * @param deck The deck object to be drawn from.
   * @param size The size of hand to be drawn.
   */
  public Hand(Deck deck, int size) {
    this.deck = deck;
    this.size = size;
    cards = new ArrayList<>(Arrays.asList(deck.draw(size)));
  }

  @Override
  public int compareTo(Hand arg0) {
    // TODO Auto-generated method stub
    return 0;
  }
  
  
  
}
