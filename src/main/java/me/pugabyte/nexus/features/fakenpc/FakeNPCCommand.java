package me.pugabyte.nexus.features.fakenpc;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;

@Permission("group.admin")
public class FakeNPCCommand extends CustomCommand {
	public FakeNPCCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("selected")
	public void selected() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");
		send("You have FakeNPC " + fakeNPC.getUuid() + " selected");
	}

	@Path("create")
	public void create() {
		FakeNPC fakeNPC = FakeNPCManager.createFakeNPC(player());
		FakeNPCManager.setSelected(player(), fakeNPC);

		send("Created & selected FakeNPC");
	}

	@Path("setVisible <visible>")
	public void visible(boolean visible) {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		fakeNPC.setVisible(visible);
		send("Set FakeNPC visibility to: " + visible);
	}

	@Path("tp")
	public void teleportTo() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		player().teleport(fakeNPC.getLocation());
		send("Teleported to FakeNPC");

	}

	@Path("tphere")
	public void teleportHere() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		FakeNPCManager.teleport(fakeNPC, location());
		send("Teleported FakeNPC to " + StringUtils.getShortLocationString(fakeNPC.getLocation()));
	}

	@Path("setMineSkin <url>")
	public void setMineSkin(String url) {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		send("Setting skin to: " + url);
		FakeNPCManager.setMineSkin(fakeNPC, url);
	}


}
