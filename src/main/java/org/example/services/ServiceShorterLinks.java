package org.example.services;

import org.example.domain.ShortLink;
import org.example.exceptions.*;
import org.example.managers.FileLinkManager;
import org.example.managers.LinksManager;
import org.example.utils.UUIDGenerator;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ServiceShorterLinks {

    private final LinksManager linksManager = new LinksManager();
    private final FileLinkManager fileLinkManager = new FileLinkManager();
    private String UUID = "";

    private Map<String, Map<String, ShortLink>> linkMap;
    private final Random random;
//если подменить uuid, то можно работать под другим пользователем после загрузки данных из файла и будут доступны
// только ссылки соответствующие uuid
    public void setup() throws SocketException, NoSuchAlgorithmException, UUIDNotCreatedException {
        UUID = UUIDGenerator.generateUUID();
        linkMap.put(UUID, new HashMap<>());
    }


    public ServiceShorterLinks() {
        this.linkMap = new HashMap<>();
        this.random = new Random();

    }

    public String createShortLink(String originalLink) throws LinkNotPingException, IOException, LinkNotCorrectException, UUIDNotFoundException {
        checkUUIDFound(linkMap,UUID);
        return linksManager.createShortLink(originalLink, linkMap.get(UUID), random, UUID);
    }

    public String getOriginalLink(String shortCode) throws LinkNotFoundException, UUIDNotFoundException {
        checkUUIDFound(linkMap,UUID);
        return linksManager.getOriginalLink(shortCode, linkMap.get(UUID));
    }

    public void deleteLink(String shortCode) throws LinkNotFoundException, UUIDNotFoundException {
        checkUUIDFound(linkMap,UUID);
        linksManager.deleteLink(shortCode, linkMap.get(UUID));
    }

    public List<ShortLink> searchLinks(String query) throws UUIDNotFoundException {
        checkUUIDFound(linkMap,UUID);
        return linksManager.searchLink(query, linkMap.get(UUID));
    }

    public void showRecentLinks(int n) throws UUIDNotFoundException {
        checkUUIDFound(linkMap,UUID);
        linksManager.showRecentLinks(n, linkMap.get(UUID));
    }

    public void saveToCSV(String filename) throws IOException, UUIDNotFoundException {
        checkUUIDFound(linkMap,UUID);
        fileLinkManager.saveToCSV(filename, linkMap.get(UUID));
    }
// тут нет проверки checkUUIDFound т.к. мы из файла пользователю тянем только те ссылки, которым указанный uuid соответсвует
    public void loadFromCSV(String filename) throws IOException {
        fileLinkManager.loadFromCSV(filename, linkMap, UUID);
    }

    public void openLink(String shortLink) throws UUIDNotFoundException, LinkNotFoundException, URISyntaxException, IOException, LinkMaxAccessCountException, LinkExpiredException {
        checkUUIDFound(linkMap,UUID);
        linksManager.openLink(trimLink(shortLink), linkMap.get(UUID));
    }

    public void updateOriginalLink(String link, String newOriginalLink) throws LinkNotFoundException, UUIDNotFoundException, LinkNotPingException, IOException, LinkNotCorrectException {
        checkUUIDFound(linkMap,UUID);
        linksManager.checkServiceLink(link);
        linksManager.updateLink(trimLink(link), newOriginalLink, linkMap.get(UUID));
    }

    public void showAllLinks() throws UUIDNotFoundException {
        checkUUIDFound(linkMap,UUID);
        linksManager.showAllLinks(linkMap.get(UUID));
    }

    private void checkUUIDFound(Map<String,Map<String, ShortLink>> linksMap, String UUID) throws UUIDNotFoundException {
        if (!linksMap.containsKey(UUID)) {
            throw new UUIDNotFoundException("Пользователь не найден");
        }
    }

    private String trimLink(String shortLonk){
       return shortLonk.replace("https://mylink/", "");
    }
}
