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

package matteroverdrive.data.inventory;

import matteroverdrive.client.render.HoloIcon;
import matteroverdrive.proxy.ClientProxy;
import matteroverdrive.util.MatterHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Simeon on 3/16/2015.
 */
public class MatterSlot extends Slot
{
	public MatterSlot(boolean isMainSlot)
	{
		super(isMainSlot);
	}

	@Override
	public boolean isValidForSlot(ItemStack itemStack)
	{
		return MatterHelper.containsMatter(itemStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public HoloIcon getHoloIcon()
	{
		return ClientProxy.holoIcons.getIcon("decompose");
	}

	@Override
	public String getUnlocalizedTooltip()
	{
		return "gui.tooltip.slot.matter";
	}
}
