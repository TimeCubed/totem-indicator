package io.github.timesquared.totemindicator.mixin.client;

import io.github.timesquared.totemindicator.MainClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHUD {
	@Inject(method = "render", at = @At("HEAD"))
	public void onTick(DrawContext context, float tickDelta, CallbackInfo ci) {
		MainClient.INSTANCE.tickRender(context);
	}
}
