package com.johnymuffin.beta.evolutioncore.event;

import com.johnymuffin.beta.evolutioncore.AuthReturnType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CallEvolutionAuthEvent {

    public static void callAuthenticationEvent(Player p, Boolean authStatus, AuthReturnType art) {
        final PlayerEvolutionAuthEvent event = new PlayerEvolutionAuthEvent(p, authStatus, art);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }


}
