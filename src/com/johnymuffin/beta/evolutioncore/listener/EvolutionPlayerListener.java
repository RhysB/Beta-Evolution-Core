package com.johnymuffin.beta.evolutioncore.listener;

import com.johnymuffin.beta.evolutioncore.AuthReturnType;
import com.johnymuffin.beta.evolutioncore.EvolutionCache;
import com.johnymuffin.beta.evolutioncore.EvolutionCore;
import com.johnymuffin.beta.evolutioncore.libs.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.johnymuffin.beta.evolutioncore.JsonReader.readJsonFromUrl;
import static com.johnymuffin.beta.evolutioncore.event.CallEvolutionAuthEvent.callAuthenticationEvent;

public class EvolutionPlayerListener extends PlayerListener {
    private EvolutionCore plugin;

    public EvolutionPlayerListener(EvolutionCore plugin) {
        this.plugin = plugin;
    }


    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        //Cache Start
        if(EvolutionCache.getInstance().isPlayerCached(event.getPlayer().getName(), ip)) {
            //Player is known in the cache with ip
            plugin.logInfo("Received Authentication Status From Cache for: " + event.getPlayer().getName() + " - User is verified");
            callAuthenticationEvent(event.getPlayer(), true, AuthReturnType.successful);
            return;

        }

        //Cache End
        plugin.logInfo("Fetching Authentication Status For: " + event.getPlayer().getName());
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://auth.johnymuffin.com/serverAuth.php?method=1&username=" + URLEncoder.encode(event.getPlayer().getName(), "UTF-8") + "&userip=" + URLEncoder.encode(ip, "UTF-8");
                    final JSONObject obj = readJsonFromUrl(url);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (obj.has("result") && obj.has("verified")) {
                                if (obj.getBoolean("result")) {
                                    //Auth response
                                    if(obj.getBoolean("verified")) {
                                        //User is verified
                                        plugin.logInfo("Received Authentication Status for: " + event.getPlayer().getName() + " - User is verified");
                                        callAuthenticationEvent(event.getPlayer(), true, AuthReturnType.successful);
                                        //Add player to cache
                                        EvolutionCache.getInstance().addPlayerAuthentication(event.getPlayer().getName(), ip);
                                    } else {
                                        //user isn't verified
                                        plugin.logInfo("Received Authentication Status for: " + event.getPlayer().getName() + " - User isn't verified");
                                        callAuthenticationEvent(event.getPlayer(), false, AuthReturnType.successful);
                                    }

                                } else {
                                    //Auth returned unavailable
                                    plugin.logInfo("Auth returned unavailable");
                                    callAuthenticationEvent(event.getPlayer(), false, AuthReturnType.apierror);
                                }
                            } else {
                                //Invalid Json Response
                                plugin.logInfo("Received a invalid JSON response");
                                callAuthenticationEvent(event.getPlayer(), false, AuthReturnType.apierror);
                            }
                        }
                    }, 0L);

                } catch (UnsupportedEncodingException e) {
                    callAuthenticationEvent(event.getPlayer(), false, AuthReturnType.unsuccessful);
                    e.printStackTrace();
                } catch (IOException e) {
                    callAuthenticationEvent(event.getPlayer(), false, AuthReturnType.unsuccessful);
                    e.printStackTrace();
                }


            }
        }, 0L);


    }

}
