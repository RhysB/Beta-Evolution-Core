package com.johnymuffin.beta.evolutioncore;

import com.johnymuffin.beta.evolutioncore.listener.EvolutionPlayerListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.logging.Logger;

public class EvolutionCore extends JavaPlugin {
    private EvolutionCore plugin;
    private Logger log;
    private PluginDescriptionFile pdf;


    @Override
    public void onEnable() {
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        logInfo("Enabling Plugin");
        plugin = this;
        EvolutionCache.getInstance(plugin).setPlugin(plugin);

        final EvolutionPlayerListener EPL = new EvolutionPlayerListener(plugin);
        getServer().getPluginManager().registerEvent(org.bukkit.event.Event.Type.PLAYER_JOIN, EPL, Event.Priority.Highest, plugin);


        logInfo("Plugin Enabled");
    }

    @Override
    public void onDisable() {

        logInfo("Disabling");
    }

    public void logInfo(String s) {
        if(log != null) {
            log.info("[" + pdf.getName() + "] " + s);
        }
    }
}
