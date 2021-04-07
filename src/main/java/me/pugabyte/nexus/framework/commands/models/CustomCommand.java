package me.pugabyte.nexus.framework.commands.models;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Fallback;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.commands.models.events.TabEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeCommandBlockException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeConsoleException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static me.pugabyte.nexus.utils.BlockUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.parseDate;
import static me.pugabyte.nexus.utils.StringUtils.parseDateTime;
import static me.pugabyte.nexus.utils.StringUtils.trimFirst;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess", "UnusedReturnValue"})
public abstract class CustomCommand extends ICustomCommand {
	@NonNull
	@Getter
	protected CommandEvent event;
	public String PREFIX = StringUtils.getPrefix(getName());
	public String DISCORD_PREFIX = StringUtils.getDiscordPrefix(getName());

	public String getPrefix() {
		return PREFIX;
	}

	public String getAliasUsed() {
		return event.getAliasUsed();
	}

	public void _shutdown() {}

	protected boolean isCommandEvent() {
		return !(event instanceof TabEvent);
	}

	protected String camelCase(Enum<?> _enum) {
		if (_enum == null) return null;
		return camelCase(_enum.name());
	}

	protected String camelCase(String string) {
		return StringUtils.camelCase(string);
	}

	protected String plural(String string, Number number) {
		return StringUtils.plural(string, number);
	}

	protected ItemStack getTool() {
		return ItemUtils.getTool(player());
	}

	protected ItemStack getToolRequired() {
		return ItemUtils.getToolRequired(player());
	}

	protected EquipmentSlot getHandWithTool() {
		return ItemUtils.getHandWithTool(player());
	}

	protected EquipmentSlot getHandWithToolRequired() {
		return ItemUtils.getHandWithToolRequired(player());
	}

	protected Block getTargetBlock() {
		return player().getTargetBlockExact(500);
	}

	protected Block getTargetBlockRequired() {
		Block targetBlock = getTargetBlock();
		if (isNullOrAir(targetBlock))
			error("You must be looking at a block");
		return targetBlock;
	}

	protected Sign getTargetSignRequired() {
		Block targetBlock = getTargetBlock();
		Material material = targetBlock.getType();
		if (ItemUtils.isNullOrAir(material) || !MaterialTag.SIGNS.isTagged(material))
			error("You must be looking at a sign");
		return (Sign) targetBlock.getState();
	}

	protected Entity getTargetEntity() {
		return player().getTargetEntity(120);
	}

	protected Entity getTargetEntityRequired() {
		Entity targetEntity = getTargetEntity();
		if (targetEntity == null)
			error("You must be looking at an entity");
		return targetEntity;
	}

	protected LivingEntity getTargetLivingEntityRequired() {
		Entity targetEntity = getTargetEntity();
		if (!(targetEntity instanceof LivingEntity))
			error("You must be looking at a living entity");
		return (LivingEntity) targetEntity;
	}

	protected Player getTargetPlayerRequired() {
		Entity targetEntity = getTargetEntity();
		if (!(targetEntity instanceof Player))
			error("You must be looking at a player");
		return (Player) targetEntity;
	}

	protected Location location() {
		if (isCommandBlock())
			return commandBlock().getBlock().getLocation();
		return player().getLocation();
	}

	protected World world() {
		return location().getWorld();
	}

	protected WorldGroup worldGroup() {
		return WorldGroup.get(location());
	}

	protected PlayerInventory inventory() {
		return player().getInventory();
	}

	public void giveItem(ItemStack item) {
		PlayerUtils.giveItems(player(), Collections.singletonList(item));
	}
	public void giveItems(Collection<ItemStack> items) {
		PlayerUtils.giveItems(player(), items, null);
	}

	protected void send(CommandSender sender, String message) {
		send(sender, json(message));
	}

	protected void send(CommandSender sender, BaseComponent... baseComponents) {
		sender.sendMessage(baseComponents);
	}

	protected void send(String uuid, String message) {
		send(UUID.fromString(uuid), message);
	}

	protected void send(UUID uuid, String message) {
		OfflinePlayer player = PlayerUtils.getPlayer(uuid.toString());
		if (player != null && player.isOnline())
			send((CommandSender) PlayerUtils.getPlayer(uuid), message);
	}

