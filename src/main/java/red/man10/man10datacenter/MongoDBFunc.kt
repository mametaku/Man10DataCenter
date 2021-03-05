package red.man10.man10datacenter

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level


class MongoDBFunc(var HOST: String, var USER: String, var PASS: String, var PORT: Int, var DATABASE: String, var CUSTOM: String) {
    /**
     * Created by Chiharu-Hagihara
     * Reference by takatronix:MySQLFunc
     */
    private val plugin: JavaPlugin? = null
    private var con: MongoClient? = null
    fun open(): MongoClient? {
        try {
            // mongodb://user1:pwd1@host1/?authSource=db1
            val uri = MongoClientURI("mongodb://$USER:$PASS@$HOST:$PORT/$DATABASE$CUSTOM")
            con = MongoClient(uri)
            return con
        } catch (var2: Exception) {
            plugin!!.logger.log(Level.SEVERE, "Could not connect to MySQL server, error code: $var2")
        }
        return con
    }

    fun checkConnection(): Boolean {
        return con != null
    }

    fun close(c: MongoClient?) {
        var c: MongoClient? = c
        c = null
    }

    fun getCon(): MongoClient? {
        return con
    }

    fun setCon(con: MongoClient?) {
        this.con = con
    }

}