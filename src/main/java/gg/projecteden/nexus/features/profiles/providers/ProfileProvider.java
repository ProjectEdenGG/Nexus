package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.features.commands.BankCommand;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.commands.SocialMediaCommand;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.FontUtils.FontType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.Style.Builder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

// TODO:
//  - INVITE TO PARTY BUTTON
//	- /PARTY FRIENDS -> INVITE ALL FRIENDS TO PARTY
public class ProfileProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	private static final SocialMediaUserService socialMediaUserService = new SocialMediaUserService();
	private final SocialMediaUser socialMediaUser;
	private static final FriendsUserService friendService = new FriendsUserService();
	private final OfflinePlayer target;

	private int ticks = 0;
	private int index = 0;
	private final int maxIndex;

	public ProfileProvider(OfflinePlayer target, @Nullable InventoryProvider previousMenu) {
		this(target);
		this.previousMenu = previousMenu;
	}

	public ProfileProvider(OfflinePlayer target) {
		this.target = target;
		socialMediaUser = socialMediaUserService.get(target);
		maxIndex = Arrays.stream(SocialMediaSite.values()).filter(site -> socialMediaUser.getConnection(site) != null).toList().size() - 1;
	}

	@Override
	protected int getRows(Integer page) {
		return 5;
	}

	@Override
	public ComponentLike getTitleComponent() {
		Builder style = Style.style();
		style.font(FontType.TOOL_TIP_LINE_1.getFont());

		return new JsonBuilder("A").style(style.build()).group().next("A").asComponent();
	}

//	@Override
//	public String getTitle() {
//		String whose = PlayerUtils.isSelf(viewer, target) ? "Your" : nickname(target) + "'s";
//		return "&3" + whose + " Profile &c(WIP)";

