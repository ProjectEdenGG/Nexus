package gg.projecteden.nexus.features.mcmmo.mastery.menu;

import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;

public final class MenuText {
    private static final NamespacedKey TITLE = NamespacedKey.minecraft("mcmmo/title");
    private static final NamespacedKey SPACING = NamespacedKey.minecraft("mcmmo/spacing");
    private static final int UNITS_PER_PIXEL = 16;
    private static final int TITLE_ORIGIN_X = 8;

    // Unkerned Upheaval TT advances in GUI pixels at the title font size of 20.
    private static final Map<Character, Float> TITLE_ADVANCES = Map.ofEntries(
        Map.entry(' ', 7.0f),
        Map.entry('!', 5.0f),
        Map.entry('"', 8.0f),
        Map.entry('#', 12.0f),
        Map.entry('$', 12.0f),
        Map.entry('%', 13.0f),
        Map.entry('&', 12.0f),
        Map.entry('\'', 4.0f),
        Map.entry('(', 8.0f),
        Map.entry(')', 8.0f),
        Map.entry('*', 10.0f),
        Map.entry('+', 12.0f),
        Map.entry(',', 4.0f),
        Map.entry('-', 12.0f),
        Map.entry('.', 4.0f),
        Map.entry('/', 13.0f),
        Map.entry('0', 12.0f),
        Map.entry('1', 5.0f),
        Map.entry('2', 12.0f),
        Map.entry('3', 12.0f),
        Map.entry('4', 12.0f),
        Map.entry('5', 12.0f),
        Map.entry('6', 12.0f),
        Map.entry('7', 12.0f),
        Map.entry('8', 12.0f),
        Map.entry('9', 12.0f),
        Map.entry(':', 4.0f),
        Map.entry(';', 4.0f),
        Map.entry('<', 9.0f),
        Map.entry('=', 12.0f),
        Map.entry('>', 9.0f),
        Map.entry('?', 12.0f),
        Map.entry('@', 14.0f),
        Map.entry('A', 12.0f),
        Map.entry('B', 12.0f),
        Map.entry('C', 12.0f),
        Map.entry('D', 12.0f),
        Map.entry('E', 12.0f),
        Map.entry('F', 12.0f),
        Map.entry('G', 12.0f),
        Map.entry('H', 12.0f),
        Map.entry('I', 11.0f),
        Map.entry('J', 12.0f),
        Map.entry('K', 13.0f),
        Map.entry('L', 12.0f),
        Map.entry('M', 14.0f),
        Map.entry('N', 12.0f),
        Map.entry('O', 12.0f),
        Map.entry('P', 12.0f),
        Map.entry('Q', 12.0f),
        Map.entry('R', 12.0f),
        Map.entry('S', 12.0f),
        Map.entry('T', 11.0f),
        Map.entry('U', 12.0f),
        Map.entry('V', 12.0f),
        Map.entry('W', 14.0f),
        Map.entry('X', 12.0f),
        Map.entry('Y', 13.0f),
        Map.entry('Z', 12.0f),
        Map.entry('[', 8.0f),
        Map.entry('\\', 13.0f),
        Map.entry(']', 8.0f),
        Map.entry('^', 10.0f),
        Map.entry('_', 12.0f),
        Map.entry('`', 4.0f),
        Map.entry('a', 12.0f),
        Map.entry('b', 12.0f),
        Map.entry('c', 12.0f),
        Map.entry('d', 12.0f),
        Map.entry('e', 12.0f),
        Map.entry('f', 12.0f),
        Map.entry('g', 12.0f),
        Map.entry('h', 12.0f),
        Map.entry('i', 11.0f),
        Map.entry('j', 12.0f),
        Map.entry('k', 13.0f),
        Map.entry('l', 12.0f),
        Map.entry('m', 14.0f),
        Map.entry('n', 12.0f),
        Map.entry('o', 12.0f),
        Map.entry('p', 12.0f),
        Map.entry('q', 12.0f),
        Map.entry('r', 12.0f),
        Map.entry('s', 12.0f),
        Map.entry('t', 11.0f),
        Map.entry('u', 12.0f),
        Map.entry('v', 12.0f),
        Map.entry('w', 14.0f),
        Map.entry('x', 12.0f),
        Map.entry('y', 13.0f),
        Map.entry('z', 12.0f),
        Map.entry('{', 9.0f),
        Map.entry('|', 5.0f),
        Map.entry('}', 9.0f),
        Map.entry('~', 15.0f)
    );

    public static Component centered(String text, TextColor color, float centerX) {
        int widthUnits = Math.round(width(text) * UNITS_PER_PIXEL);
        int start = Math.round((centerX - TITLE_ORIGIN_X) * UNITS_PER_PIXEL - widthUnits / 2f);
        return Component.empty()
            .append(space(start))
            .append(Component.text(text, color).font(TITLE))
            .append(space(-start - widthUnits));
    }

    public static float width(String text) {
        return (float) text.codePoints().mapToDouble(MenuText::advance).sum();
    }

    private static float advance(int codePoint) {
        Float width = Character.isBmpCodePoint(codePoint) ? TITLE_ADVANCES.get((char) codePoint) : null;
        if (width == null)
            throw new IllegalArgumentException("Unsupported glyph U+" + Integer.toHexString(codePoint));

        return width;
    }

    public static Component space(int units) {
        int magnitude = Math.abs(units);
        if (magnitude >= (1 << 15))
            throw new IllegalArgumentException("Spacing exceeds the font's supported range");

        StringBuilder glyphs = new StringBuilder();
        int base = units < 0 ? 0xe010 : 0xe000;
        for (int bit = 0; magnitude != 0; ++bit, magnitude >>>= 1) {
            if ((magnitude & 1) != 0)
                glyphs.append((char) (base + bit));
        }
        return Component.text(glyphs.toString()).font(SPACING);
    }
}
