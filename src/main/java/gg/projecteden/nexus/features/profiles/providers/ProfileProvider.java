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
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;
import static gg.projecteden.nexus.features.profiles.providers.ProfileProvider.ProfileMenuItem.nickname;

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
	public String getTitle() {
		String whose = PlayerUtils.isSelf(player, target) ? "Your" : nickname(target) + "'s";
		return "&3" + whose + " Profile &c(WIP)";
	}

	@Override
	public void init() {
		addBackOrCloseItem(this);

		// TODO: do rank using magic spaces, and text

		for (ProfileMenuItem menuItem : ProfileMenuItem.values()) {
			menuItem.setClickableItem(player, target, contents);
			menuItem.setExtraClickableItems(player, target, contents);
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
		ClickableItem clickableItem = ProfileMenuItem.VIEW_SOCIAL_MEDIA.getClickableItem(player, target, index);
		contents.set(slotPos, clickableItem);

		index++;
		if (index > maxIndex)
			index = 0;
	}

	@Getter
	@AllArgsConstructor
	enum ProfileMenuItem {
		PLAYER(2, 2, Material.PLAYER_HEAD, 2) {
			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Nerd.of(target).getNickname();
			}

			@Override
			public ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target) {
				return super.getItemBuilder(viewer, target).skullOwner(target);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return List.of(
					SlotPos.of(1, 1), SlotPos.of(1, 2), SlotPos.of(1, 3),
					SlotPos.of(2, 1), /* 						*/ SlotPos.of(2, 3),
					SlotPos.of(3, 1), SlotPos.of(3, 2), SlotPos.of(3, 3)
				);
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getItemBuilder(viewer, target).material(Material.PAPER).modelId(8500);
			}
		},

		PRESENCE(1, 7, Material.PAPER, 25000) {
			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				return Presence.of(target, viewer).getModelId();
			}

			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Presence.of(target).getName();
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
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
		},

		LEVELS(3, 4, Material.EXPERIENCE_BOTTLE, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon); // TODO
			}
		},

		WALLET(3, 5, Material.PAPER, 1510) {
			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&3Wallet:";
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return BankCommand.getLines(Nerd.of(viewer), Nerd.of(target));
			}
		},

		VIEW_SOCIAL_MEDIA(3, 7, Material.PAPER, 901) {
			@Override
			public int getModelId(Player viewer, OfflinePlayer target, int index) {
				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> sites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				if (sites.size() == 0)
					return -1;

				return sites.get(index).getModelId();
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				SocialMediaCommand.open(viewer, target, "/profile " + nickname(target));
			}
		},

		ADD_FRIEND(4, 1, Material.STONE_BUTTON, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (PlayerUtils.isSelf(viewer, target))
					return false;

				return !isFriendsWith(friendService.get(target), viewer);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon); // TODO
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target); // TODO
			}
		},

		REMOVE_FRIEND(4, 1, Material.STONE_BUTTON, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (PlayerUtils.isSelf(viewer, target))
					return false;

				return isFriendsWith(friendService.get(target), viewer);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of("Click to remove " + nickname(target));
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target); // TODO
			}
		},

		VIEW_FRIENDS(4, 3, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon); // TODO
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target); // TODO
			}
		},

		VIEW_HOMES(4, 4, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon); // TODO
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target); // TODO
			}
		},

		VIEW_SHOP(4, 5, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon); // TODO
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target); // TODO
			}
		},

		EDIT_TRUSTS(4, 6, Material.STONE_BUTTON, 0) {
			// TODO: dont show for self
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon); // TODO
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target); // TODO
			}
		},

		TELEPORT(4, 7, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon); // TODO
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target); // TODO
			}
		},
		;

		private final int row, col;
		private final Material material;
		private final int modelId;

		private static final String soon = "&3Coming Soon!";

		public boolean shouldShow(Player viewer, OfflinePlayer target) {
			return true;
		}

		public void onClick(Player viewer, OfflinePlayer target) {}

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

		public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
			return getItemBuilder(viewer, target).clone();
		}


		private static boolean isFriendsWith(FriendsUser friend, Player viewer) {
			return friend.isFriendsWith(friendService.get(viewer));
		}

		public void setClickableItem(Player player, OfflinePlayer target, InventoryContents contents) {
			ClickableItem clickableItem = getClickableItem(player, target);
			if (clickableItem == null)
				return;

			contents.set(getSlotPos(), clickableItem);
		}

		public void setExtraClickableItems(Player viewer, OfflinePlayer target, InventoryContents contents) {
			for (SlotPos slotPos : getExtraSlots()) {
				ClickableItem clickableItem = getClickableItem(viewer, target, getExtraSlotItemBuilder(viewer, target));
				if (clickableItem == null)
					continue;

				contents.set(slotPos, clickableItem);
			}
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target));
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, int index) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target, index));
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, ItemBuilder itemBuilder) {
			if (Nullables.isNullOrAir(itemBuilder) || !shouldShow(viewer, target))
				return null;

			return ClickableItem.of(itemBuilder, e -> onClick(viewer, target));
		}

		public static String nickname(OfflinePlayer player) {
			return Nickname.of(player);
		}
	}


}
