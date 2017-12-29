package ru.justagod.justacore.initialization.core;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by JustAGod on 29.12.2017.
 */
public class SendMethodVisitor extends MethodVisitor {
    public SendMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM4, mv);
    }

    @SuppressWarnings("unused")
    public static void initialize(FMLEvent event, ModContainer container) {
        try {
            InitHandler handler = InitHandler.findHandlerForMod(container.getMod());
            if (handler != null) {
                if (event instanceof FMLPreInitializationEvent) {
                    handler.preInit((FMLPreInitializationEvent) event);
                }
                if (event instanceof FMLInitializationEvent) {
                    handler.init((FMLInitializationEvent) event);
                }
                if (event instanceof FMLPostInitializationEvent) {
                    handler.postInit((FMLPostInitializationEvent) event);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        if (owner.equals("com/google/common/eventbus/EventBus") && name.equals("post")) {
            visitVarInsn(Opcodes.ALOAD, 1);
            visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    getClass().getName().replaceAll("\\.", "/"),
                    "initialize",
                    "(Lcpw/mods/fml/common/event/FMLEvent;Lcpw/mods/fml/common/ModContainer;)V",
                    false
                    );
        }
    }
}
