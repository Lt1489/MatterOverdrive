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

package matteroverdrive.items.starmap;

import matteroverdrive.api.starmap.BuildingType;
import matteroverdrive.api.starmap.IPlanetStatChange;
import matteroverdrive.api.starmap.PlanetStatType;
import matteroverdrive.starmap.data.Planet;
import matteroverdrive.util.MOStringHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Simeon on 12/20/2015.
 */
public class ItemBuildingMatterExtractor extends ItemBuildingAbstract implements IPlanetStatChange
{
	private static final int MATTER_PRODUCTION = 10;
	private static final int ENERGY_DRAIN = 6;

	public ItemBuildingMatterExtractor(String name)
	{
		super(name);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addDetails(ItemStack itemstack, EntityPlayer player, List<String> infos)
	{
		super.addDetails(itemstack, player, infos);
		infos.add(TextFormatting.GREEN + MOStringHelper.translateToLocal(PlanetStatType.MATTER_PRODUCTION) + ": +" + MATTER_PRODUCTION);
		infos.add(TextFormatting.RED + MOStringHelper.translateToLocal(PlanetStatType.ENERGY_PRODUCTION) + ": -" + ENERGY_DRAIN);
	}

	@Override
	public BuildingType getType(ItemStack building)
	{
		return BuildingType.ORE_EXTRACTOR;
	}

	@Override
	protected int getBuildLengthUnscaled(ItemStack buildableStack, Planet planet)
	{
		return 20 * 60 * 12;
	}

	@Override
	public boolean canBuild(ItemStack building, Planet planet, List<String> info)
	{
		return true;
	}

	@Override
	public float changeStat(ItemStack stack, Planet planet, PlanetStatType statType, float original)
	{
		switch (statType)
		{
			case MATTER_PRODUCTION:
				return original + MATTER_PRODUCTION;
			case ENERGY_PRODUCTION:
				return original - ENERGY_DRAIN;
			default:
				return original;
		}
	}
}
