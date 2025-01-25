package gg.projecteden.nexus.features.store.perks.visuals;

import com.comphenix.protocol.wrappers.EnumWrappers;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import kotlin.Pair;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aliases("emojihat")
public class EmojiHatsCommand extends CustomCommand {

	public EmojiHatsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		EmojiHat.init();
	}

	@Path("list [page]")
	@Description("List owned emoji hats")
	void list(@Arg("1") int page) {
		final List<EmojiHat> hats = Arrays.stream(EmojiHat.values())
			.filter(type -> type.canBeUsedBy(player()))
			.toList();

		if (hats.isEmpty())
			error("You do not own any emoji hats, purchase with &c/event store");

		new Paginator<EmojiHat>()
			.values(hats)
			.formatter((hat, index) -> json("&3" + index + " &a[Start] &e" + camelCase(hat))
				.command("/emojihats " + hat.name().toLowerCase())
				.hover("&eClick to start")
			)
			.command("/emojihats")
			.page(page)
			.send();
	}

	@Path("<type>")
	@Description("Activate an emoji hat")
	void run(EmojiHat type) {
		if (!type.canBeUsedBy(player()))
			error("You do not have permission for this emoji hat");

		type.run(player());
	}

	@Path("run <player> <type>")
	@Permission(Group.ADMIN)
	@Description("Activate an emoji hat on another player")
	void run(Player player, EmojiHat type) {
		type.run(player);
	}

	@Path("give <player> <type>")
	@Permission(Group.ADMIN)
	@Description("Give a player access to an emoji hat")
	void give(Player player, EmojiHat type) {
		if (type.canBeUsedBy(player))
			error("&e" + Nickname.of(player) + " &calready owns &e" + camelCase(type));

		PermissionChange.set().player(player).permissions(type.getPermission()).runAsync();
		send(PREFIX + "Gave &e" + camelCase(type) + " &3to &e" + Nickname.of(player));
	}

	@Path("getFrameItems <type>")
	@Permission(Group.ADMIN)
	@Description("Spawn the individual frames of an emoji hat")
	void getFrameItems(EmojiHat type) {
		PlayerUtils.giveItems(player(), type.getFrameItems());
	}

	@TabCompleterFor(EmojiHat.class)
	List<String> tabCompleterForEmojiHat(String filter) {
		return Arrays.stream(EmojiHat.values())
				.filter(type -> type.canBeUsedBy(player()))
				.map(Enum::name)
				.map(String::toLowerCase)
				.toList();
	}

	public enum EmojiHat {
		CHEEKY("5f15c9b8edc5629b6caa49148a20c5890853c2674385e43876ca56d1d465f", new ArrayList<>() {{
			add(new Pair<>("4db4c8dfdc7792d24c5334a5a2d1d467b6e10abfabd637e4a80ccb05b9ccfbd", 15L));
			add(new Pair<>("b7d533e65f2cae97afe334c81ecc97e2fa5b3e5d3ecf8b91bc39a5adb2e79a", 2L));
			add(new Pair<>("35a46f8334e49d273384eb72b2ac15e24a640d7648e4b28c348efce93dc97ab", 8L));
			add(new Pair<>("302110b4f2911c0f5597796ec812e8fa260ba5ab7cff725e16b7eee7c677b7", 0L));
			add(new Pair<>("5f15c9b8edc5629b6caa49148a20c5890853c2674385e43876ca56d1d465f", 48L));
			add(new Pair<>("302110b4f2911c0f5597796ec812e8fa260ba5ab7cff725e16b7eee7c677b7", 0L));
			add(new Pair<>("447dcf9dd283ad6d83942b6607a7ce45bee9cdfeefb849da29d661d03e7938", 0L));
			add(new Pair<>("de355559f4cd56118b4bc8b4697b625e1845b635790c07bf4924c8c7673a2e4", 0L));
			add(new Pair<>("207eef91a453a5151487c9d6b9d4c434db7f8a02a4caf18ef6f3358677f6", 0L));
			add(new Pair<>("5f15c9b8edc5629b6caa49148a20c5890853c2674385e43876ca56d1d465f", 48L));
			add(new Pair<>("302110b4f2911c0f5597796ec812e8fa260ba5ab7cff725e16b7eee7c677b7", 0L));
			add(new Pair<>("447dcf9dd283ad6d83942b6607a7ce45bee9cdfeefb849da29d661d03e7938", 0L));
			add(new Pair<>("de355559f4cd56118b4bc8b4697b625e1845b635790c07bf4924c8c7673a2e4", 0L));
			add(new Pair<>("207eef91a453a5151487c9d6b9d4c434db7f8a02a4caf18ef6f3358677f6", 0L));
			add(new Pair<>("5f15c9b8edc5629b6caa49148a20c5890853c2674385e43876ca56d1d465f", 8L));
		}}),
		COOL("44c8cd31bac657a3f26b52e262c83eb8338d56651fd22c1633565f3dbbc45777", new ArrayList<>() {{
			add(new Pair<>("d3611069c25ad9b32a7bdf48534935b9f5875cd85401bc0a0346556471d48", 38L));
			add(new Pair<>("d0f294918d9ac9a65b3cfc752668f323f874ffa7b161e3617724592c12657f13", 6L));
			add(new Pair<>("371a66164896dcd8b17bfe9bbf5e1e8c43afbbd7c7e42b751483c9c468e52", 2L));
			add(new Pair<>("44c8cd31bac657a3f26b52e262c83eb8338d56651fd22c1633565f3dbbc45777", 100L));
		}}),
		CRY("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", new ArrayList<>() {{
			add(new Pair<>("9eabeda496e0684f80d88d4b5a71d65afaf9faf184fa4a77f81ed24c6da0f4", 18L));
			add(new Pair<>("196b8e272c54a422d9df36d85caff26624c733e7b3f6040d3e4c9cd6e", 6L));
			add(new Pair<>("dec9aa9b3f46195ae9c7fea7c61148764a41e0d68dae41e82868d792b3c", 2L));
			add(new Pair<>("be29dadb60c9096fab92ffa7749e30462e14a8afaf6de938d9c0a4d78781", 10L));
			add(new Pair<>("c8aba1f49fbf8829859ddd8f7e5918155e7ddc78919768b6e6c536e5278c31", 2L));
			add(new Pair<>("1073ba3f1ca1d1e4f7e1ec742ddcff8fb0d962bc5662d127622a3726e3bb66", 0L));
			add(new Pair<>("952dcdb13f732342ef37cbf0902960984992f5e67289373054b00c2a1f7", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 1L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 1L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
			add(new Pair<>("3864b0925afc2d31af69ae124d05c9ca31ce25f9d2e97b569b90ca236a3e", 0L));
			add(new Pair<>("4b0f2f3d3499959e97d27e610bcfd90dbf8df5e1cf4b98f259284f2e355728", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 1L));
			add(new Pair<>("fe3e22761c76b4f8fad89dbc80f3af203e7b8211238011be7ffb80261d9c64", 0L));
			add(new Pair<>("732fe121a63eaabd99ced6d1acc91798652d1ee8084d2f9127d8a315cad5ce4", 0L));
			add(new Pair<>("ede4d485eec0b08e32ff4a3db8b79c1524cba93e47f861d8468adf367044ab", 0L));
		}}),
		FROWN("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", new ArrayList<>() {{
			add(new Pair<>("4db4c8dfdc7792d24c5334a5a2d1d467b6e10abfabd637e4a80ccb05b9ccfbd", 38L));
			add(new Pair<>("b7d533e65f2cae97afe334c81ecc97e2fa5b3e5d3ecf8b91bc39a5adb2e79a", 6L));
			add(new Pair<>("6f5c3992ed5f213dbc6e9f368915fb519db4e187407518dd25e014b81c6e6eb", 2L));
			add(new Pair<>("bb4f3959d776a4aa16c43dde16ee3777f958b5c66ac05c542cbe27f67d7be7ce", 6L));
			add(new Pair<>("6ea079c93141c8c6bade60b1987d424739b46361dbf513c0a4903dba4e67", 1L));
			add(new Pair<>("1c2672aaae58f3d1852d19b8422caf70b32582f8de3fcb5c7c24dacb7ebc3", 2L));
			add(new Pair<>("d541992d7612a14ba58978d12fcb212bcff773977868ac491feb8f1fa5bc", 1L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 36L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 0L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 36L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 1L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 36L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 0L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 36L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 1L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 20L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 14L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 0L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 20L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 14L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 1L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 36L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 0L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 36L));
			add(new Pair<>("865e1f444fafb737c0abf5be1bf97fc27adedae3fd98226a468fa72c1d1753", 1L));
			add(new Pair<>("913ebedac47e4486a18fc634324e6a22b1b9c56c32c0f13397a747c6fc44c9", 2L));
		}}),
		GRIN("01b9def55876c41c17c815f88115f02c95f89620fbed6a6cb2d38d46fe05", new ArrayList<>() {{
			add(new Pair<>("465b5611f8abc01d9bb8fcd62f3a64b5125534a428731f202e619d9ce1", 3L));
			add(new Pair<>("a5d43eb0ec5f6de1d469b69680978a6dd7117772ee0d82ffdf08749e84df7ed", 3L));
			add(new Pair<>("8d8f5fb387ca66fc2f65b91fcb231604548e8565895bb96c676984205e6f19", 28L));
			add(new Pair<>("01b9def55876c41c17c815f88115f02c95f89620fbed6a6cb2d38d46fe05", 117L));
		}}),
		RAGE("a750127f1c3c71f6a5f5e9917a825e9235e1959b258ff29b6ff9771cb44", new ArrayList<>() {{
			add(new Pair<>("513f7eb9fcf9926bf7b94049aef5efdb7bbe70bcc74f3f6618e12dc181d627", 6L));
			add(new Pair<>("47bbf6d9f4c57556eef816c50eb75f9d158f53954957aabe6c2e14ffa6c90", 2L));
			add(new Pair<>("a750127f1c3c71f6a5f5e9917a825e9235e1959b258ff29b6ff9771cb44", 2L));
			add(new Pair<>("e95b20fb1fcfbef222062dd43eecbcb3871c528665f8ed675f42fc6e589a0b7", 2L));
			add(new Pair<>("275c46184f9a85351d6ba618f8d1655cb5b71d6fc6ed3ccc462d916d376a8db", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 1L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 1L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 1L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("7f8db8cf241f2565c5bd495a0695b7cac9370c8bfd732d6d874e62fb12f3da", 0L));
			add(new Pair<>("fa151ceb66b3412775e9d44879046a398dbdb7dfcb0af571b7a03e72d9fbf1", 0L));
			add(new Pair<>("c9f8d04057978817cb81e095ccc59799fe4b780ffdbfb9f0d62aa286721856", 0L));
		}}),
		SLEEPY("a058ce61d1e9ad6b115da1cbdf56d97e2a2cb38e31ea179431d935616163581", new ArrayList<>() {{
			add(new Pair<>("4db4c8dfdc7792d24c5334a5a2d1d467b6e10abfabd637e4a80ccb05b9ccfbd", 0L));
			add(new Pair<>("505c237731ae7a64c0668ce889dc6d1e21cfdbcc9fa51efe48143ee456e45a9", 0L));
			add(new Pair<>("36f549597fa56bfb8be974ff2a1887f468623db774f51ca760dfba172842", 0L));
			add(new Pair<>("888a8263112c4c939d82a2dfd3d9b9e896925d41bff7869e59df6b192d86dd7", 8L));
			add(new Pair<>("626d95a0acb4224af4818db670b36e5f20192a89efb96fa5c2bf0c7e43f2d7f", 2L));
			add(new Pair<>("46311717e8c2d5882942b9279417b51832cb2a2f7b06f8215b9fffe71f360f2", 2L));
			add(new Pair<>("e8c9c6efad9cee8fe7d3b56370f6675933db9fcdf236a1c49b6349251a4f5a56", 4L));
			add(new Pair<>("7d9f0f19ef161bfabaeaa49c52880becfe82fb962bbb217d1743d0f1b53a95", 13L));
			add(new Pair<>("a058ce61d1e9ad6b115da1cbdf56d97e2a2cb38e31ea179431d935616163581", 5L));
			add(new Pair<>("7d9f0f19ef161bfabaeaa49c52880becfe82fb962bbb217d1743d0f1b53a95", 12L));
			add(new Pair<>("a058ce61d1e9ad6b115da1cbdf56d97e2a2cb38e31ea179431d935616163581", 4L));
			add(new Pair<>("7d9f0f19ef161bfabaeaa49c52880becfe82fb962bbb217d1743d0f1b53a95", 14L));
			add(new Pair<>("a058ce61d1e9ad6b115da1cbdf56d97e2a2cb38e31ea179431d935616163581", 3L));
			add(new Pair<>("7d9f0f19ef161bfabaeaa49c52880becfe82fb962bbb217d1743d0f1b53a95", 13L));
			add(new Pair<>("a058ce61d1e9ad6b115da1cbdf56d97e2a2cb38e31ea179431d935616163581", 4L));
			add(new Pair<>("7d9f0f19ef161bfabaeaa49c52880becfe82fb962bbb217d1743d0f1b53a95", 13L));
			add(new Pair<>("a058ce61d1e9ad6b115da1cbdf56d97e2a2cb38e31ea179431d935616163581", 4L));
			add(new Pair<>("7d9f0f19ef161bfabaeaa49c52880becfe82fb962bbb217d1743d0f1b53a95", 13L));
			add(new Pair<>("a058ce61d1e9ad6b115da1cbdf56d97e2a2cb38e31ea179431d935616163581", 4L));
			add(new Pair<>("7d9f0f19ef161bfabaeaa49c52880becfe82fb962bbb217d1743d0f1b53a95", 1L));
		}}),
		SMILE("cc35dfe9b5bee1e139d728ad8aeaf23556e2e19fc7615b9965c0a9a6e566c890", new ArrayList<>() {{
			add(new Pair<>("b7d533e65f2cae97afe334c81ecc97e2fa5b3e5d3ecf8b91bc39a5adb2e79a", 18L));
			add(new Pair<>("d17e8ba17459d20fd73672bcb8a9e2a8a44cf0a5ff154122d96b5dbbd9171a", 19L));
			add(new Pair<>("cc35dfe9b5bee1e139d728ad8aeaf23556e2e19fc7615b9965c0a9a6e566c890", 116L));
		}}),
		SURPRISED("b328db1c323585adeba1907ced306050e02aa77591588fb182fdeaf423ad6", new ArrayList<>() {{
			add(new Pair<>("1115b27bd1d79b688f9106a3fc23374a24c7f73ef9d93f567261a4feffd73", 3L));
			add(new Pair<>("b328db1c323585adeba1907ced306050e02aa77591588fb182fdeaf423ad6", 3L));
			add(new Pair<>("fdbde0c44e99f8da6488a93588a82aabd4a1fc603ed2aaf51d5671c3d4d", 3L));
			add(new Pair<>("45868529fbf4be629371275b1138dab929576021716ee737db12634aa125af3", 82L));
		}}),
		WINK("27f64e27bc850a33fdfffb79b56c9baea8a18f9763b7cad56c4b6a2e9d3", new ArrayList<>() {{
			add(new Pair<>("73742defbc4677e45468b9324d9704f635a5fc311e9b8368dd5cc4cd282d6a", 8L));
			add(new Pair<>("ce25c52eee47c12e1aa16989cec9c9d3e49af8c44b71ebc953ca1d1ff2d59670", 3L));
			add(new Pair<>("27f64e27bc850a33fdfffb79b56c9baea8a18f9763b7cad56c4b6a2e9d3", 3L));
			add(new Pair<>("9d9eb60d92c1ff2e9cec65ddc23c2fb7e12450b36071dddd6690f8875486d8", 4L));
			add(new Pair<>("27f64e27bc850a33fdfffb79b56c9baea8a18f9763b7cad56c4b6a2e9d3", 53L));
			add(new Pair<>("9d9eb60d92c1ff2e9cec65ddc23c2fb7e12450b36071dddd6690f8875486d8", 4L));
			add(new Pair<>("27f64e27bc850a33fdfffb79b56c9baea8a18f9763b7cad56c4b6a2e9d3", 53L));
			add(new Pair<>("9d9eb60d92c1ff2e9cec65ddc23c2fb7e12450b36071dddd6690f8875486d8", 4L));
			add(new Pair<>("27f64e27bc850a33fdfffb79b56c9baea8a18f9763b7cad56c4b6a2e9d3", 9L));
		}}),
		GOOFY("76d8863dc79c3b4fbe4ca619b5f356a4eb5c9734b33a2552ac990551b1211", new ArrayList<>() {{
			add(new Pair<>("a5f17b61c181ef6e9fcd3e411be7aae0347210b94e71259a6fd1fc5cdf5826", 5L));
			add(new Pair<>("443ad7d16d841881baf113dace60a2ce6c473a80535948ebcead49992c5b96a", 5L));
			add(new Pair<>("76d8863dc79c3b4fbe4ca619b5f356a4eb5c9734b33a2552ac990551b1211", 5L));
			add(new Pair<>("d1df507575c94ad63e40c4d4a1dd9c9960f96ae92ae36cd1ed16c6f544626", 5L));
			add(new Pair<>("76d8863dc79c3b4fbe4ca619b5f356a4eb5c9734b33a2552ac990551b1211", 5L));
			add(new Pair<>("443ad7d16d841881baf113dace60a2ce6c473a80535948ebcead49992c5b96a", 5L));
			add(new Pair<>("a5f17b61c181ef6e9fcd3e411be7aae0347210b94e71259a6fd1fc5cdf5826", 5L));
			add(new Pair<>("443ad7d16d841881baf113dace60a2ce6c473a80535948ebcead49992c5b96a", 5L));
			add(new Pair<>("76d8863dc79c3b4fbe4ca619b5f356a4eb5c9734b33a2552ac990551b1211", 5L));
			add(new Pair<>("d1df507575c94ad63e40c4d4a1dd9c9960f96ae92ae36cd1ed16c6f544626", 5L));
			add(new Pair<>("76d8863dc79c3b4fbe4ca619b5f356a4eb5c9734b33a2552ac990551b1211", 5L));
			add(new Pair<>("443ad7d16d841881baf113dace60a2ce6c473a80535948ebcead49992c5b96a", 5L));
			add(new Pair<>("a5f17b61c181ef6e9fcd3e411be7aae0347210b94e71259a6fd1fc5cdf5826", 5L));
			add(new Pair<>("443ad7d16d841881baf113dace60a2ce6c473a80535948ebcead49992c5b96a", 5L));
			add(new Pair<>("76d8863dc79c3b4fbe4ca619b5f356a4eb5c9734b33a2552ac990551b1211", 5L));
			add(new Pair<>("d1df507575c94ad63e40c4d4a1dd9c9960f96ae92ae36cd1ed16c6f544626", 5L));
			add(new Pair<>("76d8863dc79c3b4fbe4ca619b5f356a4eb5c9734b33a2552ac990551b1211", 5L));
			add(new Pair<>("443ad7d16d841881baf113dace60a2ce6c473a80535948ebcead49992c5b96a", 5L));
			add(new Pair<>("a5f17b61c181ef6e9fcd3e411be7aae0347210b94e71259a6fd1fc5cdf5826", 5L));
			add(new Pair<>("443ad7d16d841881baf113dace60a2ce6c473a80535948ebcead49992c5b96a", 5L));
		}}),
		LOVE("fd26ae4b5793d087e62a2cf3f34359829d02869aae6626bfcff59de1469f51", new ArrayList<>() {{
			add(new Pair<>("901b958ed2c36e45bae72b42d4ee719d45240b233669091b1cc9e070e31119", 39L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 2L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("fd26ae4b5793d087e62a2cf3f34359829d02869aae6626bfcff59de1469f51", 3L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 2L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("fd26ae4b5793d087e62a2cf3f34359829d02869aae6626bfcff59de1469f51", 3L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 3L));
			add(new Pair<>("901b958ed2c36e45bae72b42d4ee719d45240b233669091b1cc9e070e31119", 39L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 2L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 0L));
			add(new Pair<>("fd26ae4b5793d087e62a2cf3f34359829d02869aae6626bfcff59de1469f51", 4L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 2L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("fd26ae4b5793d087e62a2cf3f34359829d02869aae6626bfcff59de1469f51", 3L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 0L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 3L));
			add(new Pair<>("901b958ed2c36e45bae72b42d4ee719d45240b233669091b1cc9e070e31119", 40L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 2L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("fd26ae4b5793d087e62a2cf3f34359829d02869aae6626bfcff59de1469f51", 4L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 1L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 0L));
			add(new Pair<>("fd26ae4b5793d087e62a2cf3f34359829d02869aae6626bfcff59de1469f51", 4L));
			add(new Pair<>("96fbb52a4d0c62d8e6cae8c485e551b37fec68e6daab23d85f2ff52faa4c4", 1L));
			add(new Pair<>("895f6415bd9424a664d694371a846838c20fb36c3b4a22f385fe7e3dce2996", 2L));
			add(new Pair<>("901b958ed2c36e45bae72b42d4ee719d45240b233669091b1cc9e070e31119", 16L));
		}}),
		DEALWITHIT("3923f5b42d1677155d08442c0aa39851596156c5e09b3461aa35868989a4bb", new ArrayList<>() {{
			add(new Pair<>("65ee6541245fd5b98217ceaf9410a4aa9fbd5abcfb646d33222729a1d6d159f", 19L));
			add(new Pair<>("9aaf1dcbc1a88534f4c6a83961ad08569f7559dcc967bb648ac48ee70fc4c", 6L));
			add(new Pair<>("eba7158981232a5121e955ea3f20e3611ab85bd17b3724ac2481dbc0c19054", 6L));
			add(new Pair<>("599eb74ec2bb0added18ebd777a5a7478e86256deb536dccbfc4acd9be2a28", 5L));
			add(new Pair<>("de2d826df4cf169cb8bc28ef4a1ce47b273ed57c7993c41c2069c8d36ce47df6", 40L));
			add(new Pair<>("4734d2b46ad33a689869515d17a820f1f2319aad54554b6ba28e79e2c9ce5eb", 5L));
			add(new Pair<>("a324b5c8fe69dfd062185589962da66e28a8999a797c04053abc1e3fc1c1", 15L));
			add(new Pair<>("fe79e5f9d81c8a84e1f9c2a817687f3b9543bdbb609ef8c6f90974f73d0c2", 4L));
			add(new Pair<>("bada42db13c657982fa73186db7b5949612176ede6753695d2b6d94aa7262b74", 1L));
			add(new Pair<>("3923f5b42d1677155d08442c0aa39851596156c5e09b3461aa35868989a4bb", 95L));
		}}),
		RIP("439c3df7a628af8d751ecca197642cdc1a07c30e3289b2d3261f7a65cf395b", new ArrayList<>() {{
			add(new Pair<>("30e78285d5aee0b28787ad88a5d58fb05ccf22918daa516ead85a6bf4fe068", 20L));
			add(new Pair<>("63611b5724e091854e79926fd11e486bfd0f99042721c3b34177f818639c19d", 10L));
			add(new Pair<>("83e0621b45d3a326d236293cd8ea49ae74d52e56fc8d1d133e7fc8bcf2a5988", 9L));
			add(new Pair<>("6e16a7ae186c3cfeac364eac0e83d3528741c3dd9ef8277080e03deabc714", 4L));
			add(new Pair<>("20ec3a80ed35bd9beb7d20cb75f1ecd5b8ab0d576f1db699f7def13131fbc5", 2L));
			add(new Pair<>("b03badcc9fb966c87e0dc1332d735b2b587c2602d35fecb44ba6ed94ceb4", 1L));
			add(new Pair<>("439c3df7a628af8d751ecca197642cdc1a07c30e3289b2d3261f7a65cf395b", 39L));
			add(new Pair<>("b03badcc9fb966c87e0dc1332d735b2b587c2602d35fecb44ba6ed94ceb4", 2L));
			add(new Pair<>("20ec3a80ed35bd9beb7d20cb75f1ecd5b8ab0d576f1db699f7def13131fbc5", 1L));
			add(new Pair<>("6e16a7ae186c3cfeac364eac0e83d3528741c3dd9ef8277080e03deabc714", 3L));
			add(new Pair<>("83e0621b45d3a326d236293cd8ea49ae74d52e56fc8d1d133e7fc8bcf2a5988", 30L));
			add(new Pair<>("6e16a7ae186c3cfeac364eac0e83d3528741c3dd9ef8277080e03deabc714", 3L));
			add(new Pair<>("20ec3a80ed35bd9beb7d20cb75f1ecd5b8ab0d576f1db699f7def13131fbc5", 2L));
			add(new Pair<>("b03badcc9fb966c87e0dc1332d735b2b587c2602d35fecb44ba6ed94ceb4", 1L));
			add(new Pair<>("439c3df7a628af8d751ecca197642cdc1a07c30e3289b2d3261f7a65cf395b", 85L));
		}}),
		DIZZY("4c46ee76fd9e947e3789fac348216ab98c2421268fafaf3eb29ec949c0b82169", new ArrayList<>() {{
			add(new Pair<>("4c46ee76fd9e947e3789fac348216ab98c2421268fafaf3eb29ec949c0b82169", 41L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 1L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 1L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 1L));
			add(new Pair<>("4c46ee76fd9e947e3789fac348216ab98c2421268fafaf3eb29ec949c0b82169", 41L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 1L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 1L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("4c46ee76fd9e947e3789fac348216ab98c2421268fafaf3eb29ec949c0b82169", 40L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 1L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("4c46ee76fd9e947e3789fac348216ab98c2421268fafaf3eb29ec949c0b82169", 41L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 1L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 0L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 0L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 1L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("416f3dce977d8b797e1e476f5ab93619ed2f2b21a49ac93743140cf67a088", 1L));
			add(new Pair<>("61803a7132fbe59e65060ddec6263d674a1da6d238ce0ada140e6799b28559d", 1L));
			add(new Pair<>("d892fc55b234b3b53563cd48f98c26e8e3f7e5ae3523b6eed89b262ecf1c5d5", 0L));
			add(new Pair<>("ba151a55eba734f1c651bbc7e717cc8a7ff5575d6804f137d1cd2161314989f", 2L));
			add(new Pair<>("4c46ee76fd9e947e3789fac348216ab98c2421268fafaf3eb29ec949c0b82169", 14L));
		}}),
		RELAX("2f7e6c079efa69cb3a23dd3b147643c7cb5e5c9129b74af0cab47b04f355a", new ArrayList<>() {{
			add(new Pair<>("927ebbf5c2535fe6b5cef8b8a7e1e7067a39ed21ba547f83fce4472184d80c7", 20L));
			add(new Pair<>("fd2dc4db780294919517306964d65ea078b47b823fda5628be48b6ae61c", 3L));
			add(new Pair<>("e744e61b46a5f749ccaa2bf8132981b8986234359f9a2924f547ec0111e4375", 3L));
			add(new Pair<>("33bc38c6e957419f6a484ffeb4738d1e5cef9d90cf8b287964f68313191d8c", 4L));
			add(new Pair<>("4c9b9b31f115e3cb639c64e83abfde31597d15eb7199d14ccbba6c78221f1f19", 4L));
			add(new Pair<>("b6d6581ee0ec93ca9d5f4afbf6e28f5a9582a896ccccb9e7c17e6419e597e27", 10L));
			add(new Pair<>("e08876a49b1abbad149724be3eae35aa6305c529e384c118ba381a81e2df59e", 3L));
			add(new Pair<>("927ebbf5c2535fe6b5cef8b8a7e1e7067a39ed21ba547f83fce4472184d80c7", 2L));
			add(new Pair<>("e08876a49b1abbad149724be3eae35aa6305c529e384c118ba381a81e2df59e", 4L));
			add(new Pair<>("927ebbf5c2535fe6b5cef8b8a7e1e7067a39ed21ba547f83fce4472184d80c7", 1L));
			add(new Pair<>("e08876a49b1abbad149724be3eae35aa6305c529e384c118ba381a81e2df59e", 3L));
			add(new Pair<>("7e63a0c2d631884e9f7a402ae93813988131c7b0798c15f4c7c475625fe0582d", 4L));
			add(new Pair<>("762c3a6265418977a564fa9376fb5b1a87f9f8b8052c63a2d51817691e4223a", 4L));
			add(new Pair<>("3b7891745686511d539aad92ba3037b025abaddb9e2ee6e59c79977a717a4f9b", 4L));
			add(new Pair<>("81b13b2dcb94dcfc30ec7ce7705e6e38a6457c43eb9f8ae1c43ba524163fa469", 4L));
			add(new Pair<>("2f7e6c079efa69cb3a23dd3b147643c7cb5e5c9129b74af0cab47b04f355a", 3L));
			add(new Pair<>("3b7891745686511d539aad92ba3037b025abaddb9e2ee6e59c79977a717a4f9b", 4L));
			add(new Pair<>("81b13b2dcb94dcfc30ec7ce7705e6e38a6457c43eb9f8ae1c43ba524163fa469", 3L));
			add(new Pair<>("2f7e6c079efa69cb3a23dd3b147643c7cb5e5c9129b74af0cab47b04f355a", 4L));
			add(new Pair<>("3b7891745686511d539aad92ba3037b025abaddb9e2ee6e59c79977a717a4f9b", 4L));
			add(new Pair<>("81b13b2dcb94dcfc30ec7ce7705e6e38a6457c43eb9f8ae1c43ba524163fa469", 3L));
			add(new Pair<>("2f7e6c079efa69cb3a23dd3b147643c7cb5e5c9129b74af0cab47b04f355a", 3L));
			add(new Pair<>("3b7891745686511d539aad92ba3037b025abaddb9e2ee6e59c79977a717a4f9b", 4L));
			add(new Pair<>("81b13b2dcb94dcfc30ec7ce7705e6e38a6457c43eb9f8ae1c43ba524163fa469", 3L));
			add(new Pair<>("2f7e6c079efa69cb3a23dd3b147643c7cb5e5c9129b74af0cab47b04f355a", 42L));
		}}),
		SPICY("963ba9f88fa5f5359d5cd94e9df8ceebfdcd4355d5bfb5fe673251ede0e7f63d", new ArrayList<>() {{
			add(new Pair<>("927ebbf5c2535fe6b5cef8b8a7e1e7067a39ed21ba547f83fce4472184d80c7", 19L));
			add(new Pair<>("584f9ee685ec654ea5941d789838785d3214e236153fa2e4822876bcfef89", 8L));
			add(new Pair<>("e34dad6c9eab0baf9f9d9b0f6be6e19936b3a1e20fc3e217b885eadeb318", 4L));
			add(new Pair<>("963ba9f88fa5f5359d5cd94e9df8ceebfdcd4355d5bfb5fe673251ede0e7f63d", 30L));
			add(new Pair<>("8e58ac9911456110377799cdfd75a5f4fd731a38bd29ab39887943aaa139", 3L));
			add(new Pair<>("21f96bfc905c4689698c09cd2cbb818825146c1bc618494033e8077cb9a70", 4L));
			add(new Pair<>("1e5675557dedeeedd4599fd0c0b2eae7d8defbfc613f321f510c74c662a3a6", 3L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 5L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 1L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 5L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 2L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 4L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 2L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 4L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 2L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 4L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 2L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 5L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 1L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 5L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 1L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 4L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 2L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 5L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 1L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 5L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 1L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 5L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 2L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 4L));
			add(new Pair<>("8811ec3618dfc075818af8a7fda52ee56c0bb203e4d27821e1786f2af55b9d", 2L));
			add(new Pair<>("49f0482ca6ba599fbed46a1e7bb332459ac1321bb5cc2dc9bf2a2ba7f61b8", 1L));
		}}),
		SUNTAN("803889e44b55465abff5cedc5b86d3bda346bc70c9bf8b97fcf793948f379c1", new ArrayList<>() {{
			add(new Pair<>("2d2175ebe9ae0e1a658d9af82dacfb8369052d8121d4ea3886738a1cca5", 20L));
			add(new Pair<>("e38adde3aa4df2cf8a5216643d3f6f92133e1599d50b2ef41cefc81f1eec17c", 10L));
			add(new Pair<>("285c789b1bafeb6274d5c3314e0333ccf6ab92d7312ef214f89793c959d25", 3L));
			add(new Pair<>("7ef575629a2689d63a3a3e91bd342ec3f78b4f397687c0833bf6d64bf26d12e8", 16L));
			add(new Pair<>("56f0c6a6f5525d0876773768e2a7f6bd43608d8b15f0e8780f64d512f20", 10L));
			add(new Pair<>("f03ec621ef23175ee4eecc5d1d9222b9e27ca8758bede504e3fa3c0ac1532bd", 10L));
			add(new Pair<>("778ab3781dfe96e519b174e47efdbc6881715d7ae8ca41a1d8ae620f4779d2", 9L));
			add(new Pair<>("8ddda3df9fde30208cad1a308634c95f95c9b48a427842adc9c8ccfec626b4f", 15L));
			add(new Pair<>("7365c666e7d9804397e76de355ee2e68d4c969b5ff7c0ab6af77bd7c7e266", 4L));
			add(new Pair<>("803889e44b55465abff5cedc5b86d3bda346bc70c9bf8b97fcf793948f379c1", 9L));
			add(new Pair<>("15e1122c837a87ce18d51e797983da6ad384793fdd9f77253763ae9b6d9a1", 0L));
			add(new Pair<>("803889e44b55465abff5cedc5b86d3bda346bc70c9bf8b97fcf793948f379c1", 20L));
			add(new Pair<>("15e1122c837a87ce18d51e797983da6ad384793fdd9f77253763ae9b6d9a1", 0L));
			add(new Pair<>("803889e44b55465abff5cedc5b86d3bda346bc70c9bf8b97fcf793948f379c1", 67L));
		}}),
		MOUSTACHE("fad0b1db756b33c81254d9cdb1c7192c4fe64bddac20def5f42eb25452732", new ArrayList<>() {{
			add(new Pair<>("f37ebc60f0ff80e46d1268328ddec140bfc136eda99dfea5e775ef9385a7cc28", 19L));
			add(new Pair<>("e3cec26843ec8c32947c8dc34f5dad834033c05fed9789bca32317961e584e10", 4L));
			add(new Pair<>("7d7de32d2980b236c145f6109aa029683a46896c822e77b8438e3d9759cf6c", 4L));
			add(new Pair<>("4dddf09da195ce041329b65d6608769fde019a863a8359782432a8c4a41deea", 4L));
			add(new Pair<>("e592299f6c3afb8349dc2a71fc5f1999c41f19b6d89ff290cf6e884184409aa3", 4L));
			add(new Pair<>("6d366287cc39844af142c8735548968c08125c77c784b9daec4950395f9741", 19L));
			add(new Pair<>("d5db883a71e6d6c0bcf30a856e4b6e691286b8626830aa1d178fa7de79e50", 1L));
			add(new Pair<>("6d366287cc39844af142c8735548968c08125c77c784b9daec4950395f9741", 2L));
			add(new Pair<>("d5db883a71e6d6c0bcf30a856e4b6e691286b8626830aa1d178fa7de79e50", 1L));
			add(new Pair<>("6d366287cc39844af142c8735548968c08125c77c784b9daec4950395f9741", 19L));
			add(new Pair<>("e592299f6c3afb8349dc2a71fc5f1999c41f19b6d89ff290cf6e884184409aa3", 5L));
			add(new Pair<>("4dddf09da195ce041329b65d6608769fde019a863a8359782432a8c4a41deea", 4L));
			add(new Pair<>("7d7de32d2980b236c145f6109aa029683a46896c822e77b8438e3d9759cf6c", 3L));
			add(new Pair<>("e3cec26843ec8c32947c8dc34f5dad834033c05fed9789bca32317961e584e10", 22L));
			add(new Pair<>("7d7de32d2980b236c145f6109aa029683a46896c822e77b8438e3d9759cf6c", 2L));
			add(new Pair<>("3f5e8342a644fd9aeda054a04d2b912a662570722889fde79019939cbbbd6ce", 3L));
			add(new Pair<>("2ed380c4527eb27a4d38fffce754afe3f484f4b4c7943474131f685b6a3f69", 4L));
			add(new Pair<>("efbc742ebf66f3edb0e12755ee55bfdbed153f5ec67574e21ed81d29ad5451a", 6L));
			add(new Pair<>("cb7b9873c9d3e2c2b2b61d8e348769dd0fefed893c7bd4fc1336320c188b1c5", 19L));
			add(new Pair<>("31bbb26de348cc65dbf7be4e80d9688f8e5adb91911b9a54dc77517ba731", 1L));
			add(new Pair<>("cb7b9873c9d3e2c2b2b61d8e348769dd0fefed893c7bd4fc1336320c188b1c5", 0L));
			add(new Pair<>("31bbb26de348cc65dbf7be4e80d9688f8e5adb91911b9a54dc77517ba731", 1L));
			add(new Pair<>("cb7b9873c9d3e2c2b2b61d8e348769dd0fefed893c7bd4fc1336320c188b1c5", 23L));
			add(new Pair<>("2ed380c4527eb27a4d38fffce754afe3f484f4b4c7943474131f685b6a3f69", 0L));
			add(new Pair<>("3f5e8342a644fd9aeda054a04d2b912a662570722889fde79019939cbbbd6ce", 5L));
			add(new Pair<>("7d7de32d2980b236c145f6109aa029683a46896c822e77b8438e3d9759cf6c", 5L));
			add(new Pair<>("f37ebc60f0ff80e46d1268328ddec140bfc136eda99dfea5e775ef9385a7cc28", 34L));
			add(new Pair<>("7d7de32d2980b236c145f6109aa029683a46896c822e77b8438e3d9759cf6c", 8L));
			add(new Pair<>("3f5e8342a644fd9aeda054a04d2b912a662570722889fde79019939cbbbd6ce", 7L));
			add(new Pair<>("2ed380c4527eb27a4d38fffce754afe3f484f4b4c7943474131f685b6a3f69", 0L));
			add(new Pair<>("cb7b9873c9d3e2c2b2b61d8e348769dd0fefed893c7bd4fc1336320c188b1c5", 5L));
			add(new Pair<>("38ffee6d80bde08b56b6ab2f6c06b5dd12c5afcd2ea896e86f2e5d6dac75c2d", 3L));
			add(new Pair<>("18685e944cff4b2d672b694313bb6bfa25adafb9605346af543d8be7896be8c", 5L));
			add(new Pair<>("fad0b1db756b33c81254d9cdb1c7192c4fe64bddac20def5f42eb25452732", 3L));
			add(new Pair<>("a07ff4c3986e3d49ef142184077df34158da5f373b05912bbe6387027725b28", 17L));
			add(new Pair<>("c68a9f73ee1a7117c0f23811383f864814e489a37d94641132834296ce1a6f", 1L));
			add(new Pair<>("a07ff4c3986e3d49ef142184077df34158da5f373b05912bbe6387027725b28", 1L));
			add(new Pair<>("c68a9f73ee1a7117c0f23811383f864814e489a37d94641132834296ce1a6f", 1L));
			add(new Pair<>("a07ff4c3986e3d49ef142184077df34158da5f373b05912bbe6387027725b28", 131L));
		}}),
		FACEMELTER("3481a8b6eac6771e47b4d3a171760111356d8e1854989d5f7c9e3306c2427", new ArrayList<>() {{
			add(new Pair<>("4c46ee76fd9e947e3789fac348216ab98c2421268fafaf3eb29ec949c0b82169", 44L));
			add(new Pair<>("906dc0c3ebbf6125d309564ed8ee167f870a28081d322425528a9282342fa4", 18L));
			add(new Pair<>("8eb681b591ce6a296718833e69421ebe369eb17c2449921ccb24968a2da18f1", 21L));
			add(new Pair<>("9e331ddcc958dd64bba992e75c730e2943af37f2df2faf7d4d8ba59f285d73", 19L));
			add(new Pair<>("f03e6a51657965993f28f7ea3d9997c8e07dae489dfb149a85cc28aa85d1399e", 19L));
			add(new Pair<>("3481a8b6eac6771e47b4d3a171760111356d8e1854989d5f7c9e3306c2427", 19L));
			add(new Pair<>("3481a8b6eac6771e47b4d3a171760111356d8e1854989d5f7c9e3306c2427", 20L));
			add(new Pair<>("3481a8b6eac6771e47b4d3a171760111356d8e1854989d5f7c9e3306c2427", 136L));
		}}),
		PRESENT("f35783bef598cbefed2e3b3b9ba46d484accce88487d64aa3c4d7cfc8812e6", new ArrayList<>() {{
			add(new Pair<>("505eb2793bae1450e09460b1dbafc06d94fcd82b7ba6baa33d043b2b9cf899", 39L));
			add(new Pair<>("8b1e62f9ca3101ba6b799fb79b3ac39da672821d3ffecb9b5c7215ee48e072", 1L));
			add(new Pair<>("507511bd3d3d8e374dc674a2cf14693894919966416de2f8c5d41a6d16f53dc", 3L));
			add(new Pair<>("a386cfd68f84d078ff1380a8b1acfa79bedd8e816efc622aab421e4c2ac814de", 1L));
			add(new Pair<>("bb89da640dba216f9444b7d45e999ad5de06ac5fea1c07ec7b9383d45b9a097", 1L));
			add(new Pair<>("2b46c78c3c3c2ad51a25ba7ec8a917d117b9e43d6b549e1395bfa55fc44", 2L));
			add(new Pair<>("d128f6c2d9ef6c9ff93793c9a34cb62bdf654778d5b6faec6c68bb52b7d477", 2L));
			add(new Pair<>("391c5c89dd99c41b1844b3b651f97fda913b66184d170e6fb73eb3b108ae77c", 3L));
			add(new Pair<>("ca21eeb52643cfeb26ad63bd517689951cc2941388d1dd9ef151e2811b", 1L));
			add(new Pair<>("418bfdf7b17a7f9a1d1336f15ab0cf5287a5e879819ccf22715f2ae725c6da", 3L));
			add(new Pair<>("f35783bef598cbefed2e3b3b9ba46d484accce88487d64aa3c4d7cfc8812e6", 3L));
			add(new Pair<>("24f439c1cc74a6416688d348111f3ecde65273188a96a01d3f94a018f8baf7d", 1L));
			add(new Pair<>("ce6619f6ca88ad331682096d231c89eb563d25a497130a199acc86c0bca", 1L));
			add(new Pair<>("2354ba0743137eed54b60b91ee54fcf8f4f4d3b2cecc61645f6e1a26715b4", 2L));
			add(new Pair<>("b8b3d323fa9e49127930443aa31e7323a82f209498c0f076259e43378f14188", 2L));
			add(new Pair<>("357f105043347b8cbe1eef4437ca75c937fe2721bc9bbd28c81c834c73a5c", 2L));
			add(new Pair<>("aa917f443134fdc6b55ba3e0fbcb2885bb37c6f7cdff5f29b74bf8a80e7ff", 1L));
			add(new Pair<>("5c5ed03131631f3014fcb1f435c2195bb96bc2a4649637a927e4e4a33ad", 3L));
			add(new Pair<>("b706e4175edbf55669a94687d1fcb91e545a447dd46c0f3f4e199a09a93", 1L));
			add(new Pair<>("5334f1ea2a764aad3566b4bcaee6165527ab3c1e31bb7a2b4c1e5ce4aae72d", 3L));
			add(new Pair<>("1290c68927e8792763ee36e841480a1bad12a71117813e99b25d925ed8183f5", 2L));
			add(new Pair<>("51a4dffd45c28a4a7c2eefc1f0d1f06da0402e7d987e55195177c618b49ef88", 3L));
			add(new Pair<>("f0cdb68e03d45ae4a61abc13d97e7e98afa5e79207cd3a77284cfe202d6d", 3L));
			add(new Pair<>("c2c78220d76813d997cd5213bf29db4ae84e126fcba48c9294211788a15e797", 1L));
			add(new Pair<>("e8ce5a8f0d363818dcb5c31c9a26a1bea96e7782c4a9671f7a48126e5ce05a", 1L));
			add(new Pair<>("9ae438bde33cd93b2285966ee15da63b28a25ac2e4d5fc05eec7d3c3f86ca53", 41L));
			add(new Pair<>("9f3ac078d1cee45eb34a61d24bd432da704542ad339182153b978b7bf79fc1e3", 1L));
			add(new Pair<>("e272e8fbd495c872376428d31c0d3392e22da41c3d28b18f84fa3aede787", 5L));
			add(new Pair<>("9ae438bde33cd93b2285966ee15da63b28a25ac2e4d5fc05eec7d3c3f86ca53", 16L));
		}}),
		;

		private static final String URL = "https://textures.minecraft.net/texture/";

		private final String menuTexture;
		private final List<Pair<String, Long>> frameTextures;
		@Getter
		private List<Pair<ItemStack, Long>> frames;

		EmojiHat(String menuTexture, List<Pair<String, Long>> frames) {
			this.menuTexture = menuTexture;
			this.frameTextures = frames;
			Tasks.async(this::load);
		}

		private void load() {
			this.frames = new ArrayList<>() {{
				for (Pair<String, Long> frame : frameTextures)
					add(new Pair<>(getSkull(frame.getFirst()), frame.getSecond()));
			}};
		}

		public static void init() {
			// static init
		}

		public ItemBuilder getDisplayItem() {
			return new ItemBuilder(getSkull(menuTexture)).name(StringUtils.camelCase(this));
		}

		public boolean canBeUsedBy(Player player) {
			return player.hasPermission(getPermission());
		}

		@NotNull
		public String getPermission() {
			return "emojihats.use." + name().toLowerCase();
		}

		public void run(Player player) {
			start(player, player.getLocation().getNearbyPlayers(100));
		}

		public void runSelf(Player player) {
			start(player, List.of(player));
		}

		private void start(Player player, Collection<Player> receivers) {
			int wait = 0;
			for (Pair<ItemStack, Long> frame : frames) {
				final ItemStack item = frame.getFirst();
				final long ticks = frame.getSecond();

				for (int i = 0; i <= ticks; i++)
					Tasks.wait(wait++, () -> packet(player, receivers, item));
			}

			Tasks.wait(wait + 1, () -> packet(player, receivers, player.getInventory().getItem(EquipmentSlot.HEAD)));
		}

		private void packet(Player player, Collection<Player> receivers, ItemStack item) {
			PacketUtils.sendFakeItem(player, receivers, item, EnumWrappers.ItemSlot.HEAD);
		}

		public List<ItemStack> getFrameItems() {
			return frames.stream().map(Pair::getFirst).distinct().toList();
		}

		private final static Map<String, ItemStack> loadedSkulls = new ConcurrentHashMap<>();

		private ItemStack getSkull(String base64) {
			return loadedSkulls.computeIfAbsent(base64, $ -> new ItemBuilder(Material.PLAYER_HEAD).skullOwnerUrl(URL + base64).build());
		}
	}

}
