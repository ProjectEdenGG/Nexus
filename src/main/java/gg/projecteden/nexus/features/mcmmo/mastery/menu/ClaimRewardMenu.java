package gg.projecteden.nexus.features.mcmmo.mastery.menu;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import gg.projecteden.nexus.features.crates.gemcrafter.GemCommand;
import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem;
import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem.TomeLevel;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestige;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestige.CustomEnchantReward;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestige.McMMOResetReward;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestige.PermissionReward;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestige.TomeReward;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestigeListener;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@Rows(4)
public class ClaimRewardMenu extends InventoryProvider {

	private static final McMMOPrestigeUserService SERVICE = new McMMOPrestigeUserService();

	private static final int[] ITEM_SLOTS_NO_ENCHANTS = { 21, 22, 23, 30, 31, 32 };
	private static final int[] ITEM_SLOTS_ENCHANTS = { 19, 20, 21, 28, 29, 30 };
	private static final int[] ENCHANT_SLOTS = { 23, 24, 25, 32, 33, 34 };
	private static final int[] MAIN_SLOTS = { 9, 10, 16, 17, 18, 19, 20, 24, 25, 26, 28, 29, 30, 31, 32, 33, 34 };

	private static final Map<PrimarySkillType, Material> SKILL_TYPE_MATERIAL_MAP = Map.ofEntries(
		Map.entry(PrimarySkillType.ACROBATICS, Material.DIAMOND_BOOTS),
		Map.entry(PrimarySkillType.ALCHEMY, Material.BREWING_STAND),
		Map.entry(PrimarySkillType.ARCHERY, Material.BOW),
		Map.entry(PrimarySkillType.AXES, Material.DIAMOND_AXE),
		Map.entry(PrimarySkillType.CROSSBOWS, Material.CROSSBOW),
		Map.entry(PrimarySkillType.EXCAVATION, Material.DIAMOND_SHOVEL),
		Map.entry(PrimarySkillType.FISHING, Material.FISHING_ROD),
		Map.entry(PrimarySkillType.HERBALISM, Material.HAY_BLOCK),
		Map.entry(PrimarySkillType.MACES, Material.MACE),
		Map.entry(PrimarySkillType.MINING, Material.DIAMOND_PICKAXE),
		Map.entry(PrimarySkillType.REPAIR, Material.ANVIL),
		Map.entry(PrimarySkillType.SPEARS, Material.DIAMOND_SPEAR),
		Map.entry(PrimarySkillType.SWORDS, Material.DIAMOND_SWORD),
		Map.entry(PrimarySkillType.TAMING, Material.BONE),
		Map.entry(PrimarySkillType.TRIDENTS, Material.TRIDENT),
		Map.entry(PrimarySkillType.UNARMED, Material.STICK),
		Map.entry(PrimarySkillType.WOODCUTTING, Material.OAK_LOG)
	);

	private final PrimarySkillType skill;
	private final boolean admin;
	private final boolean containsEnchants, enchantsOnly;
	private static final int lineLength = 158;

	public ClaimRewardMenu(PrimarySkillType primarySkillType, boolean admin) {
		this.skill = primarySkillType;
		this.admin = admin;

		this.containsEnchants = McMMOPrestige.get().getRewards().stream().filter(reward -> reward.getSkill() == this.skill)
			.anyMatch(reward -> reward instanceof TomeReward || reward instanceof CustomEnchantReward);
		this.enchantsOnly = McMMOPrestige.get().getRewards().stream().filter(reward -> reward.getSkill() == this.skill)
			.allMatch(reward -> reward instanceof TomeReward || reward instanceof CustomEnchantReward);
	}

