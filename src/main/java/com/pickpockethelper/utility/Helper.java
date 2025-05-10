package com.pickpockethelper.utility;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.gameval.NpcID;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Collection of helper functions.
 */
public class Helper {

    public static final Set<Integer> RANDOM_EVENT_NPC_IDS = Set.of(
            NpcID.MACRO_BEEKEEPER_INVITATION,
            NpcID.MACRO_COMBILOCK_PIRATE,
            NpcID.MACRO_COUNTCHECK_SURFACE, NpcID.MACRO_COUNTCHECK_UNDERWATER,
            NpcID.MACRO_JEKYLL, NpcID.MACRO_JEKYLL_UNDERWATER,
            NpcID.MACRO_DWARF,
            NpcID.PATTERN_INVITATION,
            NpcID.MACRO_EVIL_BOB_OUTSIDE, NpcID.MACRO_EVIL_BOB_PRISON,
            NpcID.PINBALL_INVITATION,
            NpcID.MACRO_FORESTER_INVITATION,
            NpcID.MACRO_FROG_NOHAT, NpcID.MACRO_FROG_CRIER, NpcID.MACRO_FROG_GENERIC, NpcID.MACRO_FROG_SULKING, NpcID.MACRO_FROG_NONCOMBAT,
            NpcID.MACRO_FROG_PRIN_HE, NpcID.MACRO_FROG_PRIN_SHE, NpcID.MACRO_FROG_PRIN_A, NpcID.MACRO_FROG_PRIN_B,
            NpcID.MACRO_GENI, NpcID.MACRO_GENI_UNDERWATER,
            NpcID.MACRO_GILES, NpcID.MACRO_GILES_UNDERWATER,
            NpcID.MACRO_GRAVEDIGGER_INVITATION,
            NpcID.MACRO_MILES, NpcID.MACRO_MILES_UNDERWATER,
            NpcID.MACRO_MYSTERIOUS_OLD_MAN, NpcID.MACRO_MYSTERIOUS_OLD_MAN_UNDERWATER,
            NpcID.MACRO_MAZE_INVITATION,
            NpcID.MACRO_MIME_INVITATION,
            NpcID.MACRO_NILES, NpcID.MACRO_NILES_UNDERWATER,
            NpcID.MACRO_PILLORY_GUARD,
            NpcID.GRAB_POSTMAN,
            NpcID.MACRO_MAGNESON_INVITATION,
            NpcID.MACRO_HIGHWAYMAN, NpcID.MACRO_HIGHWAYMAN_UNDERWATER,
            NpcID.MACRO_SANDWICH_LADY_NPC,
            NpcID.MACRO_DRILLDEMON_INVITATION
    );

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
