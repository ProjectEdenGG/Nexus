package me.pugabyte.nexus.features.mobheads;

import eden.utils.EnumUtils;
import lombok.NonNull;
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

import java.util.ArrayList;
import java.util.List;

@Aliases("mobheads")
@Permission("group.admin")
public class MobHeadCommand extends CustomCommand {

	public MobHeadCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("get <entityType> [variant]")
	void mobHead(MobHeadType mobHeadType, @Arg(value = "NONE", context = 1) MobHeadVariant variant) {
		giveItem(mobHeadType.getSkull(variant));
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

	@Path("checkTypes")
	void checkTypes() {
//		List<EntityType> types = Arrays.asList(EntityType.values());
//		for (EntityType entityType : MobHeads.getMobHeads().keySet()) {
//			Class<? extends Entity> entity = entityType.getEntityClass();
//			if (entity != null && LivingEntity.class.isAssignableFrom(entity) && !types.contains(entityType))
//				send("Mob Head not found: " + StringUtils.camelCase(entityType));
//		}
	}

}
