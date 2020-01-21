package me.pugabyte.bncore.features.minigames.mechanics;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.KangarooJumpingArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class KangarooJumping extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Kangaroo Jumping";
	}

	@Override
	public String getDescription() {
		return "Jump higher and higher and be the first to the finish!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.LEATHER_BOOTS);
	}

	@Override
	public void onStart(Match match) {
		super.onStart(match);
		KangarooJumpingArena arena = (KangarooJumpingArena) match.getArena();
		Utils.wait(5 * 20, () -> {
			match.broadcast("Power ups have spawned!");
			for (Location loc : arena.getPowerUpLocations())
				spawnPowerUp(loc, match);
		});
	}

	private void spawnPowerUp(Location loc, Match match) {
		POWERUP powerup = POWERUP.values()[Utils.randomInt(0, (POWERUP.values().length - 1))];
		String powerUpName = Utils.colorize(((powerup.isPositive()) ? "&a" : "&c") + powerup.getName());

		Hologram hologram = HologramsAPI.createHologram(BNCore.getInstance(), loc.clone().add(0, 2, 0));
		hologram.appendTextLine(Utils.colorize("&3&lPower Up"));
		hologram.insertTextLine(1,  powerUpName);
		ItemLine itemLine = hologram.appendItemLine(powerup.getItemStack());

		itemLine.setPickupHandler(player -> {
			player.sendMessage(Minigames.PREFIX + "You picked up a power up!");
			powerup.onPickUp(PlayerManager.get(player));
			match.getHolograms().remove(hologram);
			hologram.delete();
			Utils.wait(10 * 20, ()->{
				if(!match.isEnded()) spawnPowerUp(loc, match);
			});
		});
		match.getHolograms().add(hologram);
	}

	public enum POWERUP{
		JUMP("Extra Jump Boost", true, new ItemStackBuilder(Material.LEATHER_BOOTS).glow().build()) {
			@Override
			public void onPickUp(Minigamer minigamer) {
				minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, 20), true);
			}
		},
		P_BLINDNESS("Blindness", true, new ItemStackBuilder(Material.POTION).effectColor(ColorType.BLACK.getColor()).glow().build()) {
			@Override
			public void onPickUp(Minigamer minigamer) {
				for (Minigamer _minigamer : minigamer.getMatch().getMinigamers()) {
					if(_minigamer == minigamer) continue;
					_minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1));
				}
			}
		},
		N_BLINDNESS("Blindness", false, new ItemStackBuilder(Material.POTION).effectColor(ColorType.BLACK.getColor()).build()) {
			@Override
			public void onPickUp(Minigamer minigamer) {
				minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1));
			}
		},
		SNOWBALL("Snowball", true, new ItemStackBuilder(Material.SNOW_BALL).build()) {
			@Override
			public void onPickUp(Minigamer minigamer) {
				minigamer.getMatch().getMinigamers().forEach((_minigamer)->_minigamer.getPlayer().getInventory().addItem(
						new ItemStackBuilder(Material.SNOW_BALL)
								.name("&bKnockback Snowball")
								.enchant(Enchantment.KNOCKBACK, 2)
								.amount(3)
						.build()
				));
			}
		},
		LEVITATION("Levitation", true, new ItemStackBuilder(Material.ELYTRA).glow().build()) {
			@Override
			public void onPickUp(Minigamer minigamer) {
				for (Minigamer _minigamer : minigamer.getMatch().getMinigamers()) {
					if(_minigamer == minigamer) continue;
					_minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 3));
				}
			}
		};

		public String name;
		boolean isPositive;
		ItemStack itemStack;

		public String getName(){
			return name;
		}

		public boolean isPositive(){
			return isPositive;
		}

		public ItemStack getItemStack(){
			return itemStack;
		}

		POWERUP(String name, boolean isPositive, ItemStack itemStack) {
			this.name = name;
			this.isPositive = isPositive;
			this.itemStack = itemStack;
		}

		public abstract void onPickUp(Minigamer minigamer);
	}

	@EventHandler
	public void onEnterWinningArea(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!event.getRegion().getId().equalsIgnoreCase("kangarooJumping_" + minigamer.getMatch().getArena().getName() + "_winningRegion"))
			return;
		if (hasAnyoneScored(minigamer.getMatch())) return;
		minigamer.scored();
		minigamer.getMatch().broadcast("&e" + minigamer.getColoredName() + " has reached the finish area!");
		minigamer.getMatch().getTasks().wait(5 * 20, () -> minigamer.getMatch().end());
	}

	boolean hasAnyoneScored(Match match) {
		for (Minigamer minigamer : match.getMinigamers()) {
			if (minigamer.getScore() > 0)
				return true;
		}
		return false;
	}

}

