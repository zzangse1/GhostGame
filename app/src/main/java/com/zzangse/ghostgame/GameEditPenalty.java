package com.zzangse.ghostgame;

import androidx.annotation.NonNull;

public class GameEditPenalty {
    String penalty;

    public GameEditPenalty(String Penalty) {
        this.penalty = Penalty;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    @Override
    public String toString() {
        return penalty;
    }
}
