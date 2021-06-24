package com.example.a7_kabale.Logic;

import java.util.ArrayList;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public class BuildStackHolder {
    private ArrayList<BuildStack> stacks;

    public BuildStackHolder() {
        stacks = new ArrayList<>();
    }

    public BuildStackHolder(ArrayList<BuildStack> arrayList) {
        stacks = arrayList;
    }

    public BuildStackHolder cloneHolder() {
        BuildStackHolder temp = new BuildStackHolder();
        for (int i = 0; i < this.stacks.size(); i++) {
            temp.stacks.add(this.stacks.get(i).cloneBuildstack());
        }
        return temp;
    }

    public Block removeBlock(Card card){
        for(BuildStack stack : stacks) {
           Block temp = stack.removeCard(card);
           if (temp != null) {
               return temp;
           }
        }
        return null;
    }

    public ArrayList<BuildStack> getStackList() {
        return stacks;
    }
}
