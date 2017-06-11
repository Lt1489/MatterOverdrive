/*
 * This file is part of Matter Overdrive
 * Copyright (c) 2015., Simeon Radivoev, All rights reserved.
 *
 * Matter Overdrive is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Matter Overdrive is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Matter Overdrive.  If not, see <http://www.gnu.org/licenses>.
 */

package matteroverdrive.gui.pages.starmap;

import matteroverdrive.Reference;
import matteroverdrive.client.data.Color;
import matteroverdrive.container.ContainerStarMap;
import matteroverdrive.container.slot.MOSlot;
import matteroverdrive.gui.MOGuiBase;
import matteroverdrive.gui.element.ElementBaseGroup;
import matteroverdrive.gui.element.ElementInventorySlot;
import matteroverdrive.gui.element.starmap.ElementSlotBuilding;
import matteroverdrive.gui.element.starmap.ElementSlotShip;
import matteroverdrive.proxy.ClientProxy;
import matteroverdrive.starmap.data.Planet;
import matteroverdrive.tile.TileEntityMachineStarMap;
import matteroverdrive.util.StarmapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Created by Simeon on 6/15/2015.
 */
public class PagePlanetMenu extends ElementBaseGroup
{

	TileEntityMachineStarMap starMap;
	ElementInventorySlot[] factorySlots;
	ElementInventorySlot[] shipSlots;

	public PagePlanetMenu(MOGuiBase gui, int posX, int posY, int width, int height, ContainerStarMap starMapContainer, TileEntityMachineStarMap starMap)
	{
		super(gui, posX, posY, width, height);
		//this.starMap = starMap;
		factorySlots = new ElementInventorySlot[Planet.SLOT_COUNT / 2];
		shipSlots = new ElementInventorySlot[Planet.SLOT_COUNT / 2];
		this.starMap = starMap;

		for (int i = 0; i < factorySlots.length; i++)
		{
			double angle = (-Math.PI / 1.8) + (Math.PI / 15) * i;
			factorySlots[i] = new ElementSlotBuilding(gui, (MOSlot)starMapContainer.getSlot(i), width / 2 - 10 + (int)(Math.sin(angle) * 140), height / 2 - 48 + (int)(Math.cos(angle) * 140), 22, 22, "holo", ClientProxy.holoIcons.getIcon("factory"), starMap);
			factorySlots[i].setColor(new Color(Reference.COLOR_HOLO.getIntR() / 2, Reference.COLOR_HOLO.getIntG() / 2, Reference.COLOR_HOLO.getIntB() / 2));
		}

		for (int i = 0; i < shipSlots.length; i++)
		{
			double angle = (-Math.PI / 1.8) + (Math.PI / 15) * (i + factorySlots.length);
			MOSlot slot = (MOSlot)starMapContainer.getSlot(i + factorySlots.length);
			shipSlots[i] = new ElementSlotShip(gui, slot, width / 2 - 10 + (int)(Math.sin(angle) * 140), height / 2 - 48 + (int)(Math.cos(angle) * 140), 22, 22, "holo", ClientProxy.holoIcons.getIcon("icon_shuttle"), starMap);
			shipSlots[i].setColor(new Color(Reference.COLOR_HOLO.getIntR() / 2, Reference.COLOR_HOLO.getIntG() / 2, Reference.COLOR_HOLO.getIntB() / 2));
		}
	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks)
	{
		super.update(mouseX, mouseY, partialTicks);

		boolean planetSlotVisible = starMap.getPlanet() != null && starMap.getPlanet().isOwner(Minecraft.getMinecraft().thePlayer);

		for (ElementInventorySlot slot : factorySlots)
		{
			slot.setVisible(planetSlotVisible);
		}

		for (ElementInventorySlot slot : shipSlots)
		{
			slot.setVisible(planetSlotVisible);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		super.drawForeground(mouseX, mouseY);
		if (starMap.getPlanet() != null)
		{
			GlStateManager.pushMatrix();
			int width = getFontRenderer().getStringWidth(starMap.getPlanet().getSpaceBodyName());
			GlStateManager.translate(sizeY / 2 + width / 2, 16, 0);
			GlStateManager.scale(1, 1, 1);
			StarmapHelper.drawPlanetInfo(starMap.getPlanet(), starMap.getPlanet().getSpaceBodyName(), 12 - width / 2, 4);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void init()
	{
		super.init();
		for (ElementInventorySlot factorySlot : factorySlots)
		{
			addElement(factorySlot);
		}
		for (ElementInventorySlot shipSlot : shipSlots)
		{
			addElement(shipSlot);
		}
	}
}
