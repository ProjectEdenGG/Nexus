package gg.projecteden.nexus.features.mobheads;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.MobHeadConverter;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.mobheads.MobHeadUser.MobHeadData;
import gg.projecteden.nexus.models.mobheads.MobHeadUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.RandomUtils.randomDouble;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
public class MobHeads extends Feature implements Listener {
	private static final int REQUIRED_SKIN_DAYS = 3;
	private static final List<UUID> handledEntities = new ArrayList<>();
	@Getter
	@Setter
	private static boolean debug;

	@Override
	public void onStart() {
		MobHeadType.load();
	}

	public static void debug(String message) {
		if (debug)
			Nexus.log("[MobHeads] [DEBUG] " + message);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKillEntity(EntityDeathEvent event) {
		if (event.isCancelled())
			return;

		final LivingEntity victim = event.getEntity();
		final Player player = victim.getKiller();

		if (shouldIgnore(player, victim))
			return;

		final UUID uuid = victim.getUniqueId();
		handledEntities.add(uuid);
		Tasks.wait(TickTime.TICK.x(3), () -> handledEntities.remove(uuid));

		if (victim.getType() == EntityType.WITHER_SKELETON)
			event.getDrops().removeIf(item -> item.getType() == Material.WITHER_SKELETON_SKULL);

		final MobHead mobHead = MobHead.of(victim);

		if (mobHead == null)
			return;

		ItemStack skull = mobHead.getSkull();
		double chance = mobHead.getType().getChance();

		if (isNullOrAir(skull)) {
			Nexus.warn("[MobHeads] Skull for " + camelCase(mobHead.getType()) + " is null");
			return;
		}

		if (chance == 0) {
			Nexus.warn("[MobHeads] Chance for " + camelCase(mobHead.getType()) + " is 0");
			return;
		}

		if (victim instanceof Player player2)
			skull = new ItemBuilder(skull).name("&e" + Nickname.of(player2) + "'s Head").skullOwner(player2).build();

		final double looting = getLooting(player);
		final double boost = mobHead == MobHeadType.WITHER_SKELETON ? 1 : BoostConfig.multiplierOf(Boostable.MOB_HEADS);
		final double finalChance = (chance + looting) * boost;
		final double random = randomDouble(0, 100);
		final boolean drop = random <= finalChance;
		MobHeads.debug(
			"\nPlayer: " + player.getName() +
			"\n  Type: " + MobHeadConverter.encode(mobHead) +
			"\n  Chance: " + chance +
			"\n  Looting bonus: " + looting +
			"\n  Boost: " + boost +
			"\n  Final chance: " + finalChance +
			"\n  Random: " + random +
			"\n  Drop: " + drop);

		if (drop) {
			player.getWorld().dropItemNaturally(victim.getLocation(), skull);

			if (victim instanceof Player)
				Discord.staffLog("**[MobHeads]** Dropped " + Nickname.of(victim) + "'s head for " + Nickname.of(player) + " with texture " + SkinCache.of(victim).getTextureUrl());
		}

		new MobHeadUserService().edit(player, user -> {
			MobHeadData data = user.get(mobHead);

			data.kill();
			if (drop)
				data.head();
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPickupPlayerSkull(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();
		if (!MaterialTag.SKULLS.isTagged(itemStack.getType()))
			return;

		if (item.getItemStack().getItemMeta().getLore() != null && !item.getItemStack().getItemMeta().getLore().isEmpty())
			return;

		UUID skullOwner = ItemUtils.getSkullOwner(itemStack);
		if (skullOwner != null) {
			for (ItemStack mobHead : MobHeadType.getAllSkulls()) {
				if (!MaterialTag.SKULLS.isTagged(mobHead.getType()))
					continue;

				UUID mobOwner = ItemUtils.getSkullOwner(mobHead);
				if (mobOwner != null && mobOwner.equals(skullOwner)) {
					item.setItemStack(mobHead.clone());
					item.getItemStack().setAmount(itemStack.getAmount());
					break;
				}
			}
		} else {
			Material itemType = itemStack.getType();

			// Should only be triggered by player heads, another plugin handles it as needed.
			if (!MaterialTag.MOB_SKULLS.isTagged(itemType))
				return;

			Optional<ItemStack> skull = MobHeadType.getAllSkulls()
				.stream()
				.filter(mobHead -> mobHead.getType().equals(itemType))
				.findFirst();

			if (skull.isEmpty())
				return;

			item.setItemStack(skull.get());
			item.getItemStack().setAmount(itemStack.getAmount());
		}
	}

	private static final Set<EntityType> SPAWNER_BYPASS = Set.of(
		EntityType.BLAZE,
		EntityType.CAVE_SPIDER
	);

	private static final Set<SpawnReason> SPAWNER_REASONS = Set.of(
		SpawnReason.SPAWNER,
		SpawnReason.SPAWNER_EGG
	);

	private static final Set<SpawnReason> UNNATURAL_REASONS = Set.of(
		SpawnReason.SPAWNER,
		SpawnReason.SPAWNER_EGG,
		SpawnReason.CUSTOM,
		SpawnReason.BUILD_IRONGOLEM,
		SpawnReason.BUILD_WITHER,
		SpawnReason.COMMAND
	);

	private static final Set<DamageCause> PLAYER_DAMAGE_CAUSES = Set.of(
		DamageCause.ENTITY_ATTACK,
		DamageCause.ENTITY_SWEEP_ATTACK,
		DamageCause.PROJECTILE,
		DamageCause.THORNS,
		DamageCause.MAGIC
	);

	public static boolean shouldIgnore(Player player, LivingEntity victim) {
		if (player == null || victim == null)
			return true;

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return true;
		if (player.getGameMode() != GameMode.SURVIVAL)
			return true;

		if (isBaby(victim))
			return true;

		EntityDamageEvent damageCause = victim.getLastDamageCause();
		if (damageCause != null && !PLAYER_DAMAGE_CAUSES.contains(damageCause.getCause()))
			return true;

		if (handledEntities.contains(victim.getUniqueId()))
			return true;

		if (victim instanceof Player)
			if (isNewSkin(victim))
				return true;

		return shouldIgnore(victim);
	}

	private static boolean isNewSkin(LivingEntity player) {
		final SkinCache skinCache = SkinCache.of(player);
		skinCache.update();

		final LocalDateTime lastChanged = skinCache.getLastChanged();
		if (lastChanged == null)
			return true;

		return lastChanged.isAfter(LocalDateTime.now().minusDays(REQUIRED_SKIN_DAYS));
	}

	private static boolean shouldIgnore(LivingEntity entity) {
		EntityType type = entity.getType();
		SpawnReason reason = entity.getEntitySpawnReason();

		if (SPAWNER_BYPASS.contains(type) && SPAWNER_REASONS.contains(reason))
			return false;

		return UNNATURAL_REASONS.contains(reason);
	}

	private double getLooting(Player killer) {
		int looting = 0;
		final ItemMeta weapon = killer.getInventory().getItemInMainHand().getItemMeta();
		if (weapon != null && weapon.hasEnchant(Enchant.LOOTING))
			looting = weapon.getEnchantLevel(Enchant.LOOTING);
		return looting / 10d;
	}

	private static boolean isBaby(LivingEntity entity) {
		if (entity instanceof Ageable ageable)
			return !ageable.isAdult();

		if (entity instanceof Slime slime)
			if (slime.getSize() == 1)
				return true;

		return false;
	}

}
