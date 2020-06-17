package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

public class MinigameNight implements Listener {
	private String region = BearFair20.BFRg + "_gamelobby";

	final static String questProgress = "bf_mgn_questProgress";
	private static String mgnRg = BearFair20.BFRg + "_gamelobby";
	private static Location arcadeSoundLoc = new Location(BearFair20.world, -1170, 141, -1716);
	private static Location arcadeSmokeLoc1 = Utils.getCenteredLocation(new Location(BearFair20.world, -1170, 140, -1715));
	private static Location arcadeSmokeLoc2 = Utils.getCenteredLocation(new Location(BearFair20.world, -1169, 148, -1715));

	public MinigameNight() {
		BNCore.registerListener(this);
		soundTasks();
	}

	private void soundTasks() {
		Tasks.repeat(0, Time.SECOND.x(5), () -> {
			Bukkit.getOnlinePlayers().stream()
					.filter(player -> WGUtils.getRegionsLikeAt(player.getLocation(), mgnRg).size() > 0)
					.forEach(MinigameNight::playArcadeEffects);
		});
	}

	private static void playArcadeEffects(Player player) {
		int ran;
		SettingService service = new SettingService();
		Setting setting = service.get(player, questProgress);
		if (setting.getValue() == null) {
			// Sounds
			player.playSound(arcadeSoundLoc, Sound.BLOCK_CAMPFIRE_CRACKLE, 1F, 1F);
			Tasks.wait(Time.SECOND.x(1) + 10, () -> player.playSound(arcadeSoundLoc, Sound.BLOCK_CAMPFIRE_CRACKLE, 1F, 1F));
			Tasks.wait(Time.SECOND.x(3), () -> player.playSound(arcadeSoundLoc, Sound.BLOCK_CAMPFIRE_CRACKLE, 1F, 1F));

			ran = Utils.randomInt(0, 40);
			Tasks.wait(ran, () -> player.playSound(arcadeSoundLoc, Sound.ITEM_CROSSBOW_LOADING_MIDDLE, 1F, 2F));

			ran = Utils.randomInt(0, 40);
			Tasks.wait(ran, () -> player.playSound(arcadeSoundLoc, Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 2F));

			// Particles
			playSmokeEffects(player);

		} else if (setting.getValue().equalsIgnoreCase("finished")) {
			player.playSound(arcadeSoundLoc, Sound.BLOCK_BEACON_AMBIENT, 1F, 1F);
		}
	}

	private static void playSmokeEffects(Player player) {
		int amount = Utils.randomInt(3, 10);
		for (int i = 0; i < amount; i++) {
			int wait = Utils.randomInt(10, 20);
			Tasks.wait(i * wait, () -> {
				player.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, arcadeSmokeLoc1, 0, 0, 0.05, 0, 1);
				player.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, arcadeSmokeLoc2, 0, 0, 0.05, 0, 1);
			});
		}
	}

	static public List<String> SCRIPT_AXEL_BEFORE = Collections.singletonList("Before Quest Text");
	static public List<String> SCRIPT_AXEL_DURING = Collections.singletonList("During Quest Text");
	static public List<String> SCRIPT_AXEL_AFTER = Collections.singletonList("After Quest Text");

}
