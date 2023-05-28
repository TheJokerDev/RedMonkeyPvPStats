package me.j0keer.redmonkeypvpstats.database.type;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.j0keer.redmonkeypvpstats.Main;
import me.j0keer.redmonkeypvpstats.database.Database;
import me.j0keer.redmonkeypvpstats.type.DataPlayer;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class MongoDB extends Database {
    private MongoCollection<Document> collection;
    private MongoDatabase db;
    private MongoClient client;

    public MongoDB(Main plugin) {
        super(plugin);
    }

    @Override
    protected String getType() {
        return "MongoDB";
    }

    @Override
    public void connect() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database.mongodb");
        boolean useURI = !section.getString("uri", "mongodb://localhost:27017").equals("mongodb://localhost:27017");
        if (useURI) {
            MongoClientURI uri = new MongoClientURI(section.getString("uri"));
            client = new MongoClient(uri);
        } else {
            String host = section.getString("host");
            int port = 27017;
            String[] split = host.split(":");
            if (split.length == 2) {
                host = split[0];
                port = Integer.parseInt(split[1]);
            }
            ServerAddress address = new ServerAddress(host, port);

            String password = section.getString("password");
            if (password != null && !password.equalsIgnoreCase("")){
                MongoCredential credential = MongoCredential.createCredential(section.getString("user"), section.getString("database"), password.toCharArray());
                client = new MongoClient(address, credential, MongoClientOptions.builder().build());
            } else {
                client = new MongoClient(address);
            }
        }
        db = client.getDatabase(section.getString("database"));
        collection = db.getCollection("users");

        plugin.console("{prefix}&7Connected to database.");
    }

    @Override
    public void disconnect() {
        client.close();
        plugin.console("{prefix}&7Disconnected from database.");
    }

    @Override
    public UUID getUUIDFromName(String name) {
        Document query = new Document("name", name);
        Document found = collection.find(query).first();

        if (found == null) {
            return null;
        }

        return UUID.fromString((String) found.get("uuid"));
    }

    @Override
    public boolean getOnlineMode(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document found = collection.find(query).first();

        if (found == null) {
            return false;
        }

        return (Boolean) found.get("onlineMode");
    }

    @Override
    public String getPassword(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document found = collection.find(query).first();

        if (found == null) {
            return "";
        }

        return (String) found.get("password");
    }

    @Override
    public void resetPassword(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document found = collection.find(query).first();

        if (found == null) {
            return;
        }

        Document object = new Document("uuid", uuid.toString());
        object.put("name", found.get("name"));
        object.put("password", "");
        object.put("onlineMode", found.get("onlineMode"));
        object.put("lastIp", found.get("lastIp"));
        collection.replaceOne(found, object);
    }

    @Override
    public void setPassword(UUID uuid, String password) {
        Document query = new Document("uuid", uuid.toString());
        Document found = collection.find(query).first();

        if (found == null) {
            return;
        }

        Document object = new Document("uuid", uuid.toString());
        object.put("name", found.get("name"));
        object.put("password", password);
        object.put("onlineMode", found.get("onlineMode"));
        object.put("lastIp", found.get("lastIp"));
        collection.replaceOne(found, object);
    }

    @Override
    public void setOnlineMode(UUID uuid, boolean onlineMode) {
        Document query = new Document("uuid", uuid.toString());
        Document found = collection.find(query).first();

        if (found == null) {
            return;
        }

        Document object = new Document("uuid", uuid.toString());
        object.put("name", found.get("name"));
        object.put("password", found.get("password"));
        object.put("onlineMode", onlineMode);
        object.put("lastIp", found.get("lastIp"));
        collection.replaceOne(found, object);
    }

    @Override
    public void loadUser(DataPlayer user) {
        Document query = new Document("uuid", user.getUuid().toString());
        Document found = collection.find(query).first();

        if (found == null) {
            Document object = new Document("uuid", user.getUuid().toString());
            object.put("name", user.getName());
            object.put("worldstats", user.serialize());
            collection.insertOne(object);
            return;
        }

        user.deserialize((String) found.get("worldstats"));
    }

    @Override
    public void saveUser(DataPlayer user) {
        Document query = new Document("uuid", user.getUuid().toString());
        Document found = collection.find(query).first();

        if (found == null) {
            Document object = new Document("uuid", user.getUuid().toString());
            object.put("name", user.getName());
            object.put("worldstats", user.serialize());
            collection.insertOne(object);
            return;
        }

        Document object = new Document("uuid", user.getUuid().toString());
        object.put("name", user.getName());
        object.put("worldstats", user.serialize());
        collection.replaceOne(found, object);
    }

    @Override
    public void deleteUser(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document found = collection.find(query).first();

        if (found == null) {
            return;
        }

        collection.deleteOne(found);
    }

    @Override
    public String getLastIp(UUID uuid) {
        Document query = new Document("uuid", uuid.toString());
        Document found = collection.find(query).first();

        if (found == null) {
            return "";
        }

        return (String) found.get("lastIp");
    }
}
