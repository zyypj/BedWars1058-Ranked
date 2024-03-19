package com.tadeubw.bedwars1058ranked.queue.command;

import com.tadeubw.bedwars1058ranked.queue.QueueManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinQueueCommand implements CommandExecutor {

    private final QueueManager queueManager;

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
        queueManager.openJoinMenu(player);
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
        return true;
    }

}
