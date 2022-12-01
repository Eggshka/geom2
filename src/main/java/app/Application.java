package app;

import io.github.humbleui.jwm.*;

import java.io.File;
import java.util.function.Consumer;

/**
 * класс окна приложения
 */
public class Application implements Consumer<Event> {
    /**
     * окно приложения
     */
    private final Window window;

    /**
     * конструктор класса приложение
     */
    public Application() {
        window = App.makeWindow();
        window.setEventListener(this);
        window.setVisible(true);
        window.setTitle("Java 2D");
        window.setWindowSize(900, 900);
        window.setWindowPosition(100, 100);
        switch (Platform.CURRENT) {
            case WINDOWS -> window.setIcon(new File("src/main/resources/windows.ico"));
            case MACOS -> window.setIcon(new File("src/main/resources/macos.icns"));
        }
    }

    /**
     * Обработчик событий
     * @param e the input argument
     */
    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowClose) {
            App.terminate();
        }else if (e instanceof EventWindowCloseRequest) {
            window.close();
        }
    }
}