package me.pugabyte.bncore.features.particles;

import lombok.Getter;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParticleUtils {
	@Getter
	public static Set<EffectTask> tasks = new HashSet<>();

	public static List<EffectTask> getTasks(Player player) {
		return tasks.stream().filter(task -> task.getPlayer().equals(player)).collect(Collectors.toList());
	}

	public static List<EffectTask> getTasks(Player player, EffectType effectType) {
		return tasks.stream().filter(task -> task.getPlayer().equals(player) && task.getEffectType() == effectType).collect(Collectors.toList());
	}

	public static void cancelAllEffects(Player player) {
		getTasks(player).stream().map(EffectTask::getTaskId).forEach(Tasks::cancel);
	}

	public static void cancelEffect(Player player, EffectType effectType) {
		getTasks(player, effectType).forEach(effectTask -> {
			Tasks.cancel(effectTask.getTaskId());
			tasks.remove(effectTask);
		});
	}

	public static void cancelEffectTask(int taskId) {
		Tasks.cancel(taskId);
	}

	public static void addEffectTask(Player player, EffectType effectType, int... taskIds) {
		for (int taskId : taskIds) {
			tasks.add(new EffectTask(effectType, player, taskId));
		}
	}

	public static double incHue(double hue) {
		if (hue >= 20.0) hue = 0.0;
		hue += 0.1;
		return hue;
	}

	public static double[] incRainbow(double hue) {
		int argb = Color.HSBtoRGB((float) (hue / 20.0F), 1.0F, 1.0F);
		float r = (float) (argb >> 16 & 255) / 255.0F;
		float g = (float) (argb >> 8 & 255) / 255.0F;
		float b = (float) (argb & 255) / 255.0F;
		r = r == 0.0F ? 0.001F : r;

		double[] rgb = new double[3];
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;
		return rgb;
	}

//	Only works with async
//	public static boolean isActive(int taskId) {
//		return BNCore.getInstance().getServer().getScheduler().isCurrentlyRunning(taskId);
//	}

}
