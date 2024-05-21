package io.github.slimeymc.slimeys_utility.forge

import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import io.github.slimeymc.slimeys_utility.SlimeyplateMain
import io.github.slimeymc.slimeys_utility.client.SlimeyplateClient
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(SlimeyplateMain.MOD_ID)
class SlimeyplateModForge {
    init {
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(event)
        }

        EventBuses.registerModEventBus(SlimeyplateMain.MOD_ID, MOD_BUS)

        SlimeyplateMain.init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        SlimeyplateClient.init()
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
