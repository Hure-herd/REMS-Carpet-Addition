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

package rems.carpet.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MixinAuditInitializer implements ClientModInitializer, DedicatedServerModInitializer {

    private static final String AUDIT_PROPERTY = "carpetremsaddition.mixin_audit";
    private static final String MIXIN_PACKAGE = "rems.carpet.mixins";
    private static final String CONFIG_PATH = "rems.mixins.json";
    private static final String MIXIN_ANNOTATION_DESC = "Lorg/spongepowered/asm/mixin/Mixin;";

    @Override
    public void onInitializeClient() {
        runAuditIfRequested();
    }

    @Override
    public void onInitializeServer() {
        runAuditIfRequested();
    }

    private static void runAuditIfRequested() {
        if (!"true".equals(System.getProperty(AUDIT_PROPERTY))) {
            return;
        }
        System.out.println("[MIXIN-AUDIT] mixin audit started");
        int total = 0;
        int failed = 0;
        List<String> failures = new ArrayList<>();

        try (InputStream in = MixinAuditInitializer.class.getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (in == null) {
                System.err.println("[MIXIN-AUDIT] FATAL: cannot find " + CONFIG_PATH + " on classpath");
                System.exit(1);
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray mixins = root.getAsJsonArray("mixins");
            for (JsonElement e : mixins) {
                String mixinName = e.getAsString();
                String mixinClass = MIXIN_PACKAGE + "." + mixinName;
                total++;
                MixinInfo info = readMixinInfo(mixinClass);
                if (info.targets.isEmpty()) {
                    System.out.println("[MIXIN-AUDIT] " + mixinClass + ": no static targets (dynamic/plugin), skipped");
                    continue;
                }
                boolean ok = true;
                for (String target : info.targets) {
                    try {
                        Class.forName(target, false, MixinAuditInitializer.class.getClassLoader());
                    } catch (Throwable t) {
                        if (info.pseudo && t instanceof ClassNotFoundException) {
                            System.out.println("[MIXIN-AUDIT] " + mixinClass + " -> " + target
                                    + ": optional target missing (pseudo), skipped");
                            continue;
                        }
                        ok = false;
                        failures.add(mixinClass + " -> " + target + ": " + t);
                        System.err.println("[MIXIN-AUDIT] FAILED " + mixinClass + " -> " + target);
                        t.printStackTrace();
                    }
                }
                if (ok) {
                    System.out.println("[MIXIN-AUDIT] " + mixinClass + ": " + info.targets.size() + " target(s) OK");
                } else {
                    failed++;
                }
            }
        } catch (Exception ex) {
            System.err.println("[MIXIN-AUDIT] FATAL: " + ex);
            ex.printStackTrace();
            System.exit(1);
            return;
        }

        System.out.println("[MIXIN-AUDIT] result: " + (total - failed) + "/" + total + " mixins OK, " + failed + " failed");
        if (failed > 0) {
            System.err.println("[MIXIN-AUDIT] failures:");
            for (String f : failures) {
                System.err.println("  " + f);
            }
        }
        System.exit(failed == 0 ? 0 : 1);
    }

    private static MixinInfo readMixinInfo(String mixinClass) {
        MixinInfo info = new MixinInfo();
        String resource = mixinClass.replace('.', '/') + ".class";
        try (InputStream is = MixinAuditInitializer.class.getClassLoader().getResourceAsStream(resource)) {
            if (is == null) {
                System.err.println("[MIXIN-AUDIT] class resource not found: " + mixinClass);
                return info;
            }
            ClassReader reader = new ClassReader(is);
            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if ("Lorg/spongepowered/asm/mixin/Pseudo;".equals(descriptor)) {
                        info.pseudo = true;
                        return super.visitAnnotation(descriptor, visible);
                    }
                    if (!MIXIN_ANNOTATION_DESC.equals(descriptor)) {
                        return super.visitAnnotation(descriptor, visible);
                    }
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public AnnotationVisitor visitArray(String name) {
                            if ("value".equals(name)) {
                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @Override
                                    public void visit(String n, Object value) {
                                        if (value instanceof Type) {
                                            String cn = ((Type) value).getClassName();
                                            if (!"java.lang.Object".equals(cn)) {
                                                info.targets.add(cn);
                                            }
                                        }
                                    }
                                };
                            }
                            if ("targets".equals(name)) {
                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @Override
                                    public void visit(String n, Object value) {
                                        if (value instanceof String && !((String) value).isEmpty()) {
                                            info.targets.add((String) value);
                                        }
                                    }
                                };
                            }
                            return super.visitArray(name);
                        }
                    };
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (Exception e) {
            System.err.println("[MIXIN-AUDIT] failed to read " + mixinClass + ": " + e);
        }
        return info;
    }

    private static final class MixinInfo {
        final List<String> targets = new ArrayList<>();
        boolean pseudo;
    }
}
