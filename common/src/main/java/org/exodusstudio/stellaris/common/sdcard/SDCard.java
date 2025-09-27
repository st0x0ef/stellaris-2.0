package org.exodusstudio.stellaris.common.sdcard;

public abstract class SDCard {

    private final SDCardInfo cardInfo;

    public SDCard(SDCardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public abstract void run();

    public SDCardInfo getCardInfo() { return this.cardInfo; }

}
