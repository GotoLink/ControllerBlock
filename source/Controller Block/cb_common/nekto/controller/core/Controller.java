/*
 *  Author: Sam6982
 */
package nekto.controller.core;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
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
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@Mod(modid = GeneralRef.MOD_ID, name = GeneralRef.MOD_NAME, version = GeneralRef.VERSION)
public final class Controller {
	//Blocks
	public static Block controller, animator;
	//Items
	public static Item linker, remote;
	public static boolean tickDisplay;
	@Instance(GeneralRef.MOD_ID)
	public static Controller instance;
	@SidedProxy(clientSide = GeneralRef.CLIENT_PROXY, serverSide = GeneralRef.COMMON_PROXY)
	public static CommonProxy proxy;
    public static SimpleNetworkWrapper animatorDesc, guiChange;
    public static Logger logger;
    private Configuration config;

	@EventHandler
	public void load(FMLInitializationEvent event) {
        config.addCustomCategoryComment("Recipes", "First 3 lines define crafting slots, then all lines are couples linking symbol to item/block name. Use whitespace for empty slots.\n Generic ore names are allowed, too.");
        if(controller!=null){
            String[] option = config.getStringList("Controller", "Recipes", new String[]{"", "", ""}, "Recipe for the block, disabled by default");
            ShapedOreRecipe recipe = parse(option, new ItemStack(controller));
            if(recipe!=null)
                GameRegistry.addRecipe(recipe);
            else
                logger.trace("Controller Recipe Disabled");
            option = config.getStringList("Linker", "Recipes", new String[]{"", "", ""}, "Recipe for the item, disabled by default");
            recipe = parse(option, new ItemStack(linker));
            if(recipe!=null)
                GameRegistry.addRecipe(recipe);
            else
                logger.trace("Linker Recipe Disabled");
            GameRegistry.registerTileEntity(TileEntityController.class, "controllerBlockList");
        }
        if(animator!=null){
            String[] option = config.getStringList("Animator", "Recipes", new String[]{"IPI", "DRE", "TBW",
                    "I=oreIron", "P=minecraft:ender_pearl", "D=gemDiamond", "R=blockRedstone", "E=gemEmerald",
                    "T=minecraft:enchanting_table", "B=minecraft:book", "W=minecraft:crafting_table"}, "Recipe for the block");
            ShapedOreRecipe recipe = parse(option, new ItemStack(animator));
            if(recipe!=null)
                GameRegistry.addRecipe(recipe);
            else
                logger.trace("Animator Recipe Disabled");
            option = config.getStringList("Remote", "Recipes", new String[]{"D", "I", "I", "D=gemDiamond", "I=ingotIron"}, "Recipe for the item");
            recipe = parse(option, new ItemStack(remote));
            if(recipe!=null)
                GameRegistry.addRecipe(recipe);
            else
                logger.trace("Remote Recipe Disabled");
            GameRegistry.registerTileEntity(TileEntityAnimator.class, "animatorBlockList");
        }
        if (config.hasChanged())
            config.save();
        config = null;
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        animatorDesc = NetworkRegistry.INSTANCE.newSimpleChannel(GeneralRef.DESC_CHANNEL);
        guiChange = NetworkRegistry.INSTANCE.newSimpleChannel(GeneralRef.GUI_CHANNEL);
        animatorDesc.registerMessage(DescriptionHandler.class, DescriptionPacket.class, 0, Side.CLIENT);
        guiChange.registerMessage(GuiChangeHandler.class, GuiChangePacket.class, 0, Side.SERVER);
		proxy.registerRenderThings();
	}

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event) {
        logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile(), true);
        boolean control = config.getBoolean("Controller", "General", true, "Should block and corresponding item be added to the game");
        boolean animate = config.getBoolean("Animator", "General", true, "Should block and corresponding item be added to the game");
		if(control) {
            controller = new BlockController();
            linker = new ItemLinker().setTextureName(GeneralRef.TEXTURE_PATH + "linker");
            GameRegistry.registerBlock(controller, "controller");
            GameRegistry.registerItem(linker, "linker");
        }
        if(animate) {
            animator = new BlockAnimator();
            remote = new ItemRemote().setTextureName(GeneralRef.TEXTURE_PATH + "remote");
            GameRegistry.registerBlock(animator, "animator");
            GameRegistry.registerItem(remote, "remote");
        }
		tickDisplay = config.get("Client", "Show delay as ticks", false).getBoolean();
	}

    private ShapedOreRecipe parse(String[] option, ItemStack output){
        if(option.length>4){
            Object[] inputs = new Object[2*option.length-3];
            Set<Character> characterSet = Sets.newHashSetWithExpectedSize(9);
            for(int i = 0; i<3; i++){//Scan 3 lines for characters
                String txt = option[i];
                if(txt==null||"".equals(txt)||txt.length()>3)
                    return null;
                char[] chars = txt.toCharArray();
                for(char c:chars){
                    if(!Character.isWhitespace(c)) {
                        characterSet.add(c);
                    }
                }
                inputs[i] = txt;
            }
            for(int i = 3; i < option.length; i++){//Scan other lines for "characters=item name"
                String txt = option[i];
                if(txt==null||"".equals(txt))
                    return null;
                if(!txt.contains("="))
                    return null;
                if(!characterSet.contains(txt.charAt(0)))
                    return null;
                inputs[2*i-3] = txt.charAt(0);
                txt = txt.split("=", 2)[1];//Split at first '='
                if(txt==null||"".equals(txt))
                    return null;
                if(txt.contains(":")){
                    Item item = GameData.getItemRegistry().getObject(txt);
                    if(item == null){
                        Block block = GameData.getBlockRegistry().getObject(txt);
                        if(block==Blocks.air)
                            return null;
                        else
                            inputs[2*i-2] = block;
                    }else
                        inputs[2*i-2] = item;
                }else{
                    inputs[2*i-2] = txt;
                }
            }
            return new ShapedOreRecipe(output, inputs);
        }
        return null;
    }
}
