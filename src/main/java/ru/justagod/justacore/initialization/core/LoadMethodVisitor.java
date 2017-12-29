package ru.justagod.justacore.initialization.core;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by JustAGod on 28.12.2017.
 */

public class LoadMethodVisitor extends org.objectweb.asm.MethodVisitor {

    public LoadMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    @SuppressWarnings("unused")
    public static void addSpecial(Loader loader, ModDiscoverer discoverer) {
        InitHandler.find(loader.getActiveModList(), discoverer.getASMTable());
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (name.equals("sortModList")) {
            visitVarInsn(Opcodes.ALOAD, 0);
            visitFieldInsn(Opcodes.GETFIELD, "cpw/mods/fml/common/Loader", "discoverer", "Lcpw/mods/fml/common/discovery/ModDiscoverer;");
            visitMethodInsn(Opcodes.INVOKESTATIC, "ru/justagod/justacore/initialization/core/LoadMethodVisitor", "addSpecial", "(Lcpw/mods/fml/common/Loader;Lcpw/mods/fml/common/discovery/ModDiscoverer;)V");
            visitVarInsn(Opcodes.ALOAD, 0);
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);

    }
}
