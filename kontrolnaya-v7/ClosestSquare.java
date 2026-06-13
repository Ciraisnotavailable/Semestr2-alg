import java.util.Scanner;

/**
 * Контрольная работа, 2 семестр (заочная форма).
 * Вариант 7. Задача: вводится число; найти целое число, квадрат которого
 * расположен ближе всего к введённому числу. Пример: 123 -> 121 -> 11.
 */
public class ClosestSquare {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите число: ");
        long n = scanner.nextLong();

        long root = closestRoot(n);
        long square = root * root;

        System.out.println("Ближайший точный квадрат: " + square);
        System.out.println("Это квадрат числа: " + root);
        System.out.println("Разница с введённым числом: " + Math.abs(n - square));
    }

    /**
     * Возвращает целое k >= 0, для которого значение k*k ближе всего к n.
     * Квадраты неотрицательны, поэтому для n <= 0 ответом является 0.
     */
    static long closestRoot(long n) {
        if (n <= 0) {
            return 0;
        }
        // Приблизительный корень; из-за погрешности double проверяем соседей.
        long approx = (long) Math.sqrt((double) n);
        long best = 0;
        long bestDiff = Long.MAX_VALUE;
        for (long k = Math.max(0, approx - 1); k <= approx + 2; k++) {
            long diff = Math.abs(k * k - n);
            if (diff < bestDiff) {
                bestDiff = diff;
                best = k;
            }
        }
        return best;
    }
}
