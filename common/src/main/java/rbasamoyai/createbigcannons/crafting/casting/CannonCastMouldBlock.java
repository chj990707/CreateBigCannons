package rbasamoyai.createbigcannons.crafting.casting;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.createbigcannons.index.CBCBlocks;

import java.util.function.Supplier;

public class CannonCastMouldBlock extends Block {

	public static final BooleanProperty SAND = BooleanProperty.create("sand");
	private final VoxelShape noSandShape;
	private final Supplier<CannonCastShape> cannonShape;

	public CannonCastMouldBlock(Properties properties, VoxelShape noSandShape, Supplier<CannonCastShape> cannonShape) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(SAND, false));
		this.noSandShape = noSandShape;
		this.cannonShape = cannonShape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
		return state.getValue(SAND) ? Shapes.or(this.noSandShape, Shapes.block()) : this.noSandShape;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(SAND);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack stack = player.getItemInHand(hand);
		if (state.getValue(SAND) && stack.isEmpty()) {
			if (this.isSurroundingAreaCompleteForTransformation(state, level, pos)) {
				level.setBlock(pos, CBCBlocks.CANNON_CAST.getDefaultState(), 11);
				if (!level.isClientSide) {
					if (level.getBlockEntity(pos) instanceof AbstractCannonCastBlockEntity cast) cast.initializeCastMultiblock(this.cannonShape.get());
					ItemStack resultStack = new ItemStack(this.asItem());
					if (!player.addItem(resultStack) && !player.isCreative()) {
						ItemEntity item = player.drop(resultStack, false);
						if (item != null) {
							item.setNoPickUpDelay();
							item.setTarget(player.getUUID());
						}
					}
				}
				level.playSound(player, pos, SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 1.0f, 0.0f);
			} else {
				level.setBlock(pos, state.setValue(SAND, false), 3);
				if (!level.isClientSide) {
					ItemStack resultStack = CBCBlocks.CASTING_SAND.asStack();
					if (!player.addItem(resultStack) && !player.isCreative()) {
						ItemEntity item = player.drop(resultStack, false);
						if (item != null) {
							item.setNoPickUpDelay();
							item.setTarget(player.getUUID());
						}
					}
				}
				level.playSound(player, pos, SoundEvents.SAND_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		} else if (!state.getValue(SAND) && CBCBlocks.CASTING_SAND.isIn(stack)) {
			level.setBlock(pos, state.setValue(CannonCastMouldBlock.SAND, true), 3);
			level.playSound(player, pos, SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 1.0f, 1.0f);
			if (!level.isClientSide && !player.isCreative()) {
				stack.shrink(1);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return InteractionResult.PASS;
	}

	protected boolean isSurroundingAreaCompleteForTransformation(BlockState state, Level level, BlockPos pos) {
		return !this.cannonShape.get().isLarge() || BlockPos.betweenClosedStream(pos.offset(-1, 0, -1), pos.offset(1, 0, 1)).filter(p -> !pos.equals(p)).map(level::getBlockState).allMatch(CBCBlocks.CASTING_SAND::has);
	}

}
