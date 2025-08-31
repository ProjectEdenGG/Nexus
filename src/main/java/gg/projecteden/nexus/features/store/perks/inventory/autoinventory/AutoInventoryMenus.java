package gg.projecteden.nexus.features.store.perks.inventory.autoinventory;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoCraft;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoTool.AutoToolToolType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoInventoryProfile;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoSortInventoryType;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUserService;
import gg.projecteden.nexus.models.emoji.EmojiUser.Emoji;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.EnumUtils.nextWithLoop;
import static gg.projecteden.nexus.utils.Extensions.camelCase;

public class AutoInventoryMenus {
	private static final int GEAR_WIDTH = 20;

	public static abstract class IAutoInventoryMenu {

		public void refresh(Player player) {
			var service = new AutoInventoryUserService();
			service.save(service.get(player));
			open(player);
		}

		public abstract void open(Player player);
	}

	public static class AutoInventoryMenu extends IAutoInventoryMenu {

		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);
			var gear = Emoji.of("gear_white");

			if (gear == null)
				throw new InvalidInputException("Could not find gear emoji");

			var dialog = new DialogBuilder()
				.title("AutoInventory")
				.multiAction()
				.columns(2);

			dialog.button("Profile: &e" + user.getActiveProfileId(), click -> {
				var profiles = new ArrayList<>(user.getProfiles().keySet());
				var iterator = profiles.iterator();

				String next = profiles.getFirst();
				while (iterator.hasNext()) {
					if (!iterator.next().equals(user.getActiveProfileId()))
						continue;

					if (iterator.hasNext())
						next = iterator.next();
					else
						next = profiles.getFirst();
				}

				user.setActiveProfile(next);
				refresh(player);
			});

			dialog.button(gear.getEmoji(), GEAR_WIDTH, click -> {
				new AutoInventoryProfilesMenu().open(player);
			});

			for (AutoInventoryFeature feature : AutoInventoryFeature.values()) {
				if (feature.isInternal())
					continue;

				var enabled = user.getActiveProfile().getDisabledFeatures().contains(feature);
				var color = enabled ? "&c" : "&a";
				var label = color + feature.toString();
				var tooltip = new JsonBuilder("&7" + feature.getDescription());

				dialog.button(label, tooltip, response -> {
					if (enabled)
						user.getActiveProfile().getDisabledFeatures().remove(feature);
					else
						user.getActiveProfile().getDisabledFeatures().add(feature);
					refresh(player);
				});
				if (feature.hasSettings()) {
					dialog.button(gear.getEmoji(), "Click to view settings", GEAR_WIDTH, click -> {
						if (feature == AutoInventoryFeature.SORT_OTHER_INVENTORIES)
							new AutoSortInventoryTypeEditor(player2 -> new AutoInventoryMenu().open(player2)).open(player);
						else if (feature == AutoInventoryFeature.AUTOCRAFT)
							new AutoCraftEditor(player2 -> new AutoInventoryMenu().open(player2)).open(player);
						else if (feature == AutoInventoryFeature.AUTOTOOL)
							new AutoToolEditor(player2 -> new AutoInventoryMenu().open(player2)).open(player);
						else if (feature == AutoInventoryFeature.AUTOTRASH)
							new AutoInventoryAutoTrashSettingsMenu().open(player);
						else
							throw new InvalidInputException("Feature " + feature + " has no settings");
					});
				} else {
					dialog.button("&7-", "No settings available", GEAR_WIDTH);
				}
			}

