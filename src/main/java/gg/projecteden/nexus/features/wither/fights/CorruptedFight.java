package gg.projecteden.nexus.features.wither.fights;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.wither.WitherChallenge;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class CorruptedFight extends WitherFight {

	public double maxHealth;
	public boolean shouldSummonFirstWave = true;
	public boolean shouldSummonSecondWave = true;
	Phase phase = Phase.ONE;
	private boolean goingToCenter;

	@Override
	public WitherChallenge.Difficulty getDifficulty() {
		return WitherChallenge.Difficulty.CORRUPTED;
	}

	@Override
	public void start() {
		super.start();
		alivePlayers().forEach(player -> player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.WITHER).infinite().amplifier(0).build()));
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
		return gg.projecteden.api.common.utils.RandomUtils.chanceOf(75);
	}

	@Override
	public List<ItemStack> getAlternateDrops() {
		return new ArrayList<>() {{
			ItemStack key = CrateType.WITHER.getKey();
			key.setAmount(2);
			add(key);
		}};
	}

	@EventHandler
	public void counterAttack(EntityDamageByEntityEvent event) {
		if (!event.getEntity().equals(wither))
			return;

		if (gg.projecteden.api.common.utils.RandomUtils.chanceOf(30))
			if (gg.projecteden.api.common.utils.RandomUtils.chanceOf(35))
				EnumUtils.random(CounterAttack.class).execute(alivePlayers());
			else
				EnumUtils.random(CorruptedCounterAttacks.class).execute(alivePlayers());

		if (gg.projecteden.api.common.utils.RandomUtils.chanceOf(phase.getDodgeChance()))
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
			goToCenter();
		} else if (wither.getHealth() - event.getFinalDamage() < maxHealth / 3 && shouldSummonSecondWave) {
			shouldSummonSecondWave = false;
			goToCenter();
		}
	}

	public void goToCenter() {
		goingToCenter = true;
		wither.getPathfinder().moveTo(WitherChallenge.cageLoc);
		AtomicInteger taskId = new AtomicInteger();
		taskId.set(Tasks.repeat(1 ,1, () -> {
			if (!wither.getPathfinder().hasPath()) {
				arriveAtCenter();
				Tasks.cancel(taskId.get());
			}
		}));
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event) {
		if (!event.getEntity().equals(wither))
			return;

		if (goingToCenter)
			event.setCancelled(true);
	}

	public void arriveAtCenter() {
		goingToCenter = false;
		shouldRegen = false;
		spawnHoglins(phase.getHoglins());
		spawnBrutes(phase.getBrutes());
		spawnPiglins(phase.getPiglins());

		wither.setAI(false);
		wither.setGravity(false);
		wither.setInvulnerable(true);
		wither.teleport(WitherChallenge.cageLoc);
		phase.spawnBlazes();
	}

	@Override
	public CompletableFuture<Void> onKillBlazeShield() {
		Phase _phase = phase;
		phase = Phase.values()[phase.ordinal() + 1];
		return _phase.onComplete();
	}

	@EventHandler
	public void doublePlayerDamage(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player))
			return;

		if (!new WorldGuardUtils("events").isInRegion(event.getEntity().getLocation(), "witherarena"))
			return;

		if (event.getDamager() instanceof Projectile projectile) {
			if (projectile.getShooter() instanceof Wither)
				event.setDamage(event.getFinalDamage() * phase.getDamageMultiplier());
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
				if (Nullables.isNullOrEmpty(armor))
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
		},
