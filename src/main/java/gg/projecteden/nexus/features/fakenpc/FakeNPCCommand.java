package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.features.fakenpc.events.FakeNPCRightClickEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.features.fakenpc.FakeNPCUtils.getNameAndId;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class FakeNPCCommand extends CustomCommand implements Listener {
	private static final FakeNPCService service = new FakeNPCService();
	private static final FakeNPCUserService userService = new FakeNPCUserService();
	private static final FakeNPCConfigService configService = new FakeNPCConfigService();
	private final FakeNPCConfig config = configService.get0();
	private FakeNPCUser user;

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

	@Path("(desel|deselect)")
	@Description("Deselect an NPC")
	public void unselect() {
		user.setSelecting(false);

		FakeNPC selected = getSelectedNPC();
		user.setSelected(null);

		send(PREFIX + "Unselected NPC &e" + FakeNPCUtils.getNameAndId(selected));
	}

	@Path("(sel|select) [id]")
	@Description("Select an NPC")
	public void select(Integer id) {
		user.setSelecting(false);
		if (id != null)
			user.setSelectedNPC(FakeNPCUtils.fromId(id));
		else {
			send(PREFIX + "&eRight click &3an NPC to select it");
			user.setSelecting(true);
			return;
		}

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
		send("&3- Type: &e" + fakeNPC.getType());
		send("&3- UUID: &e" + fakeNPC.getUuid());
		send("&3- Spawned: &e" + fakeNPC.isSpawned());
		send("&3- Location: &e" + StringUtils.getShortLocationString(fakeNPC.getLocation()));

		send("&3- Hologram: &e" + hologram.getLines());
		send("  &3- Spawned: &e" + hologram.isSpawned());
		send("  &3- Type: &e" + hologram.getVisibilityType());
		send("  &3- Radius: &e" + hologram.getVisibilityRadius());

		send("&3- LookCloseTrait: &e" + fakeNPC.isLookClose());
		send("  &3- Radius: &e" + fakeNPC.getLookCloseRadius());
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

	@Path("rename <string...>")
	public void rename(String newName) {
		FakeNPC fakeNPC = getSelectedNPC();
		String oldName = fakeNPC.getName();
		fakeNPC.setName(newName);

		send(PREFIX + "Renamed &e" + oldName + " &3to &e" + newName);
	}

	// TODO: switch the signlines way
	@Path("hologram <string...>")
	@Description("Set the hologram of the selected NPC, use | for new lines")
	public void setHologram(String string) {
		FakeNPC fakeNPC = getSelectedNPC();

		List<String> lines = Arrays.asList(string.split("\\|"));

		fakeNPC.getHologram().setLines(lines);
		fakeNPC.refreshHologramLines();

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

	// trait settings

	@Path("trait lookClose [enable] [--radius]")
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

	// type settings

	@Path("player skin [player] [--url] [--mirror]")
	@Description("Set the skin of the selected NPC")
	public void setSkinTest(@Arg("self") Nerd nerd, @Switch String url, @Switch boolean mirror, @Switch boolean reapply) {
		PlayerNPC playerNPC = getSelectedType(FakeNPCType.PLAYER);

		if (!mirror && (nerd == null && url == null))
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

		playerNPC.setMirror(mirror);
		playerNPC.setSkin(nerd);

		//

		if (mirror) {
			send(PREFIX + "Set skin of &e" + playerNPC.getName() + " &3to &emirror player skins");
			return;
		}

		send(PREFIX + "Set skin of &e" + playerNPC.getName() + " &3to &e" + nerd.getNickname());
	}

	@Path("player reapplySkin")
	public void reapplySkin() {
		PlayerNPC playerNPC = getSelectedType(FakeNPCType.PLAYER);

		playerNPC.applySkin();
		send(PREFIX + "Reapplied skin of &e" + playerNPC.getName());
	}

	//

	private boolean hasSelectedNPC() {
		return user.getSelectedNPC() != null;
	}

	private @NonNull FakeNPC getSelectedNPC() {
		FakeNPC fakeNPC = user.getSelectedNPC();
		if (fakeNPC == null)
			error("You don't have a FakeNPC selected");

		return fakeNPC;
	}

	public @NonNull <T extends FakeNPC> T getSelectedType(FakeNPCType type) {
		FakeNPC npc = getSelectedNPC();

		if (!type.getClazz().equals(npc.getClass()))
			error("Can't cast FakeNPC to " + StringUtils.camelCase(type));

		return (T) npc;
	}

	@EventHandler
	public void on(FakeNPCRightClickEvent event) {
		FakeNPCUser user = userService.get(event.getClicker());
		if (!user.isSelecting())
			return;

		FakeNPC npc = event.getNpc();

		user.setSelecting(false);
		user.setSelectedNPC(npc);
		user.sendMessage(PREFIX + "You selected " + getNameAndId(npc));
	}

}
