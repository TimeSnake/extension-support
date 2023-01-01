/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.support.main;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.extension.support.book.SupportCmd;
import de.timesnake.extension.support.book.TicketManager;
import de.timesnake.extension.support.chat.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ExSupport extends JavaPlugin {

    private static ExSupport plugin;

    public static ExSupport getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        Server.getCommandManager().addCommand(this, "support", List.of("ticket", "tickets"), new SupportCmd(),
                Plugin.SUPPORT);


        new TicketManager();
    }
}
