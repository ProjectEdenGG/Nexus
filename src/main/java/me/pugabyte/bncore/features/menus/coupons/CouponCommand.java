package me.pugabyte.bncore.features.menus.coupons;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class CouponCommand extends CustomCommand {

	public CouponCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void test() {

	}


}
