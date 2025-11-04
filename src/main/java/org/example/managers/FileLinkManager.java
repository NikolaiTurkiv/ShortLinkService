package org.example.managers;

import org.example.ShortLink;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileLinkManager {

    public void saveToCSV(String filename, Map<String, ShortLink> linkMap) throws IOException {
        File file = new File(filename);
        boolean fileExists = file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.write("ShortCode,OriginalLink,CreatedAt,AccessCount,UUID,isBlocked\n");
            }
            for (ShortLink link : linkMap.values()) {
                writer.write(link.getShortCode() + "," + link.getOriginalLink() + "," +
                        link.getCreatedAt() + "," + link.getAccessCount() + "," +
                        link.getUUID() + "," + link.isBlocked() + "\n");
            }
        }
        System.out.println("Данные сохранены в " + filename);
    }

    public void loadFromCSV(String filename, Map<String, Map<String, ShortLink>> linkMap, String userUUID) throws IOException {
        linkMap.clear();
        Map<String, ShortLink> shortLinkMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 6) continue;

                String shortCode = parts[0];
                String originalLink = parts[1];
                long createdAt = Long.parseLong(parts[2]);
                int accessCount = Integer.parseInt(parts[3]);
                String UUID = parts[4];
                boolean isBlocked = Boolean.parseBoolean(parts[5]);

                ShortLink shortLink = new ShortLink(shortCode, originalLink, createdAt, accessCount, UUID, isBlocked);

                if (Objects.equals(userUUID, UUID)) {
                    shortLinkMap.put(shortCode, shortLink);
                }
            }

            linkMap.put(userUUID, shortLinkMap);

        }

        System.out.println("Данные загружены из " + filename);
    }
}
