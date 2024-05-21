package io.github.slimeymc.valkyrien_chunkloader_compat.fabric

import io.github.slimeymc.slimeyplate.server.SlimeyplateServer
import io.github.slimeymc.slimeys_utility.SlimeyplateMain
import io.github.slimeymc.slimeys_utility.client.SlimeyplateClient
import net.fabricmc.api.*
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric

class MainMod : ModInitializer {
    override fun onInitialize() {
        // force VS2 to load before eureka
        ValkyrienSkiesModFabric().onInitialize()
        SlimeyplateMain.init()
    }
}

@Environment(EnvType.CLIENT)
class ClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        SlimeyplateClient.init()
    }
}

@Environment(EnvType.SERVER)
class DedicatedServerMod : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        SlimeyplateServer.init()
    }
}