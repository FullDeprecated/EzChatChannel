package me.davamu.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.extern.java.Log;
import me.davamu.modules.FileModule;
import me.davamu.util.PlayerUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Log
public class NewChatFormatHandler implements ChatFormatHandler<AsyncChatEvent>{
    @Deprecated
    private final FileModule fileModule;

    public NewChatFormatHandler(FileModule fileModule) {
        this.fileModule = fileModule;
    }

    @Override
    public void accept(AsyncChatEvent event) {
        // global channel
        if (!PlayerUtil.localChannel.contains(event.getPlayer())) {
            log.fine("The player is using the global channel");
            return;
        }
        log.fine("The player is using the local channel");

        // distance
        final int distance = fileModule.getConfigSerializable().getLocal();
        log.fine("Distance set in the configuration: " + distance);

        // location from message
        Location mainLoc = event.getPlayer().getLocation();

        // new viewers
        Set<Audience> audiences = new HashSet<>();

        // for each of them
        event.viewers().forEach(audience -> {
            if (audience instanceof Player player) {
                Location receiverLoc = player.getLocation();
                if (mainLoc.distance(receiverLoc) <= distance) {
                    audiences.add(audience);
                }
            } else {
                // console and other cases that should not be relevant
                audiences.add(audience);
            }
        });

        // delete viewers
        event.viewers().clear();
        log.fine("Removing default viewers: " + event.viewers());

        // we add the viewers we want to send the message to
        event.viewers().addAll(audiences);
        log.fine("Adding new viewers: " + event.viewers());

    }

}
