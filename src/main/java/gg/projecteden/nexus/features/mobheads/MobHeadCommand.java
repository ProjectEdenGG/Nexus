package gg.projecteden.nexus.features.mobheads;

import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.MobHeadConverter;
import gg.projecteden.nexus.models.mobheads.MobHeadChanceConfigService;
import gg.projecteden.nexus.models.mobheads.MobHeadUser;
import gg.projecteden.nexus.models.mobheads.MobHeadUser.MobHeadData;
import gg.projecteden.nexus.models.mobheads.MobHeadUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import kotlin.Pair;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@NoArgsConstructor
@Aliases("mobheads")
public class MobHeadCommand extends CustomCommand implements Listener {
	private final MobHeadUserService service = new MobHeadUserService();

	public MobHeadCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void menu() {
		new MobHeadUserMenu().open(player());
	}

	@Permission(Group.ADMIN)
	@Path("get <type>")
	void mobHead(MobHead mobHead) {
		giveItem(mobHead.getSkull());
	}

	@Path("debug [enable]")
	@Permission(Group.ADMIN)
	void debug(Boolean enable) {
		if (enable == null)
			enable = !MobHeads.isDebug();

		MobHeads.setDebug(enable);
		send(PREFIX + "Debug " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("validate types")
	@Permission(Group.ADMIN)
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

	@Path("chances validate")
	@Permission(Group.ADMIN)
	void chances_validate() {
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

	@Permission(Group.ADMIN)
	@Path("chances get <type>")
	void chances_get(MobHeadType type) {
		send(PREFIX + "Chance of drop for &e" + camelCase(type) + "&3: &e" + StringUtils.getDf().format(type.getChance()));
	}

	@Permission(Group.ADMIN)
	@Path("chances set <type> <chance>")
	void chances_set(MobHeadType type, double chance) {
		new MobHeadChanceConfigService().edit0(config -> config.getChances().put(type, chance));
		send(PREFIX + "Set chance of drop for &e" + camelCase(type) + " &3to &e" + StringUtils.getDf().format(chance));
	}

	@Path("top kills [page]")
	void topKills(@Arg("1") int page) {
		var top = getTop(MobHeadData::getKills);
		paginate(Utils.sortByValueReverse(top).keySet(), getTopFormatter(top), "mobheads top kills", page);
	}

	@Path("top heads [page]")
	void topHeads(@Arg("1") int page) {
		var top = getTop(MobHeadData::getHeads);
		paginate(Utils.sortByValueReverse(top).keySet(), getTopFormatter(top), "mobheads top heads", page);
	}

	@NotNull
	private Map<Pair<MobHeadUser, MobHead>, Integer> getTop(Function<MobHeadData, Integer> getter) {
		return new HashMap<>() {{
			for (MobHeadUser user : service.getAll())
				for (MobHeadData data : user.getData())
					if (getter.apply(data) > 0)
						put(new Pair<>(user, data.getMobHead()), getter.apply(data));
		}};
	}

	@NotNull
	private BiFunction<Pair<MobHeadUser, MobHead>, String, JsonBuilder> getTopFormatter(Map<Pair<MobHeadUser, MobHead>, Integer> top) {
		return (pair, index) -> json(index + " " + pair.getFirst().getNerd().getColoredName()
			+ " &7- &e" + pair.getSecond().getDisplayName() + " &7- " + top.get(pair));
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
