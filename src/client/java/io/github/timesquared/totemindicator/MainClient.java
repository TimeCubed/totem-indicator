package io.github.timesquared.totemindicator;

import io.github.timecubed.tulip.TulipConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class MainClient implements ClientModInitializer {
	public static final MainClient INSTANCE = new MainClient();
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	public static TulipConfigManager tulipInstance;
	
	@Override
	public void onInitializeClient() {
		tulipInstance = new TulipConfigManager("totem-indicator", false);
		
		tulipInstance.saveProperty("red", 255);
		tulipInstance.saveProperty("green", 100);
		tulipInstance.saveProperty("blue", 75);
		tulipInstance.saveProperty("alpha", 25);
		tulipInstance.saveProperty("disabled", false);
		
		tulipInstance.load();
		
		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("totemindicator").executes(context -> {
			mc.send(() -> mc.setScreen(new ConfigScreen(Text.of("config"))));
			return 1;
		}))));
		
		MainServer.LOGGER.info("Initialized mod successfully!");
	}
	
	public void tickRender(MatrixStack matrices) {
		if (mc.player != null) {
			int r = tulipInstance.getInt("red");
			int g = tulipInstance.getInt("green");
			int b = tulipInstance.getInt("blue");
			int a = tulipInstance.getInt("alpha");
			
			ItemStack offhandStack = mc.player.getOffHandStack();
			ItemStack mainHandStack = mc.player.getMainHandStack();
			
			if (offhandStack.getItem() != Items.TOTEM_OF_UNDYING && mainHandStack.getItem() != Items.TOTEM_OF_UNDYING && !(tulipInstance.getBoolean("disabled"))) {
				DrawableHelper.fill(matrices, 0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), RGBA(r, g, b, a));
			}
		}
	}
	
	public static int RGBA(int r, int g, int b, int a) {
		return (a << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
	}
}