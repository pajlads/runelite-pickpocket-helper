package com.pickpockethelper.ui;

import com.pickpockethelper.PickpocketHelperConfig;
import com.pickpockethelper.PickpocketHelperPlugin;
import com.pickpockethelper.entity.Session;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;

/**
 * Overlay used to display the pickpocketing status of the player.
 * Honestly, pretty useless since pickpocketing isn't afk.
 */
public class StatusOverlay extends OverlayPanel {

    private final PickpocketHelperConfig config;
    private final Session session;

    @Inject
    private StatusOverlay(PickpocketHelperPlugin plugin, PickpocketHelperConfig config, Session session){
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.config = config;
        this.session = session;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if(session.getLastPickpocketAttempt() == null || !config.enableStatusOverlay()) {
            return null;
        }

        if(session.isPickpocketing(Duration.ofSeconds(2))) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Pickpocketing")
                    .color(Color.GREEN)
                    .build());
        } else {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("NOT pickpocketing")
                    .color(Color.RED)
                    .build());
        }

        return super.render(graphics);
    }
}
