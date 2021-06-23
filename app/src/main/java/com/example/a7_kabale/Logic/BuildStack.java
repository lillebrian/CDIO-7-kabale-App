package com.example.a7_kabale.Logic;

import java.util.ArrayList;
import java.util.LinkedList;

/**The Buildstack acts as an container for our blocks, which provides methods to access the blocks */
public class BuildStack {
    private final ArrayList<Block> stack;
    private int index;

    public BuildStack() {
        stack = new ArrayList<>();
    }
    public BuildStack(ArrayList<Block> stack) {
        this.stack = stack;
    }

    /**This method is used to clone the buildstack in order to get a new object of another object for the recursion */
    public BuildStack cloneBuildstack() {
        BuildStack clone = new BuildStack();
        clone.setIndex(this.getIndex());
        for (int i = 0; i < this.getStack().size(); i++) {
            clone.getStack().add(this.stack.get(i).cloneBlock());
        }
        return clone;
    }

    public ArrayList<Block> getStack() {
        return stack;
    }

    /** the stack leader is basically the block that is in front and moveable this method returns the block who is
     * in front in the stack*/
    public Block getStackLeader() {
        /*CHANGE from "return null" to "*/
        if(isStackEmpty()) {
            return null;
        }
        else
            return stack.get(stack.size()-1);
    }

    /*CHANGE*/
    public boolean isStackEmpty() {
        return (stack.size() == 0);
    }


    //Returns the index of the stack
    public int getIndex() {
        return index;
    }

    /** Sets the value of index
     * @param  index The value you want index to be set to*/
    public void setIndex(int index) {
        this.index = index;
    }

    /** This method is used to remove a card from a stack
     * @param card is the card you want to remove from the stack*/
    public Block removeCard(Card card) {
        //this stack contains card1
        Block stackLeader = this.getStackLeader();
        if (stackLeader == null)
            return null;
        int index = stackLeader.blockContainsIndex(card);
        if (index != -1) {
            //if the card isn't in leader position or docker position
            //then we need to move multiple cards.
            Block tempBlock = this.getStackLeader().removeBlock(index);
            if (index == 0)
                this.getStack().remove(this.getStackLeader());
            return tempBlock;
        }
        else
            return null;
    }

    public LinkedList<Card> getAllCardsFromLeadStack(BuildStack stack) {
        return stack.getStackLeader().getBlock();
    }


}
