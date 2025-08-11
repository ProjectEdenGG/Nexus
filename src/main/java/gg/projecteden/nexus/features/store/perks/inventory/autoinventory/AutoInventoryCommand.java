package gg.projecteden.nexus.features.store.perks.inventory.autoinventory;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoCraft;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoTool.AutoToolToolType;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.tasks.FindChestsThread;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoInventoryProfile;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoSortInventoryType;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@NoArgsConstructor
@Aliases("autoinv")
@Redirect(from = "/depositall", to = "/autoinv depositall")
@Redirect(from = "/autosort", to = "/autoinv")
@Redirect(from = "/autotrash", to = "/autoinv settings trash")
@Redirect(from = "/autocraft", to = "/autoinv settings craft")
@WikiConfig(rank = "Store", feature = "Inventory")
public class AutoInventoryCommand extends CustomCommand implements Listener {
	private final AutoInventoryUserService service = new AutoInventoryUserService();
	private AutoInventoryUser user;

	public AutoInventoryCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("profile [profile]")
	@Description("View your currently active profile or switch to another one")
	void profile(@Arg(tabCompleter = AutoInventoryProfile.class) String profile) {
		if (isNullOrEmpty(profile)) {
			send(PREFIX + "Currently active profile: &e" + user.getActiveProfileId());
			return;
		}

		if (!user.getProfiles().containsKey(profile))
			error("Profile &e%s &cnot found".formatted(profile));

		user.setActiveProfile(profile);
		service.save(user);
		send(PREFIX + "Profile &e%s &aactivated".formatted(profile));
	}

	@Path("profile create <profile>")
	@Description("Create a new profile with default settings")
	void profile_create(String profile) {
		if (user.getProfiles().containsKey(profile))
			error("Profile &e%s &calready exists".formatted(profile));

		user.getProfiles().put(profile, new AutoInventoryProfile());
		user.setActiveProfile(profile);
		service.save(user);
		send(PREFIX + "Profile &e%s &acreated and activated".formatted(profile));
	}

	@Path("profile clone <from> <to>")
	@Description("Clone an existing profile")
	void profile_clone(@Arg(tabCompleter = AutoInventoryProfile.class) String from, String to) {
		if (!user.getProfiles().containsKey(from))
			error("Profile &e%s &cnot found".formatted(from));

		if (user.getProfiles().containsKey(to))
			error("Profile &e%s &calready exists. Delete it first if you wish to run this command.".formatted(to));

		user.getProfiles().put(to, user.getProfiles().get(from).clone());
		user.setActiveProfile(to);
		service.save(user);
		send(PREFIX + "Profile &e%s &acreated and activated &3from clone of &e%s".formatted(to, from));
	}

	@Path("profile rename <from> <to>")
	@Description("Rename a profile")
	void profile_rename(@Arg(tabCompleter = AutoInventoryProfile.class) String from, String to) {
		if (!user.getProfiles().containsKey(from))
			error("Profile &e%s &cnot found".formatted(from));

		if (user.getProfiles().containsKey(to))
			error("Profile &e%s &calready exists. Delete it first if you wish to run this command.".formatted(to));

		if (user.getActiveProfileId().equals(from))
			user.setActiveProfile(to);

		user.getProfiles().put(to, user.getProfiles().remove(from));

		service.save(user);
		send(PREFIX + "Profile &e%s &3renamed to &e%s".formatted(from, to));
	}

	@Path("profile delete <profile>")
	@Description("Delete a profile")
	void profile_delete(@Arg(tabCompleter = AutoInventoryProfile.class) String profile) {
		if (!user.getProfiles().containsKey(profile))
			error("Profile &e%s &cnot found".formatted(profile));

		if (user.getActiveProfileId().equals(profile))
			error("Cannot delete your currently active profile. Please activate another profile first.");

		user.getProfiles().remove(profile);
		service.save(user);
		send(PREFIX + "Profile &e%s &cdeleted".formatted(profile));
	}

	@Path("profile list [page]")
	@Description("List your profiles")
	void profile_list(@Arg("1") int page) {
		send(PREFIX + "Available profiles");
		new Paginator<String>()
			.values(user.getProfiles().keySet())
			.formatter((name, index) -> {
				var active = user.getActiveProfileId().equals(name);
				var message = json((active ? "&a" : "&3") + " " + name + (active ? " &e [Active]" : ""));
				if (!active)
					message.command("/autoinv profile " + name).hover("&3Click to activate profile &e" + name);
				return message;
			})
			.command("/autoinv profile list")
			.page(page)
			.send();
	}

