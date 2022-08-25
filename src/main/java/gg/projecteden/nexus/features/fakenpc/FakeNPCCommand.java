package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram;
import gg.projecteden.nexus.features.fakenpc.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.features.fakenpc.types.PlayerNPC;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

@Permission(Group.ADMIN)
public class FakeNPCCommand extends CustomCommand {
	public FakeNPCCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("select")
	@Description("Select an NPC")
	public void select() {
		send(PREFIX + "TODO");
	}

	@Path("info")
	@Description("Display information of the selected NPC")
	public void info() {
		FakeNPC fakeNPC = getSelectedNPC();
		Hologram hologram = fakeNPC.getHologram();

		send(PREFIX + "Info of &e" + fakeNPC.getId() + "&3:");
		send("&3- UUID: &e" + fakeNPC.getUuid());
		send("&3- Spawned: &e" + fakeNPC.isSpawned());

		send("&3- Hologram: &e" + hologram.getLines());
		send("  &3- Spawned: &e" + hologram.isSpawned());
		send("  &3- Type: &e" + hologram.getVisibilityType());
		send("  &3- Radius: &e" + hologram.getVisibilityRadius());

		send("&3- LookClose: &e" + fakeNPC.isLookClose());
		send("  &3- Radius: &e" + fakeNPC.getLookCloseRadius());

		send("&3- Location: &e" + StringUtils.getShortLocationString(fakeNPC.getLocation()));
	}

	@Path("create <type>")
	@Description("Create an NPC")
	public void create(FakeNPCType type) {
		FakeNPC fakeNPC = FakeNPCManager.create(FakeNPCType.PLAYER, player());
		FakeNPCManager.setSelected(player(), fakeNPC);

		send(PREFIX + "Created &e" + fakeNPC.getId());
	}

	@Path("delete")
	@Description("Delete the selected NPC")
	public void delete() {
		FakeNPC fakeNPC = getSelectedNPC();
		fakeNPC.delete();

		send(PREFIX + "Deleted &e" + fakeNPC.getId());
	}

	@Path("tp")
	@Description("Teleport to the selected NPC")
	public void teleportTo() {
		FakeNPC fakeNPC = getSelectedNPC();

		player().teleportAsync(fakeNPC.getLocation());
		send(PREFIX + "Teleported to &e" + fakeNPC.getId());

	}

	@Path("tphere")
	@Description("Teleport the selected NPC to your location")
	public void teleportHere() {
		FakeNPC fakeNPC = getSelectedNPC();

		fakeNPC.teleport(location());
		send(PREFIX + "Teleported &e" + fakeNPC.getId() + " &3to &e" + StringUtils.getShortLocationString(fakeNPC.getLocation()));
	}

	@Path("spawn")
	@Description("Spawns the selected NPC")
	public void spawn() {
		FakeNPC fakeNPC = getSelectedNPC();
		if (fakeNPC.isSpawned())
			error(fakeNPC.getId() + " is already spawned");

		fakeNPC.spawn();
		send(PREFIX + "Spawned &e" + fakeNPC.getId());
	}

	@Path("despawn")
	@Description("Despawns the selected NPC")
	public void despawn() {
		FakeNPC fakeNPC = getSelectedNPC();
		if (!fakeNPC.isSpawned())
			error(fakeNPC.getId() + " is already despawned");

		fakeNPC.despawn();
		send(PREFIX + "Despawned &e" + fakeNPC.getId());
	}

	@Path("skin [player] [--url] [--reapply]")
	@Description("Set the skin of the selected NPC")
	public void setSkinTest(Nerd nerd, @Switch String url, @Switch boolean reapply) {
		PlayerNPC playerNPC = getPlayerNPC(getSelectedNPC());

		if (reapply) {
			playerNPC.applySkin();
			send(PREFIX + "Reapplied skin of &e" + playerNPC.getId());
			return;
		}

		if (nerd == null && url == null)
			error("A skin name is required");

		if (url != null) {
			playerNPC.setMineSkin(url).thenAccept(result -> {
				if (result) {
					Tasks.wait(1, playerNPC::respawn);
					send(PREFIX + "Set skin of &e" + playerNPC.getId() + " &3to url: &e" + url);
				} else {
					send(PREFIX + "&cCould not set skin via URL: " + url);
				}
			});

			return;
		}

		playerNPC.setSkin(nerd);
		send(PREFIX + "Set skin of &e" + playerNPC.getId() + " &3to &e" + nerd.getNickname());
	}

	@Path("hologram <string...>")
	@Description("Set the hologram of the selected NPC, use | for new lines")
	public void setHologram(String string) {
		FakeNPC fakeNPC = getSelectedNPC();

		List<String> lines = Arrays.asList(string.split("\\|"));

		fakeNPC.getHologram().setLines(lines);
		fakeNPC.refreshHologram();

		send(PREFIX + "Set hologram of &e" + fakeNPC.getId() + " &3to &e" + fakeNPC.getHologram().getLines());
	}

	@Path("hologram visibility <type> [--radius]")
	public void setHologramVisibility(VisibilityType type, @Switch Integer radius) {
		FakeNPC fakeNPC = getSelectedNPC();

		fakeNPC.getHologram().setVisibilityType(type);
		if (radius != null)
			fakeNPC.getHologram().setVisibilityRadius(radius);

		radius = fakeNPC.getHologram().getVisibilityRadius();
		String visibilityRadius = "";
		if (radius != null && radius != 0)
			visibilityRadius = " &3with radius &e" + radius;

		send(PREFIX + "Set hologram of &e" + fakeNPC.getId() + " &3visibility to type &e" + type + visibilityRadius);
	}

	@Path("lookClose [enable] [--radius]")
	public void lookClose(Boolean enable, @Switch Integer radius) {
		FakeNPC fakeNPC = getSelectedNPC();

		if (enable == null)
			enable = !fakeNPC.isLookClose();

		fakeNPC.setLookClose(enable);
		if (radius != null)
			fakeNPC.setLookCloseRadius(radius);

		if (enable)
			send(PREFIX + "&e" + fakeNPC.getId() + " &3will now look at nearby players with radius &e" + fakeNPC.getLookCloseRadius());
		else
			send(PREFIX + "&e" + fakeNPC.getId() + " &3will no longer look at nearby players");
	}

	//

	private @NonNull FakeNPC getSelectedNPC() {
		FakeNPC fakeNPC = FakeNPCManager.getSelected(player());
		if (fakeNPC == null)
			error("You don't have an FakeNPC selected");

		return fakeNPC;
	}

	public @NonNull PlayerNPC getPlayerNPC(FakeNPC fakeNPC) {
		if (!(fakeNPC instanceof PlayerNPC))
			error("Can't cast fakeNPC to PlayerNPC");

		return (PlayerNPC) fakeNPC;
	}


}
