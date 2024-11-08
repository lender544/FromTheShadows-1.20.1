package net.sonmok14.fromtheshadows.client.renderer.items;

import net.sonmok14.fromtheshadows.client.models.items.DiaboliumArmorModel;
import net.sonmok14.fromtheshadows.server.items.DiaboliumArmorItem;
import software.bernie.example.client.renderer.armor.GeckoArmorRenderer;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DiaboliumArmorRenderer extends GeoArmorRenderer<DiaboliumArmorItem> {
	public DiaboliumArmorRenderer() {
		super(new DiaboliumArmorModel());
	}



}
