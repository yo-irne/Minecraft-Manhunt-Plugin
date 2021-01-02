package me.irnewastaken.manhuntplugin

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.math.ceil


class ManhuntPlugin : JavaPlugin(), Listener {

    val speedrunners = mutableListOf<UUID>()
    val hunters = mutableListOf<UUID>()

    val selectedSpeedrunners = mutableMapOf<UUID, UUID?>()

    fun chatColor(text: String): String {
        return ChatColor.translateAlternateColorCodes('&', text)
    }

    override fun onEnable() {

        server.pluginManager.registerEvents(this, this)

        this.getCommand("hunter")?.setExecutor { sender, _, _, args ->
            if (args.isEmpty()) {
                sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                return@setExecutor true
            }
            when (args[0]) {
                "add" -> {
                    if (args.size != 2) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    val player = server.getPlayer(args[1])?.uniqueId
                    if (player == null) {
                        sender.sendMessage(chatColor("&cThere is no ${args[1]} on this server."))
                        return@setExecutor true
                    }
                    if (player in hunters) {
                        sender.sendMessage(chatColor("&c${args[1]} is already a hunter."))
                        return@setExecutor true
                    }
                    if (player in speedrunners) {
                        sender.sendMessage(chatColor("&c${args[1]} is already a speedrunner."))
                        return@setExecutor true
                    }
                    hunters += player
                    selectedSpeedrunners[player] = null
                    sender.sendMessage(chatColor("&l&3${args[1]} &r&eis now a hunter."))
                }
                "remove" -> {
                    if (args.size != 2) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    val player = server.getPlayer(args[1])?.uniqueId
                    if (player == null) {
                        sender.sendMessage(chatColor("&cThere is no ${args[1]} on this server."))
                        return@setExecutor true
                    }
                    if (player !in hunters) {
                        sender.sendMessage(chatColor("&c${args[1]} is not a hunter."))
                        return@setExecutor true
                    }
                    hunters.remove(player)
                    selectedSpeedrunners.remove(player)
                    sender.sendMessage(chatColor("&l&3${args[1]} &r&eis not a hunter anymore."))
                }
                "list" -> {
                    if (args.size != 1) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    if (hunters.isEmpty()) {
                        sender.sendMessage(chatColor("&eThere are no hunters at the moment."))
                        return@setExecutor true
                    }
                    sender.sendMessage(chatColor(
                        hunters.mapNotNull { server.getPlayer(it) }.joinToString(
                            prefix = "&eHunters: &3",
                            postfix = "&e.",
                            separator = "&e, &3"
                        ) { it.name }
                    ))
                }
                "clear" -> {
                    if (args.size != 1) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    if (hunters.isEmpty()) {
                        sender.sendMessage(chatColor("&eThere are no hunters anyway."))
                        return@setExecutor true
                    }
                    for (hunter in hunters) {
                        hunters.remove(hunter)
                    }
                    sender.sendMessage(chatColor("&eRemoved all hunters."))
                }
            }
            true
        }

        this.getCommand("speedrunner")?.setExecutor { sender, _, _, args ->
            if (args.isEmpty()) {
                sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                return@setExecutor true
            }
            when (args[0]) {
                "add" -> {
                    if (args.size != 2) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    val player = server.getPlayer(args[1])?.uniqueId
                    if (player == null) {
                        sender.sendMessage(chatColor("&cThere is no ${args[1]} on this server."))
                        return@setExecutor true
                    }
                    if (player in speedrunners) {
                        sender.sendMessage(chatColor("&c${args[1]} is already a speedrunner."))
                        return@setExecutor true
                    }
                    if (player in hunters) {
                        sender.sendMessage(chatColor("&c${args[1]} is already a hunter."))
                        return@setExecutor true
                    }
                    speedrunners += player
                    sender.sendMessage(chatColor("&l&3${args[1]} &r&eis now a speedrunner."))
                }
                "remove" -> {
                    if (args.size != 2) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    val player = server.getPlayer(args[1])?.uniqueId
                    if (player == null) {
                        sender.sendMessage(chatColor("&cThere is no ${args[1]} on this server."))
                        return@setExecutor true
                    }
                    if (player !in speedrunners) {
                        sender.sendMessage(chatColor("&c${args[1]} is not a speedrunner."))
                        return@setExecutor true
                    }
                    speedrunners.remove(player)
                    sender.sendMessage(chatColor("&l&3${args[1]} &r&eis not a speedrunner anymore."))
                }
                "list" -> {
                    if (args.size != 1) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    if (speedrunners.isEmpty()) {
                        sender.sendMessage(chatColor("&eThere are no speedrunners at the moment."))
                        return@setExecutor true
                    }
                    sender.sendMessage(chatColor(
                        speedrunners.mapNotNull { server.getPlayer(it) }.joinToString(
                            prefix = "&eSpeedrunners: &3",
                            postfix = "&e.",
                            separator = "&e, &3"
                        ) { it.name }
                    ))
                }
                "clear" -> {
                    if (args.size != 1) {
                        sender.sendMessage(chatColor("&cInvalid syntax, bruv."))
                        return@setExecutor true
                    }
                    if (speedrunners.isEmpty()) {
                        sender.sendMessage(chatColor("&eThere are no speedrunners anyway."))
                        return@setExecutor true
                    }
                    for (speedrunner in speedrunners) {
                        speedrunners.remove(speedrunner)
                    }
                    sender.sendMessage(chatColor("&eRemoved all speedrunners."))
                }
            }
            true
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    class CompassMenu(speedrunners: List<UUID>) : InventoryHolder {

        private val invSize = if (speedrunners.isEmpty()) 9 else (ceil((speedrunners.size) / 9.0) * 9).toInt()

        private val inv = Bukkit.createInventory(this, invSize, "Speedrunners:")

        init {
            for (speedrunner in speedrunners) {
                val speedrunnerHead = ItemStack(Material.PLAYER_HEAD)
                val itemMeta = speedrunnerHead.itemMeta ?: continue
                if (itemMeta !is SkullMeta) continue
                itemMeta.owningPlayer = Bukkit.getServer().getPlayer(speedrunner) ?: continue
                itemMeta.setDisplayName(Bukkit.getServer().getPlayer(speedrunner)?.name ?: continue)
                speedrunnerHead.itemMeta = itemMeta
                inv.addItem(speedrunnerHead)
            }
        }

        override fun getInventory(): Inventory {
            return inv
        }

    }

    @EventHandler
    fun onCompassClick(event: PlayerInteractEvent) {
        if (event.player.uniqueId !in hunters) return
        if (event.item?.type != Material.COMPASS) return
        when (event.action) {
            in listOf(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK) -> {
                event.player.openInventory(CompassMenu(speedrunners).inventory)
            }
            in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK) -> {
                val selected = selectedSpeedrunners[event.player.uniqueId]?.let { server.getPlayer(it) }
                if (selected != null) {
                    event.player.compassTarget = selected.location
                }
            }
            else -> {
            }
        }
        //event.player.sendMessage("Otworzyłeś okno interaktywne! Wow!")

        /*
        val myInventory: Inventory =
        Bukkit.createInventory(null, (ceil(speedrunners.size / 9.0) * 9).toInt(), "Speedrunners:")
        myInventory.setItem(0, ItemStack(Material.DIRT))
        myInventory.setItem(8, ItemStack(Material.GOLD_BLOCK))
        event.player.openInventory(myInventory)
         */
    }

    @EventHandler
    fun onItemClicked(event: InventoryClickEvent) {
        if (event.inventory.holder !is CompassMenu) return
        val p = event.whoClicked
        if (p !is Player) return
        val s = ((event.currentItem?.itemMeta as? SkullMeta)?.owningPlayer as? Player) ?: return
        p.compassTarget = s.location
        selectedSpeedrunners[p.uniqueId] = s.uniqueId
        //p.itemInHand.itemMeta?.setDisplayName(((event.currentItem?.itemMeta as? SkullMeta)?.owningPlayer as? Player).toString())
        //p.dropItem(true)
        /*
        val meta1 = p.itemInHand.itemMeta
        meta1?.setDisplayName("yo")
        p.itemInHand.itemMeta = meta1
        */
        fun setName(`is`: ItemStack, name: String): ItemStack {
            val m = `is`.itemMeta
            m?.setDisplayName(name)
            `is`.itemMeta = m
            return `is`
        }
        setName(p.itemInHand, "yo")
        p.closeInventory()
        event.isCancelled = true
    }

    /*
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player.uniqueId
        if (player in hunters) hunters.remove(player)
        if (player in speedrunners) speedrunners.remove(player)
    }
    */
}
