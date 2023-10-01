package com.leon.bugreport;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.leon.bugreport.BugReportManager.*;

public class BugReportDatabase {
    private Connection connection;

    public BugReportDatabase() {
        connectLocalOrRemote();
    }

    private void connectLocalOrRemote() {
        loadConfig();

        String databaseType = Objects.requireNonNull(config.getString("databaseType"));
        ConfigurationSection databaseSection = config.getConfigurationSection("database");

		if (databaseType.equalsIgnoreCase("local")) {
            System.out.println("Connecting to local database");
            connectLocal();
        }

        if (databaseType.equalsIgnoreCase("mysql")) {
            System.out.println("Connecting to remote database");

			String host = databaseSection.getString("host");
            int port = databaseSection.getInt("port");
            String database = databaseSection.getString("database");
            String username = databaseSection.getString("username");
            String password = databaseSection.getString("password");

            connectRemote(host, port, database, username, password);
        }
    }

    public void addBugReport(String username, @NotNull UUID playerId, String world, String header, String fullMessage) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO bug_reports (player_id, header, message, username, world) VALUES (?, ?, ?, ?, ?)");

            statement.setString(1, playerId.toString());
            statement.setString(2, header);
            statement.setString(3, fullMessage);
            statement.setString(4, username);
            statement.setString(5, world);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to add bug report.");
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public Map<UUID, List<String>> loadBugReports() {
        Map<UUID, List<String>> bugReports = new HashMap<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM bug_reports");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID playerId = UUID.fromString(resultSet.getString("player_id"));
                String fullMessage = resultSet.getString("message");
                String username = resultSet.getString("username");
                String world = resultSet.getString("world");
                String header = resultSet.getString("header");

                List<String> reports = bugReports.getOrDefault(playerId, new ArrayList<>());
                reports.add("Username: " + username + "\nUUID: " + playerId + "\nWorld: " + world + "\nFull Message: " + fullMessage + "\nHeader: " + header);
                bugReports.put(playerId, reports);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load bug reports.");
            plugin.getLogger().severe(e.getMessage());
        }

        return bugReports;
    }

    private void connectRemote(String host, Integer port, String database, String username, String password) {
        try {
            String databaseURL = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            connection = DriverManager.getConnection(databaseURL, username, password);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to remote database.");
            plugin.getLogger().severe(e.getMessage());
        }

        createTables();
    }

    private void connectLocal() {
        try {
            File databaseFile = new File("plugins/BugReport/bugreports.db");
            String databaseURL = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            connection = DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to local database.");
            plugin.getLogger().severe(e.getMessage());
        }

        createTables();
    }

    private void createTables() {
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS bug_reports (rowid INTEGER, player_id TEXT, header TEXT, message TEXT, username TEXT, world TEXT)");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create tables.");
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public void updateBugReportHeader(UUID playerId, int reportIndex, int hasBeenRead) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE bug_reports SET header = ? WHERE player_id = ? AND rowid = ?");
            String existingHeader = BugReportManager.bugReports.get(playerId).get(reportIndex);

            String[] lines = existingHeader.split("\n");
            StringBuilder newHeader = new StringBuilder();
            for (String line : lines) {
                if (line.startsWith("hasBeenRead:")) {
                    newHeader.append("hasBeenRead: ").append(hasBeenRead);
                } else {
                    newHeader.append(line);
                }
                newHeader.append("\n");
            }

            statement.setString(1, newHeader.toString().trim());
            statement.setString(2, playerId.toString());
            statement.setInt(3, reportIndex + 1);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to update bug report header.");
            plugin.getLogger().severe(e.getMessage());
        }
    }
}
