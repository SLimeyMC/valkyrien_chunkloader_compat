package io.github.slimeymc.valkyrien_chunkloader_compat.fabric

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

/**
 * For now, just using this class as an abusive early entrypoint to run the updater
 */
class ValkyrienPreLaunch : PreLaunchEntrypoint {
    override fun onPreLaunch() {}
}
