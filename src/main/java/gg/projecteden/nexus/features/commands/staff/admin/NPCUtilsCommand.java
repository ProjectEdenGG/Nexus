package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.CitizensUtils.NPCFinder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.utils.StringUtils.decolorize;

@Permission("group.staff")
public class NPCUtilsCommand extends CustomCommand {

	public NPCUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("list [page] [--owner] [--world] [--radius] [--spawned]")
	void getByOwner(@Arg("1") int page, @Switch OfflinePlayer owner, @Switch World world, @Switch Integer radius, @Switch Boolean spawned) {
		List<NPC> npcs = NPCFinder.builder()
				.owner(owner)
				.world(world)
				.spawned(spawned)
				.radius(radius)
				.from(location())
				.build().get();

		if (npcs.isEmpty())
			error("No matches found");

		String command = "/npcutils list" +
				" --player=" + (owner == null ? "null" : owner.getName()) +
				" --world=" + (world == null ? "null" : world.getName()) +
				" --radius=" + (radius == null ? "null" : radius) +
				" --spawned=" + spawned;

		Comparator<NPC> comparator;
		if (radius != null)
			comparator = Comparator.comparingDouble(npc -> location().distance(npc.getStoredLocation()));
		else
			comparator = Comparator.comparing(NPC::getId);

		npcs.sort(comparator);

		final BiFunction<NPC, String, JsonBuilder> formatter = (npc, index) -> {
			final String extra;
			if (radius == null)
				extra = npc.getStoredLocation().getWorld().getName();
			else
				extra = StringUtils.getDf().format(location().distance(npc.getStoredLocation())) + "m";

			return json("&3" + npc.getId() + " &e" + npc.getName() + " &7- " + extra)
					.command("/mcmd npc sel " + npc.getId() + " ;; npc tp")
					.hover("Click to teleport");
		};

		send(PREFIX + "Total: &e" + npcs.size());

		paginate(npcs, formatter, command, page);
	}

	@Async
	@Path("removeDespawned [--owner] [--world]")
	void removeDespawned(@Switch OfflinePlayer owner, @Switch World world) {
		List<NPC> npcs = NPCFinder.builder()
				.owner(owner)
				.world(world)
				.spawned(false)
				.build().get();

		if (npcs.isEmpty())
			error("No matches found");

		for (NPC npc : npcs)
			Tasks.sync(npc::destroy);

		send(PREFIX + "Removed " + npcs.size() + plural(" NPC", npcs.size()));
	}

	@Path("create <player>")
	void create(@Arg("self") Nerd nerd) {
		runCommand("mcmd npc create " + nerd.getColoredName() + " ;; npc skin -l " + nerd.getName());
	}

	@Path("setName withPrefix <player>")
	void setNameWithFormat(Nerd nerd) {
		runCommand("npc rename " + decolorize("&8&l[" + nerd.getRank().getColoredName() + "&8&l] " + nerd.getNameFormat()));
	}

	@Path("setName withColor <player>")
	void setNameWithColor(Nerd nerd) {
		runCommand("npc rename " + nerd.getNameFormat());
	}

	@Path("setNickname withPrefix <player>")
	void setNicknameWithFormat(Nerd nerd) {
		runCommand("npc rename " + decolorize("&8&l[" + nerd.getRank().getColoredName() + "&8&l] " + nerd.getColoredName()));
	}

	@Path("setNickname withColor <player>")
	void setNicknameWithColor(Nerd nerd) {
		runCommand("npc rename " + nerd.getColoredName());
	}

	@Path("recreateNpc withColor <player>")
	void recreateNpcNameWithColor(Nerd nerd) {
		runCommand("mcmd npc sel ;; npc tp ;; npc remove ;; blockcenter ;; npc create " + nerd.getNameFormat() + " ;; npc skin -l " + nerd.getName());
	}

	@Path("setNicknameAndSkin <player>")
	void getByOwner(@Arg("self") Nerd nerd) {
		runCommand("mcmd npc sel ;; npc skin -l " + nerd.getName() + " ;; npcutils setNickname withColor " + nerd.getName());
	}

	@Path("void [page]")
	void inVoid(@Arg("1") int page) {
		List<NPC> voidNpcs = new ArrayList<>();
		CitizensAPI.getNPCRegistry().forEach(npc -> {
			if (npc.getEntity() != null && npc.getEntity().getLocation().getY() < 0)
				voidNpcs.add(npc);
		});

		if (voidNpcs.isEmpty())
			error("No void NPCs found");

		send(PREFIX + "Void NPCs");

		BiFunction<NPC, String, JsonBuilder> formatter = (npc, index) -> {
			int id = npc.getId();
			return json("&3" + index + " ")
					.group()
					.next(StringUtils.X)
					.command("/mcmd npc sel " + id + " ;; npc remove")
					.hover("&cClick to delete")
					.group()
					.next(" ")
					.group()
					.next("&a↑")
					.command("/mcmd npc sel " + id + " ;; npc tphere")
					.hover("&aClick to summon")
					.group()
					.next("&e " + id + " &7- &3" + npc.getName() + " &7in " + npc.getEntity().getWorld().getName());
		};

		paginate(voidNpcs, formatter, "/npcutils void", page);
	}

}
