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
  
  public static final int DEFAULT_HAND_SIZE = 5;
  
  private static final int STRAIGHT_FLUSH_VALUE = 8;
  private static final int FOUR_OF_A_KIND_VALUE = 7;
  private static final int FULL_HOUSE_VALUE = 6;
  private static final int FLUSH_VALUE = 5;
  private static final int STRAIGHT_VALUE = 4;
  private static final int THREE_OF_A_KIND_VALUE = 3;
  private static final int TWO_PAIR_VALUE = 2;
  private static final int PAIR_VALUE = 1;
  private static final int HIGH_CARD_VALUE = 0;
  
  private final Deck deck;
  private final int size;
  private final ArrayList<Card> cards;
  
  /**
   * Constructor for Hand.
   * 
   * @param deck The deck object to be drawn from.
   * @param size The size of hand to be drawn.
   */
  protected Hand(Deck deck, int size) {
    this.deck = deck;
    this.size = size;
    cards = new ArrayList<>(Arrays.asList(deck.draw(size)));
  }
  
  /**
   * Constructor for Hand with default hand size.
   * 
   * @param deck The deck object to be drawn from.
   */
  public Hand(Deck deck) {
    this(deck, DEFAULT_HAND_SIZE);
  }
  
  @Override
  public String toString() {
    return cards.toString();
  }
  
  @Override
  public int compareTo(Hand otherHand) {
    int[] value = this.value();
    int[] otherValue = otherHand.value();
    for ( int i = 0; i < Math.min(value.length, otherValue.length); i++) {
      int comparison = Integer.compare(value[i], otherValue[i]);
      if (comparison != 0) {
        return comparison;
      }
    }
    return 0;
  }
  
  /**
   * Sorts cards by ranks.
   * 
   * @return A two dimensional array of Card objects by rank.
   */
  public Card[][] byRanks() {
    Card[][] table = new Card[Card.Rank.values().length][];
    for (Card.Rank rank: Card.Rank.values()) {
      ArrayList<Card> members = new ArrayList<>();
      for (Card card: cards) {
        if (card.rank == rank) {
          members.add(card);
        }
      }
      members.sort(null);
      table[rank.ordinal()] = members.toArray(new Card[] {});
    }
    return table;
  }
  
  /**
   * Sorts cards by suits.
   * 
   * @return A two dimensional array of Card objects by suit.
   */
  public Card[][] bySuits() {
    Card[][] table = new Card[Card.Suit.values().length][];
    for (Card.Suit suit: Card.Suit.values()) {
      ArrayList<Card> members = new ArrayList<>();
      for (Card card: cards) {
        if (card.suit == suit) {
          members.add(card);
        }
      }
      members.sort(null);
      table[suit.ordinal()] = members.toArray(new Card[] {});
    }
    return table;
  }
  
  
  private boolean flush(Card[][] table) {
    for (Card[] suitedCards: table) {
      if(suitedCards.length > 0 && suitedCards.length < size) {
        return false;
      }
      if(suitedCards.length == size) {
        return true;
      }
    }
    return false;
  }
  
  private ArrayList<Card.Rank> sets(Card[][] table, int size) {
    ArrayList<Card.Rank> result = new ArrayList<>();
    for (Card.Rank rank : Card.Rank.values()) {
      if (table[rank.ordinal()].length == size) {
        result.add(rank);
      }
    }
    return result;
  }
  
  private Card.Rank run(Card[][] table, boolean lowHigh) {
    Card.Rank runStart = null;
    int runLength = 0;
    boolean inRun = false;
    for (Card.Rank rank : Card.Rank.values()) {
      if (table[rank.ordinal()].length == 1) {
        if (!inRun) {
          runStart = rank;
          runLength = 0;
          inRun = true;
        }
        inRun = true;
        runLength++;
      } else if (runLength != size){
        inRun = false;
        runStart = null;
        runLength = 0;
      } else {
        inRun = false;
      }
    }
    if (inRun && lowHigh && table[0].length == 1) {
      runLength++;
    }
    return (runLength == size) ? runStart : null;
  }
  
  private Card.Rank run(Card[][] table) {
    return run(table, true);
  }
  
  private int[] value() {
    Card[][] byRanks = this.byRanks();
    Card[][] bySuits = this.bySuits();
    boolean flush = this.flush(bySuits);
    Card.Rank straightStart = this.run(byRanks);
    boolean straightFlush = flush && (straightStart != null);
    ArrayList<Card.Rank> fourOfAKindRanks = this.sets(byRanks, 4);
    ArrayList<Card.Rank> threeOfAKindRanks = this.sets(byRanks, 3);
    ArrayList<Card.Rank> twoOfAKindRanks = this.sets(byRanks, 2);
    ArrayList<Card.Rank> oneOfAKindRanks = this.sets(byRanks, 1);
    boolean fullHouse = (threeOfAKindRanks.size() > 0) && (twoOfAKindRanks.size() > 0);
    boolean twoPair = twoOfAKindRanks.size() == 2;
    
    // Check for a straight flush.
    if (straightFlush) {
      return straightFlushValue(straightStart);
    }
    
    // Check for four of a kind.
    if (fourOfAKindRanks.size() > 0) {
      return fourOfAKindValue(fourOfAKindRanks, oneOfAKindRanks);
    }
    
    // Check for a full house.
    if (fullHouse) {
      return fullHouseValue(threeOfAKindRanks, twoOfAKindRanks);
    }
    
    // Check for a flush.
    if (flush) {
      return flushValue();
    }
    
    // Check for a straight.
    if (straightStart != null) {
      return straightValue(straightStart);
    }
    
    // Check for three of a kind.
    if (threeOfAKindRanks.size() > 0) {
      return threeOfAKindValue(threeOfAKindRanks);
    }
    
    // Check for two pair.
    if (twoPair) {
      return twoPairValue(twoOfAKindRanks, oneOfAKindRanks);
    }
    
    // Check for two of a kind.
    if (twoOfAKindRanks.size() > 0) {
      return pairValue(twoOfAKindRanks, oneOfAKindRanks);
    }
    
    // EVERYTHING ELSE !!!
    return highCardValue();
    
  }

  private int[] straightFlushValue(Card.Rank straightStart) {
    return new int[] {STRAIGHT_FLUSH_VALUE, straightStart.ordinal()};
  }

  private int[] fourOfAKindValue(ArrayList<Card.Rank> fourOfAKindRanks,
      ArrayList<Card.Rank> oneOfAKindRanks) {
    int setRank = fourOfAKindRanks.get(0).ordinal();
    if (setRank == 0) {
      setRank = Card.Rank.values().length;
    }
    return new int[] {FOUR_OF_A_KIND_VALUE, setRank, oneOfAKindRanks.get(0).ordinal()};
  }

  private int[] fullHouseValue(ArrayList<Card.Rank> threeOfAKindRanks,
      ArrayList<Card.Rank> twoOfAKindRanks) {
    int overSetRank = threeOfAKindRanks.get(0).ordinal();
    if (overSetRank == 0) {
      overSetRank = Card.Rank.values().length;
    }
    int underSetRank = twoOfAKindRanks.get(0).ordinal();
    if (underSetRank == 0) {
      underSetRank = Card.Rank.values().length;
    }
    return new int [] {FULL_HOUSE_VALUE, overSetRank, underSetRank};
  }

  private int[] flushValue() {
    int[] ranks = new int[size];
    for (int i = 0; i < size; i++) {
      int cardRank = cards.get(i).rank.ordinal();
      if (cardRank == 0) {
        cardRank = Card.Rank.values().length;
      }
      ranks[i] = cardRank;
    }
    Arrays.sort(ranks);
    int[] score = new int[size +1];
    score[0] = FLUSH_VALUE;
    for (int i = size -1; i >= 0; i--) {
      score[size - i] = ranks[i];
    }
    return score;
  }

  private int[] straightValue(Card.Rank straightStart) {
    return new int[] {STRAIGHT_VALUE, straightStart.ordinal()};
  }

  private int[] threeOfAKindValue(ArrayList<Card.Rank> threeOfAKindRanks) {
    int setRank = threeOfAKindRanks.get(0).ordinal();
    if (setRank == 0) {
      setRank = Card.Rank.values().length;
    }
    return new int[] {THREE_OF_A_KIND_VALUE, setRank};
  }

  private int[] twoPairValue(ArrayList<Card.Rank> twoOfAKindRanks,
      ArrayList<Card.Rank> oneOfAKindRanks) {
    int firstRank = twoOfAKindRanks.get(0).ordinal();
    int secondRank = twoOfAKindRanks.get(1).ordinal();
    int otherRank = oneOfAKindRanks.get(0).ordinal();
    if (firstRank == 0) {
      return new int[] {TWO_PAIR_VALUE, Card.Rank.values().length, secondRank, otherRank};
    } else {
      return new int[] {TWO_PAIR_VALUE, secondRank, firstRank, otherRank};
    }
  }

  private int[] pairValue(ArrayList<Card.Rank> twoOfAKindRanks,
      ArrayList<Card.Rank> oneOfAKindRanks) {
    int setRank = twoOfAKindRanks.get(0).ordinal();
    if (setRank == 0) {
      setRank = Card.Rank.values().length;
    }
    int[] ranks = new int[size - 2];
    for (int i = 0; i < oneOfAKindRanks.size(); i++) {
      int cardRank = oneOfAKindRanks.get(i).ordinal();
      if (cardRank == 0) {
        cardRank = Card.Rank.values().length;
      }
      ranks[i] = cardRank;
    }
    Arrays.sort(ranks);
    int[] score = new int[size];
    score[0] = PAIR_VALUE;
    score[1] = setRank;
    for (int i = ranks.length -1; i >= 0; i--) {
      score[ranks.length - i + 1] = ranks[i];
    }
    return score;
  }

  private int[] highCardValue() {
    int[] ranks = new int[size];
    for (int i = 0; i < size; i++) {
      int cardRank = cards.get(i).rank.ordinal();
      if (cardRank == 0) {
        cardRank = Card.Rank.values().length;
      }
      ranks[i] = cardRank;
    }
    Arrays.sort(ranks);
    int[] score = new int[size +1];
    score[0] = HIGH_CARD_VALUE;
    for (int i = size -1; i >= 0; i--) {
      score[size - i] = ranks[i];
    }
    return score;
  }

}













