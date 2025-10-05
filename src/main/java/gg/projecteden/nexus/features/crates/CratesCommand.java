package gg.projecteden.nexus.features.crates;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.crates.api.models.CrateAnimation;
import gg.projecteden.crates.api.models.CrateAnimationsAPI;
import gg.projecteden.nexus.features.crates.menus.CrateEditMenu;
import gg.projecteden.nexus.features.crates.menus.CrateEditMenu.CrateEditProvider;
import gg.projecteden.nexus.features.crates.menus.CrateGroupsProvider;
import gg.projecteden.nexus.features.crates.menus.CratePreviewProvider;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.equipment.skins.EquipmentSkinType;
import gg.projecteden.nexus.features.equipment.skins.ToolSkin;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.profiles.ProfileCommand;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CrateOpeningException;
import gg.projecteden.nexus.models.crate.CrateConfig;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateConfigService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.profile.ProfileUser.ProfileTextureType;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.ToolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;

@Aliases("crate")
@NoArgsConstructor
public class CratesCommand extends CustomCommand implements Listener {

	public CratesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Teleport to the crates area")
	void warp() {
		runCommand("warp crates");
	}

	@Path("info")
	@Description("Learn about the server's crates")
	void info() {
		line();
		send("&3Hi there, I'm &eBlast.");
		line();
		send("&3These are our server's &eCrates&3. They can give you amazing rewards to help boost your survival experience.");
		send("&3To open a Crate, you must have a &eCrate Key&3. You can get these from &evoting&3, &eevents&3, &eand more&3!");
		line();
		send("&3To &epreview rewards&3, you can &eright-click with an empty hand &3to open a preview menu.");
		send("&3To &eopen multiple at a time&3, simply &eshift+click &3with multiple keys in your hand.");
		line();
		send("&3I hope you enjoy, and have a good day!");
	}

	@Path("toggle")
	@Permission(Group.ADMIN)
	@Description("Toggle all crates")
	void toggle() {
		CrateConfig config = CrateConfigService.get();
		config.setEnabled(!config.isEnabled());
		config.save();
		send(PREFIX + "Crates " + (config.isEnabled() ? "&aenabled" : "&cdisabled"));
	}

	@Path("give <type> [player] [amount]")
	@Permission(Group.ADMIN)
	@Description("Give a player a crate key")
	void key(CrateType type, @Arg("self") OfflinePlayer player, @Arg("1") Integer amount) {
		type.give(player, amount);
		if (player.isOnline())
			send(player.getPlayer(), Crates.PREFIX + "You have been given &e" + amount + " " + StringUtils.camelCase(type.name()) +
					" Crate Key" + (amount == 1 ? "" : "s"));
		if (!isSelf(player))
			send(Crates.PREFIX + "You gave &e" + amount + " " + StringUtils.camelCase(type.name()) + " Crate Key" +
					(amount == 1 ? "" : "s") + "  &3to &e" + Nickname.of(player));
	}

	@Path("reset <type> <uuid>")
	@Permission(Group.ADMIN)
	@Description("Reset a crate's animation")
	void reset(CrateType type, @Arg(context = 1) CrateEntity uuid) {
		Entity entity = Bukkit.getEntity(uuid.getUuid());
		if (entity == null)
			error("Invalid entity");
		CrateHandler.reset(entity);
		send(PREFIX + "Reset " + uuid.getUuid());
	}

	@Path("edit [filter]")
	@Permission(Group.ADMIN)
	@Description("Edit a crate")
	void edit(CrateType filter) {
		new CrateEditProvider(filter, null).open(player());
	}

	@Path("edit groups <type>")
	@Permission(Group.ADMIN)
	@Description("Edit a crate group")
	void groups(CrateType type) {
		new CrateGroupsProvider(type, null).open(player());
	}

