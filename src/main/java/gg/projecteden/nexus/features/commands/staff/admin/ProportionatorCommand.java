package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.recipes.functionals.Proportionator;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.proportionator.ProportionatorConfig;
import gg.projecteden.nexus.models.proportionator.ProportionatorConfigService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.EntityType;

@Permission(Group.ADMIN)
public class ProportionatorCommand extends CustomCommand {
	private final ProportionatorConfigService service = new ProportionatorConfigService();
	private final ProportionatorConfig config = service.get0();

	public ProportionatorCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Get a Proportionator")
	void get() {
		giveItem(Proportionator.ITEM);
	}

	@Path("config entities <entityType> [state]")
	@Description("Toggle whether entities can be scaled")
	void config_disabled(EntityType entityType, Boolean state) {
		if (state == null)
			state = config.getDisabled().contains(entityType);

		if (state)
			config.getDisabled().remove(entityType);
		else
			config.getDisabled().add(entityType);

		service.save(config);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled") + " &3entity type &e" + camelCase(entityType));
	}

	@Path("config min <min>")
	@Description("Set the default minimum scale")
	void config_min(double min) {
		config.setMin(min);
		service.save(config);
		send(PREFIX + "Set default minimum scale to " + min);
	}

	@Path("config max <max>")
	@Description("Set the default maximum scale")
	void config_max(double max) {
		config.setMax(max);
		service.save(config);
		send(PREFIX + "Set default maximum scale to " + max);
	}

	@Path("config min override <entityType> [min]")
	@Description("Set the minimum scale for a specific entity type")
	void config_min_override(EntityType entityType, Double min) {
		if (min == null)
			config.getMinOverrides().remove(entityType);
		else
			config.getMinOverrides().put(entityType, min);

		service.save(config);
		if (min == null)
			send(PREFIX + "Removed minimum scale override for entity type " + entityType);
		else
			send(PREFIX + "Set minimum scale for " + camelCase(entityType) + " to " + StringUtils.getDf().format(min));
	}

	@Path("config max override <entityType> [max]")
	@Description("Set the maximum scale for a specific entity type")
	void config_max_override(EntityType entityType, Double max) {
		if (max == null)
			config.getMaxOverrides().remove(entityType);
		else
			config.getMaxOverrides().put(entityType, max);

		service.save(config);
		if (max == null)
			send(PREFIX + "Removed maximum scale override for entity type " + entityType);
		else
			send(PREFIX + "Set maximum scale for " + camelCase(entityType) + " to " + StringUtils.getDf().format(max));
	}

}
