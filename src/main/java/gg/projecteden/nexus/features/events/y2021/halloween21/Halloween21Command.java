package gg.projecteden.nexus.features.events.y2021.halloween21;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.halloween21.Halloween21User;
import gg.projecteden.nexus.models.halloween21.Halloween21UserService;
import lombok.NonNull;

@Disabled
@HideFromWiki
public class Halloween21Command extends CustomCommand {
	private final Halloween21UserService service = new Halloween21UserService();
	private Halloween21User user;

	public Halloween21Command(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("pickupCandy [enabled]")
	void pickupCandy(Boolean enabled) {
		if (enabled == null)
			enabled = !user.isPickupCandy();

		user.setPickupCandy(enabled);
		service.save(user);
		send(PREFIX + "Picking up candy " + (enabled ? "&aenabled" : "&cdisabled"));
	}

}
