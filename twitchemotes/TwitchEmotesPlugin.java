package net.runelite.client.plugins.twitchemotes;

import com.google.common.eventbus.Subscribe;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.SetMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@PluginDescriptor(
        name = "Twitch Emotes",
        description = "Enables twitch emotes in chat.",
        tags = {"twitch", "emotes", "chat"}
)
public class TwitchEmotesPlugin extends Plugin {
    @Inject
    private Client client;

    private static final String[] EMOTES_FILES =
            {
                    "feelsbadman.png",
                    "hahaa.png",
                    "kappa.png",
                    "monkas.png",
                    "omegalul.png",
                    "pogchamp.png",
                    "trihard.png",
                    "cmonbruh.png",
                    "lul.png",
                    "aismug.png",
                    "gachigasm.png",
                    "jebaited.png",
                    "poggers.png",
                    "biblethump.png"
            };

    private final String[] EMOTES_LIST =
            {
                    ":feelsbadman:",
                    ":hahaa:",
                    ":kappa:",
                    ":monkas:",
                    ":omegalul:",
                    ":pogchamp:",
                    ":trihard:",
                    ":cmonbruh:",
                    ":lul:",
                    ":aismug:",
                    ":gachigasm:",
                    ":jebaited:",
                    ":poggers:",
                    ":bibelthump:"
            };

    private final String[] EMOTES_IMG_TAGS = new String [EMOTES_FILES.length];
    private final BufferedImage[] EMOTES_IMG = new BufferedImage[EMOTES_FILES.length];
    private int modIconsLength;

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) throws IOException
    {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN
                && modIconsLength == 0)
        {
            System.out.println("Loading twitch emotes ...");
            loadTwitchEmotes();
        }
    }

    @Subscribe
    public void onSetMessage(SetMessage setMessage) {
        if (client.getGameState() != GameState.LOADING && client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        switch (setMessage.getType())
        {
            case PUBLIC:
            case PUBLIC_MOD:
            case CLANCHAT:
            case PRIVATE_MESSAGE_RECEIVED:
            case PRIVATE_MESSAGE_SENT:
                break;
            default:
                return;
        }

        if (containsEmote(setMessage.getValue())) {
            insertIcon(setMessage);
        }
    }

    private void insertIcon(final SetMessage message) {
        String newMessage = message.getValue();

        newMessage = StringUtils.replaceEach(newMessage, EMOTES_LIST, EMOTES_IMG_TAGS);
        message.getMessageNode().setValue(newMessage);
        client.refreshChat();
    }

    private boolean containsEmote(final String message) {
        for (String word : EMOTES_LIST) {
            if (message.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private void loadTwitchEmotes() throws IOException
    {
        final IndexedSprite[] modIcons = client.getModIcons();
        final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + EMOTES_FILES.length);
        int curPosition = newModIcons.length - EMOTES_FILES.length;

        for (int i = 0; i < EMOTES_FILES.length; i++, curPosition++)
        {
            EMOTES_IMG_TAGS[i] = "<img=" + Integer.toString(curPosition) + ">";

            File file = new File("runelite-client\\src\\main\\java\\net\\runelite\\client\\plugins\\twitchemotes\\resources\\" + EMOTES_FILES[i]);
            BufferedImage IMG = ImageIO.read(file.getAbsoluteFile());
            EMOTES_IMG[i] = rgbaToIndexedBufferedImage(IMG);
            newModIcons[curPosition] = createIndexedSprite(client, EMOTES_IMG[i]);
        }

        client.setModIcons(newModIcons);
        modIconsLength = newModIcons.length;
        System.out.println(" - " + (modIconsLength - modIcons.length)  + " icons loaded");
    }

    private static BufferedImage rgbaToIndexedBufferedImage(final BufferedImage src)
    {
        final BufferedImage indexedImage = new BufferedImage(
                src.getWidth(),
                src.getHeight(),
                BufferedImage.TYPE_BYTE_INDEXED);

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                indexedImage.setRGB(x, y, src.getRGB(x, y));
            }
        }
        
        return indexedImage;
    }

    private static IndexedSprite createIndexedSprite(final Client client, final BufferedImage bufferedImage)
    {
        final IndexColorModel indexedCM = (IndexColorModel) bufferedImage.getColorModel();

        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        final byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        final int[] palette = new int[indexedCM.getMapSize()];
        indexedCM.getRGBs(palette);

        final IndexedSprite newIndexedSprite = client.createIndexedSprite();
        newIndexedSprite.setPixels(pixels);
        newIndexedSprite.setPalette(palette);
        newIndexedSprite.setWidth(width);
        newIndexedSprite.setHeight(height);
        newIndexedSprite.setOriginalWidth(width);
        newIndexedSprite.setOriginalHeight(height);
        newIndexedSprite.setOffsetX(0);
        newIndexedSprite.setOffsetY(0);
        return newIndexedSprite;
    }
}