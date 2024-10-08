package com.pickpockethelper.ui;

import com.pickpockethelper.PickpocketHelperConfig;
import com.pickpockethelper.PickpocketHelperPlugin;
import com.pickpockethelper.entity.Session;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

/**
 * Used to display the player's session stats.
 * Includes successful attempts total and per hour, and the number of failed and fail rate.
 */
public class StatisticOverlay extends OverlayPanel {

    private final PickpocketHelperConfig config;
    private final Session session;

    @Inject
    private StatisticOverlay(PickpocketHelperPlugin plugin, PickpocketHelperConfig config, Session session){
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.config = config;
        this.session = session;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if(session.getLastPickpocketAttempt() == null || session.getSessionStart() == null || !config.enableStatsOverlay()) {
            return null;
        }

		if(!session.isPickpocketing(Duration.ofSeconds(30))) {
			return null;
		}

        Integer failCount = session.getPickpocketFailCount();
        Integer successCount = session.getPickpocketSuccessCount();
        int totalCount = failCount + successCount;
        long secondsSinceSessionStart = Duration.between(session.getSessionStart(), Instant.now()).getSeconds();
        int perHourCount = (successCount < 1) ? 0 : Math.round(((float) successCount / (float) secondsSinceSessionStart) * 3600);

        int failRatio = (totalCount == 0) ? 0 : (failCount == 0) ? 0 : Math.round(((float) failCount / (float) totalCount) * 100);
		int successRatio = 100 - failRatio;

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Picked:")
                .right(String.valueOf(successCount))
                .build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Rate:")
			.right(successRatio +"%")
			.build());
        if (totalCount > 1) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Per hour:")
                    .right(String.valueOf(perHourCount))
                    .build());
        }

		panelComponent.getChildren().add(LineComponent.builder().build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Failed:")
			.right(String.valueOf(failCount))
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Rate:")
			.right(failRatio +"%")
			.build());

        return super.render(graphics);
    }
}