	@Path("edit announcement reset <id>")
	@Permission(Group.ADMIN)
	@Description("Remove a loot's announcement")
	void resetAnnouncement(int id) {
		CrateLoot loot = CrateLoot.byId(id);
		if (loot == null)
			error("Unknown loot id");
		loot.setAnnouncement(null);
		loot.setShouldAnnounce(false);
		CrateConfigService.get().save();
		new CrateEditMenu.LootSettingsProvider(loot.getType(), loot).open(player());
	}

	@Path("edit announcement set <id> <message...>")
	@Permission(Group.ADMIN)
	@Description("Set a loot's announcement")
	void setAnnouncement(int id, String message) {
		CrateLoot loot = CrateLoot.byId(id);
		if (loot == null)
			error("Unknown loot id");
		loot.setAnnouncement(message);
		loot.setShouldAnnounce(true);
		CrateConfigService.get().save();
		new CrateEditMenu.LootSettingsProvider(loot.getType(), loot).open(player());
	}
	
	@Path("preview <type>")
	@Permission(Group.ADMIN)
	@Description("Preview a crate")
	void preview(CrateType type) {
		new CratePreviewProvider(type, null, null).open(player());
	}

	@Path("animate <type> <uuid>")
	@Permission(Group.ADMIN)
	@Description("Animate a crate")
	void animate(CrateType type, UUID uuid) {
		if (!(world().getEntity(uuid) instanceof ArmorStand))
			error("You must be looking at an armor stand");

		ArmorStand armorStand = (ArmorStand) world().getEntity(uuid);

		CrateAnimation animation;
		final @Nullable RegisteredServiceProvider<CrateAnimationsAPI> serviceProvider = Bukkit.getServicesManager().getRegistration(CrateAnimationsAPI.class);
		if (serviceProvider == null)
			throw new NullPointerException("CrateAnimationsAPI does not appear to be loaded");

		BiFunction<Location, Consumer<Item>, Item> func = (location, item) -> {
			try {
				Consumer<Item> itemConsumer = item2 -> {
					type.handleItem(item2);
					item.accept(item2);
					item2.customName(new JsonBuilder("&eStone").build());
				};
				return location.getWorld().dropItem(location, new ItemStack(Material.STONE), itemConsumer::accept);
			} catch (CrateOpeningException ex) {
				CrateHandler.reset(armorStand);
				if (ex.getMessage() != null)
					send(player(), Crates.PREFIX + ex.getMessage());
				return null;
			}
		};

		animation = serviceProvider.getProvider().getAnimation(type.name(), armorStand, func);

		if (animation == null)
			error("Could not create animation instance");

		try {
			CrateHandler.ANIMATIONS.put(uuid, animation);
			animation.play().thenRun(() -> CrateHandler.ANIMATIONS.remove(uuid));
		} catch (Throwable ex) {
			ex.printStackTrace();
			animation.stop();
			animation.reset();
		}
	}

	@Path("entities add <type> <uuid>")
	@Permission(Group.ADMIN)
	@Description("Register an entity with a crate")
	void entitiesAdd(CrateType type, UUID uuid) {
		if (uuid == null)
			error("Invalid UUID");
		CrateConfig config = CrateConfigService.get();
		if (!config.getCrateEntities().containsKey(type))
			config.getCrateEntities().put(type, new ArrayList<>());
		if (config.getCrateEntities().get(type).contains(uuid))
			error("That entity is already registered to that type");
		config.getCrateEntities().get(type).add(uuid);
		config.save();
		send(PREFIX + "Added " + uuid + " to " + camelCase(type));
	}

	@Path("entities remove <type> <uuid>")
	@Permission(Group.ADMIN)
	@Description("Unregister an entity with a crate")
	void entitiesRemove(CrateType type, @Arg(context = 1) CrateEntity uuid) {
		CrateConfig config = CrateConfigService.get();
		if (!config.getCrateEntities().containsKey(type) || config.getCrateEntities().get(type).isEmpty()) {
			config.getCrateEntities().put(type, new ArrayList<>());
			config.save();
			error("That type has no registered entities");
		}
		if (!config.getCrateEntities().get(type).contains(uuid.getUuid()))
			error("That entity is not registered to that type");
		config.getCrateEntities().get(type).remove(uuid.getUuid());
		config.save();
		send(PREFIX + "Removed " + uuid.getUuid() + " from " + camelCase(type));
	}

