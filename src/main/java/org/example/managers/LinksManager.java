package org.example.managers;

import org.example.ShortLink;
import org.example.exceptions.*;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class LinksManager {
    public final int SHORT_CODE_LENGTH = 8;
    private static final String BASE_URL = "https://mylink/";
    public final String CHARS = "qwer1tyu2io3pa4sdfg5hj6klz7xcv8bnmQ9WERTYUIOP0ASDFGHJKLZXCVBNM";


    public static void checkLinkAvailable(String link) throws LinkNotPingException, IOException {
//        Пинг может быть не настроен на сайте, функция используется в качестве дополнительно проверки в рамках
//        сессионого задания
        InetAddress inet = InetAddress.getByName(link);

        System.out.println("Проверяем доступность: " + link);
        boolean reachable = inet.isReachable(5000);

        if (reachable) {
            System.out.println(link + " доступен");
        } else {
            throw new LinkNotPingException(link + " недоступен");
        }
    }

    public boolean isShortLinkLive(long timestamp) {
        long now = System.currentTimeMillis();

        long TTL = 86_400_000L;
        return now - timestamp < TTL;
    }

    public void checkLinkCorrect(String link) throws LinkNotCorrectException {

        if (link == null || link.isBlank()) {
            throw new LinkNotCorrectException("Введена пустая строка");
        }

        if (link.length() > 1000) {
            throw new LinkNotCorrectException("Извините, но ссылка слишком длинная \nМаксимальная длина 1000 символов");
        }

        if (!link.matches("https?://.*"))
            throw new LinkNotCorrectException("Ссылка должна начинаться с https или http");
    }

    public String createShortLink(String originalUrl, Map<String, ShortLink> urlMap, Random random, String UUID) throws LinkNotCorrectException, LinkNotPingException, IOException {

        checkLinkCorrect(originalUrl);
        checkLinkAvailable(originalUrl);

        Optional<ShortLink> existing = urlMap.values().stream()
                .filter(url -> url.getOriginalLink().equals(originalUrl))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get().getShortCode();
        }

        String shortCode = generateShortCode(random, urlMap);
        ShortLink shortLink = new ShortLink(shortCode, originalUrl, UUID);
        urlMap.put(shortCode, shortLink);
        return shortCode;
    }


    public String generateShortCode(Random random, Map<String, ShortLink> urlMap) {
        String shortCode;
        do {
            StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            shortCode = sb.toString();
        } while (urlMap.containsKey(shortCode));
        return shortCode;
    }

    public String getOriginalLink(String shortCode, Map<String, ShortLink> urlMap) throws LinkNotFoundException {
        ShortLink shortLink = urlMap.get(shortCode);
        if (shortLink == null) {
            throw new LinkNotFoundException("Короткая ссылка не найдена: " + shortCode);
        }
        shortLink.incrementAccessCount();
        return shortLink.getOriginalLink();
    }

    public void deleteLink(String shortCode, Map<String, ShortLink> urlMap) throws LinkNotFoundException {
        ShortLink removed = urlMap.remove(shortCode);
        if (removed == null) {
            throw new LinkNotFoundException("Короткая ссылка не найдена: " + shortCode);
        }
        System.out.println("Удалена ссылка: " + removed);
    }

    public void updateLink(String shortCode, String newOriginalUrl, Map<String, ShortLink> urlMap) throws LinkNotFoundException, LinkNotCorrectException, LinkNotPingException, IOException {
        ShortLink shortLink = urlMap.get(shortCode);
        if (shortLink == null) {
            throw new LinkNotFoundException("Короткая ссылка не найдена: " + shortCode);
        }

        checkLinkCorrect(newOriginalUrl);
        checkLinkAvailable(newOriginalUrl);

        shortLink.updateOriginalLink(newOriginalUrl);
        System.out.println("Ссылка обновлена: " + newOriginalUrl);

    }

    public void showAllLinks(Map<String, ShortLink> urlMap) {
        if (urlMap.isEmpty()) {
            System.out.println("Нет сохраненных ссылок");
            return;
        }

        System.out.println("\n=== Все сокращенные ссылки ===");
        urlMap.values().stream()
                .sorted(Comparator.comparing(ShortLink::getCreatedAt).reversed())
                .forEach(System.out::println);
    }


    public List<ShortLink> searchLink(String query, Map<String, ShortLink> urlMap) {
        return urlMap.values().stream()
                .filter(url -> url.getOriginalLink().toLowerCase().contains(query.toLowerCase()) ||
                        url.getShortCode().toLowerCase().contains(query.toLowerCase()))
                .sorted(Comparator.comparing(ShortLink::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public void showRecentLinks(int n, Map<String, ShortLink> urlMap) {
        System.out.println("\n=== Последние " + n + " созданных ссылок ===");
        urlMap.values().stream()
                .sorted(Comparator.comparing(ShortLink::getCreatedAt).reversed())
                .limit(n)
                .forEach(System.out::println);
    }

    public void openLink(String shortCode, Map<String, ShortLink> urlMap)
            throws LinkNotFoundException, LinkMaxAccessCountException, URISyntaxException, IOException, LinkExpiredException {

        ShortLink shortLink = urlMap.get(shortCode);

        if (shortLink == null)
            throw new LinkNotFoundException("Короткая ссылка не найдена: " + shortCode);

        if (!isShortLinkLive(shortLink.getCreatedAt())) {
            deleteLink(shortCode, urlMap);
            throw new LinkExpiredException("Время действия ссылки истекло");
        }

        if (shortLink.isBlocked())
            throw new LinkMaxAccessCountException("Превышен лимит переходов по ссылке");

        Desktop.getDesktop().browse(new URI(shortLink.getOriginalLink()));
        shortLink.incrementAccessCount();

        if (shortLink.getAccessCount() > ShortLink.MAX_LINK_CLICK)
            shortLink.setBlocked(true);
    }

    public void checkServiceLink(String link) throws LinkNotCorrectException {
        if (link.matches("https?://.*") && !link.startsWith(BASE_URL)) {
            throw new LinkNotCorrectException("Данная ссылка не является сгенерированной нашим сервисом");
        }
    }

}