	protected void send(Object object) {
		if (object instanceof String)
			send(sender(), (String) object);
		else if (object instanceof JsonBuilder)
			send(sender(), (JsonBuilder) object);
		else if (object instanceof Component)
			send(sender(), (Component) object);
		else
			throw new InvalidInputException("Cannot send object: " + object.getClass().getSimpleName());
	}

	protected void send(CommandSender sender, int delay, String message) {
		Tasks.wait(delay, () -> send(sender, message));
	}

	protected void send() {
		send("");
	}

	protected void send(String message) {
		send(json(message));
	}

	protected void send(JsonBuilder builder) {
		send(sender(), builder);
	}

	protected void send(Component component) {
		send(sender(), component);
	}

	protected void send(BaseComponent... baseComponents) {
		send(sender(), baseComponents);
	}

	protected void send(CommandSender sender, JsonBuilder builder) {
		builder.send(sender);
	}

	protected void send(CommandSender sender, Component component) {
		sender.sendMessage(component);
	}

	protected void send(int delay, String message) {
		Tasks.wait(delay, () -> event.reply(message));
	}

	protected void line() {
		line(1);
	}

	protected void line(CommandSender player) {
		line(player, 1);
	}

	protected void line(int count) {
		line(sender(), count);
	}

	protected void line(CommandSender player, int count) {
		for (int i = 0; i < count; i++)
			send(player, "");
	}

	protected JsonBuilder json() {
		return json("");
	}

	protected JsonBuilder json(String message) {
		return new JsonBuilder(message);
	}

	protected String toPrettyString(Object object) {
		return StringUtils.toPrettyString(object);
	}

	@Contract("_ -> fail")
	public void error(String error) {
		throw new InvalidInputException(error);
	}

	@Contract("_ -> fail")
	public void error(JsonBuilder error) {
		throw new InvalidInputException(error);
	}

	@Deprecated
	public void error(Player player, String error) {
		player.sendMessage(StringUtils.colorize("&c" + error));
	}

	public void rethrow(Throwable ex) {
		throw new RuntimeException(ex);
	}

	public void permissionError() {
		throw new NoPermissionException();
	}

	public boolean hasPermission(String permission) {
		if (isCommandBlock() || isConsole())
			return true;
		else
			return player().hasPermission(permission);
	}

	public void showUsage() {
		throw new InvalidInputException(event.getUsageMessage());
	}

	protected CommandSender sender() {
		return event.getSender();
	}

	protected Player player() {
		if (!isPlayer())
			throw new MustBeIngameException();

		return (Player) event.getSender();
	}

	protected OfflinePlayer offlinePlayer() {
		if (!isPlayer())
			throw new MustBeIngameException();

		return Bukkit.getOfflinePlayer(uuid());
	}

	protected Nerd nerd() {
		return Nerd.of(player());
	}

	protected UUID uuid() {
		return player().getUniqueId();
	}

	protected String name() {
		if (!isPlayer())
			return sender().getName();
		else
			return nerd().getName();
	}

	protected String nickname() {
		if (!isPlayer())
			return sender().getName();
		else
			return nerd().getNickname();
	}

	protected String nickname(OfflinePlayer player) {
		return Nerd.of(player).getNickname();
	}

	protected String nickname(PlayerOwnedObject player) {
		return Nerd.of(player).getNickname();
	}

	protected ConsoleCommandSender console() {
		if (!isConsole())
			throw new MustBeConsoleException();

		return (ConsoleCommandSender) event.getSender();
	}

	protected BlockCommandSender commandBlock() {
		if (!isCommandBlock())
			throw new MustBeCommandBlockException();

		return (BlockCommandSender) event.getSender();
	}

	protected boolean isPlayer() {
		return isPlayer(sender());
	}

	private boolean isPlayer(Object object) {
		return object instanceof Player;
	}

	protected boolean isOfflinePlayer() {
		return isOfflinePlayer(sender());
	}

	private boolean isOfflinePlayer(Object object) {
		return object instanceof OfflinePlayer;
	}

	protected boolean isConsole() {
		return isConsole(sender());
	}

	private boolean isConsole(Object object) {
		return object instanceof ConsoleCommandSender;
	}

	protected boolean isCommandBlock() {
		return isCommandBlock(sender());
	}

	private boolean isCommandBlock(Object object) {
		return object instanceof BlockCommandSender;
	}

	protected boolean isSelf(PlayerOwnedObject object) {
		return isSelf(object.getOfflinePlayer());
	}