	@Path("entities list [type]")
	@Permission(Group.ADMIN)
	@Description("List a crate's registered entities")
	void entitiesList(CrateType type) {
		send(Crates.PREFIX + "Crate entities:");
		if (type == null)
			Arrays.stream(CrateType.values()).forEach(type2 -> {
				list(type2);
				line();
			});
		else
			list(type);
	}

	@Path("open <type> <uuid> [amount]")
	@Permission(Group.ADMIN)
	@Description("Open a crate")
	void open(CrateType type, @Arg(context = 1) CrateEntity uuid, @Arg("1") int amount) {
		CrateHandler.openCrate(type, (ArmorStand) Bukkit.getEntity(uuid.getUuid()), player(), amount, false);
	}

	@Path("pinata <player> <type> [amount]")
	@Permission(Group.ADMIN)
	@Description("Gives a player a crate pinata")
	void pinata(Player player, CrateType type, @Arg("1") int amount) {
		CratePinatas.give(player, type, amount);
	}

	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	@Path("rewardpreview")
	void rewardPreview() {
		new RewardPreviewMenu().open(player());
	}

	@Rows(5)
	public static class RewardPreviewMenu extends InventoryProvider {
		static final int NPC_ID = 5618;
		private Step step = Step.CHOOSE_TYPE;
		private SkinType type;
		private EquipmentSkinType skin;

		@Override
		public String getTitle() {
			return StringUtils.camelCase(step);
		}

		@Override
		public void init() {
			if (step == null)
				step = Step.CHOOSE_TYPE;

			if (step == Step.CHOOSE_TYPE)
				addCloseItem();
			else
				addBackItem(e -> {
					step = step.previous();
					refresh();
				});

			List<ClickableItem> items = new ArrayList<>();

			if (step == Step.CHOOSE_TYPE) {
				contents.set(SlotPos.of(2, 2), ClickableItem.of(new ItemBuilder(Material.DIAMOND_CHESTPLATE).name("Armor"), e -> {
					type = SkinType.ARMOR;
					step = step.next();
					refresh();
				}));
				contents.set(SlotPos.of(2, 4), ClickableItem.of(new ItemBuilder(Material.NETHERITE_PICKAXE).name("Tools"), e -> {
					type = SkinType.TOOL;
					step = step.next();
					refresh();
				}));
				contents.set(SlotPos.of(2, 6), ClickableItem.of(new ItemBuilder(ItemModelType.GUI_PROFILE_TEXTURE_ITEM_SHINE).name("Profile Textures"), e -> {
					type = SkinType.PROFILE;
					step = step.next();
					refresh();
				}));
			} else if (step == Step.CHOOSE_SKIN) {
				if (this.type == SkinType.PROFILE) {
					for (ProfileTextureType textureType : ProfileTextureType.getValues()) {
						items.add(ClickableItem.of(textureType.getPreviewItem(), e -> {
							close();
							ProfileCommand.openProfile(viewer, viewer, textureType, this);
						}));
					}
				} else {
					for (Object obj : this.type.getValues()) {
						if (obj instanceof EquipmentSkinType skinType) {
							items.add(ClickableItem.of(skinType.getTemplate(), e -> {
								skin = skinType;
								if (type == SkinType.ARMOR) {
									setArmor((ArmorSkin) skinType);
									close();
								} else {
									step = step.next();
									refresh();
								}
							}));
						}
					}
				}
			} else if (step == Step.CHOOSE_TOOL) {
				if (type == SkinType.TOOL)
					for (ToolType toolType : ToolSkin.APPLICABLE_TYPES) {
						Material tool = toolType.getTools().getLast();
						items.add(ClickableItem.of(tool, e -> {
							type = SkinType.TOOL;
							setTool(skin, tool);
							close();
						}));
					}
			}

			if (!items.isEmpty())
				paginate(items);
		}

