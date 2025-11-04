package org.example;


import org.example.domain.ShortLink;
import org.example.exceptions.*;
import org.example.services.ServiceShorterLinks;


import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final ServiceShorterLinks service = new ServiceShorterLinks();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String BASE_URL = "https://mylink/";

    public static void main(String[] args) throws SocketException, NoSuchAlgorithmException, UUIDNotCreatedException {
        service.setup();
        boolean running = true;

        while (running) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            try {
                switch (choice) {
                    case 1 -> createShortLinks();
                    case 2 -> getOriginalLinks();
                    case 3 -> deleteLink();
                    case 4 -> showAllLinks();
                    case 5 -> searchLinks();
                    case 6 -> updateLink();
                    case 7 -> openLink();
                    case 8 -> showRecentLinks();
                    case 9 -> saveData();
                    case 10 -> loadData();
                    case 0 -> {
                        System.out.println("Выход из программы");
                        running = false;
                    }
                    default -> System.out.println("Неверный выбор");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              🌐  СЕРВИС СОКРАЩЕНИЯ ССЫЛОК  🌐             ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║  1. Создать короткую ссылку                        ║");
        System.out.println("║  2. Получить оригинальный URL                      ║");
        System.out.println("║  3. Удалить ссылку                                 ║");
        System.out.println("║  4. Показать все ссылки                            ║");
        System.out.println("║  5. Поиск ссылок                                   ║");
        System.out.println("║  6. Обновить ссылку                                ║");
        System.out.println("║  7. Перейти по ссылке                              ║");
        System.out.println("║  8. Последние созданные                            ║");
        System.out.println("║  9. Сохранить данные (CSV)                         ║");
        System.out.println("║ 10. Загрузить данные (CSV)                         ║");
        System.out.println("║  0. Выход                                          ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    private static void createShortLinks() throws LinkNotPingException, IOException, LinkNotCorrectException, UUIDNotFoundException {
        System.out.print("Введите полный URL: ");
        String originalUrl = scanner.nextLine();
        String shortCode = service.createShortLink(originalUrl);
        System.out.println("\nКороткая ссылка создана!");
        System.out.println("Оригинальный URL: " + originalUrl);
        System.out.println("Короткая ссылка: " + BASE_URL + shortCode);
        System.out.println("Код: " + shortCode);
    }

    private static void getOriginalLinks() throws LinkNotFoundException, UUIDNotFoundException {
        System.out.print("Введите короткий код: ");
        String shortCode = scanner.nextLine();
        String originalUrl = service.getOriginalLink(shortCode);
        System.out.println("\nПереход по ссылке: " + BASE_URL + shortCode);
        System.out.println("Оригинальный URL: " + originalUrl);
    }

    private static void deleteLink() throws LinkNotFoundException, UUIDNotFoundException {
        System.out.print("Введите короткий код: ");
        String shortCode = scanner.nextLine();
        service.deleteLink(shortCode);
    }

    private static void searchLinks() throws UUIDNotFoundException {
        System.out.print("Введите поисковый запрос: ");
        String query = scanner.nextLine();
        List<ShortLink> results = service.searchLinks(query);

        if (results.isEmpty()) {
            System.out.println("Ничего не найдено");
        } else {
            System.out.println("\nНайдено ссылок: " + results.size());
            results.forEach(System.out::println);
        }
    }

    private static void showRecentLinks() throws UUIDNotFoundException {
        int n = getIntInput("Сколько ссылок показать: ");
        service.showRecentLinks(n);
    }

    private static void saveData() throws IOException, UUIDNotFoundException {
        System.out.print("Введите имя файла (например, urls.csv): ");
        String filename = scanner.nextLine();
        service.saveToCSV(filename);
    }

    private static void loadData() throws IOException {
        System.out.print("Введите имя файла: ");
        String filename = scanner.nextLine();
        service.loadFromCSV(filename);
    }

    private static void showAllLinks() throws UUIDNotFoundException {
        service.showAllLinks();
    }

    private static void updateLink() throws LinkNotFoundException, UUIDNotFoundException, LinkNotPingException, IOException, LinkNotCorrectException {
        System.out.print("Введите короткую ссылку: ");
        String shortLink = scanner.nextLine();
        System.out.print("Введите новую ссылку: ");
        String newOriginalUrl = scanner.nextLine();
        service.updateOriginalLink(shortLink, newOriginalUrl);
    }

    private static void openLink() throws LinkNotFoundException, URISyntaxException, IOException, UUIDNotFoundException, LinkMaxAccessCountException, LinkExpiredException {
        System.out.print("Введите короткую ссылку: ");
        String shortLink = scanner.nextLine();
        service.openLink(shortLink);
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число");
            }
        }
    }
}
