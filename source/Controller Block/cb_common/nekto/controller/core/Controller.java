/*
 *  Author: Sam6982
 */
package nekto.controller.core;

import cpw.mods.fml.common.network.FMLEventChannel;
import nekto.controller.block.BlockAnimator;
import nekto.controller.block.BlockController;
import nekto.controller.item.ItemLinker;
import nekto.controller.item.ItemRemote;
import nekto.controller.network.*;
import nekto.controller.ref.GeneralRef;
import nekto.controller.tile.TileEntityAnimator;
import nekto.controller.tile.TileEntityController;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = GeneralRef.MOD_ID, name = GeneralRef.MOD_NAME, version = GeneralRef.VERSION)
public class Controller {
	//Blocks
	public static Block controller, animator;
	//Items
	public static Item linker, remote;
	public static boolean tickDisplay;
	@Instance(GeneralRef.MOD_ID)
	public static Controller instance;
	@SidedProxy(clientSide = GeneralRef.CLIENT_PROXY, serverSide = GeneralRef.COMMON_PROXY)
	public static CommonProxy proxy;
    public static FMLEventChannel animatorDesc, guiChange;

	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new ItemStack(animator), "IPI", "DRE", "TBW", 'I', Blocks.iron_ore, 'P', Items.ender_pearl, 'D',
				Items.diamond, 'R', Blocks.redstone_block, 'E', Items.emerald, 'T', Blocks.enchanting_table, 'B', Items.book,
				'W', Blocks.crafting_table);
		GameRegistry.addRecipe(new ItemStack(remote), "D", "I", "I", 'D', Items.diamond, 'I', Items.iron_ingot);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        animatorDesc = NetworkRegistry.INSTANCE.newEventDrivenChannel(GeneralRef.DESC_CHANNEL);
        guiChange = NetworkRegistry.INSTANCE.newEventDrivenChannel(GeneralRef.GUI_CHANNEL);
        if(event.getSide().isClient()){
            animatorDesc.register(new DescriptionHandler());
        }
        guiChange.register(new GuiChangeHandler());
		GameRegistry.registerTileEntity(TileEntityController.class, "controllerBlockList");
		GameRegistry.registerTileEntity(TileEntityAnimator.class, "animatorBlockList");
		proxy.registerRenderThings();
	}

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile(), true);
		config.load();
		controller = new BlockController();
		linker = new ItemLinker().setTextureName(GeneralRef.TEXTURE_PATH + "linker");
		animator = new BlockAnimator();
		remote = new ItemRemote().setTextureName(GeneralRef.TEXTURE_PATH + "remote");
		tickDisplay = config.get("general", "Show delay as ticks", false).getBoolean(false);
		if (config.hasChanged())
			config.save();
		GameRegistry.registerBlock(controller, "controller");
		GameRegistry.registerBlock(animator, "animator");
		GameRegistry.registerItem(linker, "linker");
		GameRegistry.registerItem(remote, "remote");
	}
}
