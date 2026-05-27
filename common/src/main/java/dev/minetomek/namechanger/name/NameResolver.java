package dev.minetomek.namechanger.name;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public final class NameResolver {
    public static Optional<ServerPlayer> resolvePlayer(final String name, List<ServerPlayer> players) {
        // Match custom name first
        for (ServerPlayer serverPlayer : players) {
            if (serverPlayer.getName().getString().equalsIgnoreCase(name)) {
                return Optional.of(serverPlayer);
            }
        }

        // Then try finding by original name
        for (ServerPlayer serverPlayer : players) {
            if (serverPlayer.getGameProfile().name().equalsIgnoreCase(name)) {
                return Optional.of(serverPlayer);
            }
        }

        return Optional.empty();
    }
}
