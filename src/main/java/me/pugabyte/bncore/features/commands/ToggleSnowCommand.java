package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ToggleSnowCommand extends CustomCommand {

	final static String SETTING_TYPE = "snowEffect";

	public ToggleSnowCommand(CommandEvent event) {
		super(event);
	}

	static {
		snowEffectTask();
	}

	@Path
	void toggleSnow() {
		Setting setting = new SettingService().get(player(), SETTING_TYPE);
		if (setting.getBoolean())
			toggleOff();
		else
			toggleOn();
	}

	@Path("[boolean]")
	void toggleSnow(boolean setting) {
		if (setting)
			toggleOn();
		else
			toggleOff();
	}

	void toggleOff() {
		new SettingService().delete(player(), SETTING_TYPE);
		send(PREFIX + "Snow toggled &coff");
	}

	void toggleOn() {
		Setting setting = new Setting(player(), SETTING_TYPE, "true");
		new SettingService().save(setting);
		send(PREFIX + "Snow toggled &aon");
	}

	private static void snowEffectTask() {
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			new SettingService().getFromType(SETTING_TYPE).stream()
					.map(setting -> UUID.fromString(setting.getId()))
					.filter(uuid -> Bukkit.getOfflinePlayer(uuid).isOnline())
					.map(Bukkit::getPlayer)
					.forEach(ToggleSnowCommand::playSnowEffect);

			Bukkit.getOnlinePlayers().stream()
					.filter(player -> {
						WorldGuardUtils worldGuardUtils = new WorldGuardUtils(player.getWorld());
						return worldGuardUtils.getRegionsLikeAt(player.getLocation(), ".*_snowEffect").size() > 0;
					})
					.forEach(ToggleSnowCommand::playSnowEffect);
		});
	}

	private static void playSnowEffect(Player player) {
		if (isBelowCeiling(player))
			return;
		player.spawnParticle(Particle.FALLING_DUST, player.getLocation(), 1400, 40, 15, 40, .01, Bukkit.createBlockData(Material.SNOW_BLOCK));
		Tasks.wait(20, () -> player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 1400, 40, 15, 40, .01));
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
