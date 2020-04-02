package me.pugabyte.bncore.models.particleeffect;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EffectTask {
	private EffectType effectType;
	private int taskId;

}
