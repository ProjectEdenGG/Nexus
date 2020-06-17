package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.world;

public class Halloween implements Listener {
	private String region = BearFair20.BFRg + "_halloween";
	Map<Player, Integer> musicTaskMap = new HashMap<>();
	Location halloweenMusicLoc = new Location(world, -921, 128, -1920);
	private Sound[] halloweenSounds = {Sound.AMBIENT_CAVE, Sound.ENTITY_ELDER_GUARDIAN_DEATH, Sound.ENTITY_VEX_AMBIENT,
			Sound.ENTITY_WITCH_AMBIENT, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS,
			Sound.ENTITY_ILLUSIONER_CAST_SPELL, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, Sound.ENTITY_SHULKER_AMBIENT};

	public Halloween() {
		BNCore.registerListener(this);
		soundTasks();
	}

	private void soundTasks() {
		Tasks.repeat(0, 30 * 20, () -> {
			if (Utils.chanceOf(50)) {
				Sound sound = Utils.getRandomElement(Arrays.asList(halloweenSounds));
				musicTaskMap.forEach((player, integer) -> player.playSound(player.getLocation(), sound, 10F, 0.1F));
			}
		});

		Tasks.repeat(0, 25 * 20, () -> {
			if (Utils.chanceOf(25))
				musicTaskMap.forEach((player, integer) -> player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10F, 0.1F));
		});

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		ProtectedRegion protectedRegion = WGUtils.getProtectedRegion(region);
		if (!WGUtils.getRegionsAt(event.getPlayer().getLocation()).contains(protectedRegion)) return;

		if (event.getClickedBlock() == null) return;

		Material material = event.getClickedBlock().getType();
		if (!material.equals(Material.RAIL)) return;

		Location loc = event.getClickedBlock().getLocation();
		float ran = (float) Utils.randomDouble(0.0, 2.0);
		world.playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 0.7F, ran);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(region)) return;
		startSoundsTask(event.getPlayer());
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(region)) return;
		stopSoundsTask(event.getPlayer());
	}

	private void startSoundsTask(Player player) {
		int taskId = Tasks.repeat(0, Time.SECOND.x(350), () -> {
			player.stopSound(Sound.MUSIC_DISC_13);
			player.playSound(halloweenMusicLoc, Sound.MUSIC_DISC_13, SoundCategory.AMBIENT, 7F, 0.1F);
		});

		musicTaskMap.put(player, taskId);
	}

	private void stopSoundsTask(Player player) {
		Integer taskId = musicTaskMap.remove(player);
		if (taskId != null)
			Tasks.cancel(taskId);
	}
}
