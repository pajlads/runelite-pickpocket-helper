package com.pickpockethelper;

import com.google.common.math.DoubleMath;
import com.pickpockethelper.utility.AlertID;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the playing of speech alerts.
 */
@Slf4j
@Singleton
public class AudioManager {
    private static final Map<Integer, String> clipPathById = new HashMap<>();

    @Inject
    private PickpocketHelperConfig config;

    @Inject
    private AudioPlayer audioPlayer;

    /**
     * Plays the file connected to the provided alert id.
     * @param alertId alert to be played.
     */
    public boolean play(int alertId) {
        String path = clipPathById.get(alertId);
        if (path == null) {
            log.debug("Clip doesn't exist: {}", alertId);
            return false;
        }

        var gain = (float) DoubleMath.log2(config.volume() / 50D) * 20;
        try {
            audioPlayer.play(getClass(), path, gain);
            return true;
        } catch (Exception e) {
            log.warn("Failed to play audio: {}", path, e);
            return false;
        }
    }

    static {
        clipPathById.put(AlertID.ROGUE_SET_INCOMPLETE, "/audio/rogue_set.wav");
        clipPathById.put(AlertID.DODGY_BREAK, "/audio/dodgy_necklace_break.wav");
        clipPathById.put(AlertID.HITPOINTS_LOW, "/audio/hitpoints_low.wav");
        clipPathById.put(AlertID.SHADOW_VEIL_FADED, "/audio/shadow_veil_faded.wav");
        clipPathById.put(AlertID.TARGET_DESPAWN, "/audio/target_despawn.wav");
        clipPathById.put(AlertID.SPLASHER_IDLE, "/audio/splasher_idle.wav");
        clipPathById.put(AlertID.PLAYER_IDLE, "/audio/player_idle.wav");
        clipPathById.put(AlertID.NO_INVENTORY_SPACE, "/audio/no_space.wav");
    }
}
