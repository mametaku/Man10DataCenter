package red.man10.man10datacenter

import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser


class Man10DataCenter : JavaPlugin(), Listener {
    var prefix = "§e§l[§d§lMan10DataBase§e§l]§f"
    val PLName = "Man10LoginBonus"
    var data = MongoDBManager(this, "Chest_count")
    var locationdata = MongoDBManager(this, "Chest_location")
    override fun onEnable() {
        server.pluginManager.registerEvents(this, this as Plugin)
        saveDefaultConfig()
    }
    @EventHandler
    fun setchest(e: BlockPlaceEvent){
        val c = e.block.type
        val p = e.player
        val uuid = p.uniqueId.toString()
        val x = e.block.x
        val y = e.block.y
        val z = e.block.z
        if (c == Material.CHEST){
            val doc = Document()
            val result = data.queryFind(doc)
            val parsed: JSONObject = JSONParser().parse(result[0].toJson()) as JSONObject
            val chestcount = parsed["chestcount"] as String
            val Intchestcount = chestcount.toInt() + 1
            val emptyslot = chestcount.toInt() * 27 * 64
            val filter = Document().append("uuid", uuid)
            val update = Document()
            update.append("chestcount", Intchestcount.toString())
            update.append("emptyslot",emptyslot.toString())
            data.queryUpdateOne(filter,update)
            p.sendMessage("${prefix}チェストを設置しました！")

        }
    }

    @EventHandler
    fun breakchest(e: BlockBreakEvent){
        val c = e.block.type
        val p = e.player
        val uuid = p.uniqueId.toString()
        if (c == Material.CHEST){
            val doc = Document()
            val result = data.queryFind(doc)
            val parsed: JSONObject = JSONParser().parse(result[0].toJson()) as JSONObject
            val chestcount = parsed["chestcount"] as String
            val Intchestcount = chestcount.toInt() - 1
            val emptyslot = Intchestcount * 27 * 64
            val filter = Document().append("uuid", uuid)
            val update = Document()
            update.append("chestcount", Intchestcount.toString())
            update.append("emptyslot",emptyslot.toString())
            data.queryUpdateOne(filter,update)
            p.sendMessage("${prefix}チェストを破壊しました！")
        }
    }

    @EventHandler
    fun LoginEvent(event: PlayerLoginEvent?) {
        val config = config
        if (!config.getBoolean("mode")) return
        Bukkit.getScheduler().runTaskAsynchronously(this, Runnable {
            try {
                setData(event as PlayerEvent?)
            } catch (e: Exception) {
                Bukkit.getLogger().info(e.message)
                println(e.message)
            }
        })
    }

    fun setData(event: PlayerEvent?) {
        val p = event?.player
        val uuid = p?.uniqueId.toString()
        val player = p?.name
        var chestcount = "0"
        val emptyslot = "0"
        val slot ="0"
        val doc = Document()
        doc.append("uuid", uuid)
        val result = data.queryFind(doc)
        if(result.isEmpty()) {
            doc.append("mcid", player)
            doc.append("chestcount", chestcount)
            doc.append("emptyslot",emptyslot)
            doc.append("slot",slot)
            data.queryInsertOne(doc)
        }
    }
}
