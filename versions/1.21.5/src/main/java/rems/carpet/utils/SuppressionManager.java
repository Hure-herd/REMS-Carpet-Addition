/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
 *
 * Carpet REMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet REMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet REMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.HashSet;
import java.util.Set;

public class SuppressionManager {
    private static final ThreadLocal<Set<BlockPos>> MARKED_POSITIONS = ThreadLocal.withInitial(HashSet::new);

    private static final ThreadLocal<Set<BlockPos>> MARKED_POSITIONS2 = ThreadLocal.withInitial(HashSet::new);

    public static void mark(BlockPos pos) {
        MARKED_POSITIONS.get().add(pos.toImmutable());
    }

    public static void mark2(BlockPos pos) {
        MARKED_POSITIONS2.get().add(pos.toImmutable());
    }

    public static boolean isMarked(BlockPos pos) {
        return MARKED_POSITIONS.get().contains(pos);
    }

    public static boolean isMarked2(BlockPos pos) {
        return MARKED_POSITIONS2.get().contains(pos);
    }

    public static void clear() {
        MARKED_POSITIONS.get().clear();
    }

    public static void clear2() {
        MARKED_POSITIONS2.get().clear();
    }

    private static final ThreadLocal<BlockPos> Pos = new ThreadLocal<>();
    private static final ThreadLocal<BlockState> State = new ThreadLocal<>();
    private static final ThreadLocal<BlockEntity> CapturedBE = new ThreadLocal<>();
    private static final ThreadLocal<BlockState> OldState = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> canRestore = ThreadLocal.withInitial(() -> false);

    public static void setRestorable(boolean allow) {
        canRestore.set(allow);
    }

    public static boolean isRestorable() {
        return canRestore.get();
    }

    public static void posmark(BlockPos pos) {
        if (pos == null) return;
        Pos.set(pos);
    }
    public static BlockPos getMarkedPos() {
        return Pos.get();
    }

    public static void statemark(BlockState state) {
        State.set(state);
    }

    public static BlockState getMarkedState() {
        return State.get();
    }

    public static void capturedBEmark(BlockEntity captured) {
        CapturedBE.set(captured);
    }

    public static BlockEntity getMarkedCapturedBE() {
        return CapturedBE.get();
    }

    public static void oldStatemark(BlockState oldState) {
        OldState.set(oldState);
    }

    public static BlockState getMarkedOldState() {
        return OldState.get();
    }

    public static void clears() {
        Pos.remove();
        State.remove();
        CapturedBE.remove();
        OldState.remove();
    }

    public static void forceSetBlockState(Chunk chunk, BlockPos pos, BlockState state) {
        int i = chunk.getSectionIndex(pos.getY());
        ChunkSection section = chunk.getSection(i);
        if (section != null) {
            section.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
        }
    }
}
