package ru.justagod.justacore.helper;

/**
 * Created by JustAGod on 01.11.17.
 */
public enum ChatColor {
    RED("§4"),
    LIGHT_RED("§c"),
    YELLOW("§6"),
    LIGHT_YELLOW("§e"),
    GREEN("§2"),
    LIGHT_GREEN("§a"),
    AQUA("§3"),
    LIGHT_AQUA("§b"),
    BLUE("§1"),
    LIGHT_BLUE("§9"),
    PURPLE("§5"),
    LIGHT_PURPLE("§d"),
    WHITE("§f"),
    GRAY("§7"),
    DARK_GRAY("§8"),
    BLACK("§0");



    public String code;

    ChatColor(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }



}
