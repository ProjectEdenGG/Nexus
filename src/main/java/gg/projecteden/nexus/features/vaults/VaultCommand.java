package gg.projecteden.nexus.features.vaults;

import com.drtshock.playervaults.vaultmanagement.VaultManager;
import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand.NumericPermission;
import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.vaults.VaultUser;
import gg.projecteden.nexus.models.vaults.VaultUserService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Aliases({"playervaults", "pv", "chest", "vaults"})
public class VaultCommand extends CustomCommand {
	private final VaultUserService service = new VaultUserService();

	public VaultCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[page] [user]")
	void open(@Arg(value = "1", min = 1) int page, @Arg(value = "self", permission = Group.SENIOR_STAFF) VaultUser user) {
		if (WorldGroup.of(player()) != WorldGroup.SURVIVAL && !isSeniorStaff())
			error("You can't open vaults here");

		new VaultMenu(player(), user, page);
	}

	@Path("limit add <user> [amount]")
	@Permission(Group.ADMIN)
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

		public VaultMenu(Player player, VaultUser user, int page) {
			this.player = player;
			this.user = user;
			this.page = page;

			open(user.get(page, player));
		}

		@Override
		public String getTitle() {
			return "&4Vault #" + page;
		}

		@Override
		public <T extends InventoryHolder> T getInventoryHolder() {
			return (T) new VaultHolder(page);
		}

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

	@Data
	public static class VaultHolder implements InventoryHolder {
		private Inventory inventory;
		private final int vaultNumber;
	}

	// Conversion

	@NotNull
	private List<UUID> getOldUsers() {
		final String[] list = java.nio.file.Path.of("plugins/PlayerVaults/newvaults/").toFile().list();
		if (list == null)
			error("No vault users");

		return new ArrayList<>() {{
			for (String file : list)
				if (file.endsWith(".yml"))
					add(UUID.fromString(file.replace(".yml", "")));
		}};
	}

	@Path("listVaultUsers")
	void listVaultUsers() {
		send(getOldUsers().stream().map(Nerd::of).map(Nerd::getColoredName).collect(Collectors.joining(", ")));
	}

	@Async
	@Path("convert")
	void convert() {
		for (UUID uuid : getOldUsers()) {
			for (int number = 1; number <= 99; number++) {
				final Inventory vault = VaultManager.getInstance().getVault(uuid.toString(), number);
				final long nonAir = Arrays.stream(vault.getContents()).filter(Nullables::isNotNullOrAir).count();
				if (nonAir == 0)
					continue;

				final int items = Arrays.stream(vault.getContents()).filter(Nullables::isNotNullOrAir).mapToInt(ItemStack::getAmount).sum();
				send("Converting " + Nickname.of(uuid) + " #" + number + " (" + items + " items)");
				final int vaultNumber = number;
				service.edit(uuid, user -> {
					user.setLimit(NumericPermission.VAULTS.getLimit(uuid));
					user.update(vaultNumber, Arrays.asList(vault.getContents()));
				});
			}
		}
	}

}
