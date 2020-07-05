package me.pugabyte.bncore.features.commands;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.snoweffect.SnowEffect;
import me.pugabyte.bncore.models.snoweffect.SnowEffectService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;

@Aliases("togglesnow")
@NoArgsConstructor
public class SnowEffectCommand extends CustomCommand implements Listener {
	private final SnowEffectService service = new SnowEffectService();
	private SnowEffect snowEffect;
	public static StateFlag SNOW_EFFECT_FLAG;

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
					.filter(player -> {
						WorldGuardUtils worldGuardUtils = new WorldGuardUtils(player);
						return worldGuardUtils.getRegionsLikeAt(player.getLocation(), ".*_snoweffect").size() > 0;
					})
					.forEach(SnowEffectCommand::playSnowEffect);
		});

		// Custom Flag
		Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (wg != null) {
			try {
				StateFlag flag = new StateFlag("snow-effect", false);
				WorldGuardUtils.registerFlag(flag);
				SNOW_EFFECT_FLAG = flag;
			} catch (FlagConflictException ex) {
				FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
				Flag<?> existing = registry.get("snow-effect");
				if (existing instanceof StateFlag)
					SNOW_EFFECT_FLAG = (StateFlag) existing;
			}
		}
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

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (CitizensAPI.getNPCRegistry().isNPC(player)) return;

		WorldGuardUtils WGUtils = new WorldGuardUtils(player);
		StateFlag flag = SNOW_EFFECT_FLAG;
		if (WGUtils.isFlagSetFor(player, flag)) {
			player.sendMessage("Turn on snow effect.");
		} else {
			Set<ProtectedRegion> regions = WGUtils.getRegionsLikeAt(player.getLocation(), ".*_snoweffect");
			if (regions.size() > 0)
				Utils.wakka(player.getName() + " - " + regions.size());
		}
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (CitizensAPI.getNPCRegistry().isNPC(player)) return;

		WorldGuardUtils WGUtils = new WorldGuardUtils(player);
		StateFlag flag = SNOW_EFFECT_FLAG;
		if (WGUtils.isFlagSetFor(player, flag)) {
			player.sendMessage("Turn off snow effect.");
		}
	}
}