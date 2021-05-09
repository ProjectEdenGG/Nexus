package me.pugabyte.nexus.features.commands.staff;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.StringUtils.Gradient;
import me.pugabyte.nexus.utils.StringUtils.Rainbow;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;

import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.decolorize;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.StringUtils.toHex;

public class
ColorUtilsCommand extends CustomCommand {

	public ColorUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("getHex <color>")
	void getHex(ChatColor color) {
		String hex = toHex(color);
		send(json("&" + hex + hex).copy(hex).hover("Click to copy"));
	}

	@Path("getRankHex <color>")
	void getHex(Rank rank) {
		getHex(rank.getChatColor());
	}

	@Path("runSpigotHexCommand <commandNoSlash...>")
	void runHexCommand(String commandNoSlash) {
		runCommand(decolorize(commandNoSlash));
	}

	@Description("Get the last color used in a string (including formatting)")
	@Path("getLastColor <message...>")
	void getLastColor(String message) {
		send(StringUtils.getLastColor(message) + "Last color");
	}

	@Path("gradient <color1> <color2> <input>")
	void gradient(ChatColor color1, ChatColor color2, String input) {
		send(Gradient.of(color1, color2).apply(input));
	}

	@Path("rainbow <input>")
	void rainbow(String input) {
		send(Rainbow.apply(input));
	}

	@Path("updateAllHOHNpcs")
	void updateAllHOHNpcs() {
		runCommand("hoh");
		World safepvp = world();
		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(safepvp);
		ProtectedRegion region = worldGuardUtils.getProtectedRegion("hallofhistory");
		List<NPC> npcs = safepvp.getEntities().stream()
				.filter(entity -> CitizensUtils.isNPC(entity) && worldGuardUtils.isInRegion(entity.getLocation(), region))
				.map(entity -> CitizensAPI.getNPCRegistry().getNPC(entity))
				.collect(Collectors.toList());

		int wait = 0;
		for (NPC npc : npcs) {
			Tasks.wait(wait += 20, () -> {
//				CitizensUtils.setSelectedNPC(player(), npc);
				String name = stripColor(npc.getName());
				runCommand("colorutils recreateNpc withColor " + name);
			});
		}
	}

}
