package io.github.timesquared.totemindicator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ConfigScreen extends Screen {
	public static final MinecraftClient mc = MinecraftClient.getInstance();

	Screen parentScreen;
	SettingsSlider redSlider;
	SettingsSlider greenSlider;
	SettingsSlider blueSlider;
	SettingsSlider alphaSlider;
	TextFieldWidget textWidget;
	TextFieldWidget positionXTextField;
	TextFieldWidget positionYTextField;
	ButtonWidget disableButton;
	ButtonWidget drawTotemButton;
	ButtonWidget hideUIButton;
	boolean isDisabled;
	boolean drawTotem;

	boolean hideUI = false;

	protected ConfigScreen(Text title, Screen parentScreen) {
		super(title);
		this.parentScreen = parentScreen;
	}
	
	@Override
	protected void init() {
		int rSet = MainClient.tulipInstance.getInt("red");
		int gSet = MainClient.tulipInstance.getInt("green");
		int bSet = MainClient.tulipInstance.getInt("blue");
		int aSet = MainClient.tulipInstance.getInt("alpha");
		isDisabled = MainClient.tulipInstance.getBoolean("disabled");
		drawTotem = MainClient.tulipInstance.getBoolean("draw_totem");
		String textSet = MainClient.tulipInstance.getString("text");
		int positionXSet = MainClient.tulipInstance.getInt("x_position");
		int positionYSet = MainClient.tulipInstance.getInt("y_position");

		redSlider = new SettingsSlider(this.width / 2 - 30 - 90, this.height / 2 - 50 - 5, 100, 20, rSet, 0, 255);
		greenSlider = new SettingsSlider(this.width / 2 - 30 - 90, this.height / 2 - 25 - 5, 100, 20, gSet, 0, 255);
		blueSlider = new SettingsSlider(this.width / 2 - 30 - 90, this.height / 2 - 5, 100, 20, bSet, 0, 255);
		alphaSlider = new SettingsSlider(this.width / 2 - 30 - 90, this.height / 2 + 25 - 5, 100, 20, aSet, 0, 255);
		positionXTextField = new TextFieldWidget(mc.textRenderer, this.width / 2 - 30 + 90, this.height / 2 - 25 - 5, 100, 20, Text.empty());
		positionYTextField = new TextFieldWidget(mc.textRenderer, this.width / 2 - 30 + 90, this.height / 2 - 5, 100, 20, Text.empty());

		positionXTextField.setText(String.valueOf(positionXSet));
		positionYTextField.setText(String.valueOf(positionYSet));

		this.addDrawableChild(redSlider);
		this.addDrawableChild(greenSlider);
		this.addDrawableChild(blueSlider);
		this.addDrawableChild(alphaSlider);
		this.addDrawableChild(positionXTextField);
		this.addDrawableChild(positionYTextField);

		disableButton = ButtonWidget.builder(Text.of("Disabled: " + (isDisabled ? "Yes" : "No")), button -> {
			isDisabled = !isDisabled;
			button.setMessage(Text.of("Disabled: " + (isDisabled ? "Yes" : "No")));
		}).dimensions(this.width / 2 - 75 - 90, this.height / 2 - 80, 150, 20).build();
		this.addDrawableChild(disableButton);

		drawTotemButton = ButtonWidget.builder(Text.of("Draw totem: " + (drawTotem ? "Yes" : "No")), button -> {
			drawTotem = !drawTotem;
			button.setMessage(Text.of("Draw totem: " + (drawTotem ? "Yes" : "No")));
		}).dimensions(this.width / 2 - 75 + 90, this.height / 2 - 80, 150, 20).build();
		this.addDrawableChild(drawTotemButton);

		textWidget = new TextFieldWidget(mc.textRenderer, this.width / 2 - 30 + 90, this.height / 2 - 50 - 5, 100, 20, Text.of("stringfield"));
		textWidget.setMaxLength(1024);
		textWidget.setText(String.valueOf(textSet));
		this.addDrawableChild(textWidget);

		hideUIButton = ButtonWidget.builder(Text.of(hideUI ? "Show UI" : "Hide UI"), button -> {
			hideUI = !hideUI;

			redSlider.visible = !hideUI;
			greenSlider.visible = !hideUI;
			blueSlider.visible = !hideUI;
			alphaSlider.visible = !hideUI;
			disableButton.visible = !hideUI;
			drawTotemButton.visible = !hideUI;
			positionXTextField.visible = !hideUI;
			positionYTextField.visible = !hideUI;
			textWidget.visible = !hideUI;

			button.setMessage(Text.of(hideUI ? "Show UI" : "Hide UI"));
		}).dimensions(this.width / 2 - 75, (int) (this.height / 1.1 - 25), 150, 20).build();
		this.addDrawableChild(hideUIButton);

		ButtonWidget done = ButtonWidget.builder(Text.of("Done"), button -> {
			int r = (int) redSlider.getScaledValue(), g = (int) greenSlider.getScaledValue(), b = (int) blueSlider.getScaledValue(), a = (int) alphaSlider.getScaledValue();
			String textToSet = textWidget.getText(), positionX = positionXTextField.getText(), positionY = positionYTextField.getText();
			int positionYToSet, positionXToSet;

			try {
				positionXToSet = Integer.parseInt(positionX);
				positionYToSet = Integer.parseInt(positionY);
			} catch (NumberFormatException numberFormatException) {
				if(mc.player != null) mc.player.sendMessage(Text.of("§cThe position value you entered was not an integer!"));
				this.close();
				return;
			}

			MainClient.tulipInstance.saveProperty("red", r);
			MainClient.tulipInstance.saveProperty("green", g);
			MainClient.tulipInstance.saveProperty("blue", b);
			MainClient.tulipInstance.saveProperty("alpha", a);
			MainClient.tulipInstance.saveProperty("x_position", positionXToSet);
			MainClient.tulipInstance.saveProperty("y_position", positionYToSet);
			MainClient.tulipInstance.saveProperty("text", textToSet);
			MainClient.tulipInstance.saveProperty("disabled", isDisabled);
			MainClient.tulipInstance.saveProperty("draw_totem", drawTotem);

			MainClient.tulipInstance.save();

			this.close();
		}).dimensions(this.width / 2 - 75, (int) (this.height / 1.1), 150, 20).build();
		this.addDrawableChild(done);
	}

	@Override
	public void close() {
		MainClient.isConfiguring = false;
		mc.setScreen(parentScreen);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		MainClient.isConfiguring = true;

		if(mc.player == null) this.renderBackgroundTexture(context);

		if(!hideUI) {
			context.drawTextWithShadow(this.textRenderer, "Red: ", (this.width / 2 - mc.textRenderer.getWidth("Red: ") - 35 - 90), (int) (this.height / 2 + 4.5 - 50), 0xFF0000);
			context.drawTextWithShadow(this.textRenderer, "Green: ", (this.width / 2 - mc.textRenderer.getWidth("Green: ") - 35 - 90), (int) (this.height / 2 + 4.5 - 25), 0x00FF00);
			context.drawTextWithShadow(this.textRenderer, "Blue: ", (this.width / 2 - mc.textRenderer.getWidth("Blue: ") - 35 - 90), (int) (this.height / 2 + 4.5), 0x0000FF);
			context.drawTextWithShadow(this.textRenderer, "Alpha: ", (this.width / 2 - mc.textRenderer.getWidth("Alpha: ") - 35 - 90), (int) (this.height / 2 + 4.5 + 25), 0xA0A0A0);
			context.drawTextWithShadow(this.textRenderer, "Text: ", (this.width / 2 - mc.textRenderer.getWidth("Text: ") - 35 + 90), (int) (this.height / 2 + 4.5 - 50), 0xFFFFFF);
			context.drawTextWithShadow(this.textRenderer, "X pos.: ", (this.width / 2 - mc.textRenderer.getWidth("X pos.: ") - 35 + 90), (int) (this.height / 2 + 4.5 - 25), 0xFFFFFF);
			context.drawTextWithShadow(this.textRenderer, "Y pos.: ", (this.width / 2 - mc.textRenderer.getWidth("Y pos.: ") - 35 + 90), (int) (this.height / 2 + 4.5), 0xFFFFFF);
		}

		int r = (int) redSlider.getScaledValue(), g = (int) greenSlider.getScaledValue(), b = (int) blueSlider.getScaledValue(), a = (int) alphaSlider.getScaledValue();
		boolean validValues = false;
		int positionX = 0, positionY = 0;
		try {
			positionX = Integer.parseInt(positionXTextField.getText());
			positionY = Integer.parseInt(positionYTextField.getText());
			validValues = true;
		} catch(NumberFormatException ignored) {}

		if(!isDisabled) {
			MainClient.drawOverlay(context, MainClient.RGBA(r, g, b, a), drawTotem, textWidget.getText(), positionX, positionY);
		}
		if(!validValues) {
			context.drawCenteredTextWithShadow(this.textRenderer, "Invalid position value(s)!", this.width / 2, (int) (this.height / 2 + 4.5 - 100), 0xFF1000);
		}

		super.render(context, mouseX, mouseY, delta);
	}

	static class SettingsSlider extends SliderWidget {
		private final double min;
		private final double max;
		private double scaledValue;

		public SettingsSlider(int x, int y, int width, int height, int value, float min, float max) {
			super(x, y, width, height, ScreenTexts.EMPTY, value);
			this.min = min;
			this.max = max;
			this.value = (MathHelper.clamp((float)value, min, max) - min) / (max - min);
			this.applyValue();
		}

		@Override
		protected void updateMessage() {}

		@Override
		protected void applyValue() {
			scaledValue = MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
		}

		public double getScaledValue() { return scaledValue; }
	}

}
