package com.example.a7_kabale.Logic;

import java.util.ArrayList;
import java.util.LinkedList;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
  The move class is used to contain a chain of moves and their total amount of algorithmic points*/
public class Move {
    int point;
    LinkedList<Card> moveList;
    LinkedList<Card> moveListSim;

    public Move() {
        this.point = 0;
        this.moveList = new LinkedList<>();
        this.moveListSim = new LinkedList<>();
    }

    public Move clone() {
        Move temp = new Move();
        temp.point = this.point;
        temp.moveList.addAll(this.moveList);
        temp.moveListSim.addAll(this.moveListSim);
        return temp;
    }

    public Move addMove(int points, Card card1, Card card2) {
        Move move = new Move();
        move.point = point + points;

        move.moveList.addAll(moveList);
        move.moveList.add(card1);
        move.moveList.add(card2);

        move.moveListSim.addAll(moveListSim);
        move.moveListSim.add(card1);
        move.moveListSim.add(card2);

        return move ;
    }

    public void add(Move move) {
        this.point += move.point;
        this.moveList.addAll(move.moveList);
        this.moveListSim.addAll(move.moveListSim);
    }

    public boolean hasMoves() {
        return moveListSim.size() >= 2;
    }

    public LinkedList<Card> getSimMoves() {
        return moveListSim;
    }

    public boolean alreadyMoved(Card moved) {
        for (int i = 0; i < moveListSim.size(); i += 2) {
            if (moveListSim.get(i).compareCards(moved))
                return true;
        }
        return false;
    }

    public boolean identicalMove(Card moveFrom, Card moveTo) {
        for (int i = 0; i < moveList.size(); i += 2) {
            if (moveList.get(i).compareCards(moveFrom) && (moveList.get(i+1).compareCards(moveTo)))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Move{ " + point + " points " + moveList.toString() + '}';
    }

    public LinkedList<Card> getMoveList() {
        return moveList;
    }

}