//		return ProfileTitle.SELF.getTitle();
//	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		// TODO: do rank using magic spaces, and text

		for (ProfileMenuItem menuItem : ProfileMenuItem.values()) {
			menuItem.setClickableItem(viewer, target, contents, this);
			menuItem.setExtraClickableItems(viewer, target, contents, this);
		}

	}

	@Override
	public void update() {
		if (maxIndex == 0)
			return;

		ticks++;
		if (ticks == TickTime.SECOND.x(3))
			ticks = 0;

		if (ticks != 1)
			return;

		SlotPos slotPos = ProfileMenuItem.VIEW_SOCIAL_MEDIA.getSlotPos();
		ClickableItem clickableItem = ProfileMenuItem.VIEW_SOCIAL_MEDIA.getClickableItem(viewer, target, index, this);
		contents.set(slotPos, clickableItem);

		index++;
		if (index > maxIndex)
			index = 0;
	}

	@AllArgsConstructor
	enum ProfileTitle {
		SELF("升"),
		;

		private final String title;

		public String getTitle() {
			return colorize("&fꈉ" + title);
		}
	}

	@Getter
	@AllArgsConstructor
	enum ProfileMenuItem {
		PLAYER(3, 4, Material.PLAYER_HEAD, 2) {
			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Presence.of(target).getName();
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return getPresenceLore(viewer, target);
			}

			@Override
			public ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target) {
				return super.getItemBuilder(viewer, target).skullOwner(target);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return List.of(
					SlotPos.of(2, 3), SlotPos.of(2, 4), /* 		Presence		*/
					SlotPos.of(3, 3), /* 		Player Head		*/ SlotPos.of(3, 5),
					SlotPos.of(4, 3), SlotPos.of(4, 4), SlotPos.of(4, 5)
				);
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}
		},

		PRESENCE(2, 5, CustomMaterial.PRESENCE_OFFLINE) {
			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Presence.of(target).getName();
			}

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				return Presence.of(target, viewer).getModelId();
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return getPresenceLore(viewer, target);
			}
		},

		WALLET(1, 1, CustomMaterial.GUI_PROFILE_BUTTON_WALLET) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return BankCommand.getLines(Nerd.of(viewer), Nerd.of(target));
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}
		},

		LEVELS(2, 1, CustomMaterial.GUI_PROFILE_BUTTON_LEVELS) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}
		},

		VIEW_SOCIAL_MEDIA(3, 1, CustomMaterial.GUI_PROFILE_BUTTON_SOCIAL) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> connectedSites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				return connectedSites.size() != 0;
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				SocialMediaCommand.open(viewer, target, "/profile " + nickname(target));
			}
		},

		LINK_SOCIAL_MEDIA(3, 1, CustomMaterial.GUI_PROFILE_BUTTON_SOCIAL) {  // TODO: change item

			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> connectedSites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				return connectedSites.size() == 0;
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				viewer.closeInventory();
				PlayerUtils.runCommand(viewer, "socialmedia help"); // TODO: make gui if possible
			}
		},

		MODIFY_FRIEND(4, 0, CustomMaterial.GUI_PROFILE_BUTTON_FRIENDS_SHORT_ADD) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				return !PlayerUtils.isSelf(viewer, target);
			}

			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				if (ProfileMenuItem.isFriendsWith(friendService.get(target), viewer))
					return "&3Remove Friend";

				return "&3Send Friend Request";
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				if (ProfileMenuItem.isFriendsWith(friendService.get(target), viewer))
					return List.of("&eClick &3to remove &e" + nickname(target));

				return List.of("&eClick &3to send request to &e" + nickname(target));
			}

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				if (ProfileMenuItem.isFriendsWith(friendService.get(target), viewer))
					return CustomMaterial.GUI_PROFILE_BUTTON_FRIENDS_SHORT_REMOVE.getModelId();

				return CustomMaterial.GUI_PROFILE_BUTTON_FRIENDS_SHORT_ADD.getModelId();
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				FriendsUser userFriend = friendService.get(viewer);
				FriendsUser targetFriend = friendService.get(target);
				if (ProfileMenuItem.isFriendsWith(friendService.get(target), viewer))
					userFriend.removeFriend(targetFriend);
				else
					userFriend.sendRequest(targetFriend);
			}
		},

		VIEW_FRIENDS(4, 1, CustomMaterial.GUI_PROFILE_BUTTON_FRIENDS) {
			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				if (PlayerUtils.isSelf(viewer, target))
					return CustomMaterial.GUI_PROFILE_BUTTON_FRIENDS.getModelId();

				return CustomMaterial.GUI_PROFILE_BUTTON_FRIENDS_SHORT.getModelId();
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return List.of(SlotPos.of(getRow(), getCol() + 1));
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				new FriendsProvider(target, viewer, previousMenu).open(viewer);
			}
		},

		//

		TELEPORT(1, 7, CustomMaterial.GUI_PROFILE_BUTTON_TELEPORT) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO
			}
		},

		VIEW_SHOP(2, 7, CustomMaterial.GUI_PROFILE_BUTTON_SHOP) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO
			}
		},

		VIEW_HOMES(3, 7, CustomMaterial.GUI_PROFILE_BUTTON_HOMES) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO: make gui if possible
			}
		},

		EDIT_TRUSTS(4, 7, CustomMaterial.GUI_PROFILE_BUTTON_TRUSTS) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				return !PlayerUtils.isSelf(viewer, target);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return getCentered3Wide();
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO
			}
		},
		;

		@NotNull
		private static List<String> getPresenceLore(Player viewer, OfflinePlayer target) {
			Nerd targetNerd = Nerd.of(target);

			List<String> lines = new ArrayList<>();

			// Current Time
			GeoIP geoip = new GeoIPService().get(target);
			if (GeoIP.exists(geoip))
				lines.add("&3Local Time: &e" + geoip.getCurrentTimeShort());

			// Last Seen / Online For
			if (target.isOnline() && PlayerUtils.canSee(viewer, target)) {
				LocalDateTime lastJoin = targetNerd.getLastJoin(viewer);
				lines.add("&3Online for: &e" + Timespan.of(lastJoin).format());
			} else {
				LocalDateTime lastQuit = targetNerd.getLastQuit(viewer);
				lines.add("&3Last seen: &e" + Timespan.of(lastQuit).format());
			}

			// First Join
			lines.add("&3First join: &e" + shortDateTimeFormat(targetNerd.getFirstJoin()));

			// Hours
			Hours hours = new HoursService().get(target);
			lines.add("&3Hours: &e" + TimespanBuilder.ofSeconds(hours.getTotal()).noneDisplay(true).format());
			return lines;
		}

		private final int row, col;
		private final Material material;
		private final int modelId;

		private static final String TODO = "&cTODO";

		ProfileMenuItem(int row, int col, CustomMaterial customMaterial) {
			this(row, col, customMaterial.getMaterial(), customMaterial.getModelId());
		}

		public boolean shouldShow(Player viewer, OfflinePlayer target) {
			return true;
		}

		public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {}

		public SlotPos getSlotPos() {
			return SlotPos.of(row, col);
		}

		public String getName(Player viewer, OfflinePlayer target) {
			return "&e" + StringUtils.camelCase(this);
		}

		public int getModelId(Player viewer, OfflinePlayer target) {
			return modelId;
		}

		public int getModelId(Player viewer, OfflinePlayer target, int index) {
			return modelId;
		}

		public List<String> getLore(Player viewer, OfflinePlayer target) {
			return Collections.emptyList();
		}

		public ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target) {
			int modelId = getModelId(viewer, target);
			if (modelId == -1)
				return null;

			return new ItemBuilder(getMaterial())
				.modelId(getModelId(viewer, target))
				.name(getName(viewer, target))
				.lore(getLore(viewer, target))
				.clone();
		}

		public @Nullable ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target, int index) {
			int modelId = getModelId(viewer, target, index);
			if (modelId == -1)
				return null;

			return new ItemBuilder(getMaterial())
				.modelId(modelId)
				.name(getName(viewer, target))
				.lore(getLore(viewer, target))
				.clone();
		}

		public List<SlotPos> getExtraSlots() {
			return Collections.emptyList();
		}

		public List<SlotPos> getCentered3Wide() {
			return List.of(SlotPos.of(getRow(), getCol() - 1), /* 		Main Item		*/ SlotPos.of(getRow(), getCol() + 1));
		}

		public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
			return getItemBuilder(viewer, target).clone();
		}

		public ItemBuilder getInvisibleCopy(Player viewer, OfflinePlayer target) {
			return getItemBuilder(viewer, target).clone().material(CustomMaterial.INVISIBLE);
		}


		private static boolean isFriendsWith(FriendsUser friend, Player viewer) {
			return friend.isFriendsWith(friendService.get(viewer));
		}

		public void setClickableItem(Player player, OfflinePlayer target, InventoryContents contents, InventoryProvider previousMenu) {
			ClickableItem clickableItem = getClickableItem(player, target, previousMenu);
			if (clickableItem == null)
				return;

			contents.set(getSlotPos(), clickableItem);
		}

		public void setExtraClickableItems(Player viewer, OfflinePlayer target, InventoryContents contents, InventoryProvider previousMenu) {
			for (SlotPos slotPos : getExtraSlots()) {
				ClickableItem clickableItem = getClickableItem(viewer, target, getExtraSlotItemBuilder(viewer, target), previousMenu);
				if (clickableItem == null)
					continue;

				contents.set(slotPos, clickableItem);
			}
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target), previousMenu);
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, int index, InventoryProvider previousMenu) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target, index), previousMenu);
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, ItemBuilder itemBuilder, InventoryProvider previousMenu) {
			if (Nullables.isNullOrAir(itemBuilder) || !shouldShow(viewer, target))
				return null;

			return ClickableItem.of(itemBuilder, e -> onClick(viewer, target, previousMenu));
		}

		public static String nickname(OfflinePlayer player) {
			return Nickname.of(player);
		}
	}


}
