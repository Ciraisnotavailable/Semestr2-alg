import java.util.Scanner;

/**
 * Контрольная работа, 2 семестр (заочная форма).
 * Вариант 7. Задача: преобразовать всю строку к верхнему регистру, если
 * заглавных символов больше, чем строчных, и наоборот. При равном количестве —
 * к строчному виду. Пример: ABccAAr -> ABCCAAR; abCCaaR -> abccaar.
 */
public class CaseConverter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите строку: ");
        String s = scanner.nextLine();
        System.out.println(convert(s));
    }

    /**
     * Если заглавных букв строго больше, чем строчных, — возвращает строку
     * в верхнем регистре, иначе (в том числе при равенстве) — в нижнем.
     */
    static String convert(String s) {
        long upper = s.chars().filter(Character::isUpperCase).count();
        long lower = s.chars().filter(Character::isLowerCase).count();
        return upper > lower ? s.toUpperCase() : s.toLowerCase();
    }
}
