package dev.minetomek.namechanger;

import dev.minetomek.namechanger.command.NameChangeCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

public class NameChanger implements ModInitializer {
//    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ServerPlayerEvents.COPY_FROM.register(NameChanger::handleCopyFromEvent);

        ServerPlayerEvents.JOIN.register(NameChanger::updateServerMotd);

        CommandRegistrationCallback.EVENT.register(NameChangeCommand::register);
    }

    private static void handleCopyFromEvent(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        newPlayer.setCustomName(oldPlayer.getCustomName());
    }

    public static void updateServerMotd(@NonNull ServerPlayer serverPlayer) {
        ServerLevel level = serverPlayer.level();
        MinecraftServer server = level.getServer();

        if (!server.isSingleplayer()) return;

        String levelName = server.getWorldData().getLevelName();

        server.setMotd(createMotd(serverPlayer.getName().getString(), levelName));
    }

    public static @NonNull String createMotd(String hostName, String levelName) {
        return "%s - %s".formatted(hostName, levelName);
    }
}