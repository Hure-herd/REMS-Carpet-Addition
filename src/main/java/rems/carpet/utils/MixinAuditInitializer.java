package rems.carpet.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.spongepowered.asm.mixin.Mixin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MixinAuditInitializer implements ClientModInitializer, DedicatedServerModInitializer {

    private static final String AUDIT_PROPERTY = "carpetremsaddition.mixin_audit";
    private static final String MIXIN_PACKAGE = "rems.carpet.mixins";
    private static final String CONFIG_PATH = "rems.mixins.json";

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
                try {
                    Class<?> mixinClazz = Class.forName(mixinClass, false, MixinAuditInitializer.class.getClassLoader());
                    Mixin mixinAnn = mixinClazz.getAnnotation(Mixin.class);
                    List<String> targets = new ArrayList<>();
                    if (mixinAnn != null) {
                        for (Class<?> t : mixinAnn.value()) {
                            if (t != null && t != Object.class) {
                                targets.add(t.getName());
                            }
                        }
                        for (String t : mixinAnn.targets()) {
                            if (t != null && !t.isEmpty()) {
                                targets.add(t);
                            }
                        }
                    }
                    if (targets.isEmpty()) {
                        System.out.println("[MIXIN-AUDIT] " + mixinClass + ": no static targets (dynamic/plugin), skipped");
                        continue;
                    }
                    for (String target : targets) {
                        try {
                            Class.forName(target, false, MixinAuditInitializer.class.getClassLoader());
                        } catch (Throwable t) {
                            failed++;
                            failures.add(mixinClass + " -> " + target + ": " + t);
                            System.err.println("[MIXIN-AUDIT] FAILED " + mixinClass + " -> " + target);
                            t.printStackTrace();
                        }
                    }
                    System.out.println("[MIXIN-AUDIT] " + mixinClass + ": " + targets.size() + " target(s) OK");
                } catch (Throwable t) {
                    failed++;
                    failures.add(mixinClass + ": " + t);
                    System.err.println("[MIXIN-AUDIT] FAILED loading mixin class " + mixinClass);
                    t.printStackTrace();
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
}
