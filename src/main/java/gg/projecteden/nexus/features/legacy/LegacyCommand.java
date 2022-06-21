package gg.projecteden.nexus.features.legacy;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.legacy.menus.homes.LegacyHomesMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemPendingMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemReceiveMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemReviewMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemTransferMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ReviewableMenu;
import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.legacy.homes.LegacyHome;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeOwner;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeService;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUser;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUserService;
import gg.projecteden.nexus.models.nerd.NBTPlayer;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LegacyCommand extends _WarpSubCommand {
	private final LegacyHomeService legacyHomeService = new LegacyHomeService();
	private LegacyHomeOwner legacyHomeOwner;

	public LegacyCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			legacyHomeOwner = legacyHomeService.get(player());
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.LEGACY;
	}

	private void legacyOnly() {
		if (worldGroup() != WorldGroup.LEGACY)
			error("You can only run this command in the legacy world");
	}

	// Items

	@Path("items transfer")
	@Description("Submit legacy items for transfer review")
	void items_transfer() {
		legacyOnly();
		new ItemTransferMenu(player());
	}

	@Path("items pending")
	@Description("View legacy items pending transfer approval")
	void items_pending() {
		new ItemPendingMenu(player()).open(player());
	}

	@Path("items review [player]")
	@Description("Review pending items")
	@Permission(Group.ADMIN)
	void items_review(LegacyItemTransferUser user) {
		if (user == null)
			new ReviewableMenu().open(player());
		else
			new ItemReviewMenu(user).open(player());
	}

	@Path("items receive")
	@Description("Receive transfer approved legacy items")
	void items_receive() {
		new ItemReceiveMenu(player());
	}

	// Homes

	@Path("home <home>")
	@Description("Teleport to your legacy homes")
	void home(@Arg LegacyHome legacyHome) {
		legacyHome.teleportAsync(player());
	}

	@Path("home tp <player> <home>")
	@Description("Teleport to another player's legacy homes")
	void home_tp(OfflinePlayer player, @Arg(context = 1) LegacyHome legacyHome) {
		legacyHome.teleportAsync(player());
	}

	@Path("homes [player]")
	@Description("View a list of legacy homes")
	void homes(@Arg("self") LegacyHomeOwner legacyHomeOwner) {
		new LegacyHomesMenu(legacyHomeOwner).open(player());
	}

	@Path("homes setItem <home> <material>")
	@Description("Set the display item for a legacy home")
	void homes_setItem(LegacyHome home, Material material) {
		home.setItem(new ItemStack(material));
		legacyHomeService.save(legacyHomeOwner);
		send(PREFIX + "Legacy home display item set to " + camelCase(material));
	}

	@Path("homes set <name>")
	@Description("Set a new legacy home")
	void homes_set(String legacyHomeName, @Arg(value = "self", permission = Group.STAFF) LegacyHomeOwner legacyHomeOwner) {
		legacyOnly();

		Optional<LegacyHome> home = legacyHomeOwner.getHome(legacyHomeName);

		String message;
		if (home.isPresent()) {
			home.get().setLocation(location());
			message = "Updated location of legacy home &e" + legacyHomeName + "&3";
		} else {
			legacyHomeOwner.add(LegacyHome.builder()
				.uuid(legacyHomeOwner.getUuid())
				.name(legacyHomeName)
				.location(location()));
			message = "Legacy home &e" + legacyHomeName + "&3 set to current location. Return with &c/legacy homes tp " + legacyHomeName;
		}

		legacyHomeService.save(legacyHomeOwner);
		send(PREFIX + message);
	}

	@Path("homes delete <name>")
	@Description("Delete a legacy home")
	void homes_delete(@Arg("home") LegacyHome legacyHome, @Arg(value = "self", permission = Group.STAFF) LegacyHomeOwner legacyHomeOwner) {
		legacyHomeOwner.delete(legacyHome);
		legacyHomeService.save(legacyHomeOwner);

		send(PREFIX + "Legacy home &e" + legacyHome.getName() + "&3 deleted");
	}

	@ConverterFor(LegacyHome.class)
	LegacyHome convertToLegacyHome(String value, OfflinePlayer context) {
		if (context == null) context = player();
		return legacyHomeService.get(context).getHome(value).orElseThrow(() -> new InvalidInputException("That legacy home does not exist"));
	}

	@TabCompleterFor(LegacyHome.class)
	public List<String> tabCompleteLegacyHome(String filter, OfflinePlayer context) {
		if (context == null) context = player();
		return legacyHomeService.get(context).getNames(filter);
	}

	// Vaults

	@Path("vaults [page] [user]")
	@Description("Open a legacy vault")
	void vaults(@Arg(value = "1", min = 1) int page, @Arg(value = "self", permission = Group.SENIOR_STAFF) LegacyVaultUser user) {
		legacyOnly();

		new LegacyVaultMenu(player(), user, page);
	}

	@Path("vaults limit [user]")
	@Description("View how many legacy vaults you own")
	void vaults_limit(@Arg(value = "self", permission = Group.SENIOR_STAFF) LegacyVaultUser user) {
		send(PREFIX + (isSelf(user) ? "You own" : user.getNickname() + " owns") + " &e" + user.getLimit() + " &3legacy vaults");
	}

	public static class LegacyVaultMenu implements TemporaryMenuListener {
		private final LegacyVaultUserService service = new LegacyVaultUserService();
		@Getter
		private final Player player;
		private final LegacyVaultUser user;
		private final int page;

		@Getter
		private final LegacyVaultHolder inventoryHolder = new LegacyVaultHolder();

		public LegacyVaultMenu(Player player, LegacyVaultUser user, int page) {
			this.player = player;
			this.user = user;
			this.page = page;

			open(user.get(page, player));
		}

		@Override
		public String getTitle() {
			return "Vault #" + page;
		}

		public static class LegacyVaultHolder extends CustomInventoryHolder {}

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

	// Archival

	/*
	static final List<SubWorldGroup> subWorldGroups = Arrays.asList(SubWorldGroup.SURVIVAL, SubWorldGroup.LEGACY1, SubWorldGroup.LEGACY2, SubWorldGroup.LEGACY);

	@Async
	@Path("archive homes")
	@Permission(Group.ADMIN)
	void archive_homes() {
		int count = 0;
		final HomeService homeService = new HomeService();
		final LegacyHomeService legacyHomeService = new LegacyHomeService();

		for (HomeOwner uuid : homeService.getAll()) {
			HomeOwner homeOwner = homeService.get(uuid);
			LegacyHomeOwner legacyHomeOwner = legacyHomeService.get(uuid);

			final List<Home> homes = homeOwner.getHomes();
			if (homes.isEmpty())
				continue;

			for (Home home : new ArrayList<>(homes)) {
				if (!subWorldGroups.contains(SubWorldGroup.of(home.getLocation())))
					continue;

				legacyHomeOwner.getHomes().add(LegacyHome.builder()
					.uuid(home.getUniqueId())
					.name(home.getName())
					.location(home.getLocation())
					.item(home.getItem())
					.build());

				++count;

				homeOwner.getHomes().removeIf(_home -> home.getName().equals(_home.getName()));
			}
		}

		legacyHomeService.saveCache();
		homeService.saveCache();

		send(PREFIX + "Archived " + count + " survival homes");
	}

	@Async
	@Path("archive balances")
	@Permission(Group.ADMIN)
	void archive_balances() {
		final LegacyUserService legacyUserService = new LegacyUserService();
		final BankerService bankerService = new BankerService();
		int count = 0;

		for (Banker banker : bankerService.getAll()) {
			if (!banker.getBalances().containsKey(ShopGroup.SURVIVAL))
				continue;

			legacyUserService.edit(banker, legacyUser -> legacyUser.setBalance(banker.getBalance(ShopGroup.SURVIVAL)));

			++count;

			banker.getBalances().remove(ShopGroup.SURVIVAL);
			bankerService.save(banker);
		}

		send(PREFIX + "Archived " + count + " balances");
	}

	@Async
	@Path("archive vaults")
	@Permission(Group.ADMIN)
	void archive_vaults() {
		final LegacyVaultUserService legacyVaultService = new LegacyVaultUserService();
		final VaultUserService vaultService = new VaultUserService();
		AtomicInteger countVaults = new AtomicInteger();
		AtomicInteger countUsers = new AtomicInteger();

		for (VaultUser uuid : vaultService.getAll()) {
			VaultUser user = vaultService.get(uuid);

			legacyVaultService.edit(user, legacyUser -> {
				legacyUser.setVaults(user.getVaults());
				legacyUser.setLimit(user.getLimit());
				countVaults.getAndAdd(legacyUser.getVaults().size());
			});

			countUsers.getAndIncrement();

			user.getVaults().clear();
			vaultService.save(user);
		}

		send(PREFIX + "Archived " + countVaults + " vaults for " + countUsers + " users");
	}

	@Async
	@Path("archive mcmmo")
	@Permission(Group.ADMIN)
	void archive_mcmmo() {
		final LegacyUserService legacyUserService = new LegacyUserService();
		int countLevels = 0;
		int countUsers = 0;

		for (Nerd nerd : new NerdService().getAll()) {
			final LegacyUser legacyUser = legacyUserService.get(nerd);
			final PlayerProfile mcmmoPlayer = mcMMO.getDatabaseManager().loadPlayerProfile(nerd.getUniqueId());

			for (PrimarySkillType skill : PrimarySkillType.values()) {
				final int level = mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name()));
				if (level == 0)
					continue;

				legacyUser.getMcmmo().put(skill.name(), level);

				++countLevels;

				mcmmoPlayer.modifySkill(skill, 0);
			}

			if (!legacyUser.getMcmmo().isEmpty()) {
				legacyUserService.save(legacyUser);
				++countUsers;
			}
		}

		send(PREFIX + "Archived " + countLevels + " mcmmo levels for " + countUsers + " users");
	}

	@Async
	@Path("archive mail")
	@Permission(Group.ADMIN)
	void archive_mail() {
		AtomicInteger count = new AtomicInteger();

		final MailerService service = new MailerService();
		final LegacyMailerService legacyService = new LegacyMailerService();
		for (Mailer uuid : service.getAll()) {
			service.edit(uuid, mailer -> {
				legacyService.edit(uuid, legacyMailer -> {
					legacyMailer.setMail(mailer.getMail(WorldGroup.SURVIVAL));
					legacyMailer.setPendingMail(mailer.getPendingMail().getOrDefault(WorldGroup.SURVIVAL, null));
				});

				count.addAndGet(mailer.getMail(WorldGroup.SURVIVAL).size());
				mailer.getMail().remove(WorldGroup.SURVIVAL);
				mailer.getPendingMail().remove(WorldGroup.SURVIVAL);
			});
		}

		send(PREFIX + "Archived " + count + " mail");
	}

	@Async
	@Path("archive shops")
	@Permission(Group.ADMIN)
	void archive_shops() {
		final ShopService service = new ShopService();
		final LegacyShopService legacyService = new LegacyShopService();
		final AtomicInteger countProduct = new AtomicInteger();
		final AtomicInteger countShop = new AtomicInteger();

		for (Shop uuid : service.getAll()) {
			service.edit(uuid, shop -> {
				countProduct.getAndAdd(shop.getProducts(ShopGroup.SURVIVAL).size());

				legacyService.edit(uuid, legacyShop -> {
					legacyShop.setProducts(shop.getProducts(ShopGroup.SURVIVAL));
					legacyShop.setHolding(shop.getHolding());
				});

				final boolean hasProducts = !shop.getProducts(ShopGroup.SURVIVAL).isEmpty();
				final boolean hasHolding = !shop.getHolding().isEmpty();
				if (hasProducts || hasHolding)
					countShop.getAndIncrement();

				shop.getProducts().removeIf(product -> product.getShopGroup() == ShopGroup.SURVIVAL);
				shop.getHolding().clear();
			});
		}

		send(PREFIX + "Archived " + countProduct + " products for " + countShop + " users");
	}
	*/

	@Async
	@Permission(Group.ADMIN)
	@Path("archive inventories")
	void archive_inventories() {
		final List<ItemStack> inventory = nerd().getInventory();
		final List<ItemStack> nbtInventory = new NBTPlayer(nerd()).getOfflineInventory();

		for (int i = 0; i < inventory.size(); i++) {
			final ItemStack item = inventory.get(i);
			final ItemStack nbtItem = nbtInventory.get(i);
			if (Objects.equals(item, nbtItem))
				send("&a" + i + ": " + item + " == " + nbtItem);
			else
				send("&c" + i + ": " + item + " != " + nbtItem);
		}
	}

}
