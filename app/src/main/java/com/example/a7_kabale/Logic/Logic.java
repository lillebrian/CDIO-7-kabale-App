package com.example.a7_kabale.Logic;

import com.example.a7_kabale.Logic.Enums.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;


/**
 * This Class contains all the logic around the running of the algorithm
 */
public class Logic {
    //The different variables used throughout the class
    boolean winnable = false;
    ArrayList<Move> listOfMoves = new ArrayList<>();
    MoveLogic moveLogic = new MoveLogic();
    Talon talons;
    ArrayList<BuildStack> board = new ArrayList<>();
    BuildStackHolder buildStackHolder;
    Suit suits = new Suit();
    ArrayList<Card> remainingCards = new ArrayList<>();
    Random rn = new Random();
    int counter = 0;


    boolean lost = false;
    public boolean isLost() {
        return lost;
    }

    Move absoluteMax;


    /* For now a method for setting up the game hardcoded #Todo Should make this automated and not hardcoded */
    public void setUpForCustom() {
        ArrayList<Card> deckCards = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        setUpStandard(deckCards, cards);

//        generateGame(true,1);
//        testAlgorithm(100, true);
    }


    public void setUpApp (ArrayList<Card> stacks, ArrayList<Card> deckCards) {
        setUpStacks(stacks);
        talons = new Talon(deckCards);

    }

    /*The method called to run the algorithm */
    public void run() {
        if (winnable) {
            finishUp();
        }
        else {
            listOfMoves = new ArrayList<>();
            Move move = new Move();
            absoluteMax = new Move();
            absoluteMax.point = -1;
            try {
                checkForMoves(move, buildStackHolder, talons, suits, 0);
            } catch (Exception e) {
                if (!e.getMessage().equals("win"))
                    return;
            }
            if (absoluteMax.moveList.size() == 0 && counter++ > 3) {
                System.out.println("No solution :(");
                return;
            }

            String testString = "Max: " + absoluteMax;
            System.out.println(testString);
            //For testing purposes Only!
//        if ( testString.equals("Max: Move{ 10 points [Card{9♣️}, Card{10♥️}, Card{8♥️}, Card{9♣️}, Card{6♠️}, Card{7♥️}, Card{5♥️}, Card{6♠️}, Card{4♣️}, Card{5♥️}, Card{0?}, Card{0?}, Card{0?}, Card{0?}]}"))
//            System.out.println("yayet");
            performPermanentMoves(absoluteMax.clone());
        }
    }



    private void setUpStandard(ArrayList<Card> deckCards, ArrayList<Card> cards) {
        cards.add(new Card(true, 12, Type.Clover));
        cards.add(new Card(true, 7, Type.Clover));
        cards.add(new Card(true, 1, Type.Spade));
        cards.add(new Card(true, 7, Type.Spade));
        cards.add(new Card(true, 6, Type.Heart));
        cards.add(new Card(true, 10, Type.Clover));
        cards.add(new Card(true, 3, Type.Clover));
        setUpStacks(cards);

        deckCards.add(new Card(true, 4, 2));
        deckCards.add(new Card(true, 2, 2));
        deckCards.add(new Card(true, 5, 3));
        deckCards.add(new Card(true, 11, 1));
        deckCards.add(new Card(true, 2, 1));
        deckCards.add(new Card(true, 12, 1));
        deckCards.add(new Card(true, 13, 3));
        deckCards.add(new Card(true, 11, 0));
        deckCards.add(new Card(true, 9, 3));
        deckCards.add(new Card(true, 3, 2));
        deckCards.add(new Card(true, 10, 3));
        deckCards.add(new Card(true, 13, 0));
        deckCards.add(new Card(true, 9, 1));
        deckCards.add(new Card(true, 1, 3));
        deckCards.add(new Card(true, 9, 0));
        deckCards.add(new Card(true, 11, 3));
        deckCards.add(new Card(true, 7, 1));
        deckCards.add(new Card(true, 4, 3));
        deckCards.add(new Card(true, 8, 2));
        deckCards.add(new Card(true, 11, 2));
        deckCards.add(new Card(true, 1, 1));
        deckCards.add(new Card(true, 8, 3));
        deckCards.add(new Card(true, 6, 0));
        deckCards.add(new Card(true, 4, 1));
        talons = new Talon(deckCards);
    }

    private void setUpTestForKing(ArrayList<Card> deckCards, ArrayList<Card> cards) {

        cards.add(new Card(true, 0, Type.Heart));
        cards.add(new Card(true, 0, Type.Heart));
        cards.add(new Card(true, 13, Type.Spade));
        cards.add(new Card(true, 12, Type.Diamond));
        cards.add(new Card(true, 0, Type.Heart));
        cards.add(new Card(true, 0, Type.Diamond));
        cards.add(new Card(true, 0, Type.Clover));
        setUpStacks(cards);


        deckCards.add(new Card(true, 12, Type.Heart));
        deckCards.add(new Card(true, 13, Type.Heart));

        talons = new Talon(deckCards);
    }


