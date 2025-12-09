package com.campominado;

import java.util.HashMap;
import java.util.Map;

public class BestTimeManager {

    public enum Dificuldade {
        FACIL, MEDIO, DIFICIL
    }

    private final Map<Dificuldade, Integer> melhoresTempos = new HashMap<>();

    public int getBestTime(Dificuldade dif) {
        return melhoresTempos.getOrDefault(dif, -1);
    }

    public void updateBestTime(Dificuldade dif, int tempo) {
        int atual = getBestTime(dif);
        if (atual == -1 || tempo < atual) {
            melhoresTempos.put(dif, tempo);
        }
    }
}
