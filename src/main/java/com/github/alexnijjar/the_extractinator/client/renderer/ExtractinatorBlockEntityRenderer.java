package com.github.alexnijjar.the_extractinator.client.renderer;

import com.github.alexnijjar.the_extractinator.blocks.ExtractinatorBlock;
import com.github.alexnijjar.the_extractinator.blocks.entity.ExtractinatorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

@Environment(value = EnvType.CLIENT)
public class ExtractinatorBlockEntityRenderer implements BlockEntityRenderer<ExtractinatorBlockEntity> {

    public ExtractinatorBlockEntityRenderer() {}

    @Override
    public void render(ExtractinatorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // Get grinder model.
        World world = entity.getWorld();
        if (world == null) return;
        BakedModel model = entity.getModel(world);

        Random random = new Random();

        // Make grinder face player.
        Direction direction = Direction.UP;
        BlockState blockState = world.getBlockState(entity.getPos());
        if (blockState.getBlock() instanceof ExtractinatorBlock) {
            direction = blockState.get(ExtractinatorBlock.FACING);
        }

        // Align grinder with extractinator.
        matrices.push();
        matrices.multiply(direction.getRotationQuaternion());
        // rotate grinder upright.
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));

        switch (direction) {
            case NORTH -> {}
            case EAST -> matrices.translate(0, 0, -1);
            case SOUTH -> matrices.translate(-1, 0, -1);
            case WEST -> matrices.translate(-1, 0, 0);
        }

        matrices.translate(0, 0.1, 0);

        // Grinder oscillation.
        double sine = Math.sin((world.getTime() + tickDelta) / 1.5);
        matrices.translate(0, sine * 0.09, 0);

        // Render model.
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
        List<BakedQuad> quads = model.getQuads(null, null, random);

        for (BakedQuad quad : quads) {
            vertexConsumer.quad(matrices.peek(), quad, 1, 1, 1, light, overlay);
        }

        matrices.pop();

        // 156 quads/tick
//        TheExtractinator.LOGGER.info(quads.size());
    }
}
