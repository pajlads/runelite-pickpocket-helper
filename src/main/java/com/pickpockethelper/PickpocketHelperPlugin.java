package com.pickpockethelper;

import com.google.common.collect.ImmutableSet;
import com.pickpockethelper.entity.Session;
import com.pickpockethelper.ui.SplasherOverlay;
import com.pickpockethelper.ui.StatisticOverlay;
import com.pickpockethelper.ui.StatusOverlay;
import com.pickpockethelper.ui.TimerOverlay;
import com.google.inject.Provides;
import com.pickpockethelper.utility.AlertID;
import com.pickpockethelper.utility.Helper;
import com.pickpockethelper.utility.MessagePattern;
import com.pickpockethelper.utility.SoundEffectID;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.Hooks;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
        name = "Pickpocket Helper",
        description = "Adds quality of life improvements for pickpocketing.",
        tags = {"thieving", "pickpocketing", "ardy", "knights", "master", "farmer", "vyres"}
)
public class PickpocketHelperPlugin extends Plugin {
    private static final Map<Pattern, Runnable> messageTriggers = new LinkedHashMap<>();
    private static final Set<Pattern> blockedPatterns = ImmutableSet.of(
            Pattern.compile(MessagePattern.NO_SPACE_PATTERN),
            Pattern.compile(MessagePattern.EMPTY_POUCHES_PATTERN),
            Pattern.compile(MessagePattern.CANT_REACH_PATTERN)
    );
    private static final Set<Integer> splashAnimations = Set.of(
            AnimationID.HUMAN_CASTIBANBLAST,
            AnimationID.HUMAN_CASTSTRIKE_STAFF_WALKMERGE,
            AnimationID.HUMAN_CASTWAVE_STAFF_WALKMERGE,
            AnimationID.HUMAN_CAST_SURGE_WALKMERGE
    );
    private static final Set<Integer> rogueEquipmentIds = Set.of(
            ItemID.ROGUESDEN_BOOTS,
            ItemID.ROGUESDEN_GLOVES,
            ItemID.ROGUESDEN_HELM,
            ItemID.ROGUESDEN_BODY,
            ItemID.ROGUESDEN_LEGS
    );

    @Inject
    private PickpocketHelperConfig config;
    @Inject
    private Client client;
    @Inject
    private Hooks hooks;
    @Inject
    private HighlightManager highlightManager;
    @Inject
    private FeedbackManager feedbackManager;
    @Inject
    private AlertManager alertManager;
    @Inject
    private AudioManager audioManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private StatisticOverlay statisticOverlay;
    @Inject
    private StatusOverlay statusOverlay;
    @Inject
    private SplasherOverlay splasherOverlay;
    @Inject
    private TimerOverlay timerOverlay;
    @Inject
    private Session session;

