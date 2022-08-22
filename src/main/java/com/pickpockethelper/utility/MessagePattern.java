package com.pickpockethelper.utility;

/**
 * Message patterns for chat message triggers.
 */
public final class MessagePattern {
    public static final String DODGY_NECKLACE_BREAK_PATTERN = "Your dodgy necklace protects you\\..*It then crumbles to dust\\.";
    public static final String DODGY_NECKLACE_PROTECT_PATTERN = "Your dodgy necklace protects you\\. It has 7 charges left.";
    public static final String PICKPOCKET_ATTEMPT_PATTERN = "You attempt to pick .*\\ pocket\\.";
    public static final String PICKPOCKET_SUCCEED_PATTERN = "You pick .*\\ pocket\\.";
    public static final String PICKPOCKET_FAIL_PATTERN = "You fail to pick .*\\ pocket\\.";
    public static final String PICKPOCKET_ROGUE_EQUIPMENT_PATTERN = "Your rogue clothing allows you to steal twice as much loot!";
    public static final String STUN_PATTERN = "You've been stunned!";
    public static final String POUCHES_FULL_PATTERN = "You need to empty your coin pouches before you can continue pickpocketing\\.";
    public static final String INVENTORY_FULL_PATTERN = "You don't have enough inventory space to do that\\.";
    public static final String SHADOW_VEIL_ACTIVATE_PATTERN = "Your thieving abilities have been enhanced\\.";
    public static final String SHADOW_VEIL_FADE_PATTERN = "Your Shadow Veil has faded away\\.";
}
