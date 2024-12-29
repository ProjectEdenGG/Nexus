package gg.projecteden.nexus.features.fakenpc;

import gg.projecteden.nexus.features.fakenpc.events.FakeNPCRightClickEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.fakenpcs.config.FakeNPCConfig;
import gg.projecteden.nexus.models.fakenpcs.config.FakeNPCConfigService;
import gg.projecteden.nexus.models.fakenpcs.npcs.*;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC.Hologram.VisibilityType;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.HologramTrait;
import gg.projecteden.nexus.models.fakenpcs.npcs.traits.LookCloseTrait;
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

import java.util.List;
import java.util.Set;

@HideFromWiki // TODO
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
		if (user.hasSelectedNPC())
			service.save(getSelectedNPC());

		userService.save(user);
	}

	@Path("list")
	public void listNPCs() {
		List<FakeNPC> NPCs = service.getAll();
		send("Total: " + NPCs.size());
		for (FakeNPC fakeNPC : service.getAll()) {
			send("- " + FakeNPCUtils.getNameAndId(fakeNPC));
		}
	}

	@Path("(desel|deselect)")
	@Description("Deselect an NPC")
	public void unselect() {
		user.setSelecting(false);

		FakeNPC npc = getSelectedNPC();
		user.setSelected(null);

		send(PREFIX + "Unselected NPC &e" + FakeNPCUtils.getNameAndId(npc));
	}

	@Path("(sel|select) [id]")
	@Description("Select an NPC")
	public void select(Integer id) {
		FakeNPC npc = getSelectedNPC(id);

		user.setSelecting(false);
		if (id == null) {
			send(PREFIX + "&eRight click &3an NPC to select it");
			user.setSelecting(true);
			return;
		} else {
			user.setSelectedNPC(npc);
		}

		if (!user.hasSelectedNPC())
			error("Could not find an NPC to select");

		send(PREFIX + "You selected " + FakeNPCUtils.getNameAndId(npc));
	}

	@Path("info [--expand]")
	@Description("Display information about the selected NPC")
	public void info(@Switch boolean expand) {
		FakeNPC npc = getSelectedNPC();

		send(PREFIX + "Info of &e" + npc.getName() + "&3:");
		send("&3- ID: &e" + npc.getId());
		send("&3- Type: &e" + npc.getType());
		send("&3- UUID: &e" + npc.getUuid());
		send("&3- Spawned: &e" + npc.isSpawned());
		send("&3- Location: &e" + StringUtils.getShortLocationString(npc.getLocation()));

		// TODO: remove in replace of traits
		Hologram hologram = npc.getHologram();
		send("&3- Hologram:");
		send("  &3- Name: &e" + hologram.getName());
		send("  &3- Lines: &e" + hologram.getLines());
		send("  &3- Spawned: &e" + hologram.isSpawned());
		send("  &3- Type: &e" + hologram.getVisibilityType());
		send("  &3- Radius: &e" + hologram.getVisibilityRadius());

		Set<Class<? extends Trait>> traits = npc.getTraits().keySet();
		send("&3- Traits: &e" + traits.size());
		for (Class<? extends Trait> clazz : traits) {
			Trait trait = npc.getTrait(clazz);
			send("  &3- " + StringUtils.camelCase(trait.getType()) + "&3: &e" + trait.toDebug(expand));
		}
	}

	@Path("create <string...> [--type]")
	@Description("Create an NPC")
	public void create(String name, @Switch FakeNPCType type) {
		if (type == null) type = FakeNPCType.PLAYER;

		FakeNPC npc = type.create(player(), name);
		user.setSelectedNPC(npc);

		send(PREFIX + "Created " + FakeNPCUtils.getNameAndId(npc));
	}

	@Path("delete")
	@Description("Delete the selected NPC")
	public void delete() {
		FakeNPC npc = getSelectedNPC();
		service.delete(npc);

		send(PREFIX + "Deleted " + FakeNPCUtils.getNameAndId(npc));
	}

	@Path("tp")
	@Description("Teleport to the selected NPC")
	public void teleportTo() {
		FakeNPC npc = getSelectedNPC();
		player().teleportAsync(npc.getLocation());

		send(PREFIX + "Teleported to &e" + npc.getName());
	}

	@Path("tphere")
	@Description("Teleport the selected NPC to your location")
	public void teleportHere() {
		FakeNPC npc = getSelectedNPC();
		npc.teleport(location());

		send(PREFIX + "Teleported &e" + npc.getName() + " &3to &e" + StringUtils.getShortLocationString(npc.getLocation()));
	}

	@Path("spawn")
	@Description("Spawns the selected NPC")
	public void spawn() {
		FakeNPC npc = getSelectedNPC();
		if (npc.isSpawned())
			error(npc.getName() + " is already spawned");

		npc.spawn();

		send(PREFIX + "Spawned &e" + npc.getName());
	}

	@Path("despawn")
	@Description("Despawns the selected NPC")
	public void despawn() {
		FakeNPC npc = getSelectedNPC();
		if (!npc.isSpawned())
			error(npc.getName() + " is already despawned");

		npc.despawn();

		send(PREFIX + "Despawned &e" + npc.getName());
	}

	// trait settings

	@Path("reapplyTraits")
	public void reapplyTraits() {
		FakeNPC npc = getSelectedNPC();

		npc.addDefaultTraits();
	}

	@Path("name [enable]")
	@Description("Toggle the name visibility of the selected NPC")
	public void toggleName(Boolean enable) {
		FakeNPC npc = getSelectedNPC();

		if (enable == null)
			enable = !npc.isNameVisible();

		npc.setNameVisible(enable);
		send(PREFIX + "Set name visibility of &e" + npc.getName() + " &3to &e" + npc.isNameVisible());
	}

	@Path("rename <string...>")
	@Description("Rename the selected NPC")
	public void rename(String newName) {
		FakeNPC npc = getSelectedNPC();

		String oldName = npc.getName();

		npc.setName(newName);
		send(PREFIX + "Renamed &e" + oldName + " &3to &e" + newName);
	}

	@Path("hologram [--1] [--2] [--3] [--4]")
	@Description("Set the hologram of the selected NPC, use | for new lines")
	public void setHologram(@Switch String line1, @Switch String line2, @Switch String line3, @Switch String line4) {
		FakeNPC npc = getSelectedNPC();

		List<String> lines = HologramTrait.setupLines(line1, line2, line3, line4);

		npc.getHologram().setLines(lines);
		npc.refreshHologramLines();

		send(PREFIX + "Set hologram of &e" + npc.getName() + " &3to &e" + lines);
	}

	@Path("hologram visibility <type> [--radius]")
	public void setHologramVisibility(VisibilityType type, @Switch Integer radius) {
		FakeNPC npc = getSelectedNPC();

		npc.getHologram().setVisibilityType(type);
		if (radius != null)
			npc.getHologram().setVisibilityRadius(radius);

		radius = npc.getHologram().getVisibilityRadius();
		String visibilityRadius = "";
		if (radius != null && radius != 0)
			visibilityRadius = " &3using radius of &e" + radius;

		send(PREFIX + "Set hologram visibility of &e" + npc.getName() + " &3to type &e" + type + visibilityRadius);
	}

	@Path("trait lookClose [enable] [--radius]")
	public void lookClose(Boolean enable, @Switch Integer radius) {
		FakeNPC npc = getSelectedNPC();

		LookCloseTrait trait = npc.getOrAddTrait(FakeNPCTraitType.LOOK_CLOSE);

		if (enable == null)
			enable = !trait.isEnabled();

		trait.setEnabled(enable);

		if (radius != null)
			trait.setRadius(radius);

		if (enable)
			send(PREFIX + "&e" + npc.getName() + " &3will now look at nearby players using radius of &e" + trait.getRadius());
		else
			send(PREFIX + "&e" + npc.getName() + " &3will no longer look at nearby players");
	}

	// type settings

	@Path("player skin [player] [--url] [--mirror]")
	@Description("Set the skin of the selected NPC")
	public void setSkinTest(@Arg("self") Nerd nerd, @Switch String url, @Switch boolean mirror) {
		PlayerNPC npc = getSelectedType(FakeNPCType.PLAYER);

		if (!mirror && (nerd == null && url == null))
			error("A skin name is required");

		if (url != null) {
			npc.setMineSkin(url).thenAccept(result -> {
				if (result) {
					Tasks.wait(1, npc::respawn);
					send(PREFIX + "Set skin of &e" + npc.getName() + " &3to url: &e" + url);
				} else {
					send(PREFIX + "&cCould not set skin via URL: " + url);
				}
			});

			return;
		}

		npc.setMirror(mirror);
		npc.setSkin(nerd);

		//

		if (mirror) {
			send(PREFIX + "Set skin of &e" + npc.getName() + " &3to &emirror player skins");
			return;
		}

		send(PREFIX + "Set skin of &e" + npc.getName() + " &3to &e" + nerd.getNickname());
	}

	@Path("player reapplySkin")
	public void reapplySkin() {
		PlayerNPC npc = getSelectedType(FakeNPCType.PLAYER);

		npc.applySkin();
		send(PREFIX + "Reapplied skin of &e" + npc.getName());
	}

	//

	@Path("printDebug [id]")
	@Permission(Group.ADMIN)
	public void printDebug(Integer id) {
		FakeNPC npc = getSelectedNPC(id);

		runCommand("db debug FakeNPCService " + npc.getUuid());
	}

	@Path("deleteDatabase")
	@Permission(Group.ADMIN)
	public void deleteDatabase() {
		send(PREFIX + "Clearing caches...");
		new FakeNPCConfigService().clearCache();
		new FakeNPCService().clearCache();
		new FakeNPCUserService().clearCache();

		send(PREFIX + "Deleting...");
		new FakeNPCConfigService().deleteAll();
		new FakeNPCService().deleteAll();
		new FakeNPCUserService().deleteAll();

		send(PREFIX + "Done");
	}

	private @NonNull FakeNPC getSelectedNPC(Integer id) {
		if (id != null)
			return FakeNPCUtils.fromId(id);

		return getSelectedNPC();
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
			error("Can't cast " + npc.getType() + "  to " + StringUtils.camelCase(type));

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
		user.sendMessage(PREFIX + "You selected " + FakeNPCUtils.getNameAndId(npc));
	}

}
