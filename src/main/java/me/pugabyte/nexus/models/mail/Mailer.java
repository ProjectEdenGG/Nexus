package me.pugabyte.nexus.models.mail;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import eden.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.commands.MailCommand;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static eden.utils.StringUtils.asOxfordList;
import static eden.utils.StringUtils.isNullOrEmpty;

@Data
@Builder
@Entity("mailer")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Mailer implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<WorldGroup, List<Mail>> mail = new HashMap<>();
	private Map<WorldGroup, Mail> pendingMail = new HashMap<>();

	public List<Mail> getMail(WorldGroup worldGroup) {
		return mail.computeIfAbsent(worldGroup, $ -> new ArrayList<>());
	}

	public List<Mail> getUnreadMail(WorldGroup worldGroup) {
		return getMail(worldGroup).stream().filter(mail -> mail.getReceived() == null).toList();
	}

	public void addPending(Mail mail) {
		pendingMail.put(getWorldGroup(), mail);
	}

	public boolean hasPending() {
		return pendingMail.containsKey(getWorldGroup());
	}

	public void checkPending() {
		if (!hasPending())
			throw new InvalidInputException("You do not have any pending mail");
	}

	public Mail getPending() {
		checkPending();
		return pendingMail.get(getWorldGroup());
	}

	private Mail removePending() {
		checkPending();
		return pendingMail.remove(getWorldGroup());
	}

	public void sendNotification() {
		if (!isOnline())
			return;

		List<String> groups = Arrays.stream(WorldGroup.values())
				.filter(worldGroup -> !getUnreadMail(worldGroup).isEmpty())
				.map(StringUtils::camelCase)
				.collect(Collectors.toList());

		if (groups.isEmpty())
			return;

		String message;
		if (groups.size() == 1)
			message = "an unclaimed delivery in &e" + groups.get(0);
		else
			message = "unclaimed deliveries in &e" + asOxfordList(groups, "&3, &e");

		sendMessage(json(MailCommand.PREFIX + "&3You have " + message + "&3, use &c/mail box &3to claim it!")
				.command("/mail box")
				.hover("&eClick to view your mail box"));
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Converters({UUIDConverter.class, LocalDateTimeConverter.class, ItemStackConverter.class})
	public static class Mail implements PlayerOwnedObject {
		@Id
		@NonNull
		private UUID uuid;
		private UUID from;
		private WorldGroup worldGroup;
		private LocalDateTime sent;
		private LocalDateTime received;
		private ItemStack message;
		private List<ItemStack> items;

		public Mail(@NonNull UUID uuid, UUID from, WorldGroup worldGroup, String message) {
			this(uuid, from, worldGroup, message, Collections.emptyList());
		}

		public Mail(@NonNull UUID uuid, UUID from, WorldGroup worldGroup, String message, List<ItemStack> items) {
			this.uuid = uuid;
			this.from = from;
			this.worldGroup = worldGroup;
			this.items = items;
			setMessage(message);
		}

		public void setMessage(String message) {
			if (isNullOrEmpty(message))
				this.message = null;
			else
				this.message = new ItemBuilder(Material.WRITTEN_BOOK)
						.bookAuthor(Nickname.of(from))
						.bookTitle("From " + Nickname.of(from))
						.bookPages(message)
						.build();
		}

		public List<ItemStack> getAllItems() {
			return new ArrayList<>() {{
				if (hasMessage())
					add(message);
				if (hasItems())
					addAll(items);
			}};
		}

		public Mailer getOwner() {
			return new MailerService().get(uuid);
		}

		public Mailer getFromMailer() {
			return new MailerService().get(from);
		}

		public void received() {
			received = LocalDateTime.now();
		}

		public void send() {
			if (getFromMailer().getPendingMail().containsKey(worldGroup) && getFromMailer().getPendingMail().get(worldGroup).equals(this))
				getFromMailer().getPendingMail().remove(worldGroup);

			getOwner().getMail(worldGroup).add(this);
			sent = LocalDateTime.now();
			new MailerService().save(getOwner());
		}

		public boolean hasMessage() {
			return !ItemUtils.isNullOrAir(message);
		}

		public boolean hasItems() {
			return !Utils.isNullOrEmpty(items);
		}

		public void cancel() {
			getFromMailer().removePending();
			if (hasItems())
				PlayerUtils.giveItemsAndMailExcess(getFromMailer(), items, null, worldGroup);
		}

		public String getContents() {
			return String.join(" &3and ", new ArrayList<String>() {{
				if (hasMessage()) add("&ea message");
				if (hasItems()) add("&e" + items.stream().mapToInt(ItemStack::getAmount).sum() + " items");
			}});
		}

		public ItemBuilder getDisplayItem() {
			List<String> lore = new ArrayList<>();

			if (hasMessage())
				lore.add("&e1 &7Message");

			if (hasItems()) {
				int count = 1;
				int size = items.size();
				for (ItemStack item : items) {
					lore.add("&e" + item.getAmount() + " &7" + StringUtils.camelCase(item.getType()));
					if (++count > 5) {
						lore.add("&7And " + (size - count) + " more");
						break;
					}
				}
			}

			return new ItemBuilder(Material.CHEST).name("&7From &e" + Nickname.of(from)).lore(lore);
		}

		public static Mail fromServer(UUID to, WorldGroup worldGroup, String message) {
			return fromServer(to, worldGroup, message, Collections.emptyList());
		}

		public static Mail fromServer(UUID to, WorldGroup worldGroup, ItemStack... items) {
			return fromServer(to, worldGroup, null, Arrays.asList(items));
		}

		public static Mail fromServer(UUID to, WorldGroup worldGroup, List<ItemStack> items) {
			return fromServer(to, worldGroup, null, items);
		}

		public static Mail fromServer(UUID to, WorldGroup worldGroup, String message, ItemStack... items) {
			return fromServer(to, worldGroup, message, Arrays.asList(items));
		}

		public static Mail fromServer(UUID to, WorldGroup worldGroup, String message, List<ItemStack> items) {
			return new Mail(to, StringUtils.getUUID0(), worldGroup, message, items);
		}


	}

}
