package gg.projecteden.nexus.features.commands.staff.admin;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.CitizensUtils.NPCFinder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NonNull;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.utils.StringUtils.decolorize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Permission(Group.SENIOR_STAFF)
public class NPCUtilsCommand extends CustomCommand {

	public NPCUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.wait(TickTime.SECOND, () -> {
			try {
				Plugin plugin = Bukkit.getPluginManager().getPlugin("Citizens");
				if (plugin == null)
					return;

				final Field saveOnDisable = plugin.getClass().getDeclaredField("saveOnDisable");
				saveOnDisable.setAccessible(true);
				saveOnDisable.set(plugin, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Async
	@Path("list [page] [--owner] [--rankGte] [--rankLte] [--world] [--radius] [--spawned]")
	@Description("List NPCs with a filter")
	void list(
		@Arg("1") int page,
		@Switch OfflinePlayer owner,
		@Switch Rank rankGte,
		@Switch Rank rankLte,
		@Switch World world,
		@Switch Integer radius,
		@Switch Boolean spawned
	) {
		List<NPC> npcs = NPCFinder.builder()
			.owner(owner)
			.rankGte(rankGte)
			.rankLte(rankLte)
			.world(world)
			.spawned(spawned)
			.radius(radius)
			.from(location())
			.build().get();

		if (npcs.isEmpty())
			error("No matches found");

		String command = "/npcutils list" +
			(owner == null ? "" : " --player=" + owner.getName()) +
			(rankGte == null ? "" : " --rankGte=" + rankGte) +
			(rankLte == null ? "" : " --rankLte=" + rankLte) +
			(world == null ? "" : " --world=" + world.getName()) +
			(radius == null ? "" : " --radius=" + radius) +
			(spawned == null ? "" : " --spawned=" + spawned);

		Comparator<NPC> comparator;
		if (radius != null)
			comparator = Comparator.comparingDouble(npc -> distanceTo(npc.getStoredLocation()).get());
		else
			comparator = Comparator.comparing(NPC::getId);

		npcs.sort(comparator);

		final BiFunction<NPC, String, JsonBuilder> formatter = (npc, index) -> {
			final String extra;
			if (radius == null)
				extra = npc.getStoredLocation().getWorld().getName();
			else
				extra = StringUtils.getDf().format(distanceTo(npc.getStoredLocation()).getRealDistance()) + "m";

			return json("&3" + npc.getId() + " &e" + npc.getName() + " &7- " + extra)
					.command("/mcmd npc sel " + npc.getId() + " ;; npc tp")
					.hover("Click to teleport");
		};

		send(PREFIX + "Total: &e" + npcs.size());

		paginate(npcs, formatter, command, page);
	}

	@Async
	@Path("removeDespawned [--owner] [--world]")
	@Description("Delete despawned NPCs")
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
	@Description("Create a player NPC with a colored nickname")
	void create(@Arg("self") Nerd nerd) {
		runCommand("mcmd npc create " + nerd.getColoredName() + " ;; npc skin -l " + nerd.getName());
	}

	@Path("setPlayer withRankPrefix <player>")
	@Description("Set an NPC's nameplate to a player's colored rank and nickname and updates the skin")
	void setPlayer_withRankPrefix(Nerd nerd) {
		runCommand("mcmd npc sel ;; npc skin -l " + nerd.getName() + " ;; npc rename " + decolorize("&8&l[" + nerd.getRank().getColoredName() + "&8&l] " + nerd.getColoredName()));
	}

	@Path("setPlayer withColor <player>")
	@Description("Set an NPC's nameplate to a player's colored nickname and updates the skin")
	void setPlayer_withColor(Nerd nerd) {
		runCommand("mcmd npc sel ;; npc skin -l " + nerd.getName() + " ;; npc rename " + nerd.getColoredName());
	}

	@Path("rename gradient <colors> <name...>")
	@Description("Set an NPC's nameplate to your input with a color gradient")
	void setName_gradient(@Arg(type = ChatColor.class) List<ChatColor> colors, String input) {
		runCommand("npc rename " + decolorize(Gradient.of(colors).apply(input)));
	}

	@Path("recreateNpc <player>")
	@Description("Recreate an NPC in the center of the block with a player's colored nickname")
	void recreateNpc_withColor(Nerd nerd) {
		runCommand("mcmd npc sel ;; npc tp ;; npc remove ;; blockcenter ;; npc create " + nerd.getColoredName() + " ;; npc skin -l " + nerd.getName());
	}

	@Path("void [page]")
	@Description("List NPCs in the void")
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
			return json(index + " ")
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

	@HideFromWiki
	@Path("updateAllHOHNpcs")
	@Permission(Group.ADMIN)
	@Description("Recreate all Hall of History NPCs")
	void updateAllHOHNpcs() {
		runCommand("hoh");
		World safepvp = world();
		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(safepvp);
		ProtectedRegion region = worldGuardUtils.getProtectedRegion("hallofhistory");
		List<NPC> npcs = safepvp.getEntities().stream()
			.filter(entity -> CitizensUtils.isNPC(entity) && worldGuardUtils.isInRegion(entity.getLocation(), region))
			.map(entity -> CitizensAPI.getNPCRegistry().getNPC(entity))
			.toList();

		int wait = 0;
		for (NPC npc : npcs) {
			Tasks.wait(wait += 20, () -> {
				String name = stripColor(npc.getName());
				runCommand("npcutils recreateNpc withColor " + name);
			});
		}
	}

}