    /**
     * This method is used to insert the card into the new position where we want it either in the build stack or in
     * the suit!
     *
     * @param toInsert   The card we want inserted
     * @param toInsertOn The targeted card we want our toInsert card inserted onto
     * @param stacks     The stack which this operation should be done on
     * @param suit       The suit which this operation should be done on
     */
    public void insertCard(Block toInsert, Card toInsertOn, BuildStackHolder stacks, Suit suit) {
        //First Check If toInsert is Leader If True insert whole block on toInsertOn's Block
        //If False, and toInsert isn't a Docker, Create new Sublist a New Block from toInsert's Index and remove
        //said sublist from the old block
        for (BuildStack stack : stacks.getStackList()) {
            //if a stack is empty and we're trying to move a king. Create a new block with king card
            if (toInsertOn.getType() == Type.Empty && stack.isStackEmpty()) {
                stack.getStack().add(new Block(toInsert));
                return;
            }
            //this stack contains card1
            if (stack.getStack().size() == 0)
                continue;
            if (stack.getStackLeader().getDocker().compareCards(toInsertOn)) {
                stack.getStackLeader().getBlock().addAll(toInsert.getBlock());
                return;
            }
        }
        if (suit.suitContains(toInsertOn)) {
            suit.getSuit(toInsertOn.getType().getValue()).addAll(toInsert.getBlock());
        }
    }

    /**
     * This method is used to remove a card by calling the different classes remove card functions
     *
     * @param card   The card to be removed
     * @param stacks The stack which the operation should be done on
     * @param talon  The talon which the operation should be done on
     * @param suit   The suit which the operation should be done on
     */
    public Block removeCard(Card card, BuildStackHolder stacks, Talon talon, Suit suit) {
        //checks if the card we want removed is in one of the build stacks if it is we remove it
        Block temp;
        temp = stacks.removeBlock(card);
        if (temp != null)
            return temp;
        //checks if the card we want removed is in the talon if it is we remove it
        temp = talon.removeCard(card);
        if (temp != null) {
            return temp;
        }
        //else we will attempt to remove the card from the suit in case none of the other options contained the card
        temp = suit.removeCard(card);
        return temp;
    }

    /**
     * This method is used to virtually perform the move found in the earlier iteration so the algorithm can
     * continue on as if the move had been done to check further than just the current move
     *
     * @param movesToPerform The list of moves which need to be performed
     * @param stacks         The stacks which the moves need to be performed on
     * @param talon          The talon which the moves need to be performed on
     * @param suit           The suit which the moves need to be performed on
     */
    public void performSimMove(Move movesToPerform, BuildStackHolder stacks, Talon talon, Suit suit) {
        while (movesToPerform.hasMoves()) {
            LinkedList<Card> cardsToMove = movesToPerform.getSimMoves();
            Card card1 = cardsToMove.poll();
            Card card2 = cardsToMove.poll();
            //Inserts and Removes the card from original location to it's new location
            insertCard(removeCard(card1, stacks, talon, suit), card2, stacks, suit);
        }
    }

    public void performPermanentMoves(Move movesToPerform) {
        LinkedList<Card> cardsToMove = movesToPerform.moveList;
        while (cardsToMove.size() > 0) {
            Card card1 = cardsToMove.poll();
            Card card2 = cardsToMove.poll();
            if (card1.compareCards(card2))
                continue;
            insertCard(removeCard(card1, buildStackHolder, talons, suits), card2, buildStackHolder, suits);
        }
    }

