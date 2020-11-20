package me.pugabyte.nexus.features.menus.coupons;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class CouponCommand extends CustomCommand {

	public CouponCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void test() {

	}


}
