package com.pickpockethelper;

import com.google.common.math.DoubleMath;
import com.google.common.primitives.Floats;
import com.pickpockethelper.utility.AlertID;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Handles the playing of speech alerts.
 */
@Slf4j
@Singleton
public class AudioManager {
    private final HashMap<Integer, Clip> clips = new HashMap<>();

    @Inject
    private PickpocketHelperConfig config;

    /**
     * Plays the file connected to the provided alert id.
     * If the clip is already running it does nothing.
     * @param alertId alert to be played.
     */
    public boolean play(int alertId) {
        Clip clip = clips.get(alertId);
        if (clip == null) {
            log.debug("Clip doesn't exist: " + alertId);
            return false;
        }

        if (!clip.isRunning()) {
            var control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            var gain = (float) DoubleMath.log2(config.volume() / 50D) * 20;
            control.setValue(Floats.constrainToRange(gain, control.getMinimum(), control.getMaximum()));

            clip.setFramePosition(0);
            clip.start();
        }
        return true;
    }

    public void init() {
        final Map<Integer, String> alertAudioFiles = new HashMap<>();
        alertAudioFiles.put(AlertID.ROGUE_SET_INCOMPLETE, "rogue_set.wav");
        alertAudioFiles.put(AlertID.DODGY_BREAK, "dodgy_necklace_break.wav");
        alertAudioFiles.put(AlertID.HITPOINTS_LOW, "hitpoints_low.wav");
        alertAudioFiles.put(AlertID.SHADOW_VEIL_FADED, "shadow_veil_faded.wav");
        alertAudioFiles.put(AlertID.TARGET_DESPAWN, "target_despawn.wav");
        alertAudioFiles.put(AlertID.SPLASHER_IDLE, "splasher_idle.wav");
        alertAudioFiles.put(AlertID.PLAYER_IDLE, "player_idle.wav");
        alertAudioFiles.put(AlertID.NO_INVENTORY_SPACE, "no_space.wav");

        alertAudioFiles.forEach(this::loadAudioFile);
    }

    public void clear() {
        clips.forEach((id, clip) -> {
            clip.stop();
            clip.flush();
            clip.close();
        });

        clips.clear();
    }


    /**
     * Loads file from the resources audio folder.
     * @param alertId id connected to filename.
     * @param fileName filename to be retrieved.
     */
    private void loadAudioFile(int alertId, String fileName) {
        try (
                InputStream source = getClass().getResourceAsStream("/audio/" + fileName);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(source);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream)
        ) {
            Clip clip = AudioSystem.getClip();
            clips.put(alertId, clip);
            clip.open(audioInputStream);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

}
