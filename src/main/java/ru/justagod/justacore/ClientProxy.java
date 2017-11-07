package ru.justagod.justacore;

import ru.justagod.justacore.gui.helper.DrawHelper;

/**
 * Created by JustAGod on 03.11.17.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        DrawHelper.reloadCursors();

    }
}
