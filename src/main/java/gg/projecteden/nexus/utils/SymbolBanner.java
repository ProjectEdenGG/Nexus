package gg.projecteden.nexus.utils;

import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

@Getter
public class SymbolBanner {

	public enum Symbol {
		A {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		B {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.CURLY_BORDER)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		C {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		D {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.CURLY_BORDER)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		E {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		F {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(baseDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		G {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		H {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
						.pattern(baseDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		I {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_CENTER)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		J {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		K {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_DOWNRIGHT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		L {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		M {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.TRIANGLE_TOP)
						.pattern(baseDye, PatternType.TRIANGLES_TOP)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		N {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.TRIANGLE_TOP)
						.pattern(patternDye, PatternType.STRIPE_DOWNRIGHT)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		O {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		P {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		Q {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
						.pattern(baseDye, PatternType.RHOMBUS)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.SQUARE_BOTTOM_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		R {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_DOWNRIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		S {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
						.pattern(baseDye, PatternType.RHOMBUS)
						.pattern(baseDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_DOWNRIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		T {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_CENTER)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		U {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		V {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.TRIANGLE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		W {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.TRIANGLE_BOTTOM)
						.pattern(baseDye, PatternType.TRIANGLES_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		X {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.CROSS)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		Y {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_DOWNRIGHT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		Z {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_0 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_1 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_CENTER)
						.pattern(patternDye, PatternType.SQUARE_TOP_LEFT)
						.pattern(baseDye, PatternType.CURLY_BORDER)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_2 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.RHOMBUS)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_3 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.CURLY_BORDER)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_4 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_5 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(baseDye, PatternType.RHOMBUS)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_DOWNRIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_6 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_7 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_DOWNLEFT)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_8 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(baseDye, PatternType.BORDER);
			}
		},

		_9 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, PatternType.STRIPE_LEFT)
						.pattern(baseDye, PatternType.HALF_HORIZONTAL)
						.pattern(patternDye, PatternType.STRIPE_MIDDLE)
						.pattern(patternDye, PatternType.STRIPE_TOP)
						.pattern(patternDye, PatternType.STRIPE_RIGHT)
						.pattern(patternDye, PatternType.STRIPE_BOTTOM)
						.pattern(baseDye, PatternType.BORDER);
			}
		};

		public static Symbol of(char character) {
			String input = String.valueOf(character);
			if (input.matches("[A-Za-z]"))
				return Symbol.valueOf(input.toUpperCase());
			if (input.matches("[\\d]"))
				return Symbol.valueOf("_" + input);

			return null;
		}

		abstract public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye);
	}
}
