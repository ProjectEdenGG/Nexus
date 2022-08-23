package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

// TODO: Select, Remove, Skin, Interact, LookClose
@Permission(Group.ADMIN)
public class FakeNPCCommand extends CustomCommand {
	public FakeNPCCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("select")
	@Description("Select the targeted NPC")
	public void select() {
		send(PREFIX + "TODO");
	}

	@Path("info")
	@Description("Display information of the selected NPC")
	public void info() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		send(PREFIX + "Info of &e" + fakeNPC.getName() + "&3:");
		send("&3- UUID: &e" + fakeNPC.getUuid());
		send("&3- Hologram: &e" + fakeNPC.getHologram().getLines());
		send("&3- Location: &e" + StringUtils.getShortLocationString(fakeNPC.getLocation()));
	}

	@Path("create")
	@Description("Create an NPC")
	public void create() {
		FakeNPC fakeNPC = FakeNPCManager.createFakeNPC(player());
		FakeNPCManager.setSelected(player(), fakeNPC);

		send(PREFIX + "Created &e" + fakeNPC.getName());
	}

	@Path("remove")
	@Description("Remove the selected NPC")
	public void remove() {
		send(PREFIX + "TODO");
	}

	@Path("tp")
	@Description("Teleport to the selected NPC")
	public void teleportTo() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		player().teleportAsync(fakeNPC.getLocation());
		send(PREFIX + "Teleported to &e" + fakeNPC.getName());

	}

	@Path("tphere")
	@Description("Teleport the selected NPC to your location")
	public void teleportHere() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		FakeNPCManager.teleport(fakeNPC, location());
		send(PREFIX + "Teleported &e" + fakeNPC.getName() + " &3to &e" + StringUtils.getShortLocationString(fakeNPC.getLocation()));
	}

	@Path("visible <visible>")
	@Description("Set the visibility of the selected NPC")
	public void visible(boolean visible) {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		fakeNPC.setVisible(visible);
		send(PREFIX + "Set &e" + fakeNPC.getName() + "'s &3visibility to: &e" + visible);
	}

	// TODO: doesn't set skin to the url
	@Path("mineskin <url>")
	@Description("Set the skin of the selected NPC using MineSkin")
	public void setMineSkin(String url) {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		FakeNPCManager.setMineSkin(fakeNPC, url);
		send(PREFIX + "Set &e" + fakeNPC.getName() + "'s &3skin to: &e" + url);
	}

	@Path("skin <player>")
	@Description("Set the skin of the selected NPC of a Player")
	public void setSkin(Nerd nerd) {
		send(PREFIX + "TODO");
	}

	@Path("reapplySkin")
	@Description("Reapply the skin of the selected NPC")
	public void reapplySkin() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		fakeNPC.applySkin();
		send(PREFIX + "Reapplied skin of &e" + fakeNPC.getName());
	}

}
