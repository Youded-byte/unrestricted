package me.youded.unrestricted.hooks;

import net.weavemc.loader.api.Hook;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class BlacklistEnableHook extends Hook {
    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        for (MethodNode method : classNode.methods) {
            if (!(method.access == Opcodes.ACC_PUBLIC && method.desc.equals("(Lcom/google/gson/JsonElement;)V")))
                continue;

            boolean isBlackListEnableClass = Arrays.stream(method.instructions.toArray())
                    .filter(LdcInsnNode.class::isInstance)
                    .map(LdcInsnNode.class::cast)
                    .map(it -> it.cst)
                    .anyMatch("modSettings"::equals);

            if (isBlackListEnableClass) {
                method.instructions.clear();
                method.localVariables.clear();
                method.exceptions.clear();
                method.tryCatchBlocks.clear();
                method.instructions.add(new InsnNode(Opcodes.RETURN));
            }
        }
    }
}
