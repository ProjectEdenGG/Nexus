package gg.projecteden.nexus.features.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.mastery.menu.ClaimRewardMenu;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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
		if (nerd.isOnline()) {
			try {
				return UserManager.getPlayer(nerd.getOnlinePlayer()).getSkillLevel(skill);
			} catch (Exception ignored) {
			}
		}

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

	@Path("mastery [--admin] [--skill] [--lineSpacing]")
	@Description("Prestige skills for rewards")
	void reset(@Switch @Arg(value = "false", permission = Group.SENIOR_STAFF) boolean admin, @Switch @Arg(permission = Group.SENIOR_STAFF) PrimarySkillType skill) {
		if (Nexus.getEnv() == Env.PROD && !isAdmin())
			error("Temporarily disabled");

		if (WorldGroup.of(player()) != WorldGroup.SURVIVAL)
			error("You cannot use this outside of survival");

		new ClaimRewardMenu(skill, admin).open(player());
	}

	@Path("mastery award <player> <skill> <xp>")
	@Description("Gives mastery xp to the player")
	@Permission(Group.ADMIN)
	void masteryAward(OfflinePlayer player, PrimarySkillType skill, int xp) {
		if (skill.isChildSkill())
			error("That skill is not supported for mastery");
		if (xp <= 0)
			error("XP must be greater than 0");

		new McMMOPrestigeUserService().edit(player, user -> user.awardExperience(skill, xp));
		send(PREFIX + "Gave &e" + xp + " " + camelCase(skill) + " &3mastery xp to &e" + Nickname.of(player));
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

	@Path("protectItems")
	@Description("Protect items from mcMMO repair/salvage")
	void protectItems() {
		new ProtectItemsProvider(player()).open();
	}

	@Title("&0Protect from repair/salvage")
	public record ProtectItemsProvider(@Getter Player player) implements TemporaryMenuListener {

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			var items = contents.stream().filter(Nullables::isNotNullOrAir).toList();

			for (ItemStack item : items)
				PlayerUtils.giveItem(player, new ItemBuilder(item).setting(ItemSetting.MCMMOABLE, false).build());

			PlayerUtils.send(player, PREFIX + "&aProtected " + items.size() + " items from mcMMO repair/salvage");
		}

	}

	@EventHandler
	public void on(McMMOPlayerSalvageCheckEvent event) {
		if (isNullOrAir(event.getSalvageItem()))
			return;

		if (ItemSetting.MCMMOABLE.of(new ItemBuilder(event.getSalvageItem())))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), PREFIX + "&cThat &e" + camelCase(event.getSalvageItem().getType()) + " &cis protected from mcMMO salvage");
	}

	@EventHandler
	public void on(McMMOPlayerRepairCheckEvent event) {
		if (isNullOrAir(event.getRepairedObject()))
			return;

		if (ItemSetting.MCMMOABLE.of(new ItemBuilder(event.getRepairedObject())))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), PREFIX + "&cThat &e" + camelCase(event.getRepairedObject().getType()) + " &cis protected from mcMMO repair");
	}
}
