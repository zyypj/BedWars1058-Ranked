package com.tadeubw.bedwars1058ranked.misc;

import com.tadeubw.bedwars1058ranked.elo.EloManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

    private final EloManager eloManager;

    public RankCommand(EloManager eloManager) {
        this.eloManager = eloManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Esse comando só pode ser usado por jogadores");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            int elo = eloManager.getPlayerEloGeral(player);
            sender.sendMessage("§7Seu rank atual é: " + eloManager.getRank(elo));
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            int elo = eloManager.getPlayerEloGeral(target);
            if (target == null) {
                sender.sendMessage("§cJogador não encontrado ou offline.");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                return true;
            }
            sender.sendMessage("§7O rank atual de §l" + target.getName() + "§7 é: " + eloManager.getRank(elo));
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            return true;
        }
        sender.sendMessage("§cUse /rank <nick>");
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
        return true;
    }
}
