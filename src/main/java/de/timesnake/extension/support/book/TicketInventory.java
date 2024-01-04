/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.extension.support.book;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserDropItemEvent;
import de.timesnake.basic.bukkit.util.user.inventory.*;
import de.timesnake.database.util.Database;
import de.timesnake.database.util.support.DbTicket;
import de.timesnake.extension.support.chat.Plugin;
import de.timesnake.extension.support.main.ExSupport;
import de.timesnake.library.basic.util.Status;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TicketInventory implements UserInventoryInteractListener, UserInventoryClickListener,
    InventoryHolder,
    Listener {

  public static final String ID = "§0§lID: §8";
  public static final String NAME = "§0§lName: §8";
  public static final String STATUS = "§0§lStatus: §8";
  public static final String MESSAGE = "§0§lMessage: §8\n";
  public static final String ANSWER = "§0§lAnswer: §8\n";

  public static final Set<User> OPEN_TICKET_USERS = new HashSet<>();

  public static String getMessageFromBook(BookMeta meta) {
    String message = meta.getPage(2);

    List<String> list = new ArrayList<>(List.of(message.split("\n")));
    list.remove(0);

    return String.join("\n", list);
  }

  public static String getAnswerFromBook(BookMeta meta) {
    String answer = meta.getPage(3);

    List<String> list = new ArrayList<>(List.of(answer.split("\n")));
    list.remove(0);

    return String.join("\n", list);
  }

  ;

  public static Status.Ticket getStatusFromBook(BookMeta meta) {
    String msg = meta.getPage(1);

    String[] lines = msg.split("\n");

    Status.Ticket status = null;

    for (String line : lines) {
      if (line.toLowerCase().contains("[x]")) {
        for (Status.Ticket s : Status.Ticket.values()) {
          if (line.contains(s.getDisplayName())) {
            status = s;
          }
        }
      }
    }

    return status;
  }

  public static TextComponent[] createStatusButtons(Status.Ticket ticketStatus) {
    TextComponent[] statuss = new TextComponent[Status.Ticket.values().length];

    int i = 0;
    for (Status.Ticket status : Status.Ticket.values()) {
      TextComponent msg = new TextComponent();
      msg.addExtra(status.getChatColor() + "[ ]");

      if (status.equals(ticketStatus)) {
        msg.addExtra(" §l" + status.getDisplayName());
      } else {
        msg.addExtra(" " + status.getDisplayName());
      }
      msg.addExtra("\n");

      statuss[i] = msg;

      i++;
    }
    return statuss;
  }

  private final ExItemStack ticketInv = new ExItemStack(1, Material.WRITTEN_BOOK, "§6Tickets");
  private final ExItemStack refresh = new ExItemStack(7, Material.ORANGE_DYE, "§cRefresh");
  private final ExItemStack close = new ExItemStack(8, Material.RED_DYE, "§cClose");
  private final User user;
  private final Type type;
  private final ExInventory inventory;
  private final Map<Integer, DbTicket> ticketsByItemId = new HashMap<>();
  private final Map<Integer, Ticket> editedTicketsByTicketId = new HashMap<>();
  private ExItemStack createTicket = new ExItemStack(0, Material.WRITABLE_BOOK,
      "§6Create Ticket").enchant();
  private ItemStack[] inventoryContents;
  private boolean invalid = false;

  private Code statusPerm;
  private Code editPerm;
  private Code answerPerm;

  public TicketInventory(User user, Type type) {
    this.user = user;
    this.type = type;

    this.statusPerm = Plugin.SUPPORT.createPermssionCode("support.status");
    this.editPerm = Plugin.SUPPORT.createPermssionCode("support.edit");
    this.answerPerm = Plugin.SUPPORT.createPermssionCode("support.answer");

    this.setCreationBook();

    this.inventory = new ExInventory(9 * 6, Component.text("Tickets"), this);

    int slot = 0;

    switch (type) {
      case OWN:
        for (DbTicket ticket : Database.getSupport().getTickets(user.getUniqueId())) {
          this.inventory.setItemStack(slot,
              this.getBookByTicket(ticket.toLocal(), false).setSlot(slot));
          slot++;
        }
        break;

      case OPEN:
        for (Integer id : Database.getSupport().getTicketIds(Status.Ticket.OPEN)) {
          DbTicket ticket = Database.getSupport().getTicket(id).toLocal();
          this.inventory.setItemStack(slot,
              this.getBookByTicket(ticket, true).setSlot(slot));
          slot++;
        }
        break;

      case ADMIN:
        for (Integer id : Database.getSupport().getTicketIds(Status.Ticket.ADMIN)) {
          DbTicket ticket = Database.getSupport().getTicket(id).toLocal();
          this.inventory.setItemStack(slot,
              this.getBookByTicket(ticket, true).setSlot(slot));
          slot++;
        }
        break;

      case ALL:
        for (DbTicket ticket : Database.getSupport().getTickets()) {
          this.inventory.setItemStack(slot,
              this.getBookByTicket(ticket.toLocal(), true).setSlot(slot));
          slot++;
        }
        break;
    }

    Server.getInventoryEventManager()
        .addInteractListener(this, createTicket, ticketInv, refresh, close);
    Server.getInventoryEventManager().addClickListener(this, this);
    Server.registerListener(this, ExSupport.getPlugin());
  }

  public void open() {
    OPEN_TICKET_USERS.add(user);

    this.user.updateInventory();
    this.inventoryContents = this.user.getInventory().getContents();
    this.user.clearInventory();

    this.user.setItem(createTicket);
    this.user.setItem(ticketInv);
    this.user.setItem(refresh);
    this.user.setItem(close);

    this.user.updateInventory();
  }

  public void close() {
    Server.getInventoryEventManager().removeInteractListener(this);
    Server.getInventoryEventManager().removeClickListener(this);

    this.user.getInventory().setContents(this.inventoryContents);

    this.invalid = true;

    OPEN_TICKET_USERS.remove(this.user);

    this.user.updateInventory();
  }

  private ExItemStack getBookByTicket(DbTicket ticket, boolean showName) {
    ExItemStack item = new ExItemStack(Material.WRITABLE_BOOK);

    item.setDisplayName("§6ID: " + ticket.getId());

    if (showName) {
      item.setLore("§fStatus: " + ticket.getStatus().getChatColor() + ticket.getStatus()
              .getDisplayName(),
          "§fName: §7" + ticket.getName());
    } else {
      item.setLore("§fStatus: §7" + ticket.getStatus().getDisplayName());
    }

    BookMeta meta = ((BookMeta) item.getItemMeta());

    meta.setTitle("ID: " + ticket.getId());

    List<BaseComponent[]> pages = new ArrayList<>();

    BaseComponent[] info =
        new BaseComponent[]{new TextComponent(
            ID + ticket.getId() + "\n" + NAME + ticket.getName() + "\n\n\n")};

    pages.add(
        (BaseComponent[]) ArrayUtils.addAll(info, createStatusButtons(ticket.getStatus())));

    pages.add(new BaseComponent[]{new TextComponent(MESSAGE + ticket.getMessage())});

    if (ticket.getAnswer() != null) {
      pages.add(new BaseComponent[]{new TextComponent(ANSWER + ticket.getAnswer())});
    } else {
      pages.add(new BaseComponent[]{new TextComponent(ANSWER + "§cNo Answer")});
    }

    meta.spigot().setPages(pages);

    item.setItemMeta(meta);

    this.ticketsByItemId.put(item.getId(), ticket.toDatabase());

    Server.getInventoryEventManager().addInteractListener(this, item);

    return item;
  }

  @Override
  public void onUserInventoryInteract(UserInventoryInteractEvent event) {
    User user = event.getUser();

    if (!this.user.equals(user)) {
      return;
    }

    event.setCancelled(true);

    ExItemStack clickedItem = event.getClickedItem();

    if (clickedItem.equals(close)) {
      this.close();
    } else if (clickedItem.equals(ticketInv)) {
      user.openInventory(this.inventory);
    } else if (clickedItem.equals(refresh)) {
      this.user.sendPluginMessage(Plugin.SUPPORT,
          Component.text("Refreshed", ExTextColor.PERSONAL));
      this.refresh();
    }

    if (clickedItem.getType().equals(Material.WRITABLE_BOOK)) {
      DbTicket ticket = this.ticketsByItemId.get(clickedItem.getId());

      if (ticket != null) {
        boolean success = TicketManager.getInstance()
            .requestTicketLock(this, ticket.getId());

        if (!success) {
          this.rejectTicket(ticket.getId());
        }
      }
    }
  }

  public void rejectTicket(Integer id) {
    this.user.closeInventory();
    this.user.sendPluginMessage(Plugin.SUPPORT,
        Component.text("This ticket is being edited " + "by another " +
            "player. Try later again!", ExTextColor.WARNING));
  }

  public void acceptTicket(Integer id) {
    Ticket ticket = this.editedTicketsByTicketId.remove(id);

    if (ticket == null) {
      return;
    }

    DbTicket dbTicket = ticket.getDatabase();

    dbTicket.setStatus(ticket.getStatus());
    dbTicket.setMessage(ticket.getMessage());
    dbTicket.setAnswer(ticket.getAnswer());

    this.user.sendPluginMessage(Plugin.SUPPORT,
        Component.text("Saved ticket", ExTextColor.PERSONAL));
  }

  public void refresh() {
    this.close();
    new TicketInventory(user, this.type).open();
  }

  @Override
  public void onUserInventoryClick(UserInventoryClickEvent event) {
    User user = event.getUser();
    ExItemStack clickedItem = event.getClickedItem();

    event.setCancelled(true);

    if (clickedItem == null) {
      return;
    }

    if (clickedItem.equals(this.createTicket)) {
      return;
    }

    if (event.getSlot() < 6 * 9) {
      Integer slot = null;
      for (int i = 2; i < 7; i++) {
        if (user.getInventory().getItem(i) == null) {
          slot = i;
          break;
        }
      }

      if (slot != null) {
        user.setItem(slot, clickedItem);
      }
    }
  }

  @EventHandler
  public void onPlayerEditBook(PlayerEditBookEvent event) {
    User user = Server.getUser(event.getPlayer());

    if (this.invalid) {
      return;
    }

    if (!this.user.equals(user)) {
      return;
    }

    BookMeta newMeta = event.getNewBookMeta();

    ExItemStack oldItem = ExItemStack.getItemByMeta(newMeta);
    ExItemStack item = oldItem.cloneWithId();

    BookMeta meta = ((BookMeta) item.getItemMeta());

    event.setCancelled(true);
    event.setSigning(false);

    if (item == null) {
      return;
    }

    if (this.createTicket.equals(item)) {
      int id = this.createTicket(newMeta);
      user.sendPluginMessage(Plugin.SUPPORT,
          Component.text("Created ticket with id: ", ExTextColor.PERSONAL)
              .append(Component.text(id, ExTextColor.VALUE)));
      this.setCreationBook();
      this.user.updateInventory();
      TicketManager.getInstance().broadcastTicketCreation(id);
      return;
    }

    DbTicket ticket = this.ticketsByItemId.get(item.getId());

    if (!TicketManager.getInstance().getTicketInventoryByTicketLock(ticket.getId())
        .equals(this)) {
      this.user.sendPluginMessage(Plugin.SUPPORT,
          Component.text("Ticket could not be saved", ExTextColor.WARNING));
      return;
    }

    if (ticket == null || !ticket.exists()) {
      return;
    }

    Ticket editedTicket = new Ticket(ticket, ticket.getId(), ticket.getName(), ticket.getUuid(),
        ticket.getStatus(), ticket.getMessage(), ticket.getAnswer());

    if (user.hasPermission(this.statusPerm, Plugin.SUPPORT)) {
      Status.Ticket status = getStatusFromBook(newMeta);
      if (status != null) {
        if (status.equals(Status.Ticket.DELETE)) {
          Database.getSupport().removeTicket(ticket.getId());
          this.user.sendPluginMessage(Plugin.SUPPORT,
              Component.text("Deleted ticket ", ExTextColor.PERSONAL)
                  .append(Component.text(ticket.getId(), ExTextColor.VALUE)));
          Server.runTaskLaterSynchrony(this::refresh, 1, ExSupport.getPlugin());
          return;
        } else {
          editedTicket.setStatus(status);
        }
      } else {
        status = ticket.getStatus();
      }

      BaseComponent[] info =
          new BaseComponent[]{new TextComponent(
              ID + ticket.getId() + "\n" + NAME + ticket.getName() + "\n" +
                  "\n\n")};

      meta.spigot().setPage(1,
          (BaseComponent[]) ArrayUtils.addAll(info, createStatusButtons(status)));

    }

    if (user.hasPermission(this.editPerm, Plugin.SUPPORT)) {
      String message = getMessageFromBook(newMeta);
      editedTicket.setMessage(message);
      meta.spigot().setPage(2, new TextComponent(MESSAGE + message));
    }

    if (user.hasPermission(this.answerPerm, Plugin.SUPPORT)) {
      String answer = getAnswerFromBook(newMeta);
      editedTicket.setAnswer(answer);
      meta.spigot().setPage(3, new TextComponent(ANSWER + answer));
    }

    this.editedTicketsByTicketId.put(ticket.getId(), editedTicket);
    TicketManager.getInstance().saveTicket(ticket.getId());

    item.setItemMeta(meta);

    this.user.setItem(event.getSlot(), item);
    this.inventory.setItemStack(item);
    this.user.updateInventory();
  }

  @EventHandler
  public void onUserDrop(UserDropItemEvent e) {
    if (!e.getUser().equals(this.user)) {
      return;
    }

    e.setCancelled(true);

    ItemStack item = e.getItemStack();
    ExItemStack exItem = new ExItemStack(item);

    if (exItem.equals(createTicket) || exItem.equals(ticketInv) || exItem.equals(refresh)
        || exItem.equals(close)) {
      return;
    }

    Server.runTaskLaterSynchrony(() -> e.getUser().getInventory().remove(item), 1,
        ExSupport.getPlugin());
  }

  private int createTicket(BookMeta meta) {
    return Database.getSupport()
        .addTicket(this.user.getUniqueId().toString(), this.user.getName(),
            meta.getPage(1)).getId();
  }

  private void setCreationBook() {
    this.createTicket = this.createTicket.cloneWithId();

    BookMeta meta = ((BookMeta) this.createTicket.getItemMeta());

    meta.spigot().setPages(new BaseComponent[]{new TextComponent("Your message")},
        new BaseComponent[]{new TextComponent("§cUse only the first page")});

    this.createTicket.setItemMeta(meta);

    Server.getInventoryEventManager().addClickListener(this, this.createTicket);
  }

  @Override
  public @NotNull Inventory getInventory() {
    return this.inventory.getInventory();
  }

  public enum Type {
    OWN,
    OPEN,
    ADMIN,
    ALL
  }
}
