package gg.projecteden.nexus.features.commands.staff;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.StringUtils.Rainbow;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NonNull;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.decolorize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.nexus.utils.StringUtils.toHex;

public class ColorUtilsCommand extends CustomCommand {

	public ColorUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("getHex <color>")
	void getHex(ChatColor color) {
		String hex = toHex(color);
		send(json("&" + hex + hex).copy(hex).hover("Click to copy"));
	}

	@Path("getRankHex <rank>")
	void getHex(Rank rank) {
		getHex(rank.getChatColor());
	}

	@Path("runSpigotHexCommand <commandNoSlash...>")
	@Permission(Group.ADMIN)
	void runHexCommand(String commandNoSlash) {
		runCommand(decolorize(commandNoSlash));
	}

	@Description("Get the last color used in a string (including formatting)")
	@Path("getLastColor <message...>")
	void getLastColor(String message) {
		send(StringUtils.getLastColor(message) + "Last color");
	}

	@Path("gradient <colors> <input...> [--decolorize]")
	void gradient(@Arg(type = ChatColor.class) List<ChatColor> colors, String input, @Switch boolean decolorize) {
		final String gradient = Gradient.of(colors).apply(input);
		String message = decolorize ? decolorize(gradient) : colorize(gradient);

		send(json(message).hover("Shift+Click to insert").insert(message));
	}

	@Path("rainbow <input...> [--decolorize]")
	void rainbow(String input, @Switch boolean decolorize) {
		final String rainbow = Rainbow.apply(input);
		player().sendMessage(decolorize ? decolorize(rainbow) : colorize(rainbow));
	}

	@Path("setLeatherColor <color>")
	@Permission(Group.ADMIN)
	void setLeatherColor(@Arg(type = ChatColor.class) ChatColor chatColor) {
		ItemStack item = getToolRequired();
		if (!(item.getItemMeta() instanceof LeatherArmorMeta armorMeta))
			return;

		java.awt.Color color = chatColor.getColor();
		armorMeta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
		item.setItemMeta(armorMeta);
	}

	@Path("updateAllHOHNpcs")
	@Permission(Group.ADMIN)
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
