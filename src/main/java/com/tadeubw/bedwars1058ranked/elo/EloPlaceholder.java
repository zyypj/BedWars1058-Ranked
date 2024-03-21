package com.tadeubw.bedwars1058ranked.elo;

import com.tadeubw.bedwars1058ranked.mvpSystem.MVPManager;
import com.tadeubw.bedwars1058ranked.queue.QueueManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EloPlaceholder extends PlaceholderExpansion {

    private final EloManager eloManager;
    private final QueueManager queueManager;
    private final MVPManager mvpManager;

    public EloPlaceholder(EloManager eloManager, QueueManager queueManager, MVPManager mvpManager) {
        this.queueManager = queueManager;
        this.eloManager = eloManager;
        this.mvpManager = mvpManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bw1058ranked";
    }

    @Override
    public @NotNull String getAuthor() {
        return "tadeu";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.5.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String s) {
        if (player == null) return null;
        switch (s) {
            case "rankedSolo_inqueue":
                return String.valueOf(queueManager.getQueueSize("RankedSolo"));
            case "ranked1v1_inqueue":
                return String.valueOf(queueManager.getQueueSize("Ranked1v1"));
            case "ranked4v4_inqueue":
                return String.valueOf(queueManager.getQueueSize("Ranked4v4"));
            case "rankedSolo":
                return String.valueOf(eloManager.getPlayerEloSolo(player));
            case "ranked1v1":
                return String.valueOf(eloManager.getPlayerElo1v1(player));
            case "ranked4v4":
                return String.valueOf(eloManager.getPlayerElo4v4(player));
            case "geral":
                return String.valueOf(eloManager.getPlayerEloGeral(player));
            case "rank":
                int elo = eloManager.getPlayerEloGeral(player);
                return String.valueOf(eloManager.getRank(elo));
            case "mvp_count":
                int mvpCount = mvpManager.getPlayerMVPCount(player);
                return String.valueOf(mvpCount);
            }
        return null;
    }
}
