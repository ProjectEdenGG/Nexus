package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static me.pugabyte.nexus.utils.StringUtils.decolorize;

@Permission("group.staff")
public class NPCUtilsCommand extends CustomCommand {

	public NPCUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("getByOwner [player] [world]")
	void getByOwner(@Arg("self") OfflinePlayer player, World world) {
		Nexus.getCitizens().getNPCRegistry().iterator().forEachRemaining(npc -> {
			if (player.getUniqueId().equals(npc.getTrait(Owner.class).getOwnerId()) && (world == null || world.equals(npc.getStoredLocation().getWorld())))
				send(json("&3" + npc.getId() + " &e" + npc.getName() + " &7- " + npc.getStoredLocation().getWorld().getName())
						.command("/mcmd npc sel " + npc.getId() + " ;; npc tp")
						.hover("Click to teleport"));
		});
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
