package org.exodusstudio.stellaris.common.compats.jade;

import org.exodusstudio.stellaris.common.blocks.*;
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
        registration.registerBlockIcon(ProxyBlockProvider.INSTANCE, BlenderProxyBlock.class);
        registration.registerBlockComponent(ProxyBlockProvider.INSTANCE, BlenderProxyBlock.class);

        // Show the flag's owner on both the flag and its proxies.
        registration.registerBlockComponent(FlagOwnerProvider.INSTANCE, FlagBlock.class);
        registration.registerBlockComponent(FlagOwnerProvider.INSTANCE, FlagProxyBlock.class);
        registration.registerBlockComponent(SpaceFarmBlockProvider.INSTANCE, SpaceFarmBlock.class);

    }
}
