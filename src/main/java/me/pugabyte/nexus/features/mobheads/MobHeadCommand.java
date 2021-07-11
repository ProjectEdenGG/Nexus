package me.pugabyte.nexus.features.mobheads;

import eden.utils.EnumUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.mobheads.MobHeadType.MobHeadVariant;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.Enchant;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static eden.utils.RandomUtils.chanceOf;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@NoArgsConstructor
@Aliases("mobheads")
@Permission("group.admin")
public class MobHeadCommand extends CustomCommand implements Listener {

	public MobHeadCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("get <entityType> [variant]")
	void mobHead(MobHeadType mobHeadType, @Arg(value = "NONE", context = 1) MobHeadVariant variant) {
		giveItem(mobHeadType.getSkull(variant));
	}

	@Path("reload")
	void reload() {
		MobHeadType.load();
		send(PREFIX + "Reloaded");
	}

	@ConverterFor(MobHeadVariant.class)
	MobHeadVariant convertToMobHeadVariant(String value, MobHeadType context) {
		if (context.getVariant() == null)
			return null;
		try {
			return EnumUtils.valueOf(context.getVariant(), value);
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException(camelCase(context) + " variant from &e" + value + " &cnot found");
		}
	}

	@TabCompleterFor(MobHeadVariant.class)
	List<String> tabCompleteMobHeadVariant(String filter, MobHeadType context) {
		if (context == null)
			return new ArrayList<>();

		return tabCompleteEnum(filter, (Class<? extends Enum<?>>) context.getVariant());
	}

	private static final List<EntityType> exclude = List.of(EntityType.ARMOR_STAND, EntityType.GIANT);

	@Path("checkTypes")
	void checkTypes() {
		List<EntityType> allEntityTypes = new ArrayList<>(Arrays.asList(EntityType.values()));
		allEntityTypes.removeIf(entityType -> {
			if (entityType.getEntityClass() == null)
				return true;
			if (!LivingEntity.class.isAssignableFrom(entityType.getEntityClass()))
				return true;
			if (exclude.contains(entityType))
				return true;

			return false;
		});

		for (MobHeadType mobHeadType : MobHeadType.values())
			allEntityTypes.remove(mobHeadType.getType());

		if (allEntityTypes.isEmpty()) {
			send(PREFIX + "All entity types have defined mob heads");
			return;
		}

		send(PREFIX + "Missing entity types:");
		for (EntityType entityType : allEntityTypes)
			send(" &e" + camelCase(entityType));
	}

	@Path("checkChances")
	void checkChances() {
		List<MobHeadType> zeroChance = new ArrayList<>();
		for (MobHeadType type : MobHeadType.values())
			if (type.getChance() == 0)
				zeroChance.add(type);

		if (zeroChance.isEmpty()) {
			send(PREFIX + "All mobs have a defined chance greater than 0");
			return;
		}

		send(PREFIX + "Mobs with 0% chance to drop head:");
		for (MobHeadType type : zeroChance)
			send(" &e" + camelCase(type));
	}

	private static final List<UUID> handledEntities = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKillEntity(EntityDeathEvent event) {
		if (event.isCancelled())
			return;

		LivingEntity victim = event.getEntity();
		Player player = victim.getKiller();
		if (player == null) return;

		// TODO: Remove when done
		if (!Rank.of(player).isStaff()) return;
		//

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL) return;
		if (player.getGameMode() != GameMode.SURVIVAL) return;
		if (shouldIgnore(victim)) return;
		if (isBaby(victim)) return;
		if (handledEntities.contains(victim.getUniqueId())) return;
		handledEntities.add(victim.getUniqueId());

		event.getDrops().removeIf(item -> MaterialTag.SKULLS.isTagged(item.getType()));

		EntityType type = victim.getType();
		MobHeadType mobHeadType = MobHeadType.of(type);
		ItemStack skull = MobHeadType.getSkull(victim);

		if (isNullOrAir(skull))
			return;

		if (victim instanceof Player)
			skull = new ItemBuilder(skull).name("&e" + ((Player) victim).getDisplayName() + "'s Head").skullOwner((OfflinePlayer) victim).build();

		double chance = mobHeadType.getChance();

		if (chance == 0) {
			Nexus.warn("[MobHeads] Chance for " + camelCase(type) + " head is 0");
			return;
		}

		chance += getLooting(player);

		if (chanceOf(chance))
			player.getWorld().dropItemNaturally(victim.getLocation(), skull);
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
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPickupPlayerSkull(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		// TODO: Remove when done
		if (!Rank.of(player).isStaff()) return;
		//

		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();
		if (!MaterialTag.SKULLS.isTagged(itemStack.getType()))
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
			boolean vanillaSkull = new MaterialTag(MaterialTag.SKULLS).exclude(Material.PLAYER_HEAD).isTagged(itemType);

			// Should only be triggered by player heads, another plugin handles it as needed.
			if (!vanillaSkull)
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

	private static final Set<EntityType> spawnerOnlyMobs = Set.of(EntityType.BLAZE, EntityType.CAVE_SPIDER);
	private static final Set<SpawnReason> spawnerOnlyMobSpawnReasons = Set.of(SpawnReason.SPAWNER, SpawnReason.SPAWNER_EGG);

	private boolean shouldIgnore(LivingEntity entity) {
		EntityType type = entity.getType();
		SpawnReason reason = entity.getEntitySpawnReason();

		// Special cases
		if (spawnerOnlyMobs.contains(type) && spawnerOnlyMobSpawnReasons.contains(reason))
			return false;

		// Only drop heads of slime/magma cube if they are not size 0
		if (entity instanceof Slime slime)
			return slime.getSize() == 0;

		//


		return switch (entity.getEntitySpawnReason()) {
			case SPAWNER_EGG, SPAWNER, CUSTOM, BUILD_IRONGOLEM, BUILD_WITHER, COMMAND -> true;
			default -> false;
		};
	}

}
