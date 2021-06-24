package com.example.a7_kabale.Logic;

import com.example.a7_kabale.Logic.Enums.*;
import java.util.ArrayList;
import java.util.LinkedList;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public class Suit {
    ArrayList<LinkedList<Card>> suitArray;

    /** Standard constructor for the suit*/
    public Suit() {
        suitArray = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            LinkedList<Card> temp = new LinkedList<>();
            temp.add(new Card(true, 0, Type.fromInteger(i) ));
            suitArray.add(temp);
        }
    }

    /** Clone method for recursion purposes*/
    public Suit cloneSuit() {
        Suit temp = new Suit();
        temp.suitArray = new ArrayList<>();
        for (int i = 0; i < this.suitArray.size(); i++) {
            temp.suitArray.add(new LinkedList<>());
            for (int j = 0; j < this.getSuit(i).size(); j++) {
                temp.getSuit(i).add(this.getSuit(i).get(j));
            }
        }
        return temp;
    }

    /** This method returns the top card of a type suit*/
    public Card getTop(int index) {
        if (index < 0 || index > 3)
            return new Card(false);
        return suitArray.get(index).peekLast();
    }

    /** This method returns the suit of a certain type from the index
     * which normally comes from the card.GetType().getvalue()*/
    public LinkedList<Card> getSuit(int index) {
        if (index < 0 || index > 3)
            return new LinkedList<>();
        return suitArray.get(index);
    }

    /** This method will attempt to remove a card from the suit
     * @param card The card which is to be removed*/
    public Block removeCard(Card card) {
        LinkedList<Card> list = getSuit(card.getType().getValue());
        for (int i = 0; i < list.size(); i++) {
            Card suitCard =list.get(i);
            if (suitCard.compareCards(card)) {
                list.remove(i);
                Block temp = new Block();
                temp.getBlock().add(suitCard);
                return temp;
            }
        }
        return null;
    }

    public boolean suitFinished() {
        for (LinkedList<Card> suit : this.suitArray) {
            if (suit.size() != 14)
                return false;
        }
        return true;
    }

    public Card lowestSuit() {
        Card temp = new Card(false);
        for (LinkedList<Card> suit : this.suitArray) {
            if (suit.peekLast().getFaceValue() < temp.getFaceValue())
                temp = suit.peekLast();
        }
        return temp;
    }

    public boolean suitContains(Card card) {
        return suitArray.get(card.getType().getValue()).peekLast().compareCards(card);
    }
}
