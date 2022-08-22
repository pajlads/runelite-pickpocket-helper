package com.pickpockethelper.utility;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Collection of helper functions.
 */
public class Helper {
    /**
     * Determine if an NPC can be pickpocketed.
     *
     * @param target the NPC being checked.
     */
    public static boolean canPickpocket(NPC target) {
        final NPCComposition npcComposition = target.getComposition();
        final List<String> npcMenuActions = Arrays.asList(npcComposition.getActions());

        return npcMenuActions.contains("Pickpocket");
    }

    /**
     * Determine the amount of seconds passed since a past instant.
     *
     * @param instant the instant to compare against now.
     * @return amount of seconds.
     */
    public static int secondsSince(Instant instant) {
        return (int) Duration.between(instant, Instant.now()).getSeconds();
    }

    /**
     * Determine if the given actor is currently rendered in the client.
     *
     * @param actor the actor being checked.
     * @return indication if actor is rendered.
     */
    public static boolean isRendered(Actor actor) {
        if(actor == null) {
            return false;
        }

        return actor.getName() != null;
    }
}
