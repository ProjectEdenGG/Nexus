package gg.projecteden.nexus.features.vaults;

import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.vaults.VaultUser;
import gg.projecteden.nexus.models.vaults.VaultUserService;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Aliases({"playervaults", "pv", "chest", "vaults"})
public class VaultCommand extends CustomCommand {
	private final VaultUserService service = new VaultUserService();

	public VaultCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[page] [user]")
	@Description("Open a vault")
	void open(@Arg(value = "1", min = 1) int page, @Arg(value = "self", permission = Group.SENIOR_STAFF) VaultUser user) {
		if (WorldGroup.of(player()) != WorldGroup.SURVIVAL && !isSeniorStaff())
			error("You can't open vaults here");

		new VaultMenu(player(), user, page);
	}

	@Path("limit [user]")
	@Description("View how many vaults you own")
	void limit(@Arg(value = "self", permission = Group.SENIOR_STAFF) VaultUser user) {
		send(PREFIX + (isSelf(user) ? "You own" : user.getNickname() + " owns") + " &e" + user.getLimit() + " &3vaults");
	}

	@Path("limit add <user> [amount]")
	@Permission(Group.ADMIN)
	@Description("Increase a player's vault limit")
	void limit_add(VaultUser user, @Arg("1") int amount) {
		user.increaseLimit(amount);
		service.save(user);
		send(PREFIX + "Increased " + user.getNickname() + "'s vault limit by &e" + amount + "&3. New limit: &e" + user.getLimit());
	}

	public static class VaultMenu implements TemporaryMenuListener {
		private final VaultUserService service = new VaultUserService();
		@Getter
		private final Player player;
		private final VaultUser user;
		private final int page;

		@Getter
		private final VaultHolder inventoryHolder = new VaultHolder();

		public VaultMenu(Player player, VaultUser user, int page) {
			this.player = player;
			this.user = user;
			this.page = page;

			open(4, user.get(page, player));
		}

		@Override
		public String getTitle() {
			return "Vault #" + page;
		}

		public static class VaultHolder extends CustomInventoryHolder {}

		@Override
		public boolean keepAirSlots() {
			return true;
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			user.update(page, contents);
			service.save(user);
		}
	}

}
