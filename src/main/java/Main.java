import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmytriy Lunyov, Odessa
 * Упрощённая версия перцептрона, сведённая до типа "Искусственный нейрон".
 * На вход подаются поочерёдно некие показатели в виде целых чисел (например {1, 2, 3, 4}),
 * которые могут быть показателями, например, болезни.
 * Будем считать, что предсказываем наличие болезни у людей с различными показателями.
 * Для этого была описана примитивная мат. модель анализа с наличием болезни (описанная в методе mathModel()),
 * которая говорит, что если показатели == {<2, >5, >5, 0}, то это указывает на наличие болезни.
 * На выходе получаем "Истина" или "Ложь" относительно определённой группы показателей
 */

public class Main {

    public static void main(String[] args) throws IOException {

        Journal.open("journal.html");

        int inputsNum = 4; // Кол-во входов,
        double theta = 1; // Уровень отсечки
        int cycles = 3; // Кол-во итераций

        /* ------------ Показатели, для случая, если выборка генерируется ---------------- **/
        //int iterationsNum = 1000; // Кол-во карточек анализов для генерации
        //int n = 10; // В каких пределах может варьироваться генерируемые показатели анализа

        Perceptron neural = new Perceptron(inputsNum, "sigma(2)", theta);

        /* -------------------------- Ручная обучающая выборка --------------------------- **/

        int[][] materials = {
                {1, 6, 7, 0},
                {0, 8, 9, 0},
                {5, 2, 2, 1},
                {3, 1, 1, 5},
                {0, 7, 8, 0},
                {1, 8, 7, 0},
                {4, 3, 2, 3},
                {3, 2, 2, 9},
                {4, 1, 1, 7},
                {1, 6, 6, 0},
                {0, 8, 6, 0}
        };

        // Анализы на которых перцептрон должен ответить true - положительны на %болезнь_name%
        int[] marks = mathModel(materials);

        /* ------------- Генерация последовательностислучайной выборки анализов ----------- **/
        //materials - int[][] обучающая выборка анализов, каждый [i] элемент это анализы i-того человека, а каждый [i][j] - конкретный показатель)
        // int[][] materials = new int[iterationsNum][inputsNum];
        // for(int i = 0; i < iterationsNum; i++) {
        //     for(int j = 0; j < inputsNum; j++) {
        //         materials[i][j] = new Random().nextInt(n);
        //     }
        // }
        // int[] marks = mathModel(materials);

        /* ----------------------------------- Обучение ----------------------------------- **/

        neural.startTraining(cycles, materials, marks);

        /* -------------------------------- Тестовая выборка ------------------------------ **/
        //Анализы номер 0, 3, 4 являются положительными на %болезнь_name%, здесь перцептрон должен ответить true!)
        int[][] materialsTest = {{0, 7, 8, 0}, {3, 2, 1, 10}, {4, 2, 4, 4}, {1, 6, 7, 0}, {1, 8, 7, 0}, {5, 2, 2, 1}};

        neural.startWork(materialsTest);

        Journal.close();
    }

    /* Математическая модель %болезнь_name% для 4 входов, если {<2, >5, >5, 0} - значит на этом наборе - человек болен **/
    private static int[] mathModel(int[][] maters) {
        List<Integer> resultsCol = new ArrayList<>();
        for(int i = 0; i < maters.length; i++) {
            if(maters[i][0] < 2 && maters[i][1] > 5 && maters[i][2] > 5 && maters[i][3] == 0) {
                resultsCol.add(i);
            }
        }
        int[] results = new int[resultsCol.size()];
        for(int j = 0; j < resultsCol.size(); j++) {
            results[j] = resultsCol.get(j);
        }
        return results;
    }
}