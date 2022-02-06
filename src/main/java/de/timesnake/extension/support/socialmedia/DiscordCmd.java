package de.timesnake.extension.support.socialmedia;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.extension.support.chat.Plugin;
import de.timesnake.library.basic.util.cmd.Arguments;
import de.timesnake.library.basic.util.cmd.ExCommand;
import net.md_5.bungee.api.chat.ClickEvent;

import java.util.List;

public class DiscordCmd implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {

        if (!sender.isPlayer(true)) {
            return;
        }

        if (sender.hasPermission("exsupport.discord") && args.isLengthEquals(1, false)) {
            Server.broadcastClickableMessage(Plugin.SUPPORT, "Join our discord: https://discord.gg/YRCZhFVE9z", "https://discord.gg/YRCZhFVE9z", "Click to open link", ClickEvent.Action.OPEN_URL);
        } else {
            User user = sender.getUser();

            user.sendClickablePluginMessage(Plugin.SUPPORT, "Join our discord: https://discord.gg/YRCZhFVE9z", "https://discord.gg/YRCZhFVE9z", "Click to open link", ClickEvent.Action.OPEN_URL);
        }

    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return List.of();
    }
}
