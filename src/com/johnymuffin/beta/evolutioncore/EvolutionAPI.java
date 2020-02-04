package com.johnymuffin.beta.evolutioncore;

public class EvolutionAPI {

    public static boolean isUserAuthenticatedInCache(String Username, String ip) {
        if(EvolutionCache.getInstance().isPlayerCached(Username, ip)) {
                return true;
        }

        return false;

    }


}
