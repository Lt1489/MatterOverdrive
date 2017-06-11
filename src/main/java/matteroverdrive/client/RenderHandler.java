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

package matteroverdrive.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.Reference;
import matteroverdrive.api.android.IAndroidStatRenderRegistry;
import matteroverdrive.api.android.IBioticStat;
import matteroverdrive.api.inventory.IBionicPart;
import matteroverdrive.api.renderer.IBionicPartRenderer;
import matteroverdrive.api.renderer.IBioticStatRenderer;
import matteroverdrive.api.starmap.IStarmapRenderRegistry;
import matteroverdrive.blocks.BlockDecorativeColored;
import matteroverdrive.client.model.ModelTritaniumArmor;
import matteroverdrive.client.render.*;
import matteroverdrive.client.render.biostat.BioticStatRendererShield;
import matteroverdrive.client.render.biostat.BioticStatRendererTeleporter;
import matteroverdrive.client.render.entity.*;
import matteroverdrive.client.render.tileentity.*;
import matteroverdrive.client.render.tileentity.starmap.*;
import matteroverdrive.client.render.weapons.*;
import matteroverdrive.client.render.weapons.layers.WeaponLayerAmmoRender;
import matteroverdrive.client.render.weapons.modules.ModuleHoloSightsRender;
import matteroverdrive.client.render.weapons.modules.ModuleSniperScopeRender;
import matteroverdrive.entity.*;
import matteroverdrive.entity.android_player.AndroidPlayer;
import matteroverdrive.entity.monster.EntityMeleeRougeAndroidMob;
import matteroverdrive.entity.monster.EntityMutantScientist;
import matteroverdrive.entity.monster.EntityRangedRogueAndroidMob;
import matteroverdrive.entity.player.MOPlayerCapabilityProvider;
import matteroverdrive.entity.weapon.PlasmaBolt;
import matteroverdrive.handler.ConfigurationHandler;
import matteroverdrive.init.MatterOverdriveBioticStats;
import matteroverdrive.items.IsolinearCircuit;
import matteroverdrive.items.ItemUpgrade;
import matteroverdrive.items.SecurityProtocol;
import matteroverdrive.items.android.RougeAndroidParts;
import matteroverdrive.items.food.AndroidPill;
import matteroverdrive.items.weapon.module.WeaponModuleBarrel;
import matteroverdrive.items.weapon.module.WeaponModuleColor;
import matteroverdrive.items.weapon.module.WeaponModuleHoloSights;
import matteroverdrive.items.weapon.module.WeaponModuleSniperScope;
import matteroverdrive.machines.fusionReactorController.TileEntityMachineFusionReactorController;
import matteroverdrive.machines.pattern_monitor.TileEntityMachinePatternMonitor;
import matteroverdrive.machines.pattern_storage.TileEntityMachinePatternStorage;
import matteroverdrive.machines.replicator.TileEntityMachineReplicator;
import matteroverdrive.starmap.data.Galaxy;
import matteroverdrive.starmap.data.Planet;
import matteroverdrive.starmap.data.Quadrant;
import matteroverdrive.starmap.data.Star;
import matteroverdrive.tile.*;
import matteroverdrive.util.MOLog;
import matteroverdrive.world.dimensions.alien.AlienColorsReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by Simeon on 4/17/2015.
 */