			dialog.open(player);
		}
	}

	public static class AutoInventoryProfilesMenu extends IAutoInventoryMenu {

		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);
			var gear = Emoji.of("gear_white");

			if (gear == null)
				throw new InvalidInputException("Could not find gear emoji");

			var dialog = new DialogBuilder()
				.title("AutoInventory Profiles")
				.multiAction()
				.columns(2);

			dialog.button("Create new profile", click -> new AutoInventoryProfileCreateMenu().open(player));

			dialog.button("&7-", GEAR_WIDTH);

			for (var profileId : user.getProfiles().keySet()) {
				var enabled = profileId.equals(user.getActiveProfileId());

				dialog.button((enabled ? "&a" : "") + profileId, click -> {
					user.setActiveProfile(profileId);
					refresh(player);
				});

				dialog.button(gear.getEmoji(), GEAR_WIDTH, click -> {
					new AutoInventoryProfileMenu(profileId).open(player);
				});
			}

			dialog.exitButton("Back", click -> Tasks.wait(1, () -> new AutoInventoryMenu().open(player)));
			dialog.open(player);
		}
	}

	@Data
	public static class AutoInventoryProfileMenu extends IAutoInventoryMenu {
		private final String profileId;

		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);
			var isActiveProfile = profileId.equals(user.getActiveProfileId());

			var dialog = new DialogBuilder()
				.title("AutoInventory Profile: " + profileId)
				.multiAction()
				.columns(1)
				.button("Rename", click -> new AutoInventoryProfileRenameMenu(profileId).open(player))
				.button("Clone", click -> new AutoInventoryProfileCloneMenu(profileId).open(player));

			if (isActiveProfile)
				dialog.button("&7Delete", "&cYou cannot delete your currently active profile");
			else
				dialog.button("&cDelete", click -> new AutoInventoryProfileDeleteMenu(profileId).open(player));

			dialog
				.exitButton("Back", click -> Tasks.wait(1, () -> new AutoInventoryProfilesMenu().open(player)))
				.open(player);
		}
	}

	@Data
	public static class AutoInventoryProfileCreateMenu extends IAutoInventoryMenu {
		private String errorMessage;

		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);

			var dialog = new DialogBuilder()
				.title("Create new AutoInventory Profile")
				.bodyText("Please type the name of your new profile")
				.inputText("profileId", errorMessage == null ? "New Profile Name" : errorMessage)
				.confirmation()
				.submitText("Create profile")
				.onCancel(click -> new AutoInventoryProfilesMenu().open(player))
				.onSubmit(click -> {
					var profileId = click.getText("profileId");

					if (user.getProfiles().containsKey(profileId)) {
						errorMessage = "Profile &e%s &calready exists".formatted(profileId);
						refresh(player);
					}

					user.getProfiles().put(profileId, new AutoInventoryProfile());
					user.setActiveProfile(profileId);
					service.save(user);
					new AutoInventoryProfilesMenu().open(player);
				});

			dialog.open(player);
		}

	}

	@Data
	public static class AutoInventoryProfileRenameMenu extends IAutoInventoryMenu {
		private final String profileId;
		private String errorMessage;

		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);

			var dialog = new DialogBuilder()
				.title("Rename AutoInventory Profile: " + profileId)
				.bodyText("Please type the new name of your profile")
				.inputText("profileId", errorMessage == null ? "New Name" : errorMessage)
				.confirmation()
				.submitText("Rename profile")
				.onCancel(click -> new AutoInventoryProfileMenu(profileId).open(player))
				.onSubmit(click -> {
					var from = profileId;
					var to = click.getText("profileId");

					if (!user.getProfiles().containsKey(from)) {
						errorMessage = "Profile &e%s &cnot found".formatted(from);
						refresh(player);
					}

					if (user.getProfiles().containsKey(to)) {
						errorMessage = "Profile &e%s &calready exists".formatted(to);
						refresh(player);
					}

					if (user.getActiveProfileId().equals(from))
						user.setActiveProfile(to);

					user.getProfiles().put(to, user.getProfiles().remove(from));
					service.save(user);
					new AutoInventoryProfilesMenu().open(player);
				});

			dialog.open(player);
		}

	}

	@Data
	public static class AutoInventoryProfileCloneMenu extends IAutoInventoryMenu {
		private final String profileId;
		private String errorMessage;

		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);

			var dialog = new DialogBuilder()
				.title("Clone AutoInventory Profile: " + profileId)
				.bodyText("Please type the name of your new profile")
				.inputText("profileId", errorMessage == null ? "New Profile Name" : errorMessage)
				.confirmation()
				.submitText("Clone profile")
				.onCancel(click -> new AutoInventoryProfileMenu(profileId).open(player))
				.onSubmit(click -> {
					var from = profileId;
					var to = click.getText("profileId");

					if (!user.getProfiles().containsKey(from)) {
						errorMessage = "Profile &e%s &cnot found".formatted(from);
						refresh(player);
					}

					if (user.getProfiles().containsKey(to)) {
						errorMessage = "Profile &e%s &calready exists. Delete it first if you wish to run this command.".formatted(to);
						refresh(player);
					}

					user.getProfiles().put(to, user.getProfiles().get(from).clone());
					user.setActiveProfile(to);
					service.save(user);
					new AutoInventoryProfilesMenu().open(player);
				});

			dialog.open(player);
		}

	}

	@Data
	public static class AutoInventoryProfileDeleteMenu extends IAutoInventoryMenu {
		private final String profileId;

		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);

			new DialogBuilder()
				.title("&cDelete AutoInventory Profile: " + profileId)
				.bodyText("&cAre you sure you want to delete your &e" + profileId + "&c profile?")
				.bodyBlankLine()
				.bodyText("&4WARNING: &cThis cannot be undone")
				.confirmation()
				.submitText("Delete profile")
				.onCancel(click2 -> new AutoInventoryProfileMenu(profileId).open(player))
				.onSubmit(click2 -> {
					var profile = profileId;
					if (!user.getProfiles().containsKey(profile)) {
						new DialogBuilder()
							.title("&cError")
							.bodyText("&cProfile &e" + profile + "&c not found")
							.notice()
							.button("Back to profile list", click3 -> Tasks.wait(1, () -> new AutoInventoryProfilesMenu().open(player)))
							.open(player);
						return;
					}

					if (user.getActiveProfileId().equals(profile)) {
						new DialogBuilder()
							.title("&cError")
							.bodyText("&cCannot delete your currently active profile. Please activate another profile first.")
							.notice()
							.button("Back to profile list", click3 -> Tasks.wait(1, () -> new AutoInventoryProfilesMenu().open(player)))
							.open(player);
						return;
					}

					user.getProfiles().remove(profile);
					service.save(user);
					new AutoInventoryProfilesMenu().open(player);
				})
				.open(player);
		}
	}

	public static class AutoInventoryAutoTrashSettingsMenu extends IAutoInventoryMenu {
		public void open(Player player) {
			var service = new AutoInventoryUserService();
			var user = service.get(player);
			var behavior = user.getActiveProfile().getAutoTrashBehavior();

			new DialogBuilder()
				.title(user.getActiveProfileId() + ": AutoTrash")
				.multiAction()
				.columns(1)
				.button("Behavior: " + camelCase(behavior), click -> {
					var next = nextWithLoop(AutoTrashBehavior.class, behavior.ordinal());
					user.getActiveProfile().setAutoTrashBehavior(next);
					service.save(user);
					refresh(player);
				})
				.button("Edit Materials", click -> {
					new AutoTrashMaterialEditor(player, WorldGroup.of(player), player2 -> {
						Tasks.wait(1, () -> refresh(player));
					});
				})
				.exitButton("Back", click -> Tasks.wait(1, () -> new AutoInventoryMenu().open(player)))
				.open(player);
		}
	}

	@RequiredArgsConstructor
	@Title("AutoSort Inventory Editor")
	static class AutoSortInventoryTypeEditor extends InventoryProvider {
		private final Consumer<Player> onClose;

		@Override
		public void init() {
			final var service = new AutoInventoryUserService();
			final var user = service.get(viewer);
			final var profile = user.getActiveProfile();

			addCloseItem(onClose);

			List<ClickableItem> items = new ArrayList<>();
			for (AutoSortInventoryType inventoryType : AutoSortInventoryType.values()) {
				var material = inventoryType.getMaterial();
				var model = inventoryType.getModel();
				var item = new ItemBuilder(material).name(StringUtils.camelCase(inventoryType));

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

	@Getter
	@Title("&eAutoTrash")
	public static class AutoTrashMaterialEditor implements TemporaryMenuListener {
		private final AutoInventoryUserService service = new AutoInventoryUserService();
		private final Player player;
		private final WorldGroup worldGroup;
		private final Consumer<Player> onClose;

		public AutoTrashMaterialEditor(Player player, WorldGroup worldGroup, Consumer<Player> onClose) {
			this.player = player;
			this.worldGroup = worldGroup;
			this.onClose = onClose;

			open(service.get(player).getActiveProfile().getAutoTrashInclude()
				.computeIfAbsent(worldGroup, $ -> new HashSet<>())
				.stream()
				.map(ItemStack::new)
				.sorted(new ItemStackComparator())
				.toList());
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			var materials = Arrays.stream(event.getInventory().getContents())
				.filter(Nullables::isNotNullOrAir)
				.map(ItemStack::getType)
				.collect(Collectors.toSet());

			service.edit(player, user -> {
				user.getActiveProfile().getAutoTrashInclude().put(worldGroup, materials);
				user.sendMessage(StringUtils.getPrefix("AutoTrash") + "Automatically trashing " + materials.size() + " materials");
				onClose.accept(player);
			});
		}
	}

	@RequiredArgsConstructor
	@Title("AutoCraft Editor")
	static class AutoCraftEditor extends InventoryProvider {
		private final Consumer<Player> onClose;

		@Override
		public void init() {
			final var service = new AutoInventoryUserService();
			final var user = service.get(viewer);
			final var profile = user.getActiveProfile();

			addCloseItem(onClose);

			List<ClickableItem> items = new ArrayList<>();
			for (Material material : AutoCraft.getAutoCraftable().keySet()) {
				var item = new ItemBuilder(material);

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

	@RequiredArgsConstructor
	@Title("AutoTool Editor")
	static class AutoToolEditor extends InventoryProvider {
		private final Consumer<Player> onClose;

		@Override
		public void init() {
			final var service = new AutoInventoryUserService();
			final var user = service.get(viewer);
			final var profile = user.getActiveProfile();

			addCloseItem(onClose);

			List<ClickableItem> items = new ArrayList<>();
			for (AutoToolToolType toolType : AutoToolToolType.values()) {
				var material = toolType.getToolType().getTools().getLast();
				var item = new ItemBuilder(material).name(camelCase(toolType));

				if (!profile.getAutoToolExclude().contains(toolType))
					item.lore("&aEnabled").glow();
				else
					item.lore("&cDisabled");

				items.add(ClickableItem.of(item.build(), e -> {
					if (profile.getAutoToolExclude().contains(toolType))
						profile.getAutoToolExclude().remove(toolType);
					else
						profile.getAutoToolExclude().add(toolType);

					service.save(user);

					open(viewer, contents.pagination().getPage());
				}));
			}

			paginate(items);
		}

	}
}
