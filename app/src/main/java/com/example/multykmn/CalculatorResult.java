package com.example.multykmn;

public class CalculatorResult {
    private static final CalculatorResult ourInstance = new CalculatorResult();

    public static CalculatorResult getInstance() {
        return ourInstance;
    }

    private CalculatorResult() {
    }

    public int getResult (int userId, int androidId, int moves){
        int halfQuantity = (moves- 1) / 2, size = moves;
        if(userId == androidId) return 0;
        if(userId > halfQuantity) {
            return doUserMoreHalf(userId, androidId, halfQuantity);
        } else if(userId < halfQuantity){
            return doUserLessHalf(userId, androidId, halfQuantity, size);
        } else {
            if(userId > androidId) return 2;
            else return 1;
        }
    }

    private int doUserMoreHalf(int userId, int androidId, int halfQuantity){
        int moving =  userId - halfQuantity;
        userId -= moving;
        androidId -= moving;
        if(userId < androidId) return 1;
        else {
            if(androidId >= 0) return 2;
            else return 1;
        }
    }

    private int doUserLessHalf(int userId, int androidId, int halfQuantity, int size){
        int moving = halfQuantity - userId;
        userId += moving;
        androidId += moving;
        if(userId > androidId) return 2;
        else {
            if(androidId >= size) return 2;
            else return 1;
        }
    }
}
