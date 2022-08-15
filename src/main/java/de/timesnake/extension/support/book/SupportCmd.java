package de.timesnake.extension.support.book;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import net.kyori.adventure.text.Component;

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
            case "own" -> new TicketInventory(user, TicketInventory.Type.OWN).open();
            case "all" -> new TicketInventory(user, TicketInventory.Type.ALL).open();
            case "open" -> new TicketInventory(user, TicketInventory.Type.OPEN).open();
            case "admin" -> new TicketInventory(user, TicketInventory.Type.ADMIN).open();
            default -> sender.sendPluginMessage(Component.text("Support type not found", ExTextColor.WARNING));
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return null;
    }
}