public class RenderHandler
{
	public static final Function<ResourceLocation, TextureAtlasSprite> modelTextureBakeFunc = new Function<ResourceLocation, TextureAtlasSprite>()
	{
		@Nullable
		@Override
		public TextureAtlasSprite apply(@Nullable ResourceLocation input)
		{
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(input.toString());
		}
	};
	public static int stencilBuffer;
	//endregion
	//region Item Renderers
	private static ItemRendererPhaser rendererPhaser;
	private static ItemRendererPhaserRifle rendererPhaserRifle;
	private static ItemRendererOmniTool rendererOmniTool;
	private static ItemRenderPlasmaShotgun renderPlasmaShotgun;
	private static ItemRendererIonSniper rendererIonSniper;
	private final Random random = new Random();
	private RenderMatterScannerInfoHandler matterScannerInfoHandler;
	private RenderParticlesHandler renderParticlesHandler;
	private RenderWeaponsBeam renderWeaponsBeam;
	private List<IWorldLastRenderer> customRenderers;
	private AndroidStatRenderRegistry statRenderRegistry;
	private StarmapRenderRegistry starmapRenderRegistry;
	private RenderDialogSystem renderDialogSystem;
	private AndroidBionicPartRenderRegistry bionicPartRenderRegistry;
	private WeaponModuleModelRegistry weaponModuleModelRegistry;
	private PipeRenderManager pipeRenderManager;
	private DimensionalRiftsRender dimensionalRiftsRender;
	private SpaceSkyRenderer spaceSkyRenderer;
	private WeaponRenderHandler weaponRenderHandler;
	//region Weapon Module Renderers
	private ModuleSniperScopeRender moduleSniperScopeRender;
	private ModuleHoloSightsRender moduleHoloSightsRender;
	//endregion
	//region Weapon Layers
	private final WeaponLayerAmmoRender weaponLayerAmmoRender = new WeaponLayerAmmoRender();
	//endregion
	//region World
	private final AlienColorsReloadListener alienColorsReloadListener = new AlienColorsReloadListener();
	public EntityRendererRougeAndroid rendererRougeAndroidHologram;
	//endregion
	//region Models
	public ModelTritaniumArmor modelTritaniumArmor;
	public ModelTritaniumArmor modelTritaniumArmorFeet;
	public ModelBiped modelMeleeRogueAndroidParts;
	public ModelBiped modelRangedRogueAndroidParts;
	public IBakedModel doubleHelixModel;
	//endregion
	//region Biostat Renderers
	private BioticStatRendererTeleporter rendererTeleporter;
	private BioticStatRendererShield biostatRendererShield;
	//endregion
	//region Starmap Renderers
	private StarMapRendererPlanet starMapRendererPlanet;
	private StarMapRendererQuadrant starMapRendererQuadrant;
	private StarMapRendererStar starMapRendererStar;
	private StarMapRenderGalaxy starMapRenderGalaxy;
	private StarMapRenderPlanetStats starMapRenderPlanetStats;
	//endregion
	//region Tile Entity Renderers
	private TileEntityRendererReplicator tileEntityRendererReplicator;
	private TileEntityRendererPipe tileEntityRendererPipe;
	private TileEntityRendererMatterPipe tileEntityRendererMatterPipe;
	private TileEntityRendererNetworkPipe tileEntityRendererNetworkPipe;
	private TileEntityRendererPatterStorage tileEntityRendererPatterStorage;
	private TileEntityRendererWeaponStation tileEntityRendererWeaponStation;
	private TileEntityRendererPatternMonitor tileEntityRendererPatternMonitor;
	private TileEntityRendererGravitationalAnomaly tileEntityRendererGravitationalAnomaly;
	private TileEntityRendererGravitationalStabilizer tileEntityRendererGravitationalStabilizer;
	private TileEntityRendererFusionReactorController tileEntityRendererFusionReactorController;
	private TileEntityRendererAndroidStation tileEntityRendererAndroidStation;
	private TileEntityRendererStarMap tileEntityRendererStarMap;
	private TileEntityRendererChargingStation tileEntityRendererChargingStation;
	private TileEntityRendererHoloSign tileEntityRendererHoloSign;
	private TileEntityRendererPacketQueue tileEntityRendererPacketQueue;
	private TileEntityRendererInscriber tileEntityRendererInscriber;
	private TileEntityRendererContractMarket tileEntityRendererContractMarket;
	//endregion

	public RenderHandler()
	{
		customRenderers = new ArrayList<>();
	}

	public void init(World world, TextureManager textureManager) {
		matterScannerInfoHandler = new RenderMatterScannerInfoHandler();
		renderParticlesHandler = new RenderParticlesHandler(world, textureManager);
		renderWeaponsBeam = new RenderWeaponsBeam();
		statRenderRegistry = new AndroidStatRenderRegistry();
		starmapRenderRegistry = new StarmapRenderRegistry();
		renderDialogSystem = new RenderDialogSystem();
		bionicPartRenderRegistry = new AndroidBionicPartRenderRegistry();
		weaponModuleModelRegistry = new WeaponModuleModelRegistry();
		pipeRenderManager = new PipeRenderManager();
		dimensionalRiftsRender = new DimensionalRiftsRender();
		spaceSkyRenderer = new SpaceSkyRenderer();
		weaponRenderHandler = new WeaponRenderHandler();

		moduleSniperScopeRender = new ModuleSniperScopeRender(weaponRenderHandler);
		moduleHoloSightsRender = new ModuleHoloSightsRender(weaponRenderHandler);

		addCustomRenderer(matterScannerInfoHandler);
		addCustomRenderer(renderParticlesHandler);
		addCustomRenderer(renderWeaponsBeam);
		addCustomRenderer(renderDialogSystem);
		addCustomRenderer(dimensionalRiftsRender);

		OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);

