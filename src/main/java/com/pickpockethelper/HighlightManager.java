package com.pickpockethelper;

import net.runelite.api.NPC;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

/**
 * Simplifies interaction with Runelite's overlay service.
 * Handles highlighting NPCs.
 */
@Singleton
public class HighlightManager {

    private final PickpocketHelperConfig config;

    private final NpcOverlayService npcOverlayService;

    private final HashMap<NPC, HighlightedNpc> targets = new HashMap<>();

    @Inject
    public HighlightManager(PickpocketHelperConfig config, NpcOverlayService npcOverlayService, ClientThread clientThread) {
        this.config = config;
        this.npcOverlayService = npcOverlayService;
        npcOverlayService.registerHighlighter(targets::get);

        clientThread.invoke(this::refresh);
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

    public void removeTarget(NPC target) {
        targets.remove(target);
        this.refresh();
    }

    public void replaceTarget(NPC oldTarget, NPC newTarget){
        targets.remove(oldTarget);
        addTarget(newTarget);
    }

    public void clearTargets() {
        targets.clear();
        this.refresh();
    }

    /**
     * Refresh currently highlighted targets.
     * This needs to happen after, for example, the configuration changes.
     */
    public void refresh(){
        targets.keySet().forEach(npc -> {
            targets.replace(npc, highLightNpc(npc));
        });
        npcOverlayService.rebuild();
    }
}
