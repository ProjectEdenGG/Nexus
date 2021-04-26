package me.pugabyte.nexus.features.events.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import eden.utils.TimeUtils.Time;
import lombok.Data;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.skullhunt.SkullHuntService;
import me.pugabyte.nexus.models.skullhunt.SkullHunter;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public abstract class SkullHuntEvent implements Listener {
	private SkullHuntService service = new SkullHuntService();
	// Settings
	protected String settingType = "skullHunt";
	protected List<String> skullUuids = new ArrayList<>();
	protected List<Location> skullLocations = null;
	protected List<World> activeWorlds = Collections.singletonList(Bukkit.getWorld("world"));
	protected List<ProtectedRegion> activeRegions = null;

	// Particles
	protected Particle notFoundParticle = Particle.VILLAGER_HAPPY;
	protected Particle foundAlreadyParticle = null;

	// Messages
	protected String PREFIX = StringUtils.getPrefix("SkullHunt");
	protected String foundOneMsg = "You found a skull!";
	protected String foundAlreadyMsg = "You already found this skull!";
	protected String foundAllMsg = "You found all the skulls!";

	// Sounds
	protected List<SoundUtils.SoundArgs> foundOneSounds = Arrays.asList(
			new SoundUtils.SoundArgs(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2f, 2f),
			new SoundUtils.SoundArgs(Sound.BLOCK_BEACON_POWER_SELECT, 2f, 2f)
	);
	protected List<SoundUtils.SoundArgs> foundAlreadySounds = Collections.singletonList(
			new SoundUtils.SoundArgs(Sound.ENTITY_VILLAGER_NO, 2F, 1F));
	protected List<SoundUtils.SoundArgs> foundAllSounds = Collections.singletonList(
			new SoundUtils.SoundArgs(Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 2F, 1F));

	// ItemStack Prizes
	List<ItemBuilder> singlePrizes = null;
	boolean randomSinglePrizes = true;

	List<ItemBuilder> overallPrizes = null;
	boolean randomOverallPrizes = false;

	@EventHandler
	public void onClickSkull(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) return;

		Player player = event.getPlayer();
		Location loc = block.getLocation();
		if (clickedSkull(event)) {

			if (!hasFound(loc, player)) {
				find(loc, player);

				if (hasFoundAll(player))
					foundAll(player);
			} else {
				foundAlready(player);
			}
		}
	}

	public SkullHuntEvent() {
		Nexus.registerListener(this);

		// Skull Particles Task
		Tasks.wait(Time.TICK, () ->
				Tasks.repeat(0, Time.SECOND.x(3), () -> {
					if (foundAlreadyParticle == null && notFoundParticle == null)
						return;

					if (Utils.isNullOrEmpty(skullLocations))
						return;

					Bukkit.getOnlinePlayers().forEach(player -> {
						if (!activeWorlds.contains(player.getWorld()))
							return;

						if (!Utils.isNullOrEmpty(activeRegions) && !isInActionRegion(player))
							return;

						for (Location skullLoc : skullLocations) {
							if (hasFound(skullLoc, player)) {
								if (foundAlreadyParticle != null)
									player.spawnParticle(foundAlreadyParticle, skullLoc, 10, 0.25, 0.25, 0.25, 0.01);
							} else {
								if (notFoundParticle != null)
									player.spawnParticle(notFoundParticle, skullLoc, 10, 0.25, 0.25, 0.25, 0.01);
							}
						}
					});
				})
		);
	}

	private boolean clickedSkull(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) return false;
		if (!MaterialTag.SKULLS.isTagged(block.getType())) return false;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return false;

		Skull skull = (Skull) block.getState();
		if (skull.getOwningPlayer() == null) return false;
		if (!skullUuids.contains(skull.getOwningPlayer().getUniqueId().toString())) return false;

		Player player = event.getPlayer();
		if (!Utils.isNullOrEmpty(activeRegions) && !isInActionRegion(player)) return false;

		return true;
	}

	private boolean isInActionRegion(Player player) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player);
		for (ProtectedRegion activeRegion : activeRegions) {
			if (WGUtils.isInRegion(player.getLocation(), activeRegion))
				return true;
		}

		return false;
	}

	public int getTotalHeads() {
		return skullLocations.size();
	}

	boolean hasFound(Location location, Player player) {
		SkullHunter skullHunter = service.get(player);
		return skullHunter.getFound(settingType).contains(location);
	}

	public boolean hasFoundAll(Player player) {
		SkullHunter skullHunter = service.get(player);
		return skullHunter.getFound(settingType).size() == getTotalHeads();
	}

	public void find(Location location, Player player) {
		SkullHunter skullHunter = service.get(player);
		skullHunter.found(settingType, location);
		service.save(skullHunter);

		PlayerUtils.send(player, foundOneMsg);
		playSounds(player, foundOneSounds);
		giveSinglePrize(player);
	}

	public void foundAll(Player player) {
		PlayerUtils.send(player, foundAllMsg);
		playSounds(player, foundAllSounds);
		giveOverallPrize(player);
	}

	public void foundAlready(Player player) {
		PlayerUtils.send(player, foundAlreadyMsg);
		playSounds(player, foundAlreadySounds);
	}

	public void playSounds(Player player, List<SoundUtils.SoundArgs> sounds) {
		for (SoundUtils.SoundArgs soundArgs : sounds) {
			Tasks.wait(soundArgs.getDelay(), () -> SoundUtils.playSound(player, soundArgs));
		}
	}

	public void giveSinglePrize(Player player) {
		if (randomSinglePrizes)
			giveRandomSinglePrize(player);
		else
			giveAllSinglePrize(player);
	}

	public void giveOverallPrize(Player player) {
		if (randomOverallPrizes)
			giveRandomOverallPrize(player);
		else
			giveAllOverallPrize(player);
	}

	private void giveRandomSinglePrize(Player player) {
		givePrize(player, Collections.singletonList(RandomUtils.randomElement(singlePrizes)));
	}

	private void giveRandomOverallPrize(Player player) {
		givePrize(player, Collections.singletonList(RandomUtils.randomElement(overallPrizes)));
	}

	private void giveAllSinglePrize(Player player) {
		givePrize(player, singlePrizes);
	}

	private void giveAllOverallPrize(Player player) {
		givePrize(player, overallPrizes);
	}

	private void givePrize(Player player, List<ItemBuilder> prizes) {
		for (ItemBuilder prize : prizes) {
			PlayerUtils.giveItem(player, prize.build());
		}

	}


}
