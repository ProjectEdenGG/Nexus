package me.pugabyte.nexus.features.commands;

import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import me.pugabyte.nexus.features.commands.TameablesCommand.PendingTameblesAction.PendingTameablesActionType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks.GlowTask;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.inventivetalent.glow.GlowAPI.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class TameablesCommand extends CustomCommand implements Listener {
	private static final Map<UUID, PendingTameblesAction> actions = new HashMap<>();
	private static final Map<UUID, Entity> moveQueue = new HashMap<>();
	private static final String PREFIX = StringUtils.getPrefix("Tameables");

	TameablesCommand(CommandEvent event) {
		super(event);
	}

	@Path("(info|view)")
	void info() {
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.INFO));
		send(PREFIX + "Punch the animal you wish to view information on");
	}

	@Path("untame")
	@Description("Remove ownership of an animal")
	void untame() {
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.UNTAME));
		send(PREFIX + "Punch the animal you wish to remove ownership of");
	}

	@Path("move")
	@Description("Teleport an animal to your location")
	void move() {
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.MOVE));
		send(PREFIX + "Punch the animal you wish to move");
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("move here")
	void moveHere() {
		if (!moveQueue.containsKey(uuid()))
			error("You do not have any animal pending teleport");
		Entity entity = moveQueue.remove(uuid());
		entity.teleport(player());
		send(PREFIX + "Summoned your " + camelCase(entity.getType()));
	}

	@Path("transfer <player>")
	@Description("Transfer ownership of an animal to another player")
	void transfer(OfflinePlayer transfer) {
		if (player().equals(transfer))
			error("You can't transfer an animal to yourself");
		actions.put(uuid(), new PendingTameblesAction(PendingTameablesActionType.TRANSFER, transfer));
		send(PREFIX + "Punch the animal you wish to transfer to " + nickname(transfer));
	}

	@Path("count <entityType>")
	@Description("Count the animals you own (Must be in loaded chunks)")
	void count(TameableEntity entityType) {
		send(PREFIX + "Found &e" + list(entityType).size() + " " + camelCase(entityType) + " &3in loaded chunks belonging to you");
	}

	@Path("summon <entityType>")
	@Description("Summon the animals you own (Must be in loaded chunks)")
	void summon(SummonableTameableEntity entityType) {
		List<Entity> entities = list(entityType);
		entities.forEach(entity -> entity.teleport(player()));
		send(PREFIX + "Summoned &e" + entities.size() + " " + camelCase(entityType) + "s &3in loaded chunks to your location");
	}

	@Path("find [entityType]")
	@Description("Make your nearby animals glow so you can find them")
	void find(TameableEntity entityType) {
		List<Entity> entities = list(entityType);
		entities.forEach(entity -> GlowTask.builder()
				.entity(entity)
				.color(Color.RED)
				.viewers(Collections.singletonList(player()))
				.duration(Time.SECOND.x(10))
				.start());
		send(PREFIX + "Highlighted &e" + entities.size() + " " + (entityType == null ? "animals" : camelCase(entityType) + "s") + " &3in loaded chunks");
	}

	private List<Entity> list(TameableEntityList entityType) {
		List<Entity> entities = new ArrayList<>();
		for (World world : Bukkit.getWorlds())
			if (WorldGroup.get(world) == WorldGroup.get(player()))
				for (Entity entity : world.getEntities())
					if (entityType == null || entityType.name().equals(entity.getType().name()))
						if (TameableEntity.isTameable(entity.getType()) && isOwner(player(), entity))
							entities.add(entity);

		if (entities.isEmpty())
			error("Could not find any " + camelCase(entityType.name()) + " in loaded chunks belonging to you");

		return entities;
	}

	private interface TameableEntityList {
		String name();
	}

	private enum SummonableTameableEntity implements TameableEntityList {
		WOLF,
		CAT,
		FOX,
		PARROT;

		public static boolean isSummonable(EntityType entityType) {
			try {
				valueOf(entityType.name());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	private enum TameableEntity implements TameableEntityList {
		WOLF,
		CAT,
		FOX,
		PARROT,
		HORSE,
		SKELETON_HORSE,
		DONKEY,
		MULE,
		LLAMA;

		public static boolean isTameable(EntityType entityType) {
			try {
				valueOf(entityType.name());
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class PendingTameblesAction {
		@NonNull
		private PendingTameablesActionType type;
		@With
		private OfflinePlayer player;

		public enum PendingTameablesActionType {
			TRANSFER,
			UNTAME,
			MOVE,
			INFO
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;

		Player player = (Player) event.getDamager();
		UUID uuid = player.getUniqueId();
		Entity entity = event.getEntity();
		String entityName = camelCase(entity.getType());

		try {
			if (actions.containsKey(uuid)) {
				event.setCancelled(true);
				if (!TameableEntity.isTameable(event.getEntityType())) {
					send(player, PREFIX + "&cThat animal is not tameable");
					actions.remove(uuid);
					return;
				}

				PendingTameblesAction action = actions.get(uuid);
				switch (action.getType()) {
					case TRANSFER:
						checkOwner(player, entity);
						OfflinePlayer transfer = action.getPlayer();
						updateOwner(entity, player, transfer);
						send(player, PREFIX + "You have transferred the ownership of your " + entityName + " to " + Nickname.of(transfer));
						break;
					case UNTAME:
						checkOwner(player, entity);
						updateOwner(entity, player, null);
						send(player, PREFIX + "You have untamed your " + entityName);
						break;
					case MOVE:
						if (!SummonableTameableEntity.isSummonable(event.getEntityType())) {
							send(player, PREFIX + "&cThat animal is not moveable");
							actions.remove(uuid);
							return;
						}
						checkOwner(player, entity);
						moveQueue.put(player.getUniqueId(), event.getEntity());
						send(player, json(PREFIX + "Click here to summon your animal when you are ready").command("/tameables move here"));
						break;
					case INFO:
						String owner = getOwner(entity);
						send(player, PREFIX + "That " + entityName + " is " + (isNullOrEmpty(owner) ? "not tamed" : "owned by &e" + owner));
						break;
				}
				actions.remove(uuid);
			}
		} catch (InvalidInputException ex) {
			send(player, new JsonBuilder(PREFIX).next(ex.getJson()));
		}
	}

	private void updateOwner(Entity entity, Player player, OfflinePlayer newOwner) {
		if (entity instanceof Tameable) {
			((Tameable) entity).setOwner(newOwner);
		} else if (entity instanceof Fox) {
			Fox fox = (Fox) entity;
			if (fox.getFirstTrustedPlayer() != null && fox.getFirstTrustedPlayer().getUniqueId().equals(player.getUniqueId()))
				fox.setFirstTrustedPlayer(newOwner);
			else if (fox.getSecondTrustedPlayer() != null && fox.getSecondTrustedPlayer().getUniqueId().equals(player.getUniqueId())) {
				fox.setSecondTrustedPlayer(newOwner);
			}
		}
	}

	private void checkOwner(Player player, Entity tameable) {
		if (!isOwner(player, tameable) && !isSeniorStaff(player))
			error("You do not own that animal!");
	}

	private boolean isOwner(Player player, Entity entity) {
		if (entity instanceof Tameable) {
			AnimalTamer tamer = ((Tameable) entity).getOwner();
			return tamer != null && tamer.equals(player);
		} else if (entity instanceof Fox) {
			Fox fox = (Fox) entity;
			return fox.getFirstTrustedPlayer() == player || fox.getSecondTrustedPlayer() == player;
		}
		return false;
	}

	private String getOwner(Entity entity) {
		if (entity instanceof Tameable) {
			AnimalTamer tamer = ((Tameable) entity).getOwner();
			if (tamer != null)
				return tamer.getName();
		} else if (entity instanceof Fox) {
			Fox fox = (Fox) entity;
			List<String> names = new ArrayList<>();
			if (fox.getFirstTrustedPlayer() != null)
				names.add(fox.getFirstTrustedPlayer().getName());
			if (fox.getSecondTrustedPlayer() != null)
				names.add(fox.getSecondTrustedPlayer().getName());
			if (!names.isEmpty())
				return String.join(" and ", names);
		}
		return null;
	}

}
