package com.tadeubw.bedwars1058ranked.queue.command;

import com.tadeubw.bedwars1058ranked.queue.QueueManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveQueueCommand implements CommandExecutor {

    private final QueueManager queueManager;

    public LeaveQueueCommand(QueueManager queueManager) {
        this.queueManager = queueManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser usado por jogadores!");
            return true;
        }
        Player player = (Player) sender;
        queueManager.leaveQueue(player);
        return true;
    }
}
