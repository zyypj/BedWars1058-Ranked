package com.tadeubw.bedwars1058ranked.queue.command;

import com.andrei1058.bedwars.api.BedWars;
import com.tadeubw.bedwars1058ranked.queue.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class JoinQueueCommand implements CommandExecutor {

    private final QueueManager queueManager;
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    public JoinQueueCommand(QueueManager queueManager) {
        this.queueManager = queueManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser usado por jogadores!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length > 0) {
                String gameType = args[0];
                if (args[0].equals("Ranked1v1")) {
                    UUID playerUUID = player.getUniqueId();
                    if (bedwarsAPI.getPartyUtil().hasParty(player)) {
                        player.sendMessage("§cVocê não pode entrar em uma fila ranqueada em party!");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        return true;
                    }
                    if (bedwarsAPI.getStatsUtil().getPlayerWins(playerUUID) >= 100 || player.hasPermission("bw.vip")) {
                        if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                            player.sendMessage("§cVocê já está jogando!");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                            return true;
                        }
                        queueManager.joinQueue(player, gameType);
                        return true;
                    }
                    player.sendMessage("§cPara entrar numa fila ranqueada você precisa de\n§c§l+100 WINS §cou comprar §e§lVIP§c.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    return true;
                }
                if (args[0].equals("Ranked4v4")) {
                    if (!(bedwarsAPI.getPartyUtil().hasParty(player))) {
                        player.sendMessage("§cVocê precisa estar em uma party para entrar nessa fila!");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        return true;
                    }
                    if (bedwarsAPI.getPartyUtil().hasParty(player)) {
                        if (!(bedwarsAPI.getPartyUtil().isOwner(player))) {
                            player.sendMessage("§cApenas o dono da party pode entrar em uma fila ranqueada!");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                            return true;
                        }
                        List<Player> members = bedwarsAPI.getPartyUtil().getMembers(player);
                        if (members.size() != 4) {
                            player.sendMessage("§cPara jogar 4v4, você precisa de exatamente 4 jogadores em party!");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                            return true;
                        }
                        UUID playerUUID = player.getUniqueId();
                        if (bedwarsAPI.getStatsUtil().getPlayerWins(playerUUID) >= 100 || player.hasPermission("bw.vip")) {
                            if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                                player.sendMessage("§cVocê já está jogando!");
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                                return true;
                            }
                            queueManager.joinQueue(player, gameType);
                            for (Player member : members) {
                                if (member != player) {
                                    member.sendMessage("");
                                    member.sendMessage("§7A party entrou numa fila de partida 4v4 ranqueada.");
                                    member.sendMessage("");
                                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                                }
                            }
                            return true;
                        }
                        player.sendMessage("§cPara entrar numa fila ranqueada você precisa de\n§c§l+100 WINS §cou comprar §e§lVIP§c.");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        return true;
                    }
                }
            player.sendMessage("§cUso correto: /joinqueue <Ranked1v1/Ranked4v4>");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            return true;
        }
        player.sendMessage("§cUso correto: /joinqueue <Ranked1v1/Ranked4v4>");
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
        return true;
    }

}
