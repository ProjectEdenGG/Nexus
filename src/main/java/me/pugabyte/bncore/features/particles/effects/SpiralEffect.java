package me.pugabyte.bncore.features.particles.effects;

import lombok.Builder;
import lombok.Getter;

public class SpiralEffect {
    @Getter
    private int taskId;

    @Builder(buildMethodName = "start")
    public SpiralEffect() {
        taskId = -1;
    }
}
