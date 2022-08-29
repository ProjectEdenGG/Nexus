package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.fakenpcs.config.FakeNPCConfig;
import gg.projecteden.nexus.models.fakenpcs.config.FakeNPCConfigService;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPCService;
import gg.projecteden.nexus.models.fakenpcs.npcs.types.PlayerNPC;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Permission(Group.ADMIN)
public class FakeNPCCommand extends CustomCommand {
	private static final FakeNPCService service = new FakeNPCService();
	private static final FakeNPCUserService userService = new FakeNPCUserService();
	private static final FakeNPCConfigService configService = new FakeNPCConfigService();
	private final FakeNPCConfig config = configService.get0();
	private final FakeNPCUser user;

	public FakeNPCCommand(@NonNull CommandEvent event) {
		super(event);
		user = userService.get(player());
	}

	@Override
	public void postProcess() {
		if (hasSelectedNPC())
			service.save(getSelectedNPC());

		userService.save(user);
	}

	@Path("(sel|select) [id]")
	@Description("Select an NPC")
	public void select(Integer id) {
		if (id != null)
			user.setSelectedNPC(FakeNPC.fromId(id));
		else
			error(PREFIX + "TODO");

		if (!hasSelectedNPC())
			error("Could not find an NPC to select");

		final FakeNPC npc = user.getSelectedNPC();
		send(PREFIX + "You selected " + getNameAndId(npc));
	}

	@Path("info")
	@Description("Display information about the selected NPC")
	public void info() {
		FakeNPC fakeNPC = getSelectedNPC();
		Hologram hologram = fakeNPC.getHologram();

		send(PREFIX + "Info of &e" + fakeNPC.getName() + "&3:");
		send("&3- ID: &e" + fakeNPC.getId());
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
		FakeNPC fakeNPC = type.create(player());
		user.setSelectedNPC(fakeNPC);

		send(PREFIX + "Created " + getNameAndId(fakeNPC));
	}

	@Path("delete")
	@Description("Delete the selected NPC")
	public void delete() {
		FakeNPC fakeNPC = getSelectedNPC();
		service.delete(fakeNPC);

		send(PREFIX + "Deleted " + getNameAndId(fakeNPC));
	}

	@Path("tp")
	@Description("Teleport to the selected NPC")
	public void teleportTo() {
		FakeNPC fakeNPC = getSelectedNPC();
		player().teleportAsync(fakeNPC.getLocation());

		send(PREFIX + "Teleported to &e" + fakeNPC.getName());
	}

	@Path("tphere")
	@Description("Teleport the selected NPC to your location")
	public void teleportHere() {
		FakeNPC fakeNPC = getSelectedNPC();
		fakeNPC.teleport(location());

		send(PREFIX + "Teleported &e" + fakeNPC.getName() + " &3to &e" + StringUtils.getShortLocationString(fakeNPC.getLocation()));
	}

	@Path("spawn")
	@Description("Spawns the selected NPC")
	public void spawn() {
		FakeNPC fakeNPC = getSelectedNPC();
		if (fakeNPC.isSpawned())
			error(fakeNPC.getName() + " is already spawned");

		fakeNPC.spawn();

		send(PREFIX + "Spawned &e" + fakeNPC.getName());
	}

	@Path("despawn")
	@Description("Despawns the selected NPC")
	public void despawn() {
		FakeNPC fakeNPC = getSelectedNPC();
		if (!fakeNPC.isSpawned())
			error(fakeNPC.getName() + " is already despawned");

		fakeNPC.despawn();

		send(PREFIX + "Despawned &e" + fakeNPC.getName());
	}

	@Path("skin [player] [--url] [--reapply]")
	@Description("Set the skin of the selected NPC")
	public void setSkinTest(Nerd nerd, @Switch String url, @Switch boolean reapply) {
		PlayerNPC playerNPC = getPlayerNPC(getSelectedNPC());

		if (reapply) {
			playerNPC.applySkin();
			send(PREFIX + "Reapplied skin of &e" + playerNPC.getName());
			return;
		}

		if (nerd == null && url == null)
			error("A skin name is required");

		if (url != null) {
			playerNPC.setMineSkin(url).thenAccept(result -> {
				if (result) {
					Tasks.wait(1, playerNPC::respawn);
					send(PREFIX + "Set skin of &e" + playerNPC.getName() + " &3to url: &e" + url);
				} else {
					send(PREFIX + "&cCould not set skin via URL: " + url);
				}
			});

			return;
		}

		playerNPC.setSkin(nerd);
		send(PREFIX + "Set skin of &e" + playerNPC.getName() + " &3to &e" + nerd.getNickname());
	}

	@Path("rename <string...>")
	public void rename(String newName) {
		FakeNPC fakeNPC = getSelectedNPC();
		String oldName = fakeNPC.getName();
		fakeNPC.setName(newName);

		send(PREFIX + "Renamed &e" + oldName + " &3to &e" + newName);
	}

	@Path("hologram <string...>")
	@Description("Set the hologram of the selected NPC, use | for new lines")
	public void setHologram(String string) {
		FakeNPC fakeNPC = getSelectedNPC();

		List<String> lines = Arrays.asList(string.split("\\|"));

		fakeNPC.getHologram().setLines(lines);
		fakeNPC.refreshHologram();

		send(PREFIX + "Set hologram of &e" + fakeNPC.getName() + " &3to &e" + lines);
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
			visibilityRadius = " &3using radius of &e" + radius;

		send(PREFIX + "Set hologram visibility of &e" + fakeNPC.getName() + " &3to type &e" + type + visibilityRadius);
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
			send(PREFIX + "&e" + fakeNPC.getName() + " &3will now look at nearby players using radius of &e" + fakeNPC.getLookCloseRadius());
		else
			send(PREFIX + "&e" + fakeNPC.getName() + " &3will no longer look at nearby players");
	}

	//

	@NotNull
	private String getNameAndId(FakeNPC npc) {
		return "&e" + npc.getName() + " &3(ID: &e" + npc.getId() + "&3)";
	}

	private boolean hasSelectedNPC() {
		return user.getSelectedNPC() != null;
	}

	private @NonNull FakeNPC getSelectedNPC() {
		FakeNPC fakeNPC = user.getSelectedNPC();
		if (fakeNPC == null)
			error("You don't have a FakeNPC selected");

		return fakeNPC;
	}

	public @NonNull PlayerNPC getPlayerNPC(FakeNPC fakeNPC) {
		if (!(fakeNPC instanceof PlayerNPC))
			error("Can't cast fakeNPC to PlayerNPC");

		return (PlayerNPC) fakeNPC;
	}

}
