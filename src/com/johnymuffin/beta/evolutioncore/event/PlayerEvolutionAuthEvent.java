package com.johnymuffin.beta.evolutioncore.event;

import com.johnymuffin.beta.evolutioncore.AuthReturnType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PlayerEvolutionAuthEvent extends Event implements Cancellable {
    private boolean isCancelled = false;
    private boolean playerAuth;
    private Player player;
    private AuthReturnType art;


    public PlayerEvolutionAuthEvent(Player p, Boolean status, AuthReturnType art) {
        super("EvolutionAuthEvent");
        this.playerAuth = status;
        this.player = p;
        this.art = art;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
            isCancelled = b;
    }

    public Player getPlayer() {
        return player;
    }

    public AuthReturnType getAuthReturnType() {
        return art;
    }

    public boolean isPlayerAuthenticated() {
        return playerAuth;
    }
}