		private static final Map<EquipmentSlot, Material> ARMOR_SLOTS = Map.of(
			EquipmentSlot.HELMET, Material.NETHERITE_HELMET,
			EquipmentSlot.CHESTPLATE, Material.NETHERITE_CHESTPLATE,
			EquipmentSlot.LEGGINGS, Material.NETHERITE_LEGGINGS,
			EquipmentSlot.BOOTS, Material.NETHERITE_BOOTS
		);

		private void setArmor(ArmorSkin skin) {
			var equipment = CitizensUtils.getNPC(NPC_ID).getOrAddTrait(Equipment.class);
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (!slot.toBukkit().isArmor())
					continue;

				if (!ARMOR_SLOTS.containsKey(slot))
					continue;

				var item = ItemStack.of(ARMOR_SLOTS.get(slot));
				if (slot == EquipmentSlot.HELMET) {
					var helmetCostume = skin.getHelmetCostume();
					if (isNotNullOrEmpty(helmetCostume))
						item = new ItemBuilder(Material.PAPER).model("costumes/" + helmetCostume).build();
				}

				equipment.set(slot, skin.apply(item));
			}
		}

		private void setTool(EquipmentSkinType skin, Material tool) {
			var equipment = CitizensUtils.getNPC(NPC_ID).getOrAddTrait(Equipment.class);
			equipment.set(EquipmentSlot.HAND, skin.apply(new ItemStack(tool)));
		}

		private enum Step implements IterableEnum {
			CHOOSE_TYPE,
			CHOOSE_SKIN,
			CHOOSE_TOOL,
		}

		@Getter
		@AllArgsConstructor
		private enum SkinType {
			ARMOR {
				@Override
				public List<EquipmentSkinType> getValues() {
					return ArmorSkin.getTemplateable();
				}
			},
			TOOL {
				@Override
				public List<EquipmentSkinType> getValues() {
					return EnumUtils.valuesExcept(ToolSkin.class, ToolSkin.DEFAULT);
				}
			},
			PROFILE {
				@Override
				List<EquipmentSkinType> getValues() {
					return List.of();
				}
			}
			;

			abstract List<EquipmentSkinType> getValues();
		}

	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(NPCRightClickEvent event) {
		if (event.getNPC().getId() != RewardPreviewMenu.NPC_ID)
			return;

		event.setCancelled(true);
		new RewardPreviewMenu().open(event.getClicker());
	}

	public void list(CrateType type) {
		send("&3" + camelCase(type) + ":");
		if (!CrateConfigService.get().getCrateEntities().containsKey(type) || CrateConfigService.get().getCrateEntities().get(type).isEmpty())
			send("&cNo entities for this type");
		else {
			for (UUID uuid : CrateConfigService.get().getCrateEntities().get(type)) {
				Entity entity = Bukkit.getEntity(uuid);
				if (entity == null)
					continue;
				Location loc = entity.getLocation();

				String tpCommand = String.format("/tppos %s %s %s %s", loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
				send(json("&e" + camelCase(entity.getType())).hover("&eClick to teleport").command(tpCommand).group()
					.next(" &7[Copy UUID]").hover("&eShift+Click to copy").copy(entity.getUniqueId().toString()));
			}
		}
	}

	@Data
	public static class CrateEntity {
		private final UUID uuid;
	}

	@ConverterFor(CrateEntity.class)
	CrateEntity convertToCrateEntity(String value) {
		return new CrateEntity(UUID.fromString(value));
	}

	@TabCompleterFor(CrateEntity.class)
	List<String> tabCompleteCrateEntity(String filter, CrateType context) {
		return CrateConfigService.get().getCrateEntities().getOrDefault(context, new ArrayList<>()).stream()
			.map(UUID::toString)
			.filter(str -> argsString().startsWith(filter))
			.collect(Collectors.toList());
		}

}
