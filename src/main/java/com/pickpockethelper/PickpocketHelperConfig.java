package com.pickpockethelper;

import com.pickpockethelper.utility.AlertType;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("pickpockethelper")
public interface PickpocketHelperConfig extends Config
{
	@ConfigSection(
			name = "Sounds",
			description = "",
			position = 0
	)
	String soundSection = "soundSection";

	@ConfigItem(
		keyName = "muteFailSounds",
		name = "Mute Fail Sounds",
		description = "Mute sounds caused by a failed pickpocket attempt.",
		position = 0,
		section = soundSection
	)
	default boolean muteFailSounds(){
		return true;
	}

	@ConfigItem(
			keyName = "muteSuccessSound",
			name = "Mute Success Sounds",
			description = "Mute sounds caused by a successful pickpocket attempt.",
			position = 1,
			section = soundSection
	)
	default boolean muteSuccessSounds(){
		return true;
	}

	@ConfigItem(
		keyName = "mutePouchSounds",
		name = "Mute Pouch Sounds",
		description = "Mute sounds caused by a having- or emptying a full stack of pouches.",
			position = 2,
		section = soundSection
	)
	default boolean mutePouchSounds() { return false; }

	@ConfigItem(
			keyName = "muteShadowVeilSounds",
			name = "Mute Shadow Veil Sounds",
			description = "Mute sounds caused by Shadow Veil when it activates or fades.",
			position = 3,
			section = soundSection
	)
	default boolean muteShadowVeilSounds() { return false; }

	@ConfigSection(
			name = "Alerts",
			description = "",
			position = 1
	)
	String alertSection = "alertSection";

	@ConfigItem(
			keyName = "alertType",
			name = "Type",
			description = "How you will be alerted. Either chat messages, notifications, or speech audio.",
			position = 0,
			section = alertSection
	)
	default AlertType getAlertType() {
		return AlertType.NOTIFICATION;
	}

	@ConfigItem(
			keyName = "inactiveDelay",
			name = "Inactive Delay",
			description = "The delay for being notified after not having picked any pockets. A value of 0 will disable the notification.",
			position = 1,
			section = alertSection
	)
	@Units(Units.SECONDS)
	default int getInactiveNotificationDelay()
	{
		return 30;
	}

	@ConfigItem(
			keyName = "targetDespawnDelay",
			name = "Despawn Delay",
			description = "The delay for being notified when your target is about to despawn due to not moving. A value of 0 will disable the notification,",
			position = 2,
			section = alertSection
	)
	@Units(Units.SECONDS)
	default int getTargetDespawnDelay()
	{
		return 30;
	}

	@ConfigItem(
			keyName = "hpThreshold",
			name = "HP Threshold",
			description = "The hitpoint threshold for being notified. A value of 0 will disable the notification.",
			position = 3,
			section = alertSection
	)
	default int getHitpointsThreshold()
	{
		return 12;
	}

	@ConfigItem(
			keyName = "enableIdleSplasherNotification",
			name = "Idle Splasher",
			description = "Enable being notified when, if a splasher is present, the target your are pickpocketing is no longer being splashed. Stopping pickpocketing will allow the splasher to restart attacking.",
			position = 4,
			section = alertSection
	)
	default boolean enableIdleSplasherNotification() {
		return true;
	}

	@ConfigItem(
			keyName = "enableNecklaceNotification",
			name = "Dodgly Necklace Breaking",
			description = "Enable being notified when your dodgy necklace breaks.",
			position = 5,
			section = alertSection
	)
	default boolean enableNecklaceNotification() {
		return true;
	}

	@ConfigItem(
			keyName = "enableRogueEquipmentNotification",
			name = "Missing Rogue Equipment",
			description = "Enable being notified when pickpocketing while missing one or more pieces of rogue equipment.",
			position = 6,
			section = alertSection
	)
	default boolean enableRogueEquipmentNotification() {
		return true;
	}

