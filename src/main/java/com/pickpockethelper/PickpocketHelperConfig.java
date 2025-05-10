package com.pickpockethelper;

import com.pickpockethelper.utility.AlertType;
import java.awt.Color;

import com.pickpockethelper.utility.ArdyMode;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

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
	default boolean muteFailSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteSuccessSound",
		name = "Mute Success Sounds",
		description = "Mute sounds caused by a successful pickpocket attempt.",
		position = 1,
		section = soundSection
	)
	default boolean muteSuccessSounds()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteEmptyPouch",
		name = "Mute Empty Pouch",
		description = "Mute sounds caused by emptying pouches.",
		position = 2,
		section = soundSection
	)
	default boolean muteEmptyPouchSound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteNoSpace",
		name = "Mute No Space",
		description = "Mute sounds caused by pickpocketing while having no space for new pouches.",
		position = 3,
		section = soundSection
	)
	default boolean muteNoSpaceSound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteVeilActivateSound",
		name = "Mute Shadow Veil Activate",
		description = "Mute sounds caused by Shadow Veil activating.",
		position = 4,
		section = soundSection
	)
	default boolean muteVeilActivateSound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "muteVeilFadeSound",
		name = "Mute Shadow Veil Fade",
		description = "Mute sounds caused by Shadow Veil fading.",
		position = 5,
		section = soundSection
	)
	default boolean muteVeilFadeSound()
	{
		return false;
	}

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
	default AlertType getAlertType()
	{
		return AlertType.SPEECH;
	}

	@ConfigItem(
		keyName = "volume",
		name = "Speech Volume",
		description = "Adjustable volume for Speech Alerts",
		position = 0,
		section = alertSection
	)
	@Units(Units.PERCENT)
	@Range(min = 1, max = 100)
	default int volume() {
		return 20;
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
		keyName = "targetDespawnThreshold",
		name = "Despawn Threshold",
		description = "The threshold for being notified when your target is about to despawn due to not moving. A value of 0 will disable the notification,",
		position = 2,
		section = alertSection
	)
	@Units(Units.SECONDS)
	default int getTargetDespawnThreshold()
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
	default boolean enableIdleSplasherNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableNecklaceNotification",
		name = "Dodgy Necklace Breaking",
		description = "Enable being notified when your dodgy necklace breaks.",
		position = 5,
		section = alertSection
	)
	default boolean enableNecklaceNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableGlovesNotification",
		name = "Gloves of Silence Breaking",
		description = "Enable being notified when your gloves of silence are about to break.",
		position = 6,
		section = alertSection
	)
	default boolean enableGlovesNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableRogueEquipmentNotification",
		name = "Missing Rogue Equipment",
		description = "Enable being notified when pickpocketing while missing one or more pieces of rogue equipment.",
		position = 7,
		section = alertSection
	)
	default boolean enableRogueEquipmentNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableShadowVeilNotification",
		name = "Shadow Veil Fading",
		description = "Enable being notified when the Shadow Veil spell fades.",
		position = 8,
		section = alertSection
	)
	default boolean enableShadowVeilNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableNoSpaceNotification",
		name = "Full Inventory",
		description = "Enable being notified when there is no space for new pouches.",
		position = 9,
		section = alertSection
	)
	default boolean enableNoSpaceNotification()
	{
		return true;
	}

	@ConfigItem(
		keyName = "muteChatMessages",
		name = "Mute Chat Messages",
		description = "Disable chat messages that are send accompanying notification- and voice alerts.",
		position = 10,
		section = alertSection
	)
	default boolean muteChatMessages()
	{
		return false;
	}

	@ConfigSection(
		name = "Utility",
		description = "",
		position = 2
	)
	String utilitySection = "utilitySection";

	@ConfigItem(
		keyName = "hideOthers",
		name = "Hide Others",
		description = "Hide any entity that is not you, your target, the splasher, or your pet, friend, or clanmate. This avoids yellow-clicks and being interrupted by random events or wandering NPC blocking your target.",
		position = 0,
		section = utilitySection
	)
	default boolean enableHideOthers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "blockSpam",
		name = "Hide Spam Messages",
		description = "Hide game messages that spam your chat box but aren't filtered, like having to empty your pouches.",
		position = 1,
		section = utilitySection
	)
	default boolean enableBlockSpam()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableLeftClickPickpocket",
		name = "Left-click Pickpocket",
		description = "Make pickpocket the left-click option for any NPC that can be pickpocketed.",
		position = 2,
		section = utilitySection
	)
	default boolean enableLeftClickPickpocket()
	{
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
		keyName = "ardyMode",
		name = "Ardy Knight Mode",
		description = "Controls whether Knight of Ardougne despawn timer should be 600 seconds (for large groups) or 300 seconds (for solo).",
		position = 1,
		section = indicatorsSection
	)
	default ArdyMode ardyMode() {
		return ArdyMode.GROUP;
	}

	@ConfigItem(
		keyName = "highLightTarget",
		name = "Highlight Target",
		description = "Highlight the clickable area of your last target.",
		position = 2,
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
		position = 3,
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
		position = 4,
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
		position = 5,
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
		position = 6,
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
	default boolean enableSplasherOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableStatsOverlay",
		name = "Session Stats",
		description = "Enable displaying an overlaying containing session stats.",
		position = 1,
		section = overlaySection
	)
	default boolean enableStatsOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableStatusOverlay",
		name = "Pickpocket Status",
		description = "Enable displaying an overlay indicating if you are pickpocketing.",
		position = 2,
		section = overlaySection
	)
	default boolean enableStatusOverlay()
	{
		return false;
	}
}
