package rbasamoyai.createbigcannons.crafting;

import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.createbigcannons.CBCBlockEntities;

public class CannonCastBlock extends Block implements ITE<CannonCastBlockEntity> {
	
	public CannonCastBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean dropContents) {
		if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
			this.withTileEntityDo(level, pos, CannonCastBlockEntity::destroyCastMultiblockAtLayer);
		}
	}
	
	@Override public VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
	@Override public boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) { return false; }
	@Override public float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) { return 0.8f; }
	
	@Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
	@Override public PushReaction getPistonPushReaction(BlockState state) { return PushReaction.BLOCK; }

	@Override public Class<CannonCastBlockEntity> getTileEntityClass() { return CannonCastBlockEntity.class; }
	@Override public BlockEntityType<? extends CannonCastBlockEntity> getTileEntityType() { return CBCBlockEntities.CANNON_CAST.get(); }

}