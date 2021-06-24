package com.example.a7_kabale.Logic;

import java.util.ArrayList;
import java.util.LinkedList;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public class AppController {
    Logic logic;

    public AppController () {
        logic = new Logic();
    }

    public void RunAlgorithm(ArrayList<String> initialStackCards, ArrayList<String> initialDeckCards) {
        logic.setUpApp(logic.listStringToCard(initialStackCards), logic.listStringToCard(initialDeckCards));
        logic.run();

    }

    public void RunAlgorithm(ArrayList<String> turnedCards) {
        logic.turnUnknownCard(logic.listStringToCard(turnedCards));
        logic.run();
    }


    public LinkedList<Card> returnBestMoves () {
        return logic.getAbsoluteBestMoves();
    }

    public int cardsToTurn() {
        return logic.amountOfUnturnedCards();
    }

    public boolean isWon() {
        return logic.winnable;
    }

    public boolean isLost() {
        return logic.isLost();
    }

}
