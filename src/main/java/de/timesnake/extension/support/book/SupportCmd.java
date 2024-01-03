/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.support.book;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.chat.Plugin;
import net.kyori.adventure.text.Component;

public class SupportCmd implements CommandListener {

  private final Code perm = Plugin.NETWORK.createPermssionCode("support.open");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    if (!sender.isPlayer(true)) {
      return;
    }

    User user = sender.getUser();

    if (args.isLengthEquals(0, false)) {
      new TicketInventory(user, TicketInventory.Type.OWN).open();
      return;
    }

    if (!sender.hasPermission(this.perm)) {
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
  public Completion getTabCompletion() {
    return new Completion(this.perm);
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
