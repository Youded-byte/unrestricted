package me.youded.unrestricted.hooks;

import net.weavemc.loader.api.Hook;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class TexturePackUnrestrictHook extends Hook {
    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        for (MethodNode method : classNode.methods) {
            if (!(method.access == Opcodes.ACC_PUBLIC  + Opcodes.ACC_STATIC && method.desc.equals("(Ljava/lang/String;)Z")))
                continue;

            boolean isTexturePackClass = Arrays.stream(method.instructions.toArray())
                    .filter(LdcInsnNode.class::isInstance)
                    .map(LdcInsnNode.class::cast)
                    .map(it -> it.cst)
                    .anyMatch("assets/lunar/"::equals);

            if (isTexturePackClass) {
                method.instructions.clear();
                method.localVariables.clear();
                method.exceptions.clear();
                method.tryCatchBlocks.clear();
                method.instructions.add(new InsnNode(Opcodes.ICONST_1));
                method.instructions.add(new InsnNode(Opcodes.IRETURN));
            }
        }
    }
}