    @Provides
    PickpocketHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PickpocketHelperConfig.class);
    }

    /**
     * Setup listeners for changing session values.
     */
    private void setupListeners() {
        session.getTarget().addNpcListener(this::onLastPickpocketTargetChanged);
        session.getSplasher().addIsAttackingListener(this::onSplasherIsAttackingChanged);
        session.getSplasher().addPlayerListener(this::onSplasherChanged);
        session.setupListeners();
    }

    /**
     * Setup methods that will trigger based on incoming chat messages.
     */
    private void setupMessageTriggers() {
        registerMessageTrigger(MessagePattern.DODGY_NECKLACE_BREAK_PATTERN, this::onDodgyNecklaceBreak);
        registerMessageTrigger(MessagePattern.STUN_PATTERN, this::onStun);
        registerMessageTrigger(MessagePattern.SHADOW_VEIL_FADE_PATTERN, this::onShadowVeilFade);
        registerMessageTrigger(MessagePattern.PICKPOCKET_SUCCEED_PATTERN, this::onPickpocketSuccess);
        registerMessageTrigger(MessagePattern.PICKPOCKET_FAIL_PATTERN, this::onPickpocketFail);
        registerMessageTrigger(MessagePattern.PICKPOCKET_ROGUE_EQUIPMENT_PATTERN, this::onRogueEquipmentProc);
        registerMessageTrigger(MessagePattern.POUCHES_FULL_PATTERN, this::onPouchesFull);
        registerMessageTrigger(MessagePattern.INVENTORY_FULL_PATTERN, this::onInventoryFull);
        registerMessageTrigger(MessagePattern.NO_SPACE_PATTERN, this::onInventoryFull);
        registerMessageTrigger(MessagePattern.GLOVES_OF_SILENCE_BREAKING_PATTERN, this::onGlovesBreak);
    }

    private void registerMessageTrigger(String regex, Runnable action) {
        messageTriggers.put(Pattern.compile(regex), action);
    }

    /**
     * Determines if an entity should be rendered before it is.
     * If enabled by the player, while pickpocketing this hides everyone except:
     *  - the player and their pet
     *  - their pickpocket target
     *  - the splasher
     *  - friends and clan mates
     */
    private boolean shouldRenderEntity(Renderable renderable, boolean drawingUI) {
        if(!config.enableHideOthers() || !session.isPickpocketing(Duration.ofSeconds(10))) {
            return true;
        }

        if(!(renderable instanceof Actor)) {
            return true;
        }

        Actor actor = (Actor) renderable;

        Player localPlayer = client.getLocalPlayer();
        if (session.isTarget(actor) || actor.equals(localPlayer) || actor.equals(session.getSplasher().getPlayer())) {
            return true;
        }

        if (actor instanceof NPC) {
            if (actor.getInteracting() == localPlayer) {
                NPC npc = (NPC) actor;
                if (Helper.RANDOM_EVENT_NPC_IDS.contains(npc.getId())) {
                    return config.showOwnRandomEvents();
                } else {
                    return npc.getComposition().isFollower();
                }
            }

            return false;
        }

        Player player = (Player) actor;
        return player.isFriend() || player.isClanMember();
    }

    @Override
    protected void startUp() {
        setupListeners();
        setupMessageTriggers();
        overlayManager.add(statusOverlay);
        overlayManager.add(splasherOverlay);
        overlayManager.add(statisticOverlay);
        overlayManager.add(timerOverlay);
        highlightManager.register();
        hooks.registerRenderableDrawListener(this::shouldRenderEntity);
    }

    @Override
    protected void shutDown() {
        hooks.unregisterRenderableDrawListener(this::shouldRenderEntity);
        highlightManager.clearTargets();
        highlightManager.unregister();
        overlayManager.remove(statusOverlay);
        overlayManager.remove(splasherOverlay);
        overlayManager.remove(statisticOverlay);
        overlayManager.remove(timerOverlay);
        messageTriggers.clear();
        session.clear();
    }

    @Override
    public void resetConfiguration() {
        overlayManager.resetOverlay(statisticOverlay);
        overlayManager.resetOverlay(statusOverlay);
        overlayManager.resetOverlay(splasherOverlay);
        overlayManager.resetOverlay(timerOverlay);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged change) {
        switch (change.getGameState()) {
            case LOGIN_SCREEN:
                session.reset();
                // intentional fall-through
            case CONNECTION_LOST:
            case HOPPING:
                session.getSplasher().reset();
                session.getTarget().reset();
                highlightManager.clearTargets();
                break;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (!"pickpockethelper".equals(configChanged.getGroup()) || configChanged.getNewValue() == null) {
            return;
        }
        if (HighlightManager.CONFIG_DEPENDENCIES.contains(configChanged.getKey())) {
            highlightManager.refresh();
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage chatMessage) {
        checkAndExecuteChatMessageTriggers(chatMessage);
    }

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event) {
		if (!config.enableBlockSpam() || !"chatFilterCheck".equals(event.getEventName())) {
			return;
		}

		String message = (String) client.getObjectStack()[client.getObjectStackSize() - 1];
		String content = Text.removeTags(message);

        for (Pattern pattern : blockedPatterns) {
            if (pattern.matcher(content).find()) {
                client.getIntStack()[client.getIntStackSize() - 3] = 0;
                break;
            }
        }
    }

	@Subscribe
    private void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        checkAndNotifyLowHitpoints(hitsplatApplied.getHitsplat());
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        checkAndNotifyPlayerIdle();
        checkAndUpdateTargetLocation();
        checkAndNotifyTargetDespawn();
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        swapLeftClickPickpocket();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged change) {
        checkAndUpdateSplasher(change.getActor());
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        checkAndUpdateTargetFromCache(npcSpawned.getNpc());
    }

    @Subscribe
    public void onSoundEffectPlayed(SoundEffectPlayed soundEffect) {
        checkAndMuteSoundEffect(soundEffect);
    }

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
		checkAndUpdatePickpocketAttempt(menuOptionClicked);
	}

    private void checkAndUpdatePickpocketAttempt(MenuOptionClicked menuOptionClicked) {
		if (!menuOptionClicked.getMenuOption().equalsIgnoreCase("Pickpocket")) {
			return;
		}

		session.getTarget().setNpc(menuOptionClicked.getMenuEntry().getNpc());
		session.updateLastPickpocketAttempt();
		session.getSplasher().updateIsAttacking();
    }

    private void onPickpocketSuccess() {
        session.updateLastPickpocketSuccess();
        session.increasePickpocketSuccessCount();
        checkAndNotifyRogueEquipment();
    }

    private void onPickpocketFail() {
        session.increasePickpocketFailCount();
    }

    /**
     * The player can get stunned after a failed pickpocket.
     * The stun is guaranteed, unless they are wearing a dodgy necklace.
     */
    private void onStun() {
        session.updateLastStun();
    }

    /**
     * The player attempts to pickpocket with a full inventory.
     */
    private void onInventoryFull() {
        notifyNoSpace();
    }

    /**
     * The player attempts to pickpocket with a full stack of pouches.
     */
    private void onPouchesFull() {
        notifyNoSpace();
    }

    private void onLastPickpocketTargetChanged() {
        updatePickpocketTargetHighlight();
    }

    private void onSplasherChanged() {
        checkAndNotifySplasherFound();
    }

    private void onSplasherIsAttackingChanged() {
        checkAndNotifySplasherIdle();
    }

    private void onShadowVeilFade() {
        notifyShadowVeilFade();
    }

	private void onGlovesBreak() {
        notifyGlovesBreak();
	}

    private void onDodgyNecklaceBreak() {
        notifyNecklaceBreak();
    }

    /**
     * When pickpocketing with a piece of rogue equipment, there is a 20% chance it procs and gives double loot.
     * Wearing the full set will guarantee this effect to proc.
     */
    private void onRogueEquipmentProc() {
        session.updateLastRogueEquipmentProc();
    }

    /**
     * Check if there is a splasher and, if so, notify the player of the splasher.
     * Does nothing if no splasher is actually present.
     */
    private void checkAndNotifySplasherFound() {
        if (session.getSplasher().getPlayer() == null) {
            return;
        }

        String message = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Splasher found: ")
                .append(ChatColorType.HIGHLIGHT)
                .append(session.getSplasher().getPlayer().getName())
                .build();

        feedbackManager.sendChatMessage(message);
    }

    /**
     * Update the current pickpocketed target being highlighted.
     */
    private void updatePickpocketTargetHighlight() {
        highlightManager.clearTargets();
        highlightManager.addTarget(session.getTarget().getNpc());
    }

    /**
     * Checks if the player is idle after having pickpocketed and, if so, notifies the player.
     * Takes delay configuration into account set by player. If the delay is 0, the notification is disabled entirely.
     * If no pickpocket attempt has been made since last notification, nothing will de done.
     */
    private void checkAndNotifyPlayerIdle() {
        Duration delay = Duration.ofSeconds(config.getInactiveNotificationDelay());
        if (delay.isZero() || delay.isNegative() || session.getLastPlayerIdleNotify() != null) {
            return;
        }

        if (session.getLastPickpocketAttempt() != null && !session.isPickpocketing(delay)) {
            session.updateLastPlayerIdleNotify();
            alertManager.sendAlert(AlertID.PLAYER_IDLE, false);
        }
    }

    /**
     * Check if the splasher is idle and, if so, notfies the player.
     */
    private void checkAndNotifySplasherIdle() {
        if (!config.enableIdleSplasherNotification() || session.getSplasher().getLastIdleNotify() != null || session.getSplasher().getPlayer() == null || session.getSplasher().isAttacking()) {
            return;
        }

        session.getSplasher().updateLastIdleNotify();
        alertManager.sendAlert(AlertID.SPLASHER_IDLE, true);
    }

    /**
     * Check if the player is pickpocketing and, if so, if they are wearing the complete rogue outfit.
     * To prevent checking too often, equipment is only checked if no rogue equipment proc has occurred for 3 seconds.
     */
    private void checkAndNotifyRogueEquipment() {
        if (!config.enableRogueEquipmentNotification()) {
            return;
        }

        if (session.getLastPickpocketSuccess() != null && session.getLastRogueEquipmentProc() != null) {
            Duration timeSinceLastProc = Duration.between(session.getLastRogueEquipmentProc(), Instant.now());
            if (timeSinceLastProc.getSeconds() < 3) {
                return;
            }
        }

        int[] equippedGearIds = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();
        int rogueCount = 0;
        for (int equipmentId : equippedGearIds) {
            // Only values above 2048 are items.
            if (equipmentId < PlayerComposition.ITEM_OFFSET) {
                continue;
            }

            int itemId = equipmentId - PlayerComposition.ITEM_OFFSET;
            if (rogueEquipmentIds.contains(itemId)) {
                rogueCount++;
            }
        }

        if (rogueCount < rogueEquipmentIds.size()) {
            alertManager.sendAlert(AlertID.ROGUE_SET_INCOMPLETE, true);
        }
    }

    /**
     * Notify the player that they try to pickpocket without having space left for the pouch.
     */
    private void notifyNoSpace() {
        if (!config.enableNoSpaceNotification()) {
            return;
        }

        alertManager.sendAlert(AlertID.NO_INVENTORY_SPACE, false);
    }

	/**
	 * Notify the player that their gloves of silence are about to break.
	 */
	private void notifyGlovesBreak() {
		if(!config.enableGlovesNotification()) {
			return;
		}

		alertManager.sendAlert(AlertID.GLOVES_BREAK, false);
	}
    /**
     * Notify the player that their dodgy necklace broke.
     */
    private void notifyNecklaceBreak() {
        if (!config.enableNecklaceNotification()) {
            return;
        }

        alertManager.sendAlert(AlertID.DODGY_BREAK, false);
    }

    /**
     * Notify the player that their Shadow Veil spell has faded.
     */
    private void notifyShadowVeilFade() {
        if (!config.enableShadowVeilNotification()) {
            return;
        }

        alertManager.sendAlert(AlertID.SHADOW_VEIL_FADED, false);
    }

    /**
     * Check if the player's hitpoints have subceeded their set threshold and,  if so, notify them.
     * If the threshold is set to 0, the notification is disabled.
     *
     * @param hitsplat the hitsplat applied that triggers the check.
     */
    private void checkAndNotifyLowHitpoints(Hitsplat hitsplat) {
        if (client.getGameState() != GameState.LOGGED_IN || config.getHitpointsThreshold() < 1 || !hitsplat.isMine()) {
            return;
        }

        int hitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS) - hitsplat.getAmount();
        if (hitpoints <= config.getHitpointsThreshold()) {
            alertManager.sendAlert(AlertID.HITPOINTS_LOW, true);
        }
    }

    /**
     * Check if the player's target is about to despawn and, if so, notify them.
     * If the threshold is set to 0, the notification is disabled.
     */
    private void checkAndNotifyTargetDespawn() {
        int targetDespawnThreshold = config.getTargetDespawnThreshold();
        if (client.getGameState() != GameState.LOGGED_IN || session.getTarget().getLastDespawnNotify() != null || targetDespawnThreshold == 0 || session.getTarget().getNpc() == null || !session.getTarget().isRendered()) {
            return;
        }

        int secondsBeforeDespawn = session.getTarget().getSecondsBeforeDespawn();
        if (secondsBeforeDespawn <= targetDespawnThreshold && secondsBeforeDespawn > 0) {
            session.getTarget().updateLastDespawnNotify();
            alertManager.sendAlert(AlertID.TARGET_DESPAWN, true);
        }
    }

    /**
     * Check if an incoming chat message should trigger an action and, if so, trigger the action.
     * The triggers only work for game- and spam massages.
     *
     * @param message the incoming chat message.
     */
    private void checkAndExecuteChatMessageTriggers(ChatMessage message) {
        if (message.getType() != ChatMessageType.GAMEMESSAGE && message.getType() != ChatMessageType.SPAM) {
            return;
        }

        String content = Text.removeTags(message.getMessage());
        for (var entry : messageTriggers.entrySet()) {
            if (entry.getKey().matcher(content).find()) {
                entry.getValue().run();
                break;
            }
        }
    }

    /**
     * Check if the provided player is splashing the target the local player is pickpocketing.
     * If so, either store this new splasher or process the new attack of an existing splasher.
     *
     * @param target the target being verified as splasher.
     */
    private void checkAndUpdateSplasher(Actor target) {
        if (client.getGameState() != GameState.LOGGED_IN || !(target instanceof Player)) {
            return;
        }

        Player player = (Player) target;

        if (player.getInteracting() == null || !session.isTarget(player.getInteracting()) || !splashAnimations.contains(player.getAnimation())) {
            return;
        }

        if (player == session.getSplasher().getPlayer()) {
            session.getSplasher().updateLastAttack();
        } else {
            session.getSplasher().updatePlayer(player);
        }
    }

    /**
     * Check if the provided sound effect is muted by the player and, if so, interrupt it from being played.
	 * Only interrupts sounds when the session is active.
     *
     * @param soundEffect the to-be-played sound effect to check and mute.
     */
    private void checkAndMuteSoundEffect(SoundEffectPlayed soundEffect) {
		if (!session.isActive()) {
			return;
		}

        switch (soundEffect.getSoundId()) {
            // Block fail sound
            case com.pickpockethelper.utility.SoundEffectID.PICKPOCKET_FAIL:
            // Block damage sounds
            case com.pickpockethelper.utility.SoundEffectID.HUMAN_TAKE_DAMAGE_ONE:
            case com.pickpockethelper.utility.SoundEffectID.HUMAN_TAKE_DAMAGE_TWO:
            case com.pickpockethelper.utility.SoundEffectID.HUMAN_TAKE_DAMAGE_THREE:
            case com.pickpockethelper.utility.SoundEffectID.HUMAN_TAKE_DAMAGE_FOUR:
            case com.pickpockethelper.utility.SoundEffectID.HUMAN_TAKE_DAMAGE_SIX:
            case com.pickpockethelper.utility.SoundEffectID.HUMAN_TAKE_DAMAGE_SEVEN:
			case com.pickpockethelper.utility.SoundEffectID.FEMALE_TAKE_DAMAGE_ONE:
			case com.pickpockethelper.utility.SoundEffectID.FEMALE_TAKE_DAMAGE_TWO:
                if (config.muteFailSounds()) {
                    soundEffect.consume();
                }
                break;
            case com.pickpockethelper.utility.SoundEffectID.PICKPOCKET_SUCCEED:
                if (config.muteSuccessSounds()) {
                    soundEffect.consume();
                }
                break;
            case com.pickpockethelper.utility.SoundEffectID.POUCHES_EMPTY:
				if (config.muteEmptyPouchSound()) {
					soundEffect.consume();
				}
				break;
            case com.pickpockethelper.utility.SoundEffectID.NO_SPACE:
                if (config.muteNoSpaceSound()) {
                    soundEffect.consume();
                }
                break;
            case com.pickpockethelper.utility.SoundEffectID.SHADOW_VEIL_ACTIVE:
				if(config.muteVeilActivateSound()) {
					soundEffect.consume();
				}
				break;
            case SoundEffectID.SHADOW_VEIL_FADE:
                if (config.muteVeilFadeSound()) {
                    soundEffect.consume();
                }
                break;
        }
    }

    /**
     * Check if a spawned NPC is the target being pickpocketed using the cache and, if so, update the target session info.
     * This needs to happen after the target re-renders - like walking away and bank into range, or the target dying and respawning.
     *
     * @param npc the spawned npc being checked.
     */
    private void checkAndUpdateTargetFromCache(NPC npc) {
        if (session.getTarget().getNpc() == null || npc.getName() == null) {
            return;
        }

        if (npc.getIndex() != session.getTarget().getNpc().getIndex()) {
            return;
        }

        session.getTarget().setNpc(npc);
    }

    /**
     * Check if the target being pickpocketed has moved since last check and, if so, store the new location.
     */
    private void checkAndUpdateTargetLocation() {
        if (session.getTarget().getNpc() == null) {
            return;
        }

        if (session.getTarget().getNpc().getWorldLocation().equals(session.getTarget().getLastLocation())) {
            return;
        }

        session.getTarget().updateLocation(session.getTarget().getNpc().getWorldLocation());
    }

    private void swapLeftClickPickpocket() {
        if (!config.enableLeftClickPickpocket() || client.getGameState().getState() != GameState.LOGGED_IN.getState() || client.isMenuOpen()) {
            return;
        }
        MenuEntry[] entries = client.getMenuEntries();
        HashMap<MenuEntry, Integer> swaps = new HashMap<>();
        HashMap<Integer, Map.Entry<MenuEntry, Integer>> defaultLeftClicks = new HashMap<>();

        for(int index = entries.length - 1; index >= 0; index--){
            MenuEntry entry = entries[index];

            if(entry.getNpc() == null) {
                continue;
            }

            if(entry.getOption().equalsIgnoreCase("pickpocket")) {
                Map.Entry<MenuEntry, Integer> defaultLeftClickOption = defaultLeftClicks.get(entry.getNpc().getIndex());

                if (defaultLeftClickOption != null) {
                    swaps.put(entry, defaultLeftClickOption.getValue());
                    swaps.put(defaultLeftClickOption.getKey(), index);
                }
            } else if(
                    (
                            entry.getType() == MenuAction.NPC_FIRST_OPTION
                                    || entry.getType() == MenuAction.NPC_SECOND_OPTION
                                    || entry.getType() == MenuAction.NPC_THIRD_OPTION
                                    || entry.getType() == MenuAction.NPC_FOURTH_OPTION
                    )
                            && !defaultLeftClicks.containsKey(entry.getIdentifier())
            ) {
                defaultLeftClicks.put(entry.getNpc().getIndex(), new AbstractMap.SimpleEntry<>(entry, index));
            }
        }

        if (swaps.isEmpty()) {
            return;
        }

        swaps.forEach((entry, index) -> {
            entries[index] = entry;
        });

        try {
            client.setMenuEntries(entries);
        } catch (AssertionError er) {
            log.debug(er.getMessage());
        };
    }
}
