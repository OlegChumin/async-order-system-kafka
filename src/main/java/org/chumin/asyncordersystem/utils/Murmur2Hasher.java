package org.chumin.asyncordersystem.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Утилитный класс для расчета MurmurHash2, аналогично тому,
 * как это используется в Apache Kafka для распределения ключей по партициям.
 */
@Slf4j
public class Murmur2Hasher {

    /**
     * Реализация MurmurHash2 хеш-функции (такой же алгоритм, как в Kafka).
     * Используется для равномерного распределения ключей по партициям.
     *
     * @param data массив байт (например, key.getBytes())
     * @return 32-битный хеш
     */
    public static int murmur2(final byte[] data) {
        int length = data.length;         // длина входного массива
        int seed = 0x9747b28c;            // фиксированный сид, как в Kafka
        int m = 0x5bd1e995;               // "магическая" константа умножения
        int r = 24;                       // количество бит для сдвига вправо

        // начальное значение хеша: seed xor длина данных
        int h = seed ^ length;
        int length4 = length / 4;         // количество полных блоков по 4 байта

        // основная обработка блоками по 4 байта
        for (int i = 0; i < length4; i++) {
            int i4 = i * 4;
            // собираем 4 байта в int, little-endian
            int k = (data[i4] & 0xff)
                    | ((data[i4 + 1] & 0xff) << 8)
                    | ((data[i4 + 2] & 0xff) << 16)
                    | ((data[i4 + 3] & 0xff) << 24);

            k *= m;                       // умножение на m
            k ^= k >>> r;                 // xor со сдвигом вправо на r
            k *= m;                       // снова умножение на m

            h *= m;                       // умножаем текущий хеш на m
            h ^= k;                       // xor с текущим блоком
        }

        // обработка оставшихся 1-3 байтов
        int offset = length4 * 4;
        switch (length % 4) {
            case 3:
                h ^= (data[offset + 2] & 0xff) << 16;
                // fall through
            case 2:
                h ^= (data[offset + 1] & 0xff) << 8;
                // fall through
            case 1:
                h ^= (data[offset] & 0xff);
                h *= m;
                break;
            default:
                // no remaining bytes, intentionally empty
                break;
        }

        // финализация (аваланш-эффект для хорошего перемешивания)
        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h; // возвращаем итоговый хеш
    }

    /**
     * Делает число положительным за счет маскирования знакового бита.
     * Используется Kafka для получения неотрицательного значения,
     * чтобы корректно вычислить (hash % numPartitions).
     *
     * @param number входной int
     * @return всегда >= 0
     */
    public static int toPositive(int number) {
        return number & 0x7fffffff; // убираем старший (знаковый) бит
    }

    /**
     * Демонстрация работы MurmurHash2 и распределения по партициям.
     *
     * @param args не используется
     */
    public static void main(String[] args) {
        String key = "my-key";         // ключ, который хотим захешировать
        int numPartitions = 10;        // количество партиций в топике

        int hash = murmur2(key.getBytes());          // считаем murmur2 хеш
        int positiveHash = toPositive(hash);         // убираем знак
        int partition = positiveHash % numPartitions; // получаем номер партиции

        // теперь используем Lombok Slf4j для логирования
        log.info("Key: {}", key);
        log.info("Murmur2 hash: {}", hash);
        log.info("Positive hash: {}", positiveHash);
        log.info("Assigned partition: {}", partition);
    }
}
