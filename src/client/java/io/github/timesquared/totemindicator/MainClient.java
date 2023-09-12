package io.github.timesquared.totemindicator;

import io.github.timecubed.tulip.TulipConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MainClient implements ClientModInitializer {
	public static final MainClient INSTANCE = new MainClient();
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	public static TulipConfigManager tulipInstance;
	public static boolean isConfiguring = false;
	
	@Override
	public void onInitializeClient() {
		tulipInstance = new TulipConfigManager("totem-indicator", false);
		
		tulipInstance.saveProperty("red", 255);
		tulipInstance.saveProperty("green", 100);
		tulipInstance.saveProperty("blue", 75);
		tulipInstance.saveProperty("alpha", 25);
		tulipInstance.saveProperty("disabled", false);
		tulipInstance.saveProperty("draw_totem", true);
		tulipInstance.saveProperty("text", "NO TOTEM");
		tulipInstance.saveProperty("x_position", 0);
		tulipInstance.saveProperty("y_position", 20);
		
		tulipInstance.load();
		
		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("totemindicator").executes(context -> {
			mc.send(() -> mc.setScreen(new ConfigScreen(Text.of("config"), null)));
			return 1;
		}))));
		
		MainServer.LOGGER.info("Initialized mod successfully!");
	}
	
	public void tickRender(DrawContext context) {
		if (mc.player != null) {
			
			ItemStack offhandStack = mc.player.getOffHandStack();
			ItemStack mainHandStack = mc.player.getMainHandStack();
			
			if (!isConfiguring && offhandStack.getItem() != Items.TOTEM_OF_UNDYING && mainHandStack.getItem() != Items.TOTEM_OF_UNDYING && !(tulipInstance.getBoolean("disabled"))) {

				int r = tulipInstance.getInt("red");
				int g = tulipInstance.getInt("green");
				int b = tulipInstance.getInt("blue");
				int a = tulipInstance.getInt("alpha");
				int positionX = tulipInstance.getInt("x_position");
				int positionY = tulipInstance.getInt("y_position");
				boolean drawTotem = tulipInstance.getBoolean("draw_totem");
				String text = tulipInstance.getString("text");
				drawOverlay(context, RGBA(r, g, b, a), drawTotem, text, positionX, positionY);

			}
		}
	}

	public static void drawOverlay(DrawContext context, int color, boolean drawTotem, String text, int positionX, int positionY) {
		context.getMatrices().push();
		context.getMatrices().translate(0 ,0, -9999);
		context.fill(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight(), color);
		if(drawTotem) {
			context.drawItemWithoutEntity(new ItemStack(Items.TOTEM_OF_UNDYING), getXPosition(positionX) - 8, getYPosition(positionY));
		}
		if(!text.isEmpty()) {
			ArrayList<String> textLines = new ArrayList<>(List.of(text.split("\\\\n")));
			int line = 0;
			for(String textLine : textLines) {
				context.drawCenteredTextWithShadow(mc.textRenderer, textLine, getXPosition(positionX), getYPosition(positionY) + 10 + (mc.textRenderer.fontHeight + 1) * (line + 1), (255 << 24) | color);
				line++;
			}
		}
		context.getMatrices().pop();
	}

	public static int getXPosition(float position) {
		return (int) (mc.getWindow().getScaledWidth() / 2 + (position / 100) * (mc.getWindow().getScaledWidth() / 2));
	}
	public static int getYPosition(float position) {
		return (int) (mc.getWindow().getScaledHeight() / 2 + (position / 100) * (mc.getWindow().getScaledHeight() / 2));
	}

	public static int RGBA(int r, int g, int b, int a) {
		// For some reason fill() doesn't properly set the color if some channel is 0 sometimes
		// So force at least 1 in each color channel
		r = Math.max(r, 1); g = Math.max(g, 1); b = Math.max(b, 1);
		return (a << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
	}
}