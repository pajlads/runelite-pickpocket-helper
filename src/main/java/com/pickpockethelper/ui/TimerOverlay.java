package com.pickpockethelper.ui;

import com.pickpockethelper.*;
import com.pickpockethelper.entity.Session;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

/**
 * Overlay used to display a de-spawn timer above the player's target.
 */
public class TimerOverlay extends OverlayPanel {

    private final Client client;
    private final PickpocketHelperConfig config;
    private final Session session;

    @Inject
    private TimerOverlay(Client client, PickpocketHelperPlugin plugin, PickpocketHelperConfig config, Session session){
        super(plugin);
        this.client = client;
        this.config = config;
        this.session = session;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        NPC target = session.getTarget().getNpc();
        if(target == null || !config.enableDespawnTimer() || client.isInInstancedRegion() || !session.getTarget().isRendered()) {
            return null;
        }

        int displayedAmount = session.getTarget().getSecondsBeforeDespawn();

        if(displayedAmount < 0) {
            displayedAmount = 0;
        }

        Point timerLocation = target.getCanvasTextLocation(graphics, String.valueOf(displayedAmount), target.getLogicalHeight() + 25);
        Color textColor = (displayedAmount > 30) ? Color.white : Color.RED;

        OverlayUtil.renderTextLocation(graphics, timerLocation, String.valueOf(displayedAmount), textColor);

        return null;
    }
}
