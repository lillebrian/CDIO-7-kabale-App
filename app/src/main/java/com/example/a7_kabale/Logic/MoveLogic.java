package com.example.a7_kabale.Logic;

import com.example.a7_kabale.Logic.Enums.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
//this class contain all the logic of how cards can be moved and the scoring of the moves
public class MoveLogic {
    LinkedList<Card> alreadyContained = new LinkedList<>();

    /**
     * This method checks if two cards have different colours
     *
     * @param card1 First card to compare
     * @param card2 second card to compare
     *              Returns true if they have different colours and False if they got the same colour
     */
    private boolean colourDiff(Card card1, Card card2) {
        return card1.getColour() != card2.getColour();
    }

    /**
     * This method checks if a card can be moved into it's suit
     */
    private boolean suitCheck(Card moveCard, Suit suit) {
        //gets the top card of the suit that contains that cards type
        Card toCard = suit.getTop(moveCard.getType().getValue());
        //returns True if the card that we want to add to the suit is one lower than the card already in
        return (moveCard.getFaceValue() - toCard.getFaceValue() == 1);
    }

    /**
     * The stackCheck method checks if a card is stackable with another card which would mean they only have a value
     * difference of 1
     *
     * @param moveCard The card we want to move
     * @param toCard   The card which we want to move onto
     */
    private boolean stackCheck(Card moveCard, Card toCard) {
        return (toCard.getFaceValue() - moveCard.getFaceValue() == 1);
    }

    /**
     * This method checks if moving a card to a certain other card is a legal move
     *
     * @param moveCard The card we want to move
     * @param toCard   The card we want to move the other card onto
     */
    private boolean checkLegalMove(Card moveCard, Card toCard) {
        //checks that neither of the cards is of type unturned which for the algorithm means it's a unknown card
        if (moveCard.getType() == Type.Unturned || toCard.getType() == Type.Unturned)
            return false;
        //returns the result of stackCheck and colourDiff
        return stackCheck(moveCard, toCard) && colourDiff(moveCard, toCard);
    }

    /* TODO */
    private boolean checkLegalMoveKing(Card kingCard, BuildStack toStack, BuildStack kingStack) {
        return (toStack.isStackEmpty() && kingCard.getFaceValue() == 13 && (kingStack == null || kingStack.getStack().size() > 1));
    }

    /*CHANGE*/
    /* Should prioritize stacks queens over talon */
    private Card searchForMatchingQueen(Card kingCard, ArrayList<BuildStack> allStacks, Talon talon) {
        for (BuildStack stack : allStacks) {
            if (stack.getStack().size() == 0)
                continue;

            Card stackLeader = stack.getStackLeader().getLeader();
            if (stackLeader.getFaceValue() == 12 && colourDiff(kingCard, stackLeader)) {
//                return stackLeader;
                return stackLeader;
            }
        }
        for (Card card : talon.deck) {
            if (card.getFaceValue() == 12 && colourDiff(kingCard, card)) {
//                return stackLeader;
                return card;
            }
        }

        return null;
    }

    /*TODO*/
    public Move checkKingMove(Card king, ArrayList<BuildStack> allStacks, Move move, boolean queenLess, Talon talon) {
        Card queen = searchForMatchingQueen(king, allStacks, talon);
        if (!queenLess)
            return move.addMove(1, king, new Card(Type.Empty));
        if (queen != null) {
            /* Add move that kings needs to move to empty block */
            return move.addMove(1, king, new Card(Type.Empty)).addMove(0, queen, king);
        }
        return move;
    }

    /**
     * This method checks if a stack contains an unturned card
     *
     * @param stack The stack that we searches through
     * @param move  The Move object we want the possible turn move inserted to
     */
    public Move unTurnedCard(BuildStack stack, Move move) {
        //checks if the front card aka leader of a stacks leader block is a unturned card
        Block temp = stack.getStackLeader();
        if (temp == null)
            return move;
        if (temp.getLeader().getType() == Type.Unturned) {
//                System.out.println("Unturned card found at: " + stack.getIndex() + " : " + stack.getStackLeader().block.indexOf(stack.getStackLeader().getLeader()));
            return move.addMove(0, stack.getStackLeader().getLeader(), stack.getStackLeader().getLeader());
        }
        return move;
    }

    /**
     * This method searches for a possible card which could be added to the suit
     */
    public Move checkStackToSuit(Suit suit, BuildStackHolder stack, Move move) {
        /*CHANGE*/
        for (int i = 0; i < stack.getStackList().size(); i++) {
            if (stack.getStackList().get(i).isStackEmpty())
                continue;

            //gets the stack leaders, docker aka front card
            Card frontCard = stack.getStackList().get(i).getStackLeader().getDocker();
            if (suitCheck(frontCard, suit)) {
                int type = frontCard.getType().getValue();
//            System.out.println("Legal Move Found: " + frontCard + " to Suit");
                if (stack.getStackList().get(i).getStackLeader().getBlock().size() == 1)
                    return move.addMove(stack.getStackList().get(i).getStack().size(), frontCard, suit.getTop(type));
                else
                    return move.addMove(0, frontCard, suit.getTop(type));
            }
        }
        return null;
    }

