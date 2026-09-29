/*
 * This file is part of the REMS-Carpet-Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 Hureherd and contributors
 *
 * REMS-Carpet-Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * REMS-Carpet-Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with REMS-Carpet-Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet.utils.GlobalSoundSuppression;

public final class UpdateSuppressionContext {
    private static final ThreadLocal<Integer> NEIGHBOR_UPDATE_DEPTH = ThreadLocal.withInitial(() -> 0);

    private UpdateSuppressionContext() {
    }

    public static void enterNeighborUpdate() {
        NEIGHBOR_UPDATE_DEPTH.set(NEIGHBOR_UPDATE_DEPTH.get() + 1);
    }

    public static void leaveNeighborUpdate() {
        int depth = NEIGHBOR_UPDATE_DEPTH.get();
        if (depth <= 1) {
            NEIGHBOR_UPDATE_DEPTH.remove();
        } else {
            NEIGHBOR_UPDATE_DEPTH.set(depth - 1);
        }
    }

    public static boolean isInNeighborUpdate() {
        return NEIGHBOR_UPDATE_DEPTH.get() > 0;
    }

    public static void clearNeighborUpdates() {
        NEIGHBOR_UPDATE_DEPTH.remove();
    }

    public static SoundUpdateSuppressionException createException() {
        return new SoundUpdateSuppressionException();
    }

    public static boolean isSuppression(Throwable throwable) {
        Throwable current = throwable;
        for (int depth = 0; current != null && depth < 8; depth++) {
            if (current instanceof SoundUpdateSuppressionException) {
                return true;
            }
            Throwable cause = current.getCause();
            if (cause == current) {
                break;
            }
            current = cause;
        }
        return false;
    }

    public static final class SoundUpdateSuppressionException extends RuntimeException {
        private SoundUpdateSuppressionException() {
            super("Global sound update suppression", null, false, false);
        }
    }
}
