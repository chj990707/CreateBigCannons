package rbasamoyai.createbigcannons.munitions.autocannon.ammo_container;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.base.ItemStackServerData;
import rbasamoyai.createbigcannons.base.SimpleValueContainer;
import rbasamoyai.createbigcannons.index.CBCMenuTypes;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonAmmoType;

public class AutocannonAmmoContainerMenu extends AbstractContainerMenu implements SimpleValueContainer {

	private static final ResourceLocation TRACER_SLOT = CreateBigCannons.resource("item/tracer_slot");

	public static AutocannonAmmoContainerMenu getServerMenu(int id, Inventory playerInv, ItemStack stack) {
		AutocannonAmmoContainerContainer ct = new AutocannonAmmoContainerContainer(stack);
		return new AutocannonAmmoContainerMenu(CBCMenuTypes.AUTOCANNON_AMMO_CONTAINER.get(), id, playerInv, ct, new ItemStackServerData(stack, "TracerSpacing"));
	}

	public static AutocannonAmmoContainerMenu getClientMenu(MenuType<AutocannonAmmoContainerMenu> type, int id, Inventory playerInv, FriendlyByteBuf buf) {
		ContainerData data = new SimpleContainerData(1);
		data.set(0, buf.readVarInt());
		ItemStack item = buf.readItem();
		return new AutocannonAmmoContainerMenu(type, id, playerInv, new AutocannonAmmoContainerContainer(item), data);
	}

	private final AutocannonAmmoContainerContainer container;
	private final ContainerData data;

	protected AutocannonAmmoContainerMenu(MenuType<? extends AutocannonAmmoContainerMenu> type, int id, Inventory playerInv,
										  AutocannonAmmoContainerContainer ct, ContainerData data) {
		super(type, id);

		this.addSlot(new AutocannonAmmoContainerMenuSlot(ct, AutocannonAmmoContainerContainer.AMMO_SLOT, 32, 26));
		this.addSlot(new AutocannonAmmoContainerMenuSlot(ct, AutocannonAmmoContainerContainer.TRACER_SLOT, 59, 26) {
			@Nullable
			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, TRACER_SLOT);
			}
		});

		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				this.addSlot(new Slot(playerInv, row * 9 + col + 9, col * 18 + 8, row * 18 + 105));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInv, i, i * 18 + 8, 163));
		}

		this.addDataSlots(data);
		this.data = data;
		this.container = ct;
	}

	@Override public boolean stillValid(Player player) { return true; }

	public int getValue() { return this.data.get(0); }

	@Override public void setValue(int value) { this.data.set(0, value); }

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return super.quickMoveStack(player, index);
	}

	public boolean isFilled() { return this.container.getType() != AutocannonAmmoType.NONE; }

}