	protected boolean isSelf(OfflinePlayer player) {
		return isSelf(player(), player);
	}

	protected boolean isSelf(Player player) {
		return isSelf(player(), player);
	}

	protected boolean isSelf(OfflinePlayer self, OfflinePlayer player) {
		if (!isPlayer())
			return false;
		return self.getUniqueId().equals(player.getUniqueId());
	}

	protected boolean isStaff() {
		return !isPlayer() || isStaff(player());
	}

	protected boolean isStaff(Player player) {
		return isPlayer(player) && Nerd.of(player).getRank().isStaff();
	}

	protected boolean isStaff(OfflinePlayer player) {
		return isOfflinePlayer(player) && Nerd.of(player).getRank().isStaff();
	}

	protected boolean isSeniorStaff() {
		return !isPlayer() || isSeniorStaff(player());
	}

	protected boolean isSeniorStaff(Player player) {
		return isPlayer(player) && Nerd.of(player).getRank().isSeniorStaff();
	}

	protected boolean isSeniorStaff(OfflinePlayer player) {
		return isOfflinePlayer(player) && Nerd.of(player).getRank().isSeniorStaff();
	}

	protected boolean isAdmin() {
		return !isPlayer() || isAdmin(player());
	}

	protected boolean isAdmin(Player player) {
		return isPlayer(player) && (Nerd.of(player).getRank().equals(Rank.ADMIN) || Nerd.of(player).getRank().equals(Rank.OWNER));
	}

	protected boolean isAdmin(OfflinePlayer player) {
		return isOfflinePlayer(player) && (Nerd.of(player).getRank().equals(Rank.ADMIN) || Nerd.of(player).getRank().equals(Rank.OWNER));
	}

	protected boolean isNullOrEmpty(String string) {
		return Strings.isNullOrEmpty(string);
	}

	protected void runCommand(String commandNoSlash) {
		runCommand(sender(), commandNoSlash);
	}

	protected void runCommand(CommandSender sender, String commandNoSlash) {
		PlayerUtils.runCommand(sender, commandNoSlash);
	}

	protected void runCommandAsOp(String commandNoSlash) {
		runCommandAsOp(sender(), commandNoSlash);
	}

	protected void runCommandAsOp(CommandSender sender, String commandNoSlash) {
		PlayerUtils.runCommandAsOp(sender, commandNoSlash);
	}

	protected void runCommandAsConsole(String commandNoSlash) {
		PlayerUtils.runCommandAsConsole(commandNoSlash);
	}

	protected void checkPermission(String permission) {
		if (!sender().hasPermission(permission))
			throw new NoPermissionException();
	}

	protected String argsString() {
		return argsString(args());
	}

	protected String argsString(List<String> args) {
		if (args() == null || args().size() == 0) return "";
		return String.join(" ", args());
	}

	protected List<String> args() {
		return event.getArgs();
	}

	protected void setArgs(List<String> args) {
		event.setArgs(args);
	}

	protected String arg(int i) {
		return arg(i, false);
	}

	protected String arg(int i, boolean rest) {
		if (event.getArgs().size() < i) return null;
		if (rest)
			return String.join(" ", event.getArgs().subList(i - 1, event.getArgs().size()));

		String result = event.getArgs().get(i - 1);
		if (Strings.isNullOrEmpty(result)) return null;
		return result;
	}

	protected boolean isIntArg(int i) {
		if (event.getArgs().size() < i) return false;
		return isInt(arg(i));
	}

	protected boolean isInt(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	protected int asInt(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument &e" + input + " &cis not a valid integer");
		}
	}

	protected Integer intArg(int i) {
		if (event.getArgs().size() < i) return null;
		try {
			return Integer.parseInt(arg(i));
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument &e#" + i + " &cis not a valid integer");
		}
	}

	protected boolean isDoubleArg(int i) {
		if (event.getArgs().size() < i) return false;
		return isDouble(arg(i));
	}

