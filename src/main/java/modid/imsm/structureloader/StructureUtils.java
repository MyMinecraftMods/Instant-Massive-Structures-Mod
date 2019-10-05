package modid.imsm.structureloader;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StructureUtils
{
	public static BlockPos getWorldPos(BlockPos structPos, BlockPos structCenter, BlockPos harvestPos)
	{
		return harvestPos.add(structPos).subtract(structCenter);
	}

	public static BlockPos getWorldPos(BlockPos structPos, Vec3d structCenter, Vec3d harvestPos)
	{
		return new BlockPos(getWorldPos(new Vec3d(structPos.getX() + 0.5, structPos.getY(), structPos.getZ() + 0.5), structCenter, harvestPos));
	}

	public static Vec3d getWorldPos(Vec3d structPos, Vec3d structCenter, Vec3d harvestPos)
	{
		return harvestPos.add(structPos).subtract(structCenter);
	}

	public static boolean setBlock(BlockPlacer blockPlacer, BlockState blockState, BlockPos structPos, Vec3d structCenter, Vec3d harvestPos)
	{
		return blockPlacer.add(blockState, StructureUtils.getWorldPos(structPos, structCenter, harvestPos));
	}

	public static void setTileEntity(World world, CompoundNBT tileEntity, BlockPos structPos, Vec3d structCenter, Vec3d harvestPos)
	{
		BlockPos pos = getWorldPos(structPos, structCenter, harvestPos);
		BlockState blockState = world.getBlockState(pos);

		world.removeTileEntity(pos);
		BlockPos chunkPos = new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
		TileEntity blockTileEntity = world.getChunk(pos).getTileEntity(chunkPos);

		blockTileEntity = blockState.getBlock().createTileEntity(blockState, world);
		blockTileEntity.read(tileEntity);
		blockTileEntity.setPos(pos);
		blockTileEntity.setWorld(world);

		world.setTileEntity(pos, blockTileEntity);
		blockTileEntity.updateContainingBlockInfo();
	}

	public static void setTileEntity(World world, TileEntity tileEntity, Vec3d structCenter, Vec3d harvestPos)
	{
		try{
		BlockPos pos = getWorldPos(tileEntity.getPos(), structCenter, harvestPos);
		world.removeTileEntity(pos);
		tileEntity.setPos(pos);
		tileEntity.setWorld(world);
		world.setTileEntity(pos, tileEntity);
		} catch (ArrayIndexOutOfBoundsException e){
		}
	}

	public static void setEntity(World world, Entity entity, Vec3d structCenter, Vec3d harvestPos)
	{
		Vec3d pos = getWorldPos(entity.getPositionVector(), structCenter, harvestPos);
		entity.setPosition(pos.x, pos.y, pos.z);
		world.addEntity(entity);
	}
}
