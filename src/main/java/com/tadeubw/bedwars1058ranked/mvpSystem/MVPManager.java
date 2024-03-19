package com.tadeubw.bedwars1058ranked.mvpSystem;

import com.tadeubw.bedwars1058ranked.elo.EloManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MVPManager{

    private final EloManager eloManager;
    private final Map<Player, Integer> mvpPoints;


    public MVPManager(EloManager eloManager) {
        this.eloManager = eloManager;
        this.mvpPoints = new HashMap<>();
    }

    public void addKillPoints(Player player) {
        mvpPoints.put(player, 0);
        mvpPoints.put(player,mvpPoints.get(player) + 2);
    }

    public void addFinalKillPoints(Player player) {
        mvpPoints.putIfAbsent(player, 0);
        mvpPoints.put(player, mvpPoints.get(player) + 4);
    }

    public void addBedBreakPoints(Player player) {
        mvpPoints.putIfAbsent(player, 0);
        mvpPoints.put(player, mvpPoints.get(player) + 5);
    }

    public void endGame() {
        Player mvp = determineMVP();
        if (mvp != null) {
            Bukkit.broadcastMessage("§7O MVP da partida foi: §d" + mvp.getName());
            int mvpCount = eloManager.getPlayerData().getInt(mvp.getUniqueId() + ".mvp", 0);
            eloManager.getPlayerData().set(mvp.getUniqueId() + ".mvp", mvpCount + 1);
            eloManager.savePlayerData();
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.LEVEL_UP, 1, 1);
            }
        }
        mvpPoints.clear();
    }

    private Player determineMVP() {
        Player mvp = null;
        int maxPoints = 0;
        for (Map.Entry<Player, Integer> entry : mvpPoints.entrySet()) {
            if (entry.getValue() > maxPoints) {
                maxPoints = entry.getValue();
                mvp = entry.getKey();
            }
        }
        return mvp;
    }

    public int getPlayerMVPCount(Player player) {
        return eloManager.getPlayerData().getInt(player.getUniqueId() + ".mvp", 0);
    }

}
