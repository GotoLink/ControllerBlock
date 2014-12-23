package nekto.controller.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import nekto.controller.tile.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public abstract class ItemBase extends Item {
    public final static String KEYTAG = "Control";
    public final static String MESS = "chat.item.message";
    public final static String MESSAGE_0 = MESS + 1;
    public String MESSAGE_1 = StatCollector.translateToLocal(MESS + 2) + " " + getControlName() + " at ";
    public final static String MESSAGE_2 = MESS + 3;
    public final static String MESSAGE_3 = MESS + 4;
    public String MESSAGE_4 = StatCollector.translateToLocal(MESS + "5.part1") + " " + getControlName() + " " + StatCollector.translateToLocal(MESS + "5.part2");
    public final static String MESSAGE_5 = MESS + 6;
    public final static String MESSAGE_6 = MESS + 7;
    public final static String MESSAGE_7 = MESS + 8;

    public ItemBase() {
        super();
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(KEYTAG)) {
            int[] pos = stack.getTagCompound().getIntArray(KEYTAG);
            par3List.add(MESSAGE_1 + pos[0] + ", " + pos[1] + ", " + pos[2]);
        } else {
            par3List.add(MESSAGE_4);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocal(getUnlocalizedName(stack) + ".name");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
            if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;
                TileEntityBase<?> newControl = null;
                if (isController(i, j, k, world)) {
                    newControl = (TileEntityBase<?>) world.getTileEntity(i, j, k);
                }
                if (itemStack.hasTagCompound() && itemStack.stackTagCompound.hasKey(KEYTAG)) {
                    int[] pos = itemStack.getTagCompound().getIntArray(KEYTAG);
                    //Try to find the old controller block to set its linker
                    if (!isController(pos[0], pos[1], pos[2], world))//It had data on a block that doesn't exist anymore
                    {
                        itemStack.getTagCompound().removeTag(KEYTAG);
                        player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_2, getControlName()));
                        return itemStack;
                    }
                    if (newControl != null) {
                        if (!onControlUsed(newControl, player, itemStack, (TileEntityBase<?>) world.getTileEntity(pos[0], pos[1], pos[2])))
                            player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_3, getControlName(), getItemStackDisplayName(itemStack)));
                        //Another player might be editing, let's avoid any issue and do nothing.
                    } else if (!world.isAirBlock(i, j, k)) {
                        onBlockSelected(itemStack, player, world, world.getBlock(i, j, k), i, j, k, world.getBlockMetadata(i, j, k), (TileEntityBase<?>) world.getTileEntity(pos[0], pos[1], pos[2]));
                    }
                } else if (newControl != null && newControl.getLinker() == null) {
                    player.addChatComponentMessage(new ChatComponentText(MESSAGE_1 + i + ", " + j + ", " + k));
                    newControl.setLinker(itemStack);
                    setEditAndTag(getStartData(i, j, k), itemStack, newControl);
                } else {
                    player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_0, getItemStackDisplayName(itemStack)));
                    player.addChatComponentMessage(new ChatComponentText(MESSAGE_4));
                }
            }
        }
        return itemStack;
    }

    public static boolean isInCornerMode(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey("Mode") && stack.getTagCompound().getBoolean("Mode"));
    }

    public static void setCornerMode(ItemStack stack, boolean bool) {
        NBTTagCompound tag = new NBTTagCompound();
        if (stack.hasTagCompound())
            tag = stack.getTagCompound();
        tag.setBoolean("Mode", bool);
        stack.setTagCompound(tag);
    }

    protected abstract Class<? extends TileEntityBase<?>> getControl();

    protected abstract String getControlName();

    protected int[] getStartData(int par4, int par5, int par6) {
        return new int[]{par4, par5, par6};
    }

    /**
     * Fired if player selected a block which isn't a valid control
     *
     * @param player the player who selected a block
     * @param id     blockID from {@link World#getBlock(int, int, int)}
     * @param meta   block metadata {@link World#getBlockMetadata(int, int, int)}
     */
    protected void onBlockSelected(ItemStack stack, EntityPlayer player, World world, Block id, int par4, int par5, int par6, int meta, TileEntityBase link) {
        link.setEditing(true);
        if (player.capabilities.isCreativeMode || id != Blocks.bedrock/* bedrock case out */) {
            if (!isInCornerMode(stack) && !player.isSneaking())
                link.add(player, id, par4, par5, par6, meta, true);
            else {
                int[] corner;
                if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Corner")) {
                    corner = new int[]{par4, par5, par6};
                    NBTTagCompound tag = new NBTTagCompound();
                    if (stack.hasTagCompound())
                        tag = stack.getTagCompound();
                    tag.setIntArray("Corner", corner);
                    stack.setTagCompound(tag);
                    player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_5, par4, par5, par6));
                } else {
                    corner = stack.getTagCompound().getIntArray("Corner");
                    if (!Arrays.equals(corner, new int[]{par4, par5, par6})) {
                        onMultipleSelection(player, world, corner, new int[]{par4, par5, par6}, link);
                        player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_6));
                    } else
                        player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_7));
                    stack.getTagCompound().removeTag("Corner");
                }
            }
        }
    }

    /**
     * Helper function to set data into the item and its Control in editing mode
     *
     * @param pos           Data to set into the item {@link NBTTagCompound}
     * @param par1ItemStack The item that will get the data
     */
    protected void setEditAndTag(int[] pos, ItemStack par1ItemStack, TileEntityBase link) {
        link.setEditing(true);
        NBTTagCompound tag = new NBTTagCompound();
        if (par1ItemStack.hasTagCompound())
            tag = par1ItemStack.getTagCompound();
        tag.setIntArray(KEYTAG, pos);
        par1ItemStack.setTagCompound(tag);
    }

    /**
     * Check for a controller on the coordinates
     *
     * @return True only if there is a tile entity which extends from
     * {@link #getControl()}
     */
    private boolean isController(int x, int y, int z, World world) {
        return getControl().isInstance(world.getTileEntity(x, y, z));
    }

    /**
     * Fired if corner mode is true or player is sneaking and selected two
     * different corner blocks
     *
     * @param corner    the first selected block position
     * @param endCorner the second selected block position
     */
    private void onMultipleSelection(EntityPlayer player, World world, int[] corner, int[] endCorner, TileEntityBase link) {
        int temp;
        //Sort the corners
        for (int i = 0; i < corner.length; i++) {
            if (corner[i] > endCorner[i]) {
                temp = corner[i];
                corner[i] = endCorner[i];
                endCorner[i] = temp;
            }
        }
        //Corner is now minimum, endCorner is maximum
        for (int x = corner[0]; x <= endCorner[0]; x++)
            for (int y = corner[1]; y <= endCorner[1]; y++)
                for (int z = corner[2]; z <= endCorner[2]; z++) {
                    if (!world.isAirBlock(x, y, z))
                        link.add(player, world.getBlock(x, y, z), x, y, z, world.getBlockMetadata(x, y, z), false);
                }
    }

    /**
     * What should happen if the selected {@link TileEntityBase} by a player is marked as already used by someone else
     *
     * @param newControl The selected TileEntity
     * @param player     The player doing the use
     * @param stack      The ItemStack used by player
     * @param old        stored in the ItemStack TagCompound
     * @return false to send {@link #MESSAGE_3} to player
     */
    protected boolean onControlUsed(TileEntityBase<?> newControl, EntityPlayer player, ItemStack stack, TileEntityBase old) {
        if (newControl.getLinker() == null) {
            if (old != newControl) {
                old.setEditing(false);
                old.setLinker(null);
                player.addChatComponentMessage(new ChatComponentTranslation(MESSAGE_2, getControlName()));
            }
            newControl.setLinker(stack);
            player.addChatComponentMessage(new ChatComponentText(MESSAGE_1 + newControl.xCoord + ", " + newControl.yCoord + ", " + newControl.zCoord));
            onReplaceControl(stack, newControl);
            return true;
        }
        return false;
    }

    protected void onReplaceControl(ItemStack stack, TileEntityBase<?> tempTile) {
        setEditAndTag(new int[]{tempTile.xCoord, tempTile.yCoord, tempTile.zCoord}, stack, tempTile);
    }
}
