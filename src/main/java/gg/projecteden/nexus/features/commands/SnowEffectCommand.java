package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.snoweffect.SnowEffect;
import gg.projecteden.nexus.models.snoweffect.SnowEffectService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils.CustomFlags;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
		var service = new SnowEffectService();
		service.cacheAll();
		Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
			Tasks.async(() -> service.getCache().values().stream()
					.filter(snowEffect -> snowEffect.isOnline() && snowEffect.isEnabled())
					.forEach(snowEffect -> playSnowEffect(snowEffect.getOnlinePlayer())));

			OnlinePlayers.getAll().stream()
					.filter(player -> WorldGuardFlagUtils.test(player, CustomFlags.SNOW_EFFECT))
					.forEach(SnowEffectCommand::playSnowEffect);
		});
	}

	@Path("[on|off]")
	@Description("Toggle the custom snow effect in applicable regions")
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

			player.spawnParticle(Particle.WHITE_ASH, player.getLocation(), 3500, 40, 15, 40, .01);
			player.spawnParticle(Particle.FALLING_DUST, player.getLocation(), 1400, 40, 15, 40, .01, Bukkit.createBlockData(Material.SNOW_BLOCK));
			Tasks.wait(20, () -> player.spawnParticle(Particle.FIREWORK, player.getLocation(), 1400, 40, 15, 40, .01));
		});
	}

	private static boolean isBelowCeiling(Player player) {
		return player.getLocation().getBlock().getLightFromSky() < 15;
	}
}
