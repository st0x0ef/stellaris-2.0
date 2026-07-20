package org.exodusstudio.stellaris.common.compats.jade;

import org.exodusstudio.stellaris.common.blocks.FlagBlock;
import org.exodusstudio.stellaris.common.blocks.FlagProxyBlock;
import org.exodusstudio.stellaris.common.blocks.PumpjackProxyBlock;
import org.exodusstudio.stellaris.common.blocks.RocketLaunchPadProxyBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Make each multiblock proxy show the controller block's name + icon.
        registration.registerBlockIcon(ProxyBlockProvider.INSTANCE, PumpjackProxyBlock.class);
        registration.registerBlockComponent(ProxyBlockProvider.INSTANCE, PumpjackProxyBlock.class);
        registration.registerBlockIcon(ProxyBlockProvider.INSTANCE, FlagProxyBlock.class);
        registration.registerBlockComponent(ProxyBlockProvider.INSTANCE, FlagProxyBlock.class);
        registration.registerBlockIcon(ProxyBlockProvider.INSTANCE, RocketLaunchPadProxyBlock.class);
        registration.registerBlockComponent(ProxyBlockProvider.INSTANCE, RocketLaunchPadProxyBlock.class);

        // Show the flag's owner on both the flag and its proxies.
        registration.registerBlockComponent(FlagOwnerProvider.INSTANCE, FlagBlock.class);
        registration.registerBlockComponent(FlagOwnerProvider.INSTANCE, FlagProxyBlock.class);
    }
}