//		SPIN_ATTACK {
//			@Override
//			public void execute(List<Player> players) {
//				Wither wither = WitherChallenge.currentFight.wither;
//				wither.setInvulnerable(true);
//
//				Location freezeLoc = wither.getLocation();
//				Player target = WitherChallenge.currentFight.getRandomAlivePlayer();
//
//				net.minecraft.world.entity.boss.wither.WitherBoss witherBoss = ((CraftWither) wither).getHandle();
//
//				SpinPacketListener listener = new SpinPacketListener();
//				Nexus.getProtocolManager().addPacketListener(listener);
//
//				ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(wither.getEntityId(), witherBoss.getEntityData(), true);
//				for (Player player : wither.getTrackedPlayers())
//					((CraftPlayer) player).getHandle().connection.send(packet);
//
//				AtomicInteger taskId1 = new AtomicInteger();
//				taskId1.set(Tasks.repeat(1, 1, () -> wither.lookAt(target)));
//
//				AtomicInteger taskId2 = new AtomicInteger();
//				taskId2.set(Tasks.repeat(1, 1, () -> {
//					wither.teleport(freezeLoc);
//					wither.setTarget(null);
//				}));
//
//				AtomicInteger taskId3 = new AtomicInteger();
//				taskId3.set(Tasks.repeat(TickTime.SECOND.x(2), 1, () -> {
//					Tasks.cancel(taskId2.get());
//					Vector velocity = target.getLocation().toVector().subtract(wither.getLocation().toVector()).normalize().multiply(2);
//					wither.setVelocity(velocity);
//
//					if (wither.isOnGround()) {
//
//						wither.getNearbyEntities(3, 3, 3).forEach(e -> {
//							if (!(e instanceof Player player))
//								return;
//							if (WitherChallenge.currentFight.getAlivePlayers().contains(player.getUniqueId()))
//								return;
//							double distance = distance(player, wither);
//							double damage = 10 - (distance * 2);
//							player.damage(damage, wither);
//						});
//
//						Tasks.cancel(taskId1.get());
//						Tasks.cancel(taskId3.get());
//
//						Nexus.getProtocolManager().removePacketListener(listener);
//						ClientboundSetEntityDataPacket packet2 = new ClientboundSetEntityDataPacket(wither.getEntityId(), witherBoss.getEntityData(), true);
//						for (Player player : wither.getTrackedPlayers())
//							((CraftPlayer) player).getHandle().connection.send(packet2);
//
//						wither.setInvulnerable(false);
//					}
//				}));
//			}
//
//			static class SpinPacketListener implements PacketListener {
//
//				@Override
//				public void onPacketSending(PacketEvent event) {
//					if (!(event.getPacket().getHandle() instanceof ClientboundSetEntityDataPacket packet))
//						return;
//					if (packet.getId() != WitherChallenge.currentFight.wither.getEntityId())
//						return;
//					packet.getUnpackedData().forEach(item -> {
//						if (item.getAccessor().getId() == 6) {
//							item.setValue(cast(Pose.SPIN_ATTACK));
//						}
//					});
//				}
//
//				public static <S, T> T cast(S src) {
//					return (T) src;
//				}
//
//				@Override
//				public void onPacketReceiving(PacketEvent event) {
//				}
//
//				@Override
//				public ListeningWhitelist getSendingWhitelist() {
//					return ListeningWhitelist.newBuilder().types(PacketType.fromClass(ClientboundSetEntityDataPacket.class)).build();
//				}
//
//				@Override
//				public ListeningWhitelist getReceivingWhitelist() {
//					return null;
//				}
//
//				@Override
//				public Plugin getPlugin() {
//					return Nexus.getInstance();
//				}
//			}
//
//		}
		;

		public void execute(List<Player> players) {
			for (Player player : players)
				execute(player);
		}

		public void execute(Player player) {}
	}

	@Getter
	@AllArgsConstructor
	public enum Phase {
		ONE(2, 10, 2, 5, 15) {
			@Override
			public void spawnBlazes() {
				WitherChallenge.currentFight.blazes = WitherChallenge.currentFight.spawnBlazes(15, 8);
			}

			@Override
			public CompletableFuture<Void> onComplete() {
				WorldEditUtils worldedit = new WorldEditUtils(WitherChallenge.cageLoc);
				WorldGuardUtils worldguard = new WorldGuardUtils(WitherChallenge.cageLoc);
				ProtectedRegion region = worldedit.worldguard().getProtectedRegion("witherarena-ruins");


				for (int i = 0; i < 25; i++) {
					Location loc = worldguard.getRandomBlock(region).getLocation();
					loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
				}

				WitherChallenge.currentFight.getAlivePlayers().forEach(uuid -> {
					PlayerUtils.getOnlinePlayer(uuid).playSound(WitherChallenge.cageLoc, Sound.ENTITY_GENERIC_EXPLODE, 10F, 1F);
				});

				return worldedit.paster().at(region.getMinimumPoint()).file("witherarena-ruins").pasteAsync();
			}
		},
		TWO(2.5, 12.5, 3, 5, 20) {
			@Override
			public void spawnBlazes() {
				WitherChallenge.currentFight.blazes = WitherChallenge.currentFight.spawnBlazes(10, 6);
				WitherChallenge.currentFight.blazes.addAll(WitherChallenge.currentFight.spawnBlazes(10, 9));
			}
		},
		THREE(3, 15, 0, 0, 0);

		final double damageMultiplier, dodgeChance;
		final int hoglins, brutes, piglins;

		public void spawnBlazes() {}

		public CompletableFuture<Void> onComplete() {
			return CompletableFuture.completedFuture(null);
		}

	}

}
