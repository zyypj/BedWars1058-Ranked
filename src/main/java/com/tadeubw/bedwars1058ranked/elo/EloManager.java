package com.tadeubw.bedwars1058ranked.elo;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.shop.ICategoryContent;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.tadeubw.bedwars1058ranked.Main;
import com.tadeubw.bedwars1058ranked.mvpSystem.MVPManager;
import com.tadeubw.bedwars1058ranked.queue.QueueManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EloManager implements Listener {

    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
    private FileConfiguration playerData;
    private final MVPManager mvpManager;
    private final Main plugin;
    private final QueueManager queueManager;


    public EloManager(Main plugin, MVPManager mvpManager, QueueManager queueManager, FileConfiguration playerData) {
        this.plugin = plugin;
        this.playerData = playerData;
        this.queueManager = queueManager;
        this.mvpManager = mvpManager;
        File playerDataFile = new File(plugin.getDataFolder(), "playerselo.yml");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!playerData.contains(uuid.toString())) {
            playerData.set(uuid.toString(), 1000);
            savePlayerData();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        savePlayerData();
    }

    public void savePlayerData() {
        File dataFolder = new File(Bukkit.getPluginManager().getPlugin("BedWars1058").getDataFolder(), "Addons/Ranked");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File playerDataFile = new File(dataFolder, "playersElo.yml");
        try {
            YamlConfiguration config = new YamlConfiguration();
            for (String uuidString : playerData.getKeys(false)) {

                // Define o elo inicial como 1000 em todos os modos
                int eloSolo = 1000;
                int elo1v1 = 1000;
                int elo4v4 = 1000;
                int eloGeral = (eloSolo + elo1v1 + elo4v4) / 3;
                int mvpCount = 0;

                // Se o jogador já tiver um elo registrado, mantém o elo atual
                if (playerData.contains(uuidString + ".rankedsolo")) {
                    eloSolo = playerData.getInt(uuidString + ".rankedsolo");
                }
                if (playerData.contains(uuidString + ".ranked1v1")) {
                    elo1v1 = playerData.getInt(uuidString + ".ranked1v1");
                }
                if (playerData.contains(uuidString + ".ranked4v4")) {
                    elo4v4 = playerData.getInt(uuidString + ".ranked4v4");
                }
                if (playerData.contains(uuidString + ".geral")) {
                    eloGeral = playerData.getInt(uuidString + ".geral");
                }
                if (playerData.contains(uuidString + ".mvp")) {
                    mvpCount = playerData.getInt(uuidString + ".mvp");
                }

                config.set(uuidString + ".geral", eloGeral);
                config.set(uuidString + ".rankedsolo", eloSolo);
                config.set(uuidString + ".ranked1v1", elo1v1);
                config.set(uuidString + ".ranked4v4", elo4v4);
                config.set(uuidString + ".mvp", mvpCount);
            }
            config.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getPlayerData() {
        return playerData;
    }

    public void setPlayerData(FileConfiguration playerData) {
        this.playerData = playerData;
    }

    public int getPlayerElo(Player player) {
        return playerData.getInt(player.getUniqueId().toString());
    }

    public int getElo(UUID uuid, String tipoPartida) {
        if (playerData.contains(uuid.toString() + "." + tipoPartida)) {
            return playerData.getInt(uuid.toString() + "." + tipoPartida);
        }
        return 1000; // Retorna 1000 se o jogador não tiver elo registrado para o tipo de partida
    }

    //rank

    public String getRank(int elo) {
        if (elo >= 950 && elo <= 1050) {
            return "§8[Bronze III]";
        } else if (elo > 1050 && elo <= 1100) {
            return "§8[Bronze II]";
        } else if (elo > 1100 && elo <= 1150) {
            return "§8[Bronze I]";
        } else if (elo > 1150 && elo <= 1200) {
            return "§7[Prata III]";
        } else if (elo > 1200 && elo <= 1250) {
            return "§7[Prata II]";
        } else if (elo > 1250 && elo <= 1300) {
            return "§7[Prata I]";
        } else if (elo > 1300 && elo <= 1450) {
            return "§6[Ouro III]";
        } else if (elo > 1450 && elo <= 1500) {
            return "§6[Ouro II]";
        } else if (elo > 1500 && elo <= 1550) {
            return "§6[Ouro I]";
        }
        // Caso seja maior que o último rank registrado
        return "§c[Sem Rank]";
    }

    public void addElo(UUID uuid, int elo, String tipoPartida) {
        int currentElo;
        switch (tipoPartida.toLowerCase()) {
            case "rankedsolo":
                currentElo = playerData.getInt(uuid.toString() + ".rankedsolo", 1000);
                playerData.set(uuid.toString() + ".rankedsolo", currentElo + elo);
                break;
            case "ranked1v1":
                currentElo = playerData.getInt(uuid.toString() + ".ranked1v1", 1000); // Default elo is 1000
                playerData.set(uuid.toString() + ".ranked1v1", currentElo + elo);
                break;
            case "ranked4v4":
                currentElo = playerData.getInt(uuid.toString() + ".ranked4v4", 1000); // Default elo is 1000
                playerData.set(uuid.toString() + ".ranked4v4", currentElo + elo);
                break;
            case "geral":
                int eloSolo = playerData.getInt(uuid.toString() + ".rankedsolo", 1000);
                int elo1v1 = playerData.getInt(uuid.toString() + ".ranked1v1", 1000);
                int elo4v4 = playerData.getInt(uuid.toString() + ".ranked4v4", 1000);
                int eloGeral = (eloSolo + elo1v1 + elo4v4) / 3;
                playerData.set(uuid.toString() + ".geral", eloGeral + elo);
                break;
            default:
                return;
        }
        savePlayerData();
    }

    public int getPlayerEloSolo(Player player) {
        return playerData.getInt(player.getUniqueId().toString() + ".rankedsolo");
    }
    public int getPlayerElo1v1(Player player) {
        return playerData.getInt(player.getUniqueId().toString() + ".ranked1v1");
    }

    public int getPlayerElo4v4(Player player) {
        return playerData.getInt(player.getUniqueId().toString() + ".ranked4v4");
    }

    public int getPlayerEloGeral(Player player) {
        int eloSolo = getPlayerEloSolo(player);
        int elo1v1 = getPlayerElo1v1(player);
        int elo4v4 = getPlayerElo4v4(player);
        return (eloSolo + elo1v1 + elo4v4) / 3;
    }
    
    public void setEloSolo(UUID uuid, int elo) {
        playerData.set(uuid.toString() + ".rankedsolo", elo);
        savePlayerData();
    }

    public void setElo1v1(UUID uuid, int elo) {
        playerData.set(uuid.toString() + ".ranked1v1", elo);
        savePlayerData();
    }

    public void setElo4v4(UUID uuid, int elo) {
        playerData.set(uuid.toString() + ".ranked4v4", elo);
        savePlayerData();
    }

    public void setEloGeral(UUID uuid, int elo) {
        playerData.set(uuid.toString() + ".geral", elo);
        savePlayerData();
    }

    //winstreak

    private int getWinstreak(Player player, String tipoPartida) {
        // Definir o placeholder com base no tipo de partida
        String winstreakPlaceholder = "";
        if (tipoPartida.equals("Ranked1v1")) {
            winstreakPlaceholder = "%groupstats_Ranked1v1_winstreak%";
        } else if (tipoPartida.equals("Ranked4v4")) {
            winstreakPlaceholder = "%groupstats_Ranked4v4_winstreak%";
        } else if (tipoPartida.equals("RankedSolo")) {
            winstreakPlaceholder = "%groupstats_RankedSolo_winstreak%";
        }
        return Integer.parseInt(PlaceholderAPI.setPlaceholders(player, winstreakPlaceholder));
    }

    public void checkWinstreak(Player player, String tipoPartida) {
        int winstreak = getWinstreak(player, tipoPartida);
        UUID playerUUID = player.getUniqueId();

        if (winstreak % 10 == 0) {
            // Se a winstreak for um múltiplo de 10, recompensa o jogador com Elo
            addElo(playerUUID, 20, tipoPartida); // Adicionar 20 de Elo ao jogador
            player.sendMessage("§c+20 Ranked Elo (Winstreak)");
        }
    }

    //eventos

    @EventHandler
    public void playerBedBreak(PlayerBedBreakEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
            String group = e.getArena().getGroup();
            if (group.equalsIgnoreCase("RankedSolo") || group.equalsIgnoreCase("Ranked1v1") || group.equalsIgnoreCase("Ranked4v4")) {
                Random random = new Random();
                int playerEloIncrease = random.nextInt(13) + 4; // Gera um número aleatório de 4 a 16
                addElo(playerUUID, playerEloIncrease, group.toLowerCase());
                player.sendMessage("§c+" + playerEloIncrease + " Ranked Elo (Quebra de Cama)");

                for (Player victim : e.getVictimTeam().getMembers()) {
                    UUID victimUUID = victim.getUniqueId();
                    int victimEloDecrease = random.nextInt(9) + 8; // Gera um número aleatório de -8 a -16
                    addElo(victimUUID, -victimEloDecrease, group.toLowerCase());
                    victim.sendMessage("§c-" + victimEloDecrease + " Ranked Elo (Cama Perdida)");
                }

                    mvpManager.addBedBreakPoints(player);
            }
        }
    }

    @EventHandler
    public void onKill(PlayerKillEvent e) {
        Player killer = e.getKiller();
        UUID killerUUID = killer.getUniqueId();
        if (bedwarsAPI.getArenaUtil().isPlaying(killer)) {
            String group = e.getArena().getGroup();
            Player victim = e.getVictim();
            UUID victimUUID = victim.getUniqueId();
            if (group.equalsIgnoreCase("RankedSolo") || group.equalsIgnoreCase("Ranked1v1") || group.equalsIgnoreCase("Ranked4v4")) {
                if (e.getCause().isFinalKill()) {
                    Random random = new Random();
                    int killerEloIncrease = random.nextInt(8) + 1; // Gera um número aleatório de 1 a 8
                    addElo(killerUUID, killerEloIncrease, group.toLowerCase());
                    killer.sendMessage("§c+" + killerEloIncrease + " Ranked Elo (Kill Final)");
                    killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1, 1);

                    int victimEloDecrease = random.nextInt(10) + 3; // Gera um número aleatório de 3 a 12
                    addElo(victimUUID, -victimEloDecrease, group.toLowerCase());
                    victim.sendMessage("§c-" + victimEloDecrease +" Ranked Elo (Morte Final)");
                    victim.playSound(victim.getLocation(), Sound.NOTE_BASS, 1, 1);

                    mvpManager.addFinalKillPoints(killer);
                } else {
                    mvpManager.addKillPoints(killer);
                }
            }
        }
    }

    @EventHandler
    public void gameEnd(GameEndEvent e) {
        String group = e.getArena().getGroup();
        ITeam winnerTeam = e.getTeamWinner();
        List<UUID> loserUUIDs = e.getLosers();
        if (group.equalsIgnoreCase("RankedSolo") || group.equalsIgnoreCase("Ranked1v1") || group.equalsIgnoreCase("Ranked4v4")) {
            String tipoPartida = group.equalsIgnoreCase("Ranked1v1") ? "Ranked1v1" : "Ranked4v4";
            for (Player winner : winnerTeam.getMembers()) {
                checkWinstreak(winner, tipoPartida);
                Random random = new Random();
                int winnerEloIncrease = random.nextInt(11) + 10; // Gera um número aleatório de 10 a 20
                addElo(winner.getUniqueId(), winnerEloIncrease, tipoPartida.toLowerCase());
                winner.sendMessage("§c+" + winnerEloIncrease + " Ranked Elo (Vitória)");
                winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 1, 1);
            }
            for (UUID loserUUID : loserUUIDs) {
                Player loser = Bukkit.getPlayer(loserUUID);
                if (loser != null) {
                    Random random = new Random();
                    int loserEloDecrease = random.nextInt(11) + 11; // Gera um número aleatório de -12 a -22
                    addElo(loserUUID, -loserEloDecrease, tipoPartida.toLowerCase());
                    loser.sendMessage("§c-" + loserEloDecrease + " Ranked Elo (Derrota)");
                    loser.playSound(loser.getLocation(), Sound.NOTE_BASS, 1, 1);
                }
            }
            mvpManager.endGame();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player winner : winnerTeam.getMembers()) {
                    if (bedwarsAPI.getArenaUtil().isPlaying(winner)) {
                        queueManager.endGame(winnerTeam.getMembers());
                        break;
                    }
                }
            }, 10 * 20);
        }
    }

    @EventHandler
    public void onShop(ShopBuyEvent e) {
        String group = e.getArena().getGroup();
        Player player = e.getBuyer();
        ITeam teamA = e.getArena().getTeam("Azul");
        ITeam teamB = e.getArena().getTeam("Vermelho");
        ICategoryContent categoryContent = e.getCategoryContent();
        String identifier = categoryContent.getIdentifier();
        // Permanently blocked items
        if (group.equalsIgnoreCase("RankedSolo") || group.equalsIgnoreCase("Ranked1v1") || group.equalsIgnoreCase("Ranked4v4")) {
            if (identifier.equals("utility-category.category-content.fireball") ||
                    identifier.equals("ranged-category.category-content.bow1") ||
                    identifier.equals("ranged-category.category-content.bow2") ||
                    identifier.equals("ranged-category.category-content.bow3") ||
                    identifier.equals("ranged-category.category-content.arrow") ||
                    identifier.equals("potions-category.category-content.jump-potion") ||
                    identifier.equals("potions-category.category-content.speed-potion") ||
                    identifier.equals("blocks-category.category-content.ladder")) {
                player.sendMessage("§cItem bloqueado nos modos ranqueados!");
                e.setCancelled(true);
                return;
            }
        }
        // Temporarily blocked items
        if (group.equalsIgnoreCase("RankedSolo") || group.equalsIgnoreCase("Ranked1v1") || group.equalsIgnoreCase("Ranked4v4")) {
            if (!(teamA.isBedDestroyed() || teamB.isBedDestroyed())) {
                if (identifier.equals("potions-category.category-content.invisibility") ||
                        identifier.equals("utility-category.category-content.tnt") ||
                        identifier.equals("utility-category.category-content.tower") ||
                        identifier.equals("utility-category.category-content.ender-pearl") ||
                        identifier.equals("melee-category.category-content.stick") ||
                        identifier.equals("utility-category.category-content.water-bucket")) {
                    player.sendMessage("§cItem bloqueado até alguma cama for quebrada!");
                    e.setCancelled(true);
                }
            }
        }
    }
}
