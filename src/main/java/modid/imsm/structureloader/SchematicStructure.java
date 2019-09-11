package modid.imsm.structureloader;

import java.io.DataInputStream;
import java.util.zip.GZIPInputStream;

import modid.imsm.core.IMSM;
import modid.imsm.core.StructureCreator;
import modid.imsm.userstructures.OutlineCreator;
import modid.imsm.worldgeneration.UndoCommand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SchematicStructure extends Structure
{
	
	boolean isLive;
	public SchematicStructure(String fileName, boolean isLive)
	{
		super(fileName);
		this.isLive=isLive;
		
	}
	
	public SchematicStructure(String fileName)
	{
		super(fileName,true);
		this.isLive=false;

	}
	
	private void wipeUndo(){
		UndoCommand.removedPositions.clear();
		UndoCommand.removedStates.clear();
	}
	

	// Blocks stored [y][z][x]
	private Block[][][] blocks;
	private int[][][] blockData;

	private NBTTagCompound[] entities;
	private NBTTagCompound[] tileEntities;
	private int blocksAdded;
	 World world; int posX,  posY,  posZ;
	BlockPlacer blockPlacer;
	Vec3d harvestPos;


	@Override
	public void process(World world, int posX, int posY, int posZ)
	{
		this.world=world;this.posX=posX;this.posY=posY;this.posZ=posZ;
		//Minecraft.getInstance().player.sendChatMessage("Please be patient, I'm just creating "+(height*width*length)+" blocks for the structure...");
		Block blk = Blocks.AIR;
		   // Make a position.
		   BlockPos pos0 = new BlockPos(posX,posY,posZ);
		   // Get the default state(basically metadata 0)
		   IBlockState state0=blk.getDefaultState();
		   // set the block
		   world.setBlockState(pos0, state0);
		blocksAdded=0;
		posX-=length/2-1;
		posZ-=width/2-1;
		Vec3d harvestPos = new Vec3d(posX + 0.5, posY, posZ + 0.5);
		BlockPlacer blockPlacer = new BlockPlacer(world,isLive);
		this.blockPlacer=blockPlacer;
		this.harvestPos=harvestPos;
		wipeUndo();
		
		//System.out.println("Blocks");
		for (int y = 0; y < this.height; y++)
		{
			for (int z = 0; z < this.width; z++)
			{
				for (int x = 0; x < this.length; x++)
				{
					//System.out.println("DATA=="+this.blocks[y][z][x]+blockPlacer+this.getCenterPos()+harvestPos);
					if (this.blockMode.equals("overlay") && this.blocks[y][z][x] == Blocks.AIR) continue;
					//StructureUtils.setBlock(blockPlacer2, this.blocks[y][z][x].getStateFromMeta(this.blockData[y][z][x]), new BlockPos(x, y, z), this.getCenterPos(), harvestPos);
					StructureUtils.setBlock(blockPlacer, this.blocks[y][z][x].getStateFromMeta(this.blockData[y][z][x]), new BlockPos(x, y, z), this.getCenterPos(), harvestPos);
				}
					//BlockPlaceHandler.setBlock(serverWorld,  new BlockPos(this.posX+x, this.posY+y,this.posZ-z), this.blocks[y][z][x].getStateFromMeta(this.blockData[y][z][x]));
					//BlockPlaceHandler.setBlock(world,  new BlockPos(this.posX+x, this.posY+y,this.posZ-z), this.blocks[y][z][x].getStateFromMeta(this.blockData[y][z][x]));}
			}
		}
	}
	
	public void initSingleBlockPlacer(World world, int posX, int posY, int posZ){
		this.world=world;this.posX=posX;this.posY=posY;this.posZ=posZ;
		//Minecraft.getInstance().player.sendChatMessage("Please be patient, I'm just creating "+(height*width*length)+" blocks for the structure...");
		Block blk = Blocks.AIR;
		
		   // Make a position.
		   BlockPos pos0 = new BlockPos(this.posX,this.posY,this.posZ);
		   // Get the default state(basically metadata 0)
		   IBlockState state0=blk.getDefaultState();
		   // set the block
		   this.world.setBlockState(pos0, state0);
	
		blocksAdded=0;
		
		this.posX-=length/2-1;
		this.posZ-=width/2-1;
		
		
	this.harvestPos = new Vec3d(this.posX + 0.5, this.posY, this.posZ + 0.5);

		this.blockPlacer = new BlockPlacer(world,isLive);

		wipeUndo();
	}
	
	public boolean placeBlock(StructureCreator daddy)
	{
					//System.out.println("DATA=="+this.blocks[y][z][x]+blockPlacer+this.getCenterPos()+harvestPos);
		if(this.blocks==null){
			//System.out.println("Block array is NULL!");
			return false;
		}
					if (this.blockMode.equals("overlay") && this.blocks[daddy.i][daddy.j][daddy.k] == Blocks.AIR){
						
					} else {
						BlockPos position = new BlockPos(daddy.k, daddy.i,daddy.j);
						if(IMSM.eventHandler.serverCreators.get(IMSM.eventHandler.serverCreators.size()-1) == daddy){
							BlockPos translatedPosition = StructureUtils.getWorldPos(position, this.getCenterPos(), harvestPos);
							UndoCommand.removedStates.add(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getBlockState(translatedPosition));
						UndoCommand.removedPositions.add(translatedPosition);
						}
						//System.out.prinln(daddy.i+", "+daddy.j+", "+daddy.k+", "+height+", "+width+", "+length);
					StructureUtils.setBlock(this.blockPlacer, this.blocks[daddy.i][daddy.j][daddy.k].getStateFromMeta(this.blockData[daddy.i][daddy.j][daddy.k]), position, this.getCenterPos(), this.harvestPos);
					//BlockPlaceHandler.setBlock(serverWorld,  new BlockPos(daddy.x-daddy.k, daddy.y+daddy.i,daddy.z-daddy.j), this.blocks[daddy.i][daddy.j][daddy.k].getStateFromMeta(this.blockData[daddy.i][daddy.j][daddy.k]));
						//BlockPlaceHandler.setBlock(world,  new BlockPos(daddy.x-daddy.k, daddy.y+daddy.i,daddy.z-daddy.j), this.blocks[daddy.i][daddy.j][daddy.k].getStateFromMeta(this.blockData[daddy.i][daddy.j][daddy.k]));
					if(this.blocks[daddy.i][daddy.j][daddy.k] instanceof BlockChest){
						System.out.println("Chest at "+StructureUtils.getWorldPos(position, this.getCenterPos(), this.harvestPos).toString());
					}
					}
					daddy.k++;
					if(daddy.k>=this.length){
						daddy.k=0;
						//System.out.println("WUTTT "+daddy.i);
						daddy.j++;
						if(daddy.j>=this.width){
							daddy.j=0;
							daddy.i++;
							if(daddy.i>=this.height){
								return true;
							}
						}
					}
		return false;
	}	
	
	public void postProcess(){
		//System.out.println("Structure Postprocessed!");
		try{
		for (NBTTagCompound tileEntity : this.tileEntities){
			TileEntity tE;
			//System.out.println(tileEntity.getString("id"));
			if(tileEntity.getString("id").equals("Sign")){
			 TileEntitySign signEntity = new TileEntitySign();
			 for(int i = 0; i<signEntity.signText.length; i++){
				 signEntity.signText[i].appendText(tileEntity.getString("Text" + (i + 1)));
			 }
			 signEntity.setPos(new BlockPos(tileEntity.getInt("x"), tileEntity.getInt("y"), tileEntity.getInt("z")));
			 tE = signEntity;
			} else if(tileEntity.getString("id").equals("Chest")){
				TileEntityChest chestEntity = /*(TileEntityChest)TileEntity.createTileEntity(Minecraft.getInstance().getIntegratedServer(), tileEntity)*/new TileEntityChest();
				//System.out.println(tileEntity.getTagList("Items", tileEntity.getId()).toString());
				//System.out.println(tileEntity.toString());
				NBTTagList chestItemList = tileEntity.getList("Items", tileEntity.getId());
				for(int i = 0; i<chestItemList.size(); i++){
					Item item = Item.getItemById(chestItemList.getCompound(i).getInteger("id"));
					//System.out.println(chestItemList.getCompound(i).toString());
					//System.out.println(item.getUnlocalizedName());
					chestEntity.setInventorySlotContents(chestItemList.getCompound(i).getByte("Slot"), new ItemStack(item,chestItemList.getCompound(i).getByte("Count")));
					//System.out.println(new ItemStack(item,chestItemList.getCompound(i).getByte("Count")).toString()+" at "+chestItemList.getCompound(i).getByte("Slot"));
				}
				chestEntity.setPos(new BlockPos(tileEntity.getInt("x"), tileEntity.getInt("y"), tileEntity.getInt("z")));
				//System.out.println("Tile entity at "+Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getBlockState(StructureUtils.getWorldPos(new BlockPos(tileEntity.getInt("x"), tileEntity.getInt("y"), tileEntity.getInt("z")), this.getCenterPos(), this.harvestPos)).getBlock().tile);
				tE=chestEntity;
			}else {
			tE=TileEntity.func_190200_a(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension), tileEntity);
			}
			
			StructureUtils.setTileEntity(Minecraft.getInstance().world, tE, this.getCenterPos(), harvestPos);
			}
		for (NBTTagCompound entity : this.entities)
			StructureUtils.setEntity(Minecraft.getInstance().world, EntityList.createEntityFromNBT(entity, world), this.getCenterPos(), harvestPos);
		
		for (NBTTagCompound tileEntity : this.tileEntities){
			TileEntity tE;
		if(tileEntity.getString("id").equals("Sign")){
		 TileEntitySign signEntity = new TileEntitySign();
		 for(int i = 0; i<signEntity.signText.length; i++){
			 signEntity.signText[i].appendText(tileEntity.getString("Text" + (i + 1)));
		 }
		 signEntity.setPos(new BlockPos(tileEntity.getInt("x"), tileEntity.getInt("y"), tileEntity.getInt("z")));
		 tE = signEntity;
		}else if(tileEntity.getString("id").equals("Chest")){
				TileEntityChest chestEntity = /*(TileEntityChest)TileEntity.createTileEntity(Minecraft.getInstance().getIntegratedServer(), tileEntity)*/new TileEntityChest();
				//System.out.println(tileEntity.getTagList("Items", tileEntity.getId()).toString());
				//System.out.println(tileEntity.toString());
				NBTTagList chestItemList = tileEntity.getTagList("Items", tileEntity.getId());
				for(int i = 0; i<chestItemList.size(); i++){
					Item item = Item.getItemById(chestItemList.getCompound(i).getInteger("id"));
					//System.out.println(chestItemList.getCompound(i).toString());
					//System.out.println(item.getUnlocalizedName());
					chestEntity.setInventorySlotContents(chestItemList.getCompound(i).getByte("Slot"), new ItemStack(item,chestItemList.getCompound(i).getByte("Count")));
					//System.out.println(new ItemStack(item,chestItemList.getCompound(i).getByte("Count")).toString()+" at "+chestItemList.getCompound(i).getByte("Slot"));
				}
				chestEntity.setPos(new BlockPos(tileEntity.getInt("x"), tileEntity.getInt("y"), tileEntity.getInt("z")));
				//System.out.println("Tile entity at "+Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getBlockState(StructureUtils.getWorldPos(new BlockPos(tileEntity.getInt("x"), tileEntity.getInt("y"), tileEntity.getInt("z")), this.getCenterPos(), this.harvestPos)).getBlock().tile);
				tE=chestEntity;
			}else {
		tE=TileEntity.create(tileEntity);
		}
			StructureUtils.setTileEntity(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension), tE, this.getCenterPos(), harvestPos);
			//TODO: THE ABOVE LINE SHOULD NOT BE COMMENTED!!
		}
		for (NBTTagCompound entity : this.entities)
			StructureUtils.setEntity(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension), EntityList.createEntityFromNBT(entity, world), this.getCenterPos(), harvestPos);
		
		blockPlacer.processSpecialBlocks();
		
		world.markBlockRangeForRenderUpdate(new BlockPos(posX,posY,posZ),new BlockPos(posX+length, posY+height, posZ+width));
		
		
		/*if (this.blockUpdate){
			//blockPlacer2.update();
			blockPlacer.update();
		}*/
		} catch(Exception e){
			e.printStackTrace();
		}
		
		//Minecraft.getInstance().player.sendChatMessage("I just created "+blocksAdded+" out of "+(height*width*length)+" blocks in this structure!");
	}
	
	public void showOutline(int x, int modifierx, int y,int modifiery, int z, int modifierz, OutlineCreator creator){
		UndoCommand.removedStates.clear();
		UndoCommand.removedPositions.clear();
		for(int i=0; i<width; i++){ for(int j = 0; j<height; j++){ for(int k = 0; k<length; k++){ 
			if(i==0||j==0||k==0){
				if(i==modifierx&&j==modifiery&&k==modifierz) continue;
				
				BlockPos pos0 = new BlockPos(x-i+modifierx,y+j+modifiery,z-k+modifierz);
				
				
				if(IMSM.eventHandler.serverCreators.get(IMSM.eventHandler.serverCreators.size()-1) == creator){
					UndoCommand.removedStates.add(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getBlockState(pos0));
					UndoCommand.removedPositions.add(pos0);
				}
				
			Block blk = Blocks.GLASS;
			   // Make a position.
			   
			   
			   DropFuncBlock.setBlock(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension), blk.getDefaultState(), pos0, true, true);
			   
			   // Get the default state(basically metadata 0)
			   //IBlockState state0=blk.getDefaultState();
			   // set the block
			   //Minecraft.getInstance().world.setBlockState(pos0, state0);
			   //Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).setBlockState(pos0, state0);

			//worldIn.spawnEntityInWorld(new EntitySnowball(worldIn, x+i,y+j,z+k)); 
			}}} }
		UndoCommand.numBlocksUndoable=UndoCommand.removedStates.size();
	}
	
	public void removeOutline(int x, int modifierx, int y,int modifiery, int z, int modifierz){
		//System.out.println(UndoCommand.numBlocksUndoable+", "+UndoCommand.removedStates.size());
		if(UndoCommand.numBlocksUndoable == UndoCommand.removedStates.size()){
			//IMSM.eventHandler.serverCreators.add(new StructureRemover());
			UndoCommand.runCommand();
		} else {
		for(int i=0; i<width; i++){ for(int j = 0; j<height; j++){ for(int k = 0; k<length; k++){ 
			if(i==0||j==0||k==0){
			if(i==modifierx&&j==modifiery&&k==modifierz) continue;
			//if(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getBlockState(pos))
			Block blk = Blocks.AIR;
			   // Make a position.
			   BlockPos pos0 = new BlockPos(x-i+modifierx,y+j+modifiery,z-k+modifierz);
			   // Get the default state(basically metadata 0)
			   //IBlockState state0=blk.getDefaultState();
			   // set the block
			   //worldIn.setBlockState(pos0, state0);
			   
			   DropFuncBlock.setBlock(Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension), blk.getDefaultState(), pos0, true, true);

			//worldIn.spawnEntityInWorld(new EntitySnowball(worldIn, x+i,y+j,z+k)); 
			}}} }
		}
	}


	@Override
	public void readFromFile()
	{
		NBTTagCompound nbtTagCompound = null;
		DataInputStream dataInputStream;
		try
		{
			dataInputStream = new DataInputStream(new GZIPInputStream(this.fileStream));
			nbtTagCompound = CompressedStreamTools .read(dataInputStream);
			dataInputStream.close();
		}
		catch (Exception e)
		{
			System.err.println("Instant Massive Structures Mod: Error loading structure '" + this.fileName + "'");
			return;
		}

		// In schematics, length is z and width is x. Here it is reversed.
		this.length = nbtTagCompound.getShort("Width");
		this.width = nbtTagCompound.getShort("Length");
		this.height = nbtTagCompound.getShort("Height");

		int size = this.length * this.width * this.height;
		/*if (size > STRUCTURE_BLOCK_LIMIT)
		{
			System.err.println("Instant Massive Structures Mod: Error loading structure. The structure '" + this.fileName + "' (" + size + " blocks) exceeds the " + STRUCTURE_BLOCK_LIMIT + " block limit");
			return;
		}*/

		this.blocks = new Block[this.height][this.width][this.length];
		this.blockData = new int[this.height][this.width][this.length];

		byte[] blockIdsByte = nbtTagCompound.getByteArray("Blocks");
		byte[] blockDataByte = nbtTagCompound.getByteArray("Data");
		int x = 1, y = 1, z = 1;
		for (int i = 0; i < blockIdsByte.length; i++)
		{
			int blockId = (short) (blockIdsByte[i] & 0xFF);
			this.blocks[y - 1][z - 1][x - 1] = Block.getBlockById(blockId);
			this.blockData[y - 1][z - 1][x - 1] = blockDataByte[i];
			x++;
			if (x > this.length)
			{
				x = 1;
				z++;
			}
			if (z > this.width)
			{
				z = 1;
				y++;
			}
		}

		NBTTagList entityList = nbtTagCompound.getList("Entities", 10);
		this.entities = new NBTTagCompound[entityList.size()];
		for (int i = 0; i < entityList.size(); i++)
			this.entities[i] = entityList.getCompound(i);

		NBTTagList tileEntityList = nbtTagCompound.getList("TileEntities", 10);
		this.tileEntities = new NBTTagCompound[tileEntityList.size()];
		for (int i = 0; i < tileEntityList.size(); i++)
			this.tileEntities[i] = tileEntityList.getCompound(i);

		this.initCenterPos();
	}
}
