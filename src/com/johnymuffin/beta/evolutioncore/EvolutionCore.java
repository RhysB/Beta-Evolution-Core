package com.johnymuffin.beta.evolutioncore;

import com.johnymuffin.beta.evolutioncore.listener.EvolutionPlayerListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public class EvolutionCore extends JavaPlugin {
    private EvolutionCore plugin;
    private Logger log;
    private PluginDescriptionFile pdf;
    //Poseidon
    private boolean poseidonPresent = false;
    //Authentication Server List

    @Override
    public void onEnable() {
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        logInfo("Enabling Plugin");
        plugin = this;

        if (testClassExistence("com.projectposeidon.api.PoseidonUUID")) {
            poseidonPresent = true;
            logInfo("Project Poseidon support enabled.");
        } else {
            logInfo("Project Poseidon support disabled.");
        }

        EvolutionCache.getInstance(plugin).setPlugin(plugin);

        final EvolutionPlayerListener EPL = new EvolutionPlayerListener(plugin);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PRELOGIN, EPL, Event.Priority.High, plugin);
        getServer().getPluginManager().registerEvent(org.bukkit.event.Event.Type.PLAYER_JOIN, EPL, Event.Priority.Highest, plugin);

        logInfo("Plugin Enabled");
    }


    private boolean testClassExistence(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onDisable() {

        logInfo("Disabling");
    }

    public void logInfo(String s) {
        if (log != null) {
            log.info("[" + pdf.getName() + "] " + s);
        }
    }

    public boolean isPoseidonPresent() {
        return poseidonPresent;
    }

}