    public void insertEmpties() {
        for (BuildStack stacks : buildStackHolder.getStackList()) {
            Block stackLeader = stacks.getStackLeader();
            if (stackLeader == null)
                continue;
            Card card = stackLeader.getLeader();
            if (card.getType() == Type.Unturned) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("insert card from row : " + stacks.getIndex());
                card.setFaceUp(scanner.nextInt(), scanner.nextInt());
            }
        }
    }

    /**
     * The main method of the algorithm which works as an recursive function
     *
     * @param move   The move object used through the iterations to find all possible moves
     * @param holder The Build Stack list which contains all the build stacks
     * @param talon  The talon to be used through the different iterations of the algorithm
     * @param suit   The suit to be used through the different iterations of the algorithm
     **/
    public void checkForMoves(Move move, BuildStackHolder holder, Talon talon, Suit suit, int depthChecker) throws Exception {
        //checks if the move sent on was null if that's the cast we can go no longer in this part of the route
        if (move == null)
            return;
        //we simulate the performing of moves contained in the move chain list
        performSimMove(move, holder, talon, suit);
        ArrayList<BuildStack> stackArray = holder.getStackList();
        if (checkWin(holder)) {
            System.out.println("winnable");
            winnable = true;
//            move.point = 1000;
//            listOfMoves.add(move);
            absoluteMax = move;
            throw new Exception("win");
        }
        //checks the board for internal moves
        for (int i = 0; i < 7; i++) {
            //checking for moves from the stack that can lead to card being inserted into the suit
            ArrayList<Move> tempList = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                if (i == j || stackArray.get(j).getStack().size() == 0)
                    continue;
                Move temp = moveLogic.checkInternalStackMove(holder, stackArray.get(i), stackArray.get(j), move, talon);
                //Checking for possible moves internally between the stacks'
                if (temp.moveListSim.size() != 0)
                    tempList.add(temp);
            }
            Move tempMax = new Move();
            if (tempList.size() != 0) {
                for (Move tempMove : tempList) {
                    if (tempMax.point < tempMove.point)
                        tempMax = tempMove;
                }
                move = tempMax;
            }
            if (move.moveListSim.size() != 0)
                i = -1;
            performSimMove(move, holder, talon, suit);
        }
        //checks for unturned cards
        if (checkForUnturnedCards(stackArray, move))
            return;

        //checks for deck moves as the last thing maybe try 2 but, 3 finished it same way as 4 but just faster!
//        for (int j = depthChecker; j < 4; j++) {
        for (int j = depthChecker; j < 2; j++) {
            for (int i = 0; i < talon.getDeck().size(); i++) {
                checkForMoves(moveLogic.findTalonToStackMove(i, talon, stackArray, suit, move), holder.cloneHolder(), talon.cloneTalon(), suit.cloneSuit(), j + 1);
            }
            break;
        }
        //checks for unturned cards
        if (checkForUnturnedCards(stackArray, move))
            return;

//            System.out.println("Move combination alternative move" + moveLogic.findAlternativeStackMove(stackArray, move));
        checkForMoves(moveLogic.findAlternativeStackMove(stackArray, move), holder.cloneHolder(), talon.cloneTalon(), suit.cloneSuit(), 0);
        checkForMoves(moveLogic.checkStackToSuit(suit, holder, move), holder.cloneHolder(), talon.cloneTalon(), suit.cloneSuit(), depthChecker);
        checkForMoves(moveLogic.findTalonToSuitMove(talon, suit, move), holder.cloneHolder(), talon.cloneTalon(), suit.cloneSuit(), depthChecker);
//        System.out.println("Move Combination Found: " + move);
        checkNewMax(move);
