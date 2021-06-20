package com.example.a7_kabale.Items;

public class MoveItem {
    private float point;
    private String move;

    public MoveItem(float point, String move) {
        this.point = point;
        this.move = move;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
