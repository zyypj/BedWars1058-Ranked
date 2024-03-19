package com.tadeubw.bedwars1058ranked.queue;

import com.andrei1058.bedwars.api.BedWars;
import com.tadeubw.bedwars1058ranked.Main;
import com.tadeubw.bedwars1058ranked.elo.EloManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class QueueManager implements Listener {

    private final Main plugin;
    private final EloManager eloManager;
    private final Map<String, List<Player>> gameQueue;
    private final Map<Player, String> playerGameMap = new HashMap<>();
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    public QueueManager(Main plugin, EloManager eloManager) {
        this.plugin = plugin;
        this.eloManager = eloManager;
        this.gameQueue = new HashMap<>();
    }

    public void joinQueue(Player player, String gameType) {
        if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
            player.sendMessage("§cVocê já está jogando!");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            return;
        }
        if (isInQueue(player, gameType)) {
            player.sendMessage("");
            player.sendMessage("§cVocê já está em uma fila!");
            player.sendMessage("§cUse /leavequeue.");
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            return;
        }
        List<Player> queue = gameQueue.computeIfAbsent(gameType, k -> new ArrayList<>());
        queue.add(player);
        player.sendMessage("");
        player.sendMessage("§7Você entrou na fila do modo: §d" + gameType);
        player.sendMessage("§7Digite §d/leavequeue §7para sair da fila.");
        player.sendMessage("");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        checkQueue(gameType);
    }

    public void leaveQueue(Player player) {
        for (List<Player> queue : gameQueue.values()) {
            if (queue.contains(player)) {
                queue.remove(player);
                player.sendMessage("");
                player.sendMessage("§7Você saiu da fila.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                return;
            }
        }
        player.sendMessage("§cVocê não está na fila.");
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
    }

    public void leaveQueue1(Player player) {
        for (List<Player> queue : gameQueue.values()) {
            if (queue.contains(player)) {
                queue.remove(player);
                return;
            }
        }
    }

    public boolean isInQueue(Player player, String gameType) {
        List<Player> queue = gameQueue.get(gameType);
        return queue != null && queue.contains(player);
    }

    public int getQueueSize(String gameType) {
        List<Player> queue = gameQueue.get(gameType);
        return queue != null ? queue.size() : 0;
    }

    public void checkQueue(String gameType) {
        if (gameType.equalsIgnoreCase("Ranked1v1") || gameType.equalsIgnoreCase("Ranked4v4")) {
            List<Player> queue = gameQueue.get(gameType);
            if (queue != null && queue.size() >= 2) {
                Player player1 = queue.get(0);
                Player player2 = findMatchingPlayer(player1, queue, gameType);
                if (player2 != null) {
                    queue.remove(player1);
                    queue.remove(player2);
                    startGame(Arrays.asList(player1, player2), gameType);
                } else {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (queue.contains(player1)) {
                            queue.remove(player1);
                            player1.sendMessage("§cNenhuma partida encontrada!");
                            player1.sendMessage("§cTente outro modo ou volte mais tarde.");
                            player1.playSound(player1.getLocation(), Sound.NOTE_BASS, 1, 1);
                        }
                    }, 5 * 60 * 20); // 5 minutos em ticks
                }
            }
        }
    }

    public Player findMatchingPlayer(Player player, List<Player> queue, String gameType) {
        int elo = eloManager.getPlayerEloGeral(player);
        int[] range = {100};
        AtomicReference<Player> matchingPlayerRef = new AtomicReference<>(null);
        BukkitTask[] task = new BukkitTask[1];

        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (range[0] <= 1000) {
                Player matchingPlayer = findPlayerInRange(player, queue, elo - range[0], elo + range[0], gameType);
                if (matchingPlayer != null && !matchingPlayer.equals(player)) {
                    task[0].cancel();
                    matchingPlayerRef.set(matchingPlayer);
                    return;
                }
                range[0] += 100;
            } else {
                queue.remove(player);
                player.sendMessage("§cNenhuma partida encontrada!");
                player.sendMessage("§cTente outro modo ou volte mais tarde.");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                task[0].cancel();
            }
        }, 0, 5 * 20);

        return matchingPlayerRef.get();
    }

    private Player findPlayerInRange(Player player, List<Player> queue, int minElo, int maxElo, String gameType) {
        for (Player p : queue) {
            if (p != player) {
                int elo = eloManager.getPlayerEloGeral(p);
                if (elo >= minElo && elo <= maxElo) {
                    startGame(Arrays.asList(player, p), gameType);
                    queue.remove(p); // Remover o jogador da fila
                    return p;
                }
            }
        }
        return null;
    }

    private void startGame(List<Player> players, String gameType) {
        for (Player player : players) {
            leaveQueue1(player);
            if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                player.sendMessage("§cVocê já está jogando!");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                return;
            }

            player.sendMessage("§7Partida Encontrada: §d" + gameType);
            player.sendMessage("§7Conectando...");
            player.sendMessage("");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                bedwarsAPI.getArenaUtil().joinRandomFromGroup(player, gameType);
                if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                    player.sendMessage("");
                    player.sendMessage("§7Você entrou em uma partida ranqueada!");
                    player.sendMessage("§7Modo: §d" + gameType);
                    player.sendMessage("");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                } else {
                    player.sendMessage("");
                    player.sendMessage("§cNão foi possível entrar na partida!");
                    player.sendMessage("§cRelogue e tente novamente.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                }
            }, 40L);
        }
    }
    // Método para verificar se um jogador está em uma partida
    public boolean isInGame(Player player) {
        return playerGameMap.containsKey(player);
    }

    // Método para remover um jogador da partida quando ela terminar
    public void endGame(List<Player> players) {
        for (Player player : players) {
            playerGameMap.remove(player);
        }
    }

    //eventos

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (String gameType : gameQueue.keySet()) {
            if (isInQueue(player, gameType)) {
                leaveQueue(player);
                break;
            }
        }
    }
}
