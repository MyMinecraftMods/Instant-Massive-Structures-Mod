package modid.imsm.structureloader;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class DropFuncBlock
{
	public static boolean setBlock(World world, IBlockState state, BlockPos pos, boolean update, boolean isLive)
	{
		 setBlock(world, state, pos, null, update, isLive);
		 return true;
	}

	private static boolean setBlock(World world, IBlockState state, BlockPos pos, NBTTagCompound tileEntity, boolean update, boolean isLive)
	{
		//try{
		Chunk chunk = world.getChunk(pos);
		if (world.getBlockState(pos) != state)
            world.setBlockState(pos, state, 2);

        if (update && world instanceof World)
            ((World) world).markAndNotifyBlock(pos, ((World) world).getChunk(pos),
                world.getBlockState(pos), state, 3);

		//Chunk chunkClient = Minecraft.getInstance().world.getChunkFromBlockCoords(pos);
		//ExtendedBlockStorage storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4];
		//System.out.println(pos.getX()+", "+pos.getY()+", "+pos.getZ());
		//if (storageArray == null) storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4] = new ExtendedBlockStorage(pos.getY() >> 4 << 4, !world.provider.getHasNoSky());
		
		//if (storageArray.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15).getBlock() != state.getBlock() || state.getBlock() instanceof BlockColored)
		//{
		//	IBlockState oldState = world.getBlockState(pos);
			//storageArray.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
			//chunkClient.getBlockStorageArray()[pos.getY() >> 4].set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
		//	storageArray.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
			
			//world.setBlockState(pos, state);
			//int timesFaster=1;
			//if(!isLive || IMSM.eventHandler.lightUpdate.getProcessesSize()<200 /*&& pos.getX()%timesFaster==0 && pos.getY()%timesFaster==0 && pos.getZ()%timesFaster==0*/){
				//if(!isLive || IMSM.eventHandler.lightUpdate.getProcessesSize()<200){
				//if(!world.isRemote){
						//IMSM.eventHandler.lightUpdate.processes.add(pos);
				//}
				//}
				//world.checkLight(pos);
				//world.setLightFor(EnumSkyBlock.SKY, pos, 15);
				//world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
				//System.out.println((Minecraft.getInstance().world==world)+"=="+(Minecraft.getInstance().getIntegratedServer().worldServerForDimension(0)==world));
			//}
			//world.notifyLightSet(pos);
				//world.checkLight(pos);
			//world.checkLightFor(EnumSkyBlock.SKY, pos);
			//if(world.isRemote){
			//	IMSM.eventHandler.lightUpdate.addClientProcess(pos);
			//} else {
			//	IMSM.eventHandler.lightUpdate.addServerProcess(pos);
				//IMSM.eventHandler.lightUpdate.addClientProcess(pos);
			//}
			/*if(world.isDaytime()){
			world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);world.setLightFor(EnumSkyBlock.SKY, pos, 15);
			} else {
				Block block = state.getBlock();
				if(block instanceof BlockBeacon || block instanceof BlockFire || block instanceof BlockGlowstone || block instanceof BlockTorch || block instanceof BlockRedstoneDiode || block instanceof BlockRedstoneLight || block instanceof BlockPortal){
world.checkLight(pos);
			}else {
				world.setLightFor(EnumSkyBlock.BLOCK, pos, 4);world.setLightFor(EnumSkyBlock.SKY, pos, 4);
			}
			}*/
				//world.setBlockState(pos, oldState)
			//	chunk.setChunkModified();
			//chunkClient.setChunkModified();
			//world.markBlockRangeForRenderUpdate(pos,pos);//TODO: Stil a hack
			//Minecraft.getInstance().world.markBlockRangeForRenderUpdate(pos,pos);//TODO: Stil a hack
			/*if(world.isRemote){
			//world.checkLight(pos);
				world.setLightFor(EnumSkyBlock.SKY, pos, 15);
				world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
			}*/
			//world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
			/*if (update || state.getBlock()!=Blocks.air)*/ //world.markAndNotifyBlock(pos, chunk, state, oldState, 3);
			//Minecraft.getInstance().world.markAndNotifyBlock(pos, chunk, state, oldState, 3);
		//}

		if (tileEntity != null && state.getBlock().hasTileEntity(state) && !isLive)
		{
			world.removeTileEntity(pos);
			BlockPos chunkPos = new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
			TileEntity blockTileEntity = chunk.getTileEntity(chunkPos, Chunk.EnumCreateEntityType.CHECK);

			blockTileEntity = state.getBlock().createTileEntity(state, world);
			blockTileEntity.read(tileEntity);
			world.setTileEntity(pos, blockTileEntity);
			blockTileEntity.updateContainingBlockInfo();
		}
		//} catch (Exception e){
		//	return false;
		//}
		return true;
	}

	public static synchronized void setTileEntity(World world, IBlockState state, BlockPos pos, NBTTagCompound nbt)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            tileEntity.handleUpdateTag(nbt);
            if (world instanceof World)
                ((World) world).setTileEntity(pos, tileEntity);
        } else {
            System.out.println("Error setting tile entity for block: " + state);
        }
		/*if (tileEntity != null && state.getBlock().hasTileEntity(state))
		{
			Chunk chunk = world.getChunk(pos);
			ExtendedBlockStorage storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4];
			if (storageArray == null) storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4] = new ExtendedBlockStorage(pos.getY() >> 4 << 4, !world.provider.getHasNoSky());

			world.removeTileEntity(pos);
			BlockPos chunkPos = new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
			TileEntity blockTileEntity = chunk.getTileEntity(chunkPos, Chunk.EnumCreateEntityType.CHECK);

			blockTileEntity = state.getBlock().createTileEntity(state, world);
			blockTileEntity.read(tileEntity);
			world.setTileEntity(pos, blockTileEntity);
			blockTileEntity.updateContainingBlockInfo();
		}*/
	}
}
