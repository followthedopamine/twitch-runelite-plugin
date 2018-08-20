package net.runelite.client.plugins.twitchemotes;

import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.awt.image.*;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.events.GameStateChanged;
import javax.imageio.ImageIO;
import net.runelite.api.events.SetMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

@PluginDescriptor(
        name = "Twitch Emotes",
        description = "Enables twitch emotes in chat.",
        tags = {"twitch", "emotes", "chat"}
)
public class TwitchEmotesPlugin extends Plugin {
    @Inject
    private Client client;

    private int modIconsLength;
    private static final String[] TWITCH_IMAGES =
            {
                    "feelsbadman.png",
                    "hahaa.png",
                    "kappa.png",
                    "monkas.png",
                    "omegalul.png",
                    "pogchamp.png",
                    "trihard.png"
            };

    private final String[] emoteList = {":feelsbadman:", ":hahaa:", ":kappa:", ":monkas:", ":omegalul:", ":pogchamp:", ":trihard:"};
    private final String[] imgList = {"<img=29>", "<img=30>", "<img=31>", "<img=32>", "<img=33>", "<img=34>", "<img=35>"};

    private static final Dimension TWITCH_IMAGE_DIMENSION = new Dimension(15, 15);
    private final BufferedImage[] twitchChatImages = new BufferedImage[TWITCH_IMAGES.length];

    @Subscribe
    public void onSetMessage(SetMessage setMessage) {
        if (client.getGameState() != GameState.LOADING && client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (containsEmote(setMessage.getValue())) {
            insertIcon(setMessage);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) throws IOException
    {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN
                && modIconsLength == 0)
        {
            System.out.println("Loading chat icons ...");
            loadClanChatIcons();
        }
    }

    private void insertIcon(final SetMessage message) {
        String newMessage = message.getValue();

        newMessage = StringUtils.replaceEach(newMessage, emoteList, imgList);
        message.getMessageNode().setValue(newMessage);
        client.refreshChat();
    }

    private void loadClanChatIcons() throws IOException
    {
        final IndexedSprite[] modIcons = client.getModIcons();
        final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + TWITCH_IMAGES.length);
        int curPosition = newModIcons.length - TWITCH_IMAGES.length;

        for (int i = 0; i < TWITCH_IMAGES.length; i++, curPosition++)
        {
            final String resource = TWITCH_IMAGES[i];

            BufferedImage twitch_emoticon = ImageIO.read(new File("C:\\Users\\Chris\\Desktop\\runelite-master\\runelite-client\\src\\main\\java\\net\\runelite\\client\\plugins\\twitchemotes\\resources\\" + resource));

            twitchChatImages[i] = rgbaToIndexedBufferedImage(twitch_emoticon);
            newModIcons[curPosition] = createIndexedSprite(client, twitchChatImages[i]);
        }

        client.setModIcons(newModIcons);
        modIconsLength = newModIcons.length;
        System.out.println(" - " + modIconsLength + " icons loaded");
    }

    private static BufferedImage rgbaToIndexedBufferedImage(final BufferedImage sourceBufferedImage)
    {
        final BufferedImage indexedImage = new BufferedImage(
                sourceBufferedImage.getWidth(),
                sourceBufferedImage.getHeight(),
                BufferedImage.TYPE_BYTE_INDEXED);

        final ColorModel cm = indexedImage.getColorModel();
        final IndexColorModel icm = (IndexColorModel) cm;

        final int size = icm.getMapSize();
        final byte[] reds = new byte[size];
        final byte[] greens = new byte[size];
        final byte[] blues = new byte[size];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);

        final WritableRaster raster = indexedImage.getRaster();
        final int pixel = raster.getSample(0, 0, 0);
        final IndexColorModel resultIcm = new IndexColorModel(8, size, reds, greens, blues, pixel);
        final BufferedImage resultIndexedImage = new BufferedImage(resultIcm, raster, sourceBufferedImage.isAlphaPremultiplied(), null);
        resultIndexedImage.getGraphics().drawImage(sourceBufferedImage, 0, 0, null);
        return resultIndexedImage;
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

    private boolean containsEmote(final String message) {
        for (String word : emoteList) {
            if (message.contains(word)) {
                return true;
            }
        }
        return false;
    }
}