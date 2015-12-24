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

package matteroverdrive.client.render;

import matteroverdrive.api.weapon.IWeaponModule;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.WavefrontObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simeon on 12/8/2015.
 */
public class WeaponModuleModelRegistry
{
    Map<String,WavefrontObject> models;

    public WeaponModuleModelRegistry()
    {
        models = new HashMap<>();
    }

    public void registerModule(IWeaponModule module)
    {
        IModelCustom m = AdvancedModelLoader.loadModel(new ResourceLocation(module.getModelPath()));
        if (m instanceof WavefrontObject)
        {
            models.put(module.getModelPath(),(WavefrontObject)m);
        }
    }

    public WavefrontObject getModel(String model)
    {
        return models.get(model);
    }
}