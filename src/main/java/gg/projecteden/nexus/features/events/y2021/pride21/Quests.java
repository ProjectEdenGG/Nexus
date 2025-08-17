package gg.projecteden.nexus.features.events.y2021.pride21;

import com.sk89q.worldedit.math.BlockVector3;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.DyeBombCommand;
import gg.projecteden.nexus.features.events.models.Talker;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pride21.Pride21User;
import gg.projecteden.nexus.models.pride21.Pride21UserService;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.utils.DescParseTickFormat;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Quests implements Listener {
	public Quests() {
		Nexus.registerListener(this);
	}

	private static final Pride21UserService service = new Pride21UserService();
	// Pride Related Facts, for display after
	private static final List<String> FACTS = List.of(
			"In 1918, poets and writers Elsa Gidlow and Roswell George Mills launched \"Les Mouches fantastiques\" in Montreal. It is regarded as the first LGBTQ+ publication in Canada, and North America.",
			"In 1967, the Oscar Wilde memorial bookshop opened in New York City. It was the first gay bookshop in the world.",
			"It is believed that the term 'lesbian' comes from the Greek island Lesbos. Sappho, a Greek poetess known for her poetry about the beauty of other women, and her love for them, was from the island.",
			"Amsterdam celebrates pride a little differently, their floats actually 'float'! The pride celebration takes place on 100 decorated boats that sail through the city on Prinsengracht and Amstel Rivers.",
			"The original pride flag featured 8 colors, each with a distinct meaning assigned. Hot pink (Sex), Red (Life), Orange (Healing), Yellow (Sunlight), Green (Nature), Turquoise (Magic and Art), Indigo (Serenity), and Violet (Spirit).",
			"Gilbert Baker was an American artist, gay rights activist, and the original designer of the rainbow flag (1978). This flag design was originally changed due to the fact that Hot Pink wasn't available, because of this they dropped two of the colours (Hot pink and Turquoise) so there was an even number of colours to line the streets with for the Pride Parade."
	);
	private static final Talker.TalkingNPC PARADE_MANAGER = new ParadeManager();

	@EventHandler
	public void onClickDecoration(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getClickedBlock() == null) return;
		Decorations decoration = Decorations.getByLocation(event.getClickedBlock());
		if (decoration == null) return;
		Pride21User user = service.get(event.getPlayer());
		JsonBuilder json = JsonBuilder.fromPrefix("Pride");
		if (!Pride21.QUESTS_ENABLED()) {
			user.sendMessage(json.next("You've found a bag of rainbow decorations from the &e2021 Pride event&3!"));
			return;
		}

		boolean sendFact = !user.getDecorationsCollected().contains(decoration);
		if (!sendFact) {
			if (user.isComplete())
				json.next("You've already found all the decorations! You should take them back to the &eParade Manager&3.");
			else
				addLeftToFind(json.next("You've already found this decoration!"), user);
		} else {
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(event).pitch(2F).play();
			user.getDecorationsCollected().add(decoration);
			service.save(user);
//			int tokens = 5;
//			if (user.decorationsFound() == 3 || user.isComplete())
//				tokens += 10;
//			EventUserService eventService = new EventUserService();
//			EventUser eventUser = eventService.get(user);
//			eventUser.giveTokens(tokens);
//			eventService.save(eventUser);
			if (user.isComplete())
				json.next("You've found the last bag! You should take them back to the &eParade Manager&3.");
			else
				addLeftToFind(json.next("You've found a bag of decorations!"), user);
		}
		user.sendMessage(json);
		user.sendMessage(new JsonBuilder(NamedTextColor.DARK_AQUA).next("&8&l[").next(StringUtils.Rainbow.apply("Fun Fact")).next("&8&l]").next(" " + FACTS.get(decoration.ordinal())));
	}

	private static @NotNull JsonBuilder addLeftToFind(JsonBuilder json, Pride21User user) {
		int decorationsLeft = user.decorationsLeft();
		return json.next(" You have").next(StringUtils.plural("&e " + decorationsLeft + " bag", decorationsLeft) + "&3 left to find.");
	}

	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		Player player = event.getClicker();
		if (event.getNPC().getId() != PARADE_MANAGER.getNpcId()) return;

		CooldownService cooldownService = new CooldownService();
		if (CooldownService.isOnCooldown(player, "Pride21_NPCInteract", TimeUtils.TickTime.SECOND.x(5)))
			return;

		int waitTicks = Talker.sendScript(player, PARADE_MANAGER);
		Pride21User user = service.get(player);
		if (user.isComplete()) {
			Tasks.wait(waitTicks, () -> player.teleportAsync(player.getLocation()));
			Tasks.waitAsync(waitTicks, () -> {
				player.resetPlayerTime();
				viewFloat(player, true);
				if (false && !user.isBonusTokenRewardClaimed()) {
					TrophyType.PRIDE_2021.give(player);

					user.setBonusTokenRewardClaimed(true);
					service.save(user);
					new EventUserService().edit(user, eventUser -> eventUser.giveTokens(50));
					ItemStack dyeBomb = DyeBombCommand.getDyeBomb();
					dyeBomb.setAmount(16);
					PlayerUtils.giveItemAndMailExcess(player, dyeBomb, "Pride 2021 Reward", WorldGroup.SURVIVAL);
				}
			});
		}
	}

	public static void viewFloat(Player player, boolean view) {
		Tasks.async(() -> {
			World world = Bukkit.getWorld("events");
			BlockData gray = Material.GRAY_TERRACOTTA.createBlockData();
			if (world == null) return;
			for (BlockVector3 blockVector3 : new WorldGuardUtils(world).getRegion("pride21_val")) {
				Location location = new Location(world, blockVector3.x(), blockVector3.y(), blockVector3.z());
				Material material = location.getBlock().getType();
				if (MaterialTag.ALL_TERRACOTTAS.isTagged(material))
					player.sendBlockChange(location, view ? location.getBlock().getBlockData() : gray);
			}
		});
	}

	@EventHandler
	public void onTeleportEvent(PlayerTeleportEvent event) {
		boolean fromPride = Pride21.isInRegion(event.getFrom());
		boolean toPride = Pride21.isInRegion(event.getTo());
		Player player = event.getPlayer();
		if ((fromPride && !toPride) || (toPride && service.get(player).isBonusTokenRewardClaimed()))
			event.getPlayer().resetPlayerTime();
		else if (toPride) {
			Tasks.wait(TimeUtils.TickTime.SECOND, () -> event.getPlayer().setPlayerTime(DescParseTickFormat.parseAlias("dawn"), false));
			viewFloat(player, false);
		}
	}
}
