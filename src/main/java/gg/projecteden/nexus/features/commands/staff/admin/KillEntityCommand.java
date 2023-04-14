package gg.projecteden.nexus.features.commands.staff.admin;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.ErasureType;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntityService;
import gg.projecteden.nexus.models.imagestand.ImageStandService;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.utils.Utils.combine;
import static java.util.Collections.singletonList;
import static org.bukkit.entity.EntityType.ARMOR_STAND;
import static org.bukkit.entity.EntityType.EVOKER;
import static org.bukkit.entity.EntityType.ILLUSIONER;
import static org.bukkit.entity.EntityType.PILLAGER;
import static org.bukkit.entity.EntityType.RAVAGER;
import static org.bukkit.entity.EntityType.VINDICATOR;
import static org.bukkit.entity.EntityType.WITCH;

@Permission(Group.SENIOR_STAFF)
@Aliases({"killall", "mobkill", "butcher", "killentities"})
public class KillEntityCommand extends CustomCommand {
	private static final CustomBoundingBoxEntityService boundingBoxService = new CustomBoundingBoxEntityService();
	private static final ImageStandService imageStandService = new ImageStandService();

	public KillEntityCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Kill nearby entities of the provided types")
	void run(
		@ErasureType(KillEntityArg.class) List<KillEntityArg> killEntityArg,
		double radius,
		@Switch @Optional @Permission(Group.ADMIN) boolean force,
		@Switch @Optional @Permission(Group.ADMIN) ProtectedRegion region
	) {
		if (!isAdmin() && radius > 200)
			error("Radius cannot be greater than 200");

		List<EntityType> toKill = new ArrayList<>();
		for (KillEntityArg arg : killEntityArg)
			toKill.addAll(arg.getApplicableEntityTypes());
		toKill.remove(EntityType.PLAYER);

		Set<Entity> entities = new HashSet<>();
		for (final Chunk chunk : world().getLoadedChunks())
			for (final Entity entity : chunk.getEntities()) {
				if (!distanceTo(entity).lte(radius))
					continue;
				if (!toKill.contains(entity.getType()))
					continue;
				if (!canKill(entity, force, region))
					continue;

				entities.add(entity);
			}

		entities.forEach(Entity::remove);
		int size = entities.size();
		send(PREFIX + "Killed " + size + " " + plural("entity", "entities", size));
	}

	private static boolean canKill(Entity entity, boolean forced, ProtectedRegion region) {
		if (entity instanceof Player)
			return false;

		if (entity instanceof ArmorStand armorStand) {
			if (boundingBoxService.get(armorStand.getUniqueId()).hasCustomBoundingBox())
				return false;
			if (imageStandService.get(armorStand.getUniqueId()).isActive())
				return false;
		}

		if (region != null && !new WorldGuardUtils(entity).isInRegion(entity.getLocation(), region))
			return false;

		if (forced)
			return true;

		if (entity instanceof Monster) {
			if (entity.getCustomName() != null)
				return false;
			if (entity.isPersistent())
				return false;
		}

		return true;
	}

	@Getter
	public enum KillableEntityGroup {
		ALL(combine(extending(Entity.class), extending(Hanging.class), singletonList(ARMOR_STAND))),
		LIVING(extending(LivingEntity.class)),
		HOSTILE(combine(extending(Monster.class), EntityUtils.getExtraHostileMobs())),
		RAID(List.of(PILLAGER, WITCH, EVOKER, VINDICATOR, RAVAGER, ILLUSIONER)),
		MINECART(extending(Minecart.class));

		private final List<EntityType> entityTypes;

		KillableEntityGroup(List<EntityType> include) {
			this(include, null);
		}

		KillableEntityGroup(List<EntityType> include, List<EntityType> exclude) {
			this.entityTypes = new ArrayList<>(include);
			if (exclude != null)
				this.entityTypes.removeAll(exclude);
		}

		private static List<EntityType> extending(Class<? extends Entity> entityClass) {
			List<EntityType> applicable = new ArrayList<>();
			for (EntityType entityType : EntityType.values())
				if (entityType.getEntityClass() != null && entityClass.isAssignableFrom(entityType.getEntityClass()))
					applicable.add(entityType);

			return applicable;
		}
	}

	@Getter
	public static class KillEntityArg {
		private EntityType entityType;
		private KillableEntityGroup group;

		public KillEntityArg(EntityType entityType) {
			this.entityType = entityType;
		}

		public KillEntityArg(KillableEntityGroup group) {
			this.group = group;
		}

		public List<EntityType> getApplicableEntityTypes() {
			if (entityType != null)
				return singletonList(entityType);
			else if (group != null)
				return group.getEntityTypes();
			else
				return new ArrayList<>();
		}
	}

	@TabCompleterFor(KillEntityArg.class)
	List<String> tabCompleteKillEntityArg(String value) {
		return new ArrayList<>() {{
			addAll(tabCompleteEnum(value, EntityType.class));
			addAll(tabCompleteEnum(value, KillableEntityGroup.class));
			remove(EntityType.NPC.name().toLowerCase());
			remove(EntityType.PLAYER.name().toLowerCase());
		}};
	}

	@ConverterFor(KillEntityArg.class)
	KillEntityArg convertToKillEntityArg(String value) {
		try {
			return new KillEntityArg(convertToEnum(value, KillableEntityGroup.class));
		} catch (InvalidInputException ex) {
			try {
				return new KillEntityArg(convertToEnum(value, EntityType.class));
			} catch (InvalidInputException ex2) {
				throw new InvalidInputException("Could not convert " + value + " to a killable entity");
			}
		}
	}
}
