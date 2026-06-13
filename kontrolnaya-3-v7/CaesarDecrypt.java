import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Контрольная работа, 2 семестр (заочная форма).
 * Вариант 7. Задача: расшифровка шифра Цезаря. Эталонные частоты букв
 * формируются из текста, загруженного с https://fish-text.ru, после чего
 * перебором всех сдвигов выбирается тот, при котором распределение букв
 * расшифрованного текста ближе всего к эталонному (частотный анализ).
 */
public class CaesarDecrypt {

    static final String RU = "абвгдежзийклмнопрстуфхцчшщъыьэюя";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Зашифрованный текст: ");
        String cipher = scanner.nextLine();

        Map<Character, Double> freq = loadFreq();

        int bestShift = 0;
        double best = Double.MAX_VALUE;
        for (int shift = 0; shift < RU.length(); shift++) {
            double score = score(caesar(cipher, shift), freq);
            if (score < best) {
                best = score;
                bestShift = shift;
            }
        }

        System.out.println("Найденный сдвиг: " + bestShift);
        System.out.println("Расшифровка: " + caesar(cipher, bestShift));
    }

    /** Шифр Цезаря: сдвиг каждой русской буквы на shift позиций вперёд. */
    static String caesar(String text, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            char low = Character.toLowerCase(ch);
            if (low == 'ё') {
                low = 'е';
            }
            int idx = RU.indexOf(low);
            if (idx >= 0) {
                char shifted = RU.charAt((idx + shift) % RU.length());
                sb.append(Character.isUpperCase(ch) ? Character.toUpperCase(shifted) : shifted);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /** Сумма квадратов отклонений частот букв текста от эталонных частот. */
    static double score(String text, Map<Character, Double> freq) {
        Map<Character, Double> observed = letterFreq(text);
        double sum = 0;
        for (char l : RU.toCharArray()) {
            double diff = observed.getOrDefault(l, 0.0) - freq.getOrDefault(l, 0.0);
            sum += diff * diff;
        }
        return sum;
    }

    /** Загрузка эталонных частот букв из текста fish-text.ru (с запасным текстом). */
    static Map<Character, Double> loadFreq() {
        String text;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://fish-text.ru/get?type=sentence&number=200"))
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            text = unescapeUnicode(resp.body());          // ответ приходит в формате JSON
        } catch (IOException | InterruptedException e) {
            text = "в некотором царстве в некотором государстве жил был царь "
                 + "и было у него три сына текст для частотного анализа языка";
        }
        return letterFreq(text);
    }

    /** Доли каждой русской буквы в тексте. */
    static Map<Character, Double> letterFreq(String text) {
        Map<Character, Integer> cnt = new HashMap<>();
        int total = 0;
        for (char ch : text.toLowerCase().toCharArray()) {
            if (RU.indexOf(ch) >= 0) {
                cnt.merge(ch, 1, Integer::sum);
                total++;
            }
        }
        if (total == 0) {
            total = 1;
        }
        Map<Character, Double> freq = new HashMap<>();
        for (char l : RU.toCharArray()) {
            freq.put(l, cnt.getOrDefault(l, 0) / (double) total);
        }
        return freq;
    }

    /** Преобразование последовательностей \\uXXXX из JSON-ответа в символы. */
    static String unescapeUnicode(String s) {
        Matcher m = Pattern.compile("\\\\u([0-9a-fA-F]{4})").matcher(s);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            char c = (char) Integer.parseInt(m.group(1), 16);
            m.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(c)));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