    /**
     * This method searches for internal moves of cards between build stacks on the board
     */
    public Move checkInternalStackMove(BuildStackHolder holder, BuildStack stack1, BuildStack stack2, Move move, Talon talon) {
        Block block1 = stack1.getStackLeader();
        Block block2 = stack2.getStackLeader();
        Card block1Docker;
        Card block2Leader;

        /* CHANGE */
        if (stack1.getStack().size() == 0) {
            block2Leader = block2.getLeader();
            if (checkLegalMoveKing(block2Leader, stack1, stack2)) {
                if (block2Leader.compareCards(block2.getDocker()))
                    return checkKingMove(block2Leader, holder.getStackList(), move, true, talon);
                else
                    return checkKingMove(block2Leader, holder.getStackList(), move, false, talon);
            } else
                return move;
        }

        block1Docker = block1.getDocker();
        block2Leader = block2.getLeader();

        if (checkLegalMove(block2Leader, block1Docker) && !(move.alreadyMoved(block2Leader))) {
            //legal move was found between block2leader and block1docker which means block2 can be added too block1
            int size = stack2.getStack().size();
//            System.out.println("Legal Move Found: " + block2Leader + block1Docker);
            //here we check if the block is the last one in the stack
            if (size != 1) {
                return move.addMove(1 + (stack2.getStack().size() - 1), block2Leader, block1Docker);
            } else {
                return move.addMove(1, block2Leader, block1Docker);
            }
        }
        return move;
    }

    /**
     * This method looks to find a move which can be made from the talon to either a suit or a build stack
     */
    public Move findTalonToStackMove(int i, Talon talon, ArrayList<BuildStack> stacks, Suit suit, Move move) {
//        Card deckCard = talon.getDeck().get(index);
        //checks through each build stack if the card can be added to that stacks docker
        if(talon.getDeck().size() == 0)
            return null;

        Card deckCard = talon.getDeck().get(i);
        for (BuildStack stack : stacks) {
            Block stackLeader = stack.getStackLeader();
            if (stackLeader == null) {
                /* CHANGE */
                if (checkLegalMoveKing(deckCard, stack, null)) {
                    Move temporarily = checkKingMove(deckCard, stacks, move, false, talon);
                    ;
                    if (temporarily.hasMoves())
                        return temporarily;
                } else
                    continue;
            }
            Card stackDocker = stackLeader.getDocker();
            //checks if the two card is a legal move
            if (stackDocker != null && checkLegalMove(deckCard, stackDocker)) {
                //card can be moved to the stack
//                System.out.println("Legal Move Found: From Talon " + deckCard + " To " + stackDocker);
                return move.addMove(0, deckCard, stackDocker);
            }
        }

        return null;
    }

    public Move findTalonToSuitMove(Talon talon, Suit suit, Move move) {
        if (talon.getDeck().size() == 0)
            return null;

        for (Card deckCard : talon.getDeck()) {
            //checks if the deck card can be added to the suit
            if (suitCheck(deckCard, suit)) {
                int typeInt = deckCard.getType().getValue();
                Card temp = suit.getTop(typeInt);
                //Card can be moved into goal
//            System.out.println("Legal Move Found: From Talon " + deckCard + " to Suit");
                return move.addMove(0, deckCard, temp);
            }
        }
        return null;
    }

    public Move findAlternativeStackMove(ArrayList<BuildStack> stacks, Move move) {
        for (int i = 0; i < 7; i++) {
            Block stackLeader = stacks.get(i).getStackLeader();
            if (stackLeader == null)
                continue;
            LinkedList<Card> blockOfCards = stackLeader.getBlock();
            for (int j = i + 1; j < 7; j++) {
                if (stacks.get(j).getStackLeader() == null)
                    continue;
                Card dockerCard = stacks.get(j).getStackLeader().getDocker();
                for (int k = 0; k < blockOfCards.size(); k++) {
                    Card compareWithCard = blockOfCards.get(k);
                    if (checkLegalMove(compareWithCard, dockerCard)) {
                        if (!compareWithCard.compareAlreadyContained(alreadyContained, dockerCard)) {
                            alreadyContained.addAll(compareWithCard.addCardsToContain(alreadyContained, dockerCard));
//                            System.out.println("Alternative Move found!");
                            return move.addMove(0, compareWithCard, dockerCard);
                        }
                    }
                }
            }
        }
        return null;
    }
}
