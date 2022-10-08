/*
 * extension-support.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
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
