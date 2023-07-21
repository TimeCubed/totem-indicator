package io.github.timesquared.totemindicator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	
	protected ConfigScreen(Text title) {
		super(title);
	}
	
	@Override
	protected void init() {
		TextFieldWidget redText = new TextFieldWidget(mc.textRenderer, this.width / 2 - 30, this.height / 2 - 50 - 5, 100, 20, Text.of("intfield"));
		TextFieldWidget greenText = new TextFieldWidget(mc.textRenderer, this.width / 2 - 30, this.height / 2 - 25 - 5, 100, 20, Text.of("intfield"));
		TextFieldWidget blueText = new TextFieldWidget(mc.textRenderer, this.width / 2 - 30, this.height / 2 - 5, 100, 20, Text.of("intfield"));
		TextFieldWidget alphaText = new TextFieldWidget(mc.textRenderer, this.width / 2 - 30, this.height / 2 + 25 - 5, 100, 20, Text.of("intfield"));
		
		this.addDrawableChild(redText);
		this.addDrawableChild(greenText);
		this.addDrawableChild(blueText);
		this.addDrawableChild(alphaText);

		ButtonWidget disable = ButtonWidget.builder(Text.of("Disabled: " + (MainClient.tulipInstance.getBoolean("disabled") ? "Yes" : "No")), button -> {
			MainClient.tulipInstance.saveProperty("disabled", !(MainClient.tulipInstance.getBoolean("disabled")));
			MainClient.tulipInstance.save();
			button.setMessage(Text.of("Disabled: " + (MainClient.tulipInstance.getBoolean("disabled") ? "Yes" : "No")));
		}).dimensions(this.width / 2 - 75, this.height / 2 - 75, 150, 20).build();

		this.addDrawableChild(disable);
		
		ButtonWidget done = ButtonWidget.builder(Text.of("Done"), button -> {
			String red = redText.getText(), green = greenText.getText(), blue = blueText.getText(), alpha = alphaText.getText();
			int r, g, b, a;
			
			try {
				r = Integer.parseInt(red);
				g = Integer.parseInt(green);
				b = Integer.parseInt(blue);
				a = Integer.parseInt(alpha);
			} catch (NumberFormatException numberFormatException) {
				mc.setScreen(null);
				mc.player.sendMessage(Text.of("The red, green, blue and alpha values you entered were not integer values!"));
				return;
			}
			
			if (!(r >= 0 && r <= 255) || !(g >= 0 && g <= 255) || !(b >= 0 && g <= 255) || !(a >= 0 && a <= 255)) {
				mc.setScreen(null);
				mc.player.sendMessage(Text.of("The red, green, blue and alpha values must be between 0 and 255!"));
				return;
			}
			
			MainClient.tulipInstance.saveProperty("red", r);
			MainClient.tulipInstance.saveProperty("green", g);
			MainClient.tulipInstance.saveProperty("blue", b);
			MainClient.tulipInstance.saveProperty("alpha", a);
			
			MainClient.tulipInstance.save();

			mc.setScreen(null);
		}).dimensions(this.width / 2 - 75, (int) (this.height / 1.1), 150, 20).build();
		this.addDrawableChild(done);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(matrices);

		this.textRenderer.drawWithShadow(matrices, "Red: ", (float) (this.width / 2 - mc.textRenderer.getWidth("Red: ") - 50), (float) (this.height / 2 + 4.5 - 50), 0xFFFFFF);
		this.textRenderer.drawWithShadow(matrices, "Blue: ", (float) (this.width / 2 - mc.textRenderer.getWidth("Blue: ") - 50), (float) (this.height / 2 + 4.5 - 25), 0xFFFFFF);
		this.textRenderer.drawWithShadow(matrices, "Green: ", (float) (this.width / 2 - mc.textRenderer.getWidth("Green: ") - 50), (float) (this.height / 2 + 4.5), 0xFFFFFF);
		this.textRenderer.drawWithShadow(matrices, "Alpha: ", (float) (this.width / 2 - mc.textRenderer.getWidth("Alpha: ") - 50), (float) (this.height / 2 + 4.5 + 25), 0xFFFFFF);
		
		super.render(matrices, mouseX, mouseY, delta);
	}
}
