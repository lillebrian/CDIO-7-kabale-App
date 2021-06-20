package com.example.a7_kabale.Logic;

import com.example.a7_kabale.Logic.Enums.*;


import java.util.LinkedList;

public class Card {
    private boolean faceUp;
    private int faceValue;
    private Colour colour;
    private Type type;

    /** Primary constructor of a Card object
     * @param faceUp The status of the card if it's face up or not
     * @param faceValue The card value of the card
     * @param type The type of the card Hearts, Spade etc.*/
    public Card(boolean faceUp, int faceValue, Type type) {
        this.faceUp = faceUp;
        this.faceValue = faceValue;
        if (15 % (type.getValue() + 2) == 0)
            this.colour = Colour.Red;
        else
            this.colour = Colour.Black;
        this.type = type;
    }

    public Card(boolean faceUp, int faceValue, int type) {
        this.faceUp = faceUp;
        this.faceValue = faceValue;
        this.type = Type.fromInteger(type);
        if (15 % (this.type.getValue() + 2) == 0)
            this.colour = Colour.Red;
        else
            this.colour = Colour.Black;

    }

    /**Constructor for fast creating of unturned cards*/
    public Card(boolean faceUp) {
        this.faceUp = faceUp;
        this.faceValue = 0;
        this.type = Type.Unturned;
    }

    public Card(Type type) {
        this.type = type;
    }

    /**Cloning method for recursion purposes*/
    public Card cloneCard() {
        Card temp = new Card(false);
        temp.type = this.type;
        temp.faceValue = this.faceValue;
        temp.colour = this.colour;
        temp.faceUp = this.faceUp;
        return temp;
    }

    public Colour getColour() {
        return colour;
    }

    public int getFaceValue() {
        return faceValue;
    }

    public Type getType() {
        return type;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(Card card) {
        faceUp = true;
        this.faceValue = card.getFaceValue();
        this.type = card.getType();
        this.colour = card.getColour();
    }
    public void setFaceUp(int faceValue, int type) {
        faceUp = true;
        this.faceValue = faceValue;
        this.type = Type.fromInteger(type);
        if (15 % (this.type.getValue() + 2) == 0)
            this.colour = Colour.Red;
        else
            this.colour = Colour.Black;
    }

    /** Method to compare if two cards are the same card, notice not the same Object!
     * @param toCompare The card you want to compare with the card who this method was called on*/
    public boolean compareCards(Card toCompare) {
        return this.getFaceValue() == toCompare.getFaceValue() && this.getType() == toCompare.getType();
    }

    public LinkedList<Card> addCardsToContain(LinkedList<Card> allCards, Card card2) {
        LinkedList<Card> cardsToAdd = new LinkedList<>();
                if (allCards.isEmpty() || !allCards.contains(this) && !cardsToAdd.contains(this)){
                    cardsToAdd.add(this);
                }
                if (allCards.isEmpty() || !allCards.contains(card2) && !cardsToAdd.contains(card2)){
                    cardsToAdd.add(card2);
            }
        return cardsToAdd;
    }

    public boolean compareAlreadyContained(LinkedList<Card> allCards, Card card2){
        for (int i = 0; i < allCards.size(); i++) {
            if(compareCards(allCards.get(i)) || card2.compareCards(allCards.get(i)))
                return true;
        }
        return false;
    }

    //standard to string method
    @Override
    public String toString() {
        return "Card{" + faceValue + type.getString() + '}';
    }
}
