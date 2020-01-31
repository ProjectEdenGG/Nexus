package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;

import java.util.concurrent.CompletableFuture;

public class HomesCommand extends CustomCommand {
	HomeService service;

	public HomesCommand(CommandEvent event) {
		super(event);
		service = new HomeService();
	}

	@Path("getHomeOwner")
	void getHomeOwner() {
		CompletableFuture<HomeOwner> future = service.get(player());
		future.thenAcceptAsync(homeOwner -> send("Home owner: " + homeOwner));
	}

	@Path("getHome <name>")
	void getHome(String name) {
		send("Home: " + service.getHome(player().getUniqueId().toString(), name));
	}

	@Path("getHomeNames")
	void getHomeNames() {
		send("Names: " + service.getHomeNames(player().getUniqueId().toString()));
	}

//	@Path("edit [home]")
//	void edit(Home home) {
//		if (home == null)
//			HomesMenu.edit(player());
//		else
//			HomesMenu.edit(player(), home);
//	}


}
