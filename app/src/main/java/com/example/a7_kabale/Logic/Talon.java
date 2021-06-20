package com.example.a7_kabale.Logic;

import java.util.ArrayList;

public class Talon {
    ArrayList<Card> deck;

    public Talon(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    /** cloning method for recursion purposes*/
    public Talon cloneTalon() {
        ArrayList<Card> temp = new ArrayList<>();
        temp.addAll(this.deck);
        return new Talon(temp);
    }

    /** This method looks to attempt to remove a card from the talon/deck*/
    public Block removeCard(Card card) {
        for (Card deckCard : deck) {
            if (deckCard.compareCards(card)) {
                deck.remove(deckCard);
                Block temp = new Block();
                temp.getBlock().add(card);
                return temp;
            }
        }
        return null;
    }

}
