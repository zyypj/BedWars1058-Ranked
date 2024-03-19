package com.tadeubw.bedwars1058ranked.queue;

import com.andrei1058.bedwars.api.BedWars;
import com.tadeubw.bedwars1058ranked.Main;
import com.tadeubw.bedwars1058ranked.elo.EloManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    @EventHandler
    public void onClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("§7Entrar em uma Fila")) {
            if (e.getCurrentItem().getType() == Material.BED && e.getCurrentItem().getAmount() == 1) {
                UUID playerUUID = player.getUniqueId();
                if (bedwarsAPI.getPartyUtil().hasParty(player)) {
                    player.sendMessage("§cVocê não pode entrar em uma fila ranqueada em party!");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    return;
                }
                if (bedwarsAPI.getStatsUtil().getPlayerWins(playerUUID) >= 100 || player.hasPermission("bw.vip")) {
                    if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                        player.sendMessage("§cVocê já está jogando!");
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        return;
                    }
                    joinQueue(player, "Ranked1v1");
                    player.closeInventory();
                    return;
                }
                player.sendMessage("§cPara entrar numa fila ranqueada você precisa de\n§c§l+100 WINS §cou comprar §e§lVIP§c.");
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                return;
            }
            if (e.getCurrentItem().getType() == Material.BED && e.getCurrentItem().getAmount() == 4) {
                if (!(bedwarsAPI.getPartyUtil().hasParty(player))) {
                    player.sendMessage("§cVocê precisa estar em uma party para entrar nessa fila!");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    return;
                }
                if (bedwarsAPI.getPartyUtil().hasParty(player)) {
                    if (!(bedwarsAPI.getPartyUtil().isOwner(player))) {
                        player.sendMessage("§cApenas o dono da party pode entrar em uma fila ranqueada!");
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        return;
                    }
                    List<Player> members = bedwarsAPI.getPartyUtil().getMembers(player);
                    if (members.size() != 4) {
                        player.sendMessage("§cPara jogar 4v4, você precisa de exatamente 4 jogadores em party!");
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        return;
                    }
                    UUID playerUUID = player.getUniqueId();
                    if (bedwarsAPI.getStatsUtil().getPlayerWins(playerUUID) >= 100 || player.hasPermission("bw.vip")) {
                        if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                            player.sendMessage("§cVocê já está jogando!");
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                            return;
                        }
                        openJoinMenu(player);
                        for (Player member : members) {
                            if (member != player) {
                                member.sendMessage("");
                                member.sendMessage("§7A party entrou numa fila de partida 4v4 ranqueada.");
                                member.sendMessage("");
                                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                                player.closeInventory();
                                return;
                            }
                        }
                    }
                    player.sendMessage("§cPara entrar numa fila ranqueada você precisa de\n§c§l+100 WINS §cou comprar §e§lVIP§c.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    return;
                }
                joinQueue(player, "Ranked4v4");
                player.closeInventory();
                return;
            }
            e.setCancelled(true);
        }
    }

    public void openJoinMenu(Player player) {
        Inventory testMenu = Bukkit.createInventory(player, 27, "§7Entrar em uma Fila");

        ItemStack Ranked1Item = new ItemStack(Material.BED);
        ItemMeta Ranked1Meta = Ranked1Item.getItemMeta();
        Ranked1Meta.setDisplayName("§aRanked 1v1");
        List<String> lore = new ArrayList<>();
        lore.add("§7Entrar na fila para");
        lore.add("§7§lRanked1v1");
        lore.add("");
        lore.add("§eClique para entrar na fila");
        Ranked1Meta.setLore(lore);
        Ranked1Item.setItemMeta(Ranked1Meta);
        testMenu.setItem(12, Ranked1Item);

        ItemStack Ranked4Item = new ItemStack(Material.BED);
        ItemMeta Ranked4Meta = Ranked4Item.getItemMeta();
        Ranked4Meta.setDisplayName("§aRanked 4v4");
        List<String> lore4 = new ArrayList<>();
        lore4.add("§7Entrar na fila para");
        lore4.add("§7§lRanked4v4");
        lore4.add("");
        lore4.add("§eClique para entrar na fila");
        Ranked4Meta.setLore(lore4);
        Ranked4Item.setItemMeta(Ranked4Meta);
        Ranked4Item.setAmount(4);
        testMenu.setItem(14, Ranked4Item);

        player.openInventory(testMenu);
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
