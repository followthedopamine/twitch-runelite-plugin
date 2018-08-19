
package net.runelite.client.plugins.feelsbadman;
import com.google.common.eventbus.Subscribe;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.SetMessage;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.ChatboxInputListener;
import net.runelite.client.events.ChatboxInput;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "feelsbadman",
	description = "Enables feelsbadman emoticon in game.",
	tags = {"feels", "bad", "man"}
)

public class FeelsBadmanPlugin extends Plugin implements ChatboxInputListener
{
	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatKeyboardListener chatKeyboardListener;


	@Override
	public void startUp()
	{
		keyManager.registerKeyListener(chatKeyboardListener);
		commandManager.register(this);
	}

	@Override
	public void shutDown()
	{
		keyManager.unregisterKeyListener(chatKeyboardListener);
		commandManager.unregister(this);
	}

	@Subscribe
	public void onSetMessage(SetMessage setMessage)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		String message = setMessage.getValue();

		if (message.toLowerCase().equals(":feelsbadman:"))
		{
			log.debug("feelsbadman detected");
			insertFeels(setMessage);
		}
	}

	private void insertFeels(SetMessage setMessage)
	{
		int feelsbadman_icon = ???

		String response = new ChatMessageBuilder()
			.append("<img=" + iconNumber + ">")
			.build();

		final MessageNode messageNode = setMessage.getMessageNode();
		chatMessageManager.update(messageNode);
		client.refreshChat();
	}
}
