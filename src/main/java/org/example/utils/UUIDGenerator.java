package org.example.utils;

import org.example.exceptions.UUIDNotCreatedException;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.UUID;

public class UUIDGenerator {
    public static String generateUUID() throws UUIDNotCreatedException, SocketException, NoSuchAlgorithmException {

            // Сбор уникальных данных устройства
            StringBuilder sb = new StringBuilder();

            // MAC-адрес первой сетевой карты
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                    break;
                }
            }

            // Добавим имя ОС и имя пользователя для стабильности
            sb.append(System.getProperty("os.name"))
                    .append(System.getProperty("os.version"))
                    .append(System.getProperty("user.name"));

            // Генерация детерминированного UUID через хеш (MD5)
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(sb.toString().getBytes());

            long msb = 0;
            long lsb = 0;
            for (int i = 0; i < 8; i++) msb = (msb << 8) | (hash[i] & 0xff);
            for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (hash[i] & 0xff);

            if (msb == 0 || lsb == 0)
                throw new UUIDNotCreatedException("UUID not created");

            return new UUID(msb, lsb).toString();

    }
}
