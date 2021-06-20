package com.example.a7_kabale.Logic;

import java.util.LinkedList;

//The block class act's as an container of cards that are linked up could be H5 S6, D7, C8 etc.
public class Block {
    private final LinkedList<Card> block;
    private LinkedList<Card> subBlock;

    public Block(LinkedList<Card> block) {
        this.block = block;
    }

    public Block() {
        LinkedList<Card> temp = new LinkedList<>();
        this.block = temp;
    }

    public Block(Block block) {
        this.block = new LinkedList<>(block.block);
    }

    public Block (Card card) {
        LinkedList<Card> temp = new LinkedList<>();
        temp.add(card);
        this.block = temp;
    }

    /**cloning of the block object for recursion purposes*/
    /**
     * cloning of the block object for recursion purposes
     */
    public Block cloneBlock() {
        Block temp = new Block();
        for (int i = 0; i < this.getBlock().size(); i++) {
            temp.block.add(this.getBlock().get(i).cloneCard());
        }
        return temp;
    }

    /**
     * Method to check if a block contains a certain card
     *
     * @param card The card to look for
     *             is used by writing Block.blockContains(Card card)
     */
    public int blockContainsIndex(Card card) {
        for (int i = 0; i < block.size(); i++) {
            Card comparer = block.get(i);
            if (comparer.compareCards(card))
                return i;
        }
        return -1;
    }

    public boolean blockContains(Card card){
        for (int i = 0; i < block.size(); i++) {
            Card comparer = block.get(i);
            if (comparer.compareCards(card))
                return true;
        }
        return false;
    }

    public LinkedList<Card> getBlock() {
        return block;
    }

    /**
     * This method returns the leader of a block, the leader of a block is the highest facevalue card that can be moved
     * so if a block contains S7 and H8, H8 would be the leader of the block
     */
    public Card getLeader() {
        return block.peekFirst();
    }

    /**
     * This method returns the docker of a block, the docker of a block is the lowest facevalue card that can be put
     * another card on
     * so if a block contains S7 and H8, S/ would be the docker of the block
     */
    public Card getDocker() {
        return block.peekLast();
    }

    public LinkedList<Card> splitedBlock(Block block, Card card) {
        for (int i = 0; i < block.getBlock().size(); i++) {
            if (block.getBlock().get(i).compareCards(card)){
                subBlock.add(card);
                for (int j = i ; j < block.getBlock().size(); j++) {
                    subBlock.add(card);
                }
            }
        }
        return subBlock;
    }

    public Block removeBlock(int index) {
        Block list = this;
        Block newBlock;
        LinkedList<Card> listOfCards = list.getBlock();
        newBlock = new Block(new LinkedList<>(listOfCards.subList(index, listOfCards.size())));
        for (int i = listOfCards.size()-1; i >= index; i--) {
            listOfCards.remove(i);
        }
        return newBlock;
    }


}
