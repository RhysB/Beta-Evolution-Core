package com.johnymuffin.beta.evolutioncore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class EvolutionCache {
    private static EvolutionCache singleton;
    private EvolutionCore plugin;
    private int cacheNumber;

    private HashMap<String, HashMap<String, String>> cacheMap = new HashMap<String, HashMap<String, String>>();

    public EvolutionCache(EvolutionCore plugin) {
        cacheNumber = 0;
        this.plugin = plugin;
    }

    public boolean isPlayerCached(String username, String ip) {
        for (String key : cacheMap.keySet()) {
            if (cacheMap.get(key).get("username").equals(username)) {
                //Same User
                if (cacheMap.get(key).get("ip").equals(ip)) {
                    //Same ip
                    return true;
                }
            }
        }


        return false;
    }


    public Integer deleteOldCache() {
        int deletedCaches = 0;
        long unixTime = System.currentTimeMillis() / 1000L;

        Iterator<String> iterator = cacheMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if(unixTime < Long.valueOf(cacheMap.get(key).get("timeout"))) {
                if(isPlayerOnline(cacheMap.get(key).get("username"))) {
                    deletedCaches = deletedCaches + 1;
                    iterator.remove();
                }
            }

        }
        return deletedCaches;

    }

    //Supporting function
    private boolean isPlayerOnline(String username) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }


    public void addPlayerAuthentication(String username, String ip) {
        cacheNumber = cacheNumber + 1;
        final HashMap<String, String> tmp = new HashMap<String, String>();
        tmp.put("username", username);
        tmp.put("ip", ip);
        long unixTime = System.currentTimeMillis() / 1000L;
        unixTime = unixTime + 60 * 60 * 12;
        tmp.put("timeout", String.valueOf(unixTime));

        cacheMap.put(String.valueOf(cacheNumber), tmp);

    }


    public void setPlugin(EvolutionCore plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                int deleted = deleteOldCache();
                plugin.logInfo("Cleared " + deleted + " expired players from the Authentication cache");
            }
        }, 0L, 20 * 60 * 60);
    }


    public static EvolutionCache getInstance(EvolutionCore plugin) {
        if (EvolutionCache.singleton == null) {
            EvolutionCache.singleton = new EvolutionCache(plugin);
        }
        return EvolutionCache.singleton;
    }

    public static EvolutionCache getInstance() {
        if (EvolutionCache.singleton != null) {
            return EvolutionCache.singleton;
        }
        return null;
    }

}
