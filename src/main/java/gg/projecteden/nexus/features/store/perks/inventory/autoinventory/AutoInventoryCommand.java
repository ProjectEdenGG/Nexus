package gg.projecteden.nexus.features.store.perks.inventory.autoinventory;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoCraft;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.tasks.FindChestsThread;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoSortInventoryType;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

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
			if (!user.getDisabledFeatures().contains(feature))
				error(feature + " is already enabled");
			else
				user.getDisabledFeatures().remove(feature);
		else
			if (user.getDisabledFeatures().contains(feature))
				error(feature + " is already disabled");
			else
				user.getDisabledFeatures().add(feature);

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

			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			for (AutoSortInventoryType inventoryType : AutoSortInventoryType.values()) {
				Material material = inventoryType.getMaterial();
				int modelId = inventoryType.getModelId();

				ItemBuilder item = new ItemBuilder(material).name(StringUtils.camelCase(inventoryType));
				if (modelId > 0)
					item.modelId(modelId);

				if (!user.getDisabledInventoryTypes().contains(inventoryType))
					item.lore("&aEnabled");
				else
					item.lore("&cDisabled");

				items.add(ClickableItem.of(item.build(), e -> {
					if (user.getDisabledInventoryTypes().contains(inventoryType))
						user.getDisabledInventoryTypes().remove(inventoryType);
					else
						user.getDisabledInventoryTypes().add(inventoryType);

					service.save(user);

					open(viewer, contents.pagination().getPage());
				}));
			}

			paginate(items);
		}
	}

	@Path("settings tools includeSword [enable]")
	@Description("Toggle whether AutoTool will activate while holding a sword")
	void settings_tools_includeSword(Boolean enable) {
		AutoInventoryFeature.AUTOTOOL.checkPermission(player());

		if (enable == null)
			enable = !user.isAutoToolIncludeSword();

		user.setAutoToolIncludeSword(enable);
		service.save(user);
		send(PREFIX + "AutoTool now " + (enable ? "&aincludes" : "&cexcludes") + " &3swords");
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
			send("Current behavior is " + camelCase(user.getAutoTrashBehavior()));
			return;
		}

		user.setAutoTrashBehavior(behavior);
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

			open(service.get(player).getAutoTrashInclude().stream()
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
				user.setAutoTrashInclude(materials);
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

			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			for (Material material : AutoCraft.getAutoCraftable().keySet()) {
				ItemBuilder item = new ItemBuilder(material);

				if (!user.getAutoCraftExclude().contains(material))
					item.lore("&aEnabled").glow();
				else
					item.lore("&cDisabled");

				item.lore("", "&f" + AutoCraft.getIngredients(material).stream()
					.map(StringUtils::pretty)
					.collect(joining(", ")));

				items.add(ClickableItem.of(item.build(), e -> {
					if (user.getAutoCraftExclude().contains(material))
						user.getAutoCraftExclude().remove(material);
					else
						user.getAutoCraftExclude().add(material);

					service.save(user);

					open(viewer, contents.pagination().getPage());
				}));
			}

			paginate(items);
		}

	}

}
