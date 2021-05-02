package org.example;

import java.util.ArrayList;
import java.util.List;

public class App {

    // матриця симплекс таблиці

    static Object[][] table = new Object[][]{
            {" ", "x1", "x2", "S1", "S2", "S3", "P"},
            {"Z", -7.0, -8.0, 0.0, 0.0, 0.0, 0.0},
            {"S1", -1.0, 1.0, 1.0, 0.0, 0.0, 4.0},
            {"S2", 1.0, 2.0, 0.0, 1.0, 0.0, 2.0},
            {"S3", 3.0, 2.0, 0.0, 0.0, 1.0, 6.0}
    };

    public static void main(String[] args) {

        //запускаємо цикл поки не виконається умова оптимальності

        while (!isOptimal()) {
            printTable();

            //знаходимо індекс стовпця з найменшим коефіціентом
            int minIndex = findMinIndex();
            double minValue = Double.MAX_VALUE;

            //знаходимо індекс рядка базиса що потрібно винести
            int minIndexForReplacing = 0;
            for (int i = 2; i < table.length; i++) {
                Double aDouble = (Double) table[i][minIndex];
                if (aDouble > 0) {
                    double v = aDouble / ((Double) table[i][table.length - 1]);
                    if (v < minValue) {
                        minValue = v;
                        minIndexForReplacing = i;
                    }
                }
            }
            Object toBasis = table[0][minIndex];
            System.out.println("move to basis - " + toBasis);
            Object fromBasis = table[minIndexForReplacing][0];
            System.out.println("move from basis - " + fromBasis);

            //створюємо нову матрицю
            Object[][] newTable = new Object[table.length][table[0].length];

            //перезаписуємо назви стовпців і рядків
            for (int i = 0; i < table.length; i++) {
                for (int j = 0; j < table[0].length; j++) {
                    if( i==0 || j ==0 ){
                        newTable[i][j] = table[i][j];
                    }
                }
            }

            //знаходимо опорний елемент
            Double opor = (Double) table[minIndexForReplacing][minIndex];

            //записуємо назву нової базисної зміної замість старої
            newTable[minIndexForReplacing][0] = table[0][minIndex];

            //обчислюємо опорний рядок
            for (int i = 1; i < table[0].length; i++) {
                Double aDouble = ((Double) table[minIndexForReplacing][i]);
                newTable[minIndexForReplacing][i] = aDouble / opor;
            }

            //обчислюємо опорний стовпець
            for (int i = 1; i < table.length; i++) {
                if (i != minIndexForReplacing) {
                    newTable[i][minIndex] = 0.0;
                }
            }

            //Знаходимо базисні змінні що не будуть змінюватися
            List<Object> basis = new ArrayList<>();

            for (int i = 2; i < table.length; i++) {
                if (i != minIndexForReplacing) {
                    basis.add(table[i][0]);
                }
            }

            //знаходимо індекси колонок базисних змін що не потребують змін
            List<Integer> columnToSkip = new ArrayList<>();

            for (int i = 1; i < table[0].length; i++) {
                int finalI = i;
                if (basis.stream().anyMatch(b -> b.equals(table[0][finalI]))) {
                    columnToSkip.add(i);
                }
            }

            //переносимо їх у нову таблицю без змін
            for (Integer integer : columnToSkip) {
                for (int i = 1; i < table.length; i++) {
                    newTable[i][integer] = table[i][integer];
                }
            }

            //проходимо по всіх клітинках, що потребують обчислення за правилом трикутника
            // і замінюємо значення за правилом трикутника
            for (int i = 1; i < table.length; i++) {
                for (int j = 1; j < table[0].length; j++) {
                    if (i != minIndexForReplacing && j != minIndex && !columnToSkip.contains(j)) {

                        Double aDouble = ((Double) table[i][j]);

                        double opDiag = ((Double) table[i][minIndex])* ((Double) table[minIndexForReplacing][j]);
                        newTable[i][j] = (aDouble * opor -opDiag) / opor;
                    }
                }
            }

            //присвоюємо значення нової таблиці
            table = newTable;

        }
        printTable();
    }

    //функція для виведення стану симплекс таблиці
    private static void printTable() {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                Object o = table[i][j];
                StringBuilder str = new StringBuilder(o.toString());
                while (str.length() < 6) {
                    str.append(" ");
                }
                System.out.print(str + "|");
            }
            System.out.println();
        }
    }

    //функція що повертає індекс стовпця з найменшим коефіціентом, що потім буде внесено у базис
    private static int findMinIndex() {
        double minCoefficient = findMinCoefficient();
        for (int i = 1; i < table[1].length; i++) {
            Double aDouble = (Double) table[1][i];
            if (aDouble == minCoefficient) {
                return i;
            }
        }
        throw new RuntimeException();
    }

    //функція що перевіряє ознаку оптимальності
    private static boolean isOptimal() {
        return findMinCoefficient() == 0.0;
    }

    //функція що знаходить найменший коефіціент в рядку цільової функції
    private static double findMinCoefficient() {
        double min = 0.0;
        for (int i = 1; i < table[1].length - 1; i++) {
            Double aDouble = (Double) table[1][i];
            if (min > aDouble) {
                min = aDouble;
            }
        }
        return min;
    }

}
