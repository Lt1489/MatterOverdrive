package matteroverdrive.compat.modules.jei;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.container.ContainerInscriber;
import matteroverdrive.gui.GuiInscriber;
import matteroverdrive.init.MatterOverdriveRecipes;
import mezz.jei.api.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author shadowfacts
 */
@JEIPlugin
public class MOJEIPlugin extends BlankModPlugin
{

	@Override
	public void register(@Nonnull IModRegistry registry)
	{
		registry.addRecipeCategories(new InscriberRecipeCategory(registry.getJeiHelpers().getGuiHelper()));

		registry.addRecipeHandlers(new InscriberRecipeHandler());

		registry.addRecipes(MatterOverdriveRecipes.INSCRIBER.getRecipes());

		registry.addRecipeCategoryCraftingItem(new ItemStack(MatterOverdrive.blocks.inscriber), InscriberRecipeCategory.UID);

		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerInscriber.class, InscriberRecipeCategory.UID, 0, 2, 8, 36);

		registry.addRecipeClickArea(GuiInscriber.class, 32, 55, 24, 16, InscriberRecipeCategory.UID);

		registry.addAdvancedGuiHandlers(new MOAdvancedGuiHandler());

		registry.getJeiHelpers().getItemBlacklist().addItemToBlacklist(new ItemStack(MatterOverdrive.blocks.boundingBox));
	}
}
