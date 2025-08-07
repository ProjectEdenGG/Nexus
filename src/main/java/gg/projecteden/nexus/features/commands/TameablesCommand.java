package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.TameablesCommand.PendingTameablesAction.PendingTameablesActionType;
import gg.projecteden.nexus.features.listeners.Restrictions;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Allay;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TameablesCommand extends CustomCommand implements Listener {
	private static final Map<UUID, PendingTameablesAction> actions = new HashMap<>();
	private static final Map<UUID, Entity> moveQueue = new HashMap<>();
	private static final String PREFIX = StringUtils.getPrefix("Tameables");
	private static final NamespacedKey SUMMON_LOCK_KEY = new NamespacedKey(Nexus.getInstance(), "summon-lock");

	TameablesCommand(CommandEvent event) {
		super(event);
	}

	@Path("(info|view)")
	@Description("View the owner of a tameable entity")
	void info() {
		actions.put(uuid(), new PendingTameablesAction(PendingTameablesActionType.INFO));
		send(PREFIX + "Punch the animal you wish to view information on");
	}

	@Path("untame")
	@Description("Remove ownership of an animal")
	void untame() {
		actions.put(uuid(), new PendingTameablesAction(PendingTameablesActionType.UNTAME));
		send(PREFIX + "Punch the animal you wish to remove ownership of");
	}

	@Path("sit <entityType>")
	@Description("Make all your animals sit")
	void sit(SittableTameableEntityType entityType) {
		int count = 0;
		for (Entity entity : list(entityType))
			if (entity instanceof Sittable sittable) {
				sittable.setSitting(true);
				++count;
			}

		send(PREFIX + "Made &e" + count + " " + entityType.plural(count) + " &3sit");
	}

	@Path("stand <entityType>")
	@Description("Make all your animals stand up")
	void stand(SittableTameableEntityType entityType) {
		int count = 0;
		for (Entity entity : list(entityType))
			if (entity instanceof Sittable sittable) {
				sittable.setSitting(false);
				++count;
			}

		send(PREFIX + "Made &e" + count + " " + entityType.plural(count) + " &3stand up");
	}

	@Path("move")
	@Description("Teleport an animal to your location")
	void move() {
		actions.put(uuid(), new PendingTameablesAction(PendingTameablesActionType.MOVE));
		send(PREFIX + "Punch the animal you wish to move");
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("move here")
	void moveHere() {
		if (!moveQueue.containsKey(uuid()))
			error("You do not have any animal pending teleport");
		if (!Restrictions.isPerkAllowedAt(player(), location()))
			error("You cannot teleport that animal to this location");

		Entity entity = moveQueue.remove(uuid());
		entity.teleportAsync(location());
		send(PREFIX + "Summoned your " + camelCase(entity.getType()));
	}

	@Path("transfer <player>")
	@Description("Transfer ownership of an animal to another player")
	void transfer(OfflinePlayer transfer) {
		if (player().equals(transfer))
			error("You can't transfer an animal to yourself");
		actions.put(uuid(), new PendingTameablesAction(PendingTameablesActionType.TRANSFER, transfer));
		send(PREFIX + "Punch the animal you wish to transfer to " + nickname(transfer));
	}

	@Path("count <entityType>")
	@Description("Count the animals you own (Must be in loaded chunks)")
	void count(TameableEntityType entityType) {
		final int count = list(entityType).size();
		send(PREFIX + "Found &e" + count + " " + entityType.plural(count) + " &3in loaded chunks belonging to you");
	}

	@Path("summon <entityType>")
	@Description("Summon the animals you own (Must be in loaded chunks)")
	void summon(SummonableTameableEntityType entityType) {
		int failed = 0, succeeded = 0, locked = 0;
		for (Entity entity : list(entityType)) {
			if (!entity.getPersistentDataContainer().has(SUMMON_LOCK_KEY)) {
				if (Restrictions.isPerkAllowedAt(player(), location())) {
					entity.teleportAsync(location());
					++succeeded;
				} else {
					++failed;
				}
			} else {
				++locked;
			}
		}

		if (succeeded > 0)
			send(PREFIX + "Summoned &e" + succeeded + " " + entityType.plural(succeeded) + " &3in loaded chunks to your location");
		if (failed > 0)
			send(PREFIX + "Failed to teleport &e" + failed + " " + entityType.plural(failed) + " to your location &3(not allowed here)");
		if (locked > 0)
			send(PREFIX + "Ignored &e" + locked + " " + entityType.plural(locked) + " &3(locked with &c/tameables summon lock&3)");
	}

	@Path("summon lock")
	@Description("Prevent an animal from being summoned")
	void summon_lock() {
		actions.put(uuid(), new PendingTameablesAction(PendingTameablesActionType.LOCK));
		send(PREFIX + "Punch the animal you wish to lock");
	}

	@Path("summon unlock")
	@Description("Allow a previously locked animal to be summoned again")
	void summon_unlock() {
		actions.put(uuid(), new PendingTameablesAction(PendingTameablesActionType.UNLOCK));
		send(PREFIX + "Punch the animal you wish to unlock");
	}

	@Path("find <entityType>")
	@Description("Make your nearby animals glow so you can find them")
	void find(TameableEntityType entityType) {
		List<Entity> entities = list(entityType);
		entities.forEach(entity ->
			GlowUtils.GlowTask.builder()
				.entity(entity)
				.color(GlowColor.RED)
				.viewers(Collections.singletonList(player()))
				.duration(TickTime.SECOND.x(10))
				.start());
		send(PREFIX + "Highlighted &e" + entities.size() + " " + (entityType == null ? "animals" : entityType.plural(entities.size())) + " &3in loaded chunks");
	}

	private List<Entity> list(ITameableEntityType entityType) {
		List<Entity> entities = new ArrayList<>();
		for (World world : Bukkit.getWorlds())
			if (WorldGroup.of(world) == WorldGroup.of(player()))
				if (WorldGroup.of(world) != WorldGroup.LEGACY)
					for (Entity entity : world.getEntities())
						if (entityType == null || entityType.name().equals(entity.getType().name()))
							if (TameableEntityType.isTameable(entity.getType()) && isOwner(player(), entity))
								entities.add(entity);

		if (entities.isEmpty())
			error("Could not find any " + (entityType == null ? "animals" : camelCase(entityType.name())) + " in loaded chunks belonging to you");

		return entities;
	}

	public interface ITameableEntityType {
		String name();

		default String plural(int count) {
			return count == 1 ? StringUtils.camelCase(name()) : plural();
		}

		default String plural() {
			return TameableEntityType.valueOf(name()).plural();
		}
	}

	public enum SittableTameableEntityType implements ITameableEntityType {
		WOLF,
		CAT,
		PARROT,
		;

		public static boolean isSittable(EntityType entityType) {
			try {
				valueOf(entityType.name());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	public enum SummonableTameableEntityType implements ITameableEntityType {
		WOLF,
		CAT,
		FOX,
		PARROT,
		ALLAY,
		;

		public static boolean isSummonable(EntityType entityType) {
			try {
				valueOf(entityType.name());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	@AllArgsConstructor
	public enum TameableEntityType implements ITameableEntityType {
		WOLF("Wolves"),
		CAT("Cats"),
		FOX("Foxes"),
		PARROT("Parrots"),
		HORSE("Horses"),
		SKELETON_HORSE("Skeleton Horses"),
		DONKEY("Donkeys"),
		MULE("Mules"),
		LLAMA("Llamas"),
		ALLAY("Allays"),
		CAMEL("Camels")
		;

		private final String plural;

		public static TameableEntityType of(EntityType type) {
			return valueOf(type.name());
		}

		@Override
		public String plural() {
			return plural;
		}

		public static boolean isTameable(EntityType entityType) {
			try {
				of(entityType);
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class PendingTameablesAction {
		@NonNull
		private PendingTameablesActionType type;
		private OfflinePlayer player;

		public enum PendingTameablesActionType {
			TRANSFER,
			UNTAME,
			MOVE,
			LOCK,
			UNLOCK,
			INFO
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;

		UUID uuid = player.getUniqueId();
		Entity entity = event.getEntity();
		String entityName = camelCase(entity.getType());

		try {
			if (actions.containsKey(uuid)) {
				event.setCancelled(true);
				if (!TameableEntityType.isTameable(event.getEntityType())) {
					send(player, PREFIX + "&cThat animal is not tameable");
					actions.remove(uuid);
					return;
				}

				PendingTameablesAction action = actions.get(uuid);
				switch (action.getType()) {
					case TRANSFER -> {
						checkOwner(player, entity);
						OfflinePlayer transfer = action.getPlayer();
						updateOwner(entity, player, transfer);
						send(player, PREFIX + "You have transferred the ownership of your " + entityName + " to " + Nickname.of(transfer));
					}
					case UNTAME -> {
						checkOwner(player, entity);
						updateOwner(entity, player, null);
						send(player, PREFIX + "You have untamed your " + entityName);
					}
					case MOVE -> {
						if (!SummonableTameableEntityType.isSummonable(event.getEntityType())) {
							send(player, PREFIX + "&cThat animal is not moveable");
							actions.remove(uuid);
							return;
						}
						checkOwner(player, entity);
						moveQueue.put(player.getUniqueId(), event.getEntity());
						send(player, json(PREFIX + "Click here to summon your animal when you are ready").command("/tameables move here"));
					}
					case LOCK -> {
						if (!SummonableTameableEntityType.isSummonable(event.getEntityType())) {
							send(player, PREFIX + "&cThat animal is not summonable");
							actions.remove(uuid);
							return;
						}
						checkOwner(player, entity);
						entity.getPersistentDataContainer().set(SUMMON_LOCK_KEY, PersistentDataType.BOOLEAN, true);
						send(player, PREFIX + "That animal is now locked and will not be summoned until unlocked");
					}
					case UNLOCK -> {
						if (!entity.getPersistentDataContainer().has(SUMMON_LOCK_KEY)) {
							send(player, PREFIX + "&cThat animal is not locked");
							actions.remove(uuid);
							return;
						}
						checkOwner(player, entity);
						entity.getPersistentDataContainer().remove(SUMMON_LOCK_KEY);
						send(player, PREFIX + "That animal is now unlocked and can be summoned");
					}
					case INFO -> {
						String owner = getOwnerNames(entity);
						send(player, PREFIX + "That " + entityName + " is " + (Nullables.isNullOrEmpty(owner) ? "not tamed" : "owned by &e" + owner));
					}
				}
				actions.remove(uuid);
			}
		} catch (InvalidInputException ex) {
			send(player, new JsonBuilder(PREFIX).next(ex.getJson()));
		}
	}

	private void updateOwner(Entity entity, Player player, OfflinePlayer newOwner) {
		if (entity instanceof Tameable tameable) {
			tameable.setOwner(newOwner);
		} else if (entity instanceof Fox fox) {
			if (fox.getFirstTrustedPlayer() != null && fox.getFirstTrustedPlayer().getUniqueId().equals(player.getUniqueId()))
				fox.setFirstTrustedPlayer(newOwner);
			else if (fox.getSecondTrustedPlayer() != null && fox.getSecondTrustedPlayer().getUniqueId().equals(player.getUniqueId())) {
				fox.setSecondTrustedPlayer(newOwner);
			}
		} else if (entity instanceof Allay allay) {
			allay.setMemory(MemoryKey.LIKED_PLAYER, newOwner.getUniqueId());
		}
	}

	public static void checkOwner(Player player, Entity tameable) {
		if (!isOwner(player, tameable) && !Rank.of(player).isSeniorStaff())
			throw new InvalidInputException("You do not own that animal!");
	}

	public static boolean isOwner(Player player, Entity entity) {
		if (entity instanceof Tameable tameable) {
			AnimalTamer tamer = tameable.getOwner();
			return tamer != null && tamer.equals(player);
		} else if (entity instanceof Fox fox) {
			return fox.getFirstTrustedPlayer() == player || fox.getSecondTrustedPlayer() == player;
		} else if (entity instanceof Allay allay) {
			UUID liked = allay.getMemory(MemoryKey.LIKED_PLAYER);
			return liked != null && liked == player.getUniqueId();
		}
		return false;
	}

	public static boolean isTamed(Entity entity) {
		if (entity instanceof Tameable tameable)
			return tameable.getOwner() != null;
		else if (entity instanceof Fox fox)
			return fox.getFirstTrustedPlayer() != null || fox.getSecondTrustedPlayer() != null;
		else if (entity instanceof Allay allay)
			return allay.getMemory(MemoryKey.LIKED_PLAYER) != null;

		return false;
	}

	private List<AnimalTamer> getOwners(Entity entity) {
		List<AnimalTamer> owners = new ArrayList<>();
		if (entity instanceof Tameable tameable) {
			AnimalTamer tamer = tameable.getOwner();
			if (tamer != null)
				owners.add(tamer);
		} else if (entity instanceof Fox fox) {
			if (fox.getFirstTrustedPlayer() != null)
				owners.add(fox.getFirstTrustedPlayer());
			if (fox.getSecondTrustedPlayer() != null)
				owners.add(fox.getSecondTrustedPlayer());
		} else if (entity instanceof Allay allay) {
			UUID liked = allay.getMemory(MemoryKey.LIKED_PLAYER);
			if (liked != null)
				owners.add(PlayerUtils.getPlayer(liked));
		}
		return owners;
	}

	private String getOwnerNames(Entity entity) {
		return getOwners(entity).stream().map(AnimalTamer::getName).collect(Collectors.joining(" and "));
	}

	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent event) {
		List<AnimalTamer> owners = getOwners(event.getEntity());
		if (owners.isEmpty())
			return;

		for (AnimalTamer owner : owners) {
			if (Rank.of(owner.getUniqueId()).gte(Rank.BUILDER))
				return;

			if (Restrictions.isPerkAllowedAt(Nerd.of(owner), event.getTo()))
				return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Allay allay))
			return;

		final UUID owner = allay.getMemory(MemoryKey.LIKED_PLAYER);
		if (owner == null)
			return;

		final Player player = event.getPlayer();
		if (owner.equals(player.getUniqueId()))
			return;

		PlayerUtils.send(player, "&c&lHey! &7You don't own that allay");
		event.setCancelled(true);
	}

}
