package ru.justagod.justacore.helper
/**
 * Created by JustAGod on 05.11.17.
 */
class ScissorTest extends GroovyTestCase {
    void testIntersection() {
        assertEquals("Сцизор внутри", new Scissor(0, 0, 100, 100), new Scissor(0, 0, 1000, 1000).intersection(new Scissor(0, 0, 100, 100)))
        assertEquals("Сцизор снаружи", new Scissor(0, 0, 100, 100), new Scissor(0, 0, 100, 100).intersection(new Scissor(0, 0, 1000, 1000)))
        assertEquals("Сцизор равен", new Scissor(0, 0, 100, 100), new Scissor(0, 0, 100, 100).intersection(new Scissor(0, 0, 100, 100)))
        assertEquals("Сцизор справа", new Scissor(50, 50, 50, 50), new Scissor(0, 0, 100, 100).intersection(new Scissor(50, 50, 100, 100)))
    }
}
