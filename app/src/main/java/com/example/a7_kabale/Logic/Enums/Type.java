package com.example.a7_kabale.Logic.Enums;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public enum Type {
    Clover(0, "♣️","c"),
    Spade(2, "♠️","s"),
    Heart(1, "♥️","h"),
    Diamond(3, "♦️","d"),
    Unturned(-1,"?",""),
    Empty(-2,"⚠️","");

    private final int value;
    private final String string;
    private final String typeChar;

    Type(final int newValue, final String icon, final String typeChar) {
        value = newValue;
        string = icon;
        this.typeChar = typeChar;
    }

    public static Type typeFromString (String s) {
        switch (s) {
            case "c":
                return Clover;
            case "h":
                return Heart;
            case "s":
                return Spade;
            case "d":
                return Diamond;
            default:
                return Unturned;
        }
    }

    //method uses to get an type from an integer
    public static Type fromInteger(int x) {
        switch(x) {
            case 0:
                return Clover;
            case 1:
                return Heart;
            case 2:
                return Spade;
            case 3:
                return Diamond;
            default:
                return Unturned;
        }
    }

    //methods to get either the int value or the string value of an Type enum
    public int getValue() { return value; }
    public String getString() { return string; }
}
