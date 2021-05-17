package me.pugabyte.nexus.features.autosort.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.autosort.AutoSortUserService;
import org.bukkit.event.Listener;


@NoArgsConstructor
public class AutoSortCommand extends CustomCommand implements Listener {
	private final AutoSortUserService service = new AutoSortUserService();
	private AutoSortUser user;

	public AutoSortCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("[feature] [enable]")
	void toggle(AutoSortFeature feature, Boolean enable) {
		if (enable == null)
			enable = !user.isFeatureEnabled(feature);

		if (enable)
			if (!user.getDisabledFeatures().contains(feature))
				error(camelCase(feature) + " is already enabled");
			else
				user.getDisabledFeatures().remove(feature);
		else
			if (user.getDisabledFeatures().contains(feature))
				error(camelCase(feature) + " is already disabled");
			else
				user.getDisabledFeatures().add(feature);

		service.save(user);
		send(PREFIX + camelCase(feature) + " " + (enable ? "&aenabled" : "&cdisabled"));
	}

}
