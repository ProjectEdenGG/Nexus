package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.features.store.perks.inventory.PlayerHeadCommand.PERMISSION;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Aliases("skull")
@Permission(PERMISSION)
@Redirect(from = "/donorskull", to = "/playerhead")
@WikiConfig(rank = "Store", feature = "Inventory")
public class PlayerHeadCommand extends CustomCommand {
	public static final String PERMISSION = "essentials.skull";
	private static final List<WorldGroup> allowedWorldGroups = List.of(
		WorldGroup.SURVIVAL,
		WorldGroup.CREATIVE,
		WorldGroup.SKYBLOCK
	);

	public PlayerHeadCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[owner]")
	@Description("Receive a player's skull")
	void run(@Arg(value = "self", permission = Group.SENIOR_STAFF) Nerd owner) {
		if (!isStaff()) {
			if (!allowedWorldGroups.contains(worldGroup()))
				error("You cannot use this command in this world");

			final String cooldownId = "playerhead-" + worldGroup().name();
			if (!new CooldownService().check(player(), cooldownId, TickTime.DAY))
				throw new CommandCooldownException(player(), cooldownId);
		}

		giveItem(new ItemBuilder(Material.PLAYER_HEAD).name("&f" + owner.getNickname() + "'s Head").skullOwner(owner).build());
	}

	@Path("getId")
	@Permission(Group.ADMIN)
	@Description("Print the HeadDatabase ID of the head you are holding or looking at")
	void getId() {
		final ItemStack tool = getTool();
		final Block block = getTargetBlock();

		String id = null;
		if (!isNullOrAir(tool))
			id = Nexus.getHeadAPI().getItemID(tool);
		else if (!isNullOrAir(block))
			id = Nexus.getHeadAPI().getItemID(ItemUtils.getItem(block));
		else
			error("You must be holding or looking at a head");

		ItemStack item = Nexus.getHeadAPI().getItemHead(id);
		send(json(PREFIX + item.getItemMeta().getDisplayName() + " &3head ID: &e" + id).copy(id).hover("&eClick to copy"));
	}

}
