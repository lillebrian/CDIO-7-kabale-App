package com.example.a7_kabale.Logic;

import java.util.ArrayList;
import java.util.LinkedList;

public class AppController {
    Logic logic = new Logic();

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

}
