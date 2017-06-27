/**
 * 
 */
package edu.cnm.deepdive.cards;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Creates a deck object with a standard set of 52 cards.
 * 
 * @author Sky Link
 *
 */
public class Deck {
  
  private ArrayList<Card> cards;
  private boolean shuffled;
  private Random rng = null;
  private int position;
  
  /**
   * Constructor for Deck.
   */
  public Deck() {
    super();
    cards = new ArrayList<>();
    for(Card.Suit suit: Card.Suit.values()) {
      for(Card.Rank rank: Card.Rank.values()) {
        Card card = new Card(suit, rank);
        cards.add(card);
      }
    }
    shuffled = false;
    position = 0;
  }
  
  @Override
  public String toString() {
    return cards.toString();
  }
  
  /**
   * Method to shuffle the Deck instance.
   * 
   * @throws NoSuchAlgorithmException Can't instantiate source of randomness.
   */
  public void shuffle() 
      throws NoSuchAlgorithmException {
    if (rng == null) {
      rng = SecureRandom.getInstanceStrong();
    }
    Collections.shuffle(cards, rng);
    shuffled = true;
    position = 0;
  }
  /**
   * Changes cards field to an array of Card objects.
   * 
   * @return A card array that holds the values from the cards ArrayList.
   */
  public Card[] toArray() {
    return cards.toArray(new Card[] {});
  }
  /**
   * Draws a single card from the deck and keeping track of location.
   * 
   * @return A card object from the current location in the cards ArrayList.
   * @throws IndexOutOfBoundsException Ran out of cards.
   */
  public Card draw() 
      throws IndexOutOfBoundsException {
    return cards.get(position++);
  }
  /**
   * Draws a specified number of cards.
   * 
   * @param numCards The number of cards to be drawn.
   * @return The set of cards that were drawn.
   * @throws IndexOutOfBoundsException Ran out of cards.
   */
  public Card[] draw(int numCards) 
      throws IndexOutOfBoundsException {
    Card[] hand = new Card[numCards];
    for (int i = 0; i < hand.length; i++) {
      hand[i] = draw();
    }
    return hand;
  }
  
}

