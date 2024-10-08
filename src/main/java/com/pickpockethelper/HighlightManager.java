package com.pickpockethelper;

import net.runelite.api.NPC;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Simplifies interaction with Runelite's overlay service.
 * Handles highlighting NPCs.
 */
@Singleton
public class HighlightManager {

    static final Set<String> CONFIG_DEPENDENCIES = Set.of("highLightTarget", "npcColor", "fillColor", "borderWidth", "outlineFeather");

    private final PickpocketHelperConfig config;

    private final NpcOverlayService npcOverlayService;

    private final Map<NPC, HighlightedNpc> targets = new IdentityHashMap<>();
    private final Function<NPC, HighlightedNpc> isTarget = targets::get;

    @Inject
    public HighlightManager(PickpocketHelperConfig config, NpcOverlayService npcOverlayService, ClientThread clientThread) {
        this.config = config;
        this.npcOverlayService = npcOverlayService;

        clientThread.invoke(this::refresh);
    }

    public void register() {
        npcOverlayService.registerHighlighter(isTarget);
    }

    public void unregister() {
        npcOverlayService.unregisterHighlighter(isTarget);
    }

    /**
     * Format an NPC as a highlightedNPC, so it can be highlighted.
     * @param npc the NPC to be highlighted.
     * @return an highlightedNpc object used by the highlight service.
     */
    private HighlightedNpc highLightNpc(NPC npc) {
        return HighlightedNpc.builder()
                .npc(npc)
                .highlightColor(config.highlightColor())
                .fillColor(config.fillColor())
                .hull(config.highLightTarget())
                .borderWidth((float) config.borderWidth())
                .outlineFeather(config.outlineFeather())
                .build();
    }

    public void addTarget(NPC target) {
        targets.put(target, highLightNpc(target));
        this.refresh();
    }

    public void clearTargets() {
        targets.clear();
        this.refresh();
    }

    /**
     * Refresh currently highlighted targets.
     * This needs to happen after, for example, the configuration changes.
     */
    public void refresh() {
        targets.replaceAll((npc, v) -> highLightNpc(npc));
        npcOverlayService.rebuild();
    }
}
