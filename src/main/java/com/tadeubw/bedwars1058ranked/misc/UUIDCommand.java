package com.tadeubw.bedwars1058ranked.misc;

import com.tadeubw.bedwars1058ranked.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UUIDCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Esse comando só pode ser executado por jogadores.");
            return true;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (player.hasPermission("bw.admin")) {
            if (args.length == 0) {
                player.sendMessage("§7Seu UUID é: §d" + uuid);
                return true;
            }
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage("§cJogador não encontrado ou offline.");
                    return true;
                }
                UUID targetUUID = target.getUniqueId();
                sender.sendMessage("§7O UUID de §7§l" + target.getName() + " §7é: §d" + targetUUID);
                return true;
            }
        } else {
            sender.sendMessage(Messages.CMD_NOT_FOUND);
            return true;
        }
        return false;
    }
}
