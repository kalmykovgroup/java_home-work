package org.example;

import org.example.figures.Cube;
import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    // Оконная ручка
    private long window;

    private List<Cube> cubes = new ArrayList<Cube>();

    private Cube currentCube;
    private boolean rotatingRight = false;
    private boolean rotatingLeft = false;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Освободите обратные вызовы окна и уничтожьте окно
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Завершите GLFW и освободите обратный вызов ошибки
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Настройте обратный вызов ошибки. Реализация по умолчанию
        // выведет сообщение об ошибке в System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Инициализируем GLFW. До этого большинство функций GLFW работать не будут.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Настройка GLFW
        glfwDefaultWindowHints(); // необязательно, подсказки текущего окна уже используются по умолчанию
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // после создания окно останется скрытым
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // размер окна можно будет изменять

        // Create the window
        window = glfwCreateWindow(800, 600, "ART OF WAR 4", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Настройте обратный вызов клавиши. Он будет вызываться при каждом нажатии, повторении или отпускании клавиши.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {


            if(action == GLFW_PRESS) {
                switch (key){
                    case GLFW_KEY_UP -> currentCube.y += 0.25f;
                    case GLFW_KEY_DOWN -> currentCube.y -= 0.25f;
                    case GLFW_KEY_LEFT -> currentCube.x -= 0.25f;
                    case GLFW_KEY_RIGHT -> currentCube.x += 0.25f;
                    case GLFW_KEY_W -> currentCube.z += 0.25f;
                    case GLFW_KEY_S -> currentCube.z -= 0.25f;
                    case GLFW_KEY_ENTER -> {
                        cubes.add(currentCube);
                        currentCube = new Cube(0, 0, -5, 0);
                    }
                }
            }

            if(action == GLFW_RELEASE){
                switch (key){
                    case GLFW_KEY_D -> rotatingRight = false;
                    case GLFW_KEY_A -> rotatingLeft = false;
                }
            }


            if(key == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key){
                    case GLFW_KEY_UP -> currentCube.y += 0.15f;
                    case GLFW_KEY_DOWN -> currentCube.y -= 0.15f;
                    case GLFW_KEY_LEFT -> currentCube.x -= 0.15f;
                    case GLFW_KEY_RIGHT -> currentCube.x += 0.15f;
                    case GLFW_KEY_W -> currentCube.z += 0.05f;
                    case GLFW_KEY_S -> currentCube.z -= 0.05f;
                    case GLFW_KEY_D -> rotatingRight = true;
                    case GLFW_KEY_A -> rotatingLeft = true;
                }
            }
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // Мы обнаружим это в цикле рендеринга
        });

        currentCube = new Cube(0, 0, -5, 0);

        // Получаем стек потоков и помещаем новый фрейм
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Возвращает размер окна, передаваемый в glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Получить разрешение основного монитора
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Расположить окно по центру
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // кадр стека извлекается автоматически

        // Сделать контекст OpenGL текущим
        glfwMakeContextCurrent(window);
        // Включить v-синхронизацию
        glfwSwapInterval(1);

        // Сделать окно видимым
        glfwShowWindow(window);
    }

    private void gradientBg(){
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, 800, 0, 600, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDepthMask(false);

        glBegin(GL_QUADS);

        glColor3f(0, 0.03f, 0.7f);
        glVertex2f(0, 0);
        glVertex2f(800, 0);

        glColor3f(0, 0.7f, 1f);
        glVertex2f(800, 600);
        glVertex2f(0, 600);

        glEnd();

        glDepthMask(true);
        glDisable(GL_BLEND);
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
    }

    private void loop() {
        // Эта строка имеет решающее значение для взаимодействия LWJGL с контекстом GLFW
        // OpenGL или любым другим контекстом, управляемым извне.
        // LWJGL определяет контекст, который является текущим в текущем потоке,
        // создает экземпляр GLCapabilities и делает OpenGL
        // привязки доступными для использования.
        GL.createCapabilities();

        glEnable( GL_DEPTH_TEST );
        Matrix4f projection = new Matrix4f()
                .perspective(
                        (float) Math.toRadians(45.0),
                        800.0f / 600.0f, 0.1f, 100.0f
                );
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        projection.get(fb);

        // Установите четкий цвет
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Запускайте цикл рендеринга до тех пор, пока пользователь не попытается закрыть
        // окно или не нажмет клавишу ESCAPE.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // очистить фреймбуфер

             glMatrixMode(GL_PROJECTION);
             glLoadMatrixf(fb);

             gradientBg();

             for(Cube cube : cubes){
                 cube.draw();
             }
            if(rotatingLeft){
                currentCube.angle -= 0.5f;
            }
            if(rotatingRight){
                currentCube.angle += 0.5f;
            }

             currentCube.draw();


            glfwSwapBuffers(window); // поменять местами цветовые буферы

            // Опросить события окна. Обратный вызов ключа, описанный выше, будет
            // вызван только во время этого вызова.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}