package red.man10.man10datacenter

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection

import org.bson.Document
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class MongoDBManager(private val plugin: JavaPlugin, coll: String?) : AutoCloseable {
    var HOST: String = "null"
    var USER: String = "null"
    var PASS: String = "null"
    var PORT = 0
    var DATABASE: String = "null"
    var CUSTOM: String = ""
    private var connected = false
    private val coll: MongoCollection<Document>
    private var con: MongoClient? = null
    private var MongoDB: MongoDBFunc? = null

    /////////////////////////////////
    //       Load YAML
    /////////////////////////////////
    fun loadConfig() {
        plugin.logger.info("MongoDB Config loading")
        HOST = plugin.config.getString("mongo.host")!!
        USER = plugin.config.getString("mongo.user")!!
        PASS = plugin.config.getString("mongo.pass")!!
        PORT = plugin.config.getInt("mongo.port")
        CUSTOM = plugin.config.getString("mongo.uri")!!
        DATABASE = plugin.config.getString("mongo.db")!!
        plugin.logger.info("Config loaded")
    }

    ////////////////////////////////
    //       Connect
    ////////////////////////////////
    fun Connect(): Boolean {
        MongoDB = MongoDBFunc(HOST, USER, PASS, PORT, DATABASE, CUSTOM)
        con = MongoDB!!.open()
        if (con == null) {
            plugin.logger.info("failed to open MongoDB")
            return false
        }
        try {
            connected = true
            plugin.logger.info("Connected to the database.")
        } catch (var6: Exception) {
            connected = false
            plugin.logger.info("Could not connect to the database.")
        }
        MongoDB!!.close(con)
        return connected
    }

    ////////////////////////////////
    //       InsertOne Query
    ////////////////////////////////
    fun queryInsertOne(doc: Document) {
        coll.insertOne(doc)
    }

    ////////////////////////////////
    //       UpdateOne Query
    ////////////////////////////////
    fun queryUpdateOne(filter: Document, update: Document) {
        val updateSet = Document()
        updateSet.append("\$set", update)
        coll.updateOne(filter, updateSet)
    }

    ////////////////////////////////
    //       DeleteOne Query
    ////////////////////////////////
    fun queryDelete(filter: Document) {
        coll.deleteOne(filter)
    }

    ////////////////////////////////
    //       Find Query
    ////////////////////////////////
    fun queryFind(filter: Document): List<Document> {
        return coll.find(filter).into(ArrayList())
    }

    ////////////////////////////////
    //       Count Query
    ////////////////////////////////
    fun queryCount(filter: Document): Long {
        return coll.countDocuments(filter)
    }

    ////////////////////////////////
    //       Connection Close
    ////////////////////////////////
    override fun close() {
        try {
            con!!.close()
            MongoDB!!.close(con)
        } catch (var4: Exception) {
        }
    }

    /**
     * Created by Chiharu-Hagihara
     * Reference by takatronix:MySQLManager
     */
    ////////////////////////////////
    //      Constructor
    ////////////////////////////////
    init {
        loadConfig()
        connected = false
        connected = Connect()
        this.coll = con!!.getDatabase(DATABASE).getCollection(coll)
        if (!connected) {
            plugin.logger.info("Unable to establish a MongoDB connection.")
        }
    }
}