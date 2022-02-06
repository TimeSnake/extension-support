package de.timesnake.extension.support.book;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.channel.api.message.ChannelSupportMessage;
import de.timesnake.channel.listener.ChannelSupportListener;
import de.timesnake.extension.support.main.ExSupport;

import java.util.HashMap;
import java.util.Map;

public class TicketManager implements ChannelSupportListener {

    private static TicketManager instance;

    public static TicketManager getInstance() {
        return instance;
    }

    private final Map<Integer, TicketInventory> ticketLocksInventoryByTicketId = new HashMap<>();

    public TicketManager() {
        instance = this;

        Server.getChannel().addSupportListener(this);
    }

    public boolean requestTicketLock(TicketInventory ticketInventory, Integer ticketId) {
        TicketInventory invWithLoc = this.ticketLocksInventoryByTicketId.get(ticketId);
        if (invWithLoc != null && !invWithLoc.equals(ticketInventory)) {
            return false;
        }

        Server.getChannel().sendMessage(ChannelSupportMessage.getTicketLockMessage(Server.getPort(), ticketId));

        this.ticketLocksInventoryByTicketId.put(ticketId, ticketInventory);

        return true;
    }

    public TicketInventory getTicketInventoryByTicketLock(Integer ticketId) {
        return this.ticketLocksInventoryByTicketId.get(ticketId);
    }

    @Override
    public void onSupportMessage(ChannelSupportMessage msg) {
        Integer id = Integer.parseInt(msg.getValue());

        TicketInventory ticketInventory = this.ticketLocksInventoryByTicketId.remove(id);

        if (ticketInventory == null) {
            return;
        }

        if (msg.getType().equals(ChannelSupportMessage.MessageType.REJECT)) {
            Server.runTaskSynchrony(() -> ticketInventory.rejectTicket(id), ExSupport.getPlugin());
        } else if (msg.getType().equals(ChannelSupportMessage.MessageType.ACCEPT)) {
            Server.runTaskSynchrony(() -> ticketInventory.acceptTicket(id), ExSupport.getPlugin());
        }
    }

    public void saveTicket(Integer ticketId) {
        Server.getChannel().sendMessage(ChannelSupportMessage.getTicketSubmitMessage(Server.getPort(), ticketId));
    }

    public void broadcastTicketCreation(Integer ticketId) {
        Server.getChannel().sendMessage(ChannelSupportMessage.getTicketCreationMessage(Server.getPort(), ticketId));
    }
}
