package me.pugabyte.nexus.features.mobheads;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.mobheads.common.MobHead;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.MobHeadConverter;
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

	@Path
	void menu() {
		new MobHeadUserMenu().open(player());
	}

	@Permission("group.admin")
	@Path("get <type>")
	void mobHead(MobHead mobHead) {
		giveItem(mobHead.getSkull());
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		MobHeadType.load();
		send(PREFIX + "Reloaded");
	}

	@Path("validate types")
	@Permission("group.admin")
	void validateTypes() {
		final List<EntityType> missingTypes = MobHeadType.getMissingTypes();

		if (missingTypes.isEmpty()) {
			send(PREFIX + "All entity types have defined mob heads");
			return;
		}

		send(PREFIX + "Missing entity types:");
		for (EntityType entityType : missingTypes)
			send(" &e" + camelCase(entityType));
	}

	@Path("validate chances")
	@Permission("group.admin")
	void validateChances() {
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

	@ConverterFor(MobHead.class)
	MobHead convertToMobHead(String value) {
		return MobHeadConverter.decode(value);
	}

	@TabCompleterFor(MobHead.class)
	List<String> tabCompleteMobHead(String filter) {
		return new ArrayList<>() {{
			for (MobHeadType mobHeadType : MobHeadType.values())
				if (mobHeadType.hasVariants()) {
					for (MobHeadVariant variant : mobHeadType.getVariants())
						add((mobHeadType.name() + "." + variant.name()).toLowerCase());
				} else
					add(mobHeadType.name().toLowerCase());
		}};
	}

}
