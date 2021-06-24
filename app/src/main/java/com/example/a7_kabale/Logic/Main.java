package com.example.a7_kabale.Logic;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public class Main {

    public static void main(String[] args) {
//        Logic logic = new Logic();
////        logic.setUp();
////        logic.run();
//        testAlgorithm(logic, 10000, true);
    }

    public static void testAlgorithm(Logic logic, int runs, boolean setValues) {
        ArrayList<Integer> setValueWins = new ArrayList<>();
        long totalTime = 0;
        for (int i = 0; i <= runs; i++) {
//            logic.generateGame(setValues, i);
            System.out.println("\nTEST NUMBER : "+i+"\n");
            logic = new Logic();
            logic.generateGame(setValues,i);
            long time1 = System.currentTimeMillis();
            logic.run();
            if(logic.winnable) {
                setValueWins.add(i);
                System.out.println("has won " + setValueWins.size() + " out of " + i);
                long time2 = System.currentTimeMillis();
                totalTime += time2-time1;
            }

            //System.out.println(totalTime);
        }
        System.out.println("The algorithm solved the games with the set values of: " + setValueWins + "which means that " + setValueWins.size() + " games got solved out of " + runs + " and it took an average time of " + (totalTime/setValueWins.size())/1000 + " seconds to solve them");
    }
}
