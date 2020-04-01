package me.pugabyte.bncore.features.particles;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class EffectTask {
	private EffectType effectType;
	private Player player;
	private int taskId;


}
