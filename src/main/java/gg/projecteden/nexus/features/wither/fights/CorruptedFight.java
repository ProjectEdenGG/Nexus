package gg.projecteden.nexus.features.wither.fights;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@NoArgsConstructor
public class CorruptedFight extends WitherFight {

	public double maxHealth;
	public boolean shouldSummonFirstWave = true;
	public boolean shouldSummonSecondWave = true;

	@Override
	public WitherChallenge.Difficulty getDifficulty() {
		return WitherChallenge.Difficulty.CORRUPTED;
	}

	@Override
	public void start() {
		super.start();
		alivePlayers().forEach(player -> player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.WITHER).maxDuration().amplifier(0).build()));
	}

	@EventHandler
	public void preventWitherEffect(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!isAlive(player))
			return;

		if (event.getCause() != EntityDamageEvent.DamageCause.WITHER)
			return;

		event.setCancelled(true);
	}

	public Map<UUID, Integer> playerRegenAmounts = new HashMap<>();

	@EventHandler
	public void slowRegen(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!isAlive(player))
			return;

		if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
			return;

		int regenAmount = playerRegenAmounts.getOrDefault(player.getUniqueId(), 0);
		if (regenAmount == 3) {
			playerRegenAmounts.put(player.getUniqueId(), 0);
			return;
		}

		event.setCancelled(true);
		playerRegenAmounts.put(player.getUniqueId(), regenAmount + 1);
	}

	@Override
	public void spawnWither(Location location) {
		Wither wither = location.getWorld().spawn(location, Wither.class, SpawnReason.NATURAL);
		this.wither = wither;
		this.maxHealth = EntityUtils.setHealth(wither, wither.getHealth() * 3);
	}

	@Override
	public boolean shouldGiveStar() {
		return true;
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return new ArrayList<>() {{
			ItemStack key = CrateType.BOSS.getKey();
			key.setAmount(2);
			add(key);
		}};
	}

	@EventHandler
	public void counterAttack(EntityDamageByEntityEvent event) {
		if (!event.getEntity().equals(wither))
			return;

		if (RandomUtils.chanceOf(30))
			if (RandomUtils.chanceOf(35))
				EnumUtils.random(CounterAttack.class).execute(alivePlayers());
			else
				EnumUtils.random(CorruptedCounterAttacks.class).execute(alivePlayers());

		if (RandomUtils.chanceOf(10))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamageWither(EntityDamageByEntityEvent event) {
		if (!event.getEntity().equals(wither))
			return;

		Wither wither = (Wither) event.getEntity();
		if (!shouldRegen)
			broadcastToParty("&cThe wither cannot be damaged while the blaze shield is up! &eKill the blazes to continue the fight!");
		if (!shouldSummonFirstWave && !shouldSummonSecondWave)
			return;

		if (wither.getHealth() - event.getFinalDamage() < (maxHealth / 3) * 2 && shouldSummonFirstWave) {
			shouldSummonFirstWave = false;
			shouldRegen = false;
			spawnPiglins(15);
			spawnBrutes(5);
			spawnHoglins(2);
			wither.setAI(false);
			wither.setGravity(false);
			wither.setInvulnerable(true);
			wither.teleport(WitherChallenge.cageLoc);
			this.blazes = spawnBlazes(15, 8);
		} else if (wither.getHealth() - event.getFinalDamage() < maxHealth / 3 && shouldSummonSecondWave) {
			shouldSummonSecondWave = false;
			shouldRegen = false;
			spawnPiglins(20);
			spawnBrutes(5);
			spawnHoglins(2);
			wither.setAI(false);
			wither.setGravity(false);
			wither.setInvulnerable(true);
			wither.teleport(WitherChallenge.cageLoc);
			this.blazes = spawnBlazes(10, 6);
			this.blazes.addAll(spawnBlazes(10, 9));
		}
	}

	@EventHandler
	public void doublePlayerDamage(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player))
			return;

		if (!new WorldGuardUtils("events").isInRegion(event.getEntity().getLocation(), "witherarena"))
			return;

		if (event.getDamager() instanceof Projectile projectile) {
			if (projectile.getShooter() instanceof Wither)
				event.setDamage(event.getFinalDamage() * 2);
		}
	}

	public enum CorruptedCounterAttacks {
		SCRAMBLE_INVENTORY {
			@Override
			public void execute(Player player) {
				List<ItemStack> contents = new ArrayList<>();
				for (int i = 0; i < 36; i++)
					contents.add(player.getInventory().getContents()[i]);
				contents.add(player.getInventory().getItemInOffHand());
				Collections.shuffle(contents);
				for (int i = 0; i < 36; i++)
					player.getInventory().setItem(i, contents.get(i));
				player.getInventory().setItemInOffHand(contents.get(contents.size() - 1));
				subtitle(player, "&8&kbbb &4&lInventory Scrambled &8&kbbb");
			}
		},
		STRIP_ARMOR_PIECE {
			@Override
			public void execute(Player player) {
				List<ItemStack> armor = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
				if (isNullOrEmpty(armor))
					return;

				ItemStack item = RandomUtils.randomElement(armor);
				if (PlayerUtils.hasRoomFor(player, item)) {
					armor.set(armor.indexOf(item), null);
					player.getInventory().setArmorContents(armor.toArray(ItemStack[]::new));
					PlayerUtils.giveItemPreferNonHotbar(player, item);
					subtitle(player, "&8&kbbb &4&lArmor Piece Stripped &8&kbbb");
				}
			}
		},
		SMITE {
			@Override
			public void execute(Player player) {
				player.getLocation().getWorld().strikeLightning(player.getLocation());
			}
		},
		WITHER_SKELETONS {
			@Override
			public void execute(List<Player> players) {
				List<Location> locations = new ArrayList<>();
				for (int iteration = 0; iteration < 5; iteration++) {
					double angle = 360.0 / 5 * iteration;
					angle = Math.toRadians(angle);
					double x = Math.cos(angle) * 1.5;
					double z = Math.sin(angle) * 1.5;
					locations.add(WitherChallenge.currentFight.wither.getLocation().clone().add(x, 0, z));
				}
				for (Location location : locations)
					location.getWorld().spawn(location, WitherSkeleton.class);
			}
		},
		NEGATIVE_EFFECT {
			@Override
			public void execute(List<Player> players) {
				PotionEffectType type = RandomUtils.randomElement(PotionEffectType.WEAKNESS, PotionEffectType.DARKNESS, PotionEffectType.SLOW);
				players.forEach(pl -> pl.addPotionEffect(new PotionEffectBuilder(type).duration(TickTime.SECOND.x(10)).ambient(true).build()));
			}
		},
		SILVERFISH {
			@Override
			public void execute(List<Player> players) {
				Location witherLoc = WitherChallenge.currentFight.wither.getLocation().clone();
				for (int i = 0; i < 10; i++) {
					double x = RandomUtils.randomDouble(-2.5, 2.5);
					double y = RandomUtils.randomDouble(-2.5, 2.5);
					double z = RandomUtils.randomDouble(-2.5, 2.5);
					witherLoc.getWorld().spawn(witherLoc.clone().add(x, y, z), Silverfish.class);
				}
			}
		};

		public void execute(List<Player> players) {
			for (Player player : players)
				execute(player);
		}

		public void execute(Player player) {}
	}

}
