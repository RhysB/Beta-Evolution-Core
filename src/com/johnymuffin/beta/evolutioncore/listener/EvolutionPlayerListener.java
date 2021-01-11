package com.johnymuffin.beta.evolutioncore.listener;

import com.johnymuffin.beta.evolutioncore.AuthReturnType;
import com.johnymuffin.beta.evolutioncore.BetaEvolutionsUtils;
import com.johnymuffin.beta.evolutioncore.EvolutionCache;
import com.johnymuffin.beta.evolutioncore.EvolutionCore;
import com.projectposeidon.johnymuffin.ConnectionPause;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;

import static com.johnymuffin.beta.evolutioncore.event.CallEvolutionAuthEvent.callAuthenticationEvent;

public class EvolutionPlayerListener extends PlayerListener {
    private EvolutionCore plugin;

    public EvolutionPlayerListener(EvolutionCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        if (!plugin.isPoseidonPresent()) {
            return;
        }


        final String username = event.getName();
        final String ipAddress = event.getAddress().getHostAddress();
        //Check if user is cached, if they are, skip the lookup
        if (EvolutionCache.getInstance().isPlayerCached(username, ipAddress)) {
            return;
        }

        //Add Connection Pause
        ConnectionPause connectionPause = event.addConnectionPause(plugin, "BetaEvolutions");
        //Check Entries Async
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(false);
            final BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.verifyUser(username, ipAddress);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                plugin.logInfo(username + " has authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " nodes.");
                if (verificationResults.getSuccessful() > 0) {
                    EvolutionCache.getInstance().addPlayerAuthentication(username, ipAddress);
                }
                event.removeConnectionPause(connectionPause);
            });


        });
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        final String playerName = event.getPlayer().getName();
        final String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        //Cache Start
        if (EvolutionCache.getInstance().isPlayerCached(playerName, ip)) {
            //Player is known in the cache with ip
            plugin.logInfo("Received Authentication Status From Cache for: " + playerName + " - User is verified");
            callAuthenticationEvent(event.getPlayer(), true, AuthReturnType.successful);
            return;

        } else {
            //If Poseidon is present it would have run a check when the player first joined. They obviously are not authenticated now
            if (plugin.isPoseidonPresent()) {
                callAuthenticationEvent(event.getPlayer(), false, AuthReturnType.successful);
                return;
            }
        }

        //Cache End
        plugin.logInfo("Fetching Authentication Status For: " + event.getPlayer().getName());
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(false);
            final BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.verifyUser(event.getPlayer().getName(), ip);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                plugin.logInfo(playerName + " has authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " nodes.");
                if (verificationResults.getSuccessful() > 0) {
                    EvolutionCache.getInstance().addPlayerAuthentication(playerName, ip);
                    callAuthenticationEvent(event.getPlayer(), true, AuthReturnType.successful);
                } else {
                    callAuthenticationEvent(event.getPlayer(), false, AuthReturnType.successful);
                }
            }, 0L);
        }, 0L);


    }

}