	@TabCompleterFor(AutoInventoryProfile.class)
	List<String> tabCompleteAutoInventoryProfile(String filter) {
		if (!isPlayer())
			return Collections.emptyList();

		return service.get(player()).getProfiles().keySet().stream()
			.filter(profile -> profile.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@Path("depositall")
	@Description("Deposit matching items into nearby chests")
	void depositall() {
		AutoInventoryFeature.DEPOSIT_ALL.checkPermission(player());

		Location location = player().getLocation();
		Chunk centerChunk = location.getChunk();
		World world = location.getWorld();
		ChunkSnapshot[][] snapshots = new ChunkSnapshot[3][3];
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++) {
				Chunk chunk = world.getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);
				snapshots[x + 1][z + 1] = chunk.getChunkSnapshot();
			}

		// Create a thread to search those snapshots and create a chain of quick deposit attempts
		int minY = Math.max(world.getMinHeight(), player().getEyeLocation().getBlockY() - 10);
		int maxY = Math.min(world.getMaxHeight(), player().getEyeLocation().getBlockY() + 10);
		int startY = player().getEyeLocation().getBlockY();
		int startX = player().getEyeLocation().getBlockX();
		int startZ = player().getEyeLocation().getBlockZ();
		Thread thread = new FindChestsThread(world, snapshots, minY, maxY, startX, startY, startZ, player());
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Path("protectItem")
	void protectItem() {
		final EquipmentSlot hand = getHandWithToolRequired();
		final ItemBuilder tool = new ItemBuilder(inventory().getItem(hand));
		final boolean newState = !ItemSetting.AUTODEPOSITABLE.of(tool);
		inventory().setItem(hand, tool.setting(ItemSetting.AUTODEPOSITABLE, newState).build());
		send(PREFIX + "This item can " + (newState ? "now": "no longer") + " be auto-deposited");
	}

	@Path("features")
	@Description("View available features")
	void features() {
		send(PREFIX + "Features");
		for (AutoInventoryFeature feature : AutoInventoryFeature.values()) {
			final boolean enabled = user.hasFeatureEnabledRaw(feature);
			final JsonBuilder json = json("&e" + feature + " &7- " + (enabled ? "&aEnabled" : "&cDisabled"))
				.hover("Click to " + (enabled ? "&cdisable" : "&aenable"))
				.command("/autoinv features toggle " + feature.name().toLowerCase() + " " + !enabled)
				.group().newline()
				.next("&7  " + feature.getDescription());

			if (feature.hasExtraDescription())
				json.newline().next("&c  " + feature.getExtraDescription());

			send(json);
		}
	}

	@Path("features toggle <feature> [enable]")
	@Description("Toggle a feature")
	void features_toggle(AutoInventoryFeature feature, Boolean enable) {
		feature.checkPermission(player());

		if (enable == null)
			enable = !user.hasFeatureEnabledRaw(feature);

		if (enable)
			if (!user.getActiveProfile().getDisabledFeatures().contains(feature))
				error(feature + " is already enabled");
			else
				user.getActiveProfile().getDisabledFeatures().remove(feature);
		else
			if (user.getActiveProfile().getDisabledFeatures().contains(feature))
				error(feature + " is already disabled");
			else
				user.getActiveProfile().getDisabledFeatures().add(feature);

		service.save(user);
		send(PREFIX + feature + " " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("settings inventoryTypes")
	@Description("Toggle whether certain inventory types are affected")
	void settings_inventoryTypes() {
		AutoInventoryFeature.SORT_OTHER_INVENTORIES.checkPermission(player());

		new AutoSortInventoryTypeEditor().open(player());
	}

	@Title("AutoSort Inventory Editor")
	private static class AutoSortInventoryTypeEditor extends InventoryProvider {

		@Override
		public void init() {
			final AutoInventoryUserService service = new AutoInventoryUserService();
			final AutoInventoryUser user = service.get(viewer);
			final AutoInventoryProfile profile = user.getActiveProfile();

			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			for (AutoSortInventoryType inventoryType : AutoSortInventoryType.values()) {
				Material material = inventoryType.getMaterial();
				String model = inventoryType.getModel();

				ItemBuilder item = new ItemBuilder(material).name(StringUtils.camelCase(inventoryType));
				if (model != null)
					item.model(model);

				if (!profile.getDisabledInventoryTypes().contains(inventoryType))
					item.lore("&aEnabled");
				else
					item.lore("&cDisabled");

				items.add(ClickableItem.of(item.build(), e -> {
					if (profile.getDisabledInventoryTypes().contains(inventoryType))
						profile.getDisabledInventoryTypes().remove(inventoryType);
					else
						profile.getDisabledInventoryTypes().add(inventoryType);

					service.save(user);

					open(viewer, contents.pagination().getPage());
				}));
			}

			paginate(items);
		}
	}

	@Path("settings tools exclude <toolType> [enable]")
	@Description("Toggle whether AutoTool will activate while holding certain tools")
	void settings_tools_exclude(AutoToolToolType toolType, Boolean enable) {
		AutoInventoryFeature.AUTOTOOL.checkPermission(player());

		if (enable == null)
			enable = user.getActiveProfile().getAutoToolExclude().contains(toolType);

		if (enable)
			user.getActiveProfile().getAutoToolExclude().remove(toolType);
		else
			user.getActiveProfile().getAutoToolExclude().add(toolType);

		service.save(user);
		send(PREFIX + "AutoTool now " + (enable ? "&aincludes" : "&cexcludes") + " &3" + toolType.name().toLowerCase());
	}

	@Path("settings trash materials")
	@Description("Open the AutoTrash configuration menu")
	void settings_trash_materials() {
		AutoInventoryFeature.AUTOTRASH.checkPermission(player());

		if (worldGroup() != WorldGroup.SURVIVAL)
			error("You can only use this command in survival");

		new AutoTrashMaterialEditor(player());
	}

	@Path("settings trash behavior [behavior]")
	@Description("Change the behavior of AutoTrash")
	void settings_trash_behavior(AutoTrashBehavior behavior) {
		AutoInventoryFeature.AUTOTRASH.checkPermission(player());

		if (behavior == null) {
			send("Current behavior is " + camelCase(user.getActiveProfile().getAutoTrashBehavior()));
			return;
		}

		user.getActiveProfile().setAutoTrashBehavior(behavior);
		service.save(user);
		send(PREFIX + "AutoTrash behavior set to " + camelCase(behavior));
	}

	@Getter
	@Title("&eAutoTrash")
	public static class AutoTrashMaterialEditor implements TemporaryMenuListener {
		private final AutoInventoryUserService service = new AutoInventoryUserService();
		private final Player player;

		public AutoTrashMaterialEditor(Player player) {
			this.player = player;

			open(service.get(player).getActiveProfile().getAutoTrashInclude().stream()
				.map(ItemStack::new)
				.sorted(new ItemStackComparator())
				.toList());
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			Set<Material> materials = Arrays.stream(event.getInventory().getContents())
				.filter(Nullables::isNotNullOrAir)
				.map(ItemStack::getType)
				.collect(Collectors.toSet());

			service.edit(player, user -> {
				user.getActiveProfile().setAutoTrashInclude(materials);
				user.sendMessage(StringUtils.getPrefix("AutoTrash") + "Automatically trashing " + materials.size() + " materials");
			});
		}
	}

	@Path("settings crafting")
	@Description("Open the AutoCraft configuration menu")
	void settings_crafting() {
		AutoInventoryFeature.AUTOCRAFT.checkPermission(player());

		new AutoCraftEditor().open(player());
	}

	@Title("AutoCraft Editor")
	private static class AutoCraftEditor extends InventoryProvider {

		@Override
		public void init() {
			final AutoInventoryUserService service = new AutoInventoryUserService();
			final AutoInventoryUser user = service.get(viewer);
			final AutoInventoryProfile profile = user.getActiveProfile();

			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			for (Material material : AutoCraft.getAutoCraftable().keySet()) {
				ItemBuilder item = new ItemBuilder(material);

				if (!profile.getAutoCraftExclude().contains(material))
					item.lore("&aEnabled").glow();
				else
					item.lore("&cDisabled");

				item.lore("", "&f" + AutoCraft.getIngredients(material).stream()
					.map(StringUtils::pretty)
					.collect(Collectors.joining(", ")));

				items.add(ClickableItem.of(item.build(), e -> {
					if (profile.getAutoCraftExclude().contains(material))
						profile.getAutoCraftExclude().remove(material);
					else
						profile.getAutoCraftExclude().add(material);

					service.save(user);

					open(viewer, contents.pagination().getPage());
				}));
			}

			paginate(items);
		}

	}

}
