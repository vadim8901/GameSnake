package GameSnake;

import java.awt.*;			//библиотека для создания графического (оконного) интерфейса
import java.awt.event.*;	//библиотека для обработки нажатий на клавиши
import javax.swing.*;		//библиотека для создания графического (оконного) интерфейса
import java.util.*;  		//библиотека для рандома и списка

public class GameSnake {

    final String TITLE = "Snake";				//заголовок программы
    final String MSG_GAME_OVER = "Game Over";	//сообщение об конце игры
    final int RADIUS = 20; 						//радиус точки в пикселях
    final int WIDTH = 30; 						//ширина окна в точках
    final int HEIGHT = 20;						//высота окна в точках
    final int DX = 15;							//корректировка ширины
    final int DY = 38;							//корректировка высоты
    final int LOCATION = 200;					//стартовая позиция
    final int SNAKE_SIZE = 2;					//начальный размер змейки
    final int SNAKE_X = 10;						//начачальная позиция по Х
    final int SNAKE_Y = 10;						//начальная позиция по Y
    final int SHOW_DELAY = 150;					//задержка\скорость
    final int LEFT = 37;						//код клавиши ВЛЕВО
    final int UP = 38;							//код клавиши ВВЕРХ
    final int RIGHT = 39;						//код клавиши ВПРАВО
    final int DOWN = 40;						//код клавиши ВНИЗ
    final int DIRECTION = RIGHT;				//начальное направление движения- вправо
    final Color DEFAULT_COLOR = Color.blue;		//цвет змейки - синий
    final Color APPLE_COLOR = Color.green;		//цвет яблока - зеленый

    Snake snake;
    Apple apple;
    JFrame frame;
    Canvas canvasPanel;

    Random random = new Random();
    boolean gameOver = false;

    public static void main(String[] args) 		//главная функция
    {
        new GameSnake().game();
    }

    void game() {
        frame = new JFrame(TITLE + " : " + SNAKE_SIZE);				//создание окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		//добавление операции закрытия окна
        frame.setSize(WIDTH * RADIUS + DX, HEIGHT * RADIUS + DY);	//установка размера окна
        frame.setLocation(LOCATION, LOCATION);						//установка местоположения окна
        frame.setResizable(false);									//запрет на изменение размеров окна

        canvasPanel = new Canvas();									//создание канваса
        canvasPanel.setBackground(Color.white);						//цвет фона канваса - белый

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel); //добавление панели
        frame.addKeyListener(new KeyAdapter() {						 //обработка нажатий на клавиши

            public void keyPressed(KeyEvent e) {					//получение кода нажатой клавиши
                snake.setDirection(e.getKeyCode());
            }
        });

        frame.setVisible(true);										//показать окно

        snake = new Snake(SNAKE_X, SNAKE_Y, SNAKE_SIZE, DIRECTION);	//создаем змейку
        apple = new Apple();										//создаем яблоко

