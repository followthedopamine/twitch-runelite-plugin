package net.runelite.client.plugins.feelsbadman;

import com.google.common.eventbus.Subscribe;
import javax.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.events.SetMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "feelsbadman",
        description = "Enables feelsbadman in chat.",
        tags = {"feels", "bad", "man"}
)
public class FeelsBadManPlugin extends Plugin {
    @Inject
    private Client client;

    @Subscribe
    public void onSetMessage(SetMessage setMessage) {
        if (client.getGameState() != GameState.LOADING && client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (setMessage.getValue().contains(":feelsbadman:")) {
            insertIcon(setMessage);
        }
    }

    private void insertIcon(final SetMessage message) {
        String icon = "/sprites/feelsbadman.png";
        String newMessage = message.getValue().replaceAll(":feelsbadman:", "<img=" + icon + ">");

        message.getMessageNode().setValue(newMessage);
        client.refreshChat();
    }
}
