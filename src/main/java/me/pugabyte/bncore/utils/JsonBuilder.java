package me.pugabyte.bncore.utils;

import me.pugabyte.bncore.BNCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.getLastColor;
import static me.pugabyte.bncore.utils.StringUtils.loreize;

public class JsonBuilder {
	private final ComponentBuilder result = new ComponentBuilder("");
	private ComponentBuilder builder = new ComponentBuilder("");
	// Helper boolean for loops and stuff
	private boolean initialized;

	public JsonBuilder() {
		this("");
	}

	public JsonBuilder(String text) {
		next(text);
	}

	public JsonBuilder(ComponentBuilder builder) {
		this.builder = builder;
	}

	public JsonBuilder(BaseComponent[] builder) {
		this.builder = new ComponentBuilder().append(builder);
	}

	private void debug(String message) {
		if (false)
			BNCore.log(message);
	}

	public JsonBuilder next(String text) {
		builder.append(TextComponent.fromLegacyText(getColoredWords(colorize(text))), FormatRetention.NONE);
		return this;
	}

	public JsonBuilder group() {
		result.append(builder.create(), FormatRetention.NONE);
		builder = new ComponentBuilder("");
		return this;
	}

	public JsonBuilder newline() {
		builder.append("\n");
		group();
		return this;
	}

	public JsonBuilder line() {
		newline();
		newline();
		return this;
	}

	public JsonBuilder color(ChatColor color) {
		builder.color(color);
		return this;
	}

	public JsonBuilder url(String url) {
		addClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return this;
	}

	public JsonBuilder command(String command) {
		if (!command.startsWith("/"))
			command = "/" + command;
		addClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return this;
	}

	public JsonBuilder suggest(String command) {
		addClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
		return this;
	}

	public JsonBuilder copy(String command) {
		addClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, command));
		return this;
	}

	public JsonBuilder hover(String text) {
		BaseComponent[] components = new ComponentBuilder(loreize(text).replaceAll("\\|\\|", "\n")).create();
		addHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(components)));
		return this;
	}

	public JsonBuilder hover(String text, ChatColor color) {
		BaseComponent[] components = new ComponentBuilder(loreize(text).replaceAll("\\|\\|", "\n")).color(color).create();
		addHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(components)));
		return this;
	}

	public JsonBuilder hover(ItemStack itemStack) {
		Content item = Bukkit.getServer().getItemFactory().hoverContentOf(itemStack);
		addHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, item));
		return this;
	}

	public JsonBuilder hover(Entity entity) {
		Content item = Bukkit.getServer().getItemFactory().hoverContentOf(entity);
		addHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, item));
		return this;
	}

	public JsonBuilder insert(String insertion) {
		builder.insertion(insertion);
		ComponentBuilder newBuilder = new ComponentBuilder();
		for (BaseComponent baseComponent : builder.getParts()) {
			baseComponent.setInsertion(insertion);
			newBuilder.append(baseComponent, FormatRetention.NONE);
		}
		builder = newBuilder;
		return this;
	}

	private void addClickEvent(ClickEvent event) {
		ComponentBuilder newBuilder = new ComponentBuilder();
		for (BaseComponent baseComponent : builder.getParts()) {
			baseComponent.setClickEvent(event);
			newBuilder.append(baseComponent, FormatRetention.NONE);
		}
		builder = newBuilder;
	}

	private void addHoverEvent(HoverEvent event) {
		ComponentBuilder newBuilder = new ComponentBuilder();
		for (BaseComponent baseComponent : builder.getParts()) {
			baseComponent.setHoverEvent(event);
			newBuilder.append(baseComponent, FormatRetention.NONE);
		}
		builder = newBuilder;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void initialize() {
		this.initialized = true;
	}

	public void send(CommandSender sender) {
		if (sender instanceof Player)
			sender.spigot().sendMessage(build());
		else if (sender instanceof OfflinePlayer) {
			OfflinePlayer player = (OfflinePlayer) sender;
			if (player.isOnline() && player.getPlayer() != null)
				player.getPlayer().spigot().sendMessage(build());
		} else
			sender.sendMessage(toString());
	}

	public BaseComponent[] build() {
		group();
		return new ComponentBuilder(result).create();
	}

	private String getColoredWords(String text) {
		StringBuilder builder = new StringBuilder();
		for (String word : text.split(" "))
			builder.append(getLastColor(builder.toString())).append(word).append(" ");

		// Trim trailing whitespace
		String result = builder.toString().replaceFirst("\\s++$", "");
		if (text.endsWith(" ")) result += " ";
		return result;
	}

	public String toString() {
		return BaseComponent.toPlainText(new ComponentBuilder(new ComponentBuilder(result).append(builder.create(), FormatRetention.NONE)).create());
	}

	public String serialize() {
		return ComponentSerializer.toString(build());
	}


}
