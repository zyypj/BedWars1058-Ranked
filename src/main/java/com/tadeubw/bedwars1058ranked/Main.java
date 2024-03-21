package com.tadeubw.bedwars1058ranked;

import com.tadeubw.bedwars1058ranked.elo.EloManager;
import com.tadeubw.bedwars1058ranked.elo.EloPlaceholder;
import com.tadeubw.bedwars1058ranked.elo.command.EloCommand;
import com.tadeubw.bedwars1058ranked.misc.RankCommand;
import com.tadeubw.bedwars1058ranked.misc.UUIDCommand;
import com.tadeubw.bedwars1058ranked.mvpSystem.MVPManager;
import com.tadeubw.bedwars1058ranked.queue.command.JoinQueueCommand;
import com.tadeubw.bedwars1058ranked.queue.command.LeaveQueueCommand;
import com.tadeubw.bedwars1058ranked.queue.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    private EloManager eloManager;
    private QueueManager queueManager;
    private MVPManager mvpManager;

    @Override
    public void onEnable() {
        Plugin bedWarsPlugin = Bukkit.getPluginManager().getPlugin("BedWars1058");
        if (bedWarsPlugin == null || !bedWarsPlugin.isEnabled()) {
            getLogger().severe("-----------------------");
            getLogger().severe("");
            getLogger().severe("BedWars1058 Ranked");
            getLogger().severe("");
            getLogger().severe("BedWars1058 não encontrado");
            getLogger().severe("-----------------------");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File dataFolder = new File(bedWarsPlugin.getDataFolder(), "Addons/Ranked");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File playerDataFile = new File(dataFolder, "playersElo.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Erro ao criar o arquivo playersElo.yml: " + e.getMessage());
            }
        }
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        eloManager = new EloManager(this, mvpManager, queueManager, playerData);
        queueManager = new QueueManager(this, eloManager);
        mvpManager = new MVPManager(eloManager);
        MVPManager mvpManager = new MVPManager(eloManager);
        EloManager eloManager = new EloManager(this, mvpManager, queueManager, playerData);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new EloPlaceholder(eloManager, queueManager, mvpManager).register();
        }
        getServer().getPluginManager().registerEvents(eloManager, this);
        getServer().getPluginManager().registerEvents(queueManager, this);
        Bukkit.getScheduler().runTaskTimer(this, eloManager::savePlayerData, 0, 20 * 60 * 5); // Salva os dados a cada 5 minutos
        getCommand("elo").setExecutor(new EloCommand(eloManager));
        getCommand("uuid").setExecutor(new UUIDCommand());
        getCommand("joinqueue").setExecutor(new JoinQueueCommand(queueManager));
        getCommand("leavequeue").setExecutor(new LeaveQueueCommand(queueManager));
        getCommand("rank").setExecutor(new RankCommand(eloManager));
        getLogger().info("-----------------------");
        getLogger().info("");
        getLogger().info("BedWars1058 Ranked");
        getLogger().info("by tadeu");
        getLogger().info(getDescription().getVersion());
        getLogger().info("");
        getLogger().info("Nenhum erro encontrado :)");
        getLogger().info("Você está atualizado!");
        getLogger().info("-----------------------");
    }

    @Override
    public void onDisable() {
        eloManager.savePlayerData();
        HandlerList.unregisterAll(this);
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }
}