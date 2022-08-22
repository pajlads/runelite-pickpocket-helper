package com.pickpockethelper.ui;

import com.pickpockethelper.PickpocketHelperConfig;
import com.pickpockethelper.PickpocketHelperPlugin;
import com.pickpockethelper.entity.Session;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

/**
 * Used to display information about the last active splahser.
 * Includes their name and whether they are idle or splashing.
 */
public class SplasherOverlay extends OverlayPanel {

    private final PickpocketHelperConfig config;
    private final Session session;

    @Inject
    private SplasherOverlay(PickpocketHelperPlugin plugin, PickpocketHelperConfig config, Session session){
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.config = config;
        this.session = session;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if(session.getLastPickpocketAttempt() == null || !config.enableSplasherOverlay()) {
            return null;
        }

        Player player = session.getSplasher().getPlayer();
        if(player == null) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .color(Color.RED)
                    .text("NO Splasher")
                    .build());

            return super.render(graphics);
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Name:")
                .right(player.getName())
                .build());

        boolean isAttacking = session.getSplasher().isAttacking();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Status:")
                .right(isAttacking ? "Splashing" : "Idle")
                .rightColor(isAttacking ? Color.GREEN : Color.RED)
                .build());

        return super.render(graphics);
    }
}
