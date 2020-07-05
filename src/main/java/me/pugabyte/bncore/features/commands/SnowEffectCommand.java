package me.pugabyte.bncore.features.commands;

import com.sk89q.worldguard.protection.flags.StateFlag;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.snoweffect.SnowEffect;
import me.pugabyte.bncore.models.snoweffect.SnowEffectService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldGuardFlagUtils;
import me.pugabyte.bncore.utils.WorldGuardFlagUtils.Flags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

@Aliases("togglesnow")
@NoArgsConstructor
public class SnowEffectCommand extends CustomCommand implements Listener {
	private final SnowEffectService service = new SnowEffectService();
	private SnowEffect snowEffect;

	public SnowEffectCommand(CommandEvent event) {
		super(event);
		snowEffect = service.get(player());
	}

	static {
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			Tasks.async(() -> {
				List<SnowEffect> all = new SnowEffectService().getAll();
				all.stream()
						.filter(snowEffect -> snowEffect.isOnline() && snowEffect.isEnabled())
						.forEach(snowEffect -> playSnowEffect(snowEffect.getPlayer()));
			});

			Bukkit.getOnlinePlayers().stream()
					.filter(player -> WorldGuardFlagUtils.isFlagSetFor(player, (StateFlag) Flags.SNOW_EFFECT.get()))
					.forEach(SnowEffectCommand::playSnowEffect);
		});
	}

	@Path("[on|off]")
	void snowEffect(Boolean enable) {
		if (enable == null)
			enable = !snowEffect.isEnabled();

		snowEffect.setEnabled(enable);
		service.save(snowEffect);

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	private static void playSnowEffect(Player player) {
		Tasks.sync(() -> {
			if (isBelowCeiling(player))
				return;
			player.spawnParticle(Particle.FALLING_DUST, player.getLocation(), 1400, 40, 15, 40, .01, Bukkit.createBlockData(Material.SNOW_BLOCK));
			Tasks.wait(20, () -> player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 1400, 40, 15, 40, .01));
		});
	}

	private static boolean isBelowCeiling(Player player) {
		int count = 0;
		int playerY = (int) player.getLocation().getY() + 1;
		for (int y = playerY; y <= 255; y++) {
			if (player.getLocation().getBlock().getRelative(0, y - playerY, 0).getType().isOccluding())
				++count;
			if (count >= 2)
				return true;
		}
		return false;
	}
}