		MinecraftForge.EVENT_BUS.register(pipeRenderManager);
		MinecraftForge.EVENT_BUS.register(weaponRenderHandler);
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(alienColorsReloadListener);
		if (Minecraft.getMinecraft().getFramebuffer().enableStencil())
		{
			stencilBuffer = MinecraftForgeClient.reserveStencilBit();
		}
	}

	public static void registerItemRendererVarients()
	{
		regItemRenderVer(MatterOverdrive.items.item_upgrade, "upgrade", ItemUpgrade.subItemNames);
		regItemRenderVer(MatterOverdrive.items.weapon_module_barrel, "barrel", WeaponModuleBarrel.names);
		regItemRenderVer(MatterOverdrive.items.isolinear_circuit, "isolinear_circuit", IsolinearCircuit.subItemNames);
		regItemRenderVer(MatterOverdrive.items.androidPill, "android_pill", AndroidPill.names);
		regItemRenderVer(MatterOverdrive.items.security_protocol, "security_protocol", SecurityProtocol.types);
		regItemRenderVer(MatterOverdrive.items.androidParts, "rogue_android_part", RougeAndroidParts.names);
		regItemRenderVer(MatterOverdrive.items.androidParts, "weapon_module_color", WeaponModuleColor.names);
	}

	public static void registerCustomStateMappers()
	{
		ModelLoader.setCustomStateMapper(MatterOverdrive.blocks.alienLeaves, new StateMap.Builder().ignore(BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build());
	}

	private static void regItemRenderVer(Item item, String name, String[] subNames)
	{
		for (String subName : subNames)
		{
			ModelBakery.registerItemVariants(item, new ResourceLocation(Reference.MOD_ID, name + "_" + subName));
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		for (IWorldLastRenderer renderer : customRenderers)
		{
			renderer.onRenderWorldLast(this, event);
		}
		for (IBioticStat stat : MatterOverdrive.statRegistry.getStats())
		{
			Collection<IBioticStatRenderer> statRendererCollection = statRenderRegistry.getRendererCollection(stat.getClass());
			if (statRendererCollection != null)
			{
				for (IBioticStatRenderer renderer : statRendererCollection)
				{
					renderer.onWorldRender(stat, MOPlayerCapabilityProvider.GetAndroidCapability(Minecraft.getMinecraft().thePlayer).getUnlockedLevel(stat), event);
				}
			}
		}
	}

	//Called when the client ticks.
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		renderParticlesHandler.onClientTick(event);
	}

	public void createTileEntityRenderers(ConfigurationHandler configHandler)
	{
		tileEntityRendererReplicator = new TileEntityRendererReplicator();
		tileEntityRendererPipe = new TileEntityRendererPipe();
		tileEntityRendererMatterPipe = new TileEntityRendererMatterPipe();
		tileEntityRendererNetworkPipe = new TileEntityRendererNetworkPipe();
		tileEntityRendererPatterStorage = new TileEntityRendererPatterStorage();
		tileEntityRendererWeaponStation = new TileEntityRendererWeaponStation();
		tileEntityRendererPatternMonitor = new TileEntityRendererPatternMonitor();
		tileEntityRendererGravitationalAnomaly = new TileEntityRendererGravitationalAnomaly();
		tileEntityRendererGravitationalStabilizer = new TileEntityRendererGravitationalStabilizer();
		tileEntityRendererFusionReactorController = new TileEntityRendererFusionReactorController();
		tileEntityRendererAndroidStation = new TileEntityRendererAndroidStation();
		tileEntityRendererStarMap = new TileEntityRendererStarMap();
		tileEntityRendererChargingStation = new TileEntityRendererChargingStation();
		tileEntityRendererHoloSign = new TileEntityRendererHoloSign();
		tileEntityRendererPacketQueue = new TileEntityRendererPacketQueue();
		tileEntityRendererInscriber = new TileEntityRendererInscriber();
		tileEntityRendererContractMarket = new TileEntityRendererContractMarket();

		configHandler.subscribe(tileEntityRendererAndroidStation);
		configHandler.subscribe(tileEntityRendererWeaponStation);
	}

	@SubscribeEvent
	public void onPlayerRenderPost(RenderPlayerEvent.Post event)
	{
		//GL11.glEnable(GL11.GL_LIGHTING);
		//GL11.glColor3f(1, 1, 1);

		AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntity());
		if (androidPlayer != null && androidPlayer.isAndroid() && !event.getEntity().isInvisible())
		{
			for (int i = 0; i < 5; i++)
			{
				ItemStack part = androidPlayer.getStackInSlot(i);
				if (part != null && part.getItem() instanceof IBionicPart)
				{
					IBionicPartRenderer renderer = bionicPartRenderRegistry.getRenderer(((IBionicPart)part.getItem()).getClass());
					if (renderer != null)
					{
						try
						{
							GlStateManager.pushMatrix();
							renderer.renderPart(part, androidPlayer, event.getRenderer(), event.getPartialRenderTick());
							GlStateManager.popMatrix();
						}
						catch (Exception e)
						{
							MOLog.log(Level.ERROR, e, "An Error occurred while rendering bionic part");
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRenderPre(RenderPlayerEvent.Pre event)
	{
		//GL11.glEnable(GL11.GL_LIGHTING);
		//GL11.glColor3f(1, 1, 1);

		AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(event.getEntity());
		if (androidPlayer != null && androidPlayer.isAndroid() && !event.getEntity().isInvisible())
		{
			for (int i = 0; i < 5; i++)
			{
				ItemStack part = androidPlayer.getStackInSlot(i);
				if (part != null && part.getItem() instanceof IBionicPart)
				{
					IBionicPartRenderer renderer = bionicPartRenderRegistry.getRenderer(((IBionicPart)part.getItem()).getClass());
					if (renderer != null)
					{
						renderer.affectPlayerRenderer(part, androidPlayer, event.getRenderer(), event.getPartialRenderTick());
					}
				}
			}
		}
	}

	public void registerWeaponModuleRenders()
	{
		weaponRenderHandler.addModuleRender(WeaponModuleSniperScope.class, moduleSniperScopeRender);
		weaponRenderHandler.addModuleRender(WeaponModuleHoloSights.class, moduleHoloSightsRender);
	}

	public void registerWeaponLayers()
	{
		weaponRenderHandler.addWeaponLayer(weaponLayerAmmoRender);
	}

	public void registerTileEntitySpecialRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineReplicator.class, tileEntityRendererReplicator);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachinePatternStorage.class, tileEntityRendererPatterStorage);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeaponStation.class, tileEntityRendererWeaponStation);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachinePatternMonitor.class, tileEntityRendererPatternMonitor);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGravitationalAnomaly.class, tileEntityRendererGravitationalAnomaly);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineGravitationalStabilizer.class, tileEntityRendererGravitationalStabilizer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineFusionReactorController.class, tileEntityRendererFusionReactorController);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAndroidStation.class, tileEntityRendererAndroidStation);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineStarMap.class, tileEntityRendererStarMap);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineChargingStation.class, tileEntityRendererChargingStation);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHoloSign.class, tileEntityRendererHoloSign);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachinePacketQueue.class, tileEntityRendererPacketQueue);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInscriber.class, tileEntityRendererInscriber);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMachineContractMarket.class, tileEntityRendererContractMarket);
	}

	public void registerBlockColors()
	{
		FMLClientHandler.instance().getClient().getBlockColors().registerBlockColorHandler((state, p_186720_2_, pos, tintIndex) -> {
			EnumDyeColor color = state.getValue(BlockDecorativeColored.COLOR);
			return ItemDye.DYE_COLORS[MathHelper.clamp_int(color.getMetadata(),0, ItemDye.DYE_COLORS.length-1)];
		}, MatterOverdrive.blocks.decorative_tritanium_plate_colored);
	}

	public void createItemRenderers()
	{
		rendererPhaser = new ItemRendererPhaser();
		rendererPhaserRifle = new ItemRendererPhaserRifle();
		rendererOmniTool = new ItemRendererOmniTool();
		renderPlasmaShotgun = new ItemRenderPlasmaShotgun();
		rendererIonSniper = new ItemRendererIonSniper();
	}

	public void bakeItemModels()
	{
		weaponRenderHandler.onModelBake(Minecraft.getMinecraft().getTextureMapBlocks(), this);
		rendererPhaser.bakeModel();
		rendererPhaserRifle.bakeModel();
		rendererOmniTool.bakeModel();
		rendererIonSniper.bakeModel();
		renderPlasmaShotgun.bakeModel();
	}

	public void registerModelTextures(TextureMap textureMap, OBJModel model)
	{
		model.getTextures().forEach(textureMap::registerSprite);
	}

	public OBJModel getObjModel(ResourceLocation location, ImmutableMap<String, String> customOptions)
	{
		try
		{
			OBJModel model = (OBJModel)OBJLoader.INSTANCE.loadModel(location);
			model = (OBJModel)model.process(customOptions);
			return model;
		}
		catch (Exception e)
		{
			MOLog.log(Level.ERROR, e, "There was a problem while baking %s model", location.getResourcePath());
		}
		return null;
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event)
	{
		event.getModelRegistry().putObject(new ModelResourceLocation(MatterOverdrive.items.phaser.getRegistryName(), "inventory"), rendererPhaser);
		event.getModelRegistry().putObject(new ModelResourceLocation(MatterOverdrive.items.phaserRifle.getRegistryName(), "inventory"), rendererPhaserRifle);
		event.getModelRegistry().putObject(new ModelResourceLocation(MatterOverdrive.items.omniTool.getRegistryName(), "inventory"), rendererOmniTool);
		event.getModelRegistry().putObject(new ModelResourceLocation(MatterOverdrive.items.ionSniper.getRegistryName(), "inventory"), rendererIonSniper);
		event.getModelRegistry().putObject(new ModelResourceLocation(MatterOverdrive.items.plasmaShotgun.getRegistryName(), "inventory"), renderPlasmaShotgun);

		bakeItemModels();
	}

	@SubscribeEvent
	public void onTextureStich(TextureStitchEvent.Pre event)
	{
		if (event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks())
		{
			weaponRenderHandler.onTextureStich(Minecraft.getMinecraft().getTextureMapBlocks(), this);
		}
	}

	public void registerItemRenderers()
	{
		regItemRender(MatterOverdrive.items.dataPad);
		regItemRender(MatterOverdrive.items.scoutShip);
		regItemRender(MatterOverdrive.items.buildingBase);
		regItemRender(MatterOverdrive.items.buildingMatterExtractor);
		regItemRender(MatterOverdrive.items.buildingResidential);
		regItemRender(MatterOverdrive.items.buildingShipHangar);
		regItemRender(MatterOverdrive.items.contract);
		regItemRender(MatterOverdrive.items.dilithium_crystal);
		regItemRender(MatterOverdrive.items.earl_gray_tea);
		regItemRender(MatterOverdrive.items.emergency_ration);
		regItemRender(MatterOverdrive.items.forceFieldEmitter);
		regItemRender(MatterOverdrive.items.h_compensator);
		regItemRender(MatterOverdrive.items.integration_matrix);
		regItemRender(MatterOverdrive.items.machine_casing);
		regItemRender(MatterOverdrive.items.me_conversion_matrix);
		regItemRender(MatterOverdrive.items.plasmaCore);
		regItemRender(MatterOverdrive.items.portableDecomposer);
		regItemRender(MatterOverdrive.items.romulan_ale);
		regItemRender(MatterOverdrive.items.s_magnet);
		regItemRender(MatterOverdrive.items.shipFactory);
		regItemRender(MatterOverdrive.items.sniperScope);
		regItemRender(MatterOverdrive.items.tritaniumSpine);
		regItemRender(MatterOverdrive.items.tritaniumChestplate);
		regItemRender(MatterOverdrive.items.tritanium_dust);
		regItemRender(MatterOverdrive.items.tritaniumHelmet);
		regItemRender(MatterOverdrive.items.tritaniumHoe);
		regItemRender(MatterOverdrive.items.tritaniumShovel);
		regItemRender(MatterOverdrive.items.tritanium_nugget);
		regItemRender(MatterOverdrive.items.tritaniumLeggings);
		regItemRender(MatterOverdrive.items.tritaniumPickaxe);
		regItemRender(MatterOverdrive.items.tritanium_plate);
		regItemRender(MatterOverdrive.items.tritaniumSword);
		regItemRender(MatterOverdrive.items.wrench);
		regItemRender(MatterOverdrive.items.battery);
		regItemRender(MatterOverdrive.items.hc_battery);
		regItemRender(MatterOverdrive.items.creative_battery);
		regItemRender(MatterOverdrive.items.energyPack);
		regItemRender(MatterOverdrive.items.matterContainer);
		regItemRender(MatterOverdrive.items.matterContainerFull);
		regItemRender(MatterOverdrive.items.pattern_drive);
		//regItemRender(MatterOverdrive.items.creativePatternDrive);
		regItemRender(MatterOverdrive.items.spacetime_equalizer);
		regItemRender(MatterOverdrive.items.item_upgrade, "upgrade", ItemUpgrade.subItemNames);
		regItemRender(MatterOverdrive.items.weapon_module_color, "weapon_module_color", WeaponModuleColor.names);
		regItemRender(MatterOverdrive.items.weapon_module_barrel, "barrel", WeaponModuleBarrel.names);
		regItemRender(MatterOverdrive.items.isolinear_circuit, "isolinear_circuit", IsolinearCircuit.subItemNames);
		regItemRender(MatterOverdrive.items.matter_dust);
		regItemRender(MatterOverdrive.items.matter_dust_refined);
		regItemRender(MatterOverdrive.items.androidPill, "android_pill", AndroidPill.names);
		regItemRender(MatterOverdrive.items.security_protocol, "security_protocol", SecurityProtocol.types);
		regItemRender(MatterOverdrive.items.androidParts, "rogue_android_part", RougeAndroidParts.names);
		regItemRender(MatterOverdrive.items.tritanium_ingot);
		regItemRender(MatterOverdrive.items.transportFlashDrive);
		regItemRender(MatterOverdrive.items.networkFlashDrive);
		regItemRender(MatterOverdrive.items.weaponHandle);
		regItemRender(MatterOverdrive.items.weaponReceiver);
		regItemRender(MatterOverdrive.items.matter_scanner);
		regItemRender(MatterOverdrive.items.phaser);
		regItemRender(MatterOverdrive.items.phaserRifle);
		regItemRender(MatterOverdrive.items.plasmaShotgun);
		regItemRender(MatterOverdrive.items.omniTool);
		regItemRender(MatterOverdrive.items.ionSniper);
		regItemRender(MatterOverdrive.items.omniTool);
		regItemRender(MatterOverdrive.items.plasmaShotgun);
		regItemRender(MatterOverdrive.items.colonizerShip);
		regItemRender(MatterOverdrive.items.tritaniumBoots);
		regItemRender(MatterOverdrive.items.buildingPowerGenerator);
		regItemRender(MatterOverdrive.items.weaponModuleRicochet);

		regItemRender(MatterOverdrive.blocks.weapon_station);
		regItemRender(MatterOverdrive.blocks.androidStation);
		regItemRender(MatterOverdrive.blocks.replicator);
		regItemRender(MatterOverdrive.blocks.decomposer);
		regItemRender(MatterOverdrive.blocks.recycler);
		regItemRender(MatterOverdrive.blocks.matter_analyzer);
		regItemRender(MatterOverdrive.blocks.transporter);
		regItemRender(MatterOverdrive.blocks.network_router);
		regItemRender(MatterOverdrive.blocks.network_switch);
		regItemRender(MatterOverdrive.blocks.fusion_reactor_coil);
		regItemRender(MatterOverdrive.blocks.machine_hull);
		regItemRender(MatterOverdrive.blocks.fusionReactorIO);
		regItemRender(MatterOverdrive.blocks.dilithium_ore);
		regItemRender(MatterOverdrive.blocks.tritaniumOre);
		regItemRender(MatterOverdrive.blocks.tritanium_block);
		regItemRender(MatterOverdrive.blocks.starMap);
		regItemRender(MatterOverdrive.blocks.solar_panel);
		regItemRender(MatterOverdrive.blocks.matter_pipe);
		regItemRender(MatterOverdrive.blocks.heavy_matter_pipe);
		regItemRender(MatterOverdrive.blocks.network_pipe);
		regItemRender(MatterOverdrive.blocks.spacetimeAccelerator);
		regItemRender(MatterOverdrive.blocks.forceGlass);
		regItemRender(MatterOverdrive.blocks.pattern_monitor);
		regItemRender(MatterOverdrive.blocks.holoSign);
		regItemRender(MatterOverdrive.blocks.pattern_storage);
		regItemRender(MatterOverdrive.blocks.inscriber);
		regItemRender(MatterOverdrive.blocks.gravitational_anomaly);
		regItemRender(MatterOverdrive.blocks.fusion_reactor_controller);
		regItemRender(MatterOverdrive.blocks.pylon);
		regItemRender(MatterOverdrive.blocks.tritaniumCrate);
		regItemRender(MatterOverdrive.blocks.tritaniumCrateYellow);

		regItemRender(MatterOverdrive.blocks.decorative_stripes);
		regItemRender(MatterOverdrive.blocks.decorative_coils);
		regItemRender(MatterOverdrive.blocks.decorative_clean);
		regItemRender(MatterOverdrive.blocks.decorative_vent_dark);
		regItemRender(MatterOverdrive.blocks.decorative_vent_bright);
		regItemRender(MatterOverdrive.blocks.decorative_holo_matrix);
		regItemRender(MatterOverdrive.blocks.decorative_tritanium_plate);
		regItemRender(MatterOverdrive.blocks.decorative_carbon_fiber_plate);
		regItemRender(MatterOverdrive.blocks.decorative_floor_tiles);
		regItemRender(MatterOverdrive.blocks.decorative_floor_tiles_green);
		regItemRender(MatterOverdrive.blocks.decorative_floor_noise);
		regItemRender(MatterOverdrive.blocks.decorative_tritanium_plate_stripe);
		regItemRender(MatterOverdrive.blocks.decorative_floor_tile_white);
		regItemRender(MatterOverdrive.blocks.decorative_white_plate);
		regItemRender(MatterOverdrive.blocks.decorative_tritanium_plate_colored, 16);
		regItemRender(MatterOverdrive.blocks.decorative_engine_exhaust_plasma);
		regItemRender(MatterOverdrive.blocks.decorative_beams, 2);
		regItemRender(MatterOverdrive.blocks.decorative_matter_tube, 2);
		regItemRender(MatterOverdrive.blocks.decorative_tritanium_lamp, 2);
		regItemRender(MatterOverdrive.blocks.decorative_separator, 2);
	}

	public void registerItemColors()
	{
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack,tintIndex) -> tintIndex == 1 ? Reference.COLOR_HOLO_RED.getColor() : -1,MatterOverdrive.items.energyPack);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack,tintIndex) -> tintIndex == 1 ? Reference.COLOR_MATTER.getColor() : -1,MatterOverdrive.items.battery);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack,tintIndex) -> tintIndex == 1 ? Reference.COLOR_YELLOW_STRIPES.getColor() : -1,MatterOverdrive.items.hc_battery);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack,tintIndex) -> tintIndex == 1 ? Reference.COLOR_HOLO_RED.getColor() : -1,MatterOverdrive.items.creative_battery);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack,tintIndex) -> tintIndex == 1 ? Reference.COLOR_YELLOW_STRIPES.getColor() : -1,MatterOverdrive.items.networkFlashDrive);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack,tintIndex) -> tintIndex == 1 ? Reference.COLOR_HOLO_GREEN.getColor() : -1,MatterOverdrive.items.transportFlashDrive);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack,tintIndex) -> tintIndex == 1 ? Reference.COLOR_MATTER.getColor() : tintIndex == 2 ? Reference.COLOR_YELLOW_STRIPES.getColor() : -1,MatterOverdrive.items.matterContainerFull);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			if (tintIndex == 1 && stack != null && stack.getItem() != null)
			{
				return WeaponModuleColor.colors[stack.getItemDamage()].getColor();
			}else
			{
				return 16777215;
			}
		}, MatterOverdrive.items.weapon_module_color);
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			if (tintIndex == 0 && stack != null && stack.getItem() != null)
			{
				return ItemDye.DYE_COLORS[MathHelper.clamp_int(stack.getItemDamage(),0, ItemDye.DYE_COLORS.length-1)];
			}else
			{
				return -1;
			}
		}, Item.getItemFromBlock(MatterOverdrive.blocks.decorative_tritanium_plate_colored));
		FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			if (stack.getItemDamage() == 0) return 0xd00000;
			else if (stack.getItemDamage() == 1) return 0x019fea;
			else if (stack.getItemDamage() == 2) return 0xffe400;
			return 0xffffff;
		}, MatterOverdrive.items.androidPill);
	}

	private <T extends Item> void regItemRender(T item, String name, String[] subNames)
	{
		for (int i = 0; i < subNames.length; i++)
		{
			regItemRender(item, i, name + "_" + subNames[i]);
		}
	}

	private <T extends Item> void regItemRender(T item, int meta, String name)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(Reference.MOD_ID + ":" + name, "inventory"));
	}

	private <T extends Item> void regItemRender(T item, int meta)
	{
		ResourceLocation name = item.getRegistryName();
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(name, "inventory"));
	}

	private <T extends Item> void regItemRender(T item)
	{
		ResourceLocation name = item.getRegistryName();
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(name, "inventory"));
	}

	private void regItemRender(Block block, int metaCount)
	{
		ResourceLocation name = block.getRegistryName();
		for (int i = 0; i < metaCount; i++)
		{
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), i, new ModelResourceLocation(name, "inventory"));
		}
	}

	private void regItemRender(Block block)
	{
		ResourceLocation name = block.getRegistryName();
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(name, "inventory"));
	}

	public void createEntityRenderers(RenderManager renderManager)
	{
		rendererRougeAndroidHologram = new EntityRendererRougeAndroid(renderManager, true);
	}

	public void registerEntityRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityMeleeRougeAndroidMob.class, renderManager -> new EntityRendererRougeAndroid(renderManager, false));
		RenderingRegistry.registerEntityRenderingHandler(EntityVillagerMadScientist.class, EntityRendererMadScientist::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFailedPig.class, EntityRendererFailedPig::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFailedCow.class, EntityRendererFailedCow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFailedChicken.class, EntityRendererFailedChicken::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFailedSheep.class, EntityRendererFailedSheep::new);
		RenderingRegistry.registerEntityRenderingHandler(PlasmaBolt.class, EntityRendererPhaserFire::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRangedRogueAndroidMob.class, EntityRendererRangedRougeAndroid::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMutantScientist.class, EntityRendererMutantScientist::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDrone.class, EntityRendererDrone::new);
	}

	public void createBioticStatRenderers()
	{
		rendererTeleporter = new BioticStatRendererTeleporter();
		biostatRendererShield = new BioticStatRendererShield();
	}

	public void registerBioticStatRenderers()
	{
		statRenderRegistry.registerRenderer(MatterOverdriveBioticStats.shield.getClass(), biostatRendererShield);
		statRenderRegistry.registerRenderer(MatterOverdriveBioticStats.teleport.getClass(), rendererTeleporter);
	}

	public void registerBionicPartRenderers()
	{

	}

	public void createStarmapRenderers()
	{
		starMapRendererPlanet = new StarMapRendererPlanet();
		starMapRendererQuadrant = new StarMapRendererQuadrant();
		starMapRendererStar = new StarMapRendererStar();
		starMapRenderGalaxy = new StarMapRenderGalaxy();
		starMapRenderPlanetStats = new StarMapRenderPlanetStats();
	}

	public void registerStarmapRenderers()
	{
		starmapRenderRegistry.registerRenderer(Planet.class, starMapRendererPlanet);
		starmapRenderRegistry.registerRenderer(Quadrant.class, starMapRendererQuadrant);
		starmapRenderRegistry.registerRenderer(Star.class, starMapRendererStar);
		starmapRenderRegistry.registerRenderer(Galaxy.class, starMapRenderGalaxy);
		starmapRenderRegistry.registerRenderer(Planet.class, starMapRenderPlanetStats);
	}

	public void createModels()
	{
		modelTritaniumArmor = new ModelTritaniumArmor(0);
		modelTritaniumArmorFeet = new ModelTritaniumArmor(0.5f);
		modelMeleeRogueAndroidParts = new ModelBiped(0);
		modelRangedRogueAndroidParts = new ModelBiped(0, 0, 96, 64);
		try
		{
			IModel model = OBJLoader.INSTANCE.loadModel(new ResourceLocation(Reference.PATH_MODEL + "gui/double_helix.obj"));
			doubleHelixModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, new Function<ResourceLocation, TextureAtlasSprite>()
			{
				@Nullable
				@Override
				public TextureAtlasSprite apply(@Nullable ResourceLocation input)
				{
					return Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(input);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public RenderParticlesHandler getRenderParticlesHandler()
	{
		return renderParticlesHandler;
	}

	public TileEntityRendererStarMap getTileEntityRendererStarMap()
	{
		return tileEntityRendererStarMap;
	}

	public IAndroidStatRenderRegistry getStatRenderRegistry()
	{
		return statRenderRegistry;
	}

	public IStarmapRenderRegistry getStarmapRenderRegistry()
	{
		return starmapRenderRegistry;
	}

	public ItemRendererOmniTool getRendererOmniTool()
	{
		return rendererOmniTool;
	}

	public AndroidBionicPartRenderRegistry getBionicPartRenderRegistry()
	{
		return bionicPartRenderRegistry;
	}

	public WeaponModuleModelRegistry getWeaponModuleModelRegistry()
	{
		return weaponModuleModelRegistry;
	}

	public Random getRandom()
	{
		return random;
	}

	public void addCustomRenderer(IWorldLastRenderer renderer)
	{
		customRenderers.add(renderer);
	}

	public SpaceSkyRenderer getSpaceSkyRenderer()
	{
		return spaceSkyRenderer;
	}

	public WeaponRenderHandler getWeaponRenderHandler()
	{
		return weaponRenderHandler;
	}
}
