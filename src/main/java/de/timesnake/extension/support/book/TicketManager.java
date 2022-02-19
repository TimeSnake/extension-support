package de.timesnake.extension.support.book;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.channel.util.listener.ChannelHandler;
import de.timesnake.channel.util.listener.ChannelListener;
import de.timesnake.channel.util.listener.ListenerType;
import de.timesnake.channel.util.message.ChannelSupportMessage;
import de.timesnake.channel.util.message.MessageType;
import de.timesnake.extension.support.main.ExSupport;

import java.util.HashMap;
import java.util.Map;

public class TicketManager implements ChannelListener {

    private static TicketManager instance;

    public static TicketManager getInstance() {
        return instance;
    }

    private final Map<Integer, TicketInventory> ticketLocksInventoryByTicketId = new HashMap<>();

    public TicketManager() {
        instance = this;

        Server.getChannel().addListener(this);
    }

    public boolean requestTicketLock(TicketInventory ticketInventory, Integer ticketId) {
        TicketInventory invWithLoc = this.ticketLocksInventoryByTicketId.get(ticketId);
        if (invWithLoc != null && !invWithLoc.equals(ticketInventory)) {
            return false;
        }

        Server.getChannel().sendMessage(new ChannelSupportMessage<>(Server.getPort(), MessageType.Support.TICKET_LOCK, ticketId));

        this.ticketLocksInventoryByTicketId.put(ticketId, ticketInventory);

        return true;
    }

    public TicketInventory getTicketInventoryByTicketLock(Integer ticketId) {
        return this.ticketLocksInventoryByTicketId.get(ticketId);
    }

    @ChannelHandler(type = ListenerType.SUPPORT)
    public void onSupportMessage(ChannelSupportMessage<?> msg) {
        Integer id = (Integer) msg.getValue();

        TicketInventory ticketInventory = this.ticketLocksInventoryByTicketId.remove(id);

        if (ticketInventory == null) {
            return;
        }

        if (msg.getMessageType().equals(MessageType.Support.REJECT)) {
            Server.runTaskSynchrony(() -> ticketInventory.rejectTicket(id), ExSupport.getPlugin());
        } else if (msg.getMessageType().equals(MessageType.Support.ACCEPT)) {
            Server.runTaskSynchrony(() -> ticketInventory.acceptTicket(id), ExSupport.getPlugin());
        }
    }

    public void saveTicket(Integer ticketId) {
        Server.getChannel().sendMessage(new ChannelSupportMessage<>(Server.getPort(), MessageType.Support.SUBMIT, ticketId));
    }

    public void broadcastTicketCreation(Integer ticketId) {
        Server.getChannel().sendMessage(new ChannelSupportMessage<>(Server.getPort(), MessageType.Support.CREATION, ticketId));
    }
}
