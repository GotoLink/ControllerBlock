package nekto.controller.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nekto.controller.item.ItemBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public abstract class TileEntityBase<e> extends TileEntity implements IInventory {
	private ItemStack[] items;
	private boolean previousState = false;
	private List<e> baseList;
	private ItemStack linker = null;
	private boolean editing;
	private float orbRotation = 0, hoverHeight = 0;

	public TileEntityBase(int size) {
		this.items = new ItemStack[size];
		this.setBaseList(new ArrayList<e>());
	}

	@Override
	public void updateEntity() {
        if(!worldObj.isRemote){
            if (getBaseList().size() > 0) {
                boolean powered = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0;
                if (powered != isPowered()) {
                    setState(powered);
                    onRedstoneChange();
                } else {
                    tick();
                }
            }
        }else{
            this.hoverHeight += 3;
            this.orbRotation += 3;
            if (this.orbRotation > 360) {
                this.orbRotation -= 360;
            }
        }
	}

    public void tick(){}

	public float getRotation() {
		return this.orbRotation;
	}

	public float getHoverHeight() {
		return this.hoverHeight;
	}

	/**
	 * Add or remove block from list given by {@link #getBlockList()}
	 * 
	 * @param player
	 *            The player sending the command
	 * @param block
	 *            From {@link World#getBlock(int,int,int)}
	 * @param blockMetadata
	 *            From {@link World#getBlockMetadata(int,int,int)}
	 * @param send
	 *            If message should be sent to the player chat
	 */
	public void add(EntityPlayer player, Block block, int x, int y, int z, int blockMetadata, boolean send) {
		Object[] temp = new Object[] { block, x, y, z, blockMetadata };
		boolean removed = removeFromList(getBlockList().listIterator(), temp);
		if (send)
			sendMessage(player, removed, temp);
		if (!removed)
			getBlockList().add(temp);
	}

	/**
	 * Remove given array from given Iterator
	 * 
	 * @param itr
	 *            Iterator assuming it has arrays
	 * @param temp
	 *            The array to search and remove
	 * @return true if given array has been found and removed
	 */
	private static boolean removeFromList(Iterator<Object[]> itr, Object[] temp) {
		while (itr.hasNext()) {
			if (Arrays.equals(itr.next(), temp)) {
				itr.remove();
				return true;
			}
		}
		return false;
	}

	private void sendMessage(EntityPlayer player, boolean removed, Object[] data) {
		if (removed) {
			player.addChatComponentMessage(new ChatComponentText("Removed " + dataAsString(data) + " from " + getListName()));
		} else {
			player.addChatComponentMessage(new ChatComponentText("Added " + dataAsString(data) + " to " + getListName()));
		}
	}

	/**
	 * Prints data with separator for easier to read messages
	 * 
	 * @param data to print
	 * @return an easier to read message
	 */
	private static String dataAsString(Object[] data) {
		return ((Block)data[0]).getLocalizedName() + data[4] + " [" + data[1] + "," + data[2] + "," + data[3] + "] ";
	}

	protected String getListName() {
		return "list";
	}

	public void setState(boolean active) {
		this.previousState = active;
	}

	public boolean isPowered() {
		return this.previousState;
	}

	public List<e> getBaseList() {
		return baseList;
	}

	public void setBaseList(List<e> baseList) {
		this.baseList = baseList;
	}

	public void setLinker(ItemStack par1Linker) {
		this.linker = par1Linker;
	}

	public ItemStack getLinker() {
		return this.linker;
	}

	public boolean isEditing() {
		return this.editing;
	}

	public void setEditing(boolean b) {
		this.editing = b;
	}

    protected void setUnactiveBlocks(Iterator<Object[]> itr) {
        while (itr.hasNext()) {
            Object[] block = itr.next();
            if (block != null && block.length > 4 && worldObj.getBlock((Integer) block[1], (Integer) block[2], (Integer) block[3]) != block[0]) {
                worldObj.setBlock((Integer) block[1], (Integer) block[2], (Integer) block[3], (Block) block[0], (Integer) block[4], 3);
            }
        }
    }

    protected void setActiveBlocks(Iterator<Object[]> itr) {
        while (itr.hasNext()) {
            Object[] block = itr.next();
            if (block != null && block.length > 4) {
                if (worldObj.getBlock((Integer) block[1], (Integer) block[2], (Integer) block[3]) != block[0])
                    itr.remove();
                else
                    worldObj.setBlockToAir((Integer) block[1], (Integer) block[2], (Integer) block[3]);
            }
        }
    }

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("length", getBaseList().size());
		par1NBTTagCompound.setBoolean("active", this.previousState);
		par1NBTTagCompound.setBoolean("edit", this.isEditing());
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < this.items.length; ++i) {
			if (this.items[i] != null) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte) i);
				this.items[i].writeToNBT(compound);
				list.appendTag(compound);
			}
		}
		par1NBTTagCompound.setTag("Items", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.previousState = par1NBTTagCompound.getBoolean("active");
		this.setEditing(par1NBTTagCompound.getBoolean("edit"));
		this.getBaseList().clear();
		NBTTagList list = par1NBTTagCompound.getTagList("Items", 10);
		this.items = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			int j = compound.getByte("Slot") & 255;
			if (j >= 0 && j < this.items.length) {
				this.items[j] = ItemStack.loadItemStackFromNBT(compound);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return this.items.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack aitemstack[] = items;
		if (aitemstack[i] != null) {
			if (aitemstack[i].stackSize <= j) {
				ItemStack itemstack = aitemstack[i];
				aitemstack[i] = null;
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = aitemstack[i].splitStack(j);
			if (aitemstack[i].stackSize == 0)
				aitemstack[i] = null;
			markDirty();
			return itemstack1;
		} else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		items[i] = itemstack;
		markDirty();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return !entityplayer.isDead && entityplayer.getDistanceSq(xCoord, yCoord, zCoord) <= 64D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/**
	 * TileEntity specific texture, to use in the TileEntity renderer
	 * 
	 * @return Path of the texture to use (inside textures folder)
	 */
	public String getTexture() {
		return "models/" + getName() + ".png";
	}

	/**
	 * User-friendly name of the TileEntity, to use in the Remote GUI Also used
	 * for texture path
	 */
	public abstract String getName();

	/**
	 * List to which blocks are added, as int arrays It isn't saved in NBT by this class.
	 */
	protected abstract List<Object[]> getBlockList();

    /**
     * Called when redstone state has changed
     */
    protected abstract void onRedstoneChange();
}