//        listOfMoves.add(move);
    }

    private boolean checkForUnturnedCards(ArrayList<BuildStack> stackArray, Move move) {
        //checks for unturned cards
        Move temp = new Move();
        for (int i = 0; i < 7; i++) {
            temp = moveLogic.unTurnedCard(stackArray.get(i), temp);
        }
        //if the algorithm found unturned cards we instantly ask the algorithm to stop searching on
        if (temp.hasMoves()) {
            move.add(temp);
            checkNewMax(move);
//            System.out.println("Move Combination Found: " + move);
//            listOfMoves.add(move);
            return true;
        }
        return false;
    }

    private void checkNewMax(Move move) {
        if (move.moveList.size() == 0)
            return;
        if (absoluteMax.point <= move.point)
            if (absoluteMax.moveList.size() == 0)
                absoluteMax = move;
            else if (absoluteMax.moveList.size() > move.moveList.size())
                absoluteMax = move;
    }

    public boolean checkWin(BuildStackHolder holder) {
        int counter = 0;
        for (BuildStack stack : holder.getStackList()) {
            if (stack.isStackEmpty() || stack.getStack().get(0).getLeader().getType() != Type.Unturned) {
                counter++;
                continue;
            }
            if (stack.getStack().size() == 1 && stack.getStack().get(0).getBlock().size() == 1 && stack.getStack().get(0).getLeader().getType() == Type.Unturned) {
                counter++;
            }
        }
        return (counter == 7);
    }

    public void finishUp() {
        System.out.println("Finishing up the rest of the game");
        Move move = new Move();
        Move temp;
        while (!suits.suitFinished()) {
            temp = new Move();
            temp = moveLogic.checkStackToSuit(suits, buildStackHolder, move);

            if (temp != null)
                move = temp;

            if (temp == null)
                temp = moveLogic.findTalonToStackMove(0, talons, buildStackHolder.getStackList(), suits, move);
            if (temp == null)
                temp = moveLogic.findTalonToSuitMove(talons, suits, move);
            if (temp != null)
                move = temp;

            performSimMove(move, buildStackHolder, talons, suits);
            if (checkForUnturnedCards(buildStackHolder.getStackList(), move))
                return;
        }

        System.out.println(move.toString());
        System.out.println("done for real");
    }

    public int amountOfUnturnedCards() {

        int amountOfFaceDownCard = 0;

        for (int i = 0; i < buildStackHolder.getStackList().size(); i++) {
            if (buildStackHolder.getStackList().get(i).getStackLeader().getLeader().getType() == Type.Unturned)
                amountOfFaceDownCard += 1;
        }
        return amountOfFaceDownCard;
    }

    /**
     * This method simply setup all the stacks with their cards turned and unturned
     *
     * @param cards the List of cards going into the build stacks
     */
    public void setUpStacks(ArrayList<Card> cards) {
        for (int i = 0; i <= 6; i++) {
            ArrayList<Block> blocks = new ArrayList<>();
            for (int j = i; j > 0; j--) {
                LinkedList<Card> temp = new LinkedList<>();
                temp.add(new Card(false));
                blocks.add(new Block(temp));
            }
            LinkedList<Card> temp = new LinkedList<>();
            temp.add(cards.get(i));
            blocks.add(new Block(temp));
            board.add(new BuildStack(blocks));
            board.get(i).setIndex(i + 1);
            buildStackHolder = new BuildStackHolder(board);
        }
    }

    public void resetGame() {
        remainingCards = new ArrayList<>();
        suits = new Suit();
        counter = 0;
    }

    /* USED TO SIMULATE SOLITAIRE FOR TESTING PURPOSES */
    public void generateGame(boolean wantSetValues, int setRandomValue) {
        for (int i = 0; i <= 3; i++) {
            if (i == 0) {
                for (int j = 1; j < 14; j++) {
                    remainingCards.add(new Card(true, j,0));
                }
            }
            if (i == 1) {
                for (int j = 1; j < 14; j++) {
                    remainingCards.add(new Card(true, j,1));
                }
            }
            if (i == 2) {
                for (int j = 1; j < 14; j++) {
                    remainingCards.add(new Card(true, j,2));
                }
            }
            if (i == 3) {
                for (int j = 1; j < 14; j++) {
                    remainingCards.add(new Card(true, j, 3));
                }
            }
        }

        /* Shuffles deck with set value or at random */
        System.out.println("Before shuffle: " + remainingCards);
        if(wantSetValues){
            rn.setSeed(setRandomValue);
            Collections.shuffle(remainingCards, rn);
        } else {
            System.out.println(remainingCards);
            Collections.shuffle(remainingCards);
        }
        System.out.println("After shuffle: " + remainingCards);

        /* Setting up stacks and new Talon */
        ArrayList<Card> stacks = new ArrayList<>(remainingCards.subList(0, 7));
        remainingCards.removeAll(stacks);
        setUpStacks(stacks);

        ArrayList<Card> talonCards = new ArrayList<>(remainingCards.subList(0, 24));
        talons = new Talon(talonCards);
        remainingCards.removeAll(talonCards);

    }

    public void turnUnknownCard(){
        for (BuildStack stacks : buildStackHolder.getStackList()) {
            Block stackLeader = stacks.getStackLeader();
            if (stackLeader == null)
                continue;
            Card card = stackLeader.getLeader();
            if (card.getType() == Type.Unturned) {

                int temp = rn.nextInt(remainingCards.size());
                System.out.println("insert card from row : " + stacks.getIndex() + " : "+ remainingCards.get(temp));
                card.setFaceUp(remainingCards.get(temp));
                remainingCards.remove(temp);
//                System.out.println(remainingCards);
            }
        }
    }

    public void turnUnknownCard(ArrayList<Card> cards){
        int count = 0;
        for (BuildStack stacks : buildStackHolder.getStackList()) {
            Block stackLeader = stacks.getStackLeader();
            if (stackLeader == null)
                continue;
            Card card = stackLeader.getLeader();
            if (card.getType() == Type.Unturned) {
                card.setFaceUp(cards.get(count));
                count++;
            }
        }
    }

    public ArrayList<Card> listStringToCard(ArrayList<String> stringlist) {
        ArrayList<Card> cardlist = new ArrayList<>();
        for (String s : stringlist) {
            cardlist.add(new Card(true, Integer.parseInt(s.substring(0,s.length()-1)), Type.typeFromString(s.substring(s.length()-1))));
        }
        return cardlist;
    }

    public LinkedList<Card> getAbsoluteBestMoves() {
        return absoluteMax.getMoveList();
    }



}
