import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

class Perceptron {

    private int inputsNumber; // кол-во входов
    private String fiType; // тип решающей функции
    private double theta; // порог
    private int[] weights; // веса
    private int[] inputs; // Входы перцептрона - на них каждый раз при приходе данных будут устанавливаться уровни из выборки
    private int trueSum = 0; // Сумма правильных ответов
    private int falseSum = 0; // Сумма ошибочных ответов
    private List<Integer> testMatchesStorage = new ArrayList<>(); // Здесь будут хранится индексы рядов, заболевших людей.
    private boolean isWork = false; // Флаг, показывающий, работает сеть или учится

    Perceptron(int inputsNumber, String fiType, double theta) {
        Journal.logln("<h1>Создание нейронной сети:</h1>");
        Journal.logln("<table border=\"1\"><tr><th>Кол-во входов</th>" +
                "<th>Тип решающей функции</th><th>Уровень отсечки (Тетта, порог)</th></tr><tr><td>" + 
                inputsNumber +"</td><td>"+ fiType +"</td><td>"+ theta +"</td></tr></table>");
        this.inputsNumber = inputsNumber;
        this.fiType = fiType;
        this.theta = theta;
        // Создаём веса для каждого входа
        this.weights = new int[inputsNumber];
        this.inputs = new int[inputsNumber];
        // Назначаем весам значения по умолчанию
        for(int i = 0; i < inputsNumber; i++) {
            this.weights[i] = 0;
            this.inputs[i] = 0;
        }
        System.out.println("Перцептрон успешно создан");
    }

    private void train(int cycles, int[][] materials, int[] rightAnswers) throws IOException {
        // Выводим ошибку, если кол-во входов не соответвует размеру массива с данными
        if(materials[0].length < this.inputsNumber) {
            throw new IOException("The number of inputs does not correspond to the number of array elements");
        }
        System.out.println("Начато обучение нейронной сети...");
        Journal.logln("<h1>Обучение нейронной сети:</h1>");
        for(int i = 0; i < cycles; i++) {
            Journal.logln("<div style=\"float:left; width: 800px;\"><h2>Iteration # " + i + "</h2>");
            for(int j = 0; j < materials.length; j++) {
                Journal.logln("<table border=\"1\" style=\"float:left\"><tr><th>Набор # " + j + "</th></tr><tr>");
                Journal.log("<td><br>Веса:");
                for(int weight : this.weights) {
                    Journal.log(" " + weight);
                }
            // Каждая пара(или много цифр) обучающей выборки
                this.inputs = materials[j]; // Устанавливаем на входах уровни из выборки
                // Журналируем
                Journal.log("<br>Уровни:");
                for(int h = 0; h < materials[j].length; h++) {
                    Journal.log(" " + materials[j][h]);
                }

                // Коррекция весов:
                this.evaluation(j, this.fi(materials[j]), rightAnswers);
            }
            Journal.logln("<div><h2>Conclusion</h2>");
            Journal.logln("<p>Кол-во правильных ответов: " + this.trueSum + "</p>");
            Journal.logln("<p>Кол-во ошибок: " + this.falseSum + "</p></div></div>");
        }
        System.out.println("Процесс обучения завершён, результаты в файле " + Journal.pathToFileJournal);
    }

    private boolean fi(int[] touple) throws IOException {
        double net = 0;
        // Берём каждый элемент в паре и считаем взвешенную сумму в соответствии их весам
        // Напоминаю, что индекс элемента в паре соответствует индексу веса, в конструкторе это видно
        for(int i = 0; i < touple.length; i++) {
            net += touple[i] * this.weights[i];
        }

        switch(this.fiType) {
            case("jump"):
                // Теперь сопоставляем сумму с порогом и возвращаем ответ
                return net >= this.theta;
            case("sigma(1)"):
                double r = 1/(1 + Math.exp((-1)*net));
                return r >= this.theta;
            case("sigma(2)"):
                double r2 = 1/(1 + Math.exp((-2)*net));
                return r2 >= this.theta;
            case("sigma(1/2)"):
                double r3 = 1/(1 + Math.exp((-0.5)*net));
                return r3 >= this.theta;
            default:
                throw new IOException("Type of fi method is incorrect! Must be 'jump' or 'sigma(1)' or sigma(2) or sigma(1/2) only!");
        }
    }

    // Наказывает сеть за ошибки, таким образом обучая её
    private void evaluation(int pos, boolean answer, int[] rightAnswers) {
        // Определяем права сеть или нет
        // Если позиция проверяемого нами человека совпала с позицией одного из занесённых в массив с заболевшими
        // то знаем что тут ответ должен быть "истина", а если ответ - "ложь", то наказать
        if(hasRightsIndex(pos, rightAnswers)) {
            if(!answer) {
                // Добавляем 1 к весам связей
                Journal.logln("<br>Здоров - MISTAKE<br></td></tr></table>");
                for(int j = 0; j < this.inputs.length; j++) {
                    this.weights[j] += this.inputs[j];
                }
                this.falseSum += 1;
                
            } else {
                Journal.logln("<br>Болен - CORRECT!<br></td></tr></table>");
                this.trueSum += 1;
                isWorkThenAddToStorage(pos);
                
            }
        } else {
            // Если позиция не совпадает с одной из отмеченных больных, но система говорит, что он болен,
            // то наказываем, только по-другому
            if(answer) {
                Journal.logln("<br>Болен - MISTAKE<br></td></tr></table>");
                for(int k = 0; k < this.inputs.length; k++) {
                    this.weights[k] -= this.inputs[k];
                }
                isWorkThenAddToStorage(pos);
                this.falseSum += 1;
                
            } else {
                Journal.logln("<br>Здоров - CORRECT<br></td></tr></table>");
                this.trueSum += 1;
            }
        }
    }

    private void work(int[][] materials) throws IOException {
        Journal.logln("<h1>Рабочий процесс</h1>");
        this.isWork = true;
        Journal.logln("<p>Входные данные:</p>");
        for(int j = 0; j < materials.length; j++) {
            Journal.logln("[");
            for(int i = 0; i < materials[j].length; i++) {
                Journal.log(""+materials[j][i]);
            }
            Journal.logln("]");
        // Каждая пара(или много цифр) обучающей выборки
            this.inputs = materials[j]; // Устанавливаем на входах уровни из выборки
            // Ответы:
            if(this.fi(materials[j])) {
                isWorkThenAddToStorage(j);
            }
        }
        Journal.logln("<h2>Номера анализов в списке заболевших: " + this.testMatchesStorage + "</h2>");
        System.out.println("Процесс прогнозирования завешён, результаты в файле: " + Journal.pathToFileJournal);
    }

    private void isWorkThenAddToStorage(int pos) {
        if(this.isWork) {
            this.testMatchesStorage.add(pos);
        }
    }

    private boolean hasRightsIndex(int pos, int[] rightAnswers) {
        for (int rightAnswer : rightAnswers) {
            if (pos == rightAnswer) {
                return true;
            }
        }
        return false;
    }

    void startTraining(int cycles, int[][] materials, int[] rightAnswers){
        try {
            this.train(cycles, materials, rightAnswers);
        } catch (IOException e) {
            Journal.except(e.toString());
        }
    }

    void startWork(int[][] materials) {
        try {
            this.work(materials);
        } catch (IOException e) {
            Journal.except(e.toString());
        }
    }
}