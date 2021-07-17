package me.pugabyte.nexus.features.mobheads;

import eden.utils.EnumUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Aliases("mobheads")
public class MobHeadCommand extends CustomCommand implements Listener {

	public MobHeadCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		new MobHeadListener();
	}

	@Permission("group.admin")
	@Path("get <entityType> [variant]")
	void mobHead(MobHeadType mobHeadType, @Arg(value = "NONE", context = 1) MobHeadVariant variant) {
		giveItem(mobHeadType.getSkull(variant));
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		MobHeadType.load();
		send(PREFIX + "Reloaded");
	}

	@Path("checkTypes")
	@Permission("group.admin")
	void checkTypes() {
		final List<EntityType> missingTypes = MobHeadType.getMissingTypes();

		if (missingTypes.isEmpty()) {
			send(PREFIX + "All entity types have defined mob heads");
			return;
		}

		send(PREFIX + "Missing entity types:");
		for (EntityType entityType : missingTypes)
			send(" &e" + camelCase(entityType));
	}

	@Path("checkChances")
	@Permission("group.admin")
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

	@ConverterFor(MobHeadVariant.class)
	MobHeadVariant convertToMobHeadVariant(String value, MobHeadType context) {
		if (context.getVariantClass() == null)
			return null;

		try {
			return EnumUtils.valueOf(context.getVariantClass(), value);
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException(camelCase(context) + " variant from &e" + value + " &cnot found");
		}
	}

	@TabCompleterFor(MobHeadVariant.class)
	List<String> tabCompleteMobHeadVariant(String filter, MobHeadType context) {
		if (context == null || !context.hasVariants())
			return new ArrayList<>();

		return tabCompleteEnum(filter, (Class<? extends Enum<?>>) context.getVariantClass());
	}

}
