package de.timesnake.extension.support.book;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;

import java.util.List;

public class SupportCmd implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!sender.isPlayer(true)) {
            return;
        }

        User user = sender.getUser();

        if (args.isLengthEquals(0, false)) {
            new TicketInventory(user, TicketInventory.Type.OWN).open();
            return;
        }

        if (!sender.hasPermission("support.open", 3)) {
            return;
        }

        if (!args.isLengthEquals(1, true)) {
            return;
        }

        switch (args.getString(0).toLowerCase()) {
            case "own":
                new TicketInventory(user, TicketInventory.Type.OWN).open();
                break;
            case "all":
                new TicketInventory(user, TicketInventory.Type.ALL).open();
                break;
            case "open":
                new TicketInventory(user, TicketInventory.Type.OPEN).open();
                break;
            case "admin":
                new TicketInventory(user, TicketInventory.Type.ADMIN).open();
                break;
            default:
                sender.sendPluginMessage(ChatColor.WARNING + "Support type not found");
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return null;
    }
}
