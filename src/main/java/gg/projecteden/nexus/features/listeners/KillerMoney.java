package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class KillerMoney implements Listener {

	private static final List<SpawnReason> UNNATURAL_SPAWN_REASONS = List.of(
		SpawnReason.SPAWNER,
		SpawnReason.SPAWNER_EGG,
		SpawnReason.NETHER_PORTAL,
		SpawnReason.RAID
	);

	private static final List<DamageCause> DEATH_CAUSE_BLACKLIST = List.of(
		DamageCause.CRAMMING,
		DamageCause.SUFFOCATION
	);

	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		Player player = event.getEntity().getKiller();
		if (player == null)
			return;

		if (!GameMode.SURVIVAL.equals(player.getGameMode()))
			return;

		if (player.getWorld().getName().contains("events"))
			return;

		// the annotation is a lie
		if (event.getEntity().getEntitySpawnReason() == null)
			return;

		if (UNNATURAL_SPAWN_REASONS.contains(event.getEntity().getEntitySpawnReason()))
			return;

		if (event.getEntity().getLastDamageCause() != null)
			if (DEATH_CAUSE_BLACKLIST.contains(event.getEntity().getLastDamageCause().getCause()))
				return;

		if (event.getEntityType() == EntityType.ENDERMAN && player.getWorld().getEnvironment() == Environment.THE_END)
			return;

		MobMoney mob = MobMoney.of(event.getEntityType().name());
		if (mob == null)
			return;

		if (!mob.getAllowedWorldGroups().contains(WorldGroup.of(player)))
			return;

		double boost = Booster.getTotalBoost(player, Boostable.KILLER_MONEY);
		double money = mob.getRandomMoney() * boost;

		var earnedEvent = new KillerMoneyEarnedEvent(player, money);
		if (!earnedEvent.callEvent())
			return;

		new BankerService().deposit(player, earnedEvent.getMoney(), ShopGroup.of(player), TransactionCause.KILLER_MONEY);
	}

	@Getter
	@Setter
	public static class KillerMoneyEarnedEvent extends PlayerEvent {
		private double money;

		public KillerMoneyEarnedEvent(@NotNull Player who, double money) {
			super(who);
			this.money = money;
		}

		@Getter
		private static final HandlerList handlerList = new HandlerList();

		@Override
		public @NotNull HandlerList getHandlers() {
			return handlerList;
		}
	}

	@Getter
	public enum MobMoney {
		BAT(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		BLAZE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		CAVE_SPIDER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		CREEPER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		DROWNED(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ELDER_GUARDIAN(20.0, 100.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDER_DRAGON(50.0, 150.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDERMAN(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ENDERMITE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		GHAST(3.0, 10.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		GUARDIAN(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		HUSK(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		MAGMA_CUBE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		PIG_ZOMBIE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		PHANTOM(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SHULKER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SILVERFISH(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SKELETON(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SLIME(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SPIDER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		SQUID(.25, 1.5, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		STRAY(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		WITCH(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		WITHER_SKELETON(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ZOMBIE(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		ZOMBIE_VILLAGER(.5, 2.0, WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK),
		;

		private final double min;
		private final double max;
		private final List<WorldGroup> allowedWorldGroups;

		MobMoney(double min, double max, WorldGroup... allowedWorldGroups) {
			this.min = min;
			this.max = max;
			this.allowedWorldGroups = Arrays.asList(allowedWorldGroups);
		}

		double getRandomMoney() {
			return RandomUtils.randomDouble(min, max);
		}

		public static MobMoney of(String name) {
			try {
				return MobMoney.valueOf(name);
			} catch (IllegalArgumentException ignore) {
				return null;
			}
		}

	}

}
