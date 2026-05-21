package dev.minetomek.namechanger.mixin;

import dev.minetomek.namechanger.NameChanger;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar {
	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	void namechanger$returnCustomNameIfAvailable(CallbackInfoReturnable<Component> cir) {
		Component component = this.getCustomName();

		if(component != null) {
			cir.setReturnValue(namechanger$removeClickEvents(component));
		}
	}

	@Inject(method = "getPlainTextName", at = @At("HEAD"), cancellable = true)
	void namechanger$returnPlainCustomNameIfAvailable(CallbackInfoReturnable<String> cir) {
		Component component = this.getCustomName();

		if(component != null) {
			cir.setReturnValue(component.getString());
		}
	}

	// This function literally uses `getName()` normally, but it still somehow fixes NeoForge working
	@Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
	void namechanger$returnDisplayCustomNameIfAvailable(CallbackInfoReturnable<Component> cir) {
		Component component = this.getCustomName();

		if(component != null) {
			MutableComponent result = PlayerTeam.formatNameForTeam(this.getTeam(), component);

			cir.setReturnValue(decorateDisplayNameComponent(result));
		}
	}

	@Overwrite
	private MutableComponent decorateDisplayNameComponent(MutableComponent nameComponent) {
		String name = this.getName().getString();

		return nameComponent.withStyle((s) ->
				s.withClickEvent(new ClickEvent.SuggestCommand("/tell " + name + " "))
						.withHoverEvent(this.createHoverEvent())
						.withInsertion(name));
	}

	@Overwrite
	public @NonNull String getScoreboardName() {
		return getName().getString();
	}

	@Unique
	private static Component namechanger$removeClickEvents(Component component) {
		MutableComponent mutableComponent = component.plainCopy().setStyle(component.getStyle().withClickEvent(null));

		for (Component sibling : component.getSiblings()) {
			mutableComponent.append(namechanger$removeClickEvents(sibling));
		}

		return mutableComponent;
	}
}
