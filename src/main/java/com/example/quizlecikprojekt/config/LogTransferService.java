package com.example.quizlecikprojekt.config;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


@Service
public class LogTransferService {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogTransferService.class);
    private static final String FILE_PATH = System.getenv().getOrDefault("LOG_FILE_PATH", "app.json");
    private static final String CONNECTION_STRING = "mongodb://root:admin@mongo:27017/projekt";

    public void transferLogs() {
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase("logs_Quzilecik");
            MongoCollection<Document> collection = database.getCollection("logs");

            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Document doc = Document.parse(line);
                    collection.insertOne(doc);
                }
            } catch (IOException e) {
                LOGGER.error("MONGODB - Error reading log file: ", e);
            }

            try (PrintWriter writer = new PrintWriter(FILE_PATH)) {
                writer.print("");
            } catch (IOException e) {
                LOGGER.error("MONGODB - Error clearing log file: ", e);
            }


        } catch (Exception e) {
            LOGGER.error("MONGODB - Error connecting to MongoDB: ", e);
        }
    }
}