        while (!gameOver) {											//пока игра не закончена
            snake.move();											//передвигаем змейку
            if (apple.isEaten()) {									//если съели яблоко
                apple.next();										//появление следующего яблока
            }
            canvasPanel.repaint();									//обновить окно
            try {													//обработка исключений
                Thread.sleep(SHOW_DELAY);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    class Snake {													//класс - Змейка
        ArrayList<Point> snake = new ArrayList<Point>();			//создание списочного массива - тела змейки
        int direction;												//направление

        public Snake(int x, int y, int length, int direction) {		//конструктор (позиция по х, по y, длина, направление)
            for (int i = 0; i < length; i++) {
                Point point = new Point(x-i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        boolean isInsideSnake(int x, int y) {						//находится точка в теле змейки?
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y)) {
                    return true;
                }
            }
            return false;
        }

        boolean isApple(Point Apple) {								//точка - яблоко?
            return ((snake.get(0).getX() == Apple.getX()) && (snake.get(0).getY() == Apple.getY()));
        }

        void move() {												//передвижение змейки
            int x = snake.get(0).getX();							//получаем местоположение головы
            int y = snake.get(0).getY();
            if (direction == LEFT) { x--; }							//при движении в определенном направлении
            if (direction == RIGHT) { x++; }						   //меняем соответствующие координаты
            if (direction == UP) { y--; }
            if (direction == DOWN) { y++; }
            if (x > WIDTH - 1) { x = 0; }							//обрабатываем выход за границы окна
            if (x < 0) { x = WIDTH - 1; }
            if (y > HEIGHT - 1) { y = 0; }
            if (y < 0) { y = HEIGHT - 1; }

            gameOver = isInsideSnake(x, y); 						//если наступаем на тело змейки - завершаем игру
            snake.add(0, new Point(x, y));							//добавляем новую точку в тело змейки
            if (isApple(apple)) {									//если наступили на яблоко
                apple.eat();										//съедаем яблоко
                frame.setTitle(TITLE + " : " + snake.size());		//увеличиваем счет
            } else {
                snake.remove(snake.size() - 1);						//удаляем добавленную точку
            }
            //передвижение змейки происходит за счет добавления новой точки - головы и удаления последней - хвоста
        }

        void setDirection(int direction) {							//поменять направление движения
            if ((direction >= LEFT) && (direction <= DOWN)) {		//отключаем обработку ненужных клавиш
                if (Math.abs(this.direction - direction) != 2) {	//запрет изменения направления на 180 градусов за шаг
                    this.direction = direction;						//устанавливаем новое направление
                }
            }
        }

        void paint(Graphics g) {									//функция прорисовки
            for (Point point : snake) {								//цикл foreach по всему телу змейки
                point.paint(g);										//нарисовать точку
            }
        }
    }

    class Apple extends Point {										//класс - яблоко,

        public Apple() {											//конструтор
            super(-1, -1);											//яблоко образуется за границами окна
            this.color = APPLE_COLOR;								//устанавливаем цвет яблока
        }

        void eat() {												//функция съедания
            this.setXY(-1, -1);										//перемещаем яблоко за границы окна
        }

        boolean isEaten() {											//Яблоко съедено?
            return this.getX() == -1;
        }

        void next() {												//образование следующего яблока
            int x, y;
            do {
                x = random.nextInt(WIDTH);							//получаем случайную позицию
                y = random.nextInt(HEIGHT);
            } while (snake.isInsideSnake(x, y));					//пока новая позиция внутри змейки
            this.setXY(x, y);
            //т.е до тех пор, пока позиция яблока не будет вне змейки - полчаем новые координаты
        }
    }

    class Point {													//класс точка
        int x, y;
        Color color = DEFAULT_COLOR;								//усанавливаем цвет

        public Point(int x, int y) {								//устанавливаем позицию точки
            this.setXY(x, y);
        }

        void paint(Graphics g) {									//функция прорисовки точки
            g.setColor(color);										//устанавливаем цвет
            g.fillOval(x * RADIUS, y * RADIUS, RADIUS, RADIUS);		//рисуем круг
        }

        int getX() { return x; }									//получить позицию по Х
        int getY() { return y; }									//получить позицию по Y

        void setXY(int x, int y) {									//установаить новую позицию
            this.x = x;
            this.y = y;
        }
    }

    public class Canvas extends JPanel {							//класс канвас наследуем от JPanel

        @Override													//переопределяем необходимые нам функции
        public void paint(Graphics g) {
            super.paint(g);
            snake.paint(g);
            apple.paint(g);
            if (gameOver) {											//если конец игры
                g.setColor(Color.red);								//цвет сообщения - красный
                g.setFont(new Font("Arial", Font.BOLD, 40));		//шрифт - Arial
                g.drawString(MSG_GAME_OVER, 200, 200);				//выводим сообщение
            }
        }
    }
}