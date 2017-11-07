package ru.justagod.justacore.gui.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * Created by JustAGod on 03.11.17.
 */
public final class ServerHelper {

    public static boolean isPlayerOnline(EntityPlayer player) {
        for (Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (o == player) return true;
        }

        return false;
    }

    public static List<EntityPlayer> getOnlinePlayers() {
        return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    }
}