	protected boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	protected double asDouble(String input) {
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument &e" + input + " &cis not a valid double");
		}
	}

	protected Double doubleArg(int i) {
		if (event.getArgs().size() < i) return null;
		try {
			return Double.parseDouble(arg(i));
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument &e#" + i + " &cis not a valid double");
		}
	}

	protected boolean isFloatArg(int i) {
		if (event.getArgs().size() < i) return false;
		return isFloat(arg(i));
	}

	protected boolean isFloat(String input) {
		try {
			Float.parseFloat(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	protected float asFloat(String input) {
		try {
			return Float.parseFloat(input);
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument &e" + input + " &cis not a valid float");
		}
	}

	protected Float floatArg(int i) {
		if (event.getArgs().size() < i) return null;
		try {
			return Float.parseFloat(arg(i));
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("Argument &e#" + i + " &cis not a valid float");
		}
	}

	protected Boolean booleanArg(int i) {
		if (event.getArgs().size() < i) return null;
		String value = arg(i);
		if (Arrays.asList("enable", "on", "yes", "1").contains(value)) value = "true";
		return Boolean.parseBoolean(value);
	}

	protected boolean isOfflinePlayerArg(int i) {
		if (event.getArgs().size() < i) return false;
		try {
			PlayerUtils.getPlayer(arg(i));
			return true;
		} catch (PlayerNotFoundException ex) {
			return false;
		}
	}

	protected OfflinePlayer offlinePlayerArg(int i) {
		if (event.getArgs().size() < i) return null;
		return PlayerUtils.getPlayer(arg(i));
	}

	protected boolean isPlayerArg(int i) {
		if (event.getArgs().size() < i) return false;
		try {
			return PlayerUtils.getPlayer(arg(i)).isOnline();
		} catch (PlayerNotFoundException ex) {
			return false;
		}
	}

	protected Player playerArg(int i) {
		if (event.getArgs().size() < i) return null;
		OfflinePlayer player = PlayerUtils.getPlayer(arg(i));
		if (!player.isOnline())
			throw new PlayerNotOnlineException(player);
		return player.getPlayer();
	}

	protected void fallback() {
		Fallback fallback = getClass().getAnnotation(Fallback.class);
		if (fallback != null)
			PlayerUtils.runCommand(sender(), fallback.value() + ":" + event.getAliasUsed() + " " + event.getArgsString());
		else
			throw new InvalidInputException("Nothing to fallback to");
	}

	// TODO Don't hardcode model path
	private static final Set<Class<? extends MongoService>> services = new Reflections("me.pugabyte.nexus.models").getSubTypesOf(MongoService.class);
	private static final Map<Class<? extends PlayerOwnedObject>, Class<? extends MongoService>> serviceMap = new HashMap<>();

	static {
		for (Class<? extends MongoService> service : services) {
			PlayerClass annotation = service.getAnnotation(PlayerClass.class);
			if (annotation == null) {
				Nexus.warn(service.getSimpleName() + " does not have @PlayerClass annotation");
				continue;
			}

			serviceMap.put(annotation.value(), service);
		}
	}

	@SneakyThrows
	protected <T extends PlayerOwnedObject> T convertToPlayerOwnedObject(String value, Class<? extends PlayerOwnedObject> type) {
		if (serviceMap.containsKey(type))
			return serviceMap.get(type).newInstance().get(convertToOfflinePlayer(value));
		return null;
	}

	@ConverterFor(OfflinePlayer.class)
	public OfflinePlayer convertToOfflinePlayer(String value) {
		if ("self".equalsIgnoreCase(value)) value = uuid().toString();
		return PlayerUtils.getPlayer(value);
	}

	@ConverterFor(Player.class)
	public Player convertToPlayer(String value) {
		if (isCommandBlock() || isAdmin()) {
			if ("@p".equals(value))
				return PlayerUtils.getNearestPlayer(location()).getObject();
			if ("@r".equals(value))
				return RandomUtils.randomElement(Bukkit.getOnlinePlayers());
			if ("@s".equals(value))
				return player();
		}

		OfflinePlayer offlinePlayer = convertToOfflinePlayer(value);
		return convertToPlayer(offlinePlayer);
	}

	public Player convertToPlayer(OfflinePlayer offlinePlayer) {
		if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null)
			throw new PlayerNotOnlineException(offlinePlayer);
		if (isPlayer() && !PlayerUtils.canSee(player(), offlinePlayer.getPlayer()))
			throw new PlayerNotOnlineException(offlinePlayer);

		return offlinePlayer.getPlayer();
	}

	@TabCompleterFor({Player.class, OfflinePlayer.class})
	public List<String> tabCompletePlayer(String filter) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> PlayerUtils.canSee(player(), player))
				.map(player -> Nerd.of(player).getNickname())
				.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(toList());
	}

