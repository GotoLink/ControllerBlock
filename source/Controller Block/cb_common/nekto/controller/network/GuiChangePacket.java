package nekto.controller.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public final class GuiChangePacket implements IMessage {

    public int[] data;
    public boolean remote;

    @SuppressWarnings("UnusedDeclaration")
    public GuiChangePacket(){}

    public GuiChangePacket(boolean remote, int...data){
        this.remote = remote;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = new int[buf.readInt()];
        remote = buf.readBoolean();
        for (int id = 0; id < data.length; id++)
            data[id] = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(data.length);
        buf.writeBoolean(remote);
        for (int id : data)
            buf.writeInt(id);
    }
}
