package gg.projecteden.nexus.features.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetProvider.ResetSkillType;
import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetShopMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@NoArgsConstructor
@Redirect(from = "/mcmmoreset", to = "/mcmmo reset")
@Redirect(from = "/mcsbk", to = "/mcmmo scoreboard keep")
@Redirect(from = "/mcsbc", to = "/mcmmo scoreboard clear")
public class McMMOCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("mcMMO");

	public McMMOCommand(CommandEvent event) {
		super(event);
	}

	@Path("help")
	@Override
	@Description("Help menu")
	public void help() {
		super.help();
		send("&c/<skill> [? <#>] &7- View information about skills");
		send("&c/<skill> keep &7- Activate and anchor the skill scoreboard");
	}

	@Path("(sb|scoreboard) (k|keep)")
	@Description("Anchor the current scoreboard")
	void sb_keep() {
		runCommand("mcscoreboard keep");
	}

	@Path("(sb|scoreboard) (c|clear|remove)")
	@Description("Remove the current scoreboard")
	void sb_clear() {
		runCommand("mcscoreboard clear");
	}

	@Path("stats [player]")
	@Description("View mcMMO skill levels")
	void stats(@Arg("self") Nerd player) {
		if (isSelf(player))
			runCommand("mcstats");
		else
			runCommand("mcmmo:inspect " + player.getName());
	}

	@Path("rank [player]")
	@Description("View skill ranks")
	void rank(@Arg("self") Nerd nerd) {
		runCommand("mcrank " + nerd.getName());
	}

	@Path("top [skill] [page]")
	@Description("View skill leaderboards")
	void top(PrimarySkillType skill, @Arg("1") int page) {
		runCommand("mctop " + (skill == null ? "" : skill.name()) + " " + page);
	}

	@Path("cooldowns")
	@Description("View ability cooldowns")
	void cooldowns() {
		runCommand("mccooldowns");
	}

	@Path("abilities")
	@Description("Toggle activating right click abilities")
	void abilities() {
		runCommand("mcability");
	}

	public static int getSkillLevel(Nerd nerd, PrimarySkillType skill) {
		if (nerd.isOnline())
			return UserManager.getPlayer(nerd.getOnlinePlayer()).getSkillLevel(skill);
		else
			return mcMMO.getDatabaseManager().loadPlayerProfile(nerd.getUuid()).getSkillLevel(skill);
	}

	@Permission(Group.ADMIN)
	@Path("levels give <player> <skill> <amount>")
	@Description("Modify a player's skill level")
	void levels_give(Nerd nerd, PrimarySkillType skill, int amount) {
		runCommand("mmoedit " + nerd.getName() + " " + skill.name() + " " + (getSkillLevel(nerd, skill) + amount));
	}

	@Permission(Group.ADMIN)
	@Path("levels take <player> <skill> <amount>")
	@Description("Modify a player's skill level")
	void levels_take(Nerd nerd, PrimarySkillType skill, int amount) {
		runCommand("mmoedit " + nerd.getName() + " " + skill.name() + " " + (getSkillLevel(nerd, skill) - amount));
	}

	@Permission(Group.ADMIN)
	@Path("levels reset <player> <skill>")
	@Description("Modify a player's skill level")
	void levels_reset(Nerd nerd, PrimarySkillType skill) {
		levels_set(nerd, skill, 0);
	}

	@Permission(Group.ADMIN)
	@Path("levels set <player> <skill> <amount>")
	@Description("Modify a player's skill level")
	void levels_set(Nerd nerd, PrimarySkillType skill, int amount) {
		runCommand("mmoedit " + nerd.getName() + " " + skill.name() + " " + amount);
	}

	@Permission(Group.ADMIN)
	@Path("refresh")
	@Description("Reset ability cooldowns")
	void refresh() {
		runCommand("mcrefresh");
	}

	// Custom stuff

	@Path("prestige [player]")
	@Description("View the number of times a player has prestiged their skills")
	void prestige(@Arg("self") McMMOPrestigeUser user) {
		if (user.getPrestiges().isEmpty())
			error((isSelf(user) ? "You have" : user.getNickname() + " has") + " not prestiged any of their skills");

		send(PREFIX + "Prestiges for &e" + user.getNickname());
		user.getPrestiges().forEach((type, count) -> send("&3 " + camelCase(type) + ": &e" + count));
	}

	@Path("prestige top [page] [--skill]")
	@Description("View the prestige leaderboards")
	void prestige_top(@Arg("1") int page, @Switch ResetSkillType skill) {
		Map<UUID, Integer> prestiges = new HashMap<>() {{
			for (var user : new McMMOPrestigeUserService().getAll()) {
				final int total;
				if (skill == null)
					total = user.getPrestiges().values().stream().mapToInt(Integer::valueOf).sum();
				else
					total = user.getPrestige(skill);

				if (total > 0)
					put(user.getUuid(), total);
			}
		}};

		if (prestiges.isEmpty())
			error("No prestiges found");

		send(PREFIX + (skill == null ? "" : skill.name() + " ") + "Prestige leaderboard");

		final BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) ->
			json("&3" + index + " &e" + Nerd.of(uuid).getColoredName() + " &7- " + prestiges.get(uuid))
				.command("/mcmmo prestige " + Nickname.of(uuid))
				.hover("Click to view user's prestiges");

		paginate(Utils.sortByValueReverse(prestiges).keySet(), formatter, "/mcmmo prestige top" + (skill == null ? "" : " --skill=" + skill), page);
	}

	@Path("reset")
	@Description("Prestige skills for rewards")
	void reset() {
		if (Nexus.getEnv() == Env.PROD && !isAdmin())
			error("Temporarily disabled");

		if (WorldGroup.of(player()) != WorldGroup.SURVIVAL)
			error("You cannot use this outside of survival");

		new McMMOResetShopMenu().open(player());
	}

	@Path("protectItem")
	@Description("Protect items from mcMMO repair/salvage")
	void protectItem() {
		final EquipmentSlot hand = getHandWithToolRequired();
		final ItemBuilder tool = new ItemBuilder(inventory().getItem(hand));
		final boolean newState = !ItemSetting.MCMMOABLE.of(tool);
		inventory().setItem(hand, tool.setting(ItemSetting.MCMMOABLE, newState).build());

		if (newState)
			send(PREFIX + "&cRemoved repair/salvage protection");
		else
			send(PREFIX + "&aProtected item from repair/salvage");
	}

	@EventHandler
	public void on(McMMOPlayerSalvageCheckEvent event) {
		if (Nullables.isNullOrAir(event.getSalvageItem()))
			return;

		if (ItemSetting.MCMMOABLE.of(new ItemBuilder(event.getSalvageItem())))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), PREFIX + "&cThat &e" + camelCase(event.getSalvageItem().getType()) + " &cis protected from mcMMO salvage");
	}

	@EventHandler
	public void on(McMMOPlayerRepairCheckEvent event) {
		if (Nullables.isNullOrAir(event.getRepairedObject()))
			return;

		if (ItemSetting.MCMMOABLE.of(new ItemBuilder(event.getRepairedObject())))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), PREFIX + "&cThat &e" + camelCase(event.getRepairedObject().getType()) + " &cis protected from mcMMO repair");
	}
}