//	@TabCompleterFor(OfflinePlayer.class)
	public List<String> tabCompleteOfflinePlayer(String filter) {
		List<String> online = tabCompletePlayer(filter);
		if (!online.isEmpty() || filter.length() < 3)
			return online;

		return new NerdService().find(filter).stream()
				.map(Nerd::getName)
				.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(toList());
	}

	@ConverterFor(World.class)
	World convertToWorld(String value) {
		if ("current".equalsIgnoreCase(value))
			return world();
		World world = Bukkit.getWorld(value);
		if (world == null)
			throw new InvalidInputException("World from " + value + " not found");
		return world;
	}

	@TabCompleterFor(World.class)
	List<String> tabCompleteWorld(String filter) {
		return Bukkit.getWorlds().stream()
				.map(World::getName)
				.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(toList());
	}

	@TabCompleterFor({Boolean.class})
	List<String> tabCompleteBoolean(String filter) {
		return Stream.of("true", "false")
				.filter(bool -> bool.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(toList());
	}

	@ConverterFor(Material.class)
	Material convertToMaterial(String value) {
		if (isInt(value))
			return Material.values()[Integer.parseInt(value)];

		Material material = Material.matchMaterial(value);
		if (material == null)
			throw new InvalidInputException("Material from " + value + " not found");
		return material;
	}

	@ConverterFor(ChatColor.class)
	ChatColor convertToChatColor(String value) {
		if (StringUtils.getHexPattern().matcher(value).matches())
			return ChatColor.of(value.replaceFirst("&", ""));

		try {
			return ColorType.valueOf(value.toUpperCase()).getChatColor();
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException("Color &e" + value + "&c not found");
		}
	}

	@TabCompleterFor(ChatColor.class)
	List<String> tabCompleteChatColor(String filter) {
		return Arrays.stream(ColorType.values())
				.map(colorType -> colorType.name().toLowerCase())
				.filter(name -> name.startsWith(filter.toLowerCase()))
				.collect(toList());
	}

	@ConverterFor(LocalDate.class)
	public LocalDate convertToLocalDate(String value) {
		return parseDate(value);
	}

	@ConverterFor(LocalDateTime.class)
	public LocalDateTime convertToLocalDateTime(String value) {
		return parseDateTime(value);
	}

	@ConverterFor(Enchantment.class)
	Enchantment convertToEnchantment(String value) {
		Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(value));
		if (enchantment == null)
			throw new InvalidInputException("Enchantment from &e" + value + "&c not found");
		return enchantment;
	}

	@TabCompleterFor(Enchantment.class)
	List<String> applicableEnchantmentsTabCompleter(String filter) {
		List<String> results = new ArrayList<>();
		try {
			List<String> enchants = ItemUtils.getApplicableEnchantments(getToolRequired()).stream()
					.map(enchantment -> enchantment.getKey().getKey())
					.filter(enchantment -> enchantment.toLowerCase().startsWith(filter.toLowerCase()))
					.collect(toList());
			if (enchants.size() == 0)
				return getAllEnchants(filter);
			else
				return enchants;
		} catch (InvalidInputException ignore) {
			return getAllEnchants(filter);
		}
	}

	public List<String> getAllEnchants(String filter) {
		return Arrays.stream(Enchantment.values())
				.filter(enchantment -> enchantment.getKey().getKey().toLowerCase().startsWith(filter.toLowerCase()))
				.map(enchantment -> enchantment.getKey().getKey())
				.collect(toList());
	}

	@TabCompleterFor(LivingEntity.class)
	protected List<String> tabCompleteLivingEntity(String value) {
		return new ArrayList<String>() {{
			for (EntityType entityType : EntityType.values()) {
				Class<? extends Entity> entityClass = entityType.getEntityClass();
				if (entityClass != null && LivingEntity.class.isAssignableFrom(entityClass))
					if (entityType.name().toLowerCase().startsWith(value.toLowerCase()))
						add(entityType.name().toLowerCase());
			}
		}};
	}

	protected <T> void paginate(List<T> values, BiFunction<T, String, JsonBuilder> formatter, String command, int page) {
		paginate(values, formatter, command, page, 10);
	}

	protected <T> void paginate(List<T> values, BiFunction<T, String, JsonBuilder> formatter, String command, int page, int amount) {
		if (page < 1)
			error("Page number must be 1 or greater");

		int start = (page - 1) * amount;
		if (values.size() < start)
			error("No results on page " + page);

		int end = Math.min(values.size(), start + amount);

		line();
		AtomicInteger index = new AtomicInteger(start);
		values.subList(start, end).forEach(t -> send(formatter.apply(t, getLeadingZeroIndex(index))));
		line();

		boolean first = page == 1;
		boolean last = end == values.size();

		if (first && last)
			return;

		JsonBuilder buttons = json();
		if (first)
			buttons.next("&7 « Previous  &3");
		else
			buttons.next("&e « Previous  &3").command(command + " " + (page - 1));

		buttons.group().next("&3|&3|").group();

		if (last)
			buttons.next("  &7Next »");
		else
			buttons.next("  &eNext »").command(command + " " + (page + 1));

		send(buttons.group());
	}

	@NotNull
	private String getLeadingZeroIndex(AtomicInteger index) {
		int nextIndex = index.incrementAndGet();
		int nextMagnitude = Integer.parseInt("1" + Strings.repeat("0", String.valueOf(nextIndex).length()));
		int needsLeading0 = nextMagnitude - 10;

		String string = String.valueOf(nextIndex);
		if (nextIndex > 1 && nextIndex == nextMagnitude / 10)
			return string;

		if (nextIndex > needsLeading0)
			string = "0" + string;
		return string;
	}

	@Path("help")
	protected void help() {
		List<String> aliases = getAllAliases();
		if (aliases.size() > 1)
			send(PREFIX + "Aliases: " + String.join("&e, &3", aliases));

		List<JsonBuilder> lines = new ArrayList<>();
		getPathMethods(event).forEach(method -> {
			Path path = method.getAnnotation(Path.class);
			Description desc = method.getAnnotation(Description.class);
			HideFromHelp hide = method.getAnnotation(HideFromHelp.class);

			if (hide != null) return;
			if ("help".equals(path.value()) || "?".equals(path.value())) return;

			String usage = "/" + getAliasUsed().toLowerCase() + " " + (isNullOrEmpty(path.value()) ? "" : path.value());
			String description = (desc == null ? "" : " &7- " + desc.value());
			StringBuilder suggestion = new StringBuilder();
			for (String word : usage.split(" ")) {
				if (word.startsWith("[") || word.startsWith("<"))
					break;
				if (word.startsWith("("))
					suggestion.append(trimFirst(word.split("\\|")[0]));
				else
					suggestion.append(word).append(" ");
			}

			lines.add(json("&c" + usage + description).suggest(suggestion.toString()));
		});

		if (lines.size() == 0)
			error("No usage available");

		send(PREFIX + "Usage:");
		lines.forEach(this::send);
	}


	@AllArgsConstructor
	public class WhoFormatter {
		private final OfflinePlayer self, target;
		private final WhoType whoType;

		public String format() {
			boolean self = isSelf(this.self, target);
			String targetName = (target.getName() == null ? "Unknown" : target.getName());

			if (whoType == WhoType.POSSESSIVE_UPPER || whoType == WhoType.POSSESSIVE_LOWER)
				return self ? (whoType == WhoType.POSSESSIVE_UPPER ? "Y" : "y") + "our" : targetName + "'" + (targetName.endsWith("s") ? "" : "s");
			else if (whoType == WhoType.ACTIONARY_UPPER || whoType == WhoType.ACTIONARY_LOWER)
				return self ? (whoType == WhoType.ACTIONARY_UPPER ? "Y" : "y") + "ou do" : targetName + " does";

			throw new InvalidInputException("Unknown format action");
		}
	}

	public String formatWho(PlayerOwnedObject target, WhoType whoType) {
		return formatWho(player(), target, whoType);
	}

	public String formatWho(OfflinePlayer target, WhoType whoType) {
		return formatWho(player(), target, whoType);
	}

	public String formatWho(Player self, PlayerOwnedObject target, WhoType whoType) {
		return formatWho(target.getOfflinePlayer(), whoType);
	}

	public String formatWho(Player self, OfflinePlayer target, WhoType whoType) {
		return new WhoFormatter(self, target, whoType).format();
	}

	public enum WhoType {
		POSSESSIVE_UPPER,
		POSSESSIVE_LOWER,
		ACTIONARY_UPPER,
		ACTIONARY_LOWER
	}

}