	@ConfigItem(
			keyName = "enableShadowVeilNotification",
			name = "Shadow Veil Fading",
			description = "Enable being notified when the Shadow Veil spell fades. Only recommended when you have veil sounds muted.",
			position = 7,
			section = alertSection
	)
	default boolean enableShadowVeilNotification() {
		return false;
	}

	@ConfigItem(
			keyName = "enableNoSpaceNotification",
			name = "No Space",
			description = "Enable being notified when there is no space for new pouches. Only recommended when you have pouch sounds muted.",
			position = 8,
			section = alertSection
	)
	default boolean enableNoSpaceNotification() {
		return false;
	}

	@ConfigItem(
			keyName = "muteChatMessages",
			name = "Mute Chat Messages",
			description = "Disable chat messages that are send accompanying notification- and voice alerts.",
			position = 9,
			section = alertSection
	)
	default boolean muteChatMessages() {
		return false;
	}

	@ConfigSection(
			name = "Utility",
			description = "",
			position = 2
	)
	String utilitySection = "utilitySection";
	@ConfigItem(
			keyName = "enableLeftClickPickpocket",
			name = "Left-click Pickpocket",
			description = "Make pickpocket the left-click option for any NPC that can be pickpocketed.",
			position = 0,
			section = utilitySection
	)
	default boolean enableLeftClickPickpocket(){
		return false;
	}
	@ConfigSection(
			name = "Indicators",
			description = "",
			position = 3
	)
	String indicatorsSection = "indicatorsSection";

	@ConfigItem(
			keyName = "enableDespawnTimer",
			name = "Despawn Timer",
			description = "Show a timer above your target that counts down to despawning because of not moving.",
			position = 0,
			section = indicatorsSection
	)
	default boolean enableDespawnTimer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highLightTarget",
			name = "Highlight Target",
			description = "Highlight the clickable area of your last target.",
			position = 1,
			section = indicatorsSection
	)
	default boolean highLightTarget()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "npcColor",
			name = "Highlight Color",
			description = "Color of the highlight border, menu, and text.",
			position = 2,
			section = indicatorsSection
	)
	default Color highlightColor()
	{
		return new Color(255, 57, 125, 255);
	}

	@Alpha
	@ConfigItem(
			keyName = "fillColor",
			name = "Fill Color",
			description = "Color of the highlight fill",
			position = 3,
			section = indicatorsSection
	)
	default Color fillColor()
	{
		return new Color(255, 57, 125, 85);
	}

	@ConfigItem(
			keyName = "borderWidth",
			name = "Border Width",
			description = "Width of the highlighted border",
			position = 4,
			section = indicatorsSection
	)
	default double borderWidth()
	{
		return 2;
	}

	@ConfigItem(
			keyName = "outlineFeather",
			name = "Outline Feather",
			description = "Fade the highlight outline with a value of 0 to 4.",
			position = 5,
			section = indicatorsSection
	)
	@Range(
			min = 0,
			max = 4
	)
	default int outlineFeather()
	{
		return 0;
	}

	@ConfigSection(
			name = "Overlays",
			description = "",
			position = 4
	)
	String overlaySection = "overlaySection";

	@ConfigItem(
			keyName = "enableSplasherOverlay",
			name = "Splasher Info",
			description = "Enable displaying an overlaying containing information, if present, about the splasher.",
			position = 0,
			section = overlaySection
	)
	default boolean enableSplasherOverlay() {return true; }

	@ConfigItem(
			keyName = "enableStatsOverlay",
			name = "Session Stats",
			description = "Enable displaying an overlaying containing session stats.",
			position = 1,
			section = overlaySection
	)
	default boolean enableStatsOverlay() {return true; }

	@ConfigItem(
			keyName = "enableStatusOverlay",
			name = "Pickpocket Status",
			description = "Enable displaying an overlay indicating if you are pickpocketing.",
			position = 2,
			section = overlaySection
	)
	default boolean enableStatusOverlay() {return false; }
}