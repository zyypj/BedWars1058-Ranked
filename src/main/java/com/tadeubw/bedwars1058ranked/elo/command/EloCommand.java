package com.tadeubw.bedwars1058ranked.elo.command;

import com.tadeubw.bedwars1058ranked.elo.EloManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EloCommand implements CommandExecutor {

    private final EloManager eloManager;

    public EloCommand(EloManager eloManager) {
        this.eloManager = eloManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Esse comando só pode ser executado por jogadores.");
            return true;
        }
        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // /elo - elo geral
        if (args.length == 0) {
            int eloSoma = eloManager.getElo(playerUUID, "ranked1v1") + eloManager.getElo(playerUUID, "ranked4v4") + eloManager.getElo(playerUUID, "rankedSolo");
            int eloGeral = eloSoma / 3;
            player.sendMessage("§7Seu Ranked Elo Geral é: §d" + eloGeral);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            return true;
        }
        // /elo 1v1
        if (args.length == 1) {
            String tipo = args[0];
            int eloSoma = eloManager.getElo(playerUUID, "ranked1v1") + eloManager.getElo(playerUUID, "ranked4v4") + eloManager.getElo(playerUUID, "rankedSolo");
            int eloGeral = eloSoma / 3;
            switch (tipo.toLowerCase()) {
                case "solo":
                    sender.sendMessage("§7Seu Ranked Elo Solo é: §d" + eloManager.getElo(playerUUID, "rankedSolo"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                case "1v1":
                    sender.sendMessage("§7Seu Ranked Elo 1v1 é: §d" + eloManager.getElo(playerUUID, "ranked1v1"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                case "4v4":
                    sender.sendMessage("§7Seu Ranked Elo 4v4 é: §d" + eloManager.getElo(playerUUID, "ranked4v4"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                case "geral":
                    sender.sendMessage("§7Seu Ranked Elo Geral é: §d" + eloGeral);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                default:
                    sender.sendMessage("§cTipo de partida inválido. Use 'Solo', '1v1', '4v4' ou 'Geral'.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                    return true;
            }
        }
        // /elo <nick> 1v1
        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);
            String tipo = args[1];
            if (target == null) {
                sender.sendMessage("§cJogador não encontrado ou offline.");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                return true;
            }
            UUID targetUUID = target.getUniqueId();
            int eloSoma = eloManager.getElo(targetUUID, "ranked1v1") + eloManager.getElo(targetUUID, "ranked4v4") + eloManager.getElo(targetUUID, "rankedSolo");
            int eloGeral = eloSoma / 3;
            switch (tipo.toLowerCase()) {
                case "solo":
                    sender.sendMessage("§7O Ranked Elo Solo de §7§" + target.getName() + " §7é: §d" + eloManager.getElo(targetUUID, ".rankedSolo"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                case "1v1":
                    sender.sendMessage("§7O Ranked Elo 1v1 de §7§l" + target.getName() + " §7é: §d" + eloManager.getElo(targetUUID, "ranked1v1") + "§7.");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                case "4v4":
                    sender.sendMessage("§7O Ranked Elo 4v4 de §7§l" + target.getName() + " §7é: §d" + eloManager.getElo(targetUUID, "ranked4v4") + "§7.");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                case "geral":
                    sender.sendMessage("§7O Ranked Elo de §7§l" + target.getName() + " §7é: §d" + eloGeral + "§7.");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
                default:
                    sender.sendMessage("§cTipo de partida inválido. Use 'Solo', '1v1', '4v4' ou 'Geral'.");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    return true;
            }
        }

        // /elo add <nick> 1v1 (numero)
        if (sender.hasPermission("bwranked.admin")) {
            if (args.length == 4 && args[0].equalsIgnoreCase("add")) {
                String nick = args[1];
                String tipo = args[2];
                String number = args[3];
                Player target = Bukkit.getPlayer(nick);
                if (target == null) {
                    sender.sendMessage("§cUse: /elo add <nick> <modo> <novoElo>.");
                    return true;
                }
                UUID targetUUID = target.getUniqueId();
                int newElo;
                try {
                    newElo = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cO número fornecido não é válido.");
                    return true;
                }
                switch (tipo.toLowerCase()) {
                    case "solo":
                        eloManager.setEloSolo(targetUUID, newElo);
                        sender.sendMessage("§7O novo Ranked Elo do Solo de §l" + target.getName() + " §7foi definido para §d" + newElo);
                        target.sendMessage("§7Seu Ranked Elo do Solo foi definido para §d" + newElo + " §7por §7§l" + sender.getName());
                        target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
                        return true;
                    case "1v1":
                        eloManager.setElo1v1(targetUUID, newElo);
                        sender.sendMessage("§7O novo Ranked Elo do 1v1 de §7§l" + target.getName() + " §7foi definido para §d" + newElo);
                        target.sendMessage("§7Seu Ranked Elo do 1v1 foi definido para §d" + newElo + " §7por §7§l" + sender.getName());
                        target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
                        return true;
                    case "4v4":
                        eloManager.setElo4v4(targetUUID, newElo);
                        sender.sendMessage("§7O novo Ranked Elo do 4v4 de §7§l" + target.getName() + " §7foi definido para §d" + newElo);
                        target.sendMessage("§7Seu Ranked Elo do 4v4 foi definido para §d" + newElo + " §7por §7§l" + sender.getName());
                        target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
                        return true;
                    case "geral":
                        eloManager.setEloGeral(targetUUID, newElo);
                        sender.sendMessage("§7O novo Ranked Elo Geral de §7§l" + target.getName() + " §7foi definido para §d" + newElo);
                        target.sendMessage("§7Seu Ranked Elo Geral foi definido para §d" + newElo + " §7por §7§l" + sender.getName());
                        target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
                        return true;
                    default:
                        sender.sendMessage("§cTipo de partida inválido. Use 'Solo', '1v1', '4v4' ou 'Geral'.");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                        return true;

                }
            }
        } else {
            sender.sendMessage("§cComando não encontrado ou você não tem permissão");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            return true;
        }
        sender.sendMessage("§cUse /elo <nick> <modo>");
        return true;
    }
}