	@Override
	public ComponentLike getTitleComponent() {
		if (skill == null)
			return new JsonBuilder(InventoryTexture.GUI_MCMMO_PRESTIGE.getMenuTexture())
				.next(MenuText.centered("MASTERY", NamedTextColor.WHITE, 88))
				.next(MenuText.space(30))
				.next(Component.text("\uE100\uE014".repeat(lineLength), NamedTextColor.WHITE).font(NamespacedKey.minecraft("mcmmo/line"))).build();

		JsonBuilder builder = new JsonBuilder((containsEnchants && !enchantsOnly)?
				InventoryTexture.GUI_MCMMO_ITEMS_WITH_ENCHANTS.getMenuTexture() :
				InventoryTexture.GUI_MCMMO_ITEMS_NO_ENCHANTS.getMenuTexture())
			.next(MenuText.centered(skill.name(), NamedTextColor.WHITE, 88));

		Component GEMS = Component.text("GEMS").font(NamespacedKey.minecraft("mcmmo/label"));
		Component PERKS = Component.text("PERKS").font(NamespacedKey.minecraft("mcmmo/label"));

		if (enchantsOnly)
			builder.next(MenuText.space(1075)).next(GEMS)
				.next(MenuText.space(-1515));
		else if (!containsEnchants)
			builder.next(MenuText.space(1025)).next(PERKS)
				.next(MenuText.space(-1570));
		else
			builder.next(MenuText.space(425)).next(PERKS)
				.next(MenuText.space(650)).next(GEMS)
				.next(MenuText.space(-2090));

		McMMOPrestigeUser user = SERVICE.get(getViewer());
		if (user.getPrestigeExperience().getOrDefault(skill, 0d) > McMMOPrestigeListener.XP_TO_MASTERY_REWARD)
			builder.next(Component.text("\uE100\uE014".repeat(lineLength)).color(TextColor.fromHexString("#2db8cb")).font(NamespacedKey.minecraft("mcmmo/line")));
		else {
			double ratio = user.getPrestigeExperience().getOrDefault(skill, 0d) / McMMOPrestigeListener.XP_TO_MASTERY_REWARD;
			int filledPixels = (int) Math.floor(lineLength * ratio);

			builder.next(Component.text("\uE100\uE014".repeat(filledPixels)).color(TextColor.fromHexString("#0eef37")).font(NamespacedKey.minecraft("mcmmo/line")));
			builder.next(Component.text("\uE100\uE014".repeat(lineLength - filledPixels)).color(NamedTextColor.WHITE).font(NamespacedKey.minecraft("mcmmo/line")));
		}

		return builder.build();
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public void init() {
		McMMOPrestigeUser user = SERVICE.get(getViewer());

		Double xp = user.getPrestigeExperience().getOrDefault(skill, 0d);
		if (skill == null) {
			int i = 0;
			for (PrimarySkillType type : PrimarySkillType.values()) {
				if (type.isChildSkill()) continue;
				ItemBuilder builder = new ItemBuilder(SKILL_TYPE_MATERIAL_MAP.get(type)).name("&e" + StringUtils.camelCase(type));
				if (user.getPrestigeExperience().getOrDefault(type, 0d) >= McMMOPrestigeListener.XP_TO_MASTERY_REWARD)
					builder.glow().lore("&aReward Available!");
				contents.set(MAIN_SLOTS[i++], ClickableItem.of(builder, e -> new ClaimRewardMenu(type, admin).open(e.getPlayer())));
			}
			return;
		}

		List<McMMOResetReward> resetRewards = McMMOPrestige.get().getRewards().stream().filter(reward -> reward.getSkill() == this.skill).toList();

		int[] slots = containsEnchants ? ITEM_SLOTS_ENCHANTS : ITEM_SLOTS_NO_ENCHANTS;

		int i = 0;
		for (McMMOResetReward reward : resetRewards.stream().filter(reward -> !(reward instanceof TomeReward || reward instanceof CustomEnchantReward)).toList()) {
			contents.set(slots[i], getItem(reward));
			i++;
		}
		i = 0;
		slots = enchantsOnly ? ITEM_SLOTS_NO_ENCHANTS : ENCHANT_SLOTS;

		for (McMMOResetReward reward : resetRewards.stream().filter(reward -> reward instanceof TomeReward || reward instanceof CustomEnchantReward).toList()) {
			contents.set(slots[i], getItem(reward));
			i++;
		}

		addBackItem(1, 0, e -> new ClaimRewardMenu(null, admin).open(e.getPlayer()));

		ItemBuilder builder = new ItemBuilder(ItemModelType.INVISIBLE);
		if (xp >= McMMOPrestigeListener.XP_TO_MASTERY_REWARD)
			builder.name("&eRewards Available: &3" + (int) (xp / McMMOPrestigeListener.XP_TO_MASTERY_REWARD))
				.lore("&3Mastery XP to next reward:");
		else
			builder.name("&3Mastery XP to next reward:");
		builder.lore("&e%s&3/&e%s &3(&e%s%%&3)".formatted(
			(int) (xp % McMMOPrestigeListener.XP_TO_MASTERY_REWARD),
			(int) McMMOPrestigeListener.XP_TO_MASTERY_REWARD,
			(int) (((xp % McMMOPrestigeListener.XP_TO_MASTERY_REWARD) / (double) McMMOPrestigeListener.XP_TO_MASTERY_REWARD) * 100)));

		for (int j = 0; j < 9; j++)
			contents.set(j, ClickableItem.empty(builder.build()));
	}

	private ClickableItem getItem(McMMOResetReward reward) {
		ItemStack item;
		String name = null;
		if (reward instanceof TomeReward tomeReward)
			item = new TomeItem(tomeReward.getTome(), TomeLevel.MASTER).toItemStack();
		else if (reward instanceof CustomEnchantReward customEnchantReward)
			item = GemCommand.makeGem(customEnchantReward.getEnchant());
		else {
			item = reward.getIcon() != null ? reward.getIcon() : reward.getItem();
			name = reward.getName();
		}

		ItemBuilder builder = new ItemBuilder(item);
		if (name != null)
			builder.name("&e" + name);
		if (reward.getDescription() != null)
			builder.resetLore().lore("&3" + reward.getDescription());
		if (reward instanceof CustomEnchantReward customEnchantReward && customEnchantReward.getEnchant() instanceof CustomEnchant customEnchant)
			builder.resetLore().lore("&3" + customEnchant.description());


		if (reward instanceof PermissionReward permissionReward && getViewer().hasPermission(permissionReward.getPermission()))
			builder.lore("&cYou already have this reward!");

		return ClickableItem.of(builder.build(), e -> giveReward(reward, e.getPlayer()));
	}

	private void giveReward(McMMOResetReward reward, Player player) {
		if (!admin && SERVICE.get(player).getPrestigeExperience().getOrDefault(skill, 0d) < McMMOPrestigeListener.XP_TO_MASTERY_REWARD) return;
		if (!reward.give(player, admin) || admin) return;

		SERVICE.edit(player, user -> user.claimReward(skill, reward));
	}
}
